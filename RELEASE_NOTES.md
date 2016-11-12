# Release Notes

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
