PKGNAME=props

ifeq (Darwin, $(shell uname -s))
	OS_NAME=osx
else
	OS_NAME=linux64
endif

INFER_VERSION=0.17.0
INFER=lib/infer-$(OS_NAME)-v$(INFER_VERSION)

default: build

# Determine the current commit's git hash and identify any version tags
GITHASH := $(shell git log -n1 --pretty='%H')
VERSION_TAG := $(shell git describe --exact-match --tags "$(GITHASH)" 2>/dev/null)

.PHONY: clean
clean: jabba
	@echo "==> Cleaning project artifacts and metadata"
	bazelisk clean
	rm -rf com/ infer-out/ module-info.class

.PHONY: build
build: jabba
	@echo "==> Building $(PKGNAME)"
	bazelisk build //java/core/...

test: jabba
	bazelisk test //java/core/...

.PHONY: fmt
fmt:
	@echo ""
	@echo "==> Formatting Bazel build files..."
	buildifier $(shell find . -type f \( -iname BUILD -or -iname BUILD.bazel \))

	@echo ""
	@echo "==> Formatting JAVA files..."
	bazelisk run //java:google-java-format

.PHONY: fmtcheck
fmtcheck:
	@echo ""
	@echo "==> Ensuring tht the JAVA code is properly formatted..."
	bazelisk build //java:google-java-format-check

#	TODO(mihaibojin): Re-enable once the segfault is fixed
#	@echo ""
#	@echo "==> Ensuring all Bazel build files are properly formatted..."
#	buildifier --lint=warn $(shell find . -type f \( -iname BUILD -or -iname BUILD.bazel \))

.PHONY: benchmark
benchmark:
	@echo ""
	@echo "==> Running benchmarks..."
	bazelisk run //java/benchmark/src/main:jmh

.PHONY: vet
vet:
	@echo ""
	@echo "==> Running Checkstyle..."
	bazelisk build //java/core/src/main:checkstyle

	@echo ""
	@echo "==> Running fbinfer..."
	$(INFER)/bin/infer run -- javac $(shell find ./java/core/src/main/java/ -name '*.java')

.PHONY: generate-pom-version
generate-pom-version:
ifeq (, $(VERSION_TAG))
	$(error "Could not find a tag for commit hash: $(GITHASH)")
endif
	@echo "$(VERSION_TAG:v%=%)" > java/central-sync/VERSION

.PHONY: assemble-maven
assemble-maven: jabba
ifeq (0.0.0,$(shell cat java/central-sync/VERSION))
	$(error "Before running this target, make sure to generate a VERSION file with the _generate-pom-version_ target")
endif
	@echo ""
	@echo "==> Assembling JAR artifacts for publishing to Maven Central..."
	bazelisk build //java/central-sync:assemble-maven

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
	bazelisk run //java/central-sync:deploy-maven -- release --gpg

.PHONY: javadoc
BASEDIR:=$(shell pwd)
TMPDIR := $(shell mktemp -d)
javadoc:
	@echo ""
	@echo "==> Updating the project's JavaDocs"
	@bazelisk build //java/central-sync:assemble-maven

	@echo "==> Unpacking javadocs"
	cp bazel-bin/java/central-sync/com.mihaibojin.props:props-core-javadoc.jar $(TMPDIR)/javadoc.jar
	cd $(TMPDIR) && jar xf javadoc.jar
	mv $(TMPDIR)/props-core/* $(BASEDIR)/docs/javadoc/
	cp $(BASEDIR)/docs/javadoc_stylesheet.css $(BASEDIR)/docs/javadoc/stylesheet.css
	rm -rf $(TMPDIR)

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

else
# Linux
ifeq (, $(wildcard $(INFER)/bin/infer))
	mkdir -p lib
	curl -sSfL -o $(INFER).tar.xz "https://github.com/facebook/infer/releases/download/v$(INFER_VERSION)/infer-$(OS_NAME)-v$(INFER_VERSION).tar.xz"
	tar -C lib/ -xJf $(INFER).tar.xz
endif

endif

.PHONY: jabba
jabba:
	@echo "==> This project uses jabba for selecting a JAVA version"
	@echo "==> Before running this command, run:"
	@echo "source ~/.jabba/jabba.sh && jabba use"

