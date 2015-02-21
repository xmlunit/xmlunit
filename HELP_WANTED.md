We Need Your Help!
==================

This page lists a few things that should be done before XMLUnit 2.x
can be relased in a more or less random order.  It is not exhaustive
at all and if you want to work on something that is not on this list,
that's absolutely fine - maybe drop us a line on the XMLUnit general
list so we can avoid duplicate or wasted effort.

In General
----------

* Validate the API - this is paramount to get 2.0 final out but should
  probably be complete before the first alpha release
* work on the open issues
* maybe provide convenient APIs for ignoring complete subtrees when
  comparing pieces of XML.

Java
----

* additional hamcrest matchers

.NET
----

* CI builds on Windows
* create API docs
* additional NUnit constraints
* NuGet packaging

Claimed
-------

* [@bodewig](https://github.com/bodewig) has started to work on more
  generic `ElementSelectors` and intends to add a fluent builder for
  them - this is a generalization of
  [#4](https://github.com/xmlunit/xmlunit/issues/4)

