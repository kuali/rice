package org.kuali.rice.kew.docsearch;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceRemoteServiceConnectionException;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.document.attribute.AttributeFields;
import org.kuali.rice.kew.docsearch.xml.GenericXMLSearchableAttribute;
import org.kuali.rice.kew.framework.docsearch.DocumentSearchCustomizationService;
import org.kuali.rice.kew.framework.docsearch.SearchableAttribute;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.service.RuleAttributeService;
import org.kuali.rice.kew.util.KEWConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO...
 * 
 */
public class DocumentSearchCustomizationServiceImpl implements DocumentSearchCustomizationService {

    private static final Logger LOG = Logger.getLogger(DocumentSearchCustomizationServiceImpl.class);

    private RuleAttributeService ruleAttributeService;

    @Override
    public List<AttributeFields> getSearchAttributeFields(String documentTypeName, List<String> searchableAttributeNames) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new RiceIllegalArgumentException("documentTypeName was null or blank");
        }
        if (searchableAttributeNames == null || searchableAttributeNames.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            List<AttributeFields> searchAttributeFields = new ArrayList<AttributeFields>();
            for (String searchableAttributeName : searchableAttributeNames) {
                RuleAttribute ruleAttribute = getRuleAttributeService().findByName(searchableAttributeName);
                if (ruleAttribute == null) {
                    throw new RiceIllegalArgumentException("Failed to locate a searchable attribute with the given name: " + searchableAttributeName);
                }
                SearchableAttribute searchableAttribute = loadSearchableAttribute(ruleAttribute);
                List<RemotableAttributeField> attributeSearchFields = searchableAttribute.getSearchFields(documentTypeName);
                searchAttributeFields.add(AttributeFields.create(searchableAttributeName, attributeSearchFields));
            }
            return Collections.unmodifiableList(searchAttributeFields);
        } catch (RiceRemoteServiceConnectionException e) {
            LOG.warn("Unable to connect to load searchable attributes for document type: " + documentTypeName, e);
            return Collections.emptyList();
        }
    }

    protected SearchableAttribute loadSearchableAttribute(RuleAttribute ruleAttribute) {
        Object ruleAttributeService = getRuleAttributeService().loadRuleAttributeService(ruleAttribute);
        if (ruleAttributeService == null) {
            throw new RiceIllegalArgumentException("Failed to load search attribute for: " + ruleAttribute);
        }
        if (KEWConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType())) {
            //required to make it work because ruleAttribute XML is required to construct fields
            ((GenericXMLSearchableAttribute) ruleAttributeService).setRuleAttribute(ruleAttribute);
        }
        return (SearchableAttribute)ruleAttributeService;
    }

    protected RuleAttributeService getRuleAttributeService() {
        return ruleAttributeService;
    }

    public void setRuleAttributeService(RuleAttributeService ruleAttributeService) {
        this.ruleAttributeService = ruleAttributeService;
    }

}
