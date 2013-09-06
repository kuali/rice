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
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.fail;
import static org.junit.Assert.*;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.in;

/**
 *  Test methods of ruleManagementServiceImpl relating to Agendas
 */
public class RuleManagementAgendaTest extends RuleManagementBaseTest {

    ////
    //// agenda
    ////
    @Test
    public void testCreateAgenda() {
        // Context ("ContextId5000", "Namespace5000", "ContextName5000")
        // Agenda  ("AgendaId5000", "AgendaName5000")
        //    AgendaItem ("AI5000")
        //        Rule ( TEST_PREFIX + "RuleId5000"
        AgendaDefinition.Builder agendaBuilder5000 = buildAgenda("5000");

        assertTrue("Created agenda is not active", agendaBuilder5000.isActive());

        assertEquals("Expected Context not found","ContextId5000",agendaBuilder5000.getContextId());
        assertEquals("Expected AgendaId not found","AgendaId5000",agendaBuilder5000.getId());

        assertEquals("Expected AgendaItemId not found","AI5000",agendaBuilder5000.getFirstItemId());
        assertEquals("Expected Rule of AgendaItem not found",TEST_PREFIX + "RuleId5000",ruleManagementServiceImpl.getAgendaItem(agendaBuilder5000.getFirstItemId()).getRule().getId());
    }

    @Test
    public void testGetAgendaByNameAndContextId() {
        AgendaDefinition.Builder agendaBuilder5001 = buildAgenda("5001");
        AgendaDefinition agenda5001 = ruleManagementServiceImpl.getAgendaByNameAndContextId("AgendaName5001","ContextId5001");

        assertEquals("Invalid agendaId name found","AgendaId5001",agenda5001.getId());
        assertEquals("Invalid contextId found","ContextId5001",agenda5001.getContextId());
        assertNull("Agenda typeId should not have been set", agenda5001.getTypeId());
        assertEquals("Incorrect agendaName found","AgendaName5001",agenda5001.getName());
        assertEquals("Invalid agendaFirstItemId found","AI5001",agenda5001.getFirstItemId());

        agenda5001 = ruleManagementServiceImpl.getAgendaByNameAndContextId("AgendaName5001","badContext");
        assertNull("Invalid Context, no agendas should have been found",agenda5001);

        agenda5001 = ruleManagementServiceImpl.getAgendaByNameAndContextId("badName","ContextId5001");
        assertNull("Invalid Name, no agendas should have been found",agenda5001);

        try {
            agenda5001 = ruleManagementServiceImpl.getAgendaByNameAndContextId(null,"ContextId5001");
            fail("Null Name specified for search, should have thrown .RiceIllegalArgumentException: name is blank ");
        } catch (Exception e) {
            // thrown .RiceIllegalArgumentException: name is blank
        }

        try {
            agenda5001 = ruleManagementServiceImpl.getAgendaByNameAndContextId("AgendaName5001",null);
            fail("Null Context specified for search, should have thrown .RiceIllegalArgumentException: contextId is blank");
        } catch (Exception e) {
            // thrown .RiceIllegalArgumentException: contextId is blank
        }
    }

    @Test
    public void testFindCreateAgenda() {
        String namespace =  "Namespace5002";
        // create a context
        ContextDefinition.Builder contextDefinitionBuilder = ContextDefinition.Builder.create(
                namespace, "ContextName5002");
        contextDefinitionBuilder.setId("ContextId5002");
        ContextDefinition contextDefinition = contextDefinitionBuilder.build();
        contextDefinition = ruleManagementServiceImpl.findCreateContext(contextDefinition);

        assertNull("Agenda should not have already existed", ruleManagementServiceImpl.getAgenda("AgendaId5002"));

        // create an agenda
        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(
                "AgendaId5002", "AgendaName5002", null, "ContextId5002");
        AgendaDefinition agenda = agendaBuilder.build();
        agenda = ruleManagementServiceImpl.findCreateAgenda(agenda);

        assertNotNull("Agenda should have been created", ruleManagementServiceImpl.getAgenda("AgendaId5002"));

        // update an agenda using findCreateAgenda - invalid attempt
        // ( cannot change name or context as these are used to uniquely identify agenda for findCreateAgenda
        agendaBuilder = AgendaDefinition.Builder.create(
                "AgendaId5002", "ChangedName5002", null, "ContextId5002");
        agenda = agendaBuilder.build();
        try {
            agenda = ruleManagementServiceImpl.findCreateAgenda(agenda);
            fail( "should have failed with OptimisticLockException: Object has been modified by someone else");
        } catch (Exception e) {
            // thrown OptimisticLockException: Object has been modified by someone else
        }

        // create a new agendaItem to update the agenda with
        AgendaItemDefinition agendaItem = newTestAgendaItemDefinition("AINew5002", "AgendaId5002", null);
        AgendaItemDefinition.Builder itemBuilder = AgendaItemDefinition.Builder.create(agendaItem);
        itemBuilder = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(itemBuilder.build()));

        //  findCreateAgenda with changed agendaFirstItemId
        agendaBuilder = AgendaDefinition.Builder.create("AgendaId5002", "AgendaName5002", null, "ContextId5002");
        agendaBuilder.setFirstItemId(itemBuilder.getId());
        agenda = ruleManagementServiceImpl.findCreateAgenda(agendaBuilder.build());

        assertEquals("Agenda should have been changed by findCreateAgenda","AINew5002",ruleManagementServiceImpl.getAgenda("AgendaId5002").getFirstItemId());
    }


    @Test
    public void testGetAgenda() {
        AgendaDefinition.Builder agendaBuilder = buildAgenda("5003");

        assertEquals("Agenda not found", "AgendaName5003", ruleManagementServiceImpl.getAgenda("AgendaId5003").getName());
    }


    @Test
    public void testGetAgendasByContext() {
        // each buildAgenda has a unique Context
        buildAgenda("5004");
        buildAgenda("5005");

        // set second agendaContextId to same as first
        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda("AgendaId5005"));
        agendaBuilder.setContextId(ruleManagementServiceImpl.getAgenda("AgendaId5004").getContextId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());

        List<AgendaDefinition> agendas = ruleManagementServiceImpl.getAgendasByContext("ContextId5004");
        assertEquals("Incorrect number of Agendas returned",2,agendas.size());

        List<String> agendaIds = Arrays.asList("AgendaId5004", "AgendaId5005");

        // verify expected agendas returned & count the returned agendas
        int agendasFound = 0;
        for( AgendaDefinition agenda : agendas ) {
            if(agendaIds.contains(agenda.getId())) {
                agendasFound++;
            }
        }
        assertEquals("Incorrect results of getAgendasByContext",2,agendasFound);
    }


    @Test
    public void testUpdateAgenda() {
        buildAgenda("5006");
        // create krms type AGENDA
        KrmsTypeDefinition krmsType = createKrmsActionTypeDefinition("Namespace5006", "AGENDA", null);

        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda("AgendaId5006"));
        agendaBuilder.setTypeId(krmsType.getId());
        agendaBuilder.setActive(false);
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());

        assertEquals("Updated agendaType not found",krmsType.getId(), ruleManagementServiceImpl.getAgenda("AgendaId5006").getTypeId());
        assertEquals("Agenda should have been changed to inActive",false,ruleManagementServiceImpl.getAgenda("AgendaId5006").isActive());
    }


    @Test
    public void testDeleteAgenda() {
        assertNull("Agenda should not yet exist",ruleManagementServiceImpl.getAgenda("AgendaId5007"));

        AgendaDefinition.Builder agendaBuilder = buildAgenda("5007");
        assertNotNull("Agenda should exist",ruleManagementServiceImpl.getAgenda("AgendaId5007"));

        ruleManagementServiceImpl.deleteAgenda("AgendaId5007");
        assertNull("Agenda should not exist after deletion",ruleManagementServiceImpl.getAgenda("AgendaId5007"));

        try {
            ruleManagementServiceImpl.deleteAgenda("junkAgenda5007");
            fail("Should have failed with IllegalStateException: the Agenda to delete does not exists");
        } catch (Exception e) {
            // throws  IllegalStateException: the Agenda to delete does not exists: junkAgenda5007
        }

        try {
            ruleManagementServiceImpl.deleteAgenda(null);
            fail("Should have failed with .RiceIllegalArgumentException: agendaId is null");
        } catch (Exception e) {
            // throws .RiceIllegalArgumentException: agendaId is null
        }
    }

    @Test
    public void testGetAgendasByType() {
        buildAgenda("5008");
        buildAgenda("5009");
        // create krms type AGENDA5008
        KrmsTypeDefinition krmsType = createKrmsActionTypeDefinition(  "Namespace5008", "AGENDA5008", null);

        // set agendaType for both agendas
        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda("AgendaId5008"));
        agendaBuilder.setTypeId(krmsType.getId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());
        agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda("AgendaId5009"));
        agendaBuilder.setTypeId(krmsType.getId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());

        List<AgendaDefinition> agendas = ruleManagementServiceImpl.getAgendasByType(krmsType.getId());
        assertEquals("Incorrect number of Agendas returned",2,agendas.size());

        List<String> agendaIds = Arrays.asList("AgendaId5008", "AgendaId5009");

        // verify expected agendas returned & count the returned agendas
        int agendasFound = 0;
        for( AgendaDefinition agenda : agendas ) {
            if(agendaIds.contains(agenda.getId())) {
                agendasFound++;
            }
        }
        assertEquals("Incorrect results of getAgendasByContext",2,agendasFound);
    }


    @Test
    public void testGetAgendasByTypeAndContext() {
        buildAgenda("5010");
        buildAgenda("5011");
        // create krms type AGENDA5010
        KrmsTypeDefinition krmsType = createKrmsActionTypeDefinition("Namespace5010", "AGENDA5010", null);

        // set agendaType for both agendas and contextId of 5011 to match 5010
        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda("AgendaId5010"));
        agendaBuilder.setTypeId(krmsType.getId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());
        agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda("AgendaId5011"));
        agendaBuilder.setTypeId(krmsType.getId());
        agendaBuilder.setContextId(ruleManagementServiceImpl.getAgenda("AgendaId5010").getContextId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());

        List<AgendaDefinition> agendas = ruleManagementServiceImpl.getAgendasByTypeAndContext(krmsType.getId(),
                "ContextId5010");
        assertEquals("Incorrect number of Agendas returned",2,agendas.size());

        List<String> agendaIds = Arrays.asList("AgendaId5010", "AgendaId5011");

        // verify expected agendas returned & count the returned agendas
        int agendasFound = 0;
        for( AgendaDefinition agenda : agendas ) {
            if(agendaIds.contains(agenda.getId())) {
                agendasFound++;
            }
        }
        assertEquals("Incorrect results of getAgendasByTypeAndContext",2,agendasFound);
    }


    @Test
    public void testFindAgendaIds() {
        buildAgenda("5012");
        buildAgenda("5013");
        buildAgenda("5014");
        // create krms type AGENDA5012
        KrmsTypeDefinition krmsType = createKrmsActionTypeDefinition("Namespace5012", "AGENDA5012", null);

        // set agendaType for all agendas to match / and contextId of 5013 to match 5012 but not match 5014
        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(
                "AgendaId5012"));
        agendaBuilder.setTypeId(krmsType.getId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());
        agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda("AgendaId5013"));
        agendaBuilder.setTypeId(krmsType.getId());
        agendaBuilder.setContextId(ruleManagementServiceImpl.getAgenda("AgendaId5012").getContextId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());
        agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda("AgendaId5014"));
        agendaBuilder.setTypeId(krmsType.getId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());
        // create list of agendas with same ContextId
        List<String> agendaNames =  new ArrayList<String>();
        agendaNames.add("AgendaName5012");
        agendaNames.add("AgendaName5013");
        agendaNames.add("AgendaName5014");

        CriteriaLookupServiceImpl criteriaLookupService = new CriteriaLookupServiceImpl();
        criteriaLookupService.setCriteriaLookupDao(new CriteriaLookupDaoProxy());
        ruleManagementServiceImpl.setCriteriaLookupService( criteriaLookupService);

        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        // find active agendas with same agendaType
        builder.setPredicates(equal("active","Y"),equal("typeId", krmsType.getId()));
        List<String> agendaIds = ruleManagementServiceImpl.findAgendaIds(builder.build());
        assertEquals("Wrong number of Agendas returned",3,agendaIds.size());

        // find agendas with the same Context
        builder.setPredicates(equal("contextId", "ContextId5012"));
        agendaIds = ruleManagementServiceImpl.findAgendaIds(builder.build());
        assertEquals("Wrong number of Agendas returned",2,agendaIds.size());

        // find agendas in list of agendaNames
        builder.setPredicates(in("name", agendaNames.toArray(new String[]{})));
        agendaIds = ruleManagementServiceImpl.findAgendaIds(builder.build());
        assertEquals("Wrong number of Agendas returned",3,agendaIds.size());

        // verify expected agendas returned & count the returned agendas
        int agendasFound = 0;
        for( String agendaId : agendaIds ) {
            if(agendaNames.contains(ruleManagementServiceImpl.getAgenda(agendaId).getName())) {
                agendasFound++;
            }
        }
        assertEquals("Incorrect results of findAgendaIds",3,agendasFound);
    }
}
