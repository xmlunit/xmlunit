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
namespace net.sf.xmlunit.validation {

    /// <summary>
    /// Constants for the languages supported by XMLUnit's schema
    /// validation.
    /// </summary>
public static class Languages {

    /// <summary>W3C XML Schema.</summary>
    public const string W3C_XML_SCHEMA_NS_URI =
        "http://www.w3.org/2001/XMLSchema";

    /// <summary>DTD.</summary>
    public const string XML_DTD_NS_URI = "http://www.w3.org/TR/REC-xml";

    /// <summary>XDR.</summary>
    public const string XDR_NS_URI = "xmlunit:validation:XDR";
}
}
