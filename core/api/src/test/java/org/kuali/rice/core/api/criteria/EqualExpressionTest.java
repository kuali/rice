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
package org.kuali.rice.core.api.criteria;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;
import org.kuali.rice.core.test.JAXBAssert;

/**
 * A test for the {@link EqualExpression} class. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EqualExpressionTest {

	private static final String STRING_XML = "<equal propertyPath=\"property.path\"><stringValue>value</stringValue></equal>";
	private static final String DECIMAL_XML = "<equal propertyPath=\"property.path\"><decimalValue>0</decimalValue></equal>";
	private static final String INTEGER_XML = "<equal propertyPath=\"property.path\"><integerValue>0</integerValue></equal>";
	private static final String DATE_TIME_XML = "<equal propertyPath=\"property.path\"><dateTimeValue>2011-01-15T05:30:15.500Z</dateTimeValue></equal>";
	
	
	/**
	 * Test method for {@link org.kuali.rice.core.api.criteria.EqualExpression#EqualExpression(java.lang.String, org.kuali.rice.core.api.criteria.CriteriaValue)}.
	 * 
	 * <p>EqualExpression should support all four of the different CriteriaValues
	 */
	@Test
	public void testEqualExpression() {
		
		// Test that it can take a CriteriaStringValue
		EqualExpression equalExpression = new EqualExpression("property.path", new CriteriaStringValue("value"));
		assertEquals("property.path", equalExpression.getPropertyPath());
		assertEquals("value", equalExpression.getValue().getValue());
		
		// Test that it can take a CriteriaDecimalValue
		equalExpression = new EqualExpression("property.path", new CriteriaDecimalValue(BigDecimal.ZERO));
		assertEquals("property.path", equalExpression.getPropertyPath());
		assertEquals(BigDecimal.ZERO, equalExpression.getValue().getValue());
		
		// Test that it can take a CriteriaIntegerValue
		equalExpression = new EqualExpression("property.path", new CriteriaIntegerValue(BigInteger.ZERO));
		assertEquals("property.path", equalExpression.getPropertyPath());
		assertEquals(BigInteger.ZERO, equalExpression.getValue().getValue());
		
		// Test that it can take a CriteriaDateTimeValue
		Calendar dateTime = Calendar.getInstance();
		equalExpression = new EqualExpression("property.path", new CriteriaDateTimeValue(dateTime));
		assertEquals("property.path", equalExpression.getPropertyPath());
		assertEquals(dateTime, equalExpression.getValue().getValue());
		
		// test failure cases, should throw IllegalArgumentException when null is passed
		try {
			new EqualExpression(null, null);
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected exception
		}
	}
	
	/**
	 * Tests that the EqualExpression can be marshalled and unmarshalled properly via JAXB.
	 * This method ...
	 *
	 */
	@Test
	public void testJAXB() {
		EqualExpression equalExpression = new EqualExpression("property.path", new CriteriaStringValue("value"));
		JAXBAssert.assertEqualXmlMarshalUnmarshal(equalExpression, STRING_XML, EqualExpression.class);
		
		equalExpression = new EqualExpression("property.path", new CriteriaDecimalValue(BigDecimal.ZERO));
		JAXBAssert.assertEqualXmlMarshalUnmarshal(equalExpression, DECIMAL_XML, EqualExpression.class);
		
		equalExpression = new EqualExpression("property.path", new CriteriaIntegerValue(BigInteger.ZERO));
		JAXBAssert.assertEqualXmlMarshalUnmarshal(equalExpression, INTEGER_XML, EqualExpression.class);
		
		// set the date and time to January 15, 2100 at 5:30:15.500 am in the GMT timezone
		Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		dateTime.set(Calendar.HOUR_OF_DAY, 5);
		dateTime.set(Calendar.MINUTE, 30);
		dateTime.set(Calendar.SECOND, 15);
		dateTime.set(Calendar.MILLISECOND, 500);
		dateTime.set(Calendar.MONTH, 0);
		dateTime.set(Calendar.DATE, 15);
		dateTime.set(Calendar.YEAR, 2011);
		
		equalExpression = new EqualExpression("property.path", new CriteriaDateTimeValue(dateTime));
		JAXBAssert.assertEqualXmlMarshalUnmarshal(equalExpression, DATE_TIME_XML, EqualExpression.class);
	}
	
//	protected void assertEqualXmlMarshalUnmarshal(Class<?> jaxbClass, Object object, String expectedXml) {
//		try {
//		  JAXBContext jaxbContext = JAXBContext.newInstance(jaxbClass);
//		  Marshaller marshaller = jaxbContext.createMarshaller();
//		  
//		  StringWriter stringWriter = new StringWriter();
//		  
//		  marshaller.marshal(object, stringWriter);
//
//		  Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//		  
//		  System.out.println(stringWriter.toString());
//		  
//		  Object actual = unmarshaller.unmarshal(new StringReader(stringWriter.toString()));
//		  assertEquals("Unmarshalled object should be equal to original object.", object, actual);
//		  
//		  Object expected = unmarshaller.unmarshal(new StringReader(expectedXml));
//		  assertEquals("Unmarshalled objects should be equal.", expected, actual);
//		} catch (JAXBException e) {
//			throw new RuntimeException("Failed to marshall/unmarshall with JAXB.  See the nested exception for details.", e);
//		}
//	}

}
