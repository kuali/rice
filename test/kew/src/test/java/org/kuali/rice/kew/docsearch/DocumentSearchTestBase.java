/*
 * Copyright 2007-2008 The Kuali Foundation
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

import java.util.Arrays;

import org.junit.Ignore;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.docsearch.xml.StandardGenericXMLSearchableAttribute;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;


/**
 * This is a base class used for document search unit test classes to consolidate some helper methods
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Ignore("This class is a helper class only with no specific tests within")
public class DocumentSearchTestBase extends KEWTestCase {

    /**
     * This method is used by tests that use xml attributes
     *
     * @param name - name of the attribute to retrieve
     * @return
     */
    protected StandardGenericXMLSearchableAttribute getAttribute(String name) {
        String attName = name;
        if (attName == null) {
            attName = "XMLSearchableAttribute";
        }
        RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName(attName);
        StandardGenericXMLSearchableAttribute attribute = new StandardGenericXMLSearchableAttribute();
        attribute.setRuleAttribute(ruleAttribute);
        return attribute;
    }

    protected SearchAttributeCriteriaComponent createSearchAttributeCriteriaComponent(String key,String value,DocumentType docType) {
        return createSearchAttributeCriteriaComponent(key, value, null, docType);
    }
    
    protected SearchAttributeCriteriaComponent createSearchAttributeCriteriaComponent(String key,String[] values,DocumentType docType) {
        return createSearchAttributeCriteriaComponent(key, values, null, docType);
    }

    protected SearchAttributeCriteriaComponent createSearchAttributeCriteriaComponent(String key,String value,Boolean isLowerBoundValue,DocumentType docType) {
        String formKey = (isLowerBoundValue == null) ? key : ((isLowerBoundValue != null && isLowerBoundValue.booleanValue()) ? KEWConstants.SearchableAttributeConstants.RANGE_LOWER_BOUND_PROPERTY_PREFIX + key : KEWConstants.SearchableAttributeConstants.RANGE_UPPER_BOUND_PROPERTY_PREFIX + key);
        String savedKey = key;
        SearchAttributeCriteriaComponent sacc = new SearchAttributeCriteriaComponent(formKey,value,savedKey);
        Field field = getFieldByFormKey(docType, formKey);
        if (field != null) {
            sacc.setSearchableAttributeValue(DocSearchUtils.getSearchableAttributeValueByDataTypeString(field.getFieldDataType()));
            sacc.setRangeSearch(field.isMemberOfRange());
            sacc.setCaseSensitive(!field.isUpperCase());
            sacc.setSearchInclusive(field.isInclusive());
            sacc.setSearchable(field.isIndexedForSearch());
            sacc.setCanHoldMultipleValues(Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType()));
        }
        return sacc;
    }
    
    protected SearchAttributeCriteriaComponent createSearchAttributeCriteriaComponent(String key,String[] values,Boolean isLowerBoundValue,DocumentType docType) {
        String formKey = (isLowerBoundValue == null) ? key : ((isLowerBoundValue != null && isLowerBoundValue.booleanValue()) ? KEWConstants.SearchableAttributeConstants.RANGE_LOWER_BOUND_PROPERTY_PREFIX + key : KEWConstants.SearchableAttributeConstants.RANGE_UPPER_BOUND_PROPERTY_PREFIX + key);
        String savedKey = key;
        SearchAttributeCriteriaComponent sacc = new SearchAttributeCriteriaComponent(formKey,null,savedKey);
        sacc.setValues(Arrays.asList(values));
        Field field = getFieldByFormKey(docType, formKey);
        if (field != null) {
            sacc.setSearchableAttributeValue(DocSearchUtils.getSearchableAttributeValueByDataTypeString(field.getFieldDataType()));
            sacc.setRangeSearch(field.isMemberOfRange());
            sacc.setCaseSensitive(!field.isUpperCase());
            sacc.setSearchInclusive(field.isInclusive());
            sacc.setSearchable(field.isIndexedForSearch());
            sacc.setCanHoldMultipleValues(Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType()));
        }
        return sacc;
    }

    private Field getFieldByFormKey(DocumentType docType, String formKey) {
        if (docType == null) {
            return null;
        }
        for (SearchableAttribute searchableAttribute : docType.getSearchableAttributes()) {
            for (Row row : searchableAttribute.getSearchingRows(DocSearchUtils.getDocumentSearchContext("", docType.getName(), ""))) {
                for (org.kuali.rice.kns.web.ui.Field field : row.getFields()) {
                    if (field instanceof Field) {
                        if (field.getPropertyName().equals(formKey)) {
                            return (Field)field;
                        }
                    } else {
                        throw new RiceRuntimeException("Fields must be of type org.kuali.rice.kns.Field");
                    }
                }
            }
        }
        return null;
    }

}
