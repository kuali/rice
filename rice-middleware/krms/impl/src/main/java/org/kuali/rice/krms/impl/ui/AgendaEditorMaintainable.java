/**
 * Copyright 2005-2018 The Kuali Foundation
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableTextInput;
import org.kuali.rice.core.api.util.tree.Node;
import org.kuali.rice.core.api.util.tree.Tree;
import org.kuali.rice.core.impl.cache.DistributedCacheManagerDecorator;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.maintenance.Maintainable;
import org.kuali.rice.krad.maintenance.MaintainableImpl;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.web.form.MaintenanceDocumentForm;
import org.kuali.rice.krms.api.KrmsConstants;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaTreeDefinition;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.api.repository.function.FunctionDefinition;
import org.kuali.rice.krms.api.repository.operator.CustomOperator;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterType;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermResolverDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.impl.repository.ActionAttributeBo;
import org.kuali.rice.krms.impl.repository.ActionBo;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.AgendaItemBo;
import org.kuali.rice.krms.impl.repository.ContextBoService;
import org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.repository.PropositionBo;
import org.kuali.rice.krms.impl.repository.PropositionParameterBo;
import org.kuali.rice.krms.impl.repository.RepositoryBoIncrementer;
import org.kuali.rice.krms.impl.repository.RuleAttributeBo;
import org.kuali.rice.krms.impl.repository.RuleBo;
import org.kuali.rice.krms.impl.repository.TermBo;
import org.kuali.rice.krms.impl.repository.TermParameterBo;
import org.kuali.rice.krms.impl.util.KrmsImplConstants;
import org.kuali.rice.krms.impl.util.KrmsRetriever;
import org.kuali.rice.krms.impl.util.KrmsServiceLocatorInternal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.kuali.rice.krms.impl.repository.BusinessObjectServiceMigrationUtils.findSingleMatching;

/**
 * {@link Maintainable} for the {@link AgendaEditor}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AgendaEditorMaintainable extends MaintainableImpl {

    private static final long serialVersionUID = 1L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AgendaEditorMaintainable.class);

    public static final String NEW_AGENDA_EDITOR_DOCUMENT_TEXT = "New Agenda Editor Document";
    private static final RepositoryBoIncrementer termIdIncrementer = new RepositoryBoIncrementer(TermBo.TERM_SEQ_NAME);
    private static final RepositoryBoIncrementer termParameterIdIncrementer = new RepositoryBoIncrementer(TermParameterBo.TERM_PARM_SEQ_NAME);
    private static final RepositoryBoIncrementer agendaItemIncrementer = new RepositoryBoIncrementer(AgendaBo.AGENDA_SEQ_NAME);

    private transient KrmsRetriever krmsRetriever = new KrmsRetriever();

    /**
     * return the contextBoService
     */
    private ContextBoService getContextBoService() {
        return KrmsRepositoryServiceLocator.getContextBoService();
    }

    public List<RemotableAttributeField> retrieveAgendaCustomAttributes(View view, Object model, Container container) {
        AgendaEditor agendaEditor = getAgendaEditor(model);
        return krmsRetriever.retrieveAgendaCustomAttributes(agendaEditor);
    }

    /**
     * Retrieve a list of {@link RemotableAttributeField}s for the parameters (if any) required by the resolver for
     * the selected term in the proposition that is under edit.
     */
    public List<RemotableAttributeField> retrieveTermParameters(View view, Object model, Container container) {

        List<RemotableAttributeField> results = new ArrayList<RemotableAttributeField>();

        AgendaEditor agendaEditor = getAgendaEditor(model);

        // Figure out which rule is being edited
        RuleBo rule = agendaEditor.getAgendaItemLine().getRule();
        if (null != rule) {

            // Figure out which proposition is being edited
            Tree<RuleTreeNode, String> propositionTree = rule.getPropositionTree();
            Node<RuleTreeNode, String> editedPropositionNode = findEditedProposition(propositionTree.getRootElement());

            if (editedPropositionNode != null) {
                PropositionBo propositionBo = editedPropositionNode.getData().getProposition();
                if (StringUtils.isEmpty(propositionBo.getCompoundOpCode()) && CollectionUtils.size(
                        propositionBo.getParameters()) > 0) {
                    // Get the term ID; if it is a new parameterized term, it will have a special prefix
                    PropositionParameterBo param = propositionBo.getParameters().get(0);
                    if (StringUtils.isNotBlank(param.getValue()) &&
                            param.getValue().startsWith(KrmsImplConstants.PARAMETERIZED_TERM_PREFIX)) {
                        String termSpecId = param.getValue().substring(
                                KrmsImplConstants.PARAMETERIZED_TERM_PREFIX.length());
                        TermResolverDefinition simplestResolver = getSimplestTermResolver(termSpecId,
                                rule.getNamespace());

                        // Get the parameters and build RemotableAttributeFields
                        if (simplestResolver != null) {
                            List<String> parameterNames = new ArrayList<String>(simplestResolver.getParameterNames());
                            Collections.sort(parameterNames); // make param order deterministic

                            for (String parameterName : parameterNames) {
                                // TODO: also allow for DD parameters if there are matching type attributes
                                RemotableTextInput.Builder controlBuilder = RemotableTextInput.Builder.create();
                                controlBuilder.setSize(64);

                                RemotableAttributeField.Builder builder = RemotableAttributeField.Builder.create(
                                        parameterName);

                                builder.setRequired(true);
                                builder.setDataType(DataType.STRING);
                                builder.setControl(controlBuilder);
                                builder.setLongLabel(parameterName);
                                builder.setShortLabel(parameterName);
                                builder.setMinLength(Integer.valueOf(1));
                                builder.setMaxLength(Integer.valueOf(64));

                                results.add(builder.build());
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    /**
     * finds the term resolver with the fewest parameters that resolves the given term specification
     *
     * @param termSpecId the id of the term specification
     * @param namespace the  namespace of the term specification
     * @return the simples {@link TermResolverDefinition} found, or null if none was found
     */
    // package access so that AgendaEditorController can use it too
    static TermResolverDefinition getSimplestTermResolver(String termSpecId,
            String namespace) {// Get the term resolver for the term spec

        List<TermResolverDefinition> resolvers =
                KrmsRepositoryServiceLocator.getTermBoService().findTermResolversByOutputId(termSpecId, namespace);

        TermResolverDefinition simplestResolver = null;

        for (TermResolverDefinition resolver : resolvers) {
            if (simplestResolver == null || simplestResolver.getParameterNames().size() < resolver.getParameterNames()
                    .size()) {
                simplestResolver = resolver;
            }
        }

        return simplestResolver;
    }

    /**
     * Find and return the node containing the proposition that is in currently in edit mode
     *
     * @param node the node to start searching from (typically the root)
     * @return the node that is currently being edited, if any.  Otherwise, null.
     */
    private Node<RuleTreeNode, String> findEditedProposition(Node<RuleTreeNode, String> node) {
        Node<RuleTreeNode, String> result = null;
        if (node.getData() != null && node.getData().getProposition() != null && node.getData().getProposition()
                .getEditMode()) {
            result = node;
        } else {
            for (Node<RuleTreeNode, String> child : node.getChildren()) {
                result = findEditedProposition(child);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Get the AgendaEditor out of the MaintenanceDocumentForm's newMaintainableObject
     *
     * @param model the MaintenanceDocumentForm
     * @return the AgendaEditor
     */
    private AgendaEditor getAgendaEditor(Object model) {
        MaintenanceDocumentForm maintenanceForm = (MaintenanceDocumentForm) model;
        return (AgendaEditor) maintenanceForm.getDocument().getNewMaintainableObject().getDataObject();
    }

    public List<RemotableAttributeField> retrieveRuleActionCustomAttributes(View view, Object model,
            Container container) {
        AgendaEditor agendaEditor = getAgendaEditor((MaintenanceDocumentForm) model);
        return krmsRetriever.retrieveRuleActionCustomAttributes(agendaEditor);
    }

    /**
     * This only supports a single action within a rule.
     */
    public List<RemotableAttributeField> retrieveRuleCustomAttributes(View view, Object model, Container container) {
        AgendaEditor agendaEditor = getAgendaEditor((MaintenanceDocumentForm) model);
        return krmsRetriever.retrieveRuleCustomAttributes(agendaEditor);
    }

    @Override
    public Object retrieveObjectForEditOrCopy(MaintenanceDocument document, Map<String, String> dataObjectKeys) {
        Object dataObject = null;

        try {
            // Since the dataObject is a wrapper class we need to build it and populate with the agenda bo.
            AgendaEditor agendaEditor = new AgendaEditor();
            AgendaBo agenda = findSingleMatching(getDataObjectService(),
                    ((AgendaEditor) getDataObject()).getAgenda().getClass(), dataObjectKeys);

            // HACK: force lazy loaded items to be fetched
            forceLoadLazyRelations(agenda);

            if (KRADConstants.MAINTENANCE_COPY_ACTION.equals(getMaintenanceAction())) {
                String dateTimeStamp = (new Date()).getTime() + "";
                String newAgendaName = AgendaItemBo.COPY_OF_TEXT + agenda.getName() + " " + dateTimeStamp;

                AgendaBo copiedAgenda = agenda.copyAgenda(newAgendaName, dateTimeStamp);

                document.getDocumentHeader().setDocumentDescription(NEW_AGENDA_EDITOR_DOCUMENT_TEXT);
                document.setFieldsClearedOnCopy(true);
                agendaEditor.setAgenda(copiedAgenda);
            } else {
                // set custom attributes map in AgendaEditor
                //                agendaEditor.setCustomAttributesMap(agenda.getAttributes());
                agendaEditor.setAgenda(agenda);
            }
            agendaEditor.setCustomAttributesMap(agenda.getAttributes());

            // set extra fields on AgendaEditor
            agendaEditor.setNamespace(agenda.getContext().getNamespace());
            agendaEditor.setContextName(agenda.getContext().getName());

            dataObject = agendaEditor;
        } catch (ClassNotPersistenceCapableException ex) {
            if (!document.getOldMaintainableObject().isExternalBusinessObject()) {
                throw new RuntimeException("Data Object Class: "
                        + getDataObjectClass()
                        + " is not persistable and is not externalizable - configuration error");
            }
            // otherwise, let fall through
        }

        return dataObject;
    }

    private void forceLoadLazyRelations(AgendaBo agenda) {
        for (AgendaItemBo item : agenda.getItems()) {
            for (ActionBo action : item.getRule().getActions()) {
                if (CollectionUtils.isEmpty(action.getAttributeBos())) { continue; }

                for (ActionAttributeBo actionAttribute : action.getAttributeBos()) {
                    actionAttribute.getAttributeDefinition();
                }
            }

            Tree propTree = item.getRule().refreshPropositionTree(true);
            walkPropositionTree(item.getRule().getProposition());

            for (RuleAttributeBo ruleAttribute : item.getRule().getAttributeBos()) {
                ruleAttribute.getAttributeDefinition();
            }
        }
    }

    private void walkPropositionTree(PropositionBo prop) {
        if (prop == null) { return; }

        if (prop.getParameters() != null) for (PropositionParameterBo param : prop.getParameters()) {
            param.getPropId();
        }

        if (prop.getCompoundComponents() != null) for (PropositionBo childProp : prop.getCompoundComponents()) {
            walkPropositionTree(childProp);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        super.processAfterNew(document, requestParameters);
        document.getDocumentHeader().setDocumentDescription(NEW_AGENDA_EDITOR_DOCUMENT_TEXT);
    }

    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        super.processAfterEdit(document, requestParameters);
        document.getDocumentHeader().setDocumentDescription("Modify Agenda Editor Document");
    }

    @Override
    public void processAfterCopy(MaintenanceDocument document,
            Map<String, String[]> parameters) {
        super.processAfterCopy(document, parameters);
        AgendaBo agendaBo = ((AgendaEditor) document.getDocumentDataObject()).getAgenda();
        agendaBo.setVersionNumber(null);

        for (AgendaItemBo agendaItem : agendaBo.getItems()) {
            agendaItem.setVersionNumber(null);
        }
    }
    @Override
    public void prepareForSave() {
        // set agenda attributes
        AgendaEditor agendaEditor = (AgendaEditor) getDataObject();
        agendaEditor.getAgenda().setAttributes(agendaEditor.getCustomAttributesMap());
    }

    @Override
    public void saveDataObject() {
        AgendaBo agendaBo = ((AgendaEditor) getDataObject()).getAgenda();

        AgendaItemBo firstItem = null;

        // Find the first agenda item
        for (AgendaItemBo agendaItem : agendaBo.getItems()) {
            if (agendaBo.getFirstItemId().equals(agendaItem.getId())) {
                firstItem = agendaItem;
            }
        }

        // if new agenda persist without items.  This works around a chicken and egg problem
        // with KRMS_AGENDA_T.INIT_AGENDA_ITM_ID and KRMS_AGENDA_ITM_T.AGENDA_ITM_ID both being non-nullable
        List<AgendaItemBo> agendaItems = agendaBo.getItems();
        List<AgendaItemBo> updatedItems = new ArrayList<AgendaItemBo>();
        List<AgendaItemBo> deletedItems = new ArrayList<AgendaItemBo>();
        AgendaBo existing = null;

        if (agendaBo.getId() != null) {
            existing = getDataObjectService().find(AgendaBo.class, agendaBo.getId());
        }

        if (existing == null) {
            agendaBo.setItems(updatedItems);
            agendaBo.setFirstItem(null);
            agendaBo = getDataObjectService().save(agendaBo);
            getDataObjectService().flush(AgendaBo.class);
            String agendaBoId = agendaBo.getId();
            agendaBo = getDataObjectService().find(AgendaBo.class,agendaBoId);
            agendaBo.setItems(agendaItems);
            agendaBo.setFirstItem(firstItem);
        } else {
            // Create a list of agendaItems that will be used to delete rules when the data object is saved
            for (AgendaItemBo existingAgendaItem : existing.getItems()) {
                boolean deletedAgendaItem = true;
                for (AgendaItemBo agendaItem : agendaBo.getItems()) {
                    if (agendaItem.getId().equalsIgnoreCase(existingAgendaItem.getId())) {
                        deletedAgendaItem = false;
                        break;
                    }
                }
                if (deletedAgendaItem) {
                    deletedItems.add(existingAgendaItem);
                }
            }
        }

        // handle saving new parameterized terms and processing custom operators
        for (AgendaItemBo agendaItem : agendaBo.getItems()) {
            PropositionBo propositionBo = agendaItem.getRule().getProposition();
            if (propositionBo != null) {
                saveNewParameterizedTerms(propositionBo);
                processCustomOperators(propositionBo);
            }
        }

        if (agendaBo != null) {
            flushCacheBeforeSave();
            getDataObjectService().flush(AgendaBo.class);

            // Need to set the first item for persistence to cascade
            agendaBo.setFirstItem(firstItem);
            getDataObjectService().save(agendaBo);

            // delete orphaned propositions, rules, etc.
            for (String deletedPropId : ((AgendaEditor) getDataObject()).getDeletedPropositionIds()) {
                PropositionBo toDelete = getDataObjectService().find(PropositionBo.class, deletedPropId);

                getDataObjectService().delete(toDelete);
            }

            for (AgendaItemBo deletedItem : deletedItems) {
                if (deletedItem.getRule() != null) {
                    getDataObjectService().delete(deletedItem.getRule());
                }
            }
        } else {
            throw new RuntimeException("Cannot save null " + AgendaBo.class.getName() + " with business object service");
        }
    }

    private void flushCacheBeforeSave(){
        //flush krms caches
        DistributedCacheManagerDecorator distributedCacheManagerDecorator =
                GlobalResourceLoader.getService(KrmsConstants.KRMS_DISTRIBUTED_CACHE);

        distributedCacheManagerDecorator.getCache(ActionDefinition.Cache.NAME).clear();
        distributedCacheManagerDecorator.getCache(AgendaItemDefinition.Cache.NAME).clear();
        distributedCacheManagerDecorator.getCache(AgendaTreeDefinition.Cache.NAME).clear();
        distributedCacheManagerDecorator.getCache(AgendaDefinition.Cache.NAME).clear();
        distributedCacheManagerDecorator.getCache(ContextDefinition.Cache.NAME).clear();
        distributedCacheManagerDecorator.getCache(KrmsAttributeDefinition.Cache.NAME).clear();
        distributedCacheManagerDecorator.getCache(KrmsTypeDefinition.Cache.NAME).clear();
        distributedCacheManagerDecorator.getCache(RuleDefinition.Cache.NAME).clear();
        distributedCacheManagerDecorator.getCache(PropositionDefinition.Cache.NAME).clear();
        distributedCacheManagerDecorator.getCache(RuleDefinition.Cache.NAME).clear();
        distributedCacheManagerDecorator.getCache(TermDefinition.Cache.NAME).clear();
        distributedCacheManagerDecorator.getCache(TermResolverDefinition.Cache.NAME).clear();
        distributedCacheManagerDecorator.getCache(TermSpecificationDefinition.Cache.NAME).clear();
    }

    /**
     * walk the proposition tree and save any new parameterized terms that are contained therein
     *
     * @param propositionBo the root proposition from which to search
     */
    private void saveNewParameterizedTerms(PropositionBo propositionBo) {
        if (StringUtils.isBlank(propositionBo.getCompoundOpCode())) {
            // it is a simple proposition
            if (!propositionBo.getParameters().isEmpty() && propositionBo.getParameters().get(0).getValue().startsWith(
                    KrmsImplConstants.PARAMETERIZED_TERM_PREFIX)) {
                String termId = propositionBo.getParameters().get(0).getValue();
                String termSpecId = termId.substring(KrmsImplConstants.PARAMETERIZED_TERM_PREFIX.length());
                // create new term
                TermBo newTerm = new TermBo();
                newTerm.setDescription(propositionBo.getNewTermDescription());
                newTerm.setSpecificationId(termSpecId);
                newTerm.setId(termIdIncrementer.getNewId());

                List<TermParameterBo> params = new ArrayList<TermParameterBo>();
                for (Map.Entry<String, String> entry : propositionBo.getTermParameters().entrySet()) {
                    TermParameterBo param = new TermParameterBo();
                    param.setTerm(newTerm);
                    param.setName(entry.getKey());
                    param.setValue(entry.getValue());
                    param.setId(termParameterIdIncrementer.getNewId());

                    params.add(param);
                }

                newTerm.setParameters(params);

                getLegacyDataAdapter().linkAndSave(newTerm);
                propositionBo.getParameters().get(0).setValue(newTerm.getId());
            }
        } else {
            // recurse
            for (PropositionBo childProp : propositionBo.getCompoundComponents()) {
                saveNewParameterizedTerms(childProp);
            }
        }
    }

    /**
     * walk the proposition tree and process any custom operators found, converting them to custom function invocations.
     *
     * @param propositionBo the root proposition from which to search and convert
     */
    private void processCustomOperators(PropositionBo propositionBo) {
        if (StringUtils.isBlank(propositionBo.getCompoundOpCode())) {
            // if it is a simple proposition with a custom operator
            if (!propositionBo.getParameters().isEmpty() && propositionBo.getParameters().get(2).getValue().startsWith(
                    KrmsImplConstants.CUSTOM_OPERATOR_PREFIX)) {
                PropositionParameterBo operatorParam = propositionBo.getParameters().get(2);

                CustomOperator customOperator =
                        KrmsServiceLocatorInternal.getCustomOperatorUiTranslator().getCustomOperator(operatorParam.getValue());

                FunctionDefinition operatorFunctionDefinition = customOperator.getOperatorFunctionDefinition();

                operatorParam.setParameterType(PropositionParameterType.FUNCTION.getCode());
                operatorParam.setValue(operatorFunctionDefinition.getId());
            }
        } else {
            // recurse
            for (PropositionBo childProp : propositionBo.getCompoundComponents()) {
                processCustomOperators(childProp);
            }
        }
    }

    /**
     * Build a map from attribute name to attribute definition from all the defined attribute definitions for the
     * specified agenda type
     *
     * @param agendaTypeId
     * @return
     */
    private Map<String, KrmsAttributeDefinition> buildAttributeDefinitionMap(String agendaTypeId) {
        KrmsAttributeDefinitionService attributeDefinitionService =
                KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();

        // build a map from attribute name to definition
        Map<String, KrmsAttributeDefinition> attributeDefinitionMap = new HashMap<String, KrmsAttributeDefinition>();

        List<KrmsAttributeDefinition> attributeDefinitions = attributeDefinitionService.findAttributeDefinitionsByType(
                agendaTypeId);

        for (KrmsAttributeDefinition attributeDefinition : attributeDefinitions) {
            attributeDefinitionMap.put(attributeDefinition.getName(), attributeDefinition);
        }
        return attributeDefinitionMap;
    }

    @Override
    public boolean isOldDataObjectInDocument() {
        boolean isOldDataObjectInExistence = true;

        if (getDataObject() == null) {
            isOldDataObjectInExistence = false;
        } else {
            // dataObject contains a non persistable wrapper - use agenda from the wrapper object instead
            Map<String, ?> keyFieldValues = getLegacyDataAdapter().getPrimaryKeyFieldValues(
                    ((AgendaEditor) getDataObject()).getAgenda());
            for (Object keyValue : keyFieldValues.values()) {
                if (keyValue == null) {
                    isOldDataObjectInExistence = false;
                } else if ((keyValue instanceof String) && StringUtils.isBlank((String) keyValue)) {
                    isOldDataObjectInExistence = false;
                }

                if (!isOldDataObjectInExistence) {
                    break;
                }
            }
        }

        return isOldDataObjectInExistence;
    }

    // Since the dataObject is a wrapper class we need to return the agendaBo instead.
    @Override
    public Class getDataObjectClass() {
        return AgendaBo.class;
    }

    @Override
    public boolean isLockable() {
        return true;
    }

    @Override
    public void processBeforeAddLine(ViewModel model, Object addLine, String collectionId, String collectionPath) {
        AgendaEditor agendaEditor = getAgendaEditor(model);
        if (addLine instanceof ActionBo) {
            ((ActionBo) addLine).setNamespace(agendaEditor.getAgendaItemLine().getRule().getNamespace());
        }

        super.processBeforeAddLine(model, addLine, collectionId, collectionPath);
    }
}
