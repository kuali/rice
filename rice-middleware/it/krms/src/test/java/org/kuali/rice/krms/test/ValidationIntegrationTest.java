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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krms.api.KrmsApiServiceLocator;
import org.kuali.rice.krms.api.KrmsConstants;
import org.kuali.rice.krms.api.engine.EngineResults;
import org.kuali.rice.krms.api.engine.ExecutionFlag;
import org.kuali.rice.krms.api.engine.ExecutionOptions;
import org.kuali.rice.krms.api.engine.Facts;
import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameter;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterType;
import org.kuali.rice.krms.api.repository.proposition.PropositionType;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeAttribute;
import org.kuali.rice.krms.api.repository.type.KrmsTypeBoService;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.framework.engine.expression.ComparisonOperator;
import org.kuali.rice.krms.framework.type.ValidationActionType;
import org.kuali.rice.krms.framework.type.ValidationActionTypeService;
import org.kuali.rice.krms.framework.type.ValidationRuleType;
import org.kuali.rice.krms.framework.type.ValidationRuleTypeService;
import org.kuali.rice.krms.impl.repository.ActionBoServiceImpl;
import org.kuali.rice.krms.impl.repository.AgendaBoServiceImpl;
import org.kuali.rice.krms.impl.repository.ContextBo;
import org.kuali.rice.krms.impl.repository.ContextBoServiceImpl;
import org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionBo;
import org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.repository.KrmsTypeBoServiceImpl;
import org.kuali.rice.krms.impl.repository.PropositionBoService;
import org.kuali.rice.krms.impl.repository.PropositionBoServiceImpl;
import org.kuali.rice.krms.impl.repository.RuleBo;
import org.kuali.rice.krms.impl.repository.RuleBoServiceImpl;
import org.kuali.rice.krms.impl.repository.TermBoServiceImpl;
import org.kuali.rice.krms.impl.util.KrmsServiceLocatorInternal;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

/**
 * Validation Integration Test
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineMode(Mode.CLEAR_DB)
public class ValidationIntegrationTest extends RuleManagementBaseTest {

    private static final String EVENT_ATTRIBUTE = "Event";
    private static final String TERM_NAME = "campusCodeTermSpec";

    private static final String CONTEXT_NAME = "ValidationITContext";
    private static final String WARNING_MESSAGE = "Warning Message.";
    private static final String ERROR_MESSAGE = "Error Message.";
    private static final String VALIDATION_ACTION_TYPE_SERVICE = "validationActionTypeService";
    private static final String VALIDATION_RULE_TYPE_SERVICE = "validationRuleTypeService";

    private KrmsTypeBoService krmsTypeBoService;
    private PropositionBoService propositionBoService;

    private String propOperator = "=";
    private String discriminator = null;


	@Before
	public void setup() {

        ruleManagementService = KrmsRepositoryServiceLocator.getService("ruleManagementService");
        dataObjectService = (DataObjectService)GlobalResourceLoader.getService("dataObjectService");
        krmsAttributeDefinitionService = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();
        krmsTypeRepository = KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService();
        krmsTypeBoService = new KrmsTypeBoServiceImpl();
        ((KrmsTypeBoServiceImpl)krmsTypeBoService).setDataObjectService(dataObjectService);

        // like RepositoryCreateAndExecuteIntegrationTest
        propositionBoService = new PropositionBoServiceImpl();
        ((PropositionBoServiceImpl)propositionBoService).setDataObjectService(dataObjectService);
        termBoService = new TermBoServiceImpl();
        ((TermBoServiceImpl)termBoService).setDataObjectService(dataObjectService);
        contextRepository = new ContextBoServiceImpl();
        ((ContextBoServiceImpl)contextRepository).setDataObjectService(dataObjectService);
        agendaBoService = new AgendaBoServiceImpl();
        ((AgendaBoServiceImpl)agendaBoService).setDataObjectService(dataObjectService);
        ((AgendaBoServiceImpl)agendaBoService).setAttributeDefinitionService(krmsAttributeDefinitionService);
        ruleBoService = new RuleBoServiceImpl();
        ((RuleBoServiceImpl)ruleBoService).setDataObjectService(dataObjectService);
        actionBoService = new ActionBoServiceImpl();
        ((ActionBoServiceImpl)actionBoService).setDataObjectService(dataObjectService);
    }

    @Transactional
     @Test
     public void testValidWarning() {
        discriminator = "1";
        propOperator = "=";
        String ruleName = ValidationRuleType.VALID.toString();
        String nameSpace =  KrmsConstants.KRMS_NAMESPACE;
        String ruleTypeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleAttributeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleTypeLabel = ValidationRuleType.VALID.toString();
        String ruleServiceName = VALIDATION_RULE_TYPE_SERVICE;
        String actionTypeName = "KrmsActionResolverType";
        String actionTypeServicename = VALIDATION_ACTION_TYPE_SERVICE;
        String actionName = ValidationActionType.WARNING.toString();
        Map<String, String> actionAttributes = new HashMap<String, String>();
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.WARNING.getCode());
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE,WARNING_MESSAGE);
        Map<String, String> actionAttributesDefs = new HashMap<String, String>();
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.WARNING.toString());
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE, "Validation Action Message");
        Map<String, String> ruleAttributes = new HashMap<String, String>();
        ruleAttributes.put(ruleAttributeName, ValidationRuleType.VALID.getCode());

        KrmsTypeDefinition contextTypeDefinition = createContextTypeDef("ContextTypeName" + discriminator, nameSpace,
                "ContextAttributeName" + discriminator, "ContextLabel", "ContextServiceName");

        ContextDefinition contextDefinition =  createContext(CONTEXT_NAME + discriminator, nameSpace,
                contextTypeDefinition.getId(), Collections.EMPTY_MAP );
        ContextBo contextBo = ContextBo.from(contextDefinition);

        KrmsTypeDefinition ruleTypeDefinition = createUpdateRuleTypeDef(ruleTypeName, nameSpace, ruleAttributeName,
                ruleTypeLabel, ruleServiceName);

        KrmsTypeDefinition actionTypeDefinition = createUpdateActionTypeDef(actionTypeName, nameSpace,
                actionTypeServicename);

        for (Map.Entry<String, String> actionAttributeDef : actionAttributesDefs.entrySet()) {
            createUpdateAttribute(actionAttributeDef.getKey(), nameSpace, actionAttributeDef.getValue());
        }

        RuleBo ruleBo = createUpdateRuleWithAction(ruleName, nameSpace, contextDefinition.getId(),
                ruleTypeDefinition.getId(), actionName, actionTypeDefinition.getId(), actionAttributes, ruleAttributes);

        createAgenda(ruleBo, contextBo, createEventAttributeDefinition());

        EngineResults results = engineExecute(CONTEXT_NAME + discriminator, nameSpace);
        assertTrue(results.getAttribute(ValidationActionTypeService.VALIDATIONS_ACTION_ATTRIBUTE) == null);
    }

    @Transactional
    @Test
    public void testInvalidWarning() {
        discriminator = "2";
        propOperator = "=";
        String ruleName = ValidationRuleType.INVALID.toString();
        String nameSpace =  KrmsConstants.KRMS_NAMESPACE;
        String ruleTypeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleAttributeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleTypeLabel = ValidationRuleType.INVALID.toString();
        String ruleServiceName = VALIDATION_RULE_TYPE_SERVICE;
        String actionTypeName = "KrmsActionResolverType";
        String actionTypeServicename = VALIDATION_ACTION_TYPE_SERVICE;
        String actionName = ValidationActionType.WARNING.toString();
        Map<String, String> actionAttributes = new HashMap<String, String>();
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.WARNING.getCode());
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE,WARNING_MESSAGE);
        Map<String, String> actionAttributesDefs = new HashMap<String, String>();
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.WARNING.toString());
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE, "Validation Action Message");
        Map<String, String> ruleAttributes = new HashMap<String, String>();
        ruleAttributes.put(ruleAttributeName, ValidationRuleType.INVALID.getCode());

        KrmsTypeDefinition contextTypeDefinition = createContextTypeDef("ContextTypeName" + discriminator, nameSpace,
                "ContextAttributeName" + discriminator, "ContextLabel", "ContextServiceName");

        ContextDefinition contextDefinition =  createContext(CONTEXT_NAME + discriminator, nameSpace,
                contextTypeDefinition.getId(), Collections.EMPTY_MAP );
        ContextBo contextBo = ContextBo.from(contextDefinition);

        KrmsTypeDefinition ruleTypeDefinition = createUpdateRuleTypeDef(ruleTypeName, nameSpace, ruleAttributeName,
                ruleTypeLabel, ruleServiceName);

        KrmsTypeDefinition actionTypeDefinition = createUpdateActionTypeDef(actionTypeName, nameSpace,
                actionTypeServicename);

        for (Map.Entry<String, String> actionAttributeDef : actionAttributesDefs.entrySet()) {
            createUpdateAttribute(actionAttributeDef.getKey(), nameSpace, actionAttributeDef.getValue());
        }

        RuleBo ruleBo = createUpdateRuleWithAction(ruleName, nameSpace, contextDefinition.getId(),
                ruleTypeDefinition.getId(), actionName, actionTypeDefinition.getId(), actionAttributes, ruleAttributes);

        createAgenda(ruleBo, contextBo, createEventAttributeDefinition());

        EngineResults results = engineExecute(CONTEXT_NAME + discriminator, nameSpace);
        assertNotNull(results.getAttribute(ValidationActionTypeService.VALIDATIONS_ACTION_ATTRIBUTE));
        assertEquals(ValidationActionType.WARNING.getCode() + ":" + WARNING_MESSAGE,
                results.getAttribute(ValidationActionTypeService.VALIDATIONS_ACTION_ATTRIBUTE));
    }

    @Transactional
    @Test
    public void testValidError() {
        discriminator = "3";
        propOperator = "=";
        String ruleName = ValidationRuleType.VALID.toString();
        String nameSpace =  KrmsConstants.KRMS_NAMESPACE;
        String ruleTypeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleAttributeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleTypeLabel = ValidationRuleType.VALID.toString();
        String ruleServiceName = VALIDATION_RULE_TYPE_SERVICE;
        String actionTypeName = "KrmsActionResolverType";
        String actionTypeServicename = VALIDATION_ACTION_TYPE_SERVICE;
        String actionName = ValidationActionType.ERROR.toString();
        Map<String, String> actionAttributes =  new HashMap<String, String>();
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.ERROR.getCode());
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE,ERROR_MESSAGE);
        Map<String, String> actionAttributesDefs = new HashMap<String, String>();
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.ERROR.toString());
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE, "Validation Action Message");
        Map<String, String> ruleAttributes = new HashMap<String, String>();
        ruleAttributes.put(ruleAttributeName, ValidationRuleType.VALID.getCode());

        KrmsTypeDefinition contextTypeDefinition = createContextTypeDef("ContextTypeName" + discriminator, nameSpace,
                "ContextAttributeName" + discriminator, "ContextLabel", "ContextServiceName");

        ContextDefinition contextDefinition =  createContext(CONTEXT_NAME + discriminator, nameSpace,
                contextTypeDefinition.getId(), Collections.EMPTY_MAP );
        ContextBo contextBo = ContextBo.from(contextDefinition);

        KrmsTypeDefinition ruleTypeDefinition = createUpdateRuleTypeDef(ruleTypeName, nameSpace, ruleAttributeName,
                ruleTypeLabel, ruleServiceName);

        KrmsTypeDefinition actionTypeDefinition = createUpdateActionTypeDef(actionTypeName, nameSpace,
                actionTypeServicename);

        for (Map.Entry<String, String> actionAttributeDef : actionAttributesDefs.entrySet()) {
            createUpdateAttribute(actionAttributeDef.getKey(), nameSpace, actionAttributeDef.getValue());
        }

        RuleBo ruleBo = createUpdateRuleWithAction(ruleName, nameSpace, contextDefinition.getId(),
                ruleTypeDefinition.getId(), actionName, actionTypeDefinition.getId(), actionAttributes, ruleAttributes);

        createAgenda(ruleBo, contextBo, createEventAttributeDefinition());

        EngineResults results = engineExecute(CONTEXT_NAME + discriminator, nameSpace);
        assertTrue(results.getAttribute(ValidationActionTypeService.VALIDATIONS_ACTION_ATTRIBUTE) == null);
    }

    @Transactional
    @Test
    public void testInvalidError() {
        discriminator = "4";
        propOperator = "=";
        String ruleName = ValidationRuleType.INVALID.toString();
        String nameSpace =  KrmsConstants.KRMS_NAMESPACE;
        String ruleTypeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleAttributeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleTypeLabel = ValidationRuleType.INVALID.toString();
        String ruleServiceName = VALIDATION_RULE_TYPE_SERVICE;
        String actionTypeName = "KrmsActionResolverType";
        String actionTypeServicename = VALIDATION_ACTION_TYPE_SERVICE;
        String actionName = ValidationActionType.ERROR.toString();
        Map<String, String> actionAttributes =  new HashMap<String, String>();
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.ERROR.getCode());
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE,ERROR_MESSAGE);
        Map<String, String> actionAttributesDefs = new HashMap<String, String>();
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.ERROR.toString());
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE, "Validation Action Message");
        Map<String, String> ruleAttributes = new HashMap<String, String>();
        ruleAttributes.put(ruleAttributeName, ValidationRuleType.INVALID.getCode());

        KrmsTypeDefinition contextTypeDefinition = createContextTypeDef("ContextTypeName" + discriminator,
                KrmsConstants.KRMS_NAMESPACE, "ContextAttributeName" + discriminator, "ContextLabel", "ContextServiceName");

        ContextDefinition contextDefinition =  createContext(CONTEXT_NAME + discriminator, nameSpace,
                contextTypeDefinition.getId(), Collections.EMPTY_MAP );
        ContextBo contextBo = ContextBo.from(contextDefinition);

        KrmsTypeDefinition ruleTypeDefinition = createUpdateRuleTypeDef(ruleTypeName, nameSpace, ruleAttributeName,
                ruleTypeLabel, ruleServiceName);

        KrmsTypeDefinition actionTypeDefinition = createUpdateActionTypeDef(actionTypeName, nameSpace,
                actionTypeServicename);

        for (Map.Entry<String, String> actionAttributeDef : actionAttributesDefs.entrySet()) {
            createUpdateAttribute(actionAttributeDef.getKey(), nameSpace, actionAttributeDef.getValue());
        }

        RuleBo ruleBo = createUpdateRuleWithAction(ruleName, nameSpace, contextDefinition.getId(),
                ruleTypeDefinition.getId(), actionName, actionTypeDefinition.getId(), actionAttributes, ruleAttributes);

        createAgenda(ruleBo, contextBo, createEventAttributeDefinition());

        EngineResults results = engineExecute(CONTEXT_NAME + discriminator, KrmsConstants.KRMS_NAMESPACE);
        assertNotNull(results.getAttribute(ValidationActionTypeService.VALIDATIONS_ACTION_ATTRIBUTE));
        assertEquals(ValidationActionType.ERROR.getCode() + ":" + ERROR_MESSAGE, results.getAttribute(
                ValidationActionTypeService.VALIDATIONS_ACTION_ATTRIBUTE));
    }

    @Transactional
    @Test
    public void testValidWarningReversedOperator() {
        discriminator = "5";
        // reverse operator to check Rule evaluation returns expected(opposite results)
        propOperator = "!=";
        String ruleName = ValidationRuleType.VALID.toString();
        String nameSpace =  KrmsConstants.KRMS_NAMESPACE;
        String ruleTypeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleAttributeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleTypeLabel = ValidationRuleType.VALID.toString();
        String ruleServiceName = VALIDATION_RULE_TYPE_SERVICE;
        String actionTypeName = "KrmsActionResolverType";
        String actionTypeServicename = VALIDATION_ACTION_TYPE_SERVICE;
        String actionName = ValidationActionType.WARNING.toString();
        Map<String, String> actionAttributes = new HashMap<String, String>();
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.WARNING.getCode());
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE,WARNING_MESSAGE);
        Map<String, String> actionAttributesDefs = new HashMap<String, String>();
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.WARNING.toString());
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE, "Validation Action Message");
        Map<String, String> ruleAttributes = new HashMap<String, String>();
        ruleAttributes.put(ruleAttributeName, ValidationRuleType.VALID.getCode());

        KrmsTypeDefinition contextTypeDefinition = createContextTypeDef("ContextTypeName" + discriminator, nameSpace,
                "ContextAttributeName" + discriminator, "ContextLabel", "ContextServiceName");

        ContextDefinition contextDefinition =  createContext(CONTEXT_NAME + discriminator, nameSpace,
                contextTypeDefinition.getId(), Collections.EMPTY_MAP );
        ContextBo contextBo = ContextBo.from(contextDefinition);

        KrmsTypeDefinition ruleTypeDefinition = createUpdateRuleTypeDef(ruleTypeName, nameSpace, ruleAttributeName,
                ruleTypeLabel, ruleServiceName);

        KrmsTypeDefinition actionTypeDefinition = createUpdateActionTypeDef(actionTypeName, nameSpace,
                actionTypeServicename);

        for (Map.Entry<String, String> actionAttributeDef : actionAttributesDefs.entrySet()) {
            createUpdateAttribute(actionAttributeDef.getKey(), nameSpace, actionAttributeDef.getValue());
        }

        RuleBo ruleBo = createUpdateRuleWithAction(ruleName, nameSpace, contextDefinition.getId(),
                ruleTypeDefinition.getId(), actionName, actionTypeDefinition.getId(), actionAttributes, ruleAttributes);

        createAgenda(ruleBo, contextBo, createEventAttributeDefinition());

        EngineResults results = engineExecute(CONTEXT_NAME + discriminator, nameSpace);
        assertFalse(results.getAttribute(ValidationActionTypeService.VALIDATIONS_ACTION_ATTRIBUTE) == null);
    }

    @Transactional
    @Test
    public void testInvalidWarningReversedOperator() {
        discriminator = "6";
        // reverse operator to check Rule evaluation returns expected(opposite results)
        propOperator = "!=";
        String ruleName = ValidationRuleType.INVALID.toString();
        String nameSpace =  KrmsConstants.KRMS_NAMESPACE;
        String ruleTypeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleAttributeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleTypeLabel = ValidationRuleType.INVALID.toString();
        String ruleServiceName = VALIDATION_RULE_TYPE_SERVICE;
        String actionTypeName = "KrmsActionResolverType";
        String actionTypeServicename = VALIDATION_ACTION_TYPE_SERVICE;
        String actionName = ValidationActionType.WARNING.toString();
        Map<String, String> actionAttributes = new HashMap<String, String>();
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.WARNING.getCode());
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE,WARNING_MESSAGE);
        Map<String, String> actionAttributesDefs = new HashMap<String, String>();
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.WARNING.toString());
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE, "Validation Action Message");
        Map<String, String> ruleAttributes = new HashMap<String, String>();
        ruleAttributes.put(ruleAttributeName, ValidationRuleType.INVALID.getCode());

        KrmsTypeDefinition contextTypeDefinition = createContextTypeDef("ContextTypeName" + discriminator, nameSpace,
                "ContextAttributeName" + discriminator, "ContextLabel", "ContextServiceName");

        ContextDefinition contextDefinition =  createContext(CONTEXT_NAME +discriminator, nameSpace,
                contextTypeDefinition.getId(), Collections.EMPTY_MAP );
        ContextBo contextBo = ContextBo.from(contextDefinition);

        KrmsTypeDefinition ruleTypeDefinition = createUpdateRuleTypeDef(ruleTypeName, nameSpace, ruleAttributeName,
                ruleTypeLabel, ruleServiceName);

        KrmsTypeDefinition actionTypeDefinition = createUpdateActionTypeDef(actionTypeName, nameSpace,
                actionTypeServicename);

        for (Map.Entry<String, String> actionAttributeDef : actionAttributesDefs.entrySet()) {
            createUpdateAttribute(actionAttributeDef.getKey(), nameSpace, actionAttributeDef.getValue());
        }

        RuleBo ruleBo = createUpdateRuleWithAction(ruleName, nameSpace, contextDefinition.getId(),
                ruleTypeDefinition.getId(), actionName, actionTypeDefinition.getId(), actionAttributes, ruleAttributes);

        createAgenda(ruleBo, contextBo, createEventAttributeDefinition());

        EngineResults results = engineExecute(CONTEXT_NAME + discriminator, nameSpace);
        assertNull(results.getAttribute(ValidationActionTypeService.VALIDATIONS_ACTION_ATTRIBUTE));
    }

    @Transactional
    @Test
    public void testValidErrorReversedOperator() {
        discriminator = "7";
        // reverse operator to check Rule evaluation returns expected(opposite results)
        propOperator = "!=";
        String ruleName = ValidationRuleType.VALID.toString();
        String nameSpace =  KrmsConstants.KRMS_NAMESPACE;
        String ruleTypeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleAttributeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleTypeLabel = ValidationRuleType.VALID.toString();
        String ruleServiceName = VALIDATION_RULE_TYPE_SERVICE;
        String actionTypeName = "KrmsActionResolverType";
        String actionTypeServicename = VALIDATION_ACTION_TYPE_SERVICE;
        String actionName = ValidationActionType.ERROR.toString();
        Map<String, String> actionAttributes =  new HashMap<String, String>();
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.ERROR.getCode());
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE,ERROR_MESSAGE);
        Map<String, String> actionAttributesDefs = new HashMap<String, String>();
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.ERROR.toString());
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE, "Validation Action Message");
        Map<String, String> ruleAttributes = new HashMap<String, String>();
        ruleAttributes.put(ruleAttributeName, ValidationRuleType.VALID.getCode());

        KrmsTypeDefinition contextTypeDefinition = createContextTypeDef("ContextTypeName" + discriminator, nameSpace,
                "ContextAttributeName" + discriminator, "ContextLabel", "ContextServiceName");

        ContextDefinition contextDefinition =  createContext(CONTEXT_NAME + discriminator, nameSpace,
                contextTypeDefinition.getId(), Collections.EMPTY_MAP );
        ContextBo contextBo = ContextBo.from(contextDefinition);

        KrmsTypeDefinition ruleTypeDefinition = createUpdateRuleTypeDef(ruleTypeName, nameSpace, ruleAttributeName,
                ruleTypeLabel, ruleServiceName);

        KrmsTypeDefinition actionTypeDefinition = createUpdateActionTypeDef(actionTypeName, nameSpace,
                actionTypeServicename);

        for (Map.Entry<String, String> actionAttributeDef : actionAttributesDefs.entrySet()) {
            createUpdateAttribute(actionAttributeDef.getKey(), nameSpace, actionAttributeDef.getValue());
        }

        RuleBo ruleBo = createUpdateRuleWithAction(ruleName, nameSpace, contextDefinition.getId(),
                ruleTypeDefinition.getId(), actionName, actionTypeDefinition.getId(), actionAttributes, ruleAttributes);

        createAgenda(ruleBo, contextBo, createEventAttributeDefinition());

        EngineResults results = engineExecute(CONTEXT_NAME + discriminator, nameSpace);
        assertFalse(results.getAttribute(ValidationActionTypeService.VALIDATIONS_ACTION_ATTRIBUTE) == null);
    }

    @Transactional
    @Test
    public void testInvalidErrorReversedOperator() {
        discriminator = "8";
        // reverse operator to check Rule evaluation returns expected(opposite results)
        propOperator = "!=";
        String ruleName = ValidationRuleType.INVALID.toString();
        String nameSpace =  KrmsConstants.KRMS_NAMESPACE;
        String ruleTypeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleAttributeName = ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE;
        String ruleTypeLabel = ValidationRuleType.INVALID.toString();
        String ruleServiceName = VALIDATION_RULE_TYPE_SERVICE;
        String actionTypeName = "KrmsActionResolverType";
        String actionTypeServicename = VALIDATION_ACTION_TYPE_SERVICE;
        String actionName = ValidationActionType.ERROR.toString();
        Map<String, String> actionAttributes =  new HashMap<String, String>();
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.ERROR.getCode());
        actionAttributes.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE,ERROR_MESSAGE);
        Map<String, String> actionAttributesDefs = new HashMap<String, String>();
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE, ValidationActionType.ERROR.toString());
        actionAttributesDefs.put(ValidationActionTypeService.VALIDATIONS_ACTION_MESSAGE_ATTRIBUTE, "Validation Action Message");
        Map<String, String> ruleAttributes = new HashMap<String, String>();
        ruleAttributes.put(ruleAttributeName, ValidationRuleType.INVALID.getCode());

        KrmsTypeDefinition contextTypeDefinition = createContextTypeDef("ContextTypeName" + discriminator,
                KrmsConstants.KRMS_NAMESPACE, "ContextAttributeName" + discriminator, "ContextLabel", "ContextServiceName");

        ContextDefinition contextDefinition =  createContext(CONTEXT_NAME + discriminator, nameSpace,
                contextTypeDefinition.getId(), Collections.EMPTY_MAP );
        ContextBo contextBo = ContextBo.from(contextDefinition);

        KrmsTypeDefinition ruleTypeDefinition = createUpdateRuleTypeDef(ruleTypeName, nameSpace, ruleAttributeName,
                ruleTypeLabel, ruleServiceName);

        KrmsTypeDefinition actionTypeDefinition = createUpdateActionTypeDef(actionTypeName, nameSpace,
                actionTypeServicename);

        for (Map.Entry<String, String> actionAttributeDef : actionAttributesDefs.entrySet()) {
            createUpdateAttribute(actionAttributeDef.getKey(), nameSpace, actionAttributeDef.getValue());
        }

        RuleBo ruleBo = createUpdateRuleWithAction(ruleName, nameSpace, contextDefinition.getId(),
                ruleTypeDefinition.getId(), actionName, actionTypeDefinition.getId(), actionAttributes, ruleAttributes);

        createAgenda(ruleBo, contextBo, createEventAttributeDefinition());

        EngineResults results = engineExecute(CONTEXT_NAME + discriminator, KrmsConstants.KRMS_NAMESPACE);
        assertNull(results.getAttribute(ValidationActionTypeService.VALIDATIONS_ACTION_ATTRIBUTE));
    }

    @Transactional
    @Test
    public void testDef() {
        discriminator = "9";
        KrmsTypeDefinition contextTypeDefinition = createContextTypeDef("KrmsTestContextType" + discriminator,
                KrmsConstants.KRMS_NAMESPACE, "Context1Qualifier", "Context 1 Qualifier", null);

        ContextDefinition contextDefinition =  createContext(CONTEXT_NAME + discriminator, KrmsConstants.KRMS_NAMESPACE,
                contextTypeDefinition.getId(), Collections.singletonMap("Context1Qualifier", "BLAH") );

        createAgendaDefinition(contextDefinition.getId(), "ValidationIntegration", KrmsConstants.KRMS_NAMESPACE);

        engineExecute(CONTEXT_NAME + discriminator, KrmsConstants.KRMS_NAMESPACE);
    }

    private ContextDefinition createContext(String name, String nameSpace, String typeDefId, Map<String, String> attributes) {
        ContextDefinition contextDefinition = ruleManagementService.getContextByNameAndNamespace(name, nameSpace);
        assertNull("Context with this name and namespace should not exist", contextDefinition);

        ContextDefinition.Builder contextDefinitionBuilder = ContextDefinition.Builder.create(nameSpace, name);
        contextDefinitionBuilder.setTypeId(typeDefId);
        if(attributes != null ) {
            contextDefinitionBuilder.setAttributes(attributes);
        }

        return ruleManagementService.findCreateContext(contextDefinitionBuilder.build());
    }

    private KrmsTypeDefinition createContextTypeDef(String contextTypeName, String nameSpace, String attributeName,
            String attributeLabel, String serviceName) {
        String attributeId = createUpdateAttribute(attributeName, nameSpace, attributeLabel);

        KrmsTypeDefinition.Builder typeDefinitionBuilder = KrmsTypeDefinition.Builder.create(contextTypeName, nameSpace);
        typeDefinitionBuilder.setServiceName(serviceName);

        KrmsTypeAttribute.Builder attribDefinitionBuilder = KrmsTypeAttribute.Builder.create("ContextTypeId", attributeId, 1);
        typeDefinitionBuilder.setAttributes(Collections.singletonList(attribDefinitionBuilder));
        KrmsTypeDefinition typeDef = krmsTypeBoService.createKrmsType(typeDefinitionBuilder.build());
        assertNotNull(typeDef);

        return typeDef;
    }

    private KrmsTypeDefinition createUpdateRuleTypeDef(String ruleTypeName, String nameSpace, String attributeName,
            String attributeLabel, String serviceName) {
        String attributeId = createUpdateAttribute(attributeName, nameSpace, attributeLabel);
        KrmsAttributeDefinition attributeDefinition = krmsTypeBoService.getAttributeDefinitionById(attributeId);
        KrmsAttributeDefinition.Builder attributeDefinitionBuilder = KrmsAttributeDefinition.Builder.create(attributeDefinition);

        KrmsTypeDefinition.Builder typeDefinitionBuilder = null;
        KrmsTypeDefinition typeDefinition = krmsTypeBoService.getTypeByName(nameSpace, ruleTypeName);
        if (typeDefinition == null) {
            typeDefinitionBuilder = KrmsTypeDefinition.Builder.create(ruleTypeName, nameSpace);
            typeDefinitionBuilder.setServiceName(serviceName);
            KrmsTypeAttribute.Builder typeAttributeBuilder = KrmsTypeAttribute.Builder.create(ruleTypeName, attributeId, 1);
            typeDefinitionBuilder.setAttributes(Collections.singletonList(typeAttributeBuilder));
            typeDefinition = krmsTypeBoService.createKrmsType(typeDefinitionBuilder.build());
        } else {
            typeDefinitionBuilder = KrmsTypeDefinition.Builder.create(typeDefinition);
            typeDefinitionBuilder.setServiceName(serviceName);
            KrmsTypeAttribute.Builder typeAttributeBuilder = KrmsTypeAttribute.Builder.create(typeDefinition.getAttributes().get(0));
            typeAttributeBuilder.setAttributeDefinitionId(attributeId);
            typeDefinitionBuilder.setAttributes(Collections.singletonList(typeAttributeBuilder));
            typeDefinition = krmsTypeBoService.updateKrmsType(typeDefinitionBuilder.build());
        }

        assertNotNull(typeDefinition);

        return typeDefinition;
    }

    private String createUpdateAttribute(String attributeName, String nameSpace, String attributeLabel) {
        KrmsAttributeDefinition attributeDefinition = krmsAttributeDefinitionService.getAttributeDefinitionByNameAndNamespace(attributeName, nameSpace);

        KrmsAttributeDefinition.Builder attributeDefinitionBuilder = null;
        if (attributeDefinition != null) {
            attributeDefinitionBuilder = KrmsAttributeDefinition.Builder.create(attributeDefinition);
            attributeDefinitionBuilder.setLabel(attributeLabel);
            attributeDefinitionBuilder.setActive(true);
            krmsAttributeDefinitionService.updateAttributeDefinition(attributeDefinitionBuilder.build());
        } else {
            attributeDefinitionBuilder = KrmsAttributeDefinition.Builder.create(null, attributeName, nameSpace);
            attributeDefinitionBuilder.setLabel(attributeLabel);
            attributeDefinitionBuilder.setActive(true);
            krmsAttributeDefinitionService.createAttributeDefinition(attributeDefinitionBuilder.build());
        }

        attributeDefinition = krmsAttributeDefinitionService.getAttributeDefinitionByNameAndNamespace(attributeName,
                nameSpace);
        assertNotNull(attributeDefinition.getId());

        return attributeDefinition.getId();
    }

    private void createAgendaDefinition(String contextId, String eventName, String nameSpace ) {
        AgendaDefinition agendaDef = AgendaDefinition.Builder.create(null, "testAgenda", null, contextId).build();
        agendaDef = agendaBoService.createAgenda(agendaDef);

        AgendaItemDefinition.Builder agendaItemBuilder1 = AgendaItemDefinition.Builder.create(null, agendaDef.getId());
        RuleDefinition ruleDefinition = createRuleDefinition1(contextId, nameSpace);
        agendaItemBuilder1.setRuleId(ruleDefinition.getId());
//        agendaItemBuilder1.setRule(RuleDefinition.Builder.create(ruleDefinition));

        AgendaItemDefinition agendaItem1 = agendaBoService.createAgendaItem(agendaItemBuilder1.build());

        AgendaDefinition.Builder agendaDefBuilder1 = AgendaDefinition.Builder.create(agendaDef);
        agendaDefBuilder1.setFirstItemId(agendaItem1.getId());
        agendaDef = agendaDefBuilder1.build();

        agendaBoService.updateAgenda(agendaDef);
    }

    private RuleDefinition createRuleDefinition1(String contextId, String nameSpace) {
        // Rule 1
        RuleDefinition.Builder ruleDefBuilder1 = RuleDefinition.Builder.create(null, "Rule1", nameSpace, null, null);
        RuleDefinition ruleDef1 = ruleBoService.createRule(ruleDefBuilder1.build());

        ruleDefBuilder1 = RuleDefinition.Builder.create(ruleDef1);
        ruleDefBuilder1.setProposition(createCompoundProposition(contextId, ruleDef1));
        ruleDef1 = ruleDefBuilder1.build();
        ruleDef1 = ruleBoService.updateRule(ruleDef1);

        // Action
        ActionDefinition.Builder actionDefBuilder1 = ActionDefinition.Builder.create(null, "testAction1", nameSpace,
                createUpdateActionTypeDef("KrmsActionResolverType", nameSpace, "testActionTypeService").getId(), ruleDef1.getId(), 1);
        ActionDefinition actionDef1 = actionBoService.createAction(actionDefBuilder1.build());

        return ruleDef1;
    }
    

    private KrmsTypeDefinition createKrmsCampusTypeDefinition(String nameSpace) {
	    // KrmsType for campus svc
        KrmsTypeDefinition.Builder krmsCampusTypeDefnBuilder = KrmsTypeDefinition.Builder.create("CAMPUS", nameSpace);
        KrmsTypeDefinition krmsCampusTypeDefinition = krmsTypeBoService.createKrmsType(krmsCampusTypeDefnBuilder.build());
        return krmsCampusTypeDefinition;
    }

    private KrmsTypeDefinition createKrmsActionTypeDefx(String name, String nameSpace, String actionTypeServiceName) {
        KrmsTypeDefinition.Builder krmsActionTypeDefnBuilder = KrmsTypeDefinition.Builder.create(name, nameSpace);
        krmsActionTypeDefnBuilder.setServiceName(actionTypeServiceName);
        KrmsTypeDefinition krmsActionTypeDefinition = krmsTypeBoService.createKrmsType(krmsActionTypeDefnBuilder.build());

        return krmsActionTypeDefinition;
    }

    private KrmsTypeDefinition createUpdateActionTypeDef(String actionTypeName, String nameSpace, String actionTypeServiceName) {
        KrmsTypeDefinition.Builder typeDefinitionBuilder = null;

        KrmsTypeDefinition typeDefinition = krmsTypeBoService.getTypeByName(nameSpace, actionTypeName);
        if (typeDefinition == null) {
            typeDefinitionBuilder = KrmsTypeDefinition.Builder.create(actionTypeName, nameSpace);
            typeDefinitionBuilder.setServiceName(actionTypeServiceName);
            typeDefinition = krmsTypeBoService.createKrmsType(typeDefinitionBuilder.build());
        } else {
            typeDefinitionBuilder = KrmsTypeDefinition.Builder.create(typeDefinition);
            typeDefinitionBuilder.setServiceName(actionTypeServiceName);
            typeDefinition = krmsTypeBoService.updateKrmsType(typeDefinitionBuilder.build());
        }

        assertNotNull(typeDefinition);

        return typeDefinition;
    }

    private EngineResults engineExecute(String contextName, String nameSpace) {
        Map<String, String> contextQualifiers = new HashMap<String, String>();
        contextQualifiers.put("name", contextName);
        contextQualifiers.put("namespaceCode", nameSpace);

        SelectionCriteria sc1 = SelectionCriteria.createCriteria(new DateTime(),
                contextQualifiers, Collections.<String,String>emptyMap());

        Facts.Builder factsBuilder1 = Facts.Builder.create();
        factsBuilder1.addFact(TERM_NAME, "BL");

        ExecutionOptions xOptions1 = new ExecutionOptions();
        xOptions1.setFlag(ExecutionFlag.LOG_EXECUTION, true);

        EngineResults engineResults = KrmsApiServiceLocator.getEngine().execute(sc1, factsBuilder1.build(), xOptions1);
        assertNotNull(engineResults);
        assertTrue(engineResults.getAllResults().size() > 0);
        print(engineResults);

        return engineResults;
    }

    private void print(EngineResults engineResults) {
        System.out.println(ToStringBuilder.reflectionToString(engineResults, ToStringStyle.MULTI_LINE_STYLE));
    }

    private RuleBo createUpdateRuleWithAction(String ruleName, String nameSpace, String contextId, String ruleTypeId, String actionName,
            String actionTypeId, Map<String, String> actionAttributes, Map<String, String> ruleAttributes ) {

        RuleDefinition ruleDefinition = ruleManagementService.getRuleByNameAndNamespace(ruleName, nameSpace);
        if(ruleDefinition == null) {
            //create(String ruleId, String name, String namespace, String typeId, String propId)
            RuleDefinition.Builder ruleDefinitionBuilder = RuleDefinition.Builder.create(null, ruleName, nameSpace, ruleTypeId, null);
            ruleDefinition = ruleManagementService.createRule(ruleDefinitionBuilder.build());
        }

        RuleDefinition.Builder ruleDefinitionBuilder = RuleDefinition.Builder.create(ruleDefinition);
        ruleDefinitionBuilder.setActions(createUpdateAction(ruleDefinition.getId(), nameSpace, actionName, actionTypeId,
                actionAttributes));
        ruleDefinitionBuilder.setAttributes(ruleAttributes);

        PropositionDefinition propositionDefinition = createPropositionDefinition1(contextId, ruleDefinition.getId());
        ruleDefinitionBuilder.setProposition(PropositionDefinition.Builder.create(propositionDefinition));

        ruleDefinition =  ruleManagementService.getRule(ruleDefinition.getId());
        ruleDefinitionBuilder.setVersionNumber(ruleDefinition.getVersionNumber());
        ruleManagementService.updateRule(ruleDefinitionBuilder.build());

        ruleDefinition = ruleManagementService.getRule(ruleDefinitionBuilder.getId());

        assertNotNull(ruleDefinition.getId());
        assertNotNull(ruleDefinition.getProposition().getId());
        assertEquals(ruleDefinition.getProposition().getRuleId(), ruleDefinition.getId());
        assertEquals(1, ruleDefinition.getActions().size());
        assertNotNull(ruleDefinition.getActions().get(0).getId());
        assertEquals(2, ruleDefinition.getActions().get(0).getAttributes().size());

        return RuleBo.from(ruleDefinition);
    }

    private List<ActionDefinition.Builder> createUpdateAction(String ruleId, String nameSpace, String actionName,
            String actionTypeId, Map<String, String> actionAttributes) {
        //check if action already exists
        ActionDefinition actionDefinition = null;
        QueryByCriteria.Builder criteriaBuilder = QueryByCriteria.Builder.create();
        criteriaBuilder.setPredicates(equal("name", actionName));

        List<String> actionIds = ruleManagementService.findActionIds(criteriaBuilder.build());
        if (actionIds.size() > 0) {
            //update
            actionDefinition = ruleManagementService.getAction(actionIds.get(0));
            ActionDefinition.Builder actionDefinitionBuilder = ActionDefinition.Builder.create(actionDefinition);
            actionDefinitionBuilder.setAttributes(actionAttributes);
            ruleManagementService.updateAction(actionDefinitionBuilder.build());
            actionDefinition = ruleManagementService.getAction(actionDefinitionBuilder.getId());
        } else {
            //create
            ActionDefinition.Builder actionDefinitionBuilder = ActionDefinition.Builder.create(null, actionName, nameSpace,
                    actionTypeId, ruleId, 1);
            actionDefinitionBuilder.setAttributes(actionAttributes);
            actionDefinition = ruleManagementService.createAction(actionDefinitionBuilder.build());
        }

        return Collections.singletonList(ActionDefinition.Builder.create(actionDefinition));
    }

    private PropositionDefinition createPropositionDefinition1(String contextId, String ruleId){
        String namespace = KrmsConstants.KRMS_NAMESPACE;
        String propId = null;
        String propConstant = "BL";

        TermDefinition termDefinition = createTermDefinition1(contextId);
        String termSpecId =  termDefinition.getId();
        String termSpecType = termDefinition.getSpecification().getType();
        String termSpecDescr = termDefinition.getSpecification().getDescription();

        createTestTermSpecification(termSpecId, termSpecId, namespace, termSpecType, termSpecDescr);
        KrmsTypeDefinition krmsTypeDefinition = createKrmsTypeDefinition(null, namespace, termSpecId, "testTypeService");

        PropositionDefinition.Builder propBuilder = PropositionDefinition.Builder.create(propId,
                PropositionType.SIMPLE.getCode(), ruleId, null, Collections.<PropositionParameter.Builder>emptyList());
        propBuilder.setDescription("is campus bloomington");

        PropositionDefinition propositionDefinition = ruleManagementService.createProposition(propBuilder.build());

        List<PropositionParameter.Builder> propParam =  new ArrayList<PropositionParameter.Builder>();
        propParam.add(PropositionParameter.Builder.create(propId + "_0", propositionDefinition.getId(), termSpecId,
                PropositionParameterType.TERM.getCode(), 0));
        propParam.add(PropositionParameter.Builder.create(propId + "_1", propositionDefinition.getId(), propConstant,
                PropositionParameterType.CONSTANT.getCode(), 1));
        propParam.add(PropositionParameter.Builder.create(propId + "_2", propositionDefinition.getId(), propOperator,
                PropositionParameterType.OPERATOR.getCode(), 2));

        propBuilder = PropositionDefinition.Builder.create(propositionDefinition);
        propBuilder.setParameters(propParam);

        ruleManagementService.updateProposition(propBuilder.build());
        // re-fetch to get the updated version numbers
        propositionDefinition = ruleManagementService.getProposition(propositionDefinition.getId());

        return propositionDefinition;
    }


    private PropositionDefinition.Builder createCompoundProposition(String contextId, RuleDefinition ruleDef1) {
        // Proposition for rule 1
        List<PropositionParameter.Builder> propositionParameterBuilderList = new ArrayList<PropositionParameter.Builder>();
        propositionParameterBuilderList.add(PropositionParameter.Builder.create(null, null, createTermDefinition1(contextId).getId(),
                PropositionParameterType.TERM.getCode(), 1)
        );
        propositionParameterBuilderList.add(PropositionParameter.Builder.create(null, null, "BL",
                PropositionParameterType.CONSTANT.getCode(), 2)
        );
        propositionParameterBuilderList.add(PropositionParameter.Builder.create(null, null, ComparisonOperator.EQUALS.getCode(),
                PropositionParameterType.OPERATOR.getCode(), 3)
        );

        PropositionDefinition.Builder propositionDefBuilder1 =
            PropositionDefinition.Builder.create(null, PropositionType.SIMPLE.getCode(), ruleDef1.getId(), null /* type code is only for custom props */, propositionParameterBuilderList);
        propositionDefBuilder1.setDescription("propositionDefBuilder1 Description");

        // set the parent proposition so the builder will not puke
        for (PropositionParameter.Builder propositionParamBuilder : propositionParameterBuilderList) {
            propositionParamBuilder.setProposition(propositionDefBuilder1);
        }

        return propositionDefBuilder1;
    }

    private TermDefinition createTermDefinition1(String contextId) {
        // campusCode TermSpec
        TermSpecificationDefinition campusCodeTermSpec =
                TermSpecificationDefinition.Builder.create(null, "campusCodeTermSpec", contextId, "java.lang.String").build();
        campusCodeTermSpec = termBoService.createTermSpecification(campusCodeTermSpec);

        // Term 1
        TermDefinition termDefinition1 =
            TermDefinition.Builder.create(null, TermSpecificationDefinition.Builder.create(campusCodeTermSpec), null).build();
        termDefinition1 = termBoService.createTerm(termDefinition1);

        return termDefinition1;
    }

    private TermDefinition createTermDefinitionInteger(ContextDefinition contextDefinition) {
        // campusCode TermSpec
        TermSpecificationDefinition termSpec =
            TermSpecificationDefinition.Builder.create(null, TERM_NAME, contextDefinition.getId(), "java.lang.Integer").build();
        termSpec = termBoService.createTermSpecification(termSpec);

        // Term 1
        TermDefinition termDefinition1 =
            TermDefinition.Builder.create(null, TermSpecificationDefinition.Builder.create(termSpec), null).build();
        termDefinition1 = termBoService.createTerm(termDefinition1);

        return termDefinition1;
    }


    private KrmsAttributeDefinitionBo createEventAttributeDefinition() {
        KrmsAttributeDefinitionService service = KrmsServiceLocatorInternal.getService("krmsAttributeDefinitionService");
        assertNotNull(service);

        KrmsAttributeDefinition krmsAttributeDefinition = krmsAttributeDefinitionService.getAttributeDefinitionByNameAndNamespace(
                EVENT_ATTRIBUTE, KrmsConstants.KRMS_NAMESPACE);
        if (krmsAttributeDefinition == null) {
            KrmsAttributeDefinition.Builder krmsAttributeDefinitionBuilder = KrmsAttributeDefinition.Builder.create(null, EVENT_ATTRIBUTE, KrmsConstants.KRMS_NAMESPACE);
            krmsAttributeDefinitionBuilder.setLabel("Event");
            krmsAttributeDefinitionBuilder.setActive(true);
            krmsAttributeDefinition = krmsAttributeDefinitionService.createAttributeDefinition(krmsAttributeDefinitionBuilder.build());
        }

        assertNotNull(krmsAttributeDefinition.getId());

        return KrmsAttributeDefinitionBo.from(krmsAttributeDefinition);
    }

    private AgendaDefinition createAgenda(RuleBo ruleBo, ContextBo contextBo, KrmsAttributeDefinitionBo eventAttributeDefinition) {

        KrmsTypeDefinition agendaTypeDefinition =  krmsTypeRepository.getTypeByName(contextBo.getNamespace(), "EventAgenda");

        String attributeId = createUpdateAttribute(EVENT_ATTRIBUTE, KrmsConstants.KRMS_NAMESPACE, "Event");

        if (agendaTypeDefinition == null) {
            KrmsTypeAttribute.Builder krmsTypeAttributeBuilder = KrmsTypeAttribute.Builder.create(null, attributeId, 1);
            KrmsTypeDefinition.Builder krmsTypeDefnBuilder = KrmsTypeDefinition.Builder.create("EventAgenda", KrmsConstants.KRMS_NAMESPACE);
            krmsTypeDefnBuilder.setAttributes(Collections.singletonList(krmsTypeAttributeBuilder));
            krmsTypeDefnBuilder.setServiceName("AgendaTypeService");
            agendaTypeDefinition = krmsTypeRepository.createKrmsType(krmsTypeDefnBuilder.build());
        }

        AgendaDefinition agendaDefinition = AgendaDefinition.Builder.create(null, "testAgenda", null, contextBo.getId()).build();
        agendaDefinition = ruleManagementService.findCreateAgenda(agendaDefinition);
        AgendaDefinition.Builder agendaDefinitionBuilder = AgendaDefinition.Builder.create(agendaDefinition);

        RuleDefinition.Builder ruleDefinitionBuilder = RuleDefinition.Builder.create(ruleBo);
        AgendaItemDefinition.Builder  agendaItemDefininitionBuilder = AgendaItemDefinition.Builder.create(null, agendaDefinition.getId());
        agendaItemDefininitionBuilder.setRule(ruleDefinitionBuilder);
        agendaItemDefininitionBuilder.setAgendaId(agendaDefinition.getId());
        AgendaItemDefinition agendaItemDefinition = ruleManagementService.createAgendaItem(agendaItemDefininitionBuilder.build());

        agendaDefinitionBuilder.setFirstItemId(agendaItemDefinition.getId());
        //agendaDefinitionBuilder.setAttributes(Collections.singletonMap(eventAttributeDefinition.getName(), EVENT_ATTRIBUTE));

        ruleManagementService.updateAgenda(agendaDefinitionBuilder.build());

        return ruleManagementService.getAgenda(agendaDefinitionBuilder.getId());
    }
}
