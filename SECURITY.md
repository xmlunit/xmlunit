# Security Policy

XMLUnit's primary use case is running tests against code that creates or transforms XML. As such it expects to be used on trusted inputs in general.
This means there will always be ways to enable insecure practices that - for example - enable XML External Entity (XXE) attacks. The defaults should be set up to prevent this,
but there may be good reasons to disable the safety net for trusted inputs.

## Supported Versions

Currently only the very latest version of XMLUnit for Java is supported, we don't backport patches to older versions.

## Reporting a Vulnerability

Please use https://github.com/xmlunit/xmlunit/security to report security vulnerabilites.

This project is run by volunteers. Please understand it may take time until you get any response - and there is no bug bounty program of any kind.
