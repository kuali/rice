/*
 * Copyright 2007 The Kuali Foundation
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

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HierarchicalConfigParserTest {

	private static String simpleConfig = "SIMPLE_CONFIG";
	private static String jaxbConfig = "JAXB_CONFIG";
	
	
	// We want to test with both impls
	protected Config getConfigObject(String configType, String fileName){
		Config cRet = null;
		if(simpleConfig.equals(configType)){
			cRet = new SimpleConfig(fileName);
		}else if(jaxbConfig.equals(configType)){
			cRet = new JAXBConfigImpl(fileName);
		}
		return cRet;
	}
	
	/**
	 * Tests the resolution to issue EN-68 where the overrides are working properly
	 * after attempting to overrdide a property after a few levels of depth.
	 */
	@Test public void testHierarchicalConfigParser() throws Exception {
		
		testHierarchicalConfigParserImpl(simpleConfig);
		testHierarchicalConfigParserImpl(jaxbConfig);
		
	}
		
	protected void testHierarchicalConfigParserImpl(String configType) throws Exception {
		// first load the hier-config-3.xml file
//		HierarchicalConfigParser parser = new HierarchicalConfigParser(null);
		Config simpleConfig = getConfigObject(configType, "classpath:org/kuali/rice/core/config/hier-config-3.xml");
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
		
		String fileLoc2 = "classpath:org/kuali/rice/core/config/hier-config-2.xml";
		simpleConfig = getConfigObject(configType,fileLoc2);
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
		String fileLoc3 = "classpath:org/kuali/rice/core/config/hier-config-1.xml";
		simpleConfig = getConfigObject(configType,fileLoc3);
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
		testParsingOfAltConfigLocationImpl(simpleConfig);
		testParsingOfAltConfigLocationImpl(jaxbConfig);
	}
	
	protected void testParsingOfAltConfigLocationImpl(String configType) throws Exception {
		String fileLoc1 = "classpath:org/kuali/rice/core/config/config-alt-location-param.xml";
		Config simpleConfig = getConfigObject(configType,fileLoc1);
		simpleConfig.parseConfig();
		assertNotNull(simpleConfig.getProperties());
	}
	
}
