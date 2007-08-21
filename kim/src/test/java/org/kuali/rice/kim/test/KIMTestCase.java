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
package org.kuali.rice.kim.test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.spring.ConfigFactoryBean;
import org.kuali.rice.core.Core;
import org.kuali.rice.exceptions.RiceRuntimeException;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.kuali.rice.resourceloader.SpringResourceLoader;
import org.kuali.rice.test.RiceTestCase;
import org.kuali.rice.test.lifecycles.JettyServerLifecycle;
import org.kuali.rice.test.lifecycles.KEWXmlDataLoaderLifecycle;
import org.kuali.rice.test.lifecycles.SQLDataLoaderLifecycle;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.springframework.context.ApplicationContext;

import edu.iu.uis.eden.messaging.bam.BAMService;
import edu.iu.uis.eden.messaging.bam.BAMTargetEntry;
import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;
import edu.iu.uis.eden.server.TestClient1;
import edu.iu.uis.eden.server.TestClient2;

public class KIMTestCase extends RiceTestCase {
    private ResourceLoader springContextResourceLoader;

    @Override
    protected List<String> getConfigLocations() {
	return Arrays.asList(new String[]{"classpath:META-INF/kim-test-config.xml"});
    }

    @Override
    protected String getDerbySQLFileLocation() {
	return "classpath:db/derby/kim.sql";
    }

    @Override
    protected String getModuleName() {
	return "kim";
    }

    @Override
    protected List<Lifecycle> getSuiteLifecycles() {
	List<Lifecycle> lifeCycles = super.getPerTestLifecycles();
	lifeCycles.add(new Lifecycle() {

	    boolean started = false;

	    public boolean isStarted() {
			return this.started;
	    }

	    public void start() throws Exception {
//    	    	    ConfigFactoryBean.CONFIG_OVERRIDE_LOCATION = getTestConfigFilename();
//    	    	    new SQLDataLoaderLifecycle(getSqlFilename(), getSqlDelimiter()).start();
//    		    new JettyServerLifecycle(getPort(), getContextName(), getRelativeWebappRoot()).start();
//    		    new KEWXmlDataLoaderLifecycle(getXmlFilename()).start();
	    	    this.started = true;
	    }

	    public void stop() throws Exception {
		    this.started = false;
	    }

	});
	return lifeCycles;
    }
    
    public ResourceLoader getSpringContextResourceLoader() {
	return this.springContextResourceLoader;
    }

    public void setSpringContextResourceLoader(ResourceLoader testHarnessResourceLoader) {
	this.springContextResourceLoader = testHarnessResourceLoader;
    }

}