/*
 * Copyright 2007 The Kuali Foundation Licensed under the Educational Community
 * License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.krad.uif.widget;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.DataObjectRelationship;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.core.BindingInfo;
import org.kuali.rice.krad.uif.core.Component;
import org.kuali.rice.krad.uif.field.ActionField;
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.util.KRADUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Widget for navigating to a lookup from a field (called a quickfinder)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class QuickFinder extends WidgetBase {
    private static final long serialVersionUID = 3302390972815386785L;

    // lookup configuration
    private String baseLookupUrl;
    private String dataObjectClassName;
    private String viewName;

    private String referencesToRefresh;

    private Map<String, String> fieldConversions;
    private Map<String, String> lookupParameters;

    // lookup view options
    private String readOnlySearchFields;

    private Boolean hideReturnLink;
    private Boolean suppressActions;
    private Boolean autoSearch;
    private Boolean lookupCriteriaEnabled;
    private Boolean supplementalActionsEnabled;
    private Boolean disableSearchButtons;
    private Boolean headerBarEnabled;
    private Boolean showMaintenanceLinks;

    private ActionField quickfinderActionField;

    public QuickFinder() {
        super();

        fieldConversions = new HashMap<String, String>();
        lookupParameters = new HashMap<String, String>();
    }

    /**
     * @see org.kuali.rice.krad.uif.widget.WidgetBase#performFinalize(org.kuali.rice.krad.uif.container.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.core.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        if (!isRender()) {
            return;
        }

        AttributeField field = (AttributeField) parent;

        // determine lookup class, field conversions and lookup parameters in
        // not set
        if (StringUtils.isBlank(dataObjectClassName)) {
            DataObjectRelationship relationship = getRelationshipForField(view, model, field);

            // if no relationship found cannot have a quickfinder
            if (relationship == null) {
                setRender(false);
                return;
            }

            dataObjectClassName = relationship.getRelatedClass().getName();

            if ((fieldConversions == null) || fieldConversions.isEmpty()) {
                generateFieldConversions(field, relationship);
            }

            if ((lookupParameters == null) || lookupParameters.isEmpty()) {
                generateLookupParameters(field, relationship);
            }
        }

        // adjust paths based on associated attribute field
        updateFieldConversions(field.getBindingInfo());
        updateLookupParameters(field.getBindingInfo());

        quickfinderActionField.addActionParameter(UifParameters.BASE_LOOKUP_URL, baseLookupUrl);
        quickfinderActionField.addActionParameter(UifParameters.DATA_OBJECT_CLASS_NAME, dataObjectClassName);

        if (!fieldConversions.isEmpty()) {
            quickfinderActionField.addActionParameter(UifParameters.CONVERSION_FIELDS,
                    KRADUtils.buildMapParameterString(fieldConversions));
        }

        if (!lookupParameters.isEmpty()) {
            quickfinderActionField.addActionParameter(UifParameters.LOOKUP_PARAMETERS,
                    KRADUtils.buildMapParameterString(lookupParameters));
        }

        addActionParameterIfNotNull(UifParameters.VIEW_NAME, viewName);
        addActionParameterIfNotNull(UifParameters.READ_ONLY_FIELDS, readOnlySearchFields);
        addActionParameterIfNotNull(UifParameters.HIDE_RETURN_LINK, hideReturnLink);
        addActionParameterIfNotNull(UifParameters.SUPRESS_ACTIONS, suppressActions);
        addActionParameterIfNotNull(UifParameters.REFERENCES_TO_REFRESH, referencesToRefresh);
        addActionParameterIfNotNull(UifParameters.AUTO_SEARCH, autoSearch);
        addActionParameterIfNotNull(UifParameters.LOOKUP_CRITERIA_ENABLED, lookupCriteriaEnabled);
        addActionParameterIfNotNull(UifParameters.SUPPLEMENTAL_ACTIONS_ENABLED, supplementalActionsEnabled);
        addActionParameterIfNotNull(UifParameters.DISABLE_SEARCH_BUTTONS, disableSearchButtons);
        addActionParameterIfNotNull(UifParameters.HEADER_BAR_ENABLED, headerBarEnabled);
        addActionParameterIfNotNull(UifParameters.SHOW_MAINTENANCE_LINKS, showMaintenanceLinks);

        // TODO:
        // org.kuali.rice.kns.util.FieldUtils.populateQuickfinderDefaultsForLookup(Class,
        // String, Field)
    }

    protected void addActionParameterIfNotNull(String parameterName, Object parameterValue) {
        if ((parameterValue != null) && StringUtils.isNotBlank(parameterValue.toString())) {
            quickfinderActionField.addActionParameter(parameterName, parameterValue.toString());
        }
    }

    protected DataObjectRelationship getRelationshipForField(View view, Object model, AttributeField field) {
        String propertyName = field.getBindingInfo().getBindingName();

        // get object instance and class for parent
        Object parentObject = ViewModelUtils.getParentObjectForMetadata(view, model, field);
        Class<?> parentObjectClass = null;
        if (parentObject != null) {
            parentObjectClass = parentObject.getClass();
        }

        // get relationship from metadata service
        return KRADServiceLocatorWeb.getDataObjectMetaDataService()
                .getDataObjectRelationship(parentObject, parentObjectClass, propertyName, "", true, true, false);
    }

    protected void generateFieldConversions(AttributeField field, DataObjectRelationship relationship) {
        fieldConversions = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : relationship.getParentToChildReferences().entrySet()) {
            String fromField = entry.getValue();
            String toField = entry.getKey();

            // TODO: displayedFieldnames in
            // org.kuali.rice.kns.lookup.LookupUtils.generateFieldConversions(BusinessObject,
            // String, DataObjectRelationship, String, List, String)

            fieldConversions.put(fromField, toField);
        }
    }

    protected void generateLookupParameters(AttributeField field, DataObjectRelationship relationship) {
        lookupParameters = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : relationship.getParentToChildReferences().entrySet()) {
            String fromField = entry.getKey();
            String toField = entry.getValue();

            // TODO: displayedFieldnames and displayedQFFieldNames in
            // generateLookupParameters(BusinessObject,
            // String, DataObjectRelationship, String, List, String)

            if (relationship.getUserVisibleIdentifierKey() == null ||
                    relationship.getUserVisibleIdentifierKey().equals(fromField)) {
                lookupParameters.put(fromField, toField);
            }
        }
    }

    /**
     * Adjusts the path on the field conversion to property to match the binding
     * path prefix of the given <code>BindingInfo</code>
     *
     * @param bindingInfo - binding info instance to copy binding path prefix from
     */
    public void updateFieldConversions(BindingInfo bindingInfo) {
        Map<String, String> adjustedFieldConversions = new HashMap<String, String>();
        for (String fromField : fieldConversions.keySet()) {
            String toField = fieldConversions.get(fromField);
            String adjustedToFieldPath = bindingInfo.getPropertyAdjustedBindingPath(toField);

            adjustedFieldConversions.put(fromField, adjustedToFieldPath);
        }

        this.fieldConversions = adjustedFieldConversions;
    }

    /**
     * Adjusts the path on the lookup parameter from property to match the binding
     * path prefix of the given <code>BindingInfo</code>
     *
     * @param bindingInfo - binding info instance to copy binding path prefix from
     */
    public void updateLookupParameters(BindingInfo bindingInfo) {
        Map<String, String> adjustedLookupParameters = new HashMap<String, String>();
        for (String fromField : lookupParameters.keySet()) {
            String toField = lookupParameters.get(fromField);
            String adjustedFromFieldPath = bindingInfo.getPropertyAdjustedBindingPath(fromField);

            adjustedLookupParameters.put(adjustedFromFieldPath, toField);
        }

        this.lookupParameters = adjustedLookupParameters;
    }

    /**
     * @see org.kuali.rice.krad.uif.core.ComponentBase#getNestedComponents()
     */
    @Override
    public List<Component> getNestedComponents() {
        List<Component> components = super.getNestedComponents();

        components.add(quickfinderActionField);

        return components;
    }

    public String getBaseLookupUrl() {
        return this.baseLookupUrl;
    }

    public void setBaseLookupUrl(String baseLookupUrl) {
        this.baseLookupUrl = baseLookupUrl;
    }

    public String getDataObjectClassName() {
        return this.dataObjectClassName;
    }

    public void setDataObjectClassName(String dataObjectClassName) {
        this.dataObjectClassName = dataObjectClassName;
    }

    public String getViewName() {
        return this.viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getReferencesToRefresh() {
        return this.referencesToRefresh;
    }

    public void setReferencesToRefresh(String referencesToRefresh) {
        this.referencesToRefresh = referencesToRefresh;
    }

    public Map<String, String> getFieldConversions() {
        return this.fieldConversions;
    }

    public void setFieldConversions(Map<String, String> fieldConversions) {
        this.fieldConversions = fieldConversions;
    }

    public Map<String, String> getLookupParameters() {
        return this.lookupParameters;
    }

    public void setLookupParameters(Map<String, String> lookupParameters) {
        this.lookupParameters = lookupParameters;
    }

    public String getReadOnlySearchFields() {
        return this.readOnlySearchFields;
    }

    public void setReadOnlySearchFields(String readOnlySearchFields) {
        this.readOnlySearchFields = readOnlySearchFields;
    }

    public Boolean getHideReturnLink() {
        return this.hideReturnLink;
    }

    public void setHideReturnLink(Boolean hideReturnLink) {
        this.hideReturnLink = hideReturnLink;
    }

    public Boolean getSuppressActions() {
        return suppressActions;
    }

    public void setSuppressActions(Boolean suppressActions) {
        this.suppressActions = suppressActions;
    }

    public Boolean getAutoSearch() {
        return this.autoSearch;
    }

    public void setAutoSearch(Boolean autoSearch) {
        this.autoSearch = autoSearch;
    }

    public Boolean getLookupCriteriaEnabled() {
        return this.lookupCriteriaEnabled;
    }

    public void setLookupCriteriaEnabled(Boolean lookupCriteriaEnabled) {
        this.lookupCriteriaEnabled = lookupCriteriaEnabled;
    }

    public Boolean getSupplementalActionsEnabled() {
        return this.supplementalActionsEnabled;
    }

    public void setSupplementalActionsEnabled(Boolean supplementalActionsEnabled) {
        this.supplementalActionsEnabled = supplementalActionsEnabled;
    }

    public Boolean getDisableSearchButtons() {
        return this.disableSearchButtons;
    }

    public void setDisableSearchButtons(Boolean disableSearchButtons) {
        this.disableSearchButtons = disableSearchButtons;
    }

    public Boolean getHeaderBarEnabled() {
        return this.headerBarEnabled;
    }

    public void setHeaderBarEnabled(Boolean headerBarEnabled) {
        this.headerBarEnabled = headerBarEnabled;
    }

    public Boolean getShowMaintenanceLinks() {
        return this.showMaintenanceLinks;
    }

    public void setShowMaintenanceLinks(Boolean showMaintenanceLinks) {
        this.showMaintenanceLinks = showMaintenanceLinks;
    }

    public ActionField getQuickfinderActionField() {
        return this.quickfinderActionField;
    }

    public void setQuickfinderActionField(ActionField quickfinderActionField) {
        this.quickfinderActionField = quickfinderActionField;
    }
}
