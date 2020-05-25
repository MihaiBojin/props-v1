load("@graknlabs_bazel_distribution//maven/templates:rules.bzl", "assemble_maven", "deploy_maven")
load("@rules_jvm_external//:defs.bzl", "artifact")
load("//contributors:contributors.bzl", "contributors")

assemble_maven(
    name = "assemble-maven",
    target = "//java/core/src/main:props-core",
    package = "{maven_packages}",
    project_name = "Props",
    project_description = "Props - Multi-layered properties and secrets management library",
    project_url = "https://props.MihaiBojin.com",
    scm_url = "https://github.com/MihaiBojin/props.git",
    version_file = "VERSION",
    developers = contributors(),
    license = "apache"
)

deploy_maven(
    name = "deploy-maven",
    target = ":assemble-maven",
    deployment_properties = "//java/central-sync:deployment.properties"
)
