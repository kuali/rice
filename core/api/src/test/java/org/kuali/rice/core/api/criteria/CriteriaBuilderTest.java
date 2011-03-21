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
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

/**
 * Test the {@link CriteriaBuilder}.  Runs through a few different CriteriaBuilder scenarios.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CriteriaBuilderTest {

	@Test
	public void testNewCriteriaBuilder() {
		
		// test failure conditions
		try {
			CriteriaBuilder.newCriteriaBuilder(null);
			fail("IllegalArgumentException should have been thrown.");
		} catch (IllegalArgumentException e) {
			// expected exception
		}
		
		// any class will do
		CriteriaBuilder<Object> builder = CriteriaBuilder.newCriteriaBuilder(Object.class);
		assertNotNull(builder);
	}
	
	@Test
	public void testBuild() throws Exception {
		
		Date gtBirthDate = new SimpleDateFormat("yyyyMMdd").parse("19800901");
		Date ltBirthDate = new SimpleDateFormat("yyyyMMdd").parse("19801001");
		
		CriteriaBuilder<Person> builder = CriteriaBuilder.newCriteriaBuilder(Person.class);
		builder.like("display", "*Eric*");
		builder.greaterThan("birthDate", gtBirthDate);
		builder.lessThan("birthDate", ltBirthDate);
		CriteriaBuilder<Person> orCriteria = builder.or();
		orCriteria.equal("name.first", "Eric");
		orCriteria.equal("name.last", "Westfall");
		
		// now build the criteria
		Criteria criteria = builder.build();
		assertEquals("Criteria should have 4 expressions", 4, criteria.getExpressions().size());
		
		LikeExpression foundLike = null;
		GreaterThanExpression foundGt = null;
		LessThanExpression foundLt = null;
		OrExpression foundOr = null;
		for (Expression expression : criteria.getExpressions()) {
			if (expression instanceof LikeExpression) {
				foundLike = (LikeExpression)expression;
			} else if (expression instanceof GreaterThanExpression) {
				foundGt = (GreaterThanExpression)expression;
			} else if (expression instanceof LessThanExpression) {
				foundLt = (LessThanExpression)expression;
			} else if (expression instanceof OrExpression) {
				foundOr = (OrExpression)expression;
			} else {
				fail("Found an expression which should not have been found: " + expression);
			}
		}
		assertNotNull("Should have found a LikeExpression", foundLike);
		assertNotNull("Should have found a GreaterThanExpression", foundGt);
		assertNotNull("Should have found a LessThanExpression", foundLt);
		assertNotNull("Should have found an OrExpression", foundOr);
		
		assertEquals("display", foundLike.getPropertyPath());
		assertEquals("*Eric*", foundLike.getValue().getValue());
		
		assertEquals("birthDate", foundGt.getPropertyPath());
		assertTrue(foundGt.getValue() instanceof CriteriaDateTimeValue);
		assertEquals(new CriteriaDateTimeValue(gtBirthDate), foundGt.getValue());
		
		assertEquals("birthDate", foundLt.getPropertyPath());
		assertTrue(foundLt.getValue() instanceof CriteriaDateTimeValue);
		assertEquals(new CriteriaDateTimeValue(ltBirthDate), foundLt.getValue());
		
		assertEquals("OrExpression should have 2 expressions", 2, foundOr.getExpressions().size());
		
		// note that the expressions within the or should be ordered, we should be able to fetch them out in the same order they were put in
		EqualExpression nameFirstExpression = (EqualExpression)foundOr.getExpressions().get(0);
		EqualExpression nameLastExpression = (EqualExpression)foundOr.getExpressions().get(1);
		
		assertEquals("name.first", nameFirstExpression.getPropertyPath());
		assertEquals("Eric", nameFirstExpression.getValue().getValue());
		assertEquals("name.last", nameLastExpression.getPropertyPath());
		assertEquals("Westfall", nameLastExpression.getValue().getValue());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEqual_nullPropertyPath() {
		create().equal(null, "value");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEqual_nullValue() {
		create().equal("propertyPath", null);
	}
	
	@Test
	public void testEqual() {
		CriteriaBuilder<Object> builder = create();
		builder.equal("propertyPath", "propertyValue");
		builder.equal("propertyPath", 100);
		builder.equal("propertyPath", 50.5);
		builder.equal("propertyPath", BigDecimal.ONE);
		builder.equal("propertyPath", new Date());
		builder.equal("propertyPath", Calendar.getInstance());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNotEqual_nullPropertyPath() {
		create().notEqual(null, "value");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNotEqual_nullValue() {
		create().notEqual("propertyPath", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLike_nullPropertyPath() {
		create().like(null, "value");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLike_nullValue() {
		create().like("propertyPath", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLike_illegalValue() {
		create().like("pp", 100);
	}
	
	@Test
	public void testLike() {
		create().like("pp", "val*");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIn_nullPropertyPath() {
		create().in(null, Collections.singletonList("value"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIn_nullValues() {
		create().in("propertyPath", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIn_emptyValues() {
		create().in("propertyPath", Collections.emptyList());
	}
	
	@Test
	public void testIn() {
		List<String> stringValues = new ArrayList<String>();
		stringValues.add("value1");
		stringValues.add("value2");
		stringValues.add("value3");
		create().in("pp", stringValues);
		
		List<Integer> intValues = new ArrayList<Integer>();
		intValues.add(1);
		intValues.add(2);
		intValues.add(3);
		create().in("pp", intValues);
		
		List<Double> dValues = new ArrayList<Double>();
		dValues.add(1.0);
		dValues.add(2.6);
		create().in("pp", dValues);
		
		List<Date> dtValues = new ArrayList<Date>();
		dtValues.add(new Date());
		dtValues.add(new Date());
		create().in("pp", dtValues);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNotIn_nullPropertyPath() {
		create().notIn(null, Collections.singletonList("value"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNotIn_nullValues() {
		create().notIn("propertyPath", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNotIn_emptyValues() {
		create().notIn("propertyPath", Collections.emptyList());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGreaterThan_nullPropertyPath() {
		create().greaterThan(null, 100);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGreaterThan_nullValue() {
		create().greaterThan("propertyPath", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGreaterThan_illegalValue() {
		create().greaterThan("pp", "stringValue");
	}
	
	@Test
	public void testGreaterThan() {
		create().greaterThan("pp", 100);
		create().greaterThan("pp", 50.7654354);
		create().greaterThan("pp", Calendar.getInstance());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGreaterThanOrEqual_nullPropertyPath() {
		create().greaterThanOrEqual(null, 100);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGreaterThanOrEqual_nullValue() {
		create().greaterThanOrEqual("propertyPath", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLessThan_nullPropertyPath() {
		create().lessThan(null, 100);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLessThan_nullValue() {
		create().lessThan("propertyPath", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLessThanOrEqual_nullPropertyPath() {
		create().lessThanOrEqual(null, 100);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLessThanOrEqual_nullValue() {
		create().lessThanOrEqual("propertyPath", null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsNull_nullPropertyPath() {
		create().isNull(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIsNull_emptyPropertyPath() {
		create().isNull("");
	}
	
	public void testIsNull() {
		create().isNull("any");
		create().isNull("non-empty");
		create().isNull("value");
		create().isNull("will do");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIsNotNull_nullPropertyPath() {
		create().isNotNull(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testIsNotNull_emptyPropertyPath() {
		create().isNotNull("");
	}
	
	public void testIsNotNull() {
		create().isNotNull("any");
		create().isNotNull("non-empty");
		create().isNotNull("value");
		create().isNotNull("will do");
	}
	
	@Test
	public void testAnd() {
		CriteriaBuilder<Object> andBuilder = create().and();
		assertNotNull(andBuilder);
	}
	
	@Test
	public void testOr() {
		CriteriaBuilder<Object> orBuilder = create().or();
		assertNotNull(orBuilder);
	}
	
	@Test
	public void testDeepNesting() {
		CriteriaBuilder<Object> builder = create().or().and().or().and().and().and();
		builder.equal("whatWasThat?", "That was crazy!");
	}
	
	private static CriteriaBuilder<Object> create() {
		return CriteriaBuilder.newCriteriaBuilder(Object.class);
	}

}
