/*
 * Copyright 2004 Jonathan M. Lehr
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 * 
 */

// begin Kuali Foundation modification
package org.kuali.core.web.struts.pojo;
// end Kuali Foundation modification

import java.lang.reflect.Array;

/**
 * begin Kuali Foundation modification
 * This class sets the value of an element of an array of primitives at the supplied index.
 * end Kuali Foundation modification
 * @author <a href="mailto:jonathan@sourcebeat.com">Jonathan Lehr</a>
 */
public class ArrayUtils {

    /**
     * Sets the value of an element of an array of primitives at the supplied index.
     * 
     * @param array An array.
     * @param type The component type of the array.
     * @param index An array index.
     */
    public static void setArrayValue(Object array, Class type, Object value, int index) {
        if (!type.isPrimitive())
            Array.set(array, index, value);
        else if (type.isAssignableFrom(Boolean.TYPE))
            Array.setBoolean(array, index, ((Boolean) value).booleanValue());
        else if (type.isAssignableFrom(Character.TYPE))
            Array.setChar(array, index, ((Character) value).charValue());
        else if (type.isAssignableFrom(Character.TYPE))
            Array.setByte(array, index, ((Byte) value).byteValue());
        else if (type.isAssignableFrom(Integer.TYPE))
            Array.setInt(array, index, ((Integer) value).intValue());
        else if (type.isAssignableFrom(Character.TYPE))
            Array.setShort(array, index, ((Short) value).shortValue());
        else if (type.isAssignableFrom(Character.TYPE))
            Array.setLong(array, index, ((Long) value).longValue());
        else if (type.isAssignableFrom(Float.TYPE))
            Array.setFloat(array, index, ((Float) value).floatValue());
        else if (type.isAssignableFrom(Character.TYPE))
            Array.setDouble(array, index, ((Double) value).doubleValue());
    }

    public static Object toObject(Object value) {
        if (!value.getClass().isArray())
            return value;

        Class type = value.getClass().getComponentType();
        if (Array.getLength(value) == 0)
            return null;
        if (!type.isPrimitive())
            return Array.get(value, 0);
        if (char.class.isAssignableFrom(type))
            return new Character(Array.getChar(value, 0));
        if (byte.class.isAssignableFrom(type))
            return new Byte(Array.getByte(value, 0));
        if (int.class.isAssignableFrom(type))
            return new Integer(Array.getInt(value, 0));
        if (long.class.isAssignableFrom(type))
            return new Long(Array.getLong(value, 0));
        if (short.class.isAssignableFrom(type))
            return new Short(Array.getShort(value, 0));
        if (double.class.isAssignableFrom(type))
            return new Double(Array.getDouble(value, 0));
        if (float.class.isAssignableFrom(type))
            return new Float(Array.getFloat(value, 0));

        return null;
    }

    public static Object toString(Object array, Class type) {
        if (boolean.class.isAssignableFrom(type))
            return Boolean.toString(((boolean[]) array)[0]);
        if (char.class.isAssignableFrom(type))
            return Character.toString(((char[]) array)[0]);
        if (byte.class.isAssignableFrom(type))
            return Byte.toString(((byte[]) array)[0]);
        if (int.class.isAssignableFrom(type))
            return Integer.toString(((int[]) array)[0]);
        if (long.class.isAssignableFrom(type))
            return Long.toString(((long[]) array)[0]);
        if (short.class.isAssignableFrom(type))
            return Short.toString(((short[]) array)[0]);
        if (double.class.isAssignableFrom(type))
            return Double.toString(((double[]) array)[0]);
        if (float.class.isAssignableFrom(type))
            return Float.toString(((float[]) array)[0]);

        return ((String[]) array)[0];
    }
}
