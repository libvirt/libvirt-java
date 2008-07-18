# Build must be done in org first
SUBDIRS=. jni

java_libvirt_source_files = \
  org/libvirt/LibvirtException.java \
  org/libvirt/Connect.java \
  org/libvirt/ConnectAuthDefault.java \
  org/libvirt/ConnectAuth.java \
  org/libvirt/DomainBlockStats.java \
  org/libvirt/DomainInfo.java \
  org/libvirt/DomainInterfaceStats.java \
  org/libvirt/Domain.java \
  org/libvirt/ErrorException.java \
  org/libvirt/Error.java \
  org/libvirt/Network.java \
  org/libvirt/NodeInfo.java \
  org/libvirt/SchedBooleanParameter.java \
  org/libvirt/SchedDoubleParameter.java \
  org/libvirt/SchedIntParameter.java \
  org/libvirt/SchedLongParameter.java \
  org/libvirt/SchedParameter.java \
  org/libvirt/SchedUintParameter.java \
  org/libvirt/SchedUlongParameter.java \
  org/libvirt/VcpuInfo.java

EXTRA_DIST= \
  test.java \
  $(java_libvirt_source_files)

libvirt_jardir = $(INSTALLED_JAR_DIR)
libvirt_jar_DATA = libvirt-${VERSION}.jar

libvirt_jar_class_files = $(java_libvirt_source_files:.java=.class)
$(libvirt_jar_class_files): %.class: %.java
	$(JAVAC) $(JAVAC_FLAGS) -classpath "org/libvirt" org/libvirt/*.java

all_jar_class_files = $(shell find org/libvirt -name '*.class' | sed 's%\$$%\\$$%g')

libvirt-${VERSION}.jar: $(libvirt_jar_class_files)
	$(JAR) cf $@  $(all_jar_class_files)

# running the javadoc command is better done here since we have
# the authoritative list of java files

doc-stamps: $(java_libvirt_source_files)
	$(JAVADOC) $(JAVADOC_FLAGS) -d $(top_srcdir)/doc/api \
	    -sourcepath $(srcdir) \
	    -windowtitle "Libvirt java $(VERSION) API Reference" \
	    -doctitle "Libvirt java $(VERSION) API Reference" \
	    org.libvirt
	touch doc-stamps

docs: doc-stamps

install-data-hook:

CLEANFILES = \
  org/libvirt/*.class \
  libvirt-${VERSION}.jar \
  doc-stamps

