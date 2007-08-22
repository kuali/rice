/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.test;

import org.kuali.rice.lifecycle.Lifecycle;
import org.mortbay.jetty.Server;

public class JettyLifecycle implements Lifecycle {

	private Server server;

	public boolean isStarted() {
		return server.isStarted();
	}

	public void start() throws Exception {
//		server = new Server(new Integer(Core.getCurrentContextConfig().getProperty("http.service.port")));
//		Context root = new Context(server,"/en-test",Context.SESSIONS);
//	    root.addServlet(new ServletHolder(new KSBDispatcherServlet()), "/remoting/*");
	    //server.start();
	}

	public void stop() throws Exception {
		server.stop();
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

}
