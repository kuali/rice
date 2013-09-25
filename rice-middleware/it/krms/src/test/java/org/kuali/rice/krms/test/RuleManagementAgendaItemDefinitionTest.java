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
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *   RuleManagementAgendaItemDefinitionTest is to test the methods of ruleManagementServiceImpl relating to krms AgendaItems
 *
 *   Each test focuses on one of the methods.
 */
public class RuleManagementAgendaItemDefinitionTest extends RuleManagementBaseTest {

    @Override
    @Before
    public void setClassDiscriminator() {
        // set a unique discriminator for test objects of this class
        CLASS_DISCRIMINATOR = "RMAIDT";
    }

    /**
     *  Test testCreateAgendaItem()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .createAgendaItem(AgendaItemDefinition) method
     */
    @Test
    public void testCreateAgendaItem() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t0 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t0");

        // buildAgenda uses the ruleManagementServiceImpl.createAgendaItem method
        AgendaDefinition.Builder agendaBuilder = buildAgenda(t0.object0);

        assertEquals("Expected Context not found",t0.contextId,agendaBuilder.getContextId() + " broken for Jenkins changeset test.");
        assertEquals("Expected AgendaId not found",t0.agenda_Id,agendaBuilder.getId());

        assertEquals("Expected AgendaItemId not found",t0.agendaItem_0_Id,agendaBuilder.getFirstItemId());
        assertEquals("Expected Rule of AgendaItem not found",t0.rule_0_Id,ruleManagementServiceImpl.getAgendaItem(agendaBuilder.getFirstItemId()).getRule().getId());
    }

    /**
     *  Test testCreateComplexAgendaItem()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .createAgendaItem(AgendaItemDefinition) method
     *  with a complex definition
     */
    @Test
    public void testCreateComplexAgendaItem() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t1 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t1");

        // buildComplexAgenda uses the ruleManagementServiceImpl.createAgendaItem method
        buildComplexAgenda(t1);
        List<AgendaItemDefinition> agendaItems = ruleManagementServiceImpl.getAgendaItemsByContext(t1.contextId);

        // the complex agendas created should have 7 agendaItems associated with it
        assertEquals("Invalid number of agendaItems created", 7, agendaItems.size());
    }

    /**
     *  Test testGetAgendaItem()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .getAgendaItem(AgendaItemId) method
     */
    @Test
    public void testGetAgendaItem() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t2 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t2");

        AgendaDefinition.Builder agendaBuilder = buildComplexAgenda(t2);

        AgendaItemDefinition agendaItem = ruleManagementServiceImpl.getAgendaItem(t2.agendaItem_0_Id);
        assertEquals("Invalid AgendaItem value",t2.agendaItem_0_Id,agendaItem.getId());
        assertEquals("Invalid AgendaItem value",t2.agenda_Id,agendaItem.getAgendaId());
        assertEquals("Invalid AgendaItem value",t2.agendaItem_3_Id,agendaItem.getAlwaysId());
        assertEquals("Invalid AgendaItem value",t2.agendaItem_3_Id,agendaItem.getAlways().getId());
        assertEquals("Invalid AgendaItem value",t2.rule_0_Id,agendaItem.getRuleId());
        assertEquals("Invalid AgendaItem value",t2.agendaItem_1_Id,agendaItem.getWhenTrue().getId());
        assertEquals("Invalid AgendaItem value",t2.agendaItem_1_Id,agendaItem.getWhenTrueId());
        assertEquals("Invalid AgendaItem value",t2.agendaItem_2_Id,agendaItem.getWhenFalse().getId());
        assertEquals("Invalid AgendaItem value",t2.agendaItem_2_Id,agendaItem.getWhenFalseId());

        // check rule information populated into agendaItem
        RuleDefinition ruleDefinition = agendaItem.getRule();
        assertEquals("Invalid RuleId found for agendaItem",t2.rule_0_Id,ruleDefinition.getId());
        assertEquals("Invalid RuleId found for agendaItem",t2.rule_0_Name,ruleDefinition.getName());
        assertEquals("Invalid RuleId found for agendaItem",t2.proposition_0_Descr,ruleDefinition.getProposition().getDescription());
        assertEquals("Invalid RuleId found for agendaItem","S",ruleDefinition.getProposition().getPropositionTypeCode());

        // check agendaItem count of associated items
        List<AgendaItemDefinition> agendaItems = ruleManagementServiceImpl.getAgendaItemsByContext(t2.contextId);
        assertEquals("Invalid number of agendaItems created", 7, agendaItems.size());

        // look for agendaItem which should not exist
        try {
             AgendaItemDefinition junkAgendaItem = ruleManagementServiceImpl.getAgendaItem("junk");
             fail("should have thrown a NullPointerException");
        } catch (NullPointerException e) {
            // throws NullPointerException RuleManagementServiceImpl.getAgendaItem(RuleManagementServiceImpl.java:
        }
    }

    /**
     *  Test testGetAgendaItemsByType()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .getAgendaItemsByType(NamespaceType) method
     */
    @Test
    public void testGetAgendaItemsByType() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t3 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t3");
        buildComplexAgenda(t3.namespaceName, t3.namespaceType, t3);

        // get a second set of object names for the creation of second agenda
        RuleManagementBaseTestObjectNames t4 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t4");

        // create second agenda with same namespace type as the first agenda
        buildComplexAgenda(t4.namespaceName, t3.namespaceType, t4);

        // get agendaItems for all agendas of this type
        // these complex agendas are both of type namespaceType
        // each complex agenda has 7 agendaItems
        assertEquals("Incorrect number of agendaItems found",14,ruleManagementServiceImpl.getAgendaItemsByType(t3.namespaceType).size());
    }

    /**
     *  Test testGetAgendaItemsByContext()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .getAgendaItemsByContext(ContextId) method
     */
    @Test
    public void testGetAgendaItemsByContext() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t5 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t5");
        buildComplexAgenda(t5);

        // get a second set of object names for the creation of second agenda
        RuleManagementBaseTestObjectNames t6 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t6");
        buildComplexAgenda(t6);

        // get agendaItems for all agendas with this Context
        // each complex agenda has 7 agendaItems
        // each complex agenda has a unique Context
        assertEquals("Incorrect number of agendaItems returned", 7, ruleManagementServiceImpl.getAgendaItemsByContext(t5.contextId).size());
        assertEquals("No agendaItems should have been returned",0,ruleManagementServiceImpl.getAgendaItemsByContext("junk").size());
    }

    /**
     *  Test testGetAgendaItemsByTypeAndContext()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .getAgendaItemsByTypeAndContext(NamespaceType, ContextId) method
     */
    @Test
    public void testGetAgendaItemsByTypeAndContext() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t7 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t7");
        buildComplexAgenda(t7.namespaceName, t7.namespaceType, t7);

        // get a second set of object names for the creation of second agenda
        RuleManagementBaseTestObjectNames t8 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t8");
        buildComplexAgenda(t8.namespaceName, t7.namespaceType, t8);

        // get agendaItems for all agendas of this type with this Context
        // each complex agenda has 7 agendaItems
        // complex agendas are of type "firstNamespaceType"
        // each complex agenda has a unique Context
        assertEquals("Incorrect number of agendaItems returned",7,ruleManagementServiceImpl.getAgendaItemsByTypeAndContext(t7.namespaceType,t8.contextId).size());
        assertEquals("Incorrect number of agendaItems returned",7,ruleManagementServiceImpl.getAgendaItemsByTypeAndContext(t7.namespaceType,t7.contextId).size());
        assertEquals("Incorrect number of agendaItems returned",0,ruleManagementServiceImpl.getAgendaItemsByTypeAndContext("badType",t7.contextId).size());
        assertEquals("Incorrect number of agendaItems returned",0,ruleManagementServiceImpl.getAgendaItemsByTypeAndContext(t7.namespaceType,"badContext").size());

        try {
            ruleManagementServiceImpl.getAgendaItemsByTypeAndContext(null,t7.contextId);
            fail("Should have thrown RiceIllegalArgumentException: type ID is null or blank");
        } catch (RiceIllegalArgumentException e) {
            // throws RiceIllegalArgumentException: type ID is null or blank
        }

        try {
            ruleManagementServiceImpl.getAgendaItemsByTypeAndContext("    ",t7.contextId);
            fail("Should have thrown RiceIllegalArgumentException: type ID is null or blank");
        } catch (RiceIllegalArgumentException e) {
            // throws RiceIllegalArgumentException: type ID is null or blank
        }

        try {
            ruleManagementServiceImpl.getAgendaItemsByTypeAndContext(t7.namespaceType,null);
            fail("Should have thrown RiceIllegalArgumentException: context ID is null or blank");
        } catch (RiceIllegalArgumentException e) {
            // throws RiceIllegalArgumentException: context ID is null or blank
        }
    }

    /**
     *  Test testDeleteAgendaItem()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .deleteAgendaItem(AgendaItemId) method
     */
    @Test
    public void testDeleteAgendaItem() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t9 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t9");

        // each complex agenda has 7 agendaItems
        AgendaDefinition.Builder agendaBuilder4900 = buildComplexAgenda(t9);
        // check the number created before delete
        List<AgendaItemDefinition> agendaItems = ruleManagementServiceImpl.getAgendaItemsByContext(t9.contextId);
        assertEquals("Invalid number of agendaItems created", 7, agendaItems.size());

        // delete one of the seven agendaItems
        ruleManagementServiceImpl.deleteAgendaItem(t9.agendaItem_0_Id);

        // check agendaItem count of items for Agenda, one should now be deleted
        agendaItems = ruleManagementServiceImpl.getAgendaItemsByContext(t9.contextId);
        assertEquals("Invalid number of agendaItems created", 6, agendaItems.size());

        // look for a agendaItem which does not exist
        try {
            AgendaItemDefinition junkAgendaItem = ruleManagementServiceImpl.getAgendaItem("junk");
            fail("should have thrown a NullPointerException");
        } catch (NullPointerException e) {
            // throws NullPointerException RuleManagementServiceImpl.getAgendaItem(RuleManagementServiceImpl.java:
        }
    }

    /**
     *  Test testUpdateAgendaItem()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .updateAgendaItem(AgendaItemDefinition) method
     */
    @Test
    public void testUpdateAgendaItem() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t10 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t10");

        AgendaDefinition.Builder agendaBuilder = buildComplexAgenda(t10);

        // validate default attributes before update
        AgendaItemDefinition agendaItem = ruleManagementServiceImpl.getAgendaItem(t10.agendaItem_0_Id);
        assertEquals("Invalid AgendaItem value",t10.agendaItem_0_Id,agendaItem.getId());
        assertEquals("Invalid AgendaItem value",t10.agenda_Id,agendaItem.getAgendaId());
        assertEquals("Invalid AgendaItem value",t10.agendaItem_3_Id,agendaItem.getAlwaysId());
        assertEquals("Invalid AgendaItem value",t10.agendaItem_3_Id,agendaItem.getAlways().getId());
        assertEquals("Invalid AgendaItem value",t10.rule_0_Id,agendaItem.getRuleId());
        assertEquals("Invalid AgendaItem value",t10.agendaItem_1_Id,agendaItem.getWhenTrue().getId());
        assertEquals("Invalid AgendaItem value",t10.agendaItem_1_Id,agendaItem.getWhenTrueId());
        assertEquals("Invalid AgendaItem value",t10.agendaItem_2_Id,agendaItem.getWhenFalse().getId());
        assertEquals("Invalid AgendaItem value",t10.agendaItem_2_Id,agendaItem.getWhenFalseId());

        // update some of the agendaItem attributes  ( reverse whenTrue and whenFalse values and unset always
        AgendaItemDefinition.Builder agendaItemBuilder = AgendaItemDefinition.Builder.create(agendaItem);
        agendaItemBuilder.setWhenFalse(AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem(t10.agendaItem_1_Id)));
        agendaItemBuilder.setWhenFalseId(t10.agendaItem_1_Id);
        agendaItemBuilder.setWhenTrue(AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem(t10.agendaItem_2_Id)));
        agendaItemBuilder.setWhenTrueId(t10.agendaItem_2_Id);
        agendaItemBuilder.setAlways(null);
        agendaItemBuilder.setAlwaysId(null);
        ruleManagementServiceImpl.updateAgendaItem(agendaItemBuilder.build());

        // check the update
        agendaItem = ruleManagementServiceImpl.getAgendaItem(t10.agendaItem_0_Id);
        assertEquals("Invalid AgendaItem value",t10.agendaItem_0_Id,agendaItem.getId());
        assertEquals("Invalid AgendaItem value",t10.agenda_Id,agendaItem.getAgendaId());
        assertEquals("Invalid AgendaItem value",null,agendaItem.getAlwaysId());
        assertEquals("Invalid AgendaItem value",null,agendaItem.getAlways());
        assertEquals("Invalid AgendaItem value",t10.rule_0_Id,agendaItem.getRuleId());
        assertEquals("Invalid AgendaItem value",t10.agendaItem_2_Id,agendaItem.getWhenTrue().getId());
        assertEquals("Invalid AgendaItem value",t10.agendaItem_2_Id,agendaItem.getWhenTrueId());
        assertEquals("Invalid AgendaItem value",t10.agendaItem_1_Id,agendaItem.getWhenFalse().getId());
        assertEquals("Invalid AgendaItem value",t10.agendaItem_1_Id,agendaItem.getWhenFalseId());

        // update some of the agendaItem attributes
        agendaItem = ruleManagementServiceImpl.getAgendaItem(t10.agendaItem_1_Id);
        agendaItemBuilder = AgendaItemDefinition.Builder.create(agendaItem);
        agendaItemBuilder.setWhenFalseId(null);
        agendaItemBuilder.setWhenTrueId(null);
        agendaItemBuilder.setAlwaysId(null);
        ruleManagementServiceImpl.updateAgendaItem(agendaItemBuilder.build());
        // check the update  ( should be no change - clearing Ids should not effect agendaItem
        agendaItem = ruleManagementServiceImpl.getAgendaItem(t10.agendaItem_0_Id);
        assertEquals("Invalid AgendaItem value",null,agendaItem.getAlwaysId());
        assertEquals("Invalid AgendaItem value",null,agendaItem.getAlways());
        assertEquals("Invalid AgendaItem value",t10.agendaItem_2_Id,agendaItem.getWhenTrue().getId());
        assertEquals("Invalid AgendaItem value",t10.agendaItem_2_Id,agendaItem.getWhenTrueId());
        assertEquals("Invalid AgendaItem value",t10.agendaItem_1_Id,agendaItem.getWhenFalse().getId());
        assertEquals("Invalid AgendaItem value",t10.agendaItem_1_Id,agendaItem.getWhenFalseId());

        // update some of the agendaItem attributes  ( unset when true false and always
        agendaItem = ruleManagementServiceImpl.getAgendaItem(t10.agendaItem_0_Id);
        agendaItemBuilder = AgendaItemDefinition.Builder.create(agendaItem);
        agendaItemBuilder.setWhenFalse(null);
        agendaItemBuilder.setWhenTrue(null);
        agendaItemBuilder.setAlways(null);
        ruleManagementServiceImpl.updateAgendaItem(agendaItemBuilder.build());
        // check the update  ( should have removed when true and false
        agendaItem = ruleManagementServiceImpl.getAgendaItem(t10.agendaItem_0_Id);
        assertEquals("Invalid AgendaItem value",null,agendaItem.getAlwaysId());
        assertEquals("Invalid AgendaItem value",null,agendaItem.getAlways());
        assertEquals("Invalid AgendaItem value",null,agendaItem.getWhenTrue());
        assertEquals("Invalid AgendaItem value",null,agendaItem.getWhenTrueId());
        assertEquals("Invalid AgendaItem value",null,agendaItem.getWhenFalse());
        assertEquals("Invalid AgendaItem value",null,agendaItem.getWhenFalseId());
    }
}
