FROM registry.opensuse.org/opensuse/leap:15.1

RUN zypper update -y && \
    zypper install -y \
           ant \
           ant-junit \
           bash \
           bash-completion \
           ca-certificates \
           ccache \
           gcc \
           gettext \
           git \
           glibc-devel \
           glibc-locale \
           java-11-openjdk-headless \
           jna \
           junit \
           libvirt-devel \
           patch \
           perl \
           perl-App-cpanminus \
           pkgconfig \
           python3 \
           python3-pip \
           python3-setuptools \
           python3-wheel \
           rpm-build && \
    zypper clean --all && \
    mkdir -p /usr/libexec/ccache-wrappers && \
    ln -s /usr/bin/ccache /usr/libexec/ccache-wrappers/cc && \
    ln -s /usr/bin/ccache /usr/libexec/ccache-wrappers/$(basename /usr/bin/gcc)

ENV LANG "en_US.UTF-8"

ENV MAKE "/usr/bin/make"
ENV NINJA "/usr/bin/ninja"
ENV PYTHON "/usr/bin/python3"

ENV CCACHE_WRAPPERSDIR "/usr/libexec/ccache-wrappers"
