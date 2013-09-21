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
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

/**
 *   RuleManagementActionDefinitionTest is to test the methods of ruleManagementServiceImpl relating to krms Actions
 *
 *   Each test focuses on one of the methods.
 */
    public class RuleManagementActionDefinitionTest extends RuleManagementBaseTest {

    @Override
    @Before
    public void setClassDiscriminator() {
        // set a unique discriminator for test objects of this class
        CLASS_DISCRIMINATOR = "RMADT";
    }

    /**
     *  Test testCreateAction()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .createAction(ActionDefinition) method
     */
    @Test
    public void testCreateAction() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t0 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t0");

        KrmsTypeDefinition krmsTypeDefinition = createKrmsActionTypeDefinition(t0.namespaceName);
        RuleDefinition ruleDefintion = createTestRule(t0.namespaceName, t0.discriminator);

        ActionDefinition actionDefinition = ActionDefinition.Builder.create(t0.action0_Id, t0.action0_Name,
                t0.namespaceName,krmsTypeDefinition.getId(),ruleDefintion.getId(),1).build();

        assertNull("action should not be in database", ruleManagementServiceImpl.getAction(t0.action0_Id));

        // primary statement for test
        actionDefinition =  ruleManagementServiceImpl.createAction(actionDefinition);

        ActionDefinition returnActionDefinition = ruleManagementServiceImpl.getAction(actionDefinition.getId());

        assertNotNull("created action not found", (Object) returnActionDefinition);
        assertEquals("create action error:", t0.action0_Id, returnActionDefinition.getId());
    }

    /**
     *  Test testUpdateAction()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .testUpdateAction(ActionDefinition) method
     */
    @Test
    public void testUpdateAction() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t1 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t1");

        KrmsTypeDefinition krmsTypeDefinition = createKrmsActionTypeDefinition(t1.namespaceName);
        RuleDefinition ruleDefinition = createTestRule(t1.namespaceName, t1.object0);

        ActionDefinition actionDefinition = ActionDefinition.Builder.create(t1.action0_Id,t1.action0_Name,
                t1.namespaceName,krmsTypeDefinition.getId(),ruleDefinition.getId(),1).build();

        assertNull("action should not be in database",ruleManagementServiceImpl.getAction(t1.action0_Id));

        actionDefinition =  ruleManagementServiceImpl.createAction(actionDefinition);

        ActionDefinition returnActionDefinition = ruleManagementServiceImpl.getAction(actionDefinition.getId());
        ActionDefinition.Builder builder = ActionDefinition.Builder.create(returnActionDefinition);
        builder.setDescription("ChangedDescr");

        // primary statement for test
        ruleManagementServiceImpl.updateAction(builder.build());

        returnActionDefinition = ruleManagementServiceImpl.getAction(actionDefinition.getId());

        assertNotNull("action not found", returnActionDefinition);
        assertEquals("update action error:","ChangedDescr", returnActionDefinition.getDescription());
    }

    /**
     *  Test testDeleteAction()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .testDeleteAction(ActionDefinition) method
     */
    @Test
    public void testDeleteAction() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t2 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t2");

        KrmsTypeDefinition krmsTypeDefinition = createKrmsActionTypeDefinition(t2.namespaceName);
        RuleDefinition ruleDefintion = createTestRule(t2.namespaceName, t2.object0);

        ActionDefinition actionDefinition = ActionDefinition.Builder.create(t2.action0_Id,t2.action0_Name,
                t2.namespaceName,krmsTypeDefinition.getId(),ruleDefintion.getId(),1).build();

        assertNull("action should not be in database",ruleManagementServiceImpl.getAction(t2.action0_Id));

        actionDefinition =  ruleManagementServiceImpl.createAction(actionDefinition);
        actionDefinition = ruleManagementServiceImpl.getAction(actionDefinition.getId());
        assertNotNull("action not found",ruleManagementServiceImpl.getAction(actionDefinition.getId()));

        try {
            // primary statement for test
            ruleManagementServiceImpl.deleteAction(t2.action0_Id);
            fail("should fail deleteAction not implemented");
        }   catch (RiceIllegalArgumentException e) {
            // RiceIllegalArgumentException ("not implemented yet because not supported by the bo service");
        }

        actionDefinition = ruleManagementServiceImpl.getAction(actionDefinition.getId());
        assertNotNull("action not found", (Object) actionDefinition);
    }

    /**
     *  Test testGetAction()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .testGetAction(Action_Id) method
     */
    @Test
    public void testGetAction() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t3 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t3");

        KrmsTypeDefinition krmsTypeDefinition = createKrmsActionTypeDefinition(t3.namespaceName);
        RuleDefinition ruleDefintion = createTestRule(t3.namespaceName,t3.object0);

        ActionDefinition actionDefinition = ActionDefinition.Builder.create(t3.action0_Id,t3.action0_Name,
                t3.namespaceName,krmsTypeDefinition.getId(),ruleDefintion.getId(),1).build();

        assertNull("action should not be in database", ruleManagementServiceImpl.getAction(t3.action0_Id));
        actionDefinition =  ruleManagementServiceImpl.createAction(actionDefinition);

        // primary statement being tested
        ActionDefinition returnActionDefinition = ruleManagementServiceImpl.getAction(actionDefinition.getId());

        assertNotNull("action not found", (Object) returnActionDefinition);
        assertEquals("getAction error:", t3.action0_Id, returnActionDefinition.getId());

    }

    /**
     *  Test testGetActions()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .testGetActions(List<Action_Id>) method
     */
    @Test
    public void testGetActions() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t4 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t4");

        createTestActions(t4.action0_Id, t4.action0_Name, t4.action0_Descr, 1, t4.object0, t4.namespaceName);
        createTestActions(t4.action1_Id, t4.action1_Name, t4.action1_Descr, 1, t4.object1, t4.namespaceName);
        createTestActions(t4.action2_Id, t4.action2_Name, t4.action2_Descr, 1, t4.object2, t4.namespaceName);
        createTestActions(t4.action3_Id, t4.action3_Name, t4.action3_Descr, 1, t4.object3, t4.namespaceName);
        List<String> actionIds = Arrays.asList(t4.action0_Id, t4.action1_Id, t4.action2_Id, t4.action3_Id);

        // primary statement being tested
        List<ActionDefinition> returnActionDefinitions = ruleManagementServiceImpl.getActions(actionIds);

        assertEquals("incorrect number of actions returned",4,returnActionDefinitions.size());

        // count the returned actions, returnActionDefinitions.size() may reflect nulls for not found
        int actionsFound = 0;
        for( ActionDefinition actionDefinition : returnActionDefinitions ) {
            if(actionIds.contains(actionDefinition.getId())) {
                actionsFound++;
            }
        }

        assertEquals("incorrect number of actions returned",4,actionsFound);
        assertEquals("action not found",t4.action0_Descr, ruleManagementServiceImpl.getAction(t4.action0_Id).getDescription());
        assertEquals("action not found",t4.action1_Descr, ruleManagementServiceImpl.getAction(t4.action1_Id).getDescription());
        assertEquals("action not found",t4.action2_Descr, ruleManagementServiceImpl.getAction(t4.action2_Id).getDescription());
        assertEquals("action not found",t4.action3_Descr, ruleManagementServiceImpl.getAction(t4.action3_Id).getDescription());
    }

    /**
     *  Test testFindActionIds()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .testFindActionIds(QueryByCriteria) method
     */
    @Test
    public void testFindActionIds() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t5 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t5");
        createTestActions(t5.action0_Id, t5.action0_Name, t5.action0_Descr, 1, t5.object0, t5.namespaceName);

        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(equal("name",t5.action0_Name));

        CriteriaLookupServiceImpl criteriaLookupService = new CriteriaLookupServiceImpl();
        criteriaLookupService.setCriteriaLookupDao(new CriteriaLookupDaoProxy());
        ruleManagementServiceImpl.setCriteriaLookupService( criteriaLookupService);

        List<String> actionIds = ruleManagementServiceImpl.findActionIds(builder.build());

        if(!actionIds.contains(t5.action0_Id)){
            fail("actionId not found");
        }
    }
}
