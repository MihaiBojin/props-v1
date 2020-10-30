load("@rules_java//java:defs.bzl", "java_library")
load("@com_github_mihaibojin_bazel_java_rules//nullaway:rules.bzl", "nullaway_plugin")
load("@com_github_mihaibojin_bazel_java_rules//checkstyle:rules.bzl", "checkstyle")
load("@com_github_mihaibojin_bazel_java_rules//google-java-format:rules.bzl", "java_format")
load("@graknlabs_bazel_distribution//maven/templates:rules.bzl", "assemble_maven", "deploy_maven")
load("//contributors:contributors.bzl", "contributors")

java_library(
    name = "props-core",
    srcs = glob(["java/**/*.java"]),
    javacopts = [
        "-Xep:NullAway:ERROR",
        "-XepOpt:NullAway:AnnotatedPackages=com.mihaibojin",
    ],
    plugins = ["nullaway"],
    resources = glob(["resources/**"]),
    tags = ["maven_coordinates=com.mihaibojin.props:props-core:{pom_version}"],
    visibility = [
        "//java-props-benchmark/src/main:__subpackages__",
        "//java-props-core/src/test:__subpackages__",
    ],
)

nullaway_plugin(name = "nullaway")

checkstyle(
    name = "checkstyle",
    srcs = glob(["java/**/*.java"]),
    checkstyle_suppressions = "//third-party/checkstyle:checkstyle-suppressions.xml",
    checkstyle_xml = "//third-party/checkstyle:checkstyle.xml",
    checkstyle_xpath_suppressions = "//third-party/checkstyle:checkstyle-xpath-suppressions.xml",
    license_header = "//third-party/checkstyle:license-header.txt",
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

assemble_maven(
    name = "assemble-maven",
    developers = contributors(),
    license = "apache",
    package = "{maven_packages}",
    project_description = "Props - Multi-layered properties and secrets management library",
    project_name = "Props",
    project_url = "https://props.MihaiBojin.com",
    scm_url = "https://github.com/MihaiBojin/props.git",
    target = "//java-props-core/src/main:props-core",
    version_file = "//release:VERSION",
    visibility = ["//visibility:public"],
)

deploy_maven(
    name = "deploy-maven",
    deployment_properties = "//release:deployment.properties",
    tags = ["no-ide"],
    target = ":assemble-maven",
)
