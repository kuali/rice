package org.kuali.rice.resourceloader;

import javax.xml.namespace.QName;

import org.springframework.beans.factory.BeanFactory;


/**
 * Wraps a {@link BeanFactory} as a {@link ResourceLoader}.
 * 
 * Does not start or stop the {@link BeanFactory}.  Assumes this is being done 
 * by the application that started it.
 * 
 * @author rkirkend
 *
 */
public class BeanFactoryResourceLoader extends BaseResourceLoader {

	private BeanFactory beanFactory;
	
	public BeanFactoryResourceLoader(QName name, BeanFactory beanFactory) {
		super(name);
		this.beanFactory = beanFactory;
	}

	@Override
	public Object getService(QName serviceName) {
		if (this.beanFactory.containsBean(serviceName.toString())) {
			Object service = this.beanFactory.getBean(serviceName.toString());
			return wrap(serviceName, service);
		}
		return super.getService(serviceName);
	}
	
}