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

package org.kuali.rice.kew.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrLookup;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Looks up Strings from the Config and System Parameters.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigStringLookup extends StrLookup {
	
	private String applicationId;
	
	public ConfigStringLookup() {
		
	}
	
	public ConfigStringLookup(String applicationId) {
		this.applicationId = applicationId;
	}
	
	@Override
	public String lookup(String propertyName) {
		if (StringUtils.isBlank(propertyName)) {
			return null;
		}
		
		String paramValue = null;
		
		// TODO temporarily disabling configuration parameter resolution against the racms because it's been causing some issues
		if ( applicationId != null ) {
			paramValue = KNSServiceLocatorWeb.getRiceApplicationConfigurationMediationService().getConfigurationParameter(applicationId, propertyName);
		}
		
		// check system parameters first
		if ( paramValue == null ) {
			paramValue = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.ALL_DETAIL_TYPE, propertyName);
		}
		if (paramValue == null) {
			paramValue = ConfigContext.getCurrentContextConfig().getProperty(propertyName);
		}
		return paramValue;
	}

}
