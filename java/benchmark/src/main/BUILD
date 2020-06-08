load("@rules_jmh//:defs.bzl", "jmh_java_benchmarks")

jmh_java_benchmarks(
    name = "jmh",
    srcs = glob(["java/benchmark/**/*.java"]),
    deps = ["//java/core/src/main:props-core"],
    resources = glob(["resources/**"]),
)
