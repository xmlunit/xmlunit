LIBXMLUNIT=xmlunit.dll
LIBNUNIT=NUnit.Framework.dll
SRC=src/csharp/*.cs
TESTSRC=tests/csharp/*.cs
TESTLIB=xmlunit.tests.dll

all: $(LIBXMLUNIT) tests

$(LIBXMLUNIT): $(SRC)
	mcs -out:$@ -target:library $(SRC) /r:$(LIBNUNIT)

$(TESTLIB): $(LIBXMLUNIT) $(TESTSRC)
	mcs -out:$@ /r:$(LIBXMLUNIT) /r:$(LIBNUNIT) $(TESTSRC)

test.exe: $(LIBXMLUNIT) $(TESTSRC) $(TESTLIB)
	mcs -out:$@ /r:$(LIBXMLUNIT) /r:$(LIBNUNIT) tests/csharp/AllTests.cs

tests: test.exe
	export MONO_PATH=$$MONO_PATH:.; mono test.exe
