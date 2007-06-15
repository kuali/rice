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
import org.kuali.test.KualiTestBase;

public class CharsetValidationPatternTest extends KualiTestBase {
    private CharsetValidationPattern charsetPattern;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        charsetPattern = new CharsetValidationPattern();
    }

    @Test public final void testSetBoth_AB() {
        boolean failedAsExpected = false;

        try {
            charsetPattern.setMaxLength(5);
            charsetPattern.setExactLength(5);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public final void testSetBoth_BA() {
        boolean failedAsExpected = false;

        try {
            charsetPattern.setExactLength(5);
            charsetPattern.setMaxLength(5);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public final void testMatch_exactLength1() {
        charsetPattern.setExactLength(3);
        charsetPattern.setValidChars("abc");

        assertFalse(charsetPattern.matches("aaaa"));
    }

    @Test public final void testMatch_exactLength2() {
        charsetPattern.setExactLength(3);
        charsetPattern.setValidChars("abc");

        assertFalse(charsetPattern.matches("aa"));
    }

    @Test public final void testMatch_exactLength3() {
        charsetPattern.setExactLength(3);
        charsetPattern.setValidChars("abc");

        assertTrue(charsetPattern.matches("aaa"));
    }


    @Test public final void testMatch_maxLength1() {
        charsetPattern.setMaxLength(3);
        charsetPattern.setValidChars("abc");

        assertFalse(charsetPattern.matches("aaaa"));
    }

    @Test public final void testMatch_maxLength2() {
        charsetPattern.setMaxLength(3);
        charsetPattern.setValidChars("abc");

        assertTrue(charsetPattern.matches("aa"));
    }

    @Test public final void testMatch_maxLength3() {
        charsetPattern.setMaxLength(3);
        charsetPattern.setValidChars("abc");

        assertTrue(charsetPattern.matches("aaa"));
    }


    @Test public final void testSetValidChars_emptyString() {
        boolean failedAsExpected = false;

        try {
            charsetPattern.setValidChars("");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }


    @Test public final void testMatch_trailingSlash1() {
        charsetPattern.setValidChars("abcd\\");

        assertTrue(charsetPattern.matches("a"));
    }

    @Test public final void testMatch_trailingSlash2() {
        charsetPattern.setValidChars("abcd\\");

        assertTrue(charsetPattern.matches("c"));
    }

    @Test public final void testMatch_trailingSlash3() {
        charsetPattern.setValidChars("abcd\\");

        assertTrue(charsetPattern.matches("\\"));
    }


    @Test public final void testMatch_pseudoSet1() {
        charsetPattern.setValidChars("[A-Z]");

        assertTrue(charsetPattern.matches("A"));
    }

    @Test public final void testMatch_pseudoSet2() {
        charsetPattern.setValidChars("[A-Z]");

        assertTrue(charsetPattern.matches("Z"));
    }

    @Test public final void testMatch_pseudoSet3() {
        charsetPattern.setValidChars("[A-Z]");

        assertTrue(charsetPattern.matches("-"));
    }

    @Test public final void testMatch_pseudoSet4() {
        charsetPattern.setValidChars("[A-Z]");

        assertTrue(charsetPattern.matches("["));
    }

    @Test public final void testMatch_pseudoSet5() {
        charsetPattern.setValidChars("[A-Z]");

        assertFalse(charsetPattern.matches("C"));
    }


    @Test public final void testMatch_partialPseudoSet1() {
        charsetPattern.setValidChars("[ABC");

        assertTrue(charsetPattern.matches("A"));
    }

    @Test public final void testMatch_partialPseudoSet2() {
        charsetPattern.setValidChars("[ABC");

        assertFalse(charsetPattern.matches("Z"));
    }


    @Test public final void testMatch_pseudoSetTrailingSlash1() {
        charsetPattern.setValidChars("[A-Z]\\");

        assertTrue(charsetPattern.matches("A"));
    }

    @Test public final void testMatch_pseudoSetTrailingSlash2() {
        charsetPattern.setValidChars("[A-Z]\\");

        assertTrue(charsetPattern.matches("Z"));
    }

    @Test public final void testMatch_pseudoSetTrailingSlash3() {
        charsetPattern.setValidChars("[A-Z]\\");

        assertTrue(charsetPattern.matches("-"));
    }

    @Test public final void testMatch_pseudoSetTrailingSlash4() {
        charsetPattern.setValidChars("[A-Z]\\");

        assertTrue(charsetPattern.matches("["));
    }

    @Test public final void testMatch_pseudoSetTrailingSlash5() {
        charsetPattern.setValidChars("[A-Z]\\");

        assertFalse(charsetPattern.matches("C"));
    }


    @Test public final void testMatch_pseudoCapture1() {
        charsetPattern.setValidChars("(ABC)");

        assertTrue(charsetPattern.matches("("));
    }

    @Test public final void testMatch_pseudoCapture2() {
        charsetPattern.setValidChars("(ABC)");

        assertTrue(charsetPattern.matches(")"));
    }

    @Test public final void testMatch_pseudoCapture3() {
        charsetPattern.setValidChars("(ABC)");

        assertTrue(charsetPattern.matches("B"));
    }


    @Test public final void testMatch_pseudoRange1() {
        charsetPattern.setValidChars("A-Z");

        assertTrue(charsetPattern.matches("A"));
    }

    @Test public final void testMatch_pseudoRange2() {
        charsetPattern.setValidChars("A-Z");

        assertTrue(charsetPattern.matches("-"));
    }

    @Test public final void testMatch_pseudoRange3() {
        charsetPattern.setValidChars("A-Z");

        assertFalse(charsetPattern.matches("B"));
    }


    @Test public final void testMatch_pseudoIntersection1() {
        charsetPattern.setValidChars("A&&Z");

        assertTrue(charsetPattern.matches("A"));
    }

    @Test public final void testMatch_pseudoIntersection2() {
        charsetPattern.setValidChars("A&&Z");

        assertTrue(charsetPattern.matches("&"));
    }

    @Test public final void testMatch_pseudoIntersection3() {
        charsetPattern.setValidChars("A&&Z");

        assertTrue(charsetPattern.matches("Z"));
    }
}
