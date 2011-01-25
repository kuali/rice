/*
 * Copyright 2006-2008 The Kuali Foundation
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
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.core.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.test.KNSTestCase;

import java.text.NumberFormat;

import static org.junit.Assert.*;

public class CurrencyFormatterTest extends KNSTestCase {

    CurrencyFormatter f = new CurrencyFormatter();

    @Test public void testConvertToObject_null() {
        String s = (String) f.convertToObject(null);

        assertNull(s);
    }

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


    // sorry, the names are just going to have to be vague
    @Test public void testConvertToObject01() {
        boolean failedAsExpected = false;

        try {
            f.convertToObject("9aaaaaaaaaaaa");
        }
        catch (FormatException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testConvertToObject02() {
        boolean failedAsExpected = false;

        try {
            f.convertToObject("aaaaaaaaaaaa9");
        }
        catch (FormatException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testConvertToObject03() {
        boolean failedAsExpected = false;

        try {
            f.convertToObject("9aaaaaaaaaaaa9");
        }
        catch (FormatException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * Demonstrates that trying to format a string with excessive decimal places will fail.
     */
    @Test public void testConvertToObject04() {
        String stringValue = "1.9999999";

        boolean failedAsExpected = false;
        try {
            f.convertToObject(stringValue);
        }
        catch (FormatException e) {
            failedAsExpected = true;
            assertEquals(RiceKeyConstants.ERROR_CURRENCY_DECIMAL, e.getErrorKey());
        }
        assertTrue(failedAsExpected);
    }

    /**
     * Demonstrates that trying to convert a KualiDecimal which has 0, 1, or 2 digits after the decimal place succeeds.
     */
    @Test public void testConvertToObject05() {
        String zeroDigitString = "0.";
        String oneDigitString = "0.0";
        String twoDigitString = "0.00";

        KualiDecimal zeroDigits = (KualiDecimal) f.convertToObject(zeroDigitString);
        assertEquals(KualiDecimal.ZERO, zeroDigits);

        KualiDecimal oneDigit = (KualiDecimal) f.convertToObject(oneDigitString);
        assertEquals(KualiDecimal.ZERO, oneDigit);

        KualiDecimal twoDigit = (KualiDecimal) f.convertToObject(twoDigitString);
        assertEquals(KualiDecimal.ZERO, twoDigit);

    }

    /**
     * this test is related to [KULEDOCS-742] test that the correct value is returned for the NumberFormat instance used by
     * <code>CurrencyFormatter</code> when parsing large numbers
     * 
     * @throws Exception
     */
    @Test public void testRealBigNumberFormat_parse() throws Exception {
        String number = "12345678901234567899.00";
        NumberFormat formatter = CurrencyFormatter.getCurrencyInstanceUsingParseBigDecimal();
        Number parsedNumber = formatter.parse("$" + number);
        KualiDecimal newNumber = new KualiDecimal(parsedNumber.toString());

        assertEquals(number, newNumber.toString());
    }

    /**
     * this relates to [KULEDOCS-742] test that the correct value is returned for <code>CurrencyFormatter</code> format method
     * using large numbers
     * 
     * @see CurrencyFormatter#format(java.lang.Object)
     * @throws Exception
     */
    @Test public void testRealBigNumberFormat_format() throws Exception {
        String stringNumber = "12345678901234567899";
        String expected = "12,345,678,901,234,567,899.00";

        KualiDecimal number = new KualiDecimal(stringNumber);
        Object parsedNumber = f.format(number);


        assertEquals(expected, parsedNumber);
    }


    /**
     * this relates to [KULEDOCS-742] test that the correct value is returned for <code>CurrencyFormatter</code> format method
     * using large numbers
     * 
     * @see CurrencyFormatter#format(java.lang.Object)
     * @throws Exception
     */
    @Test public void testRealBigDecimalNumberFormat_format() throws Exception {
        String stringNumber = "12345678901234567899.12";
        String expected = "12,345,678,901,234,567,899.12";

        KualiDecimal number = new KualiDecimal(stringNumber);
        Object parsedNumber = f.format(number);

        assertEquals(expected, parsedNumber);
    }
}
