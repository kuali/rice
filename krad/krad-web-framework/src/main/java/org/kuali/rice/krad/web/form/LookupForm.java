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
package org.kuali.rice.krad.web.form;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.lookup.LookupUtils;
import org.kuali.rice.krad.lookup.Lookupable;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.uif.view.LookupView;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Form class for <code>LookupView</code> screens
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LookupForm extends UifFormBase {
    private static final long serialVersionUID = -7323484966538685327L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupForm.class);

    private String dataObjectClassName;
    private String docNum;
    private String referencesToRefresh;

    private boolean multipleValuesSelect;
    private String lookupCollectionName;

    private Map<String, String> criteriaFields;
    private Map<String, String> fieldConversions;

    private boolean atLeastOneRowReturnable;
    private boolean atLeastOneRowHasActions;

    private Collection<?> searchResults;

    public LookupForm() {
        super();

        setViewTypeName(ViewType.LOOKUP);
        atLeastOneRowReturnable = false;
        atLeastOneRowHasActions = false;
        multipleValuesSelect = false;

        criteriaFields = new HashMap<String, String>();
        fieldConversions = new HashMap<String, String>();
    }

    /**
     * Picks out business object name from the request to get retrieve a
     * lookupable and set properties
     */
    @Override
    public void postBind(HttpServletRequest request) {
        super.postBind(request);

        try {
            Lookupable lookupable = getLookupable();
            if (lookupable == null) {
                LOG.error("Lookupable not found for view id " + getView().getId());
                throw new RuntimeException("Lookupable not found for view id " + getView().getId());
            }

            if (StringUtils.isBlank(getDataObjectClassName())) {
                setDataObjectClassName(((LookupView) getView()).getDataObjectClassName().getName());
            }

            // init lookupable with data object class
            Class<?> dataObjectClass = Class.forName(getDataObjectClassName());
            lookupable.setDataObjectClass(dataObjectClass);

            // if showMaintenanceLinks is not already true, only show maintenance links
            // if the lookup was called from the home application view
            if (!((LookupView) getView()).isShowMaintenanceLinks()) {
                // TODO replace with check to history
                if (StringUtils.contains(getReturnLocation(), "/" + KRADConstants.PORTAL_ACTION) ||
                        StringUtils.contains(getReturnLocation(), "/index.html")) {
                    ((LookupView) getView()).setShowMaintenanceLinks(true);
                }
            }

            // populate lookup read only fields list on lookupable
            lookupable.setReadOnlyFieldsList(getReadOnlyFieldsList());

            // populate field conversions list
            if (request.getParameter(KRADConstants.CONVERSION_FIELDS_PARAMETER) != null) {
                String conversionFields = request.getParameter(KRADConstants.CONVERSION_FIELDS_PARAMETER);
                setFieldConversions(KRADUtils.convertStringParameterToMap(conversionFields));
                lookupable.setFieldConversions(getFieldConversions());
            }

            // perform upper casing of lookup parameters
            Map<String, String> fieldValues = new HashMap<String, String>();
            Map<String, String> formFields = getCriteriaFields();

            if (formFields != null) {
                for (Map.Entry<String, String> entry : formFields.entrySet()) {
                    // check here to see if this field is a criteria element on the form
                    fieldValues.put(entry.getKey(),
                            LookupUtils.forceUppercase(dataObjectClass, entry.getKey(), entry.getValue()));
                }
            }

            // fieldValues.put(UifParameters.RETURN_FORM_KEY, getReturnFormKey());
            // fieldValues.put(UifParameters.RETURN_LOCATION, getReturnLocation());
            if (StringUtils.isNotBlank(getDocNum())) {
                fieldValues.put(KRADConstants.DOC_NUM, getDocNum());
            }

            this.setCriteriaFields(fieldValues);
        } catch (ClassNotFoundException e) {
            LOG.error("Object class " + getDataObjectClassName() + " not found");
            throw new RuntimeException("Object class " + getDataObjectClassName() + " not found", e);
        }
    }

    public Lookupable getLookupable() {
        ViewHelperService viewHelperService = getView().getViewHelperService();
        if (viewHelperService == null) {
            LOG.error("ViewHelperService is null.");
            throw new RuntimeException("ViewHelperService is null.");
        }

        if (!Lookupable.class.isAssignableFrom(viewHelperService.getClass())) {
            LOG.error("ViewHelperService class '" + viewHelperService.getClass().getName() +
                    "' is not assignable from '" + Lookupable.class + "'");
            throw new RuntimeException("ViewHelperService class '" + viewHelperService.getClass().getName() +
                    "' is not assignable from '" + Lookupable.class + "'");
        }

        return (Lookupable) viewHelperService;
    }

    protected Boolean processBooleanParameter(String parameterValue) {
        if (StringUtils.isNotBlank(parameterValue)) {
            if ("YES".equals(parameterValue.toUpperCase())) {
                return Boolean.TRUE;
            }
            return new Boolean(parameterValue);
        }
        return null;
    }

    public String getDataObjectClassName() {
        return this.dataObjectClassName;
    }

    public void setDataObjectClassName(String dataObjectClassName) {
        this.dataObjectClassName = dataObjectClassName;
    }

    public String getDocNum() {
        return this.docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getReferencesToRefresh() {
        return referencesToRefresh;
    }

    public void setReferencesToRefresh(String referencesToRefresh) {
        this.referencesToRefresh = referencesToRefresh;
    }

    /**
     * Indicates whether multiple values select should be enabled for the lookup
     *
     * <p>
     * When set to true, the select field is enabled for the lookup results group that allows the user
     * to select one or more rows for returning
     * </p>
     *
     * @return boolean true if multiple values should be enabled, false otherwise
     */
    public boolean isMultipleValuesSelect() {
        return multipleValuesSelect;
    }

    /**
     * Setter for the multiple values select indicator
     *
     * @param multipleValuesSelect
     */
    public void setMultipleValuesSelect(boolean multipleValuesSelect) {
        this.multipleValuesSelect = multipleValuesSelect;
    }

    /**
     * For the case of multi-value lookup, indicates the collection that should be populated with
     * the return results
     *
     * @return String collection name (must be full binding path)
     */
    public String getLookupCollectionName() {
        return lookupCollectionName;
    }

    /**
     * Setter for the name of the collection that should be populated with lookup results
     *
     * @param lookupCollectionName
     */
    public void setLookupCollectionName(String lookupCollectionName) {
        this.lookupCollectionName = lookupCollectionName;
    }

    public Map<String, String> getCriteriaFields() {
        return this.criteriaFields;
    }

    public void setCriteriaFields(Map<String, String> criteriaFields) {
        this.criteriaFields = criteriaFields;
    }

    public Map<String, String> getFieldConversions() {
        return this.fieldConversions;
    }

    public void setFieldConversions(Map<String, String> fieldConversions) {
        this.fieldConversions = fieldConversions;
    }

    public Collection<?> getSearchResults() {
        return this.searchResults;
    }

    public void setSearchResults(Collection<?> searchResults) {
        this.searchResults = searchResults;
    }

    public boolean isAtLeastOneRowReturnable() {
        return atLeastOneRowReturnable;
    }

    public void setAtLeastOneRowReturnable(boolean atLeastOneRowReturnable) {
        this.atLeastOneRowReturnable = atLeastOneRowReturnable;
    }

    public boolean isAtLeastOneRowHasActions() {
        return atLeastOneRowHasActions;
    }

    public void setAtLeastOneRowHasActions(boolean atLeastOneRowHasActions) {
        this.atLeastOneRowHasActions = atLeastOneRowHasActions;
    }
}
