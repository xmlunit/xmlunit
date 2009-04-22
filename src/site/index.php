<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
  <head>
    <title>&lt;xml-unit/&gt;</title>
    <meta http-equiv="content-type" content=
    "text/html; charset=ISO-8859-1">
    <meta name="keywords"
      content="unit test, test first, xml, testing, framework, junit">
<style type="text/css">
 body{font-family: Helvetica, Arial, sans-serif}
 code{font-style: italic}
</style>
  </head>

  <body>
    <table width="100%" height="100%">
      <tr>
        <td colspan="2"><img align="left" src="xmlunit.png" alt=
        "&lt;xml-unit/&gt;" width="331" height="100">


        <a href=
        "http://sourceforge.net"><img align="right" src=
        "http://sourceforge.net/sflogo.php?group_id=23187" width=
        "88" height="31" border="0" alt="SourceForge Logo"></a>
        <h2><a href="http://www.junit.org/">JUnit</a>
          testing for XML</h2>
        </td>
      </tr>

      <tr>
        <td valign="top">


          <p>For those of you who've got into it you'll no that test first
coding is great. It gives you the confidence to hack around safe in the
knowlegde that if something breaks you'll know about it. Except those bit's you
don't know about. Until now XML has been one of them. Oh sure you can use
<code><b>"&lt;stuff&gt;&lt;/stuff&gt;"</b>.equals(<b>"&lt;stuff&gt;&lt;/stuff&gt;"</b>);</code> but is that really gonna work when some joker decides to output a <code>&lt;stuff/&gt;</code>, damned right it's not.</p>

          <p>XML can be used for just about anything so deciding if two documents are equal to each other isn't as easy as a character for character match. Somtimes
<table bgcolor="black">
<tr><td bgcolor="white"><pre>&lt;stuff-doc&gt;
  &lt;stuff&gt;
    Stuff Stuff Stuff
  &lt;/stuff&gt;
  &lt;more-stuff&gt;
    Some More Stuff
  &lt;/more-stuff&gt;
&lt;/stuff-doc&gt; </pre>
</td><td bgcolor="white">equals</td><td bgcolor="white"><pre>&lt;stuff-doc&gt;
  &lt;more-stuff&gt;
    Some More Stuff&lt;/more-stuff&gt;
  &lt;stuff&gt;Stuff Stuff Stuff&lt;/stuff&gt;
&lt;/stuff-doc&gt; </pre></td></tr></table>
and sometime it doesn't.</p>

          <p>XMLUnit allows you to <code>assertXMLEquals(<b>"&lt;stuff&gt;&lt;/stuff&gt;"</b>, <b>"&lt;stuff/&gt;"</b>);</code> or and you can do this <code>assertXMLEquals(xmlFile, anotherXmlFile);</code>
          and this <code>assertXMLEquals(xmlStream, xmlFile);</code></p>
          <p>The list goes on and on. The Choice is yours.
          </p>
          <p>The current release is here -&gt; <a href=
          "http://sourceforge.net/project/showfiles.php?group_id=23187&amp;release_id=83804">XMLUnit 0.5</a>, 10th April 2002 - 
<a href="http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/xmlunit/xmlunit/README?rev=1.2&content-type=text/vnd.viewcvs-markup">Release Notes</a></p>

          <p><a href="doc">Javadocs</a></p>
          <p><a href="example.html">Example</a></p>

          <p>Browse CVS -&gt; <a href=
          "http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/xmlunit/xmlunit/">
          CVS</a></p>
        </td>
        <td valign="top">
          <table bgcolor="black" cellspacing="2" width="200">
            <tr>
              <td style="font-size: smaller;">
                <table bgcolor="white" cellspacing="5">
                  <tr><td align="right"><h2>News</h2></td></tr>
                  <tr>
                    <td style="font-size: smaller;">
      <?php
      include 'http://sourceforge.net/export/projnews.php?group_id=23187&show_summaries=1&limit=5'
      ?>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      <tr>
        <td valign="bottom" colspan="2">
    <p style="font-size: smaller;">Brought to you by, <a href="http://www.custommonkey.org/">CustomMonkey.org</a>
    and <a href="http://www.primeeight.co.uk/">PrimeEight.co.uk</a></p>
        </td>
      </tr>
    </table>
  </body>
</html>
