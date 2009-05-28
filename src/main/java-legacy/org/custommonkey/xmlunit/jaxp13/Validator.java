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
import javax.xml.transform.Source;
import javax.xml.validation.SchemaFactory;
import net.sf.xmlunit.exceptions.XMLUnitException;
import net.sf.xmlunit.validation.JAXPValidator;
import net.sf.xmlunit.validation.Languages;
import net.sf.xmlunit.validation.ValidationProblem;
import org.custommonkey.xmlunit.exceptions.XMLUnitRuntimeException;
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
    private final ArrayList<Source> sources = new ArrayList<Source>();
    private final JAXPValidator validator;

    /**
     * validates using W3C XML Schema 1.0.
     */
    public Validator() {
        this(Languages.W3C_XML_SCHEMA_NS_URI, null);
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
        validator = new JAXPValidator(schemaLanguage, factory);
    }

    /**
     * Adds a source for the schema defintion.
     */
    public void addSchemaSource(Source s) {
        sources.add(s);
        validator.setSchemaSources(sources.toArray(new Source[0]));
    }

    /**
     * Is the given schema definition valid?
     */
    public boolean isSchemaValid() {
        return validator.validateSchema().isValid();
    }

    /**
     * Obtain a list of all errors in the schema defintion.
     *
     * <p>The list contains {@link org.xml.sax.SAXParseException
     * SAXParseException}s.</p>
     */
    public List<SAXParseException> getSchemaErrors() {
        return problemToExceptionList(validator.validateSchema().getProblems());
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
    public boolean isInstanceValid(Source instance) {
        try {
            return validator.validateInstance(instance).isValid();
        } catch (XMLUnitException e) {
            throw new XMLUnitRuntimeException(e.getMessage(), e.getCause());
        }
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
    public List<SAXParseException> getInstanceErrors(Source instance) {
        try {
            return problemToExceptionList(validator.validateInstance(instance).
                                          getProblems());
        } catch (XMLUnitException e) {
            throw new XMLUnitRuntimeException(e.getMessage(), e.getCause());
        }
    }

    private static List<SAXParseException>
        problemToExceptionList(Iterable<ValidationProblem> problems) {
        final List<SAXParseException> l = new ArrayList<SAXParseException>();
        for (ValidationProblem p : problems) {
            l.add(new SAXParseException(p.getMessage(),
                                        null, null,
                                        p.getLine(), p.getColumn()));
        }
        return l;
    }
}
