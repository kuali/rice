/**
 * Copyright 2005-2018 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.web.format;

import org.joda.time.DateTime;

import java.sql.Date;

public class DateTimeFormatter extends DateFormatter {

    @Override
    protected Object convertToObject(String target) {
        Date date = (Date) super.convertToObject(target);
        return new DateTime(date.getTime());
    }

    @Override
    public Object format(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return value;
        } else if (value instanceof java.util.Date) {
            return getDateTimeService().toDateTimeString((java.util.Date)value);
        } else {
            return getDateTimeService().toDateTimeString(((DateTime) value).toDate());
        }
    }

}
