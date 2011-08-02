/*
 * Copyright 2007-2009 The Kuali Foundation
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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.framework.KewFrameworkServiceLocator;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.util.KRADConstants;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Helper class Used for building a Document Search criteria for the lookup
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentLookupCriteriaBuilder  {

    private static final Logger LOG = Logger.getLogger(DocumentLookupCriteriaBuilder.class);

	/**
	 * This method populates the criteria given a map of fields from the lookup
	 *
	 * @param fieldsForLookup map of fields
	 * @return constructed criteria
	 */
	public static DocSearchCriteriaDTO populateCriteria(Map<String,String[]> fieldsForLookup) {
    	DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
    	Map<String,String[]> fieldsToSet = new HashMap<String,String[]>();
		for (String formKey : fieldsForLookup.keySet()) {
			if(!(formKey.equalsIgnoreCase(KRADConstants.BACK_LOCATION) ||
			   formKey.equalsIgnoreCase(KRADConstants.DOC_FORM_KEY)) && fieldsForLookup.get(formKey)!=null && fieldsForLookup.get(formKey).length!=0) {
				fieldsToSet.put(formKey, fieldsForLookup.get(formKey));
			}
		}
    	for (String fieldToSet : fieldsToSet.keySet()) {
    		 String valueToSet = "";
    		 String[] valuesToSet = fieldsToSet.get(fieldToSet);
    		 // some inputs are now multi-select
    		 if(valuesToSet.length >= 1){
    			 for(String value: valuesToSet){
    				 valueToSet += value + ",";
    			 }
    			 valueToSet = valueToSet.substring(0, valueToSet.length()-1);
    		 }else{
    			 valueToSet = valuesToSet[0];
    		 }

            if (PropertyUtils.isWriteable(criteria, fieldToSet)) {
                try {
			    	PropertyUtils.setNestedProperty(criteria, fieldToSet, valueToSet);
			    } catch (Exception e) {
                    throw new IllegalStateException("Failed to set document search criteria field: " + fieldToSet, e);
                }
            } else {
                LOG.warn("Document Search was passed a piece of criteria it did not understand, ignoring: " + fieldToSet);
			}
		}

        DocumentType documentType = getValidDocumentTypeByNameCaseInsensitive(criteria.getDocTypeFullName());

    	// This will make sure that the docType is case insensitive after this point.
        if (documentType != null) {
    	    criteria.setDocTypeFullName(documentType.getName());
        }

    	addSearchableAttributesToCriteria(documentType, criteria, fieldsForLookup);

    	return criteria;
	}

	public static void addSearchableAttributesToCriteria(DocumentType documentType, DocSearchCriteriaDTO criteria, Map<String,String[]> propertyFields) {
		if (documentType != null && documentType.hasSearchableAttributes() && criteria != null) {
			criteria.getSearchableAttributes().clear();
			if (!propertyFields.isEmpty()) {
				List<RemotableAttributeField> searchFields = KEWServiceLocator.getDocumentSearchCustomizationMediator().getSearchFields(documentType);
                for (RemotableAttributeField searchField : searchFields) {
                    SearchableAttributeValue searchableAttributeValue = DocSearchUtils.getSearchableAttributeValueByDataTypeString(searchField.getDataType());
                    SearchAttributeCriteriaComponent sacc = new SearchAttributeCriteriaComponent(searchField.getName(), null, searchField.getName(), searchableAttributeValue);

                    /*

                     TODO - Rice 2.0 - implement range and case sensitivity support, pus support for this other stuff on te SearchableAttributeComponent

                    sacc.setRangeSearch(field.isMemberOfRange());
                    sacc.setCaseSensitive(!field.isUpperCase());

                    // FIXME: don't force this when dd changes are in, instead delete line 1 row below and uncomment one two lines below
                    sacc.setAllowInlineRange(true);
                    // sacc.setAllowInlineRange(dsField.isAllowInlineRange());

                    sacc.setSearchInclusive(field.isInclusive());
                    sacc.setLookupableFieldType(field.getFieldType());
                    sacc.setSearchable(field.isIndexedForSearch());
                    sacc.setCanHoldMultipleValues(Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType()));
                    */

                    // now set the value for the search attribute fields

                    String[] propertyFieldValues = propertyFields.get(searchField.getName());
                    if (propertyFieldValues != null) {
                        // TODO - Rice 2.0 - add a test here to make sure the field can support multi-values
                        if (propertyFieldValues.length > 1) {
                            sacc.setValues(Arrays.asList(propertyFieldValues));
                        }
                        sacc.setValue(propertyFieldValues[0]);
                    }
                    criteria.addSearchableAttribute(sacc);
				}
			}
		}
	}

	private static DocumentType getValidDocumentTypeByNameCaseInsensitive(String docTypeName) {
		if (StringUtils.isNotBlank(docTypeName)) {
			DocumentType dTypeCriteria = new DocumentType();
		    dTypeCriteria.setName(docTypeName.trim());
		    dTypeCriteria.setActive(true);
		    Collection<DocumentType> docTypeList = KEWServiceLocator.getDocumentTypeService().find(dTypeCriteria, null, false);
		    
		    // Return the valid doc type.
		    if(docTypeList != null){
			    for(DocumentType dType: docTypeList){
			        if (StringUtils.equals(docTypeName.toUpperCase(), dType.getName().toUpperCase())) {
			            return dType;
			        }
			    }
		    }
		}
		return null;
	} 
}
