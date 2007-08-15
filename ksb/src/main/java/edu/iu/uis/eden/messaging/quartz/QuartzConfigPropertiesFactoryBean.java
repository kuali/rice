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
package edu.iu.uis.eden.messaging.quartz;

import java.util.Properties;

import org.kuali.rice.core.Core;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * A factory bean which reads quartz-related properties from the Config system and
 * generates a Properites instance for use when configuration quartz.
 * 
 * @author Eric Westfall
 *
 */
public class QuartzConfigPropertiesFactoryBean extends AbstractFactoryBean {

    private static final String QUARTZ_PREFIX = "ksb.org.quartz";
    
    @Override
    protected Object createInstance() throws Exception {
	Properties properties = new Properties();
	Properties configProps = Core.getCurrentContextConfig().getProperties();
	for (Object keyObj : configProps.keySet()) {
	    if (keyObj instanceof String) {
		String key = (String)keyObj;
		if (key.startsWith(QUARTZ_PREFIX)) {
		    properties.put(key.substring(4), configProps.get(key));
		}
	    }
	}
	return properties;
    }
    
    @Override
    public Class getObjectType() {
	return Properties.class;
    }

}
