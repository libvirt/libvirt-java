# THIS FILE WAS AUTO-GENERATED
#
#  $ lcitool manifest ci/manifest.yml
#
# https://gitlab.com/libvirt/libvirt-ci

function install_buildenv() {
    dnf --quiet update -y
    dnf --quiet install 'dnf-command(config-manager)' -y
    dnf --quiet config-manager --set-enabled -y crb
    dnf --quiet install -y epel-release
    dnf --quiet install almalinux-release-devel -y
    dnf --quiet config-manager --set-enabled -y devel
    dnf --quiet install -y \
                ant \
                ant-junit \
                ca-certificates \
                git \
                glibc-langpack-en \
                java-25-openjdk-headless \
                jna \
                junit \
                libvirt-devel \
                rpm-build
    rpm -qa | sort > /packages.txt
}

export LANG="en_US.UTF-8"
