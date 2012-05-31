/*
 * Copyright 2007-2011 The Kuali Foundation
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
package org.kuali.rice.kns.web.pojo;


import org.junit.Test;
import org.kuali.rice.kns.web.struts.pojo.ArrayUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static org.junit.Assert.*;

/**
 * @author: Kuali Rice Team
 * @email: rice.collab@kuali.org
 * @version: $
 */

/* Unit Test for org.kuali.rice.kns.web.struts.pojo.ArrayUtils
* */
public class ArrayUtilsTest {

    @Test
    public void shouldSetArrayValue() throws ClassNotFoundException {

        byte[] barray1 = new byte[]{(byte)2, (byte)5, (byte)4, (byte)6};
        org.kuali.rice.kns.web.struts.pojo.ArrayUtils.setArrayValue(barray1, Byte.TYPE, (byte)0, 0);
        assertEquals(barray1.length, 4);
        assertArrayEquals(new byte[]{0, 5, 4, 6}, barray1);

        short[] sarray1 = new short[]{2, 5, 4, 6};
        org.kuali.rice.kns.web.struts.pojo.ArrayUtils.setArrayValue(sarray1, Short.TYPE, (short)10, 1);
        assertEquals(sarray1.length, 4);
        assertArrayEquals(new short[]{2, 10, 4, 6}, sarray1);

        int[] iarray1 = new int[]{2, 5, 4, 6};
        org.kuali.rice.kns.web.struts.pojo.ArrayUtils.setArrayValue(iarray1, iarray1.getClass(), 10, 1);
        assertEquals(iarray1.length, 4);
        assertArrayEquals(new int[]{2, 10, 4, 6}, iarray1);

        long[] larray1 = new long[]{2, 5, 4, 6};
        org.kuali.rice.kns.web.struts.pojo.ArrayUtils.setArrayValue(larray1, larray1.getClass(), (long)0, 0);
        assertEquals(larray1.length, 4);
        assertArrayEquals(new long[]{0, 5, 4, 6}, larray1);

        Character[] Carray1 = new Character[]{'a', 'b', 'c', 'd'};
        org.kuali.rice.kns.web.struts.pojo.ArrayUtils.setArrayValue(Carray1, Carray1.getClass(), 'e', 2);
        assertEquals(Carray1.length, 4);
        assertArrayEquals(new Character[]{'a', 'b', 'e', 'd'}, Carray1);

    }

    @Test
    public void shouldConvertToString()
    {
        try {
            ArrayUtils.toString(null, null);
            fail("My method didn't throw when I expected it to");
        } catch (NullPointerException expectedException){}
        assertEquals(null, ArrayUtils.toString(new String[] {null}, String.class));
        assertEquals(null, ArrayUtils.toString(new String[2], String.class));
        assertEquals("", ArrayUtils.toString(new String[]{""}, String.class));
        assertEquals("red", ArrayUtils.toString(new String[]{"red", "blue"}, String[].class));
        assertEquals("true", ArrayUtils.toString(new boolean[]{true}, boolean.class));
        assertEquals("a", ArrayUtils.toString( new char[]{'a', 'b', 'c'}, char.class));
        assertEquals("0", ArrayUtils.toString(new byte[]{0}, byte.class));
        assertEquals("123456", ArrayUtils.toString( new int[]{123456}, int.class));
        assertEquals("123456", ArrayUtils.toString(new long[]{123456}, long.class));
        assertEquals("123", ArrayUtils.toString(new short[]{(short) 123}, short.class));
        assertEquals("123456.54321", ArrayUtils.toString(new double[]{123456.54321}, double.class));
        assertEquals("123456.0", ArrayUtils.toString(new float[]{123456}, float.class));
    }

    @Test
    public void shouldConvertToObject() {
        final boolean[] b = null;
        try {
            org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(b);
            fail("My toObject_Boolean didn't throw when I expected it to");
        } catch (NullPointerException expectedException) {}
        assertNotSame(org.apache.commons.lang.ArrayUtils.toObject(new boolean[0]), org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new boolean[0]));
        assertEquals(null, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new boolean[0]));
        assertEquals(Boolean.TRUE, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new boolean[]{true}));
        assertSame(Boolean.TRUE, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new boolean[]{true, false, true}));

        final char[] c = null;
        try {
            org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(c);
            fail("My toOject_char method didn't throw when I expected it to");
        } catch (NullPointerException expectedException) {}
        assertNotSame(org.apache.commons.lang.ArrayUtils.toObject(new char[0]), org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new char[0]));
        assertEquals(null, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new char[0]));
        assertEquals('0', org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject('0'));
        assertSame('a', org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new char[]{'a', 'b', 'c'}));

        final byte[] d = null;
        try {
            org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(d);
            fail("My toOject_byte method didn't throw when I expected it to");
        } catch (NullPointerException expectedException) {}
        assertNotSame(org.apache.commons.lang.ArrayUtils.toObject(new byte[0]), org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new byte[0]));
        assertEquals(null, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new byte[0]));
        assertEquals((byte) 9999999, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject((byte) 9999999));
        assertSame((byte) 9999999, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new byte[]{(byte) 9999999, (byte) 9999990, (byte) 9999998}));

        final short[] e = null;
        try {
            org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(e);
            fail("My toOject_short method didn't throw when I expected it to");
        } catch (NullPointerException expectedException) {}
        assertNotSame(org.apache.commons.lang.ArrayUtils.toObject(new short[0]), org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new short[0]));
        assertEquals(null, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new short[0]));
        assertEquals((short) 123, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject((short) 123));
        assertSame((short) 123, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new short[]{(short) 123, (short) 122, (short) 121}));

        final int[] f = null;
        try {
            org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(f);
            fail("My toOject_int method didn't throw when I expected it to");
        } catch (NullPointerException expectedException) {}
        assertNotSame(org.apache.commons.lang.ArrayUtils.toObject(new int[0]), org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new int[0]));
        assertEquals(null, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new int[0]));
        assertSame(123, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new int[]{123, 122, 121}));

        final long[] g = null;
        try {
            org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(g);
            fail("My toOject_long method didn't throw when I expected it to");
        } catch (NullPointerException expectedException) {}
        assertNotSame(org.apache.commons.lang.ArrayUtils.toObject(new long[0]), org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new long[0]));
        assertEquals(null, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new long[0]));
        assertEquals(123, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(123));
        assertSame((long) 123, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new long[]{123, 122, 121}));

        final float[] h = null;
        try {
            org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(h);
            fail("My toOject_float method didn't throw when I expected it to");
        } catch (NullPointerException expectedException) {}
        assertNotSame(org.apache.commons.lang.ArrayUtils.toObject(new float[0]), org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new float[0]));
        assertEquals(null, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new float[0]));
        float temp = Float.parseFloat("123");
        assertEquals(temp, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new float[]{temp, (float) 122, (float) 121}));
        
        final double[] i = null;
        try {
            org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(i);
            fail("My toOject_double method didn't throw when I expected it to");
        } catch (NullPointerException expectedException) {}
        assertNotSame(org.apache.commons.lang.ArrayUtils.toObject(new double[0]), org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new double[0]));
        assertEquals(null, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new double[0]));
        assertEquals(123.0, org.kuali.rice.kns.web.struts.pojo.ArrayUtils.toObject(new double[]{123, 122, 121}));


    }
}
