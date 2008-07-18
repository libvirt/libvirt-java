GENERATED = \
  org_libvirt_Connect.h \
  org_libvirt_Network.h \
  org_libvirt_Domain.h \
  org_libvirt_Domain_CreateFlags.h \
  org_libvirt_Domain_MigrateFlags.h \
  org_libvirt_Domain_XMLFlags.h

BUILT_SOURCES = $(GENERATED)

JAVA_CLASS_ROOT=$(top_srcdir)/src

org_libvirt_Connect.h : $(JAVA_CLASS_ROOT)/org/libvirt/Connect.class
	$(JAVAH) -classpath $(JAVA_CLASS_ROOT) org.libvirt.Connect

org_libvirt_Network.h: $(JAVA_CLASS_ROOT)/org/libvirt/Network.class
	$(JAVAH) -classpath $(JAVA_CLASS_ROOT) org.libvirt.Network

org_libvirt_Domain.h org_libvirt_Domain_CreateFlags.h org_libvirt_Domain_MigrateFlags.h org_libvirt_Domain_XMLFlags.h : $(JAVA_CLASS_ROOT)/org/libvirt/Domain.class
	$(JAVAH) -classpath $(JAVA_CLASS_ROOT) org.libvirt.Domain

lib_LTLIBRARIES = libvirt_jni.la
libvirt_jni_la_SOURCES = \
  org_libvirt_Network.c \
  org_libvirt_Connect.c \
  org_libvirt_Domain.c \
  ErrorHandler.c \
  ErrorHandler.h \
  ConnectAuthCallbackBridge.c \
  ConnectAuthCallbackBridge.h
   
nodist_libvirt_jni_la_SOURCES = $(GENERATED)
libvirt_jni_la_LIBADD = $(LIBVIRT_LIBS)
libvirt_jni_la_LDFLAGS = -version-info  @JNI_VERSION_INFO@
libvirt_jni_la_CFLAGS = @JNI_CFLAGS@


CLEANFILES = \
  $(GENERATED) $(GENERATED_SUB_1)
