/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.core.api.criteria;


import org.junit.Test
import org.kuali.rice.core.test.JAXBAssert
import static org.junit.Assert.*

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class InPredicateTest {

	private static final String STRING_XML =
        """<in propertyPath="stringValues.path" xmlns="http://rice.kuali.org/core/v2_0">
            <stringValue>abcdefg</stringValue>
            <stringValue>gfedcabc</stringValue>
            <stringValue>should have failed by now!</stringValue>
          </in>""";
	private static final String DECIMAL_XML =
        """<in propertyPath="decimalValues.path" xmlns="http://rice.kuali.org/core/v2_0">
            <decimalValue>1.0</decimalValue>
            <decimalValue>1.1</decimalValue>
            <decimalValue>2.5</decimalValue>
          </in>""";
    private static final String KUALI_DECIMAL_XML =
            """<in propertyPath="kualiDecimalValues.path" xmlns="http://rice.kuali.org/core/v2_0">
            <kualiDecimalValue>2.50</kualiDecimalValue>
            <kualiDecimalValue>2.60</kualiDecimalValue>
            <kualiDecimalValue>2.70</kualiDecimalValue>
          </in>""";

    private static final String KUALI_PERCENT_XML =
            """<in propertyPath="kualiPercentValues.path" xmlns="http://rice.kuali.org/core/v2_0">
            <kualiPercentValue>25.00</kualiPercentValue>
            <kualiPercentValue>26.00</kualiPercentValue>
            <kualiPercentValue>27.00</kualiPercentValue>
          </in>""";

    private static final String INTEGER_XML =
        """<in propertyPath="integerValues.path" xmlns="http://rice.kuali.org/core/v2_0">
            <integerValue>1</integerValue>
            <integerValue>2</integerValue>
            <integerValue>3</integerValue>
            <integerValue>10</integerValue>
            <integerValue>4</integerValue>
          </in>""";
	private static final String DATE_TIME_XML =
        """<in propertyPath="dateTimeValues.path" xmlns="http://rice.kuali.org/core/v2_0">
            <dateTimeValue>2011-01-15T05:30:15.500Z</dateTimeValue>
           </in>""";
	
	/**
	 * Test method for {@link InPredicate#InPredicate(java.lang.String, java.util.Set)}.
	 */
	@Test
	public void testInExpression() {
		
		// test failure case, null propertyPath, but a valid list
		try {
			Set<CriteriaStringValue> set = new HashSet<CriteriaStringValue>();
			CriteriaStringValue value = new CriteriaStringValue("value1");
            set.add(value);
			new InPredicate(null, set);
			fail("IllegalArgumentException should have been thrown.");
		} catch (IllegalArgumentException e) {
			// expected exception
		}
		
		// test a null list
		try {
			new InPredicate("property.path", null);
			fail("IllegalArgumentException should have been thrown.");
		} catch (IllegalArgumentException e) {
			// expected exception
		}
		
		// test an empty list
		try {
			new InPredicate("property.path", new HashSet<CriteriaValue<?>>());
			fail("IllegalArgumentException should have been thrown.");
		} catch (IllegalArgumentException e) {
			// expected exception
		}
		
		// test a list with different CriteriaValue types in it
		try {
			Set<CriteriaValue<?>> valueList = new HashSet<CriteriaValue<?>>();
			valueList.add(new CriteriaStringValue("abcdefg"));
			valueList.add(new CriteriaStringValue("gfedcabc"));
			valueList.add(new CriteriaIntegerValue(100));
			valueList.add(new CriteriaStringValue("should have failed by now!"));
			new InPredicate("property.path", valueList);
			fail("IllegalArgumentException should have been thrown.");
		} catch (IllegalArgumentException e) {
			// expected exception
		}
		
		// now create a valid InExpression
		InPredicate expression = createWithStringCriteria();
		assertNotNull(expression);
		expression = createWithDecimalCriteria();
		assertNotNull(expression);
		expression = createWithIntegerCriteria();
		assertNotNull(expression);
		expression = createWithDateTimeCriteria();
		assertNotNull(expression);
		
	}

	/**
	 * Test method for {@link InPredicate#getPropertyPath()}.
	 */
	@Test
	public void testGetPropertyPath() {
		InPredicate expression = createWithStringCriteria();
		assertEquals("stringValues.path", expression.getPropertyPath());
		expression = createWithDecimalCriteria();
		assertEquals("decimalValues.path", expression.getPropertyPath());
        expression = createWithKualiDecimalCriteria();
        assertEquals("kualiDecimalValues.path", expression.getPropertyPath());
        expression = createWithKualiPercentCriteria();
        assertEquals("kualiPercentValues.path", expression.getPropertyPath());
		expression = createWithIntegerCriteria();
		assertEquals("integerValues.path", expression.getPropertyPath());
		expression = createWithDateTimeCriteria();
		assertEquals("dateTimeValues.path", expression.getPropertyPath());
	}

	/**
	 * Test method for {@link InPredicate#getValues()}.
	 */
	@Test
	public void testGetValues() {
		InPredicate expression = createWithStringCriteria();
		assertEquals(3, expression.getValues().size());
		for (CriteriaValue<?> value : expression.getValues()) {
			assertTrue("Expression should be CriteriaStringValue", value instanceof CriteriaStringValue);
		}
		
		expression = createWithDecimalCriteria();
		assertEquals(3, expression.getValues().size());
		for (CriteriaValue<?> value : expression.getValues()) {
			assertTrue("Expression should be CriteriaDecimalValue", value instanceof CriteriaDecimalValue);
		}

        expression = createWithKualiDecimalCriteria();
        assertEquals(3, expression.getValues().size());
        for (CriteriaValue<?> value : expression.getValues()) {
            assertTrue("Expression should be CriteriaKualiDecimalValue", value instanceof CriteriaKualiDecimalValue);
        }

        expression = createWithKualiPercentCriteria();
        assertEquals(3, expression.getValues().size());
        for (CriteriaValue<?> value : expression.getValues()) {
            assertTrue("Expression should be CriteriaKualiPercentValue", value instanceof CriteriaKualiPercentValue);
        }
		
		expression = createWithIntegerCriteria();
		assertEquals(5, expression.getValues().size());
		for (CriteriaValue<?> value : expression.getValues()) {
			assertTrue("Expression should be CriteriaIntegerValue", value instanceof CriteriaIntegerValue);
		}
		
		expression = createWithDateTimeCriteria();
		assertEquals(1, expression.getValues().size());
		for (CriteriaValue<?> value : expression.getValues()) {
			assertTrue("Expression should be CriteriaDateValue", value instanceof CriteriaDateTimeValue);
		}
	}
	
	/**
	 * Tests serialization to and from XML using JAXB.
	 */
	@Test
	public void testJAXB() {
		
		InPredicate expression = createWithStringCriteria();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(expression, STRING_XML, InPredicate.class);
		
		expression = createWithDecimalCriteria();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(expression, DECIMAL_XML, InPredicate.class);

        expression = createWithKualiDecimalCriteria();
        JAXBAssert.assertEqualXmlMarshalUnmarshal(expression, KUALI_DECIMAL_XML, InPredicate.class);

        expression = createWithKualiPercentCriteria();
        JAXBAssert.assertEqualXmlMarshalUnmarshal(expression, KUALI_PERCENT_XML, InPredicate.class);
		
		expression = createWithIntegerCriteria();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(expression, INTEGER_XML, InPredicate.class);
		
		expression = createWithDateTimeCriteria();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(expression, DATE_TIME_XML, InPredicate.class);
	}
	
	private static InPredicate createWithStringCriteria() {
		Set<CriteriaStringValue> valueList = new HashSet<CriteriaStringValue>();
		valueList.add(new CriteriaStringValue("abcdefg"));
		valueList.add(new CriteriaStringValue("gfedcabc"));
		valueList.add(new CriteriaStringValue("should have failed by now!"));
		return new InPredicate("stringValues.path", valueList);
	}
	
	private static InPredicate createWithDecimalCriteria() {
		Set<CriteriaDecimalValue> valueList = new HashSet<CriteriaDecimalValue>();
		valueList.add(new CriteriaDecimalValue(1.0));
		valueList.add(new CriteriaDecimalValue(1.1));
		valueList.add(new CriteriaDecimalValue(2.5));
		return new InPredicate("decimalValues.path", valueList);
	}

    private static InPredicate createWithKualiDecimalCriteria() {
        Set<CriteriaKualiDecimalValue> valueList = new LinkedHashSet<CriteriaKualiDecimalValue>();
        valueList.add(new CriteriaKualiDecimalValue(2.5));
        valueList.add(new CriteriaKualiDecimalValue(2.6));
        valueList.add(new CriteriaKualiDecimalValue(2.7));
        return new InPredicate("kualiDecimalValues.path", valueList);
    }

    private static InPredicate createWithKualiPercentCriteria() {
        Set<CriteriaKualiPercentValue> valueList = new HashSet<CriteriaKualiPercentValue>();
        valueList.add(new CriteriaKualiPercentValue(26.00));
        valueList.add(new CriteriaKualiPercentValue(27.00));
        valueList.add(new CriteriaKualiPercentValue(25.00));
        return new InPredicate("kualiPercentValues.path", valueList);
    }
	
	private static InPredicate createWithIntegerCriteria() {
		Set<CriteriaIntegerValue> valueList = new HashSet<CriteriaIntegerValue>();
		valueList.add(new CriteriaIntegerValue(1));
		valueList.add(new CriteriaIntegerValue(2));
		valueList.add(new CriteriaIntegerValue(3));
		valueList.add(new CriteriaIntegerValue(10));
		valueList.add(new CriteriaIntegerValue(4));
		return new InPredicate("integerValues.path", valueList);
	}
	
	private static InPredicate createWithDateTimeCriteria() {
		// set the date and time to January 15, 2100 at 5:30:15.500 am in the GMT timezone
        //<dateTimeValue>2011-01-15T05:30:15.500Z</dateTimeValue>
		Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		dateTime.set(Calendar.HOUR_OF_DAY, 5);
		dateTime.set(Calendar.MINUTE, 30);
		dateTime.set(Calendar.SECOND, 15);
		dateTime.set(Calendar.MILLISECOND, 500);
		dateTime.set(Calendar.MONTH, 0);
		dateTime.set(Calendar.DATE, 15);
		dateTime.set(Calendar.YEAR, 2011);
		Set<CriteriaDateTimeValue> valueList = new HashSet<CriteriaDateTimeValue>();
		valueList.add(new CriteriaDateTimeValue(dateTime));
		return new InPredicate("dateTimeValues.path", valueList);
	}

}
