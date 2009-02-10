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
package org.kuali.rice.kew.role;

import java.util.List;

import org.kuali.rice.kim.bo.role.dto.DelegateInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.KimDelegationTypeService;
import org.kuali.rice.kim.service.support.impl.KimTypeServiceBase;

public class KimDelegationTypeServiceBase extends KimTypeServiceBase implements KimDelegationTypeService {

	public AttributeSet convertQualificationAttributesToRequired(
			AttributeSet qualificationAttributes) {
		return qualificationAttributes;
	}

	public List<DelegateInfo> doDelegationQualifiersMatchQualification(
			AttributeSet qualification,
			List<DelegateInfo> delegationMemberList) {
		throw new UnsupportedOperationException("This method shouldn't be called anywhere.");
	}

	public boolean doesDelegationQualifierMatchQualification(
			AttributeSet qualification, AttributeSet delegationQualifier) {
		boolean matches = true;
		for (String qualificationKey : qualification.keySet()) {
			if (delegationQualifier.containsKey(qualificationKey)) {
				matches = matches && qualification.get(qualificationKey).equals(delegationQualifier.get(qualificationKey));
			}
		}
		return matches;
	}
	
}