# THIS FILE WAS AUTO-GENERATED
#
#  $ lcitool dockerfile opensuse-151 libvirt+dist,libvirt-java
#
# https://gitlab.com/libvirt/libvirt-ci/-/commit/050edf1c67395e5723e40dc547f73cdf44f1d8cf
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
    zypper clean --all && \
    rpm -qa | sort > /packages.txt

ENV LANG "en_US.UTF-8"
