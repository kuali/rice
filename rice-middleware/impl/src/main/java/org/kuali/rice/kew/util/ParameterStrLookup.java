/**
 * Copyright 2005-2014 The Kuali Foundation
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
import org.kuali.rice.core.api.config.property.ConfigStrLookup;
import org.kuali.rice.coreservice.api.CoreServiceApiServiceLocator;
import org.kuali.rice.coreservice.api.parameter.ParameterKey;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Uses the KEW runtime parameters to locate a string for replacement, falling back to the deploy time configuration
 * variables if necessary.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ParameterStrLookup extends ConfigStrLookup {
	
	private final String applicationId;

    /**
     * Creates a string locator to search for KEW runtime parameters for any {@code applicationId}.
     */
	public ParameterStrLookup() {
        this(null);
	}

    /**
     * Creates a string locator to search for KEW runtime parameters for a specific {@code applicationId}.
     *
     * @param applicationId the application to search for the KEW runtime parameter in.
     */
	public ParameterStrLookup(String applicationId) {
		this.applicationId = applicationId;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public String lookup(String propertyName) {
        if (StringUtils.isBlank(propertyName)) {
            return null;
        }

        String paramValue;

        // check runtime parameters first
        if (StringUtils.isBlank(applicationId)) {
            paramValue = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(
                    KewApiConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, propertyName);
        } else {
            ParameterKey parameterKey = ParameterKey.create(applicationId, KewApiConstants.KEW_NAMESPACE,
                    KRADConstants.DetailTypes.ALL_DETAIL_TYPE, propertyName);
            paramValue = CoreServiceApiServiceLocator.getParameterRepositoryService().getParameterValueAsString(parameterKey);
        }

        // fall back to deploy time configurations if empty
        if (paramValue == null) {
            paramValue = super.lookup(propertyName);
        }

        return paramValue;
    }

}