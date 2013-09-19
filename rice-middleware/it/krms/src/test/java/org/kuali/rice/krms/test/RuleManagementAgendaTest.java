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
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.springmodules.orm.ojb.OjbOperationException;

import javax.persistence.OptimisticLockException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.junit.Assert.*;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.in;

/**
 *   RuleManagementAgendaTest is to test the methods of ruleManagementServiceImpl relating to krms Agendas
 *
 *   Each test focuses on one of the methods.
 */
public class RuleManagementAgendaTest extends RuleManagementBaseTest {
    @Override
    @Before
    public void setClassDiscriminator() {
        // set a unique discriminator for test objects of this class
        CLASS_DISCRIMINATOR = "RMAT";
    }

    /**
     *  Test testCreateAgenda()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .createAgenda(AgendaDefinition) method
     */
    @Test
    public void testCreateAgenda() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t0 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t0");

        // buildAgenda utilizes the ruleManagementServiceImpl.createAgenda method
        AgendaDefinition.Builder agendaBuilder = buildAgenda(t0.object0);

        assertTrue("Created agenda is not active", agendaBuilder.isActive());

        assertEquals("Expected Context not found",t0.contextId,agendaBuilder.getContextId());
        assertEquals("Expected AgendaId not found",t0.agenda_Id,agendaBuilder.getId());

        assertEquals("Expected AgendaItemId not found",t0.agendaItem_0_Id,agendaBuilder.getFirstItemId());
        assertEquals("Expected Rule of AgendaItem not found",t0.rule_0_Id,ruleManagementServiceImpl.getAgendaItem(agendaBuilder.getFirstItemId()).getRule().getId());
    }

    /**
     *  Test testGetAgendaByNameAndContextId()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .getAgendaByNameAndContextId(AgendaName, ContextId) method
     */
    @Test
    public void testGetAgendaByNameAndContextId() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t1 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t1");
        buildAgenda(t1.object0);
        AgendaDefinition agendaDefinition = ruleManagementServiceImpl.getAgendaByNameAndContextId(t1.agenda_Name,t1.contextId);

        assertEquals("Invalid agendaId name found",t1.agenda_Id,agendaDefinition.getId());
        assertEquals("Invalid contextId found",t1.contextId,agendaDefinition.getContextId());
        assertNull("Agenda typeId should not have been set", agendaDefinition.getTypeId());
        assertEquals("Incorrect agendaName found",t1.agenda_Name,agendaDefinition.getName());
        assertEquals("Invalid agendaFirstItemId found",t1.agendaItem_0_Id,agendaDefinition.getFirstItemId());

        agendaDefinition = ruleManagementServiceImpl.getAgendaByNameAndContextId(t1.agenda_Name,"badContext");
        assertNull("Invalid Context, no agendas should have been found",agendaDefinition);

        agendaDefinition = ruleManagementServiceImpl.getAgendaByNameAndContextId("badName",t1.contextId);
        assertNull("Invalid Name, no agendas should have been found",agendaDefinition);

        try {
            agendaDefinition = ruleManagementServiceImpl.getAgendaByNameAndContextId(null,t1.contextId);
            fail("Null Name specified for search, should have thrown .RiceIllegalArgumentException: name is blank ");
        } catch (RiceIllegalArgumentException e) {
            // thrown .RiceIllegalArgumentException: name is blank
        }

        try {
            agendaDefinition = ruleManagementServiceImpl.getAgendaByNameAndContextId(t1.agenda_Name,null);
            fail("Null Context specified for search, should have thrown .RiceIllegalArgumentException: contextId is blank");
        } catch (RiceIllegalArgumentException e) {
            // thrown .RiceIllegalArgumentException: contextId is blank
        }
    }

    /**
     *  Test testFindCreateAgenda()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .findCreateAgenda(AgendaDefinition) method
     */
    @Test
    public void testFindCreateAgenda() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t2 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t2");

        // create a context
        ContextDefinition.Builder contextDefinitionBuilder = ContextDefinition.Builder.create(
                t2.namespaceName, t2.contextName);
        contextDefinitionBuilder.setId(t2.contextId);
        ContextDefinition contextDefinition = contextDefinitionBuilder.build();
        contextDefinition = ruleManagementServiceImpl.findCreateContext(contextDefinition);

        assertNull("Agenda should not have already existed", ruleManagementServiceImpl.getAgenda(t2.agendaItem_0_Id));

        // create an agenda
        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(
                t2.agenda_Id, t2.agenda_Name, null, t2.contextId);
        AgendaDefinition agenda = agendaBuilder.build();
        agenda = ruleManagementServiceImpl.findCreateAgenda(agenda);

        assertNotNull("Agenda should have been created", ruleManagementServiceImpl.getAgenda(t2.agenda_Id));

        // update an agenda using findCreateAgenda - invalid attempt
        // ( cannot change name or context as these are used to uniquely identify agenda for findCreateAgenda
        agendaBuilder = AgendaDefinition.Builder.create(t2.agenda_Id, "ChangedName", null, t2.contextId);
        agenda = agendaBuilder.build();
        try {
            agenda = ruleManagementServiceImpl.findCreateAgenda(agenda);
            fail( "should have failed with OjbOperationException: OJB operation failed");
        } catch (OjbOperationException e) {
            // thrown OjbOperationException: OJB operation failed ...OptimisticLockException: Object has been modified by someone else
        }

        // create a new agendaItem to update the agenda with
        AgendaItemDefinition agendaItem = newTestAgendaItemDefinition("AINew" + t2.action0 , t2.agenda_Id, null);
        AgendaItemDefinition.Builder itemBuilder = AgendaItemDefinition.Builder.create(agendaItem);
        itemBuilder = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(itemBuilder.build()));

        //  findCreateAgenda with changed agendaFirstItemId
        agendaBuilder = AgendaDefinition.Builder.create(t2.agenda_Id, t2.agenda_Name, null, t2.contextId);
        agendaBuilder.setFirstItemId(itemBuilder.getId());
        agenda = ruleManagementServiceImpl.findCreateAgenda(agendaBuilder.build());

        assertEquals("Agenda should have been changed by findCreateAgenda","AINew" + t2.action0,
                ruleManagementServiceImpl.getAgenda(t2.agenda_Id).getFirstItemId());
    }

    /**
     *  Test testGetAgenda()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .getAgenda(AgendaId) method
     */
    @Test
    public void testGetAgenda() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t3 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t3");
        AgendaDefinition.Builder agendaBuilder = buildAgenda(t3.object0);

        assertEquals("Agenda not found", t3.agenda_Name, ruleManagementServiceImpl.getAgenda(t3.agenda_Id).getName());

        // call getAgenda method with null
        try {
            ruleManagementServiceImpl.getAgenda(null);
            fail("Should have thrown RiceIllegalArgumentException: agenda id is null or blank");
        } catch (RiceIllegalArgumentException e) {
            // throws RiceIllegalArgumentException: agenda id is null or blank
        }

        // call getAgenda method with blank
        try {
            ruleManagementServiceImpl.getAgenda("  ");
            fail("Should have thrown RiceIllegalArgumentException: agenda id is null or blank");
        } catch (RiceIllegalArgumentException e) {
            // throws RiceIllegalArgumentException: agenda id is null or blank
        }

        // call get Agenda with bad AgendaId
        assertNull("Agenda should not have been found", ruleManagementServiceImpl.getAgenda("badAgendaId"));
    }

    /**
     *  Test testGetAgendasByContext()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .getAgendasByContext(ContextId) method
     */
    @Test
    public void testGetAgendasByContext() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t4 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t4");
        buildAgenda(t4.object0);

        // get a second set of object names for the creation of second agenda
        RuleManagementBaseTestObjectNames t5 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t5");
        buildAgenda(t5.object0);

        // set second agendaContextId to same as first
        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(t5.agenda_Id));
        agendaBuilder.setContextId(t4.contextId);
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());

        List<AgendaDefinition> agendas = ruleManagementServiceImpl.getAgendasByContext(t4.contextId);
        assertEquals("Incorrect number of Agendas returned",2,agendas.size());

        List<String> agendaIds = Arrays.asList(t4.agenda_Id, t5.agenda_Id);

        // verify expected agendas returned & count the returned agendas
        int agendasFound = 0;
        for( AgendaDefinition agenda : agendas ) {
            if(agendaIds.contains(agenda.getId())) {
                agendasFound++;
            }
        }
        assertEquals("Incorrect results of getAgendasByContext",2,agendasFound);

        // call getAgendasByContext method with null
        try {
            ruleManagementServiceImpl.getAgendasByContext(null);
            fail("Should have thrown RiceIllegalArgumentException: context ID is null or blank");
        } catch (RiceIllegalArgumentException e) {
            // throws RiceIllegalArgumentException: context ID is null or blank
        }

        // call getAgendasByContext method with blank ContextId
        try {
            ruleManagementServiceImpl.getAgendasByContext("   ");
            fail("Should have thrown RiceIllegalArgumentException: context ID is null or blank");
        } catch (RiceIllegalArgumentException e) {
            // throws RiceIllegalArgumentException: context ID is null or blank
        }

        // call getAgendasByContext with bad ContextId
        assertEquals("No Agenda's should have been found",0,ruleManagementServiceImpl.getAgendasByContext("badContextId").size());

    }

    /**
     *  Test testUpdateAgenda()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .updateAgenda(AgendaDefinition) method
     */
    @Test
    public void testUpdateAgenda() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t6 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t6");
        buildAgenda(t6.object0);
        // create krms type AGENDA
        KrmsTypeDefinition krmsType = createKrmsTypeDefinition(t6.namespaceName, "AGENDA", null);

        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(t6.agenda_Id));
        agendaBuilder.setTypeId(krmsType.getId());
        agendaBuilder.setActive(false);
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());

        assertEquals("Updated agendaType not found",krmsType.getId(), ruleManagementServiceImpl.getAgenda(t6.agenda_Id).getTypeId());
        assertEquals("Agenda should have been changed to inActive",false,ruleManagementServiceImpl.getAgenda(t6.agenda_Id).isActive());
    }

    /**
     *  Test testDeleteAgenda()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .deleteAgenda("AgendaId") method
     */
    @Test
    public void testDeleteAgenda() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t7 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t7");

        assertNull("Agenda should not yet exist",ruleManagementServiceImpl.getAgenda(t7.agenda_Id));

        AgendaDefinition.Builder agendaBuilder = buildAgenda(t7.object0);
        assertNotNull("Agenda should exist",ruleManagementServiceImpl.getAgenda(t7.agenda_Id));

        ruleManagementServiceImpl.deleteAgenda(t7.agenda_Id);
        assertNull("Agenda should not exist after deletion",ruleManagementServiceImpl.getAgenda(t7.agenda_Id));

        try {
            ruleManagementServiceImpl.deleteAgenda("junkAgenda");
            fail("Should have failed with IllegalStateException: the Agenda to delete does not exists");
        } catch (IllegalStateException e) {
            // throws  IllegalStateException: the Agenda to delete does not exists: junkAgenda
        }

        try {
            ruleManagementServiceImpl.deleteAgenda(null);
            fail("Should have failed with .RiceIllegalArgumentException: agendaId is null");
        } catch (RiceIllegalArgumentException e) {
            // throws .RiceIllegalArgumentException: agendaId is null
        }
    }

    /**
     *  Test testGetAgendasByType()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .getAgendasByType("NamespaceType") method
     */
    @Test
    public void testGetAgendasByType() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t8 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t8");
        buildAgenda(t8.object0);

        // get a second set of object names for the creation of second agenda
        RuleManagementBaseTestObjectNames t9 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t9");
        buildAgenda(t9.object0);

        // create krms type AGENDA5008
        KrmsTypeDefinition krmsType = createKrmsTypeDefinition(t8.namespaceName, t8.namespaceType, null);

        // set agendaType for both agendas
        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(t8.agenda_Id));
        agendaBuilder.setTypeId(krmsType.getId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());
        agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(t9.agenda_Id));
        agendaBuilder.setTypeId(krmsType.getId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());

        List<AgendaDefinition> agendas = ruleManagementServiceImpl.getAgendasByType(krmsType.getId());
        assertEquals("Incorrect number of Agendas returned",2,agendas.size());

        List<String> agendaIds = Arrays.asList(t8.agenda_Id, t9.agenda_Id);

        // verify expected agendas returned & count the returned agendas
        int agendasFound = 0;
        for( AgendaDefinition agenda : agendas ) {
            if(agendaIds.contains(agenda.getId())) {
                agendasFound++;
            }
        }

        assertEquals("Incorrect results of getAgendasByContext",2,agendasFound);
    }

    /**
     *  Test testGetAgendasByTypeAndContext()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .getAgendasByTypeAndContext("NamespaceType", "ContextId") method
     */
    @Test
    public void testGetAgendasByTypeAndContext() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t10 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t10");
        buildAgenda(t10.object0);

        // get a second set of object names for the creation of second agenda
        RuleManagementBaseTestObjectNames t11 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t11");
        buildAgenda(t11.object0);

        // create krms type AGENDA5010
        KrmsTypeDefinition krmsType = createKrmsTypeDefinition(t10.namespaceName, t10.namespaceType, null);

        // set agendaType for both agendas and contextId of 5011 to match 5010
        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(t10.agenda_Id));
        agendaBuilder.setTypeId(krmsType.getId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());
        agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(t11.agenda_Id));
        agendaBuilder.setTypeId(krmsType.getId());
        agendaBuilder.setContextId(ruleManagementServiceImpl.getAgenda(t10.agenda_Id).getContextId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());

        List<AgendaDefinition> agendas = ruleManagementServiceImpl.getAgendasByTypeAndContext(krmsType.getId(),t10.contextId);
        assertEquals("Incorrect number of Agendas returned",2,agendas.size());

        List<String> agendaIds = Arrays.asList(t10.agenda_Id, t11.agenda_Id);

        // verify expected agendas returned & count the returned agendas
        int agendasFound = 0;
        for( AgendaDefinition agenda : agendas ) {
            if(agendaIds.contains(agenda.getId())) {
                agendasFound++;
            }
        }

        assertEquals("Incorrect results of getAgendasByTypeAndContext",2,agendasFound);
    }

    /**
     *  Test testFindAgendaIds()
     *
     *  This test focuses specifically on the RuleManagementServiceImpl .findAgendaIds(QueryByCriteria) method
     */
    @Test
    public void testFindAgendaIds() {
        // get a set of unique object names for use by this test (discriminator passed can be any unique value within this class)
        RuleManagementBaseTestObjectNames t12 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t12");
        buildAgenda(t12.object0);

        // get a second set of object names for the creation of second agenda
        RuleManagementBaseTestObjectNames t13 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t13");
        buildAgenda(t13.object0);

        // get a third set of object names for the creation of thrid agenda
        RuleManagementBaseTestObjectNames t14 =  new RuleManagementBaseTestObjectNames( CLASS_DISCRIMINATOR, "t14");
        buildAgenda(t14.object0);

        // create krms type t12.AGENDA
        KrmsTypeDefinition krmsType = createKrmsTypeDefinition(t12.namespaceName, t12.namespaceType, null);

        // set agendaType for all agendas to match / and contextId of 5013 to match 5012 but not match 5014
        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(
                t12.agenda_Id));
        agendaBuilder.setTypeId(krmsType.getId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());
        agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(t13.agenda_Id));
        agendaBuilder.setTypeId(krmsType.getId());
        agendaBuilder.setContextId(ruleManagementServiceImpl.getAgenda(t12.agenda_Id).getContextId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());
        agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(t14.agenda_Id));
        agendaBuilder.setTypeId(krmsType.getId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());
        // create list of agendas with same ContextId
        List<String> agendaNames =  new ArrayList<String>();
        agendaNames.add(t12.agenda_Name);
        agendaNames.add(t13.agenda_Name);
        agendaNames.add(t14.agenda_Name);

        CriteriaLookupServiceImpl criteriaLookupService = new CriteriaLookupServiceImpl();
        criteriaLookupService.setCriteriaLookupDao(new CriteriaLookupDaoProxy());
        ruleManagementServiceImpl.setCriteriaLookupService( criteriaLookupService);

        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        // find active agendas with same agendaType
        builder.setPredicates(equal("active","Y"),equal("typeId", krmsType.getId()));
        List<String> agendaIds = ruleManagementServiceImpl.findAgendaIds(builder.build());
        assertEquals("Wrong number of Agendas returned",3,agendaIds.size());

        // find agendas with the same Context
        builder.setPredicates(equal("contextId", t12.contextId));
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
