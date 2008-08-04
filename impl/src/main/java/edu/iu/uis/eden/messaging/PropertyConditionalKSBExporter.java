/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.messaging;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.Core;

/**
 * A KSBExporter which only exports the service if the specified property is set to true.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PropertyConditionalKSBExporter extends KSBExporter {
	
    private String propertyName;

	public void afterPropertiesSet() throws Exception {
	    Boolean useRemoteIdentityServices = false;
	    String useRemoteIdentityServicesValue = Core.getCurrentContextConfig().getProperty(getPropertyName());
        if (!StringUtils.isBlank(useRemoteIdentityServicesValue)) {
            useRemoteIdentityServices = new Boolean(useRemoteIdentityServicesValue);
        }
        if (!useRemoteIdentityServices) {
            super.afterPropertiesSet();
        }
	}

    public String getPropertyName() {
        return this.propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
	
}
