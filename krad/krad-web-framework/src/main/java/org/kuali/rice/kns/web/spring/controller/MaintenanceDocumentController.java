/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kns.web.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.bo.PersistableAttachment;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.MaintenanceDocumentService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.MaintenanceUtils;
import org.kuali.rice.kns.web.spring.form.DocumentFormBase;
import org.kuali.rice.kns.web.spring.form.MaintenanceForm;
import org.kuali.rice.kns.web.spring.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for <code>MaintenanceView</code> screens which operate on
 * <code>MaintenanceDocument</code> instances
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/maintenance")
public class MaintenanceDocumentController extends DocumentControllerBase {
	protected static final Logger LOG = Logger.getLogger(MaintenanceDocumentController.class);

	public static final String REQUEST_MAPPING_MAINTENANCE = "maintenance";
	public static final String METHOD_TO_CALL_NEW = "start";
	public static final String METHOD_TO_CALL_NEW_WITH_EXISTING = "maintenanceNewWithExisting";
	public static final String METHOD_TO_CALL_EDIT = "maintenanceEdit";
	public static final String METHOD_TO_CALL_COPY = "maintenanceCopy";
	public static final String METHOD_TO_CALL_DELETE = "maintenanceDelete";

	@Override
	public MaintenanceForm createInitialForm(HttpServletRequest request) {
		return new MaintenanceForm();
	}

	/**
	 * After the document is loaded calls method to setup the maintenance object
	 */
	@Override
	@RequestMapping(params = "methodToCall=docHandler")
	public ModelAndView docHandler(@ModelAttribute("KualiForm") DocumentFormBase formBase, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// TODO getting double view if we call base, not sure how to handle
	    // so pasting in superclass code
		// super.docHandler(formBase, request, response);
		// * begin copy/paste from the base
	    MaintenanceForm form = (MaintenanceForm)formBase;
	    
		// in all of the following cases we want to load the document
        if (ArrayUtils.contains(DOCUMENT_LOAD_COMMANDS, form.getCommand()) && form.getDocId() != null) {
            loadDocument(form);
        }
        else if (KEWConstants.INITIATE_COMMAND.equals(form.getCommand())) {
            createDocument(form);
        }
        else {
            LOG.error("docHandler called with invalid parameters");
            throw new IllegalStateException("docHandler called with invalid parameters");
        }
		// * end copy/paste from the base
        
		if (KEWConstants.ACTIONLIST_COMMAND.equals(form.getCommand())
				|| KEWConstants.DOCSEARCH_COMMAND.equals(form.getCommand())
				|| KEWConstants.SUPERUSER_COMMAND.equals(form.getCommand())
				|| KEWConstants.HELPDESK_ACTIONLIST_COMMAND.equals(form.getCommand()) && form.getDocId() != null) {
			// TODO: set state in view
			// form.setReadOnly(true);
			form.setMaintenanceAction((form.getDocument()).getNewMaintainableObject().getMaintenanceAction());

			// Retrieving the FileName from BO table
			Maintainable tmpMaintainable = form.getDocument().getNewMaintainableObject();
			if (tmpMaintainable.getDataObject() instanceof PersistableAttachment) {
				PersistableAttachment bo = (PersistableAttachment) getBusinessObjectService().retrieve(
						(PersistableBusinessObject) tmpMaintainable.getDataObject());
				if (bo != null) {
					request.setAttribute("fileName", bo.getFileName());
				}
			}
		}
		else if (KEWConstants.INITIATE_COMMAND.equals(form.getCommand())) {
			// form.setReadOnly(false);
			setupMaintenance(form, request, KNSConstants.MAINTENANCE_NEW_ACTION);
		}
		else {
			LOG.error("We should never have gotten to here");
			throw new IllegalStateException("docHandler called with invalid parameters");
		}

		return getUIFModelAndView(form);
	}

	/**
	 * Default method for controller that setups a new
	 * <code>MaintenanceView</code> with the default new action
	 */
	@RequestMapping(params = "methodToCall=" + METHOD_TO_CALL_NEW)
	public ModelAndView start(@ModelAttribute("KualiForm") MaintenanceForm form, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		setupMaintenance(form, request, KNSConstants.MAINTENANCE_NEW_ACTION);

		return getUIFModelAndView(form);
	}

	/**
	 * Setups a new <code>MaintenanceView</code> with the edit maintenance
	 * action
	 */
	@RequestMapping(params = "methodToCall=" + METHOD_TO_CALL_EDIT)
	public ModelAndView maintenanceEdit(@ModelAttribute("KualiForm") MaintenanceForm form, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		setupMaintenance(form, request, KNSConstants.MAINTENANCE_EDIT_ACTION);

		return getUIFModelAndView(form);
	}

	/**
	 * Setups a new <code>MaintenanceView</code> with the copy maintenance
	 * action
	 */
	@RequestMapping(params = "methodToCall=" + METHOD_TO_CALL_COPY)
	public ModelAndView maintenanceCopy(@ModelAttribute("KualiForm") MaintenanceForm form, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		setupMaintenance(form, request, KNSConstants.MAINTENANCE_COPY_ACTION);

		return getUIFModelAndView(form);
	}

	/**
	 * Setups a new <code>MaintenanceView</code> with the new with existing
	 * maintenance action
	 */
	@RequestMapping(params = "methodToCall=" + METHOD_TO_CALL_NEW_WITH_EXISTING)
	public ModelAndView maintenanceNewWithExisting(@ModelAttribute("KualiForm") MaintenanceForm form,
			BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {

		setupMaintenance(form, request, KNSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);

		return getUIFModelAndView(form);
	}

	/**
	 * Sets up the <code>MaintenanceDocument</code> on initial get request
	 * 
	 * <p>
	 * First step is to create a new document instance based on the query
	 * parameters (document type name or object class name). Then call the
	 * <code>Maintainable</code> to do setup on the object being maintained.
	 * </p>
	 * 
	 * @param form
	 *            - <code>MaintenanceForm</code> instance
	 * @param request
	 *            - HTTP request object
	 * @param maintenanceAction
	 *            - the maintenance action (new, new from existing, edit, copy)
	 *            being request
	 * @throws Exception
	 */
	protected void setupMaintenance(MaintenanceForm form, HttpServletRequest request, String maintenanceAction)
			throws Exception {
		MaintenanceDocument document = form.getDocument();

		// create a new document object, if required
		if (document == null) {
			document = getMaintenanceDocumentService().setupNewMaintenanceDocument(form.getDataObjectClassName(),
					form.getDocTypeName(), maintenanceAction);

			form.setDocument(document);
			form.setDocTypeName(document.getDocumentHeader().getWorkflowDocument().getDocumentType());
		}

		// set action on form
		form.setMaintenanceAction(maintenanceAction);

		// invoke maintainable to setup the object for maintenance
		document.getNewMaintainableObject().setupMaintenanceObject(document, maintenanceAction,
				request.getParameterMap());

		// for new maintainable check if a maintenance lock exists and if so
		// warn the user
		if (KNSConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)) {
			MaintenanceUtils.checkForLockingDocument(document.getNewMaintainableObject(), false);
		}

		// Retrieve notes topic display flag from data dictionary and add to
		// document
		// TODO: should be in the view as permission
		DocumentEntry entry = getMaintenanceDocumentDictionaryService().getMaintenanceDocumentEntry(
				document.getDocumentHeader().getWorkflowDocument().getDocumentType());
		document.setDisplayTopicFieldInNotes(entry.getDisplayTopicFieldInNotes());
	}

	/**
	 * Override route to retrieve the maintenance object if it is an attachment
	 * 
	 * @see org.kuali.rice.kns.web.spring.controller.DocumentControllerBase.route
	 *      (DocumentFormBase, HttpServletRequest, HttpServletResponse)
	 */
	@Override
	@RequestMapping(params = "methodToCall=route")
    public ModelAndView route(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView modelAndView;

        try {
            modelAndView = super.route(form, result, request, response);

            MaintenanceDocument document = (MaintenanceDocument) form.getDocument();
            if (document.getNewMaintainableObject().getDataObject() instanceof PersistableAttachment) {
                PersistableAttachment bo = (PersistableAttachment) getBusinessObjectService().retrieve((PersistableBusinessObject) document.getNewMaintainableObject().getDataObject());
                request.setAttribute("fileName", bo.getFileName());
            }
        } catch (ValidationException vex) {
            modelAndView = getUIFModelAndView(form);
        }

        return modelAndView;
    }
	
	@Override
    protected ModelAndView getUIFModelAndView(UifFormBase form, String viewId, String pageId) {
	    // TODO: remove once error path is fixed in business rules
//	    // update error messages
//	    Map<String, AutoPopulatingList> adjustedPathErrors = new HashMap<String, AutoPopulatingList>();
//	    MessageMap messageMap = GlobalVariables.getMessageMap();
//	    for (Entry<String, AutoPopulatingList> pathErrors : messageMap.getErrorMessages().entrySet()) {
//	        String path = pathErrors.getKey();
//	        if (path.startsWith("document.newMaintainableObject") && !path.startsWith("document.newMaintainableObject.dataObject")) {
//	            String adjustedPath = StringUtils.replace(path, "document.newMaintainableObject", "document.newMaintainableObject.dataObject");
//	            adjustedPathErrors.put(adjustedPath, pathErrors.getValue());
//	        }
//	    }
	    
        return super.getUIFModelAndView(form, viewId, pageId);
    }

    protected MaintenanceDocumentService getMaintenanceDocumentService() {
		return KNSServiceLocatorWeb.getMaintenanceDocumentService();
	}

	protected MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		return KNSServiceLocatorWeb.getMaintenanceDocumentDictionaryService();
	}

}
