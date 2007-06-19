package edu.iu.uis.eden.messaging;

import java.util.Arrays;
import java.util.List;

import org.kuali.rice.resourceloader.ContextClassLoaderProxy;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.support.RemoteInvocationTraceInterceptor;

import edu.iu.uis.eden.messaging.bam.BAMServerProxy;

public class KEWHttpInvokerServiceExporter extends HttpInvokerServiceExporter {
	
	private List<Class> serviceInterfaces;
	private ServiceInfo serviceInfo;
	
	public ServiceInfo getServiceInfo() {
		return this.serviceInfo;
	}

	public void setServiceInfo(ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	protected Object getProxyForService() {
		checkService();
		checkServiceInterface();
		ProxyFactory proxyFactory = new ProxyFactory();
		for (Class serviceInterface : getServiceInterfaces()) {
			proxyFactory.addInterface(serviceInterface);
		}
		if (isRegisterTraceInterceptor()) {
			proxyFactory.addAdvice(new RemoteInvocationTraceInterceptor(getExporterName()));
		}
		
		Object service = BAMServerProxy.wrap(getService(), this.serviceInfo);
		proxyFactory.setTarget(service);
		return proxyFactory.getProxy();
	}

	@Override
	protected void checkServiceInterface() throws IllegalArgumentException {
		if (this.serviceInterfaces == null) {
		    this.serviceInterfaces = Arrays.asList(ContextClassLoaderProxy.getInterfacesToProxy(getService()));
		}
		if (getServiceInterfaces().isEmpty()) {
			throw new IllegalArgumentException("At least one service interface should be defined.");
		}
	}
	
	public List<Class> getServiceInterfaces() {
		return this.serviceInterfaces;
	}

	public void setServiceInterfaces(List<Class> serviceInterfaces) {
		this.serviceInterfaces = serviceInterfaces;
	}
	
	public Object getService() {
		return super.getService();
	}
	
}
