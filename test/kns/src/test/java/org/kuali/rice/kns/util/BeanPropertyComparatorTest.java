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
package org.kuali.rice.kns.util;

import org.junit.Test;
import org.kuali.rice.kns.util.BeanPropertyComparator.BeanComparisonException;
import org.kuali.test.KNSTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * This class tests the BeanPropertyComparator methods.
 */
public class BeanPropertyComparatorTest extends KNSTestCase {

    @Test public void testConstructor_nullList() {
        boolean failedAsExpected = false;

        try {
            new BeanPropertyComparator(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public void testConstructor_emptyList() {
        boolean failedAsExpected = false;
        try {
            new BeanPropertyComparator(new ArrayList());
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    @Test public void testCompare_unknownPropertyNames() {
        List unknownProperties = Arrays.asList(new String[] { "one", "two", "three" });

        BeanPropertyComparator bpc = new BeanPropertyComparator(unknownProperties);
        A a = new A("something", new Integer(0), Boolean.valueOf(false));
        B b = new B("something else", new Integer(1), Boolean.valueOf(true));


        boolean failedAsExpected = false;
        try {
            bpc.compare(a, b);
        }
        catch (BeanComparisonException e) {
            if (e.getCause() instanceof NullPointerException) {
                failedAsExpected = true;
            }
        }
        assertTrue(failedAsExpected);
    }

    @Test public void testCompare_propertyTypeMismatch() {
        List mismatchedProperties = Arrays.asList(new String[] { "i", "b" });

        BeanPropertyComparator bpc = new BeanPropertyComparator(mismatchedProperties);
        A a = new A("something", new Integer(0), Boolean.valueOf(false));
        C c = new C("something else", 1, true);


        boolean failedAsExpected = false;
        try {
            bpc.compare(a, c);
        }
        catch (ClassCastException e) {
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    @Test public void testCompare_privateProperty() {
        List privateProperty = Arrays.asList(new String[] { "s" });

        BeanPropertyComparator bpc = new BeanPropertyComparator(privateProperty);
        C c = new C("something else", 1, true);
        A a = new A("something", new Integer(0), Boolean.valueOf(false));


        boolean failedAsExpected = false;
        try {
            bpc.compare(c, a);
        }
        catch (BeanComparisonException e) {
            if (e.getCause() instanceof NullPointerException) {
                failedAsExpected = true;
            }
        }
        assertTrue(failedAsExpected);
    }


    @Test public void testCompare_oneProperty_string() {
        List properties = Arrays.asList(new String[] { "s" });

        BeanPropertyComparator bpc = new BeanPropertyComparator(properties);
        A lesser = new A("One", new Integer(0), Boolean.valueOf(false));
        B greater = new B("Two", new Integer(0), Boolean.valueOf(false));

        int lessThan = bpc.compare(lesser, greater);
        assertTrue(lessThan < 0);

        int greaterThan = bpc.compare(greater, lesser);
        assertTrue(greaterThan > 0);

        int equal = bpc.compare(greater, greater);
        assertTrue(equal == 0);
    }

    @Test public void testCompare_oneProperty_integer() {
        List properties = Arrays.asList(new String[] { "i" });

        BeanPropertyComparator bpc = new BeanPropertyComparator(properties);
        A lesser = new A("One", new Integer(-1), Boolean.valueOf(false));
        B greater = new B("One", new Integer(1), Boolean.valueOf(false));

        int lessThan = bpc.compare(lesser, greater);
        assertTrue(lessThan < 0);

        int greaterThan = bpc.compare(greater, lesser);
        assertTrue(greaterThan > 0);

        int equal = bpc.compare(greater, greater);
        assertTrue(equal == 0);
    }

    @Test public void testCompare_oneProperty_boolean() {
        List properties = Arrays.asList(new String[] { "b" });

        BeanPropertyComparator bpc = new BeanPropertyComparator(properties);
        A lesser = new A("One", new Integer(0), Boolean.valueOf(false));
        B greater = new B("One", new Integer(0), Boolean.valueOf(true));

        int lessThan = bpc.compare(lesser, greater);
        assertTrue(lessThan < 0);

        int greaterThan = bpc.compare(greater, lesser);
        assertTrue(greaterThan > 0);

        int equal = bpc.compare(greater, greater);
        assertTrue(equal == 0);
    }

    @Test public void testCompare_oneLevel() {
        List propertiesSIB = Arrays.asList(new String[] { "s", "i", "b" });

        BeanPropertyComparator bpcSIB = new BeanPropertyComparator(propertiesSIB);
        A lesser = new A("One", new Integer(0), Boolean.valueOf(false));
        B greater = new B("Two", new Integer(0), Boolean.valueOf(false));

        int lessThan = bpcSIB.compare(lesser, greater);
        assertTrue(lessThan < 0);

        int greaterThan = bpcSIB.compare(greater, lesser);
        assertTrue(greaterThan > 0);

        int equal = bpcSIB.compare(greater, greater);
        assertTrue(equal == 0);
    }

    @Test public void testCompare_twoLevels() {
        List propertiesSIB = Arrays.asList(new String[] { "s", "i", "b" });

        BeanPropertyComparator bpc = new BeanPropertyComparator(propertiesSIB);
        A lesser = new A("Same", new Integer(-1), Boolean.valueOf(false));
        B greater = new B("Same", new Integer(1), Boolean.valueOf(false));

        int lessThan = bpc.compare(lesser, greater);
        assertTrue(lessThan < 0);

        int greaterThan = bpc.compare(greater, lesser);
        assertTrue(greaterThan > 0);

        int equal = bpc.compare(greater, greater);
        assertTrue(equal == 0);
    }

    @Test public void testCompare_threeLevels() {
        List propertiesSIB = Arrays.asList(new String[] { "s", "i", "b" });

        BeanPropertyComparator bpc = new BeanPropertyComparator(propertiesSIB);
        A lesser = new A("Same", new Integer(1), Boolean.valueOf(false));
        B greater = new B("Same", new Integer(1), Boolean.valueOf(true));

        int lessThan = bpc.compare(lesser, greater);
        assertTrue(lessThan < 0);

        int greaterThan = bpc.compare(greater, lesser);
        assertTrue(greaterThan > 0);

        int equal = bpc.compare(greater, greater);
        assertTrue(equal == 0);
    }

    @Test public void testCompare_differentCases() {
        List propertiesSIB = Arrays.asList(new String[] { "s", "i", "b" });

        BeanPropertyComparator sensitive = new BeanPropertyComparator(propertiesSIB, false);
        BeanPropertyComparator insensitive = new BeanPropertyComparator(propertiesSIB, true);

        A lesser = new A("SomeThing", new Integer(1), Boolean.valueOf(false));
        B greater = new B("something", new Integer(1), Boolean.valueOf(false));

        int equal = insensitive.compare(greater, lesser);
        assertTrue(equal == 0);

        int inequal = sensitive.compare(greater, lesser);
        assertTrue(inequal != 0);
    }

    public static class A {
        private String s;
        private Integer i;
        private Boolean b;

        public A(String s, Integer i, Boolean b) {
            this.s = s;
            this.i = i;
            this.b = b;
        }

        public String getS() {
            return s;
        }

        public Integer getI() {
            return i;
        }

        public Boolean getB() {
            return b;
        }
    }

    public static class B {
        private String s;
        private Integer i;
        private Boolean b;
        private Long l;

        public B(String s, Integer i, Boolean b) {
            this.s = s;
            this.i = i;
            this.b = b;
            this.l = new Long(23);
        }

        public String getS() {
            return s;
        }

        public Integer getI() {
            return i;
        }

        public Boolean getB() {
            return b;
        }

        public Long getL() {
            return l;
        }
    }

    public static class C {
        private boolean s;
        private String i;
        private float b;

        public C(String i, float b, boolean s) {
            this.s = s;
            this.i = i;
            this.b = b;
        }

        private boolean getS() {
            return s;
        }

        public String getI() {
            return i;
        }

        public float getB() {
            return b;
        }
    }
}
