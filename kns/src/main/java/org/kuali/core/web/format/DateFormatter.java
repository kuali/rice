/*
 * Copyright 2004 Jonathan M. Lehr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 * 
 * MODIFIED BY THE KUALI FOUNDATION
 */
// begin Kuali Foundation modification
package org.kuali.core.web.format;
// end Kuali Foundation modification

// begin Kuali Foundation modification
// import order changed, and java.util.Calendar is imported instead of java.util.Date
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.kuali.Constants;
import org.kuali.KeyConstants;

/**
 * begin Kuali Foundation modification
 * This class is used to format Date objects.
 * end Kuali Foundation modification
 */
public class DateFormatter extends Formatter {
    // begin Kuali Foundation modification
    // serialVersionUID changed from 1L
    private static final long serialVersionUID = 7612442662886603084L;
    // end Kuali Foundation modification

    /** The default date format string for display (Kuali Foundation modification to comment text)*/
    public final static String DATE_FORMAT = "MM/dd/yyyy";

	// begin Kuali Foundation modification
	// static variables DATE_ERROR_KEY and PARSE_MSG removed
	// method public String getErrorKey() removed
	// end Kuali Foundation modification

	// begin Kuali Foundation modification
	// added this method
    /**
     * 
     * For a given user input date, this method returns the exact string the user entered after the last slash. This allows the
     * formatter to distinguish between ambiguous values such as "/06" "/6" and "/0006"
     * 
     * @param date
     * @return
     */
    private String verbatimYear(String date) {
        String result = "";

        int pos = date.lastIndexOf("/");
        if (pos >= 0) {
            result = date.substring(pos);
        }

        return result;
    }
    // end Kuali Foundation modification


    /**
     * Unformats its argument and return a java.util.Date instance initialized with the resulting string.
     * 
     * @return a java.util.Date intialized with the provided string
     */
    protected Object convertToObject(String target) {
    	// begin Kuali Foundation modification
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        formatter.setLenient(false);
        try {
            java.util.Date result = new java.sql.Date(formatter.parse(target).getTime());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(result);
            if (calendar.get(Calendar.YEAR) < 1000 && verbatimYear(target).length() < 4) {
                throw new FormatException("illegal year format", KeyConstants.ERROR_DATE, target);
            }
            return result;
        }
        catch (ParseException e) {
            throw new FormatException("parsing", KeyConstants.ERROR_DATE, target, e);
        }
        // end Kuali Foundation modification
    }

    /**
     * Returns a string representation of its argument, formatted as a date with the "MM/dd/yyyy" format.
     * 
     * @return a formatted String
     */
    public Object format(Object value) {
        if (value == null)
            return null;
        // begin Kuali Foundation modification
        if (value.toString().equals(Constants.EMPTY_STRING))
            return null;
        // end Kuali Foundation modification

        StringBuffer buf = new StringBuffer();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        formatter.setLenient(false);
        formatter.format(value, buf, new FieldPosition(0));

        return buf.toString();
    }
}