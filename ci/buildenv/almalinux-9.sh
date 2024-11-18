# THIS FILE WAS AUTO-GENERATED
#
#  $ lcitool manifest ci/manifest.yml
#
# https://gitlab.com/libvirt/libvirt-ci

function install_buildenv() {
    dnf update -y
    dnf install 'dnf-command(config-manager)' -y
    dnf config-manager --set-enabled -y crb
    dnf install -y epel-release
    dnf install -y \
        ant \
        ant-junit \
        ca-certificates \
        git \
        glibc-langpack-en \
        java-21-openjdk-headless \
        jna \
        junit \
        libvirt-devel \
        rpm-build
    rpm -qa | sort > /packages.txt
}

export LANG="en_US.UTF-8"
