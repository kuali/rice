package org.kuali.rice.kns.config;

import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Exports services in the {@link GlobalResourceLoader} as beans available to Spring.
 * 
 * @author rkirkend
 *
 */
public class GlobalResourceLoaderServiceFactoryBean implements FactoryBean, InitializingBean {

	private String serviceName;
	private boolean singleton;
	
	public Object getObject() throws Exception {
		return GlobalResourceLoader.getService(this.getServiceName());
	}

	public Class getObjectType() {
		return Object.class;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	public void afterPropertiesSet() throws Exception {
		if (this.getServiceName() == null) {
			throw new ConfigurationException("No serviceName given.");
		}
	}

}
