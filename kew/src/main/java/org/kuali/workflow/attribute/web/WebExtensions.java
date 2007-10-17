/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package org.kuali.workflow.attribute.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.workflow.attribute.ExtensionAttribute;

import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;

/**
 * A convienance bean for use in the web tier when collecting data from custom field extensions.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WebExtensions {

	private List<List<Row>> rows = new ArrayList<List<Row>>();
	private Map<String, String> data = new HashMap<String, String>();

	public List<List<Row>> getRows() {
		return rows;
	}

	public void setRows(List<List<Row>> rows) {
		this.rows = rows;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public void load(ExtensionAttribute attribute) {
		List<Row> attributeRows = attribute.getRows();
		rows.add(attributeRows);
		for (Row row : attributeRows) {
			for (Field field : row.getFields()) {
				if (field.isInputField() && !data.containsKey(field.getPropertyName())) {
					data.put(field.getPropertyName(), field.getPropertyValue());
				}
			}
		}
	}

	public void clear() {
		clearRows();
		data.clear();
	}

	public void clearRows() {
		rows.clear();
	}

	public boolean isEmptyExtensions() {
		return rows.isEmpty() && data.isEmpty();
	}

}
