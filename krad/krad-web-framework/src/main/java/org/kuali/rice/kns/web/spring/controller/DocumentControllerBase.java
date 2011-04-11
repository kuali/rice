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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.rice.core.framework.parameter.ParameterConstants;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.bo.AdHocRouteRecipient;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.DocumentAuthorizationException;
import org.kuali.rice.kns.exception.UnknownDocumentIdException;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.SessionTicket;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.spring.form.DocumentFormBase;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Base controller class for all KRAD <code>DocumentView</code> screens working
 * with <code>Document</code> models
 * 
 * <p>
 * Provides default controller implementations for the standard document actions
 * including: doc handler (retrieve from doc search and action list), save,
 * route (and other KEW actions)
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class DocumentControllerBase extends UifControllerBase {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentControllerBase.class);

	// COMMAND constants which cause docHandler to load an existing document
	// instead of creating a new one
	protected static final String[] DOCUMENT_LOAD_COMMANDS = { KEWConstants.ACTIONLIST_COMMAND,
			KEWConstants.DOCSEARCH_COMMAND, KEWConstants.SUPERUSER_COMMAND, KEWConstants.HELPDESK_ACTIONLIST_COMMAND };

	private BusinessObjectService businessObjectService;
	private BusinessObjectAuthorizationService businessObjectAuthorizationService;
	private BusinessObjectMetaDataService businessObjectMetaDataService;
	private DataDictionaryService dataDictionaryService;
	private DocumentService documentService;
	private DocumentHelperService documentHelperService;

	@Override
	public abstract DocumentFormBase createInitialForm(HttpServletRequest request);

	/**
	 * Used to funnel all document handling through, we could do useful things
	 * like log and record various openings and status Additionally it may be
	 * nice to have a single dispatcher that can know how to dispatch to a
	 * redirect url for document specific handling but we may not need that as
	 * all we should need is the document to be able to load itself based on
	 * document id and then which action forward or redirect is pertinent for
	 * the document type.
	 */
	@RequestMapping(params = "methodToCall=docHandler")
	public ModelAndView docHandler(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String command = form.getCommand();

		// in all of the following cases we want to load the document
		if (ArrayUtils.contains(DOCUMENT_LOAD_COMMANDS, command) && form.getDocId() != null) {
			loadDocument(form);
		}
		else if (KEWConstants.INITIATE_COMMAND.equals(command)) {
			createDocument(form);
		}
		else {
			LOG.error("docHandler called with invalid parameters");
			throw new IllegalStateException("docHandler called with invalid parameters");
		}

		// TODO: authorization on document actions
		// if (KEWConstants.SUPERUSER_COMMAND.equalsIgnoreCase(command)) {
		// form.setSuppressAllButtons(true);
		// }

		return getUIFModelAndView(form);
	}

	/**
	 * Loads the document by its provided document header id. This has been
	 * abstracted out so that it can be overridden in children if the need
	 * arises.
	 * 
	 * @param form
	 *            - form instance that contains the doc id parameter and where
	 *            the retrieved document instance should be set
	 */
	protected void loadDocument(DocumentFormBase form) throws WorkflowException {
		String docId = form.getDocId();

		Document doc = null;
		doc = getDocumentService().getByDocumentHeaderId(docId);
		if (doc == null) {
			throw new UnknownDocumentIdException(
					"Document no longer exists.  It may have been cancelled before being saved.");
		}

		KualiWorkflowDocument workflowDocument = doc.getDocumentHeader().getWorkflowDocument();
		if (!getDocumentHelperService().getDocumentAuthorizer(doc).canOpen(doc,
				GlobalVariables.getUserSession().getPerson())) {
			throw buildAuthorizationException("open", doc);
		}

		// re-retrieve the document using the current user's session - remove
		// the system user from the WorkflowDcument object
		if (workflowDocument != doc.getDocumentHeader().getWorkflowDocument()) {
			LOG.warn("Workflow document changed via canOpen check");
			doc.getDocumentHeader().setWorkflowDocument(workflowDocument);
		}

		form.setDocument(doc);
		KualiWorkflowDocument workflowDoc = doc.getDocumentHeader().getWorkflowDocument();
		form.setDocTypeName(workflowDoc.getDocumentType());

		KNSServiceLocatorWeb.getSessionDocumentService().addDocumentToUserSession(GlobalVariables.getUserSession(), workflowDoc);
	}

	/**
	 * Creates a new document of the type specified by the docTypeName property
	 * of the given form. This has been abstracted out so that it can be
	 * overridden in children if the need arises.
	 * 
	 * @param form
	 *            - form instance that contains the doc type parameter and where
	 *            the new document instance should be set
	 */
	protected void createDocument(DocumentFormBase form) throws WorkflowException {
		Document doc = getDocumentService().getNewDocument(form.getDocTypeName());

		form.setDocument(doc);
		form.setDocTypeName(doc.getDocumentHeader().getWorkflowDocument().getDocumentType());
	}

	/**
	 * Saves the <code>Document</code> instance
	 */
	@RequestMapping(params = "methodToCall=save")
	public ModelAndView save(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		doProcessingAfterPost(form, request);

		// get any possible changes to to adHocWorkgroups
		// TODO turn this back on
		// refreshAdHocRoutingWorkgroupLookups(request, form);

		Document document = form.getDocument();

		String viewName = checkAndWarnAboutSensitiveData(form, request, response,
				KNSPropertyConstants.DOCUMENT_EXPLANATION, document.getDocumentHeader().getExplanation(), "save", "");
		// TODO if the happens we may need to save form to session or account
		// for it
		if (viewName != null) {
			return new ModelAndView(viewName);
		}

		try {
    		// save in workflow
    		getDocumentService().saveDocument(document);
    
    		GlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_SAVED);
    		form.setAnnotation("");
		}
		catch(ValidationException vex) {
		    // swallow exception so screen will draw with errors
		}

		return getUIFModelAndView(form);
	}

	/**
	 * Routes the <code>Document</code> instance using the document service
	 */
	@RequestMapping(params = "methodToCall=route")
	public ModelAndView route(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		doProcessingAfterPost(form, request);

		// TODO: prerules
		// kualiDocumentFormBase.setDerivedValuesOnForm(request);
		// ActionForward preRulesForward = promptBeforeValidation(mapping, form,
		// request, response);
		// if (preRulesForward != null) {
		// return preRulesForward;
		// }

		Document document = form.getDocument();

		String viewName = checkAndWarnAboutSensitiveData(form, request, response,
				KNSPropertyConstants.DOCUMENT_EXPLANATION, document.getDocumentHeader().getExplanation(), "route", "");
		if (viewName != null) {
			return new ModelAndView(viewName);
		}

		// TODO: adhoc recipients
		// getDocumentService().routeDocument(document, form.getAnnotation(),
		// combineAdHocRecipients(kualiDocumentFormBase));
		getDocumentService().routeDocument(document, form.getAnnotation(), new ArrayList<AdHocRouteRecipient>());

		GlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_ROUTE_SUCCESSFUL);
		form.setAnnotation("");

		// GlobalVariables.getUserSession().addObject(DocumentAuthorizerBase.USER_SESSION_METHOD_TO_CALL_COMPLETE_OBJECT_KEY,Boolean.TRUE);
		return getUIFModelAndView(form);
	}

	/**
	 * Does all special processing on a document that should happen on each HTTP
	 * post (ie, save, route, approve, etc).
	 * 
	 * @param form
	 * @param request
	 */
	protected void doProcessingAfterPost(DocumentFormBase form, HttpServletRequest request) {
		getBusinessObjectService().linkUserFields(form.getDocument());
	}

	// TODO this needs more analysis before porting can finish
	/*
	 * protected void refreshAdHocRoutingWorkgroupLookups(HttpServletRequest
	 * request, DocumentFormBase form) throws WorkflowException { for
	 * (Enumeration<String> i = request.getParameterNames();
	 * i.hasMoreElements();) { String parameterName = i.nextElement();
	 * 
	 * // TODO does this really belong in the loop if
	 * (parameterName.equals("newAdHocRouteWorkgroup.recipientName") &&
	 * !"".equals(request.getParameter(parameterName))) { //check for namespace
	 * String namespace = KimConstants.KIM_GROUP_DEFAULT_NAMESPACE_CODE; if
	 * (request.getParameter("newAdHocRouteWorkgroup.recipientNamespaceCode") !=
	 * null &&
	 * !"".equals(request.getParameter("newAdHocRouteWorkgroup.recipientName"
	 * ).trim())) {
	 * 
	 * namespace =
	 * request.getParameter("newAdHocRouteWorkgroup.recipientNamespaceCode"
	 * ).trim(); } Group group =
	 * getIdentityManagementService().getGroupByName(namespace,
	 * request.getParameter(parameterName)); if (group != null) {
	 * form.getNewAdHocRouteWorkgroup().setId(group.getGroupId());
	 * form.getNewAdHocRouteWorkgroup().setRecipientName(group.getGroupName());
	 * form
	 * .getNewAdHocRouteWorkgroup().setRecipientNamespaceCode(group.getNamespaceCode
	 * ()); } else { throw new
	 * RuntimeException("Invalid workgroup id passed as parameter."); } }
	 * 
	 * // TODO need to look at screen, will most of this just be bound to the
	 * form by spring? if (parameterName.startsWith("adHocRouteWorkgroup[") &&
	 * !"".equals(request.getParameter(parameterName))) { if
	 * (parameterName.endsWith(".recipientName")) { int lineNumber =
	 * Integer.parseInt(StringUtils.substringBetween(parameterName, "[", "]"));
	 * //check for namespace String namespaceParam = "adHocRouteWorkgroup[" +
	 * lineNumber + "].recipientNamespaceCode"; String namespace =
	 * KimConstants.KIM_GROUP_DEFAULT_NAMESPACE_CODE; if
	 * (request.getParameter(namespaceParam) != null &&
	 * !"".equals(request.getParameter(namespaceParam).trim())) { namespace =
	 * request.getParameter(namespaceParam).trim(); } Group group =
	 * getIdentityManagementService().getGroupByName(namespace,
	 * request.getParameter(parameterName)); if (group != null) {
	 * form.getAdHocRouteWorkgroup(lineNumber).setId(group.getGroupId());
	 * form.getAdHocRouteWorkgroup
	 * (lineNumber).setRecipientName(group.getGroupName());
	 * form.getAdHocRouteWorkgroup
	 * (lineNumber).setRecipientNamespaceCode(group.getNamespaceCode()); } else
	 * { throw new
	 * RuntimeException("Invalid workgroup id passed as parameter."); } } } } }
	 */

	/**
	 * Checks if the given value matches patterns that indicate sensitive data
	 * and if configured to give a warning for sensitive data will prompt the
	 * user to continue.
	 * 
	 * @param form
	 * @param request
	 * @param response
	 * @param fieldName
	 *            - name of field with value being checked
	 * @param fieldValue
	 *            - value to check for sensitive data
	 * @param caller
	 *            - method that should be called back from question
	 * @param context
	 *            - additional context that needs to be passed back with the
	 *            question response
	 * @return - view for spring to forward to, or null if processing should
	 *         continue
	 * @throws Exception
	 */
	protected String checkAndWarnAboutSensitiveData(DocumentFormBase form, HttpServletRequest request,
			HttpServletResponse response, String fieldName, String fieldValue, String caller, String context)
			throws Exception {

		String viewName = null;
		Document document = form.getDocument();

		boolean containsSensitiveData = WebUtils.containsSensitiveDataPatternMatch(fieldValue);

		// check if warning is configured in which case we will prompt, or if
		// not business rules will thrown an error
		boolean warnForSensitiveData = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(
				KNSConstants.KNS_NAMESPACE, ParameterConstants.ALL_COMPONENT,
				KNSConstants.SystemGroupParameterNames.SENSITIVE_DATA_PATTERNS_WARNING_IND);

		// determine if the question has been asked yet
		Map<String, String> ticketContext = new HashMap<String, String>();
		ticketContext.put(KNSPropertyConstants.DOCUMENT_NUMBER, document.getDocumentNumber());
		ticketContext.put(KNSConstants.CALLING_METHOD, caller);
		ticketContext.put(KNSPropertyConstants.NAME, fieldName);

		boolean questionAsked = GlobalVariables.getUserSession().hasMatchingSessionTicket(
				KNSConstants.SENSITIVE_DATA_QUESTION_SESSION_TICKET, ticketContext);

		// start in logic for confirming the sensitive data
		if (containsSensitiveData && warnForSensitiveData && !questionAsked) {
			Object question = request.getParameter(KNSConstants.QUESTION_INST_ATTRIBUTE_NAME);
			if (question == null || !KNSConstants.DOCUMENT_SENSITIVE_DATA_QUESTION.equals(question)) {

				// TODO not ready for question framework yet
				/*
				 * // question hasn't been asked, prompt to continue return
				 * this.performQuestionWithoutInput(mapping, form, request,
				 * response, KNSConstants.DOCUMENT_SENSITIVE_DATA_QUESTION,
				 * getKualiConfigurationService()
				 * .getPropertyString(RiceKeyConstants
				 * .QUESTION_SENSITIVE_DATA_DOCUMENT),
				 * KNSConstants.CONFIRMATION_QUESTION, caller, context);
				 */
				viewName = "ask_user_questions";
			}
			else {
				Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);

				// if no button clicked just reload the doc
				if (ConfirmationQuestion.NO.equals(buttonClicked)) {
					// TODO figure out what to return
					viewName = "user_says_no";
				}

				// answered yes, create session ticket so we not to ask question
				// again if there are further question requests
				SessionTicket ticket = new SessionTicket(KNSConstants.SENSITIVE_DATA_QUESTION_SESSION_TICKET);
				ticket.setTicketContext(ticketContext);
				GlobalVariables.getUserSession().putSessionTicket(ticket);
			}
		}

		// returning null will indicate processing should continue (no redirect)
		return viewName;
	}

	/**
	 * Convenience method for building authorization exceptions
	 * 
	 * @param action
	 *            - the action that was requested
	 * @param document
	 *            - document instance the action was requested for
	 */
	protected DocumentAuthorizationException buildAuthorizationException(String action, Document document) {
		return new DocumentAuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(),
				action, document.getDocumentNumber());
	}

	public BusinessObjectService getBusinessObjectService() {
		if (this.businessObjectService == null) {
			this.businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return this.businessObjectService;
	}

	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}

	public BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
		if (this.businessObjectAuthorizationService == null) {
			this.businessObjectAuthorizationService = KNSServiceLocatorWeb.getBusinessObjectAuthorizationService();
		}
		return this.businessObjectAuthorizationService;
	}

	public void setBusinessObjectAuthorizationService(
			BusinessObjectAuthorizationService businessObjectAuthorizationService) {
		this.businessObjectAuthorizationService = businessObjectAuthorizationService;
	}

	public BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
		if (this.businessObjectMetaDataService == null) {
			this.businessObjectMetaDataService = KNSServiceLocatorWeb.getBusinessObjectMetaDataService();
		}
		return this.businessObjectMetaDataService;
	}

	public void setBusinessObjectMetaDataService(BusinessObjectMetaDataService businessObjectMetaDataService) {
		this.businessObjectMetaDataService = businessObjectMetaDataService;
	}

	public DataDictionaryService getDataDictionaryService() {
		if (this.dataDictionaryService == null) {
			this.dataDictionaryService = KNSServiceLocatorWeb.getDataDictionaryService();
		}
		return this.dataDictionaryService;
	}

	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

	public DocumentService getDocumentService() {
		if (this.documentService == null) {
			this.documentService = KNSServiceLocatorWeb.getDocumentService();
		}
		return this.documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public DocumentHelperService getDocumentHelperService() {
		if (this.documentHelperService == null) {
			this.documentHelperService = KNSServiceLocatorWeb.getDocumentHelperService();
		}
		return this.documentHelperService;
	}

	public void setDocumentHelperService(DocumentHelperService documentHelperService) {
		this.documentHelperService = documentHelperService;
	}
}
