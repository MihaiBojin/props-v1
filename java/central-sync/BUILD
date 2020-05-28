load("@graknlabs_bazel_distribution//maven/templates:rules.bzl", "assemble_maven", "deploy_maven")
load("@rules_jvm_external//:defs.bzl", "artifact")
load("//contributors:contributors.bzl", "contributors")

assemble_maven(
    name = "assemble-maven",
    developers = contributors(),
    license = "apache",
    package = "{maven_packages}",
    project_description = "Props - Multi-layered properties and secrets management library",
    project_name = "Props",
    project_url = "https://props.MihaiBojin.com",
    scm_url = "https://github.com/MihaiBojin/props.git",
    target = "//java/core/src/main:props-core",
    version_file = "VERSION",
)

deploy_maven(
    name = "deploy-maven",
    deployment_properties = "//java/central-sync:deployment.properties",
    target = ":assemble-maven",
)
