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
import org.kuali.test.KNSTestCase;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class tests the TypeUtils methods.
 */
public class TypeUtilsTest extends KNSTestCase {

    @Test public void testIsBooleanClass_nullClass() {
        boolean failedAsExpected = false;

        try {
            TypeUtils.isBooleanClass(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testIsBooleanClass_nonBooleanClass() {
        TypeUtils.isBooleanClass(getFieldClass("string"));
    }

    @Test public void testIsBooleanClass_BooleanClass() {
        assertTrue(TypeUtils.isBooleanClass(getFieldClass("booleanWrapper")));
    }

    @Test public void testIsBooleanClass_booleanClass() {
        assertTrue(TypeUtils.isBooleanClass(getFieldClass("booleanPrimitive")));
    }

    @Test public void testIsIntegralClass_nullClass() {
        boolean failedAsExpected = false;

        try {
            TypeUtils.isIntegralClass(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testIsIntegralClass_nonIntegralClass() {
        assertFalse(TypeUtils.isIntegralClass(getFieldClass("string")));
    }

    @Test public void testIsIntegralClass_ByteClass() {
        assertTrue(TypeUtils.isIntegralClass(getFieldClass("byteWrapper")));
    }

    @Test public void testIsIntegralClass_byteClass() {
        assertTrue(TypeUtils.isIntegralClass(getFieldClass("bytePrimitive")));
    }

    @Test public void testIsIntegralClass_ShortClass() {
        assertTrue(TypeUtils.isIntegralClass(getFieldClass("shortWrapper")));
    }

    @Test public void testIsIntegralClass_shortClass() {
        assertTrue(TypeUtils.isIntegralClass(getFieldClass("shortPrimitive")));
    }

    @Test public void testIsIntegralClass_IntegerClass() {
        assertTrue(TypeUtils.isIntegralClass(getFieldClass("integerWrapper")));

    }

    @Test public void testIsIntegralClass_integerClass() {
        assertTrue(TypeUtils.isIntegralClass(getFieldClass("integerPrimitive")));
    }

    @Test public void testIsIntegralClass_LongClass() {
        assertTrue(TypeUtils.isIntegralClass(getFieldClass("longWrapper")));
    }

    @Test public void testIsIntegralClass_longClass() {
        assertTrue(TypeUtils.isIntegralClass(getFieldClass("longPrimitive")));
    }

    @Test public void testIsIntegralClass_BigIntegerClass() {
        assertTrue(TypeUtils.isIntegralClass(getFieldClass("bigInteger")));
    }

    @Test public void testIsDecimalClass_nullClass() {
        boolean failedAsExpected = false;

        try {
            TypeUtils.isDecimalClass(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testIsDecimalClass_nonDecimalClass() {
        assertFalse(TypeUtils.isDecimalClass(getFieldClass("string")));
    }

    @Test public void testIsDecimalClass_FloatClass() {
        assertTrue(TypeUtils.isDecimalClass(getFieldClass("floatWrapper")));
    }

    @Test public void testIsDecimalClass_floatClass() {
        assertTrue(TypeUtils.isDecimalClass(getFieldClass("floatPrimitive")));
    }

    @Test public void testIsDecimalClass_DoubleClass() {
        assertTrue(TypeUtils.isDecimalClass(getFieldClass("doubleWrapper")));
    }

    @Test public void testIsDecimalClass_doubleClass() {
        assertTrue(TypeUtils.isDecimalClass(getFieldClass("doublePrimitive")));
    }

    @Test public void testIsDecimalClass_BigDecimalClass() {
        assertTrue(TypeUtils.isDecimalClass(getFieldClass("bigDecimal")));
    }

    @Test public void testIsDecimalClass_KualiDecimalClass() {
        assertTrue(TypeUtils.isDecimalClass(getFieldClass("kualiDecimal")));
    }

    @Test public void testIsTemporalClass_nullClass() {
        boolean failedAsExpected = false;

        try {
            TypeUtils.isTemporalClass(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testIsTemporalClass_nonTemporalClass() {
        assertFalse(TypeUtils.isTemporalClass(getFieldClass("string")));
    }

    @Test public void testIsTemporalClass_utilDateClass() {
        assertTrue(TypeUtils.isTemporalClass(getFieldClass("utilDate")));
    }

    @Test public void testIsTemporalClass_sqlDateClass() {
        assertTrue(TypeUtils.isTemporalClass(getFieldClass("sqlDate")));
    }

    @Test public void testIsTemporalClass_sqlTimestampClass() {
        assertTrue(TypeUtils.isTemporalClass(getFieldClass("sqlTimestamp")));
    }


    /**
     * @param fieldName
     * @return Class of the named instance field of the given object
     */
    private Class getFieldClass(String fieldName) {
        Class fieldClass = null;

        try {
            Field field = TestClass.class.getField(fieldName);
            fieldClass = field.getType();
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException("unable to retrieve fieldClass for field '" + fieldName + "'");
        }

        return fieldClass;
    }

    /**
     * Instance fields of every type I want to test with
     */
    private static class TestClass {
        public String string;
        public Boolean booleanWrapper;
        public boolean booleanPrimitive;
        public Byte byteWrapper;
        public byte bytePrimitive;
        public Short shortWrapper;
        public short shortPrimitive;
        public Integer integerWrapper;
        public int integerPrimitive;
        public Long longWrapper;
        public long longPrimitive;
        public BigInteger bigInteger;
        public Float floatWrapper;
        public float floatPrimitive;
        public Double doubleWrapper;
        public double doublePrimitive;
        public BigDecimal bigDecimal;
        public KualiDecimal kualiDecimal;
        public java.util.Date utilDate;
        public java.sql.Date sqlDate;
        public java.sql.Timestamp sqlTimestamp;
    }
}
