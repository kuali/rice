/*
 * Copyright 2006-2011 The Kuali Foundation
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
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupConfiguration;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.KEWPropertyConstants;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StandardDocumentSearchResultProcessor implements
		DocumentSearchResultProcessor {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(StandardDocumentSearchResultProcessor.class);

	private Map<String, Boolean> sortableByKey = new HashMap<String, Boolean>();
	private Map<String, String> labelsByKey = new HashMap<String, String>();
	private DocSearchCriteriaDTO searchCriteria;
	private String searchingUser;
	private boolean processFinalResults = true;

	/**
	 * @return the searchCriteria
	 */
	public DocSearchCriteriaDTO getSearchCriteria() {
		return searchCriteria;
	}

	/**
	 * @param searchCriteria
	 *            the searchCriteria to set
	 */
	public void setSearchCriteria(DocSearchCriteriaDTO searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	/**
	 * @return the searchingUser
	 */
	public String getSearchingUser() {
		return searchingUser;
	}

	/**
	 * @param searchingUser
	 *            the searchingUser to set
	 */
	public void setSearchingUser(String searchingUser) {
		this.searchingUser = searchingUser;
	}

	public List<Column> getCustomDisplayColumns() {
		return new ArrayList<Column>();
	}

	public List<Column> setUpCustomDisplayColumns(
			DocSearchCriteriaDTO criteria, List<Column> columns) {
		for (Column column : columns) {
			if (column instanceof Column) {
				Column dsColumn = (Column) column;
				for (Field field : getFields(criteria)) {
					if (field instanceof Field) {
						Field dsField = (Field) field;
						dsColumn.setFormatter((Formatter)dsField.getFormatter());
					} else {
						throw new RiceRuntimeException(
								"field must be of type org.kuali.rice.kew.docsearch.Field");
					}
				}
			} else {
				throw new RiceRuntimeException(
						"column must be of type org.kuali.rice.kew.docsearch.DocumentSearchColumn");
			}
		}
		return columns;
	}

	public List<Column> getAndSetUpCustomDisplayColumns(
			DocSearchCriteriaDTO criteria) {
		List<Column> columns = getCustomDisplayColumns();
		return setUpCustomDisplayColumns(criteria, columns);
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
	 * @param name
	 *            - name of search attribute savable property name
	 * @return the SearchAttributeCriteriaComponent object related to the given
	 *         key name or null if component is not found
	 */
	public SearchAttributeCriteriaComponent getSearchableAttributeByFieldName(
			String name) {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException(
					"Attempted to find Searchable Attribute with blank Field name '"
							+ name + "'");
		}
		for (Iterator iter = getSearchCriteria().getSearchableAttributes()
				.iterator(); iter.hasNext();) {
			SearchAttributeCriteriaComponent critComponent = (SearchAttributeCriteriaComponent) iter
					.next();
			if (name.equals(critComponent.getFormKey())) {
				return critComponent;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @seeorg.kuali.rice.kew.docsearch.DocumentSearchResultProcessor#
	 * processIntoFinalResults(java.util.List,
	 * org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO,
	 * org.kuali.rice.kew.user.WorkflowUser)
	 */
	public DocumentSearchResultComponents processIntoFinalResults(
			List<DocSearchDTO> docSearchResultRows,
			DocSearchCriteriaDTO criteria, String principalId) {
		this.setSearchCriteria(criteria);
		this.setSearchingUser(principalId);
		List columns = constructColumnList(criteria, docSearchResultRows);

		List<DocumentSearchResult> documentSearchResults = new ArrayList<DocumentSearchResult>();
		for (Iterator iter = docSearchResultRows.iterator(); iter.hasNext();) {
			DocSearchDTO docCriteriaDTO = (DocSearchDTO) iter.next();
			DocumentSearchResult docSearchResult = this.generateSearchResult(
					docCriteriaDTO, columns);
			if (docSearchResult != null) {
				documentSearchResults.add(docSearchResult);
			}
		}
		return new DocumentSearchResultComponents(columns,
				documentSearchResults);
	}

	/**
	 * Method to construct a list of columns in order of how they should appear
	 * in the search results
	 *
	 * @return a list of columns in an ordered list that will be used to
	 *         generate the final search results
	 */
	public List<Column> constructColumnList(
			DocSearchCriteriaDTO criteria, List<DocSearchDTO> docSearchResultRows) {
		List<Column> tempColumns = new ArrayList<Column>();
		List<Column> customDisplayColumnNames = getAndSetUpCustomDisplayColumns(criteria);
		if ((!getShowAllStandardFields()) && (getOverrideSearchableAttributes())) {
			// use only what is contained in displayColumns
			this.addAllCustomColumns(tempColumns, criteria, customDisplayColumnNames);
		} else if (getShowAllStandardFields() && (getOverrideSearchableAttributes())) {
			// do standard fields and use displayColumns for searchable
			// attributes
			this.addStandardSearchColumns(tempColumns, docSearchResultRows);
			this.addAllCustomColumns(tempColumns, criteria, customDisplayColumnNames);
		} else if ((!getShowAllStandardFields()) && (!getOverrideSearchableAttributes())) {
			// do displayColumns and then do standard searchable attributes
			this.addCustomStandardCriteriaColumns(tempColumns, criteria, customDisplayColumnNames);
			this.addSearchableAttributeColumnsNoOverrides(tempColumns, criteria);
		}
		if (tempColumns.isEmpty()) {
			// do default
			this.addStandardSearchColumns(tempColumns, docSearchResultRows);
			this.addSearchableAttributeColumnsNoOverrides(tempColumns,
							criteria);
		}

		List<Column> columns = new ArrayList<Column>();
		this.addDocumentIdColumn(columns);
		columns.addAll(tempColumns);
		this.addRouteLogColumn(columns);
		return columns;
	}

	public void addStandardSearchColumns(List<Column> columns, List<DocSearchDTO> docSearchResultRows) {
		this.addColumnUsingKey(
						columns,
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL);
		this.addColumnUsingKey(
						columns,
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE);
		this.addColumnUsingKey(
						columns,
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC);
		addDocStatusColumn(columns, docSearchResultRows);
		this.addColumnUsingKey(columns,
				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR);
		this.addColumnUsingKey(
						columns,
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED);
	}

	public void addDocumentIdColumn(List<Column> columns) {
		this
				.addColumnUsingKey(
						columns,
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_ID);
	}

	public void addRouteLogColumn(List<Column> columns) {
		this.addColumnUsingKey(columns,
				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG);
	}

	public void addDocStatusColumn(List<Column> columns, List<DocSearchDTO> docSearchResultRows) {
		// add this column if document status policy is defined as "both".
		for (DocSearchDTO myDTO : docSearchResultRows) {
	    	DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName(myDTO.getDocTypeName());
	    	if (docType.isAppDocStatusInUse()){
	    		this.addColumnUsingKey(columns,
	    				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_STATUS);
	    		break;
	    	}
		}
		return;
	}

	public void addSearchableAttributeColumnsNoOverrides(
			List<Column> columns, DocSearchCriteriaDTO criteria) {
		this
				.addSearchableAttributeColumnsBasedOnFields(columns, criteria,
						null);
	}

	public void addSearchableAttributeColumnsBasedOnFields(
			List<Column> columns, DocSearchCriteriaDTO criteria,
			List<String> searchAttributeFieldNames) {
		Set<String> alreadyProcessedFieldKeys = new HashSet<String>();
		List<Field> fields = this
				.getFields(criteria, searchAttributeFieldNames);
		for (Field field : fields) {
			if (field instanceof Field) {
				Field dsField = (Field) field;
				if ((dsField.getPropertyName() == null)
						|| (!alreadyProcessedFieldKeys.contains(dsField
								.getPropertyName()))) {
					if (dsField.isColumnVisible()) {
						if (Field.SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES
								.contains(dsField.getFieldType())) {
							String resultFieldLabel = dsField.getFieldLabel();
							if (dsField.isMemberOfRange()) {
								resultFieldLabel = dsField.getMainFieldLabel();
							}
							this.addSearchableAttributeColumnUsingKey(columns,
									dsField.getFormatter(), dsField.getPropertyName(),
									resultFieldLabel, Boolean.TRUE,
									Boolean.TRUE);
							if (dsField.getPropertyName() != null) {
								alreadyProcessedFieldKeys.add(dsField
										.getPropertyName());
							}

						}
					}
				}
			} else {
				throw new RiceRuntimeException(
						"Fields must be of type org.kuali.rice.kew.docsearch.Field");
			}
		}
	}

	public void addAllCustomColumns(List<Column> columns,
			DocSearchCriteriaDTO criteria,
			List<Column> customDisplayColumns) {
		for (Column customColumn : customDisplayColumns) {
			this.addCustomColumn(columns, customColumn);
		}
	}

	public void addCustomStandardCriteriaColumns(
			List<Column> columns, DocSearchCriteriaDTO criteria,
			List<Column> customDisplayColumns) {
		for (Column customColumn : customDisplayColumns) {
			if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_SET
					.contains(customColumn.getPropertyName())) {
				this.addCustomColumn(columns, customColumn);
			}
		}
	}

	public void addCustomColumn(List<Column> columns,
			Column customColumn) {

		addColumnUsingKey(columns,
				customColumn.getPropertyName(), customColumn.getColumnTitle(), new Boolean(customColumn.getSortable()));
	}

	public List<Field> getFields(DocSearchCriteriaDTO criteria) {
		return getFields(criteria, null);
	}

	public DocumentType getDocumentType(String documentTypeName) {
		DocumentType documentType = null;
		if (StringUtils.isNotBlank(documentTypeName)) {
			documentType = ((DocumentTypeService) KEWServiceLocator
					.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE))
					.findByName(documentTypeName);
		}
		return documentType;
	}

	public List<Field> getFields(DocSearchCriteriaDTO criteria,
			List<String> searchAttributeFieldNames) {
		List<Field> fields = new ArrayList<Field>();
		DocumentType documentType = getDocumentType(criteria.getDocTypeFullName());
		if (documentType != null) {
            DocumentLookupConfiguration lookupConfiguration = KEWServiceLocator.getDocumentSearchCustomizationMediator().getDocumentLookupConfiguration(documentType);
            List<RemotableAttributeField> attributeFields = lookupConfiguration.getFlattenedSearchAttributeFields();
            for (RemotableAttributeField attributeField : attributeFields) {
                if (searchAttributeFieldNames == null || searchAttributeFieldNames.contains(attributeField.getName())) {
                    fields.addAll(FieldUtils.convertRemotableAttributeField(attributeField));
                }
            }
		}
		return fields;
	}

	public DocumentSearchResult generateSearchResult(
			DocSearchDTO docCriteriaDTO, List<Column> columns) {
		Map<String, Object> alternateSortValues = getSortValuesMap(docCriteriaDTO);
		DocumentSearchResult docSearchResult = null;
		for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
			Column currentColumn = (Column) iterator
					.next();
			KeyValueSort kvs = generateSearchResult(docCriteriaDTO,
					currentColumn, alternateSortValues);
			if (kvs != null) {
				if (docSearchResult == null) {
					docSearchResult = new DocumentSearchResult();
				}
				docSearchResult.addResultContainer(kvs);
			}
		}
		return docSearchResult;
	}

	public class DisplayValues {
		public String htmlValue;
		public String userDisplayValue;
	}

	public KeyValueSort generateSearchResult(DocSearchDTO docCriteriaDTO,
			Column column,
			Map<String, Object> sortValuesByColumnKey) {
		KeyValueSort returnValue = null;
		DisplayValues fieldValue = null;
		Object sortFieldValue = null;
		String columnKeyName = column.getPropertyName();
		SearchableAttributeValue attributeValue = null;

		if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_ID
				.equals(columnKeyName)) {
			fieldValue = this.getDocumentIdFieldDisplayValue(docCriteriaDTO
					.getDocumentId(), docCriteriaDTO
					.isUsingSuperUserSearch(), docCriteriaDTO.getDocTypeName());
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG
				.equals(columnKeyName)) {
			fieldValue = this.getRouteLogFieldDisplayValue(docCriteriaDTO
					.getDocumentId());
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED
				.equals(columnKeyName)) {
			fieldValue = new DisplayValues();
			fieldValue.htmlValue = DocSearchUtils
					.getDisplayValueWithDateTime(docCriteriaDTO
							.getDateCreated());
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL
				.equals(columnKeyName)) {
			fieldValue = new DisplayValues();
			fieldValue.htmlValue = docCriteriaDTO.getDocTypeLabel();
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE
				.equals(columnKeyName)) {
			fieldValue = new DisplayValues();
			fieldValue.htmlValue = docCriteriaDTO.getDocumentTitle();
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR
				.equals(columnKeyName)) {
			fieldValue = this.getInitiatorFieldDisplayValue(docCriteriaDTO
					.getInitiatorTransposedName(), docCriteriaDTO
					.getInitiatorWorkflowId());
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC
				.equals(columnKeyName)) {
			fieldValue = new DisplayValues();
			fieldValue.htmlValue = docCriteriaDTO.getDocRouteStatusCodeDesc();
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_STATUS
				.equals(columnKeyName)) {
			fieldValue = new DisplayValues();
			fieldValue.htmlValue = docCriteriaDTO.getAppDocStatus();
			sortFieldValue = sortValuesByColumnKey.get(columnKeyName);
		} else {
			// check searchable attributes
			for (Iterator iter = docCriteriaDTO.getSearchableAttributes()
					.iterator(); iter.hasNext();) {
				KeyValueSort searchAttribute = (KeyValueSort) iter.next();
				if (searchAttribute.getKey().equals(columnKeyName)) {
					Object sortValue = sortValuesByColumnKey.get(columnKeyName);
					sortFieldValue = (sortValue != null) ? sortValue
							: searchAttribute.getSortValue();
					attributeValue = searchAttribute.getSearchableAttributeValue();
					fieldValue = new DisplayValues();
					fieldValue.htmlValue = searchAttribute.getValue();
					break;
				}
			}
		}
		if (fieldValue != null) {
			String userDisplaySortValue = fieldValue.userDisplayValue;
			if (StringUtils.isBlank(userDisplaySortValue)) {
				userDisplaySortValue = fieldValue.htmlValue;
			}
			returnValue = new KeyValueSort(columnKeyName, fieldValue.htmlValue,
					fieldValue.userDisplayValue,
					(sortFieldValue != null) ? sortFieldValue
							: userDisplaySortValue, attributeValue);
		}
		return returnValue;
	}

	/*
	 * Convenience Methods to get field values for certain Workflow Standard
	 * Search Result columns
	 */

	public DisplayValues getRouteLogFieldDisplayValue(String documentId) {
		DisplayValues dv = new DisplayValues();
		String linkPopup = "";
		if (this.isRouteLogPopup()) {
			linkPopup = " target=\"_new\"";
		}
		String imageSource = "<img alt=\"Route Log for Document\" src=\"images/my_route_log.gif\"/>";
		dv.htmlValue = "<a href=\"RouteLog.do?documentId=" + documentId
				+ "\"" + linkPopup + ">" + imageSource + "</a>";
		dv.userDisplayValue = imageSource;
		return dv;
	}

	public DisplayValues getDocumentIdFieldDisplayValue(
			String documentId, boolean isSuperUserSearch,
			String documentTypeName) {
		return this.getValueEncodedWithDocHandlerUrl(documentId,
				documentId, isSuperUserSearch, documentTypeName);
	}

	public DisplayValues getInitiatorFieldDisplayValue(
			String fieldLinkTextValue, String initiatorWorkflowId) {
		DisplayValues dv = new DisplayValues();
		
		dv.htmlValue = "<a href=\""+ ConfigContext.getCurrentContextConfig().getKRBaseURL() +
		"/inquiry.do?businessObjectClassName=org.kuali.rice.kim.bo.impl.PersonImpl&" +
		"methodToCall=continueWithInquiry&principalId="+ initiatorWorkflowId
				+ "\" target=\"_blank\">"
				+ fieldLinkTextValue + "</a>";
		dv.userDisplayValue = fieldLinkTextValue;
		return dv;
	}

	/**
	 * Convenience method to allow child classes to use a custom value string
	 * and wrap that string in the document handler URL
	 *
	 * @param value
	 *            - the value that will show on screen as the clickable link
	 * @param documentId
	 *            - the string value of the document id the doc handler
	 *            should point to
	 * @param isSuperUserSearch
	 *            - boolean indicating whether this search is a super user
	 *            search or not see
	 *            {@link org.kuali.rice.kew.docsearch.DocSearchDTO#isUsingSuperUserSearch()}
	 * @return the fully encoded html for a link using the text from the input
	 *         parameter 'value'
	 */
	public DisplayValues getValueEncodedWithDocHandlerUrl(String value,
			String documentId, boolean isSuperUserSearch,
			String documentTypeName) {
		DisplayValues dv = new DisplayValues();
		dv.htmlValue = getDocHandlerUrlPrefix(documentId, isSuperUserSearch,
				documentTypeName)
				+ value + getDocHandlerUrlSuffix(isSuperUserSearch);
		dv.userDisplayValue = value;
		return dv;
	}

	public Map<String, Object> getSortValuesMap(DocSearchDTO docCriteriaDTO) {
		Map<String, Object> alternateSort = new HashMap<String, Object>();
		alternateSort
				.put(
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_ID,
						docCriteriaDTO.getDocumentId());
		if (StringUtils.isNotBlank(docCriteriaDTO.getInitiatorTransposedName())) {
		    alternateSort.put(
				    KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR,
				    docCriteriaDTO.getInitiatorTransposedName());
		}
		else {
			alternateSort.put(
					KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR,
					docCriteriaDTO.getInitiatorWorkflowId());		
		}
		alternateSort
				.put(
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED,
						docCriteriaDTO.getDateCreated());
		return alternateSort;
	}

	public Map<String, Boolean> getSortableByKey() {
		if (sortableByKey.isEmpty()) {
			sortableByKey = constructSortableByKey();
		}
		return sortableByKey;
	}

	public Map<String, Boolean> constructSortableColumnByKey() {
		Map<String, Boolean> sortable = new HashMap<String, Boolean>();
		sortable
				.put(
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_ID,
						Boolean.TRUE);
		sortable
				.put(
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL,
						Boolean.TRUE);
		sortable
				.put(
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE,
						Boolean.TRUE);
		sortable
				.put(
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC,
						Boolean.TRUE);
		sortable
			.put(
				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_STATUS,
				Boolean.TRUE);
		sortable.put(
				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR,
				Boolean.TRUE);
		sortable
				.put(
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED,
						Boolean.TRUE);
		sortable.put(
				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG,
				Boolean.FALSE);
		return sortable;
	}

	public Map<String, Boolean> getSortableColumnByKey() {
		if (sortableByKey.isEmpty()) {
			sortableByKey = constructSortableByKey();
		}
		return sortableByKey;
	}

	public Map<String, Boolean> constructSortableByKey() {
		Map<String, Boolean> sortable = new HashMap<String, Boolean>();
		sortable
				.put(
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_ID,
						Boolean.TRUE);
		sortable
				.put(
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_TYPE_LABEL,
						Boolean.TRUE);
		sortable
				.put(
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_TITLE,
						Boolean.TRUE);
		sortable
				.put(
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_STATUS_DESC,
						Boolean.TRUE);
		sortable.put(
				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOC_STATUS,
				Boolean.TRUE);
		sortable.put(
				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR,
				Boolean.TRUE);
		sortable
				.put(
						KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DATE_CREATED,
						Boolean.TRUE);
		sortable.put(
				KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG,
				Boolean.FALSE);
		return sortable;
	}

	public Map<String, String> getLabelsByKey() {
		if (labelsByKey.isEmpty()) {
			labelsByKey = constructLabelsByKey();
		}
		return labelsByKey;
	}

	public Map<String, String> constructLabelsByKey() {
		return new HashMap<String, String>();
	}

	/*
	 * Below columns are for convenience for overriding classes
	 */

	public void addColumnUsingKey(List<Column> columns, String key) {
		this.addColumnUsingKey(columns, key,
				null, null);
	}

	public void addColumnUsingKey(List<Column> columns, String key, String label) {
		this.addColumnUsingKey(columns, key, label, null);
	}

	public void addColumnUsingKey(List<Column> columns, String key, Boolean sortable) {
		this.addColumnUsingKey(columns, key, null, sortable);
	}

	public void addColumnUsingKey(List<Column> columns, String key, String label,
			Boolean sortable) {
		columns.add(this.constructColumnUsingKey(key, label,
				sortable));
	}

	public void addSearchableAttributeColumnUsingKey(
			List<Column> columns,
			String key, String label,
			Boolean sortableOverride, Boolean defaultSortable) {
		columns.add(this
				.constructColumnUsingKey(key, label,
						(sortableOverride != null) ? sortableOverride
								: defaultSortable));
	}

   public void addSearchableAttributeColumnUsingKey(
        List<Column> columns,
        Formatter formatter, String key, String label,
        Boolean sortableOverride, Boolean defaultSortable) {
        Column column = this.constructColumnUsingKey(key, label,
                (sortableOverride != null) ? sortableOverride
                        : defaultSortable);
        //if (formatter != null) {
        column.setFormatter(formatter);
        //}
        columns.add(column);
    }


	/*
	 * Below methods should probably not be overriden by overriding classes but
	 * could be if desired
	 */

	public Column constructColumnUsingKey(String key, String label,
			Boolean sortable) {
		if (sortable == null) {
			sortable = getSortableByKey().get(key);
		}
		if (label == null) {
			label = getLabelsByKey().get(key);
		}
		Column c = new Column(
				label,key);
		return c;
	}

	public boolean isDocumentHandlerPopup() {
	    return CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(
                KEWConstants.KEW_NAMESPACE,
                KRADConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE,
                KEWConstants.DOCUMENT_SEARCH_DOCUMENT_POPUP_IND);
	}

	public boolean isRouteLogPopup() {
		return CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(
                KEWConstants.KEW_NAMESPACE,
                KRADConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE,
                KEWConstants.DOCUMENT_SEARCH_ROUTE_LOG_POPUP_IND);
	}

	public String getDocHandlerUrlPrefix(String documentId,
			boolean superUserSearch, String documentTypeName) {
		String linkPopup = "";
		if (this.isDocumentHandlerPopup()) {
			linkPopup = " target=\"_blank\"";
		}
		if (superUserSearch) {
			String url = "<a href=\"SuperUser.do?methodToCall=displaySuperUserDocument&documentId="
					+ documentId + "\"" + linkPopup + " >";
			if (!getDocumentType(documentTypeName)
					.getUseWorkflowSuperUserDocHandlerUrl().getPolicyValue()
					.booleanValue()) {
				url = "<a href=\"" + KEWConstants.DOC_HANDLER_REDIRECT_PAGE
						+ "?" + KEWConstants.COMMAND_PARAMETER + "="
						+ KEWConstants.SUPERUSER_COMMAND + "&"
						+ KEWConstants.DOCUMENT_ID_PARAMETER + "="
						+ documentId + "\"" + linkPopup + ">";
			}
			return url;
		} else {
			return "<a href=\"" + KEWConstants.DOC_HANDLER_REDIRECT_PAGE + "?"
					+ KEWConstants.COMMAND_PARAMETER + "="
					+ KEWConstants.DOCSEARCH_COMMAND + "&"
					+ KEWConstants.DOCUMENT_ID_PARAMETER + "="
					+ documentId + "\"" + linkPopup + ">";
		}
	}

	public String getDocHandlerUrlSuffix(boolean superUserSearch) {
		if (superUserSearch) {
			return "</a>";
		} else {
			return "</a>";
		}
	}

    public void setProcessFinalResults(boolean isProcessFinalResults) {
       this.processFinalResults = isProcessFinalResults;
    }

    public boolean isProcessFinalResults() {
        return this.processFinalResults;
    }
}
