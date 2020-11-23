FROM registry.centos.org/centos:8

RUN dnf install 'dnf-command(config-manager)' -y && \
    dnf config-manager --set-enabled -y PowerTools && \
    dnf install -y centos-release-advanced-virtualization && \
    dnf install -y epel-release && \
    dnf update -y && \
    dnf install -y \
        ant \
        ca-certificates \
        git \
        glibc-langpack-en \
        java-11-openjdk-headless \
        jna \
        libvirt-devel \
        rpm-build && \
    dnf autoremove -y && \
    dnf clean all -y

ENV LANG "en_US.UTF-8"
