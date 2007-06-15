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
 * @author rkirkend
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
				"org/kuali/kns/resources/KNSSpringBeans.xml");
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