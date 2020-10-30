/*
 * Copyright 2020 Mihai Bojin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mihaibojin.props.core;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.logging.Level.SEVERE;

import com.mihaibojin.props.core.annotations.Nullable;
import com.mihaibojin.props.core.converters.Cast;
import com.mihaibojin.props.core.converters.Converter;
import com.mihaibojin.props.core.internal.TemplateStringUtils;
import com.mihaibojin.props.core.resolvers.PropertyFileResolver;
import com.mihaibojin.props.core.resolvers.Resolver;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Props {

  private static final Logger log = Logger.getLogger(PropertyFileResolver.class.getName());
  private final ScheduledExecutorService executor;
  private final Map<String, Prop<?>> boundProps = new ConcurrentHashMap<>();
  private final Map<String, String> propIdToResolver = new ConcurrentHashMap<>();
  private final CountDownLatch latch = new CountDownLatch(1);

  private final List<String> prioritizedResolvers;
  private final Map<String, Resolver> resolvers;
  private final Duration shutdownGracePeriod;
  private final Duration refreshInterval;

  private Props(
      LinkedHashMap<String, Resolver> resolvers,
      Duration refreshInterval,
      Duration shutdownGracePeriod) {
    this.resolvers = Collections.unmodifiableMap(resolvers);

    // generate a list of resolver IDs, ordered by priority (highest first)
    ArrayList<String> ids = new ArrayList<>(resolvers.keySet());
    Collections.reverse(ids);
    prioritizedResolvers = Collections.unmodifiableList(ids);

    this.refreshInterval = refreshInterval;
    this.shutdownGracePeriod = shutdownGracePeriod;

    // create an executor with Daemon threads, allowing the executor to shutdown when all
    // non-daemon threads exit
    // this executor will only be used for refreshing resolvers, and as such a single
    // thread should ever be run at the same time
    executor =
        Executors.newScheduledThreadPool(
            1,
            runnable -> {
              Thread thread = Executors.defaultThreadFactory().newThread(runnable);
              thread.setDaemon(true);
              return thread;
            });

    // register a shutdown hook, allowing the executor to gracefully shutdown
    Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

    // perform an initial load, ensuring that all resolvers have values
    executor.submit(
        () -> {
          this.resolvers.entrySet().parallelStream().forEach(Props::safeReload);
          latch.countDown();
        });

    // and schedule a period refresh operation
    executor.scheduleAtFixedRate(
        () -> refreshResolvers(this.resolvers),
        refreshInterval.toMillis(),
        refreshInterval.toMillis(),
        TimeUnit.MILLISECONDS);
  }

  /**
   * Safely reload all the values managed by the specified {@link Resolver} and logs any exceptions.
   */
  private static Set<String> safeReload(Entry<String, Resolver> res) {
    try {
      return res.getValue().reload();
    } catch (Throwable t) {
      log.log(SEVERE, "Unexpected error reloading props from " + res.getKey(), t);
    }
    return Set.of();
  }

  /** Convenience method for configuring {@link Props} registry objects. */
  public static Factory factory() {
    return new Factory();
  }

  /**
   * Binds the specified prop to the current {@link Props} registry.
   *
   * <p>If a non-null <code>resolverId</code> is specified, it will link the prop to that resolver.
   *
   * @throws BindException if attempting to bind a {@link Prop} for a key which was already bound to
   *     another object. This is to encourage efficiency and define a single object per key, keeping
   *     memory usage low(er). Use the {@link #retrieveProp(String)} and {@link #retrieve(String)}
   *     methods to get a pre-existing instance.
   * @throws IllegalArgumentException if the specified <code>resolverId</code> is not known to the
   *     registry.
   */
  public <T, R extends Prop<T>> R bind(R prop, @Nullable String resolverId) {
    Prop<?> oldProp = boundProps.putIfAbsent(prop.key(), prop);
    if (nonNull(oldProp) && oldProp != prop) {
      throw new BindException(prop.key(), oldProp);
    }

    // NullAway does not recognize Objects.nonNull (https://github.com/uber/NullAway/issues/393)
    if (!Objects.isNull(resolverId)) {
      // only register the prop with a resolver, if the id is non-null and valid
      validateResolver(resolverId);
      propIdToResolver.put(prop.key(), resolverId);
    }

    update(prop);

    return prop;
  }

  /**
   * Convenience method for users who need to bind {@link Prop}s manually.
   *
   * @see #bind(Prop, String)
   */
  public <T, R extends Prop<T>> R bind(R prop) {
    return bind(prop, null);
  }

  /**
   * Returns an existing (bound) {@link Prop} object, or <code>null</code> if one does not exist for
   * the specified key.
   */
  @Nullable
  public Prop<?> retrieveProp(String key) {
    return boundProps.get(key);
  }

  /**
   * Returns an existing (bound) {@link Prop} object, cast to the expected type, or <code>null
   * </code> if a prop was not bound for the specified key.
   *
   * @throws ClassCastException if the property key is associated with a different type
   */
  @Nullable
  @SuppressWarnings("unchecked")
  public <T, R extends Prop<T>> R retrieve(String key) {
    return (R) boundProps.get(key);
  }

  /**
   * Updates the {@link Prop}'s current value.
   *
   * @return true if the property was updated, or false if it kept its value
   */
  protected <T> boolean update(Prop<T> prop) {
    // retrieve the Prop's current value
    T currentValue = ((AbstractProp<T>) prop).getValueInternal();

    // determine if the prop is linked to a specific resolver
    String resolverId = propIdToResolver.get(prop.key());
    // resolve the Props' updated value
    T updatedValue = resolveProp(prop, resolverId);

    // if the value has changed
    if (!Objects.equals(currentValue, updatedValue)) {
      // update the current value
      ((AbstractProp<T>) prop).setValue(updatedValue);
      return true;
    }

    // otherwise return false, since no updates took place
    return false;
  }

  /** Search all resolvers for a value. */
  @Nullable
  <T> T resolveProp(Prop<T> prop, @Nullable String resolverId) {
    return resolveByKey(prop.key(), prop, resolverId);
  }

  /**
   * Searches all resolvers for the specified key and converts the result to the designated type.
   *
   * <p>If a <code>resolverId</code> is specified, only search the matching resolver.
   */
  @Nullable
  <T> T resolveByKey(String key, Converter<T> converter, @Nullable String resolverId) {
    if (!waitForInitialLoad()) {
      return null;
    }

    if (nonNull(resolverId)) {
      // if the prop is bound to a single resolver, return it on the spot
      Resolver resolver = resolvers.get(resolverId);
      if (isNull(resolver)) {
        return null;
      }

      String val = resolver.get(key);
      if (isNull(val)) {
        return null;
      }

      return converter.decode(val);
    }

    for (String id : prioritizedResolvers) {
      // search each resolver, in priority order
      Resolver resolver = resolvers.get(id);
      if (isNull(resolver)) {
        continue;
      }

      // find the appropriate value, if it exists
      String value = resolver.get(key);
      if (isNull(value)) {
        continue;
      }

      // the current method is hot (can be called many times) and the following optimization
      // avoids unnecessary object allocations due to calling String.format(...)
      if (log.getLevel() == Level.FINER) {
        log.log(Level.FINER, () -> format("%s resolved by %s", key, id));
      }

      // return an optional which decodes the value on get
      // the reason for lazy decoding is to reduce confusion in a potential stacktrace
      // since the problem would be related to decoding the retrieved string and not with
      // resolving the value
      return converter.decode(value);
    }

    return null;
  }

  /**
   * Validates the specified <code>resolverId</code>. Throws an exception if a resolver was not
   * found.
   *
   * @throws IllegalArgumentException if the specified id is not known to the current {@link Props}
   *     registry
   */
  private void validateResolver(String resolverId) {
    if (!resolvers.containsKey(resolverId)) {
      throw new IllegalArgumentException(
          "Resolver " + resolverId + " is not registered with the current registry");
    }
  }

  /**
   * Returns a {@link LinkedHashMap} containing mappings for each resolver which contained a value
   * for the specified {@link Prop}'s key.
   *
   * <p>The map can be iterated over in and will return {@link Resolver}s order by priority,
   * lowest-to-highest.
   */
  public <T> Map<String, T> resolvePropLayers(Prop<T> prop) {
    // read all the values from the registry
    Map<String, T> layers = new LinkedHashMap<>();

    // process all layers and transform them into the final type
    for (Entry<String, Resolver> entry : resolvers.entrySet()) {
      String value = entry.getValue().get(prop.key());
      if (!isNull(value)) {
        T resolved = prop.decode(value);
        layers.put(entry.getKey(), resolved);
      }
    }

    return layers;
  }

  /**
   * Waits for the initial operation to load all resolvers to complete.
   *
   * @return true if the wait completed successfully
   */
  private <T> boolean waitForInitialLoad() {
    try {
      latch.await(refreshInterval.toSeconds(), TimeUnit.SECONDS);
      return true;

    } catch (InterruptedException e) {
      log.log(SEVERE, "Could not resolve in time", e);
      Thread.currentThread().interrupt();
      return false;
    }
  }

  /** Refreshes values from all the registered {@link Resolver}s. */
  private void refreshResolvers(Map<String, Resolver> resolvers) {
    Set<? extends Prop<?>> toUpdate =
        resolvers.entrySet().parallelStream()
            .filter(r -> r.getValue().isReloadable())
            .map(Props::safeReload)
            .flatMap(keys -> keys.stream().map(boundProps::get).filter(Objects::nonNull))
            // we need to collect since we need all layers to have finished their update cycle
            // before reading them
            .collect(Collectors.toSet());

    toUpdate.forEach(this::update);
  }

  /** Gracefully terminate this class's {@link ScheduledExecutorService}. */
  private void shutdown() {
    log.info("Shutting down the Props executor...");
    executor.shutdown();
    try {
      executor.awaitTermination(shutdownGracePeriod.toSeconds(), TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      log.warning("Interrupted while waiting for executor shutdown; terminating...");
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  /** Convenience method for building string {@link Prop}s. */
  public Builder<String> prop(String key) {
    return new Builder<>(key, Cast.asString());
  }

  /** Convenience method for building {@link Prop}s. */
  public <T> Builder<T> prop(String key, Converter<T> converter) {
    return new Builder<>(key, converter);
  }

  /** Factory class for building {@link Props} registry classes. */
  public static class Factory {

    private final LinkedHashMap<String, Resolver> resolvers = new LinkedHashMap<>();
    private Duration refreshInterval = Duration.ofSeconds(30);
    private Duration shutdownGracePeriod = Duration.ofSeconds(10);

    private Factory() {}

    /** Adds a resolver and identifies it by its {@link Resolver#id()}. */
    public Factory withResolver(Resolver resolver) {
      resolvers.put(resolver.id(), resolver);
      return this;
    }

    /** Adds a resolver and identifies it by its {@link Resolver#id()}. */
    public Factory withResolvers(Collection<Resolver> resolvers) {
      resolvers.forEach(r -> this.resolvers.put(r.id(), r));
      return this;
    }

    /**
     * Allows customizing the refresh interval at which auto-update-able {@link
     * com.mihaibojin.props.core.resolvers.Resolver}s are refreshed.
     */
    public Factory refreshInterval(Duration interval) {
      refreshInterval = interval;
      return this;
    }

    /**
     * Allows customizing the shutdown grace period, before the executor is forcefully shut down.
     */
    public Factory shutdownGracePeriod(Duration shutdownGracePeriod) {
      this.shutdownGracePeriod = shutdownGracePeriod;
      return this;
    }

    /**
     * Creates the {@link Props} object.
     *
     * @throws IllegalStateException if the method is called without registering any {@link
     *     Resolver}s
     */
    public Props build() {
      if (resolvers.isEmpty()) {
        throw new IllegalStateException("Cannot initialize Props without any Resolvers");
      }

      Props props = new Props(resolvers, refreshInterval, shutdownGracePeriod);

      return props;
    }
  }

  /**
   * Allows rendering templates which are made up of 0 to many tokens.
   *
   * <p>A token is an alphanumeric string (unicode chars, _, -, and .) enclosed in curly braces,
   * e.g.: {token}.
   *
   * <p>When this method is called, {@link Props} will first load the template (which must be a
   * String prop), identify all the tokens in the template, and subsequently populate every token
   * with its current value (as observed by the specified <code>resolverId</code>).
   *
   * <p>All the tokens' values will be populated as strings ({@link Cast#asString()}).
   */
  @Nullable
  public String renderTemplate(String key, String resolverId) {
    String template = resolveByKey(key, Cast.asString(), resolverId);
    if (isNull(template)) {
      return null;
    }

    String processed = template;
    Set<String> tokens = TemplateStringUtils.parseTokens(template);
    for (String token : tokens) {
      String value;

      Prop<?> maybeProp = retrieveProp(token);
      if (!isNull(maybeProp)) {
        // if a Prop is already bound, use it to benefit from its toString() implementation
        value = Objects.toString(maybeProp.value());
      } else {
        // otherwise just resolve on the spot
        value = Objects.toString(resolveByKey(token, Cast.asString(), resolverId));
      }

      processed = processed.replace("{" + token + "}", value);
    }

    return processed;
  }

  /** Builder class for creating custom {@link Prop}s from the current {@link Props} registry. */
  public class Builder<T> {

    public final String key;
    public final Converter<T> converter;
    @Nullable private T defaultValue;
    @Nullable private String description;
    private boolean isRequired;
    private boolean isSecret;
    @Nullable private String resolverId;

    private Builder(String key, Converter<T> converter) {
      this.key = key;
      this.converter = converter;

      // validate the two required properties
      if (isNull(key)) {
        throw new IllegalStateException("The property's key cannot be null");
      }
      if (isNull(converter)) {
        throw new IllegalStateException("A converter must be specified");
      }
    }

    /** Specifies the resolver (by id) to use for retrieving this property. */
    public Builder<T> resolver(String resolverId) {
      validateResolver(resolverId);
      this.resolverId = resolverId;
      return this;
    }

    public Builder<T> defaultValue(T defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    public Builder<T> description(String description) {
      this.description = description;
      return this;
    }

    public Builder<T> isRequired(boolean isRequired) {
      this.isRequired = isRequired;
      return this;
    }

    public Builder<T> isSecret(boolean isSecret) {
      this.isSecret = isSecret;
      return this;
    }

    /**
     * Constructs the {@link Prop}, binds it to the current {@link Props} instance, and returns it.
     */
    public Prop<T> build() {
      return bind(
          new AbstractProp<>(key, defaultValue, description, isRequired, isSecret) {
            @Override
            @Nullable
            public T decode(String value) {
              return converter.decode(value);
            }

            @Override
            public String encode(T value) {
              return converter.encode(value);
            }
          },
          resolverId);
    }

    /**
     * Reads the designated key without binding a <code>Prop</code> to the registry.
     *
     * <p>This is a convenience method for retrieving values on the spot.
     *
     * @throws ValidationException if a required prop does not have a value or a default
     */
    @Nullable
    public T value() {
      T result = resolveByKey(key, converter, resolverId);
      if (isNull(result)) {
        // if looking for the key did not find the value, choose the default value
        result = defaultValue;
      }

      // if the Prop is required, a value must be available
      if (isRequired && isNull(result)) {
        throw new ValidationException(
            format("Prop '%s' is required, but neither a value or a default were specified", key));
      }

      return result;
    }

    /**
     * Shorthand method that reads the value of the specified key, using the provided {@link
     * Converter}.
     *
     * <p>This method does not allow defining any advanced meta-data (e.g., <code>default values
     * </code>, <code>required</code>, etc. It also does not perform any validations. For those
     * use-cases, please see {@link #value()}.
     *
     * @return a value if found, or <code>null</code>
     */
    @Nullable
    public T valueOf(String key, Converter<T> converter) {
      return resolveByKey(key, converter, null);
    }

    /**
     * Shorthand method that reads the value of the specified key, and returns a String value.
     *
     * <p>This method does not allow defining any advanced meta-data (e.g., <code>default values
     * </code>, <code>required</code>, etc. It also does not perform any validations. For those
     * use-cases, please see {@link #value()}.
     *
     * @return a value if found, or <code>null</code>
     */
    @Nullable
    public String valueOf(String key) {
      return resolveByKey(key, Cast.asString(), null);
    }
  }
}
