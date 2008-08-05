/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.KEWPropertyConstants;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.clientapp.IDocHandler;
import org.kuali.rice.kew.doctype.DocumentType;
import org.kuali.rice.kew.doctype.DocumentTypeService;
import org.kuali.rice.kew.lookupable.Column;
import org.kuali.rice.kew.lookupable.Field;
import org.kuali.rice.kew.lookupable.Row;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kew.web.UrlResolver;


/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class StandardDocumentSearchResultProcessor implements DocumentSearchResultProcessor {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StandardDocumentSearchResultProcessor.class);

    private Map<String,Boolean> sortableByKey = new HashMap<String,Boolean>();
    private Map<String,String> labelsByKey = new HashMap<String,String>();
    private DocSearchCriteriaVO searchCriteria;
    private WorkflowUser searchingUser;

    /**
     * @return the searchCriteria
     */
    public DocSearchCriteriaVO getSearchCriteria() {
        return searchCriteria;
    }

    /**
     * @param searchCriteria the searchCriteria to set
     */
    public void setSearchCriteria(DocSearchCriteriaVO searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    /**
     * @return the searchingUser
     */
    public WorkflowUser getSearchingUser() {
        return searchingUser;
    }

    /**
     * @param searchingUser the searchingUser to set
     */
    public void setSearchingUser(WorkflowUser searchingUser) {
        this.searchingUser = searchingUser;
    }

    public List<Column> getCustomDisplayColumns() {
		return new ArrayList<Column>();
	}

    private List<Column> getAndSetUpCustomDisplayColumns(DocSearchCriteriaVO criteria) {
        List<Column> columns = getCustomDisplayColumns();
        for (Column column : columns) {
            for (Field field : getFields(criteria)) {
                if ( (field.getSavablePropertyName().equals(column.getKey())) && (column.getDisplayParameters().isEmpty()) ) {
                    column.setDisplayParameters(field.getDisplayParameters());
                }
            }
        }
        return columns;
    }

	public boolean getShowAllStandardFields() {
		return true;
	}

	public boolean getOverrideSearchableAttributes() {
		return false;
	}

    /**
     * Convenience method to find a specific searchable attribute
     *
     * @param name  - name of search attribute savable property name
     * @return the SearchAttributeCriteriaComponent object related to the given key name or null if component is not found
     */
    protected SearchAttributeCriteriaComponent getSearchableAttributeByFieldName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Attempted to find Searchable Attribute with blank Field name '" + name + "'");
        }
        for (Iterator iter = getSearchCriteria().getSearchableAttributes().iterator(); iter.hasNext();) {
            SearchAttributeCriteriaComponent critComponent = (SearchAttributeCriteriaComponent) iter.next();
            if (name.equals(critComponent.getFormKey())) {
                return critComponent;
            }
        }
        return null;
    }

	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.docsearch.DocumentSearchResultProcessor#processIntoFinalResults(java.util.List, org.kuali.rice.kew.docsearch.DocSearchCriteriaVO, org.kuali.rice.kew.user.WorkflowUser)
	 */
	public DocumentSearchResultComponents processIntoFinalResults(List<DocSearchVO> docSearchResultRows, DocSearchCriteriaVO criteria, WorkflowUser user) {
        this.setSearchCriteria(criteria);
        this.setSearchingUser(user);
		List columns = constructColumnList(criteria);

		List<DocumentSearchResult> documentSearchResults = new ArrayList<DocumentSearchResult>();
		for (Iterator iter = docSearchResultRows.iterator(); iter.hasNext();) {
			DocSearchVO docSearchVO = (DocSearchVO) iter.next();
			DocumentSearchResult docSearchResult = this.generateSearchResult(docSearchVO,columns);
			if (docSearchResult != null) {
				documentSearchResults.add(docSearchResult);
			}
		}
		return new DocumentSearchResultComponents(columns,documentSearchResults);
	}

	/**
	 * Method to construct a list of columns in order of how they should appear in the search results
	 *
	 * @return a list of columns in an ordered list that will be used to generate the final search results
	 */
	public List<Column> constructColumnList(DocSearchCriteriaVO criteria) {
		List<Column> tempColumns = new ArrayList<Column>();
		List<Column> customDisplayColumnNames = getAndSetUpCustomDisplayColumns(criteria);
        if ((!getShowAllStandardFields()) && (getOverrideSearchableAttributes())) {
			// use only what is contained in displayColumns
			this.addAllCustomColumns(tempColumns, criteria, customDisplayColumnNames);
		} else if (getShowAllStandardFields() && (getOverrideSearchableAttributes())) {
			// do standard fields and use displayColumns for searchable attributes
			this.addStandardSearchColumns(tempColumns);
			this.addAllCustomColumns(tempColumns, criteria, customDisplayColumnNames);
		} else if ((!getShowAllStandardFields()) && (!getOverrideSearchableAttributes())) {
			// do displayColumns and then do standard searchable attributes
			this.addCustomStandardCriteriaColumns(tempColumns, criteria, customDisplayColumnNames);
			this.addSearchableAttributeColumnsNoOverrides(tempColumns,criteria);
		}
		if (tempColumns.isEmpty()) {
			// do default
			this.addStandardSearchColumns(tempColumns);
			this.addSearchableAttributeColumnsNoOverrides(tempColumns,criteria);
		}

		List<Column> columns = new ArrayList<Column>();
		this.addRouteHeaderIdColumn(columns);
		columns.addAll(tempColumns);
		this.addRouteLogColumn(columns);
		return columns;
	}

	public void addStandardSearchColumns(List<Column> columns) {
		this.addColumnUsingKey(columns, KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL);
		this.addColumnUsingKey(columns, KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE);
		this.addColumnUsingKey(columns, KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC);
		this.addColumnUsingKey(columns, KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR);
		this.addColumnUsingKey(columns, KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED);
	}

	public void addRouteHeaderIdColumn(List<Column> columns) {
		this.addColumnUsingKey(columns, KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_HEADER_ID);
	}

	public void addRouteLogColumn(List<Column> columns) {
		this.addColumnUsingKey(columns, KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG);
	}

	public void addSearchableAttributeColumnsNoOverrides(List<Column> columns,DocSearchCriteriaVO criteria) {
        this.addSearchableAttributeColumnsBasedOnFields(columns, criteria, null);
	}

    protected void addSearchableAttributeColumnsBasedOnFields(List<Column> columns,DocSearchCriteriaVO criteria,List<String> searchAttributeFieldNames) {
        Set<String> alreadyProcessedFieldKeys = new HashSet<String>();
        List<Field> fields = this.getFields(criteria, searchAttributeFieldNames);
        for (Field field : fields) {
            if ( (field.getSavablePropertyName() == null) || (!alreadyProcessedFieldKeys.contains(field.getSavablePropertyName())) ) {
                if (field.isColumnVisible()) {
                    if (Field.SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.contains(field.getFieldType())) {
                        String resultFieldLabel = field.getFieldLabel();
                        if (field.isMemberOfRange()) {
                            resultFieldLabel = field.getMainFieldLabel();
                        }
                        this.addSearchableAttributeColumnUsingKey(columns, field.getDisplayParameters(), field.getSavablePropertyName(), resultFieldLabel, getSortableByKey().get(field.getSavablePropertyName()), Boolean.TRUE);
                        if (field.getSavablePropertyName() != null) {
                            alreadyProcessedFieldKeys.add(field.getSavablePropertyName());
                        }
                    }
                }
            }
        }
    }

	public void addAllCustomColumns(List<Column> columns,DocSearchCriteriaVO criteria,List<Column> customDisplayColumns) {
		for (Column customColumn : customDisplayColumns) {
			this.addCustomColumn(columns,customColumn);
		}
	}

	public void addCustomStandardCriteriaColumns(List<Column> columns,DocSearchCriteriaVO criteria,List<Column> customDisplayColumns) {
		for (Column customColumn : customDisplayColumns) {
			if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_SET.contains(customColumn.getKey())) {
				this.addCustomColumn(columns,customColumn);
			}
		}
	}

	public void addCustomColumn(List<Column> columns,Column customColumn) {
		Boolean sortable = null;
		if ( (customColumn.getSortable() != null) && (Column.COLUMN_IS_SORTABLE_VALUE.equals(customColumn.getSortable())) ) {
			sortable =  Boolean.TRUE;
		} else if ( (customColumn.getSortable() != null) && (Column.COLUMN_NOT_SORTABLE_VALUE.equals(customColumn.getSortable())) ) {
			sortable = Boolean.FALSE;
		}
		addColumnUsingKey(columns, customColumn.getDisplayParameters(), customColumn.getKey(), customColumn.getColumnTitle(), sortable);
	}

	private List<Field> getFields(DocSearchCriteriaVO criteria) {
	    return getFields(criteria, null);
	}
	
    private DocumentType getDocumentType(String documentTypeName) {
	DocumentType documentType = null;
	if (StringUtils.isNotBlank(documentTypeName)) {
	    documentType = ((DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
	}
	return documentType;
    }

    private List<Field> getFields(DocSearchCriteriaVO criteria, List<String> searchAttributeFieldNames) {
		List<Field> returnFields = new ArrayList<Field>();
		DocumentType documentType = getDocumentType(criteria.getDocTypeFullName());
		if (documentType != null) {
            List<Field> allFields = new ArrayList<Field>();
            for (SearchableAttribute searchableAttribute : documentType.getSearchableAttributes()) {
                List<Row> searchRows = searchableAttribute.getSearchingRows();
                if (searchRows == null) {
                    continue;
                }
                for (Row row : searchRows) {
                    allFields.addAll(row.getFields());
                }
            }
            if (searchAttributeFieldNames == null) {
                returnFields = allFields;
            } else {
                for (String searchAttributeName : searchAttributeFieldNames) {
                    for (Field field : allFields) {
                        if (searchAttributeName.equals(field.getSavablePropertyName())) {
                            returnFields.add(field);
                        }
                    }
                }
            }
		}
		return returnFields;
	}

	public DocumentSearchResult generateSearchResult(DocSearchVO docSearchVO, List columns) {
		Map<String,Object> alternateSortValues = getSortValuesMap(docSearchVO);
		DocumentSearchResult docSearchResult = null;
		for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
			Column currentColumn = (Column) iterator.next();
			KeyValueSort kvs = generateSearchResult(docSearchVO,currentColumn,alternateSortValues);
			if (kvs != null) {
				if (docSearchResult == null) {
					docSearchResult = new DocumentSearchResult();
				}
				docSearchResult.addResultContainer(kvs);
			}
		}
		return docSearchResult;
	}
	
	protected class DisplayValues {
		public String htmlValue;
		public String userDisplayValue;
	}

	public KeyValueSort generateSearchResult(DocSearchVO docSearchVO, Column column, Map<String,Object> sortValuesByColumnKey) {
		KeyValueSort returnValue = null;
		DisplayValues fieldValue = null;
		Object sortFieldValue = null;
		String columnKeyName = column.getKey();
		SearchableAttributeValue attributeValue = null;

		if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_HEADER_ID.equals(columnKeyName)) {
			fieldValue = this.getRouteHeaderIdFieldDisplayValue(docSearchVO.getRouteHeaderId().toString(), docSearchVO.isUsingSuperUserSearch(), docSearchVO.getDocTypeName());
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG.equals(columnKeyName)) {
			fieldValue = this.getRouteLogFieldDisplayValue(docSearchVO.getRouteHeaderId().toString());
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED.equals(columnKeyName)) {
			fieldValue = new DisplayValues();
			fieldValue.htmlValue = DocSearchUtils.getDisplayValueWithDateTime(docSearchVO.getDateCreated());
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL.equals(columnKeyName)) {
			fieldValue = new DisplayValues();
			fieldValue.htmlValue = docSearchVO.getDocTypeLabel();
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE.equals(columnKeyName)) {
			fieldValue = new DisplayValues();
			fieldValue.htmlValue = docSearchVO.getDocumentTitle();
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR.equals(columnKeyName)) {
			fieldValue = this.getInitiatorFieldDisplayValue(docSearchVO.getInitiatorTransposedName(), docSearchVO.getInitiatorWorkflowId());
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC.equals(columnKeyName)) {
			fieldValue = new DisplayValues();
			fieldValue.htmlValue = docSearchVO.getDocRouteStatusCodeDesc();
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else {
			// check searchable attributes
			for (Iterator iter = docSearchVO.getSearchableAttributes().iterator(); iter.hasNext();) {
				KeyValueSort searchAttribute = (KeyValueSort) iter.next();
				if (searchAttribute.getKey().equals(columnKeyName)) {
					Object sortValue = sortValuesByColumnKey.get(columnKeyName);
					sortFieldValue = (sortValue != null) ? sortValue : searchAttribute.getSortValue();
					attributeValue = searchAttribute.getSearchableAttributeValue();
					if ( (column.getDisplayParameters() != null) && (!column.getDisplayParameters().isEmpty()) ) {
					    fieldValue = new DisplayValues();
						fieldValue.htmlValue = searchAttribute.getSearchableAttributeValue().getSearchableAttributeDisplayValue(column.getDisplayParameters());
					}
					else {
					    fieldValue = new DisplayValues();
						fieldValue.htmlValue = searchAttribute.getValue();
					}
					break;
				}
			}
		}
		if (fieldValue != null) {
		    returnValue = new KeyValueSort(columnKeyName,fieldValue.htmlValue,fieldValue.userDisplayValue,(sortFieldValue != null) ? sortFieldValue : fieldValue, attributeValue);
		}
		return returnValue;
	}

	/*
	 * Convenience Methods to get field values for certain Workflow Standard
	 * Search Result columns
	 */

	protected DisplayValues getRouteLogFieldDisplayValue(String routeHeaderId) {
		DisplayValues dv = new DisplayValues();
		String linkPopup = "";
		if (this.isRouteLogPopup()) {
			linkPopup = " target=\"_new\"";
		}
		String imageSource = "<img alt=\"Route Log for Document\" src=\"images/my_route_log.gif\"/>";
		dv.htmlValue = "<a href=\"RouteLog.do?routeHeaderId=" + routeHeaderId + "\"" + linkPopup + ">" + imageSource + "</a>";
		dv.userDisplayValue = imageSource;
		return dv;
	}

	protected DisplayValues getRouteHeaderIdFieldDisplayValue(String routeHeaderId,boolean isSuperUserSearch, String documentTypeName) {
		return this.getValueEncodedWithDocHandlerUrl(routeHeaderId, routeHeaderId, isSuperUserSearch, documentTypeName);
	}

	protected DisplayValues getInitiatorFieldDisplayValue(String fieldLinkTextValue, String initiatorWorkflowId) {
		UrlResolver urlResolver = new UrlResolver();
		DisplayValues dv = new DisplayValues();
		dv.htmlValue = "<a href=\"" + urlResolver.getUserReportUrl() +  "?showEdit=no&methodToCall=report&workflowId=" + initiatorWorkflowId + "\" target=\"_blank\">" + fieldLinkTextValue + "</a>";
		dv.userDisplayValue = fieldLinkTextValue;
		return dv;
	}

	/**
	 * Convenience method to allow child classes to use a custom value string and wrap
	 * that string in the document handler URL
	 *
	 * @param value - the value that will show on screen as the clickable link
	 * @param routeHeaderId - the string value of the route header id the doc handler should point to
	 * @param isSuperUserSearch - boolean indicating whether this search is a super user search or not
	 *        see {@link org.kuali.rice.kew.docsearch.DocSearchVO#isUsingSuperUserSearch()}
	 * @return the fully encoded html for a link using the text from the input parameter 'value'
	 */
	protected DisplayValues getValueEncodedWithDocHandlerUrl(String value, String routeHeaderId, boolean isSuperUserSearch, String documentTypeName) {
		DisplayValues dv = new DisplayValues();
		dv.htmlValue = getDocHandlerUrlPrefix(routeHeaderId,isSuperUserSearch,documentTypeName) + value + getDocHandlerUrlSuffix(isSuperUserSearch);
		dv.userDisplayValue = value;
		return dv; 
	}

	private Map<String,Object> getSortValuesMap(DocSearchVO docSearchVO) {
		Map<String, Object> alternateSort = new HashMap<String, Object>();
		alternateSort.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_HEADER_ID, docSearchVO.getRouteHeaderId());
		alternateSort.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR, docSearchVO.getInitiatorTransposedName());
		alternateSort.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED, docSearchVO.getDateCreated());
		return alternateSort;
	}

	public Map<String,Boolean> getSortableByKey() {
		if (sortableByKey.isEmpty()) {
			sortableByKey = constructSortableByKey();
		}
		return sortableByKey;
	}

	protected Map<String,Boolean> constructSortableColumnByKey() {
		Map<String,Boolean> sortable = new HashMap<String,Boolean>();
		sortable.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_HEADER_ID, Boolean.TRUE);
		sortable.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL, Boolean.TRUE);
		sortable.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE, Boolean.TRUE);
		sortable.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC, Boolean.TRUE);
		sortable.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR, Boolean.TRUE);
		sortable.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED, Boolean.TRUE);
		sortable.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG, Boolean.FALSE);
		return sortable;
	}

	public Map<String,Boolean> getSortableColumnByKey() {
		if (sortableByKey.isEmpty()) {
			sortableByKey = constructSortableByKey();
		}
		return sortableByKey;
	}

	protected Map<String,Boolean> constructSortableByKey() {
		Map<String,Boolean> sortable = new HashMap<String,Boolean>();
		sortable.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_HEADER_ID, Boolean.TRUE);
		sortable.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL, Boolean.TRUE);
		sortable.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE, Boolean.TRUE);
		sortable.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC, Boolean.TRUE);
		sortable.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR, Boolean.TRUE);
		sortable.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED, Boolean.TRUE);
		sortable.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG, Boolean.FALSE);
		return sortable;
	}

	public Map<String,String> getLabelsByKey() {
		if (labelsByKey.isEmpty()) {
			labelsByKey = constructLabelsByKey();
		}
		return labelsByKey;
	}

	protected Map<String,String> constructLabelsByKey() {
		return new HashMap<String,String>();
	}

	/*
	 * Below columns are for convenience for overriding classes
	 *
	 */

	protected void addColumnUsingKey(List<Column> columns,String key) {
		this.addColumnUsingKey(columns, new HashMap<String,String>(), key, null, null);
	}

	protected void addColumnUsingKey(List<Column> columns,Map<String,String> displayParameters,String key,String label) {
		this.addColumnUsingKey(columns, displayParameters, key, label, null);
	}

	protected void addColumnUsingKey(List<Column> columns,Map<String,String> displayParameters,String key,Boolean sortable) {
		this.addColumnUsingKey(columns, displayParameters, key, null, sortable);
	}

	protected void addColumnUsingKey(List<Column> columns,Map<String,String> displayParameters,String key,String label,Boolean sortable) {
		columns.add(this.constructColumnUsingKey(displayParameters, key, label, sortable));
	}

	protected void addSearchableAttributeColumnUsingKey(List<Column> columns,String key,String label,Boolean sortableOverride, Boolean defaultSortable) {
	    addSearchableAttributeColumnUsingKey(columns, new HashMap<String,String>(), key, label, sortableOverride, defaultSortable);
	}

	protected void addSearchableAttributeColumnUsingKey(List<Column> columns,Map<String,String> displayParameters,String key,String label,Boolean sortableOverride, Boolean defaultSortable) {
	    columns.add(this.constructColumnUsingKey(displayParameters, key, label, (sortableOverride != null) ? sortableOverride : defaultSortable));
	}


	/*
	 * Below methods should probably not be overriden by overriding classes but could be if desired
	 */

	protected Column constructColumnUsingKey(Map<String,String> displayParameters, String key,String label,Boolean sortable) {
		if (sortable == null) {
			sortable = getSortableByKey().get(key);
		}
		if (label == null) {
			label = getLabelsByKey().get(key);
		}
		Column c = new Column(label,((sortable != null) && (sortable.booleanValue())) ? Column.COLUMN_IS_SORTABLE_VALUE : Column.COLUMN_NOT_SORTABLE_VALUE,"resultContainer(" +key + ").value","resultContainer(" +key + ").sortValue",key,displayParameters);
		return c;
	}

	private boolean isDocumentHandlerPopup() {
		String applicationConstant = Utilities.getApplicationConstant(KEWConstants.DOCUMENT_SEARCH_DOCUMENT_POPUP_KEY).trim();
		return (KEWConstants.DOCUMENT_SEARCH_DOCUMENT_POPUP_VALUE.equals(applicationConstant));
	}

	private boolean isRouteLogPopup() {
		String applicationConstant = Utilities.getApplicationConstant(KEWConstants.DOCUMENT_SEARCH_ROUTE_LOG_POPUP_KEY).trim();
		return (KEWConstants.DOCUMENT_SEARCH_ROUTE_LOG_POPUP_VALUE.equals(applicationConstant));
	}

	private String getDocHandlerUrlPrefix(String routeHeaderId,boolean superUserSearch,String documentTypeName) {
		String linkPopup = "";
		if (this.isDocumentHandlerPopup()) {
			linkPopup = " target=\"_blank\"";
		}
		if (superUserSearch) {
		    String url = "<a href=\"SuperUser.do?methodToCall=displaySuperUserDocument&routeHeaderId=" + routeHeaderId + "\"" + linkPopup + " >";
		    if (!getDocumentType(documentTypeName).getUseWorkflowSuperUserDocHandlerUrl().getPolicyValue().booleanValue()) {
			url = "<a href=\"" + KEWConstants.DOC_HANDLER_REDIRECT_PAGE + "?" + IDocHandler.COMMAND_PARAMETER + "=" + IDocHandler.SUPERUSER_COMMAND + "&" + IDocHandler.ROUTEHEADER_ID_PARAMETER + "=" + routeHeaderId + "\"" + linkPopup + ">";
		    }
		    return url;
		} else {
			return "<a href=\"" + KEWConstants.DOC_HANDLER_REDIRECT_PAGE + "?" + IDocHandler.COMMAND_PARAMETER + "=" + IDocHandler.DOCSEARCH_COMMAND + "&" + IDocHandler.ROUTEHEADER_ID_PARAMETER + "=" + routeHeaderId + "\"" + linkPopup + ">";
		}
	}

	private String getDocHandlerUrlSuffix(boolean superUserSearch) {
		if (superUserSearch) {
			return "</a>";
		} else {
			return "</a>";
		}
	}
}
