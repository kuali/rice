/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.impl.responsibility;

import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.framework.responsibility.ResponsibilityTypeService;
import org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimResponsibilityTypeServiceBase extends DataDictionaryTypeServiceBase
		implements ResponsibilityTypeService {

	@Override
	public final List<Responsibility> getMatchingResponsibilities( Map<String, String> requestedDetails, List<Responsibility> responsibilitiesList ) {
		requestedDetails = translateInputAttributes(requestedDetails);
		validateRequiredAttributesAgainstReceived(requestedDetails);
		return performResponsibilityMatches(requestedDetails, responsibilitiesList);
	}

	/**
	 * Internal method for matching Responsibilities.  Override this method to customize the matching behavior.
	 * 
	 * This base implementation uses the {@link #performMatch(Map<String, String>, Map<String, String>)} method
	 * to perform an exact match on the Responsibility details and return all that are equal.
	 */
	protected List<Responsibility> performResponsibilityMatches(Map<String, String> requestedDetails, List<Responsibility> responsibilitiesList) {
		List<Responsibility> matchingResponsibilities = new ArrayList<Responsibility>();
		for (Responsibility responsibility : responsibilitiesList) {
			if ( performMatch(requestedDetails, responsibility.getAttributes())) {
				matchingResponsibilities.add( responsibility );
			}
		}
		return matchingResponsibilities;
	}
}
