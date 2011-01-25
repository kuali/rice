/*
 * Copyright 2007-2008 The Kuali Foundation
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
import org.kuali.rice.core.web.format.BigDecimalFormatter;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.test.KNSTestCase;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class BigDecimalFormatterTest extends KNSTestCase {

    BigDecimalFormatter f = new BigDecimalFormatter();

    /**
     * This method tests for null if object is null
     */
    @Test public void testConvertToObject_null() {
        String s = (String) f.convertToObject(null);

        assertNull(s);
    }

    /**
     * This method tests that blank is not allowed
     */
    @Test public void testConvertToObject_blank() {
        boolean failedAsExpected = false;

        try {
            f.convertToObject("    ");
        }
        catch (FormatException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * This method tests that invalid characters at the beginning fail
     */
    @Test public void testNonAllowedCharacterEnd() {
        boolean failedAsExpected = false;

        try {
            f.convertToObject("9a");
            f.convertToObject("1$");
            f.convertToObject("1%");
        }
        catch (FormatException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * This method tests that invalid characters at the end fail
     */
    @Test public void testNonAllowedCharacterBeginning() {
        boolean failedAsExpected = false;

        try {
            f.convertToObject("a9");
            f.convertToObject("$1");
            f.convertToObject("%1");
        }
        catch (FormatException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * This method tests that invalid characters in the middle fail
     */
    @Test public void testNonAllowedCharacterMiddle() {
        boolean failedAsExpected = false;

        try {
            f.convertToObject("9a9");
            f.convertToObject("1$1");
            f.convertToObject("1%1");
        }
        catch (FormatException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * This method tests that invalid characters in the middle fail
     */
    @Test public void testMultipleofSingleChars() {
        boolean failedAsExpected = false;

        try {
            f.convertToObject("9..9");
            f.convertToObject("-92-");
            f.convertToObject(".9.");
        }
        catch (FormatException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * This method tests big number with precision
     */
    @Test public void testBigNumberWithBigScale() {
        String number = "39,483,098,405,908,498,608,560,954,654.546098540698546809456098450968";
        BigDecimal num = null;
        // should throw exception if problem here
        num = (BigDecimal) f.convertToObject(number);

        String bdNumber = (String) f.format(num);
        assertEquals(number, bdNumber);
    }

    /**
     * This method tests big number with precision
     */
    @Test public void testBigScale() {
        String number = "0.5460985406985468094560984509683423432546547854484575464565453454234234523";
        BigDecimal num = null;
        // should throw exception if problem here
        num = (BigDecimal) f.convertToObject(number);

        String bdNumber = (String) f.format(num);
        assertEquals(number, bdNumber);
    }

    /**
     * This method tests big number with precision
     */
    @Test public void testBigNoScale() {
        String number = "5,460,985,406,985,468,094,560,984,509,683,423,432,546,547,854,484,575,464,565,453,454,234,234,523";
        BigDecimal num = null;
        // should throw exception if problem here
        num = (BigDecimal) f.convertToObject(number);

        String bdNumber = (String) f.format(num);
        assertEquals(number, bdNumber);
    }

    /**
     * This method tests negative numbers
     */
    @Test public void testNegScale() {
        String number = "-5,460,985,406,985,468,094,560,984,509,683,423,432,546,547,854,484,575,464,565,453,454,234,234,523";
        BigDecimal num = null;
        // should throw exception if problem here
        num = (BigDecimal) f.convertToObject(number);

        String bdNumber = (String) f.format(num);
        assertEquals(number, bdNumber);
    }

    /**
     * This method tests trailing zeroes
     */
    @Test public void testTrailingZeroesOut() {
        String numberResult = "1.00";
        String inputNumber = "1";
        BigDecimal num = null;
        // should throw exception if problem here
        num = new BigDecimal(inputNumber).setScale(2, BigDecimal.ROUND_HALF_EVEN);

        String bdNumber = (String) f.format(num);
        assertEquals(numberResult, bdNumber);
    }
}
