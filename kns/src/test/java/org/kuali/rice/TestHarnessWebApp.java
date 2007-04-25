/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice;

import org.kuali.rice.web.jetty.JettyServer;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import edu.iu.uis.eden.core.BaseLifecycle;

public class TestHarnessWebApp extends JettyServer {

    private int port;
    private String contextName;
    
    public TestHarnessWebApp() {
        this(9912, null);
    }
    
    public TestHarnessWebApp(int port, String contextName) {
        super(port, contextName);
    }
    
    protected Server createServer() {
        Server server = new Server(getPort());
        WebAppContext context = new WebAppContext("sampleapp/web-root", getContextName());
        server.addHandler(context);
        return server;
    }

    public String getContextName() {
        if (contextName == null) {
            return "/SampleRiceClient";
        }
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public int getPort() {
        if (port == 0) {
            return 9912;
        }
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
}
