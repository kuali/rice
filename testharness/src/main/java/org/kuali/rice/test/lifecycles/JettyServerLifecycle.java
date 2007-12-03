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
package org.kuali.rice.test.lifecycles;

import java.util.Map;

import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.kuali.rice.web.jetty.JettyServer;
import org.mortbay.jetty.webapp.WebAppClassLoader;

/**
 * A lifecycle for running a jetty web server.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class JettyServerLifecycle implements Lifecycle {

	private boolean started;
	
	private JettyServer jettyServer;
		
	public JettyServerLifecycle() {
		this(8080, null);
	}

	public JettyServerLifecycle(int port) {
		this(port, null, null);
	}

	public JettyServerLifecycle(int port, String contextName) {
		this(port, contextName, null);
	}
	
	public JettyServerLifecycle(int port, String contextName, String relativeWebappRoot) {
		jettyServer = new JettyServer(port, contextName, relativeWebappRoot);
	}	
	
	public boolean isStarted() {
		return started;
	}

	public void start() throws Exception {
		jettyServer.start();
		Map<ClassLoader, Config> configs = Core.getCONFIGS();
		for (Map.Entry<ClassLoader, Config> configEntry : configs.entrySet()) {
			if (configEntry.getKey() instanceof WebAppClassLoader) {
				ResourceLoader rl = GlobalResourceLoader.getResourceLoader(configEntry.getKey());
				if (rl == null) {
					throw new RuntimeException("Could not find resource loader for workflow test harness web app for: " + configEntry.getKey());
				}
				GlobalResourceLoader.addResourceLoader(rl);
				configs.put(Thread.currentThread().getContextClassLoader(), configEntry.getValue());
			}
		}
		started = true;
	}

	public void stop() throws Exception {
		started = false;
	}
}
