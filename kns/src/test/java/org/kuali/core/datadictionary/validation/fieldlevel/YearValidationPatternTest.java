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
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;


@WithTestSpringContext
public class YearValidationPatternTest extends KualiTestBase {
    YearValidationPattern pattern;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        pattern = new YearValidationPattern();
    }

    @Test public final void testMatches_valid1() {
        assertTrue(pattern.matches("1901"));
    }

    @Test public final void testMatches_valid2() {
        assertTrue(pattern.matches("1991"));
    }

    @Test public final void testMatches_valid3() {
        assertTrue(pattern.matches("2005"));
    }

    @Test public final void testMatches_valid4() {
        assertTrue(pattern.matches("1837"));
    }


    @Test public final void testMatches_invalid1() {
        assertFalse(pattern.matches("992"));
    }

    @Test public final void testMatches_invalid2() {
        assertFalse(pattern.matches("1514"));
    }

    @Test public final void testMatches_invalid3() {
        assertFalse(pattern.matches("3103"));
    }

    @Test public final void testMatches_invalid4() {
        assertFalse(pattern.matches("10001"));
    }
}
