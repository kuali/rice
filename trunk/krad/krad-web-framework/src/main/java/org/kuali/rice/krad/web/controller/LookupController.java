/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.web.controller;

import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.lookup.Lookupable;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.LookupForm;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that handles requests coming from a <code>LookupView</code>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/lookup")
public class LookupController extends UifControllerBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupController.class);

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected LookupForm createInitialForm(HttpServletRequest request) {
        return new LookupForm();
    }

    protected void suppressActionsIfNeeded(LookupForm lookupForm) {
//        try {
//            // TODO; move to authorizer for lookup view
//            Class<?> dataObjectClass = Class.forName(lookupForm.getDataObjectClassName());
//            Person user = GlobalVariables.getUserSession().getPerson();
//            // check if creating documents is allowed
//            String documentTypeName = KRADServiceLocatorWeb.getDocumentDictionaryService()
//                    .getMaintenanceDocumentTypeName(dataObjectClass);
//            if ((documentTypeName != null) &&
//                    !KRADServiceLocatorWeb.getDocumentHelperService().getDocumentAuthorizer(documentTypeName)
//                            .canInitiate(documentTypeName, user)) {
//                ((LookupView) lookupForm.getView()).setSuppressActions(true);
//            }
//        } catch (ClassNotFoundException e) {
//            LOG.warn("Unable to load Data Object Class: " + lookupForm.getDataObjectClassName(), e);
//        }
    }

    /**
     * Invoked to request an lookup view for a data object class
     *
     * <p>
     * Checks if the data object is externalizable and we need to redirect to the appropriate lookup URL, else
     * continues with the lookup view display
     * </p>
     */
    @RequestMapping(params = "methodToCall=start")
    @Override
    public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        LookupForm lookupForm = (LookupForm) form;

        // if request is not a redirect, determine if we need to redirect for an externalizable object lookup
        if (!lookupForm.isRedirectedLookup()) {
            Class lookupObjectClass = null;
            try {
                lookupObjectClass = Class.forName(lookupForm.getDataObjectClassName());
            } catch (ClassNotFoundException e) {
                throw new RiceRuntimeException("Unable to get class for name: " + lookupForm.getDataObjectClassName());
            }

            ModuleService responsibleModuleService =
                    KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(lookupObjectClass);
            if (responsibleModuleService != null && responsibleModuleService.isExternalizable(lookupObjectClass)) {
                String lookupUrl = responsibleModuleService.getExternalizableDataObjectLookupUrl(lookupObjectClass,
                        KRADUtils.convertRequestMapToProperties(request.getParameterMap()));

                Properties redirectUrlProps = new Properties();
                redirectUrlProps.put(UifParameters.REDIRECTED_LOOKUP, "true");

                return performRedirect(form, lookupUrl, redirectUrlProps);
            }
        }

        return super.start(lookupForm, result, request, response);
    }

    /**
     * Just returns as if return with no value was selected
     */
    @Override
    @RequestMapping(params = "methodToCall=cancel")
    public ModelAndView cancel(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        LookupForm lookupForm = (LookupForm) form;
        suppressActionsIfNeeded(lookupForm);

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
    public ModelAndView clearValues(@ModelAttribute("KualiForm") LookupForm lookupForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        suppressActionsIfNeeded(lookupForm);

        Lookupable lookupable = (Lookupable) lookupForm.getLookupable();
        lookupForm.setCriteriaFields(lookupable.performClear(lookupForm, lookupForm.getCriteriaFields()));

        return getUIFModelAndView(lookupForm);
    }

    /**
     * search - sets the values of the data entered on the form on the jsp into a map and then searches for the
     * results.
     */
    @RequestMapping(params = "methodToCall=search")
    public ModelAndView search(@ModelAttribute("KualiForm") LookupForm lookupForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        suppressActionsIfNeeded(lookupForm);

        Lookupable lookupable = lookupForm.getLookupable();
        if (lookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        // validate search parameters
        lookupable.validateSearchParameters(lookupForm, lookupForm.getCriteriaFields());

        Collection<?> displayList =
                lookupable.performSearch(lookupForm, lookupForm.getCriteriaFields(), true);

        if (displayList instanceof CollectionIncomplete<?>) {
            request.setAttribute("reqSearchResultsActualSize",
                    ((CollectionIncomplete<?>) displayList).getActualSizeIfTruncated());
        } else {
            request.setAttribute("reqSearchResultsActualSize", new Integer(displayList.size()));
        }

        lookupForm.setSearchResults(displayList);

        return getUIFModelAndView(lookupForm);
    }

    /**
     * Invoked from the UI to return the selected lookup results lines, parameters are collected to build a URL to
     * the caller and then a redirect is performed
     *
     * @param lookupForm - lookup form instance containing the selected results and lookup configuration
     */
    @RequestMapping(params = "methodToCall=returnSelected")
    public ModelAndView returnSelected(@ModelAttribute("KualiForm") LookupForm lookupForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.RETURN_METHOD_TO_CALL);

        if (StringUtils.isNotBlank(lookupForm.getReturnFormKey())) {
            parameters.put(UifParameters.FORM_KEY, lookupForm.getReturnFormKey());
        }

        parameters.put(KRADConstants.REFRESH_CALLER, lookupForm.getView().getId());
        parameters.put(KRADConstants.REFRESH_CALLER_TYPE, UifConstants.RefreshCallerTypes.MULTI_VALUE_LOOKUP);
        parameters.put(KRADConstants.REFRESH_DATA_OBJECT_CLASS, lookupForm.getDataObjectClassName());

        if (StringUtils.isNotBlank(lookupForm.getDocNum())) {
            parameters.put(UifParameters.DOC_NUM, lookupForm.getDocNum());
        }

        if (StringUtils.isNotBlank(lookupForm.getLookupCollectionName())) {
            parameters.put(UifParameters.LOOKUP_COLLECTION_NAME, lookupForm.getLookupCollectionName());
        }

        if (StringUtils.isNotBlank(lookupForm.getReferencesToRefresh())) {
            parameters.put(KRADConstants.REFERENCES_TO_REFRESH, lookupForm.getReferencesToRefresh());
        }

        // build string of select line identifiers
        String selectedLineValues = "";
        Set<String> selectedLines = lookupForm.getSelectedCollectionLines().get(UifPropertyPaths.SEARCH_RESULTS);
        if (selectedLines != null) {
            for (String selectedLine : selectedLines) {
                selectedLineValues += selectedLine + ",";
            }
            selectedLineValues = StringUtils.removeEnd(selectedLineValues, ",");
        }

        parameters.put(UifParameters.SELECTED_LINE_VALUES, selectedLineValues);

        return performRedirect(lookupForm, lookupForm.getReturnLocation(), parameters);
    }
}
