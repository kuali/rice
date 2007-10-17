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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ResourceFactoryBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;

/**
 * ResourceFactoryBean subclass that reads the location from the Notification config
 * System property, 'notification.config'
 * TODO: when we move to Spring 2.0, try placing ${notification.config} directly in the locations list
 * it may resolve correctly.
 * e.g.
 * <bean id="propertyPlaceholderConfigurer" class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
 *              <property name="order" value="1" />
 *              <property name="locations">
 *                       <list>
 *                               <value>classpath:path/whatever.properties</value>
 *                               <value>${some.variable}</value>
 *                       </list>
 *               </property>
 *               <property name="ignoreResourceNotFound" value="true" />
 *       </bean>
 * see Wired in Freenode #spring channel
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationConfigResourceFactoryBean extends ResourceFactoryBean implements InitializingBean, ResourceLoaderAware {
    private static final String CFG_LOCATION_PROPERTY = "notification.config";

    /**
     * The containing resource loader
     */
    private ResourceLoader resourceLoader;
    
    /**
     * Sets the containing resource loader
     * @see org.springframework.context.ResourceLoaderAware#setResourceLoader(org.springframework.core.io.ResourceLoader)
     */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Reads the resource location from the 'notification.config' System property, if it is set
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() {
        String cfgLocation = System.getProperty(CFG_LOCATION_PROPERTY);
        if (cfgLocation != null) {
            setLocation(resourceLoader.getResource(cfgLocation));
        } else {
            // have to set a filename because PropertiesLoaderSupport insists
            // on dereferencing it
            setLocation(new ByteArrayResource(new byte[0]) {
                public boolean exists() {
                    return false;
                }
                public String getFilename() {
                    return "";
                }
            });
        }
    }
}