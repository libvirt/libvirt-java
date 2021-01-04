# THIS FILE WAS AUTO-GENERATED
#
#  $ lcitool dockerfile centos-7 libvirt+dist,libvirt-java
#
# https://gitlab.com/libvirt/libvirt-ci/-/commit/050edf1c67395e5723e40dc547f73cdf44f1d8cf
FROM registry.centos.org/centos:7

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
