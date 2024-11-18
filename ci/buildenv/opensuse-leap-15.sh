# THIS FILE WAS AUTO-GENERATED
#
#  $ lcitool manifest ci/manifest.yml
#
# https://gitlab.com/libvirt/libvirt-ci

function install_buildenv() {
    zypper update -y
    zypper addrepo -fc https://download.opensuse.org/update/leap/15.6/backports/openSUSE:Backports:SLE-15-SP6:Update.repo
    zypper install -y \
           ant \
           ant-junit \
           ca-certificates \
           git \
           glibc-locale \
           java-21-openjdk-headless \
           jna \
           junit \
           libvirt-devel \
           rpm-build
    rpm -qa | sort > /packages.txt
}

export LANG="en_US.UTF-8"
