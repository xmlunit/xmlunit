# Release Notes

## XMLUnit for Java 2.8.4 - /Released 2021-12-16/

* improved comparison performance for documents with many siblings
  based on a suggestion by [@gerpres](https://github.com/gerpres) made
  in [#236](https://github.com/xmlunit/xmlunit/issues/236)

## XMLUnit for Java 2.8.3 - /Released 2021-10-17/

* added a new `fullDescription` method to `Diff` that provides a
  string-representation of all differences - not just the first one
  like `toString` does.
  PR [#235](https://github.com/xmlunit/xmlunit/issues/235) fixing
  [#232](https://github.com/xmlunit/xmlunit/issues/232) by
  [@Boiarshinov](https://github.com/Boiarshinov)

* made sure AssertJ's methods to override the assertion message like
  `withFailMessage` are honored.
  [#225](https://github.com/xmlunit/xmlunit/issues/225)

* adjusted unit tests so they pass when AssertJ 3.19.0 is used.
  PR [#212](https://github.com/xmlunit/xmlunit/pull/212) by
  [mmathesius](https://github.com/mmathesius)

## XMLUnit for Java 2.8.2 - /Released 2020-12-21/

* `CompareAssert` inside the AssertJ3 module swapped the expected and
  actual parameters when creating the assertion error.
  [#210](https://github.com/xmlunit/xmlunit/pull/210) by
  [@f-lopes](https://github.com/f-lopes)

## XMLUnit for Java 2.8.1 - /Released 2020-11-15/

* added a new xmlunit-assertj3 module that requires AssertJ 3.18.1 or
  later.

  This module no longer uses AssertJ internal classes that are not
  exported to OSGi environments and thus fixes issue [#203](https://github.com/xmlunit/xmlunit/issues/203).

  The module (like AssertJ 3.x itself) requires Java 8 at runtime and
  is similar to xmlunit-assertj but is no drop-in replacement. It uses
  a different Java package from xmlunit-assertj and `CompareAssert`
  will no longer throw a JUnit 4.x `ComparisonException` but an
  opentest4j `AssertionFailedError` instead.

  The existing xmlunit-assertj module will still be supported in
  future releases but expect AssertJ 3.x specific changes to only get
  applied to xmlunit-assertj3.

  Many thanks to [@Zegveld](https://github.com/Zegveld),
  [@scordio](https://github.com/scordio) and
  [@joel-costigliola](https://github.com/joel-costigliola).

## XMLUnit for Java 2.8.0 - /Released 2020-10-30/

* changed optional JAXB dependency to use Jakarta XML Binding API
  PR [#186](https://github.com/xmlunit/xmlunit/pull/186)
  by [@endrejeges](https://github.com/endrejeges)

* bumped the bytebuddy dependency to 1.10.10 for the AssertJ module in
  the hope it would help with
  [#188](https://github.com/xmlunit/xmlunit/issues/188)
  - and change its scope from provided to compile time, which should
  finally fix the issue.

* added a new `extractingText` method to `MultipleNodeAssert` to make
  it possible to write AssertJ assertions against the textual content
  of nodes more easily.
  Issue [#192](https://github.com/xmlunit/xmlunit/issues/192) and PR
  [#193](https://github.com/xmlunit/xmlunit/pull/193)
  by [@keesvandieren](https://github.com/keesvandieren)

* changed the minimum Java version required from Java 6 to Java 7 for
  all modules (it has already been Java 7 for the AssertJ module
  before).

* `DefaultNodeMatcher` with multiple `ElementSelector`s could fail to
  find the best matches as the order of `ElementSelector`s should
  select them.
  [#197](https://github.com/xmlunit/xmlunit/issues/197)

* `Input` builder now supports `java.nio.file.Path`
  [#196](https://github.com/xmlunit/xmlunit/issues/196)

* It is now possible to specify a custom `TransformerFactory` for
  `DefaultComparisonFormatter`.
  [#195](https://github.com/xmlunit/xmlunit/issues/195)

## XMLUnit for Java 2.7.0 - /Released 2020-05-12/

This version contains a backwards incompatible change to the
`PlaceholderHandler` interface that is part of the experimental
placeholders module: The `evaluate` method now receives a variable
number of string arguments in addition to the textual content of the
element/attribute. This allows placeholders like
`${xmlunit.matchesRegex(some\s*regex)}`.

* the AssertJ tests now pass on non-English locales as well
  Issue [#180](https://github.com/xmlunit/xmlunit/pull/180)

* added a workaround for a binary incompatible change in AssertJ that
  caused xmlunit-assertj to be incompatible with AssertJ 3.15.0
  Issue [#181](https://github.com/xmlunit/xmlunit/issues/181)

* added a new `${xmlunit.matchesRegex(regex)}` placeholder
  PR [#178](https://github.com/xmlunit/xmlunit/issues/178) by
  [@Jazzyekim](https://github.com/Jazzyekim).

* add a new `${xmlunit.isDateTime}` placeholder
  inspired by [#xmlunit.net/31](https://github.com/xmlunit/xmlunit.net/pull/31) and
  [#xmlunit.net/32](https://github.com/xmlunit/xmlunit.net/pull/32) by
  [MilkyWare](https://github.com/MilkyWare)
  Issue [#174](https://github.com/xmlunit/xmlunit/issues/174)

* avoid unnecessary creation of `DocumentBuilderFactory` in
  `DOMDifferenceEngine` when a custom factory has been provided to
  `DiffBuilder`.
  Issue [#182](https://github.com/xmlunit/xmlunit/issues/182)

## XMLUnit for Java 2.6.4 - /Released 2020-03-08/

* the dependencies on JAXB implementation and its transitive
  dependencies has been promoted from test scope to optional for Java
  9 and later
  Issue [#162](https://github.com/xmlunit/xmlunit/issues/162)

* added `containsAnyNodeHavingXPath`, `containsAllNodesHavingXPath`
  and `hasXPath` assertions to xmlunit-assertj.

* added `extractingAttribute` method to xmlunit-assertj.

* removed some redundant `instanceof` checks.
  PR [#171](https://github.com/xmlunit/xmlunit/issues/171) by
  [@PascalSchumacher](https://github.com/PascalSchumacher).

* xmlunit-assertj should now work with AssertJ-Core 3.13.x
  Issue [#166](https://github.com/xmlunit/xmlunit/issues/166)

* the XPath values for comparisons resulting in `CHILD_LOOKUP`
  differences could be wrong when `NodeFilter`s were present.
  XMLUnit.NET Issue
  [xmlunit.net/#29](https://github.com/xmlunit/xmlunit.net/issues/29)

* xmlunit-legacy will now use `NewDifferenceEngine` even when an
  `ElementQualifier` different from the built-in ones is used.

## XMLUnit for Java 2.6.3 - /Released 2019-06-21/

* add a new `${xmlunit.isNumber}` placeholder
  Issue [#153](https://github.com/xmlunit/xmlunit/issues/153) via PR
  [#154](https://github.com/xmlunit/xmlunit/pull/154) by
  [@NathanAtClarity](https://github.com/NathanAtClarity).

* the XPath values of a comparison should not be affected by any
  `NodeFilter` being in effect.
  Issue [#156](https://github.com/xmlunit/xmlunit/issues/156)

## XMLUnit for Java 2.6.2 - /Released 2018-08-27/

* xmlunit-assertj can now be used with AssertJ 3.9.1+ as well as 2.9.x.
  Issue [#135](https://github.com/xmlunit/xmlunit/issues/135).

* added a new `TypeMatcher` to the xmlunit-matchers module that can be
  used to conveniently translate XPath result strings into numbers or
  booleans and verify they match type safe assertions.
  Issue [#133](https://github.com/xmlunit/xmlunit/issues/133) via PR
  [#137](https://github.com/xmlunit/xmlunit/pull/137).

* fixed the `Automatic-Module-Name` of all modules to be valid Java
  identifiers.
  Issue [#136](https://github.com/xmlunit/xmlunit/issues/136).

## XMLUnit for Java 2.6.1 - /Released 2018-08-16/

* add a new module with AssertJ support. This module requires Java7 at
  runtime. Issue [#117](https://github.com/xmlunit/xmlunit/pull/117)
  via PRs [#120](https://github.com/xmlunit/xmlunit/pull/120),
  [#126](https://github.com/xmlunit/xmlunit/pull/126),
  [#128](https://github.com/xmlunit/xmlunit/pull/128), and
  [#129](https://github.com/xmlunit/xmlunit/pull/129) by
  [@krystiankaluzny](https://github.com/krystiankaluzny).

* The `XPathFactory` used by the XPath related Hamcrest matchers is
  now configurable.
  Issue [#131](https://github.com/xmlunit/xmlunit/pull/132)
  via PRs [#132](https://github.com/xmlunit/xmlunit/pull/132)

## XMLUnit for Java 2.6.0 - /Released 2018-04-22/

* add a new experimental project xmlunit-placeholders which aims to
  use `${xmlunit.FOO}` expressions inside of the control document to
  allow for a DSL-like approach of defining more complex tests.
  This initial seed only supports `${xmlunit.ignore}` which can be
  used to make XMLUnit ignore the element containing this text.
  PR [#105](https://github.com/xmlunit/xmlunit/pull/105) by
  [@zheng-wang](https://github.com/zheng-wang).

* added `withDocumentBuilderFactory` methods to `HasXPathMatcher` and
  `EvaluateXPathMatcher` to allow explicit configuration of the
  `DocumentBuilderFactory` used.
  Issue [#108](https://github.com/xmlunit/xmlunit/issues/108).

* the `DocmentBuilderFactory` and `TransformerFactory` instances used
  by XMLUnit are now configured to not load any external DTDs or parse
  external entities. They are now configured according to the [OWASP
  recommendations for XML eXternal Entity injection
  preventions](https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Java). The
  `TransformerFactory` used by the
  `org.xmlunit.transform.Transformation` class is still configured to
  load external stylesheets, though.

  For the `legacy` package XXE prevention has to be enabled via
  `XMLUnit.enableXXEProtection` explicitly.

  This is a breaking change and you may need to provide
  `DocmentBuilderFactory` or `TransformerFactory` instances of your
  own if you need to load external entities.

  The `SAXParserFactory` and `SchemaFactory` instances used inside of
  the `validation` package have not been changed as their use is
  likely to require loading of external DTDs or schemas.

  Issue [#91](https://github.com/xmlunit/xmlunit/issues/91).

* the configured `NodeFilter` is now applied before comparing
  `DocumentType` nodes.

  This change breaks backwards compatibility as the old behavior was
  to ignore `DocumentType` when counting the children of the
  `Document` node but not when actually comparing the
  `DocumentType`. Prior to this change if one document had a document
  type declaration but the other didn't, a `HAS_DOCTYPE_DECLARATION`
  difference was detected, this will no longer be the case now. If you
  want to detect this difference, you need to use a more lenient
  `NodeFilter` than `NodeFilters.Default`
  (i.e. `NodeFilters.AcceptAll`) but then you will see an additional
  `CHILD_NODELIST_LENGTH` difference.

  The legacy package has been adapted and will behave as before even
  when using `NewDifferenceEngine`.

  Issue [#116](https://github.com/xmlunit/xmlunit/issues/116).

* added a new `Source` implementation
  `ElementContentWhitespaceStrippedSource` which is similar to
  `WhitespaceStrippedSource` but only affects text nodes that solely
  consist of whitespace and doesn't affect any other text nodes. Also
  added convenience `ignoreElementContentWhitespace` methods to
  `DiffBuilder` and `CompareMatcher`.
  Issue [#119](https://github.com/xmlunit/xmlunit/issues/119).

## XMLUnit for Java 2.5.1 - /Released 2017-11-09/

* Made Travis build work with OpenJDK6 again.
  PR [#101](https://github.com/xmlunit/xmlunit/pull/101) by
  [@PascalSchumacher](https://github.com/PascalSchumacher).

* `CompareMatcher`'s `describeTo` method threw an exception if the
  comparison yielded no differences.
  Issue [#107](https://github.com/xmlunit/xmlunit/issues/107).

## XMLUnit for Java 2.5.0 - /Released 2017-09-03/

* `CommentLessSource`, `DiffBuilder#ignoreComments` and
  `CompareMatcher#ignoreComments` now all use XSLT version 2.0
  stylesheets in order to strip comments. New constructors and methods
  have been added if you need a different version of XSLT (in
  particular if you need 1.0 which used to be the default up to
  XMLUnit 2.4.0).
  Issue [#99](https://github.com/xmlunit/xmlunit/issues/99).

## XMLUnit for Java 2.4.0 - /Released 2017-07-23/

* made `DefaultComparisonFormatter` more subclass friendly.
  Issue [#93](https://github.com/xmlunit/xmlunit/issues/93).

## XMLUnit for Java 2.3.0 - /Released 2016-11-12/

* `JAXPValidator` and `ValidationMatcher` now accept using `Schema`
  instances for the schema when validating instance documents.
  Issue [#89](https://github.com/xmlunit/xmlunit/issues/89).

* updated test dependency to Mockito 2.1.0
  PR [#87](https://github.com/xmlunit/xmlunit/pull/87) by
  [@PascalSchumacher](https://github.com/PascalSchumacher).

## XMLUnit for Java 2.2.1 - /Released 2016-06-19/

* The `DocumentBuilderFactory` set on `DiffBuilder` wasn't used
  properly when `ignoreWhitespace` or `normalizeWhitespace` has been
  set.
  Issue [#86](https://github.com/xmlunit/xmlunit/issues/86).

## XMLUnit for Java 2.2.0 - /Released 2016-06-04/

* `Input.fromByteArray` and `Input.fromString` now return `Source`s that
  can be used multiple times.
  Issue [#84](https://github.com/xmlunit/xmlunit/issues/84).

* The `DocumentBuilderFactory` used by `DOMDifferenceEngine` is now
  configurable.
  Issue [#83](https://github.com/xmlunit/xmlunit/issues/83).

## XMLUnit for Java 2.1.1 - /Released 2016-04-09/

* various code style fixes
  PR [#74](https://github.com/xmlunit/xmlunit/pull/74),
  PR [#75](https://github.com/xmlunit/xmlunit/pull/75),
  PR [#78](https://github.com/xmlunit/xmlunit/pull/78),
  PR [#79](https://github.com/xmlunit/xmlunit/pull/79),
  PR [#80](https://github.com/xmlunit/xmlunit/pull/80)
  by [@georgekankava](https://github.com/georgekankava).

* `CompareMatcher` and `ValidationMatcher` threw
  `NullPointerException`s when combined with another failing
  `Matcher`.
  Issue [#81](https://github.com/xmlunit/xmlunit/issues/81).

## XMLUnit for Java 2.1.0 - /Released 2016-03-26/

* fixed swapped constant assignments in `DifferenceEvaluators`
  PR [#53](https://github.com/xmlunit/xmlunit/pull/53) by
  [@cboehme](https://github.com/cboehme).

* added `CompareMatcher#withNamespaceContext`
  PR [#54](https://github.com/xmlunit/xmlunit/pull/54) by
  [@cboehme](https://github.com/cboehme).

* `DiffBuilder#withNamespaceContext` falsely claimed the map would
  pass prefixes to URIs rather than the other way around.
  PR [#62](https://github.com/xmlunit/xmlunit/pull/62) and issue
  [#52](https://github.com/xmlunit/xmlunit/pull/52) by
  [@mariusneo](https://github.com/mariusneo).

* various code style fixes
  PR [#64](https://github.com/xmlunit/xmlunit/pull/64),
  PR [#65](https://github.com/xmlunit/xmlunit/pull/65),
  PR [#67](https://github.com/xmlunit/xmlunit/pull/67),
  PR [#68](https://github.com/xmlunit/xmlunit/pull/68),
  PR [#69](https://github.com/xmlunit/xmlunit/pull/69),
  PR [#70](https://github.com/xmlunit/xmlunit/pull/70) and
  PR [#71](https://github.com/xmlunit/xmlunit/pull/71) by
  [@georgekankava](https://github.com/georgekankava).

* new `hasXPath` matchers that check for the existence of an XPath
  inside of a piece of XML or verify additional assertions on the
  XPath's stringified result.
  PR [#63](https://github.com/xmlunit/xmlunit/pull/63) and
  PR [#66](https://github.com/xmlunit/xmlunit/pull/66) by
  [@mariusneo](https://github.com/mariusneo).

* added new implementations inside `DifferenceEvaluators` for common
  tasks like changing the outcome for specific differences or ignoring
  changes inside the XML prolog.

* `DiffBuilder.withComparisonFormatter` now also fully applies to the
  `Difference`s contained within the `Diff`.
  Issue [#55](https://github.com/xmlunit/xmlunit/issues/55)

## XMLUnit for Java 2.0.0 - /Released 2016-03-06/

* implemented `DiffBuilder.withComparisonFormatter` mentioned in user
  guide.
  Issue [#51](https://github.com/xmlunit/xmlunit/issues/51)
* eliminated dead-stores.
  PR [#52](https://github.com/xmlunit/xmlunit/pull/52) by
  [@georgekankava](https://github.com/georgekankava).

## XMLUnit for Java 2.0.0-alpha-04 - /Released 2016-02-06/

* the `schemaURI` in `Validator` has been pushed down to
  `ParsingValidator` since it is only used inside this class.
* the mapping of `DifferenceEngine#setNamespaceContext` has been
  inverted from prefix -> URI to URI -> prefix in order to be
  consistent with the same concept in `XPathEngine`.
* `CommentLessSource` uses an XSLT stylesheet internally which lacked
  the required `version` attribute. PR
  [#47](https://github.com/xmlunit/xmlunit/pull/47) by
  [@phbenisc](https://github.com/phbenisc).
* `Comparison` now also contains the XPath of the parent of the
  compared nodes or attributes which is most useful in cases of
  missing nodes/attributes because the XPath on one side is `null` in
  these cases.
  Issue [#48](https://github.com/xmlunit/xmlunit/issues/48)
  implemented via PR [#50](https://github.com/xmlunit/xmlunit/pull/50)
  by [@eguib](https://github.com/eguib).

## XMLUnit for Java 2.0.0-alpha-03 - /Released 2015-12-13/

* the xmlunit-parent POM no longer uses the deprecated
  `org.sonatype.oss:oss-parent` as its parent.
* added new overloads to `XPathEngine`
* fixed the XPath context used by the `byXPath` element selector so
  that "." now refers to the current element.
  Issue [#39](https://github.com/xmlunit/xmlunit/issues/39)
* `ElementSelectors#conditionalBuilder` now stops at the first
  predicate returning `true`, even if the associated `ElementSelector`
  returns false.
  Issue [#40](https://github.com/xmlunit/xmlunit/issues/40)

## XMLUnit for Java 2.0.0-alpha-02 - /Released 2015-11-21/

This is the initial alpha release of XMLUnit.NET.  We expect the API
to change for the next release based on user feedback.
