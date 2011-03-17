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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

/**
 * Provides utilities for checking the types of objects.
 */
@SuppressWarnings("all")
public class TypeUtils {
	private static final Class[] BOOLEAN_CLASSES = { Boolean.class, Boolean.TYPE };
	private static final Class[] INTEGRAL_CLASSES = { Byte.class, Byte.TYPE, Short.class, Short.TYPE, Integer.class,
			Integer.TYPE, Long.class, Long.TYPE, BigInteger.class, KualiInteger.class };
	// KRACOEUS-1493 : add AbstractKualiDecimal for budgetdecimal
	private static final Class[] DECIMAL_CLASSES = { Float.class, Float.TYPE, Double.class, Double.TYPE,
			BigDecimal.class, KualiDecimal.class, KualiPercent.class, AbstractKualiDecimal.class };
	private static final Class[] TEMPORAL_CLASSES = { java.util.Date.class, java.sql.Date.class,
			java.sql.Timestamp.class };
	private static final Class[] STRING_CLASSES = { String.class };
	private static final Class[] CLASS_CLASSES = { Class.class };

	private static final HashMap<Class, Boolean> isBooleanCache = new HashMap<Class, Boolean>();
	private static final HashMap<Class, Boolean> isIntegralCache = new HashMap<Class, Boolean>();
	private static final HashMap<Class, Boolean> isDecimalCache = new HashMap<Class, Boolean>();
	private static final HashMap<Class, Boolean> isTemporalCache = new HashMap<Class, Boolean>();
	private static final HashMap<Class, Boolean> isStringCache = new HashMap<Class, Boolean>();
	private static final HashMap<Class, Boolean> isSimpleCache = new HashMap<Class, Boolean>();
	private static final HashMap<Class, Boolean> isClassCache = new HashMap<Class, Boolean>();

	public static class JoinType {

	}

	/**
	 * @param clazz
	 * @return true if the given Class is an join type
	 * @throws IllegalArgumentException
	 *             if the given Class is null
	 */
	public static boolean isJoinClass(Class clazz) {
		return clazz.isAssignableFrom(JoinType.class);
	}

	/**
	 * @param clazz
	 * @return true if the given Class is an boolean type
	 * @throws IllegalArgumentException
	 *             if the given Class is null
	 */
	public static boolean isBooleanClass(Class clazz) {
		Boolean result = isBooleanCache.get(clazz);
		if (result == null) {
			result = isa(BOOLEAN_CLASSES, clazz);
			synchronized (isBooleanCache) {
				isBooleanCache.put(clazz, result);
			}
		}

		return result;
	}

	/**
	 * @param clazz
	 * @return true if the given Class is an integral type
	 * @throws IllegalArgumentException
	 *             if the given Class is null
	 */
	public static boolean isIntegralClass(Class clazz) {
		Boolean result = isIntegralCache.get(clazz);
		if (result == null) {
			result = isa(INTEGRAL_CLASSES, clazz);
			synchronized (isIntegralCache) {
				isIntegralCache.put(clazz, result);
			}
		}
		return result;
	}

	/**
	 * @param clazz
	 * @return true if the given Class is a decimal type
	 * @throws IllegalArgumentException
	 *             if the given Class is null
	 */
	public static boolean isDecimalClass(Class clazz) {
		Boolean result = isDecimalCache.get(clazz);
		if (result == null) {
			result = isa(DECIMAL_CLASSES, clazz);
			synchronized (isDecimalCache) {
				isDecimalCache.put(clazz, result);
			}
		}
		return result;
	}

	/**
	 * @param clazz
	 * @return true if the given Class is a temporal type
	 * @throws IllegalArgumentException
	 *             if the given Class is null
	 */
	public static boolean isTemporalClass(Class clazz) {
		Boolean result = isTemporalCache.get(clazz);
		if (result == null) {
			result = isa(TEMPORAL_CLASSES, clazz);
			synchronized (isTemporalCache) {
				isTemporalCache.put(clazz, result);
			}
		}
		return result;
	}

	/**
	 * @param clazz
	 * @return true if the given Class is a string type
	 * @throws IllegalArgumentException
	 *             if the given Class is null
	 */
	public static boolean isStringClass(Class clazz) {
		Boolean result = isStringCache.get(clazz);
		if (result == null) {
			result = isa(STRING_CLASSES, clazz);
			synchronized (isStringCache) {
				isStringCache.put(clazz, result);
			}
		}
		return result;
	}
	
	/**
	 * @param clazz
	 * @return true if the given Class is a Class type
	 * @throws IllegalArgumentException
	 *             if the given Class is null
	 */
	public static boolean isClassClass(Class clazz) {
		Boolean result = isClassCache.get(clazz);
		if (result == null) {
			result = isa(CLASS_CLASSES, clazz);
			synchronized (isClassCache) {
				isClassCache.put(clazz, result);
			}
		}
		return result;
	}

	/**
	 * @param clazz
	 * @return true if the given Class is a "simple" - one of the primitive
	 *         types, their wrappers, or a temporal type
	 * @throws IllegalArgumentException
	 *             if the given Class is null
	 */
	public static boolean isSimpleType(Class clazz) {
		Boolean result = isSimpleCache.get(clazz);
		if (result == null) {
			result = isa(STRING_CLASSES, clazz) || isa(DECIMAL_CLASSES, clazz) || isa(INTEGRAL_CLASSES, clazz)
					|| isa(BOOLEAN_CLASSES, clazz) || isa(TEMPORAL_CLASSES, clazz);
			synchronized (isSimpleCache) {
				isSimpleCache.put(clazz, result);
			}
		}
		return result;
	}

	/**
	 * @param types
	 * @param type
	 * @return true if the given Class is assignable from one of the classes in
	 *         the given Class[]
	 * @throws IllegalArgumentException
	 *             if the given Class is null
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
