/*
 * Copyright 2007-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.web.struts.action;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.PropertyMessageResourcesFactory;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.struts.action.KualiPropertyMessageResources;

/**
 * A custom MessageResourceFactory that delegates to the KualiConfigurationService's pre-loaded properties. 
 * 
 * This factory can be used in struts-config.xml files by specifying a factory attribute in the <message-resources/> tag.  
 * Example: 
 *   <message-resources 
 *       factory="org.kuali.rice.kns.web.struts.action.KualiPropertyMessageResourcesFactory" 
 *       parameter="SampleApplicationResources" />
 */
public class KualiPropertyMessageResourcesFactory extends PropertyMessageResourcesFactory {

    private static final long serialVersionUID = 9045578011738154255L;

    /**
     * Uses KualiPropertyMessageResources, which allows multiple property files to be loaded into the defalt message set.
     * 
     * @see org.apache.struts.util.MessageResourcesFactory#createResources(java.lang.String)
     */
    @Override
    public MessageResources createResources(String config) {
        if (StringUtils.isBlank(config)) {
            config = (String)ConfigContext.getCurrentContextConfig().getProperties().get(KNSConstants.MESSAGE_RESOURCES);
            //config = KNSServiceLocator.getKualiConfigurationService().getPropertyString(KNSConstants.MESSAGE_RESOURCES);
        }
        return new KualiPropertyMessageResources(this, config, this.returnNull);
    }
    
}
