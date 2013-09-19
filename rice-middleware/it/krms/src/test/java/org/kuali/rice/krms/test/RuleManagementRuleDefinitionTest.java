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

package org.kuali.rice.krms.test;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krad.criteria.CriteriaLookupDaoProxy;
import org.kuali.rice.krad.criteria.CriteriaLookupServiceImpl;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.in;


/**
 *   RuleManagementRuleDefinitionTest is to test the methods of ruleManagementServiceImpl relating to RuleDefinitions
 *
 *   Each test focuses on one of the methods.
 */
public class RuleManagementRuleDefinitionTest  extends RuleManagementBaseTest{
    @Override
    @Before
    public void setClassDiscriminator() {
        // set a unique discriminator for test objects of this class
        CLASS_DISCRIMINATOR = "RMRDT";
    }

    /**
     *  Test testGetRuleByNameAndNamespace()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .getRuleByNameAndNamespace("rule name", "namespace") method
     */
    @Test
    public void testGetRuleByNameAndNamespace() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t0 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t0");

        RuleDefinition ruleDefinition = ruleManagementServiceImpl.createRule(newTestRuleDefinition(t0.namespaceName,t0.object0));

        RuleDefinition returnRuleDefinition = ruleManagementServiceImpl.getRuleByNameAndNamespace(
                ruleDefinition.getName(), ruleDefinition.getNamespace());

        assertEquals("rule not found", ruleDefinition.getId(), returnRuleDefinition.getId());

        // try getRuleByNameAndNamespace with null Name parameter
        try {
            ruleManagementServiceImpl.getRuleByNameAndNamespace(null, t0.namespaceName);
            fail("Should have thrown IllegalArgumentException: name is null or blank");
        } catch (IllegalArgumentException e) {
            // throws IllegalArgumentException: name is null or blank
        }

        // try getRuleByNameAndNamespace with null Name parameter
        try {
            ruleManagementServiceImpl.getRuleByNameAndNamespace("   ", t0.namespaceName);
            fail("Should have thrown IllegalArgumentException: name is null or blank");
        } catch (IllegalArgumentException e) {
            // throws IllegalArgumentException: name is null or blank
        }

        // try getRuleByNameAndNamespace with null namespace parameter
        try {
            ruleManagementServiceImpl.getRuleByNameAndNamespace(ruleDefinition.getName(),null);
            fail("should throw IllegalArgumentException: namespace is null or blank");
        } catch (IllegalArgumentException e) {
            // IllegalArgumentException: namespace is null or blank
        }

        // try getRuleByNameAndNamespace with blank namespace parameter
        try {
            ruleManagementServiceImpl.getRuleByNameAndNamespace(ruleDefinition.getName(),"    ");
            fail("should throw IllegalArgumentException: namespace is null or blank");
        } catch (IllegalArgumentException e) {
            // IllegalArgumentException: namespace is null or blank
        }


    }

    /**
     *  Test testCreateRule()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .createRule(RuleDefinition) method
     */
    @Test
    public void testCreateRule() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t1 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t1");

        // create a Rule
        RuleDefinition ruleFirstCreate = ruleManagementServiceImpl.createRule(newTestRuleDefinition(t1.namespaceName,t1.object0));
        assertTrue("created Rule not found", ruleManagementServiceImpl.getRule(ruleFirstCreate.getId()).getId().contains(t1.rule_0_Id));

        // try to create a duplicate Rule
        try {
            RuleDefinition ruleSecondCreate = ruleManagementServiceImpl.createRule(ruleFirstCreate);
            fail("should have thrown RiceIllegalArgumentException");
        } catch (RiceIllegalArgumentException e) {
            //  throw new RiceIllegalArgumentException(ruleDefinition.getId());
        }

        // try to create a malformed Rule
        RuleDefinition malformedRule = newTestRuleDefinition(t1.namespaceName,t1.object1);
        RuleDefinition.Builder builder = RuleDefinition.Builder.create(malformedRule);
        builder.setPropId("invalidValue");
        malformedRule =  builder.build();
        try {
            ruleManagementServiceImpl.createRule(malformedRule);
            fail("should have thrown RiceIllegalArgumentException");
        } catch (RiceIllegalArgumentException e) {
            // throw new RiceIllegalArgumentException("propId does not match proposition.getId"
        }
    }

    /**
     *  Test testUpdateRule()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .updateRule(RuleDefinition) method
     */
    @Test
    public void testUpdateRule() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t2 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t2");

        // build a rule to test with
        RuleDefinition.Builder ruleBuilder0 = RuleDefinition.Builder.create(
                ruleManagementServiceImpl.createRule(newTestRuleDefinition(t2.namespaceName,t2.object0)));

        // update the rule's Name
        ruleBuilder0.setName("updatedName");
        ruleManagementServiceImpl.updateRule(ruleBuilder0.build());

        // verify update
        RuleDefinition rule0 = ruleManagementServiceImpl.getRule(t2.rule_0_Id);
        assertNotEquals("Rule Name Not Updated", t2.rule_0_Name, rule0.getName());
        assertEquals("Rule Name Not Updated", "updatedName", rule0.getName());

        // build new rule to for test
        RuleDefinition.Builder ruleBuilder1 = RuleDefinition.Builder.create(
                ruleManagementServiceImpl.createRule(newTestRuleDefinition(t2.namespaceName,t2.object1)));
        assertEquals("Expected Proposition not found in Rule",t2.proposition_1_Descr,ruleBuilder1.getProposition().getDescription());

        // create new proposition to update rule with
        String newPropId = "PropNewId";
        PropositionDefinition prop = createTestSimpleProposition(t2.namespaceName, newPropId, "TSI_"+newPropId,
                "ABC", "=", "java.lang.String", t2.rule_0_Id, "TSI_" + newPropId + "_Descr");
        PropositionDefinition.Builder propBuilder = PropositionDefinition.Builder.create(prop);
        ruleBuilder1.setPropId(newPropId);
        ruleBuilder1.setProposition(propBuilder);

        // Update Proposition in rule
        ruleManagementServiceImpl.updateRule(ruleBuilder1.build());

        rule0 = ruleManagementServiceImpl.getRule(ruleBuilder1.getId());
        assertEquals("Expected Proposition not found in Rule","PropNewId_simple_proposition",rule0.getProposition().getDescription());
    }

    /**
     *  Test testDeleteRule()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .deleteRule("rule id") method
     */
    @Test
    public void testDeleteRule() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t3 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t3");

        // create a Rule
        RuleDefinition rule = ruleManagementServiceImpl.createRule(newTestRuleDefinition(t3.namespaceName,t3.object0));
        assertTrue("created Rule not found", ruleManagementServiceImpl.getRule(rule.getId()).getId().contains(t3.rule_0_Id));
        String propositionId = rule.getPropId();
        assertEquals("Proposition for Rule not found", t3.proposition_0_Descr,ruleManagementServiceImpl.getProposition(propositionId).getDescription());


        ruleManagementServiceImpl.deleteRule(rule.getId());

        assertNull("Rule was not deleted", ruleManagementServiceImpl.getRule(rule.getId()));

        // make sure proposition was cleaned up when rule was deleted
        try {
            ruleManagementServiceImpl.deleteProposition(propositionId);
            fail("should fail with IllegalStateException: the Proposition to delete does not exists");
        } catch (IllegalStateException e) {
            // IllegalStateException: the Proposition to delete does not exists
        }
    }

    /**
     *  Test testFindRuleIds()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .findRuleIds( QueryByCriteria) method
     */
    @Test
    public void testFindRuleIds() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t4 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t4");

        RuleDefinition rule0 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(t4.namespaceName,t4.object0));
        RuleDefinition rule1 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(t4.namespaceName,t4.object1));
        RuleDefinition rule2 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(t4.namespaceName,t4.object2));
        RuleDefinition rule3 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(t4.namespaceName,t4.object3));
        String ruleNameSpace = rule0.getNamespace();
        List<String> ruleNames =  new ArrayList<String>();
        ruleNames.add(rule0.getName());
        ruleNames.add(rule1.getName());
        ruleNames.add(rule2.getName());
        ruleNames.add(rule3.getName());

        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();

        builder.setPredicates(equal("namespace", ruleNameSpace),in("name", ruleNames.toArray(new String[]{})));

        CriteriaLookupServiceImpl criteriaLookupService = new CriteriaLookupServiceImpl();
        criteriaLookupService.setCriteriaLookupDao(new CriteriaLookupDaoProxy());
        ruleManagementServiceImpl.setCriteriaLookupService( criteriaLookupService);

        List<String> ruleIds = ruleManagementServiceImpl.findRuleIds(builder.build());
        assertEquals("Wrong number of RuleIds returned",4,ruleIds.size());

        if(!ruleIds.contains(rule0.getId())){
            fail("RuleId not found in results");
        }
    }

    /**
     *  Test testGetRule()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .getRule("rule id") method
     */
    @Test
    public void testGetRule() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t5 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t5");

        // create a rule to test with
        RuleDefinition ruleDefinition = ruleManagementServiceImpl.createRule(newTestRuleDefinition(t5.namespaceName,t5.object0));

        assertNotNull(ruleManagementServiceImpl.getRule(ruleDefinition.getId()));

        assertNull("Should have returned null", ruleManagementServiceImpl.getRule(null));
        assertNull("Should have returned null",ruleManagementServiceImpl.getRule("   "));
        assertNull("Should have returned null",ruleManagementServiceImpl.getRule("badValueId"));
    }

    /**
     *  Test testGetRules()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .getRules(List<String> ruleIds) method
     */
    @Test
    public void testGetRules() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t6 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t6");

        // build two rules for testing
        ruleManagementServiceImpl.createRule(newTestRuleDefinition(t6.namespaceName,t6.object0));
        ruleManagementServiceImpl.createRule(newTestRuleDefinition(t6.namespaceName,t6.object1));

        // build List rule ids for the rules created
        List<String> ruleIds = new ArrayList<String>();
        ruleIds.add(t6.rule_0_Id);
        ruleIds.add(t6.rule_1_Id);

        // get test rules by List of rule ids
        List<RuleDefinition> ruleDefinitions = ruleManagementServiceImpl.getRules(ruleIds);
        assertEquals("Two RuleDefintions should have been returned",2,ruleDefinitions.size());

        for(RuleDefinition ruleDefinition : ruleDefinitions) {
            if (!ruleIds.contains(ruleDefinition.getId())) {
                fail("Invalid RuleDefinition returned");
            }
        }

        try {
            ruleManagementServiceImpl.getRules(null);
            fail("Should have failed with RiceIllegalArgumentException: ruleIds must not be null");
        } catch (RiceIllegalArgumentException e) {
            // throws RiceIllegalArgumentException: ruleIds must not be null
        }

        assertEquals("No RuleDefinitions should have been returned",0,ruleManagementServiceImpl.getRules(new ArrayList<String>()).size());

        ruleIds = Arrays.asList("badValueId");
        assertEquals("No RuleDefinitions should have been returned",0,ruleManagementServiceImpl.getRules(ruleIds).size());
    }
}
