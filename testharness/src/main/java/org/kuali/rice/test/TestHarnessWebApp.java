package org.kuali.rice.test;


import org.apache.commons.lang.StringUtils;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;
import org.kuali.rice.web.jetty.JettyServer;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class TestHarnessWebApp extends JettyServer {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TestHarnessWebApp.class);

    private int port;
    private String webAppRoot;
    private String contextName;

    public TestHarnessWebApp() {
        this(9912, null);
    }

    public TestHarnessWebApp(int port, String contextName) {
        super(port, contextName);
    }

    protected Server createServer() {
    	Config config = Core.getCurrentContextConfig();
    	this.webAppRoot = config.getProperty("test.web.app.root");
    	this.contextName = config.getProperty("test.context.name");
    	if (StringUtils.isBlank(this.webAppRoot) || StringUtils.isBlank(this.contextName)) {
    		LOG.warn("Could not locate both the 'test.web.app.root' and 'test.context.name' configuration parameters.  Not starting embedded Jetty server.");
    		return null;
    	}
        Server server = new Server(getPort());
        WebAppContext context = new WebAppContext(getWebAppRoot(), getContextName());
        server.addHandler(context);
        return server;
    }

    public String getWebAppRoot() {
    	return this.webAppRoot;
    }

    public String getContextName() {
        return this.contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public int getPort() {
        if (this.port == 0) {
            return 9912;
        }
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
