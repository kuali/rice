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

    // TODO:

    // methods left to test:
    //
    // deleteAgendaItem
    // updateAgenda
    // deleteAgenda
    // createAgendaItem

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

        RuleDefinition.Builder rule1Builder = RuleDefinition.Builder.create( agendaWrapper.firstItemRule );
        // update rule proposition
        PropositionDefinition.Builder parentProposition =
                PropositionDefinition.Builder.create(null, PropositionType.COMPOUND.getCode(), agendaWrapper.firstItemRule.getId(),
                        null, null);
        parentProposition.setCompoundComponents(new ArrayList<PropositionDefinition.Builder>());

        agendaWrapper.firstItemRule = ruleBoService.getRuleByRuleId( agendaWrapper.firstItemRule.getId() );
        RuleDefinition.Builder rule2Builder = RuleDefinition.Builder.create( agendaWrapper.firstItemRule );


        PropositionParametersBuilder params1 = new PropositionParametersBuilder();
        params1.add(createTermDefinition(CAMPUS_CODE_TERM_NAME, String.class, agendaWrapper.context).getId(), PropositionParameterType.TERM);
        params1.add("DC", PropositionParameterType.CONSTANT);
        params1.add("=", PropositionParameterType.OPERATOR);

        PropositionParametersBuilder params2 = new PropositionParametersBuilder();
        params2.add(createTermDefinition(CAMPUS_CODE_TERM_NAME, String.class, agendaWrapper.context).getId(), PropositionParameterType.TERM);
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

    // The asserts should be uncommented once KULRICE- 14014 and KULRICE - 14015 have been fixed.
    @Test
    public void testOrphanAgendaItems() {
        AgendaDefinitionDataWrapper agendaWrapper = new AgendaDefinitionDataWrapper();

        // updating single agendaItem
        AgendaItemDefinition.Builder agendaItemBuilder = AgendaItemDefinition.Builder.create( agendaWrapper.firstItem );
        RuleDefinition nRule = createRuleDefinition1(agendaWrapper.context, "Updated Agenda", NAMESPACE1);
        agendaItemBuilder.setRuleId(nRule.getId());

        RuleDefinition n1Rule = ruleBoService.getRuleByRuleId( nRule.getId() );

        agendaItemBuilder.setRule( RuleDefinition.Builder.create( n1Rule) );
        AgendaItemDefinition updatedItem = agendaItemBuilder.build();
        AgendaItemDefinition updatedAgendaItem = agendaBoService.updateAgendaItem(updatedItem);

        RuleDefinition deletedRule = ruleBoService.getRuleByRuleId( agendaWrapper.firstItemRule.getId() );
        //  assertNull("Old rule should be deleted after updating agendaItem", deletedRule);

        // updating all agendaItems and their rules  through updateAgenda
        AgendaItemDefinition.Builder agendaItemBuilder1 = AgendaItemDefinition.Builder.create(null, agendaWrapper.agenda.getId());
        agendaItemBuilder1.setRuleId(createRuleDefinition1(agendaWrapper.context, "New Agenda", NAMESPACE1).getId());
        agendaItemBuilder1.setRule(RuleDefinition.Builder.create(ruleBoService.getRuleByRuleId(agendaItemBuilder1.getRuleId())));

        AgendaItemDefinition.Builder agendaItemBuilder2 = AgendaItemDefinition.Builder.create(null, agendaWrapper.agenda.getId());
        agendaItemBuilder1.setAlways(agendaItemBuilder2);
        agendaItemBuilder2.setRuleId(createRuleDefinition2(agendaWrapper.context,"New Agenda", NAMESPACE1).getId());
        agendaItemBuilder2.setRule(RuleDefinition.Builder.create(ruleBoService.getRuleByRuleId(agendaItemBuilder2.getRuleId())));

        AgendaItemDefinition.Builder agendaItemBuilder3 = AgendaItemDefinition.Builder.create(null, agendaWrapper.agenda.getId());
        agendaItemBuilder2.setAlways(agendaItemBuilder3);
        agendaItemBuilder3.setRuleId(createRuleDefinition3(agendaWrapper.context, "New Agenda", NAMESPACE1).getId());
        agendaItemBuilder3.setRule(RuleDefinition.Builder.create(ruleBoService.getRuleByRuleId(agendaItemBuilder3.getRuleId())));

        AgendaItemDefinition.Builder agendaItemBuilder4 = AgendaItemDefinition.Builder.create(null, agendaWrapper.agenda.getId());
        agendaItemBuilder3.setAlways(agendaItemBuilder4);
        agendaItemBuilder4.setRuleId(createRuleDefinition4(agendaWrapper.context, "New Agenda", NAMESPACE1).getId());
        agendaItemBuilder4.setRule(RuleDefinition.Builder.create(ruleBoService.getRuleByRuleId(agendaItemBuilder4.getRuleId())));

        AgendaItemDefinition agendaItem4 = agendaBoService.createAgendaItem(agendaItemBuilder4.build());

        agendaItemBuilder3.setAlwaysId(agendaItem4.getId());
        agendaItemBuilder3.setAlways(AgendaItemDefinition.Builder.create(agendaItem4));
        AgendaItemDefinition agendaItem3 = agendaBoService.createAgendaItem(agendaItemBuilder3.build());

        agendaItemBuilder2.setAlwaysId(agendaItem3.getId());
        agendaItemBuilder2.setAlways(AgendaItemDefinition.Builder.create(agendaItem3));
        AgendaItemDefinition agendaItem2 = agendaBoService.createAgendaItem(agendaItemBuilder2.build());

        agendaItemBuilder1.setAlwaysId(agendaItem2.getId());
        agendaItemBuilder1.setAlways(AgendaItemDefinition.Builder.create(agendaItem2));
        AgendaItemDefinition agendaItem1 = agendaBoService.createAgendaItem(agendaItemBuilder1.build());

        AgendaDefinition.Builder agendaDefBuilder1 = AgendaDefinition.Builder.create(agendaWrapper.agenda);
        agendaDefBuilder1.setFirstItemId(agendaItem1.getId());
        AgendaDefinition agenda = agendaDefBuilder1.build();

        AgendaDefinition result = agendaBoService.updateAgenda(agenda);

        AgendaItemDefinition deletedItem1 = agendaBoService.getAgendaItemById( agendaWrapper.firstItem.getId() );
        AgendaItemDefinition deletedItem2 = agendaBoService.getAgendaItemById( agendaWrapper.secondItem.getId() );
        AgendaItemDefinition deletedItem3 = agendaBoService.getAgendaItemById( agendaWrapper.thirdItem.getId() );
        AgendaItemDefinition deletedItem4 = agendaBoService.getAgendaItemById( agendaWrapper.fourthItem.getId() );

        RuleDefinition deletedRule1 = ruleBoService.getRuleByRuleId( agendaWrapper.firstItem.getRuleId() );
        RuleDefinition deletedRule2 = ruleBoService.getRuleByRuleId( agendaWrapper.secondItem.getRuleId() );
        RuleDefinition deletedRule3 = ruleBoService.getRuleByRuleId( agendaWrapper.thirdItem.getRuleId() );
        RuleDefinition deletedRule4 = ruleBoService.getRuleByRuleId( agendaWrapper.fourthItem.getRuleId() );

//                assertNull("First item should be deleted", deletedItem1);
//                assertNull("Second item should be deleted", deletedItem2);
//                assertNull("Third item should be deleted", deletedItem3);
//                assertNull("Fourth item should be deleted", deletedItem4);
//
//                assertNull("First Item's rule should be deleted", deletedRule1);
//                assertNull("Second Item's rule should be deleted", deletedRule2);
//                assertNull("Third Item's rule should be deleted", deletedRule3);
//                assertNull("Fourth Item's rule should be deleted", deletedRule4);

    }

    @Test
    public void testDeleteAgenda() {
        ContextDefinition context = getContextRepository().getContextByNameAndNamespace(CONTEXT2, NAMESPACE2);
        assertNotNull("context " + CONTEXT2 + " not found", context);
        AgendaDefinition agenda = getAgendaBoService().getAgendaByNameAndContextId(AGENDA2, context.getId());
        assertNotNull("agenda " + AGENDA2 + " not found", agenda);
        AgendaItemDefinition agendaItem = getAgendaBoService().getAgendaItemById(agenda.getFirstItemId());
        assertNotNull("agenda item " + agenda.getFirstItemId() + " not found", agendaItem);

        AgendaItemDefinition firstItem = getAgendaBoService().getAgendaItemById( agenda.getFirstItemId() );
        RuleDefinition firstItemRule = firstItem.getRule();
        List<ActionDefinition> firstActionDefinitions = firstItemRule.getActions();

        AgendaItemDefinition secondItem = firstItem.getAlways();
        RuleDefinition secondItemRule =   secondItem.getRule();
        List<ActionDefinition> secondActionDefinitions = secondItemRule.getActions();

        AgendaItemDefinition thirdItem = secondItem.getAlways();
        RuleDefinition thirdItemRule = thirdItem.getRule();
        List<ActionDefinition> thirdActionDefinitions = thirdItemRule.getActions();

        getAgendaBoService().deleteAgenda( agenda.getId() );

        AgendaDefinition deletedAgenda = getAgendaBoService().getAgendaByAgendaId( agenda.getId() );
        assertNull("Agenda should have been deleted", deletedAgenda);

        AgendaItemDefinition deletedAgendaItem =  getAgendaBoService().getAgendaItemById( firstItem.getId() );
        assertNull("First Agenda item should have been deleted as part of agenda delete", deletedAgendaItem);

        AgendaItemDefinition deletedSecondAgendaItem =  getAgendaBoService().getAgendaItemById( secondItem.getId() );
        assertNull("Second Agenda item should have been deleted as part of agenda delete", deletedSecondAgendaItem);

        AgendaItemDefinition deletedThirdAgendaItem =  getAgendaBoService().getAgendaItemById( thirdItem.getId() );
        assertNull("Third Agenda item should have been deleted as part of agenda delete", deletedThirdAgendaItem);

        RuleDefinition deletedFirstRule = getRuleBoService().getRuleByRuleId( firstItemRule.getId() );
        assertNull("First rule should have been deleted as part of agenda delete", deletedFirstRule);

        RuleDefinition deletedSecondRule = getRuleBoService().getRuleByRuleId( secondItemRule.getId() );
        assertNull("Second rule should have been deleted as part of agenda delete", deletedSecondRule);

        RuleDefinition deletedThirdRule = getRuleBoService().getRuleByRuleId( thirdItemRule.getId() );
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

}
