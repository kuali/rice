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

import org.junit.Assert;
import org.junit.Test;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.krad.criteria.CriteriaLookupDaoProxy;
import org.kuali.rice.krad.criteria.CriteriaLookupServiceImpl;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

/**
 *   Test methods of ruleManagementServiceImpl relating to Actions
 */
public class RuleManagementActionDefinitionTest extends RuleManagementBaseTest {
    static final String ACTION1_ID = "ActionId1";
    static final String ACTION1_NAME = "Action1Name";
    static final String ACTION2_ID = "ActionId2";
    static final String ACTION2_NAME = "Action2Name";
    static final String ACTION3_ID = "ActionId3";
    static final String ACTION3_NAME = "Action3Name";
    static final String ACTION4_ID = "ActionId4";
    static final String ACTION4_NAME = "Action4Name";

    /*
    ////
    //// action methods
    ////
    */

    @Test
    public void testCreateAction() {
        KrmsTypeDefinition krmsTypeDefinition = createKrmsActionTypeDefinition(NAMESPACE1);
        RuleDefinition ruleDefintion = createTestRule(NAMESPACE1, "0001");

        ActionDefinition actionDefinition = ActionDefinition.Builder.create(ACTION1_ID,ACTION1_NAME,
                NAMESPACE1,krmsTypeDefinition.getId(),ruleDefintion.getId(),1).build();

        assertNull("action should not be in database", ruleManagementServiceImpl.getAction(ACTION1_ID));

        // primary statement for test
        actionDefinition =  ruleManagementServiceImpl.createAction(actionDefinition);

        ActionDefinition returnActionDefinition = ruleManagementServiceImpl.getAction(actionDefinition.getId());

        assertNotNull("created action not found", (Object) returnActionDefinition);
        assertEquals("create action error:", ACTION1_ID, returnActionDefinition.getId());
    }

    @Test
    public void testUpdateAction() {
        KrmsTypeDefinition krmsTypeDefinition = createKrmsActionTypeDefinition(NAMESPACE1);
        RuleDefinition ruleDefintion = createTestRule(NAMESPACE1, "0002");

        ActionDefinition actionDefinition = ActionDefinition.Builder.create(ACTION2_ID,ACTION2_NAME,
                NAMESPACE1,krmsTypeDefinition.getId(),ruleDefintion.getId(),1).build();

        assertNull("action should not be in database",ruleManagementServiceImpl.getAction(ACTION2_ID ));

        actionDefinition =  ruleManagementServiceImpl.createAction(actionDefinition);

        ActionDefinition returnActionDefinition = ruleManagementServiceImpl.getAction(actionDefinition.getId());
        ActionDefinition.Builder builder = ActionDefinition.Builder.create(returnActionDefinition);
        builder.setDescription(ACTION2_NAME);

        // primary statement for test
        ruleManagementServiceImpl.updateAction(builder.build());

        returnActionDefinition = ruleManagementServiceImpl.getAction(actionDefinition.getId());

        assertNotNull("action not found", (Object) returnActionDefinition);
        assertEquals("update action error:", ACTION2_NAME, returnActionDefinition.getDescription());
    }

    /*
    @Override
    public void updateAction(ActionDefinition actionDefinition) throws RiceIllegalArgumentException {
        actionBoService.updateAction(actionDefinition);
    }
    */

    @Test
    public void testDeleteAction() {
        KrmsTypeDefinition krmsTypeDefinition = createKrmsActionTypeDefinition(NAMESPACE1);
        RuleDefinition ruleDefintion = createTestRule(NAMESPACE1, "0003");

        ActionDefinition actionDefinition = ActionDefinition.Builder.create(ACTION3_ID,ACTION3_NAME,
                NAMESPACE1,krmsTypeDefinition.getId(),ruleDefintion.getId(),1).build();

        assertNull("action should not be in database",ruleManagementServiceImpl.getAction(ACTION3_ID ));

        // primary statement for test
        actionDefinition =  ruleManagementServiceImpl.createAction(actionDefinition);

        actionDefinition = ruleManagementServiceImpl.getAction(actionDefinition.getId());
        assertNotNull("action not found", (Object) ruleManagementServiceImpl.getAction(actionDefinition.getId()));

        try {
            ruleManagementServiceImpl.deleteAction(ACTION3_ID);
            fail("should fail deleteAction not implemented");
        }   catch (Exception e) {
            // RiceIllegalArgumentException ("not implemented yet because not supported by the bo service");
        }

        actionDefinition = ruleManagementServiceImpl.getAction(actionDefinition.getId());
        assertNotNull("action not found", (Object) actionDefinition);
    }

    @Test
    public void testGetAction() {
        KrmsTypeDefinition krmsTypeDefinition = createKrmsActionTypeDefinition(NAMESPACE1);
        RuleDefinition ruleDefintion = createTestRule(NAMESPACE1,"0004");

        ActionDefinition actionDefinition = ActionDefinition.Builder.create(ACTION4_ID,ACTION4_NAME,
                NAMESPACE1,krmsTypeDefinition.getId(),ruleDefintion.getId(),1).build();

        assertNull("action should not be in database", ruleManagementServiceImpl.getAction(ACTION4_ID));
        actionDefinition =  ruleManagementServiceImpl.createAction(actionDefinition);

        // primary statement being tested
        ActionDefinition returnActionDefinition = ruleManagementServiceImpl.getAction(actionDefinition.getId());

        assertNotNull("action not found", (Object) returnActionDefinition);
        assertEquals("getAction error:", ACTION4_ID, returnActionDefinition.getId());

    }

    @Test
    public void testGetActions() {
        createTestActions("Action1001", "Action1001Name", "Action1001Desc", 1, "1001", NAMESPACE1);
        createTestActions("Action1002", "Action1002Name", "Action1002Desc", 1, "1002", NAMESPACE1);
        createTestActions("Action1003", "Action1003Name", "Action1003Desc", 1, "1003", NAMESPACE1);
        createTestActions("Action1004", "Action1004Name", "Action1004Desc", 1, "1004", NAMESPACE1);

        List<String> actionIds = Arrays.asList("Action1001", "Action1002", "Action1003", "Action1004");
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
        assertEquals("action not found","Action1001Desc", ruleManagementServiceImpl.getAction("Action1001").getDescription());
        assertEquals("action not found","Action1002Desc", ruleManagementServiceImpl.getAction("Action1002").getDescription());
        assertEquals("action not found","Action1003Desc", ruleManagementServiceImpl.getAction("Action1003").getDescription());
        assertEquals("action not found","Action1004Desc", ruleManagementServiceImpl.getAction("Action1004").getDescription());
    }

    @Test
    public void testFindActionIds() {
        createTestActions("Action1011", "Action1011Name", "Action1011Desc", 1, "1011", NAMESPACE1);

        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(equal("name","Action1011Name"));

        CriteriaLookupServiceImpl criteriaLookupService = new CriteriaLookupServiceImpl();
        criteriaLookupService.setCriteriaLookupDao(new CriteriaLookupDaoProxy());
        ruleManagementServiceImpl.setCriteriaLookupService( criteriaLookupService);

        List<String> actionIds = ruleManagementServiceImpl.findActionIds(builder.build());

        if(!actionIds.contains("Action1011")){
            fail("actionId not found");
        }
    }



}
