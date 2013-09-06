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
import org.kuali.rice.krms.api.repository.agenda.AgendaTreeDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaTreeEntryDefinitionContract;
import org.kuali.rice.krms.api.repository.agenda.AgendaTreeRuleEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;


/**
 *
 */
public class RuleManagementAgendaTreeDefinitionTest  extends RuleManagementBaseTest {
    ////
    //// AgendaTreeDefinition tests
    ////

    @Test
    public void testGetAgendaTree() {
        // Build this Agenda to generate tree from
        // "Complex" agenda7000
        //  agendaItem7000 ( rule7000)
        //      WhenTrue   agendaItem7001( rule7001 )
        //      WhenFalse  agendaItem7002( rule7002 )
        //      Always     agendaItem7003( rule7003 )
        //  agendaItem7001 ( rule7001 )
        //      Always     agendaItem7005
        //  agendaItem7002 ( rule7002 )
        //      WhenFalse  agendaItem7004
        //      Always     agendaItem7006
        //  agendaItem7003 ( rule7003 )
        //  agendaItem7004 ( rule7004 )
        //  agendaItem7005 ( rule7005 )
        //  agendaItem7006 ( rule7006 )

        /*  This is the tree which should be generated from complex Agenda7000
        AgendaTreeDefinition agendaTreeDefinition
            agendaId ="AgendaId7000"
            entries = ArrayList size = 2
            [0] = AgendaTreeRuleEntry
                agendaItemId = "AI7000"
                ruleId = "RuleManagementITRuleId7000"
                ifTrue = AgendaTreeDefinition
                    agendaId = "AgendaId7000"
                    entries = ArrayList size = 2
                        [0] = AgendaTreeRuleEntry
                            agendaItemId = "AI7001"
                            ruleId = "RuleManagementITRuleId7001"
                            ifTrue = null
                            ifFalse = null
                        [1] = AgendaTreeRuleEntry
                            agendaItemId = "AI7005"
                            ruleId = "RuleManagementITRuleId7005"
                            ifTrue = null
                            ifFalse = null
                ifFalse = AgendaTreeDefinition
                    agendaId = "AgendaId7000"
                    entries = ArrayList size = 2
                        [0] = AgendaTreeRuleEntry
                            agendaItemId = "AI7002"
                            ruleId = "RuleManagementITRuleId7002"
                            ifTrue = null
                            ifFalse = AgendaTreeDefinition
                                agendaId = AgendaId7000"
                                entries = ArrayList  size = 1
                                    [0] = AgendaTreeRuleEntry
                                        agendaItemId = "AI7004"
                                        ruleId = "RuleManagementITRuleId7004"
                                        ifTrue = null
                                        ifFalse = null
                        [1] = AgendaTreeRuleEntry
                            agendaItemId = "AI7006"
                            ruleId = "RuleManagementITRuleId7006"
                            ifTrue = null
                            ifFalse = null
            [1] = AgendaTreeRuleEntry
                agendaItemId = "AI7003"
                ruleId = "RuleManagementITRuleId7003"
                ifTrue = null
                ifFalse = null
         */
        // create the Agenda7000 to test with
        AgendaDefinition.Builder agendaBuilder7000 = buildComplexAgenda("70");

        // Get the AgendaTreeDefinition and drill down a branch to one of the lowest levels for information
        AgendaTreeDefinition agendaTreeDefinition = ruleManagementServiceImpl.getAgendaTree(agendaBuilder7000.getId());
        assertNotNull("Should have returned a AgendaTreeDefinition", agendaTreeDefinition);

        List<AgendaTreeEntryDefinitionContract> agendaTreeRuleEntrys = agendaTreeDefinition.getEntries();
        assertEquals("First level of tree should of had 2 entries",2,agendaTreeRuleEntrys.size());

        // drill down a branch
        AgendaTreeRuleEntry firstLevelFirstEntry = (AgendaTreeRuleEntry) agendaTreeRuleEntrys.get(0);
        AgendaTreeRuleEntry firstLevelSecondEntry = (AgendaTreeRuleEntry) agendaTreeRuleEntrys.get(1);
        AgendaTreeDefinition ifTrueEntry = firstLevelFirstEntry.getIfTrue();
        agendaTreeRuleEntrys = ifTrueEntry.getEntries();
        assertEquals("IfTrue level of first entry of tree should of had 2 entries",2,agendaTreeRuleEntrys.size());
        AgendaTreeRuleEntry agendaTreeRuleEntry = (AgendaTreeRuleEntry) agendaTreeRuleEntrys.get(1);
        assertEquals("Incorrect AgendaItemId found", "AI7005", agendaTreeRuleEntry.getAgendaItemId());

        // drill down to another location
        AgendaTreeDefinition ifFalseEntry = firstLevelFirstEntry.getIfFalse();
        agendaTreeRuleEntrys = ifFalseEntry.getEntries();
        assertEquals("IfFalse level of first entry of tree should of had 2 entries",2,agendaTreeRuleEntrys.size());
        agendaTreeRuleEntry = (AgendaTreeRuleEntry) agendaTreeRuleEntrys.get(0);
        ifFalseEntry = agendaTreeRuleEntry.getIfFalse();
        agendaTreeRuleEntrys = ifFalseEntry.getEntries();
        agendaTreeRuleEntry = (AgendaTreeRuleEntry) agendaTreeRuleEntrys.get(0);
        assertEquals("Incorrect AgendaItemId found", "AI7004", agendaTreeRuleEntry.getAgendaItemId());

        // Test call with blank parameter
        try {
            agendaTreeDefinition = ruleManagementServiceImpl.getAgendaTree(" ");
            fail("Should have thrown RiceIllegalArgumentException: agenda id is null or blank");
        } catch (Exception e) {
            // throws RiceIllegalArgumentException: agenda id is null or blank
        }

        // Test call with null parameter
        try {
            agendaTreeDefinition = ruleManagementServiceImpl.getAgendaTree(null);
            fail("Should have thrown RiceIllegalArgumentException: agenda id is null or blank");
        } catch (Exception e) {
            // throws RiceIllegalArgumentException: agenda id is null or blank
        }

        assertNull("Should have return null",ruleManagementServiceImpl.getAgendaTree("badValueId"));
    }


    @Test
    public void testGetAgendaTrees() {
        // create  Agenda7100 & Agenda7200  to test with
        List<String> agendaIds = new ArrayList<String>();
        AgendaDefinition.Builder agendaBuilder7100 = buildComplexAgenda("71");
        agendaIds.add(agendaBuilder7100.getId());
        AgendaDefinition.Builder agendaBuilder7200 = buildComplexAgenda("72");
        agendaIds.add(agendaBuilder7200.getId());

        List<AgendaTreeDefinition> agendaTreeDefinitions = ruleManagementServiceImpl.getAgendaTrees( agendaIds);
        assertEquals("Two agendaTree definitions should have been return",2,agendaTreeDefinitions.size());
        for (AgendaTreeDefinition agendaTreeDefinition: agendaTreeDefinitions ) {
            if (!agendaIds.contains(agendaTreeDefinition.getAgendaId())) {
                fail("Invalid AgendaTreeDefinition returned");
            }
        }

        assertEquals("No AgendaTreeDefinitions should have been returned",0,ruleManagementServiceImpl.getAgendaTrees( null).size());

        agendaIds = Arrays.asList("badValueId");
        assertEquals("No AgendaTreeDefinitions should have been returned",0,ruleManagementServiceImpl.getAgendaTrees( agendaIds).size());
    }
}
