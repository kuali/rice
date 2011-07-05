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
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kew.doctype.bo.DocumentType;
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

			try {
				PropertyUtils.setNestedProperty(criteria, fieldToSet, valueToSet);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				//ignore this
				//				e.printStackTrace();
			}
		}

    	// This will make sure that the docType is case insensitive after this point.
    	criteria.setDocTypeFullName(getValidDocumentTypeName(criteria.getDocTypeFullName()));

    	if(StringUtils.isNotEmpty(criteria.getDocTypeFullName())) {
    		addSearchableAttributesToCriteria(criteria, fieldsForLookup);
    	}
		return criteria;
	}

	/**
	 * TODO: Chris, Should be reevaluated in whole after released for KFS
	 * This method ...
	 *
	 * @param criteria
	 * @param propertyFields
	 */
	public static void addSearchableAttributesToCriteria(DocSearchCriteriaDTO criteria, Map<String,String[]> propertyFields) {
		if (criteria != null) {
			DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName(criteria.getDocTypeFullName());
			if (docType == null) {
				return;
			}
			criteria.getSearchableAttributes().clear();
			if (!propertyFields.isEmpty()) {
				Map<String, SearchAttributeCriteriaComponent> criteriaComponentsByFormKey = new HashMap<String, SearchAttributeCriteriaComponent>();
				for (SearchableAttribute searchableAttribute : docType.getSearchableAttributes()) {
					for (Row row : searchableAttribute.getSearchingRows(
							DocSearchUtils.getDocumentSearchContext("", docType.getName(), ""))) {
						for (Field field : row.getFields()) {
							if (field instanceof Field) {
                                SearchableAttributeValue searchableAttributeValue = DocSearchUtils.getSearchableAttributeValueByDataTypeString(field.getFieldDataType());
								SearchAttributeCriteriaComponent sacc = new SearchAttributeCriteriaComponent(field.getPropertyName(), null, field.getPropertyName(), searchableAttributeValue);
								sacc.setRangeSearch(field.isMemberOfRange());
								sacc.setCaseSensitive(!field.isUpperCase());

								//FIXME: don't force this when dd changes are in, instead delete line 1 row below and uncomment one two lines below
								sacc.setAllowInlineRange(true);
//								sacc.setAllowInlineRange(dsField.isAllowInlineRange());

								sacc.setSearchInclusive(field.isInclusive());
								sacc.setLookupableFieldType(field.getFieldType());
								sacc.setSearchable(field.isIndexedForSearch());
								sacc.setCanHoldMultipleValues(Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType()));
								criteriaComponentsByFormKey.put(field.getPropertyName(), sacc);
							} else {
								throw new RiceRuntimeException("Fields must be of type org.kuali.rice.kew.docsearch.Field");
							}
						}
					}
				}
                for (String propertyField : propertyFields.keySet())
                {
                    SearchAttributeCriteriaComponent sacc = (SearchAttributeCriteriaComponent) criteriaComponentsByFormKey.get(propertyField);
                    if (sacc != null)
                    {
                        if (sacc.getSearchableAttributeValue() == null)
                        {
                            String errorMsg = "Searchable attribute with form field key " + sacc.getFormKey() + " does not have a valid SearchableAttributeValue";
                            //                            LOG.error("addSearchableAttributesToCriteria() " + errorMsg);
                            throw new RuntimeException(errorMsg);
                        }
                        String[] values = propertyFields.get(propertyField);
                        if (Field.MULTI_VALUE_FIELD_TYPES.contains(sacc.getLookupableFieldType()))
                        {
                            // set the multivalue lookup indicator
                            sacc.setCanHoldMultipleValues(true);
                            if (propertyField == null)
                            {
                                sacc.setValues(new ArrayList<String>());
                            } else
                            {
                                if (values != null)
                                {
                                    sacc.setValues(Arrays.asList(values));
                                }
                            }
                        } else
                        {
                            sacc.setValue(values[0]);
                        }
                        criteria.addSearchableAttribute(sacc);
                    }
                }
			}
		}
	}

	private static String getValidDocumentTypeName(String docTypeName) {
		if (StringUtils.isNotEmpty(docTypeName)) {
			DocumentType dTypeCriteria = new DocumentType();
		    dTypeCriteria.setName(docTypeName.trim());
		    dTypeCriteria.setActive(true);
		    Collection<DocumentType> docTypeList = KEWServiceLocator.getDocumentTypeService().find(dTypeCriteria, null, false);
		    
		    // Return the valid doc type.
		    if(docTypeList != null){
			    for(DocumentType dType: docTypeList){
			        if (StringUtils.equals(docTypeName.toUpperCase(), dType.getName().toUpperCase())) {
			            return dType.getName();
			        }
			    }
		    }
		}
		return docTypeName;
	} 
}
