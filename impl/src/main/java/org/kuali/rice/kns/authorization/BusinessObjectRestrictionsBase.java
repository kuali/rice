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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kns.datadictionary.mask.MaskFormatter;
import org.kuali.rice.kns.web.ui.Field;

public class BusinessObjectRestrictionsBase implements
		BusinessObjectRestrictions {
	private Map<String, MaskFormatter> partiallyMaskedFields;
	private Map<String, MaskFormatter> fullyMaskedFields;

	protected Set<String> allRestrictedFields;

	public BusinessObjectRestrictionsBase() {
		clearAllRestrictions();
	}

	public Set<String> getRestrictedFieldNames() {
		if (allRestrictedFields == null) {
			allRestrictedFields = new HashSet<String>();
			allRestrictedFields.addAll(partiallyMaskedFields.keySet());
			allRestrictedFields.addAll(fullyMaskedFields.keySet());
		}
		return allRestrictedFields;
	}

	public boolean hasAnyFieldRestrictions() {
		return !getRestrictedFieldNames().isEmpty();
	}

	public boolean hasRestriction(String fieldName) {
		return getRestrictedFieldNames().contains(fieldName);
	}

	public void addFullyMaskedField(String fieldName,
			MaskFormatter maskFormatter) {
		fullyMaskedFields.put(fieldName, maskFormatter);
	}

	public void addPartiallyMaskedField(String fieldName,
			MaskFormatter maskFormatter) {
		partiallyMaskedFields.put(fieldName, maskFormatter);
	}

	/**
	 * 
	 * This method returns the authorization setting for the given field name.
	 * If the field name is not restricted in any way, a default full-editable
	 * value is returned.
	 * 
	 * @param fieldName
	 *            - name of field to get authorization restrictions for.
	 * @return a populated FieldAuthorization class for this field
	 * 
	 */
	public FieldRestriction getFieldRestriction(String fieldName) {
		if (hasRestriction(fieldName)) {
			FieldRestriction fieldRestriction = null;
			if (partiallyMaskedFields.containsKey(fieldName)) {
				fieldRestriction = new FieldRestriction(fieldName,
						Field.PARTIALLY_MASKED);
				fieldRestriction.setMaskFormatter(partiallyMaskedFields
						.get(fieldName));
			}
			if (fullyMaskedFields.containsKey(fieldName)) {
				fieldRestriction = new FieldRestriction(fieldName, Field.MASKED);
				fieldRestriction.setMaskFormatter(fullyMaskedFields
						.get(fieldName));
			}
			return fieldRestriction;
		} else {
			return new FieldRestriction(fieldName, Field.EDITABLE);
		}
	}

	public void clearAllRestrictions() {
		partiallyMaskedFields = new HashMap<String, MaskFormatter>();
		fullyMaskedFields = new HashMap<String, MaskFormatter>();
		allRestrictedFields = null;
	}
}
