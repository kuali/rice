package org.kuali.rice.kew.impl.extension;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.api.extension.ExtensionRepositoryService;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.service.RuleAttributeService;

import java.util.HashMap;
import java.util.Map;

/**
 * Reference implementation of the {@code ExtensionRepositoryService}.  This implementation
 * essentially sits on top of the legacy "RuleAttribute" service.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ExtensionRepositoryServiceImpl implements ExtensionRepositoryService {

    private RuleAttributeService ruleAttributeService;

    @Override
    public ExtensionDefinition getExtensionById(String id) throws RiceIllegalArgumentException {
        if (StringUtils.isBlank(id)) {
            throw new RiceIllegalArgumentException("id was null or blank");
        }
        RuleAttribute ruleAttribute = ruleAttributeService.findByRuleAttributeId(id);
        return translateFromRuleAttribute(ruleAttribute);
    }

    @Override
    public ExtensionDefinition getExtensionByName(String name) throws RiceIllegalArgumentException {
        if (StringUtils.isBlank(name)) {
            throw new RiceIllegalArgumentException("name was null or blank");
        }
        RuleAttribute ruleAttribute = ruleAttributeService.findByName(name);
        return translateFromRuleAttribute(ruleAttribute);
    }

    private ExtensionDefinition translateFromRuleAttribute(RuleAttribute ruleAttribute) {
        if (ruleAttribute == null) {
            return null;
        }
        ExtensionDefinition.Builder builder = ExtensionDefinition.Builder.create(ruleAttribute.getName(), ruleAttribute.getType(), ruleAttribute.getClassName());
        builder.setApplicationId(ruleAttribute.getApplicationId());
        builder.setDescription(ruleAttribute.getDescription());
        builder.setId(ruleAttribute.getRuleAttributeId());
        builder.setLabel(ruleAttribute.getLabel());
        builder.setVersionNumber(ruleAttribute.getVersionNumber());
        Map<String, String> configuration = new HashMap<String, String>();
        if (StringUtils.isNotBlank(ruleAttribute.getXmlConfigData())) {
            configuration.put(RuleAttribute.XML_CONFIG_DATA, ruleAttribute.getXmlConfigData());
        }
        builder.setConfiguration(configuration);
        return builder.build();
    }

    public void setRuleAttributeService(RuleAttributeService ruleAttributeService) {
        this.ruleAttributeService = ruleAttributeService;
    }

}
