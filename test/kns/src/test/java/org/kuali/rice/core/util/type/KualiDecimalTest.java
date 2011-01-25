/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.core.util.type;

import org.junit.Test;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.test.BaseRiceTestCase;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * This class tests the KualiDecimal methods.
 */
public class KualiDecimalTest extends BaseRiceTestCase {
    private static final int OPERAND_VALUE = 25;

    private KualiDecimal decimalValue1;
    private KualiDecimal decimalValue2;  
    private KualiDecimal result;

    private KualiDecimal operand;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        decimalValue1 = new KualiDecimal("15.97"); 
        decimalValue2 = new KualiDecimal("11.23");
        operand = new KualiDecimal(OPERAND_VALUE);
    }

    @Test public void testIntConstructor() throws Exception {
        final int INTVALUE = -1;

        KualiDecimal k = new KualiDecimal(INTVALUE);
        assertEquals(INTVALUE, k.intValue());
    }

    @Test public void testStringConstructor_nullString() throws Exception {
    	KualiDecimal k = new KualiDecimal((String)null);
    	assertEquals("When KualiDecimal passed null, it should be constructed as zero", 0, k.intValue());
    }

    @Test public void testStringConstructor_nonnumericString() throws Exception {
        boolean failedAsExpected = false;

        try {
            new KualiDecimal("n0nnum3r1c");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testStringConstructor_integerString() throws Exception {
        final String INTSTRING = "123";

        KualiDecimal k = new KualiDecimal(INTSTRING);
        assertEquals(Integer.valueOf(INTSTRING).intValue(), k.intValue());
    }

    @Test public void testStringConstructor_floatingPointString() throws Exception {
        final String DOUBLESTRING = "0.4567";

        KualiDecimal k = new KualiDecimal(DOUBLESTRING);
        assertEquals("0.46", k.toString());
    }


    @Test public void testDoubleConstructor_noSig() throws Exception {
        double value = 21;

        KualiDecimal k = new KualiDecimal(value);
        assertEquals(value, k.doubleValue(), 0);
    }

    @Test public void testDoubleConstructor_oneSig() throws Exception {
        double value = 21.1;

        KualiDecimal k = new KualiDecimal(value);
        assertEquals(value, k.doubleValue(), 0);
    }

    @Test public void testDoubleConstructor_twoSig() throws Exception {
        double value = 21.12;

        KualiDecimal k = new KualiDecimal(value);
        assertEquals(value, k.doubleValue(), 0);
    }

    @Test public void testDoubleConstructor_threeSig() throws Exception {
        double value = 21.123;
        double roundedValue = 21.12;

        KualiDecimal k = new KualiDecimal(value);
        assertEquals(roundedValue, k.doubleValue(), 0);
    }


    @Test public void testAdd_nullAddend() throws Exception {
        boolean failedAsExpected = false;

        try {
            this.operand.add(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testAdd_validAddend() throws Exception {
        KualiDecimal addend = new KualiDecimal(2);
        KualiDecimal sum = this.operand.add(addend);

        assertEquals(new KualiDecimal(OPERAND_VALUE + 2), sum);
    }

    @Test public void testSubtract_nullSubtrahend() throws Exception {
        boolean failedAsExpected = false;

        try {
            this.operand.subtract(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testSubtract_validSubtrahend() throws Exception {
        KualiDecimal subtrahend = new KualiDecimal(2);
        KualiDecimal difference = this.operand.subtract(subtrahend);

        assertEquals(new KualiDecimal(OPERAND_VALUE - 2), difference);
    }

    @Test public void testMultiply_nullMultiplier() throws Exception {
        boolean failedAsExpected = false;

        try {
            this.operand.multiply(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testMultiply_validMultiplier() throws Exception {
        KualiDecimal multiplier = new KualiDecimal(2);
        KualiDecimal product = this.operand.multiply(multiplier);

        assertEquals(new KualiDecimal(OPERAND_VALUE * 2), product);
    }

    @Test public void testDivide_nullDivisor() throws Exception {
        boolean failedAsExpected = false;

        try {
            this.operand.divide(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testDivide_validDivisor() throws Exception {
        KualiDecimal divisor = new KualiDecimal(2).setScale();
        KualiDecimal quotient = this.operand.divide(divisor).setScale();

        double expectedQuotient = OPERAND_VALUE / 2.0;

        assertEquals(expectedQuotient, quotient.doubleValue(), 0);
    }

    @Test public void testMod_nullModulus() throws Exception {
        boolean failedAsExcpected = false;

        try {
            this.operand.mod(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExcpected = true;
        }

        assertTrue(failedAsExcpected);
    }

    @Test public void testMod_validZeroModulus() throws Exception {
        // divide by some number to make sure that the two aren't the same
        KualiDecimal zeroModulus = this.operand.divide(new KualiDecimal(5));
        KualiDecimal expectedZero = this.operand.mod(zeroModulus);

        assertEquals(KualiDecimal.ZERO, expectedZero);
    }

    @Test public void testMod_validNonZeroModulus() throws Exception {
        KualiDecimal nonZeroModulus = this.operand.divide(new KualiDecimal(5)).add(new KualiDecimal(1));
        KualiDecimal expectedNonZero = this.operand.mod(nonZeroModulus);

        assertNotSame(KualiDecimal.ZERO, expectedNonZero);
    }

    @Test public void testNegative_zeroValue() {
        KualiDecimal zeroValue = KualiDecimal.ZERO;
        KualiDecimal negativeZeroValue = zeroValue.negated();

        assertEquals(zeroValue, negativeZeroValue);
    }

    @Test public void testNegative_negativeValue() {
        KualiDecimal negativeValue = new KualiDecimal(-4000);
        KualiDecimal negativeNegativeValue = negativeValue.negated();

        assertEquals(negativeValue.intValue() * -1, negativeNegativeValue.intValue());
    }

    @Test public void testNegative_positiveValue() {
        KualiDecimal positiveValue = new KualiDecimal(2112);
        KualiDecimal negativePositiveValue = positiveValue.negated();

        assertEquals(positiveValue.intValue() * -1, negativePositiveValue.intValue());
    }

    @Test public void testNegative_negativeDoubleValue() {
        KualiDecimal negativeValue = new KualiDecimal("-23.12");
        KualiDecimal negativeNegativeValue = negativeValue.negated();

        assertEquals(new Double(negativeValue.doubleValue() * -1.0), new Double(negativeNegativeValue.doubleValue()));
    }

    @Test public void testNegative_positiveDoubleValue() {
        KualiDecimal positiveValue = new KualiDecimal("12.34");
        KualiDecimal negativePositiveValue = positiveValue.negated();

        assertEquals(new Double(positiveValue.doubleValue() * -1.0), new Double(negativePositiveValue.doubleValue()));
    }


    @Test public void testEquals_inequalIntegerValues() {
        KualiDecimal v1 = new KualiDecimal(1);
        KualiDecimal v2 = new KualiDecimal(2);

        assertFalse(v1.equals(v2));
        assertFalse(v2.equals(v1));
    }

    @Test public void testEquals_equalIntegerValues() {
        KualiDecimal v1 = new KualiDecimal(3);
        KualiDecimal v2 = new KualiDecimal(3);

        assertTrue(v1.equals(v2));
        assertTrue(v2.equals(v1));
    }

    @Test public void testEquals_inequalDoubleValues() {
        KualiDecimal v1 = new KualiDecimal(1.0d);
        KualiDecimal v2 = new KualiDecimal(2.0d);

        assertFalse(v1.equals(v2));
        assertFalse(v2.equals(v1));
    }

    @Test public void testEquals_roughlyEqualDoubleValues() {
        KualiDecimal v1 = new KualiDecimal(6.03d);
        KualiDecimal v2 = new KualiDecimal(6.029997d);

        assertTrue(v1.equals(v2));
        assertTrue(v2.equals(v1));
    }

    @Test public void testEquals_equalDoubleValues() {
        KualiDecimal v1 = new KualiDecimal(6.03d);
        KualiDecimal v2 = new KualiDecimal(6.03d);

        assertTrue(v1.equals(v2));
        assertTrue(v2.equals(v1));
    }


    @Test public void testEquals_inequalStringValue() {
        KualiDecimal s1 = new KualiDecimal("100.00");
        KualiDecimal s2 = new KualiDecimal("100.01");

        assertFalse(s1.equals(s2));
        assertFalse(s2.equals(s1));
    }

    @Test public void testEquals_equalStringValues() {
        KualiDecimal s1 = new KualiDecimal("100.00");
        KualiDecimal s2 = new KualiDecimal("100.00");

        assertTrue(s1.equals(s2));
        assertTrue(s2.equals(s1));
    }

    @Test public void testEquals_equivalentValues() {
        KualiDecimal d1 = new KualiDecimal(100);
        KualiDecimal s1 = new KualiDecimal("100");

        assertTrue(d1.equals(s1));
        assertTrue(s1.equals(d1));
    }


    /**
     * The specific values used in this test case were copied from the code which was breaking because it was converting the
     * KualiDecimals into floats, adding the floats, and comparing them. The float addition is here to illustrate one of the
     * problems that requires us to do KualiDecimal math instead of converting back to a primitive format.
     */
    @Test public void testEquals_summedFloatValues() {

        // sum them as floats
        float c1 = 1.01f;
        float c2 = 3.00f;
        float c3 = 2.02f;

        float d1 = 4.02f;
        float d2 = 2.01f;

        float c = c1 + c2 + c3;
        float d = d1 + d2;

        assertFalse(c == d);


        // sum them as KualiDecimals built from Strings, as the JSP was doing
        KualiDecimal a1 = new KualiDecimal("1.01");
        KualiDecimal a2 = new KualiDecimal("3.00");
        KualiDecimal a3 = new KualiDecimal("2.02");

        KualiDecimal b1 = new KualiDecimal("4.02");
        KualiDecimal b2 = new KualiDecimal("2.01");

        KualiDecimal a = a1.add(a2).add(a3);
        KualiDecimal b = b1.add(b2);

        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
    }


    @Test public void testIsNegative_negativeValue() {
        KualiDecimal v1 = new KualiDecimal(-231);

        assertTrue(v1.isNegative());
    }

    @Test public void testIsNegative_positiveValue() {
        KualiDecimal v1 = new KualiDecimal(987);

        assertFalse(v1.isNegative());
    }

    @Test public void testIsNegative_zeroValue() {
        KualiDecimal v1 = KualiDecimal.ZERO;

        assertFalse(v1.isNegative());
    }


    @Test public void testIsPositive_negativeValue() {
        KualiDecimal v1 = new KualiDecimal(-231);

        assertFalse(v1.isPositive());
    }

    @Test public void testIsPositive_positiveValue() {
        KualiDecimal v1 = new KualiDecimal(987);

        assertTrue(v1.isPositive());
    }

    @Test public void testIsPositive_zeroValue() {
        KualiDecimal v1 = KualiDecimal.ZERO;

        assertFalse(v1.isPositive());
    }


    @Test public void testIsZero_negativeValue() {
        KualiDecimal v1 = new KualiDecimal(-231);

        assertFalse(v1.isZero());
    }

    @Test public void testIsZero_positiveValue() {
        KualiDecimal v1 = new KualiDecimal(987);

        assertFalse(v1.isZero());
    }

    @Test public void testIsZero_zeroValue() {
        KualiDecimal v1 = KualiDecimal.ZERO;

        assertTrue(v1.isZero());
    }


    @Test public void testIsNonZero_negativeValue() {
        KualiDecimal v1 = new KualiDecimal(-231);

        assertTrue(v1.isNonZero());
    }

    @Test public void testIsNonZero_positiveValue() {
        KualiDecimal v1 = new KualiDecimal(987);

        assertTrue(v1.isNonZero());
    }

    @Test public void testIsNonZero_zeroValue() {
        KualiDecimal v1 = KualiDecimal.ZERO;

        assertFalse(v1.isNonZero());
    }


    @Test public void testIsLessThan_nullOperand() {
        boolean failedAsExpected = false;

        KualiDecimal v1 = new KualiDecimal(123);
        try {
            v1.isLessThan(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testIsLessThan_greaterOperand() {
        KualiDecimal v1 = new KualiDecimal(123);
        KualiDecimal v2 = new KualiDecimal(456);

        assertTrue(v1.isLessThan(v2));
    }

    @Test public void testIsLessThan_equalOperand() {
        KualiDecimal v1 = new KualiDecimal(456);
        KualiDecimal v2 = new KualiDecimal(456);

        assertFalse(v1.isLessThan(v2));
    }

    @Test public void testIsLessThan_lesserOperand() {
        KualiDecimal v1 = new KualiDecimal(789);
        KualiDecimal v2 = new KualiDecimal(345);

        assertFalse(v1.isLessThan(v2));
    }


    @Test public void testIsGreaterThan_nullOperand() {
        boolean failedAsExpected = false;

        KualiDecimal v1 = new KualiDecimal(123);
        try {
            v1.isGreaterThan(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testIsGreaterThan_greaterOperand() {
        KualiDecimal v1 = new KualiDecimal(123);
        KualiDecimal v2 = new KualiDecimal(456);

        assertTrue(v2.isGreaterThan(v1));
    }

    @Test public void testIsGreaterThan_equalOperand() {
        KualiDecimal v1 = new KualiDecimal(456);
        KualiDecimal v2 = new KualiDecimal(456);

        assertFalse(v1.isGreaterThan(v2));
    }

    @Test public void testIsGreaterThan_lesserOperand() {
        KualiDecimal v1 = new KualiDecimal(789);
        KualiDecimal v2 = new KualiDecimal(345);

        assertFalse(v2.isGreaterThan(v1));
    }


    @Test public void testIsLessEqual_nullOperand() {
        boolean failedAsExpected = false;

        KualiDecimal v1 = new KualiDecimal(123);
        try {
            v1.isLessEqual(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testIsLessEqual_greaterOperand() {
        KualiDecimal v1 = new KualiDecimal(123);
        KualiDecimal v2 = new KualiDecimal(456);

        assertTrue(v1.isLessEqual(v2));
    }

    @Test public void testIsLessEqual_equalOperand() {
        KualiDecimal v1 = new KualiDecimal(456);
        KualiDecimal v2 = new KualiDecimal(456);

        assertTrue(v1.isLessEqual(v2));
    }

    @Test public void testIsLessEqual_lesserOperand() {
        KualiDecimal v1 = new KualiDecimal(789);
        KualiDecimal v2 = new KualiDecimal(345);

        assertFalse(v1.isLessEqual(v2));
    }


    @Test public void testIsGreaterEqual_nullOperand() {
        boolean failedAsExpected = false;

        KualiDecimal v1 = new KualiDecimal(123);
        try {
            v1.isGreaterEqual(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testIsGreaterEqual_greaterOperand() {
        KualiDecimal v1 = new KualiDecimal(123);
        KualiDecimal v2 = new KualiDecimal(456);

        assertTrue(v2.isGreaterEqual(v1));
    }

    @Test public void testIsGreaterEqual_equalOperand() {
        KualiDecimal v1 = new KualiDecimal(456);
        KualiDecimal v2 = new KualiDecimal(456);

        assertTrue(v1.isGreaterEqual(v2));
    }

    @Test public void testIsGreaterEqual_lesserOperand() {
        KualiDecimal v1 = new KualiDecimal(789);
        KualiDecimal v2 = new KualiDecimal(345);

        assertFalse(v2.isGreaterEqual(v1));
    }


    @Test public void testIsNumeric_null() {
        assertFalse(KualiDecimal.isNumeric(null));
    }

    @Test public void testIsNumeric_blank() {
        assertFalse(KualiDecimal.isNumeric("   "));
    }

    @Test public void testIsNumeric_alphanumeric() {
        assertFalse(KualiDecimal.isNumeric("12YYZ23"));
    }

    @Test public void testIsNumeric_integral() {
        assertTrue(KualiDecimal.isNumeric("1234"));
    }

    @Test public void testIsNumeric_decimal() {
        assertTrue(KualiDecimal.isNumeric("1234.56"));
    }

    @Test public void testIsNumeric_moreSignificantDecimal() {
        assertTrue(KualiDecimal.isNumeric("1234.56789"));
    }

    @Test public void testIsNumeric_negativeIntegral() {
        assertTrue(KualiDecimal.isNumeric("-1234"));
    }

    @Test public void testIsNumeric_negativeDecimal() {
        assertTrue(KualiDecimal.isNumeric("-1234.56"));
    }

    @Test public void testIsNumeric_zero() {
        assertTrue(KualiDecimal.isNumeric("0"));
    }

    @Test public void testIsNumeric_multiZero() {
        assertTrue(KualiDecimal.isNumeric("0000"));
    }

    @Test public void testIsNumeric_negativeZero() {
        assertTrue(KualiDecimal.isNumeric("-0"));
    }

    @Test public void testIsNumeric_decimalZero() {
        assertTrue(KualiDecimal.isNumeric("0.0"));
    }

    @Test public void testIsNumeric_negativeDecimalZero() {
        assertTrue(KualiDecimal.isNumeric("-0.00"));
    }
    
    @Test 
    public void testKualiDecimal_Add() throws Exception {
        result = decimalValue1.add(decimalValue2);
        assertEquals(new BigDecimal("27.2").setScale(2), result.value);
        
        KualiDecimal modifiedResult = new KualiDecimal(result.doubleValue(), 3);
        assertEquals(new BigDecimal("27.2").setScale(3), modifiedResult.value);
    }

    @Test 
    public void testKualiDecimal_Subtract() throws Exception {
        result = decimalValue1.subtract(decimalValue2);
        assertEquals(new BigDecimal("4.74").setScale(2), result.value);
        
        KualiDecimal modifiedResult = new KualiDecimal(result.doubleValue(), 3);
        assertEquals(new BigDecimal("4.74").setScale(3), modifiedResult.value);
    }

    @Test 
    public void testKualiDecimal_Multiply() throws Exception {
        result = decimalValue1.multiply(decimalValue2);
        assertEquals(new BigDecimal("179.3431").setScale(2, AbstractKualiDecimal.ROUND_BEHAVIOR), result.value);
        
        KualiDecimal modifiedResult = new KualiDecimal(result.doubleValue(), 3);
        assertEquals(new BigDecimal("179.3431").setScale(2, AbstractKualiDecimal.ROUND_BEHAVIOR).setScale(3), modifiedResult.value);
    }

    @Test 
    public void testKualiDecimal_Divide() throws Exception {
        result = decimalValue1.divide(decimalValue2);
        assertEquals(new BigDecimal("1.4220").setScale(2, AbstractKualiDecimal.ROUND_BEHAVIOR), result.value);
        
        KualiDecimal modifiedResult = new KualiDecimal(result.doubleValue(), 3);
        assertEquals(new BigDecimal("1.4220").setScale(2, AbstractKualiDecimal.ROUND_BEHAVIOR).setScale(3), modifiedResult.value);
    }

    @Test 
    public void testKualiDecimal_Divide_Overridden() throws Exception {
        KualiDecimal operand1 = new KualiDecimal("100", 3);
        KualiDecimal operand2 = new KualiDecimal("365", 3); 
        result = operand1.divide(operand2, false).multiply(operand2, false); 
        assertEquals(new KualiDecimal("100.01"), new KualiDecimal(result.bigDecimalValue().setScale(2, BigDecimal.ROUND_HALF_UP), 2));
    }

    @Test 
    public void testKualiDecimal_Mod() throws Exception {
        result = decimalValue1.mod(decimalValue2);
        assertEquals(new BigDecimal("4.74").setScale(2), result.value);
        
        KualiDecimal modifiedResult = new KualiDecimal(result.doubleValue(), 3);
        assertEquals(new BigDecimal("4.74").setScale(3), modifiedResult.value);
    }

    @Test 
    public void testKualiDecimal_Negated() throws Exception {
        result = decimalValue1.negated();
        assertEquals(new BigDecimal("-15.97").setScale(2), result.value);
        
        KualiDecimal modifiedResult = new KualiDecimal(result.doubleValue(), 3);
        assertEquals(new BigDecimal("-15.97").setScale(3), modifiedResult.value);
    }

    @Test 
    public void testKualiDecimal_isNegative() throws Exception {
        result = decimalValue1.negated();
        assertEquals(new BigDecimal("-15.97").setScale(2), result.value);
        assertEquals(true, result.isNegative());
    }

    @Test 
    public void testKualiDecimal_isPositive() throws Exception {  
        assertEquals(true, decimalValue1.isPositive());
    }

    @Test 
    public void testKualiDecimal_isZero() throws Exception {
        assertEquals(false, decimalValue1.isZero());
        
        result = decimalValue1.multiply(KualiDecimal.ZERO);
        assertEquals(true, result.isZero());
    }

    @Test 
    public void testKualiDecimal_abs() throws Exception {
        result = decimalValue1.abs();
        assertEquals(result.value, decimalValue1.value);
        
        result = decimalValue1.negated().abs();    
        assertEquals(decimalValue1.value, result.value);
    }

    @Test 
    public void testKualiDecimal_isNonZero() throws Exception {
        assertEquals(true, decimalValue1.isNonZero());
        
        result = decimalValue1.multiply(KualiDecimal.ZERO);
        assertEquals(false, result.isNonZero());
    }

    @Test 
    public void testKualiDecimal_isLessThan() throws Exception {
        assertEquals(false, decimalValue1.isLessThan(decimalValue2));
        
        result = decimalValue1.negated();
        assertEquals(true, result.isLessThan(decimalValue1));
    }

    @Test 
    public void testKualiDecimal_isGreaterThan() throws Exception {
        assertEquals(true, decimalValue1.isGreaterThan(decimalValue2));
        assertEquals(false, decimalValue2.isGreaterThan(decimalValue1));
   }
    
    @Test 
    public void testKualiDecimal_isLessEqual() throws Exception {
        assertEquals(false, decimalValue1.isLessEqual(decimalValue2));
        assertEquals(true, decimalValue2.isLessEqual(decimalValue1));
        assertEquals(true, decimalValue1.isLessEqual(decimalValue1));
    }    

    @Test 
    public void testKualiDecimal_isGreaterEqual() throws Exception {
        assertEquals(true, decimalValue1.isGreaterEqual(decimalValue2));
        assertEquals(false, decimalValue2.isGreaterEqual(decimalValue1));
        assertEquals(true, decimalValue1.isGreaterEqual(decimalValue1));
    }    

    @Test 
    public void testKualiDecimal_isNumeric() throws Exception {
        assertEquals(true, KualiDecimal.isNumeric("10.234"));
        assertEquals(false, KualiDecimal.isNumeric("10.df"));
    }    

    @Test 
    public void testKualiDecimal_compareTo() throws Exception {
        assertEquals(1, decimalValue1.compareTo(decimalValue2));
        assertEquals(0, decimalValue1.compareTo(decimalValue1));
        assertEquals(-1, decimalValue2.compareTo(decimalValue1));
    }    

    @Test 
    public void testKualiDecimal_displayFormat() throws Exception {
        Formatter testFormatter = Formatter.getFormatter(KualiDecimal.class, null);
        System.out.println(testFormatter); 
        
        KualiDecimal operand1;
        KualiDecimal operand2;
        
        operand1 = new KualiDecimal("100");
        operand2 = new KualiDecimal("365"); 
        result = operand1.divide(operand2).multiply(operand2);
        assertEquals(result.toString(), testFormatter.format(result));
        CurrencyFormatter currencyFormatter = new CurrencyFormatter();
        assertEquals(currencyFormatter.format(result), testFormatter.format(result));
        
        operand1 = new KualiDecimal("100", 3);
        operand2 = new KualiDecimal("365", 3); 
        result = operand1.divide(operand2, false).multiply(operand2, false);
        
        //CurrencyDecimal cannot be used with KualiDecimal objects of scale > 2.
        try {
            currencyFormatter.format(result);
        } catch (FormatException e) {
            assertEquals("parsing, error.currency.decimal["+ result.bigDecimalValue() + "]", e.getMessage());
        }
    }    

}
