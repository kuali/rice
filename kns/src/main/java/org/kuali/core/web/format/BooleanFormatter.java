/*
 * Copyright 2004 Jonathan M. Lehr
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * MODIFIED BY THE KUALI FOUNDATION
 */
// begin Kuali Foundation modification
package org.kuali.core.web.format;
// end Kuali Foundation modification

import java.util.Arrays;
import java.util.List;

import org.kuali.KeyConstants;

/**
 * begin Kuali Foundation modification
 * This class is used to format boolean values.
 * end Kuali Foundation modification
 * @author <a href="mailto:jonathan@sourcebeat.com">Jonathan Lehr</a>
 */
public class BooleanFormatter extends Formatter {
    // begin Kuali Foundation modification
    // deleted line: public static final String BOOLEAN_ERROR_KEY = "error.boolean";
    private static final long serialVersionUID = -4109390572922205211L;

    // deleted line: static final String CONVERT_MSG = "Unable to create Boolean object from ";
    // end Kuali Foundation modification

	// begin Kuali Foundation modification
	// "y" and "t" added to TRUE_VALUES, "n" and "f" added to FALSE_VALUES
    static final List TRUE_VALUES = Arrays.asList(new String[] { "yes", "y", "true", "t", "on", "1", "enabled" });
    static final List FALSE_VALUES = Arrays.asList(new String[] { "no", "n", "false", "f", "off", "0", "disabled" });
	// end Kuali Foundation modification

	/* begin Kuali Foundation modification
	   deleted following method */
    // /**
	//  * Returns the error key for this Formatter.
	//  * 
	//  * @see Formatter#getErrorKey()
	//  */
	// public String getErrorKey() {
	// 	return BOOLEAN_ERROR_KEY;
	// }
	// end Kuali Foundation modification
	
    protected Object convertToObject(String target) {
        if (Formatter.isEmptyValue(target))
            return null;

        String stringValue = target.getClass().isArray() ? unwrapString(target) : (String) target;
        stringValue = stringValue.trim().toLowerCase();

        if (TRUE_VALUES.contains(stringValue))
            return Boolean.TRUE;
        if (FALSE_VALUES.contains(stringValue))
            return Boolean.FALSE;

		// begin Kuali Foundation modification
		// was: throw new FormatException(CONVERT_MSG + stringValue);
        throw new FormatException("converting", KeyConstants.ERROR_BOOLEAN, stringValue);
        // end Kuali Foundation modification
    }

    public Object format(Object target) {
        if (target == null)
            return null;
        // begin Kuali Foundation modification
        if (target instanceof String) {
            return target;
        }
        // end Kuali Foundation modification

        boolean isTrue = ((Boolean) target).booleanValue();

        return isTrue ? "Yes" : "No";
    }
}
