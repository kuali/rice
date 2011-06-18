/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kew.test;

import org.kuali.rice.core.impl.config.property.BaseConfig;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * BaseConfig implementation that is the entry point for workflow configuration
 * running under unit tests.
 * A 'test.platform' property is defined in the base properties.  This property
 * is initialized from TestUtilities.getTestPlatform() which obtains the property
 * from the Ant build.properties as a convenience.
 */
public class TestConfigurer extends BaseConfig {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(TestConfigurer.class);

	private static final String TEST_PLATFORM = "test.platform";

	private static List<String> fileLocations = new ArrayList<String>();
	private String testPlatform;

	public TestConfigurer() throws IOException {
		super(fileLocations);
		this.testPlatform = TestUtilities.getTestPlatform();
		System.setProperty(TEST_PLATFORM, testPlatform);
		// this is ghetto but the constructor aspect of the BaseConfig is making it difficult for me to do what I want to here
		fileLocations.clear();
		String configFile = "classpath:META-INF/test-"+testPlatform+"-workflow.xml";
		fileLocations.add(configFile);
		LOG.info("Intitializing TestConfigurer with configuration file: " + configFile);
	}

	public Properties getBaseProperties() {
		Properties baseProps = new Properties();
		try {
			//baseProps.put("workflow.base", ResourceUtils.getFile("classpath:").getAbsolutePath());
			baseProps.put("workflow.base", ResourceUtils.getFile("classpath:").getAbsolutePath()+"/../../..");
			baseProps.put(TEST_PLATFORM, testPlatform);
			//baseProps.put("workflowBase", ".");
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
		return baseProps;
	}

	public Map getBaseObjects() {
		return null;
	}

}
