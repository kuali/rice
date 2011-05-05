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

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.exception.AuthorizationException;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.UifParameters;
import org.kuali.rice.kns.uif.container.LookupView;
import org.kuali.rice.kns.uif.core.Component;
import org.kuali.rice.kns.uif.service.LookupViewHelperService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSUtils;
import org.kuali.rice.kns.web.spring.form.LookupForm;
import org.kuali.rice.kns.web.spring.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * This is a handler for Lookups
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/lookup")
public class LookupController extends UifControllerBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupController.class);

	/**
	 * @see org.kuali.rice.kns.web.spring.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected LookupForm createInitialForm(HttpServletRequest request) {
        return new LookupForm();
	}

	protected void supressActionsIfNeeded(LookupForm lookupForm) {
        try {
            Class<?> dataObjectClass = Class.forName(lookupForm.getDataObjectClassName());
        	Person user = GlobalVariables.getUserSession().getPerson();
        	// check if creating documents is allowed
            String documentTypeName = KNSServiceLocatorWeb.getMaintenanceDocumentDictionaryService().getDocumentTypeName(dataObjectClass);
            if ((documentTypeName != null) && !KNSServiceLocatorWeb.getDocumentHelperService().getDocumentAuthorizer(documentTypeName).canInitiate(documentTypeName, user)) {
                ((LookupView)lookupForm.getView()).setSuppressActions( true );
            }
        }
        catch (ClassNotFoundException e) {
        	LOG.warn("Unable to load Data Object Class: " + lookupForm.getDataObjectClassName(), e);
        }
	}

	/**
     * @see org.kuali.rice.kns.web.spring.controller.UifControllerBase#checkAuthorization(org.kuali.rice.kns.web.spring.form.UifFormBase, java.lang.String)
     */
    @Override
	public void checkAuthorization(UifFormBase form, String methodToCall) throws AuthorizationException {
        if (!(form instanceof LookupForm)) {
            super.checkAuthorization(form, methodToCall);
        } else {
        	LookupForm lookupForm = (LookupForm) form;
            try {
                Class<?> dataObjectClass = Class.forName(lookupForm.getDataObjectClassName());
            	Person user = GlobalVariables.getUserSession().getPerson();
            	// check if user is allowed to lookup object
                if (!KimApiServiceLocator.getIdentityManagementService().isAuthorizedByTemplateName(user.getPrincipalId(), KNSConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.LOOK_UP_RECORDS, KNSUtils.getNamespaceAndComponentSimpleName(dataObjectClass), null)) {
                    throw new AuthorizationException(user.getPrincipalName(),
                    		KimConstants.PermissionTemplateNames.LOOK_UP_RECORDS,
                    		dataObjectClass.getSimpleName());
                }
            }
            catch (ClassNotFoundException e) {
            	LOG.warn("Unable to load Data Object Class class: " + lookupForm.getDataObjectClassName(), e);
                super.checkAuthorization(lookupForm, methodToCall);
            }
        }
    }

	@RequestMapping(params = "methodToCall=start")
	public ModelAndView start(@ModelAttribute("KualiForm") LookupForm lookupForm, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
//		checkAuthorization(lookupForm, request.getParameter("methodToCall"));
	    supressActionsIfNeeded(lookupForm);
		return getUIFModelAndView(lookupForm);
	}

    /**
     * Just returns as if return with no value was selected.
     */
	@Override
    @RequestMapping(params = "methodToCall=cancel")
	public ModelAndView cancel(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
	    LookupForm lookupForm = (LookupForm)form;
	    supressActionsIfNeeded(lookupForm);

	    Properties props = new Properties();
    	props.put(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.REFRESH);
    	if (StringUtils.isNotBlank(lookupForm.getReturnFormKey())) {
            props.put(UifParameters.FORM_KEY, lookupForm.getReturnFormKey());
        }
    	if (StringUtils.isNotBlank(lookupForm.getDocNum())) {
        	props.put(UifParameters.DOC_NUM, lookupForm.getDocNum());
    	}
    	return performRedirect(lookupForm, lookupForm.getReturnLocation(), props);
    }

    /**
     * clearValues - clears the values of all the fields on the jsp.
     */
    @RequestMapping(params = "methodToCall=clearValues")
	public ModelAndView clearValues(@ModelAttribute("KualiForm") LookupForm lookupForm, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
//        LookupViewHelperService lookupViewHelperService = lookupForm.getLookupViewHelperService();
	    supressActionsIfNeeded(lookupForm);
        LookupViewHelperService lookupViewHelperService = (LookupViewHelperService) lookupForm.getView().getViewHelperService();
        lookupForm.setCriteriaFields(lookupViewHelperService.performClear(lookupForm.getCriteriaFieldsForLookup()));
		return getUIFModelAndView(lookupForm);
    }

    /**
     * search - sets the values of the data entered on the form on the jsp into a map and then searches for the results.
     */
	@RequestMapping(params = "methodToCall=search")
	public ModelAndView search(@ModelAttribute("KualiForm") LookupForm lookupForm, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
	    supressActionsIfNeeded(lookupForm);
		GlobalVariables.getUserSession().removeObjectsByPrefix(KNSConstants.SEARCH_METHOD);

//        LookupViewHelperService lookupViewHelperService = lookupForm.getLookupViewHelperService();
        LookupViewHelperService lookupViewHelperService = (LookupViewHelperService) lookupForm.getView().getViewHelperService();
        if (lookupViewHelperService == null) {
            LOG.error("LookupViewHelperService is null.");
            throw new RuntimeException("LookupViewHelperService is null.");
        }

        // validate search parameters
        List<? extends Component> criteriaComponents = ((LookupView) lookupForm.getView()).getCriteriaGroup().getItems();
        lookupViewHelperService.validateSearchParameters(criteriaComponents, lookupForm.getCriteriaFields());

        Collection<?> displayList = lookupViewHelperService.performSearch(lookupForm.getCriteriaFieldsForLookup(), true);

        if ( displayList instanceof CollectionIncomplete<?> ){
            request.setAttribute("reqSearchResultsActualSize", ((CollectionIncomplete<?>) displayList).getActualSizeIfTruncated());
        } else {
            request.setAttribute("reqSearchResultsActualSize", new Integer(displayList.size()) );
        }

        lookupForm.setSearchResults(displayList);

        return getUIFModelAndView(lookupForm);
    }

}
