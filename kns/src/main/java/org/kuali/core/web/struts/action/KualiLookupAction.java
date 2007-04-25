/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.web.struts.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.lookup.Lookupable;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.ResultRow;
import org.kuali.core.web.ui.Row;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class handles Actions for lookup flow
 * 
 * 
 */

public class KualiLookupAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiLookupAction.class);

    protected void checkAuthorization( ActionForm form, String methodToCall ) throws AuthorizationException {
        if ( !(form instanceof LookupForm) ) {
            super.checkAuthorization(form, methodToCall);
        } else {
            try {
                Class businessObjectClass = Class.forName( ((LookupForm)form).getBusinessObjectClassName() );
                AuthorizationType lookupAuthorizationType = new AuthorizationType.Lookup(businessObjectClass);
                // check if the lookup is allowed
                if ( !getKualiModuleService().isAuthorized( GlobalVariables.getUserSession().getUniversalUser(), lookupAuthorizationType ) ) {                
                    LOG.error("User not authorized for lookup action for this object: " + businessObjectClass.getName() );
                    throw new ModuleAuthorizationException( GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), lookupAuthorizationType, getKualiModuleService().getResponsibleModule( businessObjectClass ) );
                }
            } catch ( ClassNotFoundException ex ) {
                super.checkAuthorization(form, methodToCall);
            }
        }
    }

    /**
     * Checks if the user can create a document for this business object.  Used to suppress the actions on the results.
     * 
     * @param form
     * @return
     * @throws ClassNotFoundException
     */
    protected void supressActionsIfNeeded( ActionForm form ) throws ClassNotFoundException {
        if ((form instanceof LookupForm) && ( ((LookupForm)form).getBusinessObjectClassName() != null )) {
            Class businessObjectClass = Class.forName( ((LookupForm)form).getBusinessObjectClassName() );
            // check if creating documents is allowed
            if ( !KNSServiceLocator.getKualiModuleService().isAuthorized( GlobalVariables.getUserSession().getUniversalUser(), new AuthorizationType.Document(businessObjectClass, null) ) ) {
                ((LookupForm)form).setSuppressActions( true );
            }
        }
    }
    
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(Constants.PARAM_MAINTENANCE_VIEW_MODE, Constants.PARAM_MAINTENANCE_VIEW_MODE_LOOKUP);
        supressActionsIfNeeded(form);
        return super.execute(mapping, form, request, response);
    }

    /**
     * Entry point to lookups, forwards to jsp for search render.
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * search - sets the values of the data entered on the form on the jsp into a map and then searches for the results.
     */
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable kualiLookupable = lookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Collection displayList = new ArrayList();
        List<ResultRow> resultTable = new ArrayList<ResultRow>();

        // validate search parameters
        kualiLookupable.validateSearchParameters(lookupForm.getFields());

        boolean bounded = true;

        displayList = kualiLookupable.performLookup(lookupForm, resultTable, bounded);

        if (kualiLookupable.isSearchUsingOnlyPrimaryKeyValues()) {
            lookupForm.setSearchUsingOnlyPrimaryKeyValues(true);
            lookupForm.setPrimaryKeyFieldLabels(kualiLookupable.getPrimaryKeyFieldLabels());
        }
        else {
            lookupForm.setSearchUsingOnlyPrimaryKeyValues(false);
            lookupForm.setPrimaryKeyFieldLabels(Constants.EMPTY_STRING);
        }
        
        if ( displayList instanceof CollectionIncomplete ) {
            request.setAttribute("reqSearchResultsActualSize", ((CollectionIncomplete) displayList).getActualSizeIfTruncated());
        } else {
            request.setAttribute("reqSearchResultsActualSize", displayList.size() );
        }
        request.setAttribute("reqSearchResults", resultTable);
        
        if (request.getParameter(Constants.SEARCH_LIST_REQUEST_KEY) != null) {
            GlobalVariables.getUserSession().removeObject(request.getParameter(Constants.SEARCH_LIST_REQUEST_KEY));
        }
        request.setAttribute(Constants.SEARCH_LIST_REQUEST_KEY, GlobalVariables.getUserSession().addObject(resultTable, Constants.SEARCH_LIST_KEY_PREFIX));

        String refreshCaller = request.getParameter(Constants.REFRESH_CALLER);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }


    /**
     * refresh - is called when one quickFinder returns to the previous one. Sets all the values and performs the new search.
     */
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable kualiLookupable = lookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Map fieldValues = new HashMap();
        Map values = lookupForm.getFields();

        for (Iterator iter = kualiLookupable.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();

            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();

                if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
                    if (request.getParameter(field.getPropertyName()) != null) {
                        field.setPropertyValue(request.getParameter(field.getPropertyName()));
                    }
                    else if (values.get(field.getPropertyName()) != null) {
                        field.setPropertyValue(values.get(field.getPropertyName()));
                    }
                }
                fieldValues.put(field.getPropertyName(), field.getPropertyValue());
            }
        }
        fieldValues.put("docFormKey", lookupForm.getFormKey());
        fieldValues.put("backLocation", lookupForm.getBackLocation());

        if (kualiLookupable.checkForAdditionalFields(fieldValues)) {
            for (Iterator iter = kualiLookupable.getRows().iterator(); iter.hasNext();) {
                Row row = (Row) iter.next();
                for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                    Field field = (Field) iterator.next();
                    if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
                        if (request.getParameter(field.getPropertyName()) != null) {
                            field.setPropertyValue(request.getParameter(field.getPropertyName()));
                            fieldValues.put(field.getPropertyName(), request.getParameter(field.getPropertyName()));
                        }
                        else if (values.get(field.getPropertyName()) != null) {
                            field.setPropertyValue(values.get(field.getPropertyName()));
                        }
                    }
                }
            }
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Just returns as if return with no value was selected.
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;

        String backUrl = lookupForm.getBackLocation() + "?methodToCall=refresh&docFormKey=" + lookupForm.getFormKey();
        return new ActionForward(backUrl, true);
    }


    /**
     * clearValues - clears the values of all the fields on the jsp.
     */
    public ActionForward clearValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable kualiLookupable = lookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        for (Iterator iter = kualiLookupable.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();
            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (!field.getFieldType().equals(Field.RADIO)) {
                    field.setPropertyValue(field.getDefaultValue());
                }
            }
        }


        return mapping.findForward(Constants.MAPPING_BASIC);
    }


    public ActionForward viewResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;
        if (lookupForm.isSearchUsingOnlyPrimaryKeyValues()) {
            lookupForm.setPrimaryKeyFieldLabels(lookupForm.getLookupable().getPrimaryKeyFieldLabels());
        }
        request.setAttribute(Constants.SEARCH_LIST_REQUEST_KEY, request.getParameter(Constants.SEARCH_LIST_REQUEST_KEY));
        request.setAttribute("reqSearchResults", GlobalVariables.getUserSession().retrieveObject(request.getParameter(Constants.SEARCH_LIST_REQUEST_KEY)));
        request.setAttribute("reqSearchResultsActualSize", request.getParameter("reqSearchResultsActualSize"));
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
}