#!/bin/sh
set -eu

if [ "$#" -lt 2 ]; then
    echo "Usage: jaxb-test.sh XMLUNIT_VERSION (javax|jakarta)"
    exit 1
fi

XMLUNIT_VERSION=$1
if [ "$2" != "javax" -a "$2" != "jakarta" ]; then
    echo "Usage: jaxb-test.sh XMLUNIT_VERSION (javax|jakarta)"
    exit 1
fi
PACKAGE_ROOT=$2
if [ "$PACKAGE_ROOT" = "javax" ]; then
    JAVA_VERSION=1.7
else
    JAVA_VERSION=8
fi

SCRATCH_DIR=scratch/jaxb-${PACKAGE_ROOT}

rm -rf scratch && mkdir -p ${SCRATCH_DIR}/src/test/java/org/xmlunit/ \
    && mkdir -p ${SCRATCH_DIR}/src/main/java/org/xmlunit/

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
  <artifactId>xmlunit-compat-tests-jaxb-${PACKAGE_ROOT}</artifactId>
  <packaging>jar</packaging>
  <name>org.xmlunit:xmlunit-compat-tests-jaxb-${PACKAGE_ROOT}</name>
  <description>Verifies the JAXB works for ${PACKAGE_ROOT}.xml.bind</description>
  <url>https://www.xmlunit.org/</url>

  <properties>
    <automatic.module.name>\${project.groupId}.compat-tests-jaxb-${PACKAGE_ROOT}</automatic.module.name>
    <maven.compile.source>${JAVA_VERSION}</maven.compile.source>
    <maven.compile.target>${JAVA_VERSION}</maven.compile.target>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.xmlunit</groupId>
      <artifactId>xmlunit-core</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <scope>test</scope>
    </dependency>
EOF


if [ "$PACKAGE_ROOT" = "javax" ]; then
    cat >> ${SCRATCH_DIR}/pom.xml <<EOF
  </dependencies>

  <profiles>
    <profile>
      <id>java11+</id>
      <activation>
        <jdk>[9,)</jdk>
      </activation>
      <dependencies>
        <dependency>
          <groupId>jakarta.xml.bind</groupId>
          <artifactId>jakarta.xml.bind-api</artifactId>
          <version>2.3.3</version>
        </dependency>
        <dependency>
          <groupId>org.glassfish.jaxb</groupId>
          <artifactId>jaxb-runtime</artifactId>
          <scope>runtime</scope>
          <optional>true</optional>
          <version>2.3.3</version>
        </dependency>
      </dependencies>
    </profile>
  </profiles>
</project>
EOF
else
    cat >> ${SCRATCH_DIR}/pom.xml <<EOF
    <dependency>
      <groupId>org.xmlunit</groupId>
      <artifactId>xmlunit-jakarta-jaxb-impl</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>jakarta.xml.bind</groupId>
      <artifactId>jakarta.xml.bind-api</artifactId>
      <version>3.0.1</version>
    </dependency>
    <dependency>
      <groupId>org.glassfish.jaxb</groupId>
      <artifactId>jaxb-runtime</artifactId>
      <scope>runtime</scope>
      <optional>true</optional>
      <version>3.0.1</version>
    </dependency>
  </dependencies>
</project>
EOF
fi

cat > ${SCRATCH_DIR}/src/main/java/org/xmlunit/Foo.java <<EOF
package org.xmlunit;

import ${PACKAGE_ROOT}.xml.bind.annotation.XmlAttribute;
import ${PACKAGE_ROOT}.xml.bind.annotation.XmlElement;
import ${PACKAGE_ROOT}.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Foo {
    private int attribute;
    private String element;

    public Foo() {}

    public Foo(int attribute, String element) {
        this.attribute = attribute;
        this.element = element;
    }

    @XmlAttribute
    public int getAttribute() {
        return attribute;
    }
    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }
    @XmlElement
    public String getelement() {
    return element;
    }
    public void setelement(String name) {
        this.element = element;
    }
}
EOF

cp JaxbTest.java ${SCRATCH_DIR}/src/test/java/org/xmlunit
mvn -f ${SCRATCH_DIR}/pom.xml test
