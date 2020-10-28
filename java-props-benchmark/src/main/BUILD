load("@rules_jmh//:defs.bzl", "jmh_java_benchmarks")
load("@com_github_mihaibojin_bazel_java_rules//google-java-format:rules.bzl", "java_format")

jmh_java_benchmarks(
    name = "jmh",
    srcs = glob(["java/**/*.java"]),
    resources = glob(["resources/**"]),
    deps = ["//java-props-core/src/main:props-core"],
)

java_format(
    name = "google-java-format-check",
    srcs = glob(["java/**/*.java"]),
    dry_run = True,
    tags = ["no-ide"],
)

java_format(
    name = "google-java-format",
    srcs = glob(["java/**/*.java"]),
    dry_run = False,
    replace = True,
    set_exit_if_changed = False,
    tags = ["no-ide"],
)

java_binary(
    name = "PubSubMain",
    srcs = glob(["java/**/*.java"]),
    resources = glob(["resources/**"]),
    deps = [
        "//java-props-core/src/main:props-core",
        "@rules_jmh_maven//:org_openjdk_jmh_jmh_core",
    ],
    #jvm_flags = ["-agentpath:/Applications/YourKit-Java-Profiler-2020.9.app/Contents/Resources/bin/mac/libyjpagent.dylib"],
)