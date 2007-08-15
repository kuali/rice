/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.messaging;

import java.lang.reflect.Method;

import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.exceptions.RiceRuntimeException;
import org.kuali.rice.lifecycle.BaseLifecycle;

import edu.iu.uis.eden.messaging.config.ServiceBasedServiceDefinitionRegisterer;

public class RepeatTopicInvokerQueueImpl extends BaseLifecycle implements RepeatTopicInvokerQueue {

	private ServiceBasedServiceDefinitionRegisterer defRegisterer;
	
	public Object invokeTopic(AsynchronousCall methodCall) {
		ServiceInfo serviceInfo = methodCall.getServiceInfo();
		Object service = KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceInfo.getQname());
		try {
			Method method = service.getClass().getMethod(methodCall.getMethodName(), methodCall.getParamTypes());
			return method.invoke(service, methodCall.getArguments());
		} catch (Throwable t) {
			throw new RiceRuntimeException("Caught Exception invoking repeatable topic", t);
		}
	}
	
	public void start() throws Exception {
	    this.defRegisterer = new ServiceBasedServiceDefinitionRegisterer("repeatTopicInvokerQueueDefinition");
	    this.defRegisterer.registerServiceDefinition(false);
		setStarted(true);
	}

	public void stop() throws Exception {
	    this.defRegisterer.unregisterServiceDefinition();
		setStarted(false);
	}
}
