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
package org.kuali.rice.kns.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimCommonUtils;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class NamespaceRoleTypeServiceImpl extends KimRoleTypeServiceBase {

	protected List<String> requiredAttributes = new ArrayList<String>();
	{
		requiredAttributes.add(KimAttributes.NAMESPACE_CODE);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#performRoleQualifierQualificationMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	public boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
		validateRequiredAttributesAgainstReceived(requiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
		validateRequiredAttributesAgainstReceived(requiredAttributes, roleQualifier, ROLE_QUALIFIERS_RECEIVED_ATTIBUTES_NAME);

		//Create a role type that checks the namespace code. In the namespaceCode attribute, wildcards are allowed ("*")
		//In this case we DO want partial value matching (as in KFS-* should match all namespaces which begin with KFS.)
		//Assuming that a namespace can contain digits (0-9), alphabets (a-z and A-Z), -, _ and $.
		return KimCommonUtils.matchInputWithWildcard(
				qualification.get(KimAttributes.NAMESPACE_CODE), 
				roleQualifier.get(KimAttributes.NAMESPACE_CODE));
	}

}
