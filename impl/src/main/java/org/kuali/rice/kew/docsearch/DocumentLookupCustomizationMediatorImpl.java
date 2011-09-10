package org.kuali.rice.kew.docsearch;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.document.attribute.AttributeFields;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupCriteriaConfiguration;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupCriteria;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupResultSetConfiguration;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupResultValues;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupResults;
import org.kuali.rice.kew.doctype.DocumentTypeAttribute;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.framework.KewFrameworkServiceLocator;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupCustomization;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupCustomizationHandlerService;
import org.kuali.rice.kew.rule.bo.RuleAttribute;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reference implementation of {@code DocumentSearchCustomizationMediator}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentLookupCustomizationMediatorImpl implements DocumentLookupCustomizationMediator {

    @Override
    public DocumentLookupCriteriaConfiguration getDocumentLookupCriteriaConfiguration(DocumentType documentType) {

        List<DocumentTypeAttribute> searchableAttributes = documentType.getSearchableAttributes();

        // This first map is used to partition our attributes by application id.  It maps an application id to the
        // list of searchable attribute names that are associated with that application id.  Note that 'null' is a
        // valid key in this map for those attributes that have no application id.
        LinkedHashMap<String, List<String>> applicationIdToAttributeNameMap = new LinkedHashMap<String, List<String>>();

        // This second map is used to map the searchable attribute name to the List of RemotableAttributeFields
        // that are returned by invocations of it's getSearchFields method.  This is a LinkedHashMap because it
        // preserves the order of the keys as they are entered.  This allows us to return attribute fields in the
        // proper order as defined by the order of searchable attributes on the doc type, despite the partitioning
        // of our attributes by application id.
        LinkedHashMap<String, AttributeFields> orderedSearchFieldMap = new LinkedHashMap<String, AttributeFields>();
        LinkedHashMap<String, AttributeFields> orderedResultSetFieldMap = new LinkedHashMap<String, AttributeFields>();

        for (DocumentTypeAttribute searchableAttribute : searchableAttributes) {
            RuleAttribute ruleAttribute = searchableAttribute.getRuleAttribute();
            String attributeName = ruleAttribute.getName();
            String applicationId = ruleAttribute.getApplicationId();
            if (!applicationIdToAttributeNameMap.containsKey(applicationId)) {
                applicationIdToAttributeNameMap.put(applicationId, new ArrayList<String>());
            }
            applicationIdToAttributeNameMap.get(applicationId).add(attributeName);
            // reserve a spot in the field map
            orderedSearchFieldMap.put(attributeName, null);
        }

        for (String applicationId : applicationIdToAttributeNameMap.keySet()) {
            DocumentLookupCustomizationHandlerService documentSearchCustomizationService = loadCustomizationService(
                    applicationId);
            List<String> searchableAttributeNames = applicationIdToAttributeNameMap.get(applicationId);
            DocumentLookupCriteriaConfiguration documentLookupConfiguration = documentSearchCustomizationService.getDocumentLookupConfiguration(
                    documentType.getName(), searchableAttributeNames);
            mergeAttributeFields(documentLookupConfiguration.getSearchAttributeFields(), orderedSearchFieldMap);
        }

        DocumentLookupCriteriaConfiguration.Builder configBuilder = DocumentLookupCriteriaConfiguration.Builder.create();
        configBuilder.setSearchAttributeFields(flattenOrderedFieldMap(orderedSearchFieldMap));
        return configBuilder.build();
    }

    @Override
    public List<RemotableAttributeError> validateLookupFieldParameters(DocumentType documentType,
            Map<String, List<String>> parameters) {

        List<DocumentTypeAttribute> searchableAttributes = documentType.getSearchableAttributes();
        LinkedHashMap<String, List<String>> applicationIdToAttributeNameMap = new LinkedHashMap<String, List<String>>();

        for (DocumentTypeAttribute searchableAttribute : searchableAttributes) {
            RuleAttribute ruleAttribute = searchableAttribute.getRuleAttribute();
            String attributeName = ruleAttribute.getName();
            String applicationId = ruleAttribute.getApplicationId();
            if (!applicationIdToAttributeNameMap.containsKey(applicationId)) {
                applicationIdToAttributeNameMap.put(applicationId, new ArrayList<String>());
            }
            applicationIdToAttributeNameMap.get(applicationId).add(attributeName);
        }

        List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
        for (String applicationId : applicationIdToAttributeNameMap.keySet()) {
            DocumentLookupCustomizationHandlerService documentSearchCustomizationService = loadCustomizationService(applicationId);
            List<String> searchableAttributeNames = applicationIdToAttributeNameMap.get(applicationId);
            List<RemotableAttributeError> searchErrors = documentSearchCustomizationService.validateSearchFieldParameters(documentType.getName(), searchableAttributeNames, parameters);
            if (!CollectionUtils.isEmpty(searchErrors)) {
                errors.addAll(searchErrors);
            }
        }

        return errors;
    }

    @Override
    public DocumentLookupCriteria customizeCriteria(DocumentType documentType, DocumentLookupCriteria documentLookupCriteria) {
        DocumentTypeAttribute customizerAttribute = documentType.getCustomizerAttribute();
        if (customizerAttribute != null) {
            DocumentLookupCustomizationHandlerService service = loadCustomizationService(customizerAttribute.getRuleAttribute().getApplicationId());
            if (service.getEnabledCustomizations(documentType.getName(), customizerAttribute.getRuleAttribute().getName()).contains(DocumentLookupCustomization.CRITERIA)) {
                DocumentLookupCriteria customizedCriteria = service.customizeCriteria(documentLookupCriteria, customizerAttribute.getRuleAttribute().getName());
                if (customizedCriteria != null) {
                    return customizedCriteria;
                }
            }
        }
        return null;
    }

    @Override
    public DocumentLookupCriteria customizeClearCriteria(DocumentType documentType, DocumentLookupCriteria documentLookupCriteria) {
        DocumentTypeAttribute customizerAttribute = documentType.getCustomizerAttribute();
        if (customizerAttribute != null) {
            DocumentLookupCustomizationHandlerService service = loadCustomizationService(customizerAttribute.getRuleAttribute().getApplicationId());
            if (service.getEnabledCustomizations(documentType.getName(), customizerAttribute.getRuleAttribute().getName()).contains(DocumentLookupCustomization.CLEAR_CRITERIA)) {
                DocumentLookupCriteria customizedCriteria = service.customizeClearCriteria(documentLookupCriteria, customizerAttribute.getRuleAttribute().getName());
                if (customizedCriteria != null) {
                    return customizedCriteria;
                }
            }
        }
        return documentLookupCriteria;
    }

    @Override
    public DocumentLookupResultValues customizeResults(DocumentType documentType,
            DocumentLookupCriteria documentLookupCriteria, DocumentLookupResults results) {
        DocumentTypeAttribute customizerAttribute = documentType.getCustomizerAttribute();
        if (customizerAttribute != null) {
            DocumentLookupCustomizationHandlerService service = loadCustomizationService(customizerAttribute.getRuleAttribute().getApplicationId());
            if (service.getEnabledCustomizations(documentType.getName(), customizerAttribute.getRuleAttribute().getName()).contains(DocumentLookupCustomization.RESULTS)) {
                DocumentLookupResultValues customizedResults = service.customizeResults(documentLookupCriteria, results.getLookupResults(), customizerAttribute.getRuleAttribute().getName());
                if (customizedResults != null) {
                    return customizedResults;
                }
            }
        }
        return null;
    }

    @Override
    public DocumentLookupResultSetConfiguration customizeResultSetConfiguration(DocumentType documentType,
            DocumentLookupCriteria documentLookupCriteria) {
        DocumentTypeAttribute customizerAttribute = documentType.getCustomizerAttribute();
        if (customizerAttribute != null) {
            DocumentLookupCustomizationHandlerService service = loadCustomizationService(customizerAttribute.getRuleAttribute().getApplicationId());
            if (service.getEnabledCustomizations(documentType.getName(), customizerAttribute.getRuleAttribute().getName()).contains(DocumentLookupCustomization.RESULT_SET_FIELDS)) {
                DocumentLookupResultSetConfiguration resultSetConfiguration = service.customizeResultSetConfiguration(
                        documentLookupCriteria, customizerAttribute.getRuleAttribute().getName());
                if (resultSetConfiguration != null) {
                    return resultSetConfiguration;
                }
            }
        }
        return null;
    }

    protected DocumentLookupCustomizationHandlerService loadCustomizationService(String applicationId) {
        DocumentLookupCustomizationHandlerService service = KewFrameworkServiceLocator.getDocumentLookupCustomizationHandlerService(
                applicationId);
        if (service == null) {
            throw new WorkflowRuntimeException("Failed to locate DocumentSearchCustomizationService for applicationId: " + applicationId);
        }
        return service;
    }

    protected void mergeAttributeFields(List<AttributeFields> attributeFieldsList, LinkedHashMap<String, AttributeFields> orderedFieldMap) {
        if (attributeFieldsList == null) {
            return;
        }
        for (AttributeFields attributeFields : attributeFieldsList) {
            orderedFieldMap.put(attributeFields.getAttributeName(), attributeFields);
        }
    }

    protected List<AttributeFields> flattenOrderedFieldMap(LinkedHashMap<String, AttributeFields> orderedFieldMap) {
        return new ArrayList<AttributeFields>(orderedFieldMap.values());
    }

}
