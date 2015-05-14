We Need Your Help!
==================

This page lists a few things that should be done before XMLUnit 2.x
can be relased in a more or less random order.  It is not exhaustive
at all and if you want to work on something that is not on this list,
that's absolutely fine - maybe drop us a line on the XMLUnit general
list so we can avoid duplicate or wasted effort.

In General
----------

* provide a User's Guide - at least a first sketch that explains how
  to use parts of the API - [here](https://github.com/xmlunit/user-guide/wiki)
* Validate the API - this is paramount to get 2.0 final out but should
  probably be complete before the first alpha release
* work on the open issues
* provide convenient APIs for ignoring complete subtrees (see
  [#26](https://github.com/xmlunit/xmlunit/issues/26)) or certain
  attributes (see [#2](https://github.com/xmlunit/xmlunit/issues/2))
  when comparing pieces of XML.

Java
----

* additional hamcrest matchers

.NET
----

* CI builds on Windows
* create API docs
* additional NUnit constraints
* build ISource from objects via XmlSerializer

Claimed
-------

* [@bodewig](https://github.com/bodewig) has added some more generic
  `ElementSelectors` and a fluent builder for them - this is a
  generalization of [#4](https://github.com/xmlunit/xmlunit/issues/4)
  but isn't completely satisfied, yet.  The (two-arg-element-selector
  branch)[https://github.com/xmlunit/xmlunit/tree/two-arg-element-selector]
  contains a more elaborated version which changes the
  `ElementSelector` API to also provide the XPath of the `Element`s to
  compare - feedback is needed badly.

