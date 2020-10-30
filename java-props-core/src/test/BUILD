load("@com_github_mihaibojin_bazel_java_rules//junit5:rules.bzl", "junit5_test")

junit5_test(
    name = "AllTests",
    srcs = glob(["**/*.java"]),
    resources = glob(["resources/**"]),
    test_package = "com.mihaibojin.props",
    deps = [
        "//java-props-core/src/main:props-core",
    ],
)

junit5_test(
    name = "Examples",
    srcs = glob(["**/*.java"]),
    resources = glob(["resources/**"]),
    test_package = "examples",
    deps = [
        "//java-props-core/src/main:props-core",
    ],
)
