/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.core.web.format;

import java.text.ParseException;
import java.util.Date;

import org.kuali.KeyConstants;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class is used to format timestamp objects.
 */

public class TimestampAMPMFormatter extends Formatter {
    private static final long serialVersionUID = 7612442662886603084L;

    /**
     * Unformats its argument and return a java.util.Date instance initialized with the resulting string.
     * 
     * @return a java.util.Date intialized with the provided string
     */
    public Object convertToObject(String target) {
        try {
        	return KNSServiceLocator.getDateTimeService().convertToSqlTimestamp(target);
        }
        catch (ParseException e) {
            throw new FormatException("parsing", KeyConstants.ERROR_DATE_TIME, target, e);
        }
    }


    /**
     * Returns a string representation of its argument, formatted as a date with the "MM/dd/yyyy h:mm a" format.
     * 
     * @return a formatted String
     */
    public Object format(Object value) {
        if (value == null)
            return null;
        return KNSServiceLocator.getDateTimeService().toDateTimeString((Date)value);
    }
}