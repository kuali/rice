/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary.validation.charlevel;

import org.junit.Test;
import org.kuali.rice.kns.datadictionary.validation.charlevel.RegexValidationPattern;
import org.kuali.rice.test.BaseRiceTestCase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This is a description of what this class does - ctdang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RegexValidationPatternTest extends BaseRiceTestCase {
    private RegexValidationPattern regexValidationPattern;

    public void setUp() throws Exception {      
        regexValidationPattern=new RegexValidationPattern();
    }
    
    @Test public final void testNumericPattern() {
        regexValidationPattern.setPattern("[0-9]");
        
        assertTrue(regexValidationPattern.matches("123456789"));
        assertFalse(regexValidationPattern.matches("abc"));
    }

    @Test public final void testAlphaPattern() {
        regexValidationPattern.setPattern("[a-zA-Z]");
        
        assertTrue(regexValidationPattern.matches("abc"));
        assertFalse(regexValidationPattern.matches("a12345"));
    }

    @Test public final void testAlphaNumericPattern() {
        regexValidationPattern.setPattern("[a-zA-Z0-9]");
        
        assertTrue(regexValidationPattern.matches("ab12345c"));
        assertFalse(regexValidationPattern.matches("a1*234?"));
    }

}
