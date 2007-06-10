/*
 * Copyright 2004 Jonathan M. Lehr
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
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
import org.kuali.KeyConstants;


/**
 * begin Kuali Foundation modification
 * This class is used to format phone number objects.
 * end Kuali Foundation modification
 */
public class PhoneNumberFormatter extends Formatter {
	// begin Kuali Foundation modification
    private static final long serialVersionUID = 241458864711484787L;
    // end Kuali Foundation modification

	// begin Kuali Foundation modification
	// removed: PHONE_NUMBER_ERROR_KEY, PARSE_MSG, FORMAT_MSG
    // todo: foreign phone numbers can be different lengths
    // end Kuali Foundation modification
    static final int NUM_DIGITS = 10;

    /**
     * begin Kuali Foundation modification
     * Removes formatting characters from the provided phone number and returns just the digits. Very lenient about formatting, but
     * requires a ten-digit number.
     * end Kuali Foundation modification
     */
    protected Object convertToObject(String target) {
        String digits = target.replaceAll("[^0-9]", "");
        if (digits.length() != NUM_DIGITS)
        	// begin Kuali Foundation modification
            throw new FormatException("parsing", KeyConstants.ERROR_PHONE_NUMBER, target);
            // end Kuali Foundation modification

        return digits;
    }

    /**
     * Returns its argument formatted as a phone number in the style:
     * <p>
     * 
     * <pre>
     *   (999) 999-9999
     * </pre>
     */
    public Object format(Object value) {
        if (value == null)
            return null;
        if (!(value instanceof String))
        	// begin Kuali Foundation modification
            throw new FormatException("formatting", KeyConstants.ERROR_PHONE_NUMBER, value.toString());
            // end Kuali Foundation modification

		// begin Kuali Foundation modification
        String digits = (String) value;
        if (digits.length() != NUM_DIGITS)
            throw new FormatException("formatting", KeyConstants.ERROR_PHONE_NUMBER, value.toString());
        // end Kuali Foundation modification

        StringBuffer buf = new StringBuffer("(");
        buf.append(digits.substring(0, 3));
        buf.append(") ");
        buf.append(digits.substring(3, 6));
        buf.append("-");
        buf.append(digits.substring(6));

        return buf.toString();
    }
}
