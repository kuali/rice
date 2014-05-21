/**
 * Copyright 2005-2014 The Kuali Foundation
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

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.core.api.util.ContextClassLoaderProxy;
import org.kuali.rice.core.api.util.reflect.BaseTargetedInvocationHandler;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;
import org.kuali.rice.ksb.api.bus.Endpoint;
import org.kuali.rice.ksb.api.bus.ServiceConfiguration;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class BusClientFailureProxy extends BaseTargetedInvocationHandler<Object> {

	private static final Logger LOG = Logger.getLogger(BusClientFailureProxy.class);

    static final String SERVICE_REMOVAL_EXCEPTIONS_BEAN = "rice.ksb.serviceRemovalExceptions";
    static final String SERVICE_REMOVAL_RESPONSE_CODES_BEAN = "rice.ksb.serviceRemovalResponseCodes";

	private final Object failoverLock = new Object();
	
	private ServiceConfiguration serviceConfiguration;

	private BusClientFailureProxy(Object target, ServiceConfiguration serviceConfiguration) {
		super(target);
		this.serviceConfiguration = serviceConfiguration;
	}

	public static Object wrap(Object target, ServiceConfiguration serviceConfiguration) {
		return Proxy.newProxyInstance(ClassLoaderUtils.getDefaultClassLoader(), ContextClassLoaderProxy.getInterfacesToProxy(target), new BusClientFailureProxy(target, serviceConfiguration));
	}

	protected Object invokeInternal(Object proxyObject, Method method, Object[] params) throws Throwable {
		Set<ServiceConfiguration> servicesTried = null;
		
		do {
			try {
				return method.invoke(getTarget(), params);
			} catch (Throwable throwable) {			
				if (isServiceRemovalException(throwable)) {
					synchronized (failoverLock) {
                        LOG.error("Exception caught accessing remote service " + this.serviceConfiguration.getServiceName() + " at " + this.serviceConfiguration.getEndpointUrl(), throwable);
                        if (servicesTried == null) {
							servicesTried = new HashSet<ServiceConfiguration>();
							servicesTried.add(serviceConfiguration);
						}
						Object failoverService = null;
						List<Endpoint> endpoints = KsbApiServiceLocator.getServiceBus().getEndpoints(serviceConfiguration.getServiceName(), serviceConfiguration.getApplicationId());
						for (Endpoint endpoint : endpoints) {
							if (!servicesTried.contains(endpoint.getServiceConfiguration())) {
								failoverService = endpoint.getService();
                                if(Proxy.isProxyClass(failoverService.getClass()) && Proxy.getInvocationHandler(failoverService) instanceof BusClientFailureProxy) {
                                    failoverService = ((BusClientFailureProxy)Proxy.getInvocationHandler(failoverService)).getTarget();
                                }
								servicesTried.add(endpoint.getServiceConfiguration());
                                break; // KULRICE-8728: BusClientFailureProxy doesn't try all endpoint options
							}
						}									
						if (failoverService != null) {
                            LOG.info("Refetched replacement service for service " + this.serviceConfiguration.getServiceName() + " at " + this.serviceConfiguration.getEndpointUrl());
                            // as per KULRICE-4287, reassign target to the new service we just fetched, hopefully this one works better!
							setTarget(failoverService);
						} else {
							LOG.error("Didn't find replacement service throwing exception");
							throw throwable;					
						}
					}
				} else {
					throw throwable;
				}
			}
		} while (true);
	}

	private static boolean isServiceRemovalException(Throwable throwable) {
		LOG.info("Checking for Service Removal Exception: " + throwable.getClass().getName());
		if (getServiceRemovalExceptions().contains(throwable.getClass())) {
			LOG.info("Found a Service Removal Exception: " + throwable.getClass().getName());
			return true;
		} else if (throwable instanceof org.kuali.rice.ksb.messaging.HttpException) {
			org.kuali.rice.ksb.messaging.HttpException httpException = (org.kuali.rice.ksb.messaging.HttpException)throwable;
			if (getServiceRemovalResponseCodes().contains(httpException.getResponseCode())) {
				LOG.info("Found a Service Removal Exception because of a " + httpException.getResponseCode() + " " + throwable.getClass().getName());
				return true;
			}
		} else if (throwable instanceof org.apache.cxf.transport.http.HTTPException) {
			org.apache.cxf.transport.http.HTTPException httpException = (org.apache.cxf.transport.http.HTTPException)throwable;
			if (getServiceRemovalResponseCodes().contains(httpException.getResponseCode())) {
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

    /**
     * Lazy initialization holder class idiom for static fields, see Effective Java item 71
     */
    private static class ServiceRemovalExceptionsHolder {
        static final List<Class<?>> serviceRemovalExceptions =
                GlobalResourceLoader.getService(SERVICE_REMOVAL_EXCEPTIONS_BEAN);
    }

    /**
     * Get the list of exception classes that are considered service removal exceptions.
     *
     * <p>These are the exceptions that, when thrown from a service proxy, indicate that we should fail over to the
     * next available endpoint for that service and remove the failing endpoint from the bus.</p>
     *
     * <p>On first call, the bean specified by {@link #SERVICE_REMOVAL_EXCEPTIONS_BEAN} will be lazily assigned and
     * used.</p>
     */
    private static List<Class<?>> getServiceRemovalExceptions() {
        return ServiceRemovalExceptionsHolder.serviceRemovalExceptions;
    }

    /**
     * Lazy initialization holder class idiom for static fields, see Effective Java item 71
     */
    private static class ServiceRemovalResponseCodesHolder {
        static final List<Integer> serviceRemovalResponseCodes =
                GlobalResourceLoader.getService(SERVICE_REMOVAL_RESPONSE_CODES_BEAN);
    }

    /**
     * Get the list of HTTP response codes that indicate the need to fail over.
     *
     * <p>These are the response codes that, when detected within an exception thrown by a service proxy, indicate
     * that we should fail over to the next available endpoint for that service and remove the failing endpoint from
     * the bus.</p>
     *
     * <p>On first call, the bean specified by {@link #SERVICE_REMOVAL_RESPONSE_CODES_BEAN} will be lazily assigned and
     * used.</p>
     */
    private static List<Integer> getServiceRemovalResponseCodes() {
        return ServiceRemovalResponseCodesHolder.serviceRemovalResponseCodes;
    }

}
