XMLUnit for Java 2.x
====================

[![Build Status XMLUnit 2.x for Java](https://travis-ci.org/xmlunit/xmlunit.svg?branch=master)](https://travis-ci.org/xmlunit/xmlunit)

XMLUnit is a library that supports testing XML output in several ways.

XMLUnit 2.x is a complete rewrite of XMLUnit and actually doesn't
share any code with XMLUnit for Java 1.x.

Some goals for XMLUnit 2.x:

* create .NET and Java versions that are compatible in design while
  trying to be idiomatic for each platform
* remove all static configuration (the old XMLUnit class setter methods)
* focus on the parts that are useful for testing
  - XPath
  - (Schema) validation
  - comparisons
* be independent of any test framework

We are in the process of finalizing the API for XMLUnit 2.x.  XMLUnit
1.x for Java is still supported and will stay at
[sourceforge](https://sourceforge.net/projects/xmlunit/).

## Help Wanted!

If you are looking for something to work on, we've compiled a
[list](HELP_WANTED.md) of things that should be done before XMLUnit
2.0 can be released.

Please see the [contributing guide](CONTRIBUTING.md) for details on
how to contribute.

## SNAPSHOT builds

There is no released version of XMLUnit for Java 2.x, we'll need to
hash out a few remaining API questions before we can think about
cutting an alpha version.

We are providing SNAPSHOT builds from
[Sonatypes OSS Nexus Repository](https://oss.sonatype.org/content/repositories/snapshots/org/xmlunit/),
you need to add

```xml
<repository>
  <id>snapshots-repo</id>
  <url>https://oss.sonatype.org/content/repositories/snapshots</url>
  <releases><enabled>false</enabled></releases>
  <snapshots><enabled>true</enabled></snapshots>
</repository>
```

to your Maven settings.  The core library is

```xml
<dependency>
  <groupId>org.xmlunit</groupId>
  <artifactId>xmlunit-core</artifactId>
  <version>2.0.0-SNAPSHOT</version>
</dependency>
```

## Examples

These are some really small examples, more is to come in the [user guide](https://github.com/xmlunit/user-guide/wiki)

### Comparing Two Documents

```java
Source control = Input.fromFile("test-data/good.xml").build();
Source test = Input.fromMemory(createTestDocument()).build();
DifferenceEngine diff = new DOMDifferenceEngine();
diff.addDifferenceListener(new ComparisonListener() {
        public void comparisonPerformed(Comparison comparison, ComparisonResult outcome) {
            Assert.fail("found a difference: " + comparison);
        }
    });
diff.compare(control, test);
```

or using the fluent builder API

```java
Diff d = DiffBuilder.compare(Input.fromFile("test-data/good.xml"))
             .withTest(createTestDocument()).build();
assert !d.hasDifferences();
```

### Asserting an XPath Value

```java
Source source = Input.fromMemory("<foo>bar</foo>").build();
XPathEngine xpath = new JAXPXPathEngine();
Iterable<Node> allMatches = xpath.selectNodes("/foo", source);
String content = xpath.evaluate("/foo/text()", source);
```

### Validating a Document Against an XML Schema

```java
Validator v = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
v.setSchemaSources(Input.fromUri("http://example.com/some.xsd").build(),
                   Input.fromFile("local.xsd").build());
ValidationResult result = v.validateInstance(Input.fromDocument(createDocument()).build());
boolean valid = result.isValid();
Iterable<ValidationProblem> problems = result.getProblems();
```

## Requirements

XMLUnit requires Java6.

The `core` library of provides all functionality needed to test XML
output and hasn't got any dependencies.  It uses JUnit 4.x for its own
tests.

The core library is complemented by Hamcrest matchers.  There also
exists a `legacy` project that provides the API of XMLUnit 1.x on top
of the 2.x core library.

## Checking out XMLUnit for Java

XMLUnit for Java uses a git submodule for test resources it shares
with XMLUnit.NET.  You can either clone this repository using `git
clone --recursive` or run `git submodule update --init` after inside
your fresh working copy after cloning normally.

If you have checked out a working copy before we added the submodule,
you'll need to run `git submodule update --init` once.

## Building

XMLUnit for Java builds using Apache Maven 3.x, mainly you want to run

```sh
$ mvn install
```

in order to compile `core`, `matchers` and `legacy` and run the
tests.
