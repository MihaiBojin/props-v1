PKGNAME=props

# Determine the OS
KERNEL=$(shell uname -s)
ifeq (Darwin,$(KERNEL))
	OS_NAME=osx
else ifeq (Linux,$(KERNEL))
	OS_NAME=linux64
else
	OS_NAME=unsupported
endif

INFER_VERSION=0.17.0
INFER=lib/infer-$(OS_NAME)-v$(INFER_VERSION)

.DEFAULT_GOAL := build

# Determine the current commit's git hash and identify any version tags
GITHASH := $(shell git log -n1 --pretty='%H')
VERSION_TAG := $(shell git describe --exact-match --tags "$(GITHASH)" 2>/dev/null)

.PHONY: clean
clean: jabba
	@echo "==> Cleaning project artifacts and metadata"
	bazelisk clean
	rm -rf com/ infer-out/ module-info.class docs/javadoc

.PHONY: build
build: jabba
	@echo "==> Building $(PKGNAME)"
	bazelisk build //java-props-core/...

test: jabba
	bazelisk test //java-props-core/...

.PHONY: fmt
fmt:
	@echo ""
	@echo "==> Formatting Bazel build files..."
	buildifier $(shell find . -type f \( -iname BUILD -or -iname BUILD.bazel \))

	@echo ""
	@echo "==> Formatting JAVA files..."
	bazelisk run //props-java-core/src/main:google-java-format
	bazelisk run //props-java-benchmark/src/main:google-java-format

.PHONY: fmtcheck
fmtcheck:
	@echo ""
	@echo "==> Ensuring that the JAVA code is properly formatted..."
	bazelisk run //props-java-core/src/main:google-java-format-check
	bazelisk run //props-java-benchmark/src/main:google-java-format-check

#	TODO(mihaibojin): Re-enable once the segfault is fixed
#	@echo ""
#	@echo "==> Ensuring all Bazel build files are properly formatted..."
#	buildifier --lint=warn $(shell find . -type f \( -iname BUILD -or -iname BUILD.bazel \))

.PHONY: benchmark
benchmark:
	@echo ""
	@echo "==> Running benchmarks..."
	bazelisk run //java-props-benchmark/src/main:jmh

.PHONY: vet
vet:
	@echo ""
	@echo "==> Running Checkstyle..."
	bazelisk build //java-props-core/src/main:checkstyle

	@echo ""
	@echo "==> Running fbinfer..."
	$(INFER)/bin/infer run -- javac $(shell find ./java-props-core/src/main/java/ -name '*.java')

.PHONY: generate-pom-version
generate-pom-version:
ifeq (, $(VERSION_TAG))
	$(error "Could not find a tag for commit hash: $(GITHASH)")
endif
	@echo ""
	@echo "==> Updating the release version..."
	echo "$(VERSION_TAG:v%=%)" > release/VERSION

.PHONY: assemble-maven
assemble-maven: jabba
ifeq (0.0.0,$(shell cat release/VERSION))
	$(error "Before running this target, make sure to generate a VERSION file with the _generate-pom-version_ target")
endif
	@echo ""
	@echo "==> Assembling JAR artifacts for publishing to Maven Central..."
	bazelisk build //java-props-core/src/main:assemble-maven

.PHONY: deploy-maven
deploy-maven: assemble-maven
ifeq (, ${DEPLOY_MAVEN_USERNAME})
	$(error "Cannot assemble a JAR without a value for DEPLOY_MAVEN_USERNAME")
endif
ifeq (, ${DEPLOY_MAVEN_PASSWORD})
	$(error "Cannot assemble a JAR without a value for DEPLOY_MAVEN_PASSWORD")
endif
	@echo ""
	@echo "==> Deploying JAR artifacts to Maven Central"
	bazelisk run //java-props-core/src/main:deploy-maven -- release --gpg

.PHONY: javadoc
BASEDIR:=$(shell pwd)
TMPDIR := $(shell mktemp -d)
javadoc:
	@echo ""
	@echo "==> Updating the project's JavaDocs"
	@bazelisk build //java-props-core/src/main:assemble-maven

	@echo "==> Unpacking javadocs"
	cp bazel-bin/java-props-core/src/main/com.mihaibojin.props:props-core-javadoc.jar $(TMPDIR)/javadoc.jar
	cd $(TMPDIR) && jar xf javadoc.jar
	rm -f $(TMPDIR)/javadoc.jar
	rm -rf $(BASEDIR)/docs/javadoc
	mv $(TMPDIR) $(BASEDIR)/docs/javadoc

.PHONY: git-hooks
git-hooks:
	@echo ""
	@echo "==> Ensuring all git hooks are linked..."
	find .git/hooks -type l -exec rm {} \;
	find .githooks -type f -exec ln -sf ../../{} .git/hooks/ \;

.PHONY: setup
setup: git-hooks
ifeq (, $(shell which bazelisk))

ifeq (, $(shell which go))
	$(error "Bazelisk is not installed and golang is not available")
endif

	@echo ""
	@echo "==> Installing bazelisk..."
	go get github.com/bazelbuild/bazelisk

ifeq (, $(shell which bazelisk))
	$(error "Please add '$(shell go env GOPATH)/bin' to your current PATH")
endif

endif

ifeq (, $(shell which buildifier))
	@echo ""
	@echo "==> Installing buildifier..."
	go get github.com/bazelbuild/buildtools/buildifier
endif

ifeq (,$(wildcard ~/.jabba/jabba.sh))
	@echo ""
	@echo "==> Installing jabba..."
	curl -sL https://github.com/shyiko/jabba/raw/master/install.sh | bash && . ~/.jabba/jabba.sh
	@echo ""
	@echo "Don't forget to: source ~/.jabba/jabba.sh"
	@echo ""
endif

	@echo ""
	@echo "==> Installing fbinfer..."
ifeq (osx, $(OS_NAME))
# MacOS
ifeq (, $(shell which infer))
# Can't use the GitHub version directly, due to https://github.com/facebook/infer/issues/1081
	brew install infer
	@echo "==> Linking the executable..."
	mkdir -p $(INFER)/bin
	ln -sf $(shell which infer) $(INFER)/bin/infer
endif

else ifeq (linux64, $(OS_NAME))
# Linux
ifeq (, $(wildcard $(INFER)/bin/infer))
	mkdir -p lib
	curl -sSfL -o $(INFER).tar.xz "https://github.com/facebook/infer/releases/download/v$(INFER_VERSION)/infer-$(OS_NAME)-v$(INFER_VERSION).tar.xz"
	tar -C lib/ -xJf $(INFER).tar.xz
endif

else
# Unsupported
	$(error Cannot install infer on $(KERNEL))
endif

.PHONY: jabba
jabba:
	@echo "==> This project uses jabba for selecting a JAVA version"
	@echo "==> Before running this command, run:"
	@echo "source ~/.jabba/jabba.sh && jabba use"
