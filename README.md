XMLUnit 2.x
===========

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

This will be a work in progress for quite some time.  We are in the
process of migrating the - unpublished so far - XMLUnit 2.x from
sourceforge to github.  XMLUnit 1.x for Java and 0.x for .NET will
stay at [sourceforge](https://sourceforge.net/projects/xmlunit/).

## Help Wanted!

If you are looking for something to work on, we've compiled a
[list](HELP_WANTED.md) of things that should be done before XMLUnit
2.0 can be released.

Please see the [contributing guide](CONTRIBUTING.md) for details on
how to contribute.

## Examples

These are some really small examples, more is to come in the [user guide](https://github.com/xmlunit/user-guide/wiki)

### Comparing Two Documents

```java
Source control = Input.fromFile("test-data/good.xml").build();
Source test = Input.fromMemory(createTestDocument()).build();
AbstractDifferenceEngine diff = new DOMDifferenceEngine();
diff.addDifferenceListener(new ComparisonListener() {
        public void comparisonPerformed(Comparison comparison, ComparisonResult outcome) {
            Assert.fail("found a difference: " + comparison);
        }
    });
diff.compare(control, test);
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

## Building

XMLUnit for Java builds using Apache Ant, run `ant -projecthelp` for
the available targets, but mainly you want to run

```sh
$ ant
```

in order to compile `core`, `matchers` and `legacy` and run the
tests.

```sh
$ ant jar
```

creates the corresponding jar files.

