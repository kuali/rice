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
package org.kuali.notification.config;

import javax.xml.namespace.QName;

import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.kuali.rice.resourceloader.SpringResourceLoader;

/**
 * Creates a {@link ResourceLoader} for KIM services and puts the resource loader in the 
 * correct place in the {@link GlobalResourceLoader} resource loading mix.
 * 
 * Returns the {@link ResourceLoader} ready to be started.
 *  
 * Ripped from KIMResourceLoaderFactory
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KENResourceLoaderFactory {

	private static final String KEN_SPRING_RESOURCE_LOADER_LOCAL_NAME = "KEN_SPRING_RESOURCE_LOADER";
	
	/**
	 * This method initializes the resource loader for KIM.
	 */
	private static void initialize() {
		if (getSpringResourceLoaderName() == null) {
			setSpringResourceLoaderName(new QName(Core.getCurrentContextConfig().getMessageEntity(), KEN_SPRING_RESOURCE_LOADER_LOCAL_NAME));
		}
	}
	
	/**
	 * This method pulls in the KIM specific Spring beans file.
	 * 
	 * @return ResourceLoader
	 */
	public static ResourceLoader createRootKENResourceLoader(String context) {
		initialize();
		ResourceLoader resourceLoader = new SpringResourceLoader(getSpringResourceLoaderName(), context);
		GlobalResourceLoader.addResourceLoaderFirst(resourceLoader);
		return resourceLoader;
	}
	
	/**
	 * This method retrieves the KIM specific resource loader.
	 * 
	 * @return SpringResourceLoader
	 */
	public static SpringResourceLoader getSpringResourceLoader() {
		return (SpringResourceLoader)GlobalResourceLoader.getResourceLoader(getSpringResourceLoaderName());
	}
	
	/**
	 * This method retrieves the name of the KIM Spring resource loader.
	 * 
	 * @return QName
	 */
	public static QName getSpringResourceLoaderName() {
		return (QName)Core.getCurrentContextConfig().getObject(KEN_SPRING_RESOURCE_LOADER_LOCAL_NAME);
	}
	
	/**
	 * This method sets the name of the KIM specific resource loader.
	 * 
	 * @param kimSpringResourceLoaderName
	 */
	public static void setSpringResourceLoaderName(QName kimSpringResourceLoaderName) {
		Core.getCurrentContextConfig().getObjects().put(KEN_SPRING_RESOURCE_LOADER_LOCAL_NAME, kimSpringResourceLoaderName);
	}	
}
