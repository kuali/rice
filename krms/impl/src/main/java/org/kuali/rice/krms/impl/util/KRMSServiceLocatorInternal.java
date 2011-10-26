/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krms.impl.util;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.module.RunMode;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;

/**
 * Like {@link org.kuali.rice.krms.api.KrmsApiServiceLocator} only for non-remotable.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KRMSServiceLocatorInternal {
	
	public static final String KRMS_RUN_MODE_PROPERTY = "krms.mode";
	public static final String KRMS_MODULE_NAMESPACE = "KRMS";
    public static final String VALIDATION_ACTION_SERVICE = "validationActionService";
    public static final String VALIDATION_RULE_SERVICE = "validationRuleService";

	private static final Logger LOG = Logger.getLogger(KRMSServiceLocatorInternal.class);

	
	@SuppressWarnings("unchecked")
	public static <A> A getService(String serviceName) {
		return (A)getBean(serviceName);
	}
	
	public static Object getBean(String serviceName) {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug("Fetching service " + serviceName);
		}
		return GlobalResourceLoader.getResourceLoader().getService(
				(RunMode.REMOTE.equals(RunMode.valueOf(ConfigContext.getCurrentContextConfig().getProperty(KRMS_RUN_MODE_PROPERTY)))) ?
						new QName(KRMS_MODULE_NAMESPACE, serviceName) : new QName(serviceName) );
	}
	
//    public static BusinessObjectService getBusinessObjectService() {
//    	return getService(KRMS_BO_SERVICE);
//    }
	
}
