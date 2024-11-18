# THIS FILE WAS AUTO-GENERATED
#
#  $ lcitool manifest ci/manifest.yml
#
# https://gitlab.com/libvirt/libvirt-ci

FROM docker.io/library/almalinux:9

RUN dnf update -y && \
    dnf install 'dnf-command(config-manager)' -y && \
    dnf config-manager --set-enabled -y crb && \
    dnf install -y epel-release && \
    dnf install -y \
        ant \
        ant-junit \
        ca-certificates \
        git \
        glibc-langpack-en \
        java-21-openjdk-headless \
        jna \
        junit \
        libvirt-devel \
        rpm-build && \
    dnf autoremove -y && \
    dnf clean all -y && \
    rpm -qa | sort > /packages.txt

ENV LANG "en_US.UTF-8"
