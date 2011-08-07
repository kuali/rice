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
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.reflect.ObjectDefinition;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SqlBuilder;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocSearchDTO;
import org.kuali.rice.kew.docsearch.DocSearchUtils;
import org.kuali.rice.kew.docsearch.DocumentLookupCustomizationMediator;
import org.kuali.rice.kew.docsearch.DocumentSearchGenerator;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.docsearch.DocumentSearchResultProcessor;
import org.kuali.rice.kew.docsearch.SavedSearchResult;
import org.kuali.rice.kew.docsearch.SearchAttributeCriteriaComponent;
import org.kuali.rice.kew.docsearch.StandardDocumentSearchGenerator;
import org.kuali.rice.kew.docsearch.StandardDocumentSearchResultProcessor;
import org.kuali.rice.kew.docsearch.dao.DocumentSearchDAO;
import org.kuali.rice.kew.docsearch.service.DocumentSearchService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.exception.WorkflowServiceError;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.useroptions.UserOptions;
import org.kuali.rice.kew.useroptions.UserOptionsService;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class DocumentSearchServiceImpl implements DocumentSearchService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentSearchServiceImpl.class);

	private static final int MAX_SEARCH_ITEMS = 5;
	private static final String LAST_SEARCH_ORDER_OPTION = "DocSearch.LastSearch.Order";
	private static final String NAMED_SEARCH_ORDER_BASE = "DocSearch.NamedSearch.";
	private static final String LAST_SEARCH_BASE_NAME = "DocSearch.LastSearch.Holding";
	private static final String DOC_SEARCH_CRITERIA_DTO_CLASS = "org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO";

	private volatile DictionaryValidationService dictionaryValidationService;
	private volatile DataDictionaryService dataDictionaryService;
	private volatile ConfigurationService kualiConfigurationService;
    private DocumentLookupCustomizationMediator documentLookupCustomizationMediator;

	private DocumentSearchDAO docSearchDao;
	private UserOptionsService userOptionsService;

	private SqlBuilder sqlBuilder = null;

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

	public SavedSearchResult getSavedSearchResults(String principalId, String savedSearchName) {
		UserOptions savedSearch = userOptionsService.findByOptionId(savedSearchName, principalId);
		if (savedSearch == null || savedSearch.getOptionId() == null) {
			return null;
		}
		DocSearchCriteriaDTO criteria = getCriteriaFromSavedSearch(savedSearch);
		return new SavedSearchResult(criteria, getList(principalId, criteria));
	}

    public DocumentSearchResultComponents getList(String principalId, DocSearchCriteriaDTO criteria) {
        return getList(principalId, criteria, false);
    }

    public DocumentSearchResultComponents getListRestrictedByCriteria(String principalId, DocSearchCriteriaDTO criteria) {
        return getList(principalId, criteria, true);
    }

	private DocumentSearchResultComponents getList(String principalId, DocSearchCriteriaDTO criteria, boolean useCriteriaRestrictions) {
		DocumentSearchGenerator docSearchGenerator = null;
		DocumentSearchResultProcessor docSearchResultProcessor = null;

		DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(criteria.getDocTypeFullName());
		if (documentType != null ) {
	        docSearchGenerator = documentType.getDocumentSearchGenerator();
	        docSearchResultProcessor = documentType.getDocumentSearchResultProcessor();
		} else {
			docSearchGenerator = getStandardDocumentSearchGenerator();
	        docSearchResultProcessor = getStandardDocumentSearchResultProcessor();
		}
		docSearchGenerator.setSearchingUser(principalId);
		performPreSearchConditions(docSearchGenerator,principalId,criteria);
        validateDocumentSearchCriteria(docSearchGenerator,criteria);
        DocSearchCriteriaDTO customizedCriteria = applyCriteriaCustomizations(documentType, criteria);
        DocumentSearchResultComponents searchResult = null;
        try {
            List<DocSearchDTO> docListResults = null;
            if (useCriteriaRestrictions) {
                docListResults = docSearchDao.getListBoundedByCritera(docSearchGenerator, customizedCriteria, principalId);
            } else {
                docListResults = docSearchDao.getList(docSearchGenerator, customizedCriteria, principalId);
            }
            if (docSearchResultProcessor.isProcessFinalResults()) {
                searchResult = docSearchResultProcessor.processIntoFinalResults(docListResults, customizedCriteria, principalId);
            } else {
                searchResult = new StandardDocumentSearchResultProcessor().processIntoFinalResults(docListResults, customizedCriteria, principalId);
            }

        } catch (Exception e) {
			String errorMsg = "Error received trying to execute search: " + e.getLocalizedMessage();
            throw new WorkflowServiceErrorException(errorMsg, e, new WorkflowServiceErrorImpl(errorMsg,"docsearch.DocumentSearchService.generalError",errorMsg));
		}

        // be sure to save the original criteria that was submitted by the user, not the "customized" criteria
		if (!useCriteriaRestrictions || !criteria.isSaveSearchForUser()) {
            try {
                saveSearch(principalId, criteria);
            } catch (RuntimeException e) {
            	LOG.warn("Unable to save search due to RuntimeException with message: " + e.getMessage());
            	LOG.warn("RuntimeException will be ignored and may cause transaction problems");
                // swallerin it, cuz we look to be read only
    		}
	    }
        return searchResult;
	}

    /**
     * Applies any document type-specific customizations to the lookup criteria.  If no customizations are configured
     * for the document type, this method will simply return the criteria that is passed to it.  If
     * the given DocumentType is null, then this method will also simply return the criteria that is passed to it.
     */
    protected DocSearchCriteriaDTO applyCriteriaCustomizations(DocumentType documentType, DocSearchCriteriaDTO criteria) {
        if (documentType == null) {
            return criteria;
        }
        return getDocumentLookupCustomizationMediator().customizeCriteria(documentType, criteria);
    }

    public DocumentSearchGenerator getStandardDocumentSearchGenerator() {
	String searchGeneratorClass = ConfigContext.getCurrentContextConfig().getProperty(KEWConstants.STANDARD_DOC_SEARCH_GENERATOR_CLASS_CONFIG_PARM);
	if (searchGeneratorClass == null){
	    return new StandardDocumentSearchGenerator();
	}
    	return (DocumentSearchGenerator)GlobalResourceLoader.getObject(new ObjectDefinition(searchGeneratorClass));
    }

    public DocumentSearchResultProcessor getStandardDocumentSearchResultProcessor() {
	String searchGeneratorClass = ConfigContext.getCurrentContextConfig().getProperty(KEWConstants.STANDARD_DOC_SEARCH_RESULT_PROCESSOR_CLASS_CONFIG_PARM);
	if (searchGeneratorClass == null){
	    return new StandardDocumentSearchResultProcessor();
	}
    	return (DocumentSearchResultProcessor)GlobalResourceLoader.getObject(new ObjectDefinition(searchGeneratorClass));
    }

    public void performPreSearchConditions(DocumentSearchGenerator docSearchGenerator,String principalId,DocSearchCriteriaDTO criteria) {
        List<WorkflowServiceError> errors = docSearchGenerator.performPreSearchConditions(principalId,criteria);
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("Document Search Precondition Errors", errors);
        }
    }

    public void validateDocumentSearchCriteria(DocumentSearchGenerator docSearchGenerator,DocSearchCriteriaDTO criteria) {
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

    protected List<WorkflowServiceError> validateWorkflowDocumentSearchCriteria(DocSearchCriteriaDTO criteria) {
        List<WorkflowServiceError> errors = new ArrayList<WorkflowServiceError>();

        // trim the network ids.  Validation isn't really necessary, because if not found, no results will
        // be returned.
        if (!StringUtils.isEmpty(criteria.getApprover())) {
            criteria.setApprover(criteria.getApprover().trim());
        }
        if (!StringUtils.isEmpty(criteria.getViewer())) {
            criteria.setViewer(criteria.getViewer().trim());
        }
        if (!StringUtils.isEmpty(criteria.getInitiator())) {
            criteria.setInitiator(criteria.getInitiator().trim());
        }

        if (! validateWorkgroup(criteria.getWorkgroupViewerId(), criteria.getWorkgroupViewerName())) {
            errors.add(new WorkflowServiceErrorImpl("Workgroup Viewer Name is not a workgroup", "docsearch.DocumentSearchService.workgroup.viewer"));
        } else {
            if (!org.apache.commons.lang.StringUtils.isEmpty(criteria.getWorkgroupViewerName())){
                criteria.setWorkgroupViewerName(criteria.getWorkgroupViewerName().trim());
            }
        }

        if (!validateNumber(criteria.getDocVersion())) {
            errors.add(new WorkflowServiceErrorImpl("Non-numeric document version", "docsearch.DocumentSearchService.docVersion"));
        } else {
            if (criteria.getDocVersion() != null && !"".equals(criteria.getDocVersion().trim())) {
                criteria.setDocVersion(criteria.getDocVersion().trim());
            }
        }

        if (criteria.getDocumentId() != null && !"".equals(criteria.getDocumentId().trim())) {
                criteria.setDocumentId(criteria.getDocumentId().trim());
        }
        
        // validate any dates
        boolean compareDatePairs = true;
        if (!validateDate("fromDateCreated", criteria.getFromDateCreated(), "fromDateCreated")) {
            compareDatePairs = false;
        } else {
            if (criteria.getFromDateCreated() != null && !"".equals(criteria.getFromDateCreated().trim())) {
                criteria.setFromDateCreated(criteria.getFromDateCreated().trim());
            } else {
                compareDatePairs = false;
            }
        }
        if (!validateDate("toDateCreated", criteria.getToDateCreated(), "toDateCreated")) {
            compareDatePairs = false;
        } else {
            if (criteria.getToDateCreated() != null && !"".equals(criteria.getToDateCreated().trim())) {
                criteria.setToDateCreated(criteria.getToDateCreated().trim());
            } else {
                compareDatePairs = false;
            }
        }
        if (compareDatePairs) {
            if (!checkDateRanges(criteria.getFromDateCreated(), criteria.getToDateCreated())) {
            	String[] messageArgs = getDataDictionaryService().getAttributeValidatingErrorMessageParameters(
            			DOC_SEARCH_CRITERIA_DTO_CLASS, "fromDateCreated");
            	errors.add(new WorkflowServiceErrorImpl(MessageFormat.format(getKualiConfigurationService().getPropertyValueAsString(
                        getDataDictionaryService().getAttributeValidatingErrorMessageKey(DOC_SEARCH_CRITERIA_DTO_CLASS,
                                "fromDateCreated") + ".range"), messageArgs[0]), "docsearch.DocumentSearchService.dateCreatedRange"));
            }
        }
        compareDatePairs = true;
        if (!validateDate("fromDateApproved", criteria.getFromDateApproved(), "fromDateApproved")) {
            compareDatePairs = false;
        } else {
            if (criteria.getFromDateApproved() != null && !"".equals(criteria.getFromDateApproved().trim())) {
                criteria.setFromDateApproved(criteria.getFromDateApproved().trim());
            } else {
                compareDatePairs = false;
            }
        }
        if (!validateDate("toDateApproved", criteria.getToDateApproved(), "toDateApproved")) {
            compareDatePairs = false;
        } else {
            if (criteria.getToDateApproved() != null && !"".equals(criteria.getToDateApproved().trim())) {
                criteria.setToDateApproved(criteria.getToDateApproved().trim());
            } else {
                compareDatePairs = false;
            }
        }
        if (compareDatePairs) {
            if (!checkDateRanges(criteria.getFromDateApproved(), criteria.getToDateApproved())) {
            	String[] messageArgs = getDataDictionaryService().getAttributeValidatingErrorMessageParameters(
            			DOC_SEARCH_CRITERIA_DTO_CLASS, "fromDateApproved");
            	errors.add(new WorkflowServiceErrorImpl(MessageFormat.format(getKualiConfigurationService().getPropertyValueAsString(
                        getDataDictionaryService().getAttributeValidatingErrorMessageKey(DOC_SEARCH_CRITERIA_DTO_CLASS,
                                "fromDateApproved") + ".range"), messageArgs[0]), "docsearch.DocumentSearchService.dateApprovedRange"));
            }
        }
        compareDatePairs = true;
        if (!validateDate("fromDateFinalized", criteria.getFromDateFinalized(), "fromDateFinalized")) {
            compareDatePairs = false;
        } else {
            if (criteria.getFromDateFinalized() != null && !"".equals(criteria.getFromDateFinalized().trim())) {
                criteria.setFromDateFinalized(criteria.getFromDateFinalized().trim());
            } else {
                compareDatePairs = false;
            }
        }
        if (!validateDate("toDateFinalized", criteria.getToDateFinalized(), "toDateFinalized")) {
            compareDatePairs = false;
        } else {
            if (criteria.getToDateFinalized() != null && !"".equals(criteria.getToDateFinalized().trim())) {
                criteria.setToDateFinalized(criteria.getToDateFinalized().trim());
            } else {
                compareDatePairs = false;
            }
        }
        if (compareDatePairs) {
            if (!checkDateRanges(criteria.getFromDateFinalized(), criteria.getToDateFinalized())) {
            	String[] messageArgs = getDataDictionaryService().getAttributeValidatingErrorMessageParameters(
            			DOC_SEARCH_CRITERIA_DTO_CLASS, "fromDateFinalized");
            	errors.add(new WorkflowServiceErrorImpl(MessageFormat.format(getKualiConfigurationService().getPropertyValueAsString(
                        getDataDictionaryService().getAttributeValidatingErrorMessageKey(DOC_SEARCH_CRITERIA_DTO_CLASS,
                                "fromDateFinalized") + ".range"), messageArgs[0]), "docsearch.DocumentSearchService.dateFinalizedRange"));
            }
        }
        compareDatePairs = true;
        if (!validateDate("fromDateLastModified", criteria.getFromDateLastModified(), "fromDateLastModified")) {
            compareDatePairs = false;
        } else {
            if (criteria.getFromDateLastModified() != null && !"".equals(criteria.getFromDateLastModified().trim())) {
                criteria.setFromDateLastModified(criteria.getFromDateLastModified().trim());
            } else {
                compareDatePairs = false;
            }
        }
        if (!validateDate("toDateLastModified", criteria.getToDateLastModified(), "toDateLastModified")) {
            compareDatePairs = false;
        } else {
            if (criteria.getToDateLastModified() != null && !"".equals(criteria.getToDateLastModified().trim())) {
                criteria.setToDateLastModified(criteria.getToDateLastModified().trim());
            } else {
                compareDatePairs = false;
            }
        }
        if (compareDatePairs) {
            if (!checkDateRanges(criteria.getFromDateLastModified(), criteria.getToDateLastModified())) {
            	String[] messageArgs = getDataDictionaryService().getAttributeValidatingErrorMessageParameters(
            			DOC_SEARCH_CRITERIA_DTO_CLASS, "fromDateLastModified");
            	errors.add(new WorkflowServiceErrorImpl(MessageFormat.format(getKualiConfigurationService().getPropertyValueAsString(
                        getDataDictionaryService().getAttributeValidatingErrorMessageKey(DOC_SEARCH_CRITERIA_DTO_CLASS,
                                "fromDateLastModified") + ".range"), messageArgs[0]), "docsearch.DocumentSearchService.dateLastModifiedRange"));
            }
        }
        return errors;
    }

    private boolean validateNetworkId(List<String> networkIds){
    	for(String networkId: networkIds){
    		if(!this.validateNetworkId(networkId)){
    			return false;
    		}
    	}
    	return true;
    }
	private boolean validateNetworkId(String networkId) {
		if ((networkId == null) || networkId.trim().equals("")) {
			return true;
		}
		try {
			return KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(networkId.trim()) != null;
		} catch (Exception ex) {
			LOG.debug(ex, ex);
			return false;
		}
	}

	private boolean validatePersonByPrincipalName(String principalName){
        return true;
		/*if(StringUtils.isBlank(principalName)) {
			return true;
		}
		Person person = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(principalName);
		return person != null;*/
	}

	private boolean validateDate(String dateFieldName, String dateFieldValue, String dateFieldErrorKey) {
		// Validates the date format via the dictionary validation service. If validation fails, the validation service adds an error to the message map.
		int oldErrorCount = GlobalVariables.getMessageMap().getErrorCount();
		getDictionaryValidationService().validateAttributeFormat(DOC_SEARCH_CRITERIA_DTO_CLASS, dateFieldName, dateFieldValue,
				KEWConstants.SearchableAttributeConstants.DATA_TYPE_DATE, dateFieldErrorKey);
		return (GlobalVariables.getMessageMap().getErrorCount() <= oldErrorCount);
		//return Utilities.validateDate(date, true);
	}

	private boolean checkDateRanges(String fromDate, String toDate) {
		return Utilities.checkDateRanges(fromDate, toDate);
	}

	private boolean validateNumber(List<String> integers) {
		for(String integer: integers){
    		if(!this.validateNumber(integer)){
    			return false;
    		}
    	}
    	return true;
	}

	private boolean validateNumber(String integer) {
		if ((integer == null) || integer.trim().equals("")) {
			return true;
		}
		return SqlBuilder.isValidNumber(integer);

	}

    private boolean validateWorkgroup(String id, String workgroupName) {
        if (org.apache.commons.lang.StringUtils.isEmpty(workgroupName)) {
            return true;
        }
        Group group = KimApiServiceLocator.getGroupService().getGroup(id);
        return group != null;
    }

	public List<KeyValue> getNamedSearches(String principalId) {
		List<UserOptions> namedSearches = userOptionsService.findByUserQualified(principalId, NAMED_SEARCH_ORDER_BASE + "%");
		List<KeyValue> sortedNamedSearches = new ArrayList<KeyValue>(0);
		if (namedSearches != null && namedSearches.size() > 0) {
			Collections.sort(namedSearches);
			for (UserOptions namedSearch : namedSearches) {
				KeyValue keyValue = new ConcreteKeyValue(namedSearch.getOptionId(), namedSearch.getOptionId().substring(NAMED_SEARCH_ORDER_BASE.length(), namedSearch.getOptionId().length()));
				sortedNamedSearches.add(keyValue);
			}
		}
		return sortedNamedSearches;
	}

	public List<KeyValue> getMostRecentSearches(String principalId) {
		UserOptions order = userOptionsService.findByOptionId(LAST_SEARCH_ORDER_OPTION, principalId);
		List<KeyValue> sortedMostRecentSearches = new ArrayList<KeyValue>();
		if (order != null && order.getOptionVal() != null && !"".equals(order.getOptionVal())) {
			List<UserOptions> mostRecentSearches = userOptionsService.findByUserQualified(principalId, LAST_SEARCH_BASE_NAME + "%");
			String[] ordered = order.getOptionVal().split(",");
            for (String anOrdered : ordered)
            {
                UserOptions matchingOption = null;
                for (UserOptions option : mostRecentSearches)
                {
                    if (anOrdered.equals(option.getOptionId()))
                    {
                        matchingOption = option;
                        break;
                    }
                }
                if (matchingOption != null)
                {
                	sortedMostRecentSearches.add(new ConcreteKeyValue(anOrdered, getCriteriaFromSavedSearch(matchingOption).getDocumentSearchAbbreviatedString()));
                }
            }
		}
		return sortedMostRecentSearches;
	}

	private void saveSearch(String principalId, DocSearchCriteriaDTO criteria) {
		if (StringUtils.isBlank(principalId)) {
			String message = "User given to save search was null.";
			LOG.warn(message);
			throw new IllegalArgumentException(message);
		}
		StringBuffer savedSearchString = new StringBuffer();
		savedSearchString.append(criteria.getAppDocId() == null || "".equals(criteria.getAppDocId()) ? "" : ",,appDocId=" + criteria.getAppDocId());
		savedSearchString.append(criteria.getApprover() == null || "".equals(criteria.getApprover()) ? "" : ",,approver=" + criteria.getApprover());

        if (! org.apache.commons.lang.StringUtils.isEmpty(criteria.getDocRouteNodeId()) && !criteria.getDocRouteNodeId().equals("-1")) {
            RouteNode routeNode = KEWServiceLocator.getRouteNodeService().findRouteNodeById(criteria.getDocRouteNodeId());
            // this block will result in NPE if routeNode is not found; is the intent to preserve the requested criteria? if so, then the following line fixes it
            //savedSearchString.append(",,docRouteNodeId=" + (routeNode != null ? routeNode.getRouteNodeId() : criteria.getDocRouteNodeId()));
            savedSearchString.append(",,docRouteNodeId=");
            savedSearchString.append(routeNode.getRouteNodeId());
            savedSearchString.append(criteria.getDocRouteNodeLogic() == null || "".equals(criteria.getDocRouteNodeLogic()) ? "" : ",,docRouteNodeLogic=" + criteria.getDocRouteNodeLogic());
        }

		savedSearchString.append(criteria.getDocRouteStatus() == null || "".equals(criteria.getDocRouteStatus()) ? "" : ",,docRouteStatus=" + criteria.getDocRouteStatus());
		savedSearchString.append(criteria.getDocTitle() == null || "".equals(criteria.getDocTitle()) ? "" : ",,docTitle=" + criteria.getDocTitle());
		savedSearchString.append(criteria.getDocTypeFullName() == null || "".equals(criteria.getDocTypeFullName()) ? "" : ",,docTypeFullName=" + criteria.getDocTypeFullName());
		savedSearchString.append(criteria.getDocVersion() == null || "".equals(criteria.getDocVersion()) ? "" : ",,docVersion=" + criteria.getDocVersion());
		savedSearchString.append(criteria.getFromDateApproved() == null || "".equals(criteria.getFromDateApproved()) ? "" : ",,fromDateApproved=" + criteria.getFromDateApproved());
		savedSearchString.append(criteria.getFromDateCreated() == null || "".equals(criteria.getFromDateCreated()) ? "" : ",,fromDateCreated=" + criteria.getFromDateCreated());
		savedSearchString.append(criteria.getFromDateFinalized() == null || "".equals(criteria.getFromDateFinalized()) ? "" : ",,fromDateFinalized=" + criteria.getFromDateFinalized());
		savedSearchString.append(criteria.getFromDateLastModified() == null || "".equals(criteria.getFromDateLastModified()) ? "" : ",,fromDateLastModified=" + criteria.getFromDateLastModified());
		savedSearchString.append(criteria.getInitiator() == null || "".equals(criteria.getInitiator()) ? "" : ",,initiator=" + criteria.getInitiator());
		savedSearchString.append(criteria.getOverrideInd() == null || "".equals(criteria.getOverrideInd()) ? "" : ",,overrideInd=" + criteria.getOverrideInd());
		savedSearchString.append(criteria.getDocumentId() == null || "".equals(criteria.getDocumentId()) ? "" : ",,documentId=" + criteria.getDocumentId());
		savedSearchString.append(criteria.getToDateApproved() == null || "".equals(criteria.getToDateApproved()) ? "" : ",,toDateApproved=" + criteria.getToDateApproved());
		savedSearchString.append(criteria.getToDateCreated() == null || "".equals(criteria.getToDateCreated()) ? "" : ",,toDateCreated=" + criteria.getToDateCreated());
		savedSearchString.append(criteria.getToDateFinalized() == null || "".equals(criteria.getToDateFinalized()) ? "" : ",,toDateFinalized=" + criteria.getToDateFinalized());
		savedSearchString.append(criteria.getToDateLastModified() == null || "".equals(criteria.getToDateLastModified()) ? "" : ",,toDateLastModified=" + criteria.getToDateLastModified());
        savedSearchString.append(criteria.getViewer() == null || "".equals(criteria.getViewer()) ? "" : ",,viewer=" + criteria.getViewer());
        savedSearchString.append(criteria.getWorkgroupViewerName() == null || "".equals(criteria.getWorkgroupViewerName()) ? "" : ",,workgroupViewerName=" + criteria.getWorkgroupViewerName());
        savedSearchString.append(criteria.getWorkgroupViewerName() == null || "".equals(criteria.getWorkgroupViewerId()) ? "" : ",,workgroupViewerId=" + criteria.getWorkgroupViewerId());
		savedSearchString.append(criteria.getNamedSearch() == null || "".equals(criteria.getNamedSearch()) ? "" : ",,namedSearch=" + criteria.getNamedSearch());
		savedSearchString.append(criteria.getSearchableAttributes().isEmpty() ? "" : ",,searchableAttributes=" + buildSearchableAttributeString(criteria.getSearchableAttributes()));

		if (savedSearchString.toString() != null && !"".equals(savedSearchString.toString().trim())) {

            savedSearchString.append(criteria.getIsAdvancedSearch() == null || "".equals(criteria.getIsAdvancedSearch()) ? "" : ",,isAdvancedSearch=" + criteria.getIsAdvancedSearch());
            savedSearchString.append(criteria.getSuperUserSearch() == null || "".equals(criteria.getSuperUserSearch()) ? "" : ",,superUserSearch=" + criteria.getSuperUserSearch());

			if (criteria.getNamedSearch() != null && !"".equals(criteria.getNamedSearch().trim())) {
				userOptionsService.save(principalId, NAMED_SEARCH_ORDER_BASE + criteria.getNamedSearch(), savedSearchString.toString());
			} else {
				// first determine the current ordering
				UserOptions searchOrder = userOptionsService.findByOptionId(LAST_SEARCH_ORDER_OPTION, principalId);
				if (searchOrder == null) {
					userOptionsService.save(principalId, LAST_SEARCH_BASE_NAME + "0", savedSearchString.toString());
					userOptionsService.save(principalId, LAST_SEARCH_ORDER_OPTION, LAST_SEARCH_BASE_NAME + "0");
				} else {
					String[] currentOrder = searchOrder.getOptionVal().split(",");
					if (currentOrder.length == MAX_SEARCH_ITEMS) {
						String searchName = currentOrder[currentOrder.length - 1];
						String[] newOrder = new String[MAX_SEARCH_ITEMS];
						newOrder[0] = searchName;
						for (int i = 0; i < currentOrder.length - 1; i++) {
							newOrder[i + 1] = currentOrder[i];
						}
						String newSearchOrder = "";
                        for (String aNewOrder : newOrder)
                        {
                            if (!"".equals(newSearchOrder))
                            {
                                newSearchOrder += ",";
                            }
                            newSearchOrder += aNewOrder;
                        }
						userOptionsService.save(principalId, searchName, savedSearchString.toString());
						userOptionsService.save(principalId, LAST_SEARCH_ORDER_OPTION, newSearchOrder);
					} else {
						// here we need to do a push so identify the highest used number which is from the
						// first one in the array, and then add one to it, and push the rest back one
						int absMax = 0;
                        for (String aCurrentOrder : currentOrder)
                        {
                            int current = new Integer(aCurrentOrder.substring(LAST_SEARCH_BASE_NAME.length(), aCurrentOrder.length()));
                            if (current > absMax)
                            {
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
                        for (String aNewOrder : newOrder)
                        {
                            if (!"".equals(newSearchOrder))
                            {
                                newSearchOrder += ",";
                            }
                            newSearchOrder += aNewOrder;
                        }
						userOptionsService.save(principalId, searchName, savedSearchString.toString());
						userOptionsService.save(principalId, LAST_SEARCH_ORDER_OPTION, newSearchOrder);
					}
				}
			}
		}
	}

	/**
	 * Build String of searchable attributes that can be saved with search criteria
	 *
	 * @param searchableAttributes
	 *            searchable attributes to save
	 * @return String representation of searchable attributes
	 */
	private String buildSearchableAttributeString(List<SearchAttributeCriteriaComponent> searchableAttributes) {
		final StringBuilder searchableAttributeBuffer = new StringBuilder();

		for (SearchAttributeCriteriaComponent component : searchableAttributes) {
			// the following code will remove quickfinder fields
			if ( (component.getFormKey() == null) ||
                 (component.getValue() == null && (CollectionUtils.isEmpty(component.getValues()))) ) {
				continue;
			}

            if (component.getValue() != null) {
				if (searchableAttributeBuffer.length() > 0) {
					searchableAttributeBuffer.append(",");
				}
				searchableAttributeBuffer.append(component.getFormKey());
				searchableAttributeBuffer.append(":");
				searchableAttributeBuffer.append(component.getValue());
            } else if (!CollectionUtils.isEmpty(component.getValues())) {
                for (String value : component.getValues()) {
                    if (searchableAttributeBuffer.length() > 0) {
                        searchableAttributeBuffer.append(",");
                    }
                    searchableAttributeBuffer.append(component.getFormKey());
                    searchableAttributeBuffer.append(":");
                    searchableAttributeBuffer.append(value);
                }
            } else {
                throw new RuntimeException("Error occurred building searchable attribute string trying to find search attribute component value or values");
            }
		}

		return searchableAttributeBuffer.toString();
	}

	/**
	 * Build List of searchable attributes from saved searchable attributes string
	 *
	 * @param searchableAttributeString
	 *            String representation of searchable attributes
	 * @return searchable attributes list
	 */
//	private List buildSearchableAttributesFromString(String searchableAttributeString, String documentTypeName) {
//		List searchableAttributes = new ArrayList();
//		Map criteriaComponentsByKey = new HashMap();
//
//		if (!org.apache.commons.lang.StringUtils.isEmpty(documentTypeName)) {
//			DocumentType docType = ((DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(documentTypeName);
//			if (docType == null) {
//				String errorMsg = "Cannot find document type for given name '" + documentTypeName + "'";
//				LOG.error("buildSearchableAttributesFromString() " + errorMsg);
//				throw new RuntimeException(errorMsg);
//			}
//			for (SearchableAttributeOld searchableAttribute : docType.getSearchableAttributesOld()) {
//				for (Row row : searchableAttribute.getSearchingRows()) {
//					for (Field field : row.getFields()) {
//						SearchableAttributeValue searchableAttributeValue = DocSearchUtils.getSearchableAttributeValueByDataTypeString(field.getFieldDataType());
//						SearchAttributeCriteriaComponent sacc = new SearchAttributeCriteriaComponent(field.getPropertyName(),null,field.getSavablePropertyName(),searchableAttributeValue);
//			        	sacc.setRangeSearch(field.isMemberOfRange());
//			        	sacc.setAllowWildcards(field.isAllowingWildcards());
//			        	sacc.setAutoWildcardBeginning(field.isAutoWildcardAtBeginning());
//			        	sacc.setAutoWildcardEnd(field.isAutoWildcardAtEnding());
//			        	sacc.setCaseSensitive(field.isCaseSensitive());
//			        	sacc.setSearchInclusive(field.isInclusive());
//                        sacc.setSearchable(field.isSearchable());
//                        sacc.setCanHoldMultipleValues(Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType()));
//			        	criteriaComponentsByKey.put(field.getPropertyName(), sacc);
//					}
//				}
//			}
//		}
//
//        Map<String,List<String>> checkForMultiValueSearchableAttributes = new HashMap<String,List<String>>();
//		if ((searchableAttributeString != null) && (searchableAttributeString.trim().length() > 0)) {
//			StringTokenizer tokenizer = new StringTokenizer(searchableAttributeString, ",");
//			while (tokenizer.hasMoreTokens()) {
//				String searchableAttribute = tokenizer.nextToken();
//				int index = searchableAttribute.indexOf(":");
//				if (index != -1) {
//					String key = searchableAttribute.substring(0, index);
////					String savedKey = key;
////					if (key.indexOf(SearchableAttributeOld.RANGE_LOWER_BOUND_PROPERTY_PREFIX) == 0) {
////						savedKey = key.substring(SearchableAttributeOld.RANGE_LOWER_BOUND_PROPERTY_PREFIX.length());
////					} else if (key.indexOf(SearchableAttributeOld.RANGE_UPPER_BOUND_PROPERTY_PREFIX) == 0) {
////						savedKey = key.substring(SearchableAttributeOld.RANGE_UPPER_BOUND_PROPERTY_PREFIX.length());
////					}
//					String value = searchableAttribute.substring(index + 1);
//					SearchAttributeCriteriaComponent critComponent = (SearchAttributeCriteriaComponent) criteriaComponentsByKey.get(key);
//                    if (critComponent == null) {
//                        // here we potentially have a change to the searchable attributes dealing with naming or ranges... so we just ignore the values
//                        continue;
//                    }
//                    if (critComponent.getSearchableAttributeValue() == null) {
//						String errorMsg = "Cannot find SearchableAttributeValue for given key '" + key + "'";
//						LOG.error("buildSearchableAttributesFromString() " + errorMsg);
//						throw new RuntimeException(errorMsg);
//					}
//                    if (critComponent.isCanHoldMultipleValues()) {
//                        // should be multivalue
//                        if (checkForMultiValueSearchableAttributes.containsKey(key)) {
//                            List<String> keyList = checkForMultiValueSearchableAttributes.get(key);
//                            keyList.add(value);
//                            checkForMultiValueSearchableAttributes.put(key, keyList);
//                        } else {
//                            List<String> tempList = new ArrayList<String>();
//                            tempList.add(value);
////                            tempList.addAll(Arrays.asList(new String[]{value}));
//                            checkForMultiValueSearchableAttributes.put(key, tempList);
//                            searchableAttributes.add(critComponent);
//                        }
//                    }
//                    else {
//                        // should be single value
//                        if (checkForMultiValueSearchableAttributes.containsKey(key)) {
//                            // attempting to use multiple values in a field that does not support it
//                            String error = "Attempting to add multiple values to a search attribute (key: '" + key + "') that does not suppor them";
//                            LOG.error("buildSearchableAttributesFromString() " + error);
//                            // we don't blow chunks here in case an attribute has been altered from multi-value to non-multi-value
//                        }
//                        critComponent.setValue(value);
//                        searchableAttributes.add(critComponent);
//                    }
//
//
//				}
//			}
//            for (Iterator iter = searchableAttributes.iterator(); iter.hasNext();) {
//                SearchAttributeCriteriaComponent criteriaComponent = (SearchAttributeCriteriaComponent) iter.next();
//                if (criteriaComponent.isCanHoldMultipleValues()) {
//                    List values =(List)checkForMultiValueSearchableAttributes.get(criteriaComponent.getFormKey());
//                    criteriaComponent.setValue(null);
//                    criteriaComponent.setValues(values);
//                }
//            }
//		}
//
//		return searchableAttributes;
//	}

	/**
	 *
	 * retrieve a document type. This is not a case sensitive search so "TravelRequest" == "Travelrequest"
	 *
	 * @param docTypeName
	 * @return
	 */
   private static DocumentType getValidDocumentType(String docTypeName) {

	   if (org.apache.commons.lang.StringUtils.isEmpty(docTypeName)) {
			return null;
		}
   		DocumentType dTypeCriteria = new DocumentType();
		dTypeCriteria.setName(docTypeName.trim());
		dTypeCriteria.setActive(true);
		Collection<DocumentType> docTypeList = KEWServiceLocator.getDocumentTypeService().find(dTypeCriteria, null, false);

		// Return the first valid doc type.
		DocumentType firstDocumentType = null;
		if(docTypeList != null && !docTypeList.isEmpty()){
			for(DocumentType dType: docTypeList){
			    if (firstDocumentType == null) {
                    firstDocumentType = dType;
                }
                if (StringUtils.equals(docTypeName.toUpperCase(), dType.getName().toUpperCase())) {
                    return dType;
                }
            }
            return firstDocumentType;
		}else{
			throw new RuntimeException("No Valid Document Type Found for document type name '" + docTypeName + "'");
		}
   }

	private DocumentType getValidDocumentTypeOld(String documentTypeFullName) {
		if (org.apache.commons.lang.StringUtils.isEmpty(documentTypeFullName)) {
			return null;
		}
		DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeFullName);
		if (docType == null) {
			throw new RuntimeException("No Valid Document Type Found for document type name '" + documentTypeFullName + "'");
		} else {
			return docType;
		}
	}

	private DocSearchCriteriaDTO getCriteriaFromSavedSearch(UserOptions savedSearch) {
		DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
		if (savedSearch != null) {
			String docTypeFullName = getOptionCriteriaField(savedSearch, "docTypeFullName");
			if (!org.apache.commons.lang.StringUtils.isEmpty(docTypeFullName)) {
				criteria = new DocSearchCriteriaDTO();
			}
			criteria.setDocTypeFullName(getOptionCriteriaField(savedSearch, "docTypeFullName"));
			criteria.setAppDocId(getOptionCriteriaField(savedSearch, "appDocId"));
			criteria.setApprover(getOptionCriteriaField(savedSearch, "approver"));
			criteria.setDocRouteNodeId(getOptionCriteriaField(savedSearch, "docRouteNodeId"));
			if (criteria.getDocRouteNodeId() != null) {
				criteria.setDocRouteNodeLogic(getOptionCriteriaField(savedSearch, "docRouteNodeLogic"));
			}
            criteria.setIsAdvancedSearch(getOptionCriteriaField(savedSearch, "isAdvancedSearch"));
            criteria.setSuperUserSearch(getOptionCriteriaField(savedSearch, "superUserSearch"));
			criteria.setDocRouteStatus(getOptionCriteriaField(savedSearch, "docRouteStatus"));
			criteria.setDocTitle(getOptionCriteriaField(savedSearch, "docTitle"));
			criteria.setDocVersion(getOptionCriteriaField(savedSearch, "docVersion"));
			criteria.setFromDateApproved(getOptionCriteriaField(savedSearch, "fromDateApproved"));
			criteria.setFromDateCreated(getOptionCriteriaField(savedSearch, "fromDateCreated"));
			criteria.setFromDateFinalized(getOptionCriteriaField(savedSearch, "fromDateFinalized"));
			criteria.setFromDateLastModified(getOptionCriteriaField(savedSearch, "fromDateLastModified"));
			criteria.setInitiator(getOptionCriteriaField(savedSearch, "initiator"));
			criteria.setOverrideInd(getOptionCriteriaField(savedSearch, "overrideInd"));
			criteria.setDocumentId(getOptionCriteriaField(savedSearch, "documentId"));
			criteria.setToDateApproved(getOptionCriteriaField(savedSearch, "toDateApproved"));
			criteria.setToDateCreated(getOptionCriteriaField(savedSearch, "toDateCreated"));
			criteria.setToDateFinalized(getOptionCriteriaField(savedSearch, "toDateFinalized"));
			criteria.setToDateLastModified(getOptionCriteriaField(savedSearch, "toDateLastModified"));
			criteria.setViewer(getOptionCriteriaField(savedSearch, "viewer"));
			criteria.setWorkgroupViewerNamespace(getOptionCriteriaField(savedSearch, "workgroupViewerNamespace"));
            criteria.setWorkgroupViewerName(getOptionCriteriaField(savedSearch, "workgroupViewerName"));
			criteria.setNamedSearch(getOptionCriteriaField(savedSearch, "namedSearch"));
			criteria.setSearchableAttributes(DocSearchUtils.buildSearchableAttributesFromString(getOptionCriteriaField(savedSearch, "searchableAttributes"),criteria.getDocTypeFullName()));
		}
		return criteria;
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

	public DictionaryValidationService getDictionaryValidationService() {
		if (dictionaryValidationService == null) {
			dictionaryValidationService = KNSServiceLocator.getDictionaryValidationService();
		}
		return dictionaryValidationService;
	}

	public DataDictionaryService getDataDictionaryService() {
		if (dataDictionaryService == null) {
			dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
		}
		return dataDictionaryService;
	}

	public ConfigurationService getKualiConfigurationService() {
		if (kualiConfigurationService == null) {
			kualiConfigurationService = KRADServiceLocator.getKualiConfigurationService();
		}
		return kualiConfigurationService;
	}

	private List<String> tokenizeCriteria(String input){
		List<String> lRet = null;

		lRet = Arrays.asList(input.split("\\|"));

		return lRet;
	}

	/**
	 * @return the sqlBuilder
	 */
	public SqlBuilder getSqlBuilder() {
		if(sqlBuilder == null){
			sqlBuilder = new SqlBuilder();
			sqlBuilder.setDbPlatform((DatabasePlatform) GlobalResourceLoader.getService(RiceConstants.DB_PLATFORM));
			sqlBuilder.setDateTimeService(CoreApiServiceLocator.getDateTimeService());
		}
		return this.sqlBuilder;
	}

	/**
	 * @param sqlBuilder the sqlBuilder to set
	 */
	public void setSqlBuilder(SqlBuilder sqlBuilder) {
		this.sqlBuilder = sqlBuilder;
	}
}
