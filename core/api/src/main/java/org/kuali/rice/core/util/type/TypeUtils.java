/*
 * Copyright 2005-2007 The Kuali Foundation
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

import com.google.common.collect.MapMaker;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentMap;

/**
 * Provides utilities for checking the types of objects.
 */
public class TypeUtils {
    private static final Collection<Class<?>> BOOLEAN_CLASSES;
    private static final Collection<Class<?>> INTEGRAL_CLASSES;
    private static final Collection<Class<?>> DECIMAL_CLASSES;
    private static final Collection<Class<?>> TEMPORAL_CLASSES;
    private static final Collection<Class<?>> STRING_CLASSES = Collections.<Class<?>>singleton(CharSequence.class);
    private static final Collection<Class<?>> CLASS_CLASSES = Collections.<Class<?>>singleton(Class.class);
    private static final Collection<Class<?>> SIMPLE_CLASSES;

    static {
        final Collection<Class<?>> temp = new ArrayList<Class<?>>();
        temp.add(Boolean.class);
        temp.add(Boolean.TYPE);
        BOOLEAN_CLASSES = Collections.unmodifiableCollection(temp);
    }

    static {
        final Collection<Class<?>> temp = new ArrayList<Class<?>>();
        temp.add(Byte.class);
        temp.add(Byte.TYPE);
        temp.add(Short.class);
        temp.add(Short.TYPE);
        temp.add(Integer.class);
        temp.add(Integer.TYPE);
        temp.add(Long.class);
        temp.add(Long.TYPE);
        temp.add(BigInteger.class);
        temp.add(KualiInteger.class);
        INTEGRAL_CLASSES = Collections.unmodifiableCollection(temp);
    }

    static {
        final Collection<Class<?>> temp = new ArrayList<Class<?>>();
        temp.add(Float.class);
        temp.add(Float.TYPE);
        temp.add(Double.class);
        temp.add(Double.TYPE);
        temp.add(BigDecimal.class);
        temp.add(AbstractKualiDecimal.class);
        DECIMAL_CLASSES = Collections.unmodifiableCollection(temp);
    }

    static {
        final Collection<Class<?>> temp = new ArrayList<Class<?>>();
        temp.add(java.util.Date.class);
        temp.add(java.sql.Date.class);
        temp.add(java.sql.Timestamp.class);
        TEMPORAL_CLASSES = Collections.unmodifiableCollection(temp);
    }

    static {
        final Collection<Class<?>> temp = new ArrayList<Class<?>>();
        temp.addAll(BOOLEAN_CLASSES);
        temp.addAll(INTEGRAL_CLASSES);
        temp.addAll(DECIMAL_CLASSES);
        temp.addAll(TEMPORAL_CLASSES);
        temp.addAll(STRING_CLASSES);
        SIMPLE_CLASSES = Collections.unmodifiableCollection(temp);
    }

    private static final ConcurrentMap<Class<?>, Boolean> IS_BOOLEAN_CACHE = new MapMaker().softKeys().makeMap();
    private static final ConcurrentMap<Class<?>, Boolean> IS_INTEGRAL_CACHE = new MapMaker().softKeys().makeMap();
    private static final ConcurrentMap<Class<?>, Boolean> IS_DECIMAL_CACHE = new MapMaker().softKeys().makeMap();
    private static final ConcurrentMap<Class<?>, Boolean> IS_TEMPORAL_CACHE = new MapMaker().softKeys().makeMap();
    private static final ConcurrentMap<Class<?>, Boolean> IS_STRING_CACHE = new MapMaker().softKeys().makeMap();
    private static final ConcurrentMap<Class<?>, Boolean> IS_SIMPLE_CACHE = new MapMaker().softKeys().makeMap();
    private static final ConcurrentMap<Class<?>, Boolean> IS_CACHE_CACHE = new MapMaker().softKeys().makeMap();

    private TypeUtils() {
        throw new UnsupportedOperationException("do not call.");
    }

    /**
     * @param clazz class token
     * @return true if the given Class is an boolean type
     * @throws IllegalArgumentException if the given Class is null
     */
    public static boolean isBooleanClass(Class<?> clazz) {
        return is(clazz, BOOLEAN_CLASSES, IS_BOOLEAN_CACHE);
    }

    /**
     * @param clazz class token
     * @return true if the given Class is an integral type
     * @throws IllegalArgumentException if the given Class is null
     */
    public static boolean isIntegralClass(Class<?> clazz) {
        return is(clazz, INTEGRAL_CLASSES, IS_INTEGRAL_CACHE);
    }

    /**
     * @param clazz class token
     * @return true if the given Class is a decimal type
     * @throws IllegalArgumentException if the given Class is null
     */
    public static boolean isDecimalClass(Class<?> clazz) {
        return is(clazz, DECIMAL_CLASSES, IS_DECIMAL_CACHE);
    }

    /**
     * @param clazz class token
     * @return true if the given Class is a temporal type
     * @throws IllegalArgumentException if the given Class is null
     */
    public static boolean isTemporalClass(Class<?> clazz) {
        return is(clazz, TEMPORAL_CLASSES, IS_TEMPORAL_CACHE);
    }

    /**
     * @param clazz class token
     * @return true if the given Class is a string type
     * @throws IllegalArgumentException if the given Class is null
     */
    public static boolean isStringClass(Class<?> clazz) {
        return is(clazz, STRING_CLASSES, IS_STRING_CACHE);
    }

    /**
     * @param clazz class token
     * @return true if the given Class is a Class type
     * @throws IllegalArgumentException if the given Class is null
     */
    public static boolean isClassClass(Class<?> clazz) {
        return is(clazz, CLASS_CLASSES, IS_CACHE_CACHE);
    }

    /**
     * @param clazz class token
     * @return true if the given Class is a "simple" - one of the primitive
     *         types, their wrappers, or a temporal type
     * @throws IllegalArgumentException if the given Class is null
     */
    public static boolean isSimpleType(Class<?> clazz) {
        return is(clazz, SIMPLE_CLASSES, IS_SIMPLE_CACHE);
    }

    private static boolean is(Class<?> clazz, Collection<Class<?>> clazzes, ConcurrentMap<Class<?>, Boolean> cache) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }

        if (clazzes == null) {
            throw new IllegalArgumentException("clazzes is null");
        }

        if (cache == null) {
            throw new IllegalArgumentException("cache is null");
        }

        Boolean result = cache.get(clazz);
        if (result == null) {
            result = isa(clazzes, clazz);
            cache.putIfAbsent(clazz, result);
        }
        return result;
    }

    /**
     * @param types class tokens to check against
     * @param type class token
     * @return true if the given Class is assignable from one of the classes in
     *         the given Class[]
     * @throws IllegalArgumentException if the given Class is null
     */
    private static boolean isa(Collection<Class<?>> types, Class<?> type) {
        for (Class<?> cur : types) {
            if (cur.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }
}
