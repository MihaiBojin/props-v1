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
        "//java/benchmark/src/main:__subpackages__",
    ],
)

load("@com_github_mihaibojin_bazel_java_rules//nullaway:rules.bzl", "nullaway_plugin")

nullaway_plugin(name = "nullaway")

load("@graknlabs_bazel_distribution//maven/templates:rules.bzl", "javadoc")

javadoc(
    name = "props-javadoc",
    target = ":props-core",
)

load("@com_github_mihaibojin_bazel_java_rules//checkstyle:rules.bzl", "checkstyle")

checkstyle(
    name = "checkstyle",
    srcs = glob(["java/**/*.java"]),
    checkstyle_xml = "//java/third_party/checkstyle:checkstyle.xml",
    checkstyle_suppressions = "//java/third_party/checkstyle:checkstyle-suppressions.xml",
    checkstyle_xpath_suppressions = "//java/third_party/checkstyle:checkstyle-xpath-suppressions.xml",
    license_header = "//java/third_party/checkstyle:license-header.txt",
)
