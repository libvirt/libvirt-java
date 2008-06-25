JAVA_CLASS_ROOT=$(top_srcdir)/src

SOURCES = \
  org_libvirt_VirNetwork.c \
  org_libvirt_VirConnect.c \
  org_libvirt_VirDomain.c \
  VirErrorHandler.c \
  VirErrorHandler.h

GENERATED = \
  org_libvirt_VirConnect.h \
  org_libvirt_VirNetwork.h \
  org_libvirt_VirDomain.h

BUILT_SOURCES = $(GENERATED)

org_libvirt_VirConnect.h : $(JAVA_CLASS_ROOT)/org/libvirt/VirConnect.class
	$(JAVAH) -classpath $(JAVA_CLASS_ROOT) org.libvirt.VirConnect

org_libvirt_VirNetwork.h: $(JAVA_CLASS_ROOT)/org/libvirt/VirNetwork.class
	$(JAVAH) -classpath $(JAVA_CLASS_ROOT) org.libvirt.VirNetwork

org_libvirt_VirDomain.h: $(JAVA_CLASS_ROOT)/org/libvirt/VirDomain.class
	$(JAVAH) -classpath $(JAVA_CLASS_ROOT) org.libvirt.VirDomain

lib_LTLIBRARIES = libvirt_jni.la
libvirt_jni_la_SOURCES = $(SOURCES)
nodist_libvirt_jni_la_SOURCES = $(GENERATED)
libvirt_jni_la_LIBADD = $(LIBVIRT_LIBS)
libvirt_jni_la_LDFLAGS = -version-info  @JNI_VERSION_INFO@


CLEANFILES = \
  $(GENERATED)
