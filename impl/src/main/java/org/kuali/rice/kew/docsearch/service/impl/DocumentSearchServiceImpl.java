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

package org.kuali.rice.kew.docsearch.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.reflect.ObjectDefinition;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.document.search.DocumentSearchResults;
import org.kuali.rice.kew.docsearch.DocumentLookupInternalUtils;
import org.kuali.rice.kew.framework.document.search.AttributeFields;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeFactory;
import org.kuali.rice.kew.docsearch.DocumentLookupCustomizationMediator;
import org.kuali.rice.kew.docsearch.dao.DocumentSearchDAO;
import org.kuali.rice.kew.docsearch.service.DocumentSearchService;
import org.kuali.rice.kew.doctype.SecuritySession;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.exception.WorkflowServiceError;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.framework.document.search.DocumentSearchCriteriaConfiguration;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultValue;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultValues;
import org.kuali.rice.kew.impl.document.search.DocumentSearchGenerator;
import org.kuali.rice.kew.impl.document.search.DocumentSearchGeneratorImpl;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.useroptions.UserOptions;
import org.kuali.rice.kew.useroptions.UserOptionsService;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.util.GlobalVariables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocumentSearchServiceImpl implements DocumentSearchService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentSearchServiceImpl.class);

	private static final int MAX_SEARCH_ITEMS = 5;
	private static final String LAST_SEARCH_ORDER_OPTION = "DocSearch.LastSearch.Order";
	private static final String NAMED_SEARCH_ORDER_BASE = "DocSearch.NamedSearch.";
	private static final String LAST_SEARCH_BASE_NAME = "DocSearch.LastSearch.Holding";

	private volatile ConfigurationService kualiConfigurationService;
    private DocumentLookupCustomizationMediator documentLookupCustomizationMediator;

	private DocumentSearchDAO docSearchDao;
	private UserOptionsService userOptionsService;

	public void setDocumentSearchDAO(DocumentSearchDAO docSearchDao) {
		this.docSearchDao = docSearchDao;
	}

	public void setUserOptionsService(UserOptionsService userOptionsService) {
		this.userOptionsService = userOptionsService;
	}

    public void setDocumentLookupCustomizationMediator(DocumentLookupCustomizationMediator documentLookupCustomizationMediator) {
        this.documentLookupCustomizationMediator = documentLookupCustomizationMediator;
    }

    protected DocumentLookupCustomizationMediator getDocumentLookupCustomizationMediator() {
        return this.documentLookupCustomizationMediator;
    }

	public void clearNamedSearches(String principalId) {
		String[] clearListNames = { NAMED_SEARCH_ORDER_BASE + "%", LAST_SEARCH_BASE_NAME + "%", LAST_SEARCH_ORDER_OPTION + "%" };
        for (String clearListName : clearListNames)
        {
            List<UserOptions> records = userOptionsService.findByUserQualified(principalId, clearListName);
            for (UserOptions userOptions : records) {
                userOptionsService.deleteUserOptions((UserOptions) userOptions);
            }
        }
	}

    public DocumentSearchCriteria getNamedSearchCriteria(String principalId, String searchName) {
        return getSavedSearchCriteria(principalId, NAMED_SEARCH_ORDER_BASE + searchName);
    }

    public DocumentSearchCriteria getSavedSearchCriteria(String principalId, String searchName) {
        UserOptions savedSearch = userOptionsService.findByOptionId(searchName, principalId);
        if (savedSearch == null) {
            return null;
        }
        return getCriteriaFromSavedSearch(savedSearch);
    }

    protected DocumentSearchCriteria getCriteriaFromSavedSearch(UserOptions savedSearch) {
        String optionValue = savedSearch.getOptionVal();
        try {
            return DocumentLookupInternalUtils.unmarshalDocumentLookupCriteria(optionValue);
        } catch (IOException e) {
            throw new WorkflowRuntimeException("Failed to load saved search for name '" + savedSearch.getOptionId() + "'", e);
        }
    }

    private String getOptionCriteriaField(UserOptions userOption, String fieldName) {
        String value = userOption.getOptionVal();
        if (value != null) {
            String[] fields = value.split(",,");
            for (String field : fields)
            {
                if (field.startsWith(fieldName + "="))
                {
                    return field.substring(field.indexOf(fieldName) + fieldName.length() + 1, field.length());
                }
            }
        }
        return null;
    }

    @Override
	public DocumentSearchResults lookupDocuments(String principalId, DocumentSearchCriteria criteria) {
		DocumentSearchGenerator docSearchGenerator = getStandardDocumentSearchGenerator();
		DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(criteria.getDocumentTypeName());
        DocumentSearchCriteria.Builder criteriaBuilder = DocumentSearchCriteria.Builder.create(criteria);
        validateDocumentSearchCriteria(docSearchGenerator, criteriaBuilder);
        DocumentSearchCriteria builtCriteria = applyCriteriaCustomizations(documentType, criteriaBuilder.build());
        builtCriteria = applyCriteriaDefaults(builtCriteria);
        boolean criteriaModified = !criteria.equals(builtCriteria);
        List<RemotableAttributeField> searchFields = determineSearchFields(documentType);
        DocumentSearchResults.Builder searchResults = docSearchDao.findDocuments(docSearchGenerator, builtCriteria, criteriaModified, searchFields);
        if (documentType != null) {
            DocumentSearchResultValues resultValues = getDocumentLookupCustomizationMediator().customizeResults(documentType, builtCriteria, searchResults.build());
            if (resultValues != null && CollectionUtils.isNotEmpty(resultValues.getResultValues())) {
                Map<String, DocumentSearchResultValue> resultValueMap = new HashMap<String, DocumentSearchResultValue>();
                for (DocumentSearchResultValue resultValue : resultValues.getResultValues()) {
                    resultValueMap.put(resultValue.getDocumentId(), resultValue);
                }
                for (DocumentSearchResult.Builder result : searchResults.getSearchResults()) {
                    DocumentSearchResultValue value = resultValueMap.get(result.getDocument().getDocumentId());
                    if (value != null) {
                        applyResultCustomization(result, value);
                    }
                }
            }
        }

        if (StringUtils.isNotBlank(principalId) && !searchResults.getSearchResults().isEmpty()) {
            DocumentSearchResults builtResults = searchResults.build();
            Set<String> authorizedDocumentIds = KEWServiceLocator.getDocumentSecurityService().documentLookupResultAuthorized(principalId, builtResults, new SecuritySession(principalId));
            if (CollectionUtils.isNotEmpty(authorizedDocumentIds)) {
                int numFiltered = 0;
                List<DocumentSearchResult.Builder> finalResults = new ArrayList<DocumentSearchResult.Builder>();
                for (DocumentSearchResult.Builder result : searchResults.getSearchResults()) {
                    if (authorizedDocumentIds.contains(result.getDocument().getDocumentId())) {
                        finalResults.add(result);
                    } else {
                        numFiltered++;
                    }
                }
                searchResults.setLookupResults(finalResults);
                searchResults.setNumberOfSecurityFilteredResults(numFiltered);
            }
        }
        saveSearch(principalId, builtCriteria);
        return searchResults.build();
	}

    protected void applyResultCustomization(DocumentSearchResult.Builder result, DocumentSearchResultValue value) {
        Map<String, List<DocumentAttribute.AbstractBuilder<?>>> customizedAttributeMap =
                new LinkedHashMap<String, List<DocumentAttribute.AbstractBuilder<?>>>();
        for (DocumentAttribute customizedAttribute : value.getDocumentAttributes()) {
            List<DocumentAttribute.AbstractBuilder<?>> attributesForName = customizedAttributeMap.get(value.getDocumentId());
            if (attributesForName == null) {
                attributesForName = new ArrayList<DocumentAttribute.AbstractBuilder<?>>();
                customizedAttributeMap.put(value.getDocumentId(), attributesForName);
            }
            attributesForName.add(DocumentAttributeFactory.loadContractIntoBuilder(customizedAttribute));
        }
        // keep track of what we've already applied customizations for, since those will replace existing attributes with that name
        Set<String> documentAttributeNamesCustomized = new HashSet<String>();
        List<DocumentAttribute.AbstractBuilder<?>> newDocumentAttributes = new ArrayList<DocumentAttribute.AbstractBuilder<?>>();
        for (DocumentAttribute.AbstractBuilder<?> documentAttribute : result.getDocumentAttributes()) {
            String name = documentAttribute.getName();
            if (!documentAttributeNamesCustomized.contains(name) && customizedAttributeMap.containsKey(name)) {
                documentAttributeNamesCustomized.add(name);
                newDocumentAttributes.addAll(customizedAttributeMap.get(name));
            }
        }
    }


    /**
     * Applies any document type-specific customizations to the lookup criteria.  If no customizations are configured
     * for the document type, this method will simply return the criteria that is passed to it.  If
     * the given DocumentType is null, then this method will also simply return the criteria that is passed to it.
     */
    protected DocumentSearchCriteria applyCriteriaCustomizations(DocumentType documentType, DocumentSearchCriteria criteria) {
        if (documentType == null) {
            return criteria;
        }
        DocumentSearchCriteria customizedCriteria = getDocumentLookupCustomizationMediator().customizeCriteria(documentType, criteria);
        if (customizedCriteria != null) {
            return customizedCriteria;
        }
        return criteria;
    }

    protected DocumentSearchCriteria applyCriteriaDefaults(DocumentSearchCriteria criteria) {
        DocumentSearchCriteria.Builder comparisonCriteria = createEmptyComparisonCriteria(criteria);
        boolean isCriteriaEmpty = criteria.equals(comparisonCriteria.build());
        boolean isTitleOnly = false;
        if (!isCriteriaEmpty) {
            comparisonCriteria.setTitle(criteria.getTitle());
            isTitleOnly = criteria.equals(comparisonCriteria.build());
        }

        if (isCriteriaEmpty || isTitleOnly) {
            DocumentSearchCriteria.Builder criteriaBuilder = DocumentSearchCriteria.Builder.create(criteria);
            Integer defaultCreateDateDaysAgoValue = null;
            if (isCriteriaEmpty) {
                // if they haven't set any criteria, default the from created date to today minus days from constant variable
                defaultCreateDateDaysAgoValue = KEWConstants.DOCUMENT_SEARCH_NO_CRITERIA_CREATE_DATE_DAYS_AGO;
            } else if (isTitleOnly) {
                // If the document title is the only field which was entered, we want to set the "from" date to be X
                // days ago.  This will allow for a more efficient query.
                defaultCreateDateDaysAgoValue = KEWConstants.DOCUMENT_SEARCH_DOC_TITLE_CREATE_DATE_DAYS_AGO;
            }
            if (defaultCreateDateDaysAgoValue != null) {
                // add a default create date
                MutableDateTime mutableDateTime = new MutableDateTime();
                mutableDateTime.addDays(defaultCreateDateDaysAgoValue.intValue());
                criteriaBuilder.setDateCreatedFrom(mutableDateTime.toDateTime());
            }
            criteria = criteriaBuilder.build();
        }
        return criteria;
    }

    protected DocumentSearchCriteria.Builder createEmptyComparisonCriteria(DocumentSearchCriteria criteria) {
        DocumentSearchCriteria.Builder builder = DocumentSearchCriteria.Builder.create();
        // copy over the fields that shouldn't be considered when determining if the criteria is empty
        builder.setSaveName(criteria.getSaveName());
        builder.setStartAtIndex(criteria.getStartAtIndex());
        builder.setMaxResults(criteria.getMaxResults());
        return builder;
    }

    protected List<RemotableAttributeField> determineSearchFields(DocumentType documentType) {
        List<RemotableAttributeField> searchFields = new ArrayList<RemotableAttributeField>();
        if (documentType != null) {
            DocumentSearchCriteriaConfiguration searchConfiguration =
                    getDocumentLookupCustomizationMediator().getDocumentLookupCriteriaConfiguration(documentType);
            if (searchConfiguration != null) {
                List<AttributeFields> attributeFields = searchConfiguration.getSearchAttributeFields();
                if (attributeFields != null) {
                    for (AttributeFields fields : attributeFields) {
                        searchFields.addAll(fields.getRemotableAttributeFields());
                    }
                }
            }
        }
        return searchFields;
    }

    public DocumentSearchGenerator getStandardDocumentSearchGenerator() {
	String searchGeneratorClass = ConfigContext.getCurrentContextConfig().getProperty(KEWConstants.STANDARD_DOC_SEARCH_GENERATOR_CLASS_CONFIG_PARM);
	if (searchGeneratorClass == null){
	    return new DocumentSearchGeneratorImpl();
	}
    	return (DocumentSearchGenerator)GlobalResourceLoader.getObject(new ObjectDefinition(searchGeneratorClass));
    }

    @Override
    public void validateDocumentSearchCriteria(DocumentSearchGenerator docSearchGenerator, DocumentSearchCriteria.Builder criteria) {
        List<WorkflowServiceError> errors = this.validateWorkflowDocumentSearchCriteria(criteria);
        List<RemotableAttributeError> searchAttributeErrors = docSearchGenerator.validateSearchableAttributes(criteria);
        if (!CollectionUtils.isEmpty(searchAttributeErrors)) {
            // attribute errors are fully materialized error messages, so the only "key" that makes sense is to use "error.custom"
            for (RemotableAttributeError searchAttributeError : searchAttributeErrors) {
                for (String errorMessage : searchAttributeError.getErrors()) {
                    WorkflowServiceError error = new WorkflowServiceErrorImpl(errorMessage, "error.custom", errorMessage);
                    errors.add(error);
                }
            }
        }
        if (!errors.isEmpty() || !GlobalVariables.getMessageMap().hasNoErrors()) {
            throw new WorkflowServiceErrorException("Document Search Validation Errors", errors);
        }
    }

    protected List<WorkflowServiceError> validateWorkflowDocumentSearchCriteria(DocumentSearchCriteria.Builder criteria) {
        List<WorkflowServiceError> errors = new ArrayList<WorkflowServiceError>();

        // trim the principal names, validation isn't really necessary, because if not found, no results will be
        // returned.
        criteria.setApproverPrincipalName(trimCriteriaValue(criteria.getApproverPrincipalName()));
        criteria.setViewerPrincipalName(trimCriteriaValue(criteria.getViewerPrincipalName()));
        criteria.setInitiatorPrincipalName(trimCriteriaValue(criteria.getInitiatorPrincipalName()));
        validateGroupCriteria(criteria, errors);
        criteria.setDocumentId(criteria.getDocumentId());
        return errors;
    }

    private String trimCriteriaValue(String criteriaValue) {
        if (StringUtils.isNotBlank(criteriaValue)) {
            criteriaValue = criteriaValue.trim();
        }
        if (StringUtils.isBlank(criteriaValue)) {
            return null;
        }
        return criteriaValue;
    }

    private void validateGroupCriteria(DocumentSearchCriteria.Builder criteria, List<WorkflowServiceError> errors) {
        if (StringUtils.isNotBlank(criteria.getViewerGroupId())) {
            Group group = KimApiServiceLocator.getGroupService().getGroup(criteria.getViewerGroupId());
            if (group == null) {
                errors.add(new WorkflowServiceErrorImpl("Workgroup Viewer Name is not a workgroup", "docsearch.DocumentSearchService.workgroup.viewer"));
            }
        } else {
            criteria.setViewerGroupId(null);
        }
    }

    @Override
	public List<KeyValue> getNamedSearches(String principalId) {
		List<UserOptions> namedSearches = userOptionsService.findByUserQualified(principalId, NAMED_SEARCH_ORDER_BASE + "%");
		List<KeyValue> sortedNamedSearches = new ArrayList<KeyValue>(0);
		if (!namedSearches.isEmpty()) {
			Collections.sort(namedSearches);
			for (UserOptions namedSearch : namedSearches) {
				KeyValue keyValue = new ConcreteKeyValue(namedSearch.getOptionId(), namedSearch.getOptionId().substring(NAMED_SEARCH_ORDER_BASE.length(), namedSearch.getOptionId().length()));
				sortedNamedSearches.add(keyValue);
			}
		}
		return sortedNamedSearches;
	}

    @Override
	public List<KeyValue> getMostRecentSearches(String principalId) {
		UserOptions order = userOptionsService.findByOptionId(LAST_SEARCH_ORDER_OPTION, principalId);
		List<KeyValue> sortedMostRecentSearches = new ArrayList<KeyValue>();
		if (order != null && order.getOptionVal() != null && !"".equals(order.getOptionVal())) {
			List<UserOptions> mostRecentSearches = userOptionsService.findByUserQualified(principalId, LAST_SEARCH_BASE_NAME + "%");
			String[] ordered = order.getOptionVal().split(",");
            for (String anOrdered : ordered) {
                UserOptions matchingOption = null;
                for (UserOptions option : mostRecentSearches) {
                    if (anOrdered.equals(option.getOptionId())) {
                        matchingOption = option;
                        break;
                    }
                }
                if (matchingOption != null) {
                    DocumentSearchCriteria matchingCriteria = getCriteriaFromSavedSearch(matchingOption);
                	sortedMostRecentSearches.add(new ConcreteKeyValue(anOrdered, getSavedSearchAbbreviatedString(matchingCriteria)));
                }
            }
		}
		return sortedMostRecentSearches;
	}

    public DocumentSearchCriteria clearCriteria(DocumentType documentType, DocumentSearchCriteria criteria) {
        DocumentSearchCriteria clearedCriteria = getDocumentLookupCustomizationMediator().customizeClearCriteria(
                documentType, criteria);
        if (clearedCriteria == null) {
            clearedCriteria = getStandardDocumentSearchGenerator().clearSearch(criteria);
        }
        return clearedCriteria;
    }

    protected String getSavedSearchAbbreviatedString(DocumentSearchCriteria criteria) {
        Map<String, String> abbreviatedStringMap = new LinkedHashMap<String, String>();
        addAbbreviatedString(abbreviatedStringMap, "Doc Type", criteria.getDocumentTypeName());
        addAbbreviatedString(abbreviatedStringMap, "Initiator", criteria.getInitiatorPrincipalName());
        addAbbreviatedString(abbreviatedStringMap, "Doc Id", criteria.getDocumentId());
        addAbbreviatedRangeString(abbreviatedStringMap, "Created", criteria.getDateCreatedFrom(),
                criteria.getDateCreatedTo());
        addAbbreviatedString(abbreviatedStringMap, "Title", criteria.getTitle());
        addAbbreviatedString(abbreviatedStringMap, "App Doc Id", criteria.getApplicationDocumentId());
        addAbbreviatedRangeString(abbreviatedStringMap, "Approved", criteria.getDateApprovedFrom(),
                criteria.getDateApprovedTo());
        addAbbreviatedRangeString(abbreviatedStringMap, "Modified", criteria.getDateLastModifiedFrom(), criteria.getDateLastModifiedTo());
        addAbbreviatedRangeString(abbreviatedStringMap, "Finalized", criteria.getDateFinalizedFrom(), criteria.getDateFinalizedTo());
        addAbbreviatedRangeString(abbreviatedStringMap, "App Doc Status Changed", criteria.getDateApplicationDocumentStatusChangedFrom(), criteria.getDateApplicationDocumentStatusChangedTo());
        addAbbreviatedString(abbreviatedStringMap, "Approver", criteria.getApproverPrincipalName());
        addAbbreviatedString(abbreviatedStringMap, "Viewer", criteria.getViewerPrincipalName());
        addAbbreviatedString(abbreviatedStringMap, "Group Viewer", criteria.getViewerGroupId());
        addAbbreviatedString(abbreviatedStringMap, "Node", criteria.getRouteNodeName());
        addAbbreviatedMultiValuedString(abbreviatedStringMap, "Status", criteria.getDocumentStatuses());
        addAbbreviatedMultiValuedString(abbreviatedStringMap, "Category", criteria.getDocumentStatusCategories());
        for (String documentAttributeName : criteria.getDocumentAttributeValues().keySet()) {
            addAbbreviatedMultiValuedString(abbreviatedStringMap, documentAttributeName, criteria.getDocumentAttributeValues().get(documentAttributeName));
        }
        StringBuilder stringBuilder = new StringBuilder();
        int iteration = 0;
        for (String label : abbreviatedStringMap.keySet()) {
            stringBuilder.append(label).append("=").append(abbreviatedStringMap.get(label));
            if (iteration < abbreviatedStringMap.keySet().size()) {
                stringBuilder.append("; ");
            }
        }
        return stringBuilder.toString();
    }

    protected void addAbbreviatedString(Map<String, String> abbreviatedStringMap, String label, String value) {
        if (StringUtils.isNotBlank(value)) {
            abbreviatedStringMap.put(label, value);
        }
    }

    protected void addAbbreviatedMultiValuedString(Map<String, String> abbreviatedStringMap, String label, Collection<? extends Object> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            List<String> stringValues = new ArrayList<String>();
            for (Object value : values) {
                stringValues.add(value.toString());
            }
            abbreviatedStringMap.put(label, StringUtils.join(stringValues, ","));
        }
    }

    protected void addAbbreviatedRangeString(Map<String, String> abbreviatedStringMap, String label, DateTime dateFrom, DateTime dateTo) {
        if (dateFrom != null || dateTo != null) {
            StringBuilder abbreviatedString = new StringBuilder();
            if (dateFrom != null) {
                abbreviatedString.append(CoreApiServiceLocator.getDateTimeService().toDateString(dateFrom.toDate()));
            }
            abbreviatedString.append("..");
            if (dateTo != null) {
                abbreviatedString.append(CoreApiServiceLocator.getDateTimeService().toDateString(dateTo.toDate()));
            }
            abbreviatedStringMap.put(label, abbreviatedString.toString());
        }
    }

    /**
     * Saves a DocumentSearchCriteria into the UserOptions.  This method operates in one of two ways:
     * 1) The search is named: the criteria is saved under NAMED_SEARCH_ORDER_BASE + <name>
     * 2) The search is unnamed: the criteria is given a name that indicates its order, which is saved in a second user option
     *    which contains a list of these names comprising recent searches
     * @param principalId the user to save the criteria under
     * @param criteria the doc lookup criteria
     */
    private void saveSearch(String principalId, DocumentSearchCriteria criteria) {
        if (StringUtils.isBlank(principalId)) {
            throw new IllegalArgumentException("principalId was null or blank");
        }

        // TODO - Rice 2.0 - need to add support for "advanced" vs. "basic" vs. "super user" searches, this was originally stored with savedSearchString in Rice 1.x

        try {
            String savedSearchString = DocumentLookupInternalUtils.marshalDocumentLookupCriteria(criteria);

            if (StringUtils.isNotBlank(criteria.getSaveName())) {
                userOptionsService.save(principalId, NAMED_SEARCH_ORDER_BASE + criteria.getSaveName(), savedSearchString);
            } else {
                // first determine the current ordering
                UserOptions searchOrder = userOptionsService.findByOptionId(LAST_SEARCH_ORDER_OPTION, principalId);
                // no previous searches, save under first id
                if (searchOrder == null) {
                    userOptionsService.save(principalId, LAST_SEARCH_BASE_NAME + "0", savedSearchString);
                    userOptionsService.save(principalId, LAST_SEARCH_ORDER_OPTION, LAST_SEARCH_BASE_NAME + "0");
                } else {
                    String[] currentOrder = searchOrder.getOptionVal().split(",");
                    // we have reached MAX_SEARCH_ITEMS
                    if (currentOrder.length == MAX_SEARCH_ITEMS) {
                        // move the last item to the front of the list, and save
                        // over this key with the new criteria
                        // [5,4,3,2,1] => [1,5,4,3,2]
                        String searchName = currentOrder[currentOrder.length - 1];
                        String[] newOrder = new String[MAX_SEARCH_ITEMS];
                        newOrder[0] = searchName;
                        for (int i = 0; i < currentOrder.length - 1; i++) {
                            newOrder[i + 1] = currentOrder[i];
                        }
                        // rejoins items with comma separator...
                        String newSearchOrder = "";
                        for (String aNewOrder : newOrder) {
                            if (!"".equals(newSearchOrder)) {
                                newSearchOrder += ",";
                            }
                            newSearchOrder += aNewOrder;
                        }
                        // save the search string under the searchName (which used to be the last name in the list)
                        userOptionsService.save(principalId, searchName, savedSearchString);
                        userOptionsService.save(principalId, LAST_SEARCH_ORDER_OPTION, newSearchOrder);
                    } else {
                        // saves the search to the front of the list with incremented index
                        // [3,2,1] => [4,3,2,1]
                        // here we need to do a push to identify the highest used number which is from the
                        // first one in the array, and then add one to it, and push the rest back one
                        int absMax = 0;
                        for (String aCurrentOrder : currentOrder) {
                            int current = new Integer(aCurrentOrder.substring(LAST_SEARCH_BASE_NAME.length(),
                                    aCurrentOrder.length()));
                            if (current > absMax) {
                                absMax = current;
                            }
                        }
                        String searchName = LAST_SEARCH_BASE_NAME + ++absMax;
                        String[] newOrder = new String[currentOrder.length + 1];
                        newOrder[0] = searchName;
                        for (int i = 0; i < currentOrder.length; i++) {
                            newOrder[i + 1] = currentOrder[i];
                        }
                        String newSearchOrder = "";
                        for (String aNewOrder : newOrder) {
                            if (!"".equals(newSearchOrder)) {
                                newSearchOrder += ",";
                            }
                            newSearchOrder += aNewOrder;
                        }
                        userOptionsService.save(principalId, searchName, savedSearchString);
                        userOptionsService.save(principalId, LAST_SEARCH_ORDER_OPTION, newSearchOrder);
                    }
                }
            }
        } catch (Exception e) {
            // we don't want the failure when saving a search to affect the ability of the document search to succeed
            // and return it's results, so just log and return
            LOG.error("Unable to save search due to exception", e);
        }
    }

	public ConfigurationService getKualiConfigurationService() {
		if (kualiConfigurationService == null) {
			kualiConfigurationService = KRADServiceLocator.getKualiConfigurationService();
		}
		return kualiConfigurationService;
	}

}
