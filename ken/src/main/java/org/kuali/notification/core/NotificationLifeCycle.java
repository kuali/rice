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
package org.kuali.notification.core;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;

/**
 * Eager-initializing singleton bean that performs some notification startup operations
 * @author Aaron Hamid (arh14 at cornell edu)
 */
public class NotificationLifeCycle extends LifecycleBean implements BeanFactoryAware {
    private static final Logger LOG = Logger.getLogger(NotificationLifeCycle.class);

    private String ojbPlatform;
    private BeanFactory theFactory;

    /**
     * This method sets the OJB platform.
     * @param platform
     */
    public void setOjbPlatform(String platform) {
        this.ojbPlatform = platform;
    }

    /**
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory theFactory) throws BeansException {
	 this.theFactory = theFactory;
    }

    /**
     * @see org.kuali.notification.core.BaseLifecycle#start()
     */
    public void start() throws Exception {
        if (ojbPlatform == null) {
            throw new BeanInitializationException("No platform was configured, please configure the datasource.ojb.platform property.");
        }

        GlobalNotificationServiceLocator.init(theFactory);
        //LOG.info("Setting OJB platform to: " + ojbPlatform);
        //PersistenceBrokerFactory.defaultPersistenceBroker().serviceConnectionManager().getConnectionDescriptor().setDbms(ojbPlatform);
        super.start();
    }

    /**
     * @see org.kuali.notification.core.BaseLifecycle#stop()
     */
    public void stop() throws Exception {
        GlobalNotificationServiceLocator.destroy();
        super.stop();
    }
}