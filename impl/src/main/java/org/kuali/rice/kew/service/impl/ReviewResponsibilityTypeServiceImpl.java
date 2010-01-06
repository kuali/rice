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
package org.kuali.rice.kew.service.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ReviewResponsibilityTypeServiceImpl extends DocumentTypeResponsibilityTypeServiceImpl {
	{
		exactMatchStringAttributeName = KimAttributes.ROUTE_NODE_NAME;
		requiredAttributes.add( KimAttributes.ROUTE_NODE_NAME );
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.service.impl.DocumentTypeResponsibilityTypeServiceImpl#performResponsibilityMatches(org.kuali.rice.kim.bo.types.dto.AttributeSet, java.util.List)
	 */
	@Override
	protected List<KimResponsibilityInfo> performResponsibilityMatches(
			AttributeSet requestedDetails,
			List<KimResponsibilityInfo> responsibilitiesList) {
		// get the base responsibility matches based on the route level and document type
		List<KimResponsibilityInfo> baseMatches = super.performResponsibilityMatches(requestedDetails,
				responsibilitiesList);
		// now, if any of the responsibilities have the "qualifierResolverProvidedIdentifier" detail property
		// perform an exact match on the property with the requested details
		// if the property does not match or does not exist in the requestedDetails, remove
		// the responsibility from the list
		Iterator<KimResponsibilityInfo> respIter = baseMatches.iterator();
		while ( respIter.hasNext() ) {
			AttributeSet respDetails = respIter.next().getDetails();
			if ( respDetails.containsKey( KimAttributes.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER ) ) {
				if ( !requestedDetails.containsKey( KimAttributes.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER ) 
						|| !StringUtils.equals( respDetails.get(KimAttributes.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER)
								, requestedDetails.get(KimAttributes.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER))) {
					respIter.remove();
				}
			}
		}		
		return baseMatches;
	}
}
