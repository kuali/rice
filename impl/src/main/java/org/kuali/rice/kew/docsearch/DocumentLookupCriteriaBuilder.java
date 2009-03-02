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
package org.kuali.rice.kew.docsearch;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.docsearch.web.SearchAttributeFormContainer;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;

/**
 * This is a description of what this class does - chris don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentLookupCriteriaBuilder  {
	
	/**
	 * This method ...
	 * 
	 * @param lookupForm
	 * @return
	 */
	public static DocSearchCriteriaDTO populateCriteria(Map<String,String> fieldsForLookup) {
    	DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
    	Map<String,String> fieldsToSet = new HashMap<String,String>();
		for (String formKey : fieldsForLookup.keySet()) {
			if(!(formKey.equalsIgnoreCase(KNSConstants.BACK_LOCATION) ||
			   formKey.equalsIgnoreCase(KNSConstants.DOC_FORM_KEY)) && StringUtils.isNotEmpty(fieldsForLookup.get(formKey))) {
				fieldsToSet.put(formKey, fieldsForLookup.get(formKey));
			}
		}
		//if we use DocSearchCriteriaDTO as object we shouldn't need this conversion stuff
    	for (String fieldToSet : fieldsToSet.keySet()) {
			//need translation code here for certain fields
    		String valueToSet = fieldsToSet.get(fieldToSet);
			try {
				PropertyUtils.setNestedProperty(criteria, fieldToSet, valueToSet);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
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
	public static void addSearchableAttributesToCriteria(DocSearchCriteriaDTO criteria, Map<String,String> propertyFields) {
		if (criteria != null) {
			DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName(criteria.getDocTypeFullName());
			if (docType == null) {
				return;
			}
			criteria.getSearchableAttributes().clear();
			if (!propertyFields.isEmpty()) {
				Map criteriaComponentsByFormKey = new HashMap();
				for (SearchableAttribute searchableAttribute : docType.getSearchableAttributes()) {
					for (Row row : searchableAttribute.getSearchingRows(
							DocSearchUtils.getDocumentSearchContext("", docType.getName(), ""))) {
						for (org.kuali.rice.kns.web.ui.Field field : row.getFields()) {
							if (field instanceof Field) {
								Field dsField = (Field)field;
								SearchableAttributeValue searchableAttributeValue = DocSearchUtils.getSearchableAttributeValueByDataTypeString(dsField.getFieldDataType());
								SearchAttributeCriteriaComponent sacc = new SearchAttributeCriteriaComponent(dsField.getPropertyName(), null, dsField.getSavablePropertyName(), searchableAttributeValue);
								sacc.setRangeSearch(dsField.isMemberOfRange());
								sacc.setSearchInclusive(dsField.isInclusive());
								sacc.setLookupableFieldType(dsField.getFieldType());
								sacc.setSearchable(dsField.isIndexedForSearch());
								sacc.setCanHoldMultipleValues(dsField.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType()));
								criteriaComponentsByFormKey.put(dsField.getPropertyName(), sacc);
							} else {
								throw new RiceRuntimeException("Fields must be of type org.kuali.rice.kew.docsearch.Field");
							}
						}
					}
				}
				for (Iterator iterator = propertyFields.keySet().iterator(); iterator.hasNext();) {
					String propertyField = (String) iterator.next();
					SearchAttributeCriteriaComponent sacc = (SearchAttributeCriteriaComponent) criteriaComponentsByFormKey.get(propertyField);
					if (sacc != null) {
						if (sacc.getSearchableAttributeValue() == null) {
							String errorMsg = "Searchable attribute with form field key " + sacc.getFormKey() + " does not have a valid SearchableAttributeValue";
							//                            LOG.error("addSearchableAttributesToCriteria() " + errorMsg);
							throw new RuntimeException(errorMsg);
						}
							if (Field.MULTI_VALUE_FIELD_TYPES.contains(sacc.getLookupableFieldType())) {
								// set the multivalue lookup indicator
								sacc.setCanHoldMultipleValues(true);
								if (propertyField == null) {
									sacc.setValues(new ArrayList<String>());
								} else {
									//TODO: chris, this can't work and will need to be changed
									//                                    sacc.setValues(Arrays.asList(propertyField.getValues()));
								}
							} else {
								sacc.setValue(propertyFields.get(propertyField));
							}
							criteria.addSearchableAttribute(sacc);
						}
				}
			}
		}
	}

	/**
	 * 
	 * This method is taken from DocSearch to retrieve a document type
	 * 
	 * @param docTypeName
	 * @return
	 */
    private static DocumentType getValidDocumentType(String docTypeName) {
        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(docTypeName);
        if (documentType == null) {
            throw new RuntimeException("Document Type invalid : " + docTypeName);
        }
        return documentType;
    }
}
