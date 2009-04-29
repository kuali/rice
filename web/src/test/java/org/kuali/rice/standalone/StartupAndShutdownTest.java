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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.web.listener.StandaloneInitializeListener;
import org.kuali.rice.test.BaseRiceTestCase;
import org.springframework.mock.web.MockServletContext;

/**
 * Tests starting up and standalone Rice instance, shutting it down, and then restarting it.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Ignore
public class StartupAndShutdownTest extends BaseRiceTestCase {

    @Test public void testStartupAndShutdown() throws Exception {
        ServletContext context = createContext();
        StandaloneInitializeListener listener = new StandaloneInitializeListener();
        ServletContextEvent sce = new ServletContextEvent(context);
        
        // initialize the context
        listener.contextInitialized(sce);
        
        assertNotNull("A Spring Context should exist.", listener.getContext());
        assertTrue("Context should be active.", listener.getContext().isActive());
        assertTrue("Context should be running.", listener.getContext().isRunning());
        
        // now destroy the context
        listener.contextDestroyed(sce);
        
        assertFalse("Context should no longer be active.", listener.getContext().isActive());
        
        context = createContext();
        listener = new StandaloneInitializeListener();
        sce = new ServletContextEvent(context);
        
        // initialize the context
        listener.contextInitialized(sce);
        listener.contextDestroyed(sce);
    }
    
    protected ServletContext createContext() {
        MockServletContext mockServletContext = new MockServletContext();
        mockServletContext.setServletContextName("test");
        mockServletContext.addInitParameter(StandaloneInitializeListener.RICE_STANDALONE_EXECUTE_MESSAGE_FETCHER, "false");
        mockServletContext.addInitParameter("environment", "dev");
        mockServletContext.addInitParameter("mailing.list.batch", "localhost");
        mockServletContext.addInitParameter("mail.relay.server", "localhost");
        mockServletContext.addInitParameter("encryption.key", "7IC64w6ksLU");
        return mockServletContext;
    }
    
}
