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
package org.kuali.rice.ksb.service;

import java.util.Map;

import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.ServiceFactory;
import org.kuali.rice.ksb.messaging.MessageHelper;
import org.kuali.rice.ksb.messaging.threadpool.KSBScheduledPool;
import org.kuali.rice.ksb.messaging.threadpool.KSBThreadPool;


public class KSBContextServiceLocator {

	private Map services;
	
    public static final String THREAD_POOL_SERVICE = "enThreadPool";
    public static final String SCHEDULED_THREAD_POOL_SERVICE = "enScheduledThreadPool";    

    public Object getService(String name) {
        return services.get(name);
    }

    /**
	 * @return the services
	 */
	public Map getServices() {
		return this.services;
	}

	/**
	 * @param services the services to set
	 */
	public void setServices(Map services) {
		this.services = services;
	}

    public MessageHelper getMessageHelper() {
        return (MessageHelper) getService("enMessageHelper");
    }

    public KSBThreadPool getThreadPool() {
        return (KSBThreadPool) getService(THREAD_POOL_SERVICE);
    }

    public KSBScheduledPool getScheduledPool() {
        return (KSBScheduledPool) getService(SCHEDULED_THREAD_POOL_SERVICE);
    }

    public ServiceFactory getXFireServiceFactory() {
        return (ServiceFactory) getService("xfire.serviceFactory");
    }

    public XFire getXFire() {
        return (XFire) getService("xfire");
    }

}