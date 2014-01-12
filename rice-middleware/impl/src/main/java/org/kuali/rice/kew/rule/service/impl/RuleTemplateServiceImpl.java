/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kew.rule.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jdom.Element;
import org.kuali.rice.core.api.impex.ExportDataSet;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegationBo;
import org.kuali.rice.kew.rule.RuleTemplateOptionBo;
import org.kuali.rice.kew.rule.bo.RuleTemplateBo;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttributeBo;
import org.kuali.rice.kew.rule.dao.RuleDAO;
import org.kuali.rice.kew.rule.dao.RuleDelegationDAO;
import org.kuali.rice.kew.rule.dao.RuleTemplateAttributeDAO;
import org.kuali.rice.kew.rule.dao.RuleTemplateDAO;
import org.kuali.rice.kew.rule.dao.RuleTemplateOptionDAO;
import org.kuali.rice.kew.rule.service.RuleAttributeService;
import org.kuali.rice.kew.rule.service.RuleTemplateService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.xml.RuleTemplateXmlParser;
import org.kuali.rice.kew.xml.export.RuleTemplateXmlExporter;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Required;

public class RuleTemplateServiceImpl implements RuleTemplateService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RuleTemplateServiceImpl.class);

    private static final String RULE_TEMPLATE_NAME_REQUIRED = "rule.template.name.required";

    private static final String RULE_TEMPLATE_DESCRIPTION_REQUIRED = "rule.template.description.required";

    private static final String XML_PARSE_ERROR = "general.error.parsexml";

    private RuleTemplateDAO ruleTemplateDAO;

    private RuleDAO ruleDAO;

    private RuleDelegationDAO ruleDelegationDAO;

    private DataObjectService dataObjectService;


    public void deleteRuleTemplateOption(String ruleTemplateOptionId) {
        RuleTemplateOptionBo ruleTemplateOptionBo = getDataObjectService().find(
                RuleTemplateOptionBo.class,ruleTemplateOptionId);
        getDataObjectService().delete(ruleTemplateOptionBo);
    }

    public RuleTemplateBo findByRuleTemplateName(String ruleTemplateName) {
        return (getRuleTemplateDAO().findByRuleTemplateName(ruleTemplateName));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kuali.rice.kew.rule.RuleTemplateAttributeService#findByRuleTemplateAttributeId(java.lang.Long)
     */
    public RuleTemplateAttributeBo findByRuleTemplateAttributeId(String ruleTemplateAttributeId) {
        return getDataObjectService().find(RuleTemplateAttributeBo.class,ruleTemplateAttributeId);
    }

    public List<RuleTemplateBo> findAll() {
        return ruleTemplateDAO.findAll();
    }

    public List findByRuleTemplate(RuleTemplateBo ruleTemplate) {
        return ruleTemplateDAO.findByRuleTemplate(ruleTemplate);
    }

    public RuleTemplateBo save(RuleTemplateBo ruleTemplate) {
        LOG.debug("save RuleTemplateServiceImpl");
        validate(ruleTemplate);
        fixAssociations(ruleTemplate);


        LOG.debug("end save RuleTemplateServiceImpl");
        return getRuleTemplateDAO().save(ruleTemplate);
    }

    public void save(RuleTemplateAttributeBo ruleTemplateAttribute) {
        getDataObjectService().save(ruleTemplateAttribute);
    }

    /**
     * Saves the given RuleDelegation and RuleBaseValues as the defaults for this RuleTemplate
     */
    public void saveRuleDefaults(RuleDelegationBo ruleDelegation, RuleBaseValues ruleBaseValues) {
        KEWServiceLocator.getRuleService().saveRule(ruleBaseValues, false);
        if (ruleDelegation != null) {
        	KEWServiceLocator.getRuleService().saveRule(ruleDelegation.getDelegationRule(), false);
            KEWServiceLocator.getRuleDelegationService().save(ruleDelegation);
        }
    }

    /**
     * Ensures that dependent objects have a reference to the specified rule template
     * @param ruleTemplate the rule template whose associates to check
     */
    private void fixAssociations(RuleTemplateBo ruleTemplate) {
        // if it's a valid rule template instance
        if (ruleTemplate != null && ruleTemplate.getId() != null) {
            // for every rule template attribute
            for (RuleTemplateAttributeBo ruleTemplateAttribute: ruleTemplate.getRuleTemplateAttributes()) {
                // if the rule template is not set on the attribute, set it
                if (ruleTemplateAttribute.getRuleTemplate() == null || ruleTemplateAttribute.getRuleTemplateId() == null) {
                    ruleTemplateAttribute.setRuleTemplate(ruleTemplate);
                }
                // if the rule attribute is set, load up the rule attribute and set the BO on the ruletemplateattribute association object
                if (ruleTemplateAttribute.getRuleAttribute() == null) {
                    RuleAttributeService ruleAttributeService = (RuleAttributeService) KEWServiceLocator.getService(KEWServiceLocator.RULE_ATTRIBUTE_SERVICE);
                    ruleTemplateAttribute.setRuleAttribute(ruleAttributeService.findByRuleAttributeId(ruleTemplateAttribute.getRuleAttributeId()));
                }
            }
            // for every rule template option
            for (RuleTemplateOptionBo option: ruleTemplate.getRuleTemplateOptions()) {
                // if the rule template is not set on the option, set it
                if (option.getRuleTemplate() == null || option.getRuleTemplateId() == null) {
                    option.setRuleTemplate(ruleTemplate);
                }
            }
        }
    }

    private void validate(RuleTemplateBo ruleTemplate) {
        LOG.debug("validating ruleTemplate");
        Collection errors = new ArrayList();
        if (ruleTemplate.getName() == null || ruleTemplate.getName().trim().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("Please enter a rule template name.", RULE_TEMPLATE_NAME_REQUIRED));
            LOG.error("Rule template name is missing");
        } else {
            ruleTemplate.setName(ruleTemplate.getName().trim());
            if (ruleTemplate.getId() == null) {
                RuleTemplateBo nameInUse = findByRuleTemplateName(ruleTemplate.getName());
                if (nameInUse != null) {
                    errors.add(new WorkflowServiceErrorImpl("Rule template name already in use", "rule.template.name.duplicate"));
                    LOG.error("Rule template name already in use");
                }
            }
        }
        if (ruleTemplate.getDescription() == null || ruleTemplate.getDescription().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("Please enter a rule template description.", RULE_TEMPLATE_DESCRIPTION_REQUIRED));
            LOG.error("Rule template description is missing");
        }
        //        if (ruleTemplate.getRuleTemplateAttributes() == null ||
        // ruleTemplate.getRuleTemplateAttributes().isEmpty()) {
        //            errors.add(new WorkflowServiceErrorImpl("Please select at least one a
        // rule template attribute.", RULE_TEMPLATE_ATTRIBUTE_REQUIRED));
        //        }

        LOG.debug("end validating ruleTemplate");
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("RuleTemplate Validation Error", errors);
        }
    }

    public RuleTemplateBo findByRuleTemplateId(String ruleTemplateId) {
        LOG.debug("findByRuleTemplateId RuleTemplateServiceImpl");
        return getRuleTemplateDAO().findByRuleTemplateId(ruleTemplateId);
    }

    public void delete(String ruleTemplateId) {
        LOG.debug("delete RuleTemplateServiceImpl");
        getRuleTemplateDAO().delete(ruleTemplateId);
        LOG.debug("end delete RuleTemplateServiceImpl");
    }

    public RuleTemplateDAO getRuleTemplateDAO() {
        return ruleTemplateDAO;
    }

    public void setRuleTemplateDAO(RuleTemplateDAO ruleTemplateDAO) {
        this.ruleTemplateDAO = ruleTemplateDAO;
    }

    public RuleDAO getRuleDAO() {
        return ruleDAO;
    }

    public void setRuleDAO(RuleDAO ruleDAO) {
        this.ruleDAO = ruleDAO;
    }

    public RuleDelegationDAO getRuleDelegationDAO() {
        return ruleDelegationDAO;
    }

    public void setRuleDelegationDAO(RuleDelegationDAO ruleDelegationDAO) {
        this.ruleDelegationDAO = ruleDelegationDAO;
    }

    public void loadXml(InputStream inputStream, String principalId) {
        RuleTemplateXmlParser parser = new RuleTemplateXmlParser();
        try {
            parser.parseRuleTemplates(inputStream);
        } catch (Exception e) { //any other exception
            LOG.error("Error loading xml file", e);
            WorkflowServiceErrorException wsee = new WorkflowServiceErrorException("Error loading xml file", new WorkflowServiceErrorImpl("Error loading xml file", XML_PARSE_ERROR));
            wsee.initCause(e);
            throw wsee;
        }
    }

    public Element export(ExportDataSet dataSet) {
        RuleTemplateXmlExporter exporter = new RuleTemplateXmlExporter();
        return exporter.export(dataSet);
    }
    
    @Override
	public boolean supportPrettyPrint() {
		return true;
	}

    public String getNextRuleTemplateId() {
        return getRuleTemplateDAO().getNextRuleTemplateId();
    }


    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

}
