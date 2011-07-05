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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.kuali.rice.krad.lookup.LookupableImpl;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
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
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the Test UI Page
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/krmsAgendaEditor")
public class AgendaEditorController extends MaintenanceDocumentController {

    @Override
    public MaintenanceForm createInitialForm(HttpServletRequest request) {
        return new MaintenanceForm();
    }
    
    
    /**
     * This overridden method does extra work on refresh to populate the context and agenda
     * 
     * @see org.kuali.rice.krad.web.spring.controller.UifControllerBase#refresh(org.kuali.rice.krad.web.spring.form.UifFormBase, org.springframework.validation.BindingResult, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
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
        if (LookupableImpl.class.getName().equals(refreshCaller) &&
                conversionFields != null &&
                // TODO: this is sloppy
                conversionFields.contains("agenda.id")) {
            AgendaEditor editorDocument =
                    ((AgendaEditor) maintenanceForm.getDocument().getNewMaintainableObject().getDataObject());
            agendaId = editorDocument.getAgenda().getId();
            AgendaBo agenda = getBoService().findBySinglePrimaryKey(AgendaBo.class, agendaId);
            editorDocument.setAgenda(agenda);

            if (agenda.getContextId() != null) {
                ContextBo context = getBoService().findBySinglePrimaryKey(ContextBo.class, agenda.getContextId());
                editorDocument.setContext(context);
            }
        }
        
        return super.refresh(form, result, request, response);
    }
    
    
    @RequestMapping(params = "methodToCall=" + "delete")
    public ModelAndView delete(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        String agendaItemSelected = request.getParameter("agenda_item_selected");
        
        MaintenanceForm maintenanceForm = (MaintenanceForm) form;
        AgendaEditor editorDocument =
                ((AgendaEditor) maintenanceForm.getDocument().getNewMaintainableObject().getDataObject());
        AgendaBo agenda = editorDocument.getAgenda();
        
        AgendaItemBo firstItem = null;
        for (AgendaItemBo agendaItem : agenda.getItems()) {
            if (agenda.getFirstItemId().equals(agendaItem.getId())) {
                firstItem = agendaItem;
                break;
            }
        }
        
        if (firstItem != null) {
            // need to handle the first item here, our recursive method won't handle it.  
            if (agendaItemSelected.equals(firstItem.getAgendaId())) {
                agenda.setFirstItemId(firstItem.getAlwaysId());
            } else {
                deleteAgendaItem(firstItem, agendaItemSelected);
            }
        }
        
        return super.refresh(form, result, request, response);
    }
    

    // TODO: smarter delete would be desirable.
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
            for (AgendaItemChildAccessor nextChildAccessor : AgendaItemChildAccessor.children) {
                if (deleteAgendaItem(child, nextChildAccessor, agendaItemIdToDelete)) return true;
            }
        }
        return false;
    }

//  @RequestMapping(params = "methodToCall=" + "moveUp")
//  public ModelAndView moveUp(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
//          HttpServletRequest request, HttpServletResponse response)
//          throws Exception {
//      
//      String agendaItemSelected = request.getParameter("agenda_item_selected");
//      
//      MaintenanceForm maintenanceForm = (MaintenanceForm) form;
//      EditorDocument editorDocument = ((EditorDocument)maintenanceForm.getDocument().getDocumentBusinessObject());
//      
//      AgendaBo agenda = editorDocument.getAgenda();
//      
//      AgendaItemBo firstItem = null;
//      for (AgendaItemBo agendaItem : agenda.getItems()) {
//          if (agenda.getFirstItemId().equals(agendaItem.getId())) {
//              firstItem = agendaItem;
//              break;
//          }
//      }
//      
//      if (firstItem != null) {
//          
//          // need to handle the first item here, our recursive method won't handle it.  
//          if (agendaItemSelected.equals(firstItem.getAgendaId())) {
//              // do nothing, the first item is at the top already
//          } else {
//              AgendaItemBo [] parentAndItem = getAgendaItemParentAndChild(firstItem, agendaItemSelected);
//
//              if (parentAndItem != null && parentAndItem[0] != null) {
//                  // figure out which child of the parent we're dealing with 
//                  AgendaItemChild childAccessor = null;
//                  for (AgendaItemChild ca : AgendaItemChild.children) {
//                      childAccessor = ca;
//                      // if this is the right accessor
//                      if (childAccessor.getChild(parentAndItem[0]) == parentAndItem[1]) {
//                          break; // so now childAccessor should be set correctly
//                      }
//                  }
//                  
//                  // need the parent's parent -- man!
//                  AgendaItemBo [] grandparentAndParent = getAgendaItemParentAndChild(parentAndItem[0], agendaItemSelected);
//                  
//                  if (grandparentAndParent != null) {
//                      
//                      AgendaItemBo grandchild = parentAndItem[1].getAlways();
//                  }
//              }
//          }
//      }
//      
//      return super.refresh(form, result, request, response);
//  }
//
//    /**
//     * This method recursively finds the agenda with the given ID and returns an array where the first element is the parent
//     * and the second is the item with the search id given.
//     */
//    private AgendaItemBo [] getAgendaItemParentAndChild(AgendaItemBo item, String agendaIdToFind) {
//        if (item != null && item.getId().equals(agendaIdToFind)) {
//            // it is the root, so we can't return a parent
//            return new AgendaItemBo [] {null, item};
//        } else {
//            for (AgendaItemChild childAccessor : AgendaItemChild.children) {
//                AgendaItemBo child = childAccessor.getChild(item);
//                if (child != null) {
//                    if (child.getId().equals(agendaIdToFind)) {
//                        return new AgendaItemBo [] { item, child };
//                    } else {
//                        return getAgendaItemParentAndChild(child, agendaIdToFind);
//                    }
//                }
//            }
//        }
//        return null;
//    }

    private BusinessObjectService getBoService() {
        return KRADServiceLocator.getBusinessObjectService();
    }

    
    /**
     * This class abstracts getting and setting a child of an AgendaItemBo, making some recursive operations 
     * require less boiler plate 
     */
    private static class AgendaItemChildAccessor {
        
        private enum Child { WHEN_TRUE, WHEN_FALSE, ALWAYS };
        
        private static final AgendaItemChildAccessor whenTrue = new AgendaItemChildAccessor(Child.WHEN_TRUE); 
        private static final AgendaItemChildAccessor whenFalse = new AgendaItemChildAccessor(Child.WHEN_FALSE); 
        private static final AgendaItemChildAccessor always = new AgendaItemChildAccessor(Child.ALWAYS); 
        
        private static final AgendaItemChildAccessor [] children = { whenTrue, whenFalse, always };
        
        private final Child whichChild;
        
        private AgendaItemChildAccessor(Child whichChild) {
            if (whichChild == null) throw new IllegalArgumentException("whichChild must be non-null");
            this.whichChild = whichChild;
        }
        
        public AgendaItemBo getChild(AgendaItemBo parent) {
            switch (whichChild) {
            case WHEN_TRUE: return parent.getWhenTrue();
            case WHEN_FALSE: return parent.getWhenFalse();
            case ALWAYS: return parent.getAlways();
            default: throw new IllegalStateException();
            }
        }
        
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
