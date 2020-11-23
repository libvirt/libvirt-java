FROM registry.opensuse.org/opensuse/leap:15.1

RUN zypper update -y && \
    zypper install -y \
           ant \
           ant-junit \
           ca-certificates \
           git \
           glibc-locale \
           java-11-openjdk-headless \
           jna \
           junit \
           libvirt-devel \
           rpm-build && \
    zypper clean --all

ENV LANG "en_US.UTF-8"
