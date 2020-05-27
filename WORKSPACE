# BEGIN https://github.com/bazelbuild/rules_jvm_external
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive", "http_file")

RULES_JVM_EXTERNAL_TAG = "3.2"
RULES_JVM_EXTERNAL_SHA = "82262ff4223c5fda6fb7ff8bd63db8131b51b413d26eb49e3131037e79e324af"

http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    sha256 = RULES_JVM_EXTERNAL_SHA,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:defs.bzl", "maven_install")
# END https://github.com/bazelbuild/rules_jvm_external

# START bazel_skylib
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
http_archive(
    name = "bazel_skylib",
    urls = [
        "https://mirror.bazel.build/github.com/bazelbuild/bazel-skylib/releases/download/1.0.2/bazel-skylib-1.0.2.tar.gz",
        "https://github.com/bazelbuild/bazel-skylib/releases/download/1.0.2/bazel-skylib-1.0.2.tar.gz",
    ],
    sha256 = "97e70364e9249702246c0e9444bccdc4b847bed1eb03c5a3ece4f83dfe6abc44",
)
load("@bazel_skylib//:workspace.bzl", "bazel_skylib_workspace")
bazel_skylib_workspace()
# END bazel_skylib

# BEGIN bazel distribution
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")
git_repository(
    name = "graknlabs_bazel_distribution",
    remote = "https://github.com/graknlabs/bazel-distribution",
    commit = "8d7f8c92bc9973c90d6e040bb7e69288470165ae"
)
# END bazel distribution

# BEGIN java dependencies
load("@rules_jvm_external//:specs.bzl", "maven")
maven_install(
    fetch_sources = True,
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
    repositories = [
        "https://repo1.maven.org/maven2",
    ],
)
# END java dependencies

# BEGIN checkstyle
http_file(
    name = "checkstyle",
    urls = [
        "https://github.com/checkstyle/checkstyle/releases/download/checkstyle-8.32/checkstyle-8.32-all.jar",
    ],
    sha256 = "5a46440e980a378d73e76c50ca554cd0c38480ac33040adf16d131d7e16d50a1",
)
# END checkstyle
