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
package org.kuali.rice.krad.web.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.lookup.LookupUtils;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.field.AttributeQueryResult;
import org.kuali.rice.krad.uif.service.AttributeQueryService;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.ModelAndViewService;
import org.kuali.rice.krad.web.service.QueryControllerService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Default implementation of the query controller service.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class QueryControllerServiceImpl implements QueryControllerService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            QueryControllerServiceImpl.class);

    private ModelAndViewService modelAndViewService;
    private AttributeQueryService attributeQueryService;

    /**
     * Inspects the given request and action parameters on the form to build a URL to the requested
     * lookup view.
     *
     * <p>First the data object class for the lookup view is found from the action parameters. A call
     * is then made to check if there is a module service that handles that class, and if so the URL from the
     * module service is used. If not, the base url is and other lookup URL parameters are created from the
     * action parameters and form.</p>
     *
     * {@inheritDoc}
     *
     * @see QueryControllerServiceImpl#getLookupDataObjectClass(java.util.Properties)
     * @see QueryControllerServiceImpl#getLookupUrlFromModuleService(java.lang.Class<?>, java.util.Properties)
     * @see QueryControllerServiceImpl#buildLookupUrlParameters(org.kuali.rice.krad.web.form.UifFormBase,
     * javax.servlet.http.HttpServletRequest, java.lang.Class<?>, java.util.Properties)
     */
    @Override
    public ModelAndView performLookup(UifFormBase form) {
        Properties urlParameters = form.getActionParametersAsProperties();

        Class<?> lookupDataObjectClass = getLookupDataObjectClass(urlParameters);
        if (lookupDataObjectClass == null) {
            throw new RuntimeException("Lookup data object class not found for lookup call");
        }

        // Force skip of dirty check
        urlParameters.put(UifParameters.PERFORM_DIRTY_CHECK, "false");

        // first give module service the opportunity to build the lookup URL
        String baseLookupUrl = getLookupUrlFromModuleService(lookupDataObjectClass, urlParameters);
        if (StringUtils.isNotBlank(baseLookupUrl)) {
            // url fully built by module service
            urlParameters = new Properties();
        } else {
            baseLookupUrl = urlParameters.getProperty(UifParameters.BASE_LOOKUP_URL);
            urlParameters.remove(UifParameters.BASE_LOOKUP_URL);

            buildLookupUrlParameters(form, form.getRequest(), lookupDataObjectClass, urlParameters);
        }

        return getModelAndViewService().performRedirect(form, baseLookupUrl, urlParameters);
    }

    /**
     * Returns the Class instance for the data object whose lookup view was requested.
     *
     * @param urlParameters properties containing the lookup configuration
     * @return Class<?> lookup data object class
     * @throws java.lang.RuntimeException if class cannot be created from data object class name
     */
    protected Class<?> getLookupDataObjectClass(Properties urlParameters) {
        Class<?> lookupDataObjectClass;

        String lookupObjectClassName = urlParameters.getProperty(UifParameters.DATA_OBJECT_CLASS_NAME);
        try {
            lookupDataObjectClass = Class.forName(lookupObjectClassName);
        } catch (ClassNotFoundException e) {
            LOG.error("Unable to get class for name: " + lookupObjectClassName);
            throw new RuntimeException("Unable to get class for name: " + lookupObjectClassName, e);
        }

        return lookupDataObjectClass;
    }

    /**
     * Attempts to find a module service that claims responsibility for the given data object class and if
     * found invokes that module service to build the lookup url.
     *
     * @param lookupDataObjectClass data object class to find responsible module service for
     * @param urlParameters properties containing the lookup configuration
     * @return String lookup URL returned from module service, or null if not module service was found
     */
    protected String getLookupUrlFromModuleService(Class<?> lookupDataObjectClass, Properties urlParameters) {
        String lookupUrl = null;

        ModuleService responsibleModuleService =
                KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(lookupDataObjectClass);
        if (responsibleModuleService != null && responsibleModuleService.isExternalizable(lookupDataObjectClass)) {
            lookupUrl = responsibleModuleService.getExternalizableDataObjectLookupUrl(lookupDataObjectClass,
                    urlParameters);
        }

        return lookupUrl;
    }

    /**
     * Modifies the given properties object representing the lookup URL parameters to add additional parameters
     * based on the form and action parameters.
     *
     * @param form form instance containing the model data
     * @param request http request object being handled
     * @param lookupDataObjectClass data object class the lookup URL is being built for
     * @param urlParameters properties instance holding the lookup URL parameters
     */
    protected void buildLookupUrlParameters(UifFormBase form, HttpServletRequest request,
            Class<?> lookupDataObjectClass, Properties urlParameters) {
        urlParameters.setProperty(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.START);

        String autoSearchString = urlParameters.getProperty(UifParameters.AUTO_SEARCH);
        if (Boolean.parseBoolean(autoSearchString)) {
            urlParameters.setProperty(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.SEARCH);
        }

        buildLookupCriteriaParameters(form, request, lookupDataObjectClass, urlParameters);

        urlParameters.setProperty(UifParameters.RETURN_LOCATION, form.getFormPostUrl());
        urlParameters.setProperty(UifParameters.RETURN_FORM_KEY, form.getFormKey());       
    }

    /**
     * If lookup criteria parameters were configured, pulls the values for those parameters from the form and
     * passes as values to pre-populate the lookup view criteria.
     *
     * @param form form instance containing the model data
     * @param request http request object being handled
     * @param lookupDataObjectClass data object class the lookup URL is being built for
     * @param urlParameters properties instance holding the lookup URL parameters
     */
    protected void buildLookupCriteriaParameters(UifFormBase form, HttpServletRequest request,
            Class<?> lookupDataObjectClass, Properties urlParameters) {
        String lookupParameterString = urlParameters.getProperty(UifParameters.LOOKUP_PARAMETERS);
        if (StringUtils.isBlank(lookupParameterString)) {
            return;
        }

        Map<String, String> lookupParameterFields = KRADUtils.getMapFromParameterString(lookupParameterString);
        for (Map.Entry<String, String> lookupParameter : lookupParameterFields.entrySet()) {
            String lookupParameterValue = LookupUtils.retrieveLookupParameterValue(form, request, lookupDataObjectClass,
                    lookupParameter.getValue(), lookupParameter.getKey());

            if (StringUtils.isNotBlank(lookupParameterValue)) {
                urlParameters.setProperty(UifPropertyPaths.LOOKUP_CRITERIA + "['" + lookupParameter.getValue() + "']",
                        lookupParameterValue);
            }
        }

        urlParameters.remove(UifParameters.LOOKUP_PARAMETERS);
    }

    /**
     * Retrieves suggest query parameters from the request and invokes
     * {@link org.kuali.rice.krad.uif.service.AttributeQueryService#performFieldSuggestQuery} to carry out the
     * suggest query.
     *
     * {@inheritDoc}
     */
    @Override
    public AttributeQueryResult performFieldSuggest(UifFormBase form) {
        HttpServletRequest request = form.getRequest();

        // retrieve query fields from request
        Map<String, String> queryParameters = new HashMap<String, String>();
        for (Object parameterName : request.getParameterMap().keySet()) {
            if (parameterName.toString().startsWith(UifParameters.QUERY_PARAMETER + ".")) {
                String fieldName = StringUtils.substringAfter(parameterName.toString(),
                        UifParameters.QUERY_PARAMETER + ".");
                String fieldValue = request.getParameter(parameterName.toString());
                queryParameters.put(fieldName, fieldValue);
            }
        }

        // retrieve id for field to perform query for
        String queryFieldId = request.getParameter(UifParameters.QUERY_FIELD_ID);
        if (StringUtils.isBlank(queryFieldId)) {
            throw new RuntimeException("Unable to find id for field to perform query on under request parameter name: "
                    + UifParameters.QUERY_FIELD_ID);
        }

        // get the field term to match
        String queryTerm = request.getParameter(UifParameters.QUERY_TERM);
        if (StringUtils.isBlank(queryTerm)) {
            throw new RuntimeException(
                    "Unable to find id for query term value for attribute query on under request parameter name: "
                            + UifParameters.QUERY_TERM);
        }

        return getAttributeQueryService().performFieldSuggestQuery(form.getViewPostMetadata(), queryFieldId, queryTerm,
                queryParameters);
    }

    /**
     * Retrieves field query parameters from the request and invokes
     * {@link org.kuali.rice.krad.uif.service.AttributeQueryService#performFieldQuery} to carry out the
     * field query.
     *
     * {@inheritDoc}
     */
    @Override
    public AttributeQueryResult performFieldQuery(UifFormBase form) {
        HttpServletRequest request = form.getRequest();

        // retrieve query fields from request
        Map<String, String> queryParameters = new HashMap<String, String>();
        for (Object parameterName : request.getParameterMap().keySet()) {
            if (parameterName.toString().startsWith(UifParameters.QUERY_PARAMETER + ".")) {
                String fieldName = StringUtils.substringAfter(parameterName.toString(),
                        UifParameters.QUERY_PARAMETER + ".");
                String fieldValue = request.getParameter(parameterName.toString());
                queryParameters.put(fieldName, fieldValue);
            }
        }

        // retrieve id for field to perform query for
        String queryFieldId = request.getParameter(UifParameters.QUERY_FIELD_ID);
        if (StringUtils.isBlank(queryFieldId)) {
            throw new RuntimeException("Unable to find id for field to perform query on under request parameter name: "
                    + UifParameters.QUERY_FIELD_ID);
        }

        return getAttributeQueryService().performFieldQuery(form.getViewPostMetadata(), queryFieldId, queryParameters);
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
     * @see CollectionControllerServiceImpl#getModelAndViewService()
     */
    public void setModelAndViewService(ModelAndViewService modelAndViewService) {
        this.modelAndViewService = modelAndViewService;
    }

    public AttributeQueryService getAttributeQueryService() {
        return attributeQueryService;
    }

    public void setAttributeQueryService(AttributeQueryService attributeQueryService) {
        this.attributeQueryService = attributeQueryService;
    }
}
