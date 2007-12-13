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
package edu.iu.uis.eden.core;

import javax.xml.namespace.QName;

import org.kuali.rice.core.Core;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.BaseWrappingResourceLoader;
import org.kuali.rice.resourceloader.ServiceLocator;

import edu.iu.uis.eden.plugin.Plugin;
import edu.iu.uis.eden.plugin.PluginRegistry;

/**
 * A resource loader which is responsible for loading resources from the Workflow Core.  It is responsible for
 * searching for service overrides in the Institutional Plugin before falling back to default services
 * from the core.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class CoreResourceLoader extends BaseWrappingResourceLoader {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(CoreResourceLoader.class);

	public static final QName NAME = new QName(Core.getCurrentContextConfig().getMessageEntity(), "KEW_SPRING+PLUGIN_REGISTRY_CONTAINER_RESOURCE_LOADER");

	private final PluginRegistry registry;

	public CoreResourceLoader(ServiceLocator serviceLocator, PluginRegistry registry) {
		super(CoreResourceLoader.NAME, serviceLocator);
		this.registry = registry;
	}

	/**
	 * Overrides the standard getService method to first look in the institutional plugin for the
	 * service with the given name and then fall back to the default implementation in the
	 * core (if it exists).
	 */
	public Object getService(QName serviceName) {
		if (getRegistry() != null) {
			Plugin institutionalPlugin = getRegistry().getInstitutionalPlugin();
			if (institutionalPlugin != null) {
				Object service = institutionalPlugin.getService(serviceName);
				if (service != null) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Retrieved service override for '" + serviceName + "' from the institutional plugin.");
					}
					return postProcessService(serviceName, service);
				}
			}
		}
		Object service = super.getService(serviceName);
		if (service == null && getRegistry() != null) {
		    service = getRegistry().getService(serviceName);
		}
		return service;
	}



	@Override
	public Object getObject(ObjectDefinition objectDefinition) {
	    Object object = super.getObject(objectDefinition);
	    if (object == null && getRegistry() != null) {
		object = getRegistry().getObject(objectDefinition);
	    }
	    return object;
	}

	@Override
	protected boolean shouldWrapService(QName serviceName, Object service) {
		// transaction template is not wrappable because it does not implement an interface
		if (serviceName.getLocalPart().equals("transactionTemplate")) {
			return false;
		}
		return super.shouldWrapService(serviceName, service);
	}

	public PluginRegistry getRegistry() {
		return registry;
	}
}