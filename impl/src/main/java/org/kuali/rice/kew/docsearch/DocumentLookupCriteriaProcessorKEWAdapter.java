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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.KeyLabelPair;
import org.kuali.rice.kns.web.ui.Row;

/**
 * This is a description of what this class does - chris don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentLookupCriteriaProcessorKEWAdapter implements
		DocumentLookupCriteriaProcessor {
	DocumentSearchCriteriaProcessor criteriaProcessor;
	//TODO: remove this and use service locator or try helper in WorkflowUtils if sufficient
	DataDictionaryService dataDictionaryService;
	
	/**
	 * @return the criteriaProcessor
	 */
	public DocumentSearchCriteriaProcessor getCriteriaProcessor() {
		return this.criteriaProcessor;
	}

	public void setCriteriaProcessor(
			DocumentSearchCriteriaProcessor criteriaProcessor) {
		this.criteriaProcessor = criteriaProcessor;
	}
	
	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}
	
	/**
	 * @see org.kuali.rice.kew.docsearch.DocumentLookupCriteriaProcessor#getRows(org.kuali.rice.kns.bo.PersistableBusinessObject)
	 */
	public List<Row> getRows(DocumentType documentType, List<Row> knsRows, boolean detailed) {
		List<Row> rows = new ArrayList<Row>();
		
		List<Row> searchAttRows = new ArrayList<Row>();
		List<List<StandardDocSearchCriteriaFieldContainer>> preSearchAttFields; 
			if(!detailed) {
				 preSearchAttFields = criteriaProcessor.getBasicSearchManager().getColumnsPreSearchAttributes();
			} else {
				 preSearchAttFields = criteriaProcessor.getAdvancedSearchManager().getColumnsPreSearchAttributes();
			}
		List<Row> preSearchAttRows = standardNonSearchAttRows(documentType,preSearchAttFields);
		
		rows.addAll(preSearchAttRows);
		

		if(documentType!=null) {
			
			//search atts
			searchAttRows = searchAttRows(documentType);
			rows.addAll(searchAttRows);
	}
		
		//post atts
		List<List<StandardDocSearchCriteriaFieldContainer>> postSearchAttFields;
		if(!detailed) {
			postSearchAttFields = criteriaProcessor.getBasicSearchManager().getColumnsPostSearchAttributes();
		} else {
			postSearchAttFields = criteriaProcessor.getAdvancedSearchManager().getColumnsPostSearchAttributes();
		}
		List<Row> postSearchAttRows = standardNonSearchAttRows(documentType,postSearchAttFields);
		rows.addAll(postSearchAttRows);
		
		return rows;
	}
	/**
	 * This method ...
	 * 
	 */
	protected List<Row> standardNonSearchAttRows(DocumentType documentType, List<List<StandardDocSearchCriteriaFieldContainer>> fields) {
		List<Row> customPreRows = new ArrayList<Row>();
		for (List<StandardDocSearchCriteriaFieldContainer> list : fields) {
			

			for (StandardDocSearchCriteriaFieldContainer standardDocSearchCriteriaFieldContainer : list) {
				List<StandardSearchCriteriaField> standardSearchCriteriaFields = standardDocSearchCriteriaFieldContainer.getFields();
				for (StandardSearchCriteriaField standardSearchCriteriaField : standardSearchCriteriaFields) {
					//for now only one field per row (including for things like from/to etc)
					Row row = new Row();
					List<Field>knsFields = new ArrayList<Field>();
					Field field = new Field();
					
					String propertyName = "";
					if(StringUtils.contains(standardSearchCriteriaField.getProperty(), ".")) {
						propertyName = StringUtils.substringAfterLast(standardSearchCriteriaField.getProperty(), ".");
					} else {
						propertyName = standardSearchCriteriaField.getProperty();
					}

					//TODO: make this code also handle multiple fields per row when KNS enhancement in
					field.setPropertyName(propertyName);
					//do we care?
					//				field.setBusinessObjectClassName(dataDictionaryService.getattribute);

					String fieldType = standardSearchCriteriaField.getFieldType();
					//TODO: is there another way to derive this without specifying the field?
					if(propertyName.equals("docTypeFullName")) {
						fieldType = Field.LOOKUP_READONLY;
						//TODO: total hack, will be removed soon - should add this field to standard criteria, which will make it work for the others
						field.setQuickFinderClassNameImpl("org.kuali.rice.kew.doctype.bo.DocumentType");
						field.setFieldConversions("name:docTypeFullName");
//						field.setInquiryParameters("docTypeFullName:name");
//						field.setLookupParameters("name:docTypeFullName");
					}
					field.setFieldType(fieldType);
					//TODO: special processing for some field types
					if(StringUtils.equals(StandardSearchCriteriaField.DROPDOWN,fieldType)||
					   StringUtils.equals(StandardSearchCriteriaField.DROPDOWN_HIDE_EMPTY, fieldType)){
						if(StringUtils.equals(StandardSearchCriteriaField.DROPDOWN_HIDE_EMPTY,fieldType)) {
							field.setFieldType(Field.DROPDOWN);
							//TODO: anything special here?
						}
						//TODO: replace with real list
						field.setFieldValidValues(new ArrayList<KeyLabelPair>());
					}
					
					
					if(StringUtils.isEmpty(field.getFieldLabel())) {
						String labelMessageKey = dataDictionaryService.getAttributeLabel(DocSearchCriteriaDTO.class,propertyName);
						field.setFieldLabel(labelMessageKey);
					}
					
					boolean hasDatePicker = StringUtils.isNotEmpty(standardSearchCriteriaField.getDatePickerKey());
					field.setDatePicker(hasDatePicker);
					
					//this is set to 30 in the jsp for standard fields, should this be a parameter?!
					field.setMaxLength(30);
					
					knsFields.add(field);
					row.setFields(knsFields);
					customPreRows.add(row);
				}

			}
		}
		return customPreRows;
	}

	/**
	 * This method ...
	 * 
	 */
	protected List<Row> searchAttRows(DocumentType documentType) {
		List<Row> customSearchAttRows = new ArrayList<Row>();
		List<SearchableAttribute> searchAtts = documentType.getSearchableAttributes();
		for (SearchableAttribute searchableAttribute : searchAtts) {
			//TODO: this needs more translation like above also setup a DocumentSearchContext
			DocumentSearchContext documentSearchContext = DocSearchUtils.getDocumentSearchContext("", documentType.getName(), "");
			customSearchAttRows.addAll(searchableAttribute.getSearchingRows(documentSearchContext));
		}
		for (Row row : customSearchAttRows) {
			List<Field> fields = row.getFields();
			for (Field field : fields) {
				//force the max length for now if not set
				if(field.getMaxLength()==0) {
					field.setMaxLength(100);
				}
			}
		}
		return customSearchAttRows;
	}
	
	/**
	 * 
	 * @see org.kuali.rice.kns.lookup.LookupableHelperService#shouldDisplayHeaderNonMaintActions()
	 */
	public boolean shouldDisplayHeaderNonMaintActions() {
		//TODO: chris - should handle advanced too
		return criteriaProcessor.isHeaderBarDisplayed();
	}

	/**
	 * 
	 * @see org.kuali.rice.kns.lookup.LookupableHelperService#shouldDisplayLookupCriteria()
	 */
	public boolean shouldDisplayLookupCriteria() {
		//TODO: chris - How should this handle advanced?  I thought we were only hiding main
		return criteriaProcessor.isBasicSearchCriteriaDisplayed();
	}
}
