FROM registry.fedoraproject.org/fedora:33

RUN dnf update -y && \
    dnf install -y \
        ant \
        ant-junit \
        ca-certificates \
        git \
        glibc-langpack-en \
        java-11-openjdk-headless \
        jna \
        junit \
        libvirt-devel \
        rpm-build && \
    dnf autoremove -y && \
    dnf clean all -y

ENV LANG "en_US.UTF-8"
