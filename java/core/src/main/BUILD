load("@rules_jvm_external//:defs.bzl", "artifact")

load("@rules_java//java:defs.bzl", "java_library")
java_library(
    name = "props-core",
    srcs = glob(["java/**/*.java"]),
    plugins=['nullaway'],
    javacopts=[
        '-Xep:NullAway:ERROR',
        '-XepOpt:NullAway:AnnotatedPackages=com.mihaibojin',
    ],
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

java_plugin(
    name='nullaway',
    deps=[
        artifact("com.uber.nullaway:nullaway"),
        artifact("com.google.guava:guava"),
    ],
)

genrule(
    name = "checkstyle",
    srcs = glob(["java/**/*.java"]),
    tools = [
        "@checkstyle//file",
        "//java/third_party/checkstyle:checkstyle.xml",
        "//java/third_party/checkstyle:checkstyle-suppressions.xml",
        "//java/third_party/checkstyle:checkstyle-xpath-suppressions.xml",
        "//java/third_party/checkstyle:license-header.txt",
    ],
    cmd = " ".join([
        "java -classpath $(location @checkstyle//file)",
        "com.puppycrawl.tools.checkstyle.Main",
        "-c $(location //java/third_party/checkstyle:checkstyle.xml)",
        "--",
        "$(SRCS)",
        "| tee \"$@\"",
    ]),
    outs = ["checkstyle.log"],
)
