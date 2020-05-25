load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "props-core",
    srcs = glob(["java/**/*.java"]),
    resources = glob(["resources/**"]),
    tags = ["maven_coordinates=com.mihaibojin.props:props-core:{pom_version}"],
    visibility = ["//java/core/src/test:__subpackages__", "//java/central-sync:__subpackages__"],
)

load("//java/third_party/javadoc:javadoc.bzl", "javadoc")
javadoc(
    name = "props-core-javadoc",
    srcs = glob(["java/**/*.java"]),
    group_id = "com.mihaibojin.props",
    artifact_id = "props-core",
)
