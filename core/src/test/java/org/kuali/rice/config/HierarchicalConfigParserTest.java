package org.kuali.rice.config;

import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Test;

public class HierarchicalConfigParserTest extends TestCase {

	/**
	 * Tests the resolution to issue EN-68 where the overrides are working properly
	 * after attempting to overrdide a property after a few levels of depth.
	 */
	@Test public void testHierarchicalConfigParser() throws Exception {
		// first load the hier-config-3.xml file
//		HierarchicalConfigParser parser = new HierarchicalConfigParser(null);
		SimpleConfig simpleConfig = new SimpleConfig("classpath:org/kuali/rice/config/hier-config-3.xml");
		simpleConfig.parseConfig();
//		String fileLoc1 = ;
//		Map propertyMap = parser.parse(fileLoc1);
		Properties properties = simpleConfig.getProperties();
		assertNotNull(properties);
		assertEquals("user3", properties.get("axis:user"));
		assertEquals("password3", properties.get("axis:password"));
		assertEquals("user3", properties.get("user"));
		assertEquals("password3", properties.get("password"));
		
		// next load the hier-config-2.xml file
		
		String fileLoc2 = "classpath:org/kuali/rice/config/hier-config-2.xml";
		simpleConfig = new SimpleConfig(fileLoc2);
		simpleConfig.parseConfig();
		properties = simpleConfig.getProperties();
		assertNotNull(properties);
		assertEquals("user3", properties.get("user"));
		assertEquals("password3", properties.get("password"));
		assertEquals("user3", properties.get("axis:user"));
		assertEquals("password3", properties.get("axis:password"));
		assertEquals("user3", properties.get("user2"));
		assertEquals("password3", properties.get("password2"));
		
		// next load the hier-config-1.xml file
		String fileLoc3 = "classpath:org/kuali/rice/config/hier-config-1.xml";
		simpleConfig = new SimpleConfig(fileLoc3);
		simpleConfig.parseConfig();
		properties = simpleConfig.getProperties();
		assertNotNull(properties);

		// pull the configuration for hier-config-3 out of the parsing of hier-config-1
//		properties = (Properties)propertyMap.get(fileLoc1);
		assertEquals("user3", properties.get("axis:user"));
		assertEquals("password3", properties.get("axis:password"));
		assertEquals("user3", properties.get("user"));
		assertEquals("password3", properties.get("password"));
	}
	
	/**
	 * all we can do is test that this xml won't blow the parser up.
	 * <param name="config.location">${alt.config.location}</param>
	 * making the logging look good is out of our hands.
	 * 
	 * @throws Exception
	 */
	@Test public void testParsingOfAltConfigLocation() throws Exception {
		String fileLoc1 = "classpath:org/kuali/rice/config/config-alt-location-param.xml";
		SimpleConfig simpleConfig = new SimpleConfig(fileLoc1);
		simpleConfig.parseConfig();
		assertNotNull(simpleConfig.getProperties());
	}
	
}
