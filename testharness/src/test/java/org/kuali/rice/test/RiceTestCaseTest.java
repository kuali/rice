package org.kuali.rice.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.core.Core;

/**
 * Verifies that the RiceTestCase starts up cleanly.
 * 
 * @author rkirkend
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