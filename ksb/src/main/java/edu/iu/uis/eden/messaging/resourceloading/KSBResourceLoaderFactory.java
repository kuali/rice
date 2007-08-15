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
package edu.iu.uis.eden.messaging.resourceloading;

import javax.xml.namespace.QName;

import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.BaseResourceLoader;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.kuali.rice.resourceloader.SimpleServiceLocator;
import org.kuali.rice.resourceloader.SpringResourceLoader;

import edu.iu.uis.eden.messaging.RemoteResourceServiceLocator;
import edu.iu.uis.eden.messaging.RemoteResourceServiceLocatorImpl;


/**
 * Creates KSBs root resource loader with all the correct children attached ready for starting.
 * Uses config object to store QNames so everything is good with the current context classloader.
 * 
 * Can grab any KSB specific resource loader from the resource loading stack.
 * 
 * 
 * @author rkirkend
 *
 */
public class KSBResourceLoaderFactory {
	
	private static final String KSB_ROOT_RESOURCE_LOACER_NAME = "KSB_ROOT_RESOURCE_LOADER";
	private static final String KSB_SPRING_RESOURCE_LOADER_LOCAL_NAME = "KSB_SPRING_RESOURCE_LOADER";
	private static final String KSB_REMOTE_RESOURCE_LOADER_LOCAL_NAME = "KSB_REMOTE_RESOURCE_LOADER";
	
	private static void initialize() {
		Config config = Core.getCurrentContextConfig();
		if (config.getMessageEntity() == null) {
			throw new ConfigurationException("No message entity available at this time");
		}
		if (getRootResourceLoaderName() == null) {
			setRootResourceLoaderName(new QName(Core.getCurrentContextConfig().getMessageEntity(), KSB_ROOT_RESOURCE_LOACER_NAME));
		}
		if (getSpringResourceLoaderName() == null) {
			setSpringResourceLoaderName(new QName(Core.getCurrentContextConfig().getMessageEntity(), KSB_SPRING_RESOURCE_LOADER_LOCAL_NAME));
		}
		if (getRemoteResourceLoaderName() == null) {
			setRemoteResourceLoaderName(new QName(Core.getCurrentContextConfig().getMessageEntity(), KSB_REMOTE_RESOURCE_LOADER_LOCAL_NAME));
		}
	}
	
	public static ResourceLoader createRootKSBResourceLoader() {
		initialize();
		ResourceLoader rootResourceLoader = new BaseResourceLoader(getRootResourceLoaderName(), new SimpleServiceLocator());
		ResourceLoader springResourceLoader = new SpringResourceLoader(getSpringResourceLoaderName(), 
				"KSBSpringBeans.xml");
		GlobalResourceLoader.addResourceLoader(rootResourceLoader);
		rootResourceLoader.addResourceLoader(springResourceLoader);
		rootResourceLoader.addResourceLoader(new RemoteResourceServiceLocatorImpl(getRemoteResourceLoaderName()));
		return rootResourceLoader;
	}
	
	public static BaseResourceLoader getRootResourceLoader() {
		return (BaseResourceLoader)GlobalResourceLoader.getResourceLoader(getRootResourceLoaderName());
	}
	
	public static SpringResourceLoader getSpringResourceLoader() {
		return (SpringResourceLoader)GlobalResourceLoader.getResourceLoader(getSpringResourceLoaderName());
	}
	
	public static RemoteResourceServiceLocator getRemoteResourceLocator() {
		return (RemoteResourceServiceLocator)GlobalResourceLoader.getResourceLoader(getRemoteResourceLoaderName());
	}
	
	public static QName getRootResourceLoaderName() {
		return (QName)Core.getCurrentContextConfig().getObject(KSB_ROOT_RESOURCE_LOACER_NAME);
	}
	
	public static void setRootResourceLoaderName(QName name) {
		Core.getCurrentContextConfig().getObjects().put(KSB_ROOT_RESOURCE_LOACER_NAME, name);
	}

	public static QName getSpringResourceLoaderName() {
		return (QName)Core.getCurrentContextConfig().getObject(KSB_SPRING_RESOURCE_LOADER_LOCAL_NAME);
	}
	
	public static void setSpringResourceLoaderName(QName ksbRsourceLoaderName) {
		Core.getCurrentContextConfig().getObjects().put(KSB_SPRING_RESOURCE_LOADER_LOCAL_NAME, ksbRsourceLoaderName);
	}

	public static QName getRemoteResourceLoaderName() {
		return (QName)Core.getCurrentContextConfig().getObject(KSB_REMOTE_RESOURCE_LOADER_LOCAL_NAME);
	}

	public static void setRemoteResourceLoaderName(QName remoteResourceLoaderName) {
		Core.getCurrentContextConfig().getObjects().put(KSB_REMOTE_RESOURCE_LOADER_LOCAL_NAME, remoteResourceLoaderName);
	}

}
