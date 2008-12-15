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
package org.kuali.rice.kew.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimResponsibilityTypeServiceBase;
import org.kuali.rice.kim.util.KimCommonUtils;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ResponsibilityResponsibilityTypeServiceImpl extends KimResponsibilityTypeServiceBase {

	protected List<String> requiredAttributes = new ArrayList<String>();
	{
		requiredAttributes.add(KimAttributes.DOCUMENT_TYPE_NAME);
		requiredAttributes.add(KimAttributes.ROUTE_NODE_NAME);
		// NOTE: The following below is comment per comments on KFSMI-1996
		//requiredAttributes.add(KimAttributes.REQUIRED);
		//requiredAttributes.add(KimAttributes.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	protected boolean performMatch(AttributeSet inputAttributeSet, AttributeSet storedAttributeSet) {
		validateRequiredAttributesAgainstReceived(requiredAttributes, inputAttributeSet, REQUESTED_DETAILS_RECEIVED_ATTIBUTES_NAME);
		validateRequiredAttributesAgainstReceived(requiredAttributes, storedAttributeSet, STORED_DETAILS_RECEIVED_ATTIBUTES_NAME);
		
		if (!super.performMatch(inputAttributeSet, storedAttributeSet)) {
			return false;
		} 
		
		DocumentType currentDocType = KEWServiceLocator.getDocumentTypeService().findByName(inputAttributeSet.get(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL));
		boolean match = KimCommonUtils.checkPermissionDetailMatch(currentDocType, storedAttributeSet);
		match &= inputAttributeSet.get(KimAttributes.DOCUMENT_TYPE_NAME).equals(storedAttributeSet.get(KimAttributes.DOCUMENT_TYPE_NAME));
		match &= inputAttributeSet.get(KimAttributes.ROUTE_NODE_NAME).equals(storedAttributeSet.get(KimAttributes.ROUTE_NODE_NAME));
		//match &= inputAttributeSet.get(KimAttributes.REQUIRED).equals(storedAttributeSet.get(KimAttributes.REQUIRED));
		//match &= inputAttributeSet.get(KimAttributes.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL).equals(storedAttributeSet.get(KimAttributes.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL));
		return match;
	}

}
