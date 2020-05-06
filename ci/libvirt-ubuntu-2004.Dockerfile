FROM ubuntu:20.04

RUN export DEBIAN_FRONTEND=noninteractive && \
    apt-get update && \
    apt-get dist-upgrade -y && \
    apt-get install --no-install-recommends -y \
            ant \
            ant-optional \
            autoconf \
            automake \
            autopoint \
            bash \
            bash-completion \
            ca-certificates \
            ccache \
            chrony \
            gcc \
            gdb \
            gettext \
            git \
            junit \
            libc-dev-bin \
            libc6-dev \
            libglib2.0-dev \
            libgnutls28-dev \
            libjna-java \
            libnl-3-dev \
            libnl-route-3-dev \
            libtirpc-dev \
            libtool \
            libtool-bin \
            libvirt-dev \
            libxml2-dev \
            libxml2-utils \
            locales \
            lsof \
            make \
            meson \
            net-tools \
            ninja-build \
            openjdk-11-jdk-headless \
            patch \
            perl \
            pkgconf \
            python3 \
            python3-docutils \
            python3-setuptools \
            python3-wheel \
            screen \
            strace \
            sudo \
            vim \
            xsltproc && \
    apt-get autoremove -y && \
    apt-get autoclean -y && \
    sed -Ei 's,^# (en_US\.UTF-8 .*)$,\1,' /etc/locale.gen && \
    dpkg-reconfigure locales && \
    mkdir -p /usr/libexec/ccache-wrappers && \
    ln -s /usr/bin/ccache /usr/libexec/ccache-wrappers/cc && \
    ln -s /usr/bin/ccache /usr/libexec/ccache-wrappers/$(basename /usr/bin/gcc)

ENV LANG "en_US.UTF-8"

ENV MAKE "/usr/bin/make"
ENV NINJA "/usr/bin/ninja"
ENV PYTHON "/usr/bin/python3"

ENV CCACHE_WRAPPERSDIR "/usr/libexec/ccache-wrappers"
