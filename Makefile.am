SUBDIRS=src doc

EXTRA_DIST = \
  libvirt-java.pc.in \
  libvirt-java.spec.in \
  libvirt-java.spec \
  autogen.sh \
  INSTALL

pkgconfigdir = $(libdir)/pkgconfig
pkgconfig_DATA = libvirt-java.pc

cleantar:
	@(rm -f libxml*.tar.gz COPYING)

rpm: clean cleantar
	@(unset CDPATH ; $(MAKE) dist && rpmbuild -ta $(distdir).tar.gz)
