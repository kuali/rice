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
package edu.iu.uis.eden.docsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.lookupable.Column;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.KeyValueSort;
import edu.iu.uis.eden.web.UrlResolver;

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
	 * @see edu.iu.uis.eden.docsearch.DocumentSearchResultProcessor#processIntoFinalResults(java.util.List, edu.iu.uis.eden.docsearch.DocSearchCriteriaVO, edu.iu.uis.eden.user.WorkflowUser)
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
//		if (!customDisplayColumnNames.isEmpty()) {
            if ((!getShowAllStandardFields()) && (getOverrideSearchableAttributes())) {
				// use only what is contained in displayColumns
				this.addAllCustomColumns(tempColumns, criteria, customDisplayColumnNames);
			} else if (getShowAllStandardFields() && (getOverrideSearchableAttributes())) {
				// do standard fields and use displayColumns for searchable attributes
				this.addStandardSearchColumns(tempColumns);
//				this.addCustomSearchAttributeColumns(tempColumns, criteria, customDisplayColumnNames);
				this.addAllCustomColumns(tempColumns, criteria, customDisplayColumnNames);
			} else if ((!getShowAllStandardFields()) && (!getOverrideSearchableAttributes())) {
				// do displayColumns and then do standard searchable attributes
				this.addCustomStandardCriteriaColumns(tempColumns, criteria, customDisplayColumnNames);
				this.addSearchableAttributeColumnsNoOverrides(tempColumns,criteria);
			}
//		}
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
		this.addColumnUsingKey(columns, DocumentSearchResult.PROPERTY_NAME_DOC_TYPE_LABEL);
		this.addColumnUsingKey(columns, DocumentSearchResult.PROPERTY_NAME_DOCUMENT_TITLE);
		this.addColumnUsingKey(columns, DocumentSearchResult.PROPERTY_NAME_ROUTE_STATUS_DESC);
		this.addColumnUsingKey(columns, DocumentSearchResult.PROPERTY_NAME_INITIATOR);
		this.addColumnUsingKey(columns, DocumentSearchResult.PROPERTY_NAME_DATE_CREATED);
	}

	public void addRouteHeaderIdColumn(List<Column> columns) {
		this.addColumnUsingKey(columns, DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID);
	}

	public void addRouteLogColumn(List<Column> columns) {
		this.addColumnUsingKey(columns, DocumentSearchResult.PROPERTY_NAME_ROUTE_LOG);
	}

	public void addSearchableAttributeColumnsNoOverrides(List<Column> columns,DocSearchCriteriaVO criteria) {
        this.addSearchableAttributeColumnsBasedOnFields(columns, criteria, null);
//		Set alreadyProcessedFieldKeys = new HashSet();
//		List<Field> fields = this.getFields(criteria, null);
//		for (Field field : fields) {
//			if ( (field.getSavablePropertyName() == null) || (!alreadyProcessedFieldKeys.contains(field.getSavablePropertyName())) ) {
//				if (field.isColumnVisible()) {
//					for (Iterator iter = Field.SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.iterator(); iter.hasNext();) {
//						String displayableFieldType = (String) iter.next();
//						if (field.getFieldType().equals(displayableFieldType)) {
//                            String resultFieldLabel = field.getFieldLabel();
//                            if (field.isMemberOfRange()) {
//                                resultFieldLabel = field.getMainFieldLabel();
//                            }
//							this.addSearchableAttributeColumnUsingKey(columns, field.getSavablePropertyName(), resultFieldLabel, getSortableByKey().get(field.getSavablePropertyName()), Boolean.TRUE);
//							if (field.getSavablePropertyName() != null) {
//								alreadyProcessedFieldKeys.add(field.getSavablePropertyName());
//							}
//							break;
//						}
//					}
//				}
//			}
//		}
	}

    protected void addSearchableAttributeColumnsBasedOnFields(List<Column> columns,DocSearchCriteriaVO criteria,List<String> searchAttributeFieldNames) {
	Set alreadyProcessedFieldKeys = new HashSet();
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

    protected void addSearchableAttributeColumnUsingField() {

    }

	public void addAllCustomColumns(List<Column> columns,DocSearchCriteriaVO criteria,List<Column> customDisplayColumns) {
		for (Column customColumn : customDisplayColumns) {
			this.addCustomColumn(columns,customColumn);
		}
	}

	public void addCustomStandardCriteriaColumns(List<Column> columns,DocSearchCriteriaVO criteria,List<Column> customDisplayColumns) {
		for (Column customColumn : customDisplayColumns) {
			if (DocumentSearchResult.PROPERTY_NAME_SET.contains(customColumn.getKey())) {
				this.addCustomColumn(columns,customColumn);
			}
		}
	}

//	public void addCustomSearchAttributeColumns(List<Column> columns,DocSearchCriteriaVO criteria,List<Column> customDisplayColumns) {
//		for (Column customColumn : customDisplayColumns) {
//			if (!DocumentSearchResult.PROPERTY_NAME_SET.contains(customColumn.getKey())) {
//				this.addCustomColumn(columns,customColumn);
//			}
//		}
//	}

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

    private List<Field> getFields(DocSearchCriteriaVO criteria, List<String> searchAttributeFieldNames) {
		List<Field> returnFields = new ArrayList<Field>();
		DocumentType documentType = null;
		if (StringUtils.isNotBlank(criteria.getDocTypeFullName())) {
			documentType = ((DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(criteria.getDocTypeFullName());
		}
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

	public KeyValueSort generateSearchResult(DocSearchVO docSearchVO, Column column, Map<String,Object> sortValuesByColumnKey) {
		KeyValueSort returnValue = null;
		String fieldValue = null;
		Object sortFieldValue = null;
		String columnKeyName = column.getKey();
		SearchableAttributeValue attributeValue = null;

		if (DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID.equals(columnKeyName)) {
			fieldValue = this.getRouteHeaderIdFieldDisplayValue(docSearchVO.getRouteHeaderId().toString(), docSearchVO.isUsingSuperUserSearch());
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (DocumentSearchResult.PROPERTY_NAME_ROUTE_LOG.equals(columnKeyName)) {
			fieldValue = this.getRouteLogFieldDisplayValue(docSearchVO.getRouteHeaderId().toString());
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (DocumentSearchResult.PROPERTY_NAME_DATE_CREATED.equals(columnKeyName)) {
			fieldValue = DocSearchUtils.getDisplayValueWithDateTime(docSearchVO.getDateCreated());
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (DocumentSearchResult.PROPERTY_NAME_DOC_TYPE_LABEL.equals(columnKeyName)) {
			fieldValue = docSearchVO.getDocTypeLabel();
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (DocumentSearchResult.PROPERTY_NAME_DOCUMENT_TITLE.equals(columnKeyName)) {
			fieldValue = docSearchVO.getDocumentTitle();
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (DocumentSearchResult.PROPERTY_NAME_INITIATOR.equals(columnKeyName)) {
			fieldValue = this.getInitiatorFieldDisplayValue(docSearchVO.getInitiatorTransposedName(), docSearchVO.getInitiatorWorkflowId());
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (DocumentSearchResult.PROPERTY_NAME_ROUTE_STATUS_DESC.equals(columnKeyName)) {
			fieldValue = docSearchVO.getDocRouteStatusCodeDesc();
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
					    fieldValue = searchAttribute.getSearchableAttributeValue().getSearchableAttributeDisplayValue(column.getDisplayParameters());
					}
					else {
					    fieldValue = searchAttribute.getValue();
					}
					break;
				}
			}
		}
		if (fieldValue != null) {
		    returnValue = new KeyValueSort(columnKeyName,fieldValue,(sortFieldValue != null) ? sortFieldValue : fieldValue, attributeValue);
		}
		return returnValue;
	}

	/*
	 * Convenience Methods to get field values for certain Workflow Standard
	 * Search Result columns
	 */

	protected String getRouteLogFieldDisplayValue(String routeHeaderId) {
		String linkPopup = "";
		if (this.isRouteLogPopup()) {
			linkPopup = " target=\"_new\"";
		}
		return "<a href=\"RouteLog.do?routeHeaderId=" + routeHeaderId + "\"" + linkPopup + "><img alt=\"Route Log for Document\" src=\"images/my_route_log.gif\"/></a>";
	}

	protected String getRouteHeaderIdFieldDisplayValue(String routeHeaderId,boolean isSuperUserSearch) {
		return this.getValueEncodedWithDocHandlerUrl(routeHeaderId, routeHeaderId, isSuperUserSearch);
	}

	protected String getInitiatorFieldDisplayValue(String fieldLinkTextValue, String initiatorWorkflowId) {
		UrlResolver urlResolver = new UrlResolver();
		return "<a href=\"" + urlResolver.getUserReportUrl() +  "?showEdit=no&methodToCall=report&workflowId=" + initiatorWorkflowId + "\" target=\"_blank\">" + fieldLinkTextValue + "</a>";
	}

	/**
	 * Convenience method to allow child classes to use a custom value string and wrap
	 * that string in the document handler URL
	 *
	 * @param value - the value that will show on screen as the clickable link
	 * @param routeHeaderId - the string value of the route header id the doc handler should point to
	 * @param isSuperUserSearch - boolean indicating whether this search is a super user search or not
	 *        see {@link edu.iu.uis.eden.docsearch.DocSearchVO#isUsingSuperUserSearch()}
	 * @return the fully encoded html for a link using the text from the input parameter 'value'
	 */
	protected String getValueEncodedWithDocHandlerUrl(String value, String routeHeaderId, boolean isSuperUserSearch) {
		return getDocHandlerUrlPrefix(routeHeaderId,isSuperUserSearch) + value + getDocHandlerUrlSuffix(isSuperUserSearch);
	}

	private Map<String,Object> getSortValuesMap(DocSearchVO docSearchVO) {
		Map<String, Object> alternateSort = new HashMap<String, Object>();
		alternateSort.put(DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID, docSearchVO.getRouteHeaderId());
		alternateSort.put(DocumentSearchResult.PROPERTY_NAME_INITIATOR, docSearchVO.getInitiatorTransposedName());
		alternateSort.put(DocumentSearchResult.PROPERTY_NAME_DATE_CREATED, docSearchVO.getDateCreated());
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
		sortable.put(DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID, Boolean.TRUE);
		sortable.put(DocumentSearchResult.PROPERTY_NAME_DOC_TYPE_LABEL, Boolean.TRUE);
		sortable.put(DocumentSearchResult.PROPERTY_NAME_DOCUMENT_TITLE, Boolean.TRUE);
		sortable.put(DocumentSearchResult.PROPERTY_NAME_ROUTE_STATUS_DESC, Boolean.TRUE);
		sortable.put(DocumentSearchResult.PROPERTY_NAME_INITIATOR, Boolean.TRUE);
		sortable.put(DocumentSearchResult.PROPERTY_NAME_DATE_CREATED, Boolean.TRUE);
		sortable.put(DocumentSearchResult.PROPERTY_NAME_ROUTE_LOG, Boolean.FALSE);
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
		sortable.put(DocumentSearchResult.PROPERTY_NAME_ROUTE_HEADER_ID, Boolean.TRUE);
		sortable.put(DocumentSearchResult.PROPERTY_NAME_DOC_TYPE_LABEL, Boolean.TRUE);
		sortable.put(DocumentSearchResult.PROPERTY_NAME_DOCUMENT_TITLE, Boolean.TRUE);
		sortable.put(DocumentSearchResult.PROPERTY_NAME_ROUTE_STATUS_DESC, Boolean.TRUE);
		sortable.put(DocumentSearchResult.PROPERTY_NAME_INITIATOR, Boolean.TRUE);
		sortable.put(DocumentSearchResult.PROPERTY_NAME_DATE_CREATED, Boolean.TRUE);
		sortable.put(DocumentSearchResult.PROPERTY_NAME_ROUTE_LOG, Boolean.FALSE);
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
		String applicationConstant = Utilities.getApplicationConstant(EdenConstants.DOCUMENT_SEARCH_DOCUMENT_POPUP_KEY).trim();
		return (EdenConstants.DOCUMENT_SEARCH_DOCUMENT_POPUP_VALUE.equals(applicationConstant));
	}

	private boolean isRouteLogPopup() {
		String applicationConstant = Utilities.getApplicationConstant(EdenConstants.DOCUMENT_SEARCH_ROUTE_LOG_POPUP_KEY).trim();
		return (EdenConstants.DOCUMENT_SEARCH_ROUTE_LOG_POPUP_VALUE.equals(applicationConstant));
	}

	private String getDocHandlerUrlPrefix(String routeHeaderId,boolean superUserSearch) {
		String linkPopup = "";
		if (this.isDocumentHandlerPopup()) {
			linkPopup = " target=\"_blank\"";
		}
		if (superUserSearch) {
			return "<a href=\"SuperUser.do?methodToCall=displaySuperUserDocument&routeHeaderId=" + routeHeaderId + "\"" + linkPopup + " >";
		} else {
			return "<a href=\"" + EdenConstants.DOC_HANDLER_REDIRECT_PAGE + "?command=displayDocSearchView&docId=" + routeHeaderId + "\"" + linkPopup + ">";
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
