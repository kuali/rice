/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import java.sql.Timestamp;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;

/**
 * This class is the base clase used to format Timestamp objects.
 */
public abstract class TimestampFormatterBase extends Formatter {
    private final String dateFormat;

    /**
     * Protected to keep people from trying to instantiate it directly
     */
    protected TimestampFormatterBase(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * Unformats its argument and return a java.util.Date instance initialized with the resulting string.
     * 
     * @return a java.util.Date intialized with the provided string
     */
    protected Object convertToObject(String target) {

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Checking whether or not the year is 4 digit number format
        // Using it temporarily until implement calendar function
        Timestamp date = null;
        if (target.length() > 4) {
            if (StringUtils.contains(target.substring(target.length() - 4, target.length()), "/")) {
                throw new FormatException("In the Date field, Year should be four digit numbers");
            }
        }


        formatter.setLenient(false);
        try {
            return new java.sql.Timestamp(formatter.parse(target).getTime());
        }
        catch (ParseException e) {
            throw new FormatException("parsing", KeyConstants.ERROR_DATE, target, e);
        }
    }

    /**
     * Returns a string representation of its argument, formatted as a date with the "MM/dd/yyyy" format.
     * 
     * @return a formatted String
     */
    public Object format(Object value) {
        if (value == null)
            return null;
        if (value.toString().equals(Constants.EMPTY_STRING))
            return null;
        StringBuffer buf = new StringBuffer();
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        formatter.setLenient(false);
        formatter.format(value, buf, new FieldPosition(0));

        return buf.toString();
    }
}
