# THIS FILE WAS AUTO-GENERATED
#
#  $ lcitool dockerfile centos-7 libvirt+dist,libvirt-java
#
# https://gitlab.com/libvirt/libvirt-ci/-/commit/d527e0c012f476c293f3bc801b7da08bc85f98ef
FROM docker.io/library/centos:7

RUN yum update -y && \
    echo 'skip_missing_names_on_install=0' >> /etc/yum.conf && \
    yum install -y epel-release && \
    yum install -y \
        ant \
        ant-junit \
        ca-certificates \
        git \
        glibc-common \
        java-11-openjdk-headless \
        jna \
        junit \
        libvirt-devel \
        rpm-build && \
    yum autoremove -y && \
    yum clean all -y && \
    rpm -qa | sort > /packages.txt

ENV LANG "en_US.UTF-8"
