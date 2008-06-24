
docdir = $(datadir)/doc/libvirt-java-$(VERSION)
javadocdir = $(datadir)/javadoc/libvirt-java-$(VERSION)

doc_DATA = $(top_srcdir)/AUTHORS $(top_srcdir)/COPYING.LIB \
  $(top_srcdir)/NEWS $(top_srcdir)/README

all-local: docs

docs:
	@(cd ../src && $(MAKE) docs)

all_dest_javadoc_html_files = $(shell (find api/ -type f | sed 's+api/++'))
dest_javadoc_dirs = $(shell (find api -mindepth 1 -type d | sed 's+api/++'))

install-data-hook:
	$(mkinstalldirs) $(DESTDIR)$(docdir)
	@for dir in $(dest_javadoc_dirs); do \
	 $(mkinstalldirs) $(DESTDIR)$(javadocdir)/$${dir}; done
	@for file in $(all_dest_javadoc_html_files); do \
	 $(INSTALL_DATA) api/$${file} $(DESTDIR)$(javadocdir)/$${file}; done

CLEANFILES = \
  api/resources/inherit.gif \
  api/package-list \
  api/stylesheet.css \
  $(shell find api -name '*.html') \
  ../src/doc-stamps

