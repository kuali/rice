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
package edu.iu.uis.eden.plugin;

import javax.xml.namespace.QName;

import org.kuali.rice.core.Core;
import org.kuali.rice.definition.ObjectDefinition;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;


public class TestPluginRegistry extends BasePluginRegistry implements PluginRegistry {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(TestPluginRegistry.class);

	private boolean embeddedInitialized = false;
	private String currentClientProtocol;
	private PluginRegistry embeddedPluginRegistry = new NullPluginRegistry();
	private PluginRegistry actualPluginRegistry;

	public Plugin getInstitutionalPlugin() {
		updatePluginRegistry();
		return actualPluginRegistry.getInstitutionalPlugin();
	}

	public Object getObject(ObjectDefinition definition) {
		updatePluginRegistry();
		return super.getObject(definition);
	}

	public PluginEnvironment getPluginEnvironment(QName pluginName) {
		updatePluginRegistry();
		return actualPluginRegistry.getPluginEnvironment(pluginName);
	}

	public Object getService(QName serviceName) {
		updatePluginRegistry();
		return super.getService(serviceName);
	}

	public void start() throws Exception {
		updatePluginRegistry();
		// we don't want to call super.start() because the updatePluginRegistry is going to start our resource loaders
		//super.start();
	}

	public void stop() throws Exception {
		if (actualPluginRegistry != null) {
			actualPluginRegistry.stop();
		}
		if (embeddedPluginRegistry != null) {
			embeddedPluginRegistry.stop();
		}
		super.stop();
	}

	protected synchronized void updatePluginRegistry() {
		try {
			// it's important to always check the root config for this since our config system utilizes a ghetto hierarchy.  This means
			// that when I manually change the client protocol it only happens on the root config and doesn't get propogated to
			// it's "children" which still contain the original client protocol.  This could cause some other subtle issues to
			// crop up in the test harness...let's hope not ;)
			String clientProtocol = Core.getRootConfig().getClientProtocol();
			if (EdenConstants.EMBEDDED_CLIENT_PROTOCOL.equals(clientProtocol) && !embeddedInitialized) {
				LOG.info("The bootstrap EMBEDDED plugin registry has not yet been started, starting...");
				actualPluginRegistry = new NullPluginRegistry();
				removeAllResourceLoaders();
				addResourceLoader(actualPluginRegistry);
				embeddedInitialized = true;
				currentClientProtocol = EdenConstants.EMBEDDED_CLIENT_PROTOCOL;
				embeddedPluginRegistry = new EmbeddedPluginRegistry();
				addResourceLoader(embeddedPluginRegistry);
				embeddedPluginRegistry.start();
				LOG.info("... started the bootstrap EMBEDDED plugin registry.");
			} else if (currentClientProtocol != null && !currentClientProtocol.equals(clientProtocol)) {
				LOG.info("Client protocol changed from '" + currentClientProtocol + "' to '" + clientProtocol +"', stopping current plugin registry.");
				actualPluginRegistry.stop();
				LOG.info("Current plugin registry was stopped.");
				currentClientProtocol = clientProtocol;
				if (currentClientProtocol.equals(EdenConstants.LOCAL_CLIENT_PROTOCOL) || currentClientProtocol.equals(EdenConstants.EMBEDDED_CLIENT_PROTOCOL)) {
					actualPluginRegistry = new NullPluginRegistry();
				} else {
					actualPluginRegistry = new PluginRegistryFactory().createPluginRegistry();
				}
				LOG.info("Initialized new plugin registry for client protocol '" + currentClientProtocol +"', starting...");
				removeAllResourceLoaders();
				addResourceLoader(actualPluginRegistry);
				addResourceLoader(embeddedPluginRegistry);
				actualPluginRegistry.start();
				LOG.info("...new plugin registry started.");
			}
		} catch (Exception e) {
			throw new WorkflowRuntimeException("Failed to start plugin registry for client protocol: " + currentClientProtocol, e);
		}
	}

	private class NullPluginRegistry extends BasePluginRegistry {}

}
