/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
using System;
using System.Collections.Generic;
using System.IO;
using System.Xml;
using System.Xml.Schema;
using net.sf.xmlunit.exceptions;

namespace net.sf.xmlunit.validation {

    /// <summary>
    /// Validates a piece of XML against a schema given in a supported
    /// language or the defintion of such a schema itself.
    /// </summary>
    public class Validator {
        private readonly ValidationType language;
        private string schemaURI;
        private ISource[] sourceLocations;

        private Validator(ValidationType language) {
            this.language = language;
        }

        /// <summary>
        /// The URI (or for example the System ID in case of a DTD) that
        /// identifies the schema to validate or use during validation.
        /// </summary>
        public virtual string SchemaURI {
            set {
                schemaURI = value;
            }
            get {
                return schemaURI;
            }
        }

        /// <summary>
        /// Where to find the schema.
        /// </summary>
        public virtual ISource[] SchemaSources {
            set {
                if (value != null) {
                    sourceLocations = new ISource[value.Length];
                    Array.Copy(value, 0, sourceLocations, 0, value.Length);
                } else {
                    sourceLocations = null;
                }
            }
            get {
                return sourceLocations;
            }
        }

        /// <summary>
        /// Where to find the schema.
        /// </summary>
        public ISource SchemaSource {
            set {
                SchemaSources = value == null ? null : new ISource[] {value};
            }
        }

        /// <summary>
        /// Validates a schema.
        /// </summary>
        public virtual ValidationResult ValidateSchema() {
            if (language == ValidationType.Schema && sourceLocations != null) {
                List<ValidationProblem> problems =
                    new List<ValidationProblem>();
                foreach (ISource loc in sourceLocations) {
                    XmlSchema.Read(loc.Reader, CollectProblems(problems));
                }
                return new ValidationResult(problems.Count == 0, problems);
            }
            throw new NotImplementedException();
        }

        /// <summary>
        /// Validates an instance against the schema.
        /// </summary>
        public virtual ValidationResult ValidateInstance(ISource instance) {
            XmlReaderSettings settings = new XmlReaderSettings();
            settings.ValidationType = language;
            settings.ValidationFlags =
                XmlSchemaValidationFlags.ProcessIdentityConstraints
                | XmlSchemaValidationFlags.ReportValidationWarnings;
            if (language == ValidationType.Schema && sourceLocations != null) {
                foreach (ISource loc in sourceLocations) {
                    try {
                        XmlSchema s = XmlSchema.Read(loc.Reader, ThrowOnError);
                        settings.Schemas.Add(s);
                    } catch (IOException ex) {
                        throw new XMLUnitException("Schema is not readable",
                                                   ex);
                    }
                }
            }
            List<ValidationProblem> problems = new List<ValidationProblem>();
            settings.ValidationEventHandler += CollectProblems(problems);
            settings.XmlResolver = new ThrowingResolver();
            using (XmlReader r = XmlReader.Create(instance.Reader,
                                                  settings)) {
                while (r.Read()) ;
            }
            return new ValidationResult(problems.Count == 0, problems);
        }

        private class ThrowingResolver : XmlResolver {
            public override object GetEntity(Uri uri, string role, Type type) {
                throw new Exception(string.Format("GetEntity invoked with ({0}, {1}, {2}",
                                                  uri, role, type));
            }

            public override System.Net.ICredentials Credentials {
                set {}
            }
        } 

        private static readonly IDictionary<string, ValidationType> types;

        static Validator() {
            types = new Dictionary<string, ValidationType>();
            types[Languages.W3C_XML_SCHEMA_NS_URI] = ValidationType.Schema;
            types[Languages.XML_DTD_NS_URI] = ValidationType.DTD;
            types[Languages.XDR_NS_URI] = ValidationType.XDR;
        }

        /// <summary>
        /// Factory that obtains a Validator instance based on the schema language.
        /// </summary>
        public static Validator ForLanguage(string language) {
            ValidationType t;
            if (types.TryGetValue(language, out t)) {
                return new Validator(t);
            }
            // TODO pick a better exception type
            throw new NotImplementedException();
        }

        private static ValidationEventHandler CollectProblems(List<ValidationProblem> problems) {
            return delegate(object sender, ValidationEventArgs e) {
                problems.Add(ValidationProblem.FromEvent(e));
            };
        }

        private static void ThrowOnError(object sender, ValidationEventArgs e) {
            throw new XMLUnitException("Schema is invalid", e.Exception);
        }

    }
}
