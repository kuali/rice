/*
 * Copyright 2007 The Kuali Foundation
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

import org.kuali.rice.krad.web.ui.Field;
import org.kuali.rice.krad.web.ui.Row;

import java.util.*;


/**
 * This is the standard document search criteria processor implementation.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class StandardDocumentSearchCriteriaProcessor implements DocumentSearchCriteriaProcessor {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StandardDocumentSearchCriteriaProcessor.class);

    private String searchingUser;
    private DocSearchCriteriaDTO docSearchCriteriaDTO = new DocSearchCriteriaDTO();

    public StandardDocumentSearchCriteriaProcessor() {}

    /**
     * @return the searchingUser
     */
    public String getSearchingUser() {
        return this.searchingUser;
    }

    /**
     * @param principalId the searchingUser to set
     */
    public void setSearchingUser(String principalId) {
        this.searchingUser = searchingUser;
    }

    /**
     * @return the docSearchCriteriaDTO
     */
    public DocSearchCriteriaDTO getDocSearchCriteriaDTO() {
        return this.docSearchCriteriaDTO;
    }

    /**
     * @param docSearchCriteriaDTO the docSearchCriteriaDTO to set
     */
    public void setDocSearchCriteriaDTO(DocSearchCriteriaDTO docSearchCriteriaDTO) {
        this.docSearchCriteriaDTO = docSearchCriteriaDTO;
    }

    private Set<String> generateHiddenFieldKeySet(List<String> hiddenFieldKeys) {
        hiddenFieldKeys.addAll(getGlobalHiddenFieldKeys());
        return new HashSet<String>(hiddenFieldKeys);
    }

    /*
     *   GLOBAL HELPER METHODS
     *
     */

    public List<String> getGlobalHiddenFieldKeys() {
    	List<String> hiddenFieldKeys = new ArrayList<String>();
    	hiddenFieldKeys.add(CRITERIA_KEY_WORKGROUP_VIEWER_ID);
        return hiddenFieldKeys;
    }

	/**
	 * Standard implementation of this method is that the header bar is always displayed
	 * so this returns true.
	 *
	 * @see org.kuali.rice.kew.docsearch.DocumentSearchCriteriaProcessor#isHeaderBarDisplayed()
	 */
	public boolean isHeaderBarDisplayed() {
		return true;
	}

    /**
     * This method returns a map of the standard criteria field containers by key.  The values
     * for the keys are constants in the {@link DocumentSearchCriteriaProcessor} class using
     * the name format CRITERIA_KEY_XXXXXXX.
     * @return map of standard critera field containers by key
     */
    public Map<String,StandardDocSearchCriteriaFieldContainer> getStandardCriteriaFieldContainerMap() {
    	Map<String,StandardDocSearchCriteriaFieldContainer> containersByKey = new HashMap<String,StandardDocSearchCriteriaFieldContainer>();
    	putContainerIntoMap(containersByKey, new StandardDocSearchCriteriaFieldContainer("docSearch.DocumentSearch.criteria.label.searchName", new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_NAMED_SEARCH,"criteria.namedSearch",StandardSearchCriteriaField.TEXT,null,null,"DocSearchNamedSearch",false,null,null,false)));
    	putContainerIntoMap(containersByKey, new StandardDocSearchCriteriaFieldContainer("docSearch.DocumentSearch.criteria.label.documentType", new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_DOC_TYPE_FULL_NAME,"criteria.docTypeFullName",StandardSearchCriteriaField.TEXT,null,null,"DocSearchDocumentType",false,"docTypeDisplayName",DOC_TYP_LOOKUPABLE,false)));
    	putContainerIntoMap(containersByKey, new StandardDocSearchCriteriaFieldContainer("docSearch.DocumentSearch.criteria.label.initiatorId", new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_INITIATOR,"criteria.initiator",StandardSearchCriteriaField.TEXT,null,null,"DocSearchInitiator",false,null,PERSON_LOOKUPABLE,true)));
    	putContainerIntoMap(containersByKey, new StandardDocSearchCriteriaFieldContainer("docSearch.DocumentSearch.criteria.label.documentId", new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_DOCUMENT_ID,"criteria.documentId",StandardSearchCriteriaField.TEXT,null,null,"DocSearchDocumentId",false,null,null,false)));
    	putContainerIntoMap(containersByKey, new StandardDocSearchCriteriaFieldContainer("docSearch.DocumentSearch.criteria.label.viewerId", new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_VIEWER_ID,"criteria.viewer",StandardSearchCriteriaField.TEXT,null,null,"DocSearchViewer",false,null,PERSON_LOOKUPABLE,true)));
    	putContainerIntoMap(containersByKey, new StandardDocSearchCriteriaFieldContainer("docSearch.DocumentSearch.criteria.label.approverId", new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_APPROVER_ID,"criteria.approver",StandardSearchCriteriaField.TEXT,null,null,"DocSearchApprover",false,null,PERSON_LOOKUPABLE,true)));
    	putContainerIntoMap(containersByKey, new StandardDocSearchCriteriaFieldContainer("docSearch.DocumentSearch.criteria.label.workgroupViewer", new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_WORKGROUP_VIEWER,"criteria.workgroupViewerName",StandardSearchCriteriaField.TEXT,null,null,"WorkgroupName",false,null,WORKGROUP_LOOKUPABLE,false)));
    	putContainerIntoMap(containersByKey, new StandardDocSearchCriteriaFieldContainer("docSearch.DocumentSearch.criteria.label.workgroupViewerId", new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_WORKGROUP_VIEWER_ID,"criteria.workgroupViewerId",StandardSearchCriteriaField.TEXT,null,null,"WorkgroupID",true,null,null,false)));
    	putContainerIntoMap(containersByKey, new StandardDocSearchCriteriaFieldContainer("docSearch.DocumentSearch.criteria.label.applicationDocumentId", new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_APPLICATION_DOCUMENT_ID,"criteria.appDocId",StandardSearchCriteriaField.TEXT,null,null,"DocSearchApplicationDocId",false,null,null,false)));
    	putContainerIntoMap(containersByKey, new StandardDocSearchCriteriaFieldContainer("docSearch.DocumentSearch.criteria.label.documentTitle", new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_DOCUMENT_TITLE,"criteria.docTitle",StandardSearchCriteriaField.TEXT,null,null,"DocSearchDocumentTitle",false,null,null,false)));
    	// create date
    	putContainerIntoMap(containersByKey, buildStandardDateRangeFieldContainer(DocumentSearchCriteriaProcessor.CRITERIA_KEY_CREATE_DATE, "docSearch.DocumentSearch.criteria.label.dateCreated", "DocSearchDateCreated", "fromDateCreated", "toDateCreated", "fromDateCreated", "toDateCreated"));
    	// last modified date
    	putContainerIntoMap(containersByKey, buildStandardDateRangeFieldContainer(DocumentSearchCriteriaProcessor.CRITERIA_KEY_LAST_MODIFIED_DATE, "docSearch.DocumentSearch.criteria.label.dateLastModified", "DocSearchDateLastModified", "fromDateLastModified", "toDateLastModified", "advancedFromDateLastModified", "advancedToDateLastModified"));
    	// finalized date
    	putContainerIntoMap(containersByKey, buildStandardDateRangeFieldContainer(DocumentSearchCriteriaProcessor.CRITERIA_KEY_FINALIZED_DATE, "docSearch.DocumentSearch.criteria.label.dateFinalized", "DocSearchDateFinalized", "fromDateFinalized", "toDateFinalized", "advancedFromDateFinalized", "advancedToDateFinalized"));
    	// approved date
    	putContainerIntoMap(containersByKey, buildStandardDateRangeFieldContainer(DocumentSearchCriteriaProcessor.CRITERIA_KEY_APPROVED_DATE, "docSearch.DocumentSearch.criteria.label.dateApproved", "DocSearchDateApproved", "fromDateApproved", "toDateApproved", "advancedFromDateApproved", "advancedToDateApproved"));

    	// custom fields including drop down type
    	StandardSearchCriteriaField dropDown = new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_DOCUMENT_ROUTE_STATUS,"criteria.docRouteStatus",StandardSearchCriteriaField.DROPDOWN,null,null,"DocSearchDocumentRouteStatus",false,null,null,false);
    	dropDown.setOptionsCollectionProperty("documentRouteStatus");
    	dropDown.setCollectionKeyProperty("key");
    	dropDown.setCollectionLabelProperty("value");
    	putContainerIntoMap(containersByKey, new StandardDocSearchCriteriaFieldContainer("docSearch.DocumentSearch.criteria.label.documentRouteStatus", dropDown));

    	// route node dropdown
    	StandardDocSearchCriteriaFieldContainer container = new StandardDocSearchCriteriaFieldContainer();
    	container.setLabelMessageKey("docSearch.DocumentSearch.criteria.label.documentRouteNode");
    	container.setFieldKey(DocumentSearchCriteriaProcessor.CRITERIA_KEY_DOCUMENT_ROUTE_NODE);
    	dropDown = new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_DOCUMENT_ROUTE_NODE + "_VALUES","criteria.docRouteNodeId",StandardSearchCriteriaField.DROPDOWN_HIDE_EMPTY,null,null,"DocSearchDocumentRouteLevel",false,null,null,false);
    	dropDown.setOptionsCollectionProperty("routeNodes");
    	dropDown.setCollectionKeyProperty("routeNodeName");
    	dropDown.setCollectionLabelProperty("routeNodeName");
    	dropDown.setEmptyCollectionMessage("Select a document type.");
    	container.addField(dropDown);
    	dropDown = new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_DOCUMENT_ROUTE_NODE + "_LOGIC","criteria.docRouteNodeLogic",StandardSearchCriteriaField.DROPDOWN_HIDE_EMPTY,null,null,null,false,null,null,false);
    	dropDown.setOptionsCollectionProperty("qualifierLogic");
    	dropDown.setCollectionKeyProperty("key");
    	dropDown.setCollectionLabelProperty("value");
    	container.addField(dropDown);
    	putContainerIntoMap(containersByKey, container);

    	return containersByKey;
    }

    /**
     * Helper method used in constructing the date range fields for standard criteria
     */
    public StandardDocSearchCriteriaFieldContainer buildStandardDateRangeFieldContainer(String criteriaKey, String labelKey, String helpMessageArgument, String lowerBoundPropertyName, String upperBoundPropertyName, String lowerBoundDatePickerKey, String upperBoundDatePickerKey) {
    	List<StandardSearchCriteriaField> dateFields = new ArrayList<StandardSearchCriteriaField>();
    	dateFields.add(new StandardSearchCriteriaField(criteriaKey + DocumentSearchCriteriaProcessor.CRITERIA_KEYS_SUFFIX_RANGE_LOWER_BOUND,lowerBoundPropertyName,StandardSearchCriteriaField.TEXT,lowerBoundDatePickerKey,"docSearch.DocumentSearch.criteria.label.from",helpMessageArgument,false,null,null,false));
    	dateFields.add(new StandardSearchCriteriaField(criteriaKey + DocumentSearchCriteriaProcessor.CRITERIA_KEYS_SUFFIX_RANGE_UPPER_BOUND,upperBoundPropertyName,StandardSearchCriteriaField.TEXT,upperBoundDatePickerKey,"docSearch.DocumentSearch.criteria.label.to",null,false,null,null,false));
    	return new StandardDocSearchCriteriaFieldContainer(criteriaKey, labelKey, dateFields);
    }

    /**
     * Helper method used in constructing the standard field map
     */
    public void putContainerIntoMap(Map<String,StandardDocSearchCriteriaFieldContainer> containersByKey, StandardDocSearchCriteriaFieldContainer container) {
    	containersByKey.put(container.getFieldKey(), container);
    }

    private List<Row> processSearchableAttributeRows(List<Row> searchableAttributeRows, List<String> hiddenFieldKeys) {
        Set<String> hiddenKeys = generateHiddenFieldKeySet(hiddenFieldKeys);
        if (hiddenKeys.isEmpty()) {
            return searchableAttributeRows;
        }
        for (Row searchableAttributeRow : searchableAttributeRows)
        {
            for (Field field : searchableAttributeRow.getFields())
            {
                // set hidden field to true if hiddenKeys is not empty and if hiddenKeys contains either the field's propertyName value or savablePropertyName value
                final boolean hidden = (!hiddenKeys.isEmpty()) && ((hiddenKeys.contains(field.getPropertyName())) || (hiddenKeys.contains(field.getPropertyName())));
                if (hidden) {
                    field.setFieldType(Field.HIDDEN);
                }
            }
        }
        return searchableAttributeRows;
    }

    /*
     *   BASIC SEARCH METHODS
     *
     */

    public List<Row> processSearchableAttributeRowsForBasicSearch(List<Row> searchableAttributeRows) {
        return processSearchableAttributeRows(searchableAttributeRows, getBasicSearchHiddenFieldKeys());
    }

    public StandardDocSearchCriteriaManager getBasicSearchManager() {
        return buildBasicSearchManager();
    }

    public List<String> getBasicSearchHiddenFieldKeys() {
        return new ArrayList<String>();
    }

    /**
     * Standard implementation of this method is that the search criteria is always displayed
     * on a basic search so this returns true.
     *
     * @see org.kuali.rice.kew.docsearch.DocumentSearchCriteriaProcessor#isBasicSearchCriteriaDisplayed()
     */
    public Boolean isBasicSearchCriteriaDisplayed() {
        return Boolean.TRUE;
    }

    /**
     * This method returns the number of columns that should exist on a basic search
     * before the searchable attributes display in the criteria section.
     * @return one
     */
    public static int getBasicSearchPreSearchAttributesColumnSize() {
        return 1;
    }

    /**
     * This method returns the number of columns that should exist on a basic search
     * after the searchable attributes display in the criteria section.
     * @return one
     */
    public static int getBasicSearchPostSearchAttributesColumnSize() {
        return 1;
    }

    public StandardDocSearchCriteriaManager buildBasicSearchManager() {
        StandardDocSearchCriteriaManager manager = new StandardDocSearchCriteriaManager(getBasicSearchPreSearchAttributesColumnSize(),getBasicSearchPostSearchAttributesColumnSize(),isBasicSearchCriteriaDisplayed(),isHeaderBarDisplayed());
    	Map<String,StandardDocSearchCriteriaFieldContainer> containersByKey = getStandardCriteriaFieldContainerMap();
    	manager.setColumnsPreSearchAttributes(getBasicSearchPreSearchAttributeColumns(containersByKey));
    	manager.setColumnsPostSearchAttributes(getBasicSearchPostSearchAttributeColumns(containersByKey));
    	return manager;
    }

    public List<List<StandardDocSearchCriteriaFieldContainer>> getBasicSearchPreSearchAttributeColumns(Map<String,StandardDocSearchCriteriaFieldContainer> containersByKey) {
        List<List<StandardDocSearchCriteriaFieldContainer>> columnHolder = new ArrayList<List<StandardDocSearchCriteriaFieldContainer>>();
        for (int i = 1; i <= getBasicSearchPreSearchAttributesColumnSize(); i++) {
            columnHolder.add(getBasicSearchPreSearchAttributeContainerList(i, containersByKey));
        }
        return columnHolder;
    }

    public List<List<StandardDocSearchCriteriaFieldContainer>> getBasicSearchPostSearchAttributeColumns(Map<String,StandardDocSearchCriteriaFieldContainer> containersByKey) {
        List<List<StandardDocSearchCriteriaFieldContainer>> columnHolder = new ArrayList<List<StandardDocSearchCriteriaFieldContainer>>();
        for (int i = 1; i <= getBasicSearchPostSearchAttributesColumnSize(); i++) {
            columnHolder.add(getBasicSearchPostSearchAttributeContainerList(i, containersByKey));
        }
        return columnHolder;
    }

    public List<StandardDocSearchCriteriaFieldContainer> getBasicSearchPreSearchAttributeContainerList(int columnNumber, Map<String,StandardDocSearchCriteriaFieldContainer> containersByKey) {
        return getBasicSearchContainerList(containersByKey, generateHiddenFieldKeySet(getBasicSearchHiddenFieldKeys()), getBasicSearchPreSearchAttributeFieldKeys(columnNumber));
    }

    public List<StandardDocSearchCriteriaFieldContainer> getBasicSearchPostSearchAttributeContainerList(int columnNumber, Map<String,StandardDocSearchCriteriaFieldContainer> containersByKey) {
        return getBasicSearchContainerList(containersByKey, generateHiddenFieldKeySet(getBasicSearchHiddenFieldKeys()), getBasicSearchPostSearchAttributeFieldKeys(columnNumber));
    }

    public List<StandardDocSearchCriteriaFieldContainer> getBasicSearchContainerList(Map<String,StandardDocSearchCriteriaFieldContainer> containersByKey, Set<String> hiddenKeys, List<String> fieldKeys) {
        List<StandardDocSearchCriteriaFieldContainer> containers = new ArrayList<StandardDocSearchCriteriaFieldContainer>();
        boolean setWidth = false;
        for (String fieldKey : fieldKeys)
        {
            StandardDocSearchCriteriaFieldContainer container = new StandardDocSearchCriteriaFieldContainer(containersByKey.get(fieldKey));
            container.hideFieldsIfNecessary(hiddenKeys);
            if ((!setWidth) && (!container.isHidden()))
            {
                container.setLabelFieldWidthValue("22%");
                container.setDataFieldWidthValue("78%");
                setWidth = true;
            }
            containers.add(container);
        }
        return containers;
    }

    public List<String> getBasicSearchPreSearchAttributeFieldKeys(int columnNumber) {
        if (columnNumber == 1) {
            List<String> fieldKeys = new ArrayList<String>();
            fieldKeys.add(CRITERIA_KEY_DOC_TYPE_FULL_NAME);
            fieldKeys.add(CRITERIA_KEY_INITIATOR);
            fieldKeys.add(CRITERIA_KEY_DOCUMENT_ID);
            fieldKeys.add(CRITERIA_KEY_CREATE_DATE);
            return fieldKeys;
        } else {
            throw new RuntimeException("Column number given (" + columnNumber + ") is invalid for standard search");
        }
    }

    public List<String> getBasicSearchPostSearchAttributeFieldKeys(int columnNumber) {
        if (columnNumber == 1) {
            List<String> fieldKeys = new ArrayList<String>();
            fieldKeys.add(CRITERIA_KEY_NAMED_SEARCH);
            return fieldKeys;
        } else {
            throw new RuntimeException("Column number given (" + columnNumber + ") is invalid for standard search");
        }
    }

    /*
     *   ADVANCED SEARCH METHODS
     *
     */

    public List<Row> processSearchableAttributeRowsForAdvancedSearch(List<Row> searchableAttributeRows) {
        return processSearchableAttributeRows(searchableAttributeRows, getAdvancedSearchHiddenFieldKeys());
    }

    public StandardDocSearchCriteriaManager getAdvancedSearchManager() {
        return buildAdvancedSearchManager();
    }

    public List<String> getAdvancedSearchHiddenFieldKeys() {
        return new ArrayList<String>();
    }

    /**
     * Standard implementation of this method is that the search criteria is always displayed
     * on an advanced search so this returns true.
     *
     * @see org.kuali.rice.kew.docsearch.DocumentSearchCriteriaProcessor#isAdvancedSearchCriteriaDisplayed()
     */
    public Boolean isAdvancedSearchCriteriaDisplayed() {
        return Boolean.TRUE;
    }

    /**
     * This method returns the number of columns that should exist on a advanced search
     * before the searchable attributes display in the criteria section.
     * @return two
     */
    public static int getAdvancedSearchPreSearchAttributesColumnSize() {
        return 2;
    }

    /**
     * This method returns the number of columns that should exist on a advanced search
     * after the searchable attributes display in the criteria section.
     * @return one
     */
    public static int getAdvancedSearchPostSearchAttributesColumnSize() {
        return 1;
    }

    public StandardDocSearchCriteriaManager buildAdvancedSearchManager() {
        StandardDocSearchCriteriaManager manager = new StandardDocSearchCriteriaManager(getBasicSearchPreSearchAttributesColumnSize(),getBasicSearchPostSearchAttributesColumnSize(),isAdvancedSearchCriteriaDisplayed(),isHeaderBarDisplayed());
        Map<String,StandardDocSearchCriteriaFieldContainer> containersByKey = getStandardCriteriaFieldContainerMap();
        manager.setColumnsPreSearchAttributes(getAdvancedSearchPreSearchAttributeColumns(containersByKey));
        manager.setColumnsPostSearchAttributes(getAdvancedSearchPostSearchAttributeColumns(containersByKey));
        return manager;
    }

    public List<List<StandardDocSearchCriteriaFieldContainer>> getAdvancedSearchPreSearchAttributeColumns(Map<String,StandardDocSearchCriteriaFieldContainer> containersByKey) {
        List<List<StandardDocSearchCriteriaFieldContainer>> columnHolder = new ArrayList<List<StandardDocSearchCriteriaFieldContainer>>();
        for (int i = 1; i <= getAdvancedSearchPreSearchAttributesColumnSize(); i++) {
            columnHolder.add(getAdvancedSearchPreSearchAttributeContainerList(i, containersByKey));
        }
        return columnHolder;
    }

    public List<List<StandardDocSearchCriteriaFieldContainer>> getAdvancedSearchPostSearchAttributeColumns(Map<String,StandardDocSearchCriteriaFieldContainer> containersByKey) {
        List<List<StandardDocSearchCriteriaFieldContainer>> columnHolder = new ArrayList<List<StandardDocSearchCriteriaFieldContainer>>();
        for (int i = 1; i <= getAdvancedSearchPostSearchAttributesColumnSize(); i++) {
            columnHolder.add(getAdvancedSearchPostSearchAttributeContainerList(i, containersByKey));
        }
        return columnHolder;
    }

    public List<StandardDocSearchCriteriaFieldContainer> getAdvancedSearchPreSearchAttributeContainerList(int columnNumber, Map<String,StandardDocSearchCriteriaFieldContainer> containersByKey) {
        return getAdvancedSearchContainerList(containersByKey, generateHiddenFieldKeySet(getAdvancedSearchHiddenFieldKeys()), getAdvancedSearchPreSearchAttributeFieldKeys(columnNumber));
    }

    public List<StandardDocSearchCriteriaFieldContainer> getAdvancedSearchPostSearchAttributeContainerList(int columnNumber, Map<String,StandardDocSearchCriteriaFieldContainer> containersByKey) {
        return getAdvancedSearchContainerList(containersByKey, generateHiddenFieldKeySet(getAdvancedSearchHiddenFieldKeys()), getAdvancedSearchPostSearchAttributeFieldKeys(columnNumber));
    }

    public List<StandardDocSearchCriteriaFieldContainer> getAdvancedSearchContainerList(Map<String,StandardDocSearchCriteriaFieldContainer> containersByKey, Set<String> hiddenKeys, List<String> fieldKeys) {
        List<StandardDocSearchCriteriaFieldContainer> containers = new ArrayList<StandardDocSearchCriteriaFieldContainer>();
        for (String fieldKey : fieldKeys)
        {
            StandardDocSearchCriteriaFieldContainer container = new StandardDocSearchCriteriaFieldContainer(containersByKey.get(fieldKey));
            container.hideFieldsIfNecessary(hiddenKeys);
            containers.add(container);
        }
        return containers;
    }

    public List<String> getAdvancedSearchPreSearchAttributeFieldKeys(int columnNumber) {
        if (columnNumber == 1) {
            List<String> fieldKeys = new ArrayList<String>();
            fieldKeys.add(CRITERIA_KEY_INITIATOR);
            fieldKeys.add(CRITERIA_KEY_APPROVER_ID);
            fieldKeys.add(CRITERIA_KEY_DOCUMENT_ID);
            fieldKeys.add(CRITERIA_KEY_APPLICATION_DOCUMENT_ID);
            fieldKeys.add(CRITERIA_KEY_LAST_MODIFIED_DATE);
            fieldKeys.add(CRITERIA_KEY_FINALIZED_DATE);
            fieldKeys.add(CRITERIA_KEY_DOC_TYPE_FULL_NAME);
            return fieldKeys;
        } else if (columnNumber == 2) {
            List<String> fieldKeys = new ArrayList<String>();
            fieldKeys.add(CRITERIA_KEY_VIEWER_ID);
            fieldKeys.add(CRITERIA_KEY_WORKGROUP_VIEWER);
            fieldKeys.add(CRITERIA_KEY_WORKGROUP_VIEWER_ID);
            fieldKeys.add(CRITERIA_KEY_DOCUMENT_ROUTE_STATUS);
            fieldKeys.add(CRITERIA_KEY_DOCUMENT_ROUTE_NODE);
            fieldKeys.add(CRITERIA_KEY_CREATE_DATE);
            fieldKeys.add(CRITERIA_KEY_APPROVED_DATE);
            fieldKeys.add(CRITERIA_KEY_DOCUMENT_TITLE);
            return fieldKeys;
        } else {
            throw new RuntimeException("Column number given (" + columnNumber + ") is invalid for standard search");
        }
    }

    public List<String> getAdvancedSearchPostSearchAttributeFieldKeys(int columnNumber) {
        if (columnNumber == 1) {
            List<String> fieldKeys = new ArrayList<String>();
            fieldKeys.add(CRITERIA_KEY_NAMED_SEARCH);
            return fieldKeys;
        } else {
            throw new RuntimeException("Column number given (" + columnNumber + ") is invalid for standard search");
        }
    }
}
