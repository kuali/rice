package org.kuali.rice.kew.docsearch;

import org.aspectj.lang.reflect.FieldSignature;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.document.attribute.AttributeFields;
import org.kuali.rice.kew.doctype.DocumentTypeAttribute;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.framework.KewFrameworkServiceLocator;
import org.kuali.rice.kew.framework.docsearch.DocumentSearchCustomizationService;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reference implementation of {@code DocumentSearchCustomizationMediator}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentSearchCustomizationMediatorImpl implements DocumentSearchCustomizationMediator {

    @Override
    public List<RemotableAttributeField> getSearchFields(DocumentType documentType) {

        List<DocumentTypeAttribute> searchableAttributes = documentType.getSearchableAttributes();

        // This first map is used to partition our attributes by application id.  It maps an application id to the
        // list of searchable attribute names that are associated with that application id.  Note that 'null' is a
        // valid key in this map for those attributes that have no application id.
        LinkedHashMap<String, List<String>> applicationIdToAttributeNameMap = new LinkedHashMap<String, List<String>>();

        // This second map is used to map the searchable attribute name to the List of RemotableAttributeFields
        // that are returned by invocations of it's getSearchFields method.  This is a LinkedHashMap because it
        // preserves the order of the keys as they entered.  This allows us to return attribute fields in the
        // proper order as defined by the order of searchable attributes on the doc type, despite the partitioning
        // of our attributes by application id.
        LinkedHashMap<String, List<RemotableAttributeField>> orderedFieldMap = new LinkedHashMap<String, List<RemotableAttributeField>>();

        for (DocumentTypeAttribute searchableAttribute : searchableAttributes) {
            RuleAttribute ruleAttribute = searchableAttribute.getRuleAttribute();
            String attributeName = ruleAttribute.getName();
            String applicationId = ruleAttribute.getApplicationId();
            if (!applicationIdToAttributeNameMap.containsKey(applicationId)) {
                applicationIdToAttributeNameMap.put(applicationId, new ArrayList<String>());
            }
            applicationIdToAttributeNameMap.get(applicationId).add(attributeName);
            // reserve a spot in the field map
            orderedFieldMap.put(attributeName, null);
        }

        for (String applicationId : applicationIdToAttributeNameMap.keySet()) {
            DocumentSearchCustomizationService documentSearchCustomizationService = loadCustomizationService(applicationId);
            List<String> searchableAttributeNames = applicationIdToAttributeNameMap.get(applicationId);
            List<AttributeFields> attributeFieldsList = documentSearchCustomizationService.getSearchAttributeFields(documentType.getName(), searchableAttributeNames);
            mergeAttributeFields(attributeFieldsList, orderedFieldMap);
        }

        return flattenOrderedFieldMap(orderedFieldMap);
        
    }

    protected DocumentSearchCustomizationService loadCustomizationService(String applicationId) {
        DocumentSearchCustomizationService service = KewFrameworkServiceLocator.getDocumentSearchCustomizationService(applicationId);
        if (service == null) {
            throw new WorkflowRuntimeException("Failed to locate DocumentSearchCustomizationService for applicationId: " + applicationId);
        }
        return service;
    }

    protected void mergeAttributeFields(List<AttributeFields> attributeFieldsList, LinkedHashMap<String, List<RemotableAttributeField>> orderedFieldMap) {
        if (attributeFieldsList == null) {
            return;
        }
        for (AttributeFields attributeFields : attributeFieldsList) {
            orderedFieldMap.put(attributeFields.getAttributeName(), attributeFields.getRemotableAttributeFields());
        }
    }

    protected List<RemotableAttributeField> flattenOrderedFieldMap(LinkedHashMap<String, List<RemotableAttributeField>> orderedFieldMap) {
        List<RemotableAttributeField> flattenedFields = new ArrayList<RemotableAttributeField>();
        for (List<RemotableAttributeField> fields : orderedFieldMap.values()) {
            if (fields != null) {
                flattenedFields.addAll(fields);
            }
        }
        return flattenedFields;
    }

}
