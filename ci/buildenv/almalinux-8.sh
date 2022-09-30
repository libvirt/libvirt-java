# THIS FILE WAS AUTO-GENERATED
#
#  $ lcitool manifest ci/manifest.yml
#
# https://gitlab.com/libvirt/libvirt-ci

function install_buildenv() {
    dnf update -y
    dnf install 'dnf-command(config-manager)' -y
    dnf config-manager --set-enabled -y powertools
    dnf install -y centos-release-advanced-virtualization
    dnf install -y epel-release
    dnf install -y \
        ant \
        ca-certificates \
        git \
        glibc-langpack-en \
        java-11-openjdk-headless \
        jna \
        libvirt-devel \
        rpm-build
    rpm -qa | sort > /packages.txt
}

export LANG="en_US.UTF-8"
