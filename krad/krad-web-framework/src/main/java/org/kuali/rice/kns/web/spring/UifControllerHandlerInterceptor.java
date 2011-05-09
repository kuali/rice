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
package org.kuali.rice.kns.web.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.service.SessionDocumentService;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.history.History;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.spring.controller.UifControllerBase;
import org.kuali.rice.kns.web.spring.form.DocumentFormBase;
import org.kuali.rice.kns.web.spring.form.UifFormBase;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * This is a description of what this class does - swgibson don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class UifControllerHandlerInterceptor implements HandlerInterceptor {
    
    private static final Logger LOG = Logger.getLogger(UifControllerHandlerInterceptor.class);
    
    private static Boolean OUTPUT_ENCRYPTION_WARNING = null;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    // do nothing
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        
        if(handler instanceof UifControllerBase) {
            UifControllerBase controller = (UifControllerBase)handler;
            UifFormBase form = null;
            
            Object model = modelAndView.getModelMap().get(UifConstants.DEFAULT_MODEL_NAME);
            if(model instanceof UifFormBase) {
                form = (UifFormBase)model;
                //Main history/breadcrumb tracking support
                History history = form.getFormHistory();
                View view = form.getView();

                history.setHomewardPath(view.getBreadcrumbs().getHomewardPathList());
                history.setAppendHomewardPath(view.getBreadcrumbs().isDisplayHomewardPath());
                history.setAppendPassedHistory(view.getBreadcrumbs().isDisplayPassedHistory());
                //Passed settings ALWAYS override the defaults
                if(StringUtils.isNotBlank(request.getParameter("showHome"))){
                    history.setAppendHomewardPath(Boolean.parseBoolean(request.getParameter("showHome")));
                }
                if(StringUtils.isNotBlank(request.getParameter("showHistory"))){
                    history.setAppendPassedHistory(Boolean.parseBoolean(request.getParameter("showHistory")));
                }
                history.setCurrent(form, request);
                history.buildHistoryFromParameterString(request.getParameter("history"));
            }
            
            form.setPreviousView(null);

            //Store form to session and persist document form to db as well
            request.getSession().setAttribute(form.getFormKey(), model);
            if (form instanceof DocumentFormBase){
            	UserSession userSession = (UserSession) request.getSession().getAttribute(KNSConstants.USER_SESSION_KEY);            
            	getSessionDocumentService().setDocumentForm((DocumentFormBase)form, userSession, request.getRemoteAddr());
            }
            
            // currently methodToCall must be a regularly parseable request parameter, so just get from request
            String methodToCall = request.getParameter(KNSConstants.DISPATCH_REQUEST_PARAMETER);
            
            // make sure the user can do what they're trying to according to the module that owns the functionality
            // this is done post handle to be able to access the form and whatever processing
            // was done (this was the same pre-krad)
            if (!controller.getMethodToCallsToNotCheckAuthorization().contains(methodToCall)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("'" + methodToCall + "' not in set of excempt methods: " + controller.getMethodToCallsToNotCheckAuthorization());
                }
                
                controller.checkAuthorization(form, methodToCall);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("'" + methodToCall + "' is exempt from auth checks.");
                }
            }
        }

        // check if demonstration encryption is enabled
        if (LOG.isEnabledFor(Level.WARN)) {
        	// TODO: need someway to determine if demo encryption is running
//            if (OUTPUT_ENCRYPTION_WARNING == null) {
//                OUTPUT_ENCRYPTION_WARNING = Boolean.valueOf(CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(KNSConstants.KNS_NAMESPACE, KNSConstants.DetailTypes.ALL_DETAIL_TYPE, KNSConstants.SystemGroupParameterNames.CHECK_ENCRYPTION_SERVICE_OVERRIDE_IND) && CoreApiServiceLocator.getEncryptionService() instanceof Demonstration);
//            }
//            if (OUTPUT_ENCRYPTION_WARNING.booleanValue()) {
//                LOG.warn("WARNING: This implementation of Kuali uses the demonstration encryption framework.");
//            }
        }
    }

    /**
     * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        final UserSession session = WebUtils.getUserSessionFromRequest(request);

        if (session == null) {
            throw new IllegalStateException("the user session has not been established");
        }

        GlobalVariables.setUserSession(session);
        GlobalVariables.clear();

        return true;
    }
    
	/**
	 * @return the sessionDocumentService
	 */
	public SessionDocumentService getSessionDocumentService() {
		return KNSServiceLocatorWeb.getSessionDocumentService();
	}    
}
