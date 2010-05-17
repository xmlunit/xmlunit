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
package net.sf.xmlunit.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import net.sf.xmlunit.exceptions.ConfigurationException;
import net.sf.xmlunit.exceptions.XMLUnitException;

class TransformHelper {
    private final Source source;
    private Source styleSheet;
    private TransformerFactory factory;
    private URIResolver uriResolver;
    private final Properties output = new Properties();
    private final Map<String, Object> params = new HashMap<String, Object>();
    TransformHelper(Source s) {
        source = s;
    }
    void setStylesheet(Source s) {
        styleSheet = s;
    }
    void setOutputProperty(String name, String value) {
        output.setProperty(name, value);
    }
    void setParameter(String name, Object value) {
        params.put(name, value);
    }
    void setFactory(TransformerFactory f) {
        factory = f;
    }
    void setUriResolver(URIResolver r) {
        uriResolver = r;
    }
    void transformTo(Result r) {
        try {
            TransformerFactory fac = factory;
            if (fac == null) {
                fac = TransformerFactory.newInstance();
            }
            Transformer t = null;
            if (styleSheet != null) {
                t = fac.newTransformer(styleSheet);
            } else {
                t = fac.newTransformer();
            }
            if (uriResolver != null) {
                t.setURIResolver(uriResolver);
            }
            t.setOutputProperties(output);
            for (Map.Entry<String, Object> ent : params.entrySet()) {
                t.setParameter(ent.getKey(), ent.getValue());
            }
            t.transform(source, r);
        } catch (javax.xml.transform.TransformerConfigurationException e) {
            throw new ConfigurationException(e);
        } catch (javax.xml.transform.TransformerException e) {
            throw new XMLUnitException(e);
        }
    }
}