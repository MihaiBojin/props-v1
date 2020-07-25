module com.mihaibojin.props.core {
  requires java.base;
  requires java.logging;

  exports com.mihaibojin.props.core;
  exports com.mihaibojin.props.core.converters;
  exports com.mihaibojin.props.core.resolvers;
  exports com.mihaibojin.props.core.types;
}
