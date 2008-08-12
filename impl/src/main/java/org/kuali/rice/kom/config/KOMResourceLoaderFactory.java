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
package org.kuali.rice.kom.config;

import javax.xml.namespace.QName;

import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.core.resourceloader.SpringResourceLoader;

/**
 * Creates a {@link ResourceLoader} for KOM services and puts the resource loader in the 
 * correct place in the {@link GlobalResourceLoader} resource loading mix.
 * 
 * Returns the {@link ResourceLoader} ready to be started.
 *  
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KOMResourceLoaderFactory {

	private static final String KOM_SPRING_RESOURCE_LOADER_LOCAL_NAME = "KOM_SPRING_RESOURCE_LOADER";
	private static final String KOM_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kom/config/KOMSpringBeans.xml";
	
	/**
	 * This method initializes the resource loader for KOM.
	 */
	private static void initialize() {
		if (getSpringResourceLoaderName() == null) {
			setSpringResourceLoaderName(new QName(ConfigContext.getCurrentContextConfig().getMessageEntity(), KOM_SPRING_RESOURCE_LOADER_LOCAL_NAME));
		}
	}
	
	/**
	 * This method pulls in the KOM specific Spring beans file.
	 * 
	 * @return ResourceLoader
	 */
	public static ResourceLoader createRootKOMResourceLoader() {
		initialize();
		ResourceLoader resourceLoader = new SpringResourceLoader(getSpringResourceLoaderName(), KOM_SPRING_BEANS_PATH);
		GlobalResourceLoader.addResourceLoaderFirst(resourceLoader);
		return resourceLoader;
	}
	
	/**
	 * This method retrieves the KOM specific resource loader.
	 * 
	 * @return SpringResourceLoader
	 */
	public static SpringResourceLoader getSpringResourceLoader() {
		return (SpringResourceLoader)GlobalResourceLoader.getResourceLoader(getSpringResourceLoaderName());
	}
	
	/**
	 * This method retrieves the name of the KOM Spring resource loader.
	 * 
	 * @return QName
	 */
	public static QName getSpringResourceLoaderName() {
		return (QName)ConfigContext.getCurrentContextConfig().getObject(KOM_SPRING_RESOURCE_LOADER_LOCAL_NAME);
	}
	
	/**
	 * This method sets the name of the KOM specific resource loader.
	 * 
	 * @param KOMSpringResourceLoaderName
	 */
	public static void setSpringResourceLoaderName(QName KOMSpringResourceLoaderName) {
		ConfigContext.getCurrentContextConfig().getObjects().put(KOM_SPRING_RESOURCE_LOADER_LOCAL_NAME, KOMSpringResourceLoaderName);
	}	
}