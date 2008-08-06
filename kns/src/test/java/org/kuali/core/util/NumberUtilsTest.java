/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.util;

import org.junit.Test;
import org.kuali.rice.test.KNSTestCase;

/**
 * This class tests the NumberUtils methods.
 */
public class NumberUtilsTest extends KNSTestCase {

    @Test public void testIntValue_nullInteger() {
        Integer testInteger = null;
        int testInt = 123;

        int intValue = NumberUtils.intValue(testInteger, testInt);
        assertEquals(intValue, testInt);
    }

    @Test public void testIntValue_nonNullInteger() {
        Integer testInteger = new Integer(456);
        int testInt = 123;

        int intValue = NumberUtils.intValue(testInteger, testInt);
        assertEquals(intValue, 456);
    }


    @Test public void testIntegerEquals_bothNull() {
        assertTrue(NumberUtils.equals((Integer) null, (Integer) null));
    }

    @Test public void testIntegerEquals_bothNonNull_inequal() {
        Integer i = new Integer(0);
        Integer j = new Integer(1);

        assertFalse(NumberUtils.equals(i, j));
    }

    @Test public void testIntegerEquals_bothNonNull_equal() {
        Integer i = new Integer(2);
        Integer j = new Integer(2);

        assertTrue(NumberUtils.equals(i, j));
    }

    @Test public void testIntegerEquals_firstNotNull() {
        Integer i = new Integer(3);
        Integer j = null;

        assertFalse(NumberUtils.equals(i, j));
    }

    @Test public void testIntegerEquals_secondNotNull() {
        Integer i = null;
        Integer j = new Integer(4);

        assertFalse(NumberUtils.equals(i, j));
    }

    @Test public void testKualiDecimalEquals_bothNull() {
        assertTrue(NumberUtils.equals((KualiDecimal) null, (KualiDecimal) null));
    }

    @Test public void testKualiDecimalEquals_bothNonNull_inequal() {
        KualiDecimal i = KualiDecimal.ZERO;
        KualiDecimal j = new KualiDecimal(1);

        assertFalse(NumberUtils.equals(i, j));
    }

    @Test public void testKualiDecimalEquals_bothNonNull_equal() {
        KualiDecimal i = new KualiDecimal(2);
        KualiDecimal j = new KualiDecimal(2);

        assertTrue(NumberUtils.equals(i, j));
    }

    @Test public void testKualiDecimalEquals_firstNotNull() {
        KualiDecimal i = new KualiDecimal(3);
        KualiDecimal j = null;

        assertFalse(NumberUtils.equals(i, j));
    }

    @Test public void testKualiDecimalEquals_secondNotNull() {
        KualiDecimal i = null;
        KualiDecimal j = new KualiDecimal(4);

        assertFalse(NumberUtils.equals(i, j));
    }
}
