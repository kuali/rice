/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.web;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;

/**
 * Utility class for registering the various context sensitive converters.
 *
 * @see ContextSensitiveConverter
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ConverterUtils {

	public static synchronized void registerContextSensitiveConverters() {
    	registerBigDecimalConverter();
    	registerBigIntegerConverter();
    	registerBooleanConverter();
    	registerByteConverter();
    	registerCharacterConverter();
    	registerDoubleConverter();
    	registerFloatConverter();
    	registerIntegerConverter();
    	registerLongConverter();
    	registerShortConverter();
    }

	public static void registerBigDecimalConverter() {
		Converter currentConverter = ConvertUtils.lookup(BigDecimal.class);
		ConvertUtils.register(new ContextSensitiveConverter(currentConverter, new BigDecimalConverter(null)), BigDecimal.class);
	}

	public static void registerBigIntegerConverter() {
		Converter currentConverter = ConvertUtils.lookup(BigInteger.class);
		ConvertUtils.register(new ContextSensitiveConverter(currentConverter, new BigIntegerConverter(null)), BigInteger.class);
	}

	public static void registerBooleanConverter() {
		Converter currentConverter = ConvertUtils.lookup(Boolean.class);
		ConvertUtils.register(new ContextSensitiveConverter(currentConverter, new BooleanConverter(null)), Boolean.class);
	}

	public static void registerByteConverter() {
		Converter currentConverter = ConvertUtils.lookup(Byte.class);
		ConvertUtils.register(new ContextSensitiveConverter(currentConverter, new ByteConverter(null)), Byte.class);
	}

	public static void registerCharacterConverter() {
		Converter currentConverter = ConvertUtils.lookup(Character.class);
		ConvertUtils.register(new ContextSensitiveConverter(currentConverter, new CharacterConverter(null)), Character.class);
	}

	public static void registerDoubleConverter() {
		Converter currentConverter = ConvertUtils.lookup(Double.class);
		ConvertUtils.register(new ContextSensitiveConverter(currentConverter, new DoubleConverter(null)), Double.class);
	}

	public static void registerFloatConverter() {
		Converter currentConverter = ConvertUtils.lookup(Float.class);
		ConvertUtils.register(new ContextSensitiveConverter(currentConverter, new FloatConverter(null)), Float.class);
	}

	public static void registerIntegerConverter() {
		Converter currentConverter = ConvertUtils.lookup(Integer.class);
		ConvertUtils.register(new ContextSensitiveConverter(currentConverter, new IntegerConverter(null)), Integer.class);
	}

	public static void registerLongConverter() {
		Converter currentConverter = ConvertUtils.lookup(Long.class);
		ConvertUtils.register(new ContextSensitiveConverter(currentConverter, new LongConverter(null)), Long.class);
	}

	public static void registerShortConverter() {
		Converter currentConverter = ConvertUtils.lookup(Short.class);
		ConvertUtils.register(new ContextSensitiveConverter(currentConverter, new ShortConverter(null)), Short.class);
	}

}
