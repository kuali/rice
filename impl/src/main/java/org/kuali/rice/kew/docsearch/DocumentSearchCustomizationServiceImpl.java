package org.kuali.rice.kew.docsearch;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceRemoteServiceConnectionException;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.document.attribute.AttributeFields;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupConfiguration;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.api.extension.ExtensionRepositoryService;
import org.kuali.rice.kew.api.extension.ExtensionUtils;
import org.kuali.rice.kew.framework.document.lookup.DocumentSearchCustomizationService;
import org.kuali.rice.kew.framework.document.lookup.SearchableAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * TODO...
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentSearchCustomizationServiceImpl implements DocumentSearchCustomizationService {

    private static final Logger LOG = Logger.getLogger(DocumentSearchCustomizationServiceImpl.class);

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
                        throw new RiceIllegalArgumentException("Failed to locate a searchable attribute with the given name: " + searchableAttributeName);
                    }
                    SearchableAttribute searchableAttribute = loadSearchableAttribute(extensionDefinition);

                    // TODO temporary, remove once SearchableAttributeOld has been removed from the picture
                    if (searchableAttribute == null) continue;

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
    public boolean isResultProcessingNeeded(String documentTypeName, String resultProcessorAttributeName) {
        ExtensionDefinition extensionDefinition = getExtensionRepositoryService().getExtensionByName(resultProcessorAttributeName);
        if (extensionDefinition == null) {
            throw new RiceIllegalArgumentException("Failed to locate a result processor attribute with the given name: " + resultProcessorAttributeName);
        }
        DocumentSearchResultProcessor resultProcessor = loadResultProcessor(extensionDefinition);
        if (resultProcessor != null) {
            return resultProcessor.isProcessFinalResults();
        }
        return false;
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
                    throw new RiceIllegalArgumentException("Failed to locate a searchable attribute with the given name: " + searchableAttributeName);
                }
                SearchableAttribute searchableAttribute = loadSearchableAttribute(extensionDefinition);

                // TODO temporary, remove once SearchableAttributeOld has been removed from the picture
                if (searchableAttribute == null) continue;

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

    protected SearchableAttribute loadSearchableAttribute(ExtensionDefinition extensionDefinition) {
        Object searchableAttribute = ExtensionUtils.loadExtension(extensionDefinition);
        if (searchableAttribute == null) {
            throw new RiceIllegalArgumentException("Failed to load search attribute for: " + extensionDefinition);
        }
        // TODO temporary, remove once SearchableAttributeOld has been removed from the picture
        if (!(searchableAttribute instanceof SearchableAttribute)) {
            return null;
        }
        return (SearchableAttribute)searchableAttribute;
    }

    protected DocumentSearchResultProcessor loadResultProcessor(ExtensionDefinition extensionDefinition) {
        DocumentSearchResultProcessor resultProcessor = ExtensionUtils.loadExtension(extensionDefinition);
        if (resultProcessor == null) {
            throw new RiceIllegalArgumentException("Failed to load result processor for: " + extensionDefinition);
        }
        return resultProcessor;
    }

    protected ExtensionRepositoryService getExtensionRepositoryService() {
        return extensionRepositoryService;
    }

    public void setExtensionRepositoryService(ExtensionRepositoryService extensionRepositoryService) {
        this.extensionRepositoryService = extensionRepositoryService;
    }

}
