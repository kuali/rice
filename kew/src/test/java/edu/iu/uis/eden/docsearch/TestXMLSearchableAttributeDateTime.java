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
package edu.iu.uis.eden.docsearch;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.routetemplate.WorkflowAttributeValidationError;

public class TestXMLSearchableAttributeDateTime implements SearchableAttribute {

    private static final long serialVersionUID = 1479059967548234181L;

    public static final String SEARCH_STORAGE_KEY = "testDateTimeKey";
    public static final Long SEARCH_STORAGE_VALUE_IN_MILLS = (new Long("1173995646535"));
    public static final Timestamp SEARCH_STORAGE_VALUE = new Timestamp(SEARCH_STORAGE_VALUE_IN_MILLS.longValue());

    public String getSearchContent() {
		return "TestXMLSearchableAttributeDateTime";
	}

	public List<SearchableAttributeValue> getSearchStorageValues(String docContent) {
		List<SearchableAttributeValue> savs = new ArrayList<SearchableAttributeValue>();
        SearchableAttributeDateTimeValue sav = new SearchableAttributeDateTimeValue();
        sav.setSearchableAttributeKey(SEARCH_STORAGE_KEY);
        sav.setSearchableAttributeValue(SEARCH_STORAGE_VALUE);
		savs.add(sav);
		return savs;
	}

	public List<Row> getSearchingRows() {
		List fields = new ArrayList();
		Field myField = new Field("title", "", "", false, SEARCH_STORAGE_KEY, "", null, "");
		myField.setColumnVisible(true);
		myField.setFieldDataType((new SearchableAttributeDateTimeValue()).getAttributeDataType());
		fields.add(myField);
		Row row = new Row(fields);
		List<Row> rows = new ArrayList<Row>();
		rows.add(row);
		return rows;
	}

	public List<WorkflowAttributeValidationError> validateUserSearchInputs(Map<Object, String> paramMap) {
		List<WorkflowAttributeValidationError> waves = new ArrayList<WorkflowAttributeValidationError>();
//		WorkflowAttributeValidationError wave = new WorkflowAttributeValidationError("key1", "message1");
//		waves.add(wave);
		return waves;
	}
}
