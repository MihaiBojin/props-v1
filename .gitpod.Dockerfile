FROM gitpod/workspace-full

# Install custom tools, runtime, etc.
RUN sudo apt-get update \
    && sudo apt-get install -y \
        curl \
        unzip \
        zip \
    && sudo apt-get clean \
    && sudo rm -rf /var/cache/apt/* \
    && sudo rm -rf /var/lib/apt/lists/* \
    && sudo rm -rf /tmp/*

# Install SDKman
RUN curl -s "https://get.sdkman.io?rcupdate=false" | bash
RUN chmod a+x "$HOME/.sdkman/bin/sdkman-init.sh"

# Install the JDK
RUN . "$HOME/.sdkman/bin/sdkman-init.sh" && sdk env
