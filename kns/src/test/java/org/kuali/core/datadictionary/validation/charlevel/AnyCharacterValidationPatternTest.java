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
package org.kuali.core.datadictionary.validation.charlevel;

import org.junit.Test;
import org.kuali.core.datadictionary.validation.ValidationTestUtils;
import org.kuali.test.KNSTestBase;

public class AnyCharacterValidationPatternTest extends KNSTestBase {
    private AnyCharacterValidationPattern pattern;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        pattern = new AnyCharacterValidationPattern();
    }


    @Test public final void testMatch_allowDefault() {
        boolean[] expected = { true, // ""
                true, // "!!!"
                true, // "[a-9]"
                true, // "^A-Z"
                true, // "abc"
                false, // "a bc"
                true, // "a_bc"
                true, // "123"
                false, // "12 3"
                true, // "12_3"
                true, // "a1b2c3"
                true, // "a1b2_c3"
                false, // "a 1b2c3"
                false, // "a 1b2_c3"
        };

        ValidationTestUtils.assertPatternMatches(pattern, expected);
    }

    @Test public final void testMatch_allowWhitespace() {
        boolean[] expected = { true, // ""
                true, // "!!!"
                true, // "[a-9]"
                true, // "^A-Z"
                true, // "abc"
                true, // "a bc"
                true, // "a_bc"
                true, // "123"
                true, // "12 3"
                true, // "12_3"
                true, // "a1b2c3"
                true, // "a1b2_c3"
                true, // "a 1b2c3"
                true, // "a 1b2_c3"
        };

        pattern.setAllowWhitespace(true);
        ValidationTestUtils.assertPatternMatches(pattern, expected);
    }
}
