/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.core.config;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Unit Test for RiceConfigurer class -- highly incomplete at the moment
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RiceConfigurerTest {
	
	/**
	 * tests the setAdditionalSpringFiles method, particularly its
	 * handling of comma separated string input
	 */
	@Test 
	public void testSetAdditionalSpringFiles() {
		ModuleConfigurer rc = new ModuleConfigurer() {{
			setModuleName("foo"); 
		}};
		
		Config c = new JAXBConfigImpl();
		ConfigContext.init(c);
		
		assertTrue(rc.getAdditionalSpringFiles() == null || rc.getAdditionalSpringFiles().size() == 0);
		
		// all the above should expand to contain the following:
		List<String> expectedResult = Arrays.asList("one","two","three","four","five","six");
		
		c.putProperty("rice.foo.additionalSpringFiles", "one,two,three,four,five,six");
		assertEquals(expectedResult, rc.getAdditionalSpringFiles());
		
		c.putProperty("rice.foo.additionalSpringFiles", "one, two,three,four,five,six ");
		assertEquals(expectedResult, rc.getAdditionalSpringFiles());
		
		c.putProperty("rice.foo.additionalSpringFiles", "one,two,three,,four,five,six");
		assertEquals(expectedResult, rc.getAdditionalSpringFiles());
		
		c.putProperty("rice.foo.additionalSpringFiles", "one");
		assertEquals(Arrays.asList("one"), rc.getAdditionalSpringFiles());
	}

}
