load("@rules_jmh//:defs.bzl", "jmh_java_benchmarks")

jmh_java_benchmarks(
    name = "jmh",
    srcs = glob(["java/**/*.java"]),
    resources = glob(["resources/**"]),
    deps = ["//java-props-core/src/main:props-core"],
)
