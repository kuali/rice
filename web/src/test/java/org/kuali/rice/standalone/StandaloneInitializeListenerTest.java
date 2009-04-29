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
package org.kuali.rice.standalone;

import java.net.URL;
import java.net.URLClassLoader;

import javax.servlet.ServletContextEvent;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.resourceloader.ContextClassLoaderBinder;
import org.kuali.rice.core.util.JSTLConstants;
import org.kuali.rice.core.web.listener.StandaloneInitializeListener;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.test.BaseRiceTestCase;
import org.springframework.mock.web.MockServletContext;


/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class StandaloneInitializeListenerTest extends BaseRiceTestCase {

    private static final String CONTEXT_NAME = "rice-standalone-version";
    private static final String TEST_INIT_PARAM = "test.init.param";
    private static final String TEST_INIT_PARAM_VAL = "test.init.param.val";
    
    @Test public void testConfigInitialization() {
        // switch to a different context classloader context so that we don't blow away our existing configuration
        ContextClassLoaderBinder binder = new ContextClassLoaderBinder();
        binder.bind(new URLClassLoader(new URL[0]));
        try{
            MockServletContext mockServletContext = new MockServletContext();
            mockServletContext.setServletContextName(CONTEXT_NAME);
            mockServletContext.addInitParameter(KEWConstants.BOOTSTRAP_SPRING_FILE, "org/kuali/rice/standalone/TestStandaloneInitializeListener.xml");
            mockServletContext.addInitParameter(TEST_INIT_PARAM, TEST_INIT_PARAM_VAL);
            mockServletContext.addInitParameter(StandaloneInitializeListener.RICE_STANDALONE_EXECUTE_MESSAGE_FETCHER, "false");
            
            StandaloneInitializeListener listener = new StandaloneInitializeListener();
            ServletContextEvent sce = new ServletContextEvent(mockServletContext);
        
            // initialize the context
            listener.contextInitialized(sce);
        
            assertNotNull("A Spring Context should exist.", listener.getContext());
            assertTrue("Context should be active.", listener.getContext().isActive());
            assertTrue("Context should be running.", listener.getContext().isRunning());
            
            String riceBase = ConfigContext.getCurrentContextConfig().getProperty("rice.base");
            assertFalse("rice base should exist.", StringUtils.isBlank(riceBase));
            // test that the init params are pumped into the config system
            String testInitParam = ConfigContext.getCurrentContextConfig().getProperty(TEST_INIT_PARAM);
            assertEquals(TEST_INIT_PARAM_VAL, testInitParam);
        
            assertTrue(mockServletContext.getAttribute("Constants") instanceof JSTLConstants);
        
            // now destroy the context
            listener.contextDestroyed(sce);
        
            assertFalse("Context should no longer be active.", listener.getContext().isActive());
            
        } finally {
            binder.unbind();
        }
    }
    
}
