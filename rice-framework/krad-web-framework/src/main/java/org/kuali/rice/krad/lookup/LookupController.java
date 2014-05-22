/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.UrlFactory;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Controller that handles requests for a {@link LookupView}.
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

    /**
     * Invoked to render an lookup view for a data object class.
     *
     * <p>
     * Checks if the data object is externalizable and we need to redirect to the appropriate lookup URL, else
     * continues with the lookup view display
     * </p>
     */
    @Override
    @RequestMapping(params = "methodToCall=start")
    public ModelAndView start(@ModelAttribute(UifConstants.KUALI_FORM_ATTR) UifFormBase form, HttpServletRequest request,
            HttpServletResponse response) {
        LookupForm lookupForm = (LookupForm) form;

        Lookupable lookupable = lookupForm.getLookupable();
        if (lookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        if (request.getParameter(UifParameters.MESSAGE_TO_DISPLAY) != null) {
            GlobalVariables.getMessageMap().putErrorForSectionId(UifConstants.MessageKeys.LOOKUP_RESULT_MESSAGES,
                    request.getParameter(UifParameters.MESSAGE_TO_DISPLAY));
        }

        // if request is not a redirect, determine if we need to redirect for an externalizable object lookup
        if (!lookupForm.isRedirectedLookup()) {
            Class<?> lookupObjectClass;
            try {
                lookupObjectClass = Class.forName(lookupForm.getDataObjectClassName());
            } catch (ClassNotFoundException e) {
                throw new RiceRuntimeException("Unable to get class for name: " + lookupForm.getDataObjectClassName(),
                        e);
            }

            ModuleService responsibleModuleService =
                    KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(lookupObjectClass);
            if (responsibleModuleService != null && responsibleModuleService.isExternalizable(lookupObjectClass)) {
                String lookupUrl = responsibleModuleService.getExternalizableDataObjectLookupUrl(lookupObjectClass,
                        KRADUtils.convertRequestMapToProperties(request.getParameterMap()));

                Properties redirectUrlProps = new Properties();
                redirectUrlProps.put(UifParameters.REDIRECTED_LOOKUP, "true");

                // clear current form from session
                GlobalVariables.getUifFormManager().removeSessionForm(form);

                return performRedirect(form, lookupUrl, redirectUrlProps);
            }
        }

        return super.start(lookupForm, request, response);
    }

    /**
     * Performs the search action using the given lookup criteria and sets the results onto the lookup form, then
     * renders the same lookup view.
     */
    @RequestMapping(params = "methodToCall=search")
    public ModelAndView search(@ModelAttribute(UifConstants.KUALI_FORM_ATTR) LookupForm lookupForm) {
        Lookupable lookupable = lookupForm.getLookupable();
        if (lookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Collection<?> displayList = lookupable.performSearch(lookupForm, lookupForm.getLookupCriteria(), true);

        lookupForm.setLookupResults(displayList);

        return getUIFModelAndView(lookupForm);
    }

    /**
     * Cancels the lookup request and returns with no value selected.
     */
    @Override
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=cancel")
    public ModelAndView cancel(@ModelAttribute(UifConstants.KUALI_FORM_ATTR) UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        LookupForm lookupForm = (LookupForm) form;

        Lookupable lookupable = lookupForm.getLookupable();
        if (lookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Properties props = new Properties();
        props.put(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.REFRESH);

        if (StringUtils.isNotBlank(lookupForm.getReturnFormKey())) {
            props.put(UifParameters.FORM_KEY, lookupForm.getReturnFormKey());
        }

        // clear current form from session
        GlobalVariables.getUifFormManager().removeSessionForm(form);

        return returnToHub(form);
    }

    /**
     * Resets values in the lookup criteria group to their initial default values.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=clearValues")
    public ModelAndView clearValues(@ModelAttribute(UifConstants.KUALI_FORM_ATTR) LookupForm lookupForm) {

        Lookupable lookupable = lookupForm.getLookupable();
        if (lookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Map<String, String> resetLookupCriteria = lookupable.performClear(lookupForm, lookupForm.getLookupCriteria());

        lookupForm.setLookupCriteria(resetLookupCriteria);

        return getUIFModelAndView(lookupForm);
    }

    /**
     * Invoked from the UI to mark values from all pages as selected. Copies the value from the lookupResults to
     * selectedLookupResultsCache
     *
     * @param lookupForm lookup form instance containing the selected results and lookup configuration
     * @param request servlet request
     * @param redirectAttributes redirect attributes instance
     * 
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=selectAllPages")
    public ModelAndView selectAllPages(@ModelAttribute(UifConstants.KUALI_FORM_ATTR) LookupForm lookupForm,
            HttpServletRequest request, final RedirectAttributes redirectAttributes) {

        List<? extends Object> lookupResults = (List<? extends Object>) lookupForm.getLookupResults();

        Map<String, String> fieldConversions = lookupForm.getFieldConversions();
        List<String> fromFieldNames = new ArrayList<String>(fieldConversions.keySet());

        // Loop through  the lookup results and store identifiers for all items in the set
        Set<String> selectedValues = new HashSet<String>();
        for(Object lineItem : lookupResults) {
            String lineIdentifier = LookupUtils.generateMultiValueKey(lineItem, fromFieldNames);
            selectedValues.add(lineIdentifier);
        }

        lookupForm.setSelectedLookupResultsCache(selectedValues);

        return getUIFModelAndView(lookupForm);
    }

    /**
     * Invoked from the UI to mark values from all pages as deselected. Clears the selectedLookupResultsCache
     *
     * @param lookupForm lookup form instance containing the selected results and lookup configuration
     * @param request servlet request
     * @param redirectAttributes redirect attributes instance
     * 
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=deselectAllPages")
    public ModelAndView deselectAllPages(@ModelAttribute(UifConstants.KUALI_FORM_ATTR) LookupForm lookupForm,
            HttpServletRequest request, final RedirectAttributes redirectAttributes) {

        lookupForm.getSelectedLookupResultsCache().clear();
        Set<String> selectedLines = lookupForm.getSelectedCollectionLines().get(UifPropertyPaths.LOOKUP_RESULTS);
        if (selectedLines != null) {
            selectedLines.clear();
        }

        return getUIFModelAndView(lookupForm);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @RequestMapping(params = "methodToCall=retrieveCollectionPage")
    public ModelAndView retrieveCollectionPage(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupUtils.refreshLookupResultSelections((LookupForm) form);

        return super.retrieveCollectionPage(form,result,request,response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=tableJsonRetrieval")
    public ModelAndView tableJsonRetrieval(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        LookupUtils.refreshLookupResultSelections((LookupForm) form);

        return super.tableJsonRetrieval(form, result, request, response);
    }

    /**
     * Invoked from the UI to return the selected lookup results lines, parameters are collected to build a URL to
     * the caller and then a redirect is performed.
     *
     * @param lookupForm lookup form instance containing the selected results and lookup configuration
     * @param request servlet request
     * @param redirectAttributes redirect attributes instance
     * @return redirect URL for the return location
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=returnSelected")
    public String returnSelected(@ModelAttribute(UifConstants.KUALI_FORM_ATTR) LookupForm lookupForm,
            HttpServletRequest request, final RedirectAttributes redirectAttributes) {

        LookupUtils.refreshLookupResultSelections((LookupForm) lookupForm);

        // build string of select line fields
        String multiValueReturnFieldsParam = "";
        List<String> multiValueReturnFields = lookupForm.getMultiValueReturnFields();
        Collections.sort(multiValueReturnFields);
        if (multiValueReturnFields != null && !multiValueReturnFields.isEmpty()) {
            for (String field : multiValueReturnFields) {
                multiValueReturnFieldsParam += field + ",";
            }
            multiValueReturnFieldsParam = StringUtils.removeEnd(multiValueReturnFieldsParam, ",");
        }

        // build string of select line identifiers
        String selectedLineValues = "";
        Set<String> selectedLines = lookupForm.getSelectedCollectionLines().get(UifPropertyPaths.LOOKUP_RESULTS);
        if (selectedLines != null) {
            for (String selectedLine : selectedLines) {
                selectedLineValues += selectedLine + ",";
            }
            selectedLineValues = StringUtils.removeEnd(selectedLineValues, ",");
        }

        Properties parameters = new Properties();
        parameters.put(UifParameters.SELECTED_LINE_VALUES, selectedLineValues);
        parameters.putAll(lookupForm.getInitialRequestParameters());

        String redirectUrl = UrlFactory.parameterizeUrl(lookupForm.getReturnLocation(), parameters);

        boolean lookupCameFromDifferentServer = KRADUtils.areDifferentDomains(lookupForm.getReturnLocation(),
                lookupForm.getRequestUrl());

        if (StringUtils.isNotBlank(multiValueReturnFieldsParam)) {
            redirectAttributes.addAttribute(UifParameters.MULIT_VALUE_RETURN_FILEDS, multiValueReturnFieldsParam);
        }

        if (redirectUrl.length() > RiceConstants.MAXIMUM_URL_LENGTH && !lookupCameFromDifferentServer) {
            redirectAttributes.addFlashAttribute(UifParameters.SELECTED_LINE_VALUES, selectedLineValues);
        }

        if (redirectUrl.length() > RiceConstants.MAXIMUM_URL_LENGTH && lookupCameFromDifferentServer) {
            Map<String, String[]> parms = lookupForm.getInitialRequestParameters();
            parms.remove(UifParameters.RETURN_FORM_KEY);

            //add an error message to display to the user
            redirectAttributes.mergeAttributes(parms);
            redirectAttributes.addAttribute(UifParameters.MESSAGE_TO_DISPLAY,
                    RiceKeyConstants.INFO_LOOKUP_RESULTS_MV_RETURN_EXCEEDS_LIMIT);

            String formKeyParam = request.getParameter(UifParameters.FORM_KEY);
            redirectAttributes.addAttribute(UifParameters.FORM_KEY, formKeyParam);

            return UifConstants.REDIRECT_PREFIX + lookupForm.getRequestUrl();
        }

        if (redirectUrl.length() < RiceConstants.MAXIMUM_URL_LENGTH) {
            redirectAttributes.addAttribute(UifParameters.SELECTED_LINE_VALUES, selectedLineValues);
        }

        redirectAttributes.addAttribute(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.RETURN_METHOD_TO_CALL);

        if (StringUtils.isNotBlank(lookupForm.getReturnFormKey())) {
            redirectAttributes.addAttribute(UifParameters.FORM_KEY, lookupForm.getReturnFormKey());
        }

        redirectAttributes.addAttribute(KRADConstants.REFRESH_CALLER, lookupForm.getView().getId());
        redirectAttributes.addAttribute(KRADConstants.REFRESH_CALLER_TYPE,
                UifConstants.RefreshCallerTypes.MULTI_VALUE_LOOKUP);
        redirectAttributes.addAttribute(KRADConstants.REFRESH_DATA_OBJECT_CLASS, lookupForm.getDataObjectClassName());

        if (StringUtils.isNotBlank(lookupForm.getQuickfinderId())) {
            redirectAttributes.addAttribute(UifParameters.QUICKFINDER_ID, lookupForm.getQuickfinderId());
        }

        if (StringUtils.isNotBlank(lookupForm.getLookupCollectionName())) {
            redirectAttributes.addAttribute(UifParameters.LOOKUP_COLLECTION_NAME, lookupForm.getLookupCollectionName());
        }

        if (StringUtils.isNotBlank(lookupForm.getLookupCollectionId())) {
            redirectAttributes.addAttribute(UifParameters.LOOKUP_COLLECTION_ID, lookupForm.getLookupCollectionId());
        }

        if (StringUtils.isNotBlank(lookupForm.getReferencesToRefresh())) {
            redirectAttributes.addAttribute(KRADConstants.REFERENCES_TO_REFRESH, lookupForm.getReferencesToRefresh());
        }

        // clear current form from session
        GlobalVariables.getUifFormManager().removeSessionForm(lookupForm);

        return UifConstants.REDIRECT_PREFIX + lookupForm.getReturnLocation();
    }

}
