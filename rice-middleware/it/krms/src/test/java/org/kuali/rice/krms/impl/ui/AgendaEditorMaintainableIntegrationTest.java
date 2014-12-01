/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krms.impl.ui;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.impl.repository.ActionBoService;
import org.kuali.rice.krms.impl.repository.ActionBoServiceImpl;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.AgendaBoService;
import org.kuali.rice.krms.impl.repository.AgendaBoServiceImpl;
import org.kuali.rice.krms.impl.repository.AgendaItemBo;
import org.kuali.rice.krms.impl.repository.ContextBoServiceImpl;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.repository.PropositionBoService;
import org.kuali.rice.krms.impl.repository.PropositionBoServiceImpl;
import org.kuali.rice.krms.impl.repository.RuleBoService;
import org.kuali.rice.krms.impl.repository.RuleBoServiceImpl;
import org.kuali.rice.krms.impl.repository.TermBoService;
import org.kuali.rice.krms.impl.repository.TermBoServiceImpl;
import org.kuali.rice.krms.test.AbstractAgendaBoTest;
import org.kuali.rice.krms.test.AbstractBoTest;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Validation Integration Test
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineMode(Mode.CLEAR_DB)
public class AgendaEditorMaintainableIntegrationTest extends AbstractAgendaBoTest {

    static final String NAMESPACEX = "AEMIT_KRMS_TEST_1";
    static final String CONTEXTX = "AEMIT_Context1";

    static final String CONTEXTX_QUALIFIER = "Context1Qualifier";
    static final String CONTEXTX_QUALIFIER_VALUE = "BLAH1";
    static final String AGENDAX = "TestAgenda1";

    private PropositionBoService propositionBoService;
    private TermBoService termBoService;
    private AgendaBoService agendaBoService;
    private RuleBoService ruleBoService;
    private ActionBoService actionBoService;

    @Before
    public void setup() {

        super.setup();

        krmsAttributeDefinitionService = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();
        krmsTypeRepository = KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService();

        // like RepositoryCreateAndExecuteIntegrationTest
        propositionBoService = new PropositionBoServiceImpl();
        ((PropositionBoServiceImpl)propositionBoService).setDataObjectService(getDataObjectService());
        termBoService = new TermBoServiceImpl();

        // TODO: fix
        ((TermBoServiceImpl)termBoService).setDataObjectService(GlobalResourceLoader.<DataObjectService>getService(
                "dataObjectService"));

        contextRepository = new ContextBoServiceImpl();
        ((ContextBoServiceImpl)contextRepository).setDataObjectService(getDataObjectService());
        agendaBoService = new AgendaBoServiceImpl();
        ((AgendaBoServiceImpl)agendaBoService).setDataObjectService(getDataObjectService());
        ((AgendaBoServiceImpl)agendaBoService).setAttributeDefinitionService(krmsAttributeDefinitionService);
        ruleBoService = new RuleBoServiceImpl();
        ((RuleBoServiceImpl)ruleBoService).setDataObjectService(getDataObjectService());
        actionBoService = new ActionBoServiceImpl();
        ((ActionBoServiceImpl)actionBoService).setDataObjectService(getDataObjectService());
    }

    @Test
    public void testEmptyAgendaDelete() {
        ContextDefinition contextDefintion1 = createContextDefinition(NAMESPACEX, CONTEXTX, Collections.singletonMap(
                CONTEXTX_QUALIFIER, CONTEXTX_QUALIFIER_VALUE));
        createAgendaDefinition(AGENDAX, "AgendaLabel", contextDefintion1);
        AgendaDefinition agendaDefinition = agendaBoService.getAgendaByNameAndContextId(AGENDAX,
                contextDefintion1.getId());

        lookupAndSaveDataObject(agendaDefinition);
    }

    @Test
    public void testOrphanAgendaItems() {
        AgendaEditorMaintainable aem = new AgendaEditorMaintainable();
        AgendaEditor ae = new AgendaEditor();
        AgendaDefinitionDataWrapper agendaWrapper = new AgendaDefinitionDataWrapper();

        AgendaBo agendaBoToUpdate = findAgendaByPrimaryKey(agendaWrapper.agenda);

        //  Before we change anything, verify everything is as expected
        AgendaItemDefinition toBeDeletedItem1 = agendaBoService.getAgendaItemById( agendaWrapper.firstItem.getId() );
        AgendaItemDefinition toBeDeletedItem2 = agendaBoService.getAgendaItemById( agendaWrapper.secondItem.getId() );
        AgendaItemDefinition toBeDeletedItem3 = agendaBoService.getAgendaItemById( agendaWrapper.thirdItem.getId() );
        AgendaItemDefinition toBeDeletedItem4 = agendaBoService.getAgendaItemById( agendaWrapper.fourthItem.getId() );

        RuleDefinition toBeDeletedRule1 = ruleBoService.getRuleByRuleId( agendaWrapper.firstItem.getRuleId() );
        RuleDefinition toBeDeletedRule2 = ruleBoService.getRuleByRuleId( agendaWrapper.secondItem.getRuleId() );
        RuleDefinition toBeDeletedRule3 = ruleBoService.getRuleByRuleId( agendaWrapper.thirdItem.getRuleId() );
        RuleDefinition toBeDeletedRule4 = ruleBoService.getRuleByRuleId( agendaWrapper.fourthItem.getRuleId() );

        PropositionDefinition toBeDeletedProp1 = propositionBoService.getPropositionById( agendaWrapper.firstItem.getRule().getPropId() );
        PropositionDefinition toBeDeletedProp2 = propositionBoService.getPropositionById( agendaWrapper.secondItem.getRule().getPropId() );
        PropositionDefinition toBeDeletedProp3 = propositionBoService.getPropositionById( agendaWrapper.thirdItem.getRule().getPropId() );
        PropositionDefinition toBeDeletedProp4 = propositionBoService.getPropositionById( agendaWrapper.fourthItem.getRule().getPropId() );

        assertEquals(agendaBoToUpdate.getItems().size(), 4);
        assertEquals(agendaBoToUpdate.getName(), "TestAgenda1");

        assertNotNull("First item should be present", toBeDeletedItem1);
        assertNotNull("Second item should be present", toBeDeletedItem2);
        assertNotNull("Third item should be present", toBeDeletedItem3);
        assertNotNull("Fourth item should be present", toBeDeletedItem4);

        assertNotNull("First Item's rule should be present", toBeDeletedRule1);
        assertNotNull("Second Item's rule should be present", toBeDeletedRule2);
        assertNotNull("Third Item's rule should be present", toBeDeletedRule3);
        assertNotNull("Fourth Item's rule should be present", toBeDeletedRule4);

        assertNotNull("First Item's proposition should be present", toBeDeletedProp1);
        assertNotNull("Second Item's proposition should be present", toBeDeletedProp2);
        assertNotNull("Third Item's proposition should be present", toBeDeletedProp3);
        assertNotNull("Fourth Item's proposition should be present", toBeDeletedProp4);

        // Change the agenda!
        agendaBoToUpdate.setName("Updated Agenda Name!");
        agendaBoToUpdate.setFirstItem(null);
        agendaBoToUpdate.setFirstItemId(null);
        agendaBoToUpdate.setItems(new ArrayList<AgendaItemBo>());

        ae.setAgenda(agendaBoToUpdate);
        aem.setDataObject(ae);
        aem.saveDataObject();

        // Get the updated agenda and verify agenda items, rule, propositions, etc were updated or deleted.
        AgendaBo agendaBoUpdated = findAgendaByPrimaryKey(agendaWrapper.agenda);

        AgendaItemDefinition deletedItem1 = agendaBoService.getAgendaItemById( agendaWrapper.firstItem.getId() );
        AgendaItemDefinition deletedItem2 = agendaBoService.getAgendaItemById( agendaWrapper.secondItem.getId() );
        AgendaItemDefinition deletedItem3 = agendaBoService.getAgendaItemById( agendaWrapper.thirdItem.getId() );
        AgendaItemDefinition deletedItem4 = agendaBoService.getAgendaItemById( agendaWrapper.fourthItem.getId() );

        RuleDefinition deletedRule1 = ruleBoService.getRuleByRuleId( agendaWrapper.firstItem.getRuleId() );
        RuleDefinition deletedRule2 = ruleBoService.getRuleByRuleId( agendaWrapper.secondItem.getRuleId() );
        RuleDefinition deletedRule3 = ruleBoService.getRuleByRuleId( agendaWrapper.thirdItem.getRuleId() );
        RuleDefinition deletedRule4 = ruleBoService.getRuleByRuleId( agendaWrapper.fourthItem.getRuleId() );

        PropositionDefinition deletedProp1 = propositionBoService.getPropositionById( agendaWrapper.firstItem.getRule().getPropId() );
        PropositionDefinition deletedProp2 = propositionBoService.getPropositionById( agendaWrapper.secondItem.getRule().getPropId() );
        PropositionDefinition deletedProp3 = propositionBoService.getPropositionById( agendaWrapper.thirdItem.getRule().getPropId() );
        PropositionDefinition deletedProp4 = propositionBoService.getPropositionById( agendaWrapper.fourthItem.getRule().getPropId() );

        assertEquals(agendaBoUpdated.getItems().size(), 0);
        assertEquals(agendaBoUpdated.getName(), "Updated Agenda Name!");

        assertNull("First item should be deleted", deletedItem1);
        assertNull("Second item should be deleted", deletedItem2);
        assertNull("Third item should be deleted", deletedItem3);
        assertNull("Fourth item should be deleted", deletedItem4);

        assertNull("First Item's rule should be deleted", deletedRule1);
        assertNull("Second Item's rule should be deleted", deletedRule2);
        assertNull("Third Item's rule should be deleted", deletedRule3);
        assertNull("Fourth Item's rule should be deleted", deletedRule4);

        assertNull("First Item's proposition should be deleted", deletedProp1);
        assertNull("Second Item's proposition should be deleted", deletedProp2);
        assertNull("Third Item's proposition should be deleted", deletedProp3);
        assertNull("Fourth Item's proposition should be deleted", deletedProp4);
    }

/*
   This test requires:
   1. changing the BaselineMode to (Mode.NONE).
   2. Setting a breakpoint on the first line.
   3. Debug the test.
   4. When you reach the breakpoint, re-impex (and then apply the db test script) your database.
   5. Continue.  No exceptions should be thrown.
    @Test
    public void testDbAgendaItems() {
        AgendaDefinition agendaDefinition = agendaBoService.getAgendaByNameAndContextId("My Fabulous Agenda", "CONTEXT1"); // values from populated test db
        lookupAndSaveDataObject(agendaDefinition);
        agendaDefinition = agendaBoService.getAgendaByNameAndContextId("SimpleAgendaCompoundProp", "CONTEXT1"); // values from populated test db
        lookupAndSaveDataObject(agendaDefinition);
        agendaDefinition = agendaBoService.getAgendaByNameAndContextId("One Big Rule", "CONTEXT1"); // values from populated test db
        lookupAndSaveDataObject(agendaDefinition);
    }
*/

    private void lookupAndSaveDataObject(AgendaDefinition agendaDefinition) {
        AgendaBo agendaBo = findAgendaByPrimaryKey(agendaDefinition);

        AgendaEditorMaintainable aem = new AgendaEditorMaintainable();
        AgendaEditor ae = new AgendaEditor();
        ae.setAgenda(agendaBo);
        aem.setDataObject(ae);
        aem.saveDataObject();
    }

    private AgendaBo findAgendaByPrimaryKey(AgendaDefinition agendaDefinition) {
        return getDataObjectService().find(AgendaBo.class, agendaDefinition.getId());
    }

    private void createAgendaDefinition(String agendaName, String agendaLabel, ContextDefinition contextDefinition) {
        KrmsTypeDefinition krmsGenericTypeDefinition = createKrmsGenericTypeDefinition(contextDefinition.getNamespace(),
                "testAgendaTypeService", agendaName, agendaLabel);

        AgendaDefinition agendaDefinition = AgendaDefinition.Builder.create(null, agendaName,
                krmsGenericTypeDefinition.getId(), contextDefinition.getId()).build();
        agendaDefinition = agendaBoService.createAgenda(agendaDefinition);
        agendaBoService.updateAgenda(agendaDefinition);

        AgendaBo agendaBo = findAgendaByPrimaryKey(agendaDefinition);

        AgendaDefinition.Builder agendaDefBuilder1 = AgendaDefinition.Builder.create(agendaBoService.to(agendaBo));
        agendaDefinition = agendaDefBuilder1.build();

        agendaBoService.updateAgenda(agendaDefinition);
    }

    private class AgendaDefinitionDataWrapper {
        private ContextDefinition context;
        private AgendaDefinition agenda;
        private AgendaItemDefinition firstItem;
        private AgendaItemDefinition secondItem;
        private AgendaItemDefinition thirdItem;
        private AgendaItemDefinition fourthItem;

        private RuleDefinition firstItemRule;

        AgendaDefinitionDataWrapper() {
            context = getContextRepository().getContextByNameAndNamespace(CONTEXT1, NAMESPACE1);
            assertNotNull("context " + CONTEXT1 + " not found", context);
            agenda = getAgendaBoService().getAgendaByNameAndContextId(AGENDA1, context.getId());
            assertNotNull("agenda " + AGENDA1 + " not found", agenda);

            firstItem = getAgendaBoService().getAgendaItemById( agenda.getFirstItemId() );
            assertNotNull("agenda item " + agenda.getFirstItemId() + " not found", firstItem);

            secondItem = firstItem.getAlways();
            thirdItem = secondItem.getAlways();
            fourthItem = thirdItem.getAlways();

            firstItemRule = firstItem.getRule();
        }
    }
}
