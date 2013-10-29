/**
 * Copyright 2005-2013 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.bo.Exporter;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.service.DataDictionaryService;
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
import org.kuali.rice.krad.web.form.UifFormManager;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @RequestMapping(params = "methodToCall=start")
    @Override
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

        return performRedirect(lookupForm, lookupForm.getReturnLocation(), props);
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
     * Handles exporting lookup results as xml using a custom xml exporter.
     */
    @Override
    protected String retrieveTableData(@ModelAttribute(UifConstants.KUALI_FORM_ATTR) UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        LookupForm lookupForm = (LookupForm) form;

        String formatType = getValidatedFormatType(request.getParameter(UifParameters.FORMAT_TYPE));

        // locate session form and its data object entry
//        UifFormManager uifFormManager = (UifFormManager) request.getSession().getAttribute(UifParameters.FORM_MANAGER);
//        String formKey = request.getParameter(UifParameters.FORM_KEY);
//        LookupForm currentForm = (LookupForm) uifFormManager.getSessionForm(formKey);

        // if it has a valid custom exporter, use the lookup results and the custom exporter
        DataDictionaryService dictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        DataDictionary dictionary = dictionaryService.getDataDictionary();

        String dataObjectClassName = lookupForm.getDataObjectClassName();
        DataObjectEntry dataObjectEntry = dictionary.getDataObjectEntry(dataObjectClassName);

        Class<? extends Exporter> exporterClass = dataObjectEntry.getExporterClass();

        // checks for custom xml formatting before using standard approach
        if (exporterClass != null && KRADConstants.XML_FORMAT.equals(formatType)) {
            try {
                List<? extends Object> displayList = (List<? extends Object>) lookupForm.getLookupResults();

                setAttachmentResponseHeader(response, UifConstants.EXPORT_FILE_NAME, KRADConstants.XML_MIME_TYPE);

                Exporter exporter = exporterClass.newInstance();
                exporter.export(dataObjectEntry.getDataObjectClass(), displayList, KRADConstants.XML_FORMAT,
                        response.getOutputStream());
            } catch (Exception e) {
                LOG.error("Unable to process xml export", e);
                throw new RuntimeException("Unable to process xml export", e);
            }

        } else {
            // otherwise use standard export
            return super.retrieveTableData(form, result, request, response);
        }

        // return null as custom export writes to response output stream
        return null;
    }

    /**
     * Invoked from the UI to mark values from all pages as selected. Copies the value from the lookupResults to
     * selectedLookupResultsCache
     *
     * @param lookupForm lookup form instance containing the selected results and lookup configuration
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
     * Retrieve a page defined by the page number parameter for a collection
     *
     * @param form -  Holds properties necessary to determine the <code>View</code> instance that will be used to
     * render
     * the UI
     * @param result -   represents binding results
     * @param request - http servlet request data
     * @param response - http servlet response object
     * @return the  ModelAndView object
     * @throws Exception
     */
    @RequestMapping(params = "methodToCall=retrieveCollectionPage")
    @Override
    public ModelAndView retrieveCollectionPage(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        LookupUtils.refreshLookupResultSelections((LookupForm) form);
        return super.retrieveCollectionPage(form,result,request,response);
    }


    /**
     * Get method for getting aaData for jquery datatables which are using sAjaxSource option.
     *
     * <p>This will render the aaData JSON for the displayed page of the table matching the tableId passed in the
     * request parameters.</p>
     */
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
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=returnSelected")
    public String returnSelected(@ModelAttribute(UifConstants.KUALI_FORM_ATTR) LookupForm lookupForm,
            HttpServletRequest request, final RedirectAttributes redirectAttributes) {

        LookupUtils.refreshLookupResultSelections((LookupForm) lookupForm);

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

        redirectAttributes.addAttribute(KRADConstants.REFRESH_CALLER, lookupForm.getActiveView().getId());
        redirectAttributes.addAttribute(KRADConstants.REFRESH_CALLER_TYPE,
                UifConstants.RefreshCallerTypes.MULTI_VALUE_LOOKUP);
        redirectAttributes.addAttribute(KRADConstants.REFRESH_DATA_OBJECT_CLASS, lookupForm.getDataObjectClassName());

        if (StringUtils.isNotBlank(lookupForm.getQuickfinderId())) {
            redirectAttributes.addAttribute(UifParameters.QUICKFINDER_ID, lookupForm.getQuickfinderId());
        }

        if (StringUtils.isNotBlank(lookupForm.getLookupCollectionName())) {
            redirectAttributes.addAttribute(UifParameters.LOOKUP_COLLECTION_NAME, lookupForm.getLookupCollectionName());
        }

        if (StringUtils.isNotBlank(lookupForm.getReferencesToRefresh())) {
            redirectAttributes.addAttribute(KRADConstants.REFERENCES_TO_REFRESH, lookupForm.getReferencesToRefresh());
        }

        // clear current form from session
        GlobalVariables.getUifFormManager().removeSessionForm(lookupForm);

        return UifConstants.REDIRECT_PREFIX + lookupForm.getReturnLocation();
    }

}
