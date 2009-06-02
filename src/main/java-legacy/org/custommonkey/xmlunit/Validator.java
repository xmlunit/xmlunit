/*
******************************************************************
Copyright (c) 2001-2007, Jeff Martin, Tim Bacon
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.
    * Neither the name of the xmlunit.sourceforge.net nor the names
      of its contributors may be used to endorse or promote products
      derived from this software without specific prior written
      permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
*/

package org.custommonkey.xmlunit;

import net.sf.xmlunit.validation.Languages;
import net.sf.xmlunit.validation.ParsingValidator;
import net.sf.xmlunit.validation.ValidationProblem;
import net.sf.xmlunit.validation.ValidationResult;

import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.custommonkey.xmlunit.exceptions.XMLUnitRuntimeException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

/**
 * Validates XML against its internal or external DOCTYPE, or a completely
 *  different DOCTYPE.
 * Usage:
 * <ul>
 * <li><code>new Validator(readerForXML);</code> <br/>
 *   to validate some XML that contains or references an accessible DTD or
 *   schema
 * </li>
 * <li><code>new Validator(readerForXML, systemIdForValidation);</code> <br/>
 *   to validate some XML that references a DTD but using a local systemId
 *   to perform the validation
 * </li>
 * <li><code>new Validator(readerForXML, systemIdForValidation, doctypeName);</code> <br/>
 *   to validate some XML against a completely different DTD
 * </li>
 * </ul>
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class Validator extends DefaultHandler {
    private final InputSource validationInputSource;
    private final StringBuffer messages;
    private final boolean usingDoctypeReader;
    private final String systemId;

    private Object schemaSource;
    private boolean useSchema = false;

    private Boolean isValid;

    /**
     * Kept for backwards compatibility.
     * @deprecated Use the protected three arg constructor instead.
     */
    protected Validator(InputSource inputSource,
                        boolean usingDoctypeReader) {
        this(inputSource, null, usingDoctypeReader);
    }

    /**
     * Baseline constructor: called by all others
     * 
     * @param inputSource
     * @param systemId
     * @param usingDoctypeReader
     */
    protected Validator(InputSource inputSource,
                        String systemId,
                        boolean usingDoctypeReader) {
        isValid = null;
        messages = new StringBuffer();
        this.validationInputSource = inputSource;
        this.systemId = systemId;
        this.usingDoctypeReader = usingDoctypeReader;
    }

    /**
     * DOM-style constructor: allows Document validation post-manipulation
     * of the DOM tree's contents.
     * This takes a fairly tortuous route to validation as DOM level 2 does
     * not allow creation of Doctype nodes.
     * The supplied systemId and doctype name will replace any Doctype
     * settings in the Document.
     * 
     * @param document
     * @param systemID
     * @param doctype
     */
    public Validator(Document document, String systemID, String doctype) {
        this(new InputStreamReader(new NodeInputStream(document)),
             systemID, doctype);
    }

    /**
     * Basic constructor.
     * Validates the contents of the Reader using the DTD or schema referenced
     *  by those contents.
     *  
     * @param readerForValidation
     */
    public Validator(Reader readerForValidation) {
        this(readerForValidation, null);
    }

    /**
     * Basic constructor.
     * Validates the contents of the String using the DTD or schema referenced
     *  by those contents.
     *  
     * @param stringForValidation
     */
    public Validator(String stringForValidation) {
        this(new StringReader(stringForValidation));
    }

    /**
     * Basic constructor.
     * Validates the contents of the InputSource using the DTD or
     * schema referenced by those contents.
     *  
     * @param readerForValidation
     */
    public Validator(InputSource sourceForValidation) {
        this(sourceForValidation, null);
    }

    /**
     * Extended constructor.
     * Validates the contents of the Reader using the DTD specified with the
     *  systemID. There must be DOCTYPE instruction in the markup that
     *  references the DTD or else the markup will be considered invalid: if
     *  there is no DOCTYPE in the markup use the 3-argument constructor
     *  
     * @param readerForValidation
     * @param systemID
     */
    public Validator(Reader readerForValidation, String systemID) {
        this(new InputSource(readerForValidation), systemID,
             (readerForValidation instanceof DoctypeReader));
    }

    /**
     * Extended constructor.
     * Validates the contents of the String using the DTD specified with the
     *  systemID. There must be DOCTYPE instruction in the markup that
     *  references the DTD or else the markup will be considered invalid: if
     *  there is no DOCTYPE in the markup use the 3-argument constructor
     *  
     * @param stringForValidation
     * @param systemID
     */
    public Validator(String stringForValidation, String systemID) {
        this(new StringReader(stringForValidation), systemID);
    }

    /**
     * Extended constructor.
     * Validates the contents of the InputSource using the DTD
     * specified with the systemID. There must be DOCTYPE instruction
     * in the markup that references the DTD or else the markup will
     * be considered invalid: if there is no DOCTYPE in the markup use
     * the 3-argument constructor
     *  
     * @param sourceForValidation
     * @param systemID
     */
    public Validator(InputSource sourceForValidation, String systemID) {
        this(sourceForValidation, systemID, false);
    }

    /**
     * Full constructor.
     * Validates the contents of the InputSource using the DTD
     * specified with the systemID and named with the doctype name.
     *  
     * @param sourceForValidation
     * @param systemID
     * @param doctype
     */
    public Validator(InputSource sourceForValidation, String systemID,
                     String doctype) {
        this(sourceForValidation.getCharacterStream() != null
             ? new InputSource(new DoctypeReader(sourceForValidation
                                                 .getCharacterStream(),
                                                 doctype, systemID))
             : new InputSource(new DoctypeInputStream(sourceForValidation
                                                      .getByteStream(),
                                                      sourceForValidation
                                                      .getEncoding(),
                                                      doctype, systemID)),
             systemID, true);
    }

    /**
     * Full constructor.
     * Validates the contents of the Reader using the DTD specified with the
     *  systemID and named with the doctype name.
     *  
     * @param readerForValidation
     * @param systemID
     * @param doctype
     */
    public Validator(Reader readerForValidation, String systemID,
                     String doctype) {
        this(readerForValidation instanceof DoctypeReader
             ? readerForValidation
             : new DoctypeReader(readerForValidation, doctype, systemID),
             systemID);
    }

    /**
     * Turn on XML Schema validation.
     *
     * <p><b>This feature should work with any XML parser that is JAXP
     * 1.2 compliant and supports XML Schema validation.</b></p>
     *
     * <p>For a fully JAXP 1.2 compliant parser the property {@link
     * JAXPConstants.Properties.SCHEMA_LANGUAGE
     * http://java.sun.com/xml/jaxp/properties/schemaLanguage} is set,
     * if this fails the method falls back to the features
     * http://apache.org/xml/features/validation/schema &amp;
     * http://apache.org/xml/features/validation/dynamic which should
     * cover early versions of Xerces 2 as well.</p>
     *
     * @param use indicate that XML Schema should be used to validate
     * documents.
     * @see #setJAXP12SchemaSource(Object)
     */
    public void useXMLSchema(boolean use) {
        useSchema = use;
    }

    /**
     * Perform the validation of the source against DTD / Schema.
     * 
     * @return true if the input supplied to the constructor passes validation,
     *  false otherwise
     */
    public boolean isValid() {
        validate();
        return isValid.booleanValue();
    }

    /**
     * Assert that a document is valid.
     */
    public void assertIsValid(){
        if(!isValid()){
            junit.framework.Assert.fail(messages.toString());
        }
    }

    /**
     * Append any validation message(s) to the specified StringBuffer.
     * 
     * @param toAppendTo
     * @return specified StringBuffer with message(s) appended
     */
    private StringBuffer appendMessage(StringBuffer toAppendTo) {
        if (isValid()) {
            return toAppendTo.append("[valid]");
        }
        return toAppendTo.append(messages);
    }

    /**
     * @return class name appended with validation messages
     */
    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString()).append(':');
        return appendMessage(buf).toString();
    }

    /**
     * Actually perform validation.
     */
    private void validate() {
        if (isValid != null) {
            return;
        }

        ParsingValidator v =
            new ParsingValidator(useSchema ? Languages.W3C_XML_SCHEMA_NS_URI
                                 : Languages.XML_DTD_NS_URI);
        List<Source> schemaSourceList = new ArrayList<Source>();
        if (systemId != null) {
            schemaSourceList.add(new StreamSource(systemId));
        }
        addSchemaSources(schemaSource, schemaSourceList);
        v.setSchemaSources(schemaSourceList.toArray(new Source[0]));

        try {
            ValidationResult r =
                v.validateInstance(new SAXSource(validationInputSource));
            isValid = r.isValid() ? Boolean.TRUE : Boolean.FALSE;
            for (ValidationProblem p : r.getProblems()) {
                validationProblem(p);
            }
        } catch (net.sf.xmlunit.exceptions.ConfigurationException e) {
            throw new ConfigurationException(e.getCause());
        } catch (net.sf.xmlunit.exceptions.XMLUnitException e) {
            throw new XMLUnitRuntimeException(e.getMessage(), e.getCause());
        }

        if (usingDoctypeReader && isValid == Boolean.FALSE) {
            try {
                messages.append("\nContent was: ")
                    .append(getOriginalContent(validationInputSource));
            } catch (IOException e) {
                // silent but deadly?
            }
        }
    }

    private void validationProblem(ValidationProblem p) {
        String msg = "At line " + p.getLine() + ", column: "
            + p.getColumn() + " ==> " + p.getMessage();
        if (!msg.endsWith("\n")) msg += "\n";
        invalidate(msg);
    }

    /**
     * Set the validation status flag to false and capture the message for use
     * later.
     * 
     * @param message
     */
    private void invalidate(String message) {
        isValid = Boolean.FALSE;
        messages.append(message).append(' ');
    }

    /**
     * As per JAXP 1.2 changes, which introduced a standard way for parsers to
     * support schema validation. Since only W3C Schema support was included in 
     * JAXP 1.2, this is the only mechanism currently supported by this method.
     * 
     * @param schemaSource
     *            This can be one of the following:
     * <ul>
     *   <li>String that points to the URI of the schema</li>
     *   <li>InputStream with the contents of the schema</li>
     *   <li>SAX InputSource</li>
     *   <li>File</li>
     *   <li>an array of Objects with the contents being one of the
     *       types defined above. An array of Objects can be used only when
     *       the schema language has the ability to assemble a schema at
     *       runtime. When an array of Objects is passed it is illegal to
     *       have two schemas that share the same namespace.</li>
     * </ul>
     * @see http://java.sun.com/webservices/jaxp/change-requests-11.html
     */
    public void setJAXP12SchemaSource(Object schemaSource) {
        this.schemaSource = schemaSource;
    }

    private static void addSchemaSources(Object schemaSources,
                                         List<Source> targetList) {
        if (schemaSources instanceof String) {
            targetList.add(new StreamSource((String) schemaSources));
        } else if (schemaSources instanceof File) {
            targetList.add(new StreamSource((File) schemaSources));
        } else if (schemaSources instanceof InputStream) {
            targetList.add(new StreamSource((InputStream) schemaSources));
        } else if (schemaSources instanceof InputSource) {
            targetList.add(new SAXSource((InputSource) schemaSources));
        } else if (schemaSources instanceof Object[]) {
            for (Object s : (Object[]) schemaSources) {
                addSchemaSources(s, targetList);
            }
        } else if (schemaSources != null) {
            throw new XMLUnitRuntimeException("Unknown schema source type: "
                                              + schemaSources.getClass());
        }
    }

    private static String getOriginalContent(InputSource s)
        throws IOException {
        return s.getCharacterStream() instanceof DoctypeReader
            ? ((DoctypeReader) s.getCharacterStream()).getContent()
            : ((DoctypeInputStream) s.getByteStream())
                  .getContent(s.getEncoding());
    }
}
