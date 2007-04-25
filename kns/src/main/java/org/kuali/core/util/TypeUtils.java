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

package org.kuali.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This class provides utilities for checking the types of objects.
 * 
 * 
 */

public class TypeUtils {
    private static final Class[] BOOLEAN_CLASSES = { Boolean.class, Boolean.TYPE };
    private static final Class[] INTEGRAL_CLASSES = { Byte.class, Byte.TYPE, Short.class, Short.TYPE, Integer.class, Integer.TYPE, Long.class, Long.TYPE, BigInteger.class, KualiInteger.class };
    private static final Class[] DECIMAL_CLASSES = { Float.class, Float.TYPE, Double.class, Double.TYPE, BigDecimal.class, KualiDecimal.class, KualiPercent.class };
    private static final Class[] TEMPORAL_CLASSES = { java.util.Date.class, java.sql.Date.class, java.sql.Timestamp.class };
    private static final Class[] STRING_CLASSES = { String.class };

    /**
     * @param clazz
     * @return true if the given Class is an boolean type
     * @throws IllegalArgumentException if the given Class is null
     */
    public static boolean isBooleanClass(Class clazz) {
        return isa(BOOLEAN_CLASSES, clazz);
    }

    /**
     * @param clazz
     * @return true if the given Class is an integral type
     * @throws IllegalArgumentException if the given Class is null
     */
    public static boolean isIntegralClass(Class clazz) {
        return isa(INTEGRAL_CLASSES, clazz);
    }

    /**
     * @param clazz
     * @return true if the given Class is a decimal type
     * @throws IllegalArgumentException if the given Class is null
     */
    public static boolean isDecimalClass(Class clazz) {
        return isa(DECIMAL_CLASSES, clazz);
    }

    /**
     * @param clazz
     * @return true if the given Class is a temporal type
     * @throws IllegalArgumentException if the given Class is null
     */
    public static boolean isTemporalClass(Class clazz) {
        return isa(TEMPORAL_CLASSES, clazz);
    }

    /**
     * @param clazz
     * @return true if the given Class is a string type
     * @throws IllegalArgumentException if the given Class is null
     */
    public static boolean isStringClass(Class clazz) {
        return isa(STRING_CLASSES, clazz);
    }

    /**
     * @param clazz
     * @return true if the given Class is a "simple" - one of the primitive types, their wrappers, or a temporal type
     * @throws IllegalArgumentException if the given Class is null
     */
    public static boolean isSimpleType(Class clazz) {
        return isa(STRING_CLASSES, clazz) || isa(DECIMAL_CLASSES, clazz) || isa(INTEGRAL_CLASSES, clazz) || isa(BOOLEAN_CLASSES, clazz) || isa(TEMPORAL_CLASSES, clazz);
    }

    /**
     * @param types
     * @param type
     * @return true if the given Class is assignable from one of the classes in the given Class[]
     * @throws IllegalArgumentException if the given Class is null
     */
    private static boolean isa(Class[] types, Class type) {
        if (type == null) {
            throw new IllegalArgumentException("illegal (null) type class");
        }

        boolean isa = false;

        for (int i = 0; !isa && (i < types.length); ++i) {
            isa = types[i].isAssignableFrom(type);
        }

        return isa;
    }
}