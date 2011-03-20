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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;
import org.kuali.rice.core.test.JAXBAssert;

/**
 * A test for the {@link Criteria} class. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class CriteriaTest {

	private static final String XML = "<criteria><like propertyPath=\"display\"><stringValue>*Eric*</stringValue></like><greaterThan propertyPath=\"birthDate\"><dateTimeValue>1980-09-01T05:00:00Z</dateTimeValue></greaterThan><lessThan propertyPath=\"birthDate\"><dateTimeValue>1980-10-01T05:00:00Z</dateTimeValue></lessThan><or><equal propertyPath=\"name.first\"><stringValue>Eric</stringValue></equal><equal propertyPath=\"name.last\"><stringValue>Westfall</stringValue></equal></or></criteria>";
	
	@Test
	public void testCriteria() {
		
		// criteria should be able to take a null list of expressions, but should transform to an empty list
		Criteria criteria = new Criteria(null);
		assertNotNull("Expression list should not be null.", criteria.getExpressions());
		assertTrue("Expression list should be empty.", criteria.getExpressions().isEmpty());
		
		List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(new AndExpression(null));
		criteria = new Criteria(expressions);
		
		assertEquals(1, criteria.getExpressions().size());
		
	}
	
	/**
	 * Tests the serialization of a Criteria object to and from XML using JAXB.
	 */
	@Test
	public void testJAXB() throws Exception {
		JAXBAssert.assertEqualXmlMarshalUnmarshal(create(), XML, Criteria.class);
	}
	
	// TODO add a test which tests jaxb with all possible combinations of expressions to make sure they are all getting serialized properly
	
	private static Criteria create() throws Exception {
		Date gtBirthDate = new SimpleDateFormat("yyyyMMdd").parse("19800901");
		Date ltBirthDate = new SimpleDateFormat("yyyyMMdd").parse("19801001");
		// normalize timezone to GMT for purpose of the tests
		Calendar gtCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		gtCalendar.setTimeInMillis(gtBirthDate.getTime());
		Calendar ltCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		ltCalendar.setTimeInMillis(ltBirthDate.getTime());
		
		CriteriaBuilder<Person> builder = CriteriaBuilder.newCriteriaBuilder(Person.class);
		builder.like("display", "*Eric*");
		builder.greaterThan("birthDate", gtCalendar);
		builder.lessThan("birthDate", ltCalendar);
		CriteriaBuilder<Person> orCriteria = builder.or();
		orCriteria.equal("name.first", "Eric");
		orCriteria.equal("name.last", "Westfall");
		
		return builder.build();
	}
	
}
