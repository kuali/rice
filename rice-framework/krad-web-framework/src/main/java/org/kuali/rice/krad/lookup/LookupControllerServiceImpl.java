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
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.ModelAndViewService;
import org.kuali.rice.krad.web.service.impl.ControllerServiceImpl;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Default implementation of the lookup controller service.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LookupControllerServiceImpl extends ControllerServiceImpl implements LookupControllerService {

    private ModelAndViewService modelAndViewService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView start(UifFormBase form) {
        LookupForm lookupForm = (LookupForm) form;

        Lookupable lookupable = lookupForm.getLookupable();
        if (lookupable == null) {
            throw new RuntimeException("Lookupable is null");
        }

        HttpServletRequest request = form.getRequest();
        if (request.getParameter(UifParameters.MESSAGE_TO_DISPLAY) != null) {
            GlobalVariables.getMessageMap().putErrorForSectionId(UifConstants.MessageKeys.LOOKUP_RESULT_MESSAGES,
                    request.getParameter(UifParameters.MESSAGE_TO_DISPLAY));
        }

        if (!lookupForm.isRedirectedLookup()) {
            ModelAndView redirectModelAndView = checkForModuleLookupRedirect(lookupForm, request);
            if (redirectModelAndView != null) {
                return redirectModelAndView;
            }
        }

        return super.start(lookupForm);
    }

    /**
     * Checks for a module service that claims the lookup class as an EBO, and if found redirects to the URL
     * given by the module service.
     *
     * @param lookupForm form instance containing the lookup data
     * @param request http request being handled
     * @return ModelAndView instance for redirecting to the lookup, or null if a redirect is not needed
     */
    protected ModelAndView checkForModuleLookupRedirect(LookupForm lookupForm, HttpServletRequest request) {
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
            redirectUrlProps.setProperty(UifParameters.REDIRECTED_LOOKUP, "true");

            // clear current form from session
            GlobalVariables.getUifFormManager().removeSessionForm(lookupForm);

            return getModelAndViewService().performRedirect(lookupForm, lookupUrl, redirectUrlProps);
        }

        return null;
    }

    /**
     * Carries out the search action by invoking the {@link Lookupable#performSearch)} method on the
     * configured lookupable (view helper) and then setting the results onto the given form.
     *
     * {@inheritDoc}
     */
    @Override
    public ModelAndView search(LookupForm lookupForm) {
        Lookupable lookupable = lookupForm.getLookupable();
        if (lookupable == null) {
            throw new RuntimeException("Lookupable is null.");
        }

        Collection<?> displayList = lookupable.performSearch(lookupForm, lookupForm.getLookupCriteria(), true);

        lookupForm.setLookupResults(displayList);

        return getModelAndViewService().getModelAndView(lookupForm);
    }

    /**
     * Carries out the clear values action by invoking the {@link Lookupable#performClear)} method on the
     * configured lookupable (view helper) and then setting the cleared criteria onto the given form.
     *
     * {@inheritDoc}
     */
    @Override
    public ModelAndView clearValues(LookupForm lookupForm) {
        Lookupable lookupable = lookupForm.getLookupable();
        if (lookupable == null) {
            throw new RuntimeException("Lookupable is null.");
        }

        Map<String, String> resetLookupCriteria = lookupable.performClear(lookupForm, lookupForm.getLookupCriteria());

        lookupForm.setLookupCriteria(resetLookupCriteria);

        return getModelAndViewService().getModelAndView(lookupForm);
    }

    /**
     * Loops through all the lookup results generating the line identifier for each and adding the
     * resulting set of identifies to the form property
     * {@link org.kuali.rice.krad.web.form.UifFormBase#getSelectedLookupResultsCache()}.
     *
     * {@inheritDoc}
     */
    @Override
    public ModelAndView selectAllPages(LookupForm lookupForm) {
        List<? extends Object> lookupResults = (List<? extends Object>) lookupForm.getLookupResults();

        List<String> fromFieldNames = new ArrayList<String>(lookupForm.getFieldConversions().keySet());

        // loop through  the lookup results and store identifiers for all items in the set
        Set<String> selectedValues = new HashSet<String>();
        for (Object lineItem : lookupResults) {
            String lineIdentifier = LookupUtils.generateMultiValueKey(lineItem, fromFieldNames);

            selectedValues.add(lineIdentifier);
        }

        lookupForm.setSelectedLookupResultsCache(selectedValues);

        return getModelAndViewService().getModelAndView(lookupForm);
    }

    /**
     * Clears the form property {@link org.kuali.rice.krad.web.form.UifFormBase#getSelectedLookupResultsCache()}
     * and the selected lines property.
     *
     * {@inheritDoc}
     */
    @Override
    public ModelAndView deselectAllPages(LookupForm lookupForm) {
        lookupForm.getSelectedLookupResultsCache().clear();

        Set<String> selectedLines = lookupForm.getSelectedCollectionLines().get(UifPropertyPaths.LOOKUP_RESULTS);
        if (selectedLines != null) {
            selectedLines.clear();
        }

        return getModelAndViewService().getModelAndView(lookupForm);
    }

    /**
     * Builds the URL for returning back to the calling view and passing the selected line values.
     *
     * <p>We attempt to pass back all the selected line identifiers as a request parameter on the return URL.
     * However, this could result in an URL longer than the max length supported by browsers (the most restrictive
     * is used). If this happens, for local lookups we use Spring flash attributes. In the case of a remote
     * lookup, there is nothing we can do and return an error message.</p>
     *
     * {@inheritDoc}
     */
    @Override
    public String returnSelected(LookupForm lookupForm, RedirectAttributes redirectAttributes) {
        LookupUtils.refreshLookupResultSelections(lookupForm);

        Properties urlParams = buildReturnSelectedParameters(lookupForm);
        String returnUrl = UrlFactory.parameterizeUrl(lookupForm.getReturnLocation(), urlParams);

        boolean lookupCameFromDifferentServer = KRADUtils.areDifferentDomains(lookupForm.getReturnLocation(),
                lookupForm.getRequestUrl());

        boolean urlGreaterThanMaxLength = returnUrl.length() > RiceConstants.MAXIMUM_URL_LENGTH;
        if (urlGreaterThanMaxLength) {
            // removed selected values parameter from the return url
            urlParams.remove(UifParameters.SELECTED_LINE_VALUES);

            // if lookup was on a different server, we can't return the selected lines and instead
            // will return an error message
            if (lookupCameFromDifferentServer) {
                urlParams.setProperty(UifParameters.REFRESH_STATUS, UifConstants.RefreshStatus.ERROR);
                urlParams.setProperty(UifParameters.MESSAGE_TO_DISPLAY,
                        RiceKeyConstants.INFO_LOOKUP_RESULTS_MV_RETURN_EXCEEDS_LIMIT);
            } else {
                // otherwise use flash attributes instead of the URL to return the selected line identifiers
                String selectedLineValues = getSelectedLineValues(lookupForm);
                redirectAttributes.addFlashAttribute(UifParameters.SELECTED_LINE_VALUES, selectedLineValues);
            }
        }

        GlobalVariables.getUifFormManager().removeSessionForm(lookupForm);

        // rebuild url based on updated parameters
        returnUrl = UrlFactory.parameterizeUrl(lookupForm.getReturnLocation(), urlParams);

        return UifConstants.REDIRECT_PREFIX + returnUrl;
    }

    /**
     * Builds all the request parameters for the return URL.
     *
     * @param lookupForm form instance containing the lookup data
     * @return Properties contains the request parameters key/value pairs
     */
    protected Properties buildReturnSelectedParameters(LookupForm lookupForm) {
        Properties parameters = new Properties();

        String multiValueReturnFieldsParam = getMultiValueReturnFields(lookupForm);
        parameters.setProperty(UifParameters.MULIT_VALUE_RETURN_FILEDS, multiValueReturnFieldsParam);

        String selectedLineValues = getSelectedLineValues(lookupForm);
        parameters.setProperty(UifParameters.SELECTED_LINE_VALUES, selectedLineValues);

        parameters.setProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.RETURN_METHOD_TO_CALL);
        parameters.setProperty(UifParameters.FORM_KEY, lookupForm.getReturnFormKey());
        parameters.setProperty(KRADConstants.REFRESH_CALLER, lookupForm.getView().getId());
        parameters.setProperty(KRADConstants.REFRESH_CALLER_TYPE, UifConstants.RefreshCallerTypes.MULTI_VALUE_LOOKUP);
        parameters.setProperty(KRADConstants.REFRESH_DATA_OBJECT_CLASS, lookupForm.getDataObjectClassName());

        if (StringUtils.isNotBlank(lookupForm.getQuickfinderId())) {
            parameters.setProperty(UifParameters.QUICKFINDER_ID, lookupForm.getQuickfinderId());
        }

        if (StringUtils.isNotBlank(lookupForm.getLookupCollectionName())) {
            parameters.setProperty(UifParameters.LOOKUP_COLLECTION_NAME, lookupForm.getLookupCollectionName());
        }

        if (StringUtils.isNotBlank(lookupForm.getLookupCollectionId())) {
            parameters.setProperty(UifParameters.LOOKUP_COLLECTION_ID, lookupForm.getLookupCollectionId());
        }

        if (StringUtils.isNotBlank(lookupForm.getReferencesToRefresh())) {
            parameters.setProperty(KRADConstants.REFERENCES_TO_REFRESH, lookupForm.getReferencesToRefresh());
        }

        return parameters;
    }

    /**
     * Builds a string containing the names of the fields being returned separated by a comma.
     *
     * @param lookupForm form instance containing the lookup data
     * @return String names of return fields separated by a comma
     */
    protected String getMultiValueReturnFields(LookupForm lookupForm) {
        String multiValueReturnFieldsParam = "";

        List<String> multiValueReturnFields = lookupForm.getMultiValueReturnFields();
        Collections.sort(multiValueReturnFields);
        if (multiValueReturnFields != null && !multiValueReturnFields.isEmpty()) {
            for (String field : multiValueReturnFields) {
                multiValueReturnFieldsParam += field + ",";
            }

            multiValueReturnFieldsParam = StringUtils.removeEnd(multiValueReturnFieldsParam, ",");
        }

        return multiValueReturnFieldsParam;
    }

    /**
     * Builds a string containing the selected line identifiers separated by a comma.
     *
     * @param lookupForm form instance containing the lookup data
     * @return String selected line identifiers separated by a comma
     */
    protected String getSelectedLineValues(LookupForm lookupForm) {
        String selectedLineValues = "";

        Set<String> selectedLines = lookupForm.getSelectedCollectionLines().get(UifPropertyPaths.LOOKUP_RESULTS);
        if (selectedLines != null) {
            for (String selectedLine : selectedLines) {
                selectedLineValues += selectedLine + ",";
            }

            selectedLineValues = StringUtils.removeEnd(selectedLineValues, ",");
        }

        return selectedLineValues;
    }

    /**
     * Instance of model and view service to use within the collection service.
     *
     * @return ModelAndViewService instance
     */
    protected ModelAndViewService getModelAndViewService() {
        return modelAndViewService;
    }

    /**
     * @see LookupControllerServiceImpl#getModelAndViewService()
     */
    public void setModelAndViewService(ModelAndViewService modelAndViewService) {
        this.modelAndViewService = modelAndViewService;
    }
}
