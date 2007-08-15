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
package edu.iu.uis.eden.server;

import org.apache.log4j.Logger;
import org.kuali.rice.core.Core;
import org.kuali.rice.test.BaseTestServer;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class TestClient1 extends BaseTestServer {
	
	private static final Logger LOG = Logger.getLogger(TestClient1.class);

	private static final String CONTEXT = "/TestClient1";
	
	@Override
	protected Server createServer() {
		Server server = new Server(new Integer(Core.getCurrentContextConfig().getProperty("ksb.client1.port")));
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