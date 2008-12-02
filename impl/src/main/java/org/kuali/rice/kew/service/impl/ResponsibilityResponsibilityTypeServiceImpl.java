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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimResponsibilityTypeServiceBase;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ResponsibilityResponsibilityTypeServiceImpl extends KimResponsibilityTypeServiceBase {

	/**
	 * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	protected boolean performMatch(AttributeSet inputAttributeSet, AttributeSet storedAttributeSet) {
		if (!super.performMatch(inputAttributeSet, storedAttributeSet)) {
			return false;
		} 
		// NOTE: The following below is comment per comments on KFSMI-1996
		if (StringUtils.isEmpty(inputAttributeSet.get(KimConstants.KIM_ATTRIB_DOCUMENT_TYPE_NAME))
				|| StringUtils.isEmpty(inputAttributeSet.get(KimConstants.KIM_ATTRIB_ROUTE_NODE_NAME))
				//|| StringUtils.isEmpty(inputAttributeSet.get(KimConstants.KIM_ATTRIB_REQUIRED))
				//|| StringUtils.isEmpty(inputAttributeSet.get(KimConstants.KIM_ATTRIB_ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL))
			) {
        	throw new RuntimeException(
        			KimConstants.KIM_ATTRIB_DOCUMENT_TYPE_NAME 
        			+ ", " + KimConstants.KIM_ATTRIB_ROUTE_NODE_NAME 
        			//+ ", " + KimConstants.KIM_ATTRIB_REQUIRED 
        			//+ " and " + KimConstants.KIM_ATTRIB_ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL + " should not be blank or null."
        			);
		}
		
		DocumentType currentDocType = KEWServiceLocator.getDocumentTypeService().findByName(inputAttributeSet.get(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL));
		boolean match = KimCommonUtils.checkPermissionDetailMatch(currentDocType, storedAttributeSet);
		match &= inputAttributeSet.get(KimConstants.KIM_ATTRIB_DOCUMENT_TYPE_NAME).equals(storedAttributeSet.get(KimConstants.KIM_ATTRIB_DOCUMENT_TYPE_NAME));
		match &= inputAttributeSet.get(KimConstants.KIM_ATTRIB_ROUTE_NODE_NAME).equals(storedAttributeSet.get(KimConstants.KIM_ATTRIB_ROUTE_NODE_NAME));
		//match &= inputAttributeSet.get(KimConstants.KIM_ATTRIB_REQUIRED).equals(storedAttributeSet.get(KimConstants.KIM_ATTRIB_REQUIRED));
		//match &= inputAttributeSet.get(KimConstants.KIM_ATTRIB_ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL).equals(storedAttributeSet.get(KimConstants.KIM_ATTRIB_ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL));
		return match;
	}

}
