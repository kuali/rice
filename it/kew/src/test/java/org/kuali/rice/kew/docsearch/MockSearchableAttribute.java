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

import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;

import javax.jws.WebParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class MockSearchableAttribute implements SearchableAttributeOld {

    public static final String SEARCH_CONTENT = "<mockContent>MockSearchableAttribute Search Content</mockContent>";

    // TODO - uncomment below and re-implement once this is switched over to implement SearchableAttribute instead of SearchableAttributeOld!
//    @Override
//    public String generateSearchContent(String documentTypeName, WorkflowAttributeDefinition attributeDefinition) {
//        return SEARCH_CONTENT;
//    }
//
//    @Override
//    public List<DocumentAttribute<?>> getDocumentAttributes(DocumentSearchContext documentSearchContext) {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public List<RemotableAttributeField> getAttributeFields(DocumentSearchContext documentSearchContext) {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public List<org.kuali.rice.kew.api.document.attribute.WorkflowAttributeValidationError> validateSearchParameters(
//            Map<String, String> parameters,
//            DocumentSearchContext searchContext) {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }

    public String getSearchContent(DocumentSearchContext documentSearchContext) {
        return SEARCH_CONTENT;
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
        List<Field> fields = new ArrayList<Field>();
        Field myField = new Field("MockSearchableAttributeKey","title");
        myField.setFieldDataType((new SearchableAttributeStringValue()).getAttributeDataType());
        fields.add(myField);
        Row row = new Row(fields);
        List<Row> rows = new ArrayList<Row>();
        rows.add(row);
        return rows;
    }

    public List<WorkflowAttributeValidationError> validateUserSearchInputs(Map<Object, Object> paramMap, DocumentSearchContext documentSearchContext) {
        return new ArrayList<WorkflowAttributeValidationError>(0);
    }
}
