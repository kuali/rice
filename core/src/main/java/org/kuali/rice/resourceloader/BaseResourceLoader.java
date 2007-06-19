/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.rice.resourceloader;

import javax.xml.namespace.QName;

import org.kuali.rice.core.Core;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.util.ClassLoaderUtils;

/**
 * A simple ResourceLoader implementation which will load objects from the
 * specified classloader and also locate services in an optional ServiceLocator.
 *
 * @author Eric Westfall
 */
public class BaseResourceLoader extends ResourceLoaderContainer implements ResourceLoader {

	static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BaseResourceLoader.class);

	private ServiceLocator serviceLocator;

	private ClassLoader classLoader;

	private boolean postProcessContainer = true;

	public BaseResourceLoader(QName name, ClassLoader classLoader) {
		this(name, classLoader, null);
	}

	public BaseResourceLoader(QName name) {
		this(name, ClassLoaderUtils.getDefaultClassLoader());
	}

	public BaseResourceLoader(QName name, ServiceLocator serviceLocator) {
		this(name, ClassLoaderUtils.getDefaultClassLoader(), serviceLocator);
	}

	public BaseResourceLoader(QName name, ClassLoader classLoader, ServiceLocator serviceLocator) {
		super(name);
		this.classLoader = classLoader;
		this.serviceLocator = serviceLocator;
	}

	public Object getObject(ObjectDefinition objectDefinition) {
		// if this resource locator has no NameSpaceURI(M.E.) or the
		// objectDefinition has no M.E. just try to find the class here
		// or if the M.E. of the object is the same as the M.E. of the locator
		if (getName().getNamespaceURI() == null || getName().getNamespaceURI().equals(objectDefinition.getMessageEntity()) ||
				objectDefinition.getMessageEntity() == null ||
				// TODO did we really want to check for the KEW_MESSAGING_ENTITY here???
				//(EdenConstants.KEW_MESSAGING_ENTITY.equals(objectDefinition.getMessageEntity()) && Core.getCurrentContextConfig().getRunningEmbeddedServerMode())) {
				Core.getCurrentContextConfig().getRunningEmbeddedServerMode()) {
			Object object = ObjectDefinitionResolver.createObject(objectDefinition, this.classLoader, true);
			if (object != null) {
				return postProcessObject(objectDefinition, object);
			}
		}
		Object superObject = super.getObject(objectDefinition);
		return (isPostProcessContainer() ? postProcessObject(objectDefinition, superObject) : superObject);
	}

	public Object getService(QName serviceName) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("ResourceLoader " + getName() + " fetching service " + serviceName + getMemStatus());
		}
		if (this.serviceLocator != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Using internal service locator to fetch service " + serviceName);
			}
			Object service = this.serviceLocator.getService(serviceName);
			if (service != null) {
				return postProcessService(serviceName, service);
			}
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("ResourceLoader " + getName() + " didn't find service differing to child resource loaders ");
		}
		Object superService = super.getService(serviceName);
		return (isPostProcessContainer() ? postProcessService(serviceName, superService) : superService);
	}

	public void start() throws Exception {
		if (this.classLoader instanceof Lifecycle) {
			((Lifecycle)this.classLoader).start();
		}
		if (this.serviceLocator != null) {
			LOG.info("Starting ResourceLoader " + this.getName());
			this.serviceLocator.start();
		}
		super.start();
	}

	public void stop() throws Exception {
		super.stop();
		if (this.serviceLocator != null) {
			LOG.info("Stopping ResourceLoader " + this.getName());
			this.serviceLocator.stop();
		}
		if (this.classLoader instanceof Lifecycle) {
			((Lifecycle) this.classLoader).stop();
		}
		this.classLoader = null;
		this.serviceLocator = null;
	}

	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	protected Object postProcessObject(ObjectDefinition definition, Object object) {
		return object;
	}

	protected Object postProcessService(QName serviceName, Object service) {
		return service;
	}

	public boolean isPostProcessContainer() {
		return postProcessContainer;
	}

	public void setPostProcessContainer(boolean postProcessContainer) {
		this.postProcessContainer = postProcessContainer;
	}

	/**
	 * @deprecated use {@link #postProcessService(QName, Object)} instead
	 */
	protected Object wrap(QName serviceName, Object service) {
		return postProcessService(serviceName, service);
	}

	public String getContents(String indent, boolean servicePerLine) {
		String contents = indent + this + "\n";

		if (this.serviceLocator != null) {
			contents += this.serviceLocator.getContents(indent + "+++", servicePerLine);
		}

		for (ResourceLoader resourceLoader : this.getResourceLoaders()) {
			contents += resourceLoader.getContents(indent + "+++", servicePerLine);
		}

		return contents;
	}

	private String getMemStatus() {
		return "\n############################################################## \n" + "# " + dumpMemory() + "\n##############################################################\n";
	}

	private String dumpMemory() {
		long total = Runtime.getRuntime().totalMemory() / 1024;
		long free = Runtime.getRuntime().freeMemory() / 1024;
		long max = Runtime.getRuntime().maxMemory() / 1024;
		return "[Memory] max: " + max + "K, total: " + total + "K, free: " + free + "K, used: " + (total - free) + "K";
	}

	public ServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}
}