load("@com_github_mihaibojin_bazel_java_rules//google-java-format:rules.bzl", "java_format")

java_format(
    name = "google-java-format-check",
    dry_run = True,
)

java_format(
    name = "google-java-format",
    dry_run = False,
    replace = True,
    set_exit_if_changed = False,
)
