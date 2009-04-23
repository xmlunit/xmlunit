%define name		xmlunit
%define version		1.0
%define release		1
%define javadir		%{_datadir}/java
%define javadocdir	%{_datadir}/javadoc
%define section		free

Name:		%{name}
Version:	%{version}
Release:	%{release}
Summary:	XML testing package
License:	BSD Style Software License
URL:		http://xmlunit.sf.net/
Group:		Development/Testing
Vendor:		XMLUnit Project
Distribution:	XMLUnit
Source0:	http://xmlunit.sf.net/dist/%{name}-%{version}-src.tgz
Provides:	xmlunit
Requires:	junit
Requires:	/usr/sbin/update-alternatives
BuildRequires:	ant >= 1.5
BuildArch:	noarch
BuildRoot:	%{_tmppath}/%{name}-%{version}-%{release}-buildroot

%description
XMLUnit extends JUnit to simplify unit testing of XML. It compares a control XML document to a test document or the result of a transformation, validates documents against a DTD, and (from v0.5) compares the results of XPath expressions.

%prep
%setup -q -n xmlunit-%{version}
#%patch0 -p0
#%patch1 -p2

%build

# build with empty CLASSPATH
ant -buildfile build.xml -Dbuild.compiler=modern jar docs

%install
rm -rf $RPM_BUILD_ROOT

# jars
mkdir -p $RPM_BUILD_ROOT%{javadir}
cp -p lib/%{name}-%{version}.jar $RPM_BUILD_ROOT%{javadir}/%{name}-%{version}.jar
(cd $RPM_BUILD_ROOT%{javadir} && for jar in *-%{version}.jar; do ln -sf ${jar} `echo $jar| sed "s|-%{version}||g"`; done)

# docs
#mkdir -p $RPM_BUILD_ROOT%{javadocdir}/%{name}-%{version}
#cp -pr doc/* \
  #$RPM_BUILD_ROOT%{javadocdir}/%{name}-%{version}

%clean
rm -rf $RPM_BUILD_ROOT

%post

%postun

%files
%defattr(0644,root,root,0755)
%doc LICENSE.txt README.txt ISSUES 
%{javadir}/*
#%{javadocdir}/*

%changelog
* Wed Mar 05 2003 Jeff Martin <jeff@custommonkey.org> 1.0.0-1jpp 
- first release
