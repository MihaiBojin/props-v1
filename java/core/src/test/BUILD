load("@rules_jvm_external//:defs.bzl", "artifact")

## https://docs.bazel.build/versions/master/test-encyclopedia.html
## https://docs.bazel.build/versions/master/be/java.html#java_test

# For more information, see https://github.com/junit-team/junit5-samples/tree/master/junit5-jupiter-starter-bazel
#load("//java/third_party/junit5:junit5.bzl", "java_junit5_test")

load("@com_github_mihaibojin_bazel_java_rules//junit5:rules.bzl", "junit5_test")

junit5_test(
    name = "AllTests",
    srcs = glob(["**/*.java"]),
    resources = glob(["resources/**"]),
    test_package = "com.mihaibojin",
    deps = [
        "//java/core/src/main:props-core",
    ],
)

junit5_test(
    name = "Examples",
    srcs = glob(["**/*.java"]),
    resources = glob(["resources/**"]),
    test_package = "examples",
    deps = [
        "//java/core/src/main:props-core",
    ],
)
