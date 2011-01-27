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
package org.kuali.rice.kns.uif.container;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kns.uif.field.AttributeField;
import org.kuali.rice.kns.uif.field.Field;
import org.kuali.rice.kns.uif.field.GroupField;

/**
 * Holds field indexes of a <code>View</code> instance for retrieval
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewIndex implements Serializable {
	private Set<AttributeField> attributeFields;
	private Map<String, AttributeField> attributeFieldIndex;

	public ViewIndex() {
		attributeFields = new HashSet<AttributeField>();
	}

	/**
	 * Creates the field indexes based on the currently held fields
	 * 
	 * <p>
	 * <code>AttributeField</code> instances are indexed by the attribute path.
	 * This is useful for retrieving the AttributeField based on the incoming
	 * request parameter
	 * </p>
	 */
	public void index() {
		attributeFieldIndex = new HashMap<String, AttributeField>();
		for (AttributeField field : attributeFields) {
			attributeFieldIndex.put(field.getBindingInfo().getBindingPath(), field);
		}
	}

	/**
	 * Retrieves a <code>AttributeField</code> instance from the index
	 * 
	 * @param attributePath
	 *            - full path of the attribute (from the form)
	 * @return AttributeField instance for the path or Null if not found
	 */
	public AttributeField getAttributeFieldByPath(String attributePath) {
		if (attributeFieldIndex.containsKey(attributePath)) {
			return attributeFieldIndex.get(attributePath);
		}

		return null;
	}

	public Set<AttributeField> getAttributeFields() {
		return this.attributeFields;
	}

	public void setAttributeFields(Set<AttributeField> attributeFields) {
		this.attributeFields = attributeFields;
	}

	public void addAttributeField(AttributeField field) {
		attributeFields.add(field);
	}

	public void addFields(List<Field> fields) {
		for (Field field : fields) {
			if (field instanceof AttributeField) {
				attributeFields.add((AttributeField) field);
			}
			else if (field instanceof GroupField) {
				addFields((List<Field>) ((GroupField) field).getItems());
			}
		}
	}

	public Map<String, AttributeField> getAttributeFieldIndex() {
		return this.attributeFieldIndex;
	}

	public void setAttributeFieldIndex(Map<String, AttributeField> attributeFieldIndex) {
		this.attributeFieldIndex = attributeFieldIndex;
	}

}
