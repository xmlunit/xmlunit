Contributing to XMLUnit
=======================

The current focus is on getting a complete rewrite of XMLUnit 1.x -
called 2.x - out of the door.  Please help us shape the new API and
discuss it on the `xmlunit-general` list over at
[Sourceforge](http://sourceforge.net/p/xmlunit/mailman/?source=navbar).

Contributing to XMLUnit 2.x
---------------------------

If you think you've found a bug or are missing a feature, please
[open an issue](https://github.com/xmlunit/xmlunit/issues), we'll also
gladly accept
[pull requests](https://github.com/xmlunit/xmlunit/pulls).

Before you start working on a big feature, please tell us about it on
the mailing list, though.  This way you can make sure you're not
wasting your time on something that isn't considered to be in
XMLUnit's scope.

If you are looking for something to work on, we've compiled a
[list](HELP_WANTED.md) of things that should be done before XMLUnit
2.0 can be released.

XMLUnit's users guide is developed (or rather will be developed)
inside [a github Wiki](https://github.com/xmlunit/user-guide/wiki) -
a lot of help is needed here, before we can cut a new release.

### test-resources

Because XmlUnit is developed for Java and .NET, both Projects shares the same test-resources.
The test-resources folder is integrated as git submodule.
This means you need to run 'git submodule update --init' once inside your
working copies after merging github's master branches of xmlunit or
xmlunit.net.

### Preparing a Pull Request

+ Create a topic branch from where you want to base your work (this is
  usually the master branch).
+ Make commits of logical units.
+ Respect the original code style:
  + Only use spaces for indentation.
  + Create minimal diffs - disable on save actions like reformat
    source code or organize imports. If you feel the source code
    should be reformatted create a separate issue/PR for this change.
  + Check for unnecessary whitespace with `git diff --check` before committing.
+ Make sure your commit messages are in the proper format. Your commit
  message should contain the key of the issue if you created one.
+ Make sure you have added the necessary tests for your changes.
+ Run all the tests with `mvn clean test` to assure nothing else was
  accidentally broken.

Contributing to XMLUnit for Java 1.x
------------------------------------

XMLUnit for Java 1.x's feature set is frozen, but we still fix bugs
and maintain it.  If you've found a bug, please raise an issue at
http://sourceforge.net/p/xmlunit/bugs/ - patches against
http://svn.code.sf.net/p/xmlunit/code/trunk will be gladly accepted.

The same rules that have been laid out for pull requests also apply to
patches against the subversion trunk.

