/*
 * Copyright 2007-2010 The Kuali Foundation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JAXBConfigImplTest {

    private static final String DEFAULT_LOG4J_CONFIG = "org/kuali/rice/core/logging/default-log4j.properties";

    @BeforeClass
    public static void setupClass() {
        try {
            Properties p = new Properties();
            p.load(JAXBConfigImplTest.class.getClassLoader().getResourceAsStream(DEFAULT_LOG4J_CONFIG));
            PropertyConfigurator.configure(p);
        } catch (Exception e) {
            // if there is an issue initializing logging system, let's be sure to print the stack trace so we can debug!
            e.printStackTrace();
            throw new RiceRuntimeException(e);
        }
    }

    @AfterClass
    public static void destroyClass() {
        LogManager.shutdown();
    }

    @Test
    public void testBasicFunctionality() throws Exception {
        System.setProperty("some.system.property", "sys-value");
        JAXBConfigImpl config = new JAXBConfigImpl("classpath:org/kuali/rice/core/config/jaxb-test-config.xml");
    
        config.parseConfig();

        doBasicAssertions(config);
    }

    protected void doBasicAssertions(Config config) {
        // testing deferred resolution
        assertEquals("2", config.getProperty("b"));

        // testing nested parameters
        assertEquals("oracle-user", config.getProperty("username"));

        // testing multiple parameters in value
        assertEquals("oracle-user+oracle", config.getProperty("multi"));

        // testing random integer generation
        int x = Integer.valueOf(config.getProperty("randomInt"));
        assertTrue(x >= 10);
        assertTrue(x <= 20);

        // testing setting of system parameters
        assertEquals("doIt", System.getProperty("some.derby.config"));

        // testing override setting
        assertEquals("original", config.getProperty("foo"));

        // testing default to system param when not in file
        assertEquals("sys-value", config.getProperty("defaultToSystem"));
    }

    @Test
    public void testSystemOverride() throws Exception {
        System.setProperty("a", "3");

        JAXBConfigImpl config = new JAXBConfigImpl("classpath:org/kuali/rice/core/config/jaxb-test-config.xml");
        
        config.setSystemOverride(true);
        config.parseConfig();
        assertEquals("3", config.getProperty("b"));
    }

    @Test
    public void testOverrideResolution() throws Exception {
        List<String> files = new ArrayList<String>();
        files.add("classpath:org/kuali/rice/core/config/jaxb-test-config.xml");
        files.add("classpath:org/kuali/rice/core/config/jaxb-test-override.xml");

        JAXBConfigImpl config = new JAXBConfigImpl(files);
        
        config.parseConfig();
        assertEquals("mysql-user", config.getProperty("username"));
    }

    @Test
    public void testImportOverride() throws Exception {
        List<String> files = new ArrayList<String>();
        files.add("classpath:org/kuali/rice/core/config/jaxb-test-import-config.xml");

        JAXBConfigImpl config = new JAXBConfigImpl(files);
        
        config.parseConfig();
        assertEquals("mysql-user", config.getProperty("username"));
    }

    @Test(expected = ConfigurationException.class)
    public void testCircularReference() throws Exception {
        JAXBConfigImpl config = new JAXBConfigImpl("classpath:org/kuali/rice/core/config/jaxb-test-circular.xml");
        
        config.parseConfig();
    }
    
    @Test(expected = ConfigurationException.class)
    public void testCircularReference2() throws Exception {
        JAXBConfigImpl config = new JAXBConfigImpl("classpath:org/kuali/rice/core/config/jaxb-test-circular2.xml");
        
        config.parseConfig();
    }
    
    /**
     * 		
     * This method tests having a variable already set and then doing a circular reference.
     * 
     * @throws Exception
     */
    @Test
    public void testCircularReference3() throws Exception{
        JAXBConfigImpl config = new JAXBConfigImpl("classpath:org/kuali/rice/core/config/jaxb-test-circular2.xml");
        // The next line tests 2 things. 1. the inline replace, but with a system var
        System.setProperty("environment", "test");
        config.parseConfig();
        
        // run the test again, but this time use a standard config var.
        config = new JAXBConfigImpl("classpath:org/kuali/rice/core/config/jaxb-test-circular2.xml");   
        config.putProperty("environment", "test");
        config.parseConfig();
    }

    /**
     * 		
     * This method tests parsing a config file and then altering a variable that is used as a composite for other variables.
     * 
     * @throws Exception
     */
    @Test
    public void testResolutionAfterParse() throws Exception{
        JAXBConfigImpl config = new JAXBConfigImpl("classpath:org/kuali/rice/core/config/jaxb-test-config.xml");        
        config.parseConfig();
                  
        config.putProperty("db", "mysql");        
        
        assertEquals("mysql-user", config.getProperty("username"));
        assertEquals("mysql-user+mysql", config.getProperty("multi"));
        assertEquals("mysql", config.getProperty("db"));
        
    }
    
    @Ignore
    public void testPropertiesParams() throws Exception {
    	Config rootConfig = ConfigContext.getCurrentContextConfig();    	
        
        JAXBConfigImpl config = new JAXBConfigImpl("classpath:org/kuali/rice/core/config/jaxb-test-config.xml");
        config.parseConfig();

        // When you run the tests via maven the suite initially sets up the a rootConfig object.
        // If this is the case then we should just Add our properties to the root config.
        // If we call the init then we overwrite the existing config in the context, which breaks
        // other tests in the suite.
        if(rootConfig != null){
        	rootConfig.putProperties(config.getProperties());
        }else{
        ConfigContext.init(config);
        }
        
        ApplicationContext context = new ClassPathXmlApplicationContext("org/kuali/rice/core/config/jaxb-test-context.xml");
        PropertyHolder holder = (PropertyHolder) context.getBean("jpaProps");

        assertEquals(3, holder.getJpaProps().size());
        assertEquals("1", holder.getJpaProps().getProperty("jpa1"));
        assertEquals(null, holder.getJpaProps().getProperty("jpaFail"));

    }

}
