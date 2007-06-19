package edu.iu.uis.eden.messaging;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.util.ClassLoaderUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;


public class KEWHttpInvokerProxyFactoryBean extends HttpInvokerProxyFactoryBean {
	private static final Logger LOG = Logger.getLogger(KEWHttpInvokerProxyFactoryBean.class);

	private Object serviceProxy;

	private ServiceInfo serviceInfo;

	public ServiceInfo getServiceInfo() {
		return this.serviceInfo;
	}

	public void setServiceInfo(ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	@Override
	public void afterPropertiesSet() {
		ProxyFactory proxyFactory = new ProxyFactory(getServiceInterfaces());
		proxyFactory.addAdvice(this);
		LOG.debug("Http proxying service " + this.serviceInfo);
		this.serviceProxy = proxyFactory.getProxy();
	}

	@Override
	public Object getObject() {
		return this.serviceProxy;
	}

	@Override
	public Class getObjectType() {
		return getObject().getClass();
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public Class[] getServiceInterfaces() {
		List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>();
		JavaServiceDefinition javaServiceDefinition = (JavaServiceDefinition) this.serviceInfo.getServiceDefinition();
		try {
			for (String interfaceName : javaServiceDefinition.getServiceInterfaces()) {
				Class<?> clazz = Class.forName(interfaceName, true, ClassLoaderUtils.getDefaultClassLoader());
				LOG.debug("Adding service interface '" + clazz + "' to proxy object for service " + this.serviceInfo);
				serviceInterfaces.add(clazz);
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return serviceInterfaces.toArray(new Class[0]);
	}
}
