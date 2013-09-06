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

import org.junit.Test;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
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
 *  Test methods of ruleManagementServiceImpl relating to Rules
 */
public class RuleManagementRuleDefinitionTest  extends RuleManagementBaseTest{
    ////
    //// rule methods
    ////

    @Test
    public void testGetRuleByNameAndNamespace() {
        RuleDefinition ruleDefinition = ruleManagementServiceImpl.createRule(newTestRuleDefinition(NAMESPACE1,"3000"));

        RuleDefinition returnRuleDefinition = ruleManagementServiceImpl.getRuleByNameAndNamespace(ruleDefinition.getName(),ruleDefinition.getNamespace());

        assertEquals("rule not found", ruleDefinition.getId(), returnRuleDefinition.getId());

        try {
            returnRuleDefinition = ruleManagementServiceImpl.getRuleByNameAndNamespace(ruleDefinition.getName(),null);
            fail("should throw IllegalArgumentException: namespace is blank");
        } catch (Exception e) {
            // IllegalArgumentException: namespace is blank
        }
    }

    @Test
    public void testCreateRule() {
        // create a Rule
        RuleDefinition ruleFirstCreate = ruleManagementServiceImpl.createRule(newTestRuleDefinition(NAMESPACE1,"3001"));
        assertTrue("created Rule not found", ruleManagementServiceImpl.getRule(ruleFirstCreate.getId()).getId().contains("3001"));

        // try to create a duplicate Rule
        try {
            RuleDefinition ruleSecondCreate = ruleManagementServiceImpl.createRule(newTestRuleDefinition(NAMESPACE1,"3001"));
            fail("should have thrown RiceIllegalArgumentException");
        } catch (Exception e) {
            //  throw new RiceIllegalArgumentException(ruleDefinition.getId());
        }

        // try to create a malformed Rule
        RuleDefinition malformedRule = newTestRuleDefinition(NAMESPACE1,"3002");
        RuleDefinition.Builder builder = RuleDefinition.Builder.create(malformedRule);
        builder.setPropId("junk");
        malformedRule =  builder.build();
        try {
            ruleManagementServiceImpl.createRule(malformedRule);
            fail("should have thrown RiceIllegalArgumentException");
        } catch (Exception e) {
            // throw new RiceIllegalArgumentException("propId does not match proposition.getId"
        }
    }

    @Test
    public void testUpdateRule() {
        RuleDefinition.Builder ruleBuilder3003 = RuleDefinition.Builder.create(ruleManagementServiceImpl.createRule(newTestRuleDefinition(NAMESPACE1,"3003")));
        String ruleId = ruleBuilder3003.getId();
        String ruleName = ruleBuilder3003.getName();

        ruleBuilder3003.setName("updatedName");
        ruleManagementServiceImpl.updateRule(ruleBuilder3003.build());

        RuleDefinition rule3003 = ruleManagementServiceImpl.getRule(ruleId);
        assertNotEquals("Rule Name Not Updated", ruleName, rule3003.getName());
        assertEquals("Rule Name Not Updated", "updatedName", rule3003.getName());

        // Update Proposition in rule
        RuleDefinition.Builder ruleBuilder3004 = RuleDefinition.Builder.create(ruleManagementServiceImpl.createRule(newTestRuleDefinition(NAMESPACE1,"3004")));
        assertEquals("Expected Proposition not found in Rule","P3004_simple_proposition",ruleBuilder3004.getProposition().getDescription());

        String newPropId = "Pnew3004";

        PropositionDefinition prop3004 = createTestSimpleProposition(NAMESPACE1, newPropId, "TSI_"+newPropId, "ABC", "=", "java.lang.String", ruleId, "TSI_" + newPropId + "_Descr");
        PropositionDefinition.Builder propBuilder3004 = PropositionDefinition.Builder.create(prop3004);
        ruleBuilder3004.setPropId(newPropId);
        ruleBuilder3004.setProposition(propBuilder3004);
        ruleManagementServiceImpl.updateRule(ruleBuilder3004.build());

        RuleDefinition rule3004 = ruleManagementServiceImpl.getRule(ruleBuilder3004.getId());
        assertEquals("Expected Proposition not found in Rule","Pnew3004_simple_proposition",rule3004.getProposition().getDescription());
    }

    @Test
    public void testDeleteRule() {
        // create a Rule
        RuleDefinition rule3005 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(NAMESPACE1,"3005"));
        assertTrue("created Rule not found", ruleManagementServiceImpl.getRule(rule3005.getId()).getId().contains("3005"));
        String propositionId = rule3005.getPropId();
        assertEquals("Proposition for Rule not found", "P3005_simple_proposition",
                ruleManagementServiceImpl.getProposition(propositionId).getDescription());


        ruleManagementServiceImpl.deleteRule(rule3005.getId());

        assertNull("Rule was not deleted", ruleManagementServiceImpl.getRule(rule3005.getId()));

        // make sure proposition was cleaned up
        try {
            ruleManagementServiceImpl.deleteProposition(propositionId);
            fail("should fail with IllegalStateException: the Proposition to delete does not exists");
        } catch (Exception e) {
            // IllegalStateException: the Proposition to delete does not exists
        }
    }

    @Test
    public void testFindRuleIds() {
        RuleDefinition rule3010 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(NAMESPACE1,"3010"));
        RuleDefinition rule3011 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(NAMESPACE1,"3011"));
        RuleDefinition rule3012 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(NAMESPACE1,"3012"));
        RuleDefinition rule3013 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(NAMESPACE1,"3013"));
        String ruleNameSpace = rule3010.getNamespace();
        List<String> ruleNames =  new ArrayList<String>();
        ruleNames.add(rule3010.getName());
        ruleNames.add(rule3011.getName());
        ruleNames.add(rule3012.getName());
        ruleNames.add(rule3013.getName());

        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();

        builder.setPredicates(equal("namespace", ruleNameSpace),in("name", ruleNames.toArray(new String[]{})));

        CriteriaLookupServiceImpl criteriaLookupService = new CriteriaLookupServiceImpl();
        criteriaLookupService.setCriteriaLookupDao(new CriteriaLookupDaoProxy());
        ruleManagementServiceImpl.setCriteriaLookupService( criteriaLookupService);

        List<String> ruleIds = ruleManagementServiceImpl.findRuleIds(builder.build());
        assertEquals("Wrong number of RuleIds returned",4,ruleIds.size());

        if(!ruleIds.contains(rule3010.getId())){
            fail("RuleId not found in results");
        }
    }

    @Test
    public void testGetRule() {
        RuleDefinition ruleDefinition = ruleManagementServiceImpl.createRule(newTestRuleDefinition(NAMESPACE1,"3014"));

        assertNotNull(ruleManagementServiceImpl.getRule(ruleDefinition.getId()));

        assertNull("Should have returned null", ruleManagementServiceImpl.getRule(null));
        assertNull("Should have returned null",ruleManagementServiceImpl.getRule("   "));
        assertNull("Should have returned null",ruleManagementServiceImpl.getRule("badValueId"));
    }


    @Test
    public void testGetRules() {
        List<String> ruleIds = new ArrayList<String>();
        RuleDefinition rule3015 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(NAMESPACE1,"3015"));
        ruleIds.add(rule3015.getId());
        RuleDefinition rule3016 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(NAMESPACE1,"3016"));
        ruleIds.add(rule3016.getId());

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
        } catch (Exception e) {
            // throws RiceIllegalArgumentException: ruleIds must not be null
        }

        assertEquals("No RuleDefinitions should have been returned",0,ruleManagementServiceImpl.getRules(new ArrayList<String>()).size());

        ruleIds = Arrays.asList("badValueId");
        assertEquals("No RuleDefinitions should have been returned",0,ruleManagementServiceImpl.getRules(ruleIds).size());
    }
}
