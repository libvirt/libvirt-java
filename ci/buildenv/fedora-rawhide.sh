# THIS FILE WAS AUTO-GENERATED
#
#  $ lcitool manifest ci/manifest.yml
#
# https://gitlab.com/libvirt/libvirt-ci

function install_buildenv() {
    dnf update -y --nogpgcheck fedora-gpg-keys
    dnf distro-sync -y
    dnf install -y \
        ant \
        ant-junit \
        ca-certificates \
        git \
        glibc-langpack-en \
        java-11-openjdk-headless \
        jna \
        junit \
        libvirt-devel \
        rpm-build
    rpm -qa | sort > /packages.txt
}

export LANG="en_US.UTF-8"
