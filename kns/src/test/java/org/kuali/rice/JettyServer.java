package org.kuali.rice;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import edu.iu.uis.eden.core.Lifecycle;

public class JettyServer implements Lifecycle {

	private int port;

	private String contextName;

	private Server server;

	public JettyServer() {
		this(8080, null);
	}

	public JettyServer(int port) {
		this.port = port;
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
		try {
			WebAppContext context = new WebAppContext("sampleapp/src/main/webapp", getContextName());
			server.addHandler(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
