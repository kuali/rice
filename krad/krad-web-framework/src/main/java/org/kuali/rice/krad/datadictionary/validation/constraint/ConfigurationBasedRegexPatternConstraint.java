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
package org.kuali.rice.krad.datadictionary.validation.constraint;

import org.kuali.rice.krad.service.KRADServiceLocator;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ConfigurationBasedRegexPatternConstraint extends ValidDataPatternConstraint {

	/**
	 * This method implementation uses the key returned by {@link #getPatternTypePropertyString()} to fetch the 
	 * validationPattern's regex string from the ConfigurationService which should not include the start(^) and end($) symbols
	 */
    protected String getRegexString() {
//        return (String) KRADServiceLocator.getKualiConfigurationService().getPropertyString("validationPatternRegex." + getPatternTypeName());
        return (String) KRADServiceLocator.getKualiConfigurationService().getPropertyString(getPatternTypeKey());
    }

}