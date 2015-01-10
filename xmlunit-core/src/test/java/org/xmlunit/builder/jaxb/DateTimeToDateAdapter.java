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
package org.xmlunit.builder.jaxb;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateTimeToDateAdapter extends XmlAdapter<String, Date> {

    @Override
    public Date unmarshal(final String value) {
        return DatatypeConverter.parseDate(value).getTime();
    }

    @Override
    public String marshal(final Date value) {
        final Calendar cal = new GregorianCalendar();
        cal.setTime(value);
        return DatatypeConverter.printDateTime(cal);
    }

}
