package org.kuali.rice.kew.docsearch;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.api.exception.RiceRemoteServiceConnectionException;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.attribute.AttributeFields;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupConfiguration;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.api.extension.ExtensionRepositoryService;
import org.kuali.rice.kew.api.extension.ExtensionUtils;
import org.kuali.rice.kew.doctype.DocumentTypeAttribute;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupCustomization;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupCustomizationHandlerService;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupCustomizer;
import org.kuali.rice.kew.framework.document.lookup.SearchableAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO...
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentLookupCustomizationHandlerServiceImpl implements DocumentLookupCustomizationHandlerService {

    private static final Logger LOG = Logger.getLogger(DocumentLookupCustomizationHandlerServiceImpl.class);

    private ExtensionRepositoryService extensionRepositoryService;

    @Override
    public DocumentLookupConfiguration getDocumentLookupConfiguration(String documentTypeName, List<String> searchableAttributeNames) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new RiceIllegalArgumentException("documentTypeName was null or blank");
        }
        DocumentLookupConfiguration.Builder configBuilder = DocumentLookupConfiguration.Builder.create(documentTypeName);
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
                    searchAttributeFields.add(AttributeFields.create(searchableAttributeName, attributeSearchFields));
                }
                configBuilder.setSearchAttributeFields(searchAttributeFields);
            } catch (RiceRemoteServiceConnectionException e) {
                LOG.warn("Unable to connect to load searchable attributes for document type: " + documentTypeName, e);
            }
        }

        // TODO - Rice 2.0 - add in the "resultSetFields"...

        return configBuilder.build();
    }

    @Override
    public List<RemotableAttributeError> validateSearchFieldParameters(String documentTypeName, List<String> searchableAttributeNames, Map<String, List<String>> parameters) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new RiceIllegalArgumentException("documentTypeName was null or blank");
        }
        if (CollectionUtils.isEmpty(searchableAttributeNames)) {
            return Collections.emptyList();
        }
        try {
            List<RemotableAttributeError> searchFieldErrors = new ArrayList<RemotableAttributeError>();
            for (String searchableAttributeName : searchableAttributeNames) {
                ExtensionDefinition extensionDefinition = getExtensionRepositoryService().getExtensionByName(searchableAttributeName);
                if (extensionDefinition == null) {
                   throw new RiceIllegalArgumentException("Failed to locate a SearchableAttribute with the given name: " + searchableAttributeName);
                }
                SearchableAttribute searchableAttribute = loadSearchableAttribute(extensionDefinition);
                List<RemotableAttributeError> errors = searchableAttribute.validateSearchFieldParameters(extensionDefinition, parameters, documentTypeName);
                if (!CollectionUtils.isEmpty(errors)) {
                    searchFieldErrors.addAll(errors);
                }
            }
            return Collections.unmodifiableList(searchFieldErrors);
        } catch (RiceRemoteServiceConnectionException e) {
            LOG.warn("Unable to connect to load searchable attributes for document type: " + documentTypeName, e);
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
        DocumentLookupCustomizer customizer = loadCustomizer(customizerName);
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
        DocumentLookupCustomizer customizer = loadCustomizer(customizerName);
        return customizer.customizeClearCriteria(documentLookupCriteria);
    }

    @Override
    public List<RemotableAttributeField> customizeResultSetFields(DocumentLookupCriteria documentLookupCriteria,
            List<RemotableAttributeField> defaultResultSetFields,
            String customizerName) throws RiceIllegalArgumentException {
        if (documentLookupCriteria == null) {
            throw new RiceIllegalArgumentException("documentLookupCriteria was null");
        }
        if (defaultResultSetFields == null) {
            throw new RiceIllegalArgumentException("defaultResultSetFields was null");
        }
        if (StringUtils.isBlank(customizerName)) {
            throw new RiceIllegalArgumentException("customizerName was null or blank");
        }
        DocumentLookupCustomizer customizer = loadCustomizer(customizerName);
        return customizer.customizeResultSetFields(documentLookupCriteria, defaultResultSetFields);
    }

    @Override
    public Set<DocumentLookupCustomization> getEnabledCustomizations(String documentTypeName, String customizerName)
            throws RiceIllegalArgumentException {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new RiceIllegalArgumentException("documentTypeName was null or blank");
        }
        if (StringUtils.isBlank(customizerName)) {
            throw new RiceIllegalArgumentException("customizerName was null or blank");
        }
        DocumentLookupCustomizer customizer = loadCustomizer(documentTypeName);
        Set<DocumentLookupCustomization> customizations = new HashSet<DocumentLookupCustomization>();
        if (customizer.isCustomizeCriteriaEnabled(documentTypeName)) {
            customizations.add(DocumentLookupCustomization.CRITERIA);
        }
        if (customizer.isCustomizeClearCriteriaEnabled(documentTypeName)) {
            customizations.add(DocumentLookupCustomization.CLEAR_CRITERIA);
        }
        if (customizer.isCustomizeResultsEnabled(documentTypeName)) {
            customizations.add(DocumentLookupCustomization.RESULTS);
        }
        if (customizer.isCustomizeResultSetFieldsEnabled(documentTypeName)) {
            customizations.add(DocumentLookupCustomization.RESULT_SET_FIELDS);
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

    private DocumentLookupCustomizer loadCustomizer(String customizerName) {
        ExtensionDefinition extensionDefinition = getExtensionRepositoryService().getExtensionByName(customizerName);
        if (extensionDefinition == null) {
            throw new RiceIllegalArgumentException("Failed to locate a DocumentLookupCustomizer with the given name: " + customizerName);
        }
        DocumentLookupCustomizer customizer = ExtensionUtils.loadExtension(extensionDefinition);
        if (customizer == null) {
            throw new RiceIllegalArgumentException("Failed to load DocumentLookupCustomizer for: " + extensionDefinition);
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
