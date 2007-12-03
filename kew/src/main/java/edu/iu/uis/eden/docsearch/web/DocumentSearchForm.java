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
package edu.iu.uis.eden.docsearch.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.docsearch.DocSearchCriteriaVO;
import edu.iu.uis.eden.docsearch.DocSearchUtils;
import edu.iu.uis.eden.docsearch.SavedSearchResult;
import edu.iu.uis.eden.docsearch.SearchAttributeCriteriaComponent;
import edu.iu.uis.eden.docsearch.SearchableAttribute;
import edu.iu.uis.eden.docsearch.SearchableAttributeValue;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.lookupable.Column;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.util.Utilities;

/**
 * Struts form for document search action
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentSearchForm extends ActionForm {

    private static final long serialVersionUID = 8680419749805107805L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentSearchForm.class);
	private DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();

	private String searchTarget;
	private String searchIdValue;
	private String searchLabelValue;
	private String backIdPropName;
	private String backLabelPropName;
	private String backURL;
	private String searchAction;
	private String action;

	private String returnAction;
	private String namedSearch = "";
	private String lookupableImplServiceName;
	private String conversionFields = "";
	private String methodToCall = "";
	private String quickFinderLookupable;
	private String lookupType;
    private List searchableAttributeRows;
	private List searchableAttributeColumns;
	private List propertyFields;

	private boolean headerBarEnabled = true;
	private boolean searchCriteriaEnabled = true;
	private boolean initiatorUser = false;
	private String searchableAttributes = "";

	public DocumentSearchForm() {
		super();
		searchableAttributeRows = new ArrayList();
		searchableAttributeColumns = new ArrayList();
		propertyFields = new ArrayList();
	}

	public DocSearchCriteriaVO getCriteria() {
		return this.criteria;
	}

	public void setDocTypeFullName(String docTypeFullName) {
		criteria.setDocTypeFullName(docTypeFullName);
	}

	public void clearSearchableAttributes() {
		searchableAttributeRows = new ArrayList();
		searchableAttributeColumns = new ArrayList();
        propertyFields = new ArrayList();
	}

    public void updateFormUsingSavedSearch(SavedSearchResult result) {
        setCriteria(result.getDocSearchCriteriaVO());
        clearSearchableAttributes();
        checkForAdditionalFields();
        setupPropertyFieldsUsingCriteria();
        setNamedSearch("");
        //TODO this is for historic reasons only and can be deleted after release 2.1
        // but we need to check and notify that any user option saved search without a key containing 'isAdvancedSearch'
        // will lose the search location context and will always be brought to the 'basic' search screen
        //if ("".equals(getIsAdvancedSearch()) || "NO".equals(getIsAdvancedSearch())) {
        //    setIsAdvancedSearch(result.isAdvancedSearch() ? "YES" : "NO");
        //}
    }

	public void checkForAdditionalFields() {
		DocumentType documentType = getDocumentType();
//		String docTypeFullName = criteria.getDocTypeFullName();
		if (documentType != null) {
			List<SearchableAttribute> searchableAttributes = documentType.getSearchableAttributes();
			// we only want to initialize the searchable attribute fields, rows,
			// and columns if this is the first time that they are being
			// displayed
			// on the form, therefore we check that each of the lists is empty.
			// Originally, this code was clearing these lists out on every
			// entry to the DocumentSearch screen which would only work in the
			// case of a post of the entire form. In the case of lookups, this
			// would result in the searchable attribute field values being
			// cleared out, this fix resolves EN-122.
			if (searchableAttributeRows.isEmpty() && searchableAttributeColumns.isEmpty() && propertyFields.isEmpty()) {
				Set alreadyProcessedFieldKeys = new HashSet();
				for (SearchableAttribute searchableAttribute : searchableAttributes) {
					List<Row> searchRows = searchableAttribute.getSearchingRows();
					if (searchRows == null) {
						continue;
					}
					for (Row row : searchRows) {
						for (Field field : row.getFields()) {
							if (!Utilities.isEmpty(field.getPropertyName())) {
                                if (Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType())) {
                                    SearchAttributeFormContainer newFormContainer = new SearchAttributeFormContainer();
                                    newFormContainer.setKey(field.getPropertyName());
                                    newFormContainer.setValues(field.getPropertyValues());
                                    propertyFields.add(newFormContainer);
                                } else {
                                    propertyFields.add(new SearchAttributeFormContainer(field.getPropertyName(), field.getPropertyValue()));
                                }
							}
                            // TODO delyea - check this... do we need it still?
							if ( (field.getSavablePropertyName() == null) || (!alreadyProcessedFieldKeys.contains(field.getSavablePropertyName())) ) {
								if (field.isColumnVisible()) {
									for (Iterator iter = Field.SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.iterator(); iter.hasNext();) {
										String displayableFieldType = (String) iter.next();
										if (field.getFieldType().equals(displayableFieldType)) {
											searchableAttributeColumns.add(new Column(field.getFieldLabel(), Column.COLUMN_IS_SORTABLE_VALUE, "searchableAttribute(" + field.getSavablePropertyName() + ").label"));
											if (field.getSavablePropertyName() != null) {
												alreadyProcessedFieldKeys.add(field.getSavablePropertyName());
											}
											break;
										}
									}
								}
							}
						}
						searchableAttributeRows.add(row);
					}
				}
			} else {
				updateSearchableAttributeData(documentType, searchableAttributes);
			}
		}
	}

	/**
	 * Updates the field valid values since they aren't submitted with the form.
	 *
	 */
	private void updateSearchableAttributeData(DocumentType documentType, List<SearchableAttribute> searchableAttributes) {
		// searchableAttributeRows is a List containing rows from all attributes, so we need to keep a global row count
		int totalRowIndex = 0;
		for (SearchableAttribute searchableAttribute : searchableAttributes) {
			List<Row> rows = searchableAttribute.getSearchingRows();
			for (Row row : rows) {
				Row existingRow = (Row)searchableAttributeRows.get(totalRowIndex++);
				int fieldIndex = 0;
				for (Field field : row.getFields()) {
					// get existing field
					Field existingField = existingRow.getField(fieldIndex++);
					// now update the valid values
					existingField.setFieldValidValues(field.getFieldValidValues());
				}
			}
		}

	}

	public void addSearchableAttributesToCriteria() {
		DocumentType docType = getDocumentType();
		if (docType == null) {
			return;
		}
		if (!StringUtils.isBlank(getSearchableAttributes())) {
			List<SearchAttributeCriteriaComponent> components = DocSearchUtils.buildSearchableAttributesFromString(getSearchableAttributes(), docType.getName());
			for (SearchAttributeCriteriaComponent component : components) {
				criteria.addSearchableAttribute(component);
			}
		}
		if (!propertyFields.isEmpty()) {
			Map criteriaComponentsByFormKey = new HashMap();
			for (SearchableAttribute searchableAttribute : docType.getSearchableAttributes()) {
				for (Row row : searchableAttribute.getSearchingRows()) {
					for (Field field : row.getFields()) {
                        SearchableAttributeValue searchableAttributeValue = DocSearchUtils.getSearchableAttributeValueByDataTypeString(field.getFieldDataType());
                        SearchAttributeCriteriaComponent sacc = new SearchAttributeCriteriaComponent(field.getPropertyName(),null,field.getSavablePropertyName(),searchableAttributeValue);
                        sacc.setRangeSearch(field.isMemberOfRange());
                        sacc.setAllowWildcards(field.isAllowingWildcards());
                        sacc.setAutoWildcardBeginning(field.isAutoWildcardAtBeginning());
                        sacc.setAutoWildcardEnd(field.isAutoWildcardAtEnding());
                        sacc.setCaseSensitive(field.isCaseSensitive());
                        sacc.setSearchInclusive(field.isInclusive());
                        sacc.setLookupableFieldType(field.getFieldType());
                        sacc.setSearchable(field.isSearchable());
                        sacc.setCanHoldMultipleValues(Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType()));
                        criteriaComponentsByFormKey.put(field.getPropertyName(), sacc);
					}
				}
			}
			for (Iterator iterator = propertyFields.iterator(); iterator.hasNext();) {
                SearchAttributeFormContainer propertyField = (SearchAttributeFormContainer) iterator.next();
				SearchAttributeCriteriaComponent sacc = (SearchAttributeCriteriaComponent) criteriaComponentsByFormKey.get(propertyField.getKey());
				if (sacc != null) {
					if (sacc.getSearchableAttributeValue() == null) {
						String errorMsg = "Searchable attribute with form field key " + sacc.getFormKey() + " does not have a valid SearchableAttributeValue";
						LOG.error("addSearchableAttributesToCriteria() " + errorMsg);
						throw new RuntimeException(errorMsg);
					}
                    if ( (Field.CHECKBOX_YES_NO.equals(sacc.getLookupableFieldType())) && (!propertyField.isValueSet()) ) {
                        // value was not set on the form so we must use the alternate value which for checkbox is the 'unchecked' value
                        sacc.setValue(propertyField.getAlternateValue());
                    } else if (Field.MULTI_VALUE_FIELD_TYPES.contains(sacc.getLookupableFieldType())) {
                        sacc.setCanHoldMultipleValues(true);
                        if (propertyField.getValues() == null) {
                            sacc.setValues(new ArrayList<String>());
                        } else {
                            sacc.setValues(Arrays.asList(propertyField.getValues()));
                        }
                    } else {
                        sacc.setValue(propertyField.getValue());
                    }
					criteria.addSearchableAttribute(sacc);
				}
			}
		}
	}

    private void setupPropertyFieldsUsingCriteria() {
        for (Iterator iter = criteria.getSearchableAttributes().iterator(); iter.hasNext();) {
            SearchAttributeCriteriaComponent searchableAttribute = (SearchAttributeCriteriaComponent) iter.next();
            SearchAttributeFormContainer container = getPropertyField(searchableAttribute.getFormKey());
            if (container != null) {
                container.setValue(searchableAttribute.getValue());
                if (searchableAttribute.getValues() != null) {
                    container.setValues(searchableAttribute.getValues().toArray(new String[searchableAttribute.getValues().size()]));
                }
            }
        }
    }

	public String getDocTypeDisplayName() {
		DocumentType docType = getDocumentType();
		if (docType != null) {
			return docType.getLabel();
		}
		return null;
	}

	private DocumentType getDocumentType() {
		if (criteria.getDocTypeFullName() != null && !"".equals(criteria.getDocTypeFullName())) {
		    return ((DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(criteria.getDocTypeFullName());
		}
		return null;
	}

	public String getRouteLogPopup() {
		return Utilities.getApplicationConstant(EdenConstants.DOCUMENT_SEARCH_ROUTE_LOG_POPUP_KEY).trim();
	}

	public String getDocumentPopup() {
		return Utilities.getApplicationConstant(EdenConstants.DOCUMENT_SEARCH_DOCUMENT_POPUP_KEY).trim();
	}

	public void setInitiator(String initiator) {
		criteria.setInitiator(initiator);
	}

	public void setApprover(String approver) {
		criteria.setApprover(approver);
	}

	public void setViewer(String viewer) {
		criteria.setViewer(viewer);
	}

	public void setCriteria(DocSearchCriteriaVO criteria) {
        // TODO JIRA KULOWF-254 - populate searchable attributes
		this.criteria = criteria;
	}

    /*
     * the super user search methods used to live here but were moved to the crieteria so search
     * context could be saved along with search data.  I kept these methods here to minimize impact on jsp.
     * Feel free to remove this call through methods and modify the jsp.
     */
	public String getSuperUserSearch() {
		return criteria.getSuperUserSearch();
	}

	public void setSuperUserSearch(String superUserSearch) {
		this.criteria.setSuperUserSearch(superUserSearch);
	}

	public void setSearchTarget(String searchTarget) {
		this.searchTarget = searchTarget;
	}

	public String getSearchTarget() {
		return searchTarget;
	}

	public void setSearchIdValue(String searchIdValue) {
		this.searchIdValue = searchIdValue;
	}

	public String getSearchIdValue() {
		return searchIdValue;
	}

	public void setSearchLabelValue(String searchLabelValue) {
		this.searchLabelValue = searchLabelValue;
	}

	public String getSearchLabelValue() {
		return searchLabelValue;
	}

	public void setBackIdPropName(String backIdPropName) {
		this.backIdPropName = backIdPropName;
	}

	public String getBackIdPropName() {
		return backIdPropName;
	}

	public void setBackLabelPropName(String backLabelPropName) {
		this.backLabelPropName = backLabelPropName;
	}

	public String getBackLabelPropName() {
		return backLabelPropName;
	}

	public void setBackURL(String backURL) {
		this.backURL = backURL;
	}

	public String getBackURL() {
		return backURL;
	}

	public void setSearchAction(String searchAction) {
		this.searchAction = searchAction;
	}

	public String getSearchAction() {
		return searchAction;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

    /*
     * the IsAdvancedSearch methods used to live here but were moved to the crieteria so search
     * context could be saved along with search data.  I kept these methods here to minimize impact on jsp.
     * Feel free to remove this call through methods and modify the jsp.
     */
	public String getIsAdvancedSearch() {
		return criteria.getIsAdvancedSearch();
	}

	public void setIsAdvancedSearch(String string) {
        this.criteria.setIsAdvancedSearch(string);
	}

	public String getReturnAction() {
		return returnAction;
	}

	public void setReturnAction(String returnAction) {
		this.returnAction = returnAction;
	}

	public void setFromDateCreated(String fromDateCreated) {
		criteria.setFromDateCreated(fromDateCreated);
	}

	public void setToDateCreated(String toDateCreated) {
		criteria.setToDateCreated(toDateCreated);
	}

	public String getFromDateCreated() {
		return criteria.getFromDateCreated();
	}

	public String getToDateCreated() {
		return criteria.getToDateCreated();
	}

	public void setFromDateLastModified(String fromDateLastModified) {
		criteria.setFromDateLastModified(fromDateLastModified);
	}

	public void setToDateLastModified(String toDateLastModified) {
		criteria.setToDateLastModified(toDateLastModified);
	}

	public String getFromDateLastModified() {
		return criteria.getFromDateLastModified();
	}

	public String getToDateLastModified() {
		return criteria.getToDateLastModified();
	}

	public void setFromDateApproved(String fromDateApproved) {
		criteria.setFromDateApproved(fromDateApproved);
	}

	public void setToDateApproved(String toDateApproved) {
		criteria.setToDateApproved(toDateApproved);
	}

	public String getFromDateApproved() {
		return criteria.getFromDateApproved();
	}

	public String getToDateApproved() {
		return criteria.getToDateApproved();
	}

	public void setFromDateFinalized(String fromDateFinalized) {
		criteria.setFromDateFinalized(fromDateFinalized);
	}

	public void setToDateFinalized(String toDateFinalized) {
		criteria.setToDateFinalized(toDateFinalized);
	}

	public String getFromDateFinalized() {
		return criteria.getFromDateFinalized();
	}

	public String getToDateFinalized() {
		return criteria.getToDateFinalized();
	}



	public String getNamedSearch() {
		return namedSearch;
	}

	public void setNamedSearch(String namedSearch) {
		this.namedSearch = namedSearch;
	}

	public String getLookupableImplServiceName() {
		return lookupableImplServiceName;
	}

	public void setLookupableImplServiceName(String lookupableImplServiceName) {
		this.lookupableImplServiceName = lookupableImplServiceName;
	}

	/**
	 * @param conversionFields
	 *            The conversionFields to set.
	 */
	public void setConversionFields(String conversionFields) {
		this.conversionFields = conversionFields;
	}

	/**
	 * @return Returns the conversionFields.
	 */
	public String getConversionFields() {
		return conversionFields;
	}

	public String getMethodToCall() {
		return methodToCall;
	}

	public void setMethodToCall(String methodToCall) {
		this.methodToCall = methodToCall;
	}

	public String getQuickFinderLookupable() {
		return quickFinderLookupable;
	}

	public void setQuickFinderLookupable(String quickFinderLookupable) {
		this.quickFinderLookupable = quickFinderLookupable;
	}

	public String getLookupType() {
		return lookupType;
	}

	public void setLookupType(String lookupType) {
		this.lookupType = lookupType;
	}

	/**
	 * @param searchableAttributeRows
	 *            The searchableAttributeRows to set.
	 */
	public void setSearchableAttributeRows(List searchableAttributeRows) {
		this.searchableAttributeRows = searchableAttributeRows;
	}

	/**
	 * @return Returns the searchableAttributeRows.
	 */
	public List getSearchableAttributeRows() {
		return searchableAttributeRows;
	}

	public void addSearchableAttributeRow(Row row) {
		searchableAttributeRows.add(row);
	}

	public Row getSearchableAttributeRow(int index) {
		while (getSearchableAttributeRows().size() <= index) {
			Row row = new Row(new ArrayList());
			getSearchableAttributeRows().add(row);
		}
		return (Row) getSearchableAttributeRows().get(index);
	}

	public void setSearchableAttributeRow(int index, Row row) {
		searchableAttributeRows.set(index, row);
	}

	/**
	 * @param searchableAttributeColumns
	 *            The searchableAttributeColumns to set.
	 */
	public void setSearchableAttributeColumns(List searchableAttributeColumns) {
		this.searchableAttributeColumns = searchableAttributeColumns;
	}

	/**
	 * @return Returns the searchableAttributeColumns.
	 */
	public List getSearchableAttributeColumns() {
		return searchableAttributeColumns;
	}

	public void addSearchableAttributeColumn(Column column) {
		searchableAttributeColumns.add(column);
	}

	public Column getSearchableAttributeColumn(int index) {
		while (getSearchableAttributeColumns().size() <= index) {
			Column column = new Column("", "", "");
			getSearchableAttributeColumns().add(column);
		}
		return (Column) getSearchableAttributeColumns().get(index);
	}

	public void setSearchableAttributeColumn(int index, Column column) {
		searchableAttributeColumns.set(index, column);
	}

	/**
	 * @param propertyFields
	 *            The propertyFields to set.
	 */
	public void setPropertyFields(List propertyFields) {
		this.propertyFields = propertyFields;
	}

	/**
	 * @return Returns the propertyFields.
	 */
	public List getPropertyFields() {
		return propertyFields;
	}

	public void addPropertyField(SearchAttributeFormContainer attributeContainer) {
		propertyFields.add(attributeContainer);
	}

    public SearchAttributeFormContainer getPropertyField(int index) {
        while (getPropertyFields().size() <= index) {
            SearchAttributeFormContainer attributeContainer = new SearchAttributeFormContainer();
            addPropertyField(attributeContainer);
        }
        return (SearchAttributeFormContainer) getPropertyFields().get(index);
    }

    public SearchAttributeFormContainer getPropertyField(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        for (Iterator iter = propertyFields.iterator(); iter.hasNext();) {
            SearchAttributeFormContainer container = (SearchAttributeFormContainer) iter.next();
            if (key.equals(container.getKey())) {
                return container;
            }
        }
        return null;
    }

	public void setPropertyField(int index, SearchAttributeFormContainer attributeContainer) {
		propertyFields.set(index, attributeContainer);
	}


	public boolean isHeaderBarEnabled() {
		return headerBarEnabled;
	}

	public void setHeaderBarEnabled(boolean headerBarEnabled) {
		this.headerBarEnabled = headerBarEnabled;
	}

	public boolean isSearchCriteriaEnabled() {
		return searchCriteriaEnabled;
	}

	public void setSearchCriteriaEnabled(boolean searchCriteriaEnabled) {
		this.searchCriteriaEnabled = searchCriteriaEnabled;
	}

	public String getSearchableAttributes() {
		return searchableAttributes;
	}

	public void setSearchableAttributes(String secureAttributes) {
		this.searchableAttributes = secureAttributes;
	}

	public boolean isInitiatorUser() {
		return initiatorUser;
	}

	public void setInitiatorUser(boolean secureInitiatorSearch) {
		this.initiatorUser = secureInitiatorSearch;
	}

}
