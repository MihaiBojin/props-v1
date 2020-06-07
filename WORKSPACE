# BEGIN https://github.com/bazelbuild/rules_jvm_external
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive", "http_file")

RULES_JVM_EXTERNAL_TAG = "3.2"

RULES_JVM_EXTERNAL_SHA = "82262ff4223c5fda6fb7ff8bd63db8131b51b413d26eb49e3131037e79e324af"

http_archive(
    name = "rules_jvm_external",
    sha256 = RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:defs.bzl", "maven_install")
# END https://github.com/bazelbuild/rules_jvm_external

# START https://github.com/bazelbuild/bazel-skylib
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
    name = "bazel_skylib",
    sha256 = "97e70364e9249702246c0e9444bccdc4b847bed1eb03c5a3ece4f83dfe6abc44",
    urls = [
        "https://mirror.bazel.build/github.com/bazelbuild/bazel-skylib/releases/download/1.0.2/bazel-skylib-1.0.2.tar.gz",
        "https://github.com/bazelbuild/bazel-skylib/releases/download/1.0.2/bazel-skylib-1.0.2.tar.gz",
    ],
)

load("@bazel_skylib//:workspace.bzl", "bazel_skylib_workspace")

bazel_skylib_workspace()
# END https://github.com/bazelbuild/bazel-skylib

# BEGIN bazel distribution
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

git_repository(
    name = "graknlabs_bazel_distribution",
    remote = "https://github.com/MihaiBojin/bazel-distribution",
    commit = "7e0ebb3db1f92d76f38c73b118ed1dde9ec32918",
)
# END bazel distribution

#local_repository(
#    name = "com_github_mihaibojin_bazel_java_rules",
#    path = "/Users/mihaibojin/git/bazel_java_rules",
#)
git_repository(
    name = "com_github_mihaibojin_bazel_java_rules",
    remote = "https://github.com/MihaiBojin/bazel_java_rules",
    commit = "514e244e7fc3bcca4ad2b14334ea39c496f710a3",
)
load("@com_github_mihaibojin_bazel_java_rules//google-java-format:workspace.bzl", "google_java_format_jar")
google_java_format_jar()

load("@com_github_mihaibojin_bazel_java_rules//checkstyle:workspace.bzl", "checkstyle_jar")
checkstyle_jar()

load("@com_github_mihaibojin_bazel_java_rules//errorprone:workspace.bzl", "errorprone_workspace")
load("@com_github_mihaibojin_bazel_java_rules//nullaway:workspace.bzl", "nullaway_workspace")
load("@com_github_mihaibojin_bazel_java_rules//junit5:workspace.bzl", "junit5_workspace")

# BEGIN java dependencies
load("@rules_jvm_external//:specs.bzl", "maven")
maven_install(
    artifacts =
        errorprone_workspace() +
        nullaway_workspace() +
        junit5_workspace(),
    fetch_sources = True,
    strict_visibility = True,
    repositories = [
        "https://repo1.maven.org/maven2",
    ],
)
# END java dependencies
