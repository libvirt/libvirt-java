# THIS FILE WAS AUTO-GENERATED
#
#  $ lcitool manifest ci/manifest.yml
#
# https://gitlab.com/libvirt/libvirt-ci

FROM registry.fedoraproject.org/fedora:43

RUN dnf --quiet install -y nosync && \
    printf '#!/bin/sh\n\
if test -d /usr/lib64\n\
then\n\
    export LD_PRELOAD=/usr/lib64/nosync/nosync.so\n\
else\n\
    export LD_PRELOAD=/usr/lib/nosync/nosync.so\n\
fi\n\
exec "$@"\n' > /usr/bin/nosync && \
    chmod +x /usr/bin/nosync && \
    nosync dnf --quiet update -y && \
    nosync dnf --quiet install -y \
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
    nosync dnf --quiet autoremove -y && \
    nosync dnf --quiet clean all -y && \
    rpm -qa | sort > /packages.txt

ENV LANG="en_US.UTF-8"
