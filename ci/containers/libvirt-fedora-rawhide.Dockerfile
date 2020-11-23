FROM registry.fedoraproject.org/fedora:rawhide

RUN dnf update -y --nogpgcheck fedora-gpg-keys && \
    dnf update -y && \
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
