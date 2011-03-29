/*
 * Copyright 2006-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.ksb.messaging;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.ConnectionPoolTimeoutException;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.log4j.Logger;
import org.kuali.rice.core.impl.proxy.BaseTargetedInvocationHandler;
import org.kuali.rice.core.impl.resourceloader.ContextClassLoaderProxy;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.ksb.messaging.resourceloader.KSBResourceLoaderFactory;

import java.io.InterruptedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;



public class BusClientFailureProxy extends BaseTargetedInvocationHandler {

	private static final Logger LOG = Logger.getLogger(BusClientFailureProxy.class);

	private ServiceInfo serviceInfo;

	// exceptions that will cause this Proxy to remove the service from the bus
	private static List<Class> serviceRemovalExceptions = new ArrayList<Class>();
	private static List<Integer> serviceRemovalResponseCodes = new ArrayList<Integer>();

	static {
		serviceRemovalExceptions.add(NoHttpResponseException.class);
		serviceRemovalExceptions.add(InterruptedIOException.class);
		serviceRemovalExceptions.add(UnknownHostException.class);
		serviceRemovalExceptions.add(NoRouteToHostException.class);
		serviceRemovalExceptions.add(ConnectTimeoutException.class);
		serviceRemovalExceptions.add(ConnectionPoolTimeoutException.class);
		serviceRemovalExceptions.add(ConnectException.class);
	}
	
	static {
	    serviceRemovalResponseCodes.add(new Integer(404));
        serviceRemovalResponseCodes.add(new Integer(503));
	}

	private BusClientFailureProxy(Object target, ServiceInfo serviceInfo) {
		super(target);
		this.serviceInfo = serviceInfo;
	}

	public static Object wrap(Object target, ServiceInfo serviceInfo) {
		return Proxy.newProxyInstance(ClassLoaderUtils.getDefaultClassLoader(), ContextClassLoaderProxy.getInterfacesToProxy(target), new BusClientFailureProxy(target, serviceInfo));
	}

	protected Object invokeInternal(Object proxyObject, Method method, Object[] params) throws Throwable {
		Object service = getTarget();
		
		do {
			try {
				return method.invoke(service, params);
			} catch (Throwable throwable) {			
				if (isServiceRemovalException(throwable)) {
					LOG.error("Exception caught accessing remote service " + this.serviceInfo.getQname(), throwable);
					RemoteResourceServiceLocator remoteResourceLocator = KSBResourceLoaderFactory.getRemoteResourceLocator();
					remoteResourceLocator.removeService(this.serviceInfo);
	
					service = remoteResourceLocator.getService(this.serviceInfo.getQname());
									
					if (service != null) {
						LOG.info("Refetched replacement service for service " + this.serviceInfo.getQname());
						
						// as per KULRICE-4287, reassign target to the new service we just fetched, hopefully this one works better!
						setTarget(service);
						
					} else {
						LOG.error("Didn't find replacement service throwing exception");
						throw throwable;					
					}
				} else {
					throw throwable;
				}
			}
		} while (true);
	}

	private static boolean isServiceRemovalException(Throwable throwable) {
		LOG.info("Checking for Service Removal Exception: " + throwable.getClass().getName());
		if (serviceRemovalExceptions.contains(throwable.getClass())) {
			LOG.info("Found a Service Removal Exception: " + throwable.getClass().getName());
			return true;
		} else if (throwable instanceof HttpException) {
			HttpException httpException = (HttpException)throwable;
			if (serviceRemovalResponseCodes.contains(httpException.getResponseCode())) {
				LOG.info("Found a Service Removal Exception because of a " + httpException.getResponseCode() + " " + throwable.getClass().getName());
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
