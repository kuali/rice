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

/**
 * begin Kuali Foundation modification
 * This class is used to format objects.
 * end Kuali Foundation modification
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class TypeFormatter extends Formatter {
	// begin Kuali Foundation modification
    private static final long serialVersionUID = -7766095355995725855L;
    // end Kuali Foundation modification

    public final static String ERROR_KEY = "error.type";

    static final String PARSE_MSG = "Unable to parse type ";
    static final String FORMAT_MSG = "Unable to format type ";

    protected Object convertToObject(String stringValue) {

        // TODO Auto-generated method stub
        return super.convertToObject(stringValue);
    }

    public Object format(Object target) {
        // TODO Auto-generated method stub
        return super.formatForPresentation(target);
    }
}
