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

import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;

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
	public void testCriteriaBuilder_example() throws Exception {
		
		CriteriaBuilder<Person> builder = CriteriaBuilder.newCriteriaBuilder(Person.class);
		builder.like("display", "*Eric*");
		builder.greaterThan("birthDate", new SimpleDateFormat("yyyyMMdd").parse("19800901"));
		builder.lessThan("birthDate", new SimpleDateFormat("yyyyMMdd").parse("19801001"));
		CriteriaBuilder<Person> orCriteria = builder.or();
		orCriteria.equal("name.first", "Eric");
		orCriteria.equal("name.last", "Westfall");
		
		// now build the criteria
		Criteria criteria = builder.build();
		// TODO
	}
	
}
