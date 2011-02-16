/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary.validation.processor;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.validation.SingleAttributeValueReader;
import org.kuali.rice.kns.datadictionary.validation.capability.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validation.capability.DataType;
import org.kuali.rice.kns.datadictionary.validation.capability.ErrorLevel;
import org.kuali.rice.kns.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validation.result.DictionaryValidationResult;


/**
 * Things this test should check:
 * 
 * 1. Boolean as boolean true (success) {@link DataTypeConstraintProcessorTest#testBooleanAsBooleanTrue()}
 * 2. Boolean as boolean false (success) {@link DataTypeConstraintProcessorTest#testBooleanAsBooleanFalse()}
 * 3. Boolean as null (no constraint) {@link DataTypeConstraintProcessorTest#testBooleanAsNull()}
 * 4. Boolean as string "true" (success) {@link DataTypeConstraintProcessorTest#testBooleanAsStringTrue()}
 * 5. Boolean as string "false" (success) {@link DataTypeConstraintProcessorTest#testBooleanAsStringFalse()}
 * 6. Boolean as string "potato" (failure) {@link DataTypeConstraintProcessorTest#testBooleanAsStringPotato()}
 * 7. Integer as string "12" (success) {@link DataTypeConstraintProcessorTest#testIntegerAsString12()}
 * 8. Integer as out of integer range string (failure) {@link DataTypeConstraintProcessorTest#testIntegerAsOutOfRangeIntegerString()}
 * 9. Integer as negative integer (success) {@link DataTypeConstraintProcessorTest#testIntegerAsNegativeIntegerString()}
 * 10. Long as long (success) {@link DataTypeConstraintProcessorTest#testLongAsOutOfIntegerRangeLong()}
 * 11. Long as negative long (success) {@link DataTypeConstraintProcessorTest#testLongAsNegativeLong()}
 * 12. Long as out of integer range string (success) {@link DataTypeConstraintProcessorTest#testLongAsOutOfIntegerRangeString()}
 * 13. Double as string "234897.2323" (success) {@link DataTypeConstraintProcessorTest#testDoubleAsPositiveDoubleString()}
 * 14. Double as string "-234897.2323" (success) {@link DataTypeConstraintProcessorTest#testDoubleAsNegativeDouble()}
 * 15. Double as null (no constraint) {@link DataTypeConstraintProcessorTest#testDoubleAsNull()}
 * 16. Double as out of double range string (failure) {@link DataTypeConstraintProcessorTest#testDoubleAsOutOfDoubleRangeString()}
 * 17. Double as out of negative double range string (failure) {@link DataTypeConstraintProcessorTest#testDoubleAsNegativeOutOfDoubleRangeString()}
 * 18. Float as string "123.2" (success) {@link DataTypeConstraintProcessorTest#testFloatAsPositiveFloatString()}
 * 19. Float as string "-12312.42" (success) {@link DataTypeConstraintProcessorTest#testFloatAsNegativeFloatString()}
 * 20. Float as out of float range string (failure) {@link DataTypeConstraintProcessorTest#testFloatAsOutOfFloatRangeString()}
 * 21. Float as out of negative float range string (failure) {@link DataTypeConstraintProcessorTest#testFloatAsNegativeOutOfFloatRangeString()}
 * 22. Date as string in format yyyy-MM-dd'T'HH:mm:ss.SSSZ (success) {@link DataTypeConstraintProcessorTest#testDateAsStringInFormat1Success()}
 * 23. Date as string in format yyyy-MM-dd (success) {@link DataTypeConstraintProcessorTest#testDateAsStringInFormat2Success()}
 * 24. Date as string in format yyyy-MMM-dd (success) {@link DataTypeConstraintProcessorTest#testDateAsStringInFormat3Success()}
 * 25. Date as string in format dd-MM-yyyy (success) {@link DataTypeConstraintProcessorTest#testDateAsStringInFormat4Success()}
 * 26. Date as string in format dd-MMM-yyyy (success) {@link DataTypeConstraintProcessorTest#testDateAsStringInFormat5Success()}
 * 27. Date as string in format 'yyyymmdd' (failure) {@link DataTypeConstraintProcessorTest#testDateAsStringInFormat6Failure()}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataTypeConstraintProcessorTest {

	private DataTypeConstraintProcessor processor;
	private AttributeValueReader attributeValueReader;
	
	@Before
	public void setUp() throws Exception {
		processor = new DataTypeConstraintProcessor();
		
	}
	
	@Test
	public void testBooleanAsBooleanTrue() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.BOOLEAN);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, Boolean.TRUE, definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testBooleanAsBooleanFalse() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.BOOLEAN);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, Boolean.FALSE, definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testBooleanAsNull() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.BOOLEAN);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, null, definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.INAPPLICABLE, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testBooleanAsStringTrue() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.BOOLEAN);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "true", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testBooleanAsStringFalse() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.BOOLEAN);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "false", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testBooleanAsStringPotato() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.BOOLEAN);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "potato", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testIntegerAsString12() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.INTEGER);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "12", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testIntegerAsOutOfRangeIntegerString() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.INTEGER);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "923423423423423412", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testIntegerAsNegativeIntegerString() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.INTEGER);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "-3412", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testIntegerAsNull() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.INTEGER);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, null, definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.INAPPLICABLE, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testIntegerAsStringPotato() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.INTEGER);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "potato", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testIntegerAsInteger12() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.INTEGER);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, Integer.valueOf(12), definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testIntegerAsBigDecimal12() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.INTEGER);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, BigDecimal.valueOf(12), definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testIntegerAsBigDecimal12point32() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.INTEGER);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, BigDecimal.valueOf(12.32), definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testLongAsOutOfIntegerRangeString() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.LONG);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "923423423423423412", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testLongAsOutOfIntegerRangeLong() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.LONG);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, Long.valueOf(923423423423423412l), definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testLongAsNegativeLong() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.LONG);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, Long.valueOf(-923423423423423412l), definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testDoubleAsPositiveDoubleString() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.DOUBLE);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "234897.2323", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testDoubleAsNegativeDoubleString() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.DOUBLE);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "-234897.2323", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testDoubleAsNull() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.DOUBLE);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, null, definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.INAPPLICABLE, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testDoubleAsNegativeDouble() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.DOUBLE);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, Double.valueOf(-234897.2323d), definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testDoubleAsOutOfDoubleRangeString() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.DOUBLE);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "923423234234423423423412999999999999999999999999e12312321", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testDoubleAsNegativeOutOfDoubleRangeString() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.DOUBLE);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "-923423234234423423423412999999999999999999999999e99234234", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testFloatAsPositiveFloatString() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.FLOAT);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "234897.2323", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testFloatAsNegativeFloatString() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.FLOAT);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "-234897.2323", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testFloatAsOutOfFloatRangeString() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.FLOAT);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "923423234234423423423412999999999999999999999999", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testFloatAsNegativeOutOfFloatRangeString() {
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.FLOAT);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "-923423234234423423423412999999999999999999999999", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	/*
	  	* 22. Date as string in format yyyy-MM-dd'T'HH:mm:ss.SSSZ (success)
		* 23. Date as string in format yyyy-MM-dd (success)
		* 24. Date as string in format yyyy-MMM-dd (success)
		* 25. Date as string in format dd-MM-yyyy (success)
		* 26. Date as string in format dd-MMM-yyyy (success)
		* 27. Date as string in format 'yyyymmdd' (failure)
	 */
	
	
	@Test
	public void testDateAsStringInFormat1Success() {
		// Format yyyy-MM-dd'T'HH:mm:ss.SSSZ
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.DATE);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "2001-07-04T12:08:56.235-0700", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testDateAsStringInFormat2Success() {
		// Format yyyy-MM-dd
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.DATE);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "2001-03-04", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testDateAsStringInFormat3Success() {
		// Format yyyy-MMM-dd
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.DATE);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "2001-JUL-12", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testDateAsStringInFormat3Failure() {
		// Format yyyy-MMM-dd
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.DATE);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "2001-KUA-12", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testDateAsStringInFormat4Success() {
		// Format dd-MM-yyyy
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.DATE);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "22-12-2001", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testDateAsStringInFormat5Success() {
		// Format dd-MMM-yyyy
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.DATE);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "12-AUG-2001", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testDateAsStringInFormat5Failure() {
		// Format dd-MMM-yyyy
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.DATE);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "12-KUA-2001", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	@Test
	public void testDateAsStringInFormat6Failure() {
		// Format yyyymmdd
		DictionaryValidationResult dictionaryValidationResult = new DictionaryValidationResult();
		AttributeDefinition definition = new AttributeDefinition();
		definition.setDataType(DataType.DATE);
		ConstraintValidationResult validationResult = process(dictionaryValidationResult, "20010704", definition);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, validationResult.getStatus());
		Assert.assertEquals(new DataTypeConstraintProcessor().getName(), validationResult.getConstraintName());
	}
	
	protected ConstraintValidationResult process(DictionaryValidationResult result, Object value, AttributeDefinition definition) {
		attributeValueReader = new SingleAttributeValueReader(value, "testEntry", "testAttribute", definition);
		return processor.process(result, value, definition, attributeValueReader).getFirstConstraintValidationResult();
	}

	
}
