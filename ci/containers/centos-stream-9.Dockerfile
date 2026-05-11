# THIS FILE WAS AUTO-GENERATED
#
#  $ lcitool manifest ci/manifest.yml
#
# https://gitlab.com/libvirt/libvirt-ci

FROM quay.io/centos/centos:stream9

RUN dnf --quiet distro-sync -y && \
    dnf --quiet install 'dnf-command(config-manager)' -y && \
    dnf --quiet config-manager --set-enabled -y crb && \
    dnf --quiet install -y epel-release && \
    dnf --quiet install -y epel-next-release && \
    dnf --quiet install -y \
                ant \
                ant-junit \
                ca-certificates \
                git \
                glibc-langpack-en \
                java-25-openjdk-headless \
                jna \
                junit \
                libvirt-devel \
                rpm-build && \
    dnf --quiet autoremove -y && \
    dnf --quiet clean all -y && \
    rpm -qa | sort > /packages.txt

ENV LANG="en_US.UTF-8"
