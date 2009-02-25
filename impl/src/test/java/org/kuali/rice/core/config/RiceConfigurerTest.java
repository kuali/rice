/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.core.config;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Unit Test for RiceConfigurer class -- highly incomplete at the moment
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RiceConfigurerTest {
	
	/**
	 * tests the setAdditionalSpringFiles method, particularly its
	 * handling of comma separated string input
	 */
	@Test 
	public void testSetAdditionalSpringFiles() {
		RiceConfigurer rc = new RiceConfigurer();
		
		List<String> commaSeparated = Arrays.asList("one,two,three,four,five,six"); 
		List<String> multipleCommaSeparated = Arrays.asList("one,two,three","four,five,six"); 
		List<String> commaSeparatedAndNot = Arrays.asList("one,two,three","four","five","six");
		
		// all the above should expand to contain the following:
		List<String> notCommaSeparated = Arrays.asList("one","two","three","four","five","six");
		
		rc.setAdditionalSpringFiles(null); // shouldn't throw an exception
		assertTrue(rc.getAdditionalSpringFiles() == null || rc.getAdditionalSpringFiles().size() == 0);
		
		rc.setAdditionalSpringFiles(commaSeparated);
		assertEquals(rc.getAdditionalSpringFiles(), notCommaSeparated);
		
		rc.setAdditionalSpringFiles(multipleCommaSeparated);
		assertEquals(rc.getAdditionalSpringFiles(), notCommaSeparated);
		
		rc.setAdditionalSpringFiles(commaSeparatedAndNot);
		assertEquals(rc.getAdditionalSpringFiles(), notCommaSeparated);
		
		rc.setAdditionalSpringFiles(notCommaSeparated);
		assertEquals(rc.getAdditionalSpringFiles(), notCommaSeparated);
	}

}
