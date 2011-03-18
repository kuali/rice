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
import org.kuali.rice.core.xml.dto.AttributeSet;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.service.support.KimResponsibilityTypeService;
import org.kuali.rice.kim.util.KimConstants;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ReviewResponsibilityTypeServiceImpl extends DocumentTypeResponsibilityTypeServiceImpl implements KimResponsibilityTypeService{
	{
		exactMatchStringAttributeName = KimConstants.AttributeConstants.ROUTE_NODE_NAME;
		requiredAttributes.add( KimConstants.AttributeConstants.ROUTE_NODE_NAME );
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.service.impl.DocumentTypeResponsibilityTypeServiceImpl#performResponsibilityMatches(org.kuali.rice.core.xml.dto.AttributeSet, java.util.List)
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
			if ( respDetails.containsKey( KimConstants.AttributeConstants.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER ) && StringUtils.isNotBlank( respDetails.get(KimConstants.AttributeConstants.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER) ) ) {
				if ( !requestedDetails.containsKey( KimConstants.AttributeConstants.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER )
						|| !StringUtils.equals( respDetails.get(KimConstants.AttributeConstants.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER)
								, requestedDetails.get(KimConstants.AttributeConstants.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER))) {
					respIter.remove();
				}
			}
		}		
		return baseMatches;
	}
}
