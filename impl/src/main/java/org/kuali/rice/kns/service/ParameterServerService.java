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
package org.kuali.rice.kns.service;

import java.util.List;

import org.kuali.rice.kns.bo.ParameterDetailType;

/**
 * An interface for the ParameterService that lives on the Rice Standalone server.
 * 
 * The additional method here is getNonDatabaseComponents which is returns a List of
 * the non-database components from Rice and all Rice client applications that publish
 * their own non-database components.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ParameterServerService extends ParameterService {

	/**
	 * Returns a List of all ParameterDetailType objects that don't come from the database.
	 * The implementation of this method should also include these components from Rice
	 * client applications that publish their own custom components.
	 */
	public List<ParameterDetailType> getNonDatabaseComponents();
	
}
