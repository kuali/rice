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
package org.kuali.rice.kns.service;

import java.util.List;

import org.kuali.rice.kns.bo.ParameterDetailType;

/**
 * This service handles mediation between all RiceApplicationConfiguration services that are
 * deployed in all Rice client applications on the bus.  It's responsible for identifying
 * which RiceApplicationConfiguration services to contact and dispatching service calls
 * to them to properly implement the methods on the service.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface RiceApplicationConfigurationMediationService {

	/**
	 * Returns the configuration parameter value for the given namespaceCode and parameterName.
	 * The namespaceCode is used to determine which RiceApplicationConfigurationService to
	 * call by looking up the applicationNamespaceCode associated with it's namespace.
	 * 
	 * @param namespace
	 * @param parameterName
	 * @return
	 */
	public String getConfigurationParameter(String namespaceCode, String parameterName);

	/**
	 * Returns the non-database components that have been published by all Rice client applications.
	 */
	public List<ParameterDetailType> getNonDatabaseComponents();

}