# THIS FILE WAS AUTO-GENERATED
#
#  $ lcitool dockerfile opensuse-152 libvirt+dist,libvirt-java
#
# https://gitlab.com/libvirt/libvirt-ci/-/commit/d527e0c012f476c293f3bc801b7da08bc85f98ef
FROM registry.opensuse.org/opensuse/leap:15.2

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
