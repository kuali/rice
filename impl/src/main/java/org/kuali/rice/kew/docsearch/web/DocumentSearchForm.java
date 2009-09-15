/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.kew.docsearch.web;

import org.apache.struts.action.ActionForm;
import org.kuali.rice.kns.web.struts.form.KualiForm;


/**
 * Struts form for document search action
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentSearchForm extends KualiForm {
//FIXME: delete this class when doc search is fully moved over

//    private static final long serialVersionUID = 8680419749805107805L;
//    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentSearchForm.class);
////	private DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
//    private DocumentSearchCriteriaProcessor criteriaProcessor = new StandardDocumentSearchCriteriaProcessor();
//
//	private String searchTarget;
//	private String searchIdValue;
//	private String searchLabelValue;
//	private String backIdPropName;
//	private String backLabelPropName;
//	private String backURL;
//	private String searchAction;
//	private String action;
//
//	private String returnAction;
//	private String namedSearch = "";
//	private String lookupableImplServiceName;
//	private String conversionFields = "";
//	private String methodToCall = "";
//	private String quickFinderLookupable;
//	private String lookupType;
//    private List searchableAttributeRows;
//	private List searchableAttributeColumns;
//	private List propertyFields;
//
//	private boolean headerBarEnabled = true;
//	private boolean searchCriteriaEnabled = true;
//	private boolean initiatorUser = false;
//	private String searchableAttributes = "";
//
//	public DocumentSearchForm() {
//		super();
//		searchableAttributeRows = new ArrayList<DocumentSearchRow>();
//		searchableAttributeColumns = new ArrayList();
//		propertyFields = new ArrayList();
//	}
//
//	public DocumentSearchCriteriaProcessor getCriteriaProcessor() {
//        return this.criteriaProcessor;
//    }
//
//    public void setCriteriaProcessor(DocumentSearchCriteriaProcessor criteriaProcessor) {
//        this.criteriaProcessor = criteriaProcessor;
//    }
//
//    public DocSearchCriteriaDTO getCriteria() {
//        if (this.criteriaProcessor == null) {
//            return null;
//        }
//		return this.criteriaProcessor.getDocSearchCriteriaDTO();
//	}
//
////    public void setCriteria(DocSearchCriteriaDTO criteria) {
////        if (criteria == null) {
////            throw new RuntimeException("Criteria should never be null");
////        }
////        this.criteriaProcessor.setDocSearchCriteriaDTO(criteria);
////    }
////
//	public void setDocTypeFullName(String docTypeFullName) {
//		getCriteria().setDocTypeFullName(docTypeFullName);
//	}
//
//	public String getDocTypeFullName() {
//	    return getCriteria().getDocTypeFullName();
//	}
//
//	public void clearSearchableAttributeProperties() {
//		searchableAttributeRows = new ArrayList<DocumentSearchRow>();
//		searchableAttributeColumns = new ArrayList();
//        propertyFields = new ArrayList();
//	}
//
//	public void checkForAdditionalFields() {
//		DocumentType documentType = getDocumentType();
//		if (documentType != null) {
//			List<SearchableAttribute> searchableAttributes = documentType.getSearchableAttributes();
//			// we only want to initialize the searchable attribute fields, rows,
//			// and columns if this is the first time that they are being
//			// displayed
//			// on the form, therefore we check that each of the lists is empty.
//			// Originally, this code was clearing these lists out on every
//			// entry to the DocumentSearch screen which would only work in the
//			// case of a post of the entire form. In the case of lookups, this
//			// would result in the searchable attribute field values being
//			// cleared out, this fix resolves EN-122.
//			if (searchableAttributeRows.isEmpty() && searchableAttributeColumns.isEmpty() && propertyFields.isEmpty()) {
//				Set alreadyProcessedFieldKeys = new HashSet();
//				for (SearchableAttribute searchableAttribute : searchableAttributes) {
//					List<DocumentSearchRow> searchRows = searchableAttribute.getSearchingRows(
//							DocSearchUtils.getDocumentSearchContext("", documentType.getName(), ""));
//					if (searchRows == null) {
//						continue;
//					}
//					for (DocumentSearchRow row : searchRows) {
//						for (org.kuali.rice.kns.web.ui.Field field : row.getFields()) {
//					        DocumentSearchField dsField = (DocumentSearchField)field;
//							if (!Utilities.isEmpty(dsField.getPropertyName())) {
//                                if (dsField.MULTI_VALUE_FIELD_TYPES.contains(dsField.getFieldType())) {
//                                    SearchAttributeFormContainer newFormContainer = new SearchAttributeFormContainer();
//                                    newFormContainer.setKey(dsField.getPropertyName());
//                                    newFormContainer.setValues(dsField.getPropertyValues());
//                                    propertyFields.add(newFormContainer);
//                                } else {
//                                    propertyFields.add(new SearchAttributeFormContainer(dsField.getPropertyName(), dsField.getPropertyValue()));
//                                }
//
//							}
//                            // TODO delyea - check this... do we need it still?
//							if ( (dsField.getSavablePropertyName() == null) || (!alreadyProcessedFieldKeys.contains(dsField.getSavablePropertyName())) ) {
//								if (dsField.isColumnVisible()) {
//									for (Iterator iter = dsField.SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.iterator(); iter.hasNext();) {
//										String displayableFieldType = (String) iter.next();
//										if (dsField.getFieldType().equals(displayableFieldType)) {
//											searchableAttributeColumns.add(new DocumentSearchColumn(field.getFieldLabel(), DocumentSearchColumn.COLUMN_IS_SORTABLE_VALUE, "searchableAttribute(" + dsField.getSavablePropertyName() + ").label"));
//											if (dsField.getSavablePropertyName() != null) {
//												alreadyProcessedFieldKeys.add(dsField.getSavablePropertyName());
//											}
//											break;
//										}
//									}
//								}
//							}
//						}
//						addSearchableAttributeRow(row);
//					}
//				}
//				// update any potential propertyFields to hold data already in searchable attributes
//				setupPropertyFieldsUsingCriteria();
//			} else {
//				updateSearchableAttributeData(documentType, searchableAttributes);
//			}
//		}
//	}
//
//	/**
//	 * Updates the field valid values since they aren't submitted with the form.
//	 *
//	 */
//	private void updateSearchableAttributeData(DocumentType documentType, List<SearchableAttribute> searchableAttributes) {
//		// searchableAttributeRows is a List containing rows from all attributes, so we need to keep a global row count
//		int totalRowIndex = 0;
//		for (SearchableAttribute searchableAttribute : searchableAttributes) {
//			List<DocumentSearchRow> rows = searchableAttribute.getSearchingRows(DocSearchUtils.getDocumentSearchContext("", documentType.getName(), ""));
//			for (DocumentSearchRow row : rows) {
//			    DocumentSearchRow existingRow = (DocumentSearchRow)getSearchableAttributeRows().get(totalRowIndex++);
//				int fieldIndex = 0;
//				for (org.kuali.rice.kns.web.ui.Field field : row.getFields()) {
//					// get existing field
//					org.kuali.rice.kns.web.ui.Field existingField = existingRow.getFields().get(fieldIndex++);
//					// now update the valid values
//					existingField.setFieldValidValues(field.getFieldValidValues());
//				}
//			}
//		}
//
//	}
//
//	public void addSearchableAttributesToCriteria() {
//	    DocSearchUtils.addSearchableAttributesToCriteria(getCriteria(), propertyFields, getSearchableAttributes());
//	    setSearchableAttributes(null);
////		DocumentType docType = getDocumentType();
////		if (docType == null) {
////			return;
////		}
////		getCriteria().getSearchableAttributes().clear();
////		Map<String,SearchAttributeCriteriaComponent> urlParameterSearchAttributesByFormKey = new HashMap<String,SearchAttributeCriteriaComponent>();
////        if (!StringUtils.isBlank(getSearchableAttributes())) {
////            List<SearchAttributeCriteriaComponent> components = DocSearchUtils.buildSearchableAttributesFromString(getSearchableAttributes(), docType.getName());
////            for (SearchAttributeCriteriaComponent component : components) {
////                urlParameterSearchAttributesByFormKey.put(component.getFormKey(), component);
////                getCriteria().addSearchableAttribute(component);
////            }
////            setSearchableAttributes(null);
////        }
////		if (!propertyFields.isEmpty()) {
////			Map criteriaComponentsByFormKey = new HashMap();
////			for (SearchableAttribute searchableAttribute : docType.getSearchableAttributes()) {
////				for (Row row : searchableAttribute.getSearchingRows()) {
////					for (Field field : row.getFields()) {
////                        SearchableAttributeValue searchableAttributeValue = DocSearchUtils.getSearchableAttributeValueByDataTypeString(field.getFieldDataType());
////                        SearchAttributeCriteriaComponent sacc = new SearchAttributeCriteriaComponent(field.getPropertyName(),null,field.getSavablePropertyName(),searchableAttributeValue);
////                        sacc.setRangeSearch(field.isMemberOfRange());
////                        sacc.setAllowWildcards(field.isAllowingWildcards());
////                        sacc.setAutoWildcardBeginning(field.isAutoWildcardAtBeginning());
////                        sacc.setAutoWildcardEnd(field.isAutoWildcardAtEnding());
////                        sacc.setCaseSensitive(field.isCaseSensitive());
////                        sacc.setSearchInclusive(field.isInclusive());
////                        sacc.setLookupableFieldType(field.getFieldType());
////                        sacc.setSearchable(field.isSearchable());
////                        sacc.setCanHoldMultipleValues(Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType()));
////                        criteriaComponentsByFormKey.put(field.getPropertyName(), sacc);
////					}
////				}
////			}
////			for (Iterator iterator = propertyFields.iterator(); iterator.hasNext();) {
////                SearchAttributeFormContainer propertyField = (SearchAttributeFormContainer) iterator.next();
////				SearchAttributeCriteriaComponent sacc = (SearchAttributeCriteriaComponent) criteriaComponentsByFormKey.get(propertyField.getKey());
////				if (sacc != null) {
////					if (sacc.getSearchableAttributeValue() == null) {
////						String errorMsg = "Searchable attribute with form field key " + sacc.getFormKey() + " does not have a valid SearchableAttributeValue";
////						LOG.error("addSearchableAttributesToCriteria() " + errorMsg);
////						throw new RuntimeException(errorMsg);
////					}
////					// if the url parameter has already set up the search attribute change the propertyField
////					if (urlParameterSearchAttributesByFormKey.containsKey(sacc.getFormKey())) {
////					    setupPropertyField(urlParameterSearchAttributesByFormKey.get(sacc.getFormKey()));
////					} else {
////                        if ( (Field.CHECKBOX_YES_NO.equals(sacc.getLookupableFieldType())) && (!propertyField.isValueSet()) ) {
////                            // value was not set on the form so we must use the alternate value which for checkbox is the 'unchecked' value
////                            sacc.setValue(propertyField.getAlternateValue());
////                        } else if (Field.MULTI_VALUE_FIELD_TYPES.contains(sacc.getLookupableFieldType())) {
////                            // set the multivalue lookup indicator
////                            sacc.setCanHoldMultipleValues(true);
////                            if (propertyField.getValues() == null) {
////                                sacc.setValues(new ArrayList<String>());
////                            } else {
////                                sacc.setValues(Arrays.asList(propertyField.getValues()));
////                            }
////                        } else {
////                            sacc.setValue(propertyField.getValue());
////                        }
////                        getCriteria().addSearchableAttribute(sacc);
////					}
////				}
////			}
////		}
//	}
//
//    public void setupPropertyFieldsUsingCriteria() {
//        for (Iterator iter = getCriteria().getSearchableAttributes().iterator(); iter.hasNext();) {
//            SearchAttributeCriteriaComponent searchableAttribute = (SearchAttributeCriteriaComponent) iter.next();
//            DocSearchUtils.setupPropertyField(searchableAttribute, propertyFields);
////            setupPropertyField(searchableAttribute);
//        }
//    }
//
////    public void setupPropertyField(SearchAttributeCriteriaComponent searchableAttribute) {
////        SearchAttributeFormContainer propertyField = getPropertyField(searchableAttribute.getFormKey());
////        if (propertyField != null) {
////            propertyField.setValue(searchableAttribute.getValue());
////            if (searchableAttribute.getValues() != null) {
////                propertyField.setValues(searchableAttribute.getValues().toArray(new String[searchableAttribute.getValues().size()]));
////            }
////        }
////    }
////
//	public String getDocTypeDisplayName() {
//		DocumentType docType = getDocumentType();
//		if (docType != null) {
//			return docType.getLabel();
//		}
//		return null;
//	}
//
//	private DocumentType getDocumentType() {
//		if ( (getCriteria() != null) && (getCriteria().getDocTypeFullName() != null && !"".equals(getCriteria().getDocTypeFullName())) ) {
//		    return ((DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(getCriteria().getDocTypeFullName());
//		}
//		return null;
//	}
//
//	public String getRouteLogPopup() {
//		return new Boolean(Utilities.getKNSParameterBooleanValue(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE, KEWConstants.DOCUMENT_SEARCH_ROUTE_LOG_POPUP_IND)).toString();
//	}
//
//	public String getDocumentPopup() {
//		return new Boolean(Utilities.getKNSParameterBooleanValue(KEWConstants.KEW_NAMESPACE, KNSConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE, KEWConstants.DOCUMENT_SEARCH_DOCUMENT_POPUP_IND)).toString();
//	}
//
//	public void setInitiator(String initiator) {
//		getCriteria().setInitiator(initiator);
//	}
//
//	public void setApprover(String approver) {
//		getCriteria().setApprover(approver);
//	}
//
//	public void setViewer(String viewer) {
//		getCriteria().setViewer(viewer);
//	}
//
//    /*
//     * the super user search methods used to live here but were moved to the criteria so search
//     * context could be saved along with search data.  I kept these methods here to minimize impact on jsp.
//     * Feel free to remove this call through methods and modify the jsp.
//     */
//	public String getSuperUserSearch() {
//		return getCriteria().getSuperUserSearch();
//	}
//
//	public void setSuperUserSearch(String superUserSearch) {
//		getCriteria().setSuperUserSearch(superUserSearch);
//	}
//
//	public void setSearchTarget(String searchTarget) {
//		this.searchTarget = searchTarget;
//	}
//
//	public String getSearchTarget() {
//		return searchTarget;
//	}
//
//	public void setSearchIdValue(String searchIdValue) {
//		this.searchIdValue = searchIdValue;
//	}
//
//	public String getSearchIdValue() {
//		return searchIdValue;
//	}
//
//	public void setSearchLabelValue(String searchLabelValue) {
//		this.searchLabelValue = searchLabelValue;
//	}
//
//	public String getSearchLabelValue() {
//		return searchLabelValue;
//	}
//
//	public void setBackIdPropName(String backIdPropName) {
//		this.backIdPropName = backIdPropName;
//	}
//
//	public String getBackIdPropName() {
//		return backIdPropName;
//	}
//
//	public void setBackLabelPropName(String backLabelPropName) {
//		this.backLabelPropName = backLabelPropName;
//	}
//
//	public String getBackLabelPropName() {
//		return backLabelPropName;
//	}
//
//	public void setBackURL(String backURL) {
//		this.backURL = backURL;
//	}
//
//	public String getBackURL() {
//		return backURL;
//	}
//
//	public void setSearchAction(String searchAction) {
//		this.searchAction = searchAction;
//	}
//
//	public String getSearchAction() {
//		return searchAction;
//	}
//
//	public void setAction(String action) {
//		this.action = action;
//	}
//
//	public String getAction() {
//		return action;
//	}
//
//    /*
//     * the IsAdvancedSearch methods used to live here but were moved to the crieteria so search
//     * context could be saved along with search data.  I kept these methods here to minimize impact on jsp.
//     * Feel free to remove this call through methods and modify the jsp.
//     */
//	public String getIsAdvancedSearch() {
//		return getCriteria().getIsAdvancedSearch();
//	}
//
//	public void setIsAdvancedSearch(String string) {
//        getCriteria().setIsAdvancedSearch(string);
//	}
//
//	public String getReturnAction() {
//		return returnAction;
//	}
//
//	public void setReturnAction(String returnAction) {
//		this.returnAction = returnAction;
//	}
//
//	public void setFromDateCreated(String fromDateCreated) {
//		getCriteria().setFromDateCreated(fromDateCreated);
//	}
//
//	public void setToDateCreated(String toDateCreated) {
//		getCriteria().setToDateCreated(toDateCreated);
//	}
//
//	public String getFromDateCreated() {
//		return getCriteria().getFromDateCreated();
//	}
//
//	public String getToDateCreated() {
//		return getCriteria().getToDateCreated();
//	}
//
//	public void setFromDateLastModified(String fromDateLastModified) {
//		getCriteria().setFromDateLastModified(fromDateLastModified);
//	}
//
//	public void setToDateLastModified(String toDateLastModified) {
//		getCriteria().setToDateLastModified(toDateLastModified);
//	}
//
//	public String getFromDateLastModified() {
//		return getCriteria().getFromDateLastModified();
//	}
//
//	public String getToDateLastModified() {
//		return getCriteria().getToDateLastModified();
//	}
//
//	public void setFromDateApproved(String fromDateApproved) {
//		getCriteria().setFromDateApproved(fromDateApproved);
//	}
//
//	public void setToDateApproved(String toDateApproved) {
//		getCriteria().setToDateApproved(toDateApproved);
//	}
//
//	public String getFromDateApproved() {
//		return getCriteria().getFromDateApproved();
//	}
//
//	public String getToDateApproved() {
//		return getCriteria().getToDateApproved();
//	}
//
//	public void setFromDateFinalized(String fromDateFinalized) {
//		getCriteria().setFromDateFinalized(fromDateFinalized);
//	}
//
//	public void setToDateFinalized(String toDateFinalized) {
//		getCriteria().setToDateFinalized(toDateFinalized);
//	}
//
//	public String getFromDateFinalized() {
//		return getCriteria().getFromDateFinalized();
//	}
//
//	public String getToDateFinalized() {
//		return getCriteria().getToDateFinalized();
//	}
//
//
//
//	public String getNamedSearch() {
//		return namedSearch;
//	}
//
//	public void setNamedSearch(String namedSearch) {
//		this.namedSearch = namedSearch;
//	}
//
//	public String getLookupableImplServiceName() {
//		return lookupableImplServiceName;
//	}
//
//	public void setLookupableImplServiceName(String lookupableImplServiceName) {
//		this.lookupableImplServiceName = lookupableImplServiceName;
//	}
//
//	/**
//	 * @param conversionFields
//	 *            The conversionFields to set.
//	 */
//	public void setConversionFields(String conversionFields) {
//		this.conversionFields = conversionFields;
//	}
//
//	/**
//	 * @return Returns the conversionFields.
//	 */
//	public String getConversionFields() {
//		return conversionFields;
//	}
//
//	public String getMethodToCall() {
//		return methodToCall;
//	}
//
//	public void setMethodToCall(String methodToCall) {
//		this.methodToCall = methodToCall;
//	}
//
//	public String getQuickFinderLookupable() {
//		return quickFinderLookupable;
//	}
//
//	public void setQuickFinderLookupable(String quickFinderLookupable) {
//		this.quickFinderLookupable = quickFinderLookupable;
//	}
//
//	public String getLookupType() {
//		return lookupType;
//	}
//
//	public void setLookupType(String lookupType) {
//		this.lookupType = lookupType;
//	}
//
//	public List<DocumentSearchRow> getProcessedSearchableAttributeRows() {
//	    if (isAdvancedSearch()) {
//	        return this.criteriaProcessor.processSearchableAttributeRowsForAdvancedSearch(getSearchableAttributeRows());
//	    } else {
//            return this.criteriaProcessor.processSearchableAttributeRowsForBasicSearch(getSearchableAttributeRows());
//	    }
//	}
//
//	public void setSearchableAttributeRows(List searchableAttributeRows) {
//	    this.searchableAttributeRows = searchableAttributeRows;
//	}
//
//	public List getSearchableAttributeRows() {
//	    return this.searchableAttributeRows;
//	}
//
//	public void addSearchableAttributeRow(DocumentSearchRow row) {
//	    getSearchableAttributeRows().add(row);
//	}
//
//	public DocumentSearchRow getSearchableAttributeRow(int index) {
//        while (getSearchableAttributeRows().size() <= index) {
//            DocumentSearchRow row = new DocumentSearchRow(new ArrayList<org.kuali.rice.kns.web.ui.Field>());
//            getSearchableAttributeRows().add(row);
//        }
//        return (DocumentSearchRow) getSearchableAttributeRows().get(index);
//	}
//
//	public void setSearchableAttributeRow(int index, DocumentSearchRow row) {
//	    getSearchableAttributeRows().set(index, row);
//	}
//
//	/**
//	 * @param searchableAttributeColumns
//	 *            The searchableAttributeColumns to set.
//	 */
//	public void setSearchableAttributeColumns(List searchableAttributeColumns) {
//		this.searchableAttributeColumns = searchableAttributeColumns;
//	}
//
//	/**
//	 * @return Returns the searchableAttributeColumns.
//	 */
//	public List getSearchableAttributeColumns() {
//		return searchableAttributeColumns;
//	}
//
//	public void addSearchableAttributeColumn(DocumentSearchColumn column) {
//		searchableAttributeColumns.add(column);
//	}
//
//	public DocumentSearchColumn getSearchableAttributeColumn(int index) {
//		while (getSearchableAttributeColumns().size() <= index) {
//		    DocumentSearchColumn column = new DocumentSearchColumn("", "", "");
//			getSearchableAttributeColumns().add(column);
//		}
//		return (DocumentSearchColumn) getSearchableAttributeColumns().get(index);
//	}
//
//	public void setSearchableAttributeColumn(int index, DocumentSearchColumn column) {
//		searchableAttributeColumns.set(index, column);
//	}
//
//	/**
//	 * @param propertyFields
//	 *            The propertyFields to set.
//	 */
//	public void setPropertyFields(List propertyFields) {
//		this.propertyFields = propertyFields;
//	}
//
//	/**
//	 * @return Returns the propertyFields.
//	 */
//	public List getPropertyFields() {
//		return propertyFields;
//	}
//
//	public void addPropertyField(SearchAttributeFormContainer attributeContainer) {
//		propertyFields.add(attributeContainer);
//	}
//
//    public SearchAttributeFormContainer getPropertyField(int index) {
//        while (getPropertyFields().size() <= index) {
//            SearchAttributeFormContainer attributeContainer = new SearchAttributeFormContainer();
//            addPropertyField(attributeContainer);
//        }
//        return (SearchAttributeFormContainer) getPropertyFields().get(index);
//    }
//
//    public SearchAttributeFormContainer getPropertyField(String key) {
//        if (StringUtils.isBlank(key)) {
//            return null;
//        }
//        for (Iterator iter = propertyFields.iterator(); iter.hasNext();) {
//            SearchAttributeFormContainer container = (SearchAttributeFormContainer) iter.next();
//            if (key.equals(container.getKey())) {
//                return container;
//            }
//        }
//        return null;
//    }
//
//	public void setPropertyField(int index, SearchAttributeFormContainer attributeContainer) {
//		propertyFields.set(index, attributeContainer);
//	}
//
//    public boolean isAdvancedSearch() {
//        return (StringUtils.equals(DocSearchCriteriaDTO.ADVANCED_SEARCH_INDICATOR_STRING,getCriteria().getIsAdvancedSearch()));
//    }
//
//	public StandardDocSearchCriteriaManager getDocumentSearchCriteriaManager() {
//	    if (isAdvancedSearch()) {
//	        return this.criteriaProcessor.getAdvancedSearchManager();
//	    } else {
//	        return this.criteriaProcessor.getBasicSearchManager();
//	    }
//	}
//
//	public boolean isHeaderBarEnabled() {
//		return headerBarEnabled;
//	}
//
//	public void setHeaderBarEnabled(boolean headerBarEnabled) {
//		this.headerBarEnabled = headerBarEnabled;
//	}
//
//	public boolean isSearchCriteriaEnabled() {
//		return searchCriteriaEnabled;
//	}
//
//	public void setSearchCriteriaEnabled(boolean searchCriteriaEnabled) {
//		this.searchCriteriaEnabled = searchCriteriaEnabled;
//	}
//
//	public boolean isShowSearchCriteria() {
//		if (!isSearchCriteriaEnabled()) {
//			return false;
//		}
//		if (isAdvancedSearch()) {
//		    return this.criteriaProcessor.isAdvancedSearchCriteriaDisplayed();
//		} else {
//		    return this.criteriaProcessor.isBasicSearchCriteriaDisplayed();
//		}
//	}
//
//	public boolean isShowHeaderBar() {
//		if (!isHeaderBarEnabled()) {
//			return false;
//		}
//		return this.criteriaProcessor.isHeaderBarDisplayed();
//	}
//
//	public String getSearchableAttributes() {
//		return searchableAttributes;
//	}
//
//	public void setSearchableAttributes(String secureAttributes) {
//		this.searchableAttributes = secureAttributes;
//	}
//
//	public boolean isInitiatorUser() {
//		return initiatorUser;
//	}
//
//	public void setInitiatorUser(boolean secureInitiatorSearch) {
//		this.initiatorUser = secureInitiatorSearch;
//	}

}
