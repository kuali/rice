/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.core.web.struts.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.OptimisticLockException;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.InvalidCancelException;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.config.ForwardConfig;
import org.kuali.RiceConstants;
import org.kuali.RiceKeyConstants;
import org.kuali.core.UserSession;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.util.ErrorContainer;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.ExceptionUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.Timer;
import org.kuali.core.web.struts.pojo.PojoForm;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.core.Core;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springmodules.orm.ojb.OjbOperationException;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles setup of user session and restoring of action form.
 * 
 * 
 */
public class KualiRequestProcessor extends RequestProcessor {

    private static Logger LOG = Logger.getLogger(KualiRequestProcessor.class);

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

	Timer t0 = new Timer("KualiRequestProcessor.process");

	LOG.info(new StringBuffer("Started processing request: '").append(request.getRequestURI()).append(
		"' w/ query string: '").append(request.getQueryString()).append("'"));
	super.process(request, response);
	LOG.info(new StringBuffer("Finished processing request: '").append(request.getRequestURI()).append(
		"' w/ query string: '").append(request.getQueryString()).append("'"));
	t0.log();
    }

    /**
         * override of the pre process for all struts requests which will ensure that we have the appropriate state for user
         * sessions for all of our requests, also populating the GlobalVariables class with our UserSession for convenience
         * to the non web layer based classes and implementations
         */
    @Override
    protected boolean processPreprocess(HttpServletRequest request, HttpServletResponse response) {
	Timer t0 = new Timer("KualiRequestProcessor.processPreprocess");

	String id = null;
	try {
	    UserSession userSession = null;
	    if (!isUserSessionEstablished(request)) {
		id = KNSServiceLocator.getWebAuthenticationService().getNetworkId(request);
		userSession = new UserSession(id);
	    } else {
		userSession = (UserSession) request.getSession().getAttribute(org.kuali.RiceConstants.USER_SESSION_KEY);
	    }
	    if (request.getParameter(RiceConstants.BACKDOOR_PARAMETER) != null
		    && request.getParameter(RiceConstants.BACKDOOR_PARAMETER).trim().length() > 0) {
		if (Core.getCurrentContextConfig().getProperty("rice.user") != null
			&& !new Boolean(Core.getCurrentContextConfig().getProperty("rice.user"))) {
		    userSession = (UserSession) request.getSession().getAttribute(RiceConstants.USER_SESSION_KEY);
		}
		userSession.setBackdoorUser(request.getParameter(RiceConstants.BACKDOOR_PARAMETER));
	    }

	    if (!userSession.getUniversalUser().isActiveForAnyModule()) {
		throw new RuntimeException(
			"You cannot log in, because you are not an active Kuali user.\nPlease ask someone to activate your account, if you need to use Kuali Financial Systems.\nThe user id provided was: "
				+ userSession.getUniversalUser().getPersonUserIdentifier() + ".\n");
	    }
	    request.getSession().setAttribute(org.kuali.RiceConstants.USER_SESSION_KEY, userSession);
	    GlobalVariables.setUserSession(userSession);
	    GlobalVariables.setErrorMap(new ErrorMap());
	    GlobalVariables.setMessageList(new ArrayList());
	    GlobalVariables.setAuditErrorMap(new HashMap());
	} catch (UserNotFoundException e) {
	    LOG.error("Caught a User Not found exception: " + id, e);
	    throw new RuntimeException("Invalid User: " + id, e);
	} catch (WorkflowException e) {
	    LOG.error("Caught a ResourceUnavailableException: " + id, e);
	    throw new RuntimeException("ResourceUnavailableException: ", e);
	}
	t0.log();
	return true;
    }

    /**
         * Checks if the user who made the request has a UserSession established
         * 
         * @param request
         *                the HTTPServletRequest object passed in
         * @return true if the user session has been established, false otherwise
         */
    private boolean isUserSessionEstablished(HttpServletRequest request) {
	Timer t0 = new Timer("KualiRequestProcessor.isUserSessionEstablished");
	boolean result = (request.getSession().getAttribute(org.kuali.RiceConstants.USER_SESSION_KEY) != null);
	t0.log();
	return result;
    }

    /**
         * Hooks into populate process to call form populate method if form is an instanceof PojoForm.
         */
    @Override
    protected void processPopulate(HttpServletRequest request, HttpServletResponse response, ActionForm form,
	    ActionMapping mapping) throws ServletException {

	Timer t0 = new Timer("KualiRequestProcessor.processPopulate");

	// if not PojoForm, call struts populate
	if (!(form instanceof PojoForm)) {
	    super.processPopulate(request, response, form, mapping);
	    t0.log();
	    return;
	}

	String multipart = mapping.getMultipartClass();
	if (multipart != null)
	    request.setAttribute(Globals.MULTIPART_KEY, multipart);

	form.setServlet(this.servlet);
	form.reset(mapping, request);

	// call populate on ActionForm
	((PojoForm) form).populate(request);
	request.setAttribute("UnconvertedValues", ((PojoForm) form).getUnconvertedValues().keySet());
	request.setAttribute("UnconvertedHash", ((PojoForm) form).getUnconvertedValues());

	t0.log();

    }

    /**
         * Hooks into validate to catch any errors from the populate, and translate the ErrorMap to ActionMessages.
         */
    @Override
    protected boolean processValidate(HttpServletRequest request, HttpServletResponse response, ActionForm form,
	    ActionMapping mapping) throws IOException, ServletException, InvalidCancelException {

	Timer t0 = new Timer("KualiRequestProcessor.processValidate");

	// skip form validate if we had errors from populate
	if (GlobalVariables.getErrorMap().isEmpty()) {
	    if (form == null) {
		t0.log();
		return (true);
	    }
	    // Was this request cancelled?
	    if (request.getAttribute(Globals.CANCEL_KEY) != null) {
		if (log.isDebugEnabled()) {
		    log.debug(" Cancelled transaction, skipping validation");
		}
		t0.log();
		return (true);
	    }

	    // Has validation been turned off for this mapping?
	    if (!mapping.getValidate()) {
		t0.log();
		return (true);
	    }

	    // call super to call forms validate
	    super.processValidate(request, response, form, mapping);
	}

	if (!GlobalVariables.getErrorMap().isEmpty()) {
	    publishErrorMessages(request);
	    // Special handling for multipart request
	    if (form.getMultipartRequestHandler() != null) {
		if (log.isTraceEnabled()) {
		    log.trace("  Rolling back multipart request");
		}
		form.getMultipartRequestHandler().rollback();
	    }

	    // Fix state that could be incorrect because of validation failure
	    if (form instanceof PojoForm)
		((PojoForm) form).processValidationFail();

	    // Was an input path (or forward) specified for this mapping?
	    String input = mapping.getInput();
	    if (input == null) {
		if (log.isTraceEnabled()) {
		    log.trace("  Validation failed but no input form available");
		}
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, getInternal().getMessage("noInput",
			mapping.getPath()));
		t0.log();
		return (false);
	    }

	    if (moduleConfig.getControllerConfig().getInputForward()) {
		ForwardConfig forward = mapping.findForward(input);
		processForwardConfig(request, response, forward);
	    } else {
		internalModuleRelativeForward(input, request, response);
	    }

	    t0.log();
	    return (false);
	} else {
	    t0.log();
	    return true;
	}

    }

    /**
         * Checks for return from a lookup or question, and restores the action form stored under the request parameter
         * docFormKey.
         */
    @Override
    protected ActionForm processActionForm(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) {
	Timer t0 = new Timer("KualiRequestProcessor.processActionForm");

	UserSession userSession = (UserSession) request.getSession().getAttribute(RiceConstants.USER_SESSION_KEY);

	String docFormKey = request.getParameter(RiceConstants.DOC_FORM_KEY);
	String methodToCall = request.getParameter(RiceConstants.DISPATCH_REQUEST_PARAMETER);
	String refreshCaller = request.getParameter(RiceConstants.REFRESH_CALLER);
	String searchListRequestKey = request.getParameter(RiceConstants.SEARCH_LIST_REQUEST_KEY);
	String documentWebScope = request.getParameter(RiceConstants.DOCUMENT_WEB_SCOPE);

	if (StringUtils.isNotBlank(docFormKey)
		&& (mapping.getPath().startsWith(RiceConstants.REFRESH_MAPPING_PREFIX)
			|| RiceConstants.RETURN_METHOD_TO_CALL.equalsIgnoreCase(methodToCall)
			|| RiceConstants.QUESTION_REFRESH.equalsIgnoreCase(refreshCaller) || RiceConstants.SESSION_SCOPE
			.equalsIgnoreCase(documentWebScope))) {

	    // check for search result storage and clear
	    GlobalVariables.getUserSession().removeObjectsByPrefix(RiceConstants.SEARCH_LIST_KEY_PREFIX);

	    if (userSession.retrieveObject(docFormKey) != null) {
		ActionForm form = (ActionForm) userSession.retrieveObject(docFormKey);
		request.setAttribute(mapping.getAttribute(), form);
		userSession.removeObject(docFormKey);
		t0.log();
		return form;
	    }
	}
	t0.log();
	return super.processActionForm(request, response, mapping);
    }

    /**
         * Hook into action perform to handle errors in the error map and catch exceptions.
         * 
         * <p>
         * A transaction is started prior to the execution of the action. This allows for the action code to execute
         * efficiently without the need for using PROPAGATION_SUPPORTS in the transaction definitions. The
         * PROPAGATION_SUPPORTS propagation type does not work well with JTA.
         */
    @Override
    protected ActionForward processActionPerform(final HttpServletRequest request, final HttpServletResponse response,
	    final Action action, final ActionForm form, final ActionMapping mapping) throws IOException, ServletException {

	Timer t0 = new Timer("KualiRequestProcessor.processActionPerform");

	try {
	    TransactionTemplate template = new TransactionTemplate(KNSServiceLocator.getTransactionManager());
	    ActionForward forward = null;
	    try {
		forward = (ActionForward) template.execute(new TransactionCallback() {
		    public Object doInTransaction(TransactionStatus status) {
			ActionForward actionForward = null;
			try {
			    actionForward = action.execute(mapping, form, request, response);
			} catch (Exception e) {
			    // the doInTransaction method has no means for throwing exceptions, so we will wrap the
                                // exception in
			    // a RuntimeException and re-throw. The one caveat here is that this will always result in
                                // the
			    // transaction being rolled back (since WrappedRuntimeException is a runtime exception).
			    throw new WrappedRuntimeException(e);
			}
			if (status.isRollbackOnly()) {
			    // this means that the struts action execution caused the transaction to rollback, we want to
                                // go ahead
			    // and trigger the rollback by throwing an exception here but then return the action forward
                                // from this method
			    throw new WrappedActionForwardRuntimeException(actionForward);
			}
			return actionForward;
		    }
		});
	    } catch (WrappedActionForwardRuntimeException e) {
		forward = e.getActionForward();
	    }
	    publishErrorMessages(request);
	    saveMessages(request);
	    saveAuditErrors(request);

	    t0.log();
	    return forward;

	} catch (Exception e) {
	    if (e instanceof WrappedRuntimeException) {
		e = (Exception) e.getCause();
	    }
	    if (e instanceof ValidationException) {
		// add a generic error message if there are none
		if (GlobalVariables.getErrorMap().isEmpty()) {

		    GlobalVariables.getErrorMap().putError(RiceConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_CUSTOM,
			    e.getMessage());
		}

		// display error messages and return to originating page
		publishErrorMessages(request);
		t0.log();
		return mapping.findForward(RiceConstants.MAPPING_BASIC);
	    }

	    publishErrorMessages(request);

	    t0.log();
	    return (processException(request, response, e, form, mapping));
	}
    }

    /**
         * Adds more detailed logging for unhandled exceptions
         * 
         * @see org.apache.struts.action.RequestProcessor#processException(HttpServletRequest, HttpServletResponse,
         *      Exception, ActionForm, ActionMapping)
         */
    @Override
    protected ActionForward processException(HttpServletRequest request, HttpServletResponse response, Exception exception,
	    ActionForm form, ActionMapping mapping) throws IOException, ServletException {
	Timer t0 = new Timer("KualiRequestProcessor.processException");
	ActionForward actionForward = null;

	try {
	    actionForward = super.processException(request, response, exception, form, mapping);
	} catch (IOException e) {
	    logException(e);
	    t0.log();
	    throw e;
	} catch (ServletException e) {
	    // special case, to make OptimisticLockExceptions easier to read
	    Throwable rootCause = e.getRootCause();
	    if (rootCause instanceof OjbOperationException) {
		OjbOperationException ooe = (OjbOperationException) rootCause;

		Throwable subcause = ooe.getCause();
		if (subcause instanceof OptimisticLockException) {
		    OptimisticLockException ole = (OptimisticLockException) subcause;

		    StringBuffer message = new StringBuffer(e.getMessage());

		    Object sourceObject = ole.getSourceObject();
		    if (sourceObject != null) {
			message.append(" (sourceObject is ");
			message.append(sourceObject.getClass().getName());
			message.append(")");
		    }

		    e = new ServletException(message.toString(), rootCause);
		}
	    }

	    logException(e);
	    t0.log();
	    throw e;
	}
	t0.log();
	return actionForward;
    }

    private void logException(Exception e) {
	LOG.error("unhandled exception thrown by KualiRequestProcessor.processActionPerform");

	if (e.getCause() != null) {
	    ExceptionUtils.logStackTrace(LOG, e.getCause());
	} else {
	    ExceptionUtils.logStackTrace(LOG, e);
	}
    }

    /**
         * Checks for errors in the error map and transforms them to struts action messages then stores in the request.
         */
    private void publishErrorMessages(HttpServletRequest request) {
	if (!GlobalVariables.getErrorMap().isEmpty()) {
	    ErrorContainer errorContainer = new ErrorContainer(GlobalVariables.getErrorMap());

	    request.setAttribute("ErrorContainer", errorContainer);
	    request.setAttribute(Globals.ERROR_KEY, errorContainer.getRequestErrors());
	    request.setAttribute("ErrorPropertyList", errorContainer.getErrorPropertyList());
	}
    }

    /**
         * Checks for messages in GlobalVariables and places list in request attribute.
         */
    private void saveMessages(HttpServletRequest request) {
	if (!GlobalVariables.getMessageList().isEmpty()) {
	    request.setAttribute(RiceConstants.GLOBAL_MESSAGES, GlobalVariables.getMessageList());
	}
    }

    /**
         * Checks for messages in GlobalVariables and places list in request attribute.
         */
    private void saveAuditErrors(HttpServletRequest request) {
	if (!GlobalVariables.getAuditErrorMap().isEmpty()) {
	    request.setAttribute(RiceConstants.AUDIT_ERRORS, GlobalVariables.getAuditErrorMap());
	}
    }

    /**
         * A simple exception that allows us to wrap an exception that is thrown out of a transaction template.
         */
    private static class WrappedRuntimeException extends RuntimeException {
	public WrappedRuntimeException(Exception e) {
	    super(e);
	}
    }

    private static class WrappedActionForwardRuntimeException extends RuntimeException {
	private ActionForward actionForward;

	public WrappedActionForwardRuntimeException(ActionForward actionForward) {
	    this.actionForward = actionForward;
	}

	public ActionForward getActionForward() {
	    return actionForward;
	}
    }

}