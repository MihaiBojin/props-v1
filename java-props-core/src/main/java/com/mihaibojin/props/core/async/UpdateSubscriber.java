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

package com.mihaibojin.props.core.async;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class UpdateSubscriber<T> implements Subscriber<T> {
  private static final Logger log = Logger.getLogger(UpdateSubscriber.class.getName());

  private final Consumer<T> consumer;
  private final Consumer<Throwable> errConsumer;
  private Flow.Subscription subscription;

  @SuppressWarnings("NullAway")
  public UpdateSubscriber(Consumer<T> consumer, Consumer<Throwable> errConsumer) {
    this.consumer = consumer;
    this.errConsumer = errConsumer;
  }

  @Override
  public void onSubscribe(Subscription subscription) {
    this.subscription = subscription;
    subscription.request(1);
  }

  @Override
  public void onNext(T item) {
    consumer.accept(item);
    subscription.request(1);
  }

  @Override
  public void onError(Throwable throwable) {
    errConsumer.accept(throwable);
  }

  @Override
  public void onComplete() {
    log.info("No more items.");
  }
}
