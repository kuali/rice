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

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.krad.criteria.CriteriaLookupDaoProxy;
import org.kuali.rice.krad.criteria.CriteriaLookupServiceImpl;
import org.kuali.rice.krms.api.repository.LogicalOperator;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameter;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterType;
import org.kuali.rice.krms.api.repository.proposition.PropositionType;
import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

/**
 * Test methods of ruleManagementServiceImpl relating to Propositions
 */
public class RuleManagementPropositionDefinitionTest extends RuleManagementBaseTest {
    static String TEST_PREFIX = "RuleManagementPropDefTest";

    ////
    //// proposition methods
    ////

    @Test
    public void testCreateProposition() {
        String ruleId = TEST_PREFIX + "RuleId" + "2000";
        PropositionDefinition propositionDefinition = createTestSimpleProposition(NAMESPACE1, "P2000", "campusCode", "BL", "=", "java.lang.String", ruleId, "Campus Code");

        assertEquals("created Proposition not found", ruleId, propositionDefinition.getRuleId());

        try {
            // returnPropositionDefinition has existing propositionId
            propositionDefinition = ruleManagementServiceImpl.createProposition(propositionDefinition);
            fail("should throw exception if trying to create and already exists");
        } catch (Exception e) {
            //  throw new RiceIllegalArgumentException(propositionDefinition.getId());
        }
    }

    @Test
    public void testCompoundCreateProposition() {
        // Tests of create a complex Compound Proposition (True if Account is 54321 and Occasion is either a Conference or Training)

        //  C1_compound_proposition            "COMPOUND" "S1_simple_proposition"  "C2_compound_proposition" "&"
        //      S1_simple_proposition          "SIMPLE"   "Account"                "54321"                   "="
        //      C2_compound_proposition        "COMPOUND" "S2_simple_proposition"  "S2_simple_proposition"   "|"
        //          S2_simple_proposition      "SIMPLE"   "Occasion"               "Conference"              "="
        //          S3_simple_proposition      "SIMPLE"   "Occasion"               "Training"                "="
        String ruleId = TEST_PREFIX + "RuleId" + "2001";

        PropositionDefinition propS3 = createTestSimpleProposition(NAMESPACE1, "S3", "Occasion", "Training", "=", "java.lang.String", ruleId, "Special Event");
        PropositionDefinition.Builder propBuilderS3 = PropositionDefinition.Builder.create(propS3);

        PropositionDefinition propS2 = createTestSimpleProposition(NAMESPACE1, "S2", "Occasion", "Conference", "=", "java.lang.String", ruleId, "Special Event");
        PropositionDefinition.Builder propBuilderS2 = PropositionDefinition.Builder.create(propS2);

        PropositionDefinition propS1 = createTestSimpleProposition(NAMESPACE1, "S1", "Account", "54321", "=", "java.lang.String", ruleId, "Charged To Account");
        PropositionDefinition.Builder propBuilderS1 = PropositionDefinition.Builder.create(propS1);

        PropositionDefinition.Builder propBuilderC2 = PropositionDefinition.Builder.create(
                null,
                PropositionType.COMPOUND.getCode(),
                ruleId,
                null,
                new ArrayList<PropositionParameter.Builder>());
        propBuilderC2.compoundOpCode(LogicalOperator.OR.getCode());
        List<PropositionDefinition.Builder> compoundComponentsC2 = new ArrayList<PropositionDefinition.Builder>();
        compoundComponentsC2.add(propBuilderS2);
        compoundComponentsC2.add(propBuilderS3);
        propBuilderC2.setCompoundComponents(compoundComponentsC2);
        propBuilderC2.setDescription("C2_compound_proposition");

        PropositionDefinition.Builder propBuilderC1 = PropositionDefinition.Builder.create(
                null,
                PropositionType.COMPOUND.getCode(),
                ruleId,
                null,
                new ArrayList<PropositionParameter.Builder>());
        propBuilderC1.compoundOpCode(LogicalOperator.AND.getCode());
        List<PropositionDefinition.Builder> compoundComponentsC1 = new ArrayList<PropositionDefinition.Builder>();
        compoundComponentsC1.add(propBuilderS1);
        compoundComponentsC1.add(propBuilderC2);
        propBuilderC1.setCompoundComponents(compoundComponentsC1);
        propBuilderC1.setDescription("C1_compound_proposition");
        PropositionDefinition propC1 = ruleManagementServiceImpl.createProposition(propBuilderC1.build());

        propC1 = ruleManagementServiceImpl.getProposition(propC1.getId());
        assertEquals("proposition not in database","C1_compound_proposition",propC1.getDescription());

        List<String> propositionDescrs = Arrays.asList("C1_compound_proposition", "C2_compound_proposition", "S1_simple_proposition", "S2_simple_proposition", "S3_simple_proposition");
        Set<PropositionDefinition> propsFound = ruleManagementServiceImpl.getPropositionsByRule(ruleId);
        for (PropositionDefinition propTemp : propsFound) {
            assertTrue(propositionDescrs.contains(propTemp.getDescription()));
        }

        assertEquals("invalid number of propositions found for ruleId", 5, propsFound.size());
    }

    @Test
    public void testGetProposition() {
        PropositionDefinition propositionDefinition = createTestPropositionForRule("2002");

        PropositionDefinition returnPropositionDefinition = ruleManagementServiceImpl.getProposition(propositionDefinition.getId());

        //  match ruleIds from returned proposition returned by get by propositionId
        assertEquals("", propositionDefinition.getRuleId(), returnPropositionDefinition.getRuleId());

        try {
            returnPropositionDefinition = ruleManagementServiceImpl.getProposition(null);
            fail("should throw RiceIllegalArgumentException");
        } catch (Exception e) {
            // throw new RiceIllegalArgumentException (id);
        }
    }


    @Test
    public void testGetPropositionsByType() {
        PropositionDefinition propositionDefinition = createTestPropositionForRule("2003");

        Set<PropositionDefinition> propositionDefinitionSet = ruleManagementServiceImpl.getPropositionsByType(propositionDefinition.getTypeId());

        boolean propositionFound = false;
        for ( PropositionDefinition pd : propositionDefinitionSet ) {
            if (pd.getId().equals(propositionDefinition.getId())){
                assertEquals("unexpected PropositionTypeId returned",propositionDefinition.getTypeId(),pd.getTypeId());
                propositionFound = true;
            }
        }

        assertTrue("proposition not found by PropositionTypeId",propositionFound);
    }

    @Test
    public void testGetPropositionsByRule() {
        PropositionDefinition propositionDefinition = createTestPropositionForRule("2004");

        Set<PropositionDefinition> propositionDefinitionSet = ruleManagementServiceImpl.getPropositionsByRule(
                propositionDefinition.getRuleId());

        boolean propositionFound = false;
        for ( PropositionDefinition pd : propositionDefinitionSet ) {
            if (pd.getId().equals(propositionDefinition.getId())){
                propositionFound = true;
            }
        }

        assertTrue("proposition not found by RuleId",propositionFound);
    }

    @Test
    public void testUpdateProposition() {
        PropositionDefinition propositionDefinition = createTestPropositionForRule("2005");
        PropositionDefinition.Builder builder = PropositionDefinition.Builder.create(propositionDefinition);
        builder.setDescription("UpdatedDescription");
        builder.setPropositionTypeCode(PropositionType.COMPOUND.getCode());

        // focus of test is on this instruction
        ruleManagementServiceImpl.updateProposition(builder.build());

        PropositionDefinition returnPropositionDefinition = ruleManagementServiceImpl.getProposition(propositionDefinition.getId());

        assertEquals("description was not updated", "UpdatedDescription", returnPropositionDefinition.getDescription());
        assertEquals("propositionType was not updated", PropositionType.COMPOUND.getCode(), returnPropositionDefinition.getPropositionTypeCode());
    }


    @Test
    public void testDeleteProposition() {
        PropositionDefinition propositionDefinition = createTestPropositionForRule("2006");

        ruleManagementServiceImpl.deleteProposition(propositionDefinition.getId());

        assertTrue("proposition should have been deleted", ruleManagementServiceImpl.getPropositionsByRule(propositionDefinition.getRuleId()).isEmpty());

        try {
            ruleManagementServiceImpl.deleteProposition(propositionDefinition.getId());
            fail("should fail with IllegalStateException: the Proposition to delete does not exists");
        } catch (Exception e) {
            // IllegalStateException: the Proposition to delete does not exists
        }
    }

    @Test
    public void testFindPropositionIds() {
        List<String> propositionIds = Arrays.asList(
                createTestPropositionForRule("2007").getId(),
                createTestPropositionForRule("2008").getId(),
                createTestPropositionForRule("2009").getId(),
                createTestPropositionForRule("2010").getId());
        for (String propositionId : propositionIds) {
            PropositionDefinition.Builder builder = PropositionDefinition.Builder.create(
                    ruleManagementServiceImpl.getProposition(propositionId));
            builder.setDescription("targetOfQuery");
            ruleManagementServiceImpl.updateProposition(builder.build());
        }

        QueryByCriteria.Builder query = QueryByCriteria.Builder.create();
        query.setPredicates(equal("description", "targetOfQuery"));

        CriteriaLookupServiceImpl criteriaLookupService = new CriteriaLookupServiceImpl();
        criteriaLookupService.setCriteriaLookupDao(new CriteriaLookupDaoProxy());
        ruleManagementServiceImpl.setCriteriaLookupService( criteriaLookupService);

        List<String> returnedPropositionIds = ruleManagementServiceImpl.findPropositionIds(query.build());

        for (String returnedPropositionId : returnedPropositionIds ) {
            assertTrue(propositionIds.contains(returnedPropositionId));
        }

        assertEquals("incorrect number of Propositions found", 4, returnedPropositionIds.size());
    }
}
