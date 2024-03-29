#!/bin/sh
set -e

if [ "$#" -lt 3 ]; then
    echo "Usage: assertj-test.sh XMLUNIT_VERSION ASSERTJ_VERSION (only-assertj|only-assertj3|both)"
    exit 1
fi

XMLUNIT_VERSION=$1
ASSERTJ_VERSION=$2
if [ "$3" != "only-assertj" -a "$3" != "only-assertj3" -a "$3" != "both" ]; then
    echo "Usage: assertj-test.sh XMLUNIT_VERSION ASSERTJ_VERSION (only-assertj|only-assertj3|both)"
    exit 1
fi
RUN_MODULES=$3

if [ "$RUN_MODULES" != "only-assertj3" ]; then
    SCRATCH_DIR=scratch/assertj-${ASSERTJ_VERSION}

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
  <artifactId>xmlunit-compat-tests-assertj-${ASSERTJ_VERSION}</artifactId>
  <packaging>jar</packaging>
  <name>org.xmlunit:xmlunit-compat-tests-assertj-${ASSERTJ_VERSION}</name>
  <description>Verifies the AssertJ assertions are compatible with AssertJ ${ASSERTJ_VERSION}</description>
  <url>https://www.xmlunit.org/</url>

  <properties>
    <automatic.module.name>\${project.groupId}.compat-tests-assertj-${ASSERTJ_VERSION}</automatic.module.name>
    <assertj.version>${ASSERTJ_VERSION}</assertj.version>
    <maven.compile.source>1.7</maven.compile.source>
    <maven.compile.target>1.7</maven.compile.target>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.xmlunit</groupId>
      <artifactId>xmlunit-core</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.xmlunit</groupId>
      <artifactId>xmlunit-assertj</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.assertj</groupId>
      <artifactId>assertj-core</artifactId>
      <version>\${assertj.version}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>net.bytebuddy</groupId>
      <artifactId>byte-buddy</artifactId>
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

    cp -r ../xmlunit-assertj/src/test/java/org/xmlunit/assertj ${SCRATCH_DIR}/src/test/java/org/xmlunit

    mvn -f ${SCRATCH_DIR}/pom.xml test
fi

if [ "$RUN_MODULES" != "only-assertj" ]; then
    SCRATCH_DIR=scratch/assertj3-${ASSERTJ_VERSION}

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
  <artifactId>xmlunit-compat-tests-assertj3-${ASSERTJ_VERSION}</artifactId>
  <packaging>jar</packaging>
  <name>org.xmlunit:xmlunit-compat-tests-assertj3-${ASSERTJ_VERSION}</name>
  <description>Verifies the AssertJ 3.x assertions are compatible with AssertJ ${ASSERTJ_VERSION}</description>
  <url>https://www.xmlunit.org/</url>

  <properties>
    <automatic.module.name>\${project.groupId}.compat-tests-assertj3-${ASSERTJ_VERSION}</automatic.module.name>
    <assertj.version>${ASSERTJ_VERSION}</assertj.version>
    <maven.compile.source>8</maven.compile.source>
    <maven.compile.target>8</maven.compile.target>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.xmlunit</groupId>
      <artifactId>xmlunit-core</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.xmlunit</groupId>
      <artifactId>xmlunit-assertj3</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.assertj</groupId>
      <artifactId>assertj-core</artifactId>
      <version>\${assertj.version}</version>
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

    cp -r ../xmlunit-assertj3/src/test/java/org/xmlunit/assertj3 ${SCRATCH_DIR}/src/test/java/org/xmlunit

    mvn -f ${SCRATCH_DIR}/pom.xml test
fi
