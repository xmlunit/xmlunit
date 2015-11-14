#!/bin/sh

set -e

if [ $# -lt 1 ]; then 
    echo "usage $0 Release-Version"
    exit 1
fi

mkdir -p target/bindist-tmp/xmlunit-$1
cp README.md LICENSE target/bindist-tmp/xmlunit-$1
cp */target/*.jar target/bindist-tmp/xmlunit-$1
cp -r target/site/apidocs target/bindist-tmp/xmlunit-$1
cd target/bindist-tmp
zip -r xmlunit-$1-bin.zip xmlunit-$1
tar cf xmlunit-$1-bin.tar xmlunit-$1
gzip -k xmlunit-$1-bin.tar
bzip2 xmlunit-$1-bin.tar
mv xmlunit-$1-bin.* ..

cd ..
for i in *.zip *.tar.gz *.tar.bz2; do
    md5sum $i > $i.md5
    sha1sum $i > $i.sha1
    sha256sum $i > $i.sha256
    gpg --detach-sign --armor $i
done
