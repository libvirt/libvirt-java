# Build must be done in org first
SUBDIRS=. jni

java_libvirt_source_files = \
  org/libvirt/LibvirtException.java \
  org/libvirt/VirConnect.java \
  org/libvirt/VirDomainBlockStats.java \
  org/libvirt/VirDomainInfo.java \
  org/libvirt/VirDomainInterfaceStats.java \
  org/libvirt/VirDomain.java \
  org/libvirt/VirErrorException.java \
  org/libvirt/VirError.java \
  org/libvirt/VirNetwork.java \
  org/libvirt/VirNodeInfo.java \
  org/libvirt/VirSchedBooleanParameter.java \
  org/libvirt/VirSchedDoubleParameter.java \
  org/libvirt/VirSchedIntParameter.java \
  org/libvirt/VirSchedLongParameter.java \
  org/libvirt/VirSchedParameter.java \
  org/libvirt/VirSchedUintParameter.java \
  org/libvirt/VirSchedUlongParameter.java \
  org/libvirt/VirVcpuInfo.java

EXTRA_DIST= \
  test.java \
  $(java_libvirt_source_files)

libvirt_jardir = $(INSTALLED_JAR_DIR)
libvirt_jar_DATA = libvirt-${VERSION}.jar

libvirt_jar_class_files = $(java_libvirt_source_files:.java=.class)
$(libvirt_jar_class_files): %.class: %.java
	$(JAVAC) -classpath "org/libvirt" org/libvirt/*.java

all_jar_class_files = $(shell find org/libvirt -name '*.class' | sed 's%\$$%\\$$%g')

libvirt-${VERSION}.jar: $(libvirt_jar_class_files)
	$(JAR) cf $@  $(patsubst org/libvirt/%.class,-C org/libvirt %.class,$(all_jar_class_files))

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

