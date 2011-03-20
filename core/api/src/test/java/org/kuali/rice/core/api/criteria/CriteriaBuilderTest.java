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

import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	// TODO probably should have a simple test for each of the builder methods
	
}
