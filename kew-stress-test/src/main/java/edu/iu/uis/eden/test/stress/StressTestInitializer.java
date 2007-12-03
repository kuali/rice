/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package edu.iu.uis.eden.test.stress;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.exceptions.RiceRuntimeException;
import org.kuali.rice.resourceloader.SpringResourceLoader;
import org.springframework.util.Log4jConfigurer;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;

public class StressTestInitializer implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(StressTestInitializer.class);

    public static SpringResourceLoader SPRING_LOADER = new SpringResourceLoader(new QName("StressTest"),
	    "classpath:WebServiceSpringBeans.xml");

    public void contextInitialized(ServletContextEvent event) {
	LOG.debug("starting stress test application");
	try {
	    Log4jConfigurer.initLogging("classpath:stress-test-log4j.properties");
	    SPRING_LOADER.start();
	} catch (Exception e) {
	    LOG.error("Failed to start stress test app", e);
	    throw new RiceRuntimeException(e);
	}
    }

    public void contextDestroyed(ServletContextEvent event) {
	try {
	    SPRING_LOADER.stop();
	    // configurer.stop();
	} catch (Exception e) {
	    throw new WorkflowRuntimeException(e);
	}
    }

    // private RiceConfigurer configureEmbeddedClient() throws Exception {
    // JotmFactoryBean jotmFactoryBean = new JotmFactoryBean();
    // jotmFactoryBean.setDefaultTimeout(600000);
    // Current jotm = (Current)jotmFactoryBean.getObject();
    //
    // // configure the datasource
    // XAPoolDataSource dataSource = new XAPoolDataSource();
    // dataSource.setTransactionManager(jotm);
    // dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
    // dataSource.setMaxSize(70);
    // dataSource.setMinSize(20);
    // dataSource.setMaxWait(15000);
    // dataSource.setValidationQuery("select 1 from dual");
    // dataSource.setUrl("jdbc:oracle:thin:@es01.uits.indiana.edu:1521:GEN2DEV");
    // dataSource.setUsername("en");
    // dataSource.setPassword("endev");
    //
    // RiceConfigurer configurer = new RiceConfigurer();
    // configurer.setEnvironment("dev");
    // configurer.setMessageEntity("StressTest");
    // configurer.setPlatform("Oracle9i");
    // configurer.setDataSource(dataSource);
    // configurer.setTransactionManager(jotm);
    // configurer.setUserTransaction(jotm);
    // KEWConfigurer kewConfig = new KEWConfigurer();
    // kewConfig.setServiceServletUrl("http://localhost:8080/en-stress/remoting");
    // kewConfig.setClientProtocol("embedded");
    // configurer.getModules().add(kewConfig);
    // return configurer;
    // }
    //
    // private String loadXML(String resourceName) {
    // try {
    // InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
    // StringWriter writer = new StringWriter();
    // int data = -1;
    // while ((data = inputStream.read()) != -1) {
    // writer.write(data);
    // }
    // return writer.toString();
    // } catch (IOException e) {
    // throw new RuntimeException("Fatal error initializing Stress Tests", e);
    // }
    // }

}
