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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.doctype.ApplicationDocumentStatus;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.lookup.valuefinder.DocumentRouteStatusValuesFinder;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.KEWWebServiceConstants;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.service.DataDictionaryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class adapts the RemotableAttributeField instances from the various attributes
 * associated with a document type and combines with the "default" rows for the search,
 * returning the final List of Row objects to render for the document search.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentLookupCriteriaProcessorKEWAdapter implements DocumentLookupCriteriaProcessor {

	private DocumentSearchCriteriaProcessor criteriaProcessor;
	private DataDictionaryService dataDictionaryService;

	protected DocumentSearchCriteriaProcessor getCriteriaProcessor() {
		return this.criteriaProcessor;
	}

    public void setCriteriaProcessor(DocumentSearchCriteriaProcessor criteriaProcessor) {
        this.criteriaProcessor = criteriaProcessor;
    }

    protected DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }

	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

    @Override
	public List<Row> getRows(DocumentType documentType, List<Row> defaultRows, boolean detailed, boolean superSearch) {
		List<Row> rows = new ArrayList<Row>();

        List<Row> searchAttRows = new ArrayList<Row>();
        List<List<StandardDocSearchCriteriaFieldContainer>> preSearchAttFields;
        if(!detailed) {
            preSearchAttFields = getCriteriaProcessor().getBasicSearchManager().getColumnsPreSearchAttributes();
        } else {
            preSearchAttFields = getCriteriaProcessor().getAdvancedSearchManager().getColumnsPreSearchAttributes();
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
			postSearchAttFields = getCriteriaProcessor().getBasicSearchManager().getColumnsPostSearchAttributes();
		} else {
			postSearchAttFields = getCriteriaProcessor().getAdvancedSearchManager().getColumnsPostSearchAttributes();
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
     * @param documentType document Type
     * @param fields containing search criteria
     * @return list of row objects
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
							fieldType = Field.TEXT; // KULRICE-2630 - doctype needs to be a text box
							field.setWebOnBlurHandler("validateDocTypeAndRefresh"); // used to call ajax dwr search for doctype
							//TODO: instead of hardcoding these let's see about getting them from spring
							field.setQuickFinderClassNameImpl("org.kuali.rice.kew.doctype.bo.DocumentType");
							field.setFieldConversions("name:"+propertyName);
						} else if (StringUtils.equals("UserLookupableImplService", lookupableImplServiceName)) {
							fieldType = Field.TEXT;
							field.setQuickFinderClassNameImpl("org.kuali.rice.kim.bo.impl.PersonImpl");
							field.setFieldConversions("principalName:"+propertyName);
						} else if (StringUtils.equals("WorkGroupLookupableImplService", lookupableImplServiceName)) {
							field.setQuickFinderClassNameImpl("org.kuali.rice.kim.impl.group.GroupBo");
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

					//now calling the dd to get size.
					Integer maxLen = dataDictionaryService.getAttributeMaxLength(DocSearchCriteriaDTO.class, propertyName);
					if(maxLen != null){
						field.setMaxLength(maxLen.intValue());
					}
					else{
						field.setMaxLength(40);
					}
					
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
							field.setFieldType(Field.MULTISELECT); // this is now multi select [KULRICE-2840]
							int size = (values.getKeyValues().size() > 10)?10:values.getKeyValues().size();
							field.setMaxLength(size);
						} else if("routeNodes".equalsIgnoreCase(standardSearchCriteriaField.getOptionsCollectionProperty())){
							if(documentType!=null) {
								//TODO: can these be used directly in values finder also there is an option key and value property that could probably be used by all of these
								List<KeyValue> keyValues = new ArrayList<KeyValue>();
								List<RouteNode> routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(documentType, true);
								for (RouteNode routeNode : routeNodes) {
									keyValues.add(new ConcreteKeyValue(routeNode.getRouteNodeId()+"",routeNode.getRouteNodeName()));
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
							List<KeyValue> keyValues = new ArrayList<KeyValue>();
							Set<String> docStatusKeys = KEWConstants.DOC_SEARCH_ROUTE_STATUS_QUALIFIERS.keySet();
							for (String string : docStatusKeys) {
								KeyValue keyLabel = new ConcreteKeyValue(string,KEWConstants.DOC_SEARCH_ROUTE_STATUS_QUALIFIERS.get(string));
								keyValues.add(keyLabel);
							}
							field.setFieldValidValues(keyValues);
						} else if("validApplicationStatuses".equalsIgnoreCase(standardSearchCriteriaField.getOptionsCollectionProperty())){
							if(documentType!=null) {
								//TODO: can these be used directly in values finder also there is an option key and value property that could probably be used by all of these
								List<KeyValue> keyValues = new ArrayList<KeyValue>();
								List<ApplicationDocumentStatus> validStatuses = documentType.getValidApplicationStatuses();
								for (ApplicationDocumentStatus appStatus : (List<ApplicationDocumentStatus>) validStatuses) {
									keyValues.add(new ConcreteKeyValue(appStatus.getStatusName(),appStatus.getStatusName()));
								}
								field.setFieldValidValues(keyValues);
								//TODO: fix this in criteria this field shouldn't be blank values otherwise have to reset this for some reason
								field.setSkipBlankValidValue(false);
							} else {
								field.setFieldType(Field.READONLY);
								skipadd=true;
							}
							
						} else {
							field.setFieldValidValues(new ArrayList<KeyValue>());
						}
					}


					if(StringUtils.isEmpty(field.getFieldLabel())) {						 
						String labelMessageKey = dataDictionaryService.getAttributeLabel(DocSearchCriteriaDTO.class,propertyName);
						field.setFieldLabel(labelMessageKey);
					}

					boolean hasDatePicker = StringUtils.isNotEmpty(standardSearchCriteriaField.getDatePickerKey());
					field.setDatePicker(hasDatePicker);

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
	 * This method gets the search att rows and fixes them where necessary
	 *
     * @param documentType seach on att rows
     * @return list of rows
     */
	protected List<Row> searchAttRows(DocumentType documentType) {

        List<Row> customSearchAttRows = new ArrayList<Row>();

        // legacy search attributes
		List<SearchableAttributeOld> searchAtts = documentType.getSearchableAttributesOld();
		for (SearchableAttributeOld searchableAttribute : searchAtts) {
			DocumentSearchContext documentSearchContext = DocSearchUtils.getDocumentSearchContext("", documentType.getName(), "");
			customSearchAttRows.addAll(searchableAttribute.getSearchingRows(documentSearchContext));
		}

        // Rice 2.0 search attributes
        List<RemotableAttributeField> remotableAttributeFields =
                KEWServiceLocator.getDocumentSearchCustomizationMediator().getSearchFields(documentType);
        if (remotableAttributeFields != null && !remotableAttributeFields.isEmpty()) {
            customSearchAttRows.addAll(FieldUtils.convertRemotableAttributeFields(remotableAttributeFields));
        }

		List<Row> fixedCustomSearchAttRows = new ArrayList<Row>();
		for (Row row : customSearchAttRows) {
			List<Field> fields = row.getFields();
			for (Field field : fields) {
				//force the max length for now if not set
				if(field.getMaxLength()==0) {
					field.setMaxLength(100);
				}
				if(field.isDatePicker() && field.isRanged()) {
					Field newDate = FieldUtils.createRangeDateField(field);
					List<Field> newFields = new ArrayList<Field>();
					newFields.add(newDate);
					fixedCustomSearchAttRows.addAll(FieldUtils.wrapFields(newFields));
				}
			}
			fixedCustomSearchAttRows.add(row);
		}
		
		// If Application Document Status policy is in effect for this document type,
		// add search attributes for document status, and transition dates.
		// Note: document status field is a drop down if valid statuses are defined,
		//       a text input field otherwise.
		fixedCustomSearchAttRows.addAll( buildAppDocStatusRows(documentType) );
		
		return fixedCustomSearchAttRows;
	}

	// Add the appropriate doc search criteria rows.
	// If the document type policy DOCUMENT_STATUS_POLICY is set to "app", or "both"
	// Then display the doc search criteria fields.
	// If the documentType.validApplicationStatuses are defined, then the criteria field is a drop down.
	// If the validApplication statuses are NOT defined, then the criteria field is a text input.
	protected List<Row> buildAppDocStatusRows(DocumentType documentType){
		List<Row> appDocStatusRows = new ArrayList<Row>();
		List<List<StandardDocSearchCriteriaFieldContainer>> columnList = new ArrayList<List<StandardDocSearchCriteriaFieldContainer>>();
		List<StandardDocSearchCriteriaFieldContainer> columns = new ArrayList<StandardDocSearchCriteriaFieldContainer>();
		if (documentType.isAppDocStatusInUse()){
			StandardDocSearchCriteriaFieldContainer container = new StandardDocSearchCriteriaFieldContainer();

			List<ApplicationDocumentStatus> validStatuses = documentType.getValidApplicationStatuses();
			if (validStatuses == null || validStatuses.size() == 0){
				// use a text input field
				container = new StandardDocSearchCriteriaFieldContainer("docSearch.DocumentSearch.criteria.label.appDocStatus", new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_APP_DOC_STATUS,"criteria.appDocStatus",StandardSearchCriteriaField.TEXT,null,null,"DocSearchApplicationDocStatus",false,null,null,false));
				
			} else {	
				// dropdown
				container.setLabelMessageKey("docSearch.DocumentSearch.criteria.label.appDocStatus");
				container.setFieldKey(DocumentSearchCriteriaProcessor.CRITERIA_KEY_APP_DOC_STATUS);
				StandardSearchCriteriaField dropDown = new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_APP_DOC_STATUS + "_VALUES","criteria.appDocStatus",StandardSearchCriteriaField.DROPDOWN_HIDE_EMPTY,null,null,"DocSearchApplicationDocStatus",false,null,null,false);
				
				dropDown.setOptionsCollectionProperty("validApplicationStatuses");
				dropDown.setCollectionKeyProperty("statusName");
				dropDown.setCollectionLabelProperty("statusName");
				dropDown.setEmptyCollectionMessage("Select a document status.");
				container.addField(dropDown);
				
			}
			// Create Date Picker fields for AppDocStatus transitions
	    	List<StandardSearchCriteriaField> dateFields = new ArrayList<StandardSearchCriteriaField>();
	    	dateFields.add(new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_STATUS_TRANSITION_DATE + DocumentSearchCriteriaProcessor.CRITERIA_KEYS_SUFFIX_RANGE_LOWER_BOUND,"fromStatusTransitionDate",StandardSearchCriteriaField.TEXT,"fromStatusTransitionDate","docSearch.DocumentSearch.criteria.label.from","DocSearchStatusTransitionDate",false,null,null,false));
	    	dateFields.add(new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_STATUS_TRANSITION_DATE + DocumentSearchCriteriaProcessor.CRITERIA_KEYS_SUFFIX_RANGE_UPPER_BOUND,"toStatusTransitionDate",StandardSearchCriteriaField.TEXT,"toStatusTransitionDate","docSearch.DocumentSearch.criteria.label.to",null,false,null,null,false));
	    	StandardDocSearchCriteriaFieldContainer dateContainer = new StandardDocSearchCriteriaFieldContainer(DocumentSearchCriteriaProcessor.CRITERIA_KEY_STATUS_TRANSITION_DATE, "docSearch.DocumentSearch.criteria.label.statusTransitionDate", dateFields);
			
			columns.add( container );
			columns.add( dateContainer );
			columnList.add( columns );
			appDocStatusRows.addAll( standardNonSearchAttRows(documentType,columnList) );
			
		}
		return appDocStatusRows;
	}

	/**
	 *
	 * @see org.kuali.rice.krad.lookup.LookupableHelperService#shouldDisplayHeaderNonMaintActions()
	 */
	public boolean shouldDisplayHeaderNonMaintActions() {
		return getCriteriaProcessor().isHeaderBarDisplayed();
	}

	/**
	 *
	 * @see org.kuali.rice.krad.lookup.LookupableHelperService#shouldDisplayLookupCriteria()
	 */
	public boolean shouldDisplayLookupCriteria() {
		//TODO: chris - How should this handle advanced?  I thought we were only hiding main
		return getCriteriaProcessor().isBasicSearchCriteriaDisplayed();
	}
}
