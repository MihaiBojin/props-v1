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

# BEGIN java dependencies
load("@rules_jvm_external//:specs.bzl", "maven")

maven_install(
    artifacts = [
        maven.artifact(
            group = "com.google.code.findbugs",
            artifact = "jsr305",
            version = "3.0.2",
            neverlink = True,
        ),
        maven.artifact(
            group = "com.google.errorprone",
            artifact = "error_prone_annotations",
            version = "2.3.4",
            neverlink = True,
        ),
        maven.artifact(
            group = "org.hamcrest",
            artifact = "hamcrest-library",
            version = "2.2",
            testonly = True,
        ),
        maven.artifact(
            group = "org.hamcrest",
            artifact = "hamcrest-core",
            version = "2.2",
            testonly = True,
        ),
        maven.artifact(
            group = "org.hamcrest",
            artifact = "hamcrest",
            version = "2.2",
            testonly = True,
        ),
        maven.artifact(
            group = "org.mockito",
            artifact = "mockito-core",
            version = "3.3.3",
            testonly = True,
        ),
        maven.artifact(
            group = "org.junit.jupiter",
            artifact = "junit-jupiter-api",
            version = "5.6.2",
            testonly = True,
        ),
        maven.artifact(
            group = "org.junit.jupiter",
            artifact = "junit-jupiter-params",
            version = "5.6.2",
            testonly = True,
        ),
        maven.artifact(
            group = "org.junit.jupiter",
            artifact = "junit-jupiter-engine",
            version = "5.6.2",
            testonly = True,
        ),
        maven.artifact(
            group = "org.junit.platform",
            artifact = "junit-platform-console",
            version = "1.6.2",
            testonly = True,
        ),
        maven.artifact(
            group = "com.uber.nullaway",
            artifact = "nullaway",
            version = "0.7.10",
        ),
        maven.artifact(
            group = "com.google.guava",
            artifact = "guava",
            version = "22.0",
        ),
    ],
    fetch_sources = True,
    strict_visibility = True,
    repositories = [
        "https://repo1.maven.org/maven2",
    ],
)
# END java dependencies

## BEGIN checkstyle
http_file(
    name = "checkstylejar",
    sha256 = "5a46440e980a378d73e76c50ca554cd0c38480ac33040adf16d131d7e16d50a1",
    urls = [
        "https://github.com/checkstyle/checkstyle/releases/download/checkstyle-8.32/checkstyle-8.32-all.jar",
    ],
)
## END checkstyle

local_repository(
    name = "com_github_mihaibojin_bazel_java_rules",
    path = "/Users/mihaibojin/git/bazel_java_rules",
)
#git_repository(
#    name = "com_github_mihaibojin_bazel_java_rules",
#    remote = "https://github.com/MihaiBojin/bazel_java_rules",
#    commit = "8b133bf904776e3d40fb6a49000ddd7e134880c8",
#)
load("@com_github_mihaibojin_bazel_java_rules//google-java-format:workspace.bzl", "google_java_format_workspace")
google_java_format_workspace()

load("@com_github_mihaibojin_bazel_java_rules//checkstyle:workspace.bzl", "checkstyle_workspace")
checkstyle_workspace()

load("@com_github_mihaibojin_bazel_java_rules//nullaway:workspace.bzl", "nullaway_workspace")
nullaway_workspace()

load("@com_github_mihaibojin_bazel_java_rules//junit5:workspace.bzl", "junit5_workspace")
junit5_workspace()

load("@com_github_mihaibojin_bazel_java_rules//errorprone:workspace.bzl", "errorprone_workspace")
errorprone_workspace()
