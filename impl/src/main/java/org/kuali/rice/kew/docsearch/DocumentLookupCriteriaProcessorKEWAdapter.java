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
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.lookup.valuefinder.DocumentRouteStatusValuesFinder;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.web.KeyValue;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
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
	public List<Row> getRows(DocumentType documentType, List<Row> knsRows, boolean detailed, boolean superSearch) {
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
		//add hidden fields
		Row hidrow = new Row();
		hidrow.setHidden(true);
		Field detailedField = new Field();
		detailedField.setPropertyName("isAdvancedSearch");
		detailedField.setPropertyValue(detailed?"YES":"NO");
		detailedField.setFieldType(Field.HIDDEN);
		Field superUserSearchField = new Field();
		superUserSearchField.setPropertyName("superUserSearch");
		superUserSearchField.setPropertyValue(superSearch?"YES":"NO");
		superUserSearchField.setFieldType(Field.HIDDEN);
		List<Field> hidFields = new ArrayList<Field>();
		hidFields.add(detailedField);
		hidFields.add(superUserSearchField);
		hidrow.setFields(hidFields);
		rows.add(hidrow);
		
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
					boolean skipadd = false;
					
					String propertyName = "";
					if(StringUtils.contains(standardSearchCriteriaField.getProperty(), ".")) {
						propertyName = StringUtils.substringAfterLast(standardSearchCriteriaField.getProperty(), ".");
					} else {
						propertyName = standardSearchCriteriaField.getProperty();
					}

					field.setPropertyName(propertyName);
					//do we care?
					//				field.setBusinessObjectClassName(dataDictionaryService.getattribute);

					String fieldType = standardSearchCriteriaField.getFieldType();
					
					String lookupableImplServiceName = standardSearchCriteriaField.getLookupableImplServiceName();
					if(lookupableImplServiceName!=null) {
						if(StringUtils.equals("DocumentTypeLookupableImplService", lookupableImplServiceName)) {
							fieldType = Field.LOOKUP_READONLY;
							//TODO: instead of hardcoding these let's see about getting them from spring
							field.setQuickFinderClassNameImpl("org.kuali.rice.kew.doctype.bo.DocumentType");
							field.setFieldConversions("name:"+propertyName);
						} else if (StringUtils.equals("UserLookupableImplService", lookupableImplServiceName)) {
							fieldType = Field.TEXT;
							field.setQuickFinderClassNameImpl("org.kuali.rice.kim.bo.impl.PersonImpl");
							field.setFieldConversions("principalName:"+propertyName);
						} else if (StringUtils.equals("WorkGroupLookupableImplService", lookupableImplServiceName)) {
							field.setQuickFinderClassNameImpl("org.kuali.rice.kim.bo.group.impl.KimGroupImpl");
							fieldType = Field.LOOKUP_READONLY;
							field.setFieldConversions("groupName:"+propertyName+","+"groupId:"+StandardDocumentSearchCriteriaProcessor.CRITERIA_KEY_WORKGROUP_VIEWER_ID);
						}
					}
					boolean fieldHidden = standardSearchCriteriaField.isHidden();
					if(fieldHidden) {
						fieldType = Field.HIDDEN;
						row.setHidden(true);
					}
					field.setFieldType(fieldType);
					//TODO: special processing for some field types
					if(StringUtils.equals(StandardSearchCriteriaField.DROPDOWN,fieldType)||
					   StringUtils.equals(StandardSearchCriteriaField.DROPDOWN_HIDE_EMPTY, fieldType)){
						if(StringUtils.equals(StandardSearchCriteriaField.DROPDOWN_HIDE_EMPTY,fieldType)) {
							
							field.setFieldType(Field.DROPDOWN);
							field.setSkipBlankValidValue(true);
									
						}
						
						if("documentRouteStatus".equalsIgnoreCase(standardSearchCriteriaField.getOptionsCollectionProperty())) {
							DocumentRouteStatusValuesFinder values = new DocumentRouteStatusValuesFinder();
							field.setFieldValidValues(values.getKeyValues());
						} else if("routeNodes".equalsIgnoreCase(standardSearchCriteriaField.getOptionsCollectionProperty())){
							if(documentType!=null) {
								//TODO: can these be used directly in values finder also there is an option key and value property that could probably be used by all of these
								List<KeyLabelPair> keyValues = new ArrayList<KeyLabelPair>();
								List routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(documentType, true);
								for (RouteNode routeNode : (List<RouteNode>)routeNodes) {
									keyValues.add(new KeyLabelPair(routeNode.getRouteNodeId()+"",routeNode.getRouteNodeName()));
								}
								field.setFieldValidValues(keyValues);
								//TODO: fix this in criteria this field shouldn't be blank values otherwise have to reset this for some reason
								field.setSkipBlankValidValue(false);
							} else {
								field.setFieldType(Field.READONLY);
							}
						} else if("qualifierLogic".equalsIgnoreCase(standardSearchCriteriaField.getOptionsCollectionProperty())){
							if(documentType==null){
								//FIXME: definitely not the best place for this
								skipadd=true;
							}
							//TODO: move to values finder class
							List<KeyLabelPair> keyValues = new ArrayList<KeyLabelPair>();
							Set<String> docStatusKeys = KEWConstants.DOC_SEARCH_ROUTE_STATUS_QUALIFIERS.keySet();
							for (String string : docStatusKeys) {
								KeyLabelPair keyLabel = new KeyLabelPair(string,KEWConstants.DOC_SEARCH_ROUTE_STATUS_QUALIFIERS.get(string));
								keyValues.add(keyLabel);
							}
							field.setFieldValidValues(keyValues);
						}
						else {
							field.setFieldValidValues(new ArrayList<KeyLabelPair>());
						}
					}
					
					
					if(StringUtils.isEmpty(field.getFieldLabel())) {
						String labelMessageKey = dataDictionaryService.getAttributeLabel(DocSearchCriteriaDTO.class,propertyName);
						field.setFieldLabel(labelMessageKey);
					}
					
					boolean hasDatePicker = StringUtils.isNotEmpty(standardSearchCriteriaField.getDatePickerKey());
					field.setDatePicker(hasDatePicker);
					
					//this is set to 30 in the jsp for standard fields, should this be a parameter?!
					field.setMaxLength(30);
					
					if(!skipadd) {
						knsFields.add(field);
						row.setFields(knsFields);
						customPreRows.add(row);
					}
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
