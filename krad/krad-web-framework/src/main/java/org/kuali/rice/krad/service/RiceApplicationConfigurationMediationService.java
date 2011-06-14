/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.krad.service;

import org.kuali.rice.core.api.component.Component;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;

import java.util.List;

/**
 * This service handles mediation between all RiceApplicationConfiguration services that are
 * deployed in all Rice client applications on the bus.  It's responsible for identifying
 * which RiceApplicationConfiguration services to contact and dispatching service calls
 * to them to properly implement the methods on the service.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface RiceApplicationConfigurationMediationService {

	/**
	 * Returns the configuration parameter value for the given applicationId and parameterName.
	 * The applicationId is used to determine which RiceApplicationConfigurationService to
	 * call by looking up the applicationNamespaceCode associated with it's applicationId.
	 * 
	 * @param applicationId
	 * @param parameterName
	 * @return
	 */
	String getConfigurationParameter(String applicationId, String parameterName);

	/**
	 * Returns the non-database components that have been published by all Rice client applications.
	 */
	List<Component> getNonDatabaseComponents();
    
    /**
	 * This method will return the attribute definition from the local data dictionary of the
	 * application which supports that business object.  If not present in any application
	 * attached to the bus, this method will return null.
	 * 
	 * This method also returns null if the given attribute definition can not be found on the business object.
     */
    AttributeDefinition getBusinessObjectAttributeDefinition( String businessObjectClassName, String attributeName );
    
    String getBaseLookupUrl( String businessObjectClassName );
    String getBaseInquiryUrl( String businessObjectClassName );
	String getBaseHelpUrl( String businessObjectClassName );
}
