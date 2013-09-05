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
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameter;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterType;
import org.kuali.rice.krms.api.repository.proposition.PropositionType;
import org.kuali.rice.krms.api.repository.reference.ReferenceObjectBinding;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermParameterDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.repository.NaturalLanguageTemplateBoServiceImpl;
import org.kuali.rice.krms.impl.repository.NaturalLanguageTemplateIntegrationGenTest;
import org.kuali.rice.krms.impl.repository.RuleManagementServiceImpl;
import org.kuali.rice.test.BaselineTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Base test case and methods for testing RuleManagementServiceImpl
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class RuleManagementBaseTest extends AbstractAgendaBoTest{

    public static final String TEST_PREFIX = "RuleManagementIT";
    RuleManagementServiceImpl ruleManagementServiceImpl;

    @Before
    public void setup() {
        ruleManagementServiceImpl = new RuleManagementServiceImpl();
        NaturalLanguageTemplateBoServiceImpl naturalLanguageTemplateBoServiceImpl = new NaturalLanguageTemplateBoServiceImpl();
        naturalLanguageTemplateBoServiceImpl.setNaturalLanguageTemplater(
        NaturalLanguageTemplateIntegrationGenTest.newStringReplaceTemplater());
        ruleManagementServiceImpl.setNaturalLanguageTemplateBoService(naturalLanguageTemplateBoServiceImpl);
        ruleManagementServiceImpl.setBusinessObjectService(getBoService()); // Business Object Service gets set to other Services
        termBoService = KrmsRepositoryServiceLocator.getTermBoService();
    }

    @Test
    public void ruleManagmentBaseTests() {
        String namespace = TEST_PREFIX + "Namespace";
        // create a context
        ContextDefinition.Builder contextDefinitionBuilder = ContextDefinition.Builder.create(
                TEST_PREFIX + "Namespace", TEST_PREFIX + "ContextName");
        contextDefinitionBuilder.setId(TEST_PREFIX + "ContextId");
        ContextDefinition contextDefinition = contextDefinitionBuilder.build();
        ruleManagementServiceImpl.createContext(contextDefinition );

        // create a agenda
        AgendaDefinition.Builder agendaDefinitionBuilder = AgendaDefinition.Builder.create(
                TEST_PREFIX + "AgendaId00", TEST_PREFIX + "AgendaName", null, TEST_PREFIX + "ContextId");

        AgendaDefinition agendaDefinition = agendaDefinitionBuilder.build();
        agendaDefinition = ruleManagementServiceImpl.createAgenda(agendaDefinition);


        // create some rules
        RuleDefinition rule00 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, "00"));
        RuleDefinition rule01 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, "01"));
        RuleDefinition rule02 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, "02"));
        RuleDefinition rule03 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, "03"));
        RuleDefinition rule04 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, "04"));
        RuleDefinition rule05 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, "05"));
        RuleDefinition rule06 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, "06"));

        AgendaItemDefinition agendaItem00 = ruleManagementServiceImpl.createAgendaItem(newTestAgendaItemDefinition("00", agendaDefinition.getId(), rule00.getId()));
        AgendaItemDefinition agendaItem01 = ruleManagementServiceImpl.createAgendaItem(newTestAgendaItemDefinition("01", agendaDefinition.getId(), rule01.getId()));
        AgendaItemDefinition agendaItem02 = ruleManagementServiceImpl.createAgendaItem(newTestAgendaItemDefinition("02", agendaDefinition.getId(), rule02.getId()));
        AgendaItemDefinition agendaItem03 = ruleManagementServiceImpl.createAgendaItem(newTestAgendaItemDefinition("03", agendaDefinition.getId(), rule03.getId()));
        AgendaItemDefinition agendaItem04 = ruleManagementServiceImpl.createAgendaItem(newTestAgendaItemDefinition("04", agendaDefinition.getId(), rule04.getId()));
        AgendaItemDefinition agendaItem05 = ruleManagementServiceImpl.createAgendaItem(newTestAgendaItemDefinition("05", agendaDefinition.getId(), rule05.getId()));
        AgendaItemDefinition agendaItem06 = ruleManagementServiceImpl.createAgendaItem(newTestAgendaItemDefinition("06", agendaDefinition.getId(), rule06.getId()));

        agendaItem00 = ruleManagementServiceImpl.getAgendaItem(agendaItem00.getId());
        agendaItem01 = ruleManagementServiceImpl.getAgendaItem(agendaItem01.getId());
        AgendaItemDefinition.Builder itemBuilder =  AgendaItemDefinition.Builder.create(agendaItem00);
        itemBuilder.setWhenTrue(AgendaItemDefinition.Builder.create(agendaItem01));
        itemBuilder.setWhenTrueId(agendaItem01.getId());
        agendaItem00 = itemBuilder.build();
        ruleManagementServiceImpl.updateAgendaItem(agendaItem00);

        // get updated agendaItem
        agendaItem00 = ruleManagementServiceImpl.getAgendaItem(agendaItem00.getId());
        agendaItem04 = ruleManagementServiceImpl.getAgendaItem(agendaItem04.getId());
        itemBuilder =  AgendaItemDefinition.Builder.create(agendaItem00);
        itemBuilder.setWhenFalse(AgendaItemDefinition.Builder.create(agendaItem04));
        itemBuilder.setWhenFalseId(agendaItem04.getId());
        agendaItem00 = itemBuilder.build();
        ruleManagementServiceImpl.updateAgendaItem(agendaItem00);

        agendaItem00 = ruleManagementServiceImpl.getAgendaItem(agendaItem00.getId());
        agendaItem05 = ruleManagementServiceImpl.getAgendaItem(agendaItem05.getId());
        itemBuilder =  AgendaItemDefinition.Builder.create(agendaItem00);
        itemBuilder.setAlways(AgendaItemDefinition.Builder.create(agendaItem05));
        itemBuilder.setAlwaysId(agendaItem05.getId());
        agendaItem00 = itemBuilder.build();
        ruleManagementServiceImpl.updateAgendaItem(agendaItem00);

        agendaItem01 = ruleManagementServiceImpl.getAgendaItem(agendaItem01.getId());
        agendaItem02 = ruleManagementServiceImpl.getAgendaItem(agendaItem02.getId());
        itemBuilder =  AgendaItemDefinition.Builder.create(agendaItem01);
        itemBuilder.setAlways(AgendaItemDefinition.Builder.create(agendaItem02));
        itemBuilder.setAlwaysId(agendaItem02.getId());
        agendaItem01 = itemBuilder.build();
        ruleManagementServiceImpl.updateAgendaItem(agendaItem01);


        agendaItem02 = ruleManagementServiceImpl.getAgendaItem(agendaItem02.getId());
        agendaItem06 = ruleManagementServiceImpl.getAgendaItem(agendaItem06.getId());
        itemBuilder =  AgendaItemDefinition.Builder.create(agendaItem02);
        itemBuilder.setWhenFalse(AgendaItemDefinition.Builder.create(agendaItem06));
        itemBuilder.setWhenFalseId(agendaItem06.getId());
        agendaItem02 = itemBuilder.build();
        ruleManagementServiceImpl.updateAgendaItem(agendaItem02);

        agendaItem02 = ruleManagementServiceImpl.getAgendaItem(agendaItem02.getId());
        agendaItem03 = ruleManagementServiceImpl.getAgendaItem(agendaItem03.getId());
        itemBuilder =  AgendaItemDefinition.Builder.create(agendaItem02);
        itemBuilder.setAlways(AgendaItemDefinition.Builder.create(agendaItem03));
        itemBuilder.setAlwaysId(agendaItem03.getId());
        agendaItem02 = itemBuilder.build();
        ruleManagementServiceImpl.updateAgendaItem(agendaItem02);

        agendaDefinitionBuilder = AgendaDefinition.Builder.create(agendaDefinition);
        agendaDefinitionBuilder.setFirstItemId(agendaItem00.getId());
        ruleManagementServiceImpl.updateAgenda(agendaDefinitionBuilder.build());

    }

    protected RuleDefinition createTestRule(String namespace, String ruleId) {
        return ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, ruleId));
    }

    protected RuleDefinition newTestRuleDefinition(String namespace, String testId) {
        String ruleId = TEST_PREFIX + "RuleId" + testId;

        PropositionDefinition prop = createTestPropositionForRule(testId);
        PropositionDefinition.Builder propBuilder = PropositionDefinition.Builder.create(prop);
        RuleDefinition.Builder builder = RuleDefinition.Builder.create(ruleId, ruleId + "Name", namespace, null, prop.getId());
        builder.setProposition(propBuilder);

        return builder.build();
    }

    protected AgendaItemDefinition newTestAgendaItemDefinition(String id, String agendaId, String ruleId) {
        AgendaItemDefinition.Builder agendaItemDefinitionBuilder = AgendaItemDefinition.Builder.create(id, agendaId);
        agendaItemDefinitionBuilder.setRuleId(ruleId);

        return agendaItemDefinitionBuilder.build();
    }

    protected ActionDefinition createTestActions(String actionId, String actionName, String actionDescr, int actionSequence, String ruleId, String namespace) {
        KrmsTypeDefinition krmsTypeDefinition = createKrmsActionTypeDefinition(namespace);

        // create rule if it does not exist
        if(ruleManagementServiceImpl.getRule(ruleId) == null) {
            RuleDefinition ruleDefinition = createTestRule(namespace, ruleId);
            ruleId = ruleDefinition.getId();
        }

        ActionDefinition actionDefinition = ActionDefinition.Builder.create(actionId,actionName,
                NAMESPACE1,krmsTypeDefinition.getId(),ruleId,actionSequence).build();

        assertNull("action["+actionId+"] should not already be in database:",ruleManagementServiceImpl.getAction(actionId ));

        actionDefinition =  ruleManagementServiceImpl.createAction(actionDefinition);
        ActionDefinition returnActionDefinition = ruleManagementServiceImpl.getAction(actionDefinition.getId());
        ActionDefinition.Builder builder = ActionDefinition.Builder.create(returnActionDefinition);
        builder.setDescription(actionDescr);
        ruleManagementServiceImpl.updateAction(builder.build());

        return ruleManagementServiceImpl.getAction(actionDefinition.getId());
    }

    protected PropositionDefinition createTestPropositionForRule(String ruleIdSuffix) {
        KrmsTypeDefinition krmsTypeDefinition = createKrmsActionTypeDefinition(NAMESPACE1);
        String ruleId = TEST_PREFIX + "RuleId" + ruleIdSuffix;
        String propId = "P" + ruleIdSuffix;

        return createTestSimpleProposition(NAMESPACE1, propId, "TSI_" + propId, "ABC", "=", "java.lang.String", ruleId,
                "TSI_" + propId + "_Descr");
    }

    protected PropositionDefinition createTestSimpleProposition(String namespace, String propId, String termSpecId, String propConstant, String propOperator, String termSpecType, String ruleId, String termSpecDescr){
        TermSpecificationDefinition termSpecificationDefinition = createTestTermSpecification(termSpecId, termSpecId, namespace, termSpecType, termSpecDescr);
        KrmsTypeDefinition krmsTypeDefinition = createKrmsActionTypeDefinition(  namespace, termSpecId, "testTypeService");

        List<PropositionParameter.Builder> propParam =  new ArrayList<PropositionParameter.Builder>();
        propParam.add(PropositionParameter.Builder.create(propId + "_0", "unused_notnull", termSpecId, PropositionParameterType.TERM.getCode(), 0));
        propParam.add(PropositionParameter.Builder.create(propId + "_1", "unused_notnull", propConstant, PropositionParameterType.CONSTANT.getCode(), 1));
        propParam.add(PropositionParameter.Builder.create(propId + "_2", "unused_notnull", propOperator, PropositionParameterType.OPERATOR.getCode(), 2));
        PropositionDefinition.Builder propBuilder = PropositionDefinition.Builder.create(null, PropositionType.SIMPLE.getCode(), ruleId, krmsTypeDefinition.getId(), propParam);
        propBuilder.setDescription(propId + "_simple_proposition");

        return ruleManagementServiceImpl.createProposition(propBuilder.build());
    }

    protected TermSpecificationDefinition createTestTermSpecification(String termSpecId, String termSpecName, String namespace, String type, String termSpecDescr){
        TermSpecificationDefinition termSpecificationDefinition = termBoService.getTermSpecificationByNameAndNamespace(termSpecName,namespace);
        if(termSpecificationDefinition != null) {

            return termSpecificationDefinition;
        }

        TermSpecificationDefinition.Builder termSpecificationDefinitionBuilder =
                TermSpecificationDefinition.Builder.create(null, termSpecName, namespace, type);
        termSpecificationDefinitionBuilder.setDescription(termSpecDescr);
        termSpecificationDefinition = termSpecificationDefinitionBuilder.build();

        return termBoService.createTermSpecification(termSpecificationDefinition);
    }

    protected KrmsTypeDefinition createKrmsActionTypeDefinition(String nameSpace, String typeName, String serviceName) {
        KrmsTypeDefinition krmsActionTypeDefinition =  krmsTypeRepository.getTypeByName(nameSpace, typeName);

        if (krmsActionTypeDefinition == null) {
            KrmsTypeDefinition.Builder krmsActionTypeDefnBuilder = KrmsTypeDefinition.Builder.create(typeName, nameSpace);
            krmsActionTypeDefnBuilder.setServiceName(serviceName);
            krmsActionTypeDefinition = krmsTypeRepository.createKrmsType(krmsActionTypeDefnBuilder.build());
        }

        return krmsActionTypeDefinition;
    }

    protected AgendaDefinition.Builder buildAgenda(String agendaSuffix) {
        String namespace =  "Namespace" + agendaSuffix;
        // create a context
        ContextDefinition.Builder contextDefinitionBuilder = ContextDefinition.Builder.create(
                namespace, "ContextName" + agendaSuffix);
        contextDefinitionBuilder.setId("ContextId" + agendaSuffix);
        ContextDefinition contextDefinition = contextDefinitionBuilder.build();
        contextDefinition = ruleManagementServiceImpl.createContext(contextDefinition );

        // create an agenda ( a agendaItem cannot be created without an existing agenda.
        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(
                "AgendaId" + agendaSuffix, "AgendaName" + agendaSuffix, null, "ContextId" + agendaSuffix);
        AgendaDefinition agenda = agendaBuilder.build();
        agenda = ruleManagementServiceImpl.createAgenda(agenda);

        // create a rule
        RuleDefinition rule = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, agendaSuffix));

        // create a agendaItem using the context agenda and rule above
        AgendaItemDefinition agendaItem = newTestAgendaItemDefinition("AI" + agendaSuffix, agenda.getId(), rule.getId());
        agendaItem = ruleManagementServiceImpl.createAgendaItem(agendaItem);

        agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda("AgendaId" + agendaSuffix));
        String defaultAgendaItemId = agendaBuilder.getFirstItemId();
        agendaBuilder.setFirstItemId("AI" + agendaSuffix);
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());

        // clean up default agendaItem created by createAgenda
        ruleManagementServiceImpl.deleteAgendaItem(defaultAgendaItemId);

        return AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(agenda.getId()));
    }

    protected AgendaDefinition.Builder buildComplexAgenda(String XX) {
        String namespace = "Namespace" + XX + "00";
        return buildComplexAgenda(namespace,"AGENDA", XX);
    }

    protected AgendaDefinition.Builder buildComplexAgenda(String namespace, String namespaceType, String XX) {
        // create "full" agendaXX00
        //  agendaItemXX00 ( ruleXX00)
        //      WhenTrue   agendaItemXX01( ruleXX01 )
        //      WhenFalse  agendaItemXX02( ruleXX02 )
        //      Always     agendaItemXX03( ruleXX03 )
        //  agendaItemXX01 ( ruleXX01 )
        //      Always     agendaItemXX05
        //  agendaItemXX02 ( ruleXX02 )
        //      WhenFalse  agendaItemXX04
        //      Always     agendaItemXX06
        //  agendaItemXX03 ( ruleXX03 )
        //  agendaItemXX04 ( ruleXX04 )
        //  agendaItemXX05 ( ruleXX05 )
        //  agendaItemXX06 ( ruleXX06 )

        // create a context
        ContextDefinition.Builder contextBuilderXX00 = ContextDefinition.Builder.create(
                namespace, "ContextName" + XX + "00");
        contextBuilderXX00.setId("ContextId" + XX + "00");
        ContextDefinition contextXX00 = contextBuilderXX00.build();
        ruleManagementServiceImpl.createContext(contextXX00 );

        // create krms type AGENDA
        createKrmsActionTypeDefinition(  "Namespace" + XX + "00", namespaceType, null);

        // create a agenda
        AgendaDefinition.Builder agendaBuilderXX00 = AgendaDefinition.Builder.create(
                "AgendaId" + XX + "00", "AgendaName" + XX + "00", namespaceType, "ContextId" + XX + "00");
        AgendaDefinition agendaXX00 = agendaBuilderXX00.build();
        agendaXX00 = ruleManagementServiceImpl.createAgenda(agendaXX00);

        // create rules
        RuleDefinition ruleXX00 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, XX + "00"));
        RuleDefinition ruleXX01 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, XX + "01"));
        RuleDefinition ruleXX02 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, XX + "02"));
        RuleDefinition ruleXX03 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, XX + "03"));
        RuleDefinition ruleXX04 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, XX + "04"));
        RuleDefinition ruleXX05 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, XX + "05"));
        RuleDefinition ruleXX06 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, XX + "06"));

        // create agendaItems
        AgendaItemDefinition agendaItemXX06 = newTestAgendaItemDefinition("AI" + XX + "06", agendaXX00.getId(), ruleXX06.getId());
        AgendaItemDefinition.Builder itemBuilderXX06 = AgendaItemDefinition.Builder.create(agendaItemXX06);
        itemBuilderXX06 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(itemBuilderXX06.build()));

        AgendaItemDefinition agendaItemXX05 = newTestAgendaItemDefinition("AI" + XX + "05", agendaXX00.getId(), ruleXX05.getId());
        AgendaItemDefinition.Builder itemBuilderXX05 = AgendaItemDefinition.Builder.create(agendaItemXX05);
        itemBuilderXX05 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(itemBuilderXX05.build()));

        AgendaItemDefinition agendaItemXX04 = newTestAgendaItemDefinition("AI" + XX + "04", agendaXX00.getId(), ruleXX04.getId());
        AgendaItemDefinition.Builder itemBuilderXX04 = AgendaItemDefinition.Builder.create(agendaItemXX04);
        itemBuilderXX04 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(
                itemBuilderXX04.build()));

        AgendaItemDefinition agendaItemXX03 = newTestAgendaItemDefinition("AI" + XX + "03", agendaXX00.getId(), ruleXX03.getId());
        AgendaItemDefinition.Builder itemBuilderXX03 = AgendaItemDefinition.Builder.create(agendaItemXX03);
        itemBuilderXX03 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(
                itemBuilderXX03.build()));

        AgendaItemDefinition agendaItemXX02 = newTestAgendaItemDefinition("AI" + XX + "02", agendaXX00.getId(), ruleXX02.getId());
        AgendaItemDefinition.Builder itemBuilderXX02 = AgendaItemDefinition.Builder.create(agendaItemXX02);
        itemBuilderXX04 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem(itemBuilderXX04.getId()));
        itemBuilderXX02.setWhenFalse(itemBuilderXX04);
        itemBuilderXX02.setWhenFalseId(itemBuilderXX04.getId());
        itemBuilderXX06 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem(itemBuilderXX06.getId()));
        itemBuilderXX02.setAlways(itemBuilderXX06);
        itemBuilderXX02.setAlwaysId(itemBuilderXX06.getId());
        itemBuilderXX02 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(
                itemBuilderXX02.build()));

        AgendaItemDefinition agendaItemXX01 = newTestAgendaItemDefinition("AI" + XX + "01", agendaXX00.getId(), ruleXX01.getId());
        AgendaItemDefinition.Builder itemBuilderXX01 = AgendaItemDefinition.Builder.create(agendaItemXX01);
        itemBuilderXX05 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem(itemBuilderXX05.getId()));
        itemBuilderXX01.setAlways(itemBuilderXX05);
        itemBuilderXX01.setAlwaysId(itemBuilderXX05.getId());
        itemBuilderXX01 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(
                itemBuilderXX01.build()));

        AgendaItemDefinition agendaItemXX00 = newTestAgendaItemDefinition("AI" + XX + "00", agendaXX00.getId(), ruleXX00.getId());
        AgendaItemDefinition.Builder itemBuilderXX00 = AgendaItemDefinition.Builder.create(agendaItemXX00);
        itemBuilderXX01 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem(itemBuilderXX01.getId()));
        itemBuilderXX00.setWhenTrue(itemBuilderXX01);
        itemBuilderXX00.setWhenTrueId(itemBuilderXX01.getId());
        itemBuilderXX02 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem(itemBuilderXX02.getId()));
        itemBuilderXX00.setWhenFalse(itemBuilderXX02);
        itemBuilderXX00.setWhenFalseId(itemBuilderXX02.getId());
        itemBuilderXX03 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem(itemBuilderXX03.getId()));
        itemBuilderXX00.setAlways(itemBuilderXX03);
        itemBuilderXX00.setAlwaysId(itemBuilderXX03.getId());
        itemBuilderXX00 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(itemBuilderXX00.build()));

        agendaBuilderXX00 = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(agendaXX00.getId()));
        String defaultAgendaItemId = agendaBuilderXX00.getFirstItemId();
        agendaBuilderXX00.setFirstItemId(itemBuilderXX00.getId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilderXX00.build());
        ruleManagementServiceImpl.deleteAgendaItem(defaultAgendaItemId);
        List<AgendaItemDefinition> agendaItems = ruleManagementServiceImpl.getAgendaItemsByContext("ContextId" + XX + "00");

        assertEquals("Invalid number of agendaItems created", 7, agendaItems.size());

        return AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(agendaXX00.getId()));
    }


    protected ReferenceObjectBinding.Builder buildReferenceObjectBinding(String agendaSuffix) {
        String namespace = "Namespace" + agendaSuffix;
        AgendaDefinition.Builder agendaBuilder = buildAgenda(agendaSuffix);
        // create krms type for AgendaXXXX
        KrmsTypeDefinition krmsTypeDefinition = createKrmsActionTypeDefinition(namespace, "AgendaType" + agendaSuffix , null);

        return buildReferenceObjectBinding("ParkingPolicies", agendaBuilder.getId(), krmsTypeDefinition.getId(),
                namespace, "PA" + agendaSuffix, "ParkingAffiliationType", true);
    }


    protected ReferenceObjectBinding.Builder buildReferenceObjectBinding(String collectionName, String krmsObjectId, String krmsDiscriminatorType,
            String namespace, String referenceObjectId, String referenceDiscriminatorType, boolean active) {

        ReferenceObjectBinding.Builder refObjBindingBuilder = ReferenceObjectBinding.Builder.
                create(krmsDiscriminatorType, krmsObjectId, namespace, referenceDiscriminatorType,   referenceObjectId);
        refObjBindingBuilder.setCollectionName(collectionName);
        refObjBindingBuilder.setActive(active);

        return  ReferenceObjectBinding.Builder.create(ruleManagementServiceImpl.createReferenceObjectBinding(refObjBindingBuilder.build()));
    }
}
