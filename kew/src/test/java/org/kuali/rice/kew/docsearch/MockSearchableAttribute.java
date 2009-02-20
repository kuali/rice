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
// Created on Mar 20, 2007

package org.kuali.rice.kew.docsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;


public class MockSearchableAttribute implements SearchableAttribute {

    public String getSearchContent(DocumentSearchContext documentSearchContext) {
        return "MockSearchableAttribute Search Content";
    }

    public List<SearchableAttributeValue> getSearchStorageValues(DocumentSearchContext documentSearchContext) {
        List<SearchableAttributeValue> savs = new ArrayList<SearchableAttributeValue>();
        SearchableAttributeValue sav = new SearchableAttributeStringValue();
        sav.setRouteHeader(new DocumentRouteHeaderValue());
        sav.setSearchableAttributeKey("MockSearchableAttributeKey");
        sav.setupAttributeValue("MockSearchableAttributeValue");
        savs.add(sav);
        return savs;
    }

    public List<Row> getSearchingRows(DocumentSearchContext documentSearchContext) {
        List fields = new ArrayList();
        Field myField = new Field("MockSearchableAttributeKey","title");
        myField.setFieldDataType((new SearchableAttributeStringValue()).getAttributeDataType());
        fields.add(myField);
        Row row = new Row(fields);
        List<Row> rows = new ArrayList<Row>();
        rows.add(row);
        return rows;
    }

    public List<WorkflowAttributeValidationError> validateUserSearchInputs(Map<Object, String> paramMap, DocumentSearchContext documentSearchContext) {
        return new ArrayList<WorkflowAttributeValidationError>(0);
    }
}