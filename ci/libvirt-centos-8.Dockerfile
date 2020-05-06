FROM centos:8

RUN dnf install 'dnf-command(config-manager)' -y && \
    dnf config-manager --set-enabled PowerTools -y && \
    dnf install -y epel-release && \
    dnf update -y && \
    dnf install -y \
        ant \
        autoconf \
        automake \
        bash \
        bash-completion \
        ca-certificates \
        ccache \
        chrony \
        gcc \
        gdb \
        gettext \
        gettext-devel \
        git \
        glibc-devel \
        glibc-langpack-en \
        java-11-openjdk-headless \
        jna \
        libtool \
        libvirt-devel \
        lsof \
        make \
        meson \
        net-tools \
        ninja-build \
        patch \
        perl \
        pkgconfig \
        python3 \
        python3-setuptools \
        python3-wheel \
        rpm-build \
        screen \
        strace \
        sudo \
        vim && \
    dnf autoremove -y && \
    dnf clean all -y && \
    mkdir -p /usr/libexec/ccache-wrappers && \
    ln -s /usr/bin/ccache /usr/libexec/ccache-wrappers/cc && \
    ln -s /usr/bin/ccache /usr/libexec/ccache-wrappers/$(basename /usr/bin/gcc)

ENV LANG "en_US.UTF-8"

ENV MAKE "/usr/bin/make"
ENV NINJA "/usr/bin/ninja"
ENV PYTHON "/usr/bin/python3"

ENV CCACHE_WRAPPERSDIR "/usr/libexec/ccache-wrappers"
