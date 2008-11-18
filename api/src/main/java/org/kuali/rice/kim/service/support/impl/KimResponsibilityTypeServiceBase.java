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
package org.kuali.rice.kim.service.support.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.service.support.KimResponsibilityTypeService;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimResponsibilityTypeServiceBase extends KimTypeServiceBase
		implements KimResponsibilityTypeService {

	public boolean areActionsAtAssignmentLevel() {
		// TODO really not sure what this is...
		return false;
	}

	public boolean doResponsibilityDetailsMatch(
			Map<String, String> requestedDetails,
			List<Map<String, String>> responsibilityDetailsList) {
		for (Map<String, String> responsibilityDetails : responsibilityDetailsList) {
			if (!doesResponsibilityDetailMatch(requestedDetails, responsibilityDetails)) {
				return false;
			}
		}
		return true;
	}

	public boolean doesResponsibilityDetailMatch(
			Map<String, String> requestedDetails,
			Map<String, String> responsibilityDetails) {
		for (String requestedDetailKey : requestedDetails.keySet()) {
			String requestedDetailValue = requestedDetails.get(requestedDetailKey);
			if (!responsibilityDetails.containsKey(requestedDetailKey)) {
				return false;
			}
			String responsibilityDetailValue = responsibilityDetails.get(requestedDetailKey);
			if (!StringUtils.equals(responsibilityDetailValue, requestedDetailValue)) {
				return false;
			}
		}
		return true;
	}

	public List<Map<String, String>> getAllImpliedDetails(
			Map<String, String> requestedDetails) {
		// TODO not sure what this is...
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.KimResponsibilityTypeService#getAllImplyingDetails(java.util.Map)
	 */
	public List<Map<String, String>> getAllImplyingDetails(
			Map<String, String> requestedDetails) {
		// TODO - not sure what this is...
		return null;
	}

}
