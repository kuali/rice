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
package org.kuali.core.datadictionary.validation.fieldlevel;

import org.junit.Test;
import org.kuali.test.KNSTestBase;
import org.kuali.test.KNSWithTestSpringContext;


@KNSWithTestSpringContext
public class MonthValidationPatternTest extends KNSTestBase {
    MonthValidationPattern pattern;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        pattern = new MonthValidationPattern();
    }

    @Test public final void testMatches_valid1() {
        assertTrue(pattern.matches("1"));
    }

    @Test public final void testMatches_valid2() {
        assertTrue(pattern.matches("01"));
    }

    @Test public final void testMatches_valid3() {
        assertTrue(pattern.matches("11"));
    }


    @Test public final void testMatches_invalid1() {
        assertFalse(pattern.matches("00"));
    }

    @Test public final void testMatches_invalid2() {
        assertFalse(pattern.matches("0"));
    }

    @Test public final void testMatches_invalid3() {
        assertFalse(pattern.matches("13"));
    }
}
