/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.kns.authorization;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.kns.inquiry.InquiryRestrictions;
import org.kuali.rice.kns.web.ui.Field;

public class InquiryOrMaintenanceDocumentRestrictionsBase extends
		BusinessObjectRestrictionsBase implements InquiryOrMaintenanceDocumentRestrictions, InquiryRestrictions {
	private Set<String> hiddenFields;
	private Set<String> hiddenSectionIds;

	@Override
	public Set<String> getRestrictedFieldNames() {
		if (allRestrictedFields == null) {
			Collection allRestrictedFields = super.getRestrictedFieldNames();
			allRestrictedFields.addAll(hiddenFields);
		}
		return allRestrictedFields;
	}

	public void addHiddenField(String fieldName) {
		hiddenFields.add(fieldName);
	}

	public void addHiddenSectionId(String sectionId) {
		hiddenSectionIds.add(sectionId);
	}

	public Set<String> getHiddenSectionIds() {
		return hiddenSectionIds;
	}

	@Override
	public FieldRestriction getFieldRestriction(String fieldName) {
		FieldRestriction fieldRestriction = super
				.getFieldRestriction(fieldName);
		if ((fieldRestriction == null) && hiddenFields.contains(fieldName)) {
			fieldRestriction = new FieldRestriction(fieldName, Field.HIDDEN);
		}
		return fieldRestriction;
	}

	@Override
	public void clearAllRestrictions() {
		super.clearAllRestrictions();
		hiddenFields = new HashSet<String>();
		hiddenSectionIds = new HashSet<String>();
	}
}
