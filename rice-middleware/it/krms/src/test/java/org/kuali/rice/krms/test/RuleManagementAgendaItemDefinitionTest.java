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
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *  Test methods of ruleManagementServiceImpl relating to AgendaItems
 */
public class RuleManagementAgendaItemDefinitionTest extends RuleManagementBaseTest {
    ////
    //// agenda item methods
    ////

    @Test
    public void testCreateAgendaItem() {
        // Context ("ContextId4000", "Namespace4000", "ContextName4000")
        // Agenda  ("AgendaId4000", "AgendaName4000")
        //    AgendaItem ("AI4000")
        //        Rule ( TEST_PREFIX + "RuleId4000"
        AgendaDefinition.Builder agendaBuilder4000 = buildAgenda("4000");

        assertEquals("Expected Context not found","ContextId4000",agendaBuilder4000.getContextId());
        assertEquals("Expected AgendaId not found","AgendaId4000",agendaBuilder4000.getId());

        assertEquals("Expected AgendaItemId not found","AI4000",agendaBuilder4000.getFirstItemId());
        assertEquals("Expected Rule of AgendaItem not found",TEST_PREFIX + "RuleId4000",ruleManagementServiceImpl.getAgendaItem(agendaBuilder4000.getFirstItemId()).getRule().getId());
    }

    @Test
    public void testCreateComplexAgendaItem() {
        // create "Complex" agenda4100
        //  agendaItem4100 ( rule4100)
        //      WhenTrue   agendaItem4101( rule4101 )
        //      WhenFalse  agendaItem4102( rule4102 )
        //      Always     agendaItem4103( rule4103 )
        //  agendaItem4101 ( rule4101 )
        //      Always     agendaItem4105
        //  agendaItem4102 ( rule4102 )
        //      WhenFalse  agendaItem4104
        //      Always     agendaItem4106
        //  agendaItem4103 ( rule4103 )
        //  agendaItem4104 ( rule4104 )
        //  agendaItem4105 ( rule4105 )
        //  agendaItem4106 ( rule4106 )

        AgendaDefinition.Builder agendaBuilder4100 = buildComplexAgenda("41");
        List<AgendaItemDefinition> agendaItems = ruleManagementServiceImpl.getAgendaItemsByContext("ContextId4100");

        assertEquals("Invalid number of agendaItems created", 7, agendaItems.size());
    }


    @Test
    public void testGetAgendaItem() {
        AgendaDefinition.Builder agendaBuilder4200 = buildComplexAgenda("42");

        AgendaItemDefinition agendaItem4200 = ruleManagementServiceImpl.getAgendaItem("AI4200");
        assertEquals("Invalid AgendaItem value","AI4200",agendaItem4200.getId());
        assertEquals("Invalid AgendaItem value","AgendaId4200",agendaItem4200.getAgendaId());
        assertEquals("Invalid AgendaItem value","AI4203",agendaItem4200.getAlwaysId());
        assertEquals("Invalid AgendaItem value","AI4203",agendaItem4200.getAlways().getId());
        assertEquals("Invalid AgendaItem value",TEST_PREFIX + "RuleId4200",agendaItem4200.getRuleId());
        assertEquals("Invalid AgendaItem value","AI4201",agendaItem4200.getWhenTrue().getId());
        assertEquals("Invalid AgendaItem value","AI4201",agendaItem4200.getWhenTrueId());
        assertEquals("Invalid AgendaItem value","AI4202",agendaItem4200.getWhenFalse().getId());
        assertEquals("Invalid AgendaItem value","AI4202",agendaItem4200.getWhenFalseId());

        // check rule information populated into agendaItem
        RuleDefinition rule4200 = agendaItem4200.getRule();
        assertEquals("Invalid RuleId found for agendaItem",TEST_PREFIX + "RuleId4200",rule4200.getId());
        assertEquals("Invalid RuleId found for agendaItem",TEST_PREFIX + "RuleId4200Name",rule4200.getName());
        assertEquals("Invalid RuleId found for agendaItem","P4200_simple_proposition",rule4200.getProposition().getDescription());
        assertEquals("Invalid RuleId found for agendaItem","S",rule4200.getProposition().getPropositionTypeCode());

        // check agendaItem tree count of items
        List<AgendaItemDefinition> agendaItems = ruleManagementServiceImpl.getAgendaItemsByContext("ContextId4200");
        assertEquals("Invalid number of agendaItems created", 7, agendaItems.size());

        // look for agendaItem which does not exist
        try {
             AgendaItemDefinition junkAgendaItem = ruleManagementServiceImpl.getAgendaItem("junk");
             fail("should have thown a NullPointerException");
        } catch (Exception e) {
            // .NullPointerException    RuleManagementServiceImpl.getAgendaItem(RuleManagementServiceImpl.java:
        }
    }

    @Test
    public void testGetAgendaItemsByType() {
        // get agendaItems for all agendas of this type
        // these complex agendas are both of type "AGENDA4300"
        // each complex agenda has 7 agendaItems
        buildComplexAgenda("Namespace4300","AGENDA4300","43");
        buildComplexAgenda("Namespace4400","AGENDA4300","44");

        assertEquals("Incorrect number of agendaItems found",14,ruleManagementServiceImpl.getAgendaItemsByType("AGENDA4300").size());
    }

    @Test
    public void testGetAgendaItemsByContext() {
        // get agendaItems for all agendas with this Context
        // each complex agenda has 7 agendaItems
        // each complex agenda has a unique Context
        buildComplexAgenda("45");
        buildComplexAgenda("46");

        assertEquals("Incorrect number of agendaItems returned",7,ruleManagementServiceImpl.getAgendaItemsByContext("ContextId4500").size());
        assertEquals("No agendaItems should have been returned",0,ruleManagementServiceImpl.getAgendaItemsByContext("junk").size());
    }

    @Test
    public void testGetAgendaItemsByTypeAndContext() {
        // get agendaItems for all agendas of this type with this Context
        // each complex agenda has 7 agendaItems
        // complex agendas are of type "AGENDAxx00"
        // each complex agenda has a unique Context
        buildComplexAgenda("Namespace4700","AGENDA4700","47");
        buildComplexAgenda("Namespace4800","AGENDA4800","48");

        assertEquals("Incorrect number of agendaItems returned",7,ruleManagementServiceImpl.getAgendaItemsByTypeAndContext("AGENDA4800","ContextId4800").size());
        assertEquals("Incorrect number of agendaItems returned",0,ruleManagementServiceImpl.getAgendaItemsByTypeAndContext("AGENDA4800","ContextId4700").size());
        assertEquals("Incorrect number of agendaItems returned",0,ruleManagementServiceImpl.getAgendaItemsByTypeAndContext("AGENDA4700","ContextId4800").size());
        assertEquals("Incorrect number of agendaItems returned",7,ruleManagementServiceImpl.getAgendaItemsByTypeAndContext("AGENDA4700","ContextId4700").size());
    }
    @Test
    public void testDeleteAgendaItem() {
        // each complex agenda has 7 agendaItems
        AgendaDefinition.Builder agendaBuilder4900 = buildComplexAgenda("49");
        // check the number created before delete
        List<AgendaItemDefinition> agendaItems = ruleManagementServiceImpl.getAgendaItemsByContext("ContextId4900");
        assertEquals("Invalid number of agendaItems created", 7, agendaItems.size());

        // delete one of the seven agendaItems
        ruleManagementServiceImpl.deleteAgendaItem("AI4900");

        // check agendaItem tree count of items
        agendaItems = ruleManagementServiceImpl.getAgendaItemsByContext("ContextId4900");
        assertEquals("Invalid number of agendaItems created", 6, agendaItems.size());

        // look for agendaItem which does not exist
        try {
            AgendaItemDefinition junkAgendaItem = ruleManagementServiceImpl.getAgendaItem("junk");
            fail("should have thown a NullPointerException");
        } catch (Exception e) {
            // .NullPointerException    RuleManagementServiceImpl.getAgendaItem(RuleManagementServiceImpl.java:
        }
    }

    @Test
    public void testUpdateAgendaItem() {
        // default "Complex" agenda4A00
        //  agendaItem4A00 ( rule4A00)
        //      WhenTrue   agendaItem4A01( rule4A01 )
        //      WhenFalse  agendaItem4A02( rule4A02 )
        //      Always     agendaItem4A03( rule4A03 )
        //  agendaItem4A01 ( rule4A01 )
        //      Always     agendaItem4A05
        //  agendaItem4A02 ( rule4A02 )
        //      WhenFalse  agendaItem4A04
        //      Always     agendaItem4A06
        //  agendaItem4A03 ( rule4A03 )
        //  agendaItem4A04 ( rule4A04 )
        //  agendaItem4A05 ( rule4A05 )
        //  agendaItem4A06 ( rule4A06
        AgendaDefinition.Builder agendaBuilder4A00 = buildComplexAgenda("4A");

        // validate default attributes before update
        AgendaItemDefinition agendaItem4A00 = ruleManagementServiceImpl.getAgendaItem("AI4A00");
        assertEquals("Invalid AgendaItem value","AI4A00",agendaItem4A00.getId());
        assertEquals("Invalid AgendaItem value","AgendaId4A00",agendaItem4A00.getAgendaId());
        assertEquals("Invalid AgendaItem value","AI4A03",agendaItem4A00.getAlwaysId());
        assertEquals("Invalid AgendaItem value","AI4A03",agendaItem4A00.getAlways().getId());
        assertEquals("Invalid AgendaItem value",TEST_PREFIX + "RuleId4A00",agendaItem4A00.getRuleId());
        assertEquals("Invalid AgendaItem value","AI4A01",agendaItem4A00.getWhenTrue().getId());
        assertEquals("Invalid AgendaItem value","AI4A01",agendaItem4A00.getWhenTrueId());
        assertEquals("Invalid AgendaItem value","AI4A02",agendaItem4A00.getWhenFalse().getId());
        assertEquals("Invalid AgendaItem value","AI4A02",agendaItem4A00.getWhenFalseId());

        // update some of the agendaItem attributes  ( reverse when true and false and unset always
        AgendaItemDefinition.Builder agendaItemBuilder4A00 = AgendaItemDefinition.Builder.create(agendaItem4A00);
        agendaItemBuilder4A00.setWhenFalse(AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem("AI4A01")));
        agendaItemBuilder4A00.setWhenFalseId("AI4A01");
        agendaItemBuilder4A00.setWhenTrue(AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem("AI4A02")));
        agendaItemBuilder4A00.setWhenTrueId("AI4A02");
        agendaItemBuilder4A00.setAlways(null);
        agendaItemBuilder4A00.setAlwaysId(null);
        ruleManagementServiceImpl.updateAgendaItem(agendaItemBuilder4A00.build());

        // check the update
        agendaItem4A00 = ruleManagementServiceImpl.getAgendaItem("AI4A00");
        assertEquals("Invalid AgendaItem value","AI4A00",agendaItem4A00.getId());
        assertEquals("Invalid AgendaItem value","AgendaId4A00",agendaItem4A00.getAgendaId());
        assertEquals("Invalid AgendaItem value",null,agendaItem4A00.getAlwaysId());
        assertEquals("Invalid AgendaItem value",null,agendaItem4A00.getAlways());
        assertEquals("Invalid AgendaItem value",TEST_PREFIX + "RuleId4A00",agendaItem4A00.getRuleId());
        assertEquals("Invalid AgendaItem value","AI4A02",agendaItem4A00.getWhenTrue().getId());
        assertEquals("Invalid AgendaItem value","AI4A02",agendaItem4A00.getWhenTrueId());
        assertEquals("Invalid AgendaItem value","AI4A01",agendaItem4A00.getWhenFalse().getId());
        assertEquals("Invalid AgendaItem value","AI4A01",agendaItem4A00.getWhenFalseId());

        // update some of the agendaItem attributes  ( reverse when true and false and unset always
        agendaItem4A00 = ruleManagementServiceImpl.getAgendaItem("AI4A00");
        agendaItemBuilder4A00 = AgendaItemDefinition.Builder.create(agendaItem4A00);
        agendaItemBuilder4A00.setWhenFalseId(null);
        agendaItemBuilder4A00.setWhenTrueId(null);
        agendaItemBuilder4A00.setAlwaysId(null);
        ruleManagementServiceImpl.updateAgendaItem(agendaItemBuilder4A00.build());
        // check the update  ( should be no change - clearing Ids should not effect agendaItem
        agendaItem4A00 = ruleManagementServiceImpl.getAgendaItem("AI4A00");
        assertEquals("Invalid AgendaItem value",null,agendaItem4A00.getAlwaysId());
        assertEquals("Invalid AgendaItem value",null,agendaItem4A00.getAlways());
        assertEquals("Invalid AgendaItem value","AI4A02",agendaItem4A00.getWhenTrue().getId());
        assertEquals("Invalid AgendaItem value","AI4A02",agendaItem4A00.getWhenTrueId());
        assertEquals("Invalid AgendaItem value","AI4A01",agendaItem4A00.getWhenFalse().getId());
        assertEquals("Invalid AgendaItem value","AI4A01",agendaItem4A00.getWhenFalseId());

        // update some of the agendaItem attributes  ( unset when true false and always
        agendaItem4A00 = ruleManagementServiceImpl.getAgendaItem("AI4A00");
        agendaItemBuilder4A00 = AgendaItemDefinition.Builder.create(agendaItem4A00);
        agendaItemBuilder4A00.setWhenFalse(null);
        agendaItemBuilder4A00.setWhenTrue(null);
        agendaItemBuilder4A00.setAlways(null);
        ruleManagementServiceImpl.updateAgendaItem(agendaItemBuilder4A00.build());
        // check the update  ( should have removed when true and false
        agendaItem4A00 = ruleManagementServiceImpl.getAgendaItem("AI4A00");
        assertEquals("Invalid AgendaItem value",null,agendaItem4A00.getAlwaysId());
        assertEquals("Invalid AgendaItem value",null,agendaItem4A00.getAlways());
        assertEquals("Invalid AgendaItem value",null,agendaItem4A00.getWhenTrue());
        assertEquals("Invalid AgendaItem value",null,agendaItem4A00.getWhenTrueId());
        assertEquals("Invalid AgendaItem value",null,agendaItem4A00.getWhenFalse());
        assertEquals("Invalid AgendaItem value",null,agendaItem4A00.getWhenFalseId());
    }
}
