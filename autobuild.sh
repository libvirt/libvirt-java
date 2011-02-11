#!/bin/sh

set -e
set -v

test -n "$1" && RESULTS=$1 || RESULTS=results.log

ant clean || :

ant build docs

ant test 2>&1 | tee $RESULTS

rm -f *.tar.gz
ant src

if [ -n "$AUTOBUILD_COUNTER" ]; then
  EXTRA_RELEASE=".auto$AUTOBUILD_COUNTER"
else
  NOW=`date +"%s"`
  EXTRA_RELEASE=".$USER$NOW"
fi

if [ -x /usr/bin/rpmbuild ]
then
  ant spec
  rpmbuild --nodeps \
     --define "extra_release $EXTRA_RELEASE" \
     --define "_sourcedir `pwd`/target" \
     -ba --clean target/libvirt-java.spec
fi
