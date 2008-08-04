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

import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.BaseLifecycle;

import edu.iu.uis.eden.messaging.callforwarding.ForwardedCallHandlerImpl;
import edu.iu.uis.eden.messaging.config.ServiceBasedServiceDefinitionRegisterer;
import edu.iu.uis.eden.messaging.threadpool.KSBThreadPool;

/**
 * Implementation of the Bus Admin service.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BusAdminServiceImpl extends BaseLifecycle implements BusAdminService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BusAdminServiceImpl.class);

    private ServiceBasedServiceDefinitionRegisterer defRegisterer;

    public void forward(PersistedMessage message) throws Exception {
	// this is just weird...
	AsynchronousCall methodCall = message.getPayload().getMethodCall();
	message.setMethodCall(methodCall);
	// TODO there's probably a better way to servisize or refactor this
	new ForwardedCallHandlerImpl().handleCall(message);
    }

    public void ping() {
    }

    public void setCorePoolSize(int corePoolSize) {
	LOG.info("Setting core pool size to " + corePoolSize);
	KSBServiceLocator.getThreadPool().setCorePoolSize(corePoolSize);
    }

    public void setMaximumPoolSize(int maxPoolSize) {
	LOG.info("Setting max pool size to " + maxPoolSize);
	KSBThreadPool threadPool = KSBServiceLocator.getThreadPool();
	if (maxPoolSize < threadPool.getCorePoolSize()) {
	    maxPoolSize = threadPool.getCorePoolSize();
	}
	threadPool.setMaximumPoolSize(maxPoolSize);
    }

    public void setConfigProperty(String propertyName, String propertyValue) {
	String originalValue = Core.getCurrentContextConfig().getProperty(propertyName);
	LOG.info("Changing config property '" + propertyName + "' from " + originalValue + " to " + propertyValue);
	if (propertyValue == null) {
	    Core.getCurrentContextConfig().getProperties().remove(propertyName);
	} else {
	    Core.getCurrentContextConfig().getProperties().put(propertyName, propertyValue);
	}
    }

    public void start() throws Exception {
	this.defRegisterer = new ServiceBasedServiceDefinitionRegisterer("ksb.busAdminServiceDefinition");
	this.defRegisterer.registerServiceDefinition(false);
	setStarted(true);
    }

    public void stop() throws Exception {
	this.defRegisterer.unregisterServiceDefinition();
	setStarted(false);
    }

}
