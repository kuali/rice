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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.Core;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.exceptions.RiceRuntimeException;
import org.kuali.rice.util.ClassLoaderUtils;

/**
 * Wrapper on all the Resource loaders.  This is what programmers typically use to get in the resource loading
 * framework.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class GlobalResourceLoader {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GlobalResourceLoader.class);

	private static Map<ClassLoader, ResourceLoader> rootResourceLoaders = new HashMap<ClassLoader, ResourceLoader>();

	private static boolean initializing;

	public static ResourceLoader getResourceLoader() {
		ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
		return getResourceLoaderCheckParent(classLoader);
	}

	private static ResourceLoader getResourceLoaderCheckParent(ClassLoader classLoader) {
	    ResourceLoader resourceLoader = getResourceLoader(classLoader);
	    if (resourceLoader != null && classLoader.getParent() != null) {
		ResourceLoader parentResourceLoader = getResourceLoaderCheckParent(classLoader.getParent());
		if (parentResourceLoader != null) {
		    resourceLoader = new ParentChildResourceLoader(parentResourceLoader, resourceLoader);
		}
	    }
	    if (resourceLoader == null && classLoader.getParent() != null) {
		resourceLoader = getResourceLoaderCheckParent(classLoader.getParent());
	    }
	    return resourceLoader;
	}

	public static ResourceLoader getResourceLoader(ClassLoader classloader) {
		return rootResourceLoaders.get(classloader);
	}

//	public static ResourceLoader getResourceLoader() {
//		ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
//		ResourceLoader resourceLoader = getResourceLoader(classLoader);
//		if (resourceLoader == null && classLoader.getParent() != null) {
//			resourceLoader = getResourceLoader(classLoader.getParent());
//		}
//		return resourceLoader;
//	}

	public synchronized static void start() throws Exception {
		try {
			initializing = true;
			ResourceLoader internalResourceLoader = getResourceLoader();
			if (internalResourceLoader == null) {
				throw new RiceRuntimeException("Cannot start GlobalResourceLoader because no resource loaders have been added for the current ContextClassLoader :" + Thread.currentThread().getContextClassLoader());
			}
			internalResourceLoader.start();
		} finally {
			initializing = false;
		}
	}

	public synchronized static void addResourceLoader(ResourceLoader resourceLoader) {
		initialize();
		LOG.info("Adding ResourceLoader " + resourceLoader.getName() + " to GlobalResourceLoader");
		if (resourceLoader == null) {
			throw new ResourceLoaderException("Attempted to add a null resource loader to the Global resource loader.");
		}
		getResourceLoader().addResourceLoader(resourceLoader);
	}

	public synchronized static void addResourceLoaderFirst(ResourceLoader resourceLoader) {
		initialize();
		LOG.info("Adding ResourceLoader " + resourceLoader.getName() + " to GlobalResourceLoader");
		if (resourceLoader == null) {
			throw new ResourceLoaderException("Attempted to add a null resource loader to the Global resource loader.");
		}
		getResourceLoader().addResourceLoaderFirst(resourceLoader);
	}

	protected static void initialize() {
		if (getResourceLoader(ClassLoaderUtils.getDefaultClassLoader()) == null) {
			LOG.info("Creating CompositeResourceLoader in GlobalResourceLoader");
			rootResourceLoaders.put(ClassLoaderUtils.getDefaultClassLoader(), new ResourceLoaderContainer(new QName(Core.getCurrentContextConfig().getMessageEntity(), ResourceLoader.ROOT_RESOURCE_LOADER_NAME)));
		}
	}

	public static ResourceLoader getResourceLoader(QName name) {
		return getResourceLoader().getResourceLoader(name);
	}

	/**
	 * Stop the resource loader for the current context classloader.  Don't stop or clear them all
	 * because the stop was issued from the context of a single classloader.
	 *
	 * @throws Exception
	 */
	public static void stop() throws Exception {
		LOG.debug("Destroy called on GlobalResourceLoader");
		if (getResourceLoader() != null) {
			LOG.info("Destroying GlobalResourceLoader");
			getResourceLoader().stop();
			rootResourceLoaders.remove(ClassLoaderUtils.getDefaultClassLoader());
		}
	}

	public static Object getService(QName serviceName) {
		if (serviceName == null) {
			throw new IllegalArgumentException("The service name must be non-null.");
		}
		LOG.debug("GlobalResourceLoader fetching service " + serviceName);
		return getResourceLoader().getService(serviceName);
	}

	public static Object getService(String localServiceName) {
		if (StringUtils.isEmpty(localServiceName)) {
			throw new IllegalArgumentException("The service name must be non-null.");
		}
		return getService(new QName(localServiceName));
	}

	public static Object getObject(ObjectDefinition objectDefinition) {
		return getResourceLoader().getObject(objectDefinition);
	}

	public static boolean isInitialized() {
		return getResourceLoader() != null;
	}

	public static void logContents() {
		if (LOG.isInfoEnabled()) {
			LOG.info(getResourceLoader().getContents("", false));
		}
	}

	public static boolean isInitializing() {
		return initializing;
	}

	public static void setInitializing(boolean initializing) {
		GlobalResourceLoader.initializing = initializing;
	}
}