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

import org.kuali.rice.core.xml.dto.AttributeSet;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
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
	public final List<KimResponsibilityInfo> getMatchingResponsibilities( AttributeSet requestedDetails, List<KimResponsibilityInfo> responsibilitiesList ) {
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
	protected List<KimResponsibilityInfo> performResponsibilityMatches(AttributeSet requestedDetails, List<KimResponsibilityInfo> responsibilitiesList) {
		List<KimResponsibilityInfo> matchingResponsibilities = new ArrayList<KimResponsibilityInfo>();
		for (KimResponsibilityInfo Responsibility : responsibilitiesList) {
			if ( performMatch(requestedDetails, Responsibility.getDetails()) ) {
				matchingResponsibilities.add( Responsibility );
			}
		}
		return matchingResponsibilities;
	}
}
