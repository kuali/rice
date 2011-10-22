package org.kuali.rice.kew.docsearch;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceRemoteServiceConnectionException;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResult;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.api.extension.ExtensionRepositoryService;
import org.kuali.rice.kew.api.extension.ExtensionUtils;
import org.kuali.rice.kew.framework.document.attribute.SearchableAttribute;
import org.kuali.rice.kew.framework.document.search.AttributeFields;
import org.kuali.rice.kew.framework.document.search.DocumentSearchCriteriaConfiguration;
import org.kuali.rice.kew.framework.document.search.DocumentSearchCustomization;
import org.kuali.rice.kew.framework.document.search.DocumentSearchCustomizationHandlerService;
import org.kuali.rice.kew.framework.document.search.DocumentSearchCustomizer;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultSetConfiguration;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO...
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentSearchCustomizationHandlerServiceImpl implements DocumentSearchCustomizationHandlerService {

    private static final Logger LOG = Logger.getLogger(DocumentSearchCustomizationHandlerServiceImpl.class);

    private ExtensionRepositoryService extensionRepositoryService;

    @Override
    public DocumentSearchCriteriaConfiguration getDocumentLookupConfiguration(String documentTypeName, List<String> searchableAttributeNames) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new RiceIllegalArgumentException("documentTypeName was null or blank");
        }

        if (searchableAttributeNames == null) {
            throw new RiceIllegalArgumentException("searchableAttributeNames was null");
        }
        DocumentSearchCriteriaConfiguration.Builder configBuilder = DocumentSearchCriteriaConfiguration.Builder.create();
        if (CollectionUtils.isNotEmpty(searchableAttributeNames)) {
            try {
                List<AttributeFields> searchAttributeFields = new ArrayList<AttributeFields>();
                for (String searchableAttributeName : searchableAttributeNames) {
                    ExtensionDefinition extensionDefinition = getExtensionRepositoryService().getExtensionByName(searchableAttributeName);
                    if (extensionDefinition == null) {
                       throw new RiceIllegalArgumentException("Failed to locate a SearchableAttribute with the given name: " + searchableAttributeName);
                    }
                    SearchableAttribute searchableAttribute = loadSearchableAttribute(extensionDefinition);
                    List<RemotableAttributeField> attributeSearchFields = searchableAttribute.getSearchFields(extensionDefinition, documentTypeName);
                    if (CollectionUtils.isNotEmpty(attributeSearchFields)) {
                        searchAttributeFields.add(AttributeFields.create(searchableAttributeName, attributeSearchFields));
                    }
                }
                configBuilder.setSearchAttributeFields(searchAttributeFields);
            } catch (RiceRemoteServiceConnectionException e) {
                LOG.warn("Unable to connect to load searchable attributes for document type: " + documentTypeName, e);
            }
        }
        return configBuilder.build();
    }

    @Override
    public List<RemotableAttributeError> validateCriteria(DocumentLookupCriteria documentLookupCriteria,
            List<String> searchableAttributeNames) {
        if (documentLookupCriteria == null) {
            throw new RiceIllegalArgumentException("documentLookupCriteria was null or blank");
        }
        if (searchableAttributeNames == null) {
            throw new RiceIllegalArgumentException("searchableAttributeNames was null");
        }
        try {
            List<RemotableAttributeError> searchFieldErrors = new ArrayList<RemotableAttributeError>();
            for (String searchableAttributeName : searchableAttributeNames) {
                ExtensionDefinition extensionDefinition = getExtensionRepositoryService().getExtensionByName(searchableAttributeName);
                if (extensionDefinition == null) {
                   throw new RiceIllegalArgumentException("Failed to locate a SearchableAttribute with the given name: " + searchableAttributeName);
                }
                SearchableAttribute searchableAttribute = loadSearchableAttribute(extensionDefinition);
                List<RemotableAttributeError> errors = searchableAttribute.validateDocumentAttributeCriteria(extensionDefinition, documentLookupCriteria);
                if (!CollectionUtils.isEmpty(errors)) {
                    searchFieldErrors.addAll(errors);
                }
            }
            return Collections.unmodifiableList(searchFieldErrors);
        } catch (RiceRemoteServiceConnectionException e) {
            LOG.warn("Unable to connect to load searchable attributes for criteria: " + documentLookupCriteria, e);
            return Collections.emptyList();
        }
    }

    @Override
    public DocumentLookupCriteria customizeCriteria(DocumentLookupCriteria documentLookupCriteria, String customizerName) throws RiceIllegalArgumentException {
        if (documentLookupCriteria == null) {
            throw new RiceIllegalArgumentException("documentLookupCriteria was null");
        }
        if (StringUtils.isBlank(customizerName)) {
            throw new RiceIllegalArgumentException("customizerName was null or blank");
        }
        DocumentSearchCustomizer customizer = loadCustomizer(customizerName);
        return customizer.customizeCriteria(documentLookupCriteria);
    }

    @Override
    public DocumentLookupCriteria customizeClearCriteria(DocumentLookupCriteria documentLookupCriteria, String customizerName)
            throws RiceIllegalArgumentException {
        if (documentLookupCriteria == null) {
            throw new RiceIllegalArgumentException("documentLookupCriteria was null");
        }
        if (StringUtils.isBlank(customizerName)) {
            throw new RiceIllegalArgumentException("customizerName was null or blank");
        }
        DocumentSearchCustomizer customizer = loadCustomizer(customizerName);
        return customizer.customizeClearCriteria(documentLookupCriteria);
    }

    @Override
    public DocumentSearchResultValues customizeResults(DocumentLookupCriteria documentLookupCriteria,
            List<DocumentLookupResult> defaultResults,
            String customizerName) throws RiceIllegalArgumentException {
        if (documentLookupCriteria == null) {
            throw new RiceIllegalArgumentException("documentLookupCriteria was null");
        }
        if (defaultResults == null) {
            throw new RiceIllegalArgumentException("defaultResults was null");
        }
        if (StringUtils.isBlank(customizerName)) {
            throw new RiceIllegalArgumentException("customizerName was null or blank");
        }
        DocumentSearchCustomizer customizer = loadCustomizer(customizerName);
        return customizer.customizeResults(documentLookupCriteria, defaultResults);
    }

    @Override
    public DocumentSearchResultSetConfiguration customizeResultSetConfiguration(
            DocumentLookupCriteria documentLookupCriteria, String customizerName) throws RiceIllegalArgumentException {
        if (documentLookupCriteria == null) {
            throw new RiceIllegalArgumentException("documentLookupCriteria was null");
        }
        if (StringUtils.isBlank(customizerName)) {
            throw new RiceIllegalArgumentException("customizerName was null or blank");
        }
        DocumentSearchCustomizer customizer = loadCustomizer(customizerName);
        return customizer.customizeResultSetConfiguration(documentLookupCriteria);
    }

    @Override
    public Set<DocumentSearchCustomization> getEnabledCustomizations(String documentTypeName, String customizerName)
            throws RiceIllegalArgumentException {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new RiceIllegalArgumentException("documentTypeName was null or blank");
        }
        if (StringUtils.isBlank(customizerName)) {
            throw new RiceIllegalArgumentException("customizerName was null or blank");
        }
        DocumentSearchCustomizer customizer = loadCustomizer(documentTypeName);
        Set<DocumentSearchCustomization> customizations = new HashSet<DocumentSearchCustomization>();
        if (customizer.isCustomizeCriteriaEnabled(documentTypeName)) {
            customizations.add(DocumentSearchCustomization.CRITERIA);
        }
        if (customizer.isCustomizeClearCriteriaEnabled(documentTypeName)) {
            customizations.add(DocumentSearchCustomization.CLEAR_CRITERIA);
        }
        if (customizer.isCustomizeResultsEnabled(documentTypeName)) {
            customizations.add(DocumentSearchCustomization.RESULTS);
        }
        if (customizer.isCustomizeResultSetFieldsEnabled(documentTypeName)) {
            customizations.add(DocumentSearchCustomization.RESULT_SET_FIELDS);
        }
        return Collections.unmodifiableSet(customizations);
    }

    private SearchableAttribute loadSearchableAttribute(ExtensionDefinition extensionDefinition) {
        Object searchableAttribute = ExtensionUtils.loadExtension(extensionDefinition);
        if (searchableAttribute == null) {
            throw new RiceIllegalArgumentException("Failed to load SearchableAttribute for: " + extensionDefinition);
        }
        return (SearchableAttribute)searchableAttribute;
    }

    private DocumentSearchCustomizer loadCustomizer(String customizerName) {
        ExtensionDefinition extensionDefinition = getExtensionRepositoryService().getExtensionByName(customizerName);
        if (extensionDefinition == null) {
            throw new RiceIllegalArgumentException("Failed to locate a DocumentSearchCustomizer with the given name: " + customizerName);
        }
        DocumentSearchCustomizer customizer = ExtensionUtils.loadExtension(extensionDefinition);
        if (customizer == null) {
            throw new RiceIllegalArgumentException("Failed to load DocumentSearchCustomizer for: " + extensionDefinition);
        }
        return customizer;
    }

    protected ExtensionRepositoryService getExtensionRepositoryService() {
        return extensionRepositoryService;
    }

    public void setExtensionRepositoryService(ExtensionRepositoryService extensionRepositoryService) {
        this.extensionRepositoryService = extensionRepositoryService;
    }

}
