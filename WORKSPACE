# BEGIN https://github.com/bazelbuild/rules_jvm_external
RULES_JVM_EXTERNAL_TAG = "3.2"
RULES_JVM_EXTERNAL_SHA = "82262ff4223c5fda6fb7ff8bd63db8131b51b413d26eb49e3131037e79e324af"

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive", "http_file")
http_archive(
    name = "rules_jvm_external",
    sha256 = RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)
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

# BEGIN https://github.com/graknlabs/bazel-distribution
#local_repository(
#    name = "graknlabs_bazel_distribution",
#    path = "/Users/mihaibojin/git/bazel-distribution",
#)
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")
git_repository(
    name = "graknlabs_bazel_distribution",
    remote = "https://github.com/MihaiBojin/bazel-distribution",
    commit = "d63b2fe5be6f24ae2423713495234144f8e6b15d",
)
# END https://github.com/graknlabs/bazel-distribution

# BEGIN https://github.com/MihaiBojin/bazel_java_rules
#local_repository(
#    name = "com_github_mihaibojin_bazel_java_rules",
#    path = "/Users/mihaibojin/git/bazel_java_rules",
#)
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")
git_repository(
    name = "com_github_mihaibojin_bazel_java_rules",
    remote = "https://github.com/MihaiBojin/bazel_java_rules",
    commit = "e76e1bc54c9e646a6495d4c43ad870296ab15427",
)

load("@com_github_mihaibojin_bazel_java_rules//google-java-format:workspace.bzl", "google_java_format_jar")
google_java_format_jar()

load("@com_github_mihaibojin_bazel_java_rules//checkstyle:workspace.bzl", "checkstyle_jar")
checkstyle_jar()

load("@com_github_mihaibojin_bazel_java_rules//errorprone:workspace.bzl", "errorprone_workspace")
load("@com_github_mihaibojin_bazel_java_rules//nullaway:workspace.bzl", "nullaway_workspace")
load("@com_github_mihaibojin_bazel_java_rules//junit5:workspace.bzl", "junit5_workspace")

# BEGIN java dependencies
load("@rules_jvm_external//:defs.bzl", "maven_install")
maven_install(
    artifacts =
        errorprone_workspace() +
        nullaway_workspace() +
        junit5_workspace() +
        [
            "org.openjdk.jmh:jmh-core:1.23",
            "org.openjdk.jmh:jmh-generator-annprocess:1.23",
        ],
    fetch_sources = True,
    strict_visibility = True,
    repositories = [
        "https://repo1.maven.org/maven2",
    ],
)
# END https://github.com/MihaiBojin/bazel_java_rules

# BEGIN https://github.com/buchgr/rules_jmh
#local_repository(
#    name = "rules_jmh",
#    path = "/Users/mihaibojin/git/rules_jmh",
#)
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
http_archive(
  name = "rules_jmh",
  strip_prefix = "MihaiBojin-rules_jmh-f3b9bd0",
  url = "https://github.com/MihaiBojin/rules_jmh/zipball/f3b9bd0435d6ab645f0a81cfcd365dc6fb65f820",
  type = "zip",
  sha256 = "d87a34ed7d17910bacebcaf2786fee8a284d5f640ef59e750975d59433e79f00",
)

load("@rules_jmh//:deps.bzl", "rules_jmh_deps")
rules_jmh_deps()

load("@rules_jmh//:defs.bzl", "rules_jmh_maven_deps")
rules_jmh_maven_deps(fetch_sources = True)
# END https://github.com/buchgr/rules_jmh
