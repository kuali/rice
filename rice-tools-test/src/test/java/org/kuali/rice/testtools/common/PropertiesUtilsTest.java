/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.testtools.common;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PropertiesUtilsTest {

    private static final String JIRA_AWARE_CONTAINS_FAILURES_PROPERTIES = "JiraAwareContainsFailures.properties";

    private static final String PATH_TO_TEST_PROPERTIES_FILE = "org/kuali/rice/testtools/common/PropertiesUtilsTest.properties";

    private static final String PROPERTIES_DIR_INTELLIJ = "rice-tools-test/src/main/resources/";

    private static final String PROPERTIES_DIR_MAVEN = "src/main/resources/";

    private static final String PROPERTY_DOESNT_EXIST_IN_FILE = "PROPERTY_DOESNT_EXIST_IN_FILE";

    private static final String REMOTE_DRIVER_SAUCELABS_PROPERTY = "remote.driver.saucelabs";

    protected static PropertiesUtils propUtils;

    @BeforeClass
    public static void setUp() {
        System.setProperty(REMOTE_DRIVER_SAUCELABS_PROPERTY, "true");
        System.setProperty(PROPERTY_DOESNT_EXIST_IN_FILE, "true");
        propUtils = new PropertiesUtils();
//        printSystemProperties();
    }

    private void assertPropertiesKeysCountNotZero(Properties props) {
        Assert.assertTrue(props.keySet().size() > 0);
    }

    private void assertProperityDoesntExistInFilePropertyTrue(Properties props) {
        Assert.assertTrue(props.getProperty(REMOTE_DRIVER_SAUCELABS_PROPERTY).equals("true"));
    }

    private void assertRemoteDriverSaucelabsPropertyTrue(Properties props) {
        Assert.assertTrue(props.getProperty(REMOTE_DRIVER_SAUCELABS_PROPERTY).equals("true"));
    }

    private static void printSystemProperties() {
        Iterator iter = System.getProperties().stringPropertyNames().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            System.out.println(key + " = " + System.getProperty(key));
        }
    }

    @Test
    public void testLoadPropertiesResource() throws IOException {
        Properties props = propUtils.loadProperties(null, JIRA_AWARE_CONTAINS_FAILURES_PROPERTIES);
        assertPropertiesKeysCountNotZero(props);
    }

    @Test
    public void testLoadPropertiesFile() throws IOException {
        Properties props = propUtils.loadProperties(PROPERTIES_DIR_INTELLIJ + JIRA_AWARE_CONTAINS_FAILURES_PROPERTIES, null); // intellij
        if (props == null) { // mvn
            props = propUtils.loadProperties(PROPERTIES_DIR_MAVEN + JIRA_AWARE_CONTAINS_FAILURES_PROPERTIES, null);
        }
        Assert.assertNotNull(props);
        assertPropertiesKeysCountNotZero(props);
    }

    @Test
    public void testLoadProperties() throws IOException {
        Properties props = propUtils.loadProperties(null, PATH_TO_TEST_PROPERTIES_FILE);
        assertPropertiesKeysCountNotZero(props);
        Assert.assertTrue(props.keySet().contains(REMOTE_DRIVER_SAUCELABS_PROPERTY));
        Assert.assertTrue(props.getProperty(REMOTE_DRIVER_SAUCELABS_PROPERTY).equals(""));
    }

    @Test
    public void testSystemPropertiesOverrides() throws IOException {
        assertProperityDoesntExistInFilePropertyTrue(System.getProperties());
        assertRemoteDriverSaucelabsPropertyTrue(System.getProperties());
        Properties props = propUtils.loadPropertiesWithSystemOverrides(PATH_TO_TEST_PROPERTIES_FILE);
        assertPropertiesKeysCountNotZero(props);
        assertRemoteDriverSaucelabsPropertyTrue(props);
        // PROPERTY_DOESNT_EXIST_IN_FILE but was set in System should not be in props
        Assert.assertFalse(props.containsKey(PROPERTY_DOESNT_EXIST_IN_FILE));
    }

    @Test
    public void testSystemPropertiesAndOverrides() throws IOException {
        assertProperityDoesntExistInFilePropertyTrue(System.getProperties());
        assertRemoteDriverSaucelabsPropertyTrue(System.getProperties());
        Properties props = propUtils.loadPropertiesWithSystemAndOverrides(PATH_TO_TEST_PROPERTIES_FILE);
        assertPropertiesKeysCountNotZero(props);
        assertRemoteDriverSaucelabsPropertyTrue(props);
        // PROPERTY_DOESNT_EXIST_IN_FILE but was set in System should be in props
        assertProperityDoesntExistInFilePropertyTrue(props);
    }

    @Test
    public void testLoadPropertiesWithSystemAndOverridesIntoSystem() throws IOException {
        Assert.assertNull(System.getProperty("saucelabs.browser"));
        assertRemoteDriverSaucelabsPropertyTrue(System.getProperties());
        Properties props = propUtils.loadPropertiesWithSystemAndOverridesIntoSystem(PATH_TO_TEST_PROPERTIES_FILE);
        //saucelabs.browser=ff defined in PATH_TO_TEST_PROPERTIES_FILE should now be in System properties
        Assert.assertTrue(System.getProperty("saucelabs.browser").equals("ff"));
    }
}
