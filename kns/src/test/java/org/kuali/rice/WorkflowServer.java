package org.kuali.rice;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class WorkflowServer extends BaseTestServer {

	private int port;
	private String contextName;
	
	public WorkflowServer() {}
	
	public WorkflowServer(int port, String contextName) {
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
		if (contextName == null) {
			return "/en-test";
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
