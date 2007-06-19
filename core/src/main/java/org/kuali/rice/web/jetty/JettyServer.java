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

import java.io.File;

import org.kuali.rice.lifecycle.Lifecycle;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class JettyServer implements Lifecycle {

	private int port;
	private String contextName;	
	private String relativeWebappRoot;
	private Server server;

	public JettyServer() {
		this(8080, null);
	}

	public JettyServer(int port) {
		this(port, null, null);
	}

	public JettyServer(int port, String contextName) {
		this(port, contextName, null);
	}
	
	public JettyServer(int port, String contextName, String relativeWebappRoot) {
		this.port = port;
		this.contextName = contextName;
		this.relativeWebappRoot = relativeWebappRoot;
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
			setBaseDirSystemProperty();
			WebAppContext context = new WebAppContext(System.getProperty("basedir") + getRelativeWebappRoot(), getContextName());
			context.setTempDirectory(new File(System.getProperty("basedir") + "/jetty-tmp"));
			server.addHandler(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return server;
	}

	protected void setBaseDirSystemProperty() {
		if (System.getProperty("basedir") == null) {
			System.setProperty("basedir", System.getProperty("user.dir"));
		}
	}
	
	public String getRelativeWebappRoot() {
		if (relativeWebappRoot == null) {
			return "/sampleapp/web-root";
		}
		return relativeWebappRoot;
	}

	public void setRelativeWebappRoot(String relativeWebappRoot) {
		this.relativeWebappRoot = relativeWebappRoot;
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
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
		String contextName = args.length > 1 ? args[1] : null;
		String relativeWebappRoot = args.length > 2 ? args[2] : null;
		try {
			new JettyServer(port, contextName, relativeWebappRoot).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
