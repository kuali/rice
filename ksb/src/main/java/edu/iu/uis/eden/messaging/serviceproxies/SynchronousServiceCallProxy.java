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
package edu.iu.uis.eden.messaging.serviceproxies;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.List;

import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;
import org.kuali.rice.exceptions.RiceRuntimeException;
import org.kuali.rice.resourceloader.ContextClassLoaderProxy;
import org.kuali.rice.util.ClassLoaderUtils;

import edu.iu.uis.eden.messaging.AsynchronousCallback;
import edu.iu.uis.eden.messaging.MessageServiceInvoker;
import edu.iu.uis.eden.messaging.PersistedMessage;
import edu.iu.uis.eden.messaging.RemotedServiceHolder;

/**
 * Used to Call a service synchronously but through the messaging code within workflow. Used to when switching generally
 * asynchronously called services to synchronously called services. Generally for testing purposes.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class SynchronousServiceCallProxy extends AsynchronousServiceCallProxy {

    private SynchronousServiceCallProxy(List<RemotedServiceHolder> serviceDefs, AsynchronousCallback callback,
	    Serializable context, String value1, String value2) {
	super(serviceDefs, callback, context, value1, value2);
    }

    public static Object createInstance(List<RemotedServiceHolder> serviceDefs, AsynchronousCallback callback,
	    Serializable context, String value1, String value2) {
	if (serviceDefs == null || serviceDefs.isEmpty()) {
	    throw new RuntimeException("Cannot create service proxy, no service(s) passed in.");
	}
	try {
	return Proxy.newProxyInstance(ClassLoaderUtils.getDefaultClassLoader(), ContextClassLoaderProxy
		.getInterfacesToProxyIncludeSpring(serviceDefs.get(0).getService()), new SynchronousServiceCallProxy(
		serviceDefs, callback, context, value1, value2));
	} catch (Exception e) {
	    throw new RiceRuntimeException(e);
    }
    }

    @Override
    protected void executeMessage(PersistedMessage message) {
	if (!new Boolean(Core.getCurrentContextConfig().getProperty(RiceConstants.MESSAGING_OFF))) {
	    new MessageServiceInvoker(message).run();
	}
    }
}