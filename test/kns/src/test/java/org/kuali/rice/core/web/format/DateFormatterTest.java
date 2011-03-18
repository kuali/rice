/*
 * Copyright 2006-2008 The Kuali Foundation
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

import org.junit.Test;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.test.KNSTestCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DateFormatterTest extends KNSTestCase {

    DateFormatter dateFormatter = new DateFormatter();

    private Date kualiParseDate(String input) {
        return (Date) dateFormatter.convertToObject(input);
    }

    private Date javaParseDate(String input) throws ParseException {
        return new java.sql.Date(simpleDateFormat.parse(input).getTime());
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
    
    @Test public void test1969() throws Exception {
        assertEquals(kualiParseDate("09/28/1969"), javaParseDate("09/28/1969"));
    }

    @Test public void testShortYear() throws Exception {
        assertEquals(kualiParseDate("09/28/1069"), javaParseDate("09/28/1069"));
    }

    @Test public void testAmbiguousYear() throws Exception {
    	/**
    	 * Note that in Rice 0.9.3, this date format would have thrown a FormatException,
    	 * however in Rice 1.0 changes were made so that 2 digit years could be interpreted properly.
    	 */
    	assertEquals(kualiParseDate("09/28/69"), javaParseDate("09/28/69"));
    }


}
