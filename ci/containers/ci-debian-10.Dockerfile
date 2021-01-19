# THIS FILE WAS AUTO-GENERATED
#
#  $ lcitool dockerfile debian-10 libvirt+dist,libvirt-java
#
# https://gitlab.com/libvirt/libvirt-ci/-/commit/d527e0c012f476c293f3bc801b7da08bc85f98ef
FROM docker.io/library/debian:10-slim

RUN export DEBIAN_FRONTEND=noninteractive && \
    apt-get update && \
    apt-get install -y eatmydata && \
    eatmydata apt-get dist-upgrade -y && \
    mkdir -p /usr/share/man/man1 && \
    eatmydata apt-get install --no-install-recommends -y \
            ant \
            ant-optional \
            ca-certificates \
            git \
            junit \
            libjna-java \
            libvirt-dev \
            locales \
            openjdk-11-jdk-headless && \
    eatmydata apt-get autoremove -y && \
    eatmydata apt-get autoclean -y && \
    sed -Ei 's,^# (en_US\.UTF-8 .*)$,\1,' /etc/locale.gen && \
    dpkg-reconfigure locales && \
    dpkg-query --showformat '${Package}_${Version}_${Architecture}\n' --show > /packages.txt

ENV LANG "en_US.UTF-8"
