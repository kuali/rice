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
package org.kuali.rice.web.jetty;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import edu.iu.uis.eden.core.BaseLifecycle;
import edu.iu.uis.eden.core.Lifecycle;

public class JettyServer implements Lifecycle {
    
    private int port;
    private String contextName;
    private Server server;
    
    public JettyServer() {
        this(8080, null);
    }
    
    public JettyServer(int port, String contextName) {
        this.port = port;
        this.contextName = contextName;
    }
    
    public Server getServer() {
        return server;
    }
    
    public void start() throws Exception {
        server = createServer();
        server.start();
    }
    
    public void stop() throws Exception {
        server.stop();
    }
    
    public boolean isStarted() {
        return server.isStarted();
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
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        try {
            new JettyServer(8080, null).start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
