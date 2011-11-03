/**
 * Copyright 2005-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.test.launch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.Servlet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyLauncher {
	
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(JettyLauncher.class);
	
    /**
     * The name of an attribute we set in the ServletContext to indicate to the webapp
     * that it is running within unit tests, in case it needs to alter its configuration
     * or behavior.
     */
    public static final String JETTYSERVER_TESTMODE_ATTRIB = "JETTYSERVER_TESTMODE";

	private int port;
	private String contextName;	
	private List<String> relativeWebappRoots = new ArrayList<String>();
	private Class<? extends Servlet> servletClass;
	private Server server;
	private ServletContextHandler context;
	private boolean failOnContextFailure;

	/**
	 * Whether we are in test mode
	 */
	private boolean testMode = false;

	public JettyLauncher() {
		this(8080);
	}

	public JettyLauncher(int port) {
		this(port, null, null, null);
	}

	public JettyLauncher(int port, String contextName) {
		this(port, contextName, null, null);
	}
	
	public JettyLauncher(int port, String contextName, String relativeWebappRoot) {
        this(port, contextName, relativeWebappRoot, null);
	}	

    public JettyLauncher(int port, String contextName, Class<? extends Servlet> servletClass) {
        this(port, contextName, null, servletClass);
    }   

    public JettyLauncher(int port, String contextName, String relativeWebappRoots, Class<? extends Servlet> servletClass) {
        this.port = port;
        this.contextName = contextName;
        StringTokenizer tokenizer = new StringTokenizer(relativeWebappRoots, ",");
        while (tokenizer.hasMoreTokens()) {
            String relativeWebappRoot = tokenizer.nextToken();
            this.relativeWebappRoots.add(relativeWebappRoot);
        }
        this.servletClass = servletClass;
    }   

    public void setTestMode(boolean t) {
	    this.testMode = t;
	}

	public boolean isTestMode() {
	    return testMode;
	}

	public Server getServer() {
		return server;
	}

	public ServletContextHandler getContext() {
	    return context;
	}

	public void start() throws Exception {
		server = createServer();
		server.start();
		if (isFailOnContextFailure() && contextStartupFailed()) {
			try {
				server.stop();
			} catch (Exception e) {
				LOG.warn("Failed to stop server after web application startup failure.");
			}
			throw new Exception("Failed to startup web application context!  Check logs for specific error.");
		}
	}

	public void stop() throws Exception {
		server.stop(); 
	}

	public boolean isStarted() {
		return server.isStarted();
	}

	protected Server createServer() {
		Server server = new Server(getPort());
		setBaseDirSystemProperty();
		if (useWebAppContext()) {
			File tmpDir = new File(System.getProperty("basedir") + "/target/jetty-tmp");
			tmpDir.mkdirs();
			WebAppContext webAppContext = new WebAppContext();
			webAppContext.setContextPath(getContextName());
			String[] fullRelativeWebappRoots = new String[this.relativeWebappRoots.size()];
			for (int i = 0; i < this.relativeWebappRoots.size(); i++) {
				String fullRelativeWebappRoot = this.relativeWebappRoots.get(i);
				fullRelativeWebappRoots[i] = System.getProperty("basedir") + fullRelativeWebappRoot;
				if (LOG.isInfoEnabled()) {
					LOG.info("WebAppRoot = " + fullRelativeWebappRoots[i]);
				}
			}
			webAppContext.setBaseResource(new ResourceCollection(fullRelativeWebappRoots));
			webAppContext.setTempDirectory(tmpDir);
			webAppContext.setAttribute(JETTYSERVER_TESTMODE_ATTRIB, String.valueOf(isTestMode()));
			context = webAppContext;
			server.setHandler(context);
		} else {
			ServletContextHandler root = new ServletContextHandler(server,"/",ServletContextHandler.SESSIONS);
			root.addServlet(new ServletHolder(servletClass), getContextName());
			root.setAttribute(JETTYSERVER_TESTMODE_ATTRIB, String.valueOf(isTestMode()));
			context = root;
		}
		return server;
	}

	protected void setBaseDirSystemProperty() {
		if (System.getProperty("basedir") == null) {
			System.setProperty("basedir", System.getProperty("user.dir"));
		}
	}
	
	private boolean useWebAppContext() {
		return CollectionUtils.isNotEmpty(this.relativeWebappRoots);
	}

	protected boolean contextStartupFailed() throws Exception {
        return !context.isAvailable();
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
	
	
	public boolean isFailOnContextFailure() {
		return this.failOnContextFailure;
	}

	public void setFailOnContextFailure(boolean failOnContextFailure) {
		this.failOnContextFailure = failOnContextFailure;
	}

	public String toString() {
	    return new ToStringBuilder(this).append("port", port)
	                                    .append("contextName", contextName)
	                                    .append("relativeWebappRoots", relativeWebappRoots)
                                        .append("servletClass", servletClass)
	                                    .toString();
	}

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        String contextName = args.length > 1 ? args[1] : null;
        String relativeWebappRoot = args.length > 2 ? args[2] : null;
        try {
            new JettyLauncher(port, contextName, relativeWebappRoot).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
