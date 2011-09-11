package org.kuali.rice.kew.impl.document.lookup;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteriaContract;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResult;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResults;
import org.kuali.rice.kew.docsearch.DocumentLookupCriteriaProcessor;
import org.kuali.rice.kew.docsearch.service.DocumentSearchService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.exception.WorkflowServiceError;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupCriteriaConfiguration;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupResultSetConfiguration;
import org.kuali.rice.kew.framework.document.lookup.StandardResultField;
import org.kuali.rice.kew.lookup.valuefinder.SavedSearchValuesFinder;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.KEWPropertyConstants;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of lookupable helper service which handles the complex lookup behavior required by the KEW
 * document lookup screen.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentLookupCriteriaBoLookupableHelperService extends KualiLookupableHelperServiceImpl {

    private static final String DOCUMENT_ATTRIBUTE_PROPERTY_NAME_PREFIX = "documentAttribute.";

    private static final String SAVED_SEARCH_NAME_PARAM = "savedSearchToLoadAndExecute";
    private static final String SUPER_USER_SEARCH_PARAM = "superUserSearch";
    private static final String ADVANCED_SEARCH_PARAM = "isAdvancedSearch";

    // warning message keys

    private static final String EXCEED_THRESHOLD_MESSAGE_KEY = "docsearch.DocumentSearchService.exceededThreshold";
    private static final String SECURITY_FILTERED_MESSAGE_KEY = "docsearch.DocumentSearchService.securityFiltered";
    private static final String EXCEED_THRESHOLD_AND_SECURITY_FILTERED_MESSAGE_KEY = "docsearch.DocumentSearchService.exceededThresholdAndSecurityFiltered";

    // TODO - Rice 2.0 - can we move these somewhere else?  Should be the job of this class to know what the defaults are for these
    private static final boolean DOCUMENT_HANDLER_POPUP_DEFAULT = true;
    private static final boolean ROUTE_LOG_POPUP_DEFAULT = true;

    // injected services

    private DocumentSearchService documentSearchService;
    private DocumentLookupCriteriaProcessor documentLookupCriteriaProcessor;
    private DocumentLookupCriteriaTranslator documentLookupCriteriaTranslator;

    // unfortunately, lookup helpers are stateful, need to store these here for other methods to use
    protected DocumentLookupResults lookupResults = null;
    protected DocumentLookupCriteria criteria = null;

    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues,
            boolean unbounded) {
        criteria = loadCriteria(fieldValues);
        lookupResults = null;
        try {
            lookupResults = KEWServiceLocator.getDocumentSearchService().lookupDocuments(
                    GlobalVariables.getUserSession().getPrincipalId(), criteria);
        } catch (WorkflowServiceErrorException wsee) {
            for (WorkflowServiceError workflowServiceError : (List<WorkflowServiceError>) wsee.getServiceErrors()) {
                if (workflowServiceError.getMessageMap() != null && workflowServiceError.getMessageMap().hasErrors()) {
                    // merge the message maps
                    GlobalVariables.getMessageMap().merge(workflowServiceError.getMessageMap());
                } else {
                    GlobalVariables.getMessageMap().putError(workflowServiceError.getMessage(),
                            RiceKeyConstants.ERROR_CUSTOM, workflowServiceError.getMessage());
                }
            }
        }

        if (!GlobalVariables.getMessageMap().hasNoErrors() || lookupResults == null) {
            throw new ValidationException("error with doc search");
        }

        populateResultWarningMessages(lookupResults);

        List<DocumentLookupResult> individualLookupResults = lookupResults.getLookupResults();

        setBackLocation(fieldValues.get(KRADConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KRADConstants.DOC_FORM_KEY));

        applyCriteriaChangesToFields(criteria);

        return populateSearchResults(individualLookupResults);

    }

    /**
     * Inspects the lookup results to determine if any warning messages should be published to the message map.
     */
    protected void populateResultWarningMessages(DocumentLookupResults lookupResults) {
        // check various warning conditions
        boolean overThreshold = lookupResults.isOverThreshold();
        int numFiltered = lookupResults.getNumberOfSecurityFilteredResults();
        int numResults = lookupResults.getLookupResults().size();
        if (overThreshold && numFiltered > 0) {
            GlobalVariables.getMessageMap().putWarning(KRADConstants.GLOBAL_MESSAGES,
                    EXCEED_THRESHOLD_AND_SECURITY_FILTERED_MESSAGE_KEY,
                    String.valueOf(numResults),
                    String.valueOf(numFiltered));
        } else if (numFiltered > 0) {
            GlobalVariables.getMessageMap().putWarning(KRADConstants.GLOBAL_MESSAGES,
                    SECURITY_FILTERED_MESSAGE_KEY,
                    String.valueOf(numFiltered));
        } else if (overThreshold) {
            GlobalVariables.getMessageMap().putWarning(KRADConstants.GLOBAL_MESSAGES,
                    EXCEED_THRESHOLD_MESSAGE_KEY,
                    String.valueOf(numResults));
        }
    }

    /**
     * Applies changes that might have happend to the criteria back to the fields so that they show up on the form.
     * Namely, this handles populating the form with today's date if the create date was not filled in on the form.
     */
    protected void applyCriteriaChangesToFields(DocumentLookupCriteriaContract criteria) {
        for (Row row : this.getRows()) {
            for (Field field : row.getFields()) {
                if(StringUtils.equals(field.getPropertyName(), KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + "dateCreated") && StringUtils.isEmpty(field.getPropertyValue())) {
                    if (criteria.getDateCreatedFrom() != null) {
                        field.setPropertyValue(CoreApiServiceLocator.getDateTimeService().toDateString(criteria.getDateCreatedFrom().toDate()));
                    }
                }
            }
        }
    }

    /**
     * Cleans up various issues with fieldValues coming from the lookup form (namely, that they don't include
     * multi-valued field values!). Handles these by adding them comma-separated.
     */
    protected Map<String, String> cleanupFieldValues(Map<String, String> fieldValues, Map<String, String[]> parameters) {
        Map<String, String> cleanedUpFieldValues = new HashMap<String, String>(fieldValues);
        if (ArrayUtils.isNotEmpty(parameters.get(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_STATUS_CODE))) {
            cleanedUpFieldValues.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_STATUS_CODE,
                    StringUtils.join(parameters.get(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_STATUS_CODE), ","));
        }
        Map<String, String> documentAttributeFieldValues = new HashMap<String, String>();
        for (String parameterName : parameters.keySet()) {
            if (parameterName.contains(KEWConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX)) {
                String[] value = parameters.get(parameterName);
                if (ArrayUtils.isNotEmpty(value)) {
                    documentAttributeFieldValues.put(parameterName, StringUtils.join(value, " " + SearchOperator.OR.op() + " "));
                }
            }
        }
        // if any of the document attributes are range values, process them
        documentAttributeFieldValues.putAll(preProcessRangeFields(documentAttributeFieldValues));
        cleanedUpFieldValues.putAll(documentAttributeFieldValues);
        return cleanedUpFieldValues;
    }

    /**
     * Loads the document lookup criteria from the given map of field values as submitted from the search screen.
     */
    protected DocumentLookupCriteria loadCriteria(Map<String, String> fieldValues) {
        fieldValues = cleanupFieldValues(fieldValues, getParameters());
        String savedSearchToLoad = fieldValues.get(SAVED_SEARCH_NAME_PARAM);
        boolean savedSearch = StringUtils.isNotBlank(savedSearchToLoad);
        if (savedSearch) {
            DocumentLookupCriteria criteria = getDocumentSearchService().getSavedSearchCriteria(
                    GlobalVariables.getUserSession().getPrincipalId(), savedSearchToLoad);
            if (criteria != null) {
                return criteria;
            }
        }
        // either it wasn't a saved search or the saved search failed to resolve
        return getDocumentLookupCriteriaTranslator().translate(fieldValues);
    }

    protected List<DocumentLookupCriteriaBo> populateSearchResults(List<DocumentLookupResult> lookupResults) {
        List<DocumentLookupCriteriaBo> searchResults = new ArrayList<DocumentLookupCriteriaBo>();
        for (DocumentLookupResult lookupResult : lookupResults) {
            DocumentLookupCriteriaBo result = new DocumentLookupCriteriaBo();
            result.populateFromDocumentLookupResult(lookupResult);
            searchResults.add(result);
        }
        return searchResults;
    }

    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Collection lookupResult = super.performLookup(lookupForm, resultTable, bounded);
        postProcessResults(resultTable, this.lookupResults);
        return lookupResult;
    }

    /**
     * Custom implementation of getInquiryUrl that sets up doc handler link.
     */
	@Override
	public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        DocumentLookupCriteriaBo criteriaBo = (DocumentLookupCriteriaBo)bo;
		if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_ID.equals(propertyName)) {
            return generateDocumentHandlerUrl(criteriaBo.getDocumentId(), criteriaBo.getDocumentType(),
                    isSuperUserSearch());
		} else if (KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG.equals(propertyName)) {
            return generateRouteLogUrl(criteriaBo.getDocumentId());
        }
		return super.getInquiryUrl(bo, propertyName);
	}

    /**
     * Generates the appropriate document handler url for the given document.  If superUserSearch is true then a super
     * user doc handler link will be generated if the document type policy allows it.
     */
    protected HtmlData.AnchorHtmlData generateDocumentHandlerUrl(String documentId, DocumentType documentType, boolean superUserSearch) {
        HtmlData.AnchorHtmlData link = new HtmlData.AnchorHtmlData();
        link.setDisplayText(documentId);
		if (isDocumentHandlerPopup()) {
            link.setTarget("_blank");
		}
        String url = ConfigContext.getCurrentContextConfig().getProperty(Config.KEW_URL) + "/";
		if (superUserSearch) {
			if (documentType.getUseWorkflowSuperUserDocHandlerUrl().getPolicyValue().booleanValue()) {
                url += "SuperUser.do?methodToCall=displaySuperUserDocument&documentId=" + documentId;
            } else {
				url = KEWConstants.DOC_HANDLER_REDIRECT_PAGE
						+ "?" + KEWConstants.COMMAND_PARAMETER + "="
						+ KEWConstants.SUPERUSER_COMMAND + "&"
						+ KEWConstants.DOCUMENT_ID_PARAMETER + "="
						+ documentId;
			}
		} else {
			url += KEWConstants.DOC_HANDLER_REDIRECT_PAGE + "?"
					+ KEWConstants.COMMAND_PARAMETER + "="
					+ KEWConstants.DOCSEARCH_COMMAND + "&"
					+ KEWConstants.DOCUMENT_ID_PARAMETER + "="
					+ documentId;
		}
        link.setHref(url);
        return link;
    }

    protected HtmlData.AnchorHtmlData generateRouteLogUrl(String documentId) {
        HtmlData.AnchorHtmlData link = new HtmlData.AnchorHtmlData();
		if (isRouteLogPopup()) {
            link.setTarget("_blank");
		}
        link.setDisplayText("Route Log for document " + documentId);
        String url = ConfigContext.getCurrentContextConfig().getProperty(Config.KEW_URL) + "/" +
                "RouteLog.do?documentId=" + documentId;
        link.setHref(url);
        return link;
    }

    /**
     * Returns true if the document handler should open in a new window.
     */
    protected boolean isDocumentHandlerPopup() {
        return BooleanUtils.toBooleanDefaultIfNull(
                CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(
                    KEWConstants.KEW_NAMESPACE,
                    KRADConstants.DetailTypes.DOCUMENT_LOOKUP_DETAIL_TYPE,
                    KEWConstants.DOCUMENT_SEARCH_DOCUMENT_POPUP_IND),
                DOCUMENT_HANDLER_POPUP_DEFAULT);
	}

    /**
     * Returns true if the route log should open in a new window.
     */
    public boolean isRouteLogPopup() {
		return BooleanUtils.toBooleanDefaultIfNull(
                CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(KEWConstants.KEW_NAMESPACE,
                        KRADConstants.DetailTypes.DOCUMENT_LOOKUP_DETAIL_TYPE,
                        KEWConstants.DOCUMENT_SEARCH_ROUTE_LOG_POPUP_IND), ROUTE_LOG_POPUP_DEFAULT);
	}
    
    /**
     * Returns true if the current search being executed is a super user search.
     */
    protected boolean isSuperUserSearch() {
        if(this.getParameters().containsKey(SUPER_USER_SEARCH_PARAM)) {
            String[] superUserSearchParams = (String[])this.getParameters().get(SUPER_USER_SEARCH_PARAM);
            if (ArrayUtils.isNotEmpty(superUserSearchParams)) {
                return Boolean.TRUE.toString().equalsIgnoreCase(superUserSearchParams[0]);
            }
        }
        return false;
    }

    /**
     * Returns true if the current search being executed is an "advanced" search.
     */
    protected boolean isAdvancedSearch() {
        if(this.getParameters().containsKey(ADVANCED_SEARCH_PARAM)) {
            String[] advancedSearchParams = (String[])this.getParameters().get(ADVANCED_SEARCH_PARAM);
	        return Boolean.TRUE.toString().equalsIgnoreCase(advancedSearchParams[0]);
        }
        return false;
    }

    @Override
	protected void setRows() {
	    this.setRows(null);
	}

    /**
     * Sets the rows for the search criteria.  This method will delegate to the DocumentLookupCriteriaProcessor
     * in order to pull in fields for custom search attributes.
     *
     * @param documentTypeName the name of the document type currently entered on the form, if this is a valid document
     * type then it may have search attribute fields that need to be displayed
     */
	protected void setRows(String documentTypeName) {
		if (getRows() == null) {
		    super.setRows();
		}
		List<Row> lookupRows = new ArrayList<Row>();
		//copy the current rows
		for (Row row : getRows()) {
			lookupRows.add(row);
		}
		//clear out
		getRows().clear();

		DocumentType docType = getValidDocumentType(documentTypeName);

        boolean advancedSearch = isAdvancedSearch();
        boolean superUserSearch = isSuperUserSearch();

		//call get rows
		List<Row> rows = getDocumentLookupCriteriaProcessor().getRows(docType,lookupRows, advancedSearch, superUserSearch);

		BusinessObjectEntry boe = (BusinessObjectEntry) KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(this.getBusinessObjectClass().getName());
        int numCols = boe.getLookupDefinition().getNumOfColumns();
        if(numCols == 0) {
			numCols = KRADConstants.DEFAULT_NUM_OF_COLUMNS;
		}

		super.getRows().addAll(FieldUtils.wrapFields(this.getFields(rows), numCols));

	}

    private List<Field> getFields(List<Row> rows) {
        List<Field> rList = new ArrayList<Field>();
        for (Row r : rows) {
            for (Field f : r.getFields()) {
                rList.add(f);
            }
        }
        return rList;
    }

    /**
     * Checks for a valid document type with the given name in a case-sensitive manner.
     *
     * @return the DocumentType which matches the given name or null if no valid document type could be found
     */
    private DocumentType getValidDocumentType(String documentTypeName) {
        if (StringUtils.isNotEmpty(documentTypeName)) {
            DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByNameCaseInsensitive(documentTypeName.trim());
            if (documentType != null && documentType.isActive()) {
                return documentType;
            }
    	}
    	return null;
    }

    @Override
	public String getSupplementalMenuBar() {
		boolean advancedSearch = isAdvancedSearch();
		boolean superUserSearch = isSuperUserSearch();
		StringBuilder suppMenuBar = new StringBuilder();

        String jqueryInclude = "<script type=\"text/javascript\" src=\"../krad/scripts/jquery/jquery-1.5.2.js\"></script>\n";
        suppMenuBar.append(jqueryInclude);

        String advancedToggleValue = isAdvancedSearch() ? "'NO'" : "'YES'";
        String advancedToggleScript = "$('input[name=" + ADVANCED_SEARCH_PARAM + "]').val(" + advancedToggleValue + ");customLookupChanged();return false";

        // Add the detailed-search-toggling button.
        suppMenuBar.append("<input type=\"image\" onclick=\"").
                append(advancedToggleScript).
                append("\" name=\"toggleAdvancedSearch\" class=\"tinybutton\" src=\"..").
                append(KEWConstants.WEBAPP_DIRECTORY).
                append(advancedSearch ?
                        "/images/tinybutton-basicsearch.gif\" alt=\"basic search\" title=\"basic search\" />" :
                        "/images/tinybutton-detailedsearch.gif\" alt=\"detailed search\" title=\"detailed search\" />");

        String superUserToggleValue = isSuperUserSearch() ? "'NO'" : "'YES'";
        String superUserToggleScript = "$('input[name=" + SUPER_USER_SEARCH_PARAM + "]').val(" + superUserToggleValue + ");customLookupChanged();return false";

		// Add the superuser-search-toggling button.
        suppMenuBar.append("&nbsp;").append("<input type=\"image\" onclick=\"").
                append(superUserToggleScript).
                append("\" name=\"toggleSuperUserSearch\" class=\"tinybutton\" src=\"..").
                append(KEWConstants.WEBAPP_DIRECTORY).
                append(superUserSearch ?
                        "/images/tinybutton-nonsupusearch.gif\" alt=\"non-superuser search\" title=\"non-superuser search\" />" :
                        "/images/tinybutton-superusersearch.gif\" alt=\"superuser search\" title=\"superuser search\" />");

		// Add the "clear saved searches" button.
		suppMenuBar.append("&nbsp;").append("<input type=\"image\" name=\"methodToCall.customLookupableMethodCall.(([true]))\" class=\"tinybutton\" src=\"..").append(KEWConstants.WEBAPP_DIRECTORY).append("/images/tinybutton-clearsavedsearch.gif\" alt=\"clear saved searches\" title=\"clear saved searches\" />");

		return suppMenuBar.toString();
	}

    @Override
	public boolean shouldDisplayHeaderNonMaintActions() {
        // TODO - Rice 2.0 - ensure this can be customized via url
		return true;
	}

	@Override
	public boolean shouldDisplayLookupCriteria() {
        // TODO - Rice 2.0 - ensure this can be customized via url
		return true;
	}

    @Override
	public boolean checkForAdditionalFields(Map fieldValues) {
        // The given map is a Map<String, String>
		String documentTypeName = (String)fieldValues.get("documentTypeName");
        if (StringUtils.isNotBlank(documentTypeName)) {
	        setRows(documentTypeName);
        }
	    return true;
	}

    @Override
	public Field getExtraField() {
		SavedSearchValuesFinder savedSearchValuesFinder = new SavedSearchValuesFinder();
		List<KeyValue> savedSearchValues = savedSearchValuesFinder.getKeyValues();
		Field savedSearch = new Field();
		savedSearch.setPropertyName(SAVED_SEARCH_NAME_PARAM);
		savedSearch.setFieldType(Field.DROPDOWN_SCRIPT);
		savedSearch.setScript("customLookupChanged()");
		savedSearch.setFieldValidValues(savedSearchValues);
		savedSearch.setFieldLabel("Saved Searches");
		return savedSearch;
	}

    @Override
	public void performClear(LookupForm lookupForm) {
        DocumentLookupCriteria criteria = loadCriteria(lookupForm.getFields());
        super.performClear(lookupForm);
        repopulateSearchTypeFlags();
		DocumentType documentType = getValidDocumentType(criteria.getDocumentTypeName());
		if (documentType != null) {
            DocumentLookupCriteria clearedCriteria = documentSearchService.clearCriteria(documentType, criteria);
            applyCriteriaChangesToFields(DocumentLookupCriteria.Builder.create(clearedCriteria));
		}
	}

    /**
     * Repopulate the fields indicating advanced/superuser search type.
     */
    protected void repopulateSearchTypeFlags() {
        boolean advancedSearch = isAdvancedSearch();
        boolean superUserSearch = isSuperUserSearch();
        int fieldsRepopulated = 0;
        for (Row row : super.getRows()) {
            if (fieldsRepopulated >= 2) {
                break;
            }
            for (Field tempField : row.getFields()) {
                if (ADVANCED_SEARCH_PARAM.equals(tempField.getPropertyName())) {
                    tempField.setPropertyValue(Boolean.toString(advancedSearch));
                    fieldsRepopulated++;
                } else if (SUPER_USER_SEARCH_PARAM.equals(tempField.getPropertyName())) {
                    tempField.setPropertyValue(Boolean.toString(superUserSearch));
                    fieldsRepopulated++;
                }
            }
        }

    }

    /**
     * Takes a collection of result rows and does final processing on them.
     */
    protected void postProcessResults(Collection<ResultRow> resultRows, DocumentLookupResults lookupResults) {
        if (resultRows.size() != lookupResults.getLookupResults().size()) {
            throw new IllegalStateException("Encountered a mismatch between ResultRow items and document lookup results "
                    + resultRows.size() + " != " + lookupResults.getLookupResults().size());
        }
        DocumentType documentType = getValidDocumentType(criteria.getDocumentTypeName());
        DocumentLookupResultSetConfiguration resultSetConfiguration = null;
        DocumentLookupCriteriaConfiguration criteriaConfiguration = null;
        if (documentType != null) {
            resultSetConfiguration =
                KEWServiceLocator.getDocumentLookupCustomizationMediator().customizeResultSetConfiguration(
                        documentType, criteria);
            criteriaConfiguration =
                    KEWServiceLocator.getDocumentLookupCustomizationMediator().getDocumentLookupCriteriaConfiguration(documentType);

        }
        int index = 0;
        for (ResultRow resultRow : resultRows) {
            DocumentLookupResult lookupResult = lookupResults.getLookupResults().get(index);
            executeColumnCustomization(resultRow, lookupResult, resultSetConfiguration, criteriaConfiguration);
            index++;
        }
    }

    /**
     * Executes customization of columns, could include removing certain columns or adding additional columns to the
     * result row (in cases where columns are added by document lookup customization, such as searchable attributes).
     */
    protected void executeColumnCustomization(ResultRow resultRow, DocumentLookupResult lookupResult,
            DocumentLookupResultSetConfiguration resultSetConfiguration,
            DocumentLookupCriteriaConfiguration criteriaConfiguration) {
        if (resultSetConfiguration == null) {
            resultSetConfiguration = DocumentLookupResultSetConfiguration.Builder.create().build();
        }
        if (criteriaConfiguration == null) {
            criteriaConfiguration = DocumentLookupCriteriaConfiguration.Builder.create().build();
        }
        List<StandardResultField> standardFieldsToRemove = resultSetConfiguration.getStandardResultFieldsToRemove();
        if (standardFieldsToRemove == null) {
            standardFieldsToRemove = Collections.emptyList();
        }
        List<Column> newColumns = new ArrayList<Column>();
        for (Column standardColumn : resultRow.getColumns()) {
            if (!standardFieldsToRemove.contains(StandardResultField.fromFieldName(standardColumn.getPropertyName()))) {
                newColumns.add(standardColumn);
                // modify the route log column so that xml values are not escaped (allows for the route log <img ...> to be
                // rendered properly)
                if (standardColumn.getPropertyName().equals(
                        KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG)) {
                    standardColumn.setEscapeXMLValue(false);
                }
            }
        }
        
        // determine which document attribute fields should be added
        List<RemotableAttributeField> searchAttributeFields = criteriaConfiguration.getFlattenedSearchAttributeFields();
        List<String> additionalFieldNamesToInclude = new ArrayList<String>();
        if (!resultSetConfiguration.isOverrideSearchableAttributes()) {
            for (RemotableAttributeField searchAttributeField : searchAttributeFields) {
                // TODO - Rice 2.0 - add check here to make sure the searchable attribute should be displayed in result set
                // right now this is default always including all searchable attributes!
                additionalFieldNamesToInclude.add(searchAttributeField.getName());
            }
        }
        if (resultSetConfiguration.getCustomFieldNamesToAdd() != null) {
            additionalFieldNamesToInclude.addAll(resultSetConfiguration.getCustomFieldNamesToAdd());
        }

        // now assemble the custom columns
        List<Column> customColumns = new ArrayList<Column>();
        List<Column> additionalAttributeColumns = FieldUtils.constructColumnsFromAttributeFields(
                resultSetConfiguration.getAdditionalAttributeFields());

        outer:for (String additionalFieldNameToInclude : additionalFieldNamesToInclude) {
            // search the search attribute fields
            for (RemotableAttributeField searchAttributeField : searchAttributeFields) {
                if (additionalFieldNameToInclude.equals(searchAttributeField.getName())) {
                    Column searchAttributeColumn = FieldUtils.constructColumnFromAttributeField(searchAttributeField);
                    wrapDocumentAttributeColumnName(searchAttributeColumn);
                    customColumns.add(searchAttributeColumn);
                    continue outer;
                }
            }
            for (Column additionalAttributeColumn : additionalAttributeColumns) {
                if (additionalFieldNameToInclude.equals(additionalAttributeColumn.getPropertyName())) {
                    wrapDocumentAttributeColumnName(additionalAttributeColumn);
                    customColumns.add(additionalAttributeColumn);
                    continue outer;
                }
            }
            LOG.warn("Failed to locate a proper column definition for requested additional field to include in"
                    + "result set with name '"
                    + additionalFieldNameToInclude
                    + "'");
        }
        populateCustomColumns(customColumns, lookupResult);

        // now merge the custom columns into the standard columns right before the route log (if the route log column wasn't removed!)
        if (newColumns.isEmpty() || !StandardResultField.ROUTE_LOG.isFieldNameValid(newColumns.get(newColumns.size() - 1).getPropertyName())) {
            newColumns.addAll(customColumns);
        } else {
            newColumns.addAll(newColumns.size() - 1, customColumns);
        }
        resultRow.setColumns(newColumns);
    }

    protected void populateCustomColumns(List<Column> customColumns, DocumentLookupResult lookupResult) {
        for (Column customColumn : customColumns) {
            DocumentAttribute documentAttribute =
                    lookupResult.getSingleDocumentAttributeByName(customColumn.getPropertyName());
            if (documentAttribute != null && documentAttribute.getValue() != null) {
                wrapDocumentAttributeColumnName(customColumn);
                // TODO - Rice 2.0 - in Rice 1.0.x we currently only display one value, but it probably makes sense to display a comma-separated
                // list moving forward if the attribute has more than one value
                Formatter formatter = customColumn.getFormatter();
                customColumn.setPropertyValue(formatter.format(documentAttribute.getValue()).toString());
            }
        }
    }

    private void wrapDocumentAttributeColumnName(Column column) {
        // TODO - comment out for now, not sure we really want to do this...
        //column.setPropertyName(DOCUMENT_ATTRIBUTE_PROPERTY_NAME_PREFIX + column.getPropertyName());
    }

    public void setDocumentSearchService(DocumentSearchService documentSearchService) {
        this.documentSearchService = documentSearchService;
    }

    public DocumentSearchService getDocumentSearchService() {
        return documentSearchService;
    }

    public DocumentLookupCriteriaProcessor getDocumentLookupCriteriaProcessor() {
        return documentLookupCriteriaProcessor;
    }

    public void setDocumentLookupCriteriaProcessor(DocumentLookupCriteriaProcessor documentLookupCriteriaProcessor) {
        this.documentLookupCriteriaProcessor = documentLookupCriteriaProcessor;
    }

    public DocumentLookupCriteriaTranslator getDocumentLookupCriteriaTranslator() {
        return documentLookupCriteriaTranslator;
    }

    public void setDocumentLookupCriteriaTranslator(DocumentLookupCriteriaTranslator documentLookupCriteriaTranslator) {
        this.documentLookupCriteriaTranslator = documentLookupCriteriaTranslator;
    }

}
