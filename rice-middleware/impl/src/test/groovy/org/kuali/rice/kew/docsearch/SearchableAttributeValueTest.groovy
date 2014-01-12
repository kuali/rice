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
package org.kuali.rice.kew.docsearch

import org.junit.Test
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

/**
 * Tests SearchableAttributeValues
 */
class SearchableAttributeValueTest {

    @Test void testStringRange() {
        assertTrue(new SearchableAttributeStringValue().isRangeValid("abc", "def"))
        // def > abc
        assertFalse(new SearchableAttributeStringValue().isRangeValid("def", "abc"))

        assertTrue(new SearchableAttributeStringValue().isRangeValid("", "abc"))
        assertTrue(new SearchableAttributeStringValue().isRangeValid("abc", ""))
        assertTrue(new SearchableAttributeStringValue().isRangeValid(null, "abc"))
        assertTrue(new SearchableAttributeStringValue().isRangeValid("abc", null))
        assertTrue(new SearchableAttributeStringValue().isRangeValid("", ""));
        // ... i guess these are valid? ...
        assertTrue(new SearchableAttributeStringValue().isRangeValid("", null))
        assertTrue(new SearchableAttributeStringValue().isRangeValid(null, ""))
    }

    /**
     * tests default validation for SearchableAttributeLongValue against some sample inputs
     */
    @Test void testLongValueDefaultValidation() {
        assertTrue(new SearchableAttributeLongValue().isPassesDefaultValidation("12345"));
        assertTrue(new SearchableAttributeLongValue().isPassesDefaultValidation("-12345"));

        // simple invalid formats
        assertFalse(new SearchableAttributeLongValue().isPassesDefaultValidation("12345.01234"));
        assertFalse(new SearchableAttributeLongValue().isPassesDefaultValidation("-12345.01234"));
        assertFalse(new SearchableAttributeLongValue().isPassesDefaultValidation("asdf."));

        // test binary operators

        // valid expressions
        assertTrue(new SearchableAttributeLongValue().isPassesDefaultValidation("-123..123 | 256..512"));
        assertTrue(new SearchableAttributeLongValue().isPassesDefaultValidation(">123 && <256"));

        // this one has an invalid operand for one of the ranged operators, so it should fail
        assertFalse(new SearchableAttributeLongValue().isPassesDefaultValidation("-123..123 | 123.45..512"));
        assertFalse(new SearchableAttributeFloatValue().isPassesDefaultValidation("-123..123|..512"));
        assertFalse(new SearchableAttributeLongValue().isPassesDefaultValidation(">123 && <256a"));

        // two operators together is invalid
        assertFalse(new SearchableAttributeLongValue().isPassesDefaultValidation("1>&&<1..2&&|3"));
    }

    /**
     * tests default validation for SearchableAttributeFloatValue against some sample inputs
     */
    @Test void testFloatValueDefaultValidation() {
        assertTrue(new SearchableAttributeFloatValue().isPassesDefaultValidation("12345.56"));
        assertTrue(new SearchableAttributeFloatValue().isPassesDefaultValidation("-12345.56"));
        assertTrue(new SearchableAttributeFloatValue().isPassesDefaultValidation("-0.56"));
        assertTrue(new SearchableAttributeFloatValue().isPassesDefaultValidation("-.56"));

        // simple invalid formats
        assertFalse(new SearchableAttributeFloatValue().isPassesDefaultValidation("192.168.0.0.1"));
        assertFalse(new SearchableAttributeFloatValue().isPassesDefaultValidation("-12345.01234a"));
        assertFalse(new SearchableAttributeFloatValue().isPassesDefaultValidation("asdf."));

        // test binary operators

        // valid expressions
        assertTrue(new SearchableAttributeFloatValue().isPassesDefaultValidation("-123.4..+123.4|256.0..512.0"));
        assertTrue(new SearchableAttributeFloatValue().isPassesDefaultValidation(">123.4&&<256.0"));

        // these ones have invalid operands for one of the ranged operators, so they should fail
        assertFalse(new SearchableAttributeFloatValue().isPassesDefaultValidation("-123.4..123.4|123.45a..512.0"));
        assertFalse(new SearchableAttributeFloatValue().isPassesDefaultValidation("-123.4..123.4|..512.0"));
        assertFalse(new SearchableAttributeFloatValue().isPassesDefaultValidation(">123.4 && <256.0a"));

        // two operators together is invalid
        assertFalse(new SearchableAttributeFloatValue().isPassesDefaultValidation("1>&&<1..2&&|2"));
    }

}
