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
public class DateValidationPatternTest extends KNSTestBase {
    DateValidationPattern pattern;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        pattern = new DateValidationPattern();
        String foo = pattern.getRegexPattern().pattern();
        String bar = "none";
    }

    @Test public final void testMatches_valid1() {
        assertTrue(pattern.matches("1966-5-1"));
    }

    @Test public final void testMatches_valid2() {
        assertTrue(pattern.matches("1966-05-01"));
    }

    @Test public final void testMatches_valid3() {
        assertTrue(pattern.matches("1966-5-01"));
    }

    @Test public final void testMatches_valid4() {
        assertTrue(pattern.matches("1966-05-1"));
    }

    @Test public final void testMatches_valid5() {
        assertTrue(pattern.matches("2237-5-1"));
    }


    @Test public final void testMatches_invalid1() {
        assertFalse(pattern.matches("1966/05/01"));
    }

    @Test public final void testMatches_invalid2() {
        assertFalse(pattern.matches("05/1966/01"));
    }

    @Test public final void testMatches_invalid3() {
        assertFalse(pattern.matches("5/1/196"));
    }

    @Test public final void testMatches_invalid4() {
        assertFalse(pattern.matches("5/1/66"));
    }


    @Test public final void testMatches_invalid5() {
        assertFalse(pattern.matches("5/32/1966"));
    }

    @Test public final void testMatches_invalid6() {
        assertFalse(pattern.matches("13/01/1966"));
    }

    @Test public final void testMatches_invalid7() {
        assertFalse(pattern.matches("05/01/3103"));
    }
}
