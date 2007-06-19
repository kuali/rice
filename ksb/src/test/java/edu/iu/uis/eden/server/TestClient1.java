package edu.iu.uis.eden.server;

import org.apache.log4j.Logger;
import org.kuali.rice.core.Core;
import org.kuali.rice.test.BaseTestServer;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class TestClient1 extends BaseTestServer {
	
	private static final Logger LOG = Logger.getLogger(TestClient1.class);

	private static final int PORT = 9910;
	private static final String CONTEXT = "/TestClient1";
	
	@Override
	protected Server createServer() {
		Server server = new Server(PORT);
		String location = Core.getCurrentContextConfig().getProperty("client1.location");
		LOG.debug("#####################################");
		LOG.debug("#");
		LOG.debug("#  Starting Client1 using location " + location);
		LOG.debug("#");
		LOG.debug("#####################################");
		WebAppContext context = new WebAppContext(location, CONTEXT);	
		server.addHandler(context);
		return server;
	}
}