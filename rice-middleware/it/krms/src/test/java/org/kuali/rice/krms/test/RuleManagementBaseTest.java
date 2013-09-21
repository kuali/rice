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
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.language.NaturalLanguageTemplate;
import org.kuali.rice.krms.api.repository.language.NaturalLanguageUsage;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameter;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterType;
import org.kuali.rice.krms.api.repository.proposition.PropositionType;
import org.kuali.rice.krms.api.repository.reference.ReferenceObjectBinding;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.repository.NaturalLanguageTemplateBoServiceImpl;
import org.kuali.rice.krms.impl.repository.NaturalLanguageTemplateIntegrationGenTest;
import org.kuali.rice.krms.impl.repository.RuleManagementServiceImpl;
import org.kuali.rice.test.BaselineTestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Base test case and methods for testing RuleManagementServiceImpl
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class RuleManagementBaseTest extends AbstractAgendaBoTest{

    //public static final String TEST_PREFIX = "RuleManagementIT";
    RuleManagementServiceImpl ruleManagementServiceImpl;

    protected String CLASS_DISCRIMINATOR = "0";

    @Override
    @Before
    public void setup() {
        ruleManagementServiceImpl = new RuleManagementServiceImpl();
        NaturalLanguageTemplateBoServiceImpl naturalLanguageTemplateBoServiceImpl = new NaturalLanguageTemplateBoServiceImpl();
        naturalLanguageTemplateBoServiceImpl.setNaturalLanguageTemplater(
                NaturalLanguageTemplateIntegrationGenTest.newStringReplaceTemplater());
        ruleManagementServiceImpl.setNaturalLanguageTemplateBoService(naturalLanguageTemplateBoServiceImpl);
        ruleManagementServiceImpl.setBusinessObjectService(getBoService()); // Business Object Service gets set to other Services
        termBoService = KrmsRepositoryServiceLocator.getTermBoService();

        contextRepository = KrmsRepositoryServiceLocator.getContextBoService();
        krmsTypeRepository = KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService();
        ruleBoService = KrmsRepositoryServiceLocator.getRuleBoService();
        agendaBoService = KrmsRepositoryServiceLocator.getAgendaBoService();
        actionBoService = KrmsRepositoryServiceLocator.getBean("actionBoService");
        functionBoService = KrmsRepositoryServiceLocator.getBean("functionRepositoryService");
        krmsAttributeDefinitionService = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();
    }

    /**
     * setClassDiscriminator needs to be run before tests to ensure uniqueness of test objects within a class
     *
     *  Should be overridden by each class which extends this class
     *
     *  Test object naming is comprised of class, test and object uniqueness discriminators.
     *  The Class Discriminator needs to be set before any tests.
     *  The Test Discriminator needs to be set at the start of each test. ex: setTestObjectNames(testDiscriminator)
     *  The Object Discriminators are Sequential (object0, OBJECT1 ...)
     */
    @Before
    public void setClassDiscriminator() {
        // set a unique discriminator for test objects of this class should be uniquely set by each extending class
        CLASS_DISCRIMINATOR = "BaseTest";
    }

    protected RuleDefinition createTestRule(String namespace, String ruleId) {
        return ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, ruleId));
    }

    protected RuleDefinition newTestRuleDefinition(String namespace, String objectDiscriminator) {
        String ruleId = "RuleId" + objectDiscriminator;
        PropositionDefinition prop = createTestPropositionForRule(objectDiscriminator);
        PropositionDefinition.Builder propBuilder = PropositionDefinition.Builder.create(prop);
        RuleDefinition.Builder builder = RuleDefinition.Builder.create(ruleId, "RuleName" + objectDiscriminator, namespace, null, prop.getId());
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

        assertNull("action[" + actionId + "] should not already be in database:", ruleManagementServiceImpl.getAction(actionId ));

        actionDefinition =  ruleManagementServiceImpl.createAction(actionDefinition);
        ActionDefinition returnActionDefinition = ruleManagementServiceImpl.getAction(actionDefinition.getId());
        ActionDefinition.Builder builder = ActionDefinition.Builder.create(returnActionDefinition);
        builder.setDescription(actionDescr);
        ruleManagementServiceImpl.updateAction(builder.build());

        return ruleManagementServiceImpl.getAction(actionDefinition.getId());
    }

    protected PropositionDefinition createTestPropositionForRule(String objectDiscriminator) {
        KrmsTypeDefinition krmsTypeDefinition = createKrmsActionTypeDefinition("Namespace" + objectDiscriminator);
        String ruleId = "RuleId" + objectDiscriminator;
        String propId = "P" + objectDiscriminator;

        return createTestSimpleProposition("Namespace" + objectDiscriminator, propId, "TSI_" + propId, "ABC", "=", "java.lang.String", ruleId,
                "TSI_" + propId + "_Descr");
    }

    protected PropositionDefinition createTestSimpleProposition(String namespace, String propId, String termSpecId, String propConstant, String propOperator, String termSpecType, String ruleId, String termSpecDescr){
        TermSpecificationDefinition termSpecificationDefinition = createTestTermSpecification(termSpecId, termSpecId, namespace, termSpecType, termSpecDescr);
        KrmsTypeDefinition krmsTypeDefinition = createKrmsTypeDefinition(namespace, termSpecId, "testTypeService");

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

    protected KrmsTypeDefinition createKrmsTypeDefinition(String nameSpace, String typeName, String serviceName) {
        KrmsTypeDefinition krmsTypeDefinition =  krmsTypeRepository.getTypeByName(nameSpace, typeName);

        if (krmsTypeDefinition == null) {
            KrmsTypeDefinition.Builder krmsTypeDefnBuilder = KrmsTypeDefinition.Builder.create(typeName, nameSpace);
            krmsTypeDefnBuilder.setServiceName(serviceName);
            krmsTypeDefinition = krmsTypeRepository.createKrmsType(krmsTypeDefnBuilder.build());
        }

        return krmsTypeDefinition;
    }

    // Context ("ContextId4000", "Namespace4000", "ContextName4000")
    // Agenda  ("AgendaId4000", "AgendaName4000")
    //    AgendaItem ("AI4000")
    //        Rule ( TEST_PREFIX + "RuleId4000"
    protected AgendaDefinition.Builder buildAgenda(String objectDiscriminator) {
        String namespace =  "Namespace" + objectDiscriminator;
        // create a context
        ContextDefinition.Builder contextDefinitionBuilder = ContextDefinition.Builder.create(
                namespace, "ContextName" + objectDiscriminator);
        contextDefinitionBuilder.setId("ContextId" + objectDiscriminator);
        ContextDefinition contextDefinition = contextDefinitionBuilder.build();
        contextDefinition = ruleManagementServiceImpl.createContext(contextDefinition );

        // create an agenda ( a agendaItem cannot be created without an existing agenda.
        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(
                "AgendaId" + objectDiscriminator, "AgendaName" + objectDiscriminator, null, "ContextId" + objectDiscriminator);
        AgendaDefinition agenda = agendaBuilder.build();
        agenda = ruleManagementServiceImpl.createAgenda(agenda);

        // create a rule
        RuleDefinition rule = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, objectDiscriminator));

        // create a agendaItem using the context agenda and rule above
        AgendaItemDefinition agendaItem = newTestAgendaItemDefinition("AI" + objectDiscriminator, agenda.getId(), rule.getId());
        agendaItem = ruleManagementServiceImpl.createAgendaItem(agendaItem);

        agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda("AgendaId" + objectDiscriminator));
        String defaultAgendaItemId = agendaBuilder.getFirstItemId();
        agendaBuilder.setFirstItemId("AI" + objectDiscriminator);
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());

        // clean up default agendaItem created by createAgenda
        ruleManagementServiceImpl.deleteAgendaItem(defaultAgendaItemId);

        return AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(agenda.getId()));
    }

    protected AgendaDefinition.Builder buildComplexAgenda(RuleManagementBaseTestObjectNames names) {
        String namespace = "Namespace" + names.discriminator;
        return buildComplexAgenda(namespace,"AGENDA",names);
    }

    /**
     *   buildComplexAgenda builds a "complex" agenda object as shown here.
     *
     *           // agenda
                 //   agendaItem0 ( rule0)
                 //       WhenTrue   agendaItem1( rule1 )
                 //       WhenFalse  agendaItem2( rule2 )
                 //       Always     agendaItem3( rule3 )
                 //   agendaItem1 ( rule1 )
                 //       Always     agendaItem5
                 //   agendaItem2 ( rule2 )
                 //       WhenFalse  agendaItem4
                 //       Always     agendaItem6
                 //   agendaItem3 ( rule3 )
                 //   agendaItem4 ( rule4 )
                 //   agendaItem5 ( rule5 )
                 //   agendaItem6 ( rule6 )
     *
     * @param namespace of the KRMS Agenda to be created
     * @param namespaceType of the namepace passed (Namespace will be created if it does not exist.)
     * @param {@link RuleManagementBaseTestObjectNames}  Unique discriminator names to base created Agenda upon
     * @return {@link AgendaDefinition.Builder} populated from the built agenda
     */
    protected AgendaDefinition.Builder buildComplexAgenda(String namespace, String namespaceType, RuleManagementBaseTestObjectNames names) {
        // create a context
        ContextDefinition.Builder contextBuilder = ContextDefinition.Builder.create(
                namespace, names.contextName);
        contextBuilder.setId(names.contextId);
        ContextDefinition context = contextBuilder.build();
        ruleManagementServiceImpl.createContext(context );

        // create krms type AGENDA
        createKrmsTypeDefinition(namespace, namespaceType, null);

        // create a agenda
        AgendaDefinition.Builder agendaBuilder = AgendaDefinition.Builder.create(
                names.agenda_Id, names.agenda_Name, namespaceType, names.contextId);
        AgendaDefinition agenda = agendaBuilder.build();
        agenda = ruleManagementServiceImpl.createAgenda(agenda);

        // create rules
        RuleDefinition rule0 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, names.object0));
        RuleDefinition rule1 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, names.object1));
        RuleDefinition rule2 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, names.object2));
        RuleDefinition rule3 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, names.object3));
        RuleDefinition rule4 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, names.object4));
        RuleDefinition rule5 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, names.object5));
        RuleDefinition rule6 = ruleManagementServiceImpl.createRule(newTestRuleDefinition(namespace, names.object6));

        // create agendaItems
        AgendaItemDefinition agendaItemOBJECT6 = newTestAgendaItemDefinition(names.agendaItem_6_Id, agenda.getId(), rule6.getId());
        AgendaItemDefinition.Builder itemBuilderOBJECT6 = AgendaItemDefinition.Builder.create(agendaItemOBJECT6);
        itemBuilderOBJECT6 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(itemBuilderOBJECT6.build()));

        AgendaItemDefinition agendaItemOBJECT5 = newTestAgendaItemDefinition(names.agendaItem_5_Id, agenda.getId(), rule5.getId());
        AgendaItemDefinition.Builder itemBuilderOBJECT5 = AgendaItemDefinition.Builder.create(agendaItemOBJECT5);
        itemBuilderOBJECT5 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(itemBuilderOBJECT5.build()));

        AgendaItemDefinition agendaItemOBJECT4 = newTestAgendaItemDefinition(names.agendaItem_4_Id, agenda.getId(), rule4.getId());
        AgendaItemDefinition.Builder itemBuilderOBJECT4 = AgendaItemDefinition.Builder.create(agendaItemOBJECT4);
        itemBuilderOBJECT4 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(
                itemBuilderOBJECT4.build()));

        AgendaItemDefinition agendaItemOBJECT3 = newTestAgendaItemDefinition(names.agendaItem_3_Id, agenda.getId(), rule3.getId());
        AgendaItemDefinition.Builder itemBuilderOBJECT3 = AgendaItemDefinition.Builder.create(agendaItemOBJECT3);
        itemBuilderOBJECT3 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(
                itemBuilderOBJECT3.build()));

        AgendaItemDefinition agendaItemOBJECT2 = newTestAgendaItemDefinition(names.agendaItem_2_Id, agenda.getId(), rule2.getId());
        AgendaItemDefinition.Builder itemBuilderOBJECT2 = AgendaItemDefinition.Builder.create(agendaItemOBJECT2);
        itemBuilderOBJECT4 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem(itemBuilderOBJECT4.getId()));
        itemBuilderOBJECT2.setWhenFalse(itemBuilderOBJECT4);
        itemBuilderOBJECT2.setWhenFalseId(itemBuilderOBJECT4.getId());
        itemBuilderOBJECT6 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem(itemBuilderOBJECT6.getId()));
        itemBuilderOBJECT2.setAlways(itemBuilderOBJECT6);
        itemBuilderOBJECT2.setAlwaysId(itemBuilderOBJECT6.getId());
        itemBuilderOBJECT2 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(
                itemBuilderOBJECT2.build()));

        AgendaItemDefinition agendaItemOBJECT1 = newTestAgendaItemDefinition(names.agendaItem_1_Id, agenda.getId(), rule1.getId());
        AgendaItemDefinition.Builder itemBuilderOBJECT1 = AgendaItemDefinition.Builder.create(agendaItemOBJECT1);
        itemBuilderOBJECT5 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem(itemBuilderOBJECT5.getId()));
        itemBuilderOBJECT1.setAlways(itemBuilderOBJECT5);
        itemBuilderOBJECT1.setAlwaysId(itemBuilderOBJECT5.getId());
        itemBuilderOBJECT1 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(
                itemBuilderOBJECT1.build()));

        AgendaItemDefinition agendaItemOBJECT0 = newTestAgendaItemDefinition(names.agendaItem_0_Id, agenda.getId(), rule0.getId());
        AgendaItemDefinition.Builder itemBuilderOBJECT0 = AgendaItemDefinition.Builder.create(agendaItemOBJECT0);
        itemBuilderOBJECT1 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem(itemBuilderOBJECT1.getId()));
        itemBuilderOBJECT0.setWhenTrue(itemBuilderOBJECT1);
        itemBuilderOBJECT0.setWhenTrueId(itemBuilderOBJECT1.getId());
        itemBuilderOBJECT2 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem(itemBuilderOBJECT2.getId()));
        itemBuilderOBJECT0.setWhenFalse(itemBuilderOBJECT2);
        itemBuilderOBJECT0.setWhenFalseId(itemBuilderOBJECT2.getId());
        itemBuilderOBJECT3 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.getAgendaItem(itemBuilderOBJECT3.getId()));
        itemBuilderOBJECT0.setAlways(itemBuilderOBJECT3);
        itemBuilderOBJECT0.setAlwaysId(itemBuilderOBJECT3.getId());
        itemBuilderOBJECT0 = AgendaItemDefinition.Builder.create(ruleManagementServiceImpl.createAgendaItem(itemBuilderOBJECT0.build()));

        agendaBuilder = AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(agenda.getId()));
        String defaultAgendaItemId = agendaBuilder.getFirstItemId();
        agendaBuilder.setFirstItemId(itemBuilderOBJECT0.getId());
        ruleManagementServiceImpl.updateAgenda(agendaBuilder.build());
        ruleManagementServiceImpl.deleteAgendaItem(defaultAgendaItemId);
        List<AgendaItemDefinition> agendaItems = ruleManagementServiceImpl.getAgendaItemsByContext(names.contextId);

        assertEquals("Invalid number of agendaItems created", 7, agendaItems.size());

        return AgendaDefinition.Builder.create(ruleManagementServiceImpl.getAgenda(agenda.getId()));
    }


    protected ReferenceObjectBinding.Builder buildReferenceObjectBinding(String objectDiscriminator) {
        String namespace = "Namespace" + objectDiscriminator;
        AgendaDefinition.Builder agendaBuilder = buildAgenda(objectDiscriminator);
        // create krms type for AgendaOBJECT
        KrmsTypeDefinition krmsTypeDefinition = createKrmsTypeDefinition(namespace, "AgendaType" + objectDiscriminator, null);

        return buildReferenceObjectBinding("ParkingPolicies", agendaBuilder.getId(), krmsTypeDefinition.getId(),
                namespace, "PA" + objectDiscriminator, "ParkingAffiliationType", true);
    }


    protected ReferenceObjectBinding.Builder buildReferenceObjectBinding(String collectionName, String krmsObjectId, String krmsDiscriminatorType,
            String namespace, String referenceObjectId, String referenceDiscriminatorType, boolean active) {

        ReferenceObjectBinding.Builder refObjBindingBuilder = ReferenceObjectBinding.Builder.
                create(krmsDiscriminatorType, krmsObjectId, namespace, referenceDiscriminatorType,   referenceObjectId);
        refObjBindingBuilder.setCollectionName(collectionName);
        refObjBindingBuilder.setActive(active);

        return  ReferenceObjectBinding.Builder.create(ruleManagementServiceImpl.createReferenceObjectBinding(
                refObjBindingBuilder.build()));
    }

    /**
     * Used to build a NaturalLanguageTemplate in the database for testing.
     *
     * @param namespace of the KRMS Type which may be created
     * @param langCode  The two character code of applicable language
     * @param nlObjectName  Seed value to create unique Template and Usage records
     * @param template The text of the template for the Template record
     *
     * @return {@link NaturalLanguageTemplate} for the created Template
     */
    protected NaturalLanguageTemplate buildTestNaturalLanguageTemplate(String namespace, String langCode, String nlObjectName, String template) {
        KrmsTypeDefinition  krmsType = createKrmsTypeDefinition(namespace, nlObjectName, null);

        // create NaturalLanguageUsage if it does not exist
        if (ObjectUtils.isNull(ruleManagementServiceImpl.getNaturalLanguageUsage("krms.nl." + nlObjectName))) {
            NaturalLanguageUsage.Builder naturalLanguageUsageBuilder =  NaturalLanguageUsage.Builder.create(nlObjectName,namespace );
            naturalLanguageUsageBuilder.setId("krms.nl." + nlObjectName);
            naturalLanguageUsageBuilder.setDescription("Description-" + nlObjectName);
            naturalLanguageUsageBuilder.setActive(true);
            NaturalLanguageUsage naturalLangumageUsage =  ruleManagementServiceImpl.createNaturalLanguageUsage(naturalLanguageUsageBuilder.build());
        }

        // create  NaturalLanguageTemplate attributes if they don't exist
        createTestKrmsAttribute("TemplateAttribute1", "TemplateAttributeName1", namespace);
        createTestKrmsAttribute("TemplateAttribute2", "TemplateAttributeName2", namespace);
        Map<String, String> nlAttributes = new HashMap<String, String>();
        nlAttributes.put("TemplateAttributeName1","value1");
        nlAttributes.put("TemplateAttributeName2","value2");


        // create NaturalLanguageTemplate
        String naturalLanguageUsageId = "krms.nl." + nlObjectName;
        String typeId = krmsType.getId();
        NaturalLanguageTemplate.Builder naturalLanguageTemplateBuilder = NaturalLanguageTemplate.Builder.create(langCode,naturalLanguageUsageId,template,typeId);
        naturalLanguageTemplateBuilder.setActive(true);
        naturalLanguageTemplateBuilder.setId(langCode + "-" + nlObjectName);
        naturalLanguageTemplateBuilder.setAttributes(nlAttributes);

        return ruleManagementServiceImpl.createNaturalLanguageTemplate(naturalLanguageTemplateBuilder.build());
    }

    protected KrmsAttributeDefinition createTestKrmsAttribute(String id, String name, String namespace) {
        // if the AttributeDefinition does not exist, create it
        if (ObjectUtils.isNull(krmsAttributeDefinitionService.getAttributeDefinitionById(id))) {
            KrmsAttributeDefinition.Builder krmsAttributeDefinitionBuilder = KrmsAttributeDefinition.Builder.create(id, name, namespace);
            KrmsAttributeDefinition  krmsAttributeDefinition = krmsAttributeDefinitionService.createAttributeDefinition(krmsAttributeDefinitionBuilder.build());
        }

        return krmsAttributeDefinitionService.getAttributeDefinitionById(id);
    }

    protected NaturalLanguageUsage buildTestNaturalLanguageUsage(String namespace, String nlUsageName ) {
        KrmsTypeDefinition  krmsType = createKrmsTypeDefinition(namespace, nlUsageName, null);

        // create NaturalLanguageUsage
        NaturalLanguageUsage.Builder naturalLanguageUsageBuilder =  NaturalLanguageUsage.Builder.create(nlUsageName,namespace );
        naturalLanguageUsageBuilder.setId("krms.nl." + nlUsageName);
        naturalLanguageUsageBuilder.setDescription("Description-" + nlUsageName);
        naturalLanguageUsageBuilder.setActive(true);

        return  ruleManagementServiceImpl.createNaturalLanguageUsage(naturalLanguageUsageBuilder.build());
    }

    protected ContextDefinition buildTestContext(String objectDiscriminator) {
        String namespace =  "Namespace" + objectDiscriminator;
        // create a context
        ContextDefinition.Builder contextDefinitionBuilder = ContextDefinition.Builder.create(
                namespace, "ContextName" + objectDiscriminator);
        contextDefinitionBuilder.setId("ContextId" + objectDiscriminator);
        ContextDefinition contextDefinition = contextDefinitionBuilder.build();

        return ruleManagementServiceImpl.createContext(contextDefinition );
    }
}
