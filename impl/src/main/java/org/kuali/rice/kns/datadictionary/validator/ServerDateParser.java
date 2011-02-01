/**
 * Copyright 2010 The Kuali Foundation Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.kuali.rice.kns.datadictionary.validator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerDateParser implements DateParser {
    SimpleDateFormat[] formats = {
    		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"), 
    		new SimpleDateFormat("yyyy-MM-dd"), 
    		new SimpleDateFormat("yyyy-MMM-dd"),
    		new SimpleDateFormat("dd-MM-yyyy"),
    		new SimpleDateFormat("dd-MMM-yyyy")
    };
    
    public Date parseDate(String input) {
        Date result = null;
        
        for (SimpleDateFormat format : formats) {
                try {
                    result = format.parse(input);
                } catch (Exception e) {
                    // just eat it
                }
                if (result != null) {
                    break;
                }
            
        }
        
        if (result == null) {
            throw new DateParseException("Invalid date value: " + input);
        }
        
        return result;
    }

    /**
     * @see org.kuali.student.common.validator.old.DateParser#toString(java.util.Date)
     */
    @Override
    public String toString(Date date) {
        String result = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss,SSS");
        result = format.format(date);

        return result;        
    }
}
