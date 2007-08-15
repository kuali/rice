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
package org.kuali.rice.test;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Creates a Jetty Server to test Workflow.
 * 
 * @author
 * @version $Revision: 1.3 $ $Date: 2007-08-15 15:49:48 $
 * @since 0.9
 */
public class WorkflowServer extends BaseTestServer {

    private int port;

    private String contextName;

    public WorkflowServer() {
        // nothing to do
    }

    public WorkflowServer(final int port, final String contextName) {
        this.port = port;
        this.contextName = contextName;
    }

    protected Server createServer() {
        Server server = new Server(getPort());
        WebAppContext context = new WebAppContext("en", getContextName());
        server.addHandler(context);
        return server;
    }

    public String getContextName() {
        if (this.contextName == null) {
            return "/en-test";
        }
        return this.contextName;
    }

    public void setContextName(final String contextName) {
        this.contextName = contextName;
    }

    public int getPort() {
        if (this.port == 0) {
            return 9912;
        }
        return this.port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

}
