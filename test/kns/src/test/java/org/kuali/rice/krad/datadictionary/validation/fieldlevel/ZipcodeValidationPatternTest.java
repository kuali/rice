/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary.validation.fieldlevel;

import org.junit.Test;
import org.kuali.test.KNSTestCase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ZipcodeValidationPatternTest extends KNSTestCase {
    private ZipcodeValidationPattern pattern;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        pattern = new ZipcodeValidationPattern();
    }


    @Test public final void testMatches_5digit_valid() {
        assertTrue(pattern.matches("12345"));
    }

    @Test public final void testMatches_9digit_valid() {
        assertTrue(pattern.matches("12345-1234"));
    }

    @Test public final void testMatches_invalid1() {
        assertFalse(pattern.matches("123456"));
    }

    @Test public final void testMatches_invalid2() {
        assertFalse(pattern.matches("1234"));
    }

    @Test public final void testMatches_invalid3() {
        assertFalse(pattern.matches("12345-12345"));
    }

    @Test public final void testMatches_invalid4() {
        assertFalse(pattern.matches("12345-123"));
    }
}
