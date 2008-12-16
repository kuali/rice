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
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase;
import org.kuali.rice.kim.util.KimCommonUtils;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NamespacePermissionTypeServiceImpl extends KimPermissionTypeServiceBase {

	protected List<String> inputRequiredAttributes = new ArrayList<String>();
	protected List<String> storedRequiredAttributes = new ArrayList<String>();
	{
		storedRequiredAttributes.add(KimAttributes.NAMESPACE_CODE);
		inputRequiredAttributes.add(KimAttributes.NAMESPACE_CODE);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	protected boolean performMatch(AttributeSet inputAttributeSet, AttributeSet storedAttributeSet) {
		validateRequiredAttributesAgainstReceived(inputRequiredAttributes, inputAttributeSet, REQUESTED_DETAILS_RECEIVED_ATTIBUTES_NAME);
		validateRequiredAttributesAgainstReceived(storedRequiredAttributes, storedAttributeSet, STORED_DETAILS_RECEIVED_ATTIBUTES_NAME);

		return KimCommonUtils.matchInputWithWildcard(inputAttributeSet.get(KimAttributes.NAMESPACE_CODE), storedAttributeSet.get(KimAttributes.NAMESPACE_CODE));
	}
	
}
