/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.bo.role.dto.DelegateInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.KimRoleTypeService;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimDelegationTypeServiceBase extends KimTypeServiceBase {
	/**
	 * Performs a simple check that the qualifier on the role matches the qualification.
	 * Extra qualification attributes are ignored.
	 * 
	 * @see KimRoleTypeService#doesRoleQualifierMatchQualification(AttributeSet, AttributeSet)
	 */
	public boolean doesDelegationQualifierMatchQualification(AttributeSet qualification, AttributeSet roleQualifier) {
		return performMatch(translateInputAttributeSet(qualification), roleQualifier);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#doRoleQualifiersMatchQualification(AttributeSet, List)
	 */
	public List<DelegateInfo> doDelegationQualifiersMatchQualification(AttributeSet qualification, List<DelegateInfo> delegationMemberList) {
		AttributeSet translatedQualification = translateInputAttributeSet(qualification);
		List<DelegateInfo> matchingMemberships = new ArrayList<DelegateInfo>();
		for ( DelegateInfo dmi : delegationMemberList ) {
			if ( performMatch( translatedQualification, dmi.getQualifier() ) ) {
				matchingMemberships.add( dmi );
			}
		}
		return matchingMemberships;
	}

	/**
	 * No conversion performed.  Simply returns the passed in Map.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#convertQualificationAttributesToRequired(AttributeSet)
	 */
	public AttributeSet convertQualificationAttributesToRequired(
			AttributeSet qualificationAttributes) {
		return qualificationAttributes;
	}
	
}
