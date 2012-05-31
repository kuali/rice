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
package org.kuali.rice.web;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.junit.Test;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.resourceloader.ContextClassLoaderBinder;
import org.kuali.rice.core.web.listener.BootstrapListener;
import org.kuali.rice.core.web.listener.StandaloneInitializeListener;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.web.test.ServerTestBase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;


/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */

public class BootstrapListenerTest extends ServerTestBase {

    private static final String CONTEXT_NAME = "rice-standalone-version";
    
    private static final String TEST_INIT_PARAM = "listener.logout.class";
    private static final String TEST_INIT_PARAM_VAL = "org.jasig.cas.client.session.SingleSignOutHttpSessionListener";
    private static final String TEST_INIT_PARAM_VAL_BAD = "org.jasig.cas.client.session.SingleSignOuHttpSessionListenerBadClassName";
    private static final String TEST_INIT_PARAM_VAL_BAD2 = "org.kuali.rice.ksb.server.KSBTestContextLoaderListener";

    private static final String TEST_INIT_PARAM_OTHER_LISTENER = "listener.donothing.class";
	
   @Test public void testAddListener() {
        // switch to a different context classloader context so that we don't blow away our existing configuration
        ContextClassLoaderBinder binder = new ContextClassLoaderBinder();
        binder.bind(new URLClassLoader(new URL[0]));
        try{
        	
            MockServletContext mockServletContext = setUpMockServletContext(TEST_INIT_PARAM, TEST_INIT_PARAM_VAL);

            StandaloneInitializeListener standaloneInitializeListener = new StandaloneInitializeListener();
            ServletContextEvent sce = new ServletContextEvent(mockServletContext);
            standaloneInitializeListener.contextInitialized(sce);

            BootstrapListener bootstrapListener = new BootstrapListener();
            HttpSessionEvent hse = new HttpSessionEvent(new MockHttpServletRequest().getSession(true));
            bootstrapListener.sessionCreated(hse);
            
            String testInitParam = ConfigContext.getCurrentContextConfig().getProperty(TEST_INIT_PARAM);
            assertEquals(TEST_INIT_PARAM_VAL, testInitParam);
            
            Map<String, HttpSessionListener> listeners = bootstrapListener.getListeners();
            assertTrue("listeners map size should be 1 since SingleSignOutHttpSessionListener was added as param", listeners.size() == 1);
            assertTrue("listeners map should contain a listener with a key of logout", listeners.containsKey("logout"));
        
            standaloneInitializeListener.contextDestroyed(sce);
            bootstrapListener.sessionDestroyed(hse);
            
        } finally {
            binder.unbind();
        }
    }   
        
    @Test public void testAddListenerBadClassName() {
        // switch to a different context classloader context so that we don't blow away our existing configuration
        ContextClassLoaderBinder binder = new ContextClassLoaderBinder();
        binder.bind(new URLClassLoader(new URL[0]));
        try{
            MockServletContext mockServletContext = setUpMockServletContext(TEST_INIT_PARAM, TEST_INIT_PARAM_VAL_BAD);
            
            StandaloneInitializeListener standaloneInitializeListener = new StandaloneInitializeListener();
            ServletContextEvent sce = new ServletContextEvent(mockServletContext);
            standaloneInitializeListener.contextInitialized(sce);

            BootstrapListener bootstrapListener = new BootstrapListener();
            HttpSessionEvent hse = new HttpSessionEvent(new MockHttpServletRequest().getSession(true));
            bootstrapListener.sessionCreated(hse);
            
            String testInitParam = ConfigContext.getCurrentContextConfig().getProperty(TEST_INIT_PARAM);
            assertEquals(TEST_INIT_PARAM_VAL_BAD, testInitParam);
            
            Map<String, HttpSessionListener> listeners = bootstrapListener.getListeners();
            assertTrue("listeners map size should be 0 since the class passed in as the param value was a non-existent class", listeners.size() == 0);
        
            standaloneInitializeListener.contextDestroyed(sce);
            bootstrapListener.sessionDestroyed(hse);
                    
        } finally {
            binder.unbind();
        }
    }  
    
    @Test public void testAddListenerClassIsNotImplementHttpSessionListener() {
        // switch to a different context classloader context so that we don't blow away our existing configuration
        ContextClassLoaderBinder binder = new ContextClassLoaderBinder();
        binder.bind(new URLClassLoader(new URL[0]));
        try{
            MockServletContext mockServletContext = setUpMockServletContext(TEST_INIT_PARAM, TEST_INIT_PARAM_VAL_BAD2);
            
            StandaloneInitializeListener standaloneInitializeListener = new StandaloneInitializeListener();
            ServletContextEvent sce = new ServletContextEvent(mockServletContext);
            standaloneInitializeListener.contextInitialized(sce);

            BootstrapListener bootstrapListener = new BootstrapListener();
            HttpSessionEvent hse = new HttpSessionEvent(new MockHttpServletRequest().getSession(true));
            bootstrapListener.sessionCreated(hse);
            
            String testInitParam = ConfigContext.getCurrentContextConfig().getProperty(TEST_INIT_PARAM);
            assertEquals(TEST_INIT_PARAM_VAL_BAD2, testInitParam);
            
            Map<String, HttpSessionListener> listeners = bootstrapListener.getListeners();
            assertTrue("listeners map size should be 0 since the class passed in does not implement HttpSessionListener", listeners.size() == 0);
        
            standaloneInitializeListener.contextDestroyed(sce);
            bootstrapListener.sessionDestroyed(hse);
                    
        } finally {
            binder.unbind();
        }
    } 
    
    @Test public void testAddDifferentListener() {
        // switch to a different context classloader context so that we don't blow away our existing configuration
        ContextClassLoaderBinder binder = new ContextClassLoaderBinder();
        binder.bind(new URLClassLoader(new URL[0]));
        try{
            MockServletContext mockServletContext = setUpMockServletContext(TEST_INIT_PARAM_OTHER_LISTENER, TEST_INIT_PARAM_VAL);
            
            StandaloneInitializeListener standaloneInitializeListener = new StandaloneInitializeListener();
            ServletContextEvent sce = new ServletContextEvent(mockServletContext);
            standaloneInitializeListener.contextInitialized(sce);

            BootstrapListener bootstrapListener = new BootstrapListener();
            HttpSessionEvent hse = new HttpSessionEvent(new MockHttpServletRequest().getSession(true));
            bootstrapListener.sessionCreated(hse);
            
            String testInitParam = ConfigContext.getCurrentContextConfig().getProperty(TEST_INIT_PARAM_OTHER_LISTENER);
            assertEquals(TEST_INIT_PARAM_VAL, testInitParam);
            
            Map<String, HttpSessionListener> listeners = bootstrapListener.getListeners();
            assertTrue("listeners map size should be 1, but it is not a listener.logout.class param", listeners.size() == 1);
            assertTrue("listeners map should contain a listener with a key of donothing", listeners.containsKey("donothing"));
            
            standaloneInitializeListener.contextDestroyed(sce);
            bootstrapListener.sessionDestroyed(hse);
                    
        } finally {
            binder.unbind();
        }
    } 
    
    private MockServletContext setUpMockServletContext(String testParam, String testParmValue) {
        MockServletContext mockServletContext = new MockServletContext();
        mockServletContext.setServletContextName(CONTEXT_NAME);
        mockServletContext.addInitParameter(KEWConstants.BOOTSTRAP_SPRING_FILE, "org/kuali/rice/standalone/TestStandaloneInitializeListener.xml");
        mockServletContext.addInitParameter(StandaloneInitializeListener.RICE_STANDALONE_EXECUTE_MESSAGE_FETCHER, "false");
        mockServletContext.addInitParameter(testParam, testParmValue);
        
        return mockServletContext;
    }
}