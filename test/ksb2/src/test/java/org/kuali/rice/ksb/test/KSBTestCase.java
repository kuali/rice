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
package org.kuali.rice.ksb.test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.ojb.BaseOjbConfigurer;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.core.resourceloader.SpringResourceLoader;
import org.kuali.rice.ksb.messaging.bam.BAMTargetEntry;
import org.kuali.rice.ksb.messaging.bam.service.BAMService;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.kuali.rice.test.RiceTestCase;


public abstract class KSBTestCase extends RiceTestCase {
    
    private ResourceLoader springContextResourceLoader;

    @Override
    public void setUp() throws Exception {
        // because we're stopping and starting so many times we need to clear
        // the core before another set of RLs get put in the core. This is 
        // because we are sometimes using the GRL to fetch a specific servers
        // spring file out for testing purposes.
        ConfigContext.destroy();
        
		// Turn off http keep-alive. Repeated jetty start/stop using same port
		// results sockets held by client not to close properly, resulting in
        // cxf test failures.
		System.setProperty("http.keepAlive", "false");

		setClearTables(false);
		
        super.setUp();
    }

    @Override
    protected String getModuleName() {
        return "ksb";
    }
    
    protected String getModuleTestConfigLocation() {
        return "classpath:org/kuali/rice/" + getModuleName().toLowerCase() + "/test/config/" + getModuleName().toLowerCase() + "-test-config.xml";
    }
    
    protected List<String> getPerTestTablesNotToClear() {
        return new ArrayList<String>();
    }

    @Override
    protected List<Lifecycle> getPerTestLifecycles() {
        List<Lifecycle> lifecycles = super.getSuiteLifecycles();
        if (this.disableJta()) {
            System.setProperty(BaseOjbConfigurer.OJB_PROPERTIES_PROP, "RiceNoJtaOJB.properties");
            this.springContextResourceLoader = new SpringResourceLoader(new QName("ksbtestharness"), "KSBTestHarnessNoJtaSpring.xml", null);
        } else {
            this.springContextResourceLoader = new SpringResourceLoader(new QName("ksbtestharness"), "KSBTestHarnessSpring.xml", null);
        }

        lifecycles.add(this.springContextResourceLoader);
        return lifecycles;
    }

    public static boolean verifyServiceCallsViaBam(QName serviceName, String methodName, boolean serverInvocation) throws Exception {
        BAMService bamService = KSBServiceLocator.getBAMService();
        List<BAMTargetEntry> bamCalls = null;
        if (methodName == null) {
            bamCalls = bamService.getCallsForService(serviceName);
        } else {
            bamCalls = bamService.getCallsForService(serviceName, methodName);
        }

        if (bamCalls.size() == 0) {
            return false;
        }
        for (BAMTargetEntry bamEntry : bamCalls) {
            if (bamEntry.getServerInvocation() && serverInvocation) {
                return true;
            } else if (!serverInvocation) {
                return true;
            }
        }
        return false;
    }

    public ResourceLoader getSpringContextResourceLoader() {
        return this.springContextResourceLoader;
    }

    public void setSpringContextResourceLoader(ResourceLoader testHarnessResourceLoader) {
        this.springContextResourceLoader = testHarnessResourceLoader;
    }

    protected boolean disableJta() {
        return false;
    }
}
