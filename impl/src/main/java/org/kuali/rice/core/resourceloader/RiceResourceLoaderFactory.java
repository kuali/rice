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
package org.kuali.rice.core.resourceloader;

import javax.xml.namespace.QName;

import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.ConfigurationException;



/**
 * Creates resource loader for rice spring context.
 * Uses config object to store QNames so everything is good with the current context classloader.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RiceResourceLoaderFactory {

	private static final String RICE_ROOT_RESOURCE_LOADER_NAME = "RICE_ROOT_RESOURCE_LOADER";
	private static final String RICE_SPRING_RESOURCE_LOADER_NAME = "RICE_SPRING_RESOURCE_LOADER_NAME";

	private static void initialize() {
		Config config = ConfigContext.getCurrentContextConfig();
		if (config.getMessageEntity() == null) {
			throw new ConfigurationException("No message entity available at this time");
		}
		if (getRootResourceLoaderName() == null) {
			setRootResourceLoaderName(new QName(ConfigContext.getCurrentContextConfig().getMessageEntity(), RICE_ROOT_RESOURCE_LOADER_NAME));
		}
		if (getSpringResourceLoaderName() == null) {
			setSpringResourceLoaderName(new QName(ConfigContext.getCurrentContextConfig().getMessageEntity(), RICE_SPRING_RESOURCE_LOADER_NAME));
		}
	}

	public static ResourceLoader createRootRiceResourceLoader(String springFileLocations) {
		initialize();
		ResourceLoader rootResourceLoader = 
			new BaseResourceLoader((QName)ConfigContext.getCurrentContextConfig().getObject(RICE_ROOT_RESOURCE_LOADER_NAME),
			new SimpleServiceLocator());
		ResourceLoader springResourceLoader = 
			new SpringResourceLoader((QName)ConfigContext.getCurrentContextConfig().getObject(RICE_SPRING_RESOURCE_LOADER_NAME),
			springFileLocations.split(SpringLoader.SPRING_SEPARATOR_CHARACTER));
		rootResourceLoader.addResourceLoaderFirst(springResourceLoader);
		return rootResourceLoader;
	}

	public static BaseResourceLoader getRootResourceLoader() {
		return (BaseResourceLoader)GlobalResourceLoader.getResourceLoader(getRootResourceLoaderName());
	}

	public static SpringResourceLoader getSpringResourceLoader() {
		return (SpringResourceLoader)GlobalResourceLoader.getResourceLoader(getSpringResourceLoaderName());
	}

	public static QName getRootResourceLoaderName() {
		return (QName)ConfigContext.getCurrentContextConfig().getObject(RICE_ROOT_RESOURCE_LOADER_NAME);
	}

	public static void setRootResourceLoaderName(QName name) {
		ConfigContext.getCurrentContextConfig().getObjects().put(RICE_ROOT_RESOURCE_LOADER_NAME, name);
	}

	public static QName getSpringResourceLoaderName() {
		return (QName)ConfigContext.getCurrentContextConfig().getObject(RICE_SPRING_RESOURCE_LOADER_NAME);
	}

	public static void setSpringResourceLoaderName(QName ksbRsourceLoaderName) {
		ConfigContext.getCurrentContextConfig().getObjects().put(RICE_SPRING_RESOURCE_LOADER_NAME, ksbRsourceLoaderName);
	}

}
