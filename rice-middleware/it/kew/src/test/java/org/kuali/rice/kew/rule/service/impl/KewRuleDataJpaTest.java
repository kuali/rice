/*
 * Copyright 2006-2013 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.rule.RuleTemplate;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegationBo;
import org.kuali.rice.kew.rule.RuleExpressionDef;
import org.kuali.rice.kew.rule.RuleExtensionBo;
import org.kuali.rice.kew.rule.RuleExtensionValue;
import org.kuali.rice.kew.rule.RuleResponsibilityBo;
import org.kuali.rice.kew.rule.RuleTemplateOptionBo;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttributeBo;
import org.kuali.rice.kew.rule.bo.RuleTemplateBo;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.test.BaselineTestCase;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Tests to confirm JPA mapping for the Kew module Rule objects
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class KewRuleDataJpaTest extends KRADTestCase{
    @Test
    public void testRuleBaseValuesPersistAndFetch() throws Exception{
        RuleBaseValues ruleBaseValues = setupRuleBaseValues();
        String ruleBaseValuesId = ruleBaseValues.getId();
        assertTrue("RuleBaseValues persisted correctly",ruleBaseValues != null &&
                StringUtils.isNotBlank(ruleBaseValuesId));
        ruleBaseValues = KRADServiceLocator.getDataObjectService().find(RuleBaseValues.class,ruleBaseValuesId);
        assertTrue("RuleBaseValues refetched correctly",ruleBaseValues != null &&
                StringUtils.equals(ruleBaseValuesId,ruleBaseValues.getId()));
        assertTrue("RuleExtension persisted correctly",
                ruleBaseValues.getRuleExtensions() != null && ruleBaseValues.getRuleExtensions().size() == 1);
        RuleExtensionBo ruleExtensionBo = ruleBaseValues.getRuleExtensions().get(0);
        assertTrue("RuleExtensionValue persisted correctly", ruleExtensionBo.getExtensionValues() != null
                && ruleExtensionBo.getExtensionValues().size() == 1);
        assertTrue("RuleResponsibilities persisted correctly", ruleBaseValues.getRuleResponsibilities() != null
                && ruleBaseValues.getRuleResponsibilities().size() == 1);
        assertTrue("RuleTemplate persisted correctly", ruleBaseValues.getRuleTemplate() != null
                && StringUtils.isNotBlank(ruleBaseValues.getRuleTemplate().getId()));
        assertTrue("RuleExpressionDef persisted correctly", ruleBaseValues.getRuleExpressionDef() != null
                && StringUtils.isNotBlank(ruleBaseValues.getRuleExpressionDef().getExpression()));
        assertTrue("Rule Template Option persisted correctly",
                ruleBaseValues.getRuleTemplate().getRuleTemplateOptions() != null
             && ruleBaseValues.getRuleTemplate().getRuleTemplateOptions().size() == 1);
        assertTrue("Rule Template Attribute persisted correctly",
                ruleBaseValues.getRuleTemplate().getRuleTemplateAttributes() != null &&
                ruleBaseValues.getRuleTemplate().getRuleTemplateAttributes().size() == 1);
    }

    @Test
    public void testRuleAttributeServiceFindByRuleAttribute() throws Exception{
        RuleAttribute ruleAttribute = setupRuleAttribute();

        List<RuleAttribute> ruleAttributeList = KEWServiceLocator.getRuleAttributeService().
                                    findByRuleAttribute(ruleAttribute);

        assertTrue("Rule attribute find by rule attribute fetched correctly",ruleAttributeList != null
                && ruleAttributeList.size() == 1);
    }

    @Test
    public void testRuleAttributeServiceGetAllRuleAttributes() throws Exception{
        setupRuleAttribute();
        setupRuleAttributeSimilar();

        List<RuleAttribute> ruleAttributeList = KEWServiceLocator.getRuleAttributeService().findAll();
        assertTrue("Rule attribute fetched all correctly",ruleAttributeList != null
                && ruleAttributeList.size() == 2);
    }

    @Test
         public void tesRuleAttributeServiceFindByName() throws Exception{
        RuleAttribute ruleAttribute = setupRuleAttribute();

        ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName(ruleAttribute.getName());
        assertTrue("RuleAttribute find by name fetched correctly",ruleAttribute != null);
    }

    @Test
    public void tesRuleAttributeServiceFindByClassName() throws Exception{
        RuleAttribute ruleAttribute = setupRuleAttribute();
        setupRuleAttributeSimilar();

        List<RuleAttribute> ruleAttributeList = KEWServiceLocator.getRuleAttributeService().findByClassName(ruleAttribute.getResourceDescriptor());
        assertTrue("Rule attribute find by class name fetched correctly",ruleAttributeList != null
                && ruleAttributeList.size() == 2);
    }

    @Test
    public void testRuleTemplateServiceFindByRuleTemplateName() throws Exception{
        RuleTemplateBo ruleTemplateBo = setupRuleTemplateBo("test");
        String name = ruleTemplateBo.getName();
        ruleTemplateBo = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(ruleTemplateBo.getName());
        assertTrue("RuleTemplate fetched based on name", ruleTemplateBo != null
                    && StringUtils.equals(name,ruleTemplateBo.getName()));
    }

    @Test
    public void testRuleTemplateServiceDeleteRuleTemplateOption() throws Exception{
        RuleTemplateBo ruleTemplateBo = setupRuleTemplateBo("test");
        String optionId = ruleTemplateBo.getRuleTemplateOptions().get(0).getId();
        KEWServiceLocator.getRuleTemplateService().deleteRuleTemplateOption(optionId);
        RuleTemplateOptionBo ruleTemplateOptionBo = KRADServiceLocator.getDataObjectService().
                        find(RuleTemplateOptionBo.class, optionId);
        assertTrue("Rule Template Option is null",ruleTemplateOptionBo == null);
    }

    @Test
    public void testRuleDelegationServiceFindAllCurrentRuleDelegations() throws Exception{
        RuleBaseValues ruleBaseValues = setupRuleBaseValues();
        RuleDelegationBo ruleDelegationBo = setupRuleDelegationBo();
        ruleDelegationBo.setDelegationRule(ruleBaseValues);
        KRADServiceLocator.getDataObjectService().save(ruleDelegationBo);
        List<RuleDelegationBo> ruleDelegationBos = KEWServiceLocator.getRuleDelegationService().
                    findAllCurrentRuleDelegations();
        assertTrue("Rule delegation bo found",ruleDelegationBos != null);
    }

    @Test
    public void testRuleDelegationServiceFindByDelegateRuleId() throws Exception{
        RuleDelegationBo ruleDelegationBo = setupRuleDelegationBo();
        List<RuleDelegationBo> ruleDelegationBos = KEWServiceLocator.getRuleDelegationService().
                        findByDelegateRuleId(ruleDelegationBo.getDelegateRuleId());
        assertTrue("Rule Delegation Bo fetched by rule id",ruleDelegationBos != null && ruleDelegationBos.size() == 1);
    }

    @Test
    public void testRuleTemplateServiceFindAll() throws Exception{
        setupRuleTemplateBo("test");
        setupRuleTemplateBo("otherTest");

        List<RuleTemplateBo> ruleTemplateBos = KEWServiceLocator.getRuleTemplateService().findAll();
        assertTrue("Rule Template Bo fetched all", ruleTemplateBos != null && ruleTemplateBos.size() == 2);
    }

    @Test
    public void testRuleDelegationServiceFindByResponsibilityIdWithCurrentRule() throws Exception{
        RuleBaseValues ruleBaseValues = setupRuleBaseValues();
        RuleDelegationBo ruleDelegationBo = setupRuleDelegationBo();
        ruleDelegationBo.setResponsibilityId(ruleBaseValues.getId());
        ruleDelegationBo.setDelegateRuleId(ruleBaseValues.getId());
        ruleDelegationBo = KRADServiceLocator.getDataObjectService().save(ruleDelegationBo);

        List<RuleDelegationBo> ruleDelegationBos = KEWServiceLocator.getRuleDelegationService().findByResponsibilityId(
                    ruleDelegationBo.getResponsibilityId());
        assertTrue("Rule Delegation Bo fetched ", ruleDelegationBos != null && ruleDelegationBos.size() == 1);
    }

    private RuleDelegationBo setupRuleDelegationBo(){
        RuleDelegationBo ruleDelegationBo = new RuleDelegationBo();
        ruleDelegationBo.setDelegationTypeCode("P");
        ruleDelegationBo.setGroupReviewerName("Testing");
        ruleDelegationBo.setPersonReviewer("blah");

        return KRADServiceLocator.getDataObjectService().save(ruleDelegationBo);
    }

    private RuleTemplateBo setupRuleTemplateBo(String name){
        RuleTemplateBo ruleTemplate = new RuleTemplateBo();
        ruleTemplate.setName(name);
        ruleTemplate.setReturnUrl("testing");
        ruleTemplate.setDescription("description");

        RuleTemplateOptionBo ruleTemplateOptionBo = new RuleTemplateOptionBo();
        ruleTemplateOptionBo.setCode("P");
        ruleTemplateOptionBo.setValue("VAL");
        ruleTemplateOptionBo.setRuleTemplate(ruleTemplate);
        ruleTemplate.getRuleTemplateOptions().add(ruleTemplateOptionBo);

        return KRADServiceLocator.getDataObjectService().save(ruleTemplate);
    }



    private RuleAttribute setupRuleAttribute(){
        RuleAttribute ruleAttribute = new RuleAttribute();
        ruleAttribute.setApplicationId("TST");
        ruleAttribute.setDescription("Testing");
        ruleAttribute.setLabel("New Label");
        ruleAttribute.setResourceDescriptor("ResourceDescriptor");
        ruleAttribute.setType("newType");
        ruleAttribute.setName("Attr");

        return KRADServiceLocator.getDataObjectService().save(ruleAttribute);
    }

    private RuleAttribute setupRuleAttributeSimilar(){
        RuleAttribute ruleAttribute = new RuleAttribute();
        ruleAttribute.setApplicationId("TST2");
        ruleAttribute.setDescription("Testingfdsa");
        ruleAttribute.setLabel("New Labefdsal");
        ruleAttribute.setResourceDescriptor("ResourceDescriptor");
        ruleAttribute.setType("newType");
        ruleAttribute.setName("Attr2");

        return KRADServiceLocator.getDataObjectService().save(ruleAttribute);
    }




    private RuleBaseValues setupRuleBaseValues() {
        final RuleBaseValues rbv = new RuleBaseValues();
        rbv.setActive(Boolean.TRUE);
        rbv.setCurrentInd(Boolean.TRUE);
        rbv.setDescription("A test rule");
        rbv.setDocTypeName("TestDocumentType");
        rbv.setForceAction(Boolean.FALSE);

        RuleExtensionBo ext = new RuleExtensionBo();
        RuleExtensionValue val = new RuleExtensionValue();
        val.setKey("emptyvalue");
        val.setValue("testing");
        val.setExtension(ext);
        ext.getExtensionValues().add(val);
        ext.setRuleBaseValues(rbv);
        rbv.getRuleExtensions().add(ext);

        RuleResponsibilityBo ruleResponsibilityBo = new RuleResponsibilityBo();

        ruleResponsibilityBo.setRuleBaseValues(rbv);
        ruleResponsibilityBo.setRuleResponsibilityName("user2");
        ruleResponsibilityBo.setRuleResponsibilityType(KewApiConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
        rbv.getRuleResponsibilities().add(ruleResponsibilityBo);

        RuleTemplateBo ruleTemplate = new RuleTemplateBo();
        ruleTemplate.setName("test");
        ruleTemplate.setDescription("description");
        rbv.setRuleTemplate(ruleTemplate);

        RuleTemplateOptionBo ruleTemplateOptionBo = new RuleTemplateOptionBo();
        ruleTemplateOptionBo.setCode("TST");
        ruleTemplateOptionBo.setValue("VAL");
        ruleTemplateOptionBo.setRuleTemplate(ruleTemplate);
        ruleTemplate.getRuleTemplateOptions().add(ruleTemplateOptionBo);

        RuleTemplateAttributeBo ruleTemplateAttributeBo = new RuleTemplateAttributeBo();
        ruleTemplateAttributeBo.setActive(true);
        ruleTemplateAttributeBo.setDefaultValue("testAttr");
        ruleTemplateAttributeBo.setDisplayOrder(1);
        ruleTemplateAttributeBo.setRequired(true);
        ruleTemplateAttributeBo.setRuleTemplate(ruleTemplate);

        ruleTemplate.getRuleTemplateAttributes().add(ruleTemplateAttributeBo);


        RuleExpressionDef ruleExpressionDef = new RuleExpressionDef();
        ruleExpressionDef.setExpression("test");
        ruleExpressionDef.setType("TST");

        rbv.setRuleExpressionDef(ruleExpressionDef);

        return KRADServiceLocator.getDataObjectService().save(rbv);
    }



}
