#!/bin/sh

# This file is licensed to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# zip up binary distributions

set -e

if [ $# -lt 1 ]; then 
    echo "usage $0 Release-Version"
    exit 1
fi

mkdir -p target/bindist-tmp/xmlunit-$1
cp README.md LICENSE RELEASE_NOTES.md target/bindist-tmp/xmlunit-$1
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
