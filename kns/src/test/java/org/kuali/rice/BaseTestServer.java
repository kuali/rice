package org.kuali.rice;

import org.kuali.rice.lifecycle.Lifecycle;
import org.mortbay.jetty.Server;

public abstract class BaseTestServer implements Lifecycle {

	private Server server;
	
	protected abstract Server createServer();
	
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

}
