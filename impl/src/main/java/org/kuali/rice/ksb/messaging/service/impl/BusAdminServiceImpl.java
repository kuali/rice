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

package org.kuali.rice.ksb.messaging.service.impl;

import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.lifecycle.BaseLifecycle;
import org.kuali.rice.ksb.messaging.service.BusAdminService;
import org.kuali.rice.ksb.messaging.threadpool.KSBThreadPool;


/**
 * Implementation of the Bus Admin service.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BusAdminServiceImpl extends BaseLifecycle implements BusAdminService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BusAdminServiceImpl.class);

    private KSBThreadPool threadPool;

    public void setThreadPool(KSBThreadPool threadPool) {
    	this.threadPool = threadPool;
    }
    
    protected KSBThreadPool getThreadPool() {
    	return this.threadPool;
    }
    
    public void ping() {
    }

    public void setCorePoolSize(int corePoolSize) {
    	LOG.info("Setting core pool size to " + corePoolSize);
    	getThreadPool().setCorePoolSize(corePoolSize);
    }

    public void setMaximumPoolSize(int maxPoolSize) {
    	LOG.info("Setting max pool size to " + maxPoolSize);
    	if (maxPoolSize < getThreadPool().getCorePoolSize()) {
    		maxPoolSize = getThreadPool().getCorePoolSize();
    	}
    	getThreadPool().setMaximumPoolSize(maxPoolSize);
    }

    public void setConfigProperty(String propertyName, String propertyValue) {
    	String originalValue = ConfigContext.getCurrentContextConfig().getProperty(propertyName);
    	LOG.info("Changing config property '" + propertyName + "' from " + originalValue + " to " + propertyValue);
    	if (propertyValue == null) {
    		ConfigContext.getCurrentContextConfig().removeProperty(propertyName);
    	} else {
    		ConfigContext.getCurrentContextConfig().putProperty(propertyName, propertyValue);
    	}
    }

    public void start() throws Exception {
    	if (getThreadPool() == null) {
    		throw new IllegalStateException("The threadPool has not been set on the busAdminService");
    	}
    	setStarted(true);
    }

    public void stop() throws Exception {
    	setStarted(false);
    }

}
