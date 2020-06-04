load("@rules_jvm_external//:defs.bzl", "artifact")
load("@rules_java//java:defs.bzl", "java_library")

java_library(
    name = "props-core",
    srcs = glob(["java/**/*.java"]),
    javacopts = [
        "-Xep:NullAway:ERROR",
        "-XepOpt:NullAway:AnnotatedPackages=com.mihaibojin",
    ],
    plugins = ["nullaway"],
    resources = glob(["resources/**"]),
    tags = ["maven_coordinates=com.mihaibojin.props:props-core:{pom_version}"],
    visibility = [
        "//java/central-sync:__subpackages__",
        "//java/core/src/test:__subpackages__",
    ],
)

java_plugin(
    name = "nullaway",
    deps = [
        artifact("com.uber.nullaway:nullaway"),
        artifact("com.google.guava:guava"),
    ],
)

genrule(
    name = "checkstyle",
    srcs = glob(["java/**/*.java"]),
    outs = ["checkstyle.log"],
    cmd = " ".join([
        "java -classpath $(location @checkstyle//file)",
        "com.puppycrawl.tools.checkstyle.Main",
        "-c $(location //java/third_party/checkstyle:checkstyle.xml)",
        "--",
        "$(SRCS)",
        "| tee \"$@\"",
    ]),
    tools = [
        "//java/third_party/checkstyle:checkstyle.xml",
        "//java/third_party/checkstyle:checkstyle-suppressions.xml",
        "//java/third_party/checkstyle:checkstyle-xpath-suppressions.xml",
        "//java/third_party/checkstyle:license-header.txt",
        "@checkstyle//file",
    ],
)

load("@graknlabs_bazel_distribution//maven/templates:rules.bzl", "javadoc")

javadoc(
    name = "props-javadoc",
    target = ":props-core",
)
