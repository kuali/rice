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
package org.kuali.rice.kns.config;

import javax.xml.namespace.QName;

import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.kuali.rice.resourceloader.SpringResourceLoader;


/**
 * Creates {@link ResourceLoader} for KNS services and puts the resource loader in the 
 * correct place in the {@link GlobalResourceLoader} resource loading mix.
 * 
 * Returns the {@link ResourceLoader} ready to be started.
 *  
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KNSResourceLoaderFactory {

	private static final String KNS_SPRING_RESOURCE_LOADER_LOCAL_NAME = "KNS_SPRING_RESOURCE_LOADER";
	
	private static void initialize() {
		if (getSpringResourceLoaderName() == null) {
			setSpringResourceLoaderName(new QName(Core.getCurrentContextConfig().getMessageEntity(), KNS_SPRING_RESOURCE_LOADER_LOCAL_NAME));
		}
	}
	
	public static ResourceLoader createRootKNSResourceLoader() {
		initialize();
		ResourceLoader resourceLoader = new SpringResourceLoader(getSpringResourceLoaderName(), 
				"KNSSpringBeans.xml");
		GlobalResourceLoader.addResourceLoaderFirst(resourceLoader);
		return resourceLoader;
	}
	
	public static SpringResourceLoader getSpringResourceLoader() {
		return (SpringResourceLoader)GlobalResourceLoader.getResourceLoader(getSpringResourceLoaderName());
	}
	
	public static QName getSpringResourceLoaderName() {
		return (QName)Core.getCurrentContextConfig().getObject(KNS_SPRING_RESOURCE_LOADER_LOCAL_NAME);
	}
	
	public static void setSpringResourceLoaderName(QName knsSpringResourceLoaderName) {
		Core.getCurrentContextConfig().getObjects().put(KNS_SPRING_RESOURCE_LOADER_LOCAL_NAME, knsSpringResourceLoaderName);
	}	
}