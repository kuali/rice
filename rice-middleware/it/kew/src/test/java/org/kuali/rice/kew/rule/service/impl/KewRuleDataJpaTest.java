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
import org.kuali.rice.kew.rule.RuleExpressionDef;
import org.kuali.rice.kew.rule.RuleExtensionBo;
import org.kuali.rice.kew.rule.RuleExtensionValue;
import org.kuali.rice.kew.rule.RuleResponsibilityBo;
import org.kuali.rice.kew.rule.RuleTemplateOptionBo;
import org.kuali.rice.kew.rule.bo.RuleTemplateBo;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.test.BaselineTestCase;

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


        RuleExpressionDef ruleExpressionDef = new RuleExpressionDef();
        ruleExpressionDef.setExpression("test");
        ruleExpressionDef.setType("TST");

        rbv.setRuleExpressionDef(ruleExpressionDef);

        return KRADServiceLocator.getDataObjectService().save(rbv);
    }



}
