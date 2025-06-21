#!/bin/sh
set -e

if [ "$#" -ne 2 ]; then
    echo "Usage: hamcrest3-test.sh XMLUNIT_VERSION HAMCREST_VERSION"
    exit 1
fi

XMLUNIT_VERSION=$1
HAMCREST_VERSION=$2

SCRATCH_DIR=scratch/hamcrest-${HAMCREST_VERSION}

rm -rf scratch && mkdir -p ${SCRATCH_DIR}/src/test/java/org/xmlunit/

cat > ${SCRATCH_DIR}/pom.xml <<EOF
<?xml version="1.0"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>org.xmlunit</groupId>
    <artifactId>xmlunit-parent</artifactId>
    <version>${XMLUNIT_VERSION}</version>
    <relativePath>../../..</relativePath>
  </parent>

  <groupId>org.xmlunit</groupId>
  <artifactId>xmlunit-compat-tests-hamcrest-${HAMCREST_VERSION}</artifactId>
  <packaging>jar</packaging>
  <name>org.xmlunit:xmlunit-compat-tests-hamcrest-${HAMCREST_VERSION}</name>
  <description>Verifies the Hamcrest Matchers are compatible with Hamcrest ${HAMCREST_VERSION}</description>
  <url>https://www.xmlunit.org/</url>

  <properties>
    <automatic.module.name>\${project.groupId}.compat-tests-hamcrest-${HAMCREST_VERSION}</automatic.module.name>
    <hamcrest.version>${HAMCREST_VERSION}</hamcrest.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.xmlunit</groupId>
      <artifactId>xmlunit-core</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.xmlunit</groupId>
      <artifactId>xmlunit-matchers</artifactId>
      <exclusions>
        <exclusion>
          <groupId>org.hamcrest</groupId>
          <artifactId>hamcrest-core</artifactId>
        </exclusion>
        <exclusion>
          <groupId>org.hamcrest</groupId>
          <artifactId>hamcrest-library</artifactId>
        </exclusion>
      </exclusions>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.hamcrest</groupId>
      <artifactId>hamcrest</artifactId>
      <version>\${hamcrest.version}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-core</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>
</project>
EOF

cp TestResources.java ${SCRATCH_DIR}/src/test/java/org/xmlunit/

cp -r ../xmlunit-matchers/src/test/java/org/xmlunit/bugreports ${SCRATCH_DIR}/src/test/java/org/xmlunit
cp -r ../xmlunit-matchers/src/test/java/org/xmlunit/matchers ${SCRATCH_DIR}/src/test/java/org/xmlunit

mvn -f ${SCRATCH_DIR}/pom.xml test
