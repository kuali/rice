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
package org.kuali.rice.kns.web.spring.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.xml.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.exception.AuthorizationException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.UifParameters;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.service.ViewService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.spring.form.UifFormBase;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Base controller class for views within the KRAD User Interface Framework
 * 
 * <p>
 * Provides common methods such as:
 * <ul>
 * <li>Authorization methods such as method to call check</li>
 * <li>Preparing the View instance and setup in the returned
 * <code>ModelAndView</code></li>
 * </ul>
 * </p>
 * 
 * <p>
 * All subclass controller methods after processing should call one of the
 * #getUIFModelAndView methods to setup the <code>View</code> and return the
 * <code>ModelAndView</code> instance.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class UifControllerBase {
	protected static final String REDIRECT_PREFIX = "redirect:";

	/**
	 * This method will create/obtain the model(form) object before it is passed
	 * to the Binder/BeanWrapper.
	 * 
	 * This method is not intended to be overridden by client applications as it
	 * handles framework setup and session maintenance. Clients should override
	 * createIntialForm() instead when they need custom form initialization.
	 * 
	 * @param request
	 * @return
	 */
	@ModelAttribute(value = "KualiForm")
	public UifFormBase initForm(HttpServletRequest request) {
		UifFormBase form;
		String formKeyParam = request.getParameter(KNSConstants.FORM_KEY);

		if (StringUtils.isNotBlank(formKeyParam)) {
			form = (UifFormBase) request.getSession().getAttribute(formKeyParam);
		}
		else {
			form = createInitialForm(request);
		}

		return form;
	}

	/**
	 * This method will be called to create a new model(form) object when
	 * necessary. This usually occurs on the initial request in a conversation
	 * (when the model is not present in the session).
	 * 
	 * This method must be overridden when extending a controller and using a
	 * different form type than the superclass.
	 * 
	 * @param request
	 * @return
	 */
	protected abstract UifFormBase createInitialForm(HttpServletRequest request);

	private Set<String> methodToCallsToNotCheckAuthorization = new HashSet<String>();
	{
		methodToCallsToNotCheckAuthorization.add("performLookup");
		methodToCallsToNotCheckAuthorization.add("performQuestion");
		methodToCallsToNotCheckAuthorization.add("performQuestionWithInput");
		methodToCallsToNotCheckAuthorization.add("performQuestionWithInputAgainBecauseOfErrors");
		methodToCallsToNotCheckAuthorization.add("performQuestionWithoutInput");
		methodToCallsToNotCheckAuthorization.add("performWorkgroupLookup");
	}

	/**
	 * Use to add a methodToCall to the a list which will not have authorization
	 * checks. This assumes that the call will be redirected (as in the case of
	 * a lookup) that will perform the authorization.
	 */
	protected final void addMethodToCallToUncheckedList(String methodToCall) {
		methodToCallsToNotCheckAuthorization.add(methodToCall);
	}

	/**
	 * Returns an immutable Set of methodToCall parameters that should not be
	 * checked for authorization.
	 * 
	 * @return
	 */
	public Set<String> getMethodToCallsToNotCheckAuthorization() {
		return Collections.unmodifiableSet(methodToCallsToNotCheckAuthorization);
	}

	/**
	 * Override this method to provide controller class-level access controls to
	 * the application.
	 * 
	 * @param form
	 * @throws AuthorizationException
	 */
	public void checkAuthorization(UifFormBase form, String methodToCall) throws AuthorizationException {
		String principalId = GlobalVariables.getUserSession().getPrincipalId();
		AttributeSet roleQualifier = new AttributeSet(getRoleQualification(form, methodToCall));
		AttributeSet permissionDetails = KimCommonUtils.getNamespaceAndActionClass(this.getClass());

		if (!KIMServiceLocator.getIdentityManagementService().isAuthorizedByTemplateName(principalId,
				KNSConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.USE_SCREEN, permissionDetails,
				roleQualifier)) {
			throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(),
					methodToCall, this.getClass().getSimpleName());
		}
	}

	/**
	 * override this method to add data from the form for role qualification in
	 * the authorization check
	 * 
	 * @param form
	 * @param methodToCall
	 */
	protected Map<String, String> getRoleQualification(UifFormBase form, String methodToCall) {
		return new HashMap<String, String>();
	}

	/**
	 * Called by the add line action for a new collection line. Method
	 * determines which collection the add action was selected for and invokes
	 * the view helper service to add the line
	 * 
	 * @param uifForm
	 *            - form instance that contains the data (should extend
	 *            UifFormBase)
	 * @param result
	 *            - contains results of the data binding
	 * @param request
	 *            - HTTP request
	 * @param response
	 *            - HTTP response
	 * @return ModelAndView
	 */
	@RequestMapping(method = RequestMethod.POST, params = "methodToCall=addLine")
	public ModelAndView addLine(@ModelAttribute("KualiForm") UifFormBase uifForm, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {

		String selectedCollectionPath = uifForm.getActionParamaterValue(UifParameters.SELLECTED_COLLECTION_PATH);
		if (StringUtils.isBlank(selectedCollectionPath)) {
			throw new RuntimeException("Selected collection was not set for add line action, cannot add new line");
		}

		View view = uifForm.getView();
		view.getViewHelperService().processCollectionAddLine(view, uifForm, selectedCollectionPath);

		return getUIFModelAndView(uifForm);
	}

	/**
	 * Called by the delete line action for a model collection. Method
	 * determines which collection the action was selected for and the line
	 * index that should be removed, then invokes the view helper service to
	 * process the action
	 * 
	 * @param uifForm
	 *            - form instance that contains the data (should extend
	 *            UifFormBase)
	 * @param result
	 *            - contains results of the data binding
	 * @param request
	 *            - HTTP request
	 * @param response
	 *            - HTTP response
	 * @return ModelAndView
	 */
	@RequestMapping(method = RequestMethod.POST, params = "methodToCall=deleteLine")
	public ModelAndView deleteLine(@ModelAttribute("KualiForm") UifFormBase uifForm, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {

		String selectedCollectionPath = uifForm.getActionParamaterValue(UifParameters.SELLECTED_COLLECTION_PATH);
		if (StringUtils.isBlank(selectedCollectionPath)) {
			throw new RuntimeException("Selected collection was not set for delete line action, cannot delete line");
		}

		int selectedLineIndex = -1;
		String selectedLine = uifForm.getActionParamaterValue(UifParameters.SELECTED_LINE_INDEX);
		if (StringUtils.isNotBlank(selectedLine)) {
			selectedLineIndex = Integer.parseInt(selectedLine);
		}

		if (selectedLineIndex == -1) {
			throw new RuntimeException("Selected line index was not set for delete line action, cannot delete line");
		}

		View view = uifForm.getView();
		view.getViewHelperService().processCollectionDeleteLine(view, uifForm, selectedCollectionPath,
				selectedLineIndex);

		return getUIFModelAndView(uifForm);
	}

	/**
	 * Handles menu navigation between view pages
	 */
	@RequestMapping(method = RequestMethod.POST, params = "methodToCall=navigate")
	public ModelAndView navigate(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {
		String pageId = form.getActionParamaterValue(UifParameters.NAVIGATE_TO_PAGE_ID);

		return getUIFModelAndView(form, form.getViewId(), pageId);
	}

	@RequestMapping(method = RequestMethod.POST, params = "methodToCall=navigateToLookup")
	public ModelAndView navigateToLookup(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {
		Properties lookupParameters = form.getActionParametersAsProperties();

		String baseLookupUrl = (String) lookupParameters.get(UifParameters.BASE_LOOKUP_URL);
		lookupParameters.remove(UifParameters.BASE_LOOKUP_URL);
		
		lookupParameters.put(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.START);

		return performRedirect(form, baseLookupUrl, lookupParameters);
	}

	protected ModelAndView performRedirect(UifFormBase form, String baseUrl, Properties urlParameters) {
		String redirectUrl = UrlFactory.parameterizeUrl(baseUrl, urlParameters);

		ModelAndView modelAndView = new ModelAndView(REDIRECT_PREFIX + redirectUrl);

		return modelAndView;
	}

	protected ModelAndView getUIFModelAndView(UifFormBase form) {
		return getUIFModelAndView(form, form.getViewId(), form.getPageId());
	}

	protected ModelAndView getUIFModelAndView(UifFormBase form, String viewId) {
		return getUIFModelAndView(form, viewId, "");
	}

	/**
	 * Prepares the <code>View</code> instance for the rendering (including
	 * applying the model) and builds the return <code>ModelAndView</code>
	 * object
	 * 
	 * @param form
	 *            - Form instance containing the model data
	 * @param viewId
	 *            - Id of the View to return
	 * @param pageId
	 *            - Id of the page within the view that should be rendered, can
	 *            be left blank in which the current or default page is rendered
	 * @return ModelAndView object with the contained form
	 */
	protected ModelAndView getUIFModelAndView(UifFormBase form, String viewId, String pageId) {
		// if we don't have the view instance or a different view was requested
		// get new instance from the view service
		View view = form.getView();
		if ((view == null) || !StringUtils.equals(viewId, view.getId())) {
			view = getViewService().getViewById(viewId);
		}

		// update the view with the model data
		getViewService().updateView(view, form);

		if (StringUtils.isNotBlank(pageId)) {
			view.setCurrentPageId(pageId);
		}

		form.setViewId(viewId);
		form.setPageId(pageId);
		form.setView(view);

		// create the spring return object pointing to View.jsp
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(UifConstants.DEFAULT_MODEL_NAME, form);
		modelAndView.setViewName(UifConstants.SPRING_VIEW_ID);

		return modelAndView;
	}

	protected ViewService getViewService() {
		return KNSServiceLocator.getViewService();
	}

}
