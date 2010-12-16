/*
 * Copyright 2005-2008 The Kuali Foundation
 *
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.docsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;


public class TestXMLSearchableAttributeString implements SearchableAttribute {

    private static final long serialVersionUID = 7069672825797402460L;

    public static final String SEARCH_STORAGE_KEY = "givenname";
    public static final String SEARCH_STORAGE_VALUE = "jack";

    public String getSearchContent(DocumentSearchContext documentSearchContext) {
		return "TestXMLSearchableAttributeString";
	}

	public List<SearchableAttributeValue> getSearchStorageValues(DocumentSearchContext documentSearchContext) {
		List<SearchableAttributeValue> savs = new ArrayList<SearchableAttributeValue>();
        SearchableAttributeStringValue sav = new SearchableAttributeStringValue();
        sav.setSearchableAttributeKey(SEARCH_STORAGE_KEY);
        sav.setSearchableAttributeValue(SEARCH_STORAGE_VALUE);
		savs.add(sav);
		return savs;
	}

	public List<Row> getSearchingRows(DocumentSearchContext documentSearchContext) {
		List fields = new ArrayList();
		Field myField = new Field(SEARCH_STORAGE_KEY, "title");
		myField.setFieldDataType((new SearchableAttributeStringValue()).getAttributeDataType());
		fields.add(myField);
		Row row = new Row(fields);
		List<Row> rows = new ArrayList<Row>();
		rows.add(row);
		return rows;
	}

	public List<WorkflowAttributeValidationError> validateUserSearchInputs(Map<Object, Object> paramMap, DocumentSearchContext documentSearchContext) {
		List<WorkflowAttributeValidationError> waves = new ArrayList<WorkflowAttributeValidationError>();
//		WorkflowAttributeValidationError wave = new WorkflowAttributeValidationError("key1", "message1");
//		waves.add(wave);
		return waves;
	}
}
