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
package edu.iu.uis.eden.docsearch;

import org.junit.Ignore;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.docsearch.xml.StandardGenericXMLSearchableAttribute;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.routetemplate.RuleAttribute;

/**
 * This is a description of what this class does - delyea don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
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
    
    protected SearchAttributeCriteriaComponent createSearchAttributeCriteriaComponent(String key,String value,Boolean isLowerBoundValue,DocumentType docType) {
        String formKey = (isLowerBoundValue == null) ? key : ((isLowerBoundValue != null && isLowerBoundValue.booleanValue()) ? SearchableAttribute.RANGE_LOWER_BOUND_PROPERTY_PREFIX + key : SearchableAttribute.RANGE_UPPER_BOUND_PROPERTY_PREFIX + key);
        String savedKey = key;
        SearchAttributeCriteriaComponent sacc = new SearchAttributeCriteriaComponent(formKey,value,savedKey);
        Field field = getFieldByFormKey(docType, formKey);
        if (field != null) {
            sacc.setSearchableAttributeValue(DocSearchUtils.getSearchableAttributeValueByDataTypeString(field.getFieldDataType()));
            sacc.setRangeSearch(field.isMemberOfRange());
            sacc.setAllowWildcards(field.isAllowingWildcards());
            sacc.setAutoWildcardBeginning(field.isAutoWildcardAtBeginning());
            sacc.setAutoWildcardEnd(field.isAutoWildcardAtEnding());
            sacc.setCaseSensitive(field.isCaseSensitive());
            sacc.setSearchInclusive(field.isInclusive());
            sacc.setSearchable(field.isSearchable());
            sacc.setCanHoldMultipleValues(Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType()));
        }
        return sacc;
    }

    private Field getFieldByFormKey(DocumentType docType, String formKey) {
        if (docType == null) {
            return null;
        }
        for (SearchableAttribute searchableAttribute : docType.getSearchableAttributes()) {
            for (Row row : searchableAttribute.getSearchingRows()) {
                for (Field field : row.getFields()) {
                    if (field.getPropertyName().equals(formKey)) {
                        return field;
                    }
                }
            }
        }
        return null;
    }

}
