/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.impl.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.MapUtils;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.web.controller.MaintenanceDocumentController;
import org.kuali.rice.krad.web.form.MaintenanceForm;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.AgendaItemBo;
import org.kuali.rice.krms.impl.repository.ContextBo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the Test UI Page
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/krmsAgendaEditor")
public class AgendaEditorController extends MaintenanceDocumentController {

    private SequenceAccessorService sequenceAccessorService;

    @Override
    public MaintenanceForm createInitialForm(HttpServletRequest request) {
        return new MaintenanceForm();
    }
    
    /**
     * This overridden method does extra work on refresh to populate the context and agenda
     *
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#refresh(org.kuali.rice.krad.web.form.UifFormBase, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @RequestMapping(params = "methodToCall=" + "refresh")
    @Override
    public ModelAndView refresh(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        MapUtils.verbosePrint(System.out, "actionParameters", form.getActionParameters());
        MapUtils.verbosePrint(System.out, "requestParameters", request.getParameterMap());
        
        String agendaId = null;

        MaintenanceForm maintenanceForm = (MaintenanceForm) form;
        String conversionFields = maintenanceForm.getActionParameters().get("conversionFields");
        String refreshCaller = request.getParameter("refreshCaller");

        // handle return from agenda lookup
        // TODO: this condition is sloppy 
        if (refreshCaller != null && refreshCaller.contains("Agenda") && refreshCaller.contains("LookupView") &&
                conversionFields != null &&
                // TODO: this is sloppy
                conversionFields.contains("agenda.id")) {
            AgendaEditor editorDocument =
                    ((AgendaEditor) maintenanceForm.getDocument().getNewMaintainableObject().getDataObject());
            agendaId = editorDocument.getAgenda().getId();
            AgendaBo agenda = getBusinessObjectService().findBySinglePrimaryKey(AgendaBo.class, agendaId);
            editorDocument.setAgenda(agenda);

            if (agenda.getContextId() != null) {
                ContextBo context = getBusinessObjectService().findBySinglePrimaryKey(ContextBo.class, agenda.getContextId());
                editorDocument.setContext(context);
            }
        }
        
        return super.refresh(form, result, request, response);
    }

    /**
     * This override is used to populate the agenda from the agenda name and context selection of the user.
     * It is triggered by the refreshWhenChanged property of the MaintenanceView.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=updateComponent")
    @Override
    public ModelAndView updateComponent(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        MaintenanceForm maintenanceForm = (MaintenanceForm) form;
        AgendaEditor editorDocument =
                ((AgendaEditor) maintenanceForm.getDocument().getNewMaintainableObject().getDataObject());
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", editorDocument.getAgenda().getName());
        map.put("contextId", editorDocument.getContext().getId());

        AgendaBo agenda = getBusinessObjectService().findByPrimaryKey(AgendaBo.class, Collections.unmodifiableMap(map));
        editorDocument.setAgenda(agenda);

        return super.updateComponent(form, result, request, response);
    }

    /**
     * This method updates the existing rule in the agenda.
     */
    @RequestMapping(params = "methodToCall=" + "goToAddRule")
    public ModelAndView goToAddRule(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        AgendaEditor agendaEditor = getAgendaEditor(form);
        // this is the root of the tree:
        AgendaItemBo firstItem = getFirstAgendaItem(agendaEditor.getAgenda());
        String selectedItemId = agendaEditor.getSelectedAgendaItemId();

        if (StringUtils.isEmpty(selectedItemId)) {
            setSelectedAgendaItemId(form, null);
        } else {
            setSelectedAgendaItemId(form, selectedItemId);
        }
        setAgendaItemLine(form, null);

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
        AgendaEditor editorDocument = getAgendaEditor(form);
        if (agendaItem == null) {
            editorDocument.setAgendaItemLine(new AgendaItemBo());
        } else {
            // TODO: Add a copy not the reference
            editorDocument.setAgendaItemLine((AgendaItemBo) ObjectUtils.deepCopy(agendaItem));
        }
    }

    /**
     * This method returns the agendaItemLine from adding/editing AgendaItems.
     *
     * @param form
     * @return agendaItem
     */
    private AgendaItemBo getAgendaItemLine(UifFormBase form) {
        AgendaEditor editorDocument = getAgendaEditor(form);
        return editorDocument.getAgendaItemLine();
    }

    /**
     * This method sets the id of the selected agendaItem.
     *
     * @param form
     * @param selectedAgendaItemId
     */
    private void setSelectedAgendaItemId(UifFormBase form, String selectedAgendaItemId) {
        AgendaEditor editorDocument = getAgendaEditor(form);
        editorDocument.setSelectedAgendaItemId(selectedAgendaItemId);
    }

    /**
     * This method returns the id of the selected agendaItem.
     *
     * @param form
     * @return selectedAgendaItemId
     */
    private String getSelectedAgendaItemId(UifFormBase form) {
        AgendaEditor editorDocument = getAgendaEditor(form);
        return editorDocument.getSelectedAgendaItemId();
    }

    /**
     * This method sets the id of the cut agendaItem.
     *
     * @param form
     * @param cutAgendaItemId
     */
    private void setCutAgendaItemId(UifFormBase form, String cutAgendaItemId) {
        AgendaEditor editorDocument = getAgendaEditor(form);
        editorDocument.setCutAgendaItemId(cutAgendaItemId);
    }

    /**
     * This method returns the id of the cut agendaItem.
     *
     * @param form
     * @return cutAgendaItemId
     */
    private String getCutAgendaItemId(UifFormBase form) {
        AgendaEditor editorDocument = getAgendaEditor(form);
        return editorDocument.getCutAgendaItemId();
    }

    /**
     * This method updates the existing rule in the agenda.
     */
    @RequestMapping(params = "methodToCall=" + "goToEditRule")
    public ModelAndView goToEditRule(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        AgendaEditor agendaEditor = getAgendaEditor(form);
        // this is the root of the tree:
        AgendaItemBo firstItem = getFirstAgendaItem(agendaEditor.getAgenda());
        String selectedItemId = agendaEditor.getSelectedAgendaItemId();
        AgendaItemBo node = getAgendaItemById(firstItem, selectedItemId);

        setSelectedAgendaItemId(form, selectedItemId);
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

        AgendaEditor editorDocument = getAgendaEditor(form);
        AgendaBo agenda = editorDocument.getAgenda();
        AgendaItemBo newAgendaItem = editorDocument.getAgendaItemLine();
        newAgendaItem.setId(getSequenceAccessorService().getNextAvailableSequenceNumber("KRMS_AGENDA_ITM_S").toString());
        newAgendaItem.setAgendaId(agenda.getId());
        if (agenda.getItems() == null) {
            agenda.setItems(new ArrayList<AgendaItemBo>());
        }
        if (agenda.getFirstItemId() == null) {
            agenda.setFirstItemId(newAgendaItem.getId());
            agenda.getItems().add(newAgendaItem);
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
                AgendaItemBo node = getAgendaItemById(firstItem, getSelectedAgendaItemId(form));
                newAgendaItem.setAlwaysId(node.getAlwaysId());
                newAgendaItem.setAlways(node.getAlways());
                node.setAlwaysId(newAgendaItem.getId());
                node.setAlways(newAgendaItem);
            }
        }
        // add it to the collection on the agenda too
        agenda.getItems().add(newAgendaItem);

        form.getActionParameters().put(UifParameters.NAVIGATE_TO_PAGE_ID, "AgendaEditorView-Agenda-Page");
        return super.navigate(form, result, request, response);
    }

    /**
     * This method updates the existing rule in the agenda.
     */
    @RequestMapping(params = "methodToCall=" + "editRule")
    public ModelAndView editRule(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        AgendaBo agenda = getAgenda(form);
        // this is the root of the tree:
        AgendaItemBo firstItem = getFirstAgendaItem(agenda);
        AgendaItemBo node = getAgendaItemById(firstItem, getSelectedAgendaItemId(form));
        AgendaItemBo agendaItemLine = getAgendaItemLine(form);
        node.setRule(agendaItemLine.getRule());

        form.getActionParameters().put(UifParameters.NAVIGATE_TO_PAGE_ID, "AgendaEditorView-Agenda-Page");
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
     * This method gets the agenda from the form
     * 
     *
     * @param form
     * @return
     */
    private AgendaBo getAgenda(UifFormBase form) {
        AgendaEditor editorDocument = getAgendaEditor((MaintenanceForm) form);
        AgendaBo agenda = editorDocument.getAgenda();
        return agenda;
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

        if (firstItem != null) {
            // need to handle the first item here, our recursive method won't handle it.
            if (agendaItemSelected.equals(firstItem.getId())) {
                agendaEditor.getAgenda().setFirstItemId(firstItem.getAlwaysId());
            } else {
                deleteAgendaItem(firstItem, agendaItemSelected);
            }
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

    protected SequenceAccessorService getSequenceAccessorService() {
        if ( sequenceAccessorService == null ) {
            sequenceAccessorService = KRADServiceLocator.getSequenceAccessorService();
        }
        return sequenceAccessorService;
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

}
