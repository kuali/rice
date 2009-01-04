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
package org.kuali.rice.kns.document.authorization;

import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.kns.authorization.FieldRestriction;
import org.kuali.rice.kns.authorization.InquiryOrMaintenanceDocumentRestrictionsBase;
import org.kuali.rice.kns.web.ui.Field;

public class MaintenanceDocumentRestrictionsBase extends
		InquiryOrMaintenanceDocumentRestrictionsBase implements
		MaintenanceDocumentRestrictions {
	private Set<String> readOnlyFields;
	private Set<String> readOnlySectionIds;

	@Override
	public Set<String> getRestrictedFieldNames() {
		if (allRestrictedFields == null) {
			Set<String> allRestrictedFields = super.getRestrictedFieldNames();
			allRestrictedFields.addAll(readOnlyFields);
		}
		return allRestrictedFields;
	}

	public void addReadOnlyField(String fieldName) {
		readOnlyFields.add(fieldName);
	}

	public void addReadOnlySectionId(String sectionId) {
		readOnlySectionIds.add(sectionId);
	}

	public Set<String> getReadOnlySectionIds() {
		return readOnlySectionIds;
	}

	@Override
	public FieldRestriction getFieldRestriction(String fieldName) {
		FieldRestriction fieldRestriction = super
				.getFieldRestriction(fieldName);
		if (Field.EDITABLE
				.equals(fieldRestriction.getKualiFieldDisplayFlag())
				&& readOnlyFields.contains(fieldName)) {
			fieldRestriction = new FieldRestriction(fieldName,
					Field.READONLY);
		}
		return fieldRestriction;
	}

	@Override
	public void clearAllRestrictions() {
		super.clearAllRestrictions();
		readOnlyFields = new HashSet<String>();
		readOnlySectionIds = new HashSet<String>();
	}
}
