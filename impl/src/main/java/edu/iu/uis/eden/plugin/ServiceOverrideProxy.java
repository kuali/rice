/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.plugin;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.kuali.rice.proxy.BaseInvocationHandler;
import org.kuali.rice.proxy.TargetedInvocationHandler;
import org.kuali.rice.resourceloader.GlobalResourceLoader;


/**
 * A proxy for a service which can be overridden in the Institutional Plugin.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ServiceOverrideProxy extends BaseInvocationHandler implements OverridableService, TargetedInvocationHandler {
	
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(ServiceOverrideProxy.class);
	
	private static final String GET_DEFAULT_SERVICE_METHOD = "getDefaultService";
	
	private final QName serviceName;
	private final Object defaultService;
	private final Method defaultServiceMethod;
	
	private Object actualService = null;
	
	public ServiceOverrideProxy(QName serviceName, Object defaultService) throws NoSuchMethodException {
		this.serviceName = serviceName;
		this.defaultService = defaultService;
		this.defaultServiceMethod = OverridableService.class.getMethod(GET_DEFAULT_SERVICE_METHOD, new Class[0]);
	}
	
	protected Object invokeInternal(Object proxy, Method m, Object[] args) throws Throwable {
        if (m.equals(defaultServiceMethod)) {
        	return m.invoke(this, args);
        }
		synchronized (this) {
        	if (actualService == null) {
        		actualService = GlobalResourceLoader.getService(serviceName);
        		// if we get an OverridableService instance back then we know that we are using the default service implementation
        		if (actualService instanceof OverridableService) {
        			actualService = ((OverridableService)actualService).getDefaultService();
        			LOG.info("Could not locate service override for service '" + serviceName + "', falling back to default implementation.");
        		} else {
        			LOG.info("Found service override for service '" + serviceName + "' in the institutional plugin.");
        		}
        	}
        }
		return m.invoke(actualService, args);
    }
	
	public Object getDefaultService() {
		return defaultService;
	}
	
	public Object getTarget() {
		return actualService;
	}
	
}
