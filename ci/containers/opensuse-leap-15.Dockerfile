# THIS FILE WAS AUTO-GENERATED
#
#  $ lcitool manifest ci/manifest.yml
#
# https://gitlab.com/libvirt/libvirt-ci

FROM registry.opensuse.org/opensuse/leap:15.6

RUN zypper update -y && \
    zypper addrepo -fc https://download.opensuse.org/update/leap/15.6/backports/openSUSE:Backports:SLE-15-SP6:Update.repo && \
    zypper install -y \
           ant \
           ant-junit \
           ca-certificates \
           git \
           glibc-locale \
           java-21-openjdk-headless \
           jna \
           junit \
           libvirt-devel \
           rpm-build && \
    zypper clean --all && \
    rpm -qa | sort > /packages.txt

ENV LANG "en_US.UTF-8"
