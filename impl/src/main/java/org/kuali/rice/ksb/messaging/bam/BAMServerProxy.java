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
package org.kuali.rice.ksb.messaging.bam;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.kuali.rice.core.proxy.BaseTargetedInvocationHandler;
import org.kuali.rice.core.resourceloader.ContextClassLoaderProxy;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.messaging.bam.service.BAMService;
import org.kuali.rice.ksb.service.KSBServiceLocator;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * A service-side proxy for that records an entry in the BAM for invocations
 * on the proxied service endpoint.
 *
 * @see BAMService
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class BAMServerProxy extends BaseTargetedInvocationHandler {

	private ServiceInfo entry;
	
	private BAMServerProxy(Object target, ServiceInfo entry) {
		super(target);
		this.entry = entry;
	}
	
	public static Object wrap(Object target, ServiceInfo entry) {
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), ContextClassLoaderProxy.getInterfacesToProxy(target), new BAMServerProxy(target, entry));
	}
	
	
	protected Object invokeInternal(Object proxiedObject, Method method, Object[] arguments) throws Throwable {
		BAMTargetEntry bamTargetEntry = KSBServiceLocator.getBAMService().recordServerInvocation(getTarget(), this.entry, method, arguments);
		try {
			return method.invoke(getTarget(), arguments);	
		} catch (Throwable throwable) {
			throwable = ExceptionUtils.getCause(throwable);
			KSBServiceLocator.getBAMService().recordServerInvocationError(throwable, bamTargetEntry);
			throw throwable;
		}
	}
}
