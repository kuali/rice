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
package org.kuali.rice.krms.test;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krms.api.repository.LogicalOperator;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinitionContract;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameter;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterType;
import org.kuali.rice.krms.api.repository.proposition.PropositionType;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.impl.repository.ActionAttributeBo;
import org.kuali.rice.krms.impl.repository.ActionBo;
import org.kuali.rice.krms.impl.repository.ActionBoService;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.AgendaItemBo;
import org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionBo;
import org.kuali.rice.krms.impl.repository.RuleBo;
import org.kuali.rice.test.BaselineTestCase;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Integration test for the AgendaBoService.  Note that we inherit the test data created by AbstractAgendaBoTest, and
 * test the service against that data.
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class AgendaBoServiceTest extends AbstractAgendaBoTest {

    private static final String NULL = new String("null"); // null String.  Hah. Using it as a "null object".

    private static <A> A nullConvertingGet(List<A> list, int index) {
        // being lazy, no input checks here
        A result = list.get(index);
        if (result == NULL) result = null;
        return result;
    }

    @Test public void testGetByContextId() {

        assertTrue(CollectionUtils.isEmpty(getAgendaBoService().getAgendasByContextId("#$^$ BogusContextId !@#$")));
        assertTrue(CollectionUtils.isEmpty(getAgendaBoService().getAgendaItemsByContext("#$^$ BogusContextId !@#$")));

        for (String contextName : Arrays.asList(CONTEXT1, CONTEXT2, CONTEXT3)) {
            String namespace = getNamespaceByContextName(contextName);
            if (StringUtils.isBlank(namespace)) {
                throw new RiceRuntimeException("namespace is " + namespace + " for context with name " + contextName);
            }

            String contextId = getContextRepository().getContextByNameAndNamespace(contextName, namespace).getId();

            List<AgendaDefinition> agendas = getAgendaBoService().getAgendasByContextId(contextId);
            List<AgendaItemDefinition> agendaItems = getAgendaBoService().getAgendaItemsByContext(contextId);

            assertEquals("agenda count doesn't match our tally for context " + contextName, agendas.size(),
                    getBoService().countMatching(AgendaBo.class, Collections.singletonMap("contextId", contextId)));

            int totalAgendaItems = 0; // count agenda items in the context for verification purposes

            Set<String> agendaIds = new HashSet<String>(); // build set of agenda ids, also for verification purposes

            for (AgendaDefinition agenda : agendas) {
                assertEquals("agenda w/ ID "+ agenda.getId() +" has a context ID that doesn't match",
                        agenda.getContextId(), contextId);

                totalAgendaItems += getBoService().countMatching(
                        AgendaItemBo.class, Collections.singletonMap("agendaId", agenda.getId())
                );

                agendaIds.add(agenda.getId());
            }

            for (AgendaItemDefinition agendaItem : agendaItems) {
                assertTrue("agenda item is not part of any agendas in " + contextName,
                        agendaIds.contains(agendaItem.getAgendaId()));
            }

            assertEquals("number of agenda items doesn't match our tally", agendaItems.size(), totalAgendaItems);
        }

    }

    @Test public void testGetAgendasByContextId_nullOrBlank() {

        for (String contextId : Arrays.asList(null, "", " ")) {
            try {
                getAgendaBoService().getAgendasByContextId(contextId);
                fail("getAgendasByContextId should have thrown "+ RiceIllegalArgumentException.class.getSimpleName() +
                        " for invalid contextId=" + contextId +".");
            } catch (RiceIllegalArgumentException e) {
                // good, that's what it should do
            }
        }
    }

    @Test public void testGetAgendaItemsByContextId_nullOrBlank() {

        for (String contextId : Arrays.asList(null, "", " ")) {
            try {
                getAgendaBoService().getAgendaItemsByContext(contextId);
                fail("getAgendaItemsByContext should have thrown "+ RiceIllegalArgumentException.class.getSimpleName() +
                        " for invalid contextId=" + contextId +".");
            } catch (RiceIllegalArgumentException e) {
                // good, that's what it should do
            }
        }
    }

    @Test
    public void testGetByType() {

        assertTrue(CollectionUtils.isEmpty(getAgendaBoService().getAgendasByType("#$^$ BogusTypeId !@#$")));
        assertTrue(CollectionUtils.isEmpty(getAgendaBoService().getAgendaItemsByType("#$^$ BogusTypeId !@#$")));

        List<KrmsTypeDefinition> agendaTypes =  getAgendaTypesForContexts(Arrays.asList(CONTEXT1, CONTEXT2, CONTEXT3));

        assertTrue("We must have some types to test with or we prove nothing", agendaTypes.size() > 0);

        for (KrmsTypeDefinition agendaType : agendaTypes) {
            String typeName = agendaType.getName();
            String typeNamespace = agendaType.getNamespace();

            KrmsTypeDefinition type = getKrmsTypeRepository().getTypeByName(typeNamespace, typeName);

            List<AgendaDefinition> agendas = getAgendaBoService().getAgendasByType(type.getId());
            List<AgendaItemDefinition> agendaItems = getAgendaBoService().getAgendaItemsByType(type.getId());


            assertEquals("agenda count doesn't match our tally for type " + typeNamespace+":"+typeName,
                    agendas.size(), getBoService().countMatching(
                    AgendaBo.class, Collections.singletonMap("typeId", type.getId()))
            );

            int totalAgendaItems = 0; // count agenda items in the type for verification purposes

            Set<String> agendaIds = new HashSet<String>(); // build set of agenda ids, also for verification purposes

            for (AgendaDefinition agenda : agendas) {
                assertEquals("agenda w/ ID "+ agenda.getTypeId() +" has a type ID that doesn't match",
                        agenda.getTypeId(), type.getId());

                totalAgendaItems += getBoService().countMatching(
                        AgendaItemBo.class, Collections.singletonMap("agendaId", agenda.getId())
                );

                agendaIds.add(agenda.getId());
            }

            for (AgendaItemDefinition agendaItem : agendaItems) {
                assertTrue("agenda item is not part of any agendas in type " + typeNamespace+":"+typeName,
                        agendaIds.contains(agendaItem.getAgendaId()));
            }

            assertEquals("number of agenda items doesn't match our tally", agendaItems.size(), totalAgendaItems);

        }
    }

    private List<KrmsTypeDefinition> getAgendaTypesForContexts(List<String> contextNames) {
        List<KrmsTypeDefinition> results = new ArrayList<KrmsTypeDefinition>();

        // collect all the types used for the agendas in our contexts
        for (String contextName : contextNames) {
            String namespace = getNamespaceByContextName(contextName);
            if (StringUtils.isBlank(namespace)) {
                throw new RiceRuntimeException("namespace is " + namespace + " for context with name " + contextName);
            }

            String contextId = getContextRepository().getContextByNameAndNamespace(contextName, namespace).getId();

            // depending on good behavior of getAgendasByContextId which is tested elsewhere
            List<AgendaDefinition> agendas = getAgendaBoService().getAgendasByContextId(contextId);

            // stacked filters here
            if (!CollectionUtils.isEmpty(agendas)) {
                for (AgendaDefinition agenda : agendas) {
                    if (agenda.getTypeId() != null) {
                        KrmsTypeDefinition type = getKrmsTypeRepository().getTypeById(agenda.getTypeId());

                        // we depend on working hashcode & equals for KrmsTypeDefinition here
                        if (!results.contains(type)) {
                            results.add(type);
                        }
                    }
                }
            }
        }
        return results;
    }

    @Test public void testGetAgendasByType_nullOrBlank() {

        for (String contextId : Arrays.asList(null, "", " ")) {
            try {
                getAgendaBoService().getAgendasByType(contextId);
                fail("getAgendasByType should have thrown "+ RiceIllegalArgumentException.class.getSimpleName() +
                        " for invalid contextId=" + contextId +".");
            } catch (RiceIllegalArgumentException e) {
                // good, that's what it should do
            }
        }
    }

    @Test public void testGetAgendaItemsByType_nullOrBlank() {

        for (String contextId : Arrays.asList(null, "", " ")) {
            try {
                getAgendaBoService().getAgendaItemsByType(contextId);
                fail("getAgendaItemsByType should have thrown "+ RiceIllegalArgumentException.class.getSimpleName() +
                        " for invalid contextId=" + contextId +".");
            } catch (RiceIllegalArgumentException e) {
                // good, that's what it should do
            }
        }
    }

    @Test public void testGetByTypeAndContext() {

        boolean testedSomeTypes = false;

        for (String contextName : Arrays.asList(CONTEXT1, CONTEXT2, CONTEXT3)) {

            List<KrmsTypeDefinition> agendaTypes =  getAgendaTypesForContexts(Collections.singletonList(contextName));

            String namespace = getNamespaceByContextName(contextName);
            if (StringUtils.isBlank(namespace)) {
                throw new RiceRuntimeException("namespace is " + namespace + " for context with name " + contextName);
            }

            ContextDefinition context = getContextRepository().getContextByNameAndNamespace(contextName, namespace);

            for (KrmsTypeDefinition agendaType : agendaTypes) {

                testedSomeTypes = true; // prove we got to the inner loop

                assertTrue(CollectionUtils.isEmpty(getAgendaBoService().getAgendasByTypeAndContext("#$^$ BogusTypeId !@#$", context.getId())));
                assertTrue(CollectionUtils.isEmpty(getAgendaBoService().getAgendaItemsByTypeAndContext("#$^$ BogusTypeId !@#$", context.getId())));
                assertTrue(CollectionUtils.isEmpty(getAgendaBoService().getAgendasByTypeAndContext(agendaType.getId(), "#$^$ BogusContextId !@#$")));
                assertTrue(CollectionUtils.isEmpty(getAgendaBoService().getAgendaItemsByTypeAndContext(
                        agendaType.getId(), "#$^$ BogusContextId !@#$")));

                List<AgendaDefinition> agendas = getAgendaBoService().getAgendasByTypeAndContext(agendaType.getId(), context.getId());
                List<AgendaItemDefinition> agendaItems = getAgendaBoService().getAgendaItemsByTypeAndContext(agendaType.getId(), context.getId());

                Map<String, String> agendaCountCrit = new HashMap<String, String>();
                agendaCountCrit.put("typeId", agendaType.getId());
                agendaCountCrit.put("contextId", context.getId());
                assertEquals(
                        "agenda count doesn't match our tally for type " + agendaType.getNamespace() + ":" + agendaType
                                .getName(), agendas.size(), getBoService().countMatching(AgendaBo.class,
                        agendaCountCrit));

                int totalAgendaItems = 0; // count agenda items in the type for verification purposes

                Set<String> agendaIds = new HashSet<String>(); // build set of agenda ids, also for verification purposes

                for (AgendaDefinition agenda : agendas) {
                    assertEquals("agenda w/ ID "+ agenda.getTypeId() +" has a type ID that doesn't match",
                            agenda.getTypeId(), agendaType.getId());

                    totalAgendaItems += getBoService().countMatching(
                            AgendaItemBo.class, Collections.singletonMap("agendaId", agenda.getId())
                    );

                    agendaIds.add(agenda.getId());
                }

                for (AgendaItemDefinition agendaItem : agendaItems) {
                    String assertionString = "agenda item is not part of any agendas in type " +
                            agendaType.getNamespace()+":"+agendaType.getName() +
                            " and context " + context.getNamespace()+":"+context.getName();

                    assertTrue(assertionString, agendaIds.contains(agendaItem.getAgendaId())
                    );
                }

                assertEquals("number of agenda items doesn't match our tally", agendaItems.size(), totalAgendaItems);
            }

            assertTrue("We have to test some types or we prove nothing", testedSomeTypes);

        }
    }

    @Test public void testGetAgendaItemsByTypeAndContext_nullOrBlank() {


        Set<String> emptyValues = new HashSet<String>();
        emptyValues.addAll(Arrays.asList(NULL, "", " "));

        Set<String> oneNonBlank = Sets.union(emptyValues, Collections.singleton("fakeId"));
        Set<List<String>> testIds = Sets.union(Sets.cartesianProduct(emptyValues, oneNonBlank),
                Sets.cartesianProduct(oneNonBlank, emptyValues));

        for (List<String> ids : testIds) {
            try {
                getAgendaBoService().getAgendaItemsByTypeAndContext(nullConvertingGet(ids, 0), nullConvertingGet(ids, 1));
                fail("getAgendaItemsByType should have thrown "+ RiceIllegalArgumentException.class.getSimpleName() +
                        " for invalid combo of contextId=" + ids +".");
            } catch (RiceIllegalArgumentException e) {
                // good, that's what it should do
            }
        }
    }

    @Test
    public void testUpdateAgendaItem() {
        ContextDefinition context = getContextRepository().getContextByNameAndNamespace(CONTEXT1, NAMESPACE1);
        assertNotNull("context " + CONTEXT1 + " not found", context);
        AgendaDefinition agenda = getAgendaBoService().getAgendaByNameAndContextId(AGENDA1, context.getId());
        assertNotNull("agenda " + AGENDA1 + " not found", agenda);
        AgendaItemDefinition agendaItem = getAgendaBoService().getAgendaItemById(agenda.getFirstItemId());
        assertNotNull("agenda item " + agenda.getFirstItemId() + " not found", agendaItem);

        KrmsAttributeDefinition attributeDefinition = getKrmsAttributeDefinitionService().createAttributeDefinition(
                KrmsAttributeDefinition.Builder.create(null, ATTRIBUTE1, NAMESPACE1).build());
        KrmsAttributeDefinitionBo attributeDefinitionBo = KrmsAttributeDefinitionBo.from(attributeDefinition);

        // verify the agenda item
        AgendaItemBo agendaItemBo = AgendaItemBo.from(agendaItem);
        assertNotNull("agenda item null", agendaItemBo);
        List<ActionBo> agendaItemActionBos = agendaItemBo.getRule().getActions();
        assertEquals("incorrect number of agenda item rule actions found", 1, agendaItemActionBos.size());
        ActionBo agendaItemActionBo = agendaItemActionBos.get(0);
        assertTrue("agenda item rule action attributes found", agendaItemActionBo.getAttributes().isEmpty());
        assertTrue("agenda item rule action attributes found", agendaItemActionBo.getAttributeBos().isEmpty());

        // verify the child agenda item
        AgendaItemBo alwaysBo = agendaItemBo.getAlways();
        assertNotNull("always agenda item null", alwaysBo);
        List<ActionBo> alwaysActionBos = alwaysBo.getRule().getActions();
        assertEquals("incorrect number of always agenda item rule actions found", 1, alwaysActionBos.size());
        ActionBo alwaysActionBo = alwaysActionBos.get(0);
        assertTrue("always agenda item rule action attributes found", alwaysActionBo.getAttributes().isEmpty());
        assertTrue("always agenda item rule action attributes found", alwaysActionBo.getAttributeBos().isEmpty());

        // add agenda item attribute
        ActionAttributeBo agendaItemActionAttributeBo = new ActionAttributeBo();
        agendaItemActionAttributeBo.setAction(agendaItemActionBo);
        agendaItemActionAttributeBo.setAttributeDefinition(attributeDefinitionBo);
        agendaItemActionAttributeBo.setValue("testAgendaItem");
        agendaItemActionBo.setAttributeBos(Arrays.asList(agendaItemActionAttributeBo));

        // add child agenda item attribute
        ActionAttributeBo whenAlwaysActionAttributeBo = new ActionAttributeBo();
        whenAlwaysActionAttributeBo.setAction(alwaysActionBo);
        whenAlwaysActionAttributeBo.setAttributeDefinition(attributeDefinitionBo);
        whenAlwaysActionAttributeBo.setValue("testAlwaysAgendaItem");
        alwaysActionBo.setAttributeBos(Arrays.asList(whenAlwaysActionAttributeBo));

        // update the agenda item
        AgendaItemDefinition updatedAgendaItem
                = getAgendaBoService().updateAgendaItem(AgendaItemDefinition.Builder.create(agendaItemBo).build());

        // verify the updated agenda item
        AgendaItemBo updatedAgendaItemBo = AgendaItemBo.from(updatedAgendaItem);
        assertNotNull("updated agenda item null", updatedAgendaItemBo);
        List<ActionBo> updatedAgendaItemActionBos = updatedAgendaItemBo.getRule().getActions();
        assertEquals("incorrect number of updated agenda item rule actions found", 1, updatedAgendaItemActionBos.size());
        ActionBo updatedAgendaItemActionBo = updatedAgendaItemActionBos.get(0);
        assertEquals("incorrect number of updated agenda item rule action attributes found", 1, updatedAgendaItemActionBo.getAttributes().size());
        assertEquals("incorrect number of updated agenda item rule action attributes found", 1, updatedAgendaItemActionBo.getAttributeBos().size());

        // verify the updated child agenda item
        AgendaItemBo updatedAlwaysBo = updatedAgendaItemBo.getAlways();
        assertNotNull("updated always agenda item null", updatedAlwaysBo);
        List<ActionBo> updatedAlwaysActionBos = updatedAlwaysBo.getRule().getActions();
        assertEquals("incorrect number of updated always agenda item rule actions found", 1, updatedAlwaysActionBos.size());
        ActionBo updatedAlwaysActionBo = updatedAlwaysActionBos.get(0);
        assertEquals("incorrect number of updated always agenda item rule action attributes found", 1, updatedAlwaysActionBo.getAttributes().size());
        assertEquals("incorrect number of updated always agenda item rule action attributes found", 1, updatedAlwaysActionBo.getAttributeBos().size());

        // Check to make sure there are no orphaned records
        KrmsAttributeDefinition orgAttribute = getKrmsAttributeDefinitionService().getAttributeDefinitionById( attributeDefinition.getId() );
        assertEquals("incorrect number of updated attributes found for the agenda", 1, getAgendaBoService().getAgendaByAgendaId(updatedAgendaItem.getAgendaId()).getAttributes().size());
        assertNotNull("Attribute definition orphaned on AgendaItem update", orgAttribute);

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


    @Test
    public void testOrphanActions(){
        AgendaDefinitionDataWrapper agendaWrapper = new AgendaDefinitionDataWrapper();

        // update the Action in the Rule
        List<ActionDefinition> actionDefinitionList = agendaWrapper.firstItemRule.getActions();
        ActionDefinition orgAction = actionDefinitionList.get(0);

        // create rule actions
        RuleDefinition.Builder rule1Builder = RuleDefinition.Builder.create( agendaWrapper.firstItemRule );
        List<ActionDefinition.Builder> newActionList = new ArrayList<ActionDefinition.Builder>();

        ActionDefinition.Builder actionDefBuilder1 = ActionDefinition.Builder.create(null,
                agendaWrapper.firstItemRule.getName() + "::UpdatedAction", NAMESPACE1, createKrmsActionTypeDefinition(NAMESPACE1)
                .getId(), agendaWrapper.firstItemRule.getId(), 1);
        newActionList.add(actionDefBuilder1);
        rule1Builder.setActions(newActionList);
        RuleDefinition updatedFirstRule = rule1Builder.build();
        ruleBoService.updateRule(updatedFirstRule);

        ActionDefinition shouldBeDeletedAction = actionBoService.getActionByActionId(orgAction.getId());
        assertNull("Original Action should have been removed", shouldBeDeletedAction);
    }

    @Test
    public void testOrphanPropositions(){
        AgendaDefinitionDataWrapper agendaWrapper = new AgendaDefinitionDataWrapper();

        // update the Action in the Rule
        List<ActionDefinition> actionDefinitionList = agendaWrapper.firstItemRule.getActions();
        ActionDefinition orgAction = actionDefinitionList.get(0);

        RuleDefinition.Builder rule1Builder = RuleDefinition.Builder.create(agendaWrapper.firstItemRule);
        // update rule proposition
        PropositionDefinition.Builder parentProposition =
                PropositionDefinition.Builder.create(null, PropositionType.COMPOUND.getCode(),
                        agendaWrapper.firstItemRule.getId(), null, null);
        parentProposition.setCompoundComponents(new ArrayList<PropositionDefinition.Builder>());

        agendaWrapper.firstItemRule = ruleBoService.getRuleByRuleId( agendaWrapper.firstItemRule.getId() );
        RuleDefinition.Builder rule2Builder = RuleDefinition.Builder.create(agendaWrapper.firstItemRule);


        PropositionParametersBuilder params1 = new PropositionParametersBuilder();
        params1.add(createTermDefinition(CAMPUS_CODE_TERM_NAME, String.class, agendaWrapper.context).getId(), PropositionParameterType.TERM);
        params1.add("DC", PropositionParameterType.CONSTANT);
        params1.add("=", PropositionParameterType.OPERATOR);

        PropositionParametersBuilder params2 = new PropositionParametersBuilder();
        params2.add(createTermDefinition(CAMPUS_CODE_TERM_NAME, String.class, agendaWrapper.context).getId(),
                PropositionParameterType.TERM);
        params2.add("DD", PropositionParameterType.CONSTANT);
        params2.add("=", PropositionParameterType.OPERATOR);

        StringBuilder propositionNameBuilder = new StringBuilder(agendaWrapper.firstItemRule.getName());

        PropositionDefinition.Builder propositionBuilder =
                createPropositionDefinition(propositionNameBuilder.toString(), params1, agendaWrapper.firstItemRule);
        parentProposition.getCompoundComponents().add(propositionBuilder);


        PropositionDefinition.Builder proposition2Builder =
                createPropositionDefinition(propositionNameBuilder.toString(), params2, agendaWrapper.firstItemRule);
        parentProposition.getCompoundComponents().add(proposition2Builder);

        rule2Builder.setProposition(parentProposition);
        RuleDefinition updatedFirstRule = rule2Builder.build();
        RuleDefinition updatedRuleDef = ruleBoService.updateRule(updatedFirstRule);

        PropositionDefinition deletedParentProp = propositionBoService.getPropositionById(
                agendaWrapper.firstItemRule.getProposition().getId());
        PropositionDefinition deletedProp1 = propositionBoService.getPropositionById(
                agendaWrapper.firstItemRule.getProposition().getCompoundComponents().get(0).getId());
        PropositionDefinition deletedProp2 = propositionBoService.getPropositionById(
                agendaWrapper.firstItemRule.getProposition().getCompoundComponents().get(1).getId());

        assertNull("Old parent proposition should be removed", deletedParentProp);
        assertNull("Old compound proposition should be removed", deletedProp1);
        assertNull("Old compound proposition should be removed", deletedProp2);
    }

    @Test
    public void testDeleteAgenda() {
        ContextDefinition context = getContextRepository().getContextByNameAndNamespace(CONTEXT2, NAMESPACE2);
        assertNotNull("context " + CONTEXT2 + " not found", context);
        AgendaDefinition agenda = getAgendaBoService().getAgendaByNameAndContextId(AGENDA2, context.getId());
        assertNotNull("agenda " + AGENDA2 + " not found", agenda);
        AgendaItemDefinition agendaItem = getAgendaBoService().getAgendaItemById(agenda.getFirstItemId());
        assertNotNull("agenda item " + agenda.getFirstItemId() + " not found", agendaItem);

        AgendaItemDefinition firstItem = getAgendaBoService().getAgendaItemById(agenda.getFirstItemId());
        RuleDefinition firstItemRule = firstItem.getRule();
        List<ActionDefinition> firstActionDefinitions = firstItemRule.getActions();

        AgendaItemDefinition secondItem = firstItem.getAlways();
        RuleDefinition secondItemRule =   secondItem.getRule();
        List<ActionDefinition> secondActionDefinitions = secondItemRule.getActions();

        AgendaItemDefinition thirdItem = secondItem.getAlways();
        RuleDefinition thirdItemRule = thirdItem.getRule();
        List<ActionDefinition> thirdActionDefinitions = thirdItemRule.getActions();

        getAgendaBoService().deleteAgenda(agenda.getId());

        AgendaDefinition deletedAgenda = getAgendaBoService().getAgendaByAgendaId( agenda.getId() );
        assertNull("Agenda should have been deleted", deletedAgenda);

        AgendaItemDefinition deletedAgendaItem =  getAgendaBoService().getAgendaItemById( firstItem.getId() );
        assertNull("First Agenda item should have been deleted as part of agenda delete", deletedAgendaItem);

        AgendaItemDefinition deletedSecondAgendaItem =  getAgendaBoService().getAgendaItemById( secondItem.getId() );
        assertNull("Second Agenda item should have been deleted as part of agenda delete", deletedSecondAgendaItem);

        AgendaItemDefinition deletedThirdAgendaItem =  getAgendaBoService().getAgendaItemById( thirdItem.getId() );
        assertNull("Third Agenda item should have been deleted as part of agenda delete", deletedThirdAgendaItem);

        RuleDefinition deletedFirstRule = getRuleBoService().getRuleByRuleId(firstItemRule.getId());
        assertNull("First rule should have been deleted as part of agenda delete", deletedFirstRule);

        RuleDefinition deletedSecondRule = getRuleBoService().getRuleByRuleId(secondItemRule.getId());
        assertNull("Second rule should have been deleted as part of agenda delete", deletedSecondRule);

        RuleDefinition deletedThirdRule = getRuleBoService().getRuleByRuleId(thirdItemRule.getId());
        assertNull("Third rule should have been deleted as part of agenda delete", deletedThirdRule);

        List<ActionDefinition> actionDefinitionList = new ArrayList<ActionDefinition>();

        for (ActionDefinition actionDefinition: firstActionDefinitions) {
            actionDefinitionList.add(actionDefinition);
        }

        for (ActionDefinition actionDefinition: secondActionDefinitions) {
            actionDefinitionList.add(actionDefinition);
        }

        for (ActionDefinition actionDefinition: thirdActionDefinitions) {
            actionDefinitionList.add(actionDefinition);
        }

        for(ActionDefinition actionDef : actionDefinitionList) {
            ActionDefinition deletedActionDef = getActionBoService().getActionByActionId( actionDef.getId() );
            assertNull("All action definitions not deleted as part of agenda delete", deletedActionDef);
        }
    }

    @Test
    public void testDeleteAgenda_WithNoAgendaItems() {
        AgendaDefinition emptyAgendaDefinition = createEmptyAgenda(CONTEXT1, "testAgenda-deleteEmptyAgenda");

        // get it as a business object
        final AgendaBo bo = getDataObjectService().find(AgendaBo.class, emptyAgendaDefinition.getId());

        getAgendaBoService().deleteAgenda(bo.getId());

        AgendaDefinition deletedAgenda = getAgendaBoService().getAgendaByAgendaId( bo.getId() );
        assertNull("Agenda should have been deleted", deletedAgenda);
    }

    private AgendaDefinition createEmptyAgenda(String contextName, String agendaName) {
        String namespace = getNamespaceByContextName(contextName);
        if (StringUtils.isBlank(namespace)) {
            throw new RiceRuntimeException("namespace is " + namespace + " for context with name " + CONTEXT1);
        }

        ContextDefinition context = getContextRepository().getContextByNameAndNamespace(CONTEXT1, namespace);

        // Get an agenda to use as a template for agenda creation
        List<AgendaDefinition> agendas = getAgendaBoService().getAgendasByContextId(context.getId());
        AgendaDefinition templateAgenda = agendas.get(0);

        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(templateAgenda);

        agendaBuilder.setFirstItemId(null);
        agendaBuilder.setId(null);
        agendaBuilder.setVersionNumber(null);
        agendaBuilder.setName("testAgendaCrud-agenda");

        // create agenda
        AgendaDefinition newAgenda = getAgendaBoService().createAgenda(agendaBuilder.build());

        return newAgenda;
    }

    @Test
    public void testAgendaCrud() {

        // create agenda
        AgendaDefinition newAgenda = createEmptyAgenda(CONTEXT1, "testAgendaCrud-agenda");

        // verify the agenda is there and
        assertNotNull(newAgenda);
        // we depend on working equals for AgendaDefinition here
        assertEquals(newAgenda, getAgendaBoService().getAgendaByAgendaId(newAgenda.getId()));

//        List<AgendaItemDefinition> templateAgendaItems = getAgendaBoService().getAgendaItemsByAgendaId(templateAgenda.getId());
//        List<AgendaItemDefinition.Builder> agendaItemBuilders = new ArrayList<AgendaItemDefinition.Builder>();
//
//        for (AgendaItemDefinition templateAgendaItem : templateAgendaItems) {
//            AgendaItemDefinition.Builder agendaItemBuilder = AgendaItemDefinition.Builder.create(templateAgendaItem);
//            agendaItemBuilder.setAlwaysId(null);
//            agendaItemBuilder.setWhenFalseId(null);
//            agendaItemBuilder.setWhenTrueId(null);
//            agendaItemBuilder.setAgendaId(newAgenda.getId());
//            agendaItemBuilder.set
//        }

    }

    /**
     * This uses an Agenda which has 4 Agenda Items, 3 of which are nested.  The Agenda is updated with all
     * new Agenda Items, also nested 3 deep.  The Rules, Proposition and Actions in the original are checked
     * to be sure they are not orphaned after the update.  The new Agenda is tested to see if the new Agenda Items
     * and Rules exist.
     */
    @Test
    public void testUpdateAgendaMega() {
        AgendaDefinitionDataWrapper agendaWrapper = new AgendaDefinitionDataWrapper();
        ContextDefinition contextDef = agendaWrapper.context;
        AgendaDefinition agendaDef = agendaWrapper.agenda;

        AgendaItemDefinition agendaItemDefOriginal1 = agendaWrapper.firstItem;
        AgendaItemDefinition agendaItemDefOriginal2 = agendaWrapper.secondItem;
        AgendaItemDefinition agendaItemDefOriginal3 = agendaWrapper.thirdItem;
        AgendaItemDefinition agendaItemDefOriginal4 = agendaWrapper.fourthItem;

        // Existing Rules
        RuleDefinition ruleDef1 = agendaItemDefOriginal1.getRule();
        RuleDefinition ruleDef2 = agendaItemDefOriginal2.getRule();
        RuleDefinition ruleDef3 = agendaItemDefOriginal3.getRule();
        RuleDefinition ruleDef4 = agendaItemDefOriginal4.getRule();

        // Existing Propositions
        PropositionDefinition propDef1 = ruleDef1.getProposition();
        PropositionDefinition propDef2 = ruleDef2.getProposition();
        PropositionDefinition propDef3 = ruleDef3.getProposition();
        PropositionDefinition propDef4 = ruleDef4.getProposition();

        // Existing Actions
        List<ActionDefinition> actionDefs1 = ruleDef1.getActions();
        List<ActionDefinition> actionDefs2 = ruleDef2.getActions();
        List<ActionDefinition> actionDefs3 = ruleDef3.getActions();
        List<ActionDefinition> actionDefs4 = ruleDef4.getActions();

        // "Build" new Agenda Item Defs and Rules
        AgendaItemDefinition.Builder agendaItemDefBuilder1 = AgendaItemDefinition.Builder.create(null, agendaDef.getId());
        agendaItemDefBuilder1.setRuleId(createRuleDefinition1(contextDef, "New Agenda", NAMESPACE1).getId());
        agendaItemDefBuilder1.setRule(RuleDefinition.Builder.create(ruleBoService.getRuleByRuleId(
                agendaItemDefBuilder1.getRuleId())));

        AgendaItemDefinition.Builder agendaItemDefBuilder2 = AgendaItemDefinition.Builder.create(null, agendaDef.getId());
        agendaItemDefBuilder1.setAlways(agendaItemDefBuilder2);
        agendaItemDefBuilder2.setRuleId(createRuleDefinition2(contextDef, "New Agenda", NAMESPACE1).getId());
        agendaItemDefBuilder2.setRule(RuleDefinition.Builder.create(ruleBoService.getRuleByRuleId(
                agendaItemDefBuilder2.getRuleId())));

        AgendaItemDefinition.Builder agendaItemDefBuilder3 = AgendaItemDefinition.Builder.create(null, agendaDef.getId());
        agendaItemDefBuilder2.setAlways(agendaItemDefBuilder3);
        agendaItemDefBuilder3.setRuleId(createRuleDefinition3(contextDef, "New Agenda", NAMESPACE1).getId());
        agendaItemDefBuilder3.setRule(RuleDefinition.Builder.create(ruleBoService.getRuleByRuleId(
                agendaItemDefBuilder3.getRuleId())));

        AgendaItemDefinition.Builder agendaItemDefBuilder4 = AgendaItemDefinition.Builder.create(null, agendaDef.getId());
        agendaItemDefBuilder3.setAlways(agendaItemDefBuilder4);
        agendaItemDefBuilder4.setRuleId(createRuleDefinition4(contextDef, "New Agenda", NAMESPACE1).getId());
        agendaItemDefBuilder4.setRule(RuleDefinition.Builder.create(ruleBoService.getRuleByRuleId(
                agendaItemDefBuilder4.getRuleId())));

        // Create 4 the new Agenda Item Defs, nesting them
        AgendaItemDefinition agendaItemDefNew4 = agendaBoService.createAgendaItem(agendaItemDefBuilder4.build());

        agendaItemDefBuilder3.setAlwaysId(agendaItemDefNew4.getId());
        agendaItemDefBuilder3.setAlways(AgendaItemDefinition.Builder.create(agendaItemDefNew4));
        AgendaItemDefinition agendaItemDefNew3 = agendaBoService.createAgendaItem(agendaItemDefBuilder3.build());

        agendaItemDefBuilder2.setAlwaysId(agendaItemDefNew3.getId());
        agendaItemDefBuilder2.setAlways(AgendaItemDefinition.Builder.create(agendaItemDefNew3));
        AgendaItemDefinition agendaItemDefNew2 = agendaBoService.createAgendaItem(agendaItemDefBuilder2.build());

        agendaItemDefBuilder1.setAlwaysId(agendaItemDefNew2.getId());
        agendaItemDefBuilder1.setAlways(AgendaItemDefinition.Builder.create(agendaItemDefNew2));
        AgendaItemDefinition agendaItemDefNew1 = agendaBoService.createAgendaItem(agendaItemDefBuilder1.build());

        AgendaDefinition.Builder agendaDefBuilder = AgendaDefinition.Builder.create(agendaDef);
        agendaDefBuilder.setFirstItemId(agendaItemDefNew1.getId());

        agendaDef = agendaDefBuilder.build();

        // Update Agenda Definition
        AgendaDefinition agendaDefUpdated = agendaBoService.updateAgenda(agendaDef);

        // Original Agenda Item Defs deleted?
        assertNull("First agenda item should be deleted", agendaBoService.getAgendaItemById(agendaItemDefOriginal1.getId()));
        assertNull("Second agenda item should be deleted", agendaBoService.getAgendaItemById(agendaItemDefOriginal2.getId()));
        assertNull("Third agenda item should be deleted", agendaBoService.getAgendaItemById(agendaItemDefOriginal3.getId()));
        assertNull("Fourth agenda item should be deleted", agendaBoService.getAgendaItemById(agendaItemDefOriginal4.getId()));

        // Original Rules deleted?
        assertNull("First agenda item rule should be deleted", ruleBoService.getRuleByRuleId(ruleDef1.getId()));
        assertNull("Second agenda item rule should be deleted", ruleBoService.getRuleByRuleId(ruleDef2.getId()));
        assertNull("Third agenda item rule should be deleted", ruleBoService.getRuleByRuleId(ruleDef3.getId()));
        assertNull("Fourth agenda item rule should be deleted", ruleBoService.getRuleByRuleId(ruleDef4.getId()));

        // Original Proposition and Compound Components deleted?
        assertNull("First rule proposition should be deleted", propositionBoService.getPropositionById(propDef1.getId()));
        assertNull("First rule compound component 0 should be deleted",
                propositionBoService.getPropositionById(propDef1.getCompoundComponents().get(0).getId()));
        assertNull("First rule compound component 1 should be deleted",
                propositionBoService.getPropositionById(propDef1.getCompoundComponents().get(1).getId()));

        assertNull("Second rule proposition should be deleted", propositionBoService.getPropositionById(propDef2.getId()));
        assertNull("Second rule compound component 0 should be deleted",
                propositionBoService.getPropositionById(propDef2.getCompoundComponents().get(0).getId()));
        assertNull("Second rule compound component 1 should be deleted",
                propositionBoService.getPropositionById(propDef2.getCompoundComponents().get(1).getId()));

        // This one does not have any Compound Components (not sure why)
        assertNull("Second rule proposition should be deleted", propositionBoService.getPropositionById(propDef3.getId()));

        assertNull("Fourth rule proposition should be deleted", propositionBoService.getPropositionById(propDef4.getId()));
        assertNull("Fourth rule compound component 0 should be deleted",
                propositionBoService.getPropositionById(propDef4.getCompoundComponents().get(0).getId()));
        assertNull("Fourth rule compound component 1 should be deleted",
                propositionBoService.getPropositionById(propDef4.getCompoundComponents().get(1).getId()));

        // Actions deleted?
        assertNull("First rule action should be deleted", actionBoService.getActionByActionId(actionDefs1.get(0).getId()));
        assertNull("Second rule action should be deleted", actionBoService.getActionByActionId(actionDefs2.get(0).getId()));
        assertNull("Third rule action should be deleted", actionBoService.getActionByActionId(actionDefs3.get(0).getId()));
        assertNull("Fourth rule action should be deleted", actionBoService.getActionByActionId(actionDefs4.get(0).getId()));

        // New Agenda Items there?
        AgendaItemDefinition agendaItemDefVerify1 = getAgendaBoService().getAgendaItemById(agendaDefUpdated.getFirstItemId());
        AgendaItemDefinition agendaItemDefVerify2 = agendaItemDefVerify1.getAlways();
        AgendaItemDefinition agendaItemDefVerify3 = agendaItemDefVerify2.getAlways();
        AgendaItemDefinition agendaItemDefVerify4 = agendaItemDefVerify3.getAlways();

        assertNotNull("First updated agenda item should not be null", agendaItemDefVerify1);
        assertNotNull("Second updated agenda item should not be null", agendaItemDefVerify2);
        assertNotNull("Third updated agenda item should not be null", agendaItemDefVerify3);
        assertNotNull("Fourth updated agenda item should not be null", agendaItemDefVerify4);

        // New Rules there?
        assertNotNull("First agenda item rule should not be null", ruleBoService.getRuleByRuleId(agendaItemDefVerify1.getRuleId()));
        assertNotNull("Second agenda item rule should not be null", ruleBoService.getRuleByRuleId(agendaItemDefVerify2.getRuleId()));
        assertNotNull("Third agenda item rule should not be null", ruleBoService.getRuleByRuleId(agendaItemDefVerify3.getRuleId()));
        assertNotNull("Fourth agenda item rule should not be null", ruleBoService.getRuleByRuleId(agendaItemDefVerify4.getRuleId()));
    }

    @Test
    public void testUpdateAgendaPullLowerTwo () {
        ContextDefinition context = getContextRepository().getContextByNameAndNamespace(CONTEXT2, NAMESPACE2);
        assertNotNull("context " + CONTEXT2 + " not found", context);
        AgendaDefinition agendaDef = getAgendaBoService().getAgendaByNameAndContextId(AGENDA3, context.getId());
        assertNotNull("agenda " + AGENDA3 + " not found", agendaDef);

        AgendaItemDefinition agendaItemDefOriginal1 = getAgendaBoService().getAgendaItemById(agendaDef.getFirstItemId());
        assertNotNull("agenda item " + agendaDef.getFirstItemId() + " not found", agendaItemDefOriginal1);
        AgendaItemDefinition agendaItemDefOriginal2 = agendaItemDefOriginal1.getAlways();
        AgendaItemDefinition agendaItemDefOriginal3 = agendaItemDefOriginal2.getAlways();
        AgendaItemDefinition agendaItemDefOriginal4 = agendaItemDefOriginal3.getAlways();

        // Existing Rules
        RuleDefinition ruleDef1 = agendaItemDefOriginal1.getRule();
        RuleDefinition ruleDef2 = agendaItemDefOriginal2.getRule();
        RuleDefinition ruleDef3 = agendaItemDefOriginal3.getRule();
        RuleDefinition ruleDef4 = agendaItemDefOriginal4.getRule();

        // Existing Propositions
        PropositionDefinition propDef1 = ruleDef1.getProposition();
        PropositionDefinition propDef2 = ruleDef2.getProposition();
        PropositionDefinition propDef3 = ruleDef3.getProposition();
        PropositionDefinition propDef4 = ruleDef4.getProposition();

        // Existing Actions
        List<ActionDefinition> actionDefs1 = ruleDef1.getActions();
        List<ActionDefinition> actionDefs2 = ruleDef2.getActions();
        List<ActionDefinition> actionDefs3 = ruleDef3.getActions();
        List<ActionDefinition> actionDefs4 = ruleDef4.getActions();

        // Rebuild based on AgendaItemDefs 1 and 2.
        AgendaItemDefinition.Builder agendaItemDefBuilder1 = AgendaItemDefinition.Builder.create(agendaItemDefOriginal1);
        AgendaItemDefinition.Builder agendaItemDefBuilder2 = AgendaItemDefinition.Builder.create(agendaItemDefOriginal2);

        // Force force the always list to end at AgendaItemDef 2
        agendaItemDefBuilder2.setAlwaysId(null);
        agendaItemDefBuilder2.setAlways(null);

        // Build
        AgendaItemDefinition agendaItemDefNew2 = agendaItemDefBuilder2.build();
        AgendaItemDefinition agendaItemDefNew1 = agendaItemDefBuilder1.build();

        // update AgendaItemDef 2.
        agendaBoService.updateAgendaItem(agendaItemDefNew2);

        // Build new AgendaDef based on existing one
        AgendaDefinition.Builder agendaDefBuilder = AgendaDefinition.Builder.create(agendaDef);
        agendaDefBuilder.setFirstItemId(agendaItemDefNew1.getId());
        agendaDef = agendaDefBuilder.build();

        // Update Agenda Definition
        AgendaDefinition agendaDefUpdated = agendaBoService.updateAgenda(agendaDef);

        // Original Agenda Item Defs deleted?
        assertNull("Third agenda item should be deleted", agendaBoService.getAgendaItemById(agendaItemDefOriginal3.getId()));
        assertNull("Fourth agenda item should be deleted", agendaBoService.getAgendaItemById(agendaItemDefOriginal4.getId()));

        // Original Rules deleted?
        RuleDefinition ruleDefCheck = ruleBoService.getRuleByRuleId(ruleDef3.getId());
        assertNull("Third agenda item rule should be deleted", ruleBoService.getRuleByRuleId(ruleDef3.getId()));
        assertNull("Fourth agenda item rule should be deleted", ruleBoService.getRuleByRuleId(ruleDef4.getId()));

        // Original Proposition and Compound Components deleted?
        // This rule does not have any Compound Components (not sure why)
        assertNull("Third rule proposition should be deleted", propositionBoService.getPropositionById(propDef3.getId()));

        assertNull("Fourth rule proposition should be deleted", propositionBoService.getPropositionById(propDef4.getId()));
        assertNull("Fourth rule compound component 0 should be deleted",
                propositionBoService.getPropositionById(propDef4.getCompoundComponents().get(0).getId()));
        assertNull("Fourth rule compound component 1 should be deleted",
                propositionBoService.getPropositionById(propDef4.getCompoundComponents().get(1).getId()));

        // Actions deleted?
        assertNull("Third rule action should be deleted", actionBoService.getActionByActionId(actionDefs3.get(0).getId()));
        assertNull("Fourth rule action should be deleted", actionBoService.getActionByActionId(actionDefs4.get(0).getId()));

        // New Agenda Items there?
        AgendaItemDefinition agendaItemDefVerify1 = getAgendaBoService().getAgendaItemById(agendaDefUpdated.getFirstItemId());
        AgendaItemDefinition agendaItemDefVerify2 = agendaItemDefVerify1.getAlways();

        assertNotNull("First updated agenda item should not be null", agendaItemDefVerify1);
        assertNotNull("Second updated agenda item should not be null", agendaItemDefVerify2);

        // New Rules there?
        assertNotNull("First agenda item rule should not be null", ruleBoService.getRuleByRuleId(agendaItemDefVerify1.getRuleId()));
        assertNotNull("Second agenda item rule should not be null", ruleBoService.getRuleByRuleId(agendaItemDefVerify2.getRuleId()));
    }

}
