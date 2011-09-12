package org.kuali.rice.kew.impl.extension;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.api.extension.ExtensionRepositoryService;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.service.RuleAttributeService;

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
        return RuleAttribute.to(ruleAttribute);
    }

    public void setRuleAttributeService(RuleAttributeService ruleAttributeService) {
        this.ruleAttributeService = ruleAttributeService;
    }

}
