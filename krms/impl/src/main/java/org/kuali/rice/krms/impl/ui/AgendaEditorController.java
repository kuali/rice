/**
 * Copyright 2005-2011 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.tree.Node;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.web.controller.MaintenanceDocumentController;
import org.kuali.rice.krad.web.form.MaintenanceForm;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.impl.repository.ActionAttributeBo;
import org.kuali.rice.krms.impl.repository.ActionBo;
import org.kuali.rice.krms.api.repository.LogicalOperator;
import org.kuali.rice.krms.api.repository.proposition.PropositionType;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.AgendaItemBo;
import org.kuali.rice.krms.impl.repository.ContextBoService;
import org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionBo;
import org.kuali.rice.krms.impl.repository.KrmsAttributeDefinitionService;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.repository.PropositionBo;
import org.kuali.rice.krms.impl.repository.RuleBo;
import org.kuali.rice.krms.impl.repository.RuleBoService;
import org.kuali.rice.krms.impl.rule.AgendaEditorBusRule;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Controller for the Test UI Page
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = org.kuali.rice.krms.impl.util.KrmsImplConstants.WebPaths.AGENDA_EDITOR_PATH)
public class AgendaEditorController extends MaintenanceDocumentController {

    private SequenceAccessorService sequenceAccessorService;

    /**
     * This overridden method does extra work on refresh to update the namespace when the context has been changed.
     *
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#refresh(org.kuali.rice.krad.web.form.UifFormBase, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @RequestMapping(params = "methodToCall=" + "refresh")
    @Override
    public ModelAndView refresh(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = super.refresh(form, result, request, response);

        // handle return from context lookup
        MaintenanceForm maintenanceForm = (MaintenanceForm) form;
        AgendaEditor agendaEditor = ((AgendaEditor) maintenanceForm.getDocument().getNewMaintainableObject().getDataObject());
        AgendaEditorBusRule rule = new AgendaEditorBusRule();
        if (rule.validContext(agendaEditor) && rule.validAgendaName(agendaEditor)) {
            // update the namespace on all agenda related objects if the contest has been changed
            if (!StringUtils.equals(agendaEditor.getOldContextId(), agendaEditor.getAgenda().getContextId())) {
                agendaEditor.setOldContextId(agendaEditor.getAgenda().getContextId());

                String namespace = "";
                if (!StringUtils.isBlank(agendaEditor.getAgenda().getContextId())) {
                    namespace = getContextBoService().getContextByContextId(agendaEditor.getAgenda().getContextId()).getNamespace();
                }

                for (AgendaItemBo agendaItem : agendaEditor.getAgenda().getItems()) {
                    agendaItem.getRule().setNamespace(namespace);
                    for (ActionBo action : agendaItem.getRule().getActions()) {
                        action.setNamespace(namespace);
                    }
                }
            }
        }
        return modelAndView;
    }

    /**
     * This method updates the existing rule in the agenda.
     */
    @RequestMapping(params = "methodToCall=" + "goToAddRule")
    public ModelAndView goToAddRule(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        setAgendaItemLine(form, null);
        AgendaEditor agendaEditor = getAgendaEditor(form);
        agendaEditor.setAddRuleInProgress(true);
        form.getActionParameters().put(UifParameters.NAVIGATE_TO_PAGE_ID, "AgendaEditorView-AddRule-Page");
        return super.navigate(form, result, request, response);
    }

    /**
     * This method sets the agendaItemLine for adding/editing AgendaItems.
     * The agendaItemLine is a copy of the agendaItem so that changes are not applied when
     * they are abandoned.  If the agendaItem is null a new empty agendaItemLine is created.
     *
     * @param form
     * @param agendaItem
     */
    private void setAgendaItemLine(UifFormBase form, AgendaItemBo agendaItem) {
        AgendaEditor agendaEditor = getAgendaEditor(form);
        if (agendaItem == null) {
            RuleBo rule = new RuleBo();
            rule.setId(getSequenceAccessorService().getNextAvailableSequenceNumber("KRMS_RULE_S")
                    .toString());
            if (StringUtils.isBlank(agendaEditor.getAgenda().getContextId())) {
                rule.setNamespace("");
            } else {
                rule.setNamespace(getContextBoService().getContextByContextId(agendaEditor.getAgenda().getContextId()).getNamespace());
            }
            agendaItem = new AgendaItemBo();
            agendaItem.setRule(rule);
            agendaEditor.setAgendaItemLine(agendaItem);
        } else {
            // TODO: Add a copy not the reference
            agendaEditor.setAgendaItemLine((AgendaItemBo) ObjectUtils.deepCopy(agendaItem));
        }


        if (agendaItem.getRule().getActions().isEmpty()) {
            ActionBo actionBo = new ActionBo();
            actionBo.setTypeId("");
            actionBo.setNamespace(agendaItem.getRule().getNamespace());
            actionBo.setRuleId(agendaItem.getRule().getId());
            actionBo.setSequenceNumber(1);
            agendaEditor.setAgendaItemLineRuleAction(actionBo);
        } else {
            agendaEditor.setAgendaItemLineRuleAction(agendaItem.getRule().getActions().get(0));
        }

        agendaEditor.setCustomRuleActionAttributesMap(agendaEditor.getAgendaItemLineRuleAction().getAttributes());
    }

    /**
     * This method returns the id of the selected agendaItem.
     *
     * @param form
     * @return selectedAgendaItemId
     */
    private String getSelectedAgendaItemId(UifFormBase form) {
        AgendaEditor agendaEditor = getAgendaEditor(form);
        return agendaEditor.getSelectedAgendaItemId();
    }

    /**
     * This method sets the id of the cut agendaItem.
     *
     * @param form
     * @param cutAgendaItemId
     */
    private void setCutAgendaItemId(UifFormBase form, String cutAgendaItemId) {
        AgendaEditor agendaEditor = getAgendaEditor(form);
        agendaEditor.setCutAgendaItemId(cutAgendaItemId);
    }

    /**
     * This method returns the id of the cut agendaItem.
     *
     * @param form
     * @return cutAgendaItemId
     */
    private String getCutAgendaItemId(UifFormBase form) {
        AgendaEditor agendaEditor = getAgendaEditor(form);
        return agendaEditor.getCutAgendaItemId();
    }

    /**
     * This method updates the existing rule in the agenda.
     */
    @RequestMapping(params = "methodToCall=" + "goToEditRule")
    public ModelAndView goToEditRule(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        AgendaEditor agendaEditor = getAgendaEditor(form);
        agendaEditor.setAddRuleInProgress(false);
        // this is the root of the tree:
        AgendaItemBo firstItem = getFirstAgendaItem(agendaEditor.getAgenda());
        String selectedItemId = agendaEditor.getSelectedAgendaItemId();
        AgendaItemBo node = getAgendaItemById(firstItem, selectedItemId);

        setAgendaItemLine(form, node);

        form.getActionParameters().put(UifParameters.NAVIGATE_TO_PAGE_ID, "AgendaEditorView-EditRule-Page");
        return super.navigate(form, result, request, response);
    }

    /**
     *  This method adds the newly create rule to the agenda.
     */
    @RequestMapping(params = "methodToCall=" + "addRule")
    public ModelAndView addRule(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        AgendaEditor agendaEditor = getAgendaEditor(form);
        AgendaBo agenda = agendaEditor.getAgenda();
        AgendaItemBo newAgendaItem = agendaEditor.getAgendaItemLine();

        newAgendaItem.getRule().setAttributes(agendaEditor.getCustomRuleAttributesMap());
        updateRuleAction(agendaEditor);

        if (agenda.getItems() == null) {
            agenda.setItems(new ArrayList<AgendaItemBo>());
        }

        AgendaEditorBusRule rule = new AgendaEditorBusRule();
        MaintenanceForm maintenanceForm = (MaintenanceForm) form;
        MaintenanceDocument document = maintenanceForm.getDocument();
        if (rule.processAgendaItemBusinessRules(document)) {
            newAgendaItem.setId(getSequenceAccessorService().getNextAvailableSequenceNumber("KRMS_AGENDA_ITM_S")
                    .toString());
            newAgendaItem.setAgendaId(getCreateAgendaId(agenda));
            if (agenda.getFirstItemId() == null) {
                agenda.setFirstItemId(newAgendaItem.getId());
            } else {
                // insert agenda in tree
                String selectedAgendaItemId = getSelectedAgendaItemId(form);
                if (StringUtils.isBlank(selectedAgendaItemId)) {
                    // add after the last root node
                    AgendaItemBo node = getFirstAgendaItem(agenda);
                    while (node.getAlways() != null) {
                        node = node.getAlways();
                    }
                    node.setAlwaysId(newAgendaItem.getId());
                    node.setAlways(newAgendaItem);
                } else {
                    // add after selected node
                    AgendaItemBo firstItem = getFirstAgendaItem(agenda);
                    AgendaItemBo node = getAgendaItemById(firstItem, selectedAgendaItemId);
                    newAgendaItem.setAlwaysId(node.getAlwaysId());
                    newAgendaItem.setAlways(node.getAlways());
                    node.setAlwaysId(newAgendaItem.getId());
                    node.setAlways(newAgendaItem);
                }
            }
            // add it to the collection on the agenda too
            agenda.getItems().add(newAgendaItem);
            agendaEditor.setAddRuleInProgress(false);
            form.getActionParameters().put(UifParameters.NAVIGATE_TO_PAGE_ID, "AgendaEditorView-Agenda-Page");
        } else {
            form.getActionParameters().put(UifParameters.NAVIGATE_TO_PAGE_ID, "AgendaEditorView-AddRule-Page");
        }
        return super.navigate(form, result, request, response);
    }

    /**
     * This method returns the agendaId of the given agenda.  If the agendaId is null a new id will be created.
     */
    private String getCreateAgendaId(AgendaBo agenda) {
        if (agenda.getId() == null) {
            agenda.setId(getSequenceAccessorService().getNextAvailableSequenceNumber("KRMS_AGENDA_S").toString());
        }
        return agenda.getId();
    }

    private void updateRuleAction(AgendaEditor agendaEditor) {
        agendaEditor.getAgendaItemLine().getRule().setActions(new ArrayList<ActionBo>());
        if (StringUtils.isNotBlank(agendaEditor.getAgendaItemLineRuleAction().getTypeId())) {
            agendaEditor.getAgendaItemLineRuleAction().setAttributes(agendaEditor.getCustomRuleActionAttributesMap());
            agendaEditor.getAgendaItemLine().getRule().getActions().add(agendaEditor.getAgendaItemLineRuleAction());
        }
    }

    /**
     * Build a map from attribute name to attribute definition from all the defined attribute definitions for the
     * specified rule action type
     * @param actionTypeId
     * @return
     */
    private Map<String, KrmsAttributeDefinition> buildAttributeDefinitionMap(String actionTypeId) {
        KrmsAttributeDefinitionService attributeDefinitionService =
            KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService();

        // build a map from attribute name to definition
        Map<String, KrmsAttributeDefinition> attributeDefinitionMap = new HashMap<String, KrmsAttributeDefinition>();

        List<KrmsAttributeDefinition> attributeDefinitions =
                attributeDefinitionService.findAttributeDefinitionsByType(actionTypeId);

        for (KrmsAttributeDefinition attributeDefinition : attributeDefinitions) {
            attributeDefinitionMap.put(attributeDefinition.getName(), attributeDefinition);
        }
        return attributeDefinitionMap;
    }

    /**
     * This method updates the existing rule in the agenda.
     */
    @RequestMapping(params = "methodToCall=" + "editRule")
    public ModelAndView editRule(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        AgendaEditor agendaEditor = getAgendaEditor(form);
        // this is the root of the tree:
        AgendaItemBo firstItem = getFirstAgendaItem(agendaEditor.getAgenda());
        AgendaItemBo node = getAgendaItemById(firstItem, getSelectedAgendaItemId(form));
        AgendaItemBo agendaItemLine = agendaEditor.getAgendaItemLine();

        agendaItemLine.getRule().setAttributes(agendaEditor.getCustomRuleAttributesMap());
        updateRuleAction(agendaEditor);

        AgendaEditorBusRule rule = new AgendaEditorBusRule();
        MaintenanceForm maintenanceForm = (MaintenanceForm) form;
        MaintenanceDocument document = maintenanceForm.getDocument();
        if (rule.processAgendaItemBusinessRules(document)) {
            node.setRule(agendaItemLine.getRule());
            form.getActionParameters().put(UifParameters.NAVIGATE_TO_PAGE_ID, "AgendaEditorView-Agenda-Page");
        } else {
            form.getActionParameters().put(UifParameters.NAVIGATE_TO_PAGE_ID, "AgendaEditorView-EditRule-Page");
        }
        return super.navigate(form, result, request, response);
    }

    /**
     * @return the ALWAYS {@link AgendaItemInstanceChildAccessor} for the last ALWAYS child of the instance accessed by the parameter.
     * It will by definition refer to null.  If the instanceAccessor parameter refers to null, then it will be returned.  This is useful
     * for adding a youngest child to a sibling group.
     */
    private AgendaItemInstanceChildAccessor getLastChildsAlwaysAccessor(AgendaItemInstanceChildAccessor instanceAccessor) {
        AgendaItemBo next = instanceAccessor.getChild();
        if (next == null) return instanceAccessor;
        while (next.getAlways() != null) { next = next.getAlways(); };
        return new AgendaItemInstanceChildAccessor(AgendaItemChildAccessor.always, next);
    }

    /**
     * @return the accessor to the child with the given agendaItemId under the given parent.  This method will search both When TRUE and 
     * When FALSE sibling groups.  If the instance with the given id is not found, null is returned.
     * @see AgendaItemChildAccessor for nomenclature explanation
     */
    private AgendaItemInstanceChildAccessor getInstanceAccessorToChild(AgendaItemBo parent, String agendaItemId) {

        // first try When TRUE, then When FALSE via AgendaItemChildAccessor.levelOrderChildren
        for (AgendaItemChildAccessor levelOrderChildAccessor : AgendaItemChildAccessor.children) {

            AgendaItemBo next = levelOrderChildAccessor.getChild(parent);
            
            // if the first item matches, return the accessor from the parent
            if (next != null && agendaItemId.equals(next.getId())) return new AgendaItemInstanceChildAccessor(levelOrderChildAccessor, parent);

            // otherwise walk the children
            while (next != null && next.getAlwaysId() != null) {
                if (next.getAlwaysId().equals(agendaItemId)) return new AgendaItemInstanceChildAccessor(AgendaItemChildAccessor.always, next);
                // move down
                next = next.getAlways();
            }
        }
        
        return null;
    }

    @RequestMapping(params = "methodToCall=" + "ajaxRefresh")
    public ModelAndView ajaxRefresh(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // call the super method to avoid the agenda tree being reloaded from the db
        return super.updateComponent(form, result, request, response);
    }

    @RequestMapping(params = "methodToCall=" + "moveUp")
    public ModelAndView moveUp(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        moveSelectedSubtreeUp(form);

        return super.refresh(form, result, request, response);
    }

    @RequestMapping(params = "methodToCall=" + "ajaxMoveUp")
    public ModelAndView ajaxMoveUp(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        moveSelectedSubtreeUp(form);

        // call the super method to avoid the agenda tree being reloaded from the db
        return super.updateComponent(form, result, request, response);
    }

    /**
     *
     * @param form
     * @see AgendaItemChildAccessor for nomenclature explanation
     */
    private void moveSelectedSubtreeUp(UifFormBase form) {

        /* Rough algorithm for moving a node up.  This is a "level order" move.  Note that in this tree,
         * level order means something a bit funky.  We are defining a level as it would be displayed in the browser,
         * so only the traversal of When FALSE or When TRUE links increments the level, since ALWAYS linked nodes are
         * considered siblings.
         *
         * find the following:
         *   node := the selected node
         *   parent := the selected node's parent, its containing node (via when true or when false relationship)
         *   parentsOlderCousin := the parent's level-order predecessor (sibling or cousin)
         *
         * if (node is first child in sibling group)
         *     if (node is in When FALSE group)
         *         move node to last position in When TRUE group
         *     else
         *         find youngest child of parentsOlderCousin and put node after it
         * else
         *     move node up within its sibling group
         */

        AgendaEditor agendaEditor = getAgendaEditor(form);
        // this is the root of the tree:
        AgendaItemBo firstItem = getFirstAgendaItem(agendaEditor.getAgenda());

        String selectedItemId = agendaEditor.getSelectedAgendaItemId();
        AgendaItemBo node = getAgendaItemById(firstItem, selectedItemId);
        AgendaItemBo parent = getParent(firstItem, selectedItemId);
        AgendaItemBo parentsOlderCousin = (parent == null) ? null : getNextOldestOfSameGeneration(firstItem, parent);

        AgendaItemChildAccessor childAccessor = getOldestChildAccessor(node, parent);
        if (childAccessor != null) { // node is first child in sibling group
            if (childAccessor == AgendaItemChildAccessor.whenFalse) {
                // move node to last position in When TRUE group
                AgendaItemInstanceChildAccessor youngestWhenTrueSiblingInsertionPoint =
                        getLastChildsAlwaysAccessor(new AgendaItemInstanceChildAccessor(AgendaItemChildAccessor.whenTrue, parent));
                youngestWhenTrueSiblingInsertionPoint.setChild(node);
                AgendaItemChildAccessor.whenFalse.setChild(parent, node.getAlways());
                AgendaItemChildAccessor.always.setChild(node, null);

            } else if (parentsOlderCousin != null) {
                // find youngest child of parentsOlderCousin and put node after it
                AgendaItemInstanceChildAccessor youngestWhenFalseSiblingInsertionPoint =
                        getLastChildsAlwaysAccessor(new AgendaItemInstanceChildAccessor(AgendaItemChildAccessor.whenFalse, parentsOlderCousin));
                youngestWhenFalseSiblingInsertionPoint.setChild(node);
                AgendaItemChildAccessor.whenTrue.setChild(parent, node.getAlways());
                AgendaItemChildAccessor.always.setChild(node, null);
            }
        } else if (!selectedItemId.equals(firstItem.getId())) { // conditional to miss special case of first node

            AgendaItemBo bogusRootNode = null;
            if (parent == null) {
                // special case, this is a top level sibling. rig up special parent node
                bogusRootNode = new AgendaItemBo();
                AgendaItemChildAccessor.whenTrue.setChild(bogusRootNode, firstItem);
                parent = bogusRootNode;
            }

            // move node up within its sibling group
            AgendaItemInstanceChildAccessor accessorToSelectedNode = getInstanceAccessorToChild(parent, node.getId());
            AgendaItemBo olderSibling = accessorToSelectedNode.getInstance();
            AgendaItemInstanceChildAccessor accessorToOlderSibling = getInstanceAccessorToChild(parent, olderSibling.getId());

            accessorToOlderSibling.setChild(node);
            accessorToSelectedNode.setChild(node.getAlways());
            AgendaItemChildAccessor.always.setChild(node, olderSibling);

            if (bogusRootNode != null) {
                // clean up special case with bogus root node
                agendaEditor.getAgenda().setFirstItemId(bogusRootNode.getWhenTrueId());
            }
        }
    }

    @RequestMapping(params = "methodToCall=" + "moveDown")
    public ModelAndView moveDown(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        moveSelectedSubtreeDown(form);
        
        return super.refresh(form, result, request, response);
    }

    @RequestMapping(params = "methodToCall=" + "ajaxMoveDown")
    public ModelAndView ajaxMoveDown(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        moveSelectedSubtreeDown(form);

        // call the super method to avoid the agenda tree being reloaded from the db
        return super.updateComponent(form, result, request, response);
    }

    /**
     *
     * @param form
     * @see AgendaItemChildAccessor for nomenclature explanation
     */
    private void moveSelectedSubtreeDown(UifFormBase form) {

        /* Rough algorithm for moving a node down.  This is a "level order" move.  Note that in this tree,
         * level order means something a bit funky.  We are defining a level as it would be displayed in the browser,
         * so only the traversal of When FALSE or When TRUE links increments the level, since ALWAYS linked nodes are
         * considered siblings.
         *
         * find the following:
         *   node := the selected node
         *   parent := the selected node's parent, its containing node (via when true or when false relationship)
         *   parentsYoungerCousin := the parent's level-order successor (sibling or cousin)
         *
         * if (node is last child in sibling group)
         *     if (node is in When TRUE group)
         *         move node to first position in When FALSE group
         *     else
         *         move to first child of parentsYoungerCousin
         * else
         *     move node down within its sibling group
         */

        AgendaEditor agendaEditor = getAgendaEditor(form);
        // this is the root of the tree:
        AgendaItemBo firstItem = getFirstAgendaItem(agendaEditor.getAgenda());

        String selectedItemId = agendaEditor.getSelectedAgendaItemId();
        AgendaItemBo node = getAgendaItemById(firstItem, selectedItemId);
        AgendaItemBo parent = getParent(firstItem, selectedItemId);
        AgendaItemBo parentsYoungerCousin = (parent == null) ? null : getNextYoungestOfSameGeneration(firstItem, parent);

        if (node.getAlways() == null && parent != null) { // node is last child in sibling group
            // set link to selected node to null
            if (parent.getWhenTrue() != null && isSiblings(parent.getWhenTrue(), node)) { // node is in When TRUE group
                // move node to first child under When FALSE

                AgendaItemInstanceChildAccessor accessorToSelectedNode = getInstanceAccessorToChild(parent, node.getId());
                accessorToSelectedNode.setChild(null);

                AgendaItemBo parentsFirstChild = parent.getWhenFalse();
                AgendaItemChildAccessor.whenFalse.setChild(parent, node);
                AgendaItemChildAccessor.always.setChild(node, parentsFirstChild);
            } else if (parentsYoungerCousin != null) { // node is in the When FALSE group
                // move to first child of parentsYoungerCousin under When TRUE

                AgendaItemInstanceChildAccessor accessorToSelectedNode = getInstanceAccessorToChild(parent, node.getId());
                accessorToSelectedNode.setChild(null);

                AgendaItemBo parentsYoungerCousinsFirstChild = parentsYoungerCousin.getWhenTrue();
                AgendaItemChildAccessor.whenTrue.setChild(parentsYoungerCousin, node);
                AgendaItemChildAccessor.always.setChild(node, parentsYoungerCousinsFirstChild);
            }
        } else if (node.getAlways() != null) { // move node down within its sibling group

            AgendaItemBo bogusRootNode = null;
            if (parent == null) {
                // special case, this is a top level sibling. rig up special parent node
                bogusRootNode = new AgendaItemBo();
                AgendaItemChildAccessor.whenFalse.setChild(bogusRootNode, firstItem);
                parent = bogusRootNode;
            }

            // move node down within its sibling group
            AgendaItemInstanceChildAccessor accessorToSelectedNode = getInstanceAccessorToChild(parent, node.getId());
            AgendaItemBo youngerSibling = node.getAlways();
            accessorToSelectedNode.setChild(youngerSibling);
            AgendaItemChildAccessor.always.setChild(node, youngerSibling.getAlways());
            AgendaItemChildAccessor.always.setChild(youngerSibling, node);

            if (bogusRootNode != null) {
                // clean up special case with bogus root node
                agendaEditor.getAgenda().setFirstItemId(bogusRootNode.getWhenFalseId());
            }
        }
    }

    @RequestMapping(params = "methodToCall=" + "moveLeft")
    public ModelAndView moveLeft(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        moveSelectedSubtreeLeft(form);
        
        return super.refresh(form, result, request, response);
    }

    @RequestMapping(params = "methodToCall=" + "ajaxMoveLeft")
    public ModelAndView ajaxMoveLeft(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        moveSelectedSubtreeLeft(form);

        // call the super method to avoid the agenda tree being reloaded from the db
        return super.updateComponent(form, result, request, response);
    }

    /**
     *
     * @param form
     * @see AgendaItemChildAccessor for nomenclature explanation
     */
    private void moveSelectedSubtreeLeft(UifFormBase form) {

        /*
         * Move left means make it a younger sibling of it's parent.
         */

        AgendaEditor agendaEditor = getAgendaEditor(form);
        // this is the root of the tree:
        AgendaItemBo firstItem = getFirstAgendaItem(agendaEditor.getAgenda());

        String selectedItemId = agendaEditor.getSelectedAgendaItemId();
        AgendaItemBo node = getAgendaItemById(firstItem, selectedItemId);
        AgendaItemBo parent = getParent(firstItem, selectedItemId);

        if (parent != null) {
            AgendaItemInstanceChildAccessor accessorToSelectedNode = getInstanceAccessorToChild(parent, node.getId());
            accessorToSelectedNode.setChild(node.getAlways());

            AgendaItemChildAccessor.always.setChild(node, parent.getAlways());
            AgendaItemChildAccessor.always.setChild(parent, node);
        }
    }


    @RequestMapping(params = "methodToCall=" + "moveRight")
    public ModelAndView moveRight(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        moveSelectedSubtreeRight(form);

        return super.refresh(form, result, request, response);
    }

    @RequestMapping(params = "methodToCall=" + "ajaxMoveRight")
    public ModelAndView ajaxMoveRight(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        moveSelectedSubtreeRight(form);

        // call the super method to avoid the agenda tree being reloaded from the db
        return super.updateComponent(form, result, request, response);
    }

    /**
     *
     * @param form
     * @see AgendaItemChildAccessor for nomenclature explanation
     */
    private void moveSelectedSubtreeRight(UifFormBase form) {

        /*
         * Move right prefers moving to bottom of upper sibling's When FALSE branch
         * ... otherwise ..
         * moves to top of lower sibling's When TRUE branch
         */

        AgendaEditor agendaEditor = getAgendaEditor(form);
        // this is the root of the tree:
        AgendaItemBo firstItem = getFirstAgendaItem(agendaEditor.getAgenda());

        String selectedItemId = agendaEditor.getSelectedAgendaItemId();
        AgendaItemBo node = getAgendaItemById(firstItem, selectedItemId);
        AgendaItemBo parent = getParent(firstItem, selectedItemId);

        AgendaItemBo bogusRootNode = null;
        if (parent == null) {
            // special case, this is a top level sibling. rig up special parent node
            bogusRootNode = new AgendaItemBo();
            AgendaItemChildAccessor.whenFalse.setChild(bogusRootNode, firstItem);
            parent = bogusRootNode;
        }

        AgendaItemInstanceChildAccessor accessorToSelectedNode = getInstanceAccessorToChild(parent, node.getId());
        AgendaItemBo olderSibling = (accessorToSelectedNode.getInstance() == parent) ? null : accessorToSelectedNode.getInstance();

        if (olderSibling != null) {
            accessorToSelectedNode.setChild(node.getAlways());
            AgendaItemInstanceChildAccessor yougestWhenFalseSiblingInsertionPoint =
                    getLastChildsAlwaysAccessor(new AgendaItemInstanceChildAccessor(AgendaItemChildAccessor.whenFalse, olderSibling));
            yougestWhenFalseSiblingInsertionPoint.setChild(node);
            AgendaItemChildAccessor.always.setChild(node, null);
        } else if (node.getAlways() != null) { // has younger sibling
            accessorToSelectedNode.setChild(node.getAlways());
            AgendaItemBo childsWhenTrue = node.getAlways().getWhenTrue();
            AgendaItemChildAccessor.whenTrue.setChild(node.getAlways(), node);
            AgendaItemChildAccessor.always.setChild(node, childsWhenTrue);
        }

        if (bogusRootNode != null) {
            // clean up special case with bogus root node
            agendaEditor.getAgenda().setFirstItemId(bogusRootNode.getWhenFalseId());
        }
    }

    /**
     *
     * @param cousin1
     * @param cousin2
     * @return
     * @see AgendaItemChildAccessor for nomenclature explanation
     */
    private boolean isSiblings(AgendaItemBo cousin1, AgendaItemBo cousin2) {
        if (cousin1.equals(cousin2)) return true; // this is a bit abusive
        
        // can you walk to c1 from ALWAYS links of c2?
        AgendaItemBo candidate = cousin2;
        while (null != (candidate = candidate.getAlways())) {
            if (candidate.equals(cousin1)) return true;
        }
        // can you walk to c2 from ALWAYS links of c1?
        candidate = cousin1;
        while (null != (candidate = candidate.getAlways())) {
            if (candidate.equals(cousin2)) return true;
        }
        return false;
    }

    /**
     * This method returns the level order accessor (getWhenTrue or getWhenFalse) that relates the parent directly 
     * to the child.  If the two nodes don't have such a relationship, null is returned. 
     * Note that this only finds accessors for oldest children, not younger siblings.
     * @see AgendaItemChildAccessor for nomenclature explanation
     */
    private AgendaItemChildAccessor getOldestChildAccessor(
            AgendaItemBo child, AgendaItemBo parent) {
        AgendaItemChildAccessor levelOrderChildAccessor = null;
        
        if (parent != null) {
            for (AgendaItemChildAccessor childAccessor : AgendaItemChildAccessor.children) {
                if (child.equals(childAccessor.getChild(parent))) {
                    levelOrderChildAccessor = childAccessor;
                    break;
                }
            }
        }
        return levelOrderChildAccessor;
    }
    
    /**
     * This method finds and returns the first agenda item in the agenda, or null if there are no items presently
     * 
     * @param agenda
     * @return
     */
    private AgendaItemBo getFirstAgendaItem(AgendaBo agenda) {
        AgendaItemBo firstItem = null;
        if (agenda != null && agenda.getItems() != null) for (AgendaItemBo agendaItem : agenda.getItems()) {
            if (agenda.getFirstItemId().equals(agendaItem.getId())) {
                firstItem = agendaItem;
                break;
            }
        }
        return firstItem;
    }
    
    /**
     * @return the closest younger sibling of the agenda item with the given ID, and if there is no such sibling, the closest younger cousin.
     * If there is no such cousin either, then null is returned.
     * @see AgendaItemChildAccessor for nomenclature explanation
     */
    private AgendaItemBo getNextYoungestOfSameGeneration(AgendaItemBo root, AgendaItemBo agendaItem) {

        int genNumber = getAgendaItemGenerationNumber(0, root, agendaItem.getId());
        List<AgendaItemBo> genList = new ArrayList<AgendaItemBo>();
        buildAgendaItemGenerationList(genList, root, 0, genNumber);

        int itemIndex = genList.indexOf(agendaItem);
        if (genList.size() > itemIndex + 1) return genList.get(itemIndex + 1);

        return null;
    }

    /**
     *
     * @param currentLevel
     * @param node
     * @param agendaItemId
     * @return
     * @see AgendaItemChildAccessor for nomenclature explanation
     */
    private int getAgendaItemGenerationNumber(int currentLevel, AgendaItemBo node, String agendaItemId) {
        int result = -1;
        if (agendaItemId.equals(node.getId())) {
            result = currentLevel;
        } else {
            for (AgendaItemChildAccessor childAccessor : AgendaItemChildAccessor.linkedNodes) {
                AgendaItemBo child = childAccessor.getChild(node);
                if (child != null) {
                    int nextLevel = currentLevel;
                    // we don't change the level order parent when we traverse ALWAYS links
                    if (childAccessor != AgendaItemChildAccessor.always) {
                        nextLevel = currentLevel +1;
                    }
                    result = getAgendaItemGenerationNumber(nextLevel, child, agendaItemId);
                    if (result != -1) break;
                }
            }
        }
        return result;
    }

    /**
     *
     * @param genList
     * @param node
     * @param currentLevel
     * @param generation
     * @see AgendaItemChildAccessor for nomenclature explanation
     */
    private void buildAgendaItemGenerationList(List<AgendaItemBo> genList, AgendaItemBo node, int currentLevel, int generation) {
        if (currentLevel == generation) {
            genList.add(node);
        }

        if (currentLevel > generation) return;

        for (AgendaItemChildAccessor childAccessor : AgendaItemChildAccessor.linkedNodes) {
            AgendaItemBo child = childAccessor.getChild(node);
            if (child != null) {
                int nextLevel = currentLevel;
                // we don't change the level order parent when we traverse ALWAYS links
                if (childAccessor != AgendaItemChildAccessor.always) {
                    nextLevel = currentLevel +1;
                }
                buildAgendaItemGenerationList(genList, child, nextLevel, generation);
            }
        }
    }

    /**
     * @return the closest older sibling of the agenda item with the given ID, and if there is no such sibling, the closest older cousin.
     * If there is no such cousin either, then null is returned.
     * @see AgendaItemChildAccessor for nomenclature explanation
     */
    private AgendaItemBo getNextOldestOfSameGeneration(AgendaItemBo root, AgendaItemBo agendaItem) {

        int genNumber = getAgendaItemGenerationNumber(0, root, agendaItem.getId());
        List<AgendaItemBo> genList = new ArrayList<AgendaItemBo>();
        buildAgendaItemGenerationList(genList, root, 0, genNumber);

        int itemIndex = genList.indexOf(agendaItem);
        if (itemIndex >= 1) return genList.get(itemIndex - 1);

        return null;
    }
    

    /**
     * returns the parent of the item with the passed in id.  Note that {@link AgendaItemBo}s related by ALWAYS relationships are considered siblings.
     * @see AgendaItemChildAccessor for nomenclature explanation
     */
    private AgendaItemBo getParent(AgendaItemBo root, String agendaItemId) {
        return getParentHelper(root, null, agendaItemId);
    }

    /**
     *
     * @param node
     * @param levelOrderParent
     * @param agendaItemId
     * @return
     * @see AgendaItemChildAccessor for nomenclature explanation
     */
    private AgendaItemBo getParentHelper(AgendaItemBo node, AgendaItemBo levelOrderParent, String agendaItemId) {
        AgendaItemBo result = null;
        if (agendaItemId.equals(node.getId())) {
            result = levelOrderParent;
        } else {
            for (AgendaItemChildAccessor childAccessor : AgendaItemChildAccessor.linkedNodes) {
                AgendaItemBo child = childAccessor.getChild(node);
                if (child != null) {
                    // we don't change the level order parent when we traverse ALWAYS links 
                    AgendaItemBo lop = (childAccessor == AgendaItemChildAccessor.always) ? levelOrderParent : node;
                    result = getParentHelper(child, lop, agendaItemId);
                    if (result != null) break;
                }
            }
        }
        return result;
    }

    /**
     * Search the tree for the agenda item with the given id.
     */
    private AgendaItemBo getAgendaItemById(AgendaItemBo node, String agendaItemId) {
        if (node == null) throw new IllegalArgumentException("node must be non-null");

        AgendaItemBo result = null;
        
        if (agendaItemId.equals(node.getId())) {
            result = node;
        } else {
            for (AgendaItemChildAccessor childAccessor : AgendaItemChildAccessor.linkedNodes) {
                AgendaItemBo child = childAccessor.getChild(node);
                if (child != null) {
                    result = getAgendaItemById(child, agendaItemId);
                    if (result != null) break;
                }
            }
        } 
        return result;
    }

    /**
     * @param form
     * @return the {@link AgendaEditor} from the form
     */
    private AgendaEditor getAgendaEditor(UifFormBase form) {
        MaintenanceForm maintenanceForm = (MaintenanceForm) form;
        return ((AgendaEditor)maintenanceForm.getDocument().getDocumentDataObject());
    }

    private void treeToInOrderList(AgendaItemBo agendaItem, List<AgendaItemBo> listToBuild) {
        listToBuild.add(agendaItem);
        for (AgendaItemChildAccessor childAccessor : AgendaItemChildAccessor.linkedNodes) {
            AgendaItemBo child = childAccessor.getChild(agendaItem);
            if (child != null) treeToInOrderList(child, listToBuild);
        }
    }

    
    @RequestMapping(params = "methodToCall=" + "delete")
    public ModelAndView delete(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        deleteSelectedSubtree(form);

        return super.refresh(form, result, request, response);
    }

    @RequestMapping(params = "methodToCall=" + "ajaxDelete")
    public ModelAndView ajaxDelete(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        deleteSelectedSubtree(form);

        // call the super method to avoid the agenda tree being reloaded from the db
        return super.updateComponent(form, result, request, response);
    }

    
    private void deleteSelectedSubtree(UifFormBase form) {
        AgendaEditor agendaEditor = getAgendaEditor(form);
        AgendaItemBo firstItem = getFirstAgendaItem(agendaEditor.getAgenda());
        String agendaItemSelected = agendaEditor.getSelectedAgendaItemId();
        AgendaItemBo selectedItem = getAgendaItemById(firstItem, agendaItemSelected);

        if (firstItem != null) {
            // need to handle the first item here, our recursive method won't handle it.
            if (agendaItemSelected.equals(firstItem.getId())) {
                agendaEditor.getAgenda().setFirstItemId(firstItem.getAlwaysId());
            } else {
                deleteAgendaItem(firstItem, agendaItemSelected);
            }

            // remove agenda item and its whenTrue & whenFalse children from the list of agendaItems of the agenda
            if (selectedItem.getWhenTrue() != null) {
                removeAgendaItem(agendaEditor.getAgenda().getItems(), selectedItem.getWhenTrue());
            }
            if (selectedItem.getWhenFalse() != null) {
                removeAgendaItem(agendaEditor.getAgenda().getItems(), selectedItem.getWhenFalse());
            }
            agendaEditor.getAgenda().getItems().remove(selectedItem);
        }
    }

    private void deleteAgendaItem(AgendaItemBo root, String agendaItemIdToDelete) {
        if (deleteAgendaItem(root, AgendaItemChildAccessor.whenTrue, agendaItemIdToDelete) || 
                deleteAgendaItem(root, AgendaItemChildAccessor.whenFalse, agendaItemIdToDelete) || 
                deleteAgendaItem(root, AgendaItemChildAccessor.always, agendaItemIdToDelete)); // TODO: this is confusing, refactor
    }
    
    private boolean deleteAgendaItem(AgendaItemBo agendaItem, AgendaItemChildAccessor childAccessor, String agendaItemIdToDelete) {
        if (agendaItem == null || childAccessor.getChild(agendaItem) == null) return false;
        if (agendaItemIdToDelete.equals(childAccessor.getChild(agendaItem).getId())) {
            // delete the child in such a way that any ALWAYS children don't get lost from the tree
            AgendaItemBo grandchildToKeep = childAccessor.getChild(agendaItem).getAlways();
            childAccessor.setChild(agendaItem, grandchildToKeep);
            return true;
        } else {
            AgendaItemBo child = childAccessor.getChild(agendaItem);
            // recurse
            for (AgendaItemChildAccessor nextChildAccessor : AgendaItemChildAccessor.linkedNodes) {
                if (deleteAgendaItem(child, nextChildAccessor, agendaItemIdToDelete)) return true;
            }
        }
        return false;
    }

    /**
     * Recursively delete the agendaItem and its children from the agendaItemBo list.
     * @param items, the list of agendaItemBo that the agenda holds
     * @param removeAgendaItem, the agendaItemBo to be removed
     */
    private void removeAgendaItem(List<AgendaItemBo> items, AgendaItemBo removeAgendaItem) {
        if (removeAgendaItem.getWhenTrue() != null) {
            removeAgendaItem(items, removeAgendaItem.getWhenTrue());
        }
        if (removeAgendaItem.getWhenFalse() != null) {
            removeAgendaItem(items, removeAgendaItem.getWhenFalse());
        }
        if (removeAgendaItem.getAlways() != null) {
            removeAgendaItem(items, removeAgendaItem.getAlways());
        }
        items.remove(removeAgendaItem);
    }

    @RequestMapping(params = "methodToCall=" + "ajaxCut")
    public ModelAndView ajaxCut(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        AgendaEditor agendaEditor = getAgendaEditor(form);
        // this is the root of the tree:
        AgendaItemBo firstItem = getFirstAgendaItem(agendaEditor.getAgenda());
        String selectedItemId = agendaEditor.getSelectedAgendaItemId();

        setCutAgendaItemId(form, selectedItemId);

        // call the super method to avoid the agenda tree being reloaded from the db
        return super.updateComponent(form, result, request, response);
    }

    @RequestMapping(params = "methodToCall=" + "ajaxPaste")
    public ModelAndView ajaxPaste(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        AgendaEditor agendaEditor = getAgendaEditor(form);
        // this is the root of the tree:
        AgendaItemBo firstItem = getFirstAgendaItem(agendaEditor.getAgenda());
        String selectedItemId = agendaEditor.getSelectedAgendaItemId();

        String agendaItemId = getCutAgendaItemId(form);

        if (StringUtils.isNotBlank(selectedItemId) && StringUtils.isNotBlank(agendaItemId)) {
            AgendaItemBo node = getAgendaItemById(firstItem, agendaItemId);
            AgendaItemBo orgRefNode = getReferringNode(firstItem, agendaItemId);
            AgendaItemBo newRefNode = getAgendaItemById(firstItem, selectedItemId);

            if (isSameOrChildNode(node, newRefNode)) {
                // do nothing; can't paste to itself
            } else {
                // remove node
                if (orgRefNode == null) {
                    agendaEditor.getAgenda().setFirstItemId(node.getAlwaysId());
                } else {
                    // determine if true, false or always
                    // do appropriate operation
                    if (node.getId().equals(orgRefNode.getWhenTrueId())) {
                        orgRefNode.setWhenTrueId(node.getAlwaysId());
                        orgRefNode.setWhenTrue(node.getAlways());
                    } else if(node.getId().equals(orgRefNode.getWhenFalseId())) {
                        orgRefNode.setWhenFalseId(node.getAlwaysId());
                        orgRefNode.setWhenFalse(node.getAlways());
                    } else {
                        orgRefNode.setAlwaysId(node.getAlwaysId());
                        orgRefNode.setAlways(node.getAlways());
                    }
                }

                // insert node
                node.setAlwaysId(newRefNode.getAlwaysId());
                node.setAlways(newRefNode.getAlways());
                newRefNode.setAlwaysId(node.getId());
                newRefNode.setAlways(node);
            }
        }

        setCutAgendaItemId(form, null);

        // call the super method to avoid the agenda tree being reloaded from the db
        return super.updateComponent(form, result, request, response);
    }

    /**
     * Updates to the category call back to this method to set the categoryId appropriately
     * TODO: shouldn't this happen automatically?  We're taking it out of the form by hand here
     */
    @RequestMapping(params = "methodToCall=" + "ajaxCategoryChangeRefresh")
    public ModelAndView ajaxCategoryChangeRefresh(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String categoryParamName = null;
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement().toString();
            if (paramName.endsWith("categoryId")) {
                categoryParamName = paramName;
                break;
            }
        }

        if (categoryParamName != null) {
            String categoryId = request.getParameter(categoryParamName);

            if (StringUtils.isBlank(categoryId)) { categoryId = null; }

            AgendaEditor agendaEditor = getAgendaEditor(form);
            RuleBo rule = agendaEditor.getAgendaItemLine().getRule();
            String selectedPropId = agendaEditor.getSelectedPropositionId();

            // TODO: This should work even if the prop isn't selected!!!  Find the node in edit mode?
            if (!StringUtils.isBlank(selectedPropId)) {
                Node<RuleTreeNode, String> selectedPropositionNode =
                        findPropositionTreeNode(rule.getPropositionTree().getRootElement(), selectedPropId);
                selectedPropositionNode.getData().getProposition().setCategoryId(categoryId);
            }
        }

        return ajaxRefresh(form, result, request, response);
    }

    /**
     * This method checks if the node is the same as the new parent node or a when-true/when-fase
     * child of the new parent node.
     *
     * @param node - the node to be checked if it's the same or a child
     * @param newParent - the parent node to check against
     * @return true if same or child, false otherwise
     * @see AgendaItemChildAccessor for nomenclature explanation
     */
    private boolean isSameOrChildNode(AgendaItemBo node, AgendaItemBo newParent) {
        return isSameOrChildNodeHelper(node, newParent, AgendaItemChildAccessor.children);
    }

    private boolean isSameOrChildNodeHelper(AgendaItemBo node, AgendaItemBo newParent, AgendaItemChildAccessor[] childAccessors) {
        boolean result = false;
        if (StringUtils.equals(node.getId(), newParent.getId())) {
            result = true;
        } else {
            for (AgendaItemChildAccessor childAccessor : childAccessors) {
                AgendaItemBo child = childAccessor.getChild(node);
                if (child != null) {
                    result = isSameOrChildNodeHelper(child, newParent, AgendaItemChildAccessor.linkedNodes);
                    if (result == true) break;
                }
            }
        }
        return result;
    }

    /**
     * This method returns the node that points to the specified agendaItemId.
     * (returns the next older sibling or the parent if no older sibling exists)
     *
     * @param root - the first agenda item of the agenda
     * @param agendaItemId - agenda item id of the agenda item whose referring node is to be returned
     * @return AgendaItemBo that points to the specified agenda item
     * @see AgendaItemChildAccessor for nomenclature explanation
     */
    private AgendaItemBo getReferringNode(AgendaItemBo root, String agendaItemId) {
        return getReferringNodeHelper(root, null, agendaItemId);
    }

    private AgendaItemBo getReferringNodeHelper(AgendaItemBo node, AgendaItemBo referringNode, String agendaItemId) {
        AgendaItemBo result = null;
        if (agendaItemId.equals(node.getId())) {
            result = referringNode;
        } else {
            for (AgendaItemChildAccessor childAccessor : AgendaItemChildAccessor.linkedNodes) {
                AgendaItemBo child = childAccessor.getChild(node);
                if (child != null) {
                    result = getReferringNodeHelper(child, node, agendaItemId);
                    if (result != null) break;
                }
            }
        }
        return result;
    }

    /**
     *  return the sequenceAssessorService
     */
    private SequenceAccessorService getSequenceAccessorService() {
        if ( sequenceAccessorService == null ) {
            sequenceAccessorService = KRADServiceLocator.getSequenceAccessorService();
        }
        return sequenceAccessorService;
    }

    /**
     * return the contextBoService
     */
    private ContextBoService getContextBoService() {
        return KrmsRepositoryServiceLocator.getContextBoService();
    }

    /**
     * return the contextBoService
     */
    private RuleBoService getRuleBoService() {
        return KrmsRepositoryServiceLocator.getRuleBoService();
    }

    /**
     * binds a child accessor to an AgendaItemBo instance.  An {@link AgendaItemInstanceChildAccessor} allows you to
     * get and set the referent
     */
    private static class AgendaItemInstanceChildAccessor {
        
        private final AgendaItemChildAccessor accessor;
        private final AgendaItemBo instance;

        public AgendaItemInstanceChildAccessor(AgendaItemChildAccessor accessor, AgendaItemBo instance) {
            this.accessor = accessor;
            this.instance = instance;
        }
        
        public void setChild(AgendaItemBo child) {
            accessor.setChild(instance, child);
        }
        
        public AgendaItemBo getChild() {
            return accessor.getChild(instance);
        }
        
        public AgendaItemBo getInstance() { return instance; }
    }
    
    /**
     * <p>This class abstracts getting and setting a child of an AgendaItemBo, making some recursive operations
     * require less boiler plate.</p>
     *
     * <p>The word 'child' in AgendaItemChildAccessor means child in the strict data structures sense, in that the
     * instance passed in holds a reference to some other node (or null).  However, when discussing the agenda tree
     * and algorithms for manipulating it, the meaning of 'child' is somewhat different, and there are notions of
     * 'sibling' and 'cousin' that are tossed about too. It's probably worth explaining that somewhat here:</p>
     *
     * <p>General principals of relationships when talking about the agenda tree:
     * <ul>
     * <li>Generation boundaries (parent to child) are across 'When TRUE' and 'When FALSE' references.</li>
     * <li>"Age" among siblings & cousins goes from top (oldest) to bottom (youngest).</li>
     * <li>siblings are related by 'Always' references.</li>
     * </ul>
     * </p>
     * <p>This diagram of an agenda tree and the following examples seek to illustrate these principals:</p>
     * <img src="doc-files/AgendaEditorController-1.png" alt="Example Agenda Items"/>
     * <p>Examples:
     * <ul>
     * <li>A is the parent of B, C, & D</li>
     * <li>E is the younger sibling of A</li>
     * <li>B is the older cousin of C</li>
     * <li>C is the older sibling of D</li>
     * <li>F is the younger cousin of D</li>
     * </ul>
     * </p>
     */
    protected static class AgendaItemChildAccessor {
        
        private enum Child { WHEN_TRUE, WHEN_FALSE, ALWAYS };
        
        private static final AgendaItemChildAccessor whenTrue = new AgendaItemChildAccessor(Child.WHEN_TRUE); 
        private static final AgendaItemChildAccessor whenFalse = new AgendaItemChildAccessor(Child.WHEN_FALSE); 
        private static final AgendaItemChildAccessor always = new AgendaItemChildAccessor(Child.ALWAYS); 

        /**
         * Accessors for all linked items
         */
        private static final AgendaItemChildAccessor [] linkedNodes = { whenTrue, whenFalse, always };
        
        /**
         * Accessors for children (so ALWAYS is omitted);
         */
        private static final AgendaItemChildAccessor [] children = { whenTrue, whenFalse };
        
        private final Child whichChild;
        
        private AgendaItemChildAccessor(Child whichChild) {
            if (whichChild == null) throw new IllegalArgumentException("whichChild must be non-null");
            this.whichChild = whichChild;
        }
        
        /**
         * @return the referenced child
         */
        public AgendaItemBo getChild(AgendaItemBo parent) {
            switch (whichChild) {
            case WHEN_TRUE: return parent.getWhenTrue();
            case WHEN_FALSE: return parent.getWhenFalse();
            case ALWAYS: return parent.getAlways();
            default: throw new IllegalStateException();
            }
        }
        
        /**
         * Sets the child reference and the child id 
         */
        public void setChild(AgendaItemBo parent, AgendaItemBo child) {
            switch (whichChild) {
            case WHEN_TRUE: 
                parent.setWhenTrue(child);
                parent.setWhenTrueId(child == null ? null : child.getId());
                break;
            case WHEN_FALSE:
                parent.setWhenFalse(child);
                parent.setWhenFalseId(child == null ? null : child.getId());
                break;
            case ALWAYS:
                parent.setAlways(child);
                parent.setAlwaysId(child == null ? null : child.getId());
                break;
            default: throw new IllegalStateException();
            }
        }
    }
    //
    // Rule Editor Controller methods
    //
    @RequestMapping(params = "methodToCall=" + "copyRule")
    public ModelAndView copyRule(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        AgendaEditor agendaEditor = getAgendaEditor(form);
        String name = agendaEditor.getCopyRuleName();
        String namespace = agendaEditor.getNamespace();
        // fetch existing rule and copy fields to new rule
        RuleDefinition oldRuleDefinition = getRuleBoService().getRuleByNameAndNamespace(name, namespace);
        RuleBo oldRule = RuleBo.from(oldRuleDefinition);
        RuleBo newRule = RuleBo.copyRule(oldRule);
        agendaEditor.getAgendaItemLine().setRule( newRule );
        // hack to set ui action object to first action in the list
        if (!newRule.getActions().isEmpty()) {
            agendaEditor.setAgendaItemLineRuleAction( newRule.getActions().get(0));
        }
        return super.refresh(form, result, request, response);
    }


    /**
     * This method starts an edit proposition.
     */
    @RequestMapping(params = "methodToCall=" + "goToEditProposition")
    public ModelAndView goToEditProposition(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        // open the selected node for editing
        AgendaEditor agendaEditor = getAgendaEditor(form);
        RuleBo rule = agendaEditor.getAgendaItemLine().getRule();
        String selectedPropId = agendaEditor.getSelectedPropositionId();

        Node<RuleTreeNode,String> root = rule.getPropositionTree().getRootElement();
        PropositionBo propositionToToggleEdit = null;
        boolean newEditMode = true;

        // find parent
        Node<RuleTreeNode,String> parent = findParentPropositionNode( root, selectedPropId);
        if (parent != null){
            List<Node<RuleTreeNode,String>> children = parent.getChildren();
            for( int index=0; index< children.size(); index++){
                Node<RuleTreeNode,String> child = children.get(index);
                if (propIdMatches(child, selectedPropId)){
                    PropositionBo prop = child.getData().getProposition();
                    propositionToToggleEdit = prop;
                    newEditMode =  !prop.getEditMode();

                    // if compound node, set all children into same edit mode
//                    if (PropositionType.COMPOUND.getCode().equalsIgnoreCase(prop.getPropositionTypeCode())){
//                       for ( PropositionBo compoundComponent : prop.getCompoundComponents() ){
//                           compoundComponent.setEditMode(editMode);
//                       }
//                    }

                    //refresh the tree
                    rule.refreshPropositionTree(null);
                    break;
                } else {
                    child.getData().getProposition().setEditMode(false);
                }
            }
        }

        resetEditModeOnPropositionTree(root);
        if (propositionToToggleEdit != null) {
            propositionToToggleEdit.setEditMode(newEditMode);
        }

        return super.updateComponent(form, result, request, response);
    }

    @RequestMapping(params = "methodToCall=" + "addProposition")
    public ModelAndView addProposition(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        AgendaEditor agendaEditor = getAgendaEditor(form);
        RuleBo rule = agendaEditor.getAgendaItemLine().getRule();
        String selectedPropId = agendaEditor.getSelectedPropositionId();


        // find parent
        Node<RuleTreeNode,String> root = agendaEditor.getAgendaItemLine().getRule().getPropositionTree().getRootElement();
        Node<RuleTreeNode,String> parent = findParentPropositionNode( root, selectedPropId);

        resetEditModeOnPropositionTree(root);

        // add new child at appropriate spot
        if (parent != null){
            List<Node<RuleTreeNode,String>> children = parent.getChildren();
            for( int index=0; index< children.size(); index++){
                Node<RuleTreeNode,String> child = children.get(index);

                // if our selected node is a simple proposition, add a new one after
                if (propIdMatches(child, selectedPropId)){
                    // handle special case of adding to a lone simple proposition.
                    // in this case, we need to change the root level proposition to a compound proposition
                    // move the existing simple proposition as the first compound component,
                    // then add a new blank simple prop as the second compound component.
                    if (parent == root &&
                        (SimplePropositionNode.NODE_TYPE.equalsIgnoreCase(child.getNodeType()) ||
                        SimplePropositionEditNode.NODE_TYPE.equalsIgnoreCase(child.getNodeType()))){

                        // create a new compound proposition
                        PropositionBo compound = PropositionBo.createCompoundPropositionBoStub(child.getData().getProposition(), true);
                        compound.setEditMode(true);
                        rule.setProposition(compound);
                        rule.refreshPropositionTree(null);
                    }
                    // handle regular case of adding a simple prop to an existing compound prop
                    else if(SimplePropositionNode.NODE_TYPE.equalsIgnoreCase(child.getNodeType()) ||
                       SimplePropositionEditNode.NODE_TYPE.equalsIgnoreCase(child.getNodeType())){

                        // build new Blank Proposition
                        PropositionBo blank = PropositionBo.createSimplePropositionBoStub(child.getData().getProposition(),PropositionType.SIMPLE.getCode());
                        //add it to the parent
                        PropositionBo parentProp = parent.getData().getProposition();
                        parentProp.getCompoundComponents().add(((index/2)+1), blank);

                        rule.refreshPropositionTree(true);
                    }

                    break;
                }
            }
        } else {
            // special case, if root has no children, add a new simple proposition
            // todo: how to add compound proposition. - just add another to the firs simple
            if (root.getChildren().isEmpty()){
                PropositionBo blank = PropositionBo.createSimplePropositionBoStub(null,PropositionType.SIMPLE.getCode());
                blank.setRuleId(rule.getId());
                blank.setTypeId(rule.getTypeId());  // ?? bug
                rule.setPropId(blank.getId());
                rule.setProposition(blank);
                rule.refreshPropositionTree(true);
            }
        }
        return super.updateComponent(form, result, request, response);
    }

    /**
     *
     * This method adds an opCode Node to separate components in a compound proposition.
     *
     * @param currentNode
     * @param prop
     * @return
     */
    private void addOpCodeNode(Node currentNode, PropositionBo prop, int index){
        String opCodeLabel = "";

        if (LogicalOperator.AND.getCode().equalsIgnoreCase(prop.getCompoundOpCode())){
            opCodeLabel = "AND";
        } else if (LogicalOperator.OR.getCode().equalsIgnoreCase(prop.getCompoundOpCode())){
            opCodeLabel = "OR";
        }
        Node<RuleTreeNode, String> aNode = new Node<RuleTreeNode, String>();
        aNode.setNodeLabel("");
        aNode.setNodeType("ruleTreeNode compoundOpCodeNode");
        aNode.setData(new CompoundOpCodeNode(prop));
        currentNode.insertChildAt(index, aNode);
    }


    private boolean propIdMatches(Node<RuleTreeNode, String> node, String propId){
        if (propId!=null && node != null && node.getData() != null && propId.equalsIgnoreCase(node.getData().getProposition().getId())) {
            return true;
        }
        return false;
    }

    /**
     * disable edit mode for all Nodes beneath and including the passed in Node
     * @param currentNode
     */
    private void resetEditModeOnPropositionTree(Node<RuleTreeNode, String> currentNode){
        if (currentNode.getData() != null){
            RuleTreeNode dataNode = currentNode.getData();
            dataNode.getProposition().setEditMode(false);
        }
        List<Node<RuleTreeNode,String>> children = currentNode.getChildren();
        for( Node<RuleTreeNode,String> child : children){
              resetEditModeOnPropositionTree(child);
        }
    }

    private Node<RuleTreeNode, String> findPropositionTreeNode(Node<RuleTreeNode, String> currentNode, String selectedPropId){
        Node<RuleTreeNode,String> bingo = null;
        if (currentNode.getData() != null){
            RuleTreeNode dataNode = currentNode.getData();
            if (selectedPropId.equalsIgnoreCase(dataNode.getProposition().getId())){
                return currentNode;
            }
        }
        List<Node<RuleTreeNode,String>> children = currentNode.getChildren();
        for( Node<RuleTreeNode,String> child : children){
              bingo = findPropositionTreeNode(child, selectedPropId);
              if (bingo != null) break;
        }
        return bingo;
    }

    private Node<RuleTreeNode, String> findParentPropositionNode(Node<RuleTreeNode, String> currentNode, String selectedPropId){
        Node<RuleTreeNode,String> bingo = null;
        if (selectedPropId != null) {
            // if it's in children, we have the parent
            List<Node<RuleTreeNode,String>> children = currentNode.getChildren();
            for( Node<RuleTreeNode,String> child : children){
                RuleTreeNode dataNode = child.getData();
                if (selectedPropId.equalsIgnoreCase(dataNode.getProposition().getId()))
                    return currentNode;
            }

            // if not found check grandchildren
            for( Node<RuleTreeNode,String> kid : children){
                  bingo = findParentPropositionNode(kid, selectedPropId);
                  if (bingo != null) break;
            }
        }
        return bingo;
    }

    /**
     * This method return the index of the position of the child that matches the id
     * @param parent
     * @param propId
     * @return index if found, -1 if not found
     */
    private int findChildIndex(Node<RuleTreeNode,String> parent, String propId){
        int index;
        List<Node<RuleTreeNode,String>> children = parent.getChildren();
        for(index=0; index< children.size(); index++){
            Node<RuleTreeNode,String> child = children.get(index);
            // if our selected node is a simple proposition, add a new one after
            if (propIdMatches(child, propId)){
                return index;
            }
        }
        return -1;
    }

    @RequestMapping(params = "methodToCall=" + "movePropositionUp")
    public ModelAndView movePropositionUp(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        moveSelectedProposition(form, true);

        return super.updateComponent(form, result, request, response);
    }

    @RequestMapping(params = "methodToCall=" + "movePropositionDown")
    public ModelAndView movePropositionDown(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        moveSelectedProposition(form, false);

        return super.updateComponent(form, result, request, response);
    }

    private void moveSelectedProposition(UifFormBase form, boolean up) {

        /* Rough algorithm for moving a node up.
         *
         * find the following:
         *   node := the selected node
         *   parent := the selected node's parent, its containing node (via when true or when false relationship)
         *   parentsOlderCousin := the parent's level-order predecessor (sibling or cousin)
         *
         */
        AgendaEditor agendaEditor = getAgendaEditor(form);
        RuleBo rule = agendaEditor.getAgendaItemLine().getRule();
        String selectedPropId = agendaEditor.getSelectedPropositionId();

        // find parent
        Node<RuleTreeNode,String> parent = findParentPropositionNode(rule.getPropositionTree().getRootElement(), selectedPropId);

        // add new child at appropriate spot
        if (parent != null){
            List<Node<RuleTreeNode,String>> children = parent.getChildren();
            for( int index=0; index< children.size(); index++){
                Node<RuleTreeNode,String> child = children.get(index);
                // if our selected node is a simple proposition, add a new one after
                if (propIdMatches(child, selectedPropId)){
                    if(SimplePropositionNode.NODE_TYPE.equalsIgnoreCase(child.getNodeType()) ||
                       SimplePropositionEditNode.NODE_TYPE.equalsIgnoreCase(child.getNodeType()) ||
                       RuleTreeNode.COMPOUND_NODE_TYPE.equalsIgnoreCase(child.getNodeType()) ){

                        if (((index > 0) && up) || ((index <(children.size() - 1)&& !up))){
                            //remove it from its current spot
                            PropositionBo parentProp = parent.getData().getProposition();
                            PropositionBo workingProp = parentProp.getCompoundComponents().remove(index/2);
                            if (up){
                                parentProp.getCompoundComponents().add((index/2)-1, workingProp);
                            }else{
                                parentProp.getCompoundComponents().add((index/2)+1, workingProp);
                            }

                            // insert it in the new spot
                            // redisplay the tree (editMode = true)
                            boolean editMode = (SimplePropositionEditNode.NODE_TYPE.equalsIgnoreCase(child.getNodeType()));
                            rule.refreshPropositionTree(editMode);
                        }
                    }

                    break;
                }
            }
        }
    }
    @RequestMapping(params = "methodToCall=" + "movePropositionLeft")
    public ModelAndView movePropositionLeft(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        /* Rough algorithm for moving a node up.
         *
         * find the following:
         *   node := the selected node
         *   parent := the selected node's parent, its containing node (via when true or when false relationship)
         *   parentsOlderCousin := the parent's level-order predecessor (sibling or cousin)
         *
         */
        AgendaEditor agendaEditor = getAgendaEditor(form);
        RuleBo rule = agendaEditor.getAgendaItemLine().getRule();
        String selectedPropId = agendaEditor.getSelectedPropositionId();

        // find agendaEditor.getAgendaItemLine().getRule().getPropositionTree().getRootElement()parent
        Node<RuleTreeNode,String> root = rule.getPropositionTree().getRootElement();
        Node<RuleTreeNode,String> parent = findParentPropositionNode(root, selectedPropId);
        if ((parent != null) && (RuleTreeNode.COMPOUND_NODE_TYPE.equalsIgnoreCase(parent.getNodeType()))){
            Node<RuleTreeNode,String> granny = findParentPropositionNode(root,parent.getData().getProposition().getId());
            if (granny != root){
                int oldIndex = findChildIndex(parent, selectedPropId);
                int newIndex = findChildIndex(granny, parent.getData().getProposition().getId());
                if (oldIndex >= 0 && newIndex >= 0){
                    PropositionBo prop = parent.getData().getProposition().getCompoundComponents().remove(oldIndex/2);
                    granny.getData().getProposition().getCompoundComponents().add((newIndex/2)+1, prop);
                    rule.refreshPropositionTree(false);
                }
            } else {
                // TODO: do we allow moving up to the root?
                // we could add a new top level compound node, with current root as 1st child,
                // and move the node to the second child.
            }
        }
        return super.updateComponent(form, result, request, response);
    }

    @RequestMapping(params = "methodToCall=" + "movePropositionRight")
    public ModelAndView movePropositionRight(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        /* Rough algorithm for moving a node Right
         * if the selected node is above a compound proposition, move it into the compound proposition as the first child
         * if the node is above a simple proposition, do nothing.
         * find the following:
         *   node := the selected node
         *   parent := the selected node's parent, its containing node
         *   nextSibling := the node after the selected node
         *
         */
        AgendaEditor agendaEditor = getAgendaEditor(form);
        RuleBo rule = agendaEditor.getAgendaItemLine().getRule();
        String selectedPropId = agendaEditor.getSelectedPropositionId();

        // find parent
        Node<RuleTreeNode,String> parent = findParentPropositionNode(
                rule.getPropositionTree().getRootElement(), selectedPropId);
        if (parent != null){
            int index = findChildIndex(parent, selectedPropId);
            // if we are the last child, do nothing, otherwise
            if (index >= 0 && index+1 < parent.getChildren().size()){
                Node<RuleTreeNode,String> child = parent.getChildren().get(index);
                Node<RuleTreeNode,String> nextSibling = parent.getChildren().get(index+2);
                // if selected node above a compound node, move it into it as first child
                if(RuleTreeNode.COMPOUND_NODE_TYPE.equalsIgnoreCase(nextSibling.getNodeType()) ){
                    // remove selected node from it's current spot
                    PropositionBo prop = parent.getData().getProposition().getCompoundComponents().remove(index/2);
                    // add it to it's siblings children
                    nextSibling.getData().getProposition().getCompoundComponents().add(0, prop);
                    rule.refreshPropositionTree(false);
                }
            }
        }
        return super.updateComponent(form, result, request, response);
    }

    /**
     * introduces a new compound proposition between the selected proposition and its parent.
     * Additionally, it puts a new blank simple proposition underneath the compound proposition
     * as a sibling to the selected proposition.
     */
    @RequestMapping(params = "methodToCall=" + "togglePropositionSimpleCompound")
    public ModelAndView togglePropositionSimpleCompound(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        AgendaEditor agendaEditor = getAgendaEditor(form);
        RuleBo rule = agendaEditor.getAgendaItemLine().getRule();
        String selectedPropId = agendaEditor.getSelectedPropositionId();

        resetEditModeOnPropositionTree(rule.getPropositionTree().getRootElement());

        if (!StringUtils.isBlank(selectedPropId)) {
            // find parent
            Node<RuleTreeNode,String> parent = findParentPropositionNode(
                    rule.getPropositionTree().getRootElement(), selectedPropId);
            if (parent != null){

                int index = findChildIndex(parent, selectedPropId);

                PropositionBo propBo = parent.getChildren().get(index).getData().getProposition();

                // create a new compound proposition
                PropositionBo compound = PropositionBo.createCompoundPropositionBoStub(propBo, true);
                compound.setDescription("New Compound Proposition " + UUID.randomUUID().toString());
                compound.setEditMode(false);

                if (parent.getData() == null) { // SPECIAL CASE: this is the only proposition in the tree
                    rule.setProposition(compound);
                } else {
                    PropositionBo parentBo = parent.getData().getProposition();
                    List<PropositionBo> siblings = parentBo.getCompoundComponents();

                    int propIndex = -1;
                    for (int i=0; i<siblings.size(); i++) {
                        if (propBo.getId().equals(siblings.get(i).getId())) {
                            propIndex = i;
                            break;
                        }
                    }

                    parentBo.getCompoundComponents().set(propIndex, compound);
                }
            }
        }

        agendaEditor.getAgendaItemLine().getRule().refreshPropositionTree(true);
        return super.updateComponent(form, result, request, response);
    }


    @RequestMapping(params = "methodToCall=" + "cutProposition")
    public ModelAndView cutProposition(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        AgendaEditor agendaEditor = getAgendaEditor(form);
        String selectedPropId = agendaEditor.getSelectedPropositionId();
        agendaEditor.setCutPropositionId(selectedPropId);

        return super.updateComponent(form, result, request, response);
    }

    @RequestMapping(params = "methodToCall=" + "pasteProposition")
    public ModelAndView pasteProposition(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        AgendaEditor agendaEditor = getAgendaEditor(form);
        RuleBo rule = agendaEditor.getAgendaItemLine().getRule();

        // get selected id
        String cutPropId = agendaEditor.getCutPropositionId();
        String selectedPropId = agendaEditor.getSelectedPropositionId();

        if (selectedPropId == cutPropId) {
                // do nothing; can't paste to itself
        } else {

            // proposition tree root
            Node<RuleTreeNode, String> root = rule.getPropositionTree().getRootElement();

            if (StringUtils.isNotBlank(selectedPropId) && StringUtils.isNotBlank(cutPropId)) {
                Node<RuleTreeNode,String> parentNode = findParentPropositionNode(root, selectedPropId);
                PropositionBo newParent;
                if (parentNode == root){
                    // special case
                    // build new top level compound proposition,
                    // add existing as first child
                    // then paste cut node as 2nd child
                    newParent = PropositionBo.createCompoundPropositionBoStub2(
                            root.getChildren().get(0).getData().getProposition());
                    newParent.setEditMode(true);
                    rule.setProposition(newParent);
                } else {
                    newParent = parentNode.getData().getProposition();
                }
                PropositionBo oldParent = findParentPropositionNode(root, cutPropId).getData().getProposition();

                PropositionBo workingProp = null;
                // cut from old
                if (oldParent != null){
                    List <PropositionBo> children = oldParent.getCompoundComponents();
                    for( int index=0; index< children.size(); index++){
                        if (cutPropId.equalsIgnoreCase(children.get(index).getId())){
                            workingProp = oldParent.getCompoundComponents().remove(index);
                            break;
                        }
                    }
                }

                // add to new
                if (newParent != null && workingProp != null){
                    List <PropositionBo> children = newParent.getCompoundComponents();
                    for( int index=0; index< children.size(); index++){
                        if (selectedPropId.equalsIgnoreCase(children.get(index).getId())){
                            children.add(index+1, workingProp);
                            break;
                        }
                    }
                }
                // TODO: determine edit mode.
//                boolean editMode = (SimplePropositionEditNode.NODE_TYPE.equalsIgnoreCase(child.getNodeType()));
                rule.refreshPropositionTree(false);
            }
        }
        agendaEditor.setCutPropositionId(null);
        // call the super method to avoid the agenda tree being reloaded from the db
        return super.updateComponent(form, result, request, response);
    }

    @RequestMapping(params = "methodToCall=" + "deleteProposition")
    public ModelAndView deleteProposition(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        AgendaEditor agendaEditor = getAgendaEditor(form);
        String selectedPropId = agendaEditor.getSelectedPropositionId();
        Node<RuleTreeNode, String> root = agendaEditor.getAgendaItemLine().getRule().getPropositionTree().getRootElement();

        PropositionBo parent = findParentPropositionNode(root, selectedPropId).getData().getProposition();
        if (parent != null){
            List <PropositionBo> children = parent.getCompoundComponents();
            for( int index=0; index< children.size(); index++){
                if (selectedPropId.equalsIgnoreCase(children.get(index).getId())){
                    parent.getCompoundComponents().remove(index);
                    break;
                }
            }
        }
        //TODO: handle edit mode
        agendaEditor.getAgendaItemLine().getRule().refreshPropositionTree(false);
        return super.updateComponent(form, result, request, response);
    }

    @RequestMapping(params = "methodToCall=" + "updateCompoundOperator")
    public ModelAndView updateCompoundOperator(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        AgendaEditor agendaEditor = getAgendaEditor(form);
        RuleBo rule = agendaEditor.getAgendaItemLine().getRule();
        rule.refreshPropositionTree(false);

        return super.updateComponent(form, result, request, response);
    }

}
