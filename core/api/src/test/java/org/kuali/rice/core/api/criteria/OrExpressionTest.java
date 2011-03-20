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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.core.test.JAXBAssert;

/**
 * Tests the {@link OrExpression} class. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class OrExpressionTest {

	private static final String XML = "<or><equal propertyPath=\"property.path\"><stringValue>abcdefg</stringValue></equal><greaterThan propertyPath=\"property.path2\"><decimalValue>100</decimalValue></greaterThan><and><greaterThan propertyPath=\"property.path3\"><integerValue>10000</integerValue></greaterThan><like propertyPath=\"property.path4\"><stringValue>wildcard*</stringValue></like></and></or>"; 
		
	/**
	 * Test method for {@link org.kuali.rice.core.api.criteria.OrExpression#OrExpression(java.util.List)}.
	 */
	@Test
	public void testOrExpression() {
		OrExpression expression = create();
		assertNotNull(expression);
		
		// should default to an empty list
		expression = new OrExpression(null);
		assertNotNull(expression.getExpressions());
		assertTrue(expression.getExpressions().isEmpty());
	}

	/**
	 * Test method for {@link org.kuali.rice.core.api.criteria.AbstractCompositeExpression#getExpressions()}.
	 */
	@Test
	public void testGetExpressions() {
		OrExpression orExpression = create();
		assertEquals("Or expression should have 3 expressions", 3, orExpression.getExpressions().size());
		// one should be an OrExpression with 2 expressions
		for (Expression expression : orExpression.getExpressions()) {
			if (expression instanceof AndExpression) {
				assertEquals("And expression should have 2 expressions", 2, ((AndExpression)expression).getExpressions().size());
			}
		}
	}
	
	/**
	 * Tests that the AndExpression can be marshaled and unmarshaled properly via JAXB.
	 */
	@Test
	public void testJAXB() {
		OrExpression orExpression = create();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(orExpression, XML, OrExpression.class);
	}
	
	private OrExpression create() {
		List<Expression> orExpressions = new ArrayList<Expression>();
		orExpressions.add(new EqualExpression("property.path", new CriteriaStringValue("abcdefg")));
		orExpressions.add(new GreaterThanExpression("property.path2", new CriteriaDecimalValue(new BigDecimal(100))));
		
		List<Expression> andExpressions = new ArrayList<Expression>();
		andExpressions.add(new GreaterThanExpression("property.path3", new CriteriaIntegerValue(BigInteger.valueOf(10000))));
		andExpressions.add(new LikeExpression("property.path4", new CriteriaStringValue("wildcard*")));
		
		AndExpression andExpression = new AndExpression(andExpressions);
		orExpressions.add(andExpression);
		
		return new OrExpression(orExpressions);
	}


}
