FROM docker.io/library/debian:10

RUN export DEBIAN_FRONTEND=noninteractive && \
    apt-get update && \
    apt-get dist-upgrade -y && \
    apt-get install --no-install-recommends -y \
            ant \
            ant-optional \
            ca-certificates \
            git \
            junit \
            libjna-java \
            libvirt-dev \
            locales \
            openjdk-11-jdk-headless && \
    apt-get autoremove -y && \
    apt-get autoclean -y && \
    sed -Ei 's,^# (en_US\.UTF-8 .*)$,\1,' /etc/locale.gen && \
    dpkg-reconfigure locales

ENV LANG "en_US.UTF-8"
