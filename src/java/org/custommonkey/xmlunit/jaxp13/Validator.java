/*
******************************************************************
Copyright (c) 2008, Jeff Martin, Tim Bacon
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

package org.custommonkey.xmlunit.jaxp13;

import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.custommonkey.xmlunit.exceptions.XMLUnitRuntimeException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Validator class based of {@link javax.xml.validation javax.xml.validation}.
 *
 * <p>This class provides support for validating schema definitions as
 * well as instance documents.  It defaults to the W3C XML Schema 1.0
 * but can be used to validate against any schema language supported
 * by your SchemaFactory implementation.</p>
 */
public class Validator {
    private final String schemaLanguage;
    private final SchemaFactory factory;
    private final ArrayList sources = new ArrayList();

    /**
     * validates using W3C XML Schema 1.0.
     */
    public Validator() {
        this(XMLConstants.W3C_XML_SCHEMA_NS_URI, null);
    }

    /**
     * validates using the specified schema language.
     *
     * @param schemaLanguage the schema language to use - see {@link
     * javax.xml.validation.SchemaFactory SchemaFactory}.
     */
    public Validator(String schemaLanguage) {
        this(schemaLanguage, null);
    }

    /**
     * validates using the specified schema factory.
     */
    public Validator(SchemaFactory factory) {
        this(null, factory);
    }

    /**
     * validates using the specified schema language or factory.
     *
     * @param schemaLanguage the schema language to use - see {@link
     * javax.xml.validation.SchemaFactory SchemaFactory}.
     * @param schemaFactory the concrete factory to use.  If this is
     * non-null, the first argument will be ignored.
     */
    protected Validator(String schemaLanguage, SchemaFactory factory) {
        this.schemaLanguage = schemaLanguage;
        this.factory = factory;
    }

    /**
     * Adds a source for the schema defintion.
     */
    public void addSchemaSource(Source s) {
        sources.add(s);
    }

    /**
     * Is the given schema definition valid?
     */
    public boolean isSchemaValid() {
        return getSchemaErrors().size() == 0;
    }

    /**
     * Obtain a list of all errors in the schema defintion.
     *
     * <p>The list contains {@link org.xml.sax.SAXParseException
     * SAXParseException}s.</p>
     */
    public List/*<SAXParseException>*/ getSchemaErrors() {
        final ArrayList l = new ArrayList();
        try {
            parseSchema(new CollectingErrorHandler(l));
        } catch (SAXException e) {
            // error should have been recorded in our ErrorHandler, at
            // least that's what the Javadocs say "SchemaFactory is
            // not allowed to throw SAXException without first
            // reporting it to ErrorHandler.".
            //
            // Unfortunately not all implementations seem to follow
            // this rule.  In particular using the setup described in
            // org.custommonkey.xmlunit.jaxp13.test_Validator#XtestGoodRelaxNGCompactSyntaxIsValid()
            // an exception ("SAXParseException: Content is not
            // allowed in prolog.") will be thrown that never enters
            // our Errorhandler.
            if (l.size() == 0) {
                l.add(e);
            }
        }
        return l;
    }

    /**
     * Is the given schema instance valid according to the configured
     * schema definition(s)?
     *
     * @throws XMLUnitRuntimeException if the schema definition is
     * invalid or the Source is a SAXSource and the underlying
     * XMLReader throws an IOException (see {@link
     * javax.xml.validation.Validator#validate validate in
     * Validator}).
     */
    public boolean isInstanceValid(Source instance)
        throws XMLUnitRuntimeException {
        return getInstanceErrors(instance).size() == 0;
    }

    /**
     * Obtain a list of all errors in the given instance.
     *
     * <p>The list contains {@link org.xml.sax.SAXParseException
     * SAXParseException}s.</p>
     *
     * @throws XMLUnitRuntimeException if the schema definition is
     * invalid or the Source is a SAXSource and the underlying
     * XMLReader throws an IOException (see {@link
     * javax.xml.validation.Validator#validate validate in
     * Validator}).
     */
    public List/*<SAXParseException>*/ getInstanceErrors(Source instance)
        throws XMLUnitRuntimeException {
        Schema schema = null;
        try {
            schema = parseSchema(null);
        } catch (SAXException e) {
            throw new XMLUnitRuntimeException("Schema is invalid", e);
        }

        final ArrayList l = new ArrayList();
        javax.xml.validation.Validator v = schema.newValidator();
        v.setErrorHandler(new CollectingErrorHandler(l));
        try {
            v.validate(instance);
        } catch (SAXException e) {
            // error should have been recorded in our ErrorHandler,
            // but better double-check.
            if (l.size() == 0) {
                l.add(e);
            }
        } catch (java.io.IOException i) {
            throw new XMLUnitRuntimeException("Error reading instance source",
                                              i);
        }
        return l;
    }

    private Schema parseSchema(ErrorHandler h) throws SAXException {
        SchemaFactory fac = factory != null ? factory
            : SchemaFactory.newInstance(schemaLanguage);
        fac.setErrorHandler(h);
        try {
            return fac.newSchema((Source[])
                                 sources.toArray(new Source[sources.size()]));
        } finally {
            fac.setErrorHandler(null);
        }
    }

    private static final class CollectingErrorHandler implements ErrorHandler {
        private final List l;

        CollectingErrorHandler(List l) {
            this.l = l;
        }
        public void error(SAXParseException e) {
            l.add(e);
        }
        public void fatalError(SAXParseException e) {
            l.add(e);
        }
        public void warning(SAXParseException e) {
            l.add(e);
        }
    }
}
