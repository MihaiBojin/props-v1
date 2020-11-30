FROM gitpod/workspace-full

# Install custom tools, runtime, etc.
RUN sudo apt-get update \
    && sudo apt-get install -y \
        curl \
        python \
        unzip \
        zip \
    && sudo apt-get clean \
    && sudo rm -rf /var/cache/apt/* \
    && sudo rm -rf /var/lib/apt/lists/* \
    && sudo rm -rf /tmp/* \
    && sudo rm /bin/sh \
    && sudo ln -s /bin/bash /bin/sh

ENV PATH="/workspace/go/bin:${PATH}"
