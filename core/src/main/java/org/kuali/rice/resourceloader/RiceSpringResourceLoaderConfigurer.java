package org.kuali.rice.resourceloader;

import javax.xml.namespace.QName;

import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.core.Core;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;

/**
 * Wraps {@link BeanFactory} in {@link BeanFactoryResourceLoader} and places the {@link ResourceLoader} 
 * at the top of the {@link GlobalResourceLoader} stack.
 * 
 * @author rkirkend
 *
 */
public class RiceSpringResourceLoaderConfigurer implements BeanFactoryAware, InitializingBean {
	
	private QName name;
	private String localServiceName;
	private String serviceNameSpaceURI;

	private BeanFactory beanFactory;

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public void afterPropertiesSet() throws Exception {
		if (this.name == null) {
			if (this.getServiceNameSpaceURI() == null) {
				this.setServiceNameSpaceURI(Core.getCurrentContextConfig().getMessageEntity());
			}
			if (this.getLocalServiceName() == null) {
				throw new ConfigurationException("Need to give " + this.getClass().getName() + " a LocalServiceName");
			}
		}
		
		ResourceLoader beanFactoryRL = new BeanFactoryResourceLoader(getName(), this.beanFactory);
		GlobalResourceLoader.addResourceLoaderFirst(beanFactoryRL);
	}

	public QName getName() {
		if (this.name == null) {
			this.setName(new QName(this.getServiceNameSpaceURI(), this.getLocalServiceName()));
		}
		return name;
	}

	public void setName(QName name) {
		this.name = name;
	}
	
	public String getLocalServiceName() {
		return localServiceName;
	}

	public void setLocalServiceName(String localServiceName) {
		this.localServiceName = localServiceName;
	}

	public String getServiceNameSpaceURI() {
		return serviceNameSpaceURI;
	}

	public void setServiceNameSpaceURI(String serviceNameSpaceURI) {
		this.serviceNameSpaceURI = serviceNameSpaceURI;
	}

}