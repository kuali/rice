package edu.iu.uis.eden.messaging;

import java.io.InterruptedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.ConnectionPoolTimeoutException;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.log4j.Logger;
import org.kuali.rice.proxy.BaseTargetedInvocationHandler;
import org.kuali.rice.resourceloader.ContextClassLoaderProxy;
import org.kuali.rice.util.ClassLoaderUtils;

import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;


public class BusClientFailureProxy extends BaseTargetedInvocationHandler {

	private static final Logger LOG = Logger.getLogger(BusClientFailureProxy.class);

	private ServiceInfo serviceInfo;

	// exceptions that will cause this Proxy to remove the service from the bus
	private static List<Class> serviceRemovalExceptions = new ArrayList<Class>();

	static {
		serviceRemovalExceptions.add(NoHttpResponseException.class);
		serviceRemovalExceptions.add(InterruptedIOException.class);
		serviceRemovalExceptions.add(UnknownHostException.class);
		serviceRemovalExceptions.add(NoRouteToHostException.class);
		serviceRemovalExceptions.add(ConnectTimeoutException.class);
		serviceRemovalExceptions.add(ConnectionPoolTimeoutException.class);
		serviceRemovalExceptions.add(ConnectException.class);
	}

	private BusClientFailureProxy(Object target, ServiceInfo serviceInfo) {
		super(target);
		this.serviceInfo = serviceInfo;
	}

	public static Object wrap(Object target, ServiceInfo serviceInfo) {
		return Proxy.newProxyInstance(ClassLoaderUtils.getDefaultClassLoader(), ContextClassLoaderProxy.getInterfacesToProxyIncludeSpring(target), new BusClientFailureProxy(target, serviceInfo));
	}

	protected Object invokeInternal(Object proxyObject, Method method, Object[] params) throws Throwable {
		try {
			return method.invoke(getTarget(), params);
		} catch (Throwable throwable) {
			if (isServiceRemovalException(throwable)) {
				LOG.error("Exception caught accessing remote service " + this.serviceInfo.getQname(), throwable);
				RemoteResourceServiceLocator remoteResourceLocator = KSBResourceLoaderFactory.getRemoteResourceLocator();
				remoteResourceLocator.removeService(this.serviceInfo, proxyObject);

				Object service = remoteResourceLocator.getService(this.serviceInfo.getQname());
				if (service != null) {
					LOG.info("Refetched replacement service for service " + this.serviceInfo.getQname());
					return method.invoke(service, params);
				}
				LOG.error("Didn't find replacement service throwing exception");
			}
			throw throwable;
		}
	}

	private static boolean isServiceRemovalException(Throwable throwable) {
		LOG.info("Checking for Service Removal Exception: " + throwable.getClass().getName());
		if (serviceRemovalExceptions.contains(throwable.getClass())) {
			LOG.info("Found a Service Removal Exception: " + throwable.getClass().getName());
			return true;
		} else if (throwable instanceof HttpException) {
			HttpException httpException = (HttpException)throwable;
			if (httpException.getResponseCode() == 503) {
				LOG.info("Found a Service Removal Exception because of a 503: " + throwable.getClass().getName());
				return true;
			}
		}
		if (throwable.getCause() != null) {
			LOG.info("Unwrapping Throwable cause to check for service removal exception from: " + throwable.getClass().getName());
			return isServiceRemovalException(throwable.getCause());
		}
		return false;
	}
}