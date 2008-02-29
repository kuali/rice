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
package org.kuali.rice.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.core.Core;

/**
 * Verifies that the RiceTestCase starts up cleanly.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RiceTestCaseTest extends RiceTestCase {

	@Test
	public void testTestCase() throws Exception {
		String testConfigVal = Core.getCurrentContextConfig().getProperty("rice.test.case.test");
		assertEquals("Test config value should have been properly configured.", "test", testConfigVal);
	}
	
	@Override
	protected List<String> getConfigLocations() {
		return Arrays.asList(new String[]{"classpath:META-INF/testharness-test-config.xml"});
	}

	@Override
	protected String getModuleName() {
		return "testharness";
	}

	@Override
	protected String getDerbySQLFileLocation() {
		return "classpath:db/derby/testharness.sql";
	}

}