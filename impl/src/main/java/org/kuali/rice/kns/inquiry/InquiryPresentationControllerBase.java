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
package org.kuali.rice.kns.inquiry;

import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.kns.bo.BusinessObject;

public class InquiryPresentationControllerBase implements
		InquiryPresentationController {

	/**
	 * @see org.kuali.rice.kns.inquiry.InquiryPresentationController#addInquiryRestrictions(org.kuali.rice.kns.inquiry.InquiryAuthorizations, org.kuali.rice.kns.bo.BusinessObject)
	 */
	public final void addInquiryRestrictions(InquiryAuthorizations auths, BusinessObject businessObject) {
		for (String propertyName : getConditionallyHiddenPropertyNames(businessObject)) {
			auths.addHiddenAuthField(propertyName);
		}
		for (String sectionId : getConditionallyHiddenSectionIds(businessObject)) {
			auths.addHiddenSectionId(sectionId);
		}
	}

	/**
	 * Implement this method to hide fields based on specific data in the record being inquired into
	 * 
	 * @return Set of property names that should be hidden
	 */
	protected Set<String> getConditionallyHiddenPropertyNames(BusinessObject businessObject) {
		return new HashSet<String>();
	}

	/**
	 * Implement this method to hide sections based on specific data in the record being inquired into
	 * 
	 * @return Set of section ids that should be hidden
	 */
	protected Set<String> getConditionallyHiddenSectionIds(BusinessObject businessObject) {
		return new HashSet<String>();
	}
}
