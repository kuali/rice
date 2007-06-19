package org.kuali.rice.test;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Creates a Jetty Server to test Workflow.
 * 
 * @author
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:13 $
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
