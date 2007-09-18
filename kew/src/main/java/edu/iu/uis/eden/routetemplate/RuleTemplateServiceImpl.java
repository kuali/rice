/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.routetemplate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.routetemplate.dao.RuleDAO;
import edu.iu.uis.eden.routetemplate.dao.RuleDelegationDAO;
import edu.iu.uis.eden.routetemplate.dao.RuleTemplateAttributeDAO;
import edu.iu.uis.eden.routetemplate.dao.RuleTemplateDAO;
import edu.iu.uis.eden.routetemplate.dao.RuleTemplateOptionDAO;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.xml.RuleTemplateXmlParser;
import edu.iu.uis.eden.xml.export.RuleTemplateXmlExporter;

public class RuleTemplateServiceImpl implements RuleTemplateService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RuleTemplateServiceImpl.class);

    private static final String RULE_TEMPLATE_NAME_REQUIRED = "rule.template.name.required";

    private static final String RULE_TEMPLATE_DESCRIPTION_REQUIRED = "rule.template.description.required";

    private static final String XML_PARSE_ERROR = "general.error.parsexml";

    private RuleTemplateDAO ruleTemplateDAO;

    private RuleTemplateAttributeDAO ruleTemplateAttributeDAO;

    private RuleTemplateOptionDAO ruleTemplateOptionDAO;

    private RuleDAO ruleDAO;

    private RuleDelegationDAO ruleDelegationDAO;

    /*
     * (non-Javadoc)
     * 
     * @see edu.iu.uis.eden.routetemplate.RuleTemplateAttributeService#delete(java.lang.Long)
     */
//    public void deleteRuleTemplateAttribute(Long ruleTemplateAttributeId, List ruleTemplateAttributes) {
//
//        RuleTemplateAttribute ruleTemplateAttributeRemove = findByRuleTemplateAttributeId(ruleTemplateAttributeId);
//
//        for (int i = ruleTemplateAttributeRemove.getDisplayOrder().intValue() + 1; i <= ruleTemplateAttributes.size(); i++) {
//            RuleTemplateAttribute ruleTemplateAttributeUpdate = (RuleTemplateAttribute) ruleTemplateAttributes.get(i - 1);
//            ruleTemplateAttributeUpdate.setDisplayOrder(new Integer(i - 1));
//            getRuleTemplateAttributeDAO().save(ruleTemplateAttributeUpdate);
//        }
//        getRuleTemplateAttributeDAO().delete(ruleTemplateAttributeId);
//    }

    public void deleteRuleTemplateOption(Long ruleTemplateOptionId) {
        getRuleTemplateOptionDAO().delete(ruleTemplateOptionId);
    }

    public RuleTemplate findByRuleTemplateName(String ruleTemplateName) {
        return (getRuleTemplateDAO().findByRuleTemplateName(ruleTemplateName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.iu.uis.eden.routetemplate.RuleTemplateAttributeService#findByRuleTemplateAttributeId(java.lang.Long)
     */
    public RuleTemplateAttribute findByRuleTemplateAttributeId(Long ruleTemplateAttributeId) {
        return getRuleTemplateAttributeDAO().findByRuleTemplateAttributeId(ruleTemplateAttributeId);
    }

    public List findAll() {
        return ruleTemplateDAO.findAll();
    }

    public List findByRuleTemplate(RuleTemplate ruleTemplate) {
        return ruleTemplateDAO.findByRuleTemplate(ruleTemplate);
    }

    public void save(RuleTemplate ruleTemplate) {
        LOG.debug("save RuleTemplateServiceImpl");
        validate(ruleTemplate);
        fixAssociations(ruleTemplate);
//        if (ruleTemplate.getRuleTemplateId() != null) {
//            RuleTemplate previousRuleTemplate = findByRuleTemplateId(ruleTemplate.getRuleTemplateId());
//            if (previousRuleTemplate != null) {
//                for (Iterator iter = previousRuleTemplate.getRuleTemplateAttributes().iterator(); iter.hasNext();) {
//                    RuleTemplateAttribute previousAttribute = (RuleTemplateAttribute) iter.next();
//                    boolean found = false;
//
//                    for (Iterator iter2 = ruleTemplate.getRuleTemplateAttributes().iterator(); iter2.hasNext();) {
//                        RuleTemplateAttribute attribute = (RuleTemplateAttribute) iter2.next();
//                        if (previousAttribute.getRuleAttribute().getName().equals(attribute.getRuleAttribute().getName())) {
//                            found = true;
//                            break;
//                        }
//                    }
//                    if (!found) {
//                        getRuleTemplateAttributeDAO().delete(previousAttribute.getRuleTemplateAttributeId());
//                    }
//                }
//            }
//        }

        getRuleTemplateDAO().save(ruleTemplate);
        LOG.debug("end save RuleTemplateServiceImpl");
    }

    public void save(RuleTemplateAttribute ruleTemplateAttribute) {
        ruleTemplateAttributeDAO.save(ruleTemplateAttribute);
    }

    public void save(RuleBaseValues ruleBaseValues) {
        ruleDAO.save(ruleBaseValues);
    }

    public void save(RuleDelegation ruleDelegation, RuleBaseValues ruleBaseValues) {
        save(ruleBaseValues);
        if (ruleDelegation != null) {
            ruleDelegationDAO.save(ruleDelegation);
        }
    }

    private void fixAssociations(RuleTemplate ruleTemplate) {
        if (ruleTemplate != null && ruleTemplate.getRuleTemplateId() != null) {
            for (Iterator iter = ruleTemplate.getRuleTemplateAttributes().iterator(); iter.hasNext();) {
                RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iter.next();
                if (ruleTemplateAttribute.getRuleTemplate() == null || ruleTemplateAttribute.getRuleTemplateId() == null) {
                    ruleTemplateAttribute.setRuleTemplate(ruleTemplate);
                }
                if (ruleTemplateAttribute.getRuleAttribute() == null) {
                    RuleAttributeService ruleAttributeService = (RuleAttributeService) KEWServiceLocator.getService(KEWServiceLocator.RULE_ATTRIBUTE_SERVICE);
                    ruleTemplateAttribute.setRuleAttribute(ruleAttributeService.findByRuleAttributeId(ruleTemplateAttribute.getRuleAttributeId()));
                }
            }
            for (Iterator iter = ruleTemplate.getRuleTemplateOptions().iterator(); iter.hasNext();) {
                RuleTemplateOption option = (RuleTemplateOption) iter.next();
                if (option.getRuleTemplate() == null || option.getRuleTemplateId() == null) {
                    option.setRuleTemplate(ruleTemplate);
                }
            }
        }
    }

    private void validate(RuleTemplate ruleTemplate) {
        LOG.debug("validating ruleTemplate");
        Collection errors = new ArrayList();
        if (ruleTemplate.getName() == null || ruleTemplate.getName().trim().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("Please enter a rule template name.", RULE_TEMPLATE_NAME_REQUIRED));
            LOG.error("Rule template name is missing");
        } else {
            ruleTemplate.setName(ruleTemplate.getName().trim());
            if (ruleTemplate.getRuleTemplateId() == null) {
                RuleTemplate nameInUse = findByRuleTemplateName(ruleTemplate.getName());
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

    public RuleTemplate findByRuleTemplateId(Long ruleTemplateId) {
        LOG.debug("findByRuleTemplateId RuleTemplateServiceImpl");
        return getRuleTemplateDAO().findByRuleTemplateId(ruleTemplateId);
    }

    public void delete(Long ruleTemplateId) {
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

    /**
     * @return Returns the ruleTemplateAttributeDAO.
     */
    public RuleTemplateAttributeDAO getRuleTemplateAttributeDAO() {
        return ruleTemplateAttributeDAO;
    }

    /**
     * @param ruleTemplateAttributeDAO
     *            The ruleTemplateAttributeDAO to set.
     */
    public void setRuleTemplateAttributeDAO(RuleTemplateAttributeDAO ruleTemplateAttributeDAO) {
        this.ruleTemplateAttributeDAO = ruleTemplateAttributeDAO;
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

    /**
     * @return Returns the ruleTemplateOptionDAO.
     */
    public RuleTemplateOptionDAO getRuleTemplateOptionDAO() {
        return ruleTemplateOptionDAO;
    }

    /**
     * @param ruleTemplateOptionDAO
     *            The ruleTemplateOptionDAO to set.
     */
    public void setRuleTemplateOptionDAO(RuleTemplateOptionDAO ruleTemplateOptionDAO) {
        this.ruleTemplateOptionDAO = ruleTemplateOptionDAO;
    }

    public void loadXml(InputStream inputStream, WorkflowUser user) {
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

    public Long getNextRuleTemplateId() {
        return getRuleTemplateDAO().getNextRuleTemplateId();
    }

}