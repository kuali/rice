/**
 * Copyright 2005-2014 The Kuali Foundation
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
import org.kuali.rice.kns.datadictionary.validation.fieldlevel.EmailAddressValidationPattern;
import org.kuali.rice.krad.test.KRADTestCase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * EmailAddressValidationPatternTest tests {@link EmailAddressValidationPattern} - only valid email addresses should match
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EmailAddressValidationPatternTest extends KRADTestCase {
    private EmailAddressValidationPattern pattern;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        pattern = new EmailAddressValidationPattern();
    }


    @Test public final void testMatches_valid1() {
        assertTrue("Valid email address did not pass the validation pattern", pattern.matches("ww5@a.b.c.org"));
    }

    @Test public final void testMatches_valid2() {
        assertTrue("Valid email address did not pass the validation pattern", pattern.matches("something.else@a2.com"));
    }

    @Test public final void testMatches_valid3() {
        assertTrue("Valid email address did not pass the validation pattern", pattern.matches("something_else@something.else.com"));
    }

    @Test public final void testMatches_valid4() {
        assertTrue("Valid email address did not pass the validation pattern", pattern.matches("something-else@et-tu.com"));
    }


    @Test public final void testMatches_invalid1() {
        assertFalse("Invalid email address passed the validation pattern", pattern.matches("@a.b.c.org"));
    }

    @Test public final void testMatches_invalid2() {
        assertFalse("Invalid email address passed the validation pattern", pattern.matches("a"));
    }

    @Test public final void testMatches_invalid3() {
        assertFalse("Invalid email address passed the validation pattern", pattern.matches("1@org"));
    }

    @Test public final void testMatches_invalid4() {
        assertFalse("Invalid email address passed the validation pattern", pattern.matches("1@a"));
    }

    @Test public final void testMatches_invalid5() {
        assertFalse("Invalid email address passed the validation pattern", pattern.matches("_@a"));
    }

    @Test public final void testMatches_invalid6() {
        assertFalse("Invalid email address passed the validation pattern", pattern.matches(".@a.org"));
    }

    @Test public final void testMatches_invalid7() {
        assertFalse("Invalid email address passed the validation pattern", pattern.matches("-@a.org"));
    }

    @Test public final void testMatches_invalid8() {
        assertFalse("Invalid email address passed the validation pattern", pattern.matches("something@a.o-rg"));
    }
}
