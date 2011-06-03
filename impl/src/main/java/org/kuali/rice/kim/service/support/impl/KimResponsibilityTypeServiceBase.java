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
package org.kuali.rice.kim.service.support.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.service.support.KimResponsibilityTypeService;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimResponsibilityTypeServiceBase extends KimTypeServiceBase
		implements KimResponsibilityTypeService {

	/**
	 * @see org.kuali.rice.kim.service.support.KimResponsibilityTypeService#getMatchingResponsibilities(AttributeSet, List)
	 */
	public final List<Responsibility> getMatchingResponsibilities( AttributeSet requestedDetails, List<Responsibility> responsibilitiesList ) {
		requestedDetails = translateInputAttributeSet(requestedDetails);
		validateRequiredAttributesAgainstReceived(requestedDetails);
		return performResponsibilityMatches(requestedDetails, responsibilitiesList);
	}

	/**
	 * Internal method for matching Responsibilities.  Override this method to customize the matching behavior.
	 * 
	 * This base implementation uses the {@link #performMatch(AttributeSet, AttributeSet)} method
	 * to perform an exact match on the Responsibility details and return all that are equal.
	 */
	protected List<Responsibility> performResponsibilityMatches(AttributeSet requestedDetails, List<Responsibility> responsibilitiesList) {
		List<Responsibility> matchingResponsibilities = new ArrayList<Responsibility>();
		for (Responsibility responsibility : responsibilitiesList) {
			if ( performMatch(requestedDetails, new AttributeSet(responsibility.getAttributes().toMap())) ) {
				matchingResponsibilities.add( responsibility );
			}
		}
		return matchingResponsibilities;
	}
}
