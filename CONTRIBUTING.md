# Contributing to props

Thanks for your interest in contributing to `props`! 


# Developer set-up


## Prerequisites and Tools 

- [Git](https://git-scm.com/) (usually provided by the OS)
- [Jabba](https://github.com/shyiko/jabba) for Java SDK management
- [Bazel](https://bazel.build/) (we prefer [Bazelisk](https://github.com/bazelbuild/bazelisk))

You can run `make setup` to install *Jabba* and *Bazelisk*.  This target also sets up git hooks,
which runs formatters and static code analysis tools.

Also, you will need a text editor or IDE; we use [IntelliJ](https://www.jetbrains.com/idea/).


## Get the code

- Fork the [repository](https://github.com/MihaiBojin/props)
- Clone your forked repository locally (`git clone https://github.com/[USERNAME]/props`)
- Add this repo as an upstream: `git remote add upstream https://github.com/MihaiBojin/props`


## IntelliJ config

The [Bazel IntelliJ plugin](https://ij.bazel.build) unfortunately doesn't work very well with
[JPMS modules](https://github.com/bazelbuild/intellij/issues/1992).
Until this issue is resolved, the JPMS `module-info.java` module descriptors will be generated
at build time, during the `make assemble-maven` target.


## Building and testing

Before doing anything else, run `jabba use` in the project's root directory.
This will ensure you are running the version of the JDK used (and tested) for this project.

You can build the project with: `make build`.

And you can run the tests with `make test`.

We use the [Google JAVA Formatter](https://github.com/google/google-java-format) to keep the code 
consistent.  You can run `make fmtcheck` to verify that all code is properly formatted.  
This target runs in the pre-commit git hook which is automatically installed when you run 
`make setup` or can be individually installed with `make git-hooks`. 

All Bazel build files are formatted with [buildifier](https://github.com/bazelbuild/buildtools/buildifier).


## Debugging

Certain targets such as `PubSubMain` cannot be debugged directly from IntelliJ.
For such targets, edit the run configuration and add `--debug=5005` to `Executable flags`, then
connect with another `Remote` configuration.

## Profiling

Profiling a Bazel target with tools such as [YourKit](https://www.yourkit.com/) does not directly 
work. Instead, find the corresponding Bazel target and pass any JVM arguments, e.g.:
```
java_binary(
    ...
    jvm_flags = ["-agentpath:/Applications/YourKit-Java-Profiler-2020.9.app/Contents/Resources/bin/mac/libyjpagent.dylib"],
)
```


# Contributing

- Please [create a GitHub issue](https://github.com/MihaiBojin/props/issues/new)
- Then open a Pull Request and tag the newly created issue (e.g., `Fixes #12345`)
- For PRs to be accepted, you must agree to the terms of the [CLA](CLA.md) and to 
  contribute the code under the project's [license](./LICENSE).

To sign the [Contributor License Agreement](./CLA.md) please follow the 
[steps outlined here](./contributors/README.md). 

**Reviewers will ensure that all requirements are met before merging PRs.**


# Releasing

The release process is automated using [GitHub Actions](/.github/workflows/deploy-maven-central.yml).

To stage a release:

A. If you are a collaborator to the [props repo](https://github.com/MihaiBojin/props/):
- create a [lightweight tag](https://git-scm.com/book/en/v2/Git-Basics-Tagging#_lightweight_tags)
  `git tag vx.y.z`
- and ask [me](https://github.com/MihaiBojin) to release it to Maven Central 
  via [Sonatype](https://oss.sonatype.org/#stagingRepositories)

B. Otherwise, [create a new issue](https://github.com/MihaiBojin/props/issues/new) and specify the
git commit hash you would like to release and a version number.

We follow [semantic versioning](https://semver.org/) so please bear that in mind when you propose
a new MAJOR.MINOR.PATCH release version:
- MAJOR version when you make incompatible API changes,
- MINOR version when you add functionality in a backwards compatible manner, and
- PATCH version when you make backwards compatible bug fixes.


# License

Copyright 2020 Mihai Bojin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Please also review the project's [contributor license agreement](CLA.md).
