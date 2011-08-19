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
package org.kuali.rice.krad.uif.view;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.RequestParameter;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.web.form.LookupForm;

import java.util.Arrays;
import java.util.List;

/**
 * View type for Maintenance documents
 *
 * <p>
 * Supports doing a search against a data object class or performing a more advanced query. The view
 * type is primarily made up of two groups, the search (or criteria) group and the results group. Many
 * options are supported on the view to enable/disable certain features, like what actions are available
 * on the search results.
 * </p>
 *
 * <p>
 * Works in conjunction with <code>LookupableImpl</code> which customizes the view and carries out the
 * business functionality
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LookupView extends FormView {
    private static final long serialVersionUID = 716926008488403616L;

    private Class<?> dataObjectClassName;

    private Group criteriaGroup;
    private CollectionGroup resultsGroup;

    private Field resultsActionsField;
    private Field resultsReturnField;

    private List<Component> criteriaFields;
    private List<Component> resultFields;
    private List<String> defaultSortAttributeNames;

    protected boolean defaultSortAscending = true;

    @RequestParameter
    private boolean hideReturnLinks = false;
    @RequestParameter
    private boolean suppressActions = false;
    @RequestParameter
    private boolean showMaintenanceLinks = false;
    @RequestParameter
    private boolean multipleValuesSelect = false;

    @RequestParameter
    private String returnTarget;

    @RequestParameter
    private boolean returnByScript;

    private boolean lookupCriteriaEnabled = true;
    private boolean supplementalActionsEnabled = false;
    private boolean disableSearchButtons = false;

    private Integer resultSetLimit = null;

    public LookupView() {
        super();
        setViewTypeName(ViewType.LOOKUP);
        setValidateDirty(false);
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Set the abstractTypeClasses map for the lookup object path</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.container.ContainerBase#performInitialization(org.kuali.rice.krad.uif.view.View)
     */
    @Override
    public void performInitialization(View view) {
        initializeGroups();
        if (getItems().isEmpty()) {
            setItems(Arrays.asList(getCriteriaGroup(), getResultsGroup()));
        }
        super.performInitialization(view);

        getAbstractTypeClasses().put(UifPropertyPaths.CRITERIA_FIELDS, getDataObjectClassName());
        if (StringUtils.isNotBlank(getDefaultBindingObjectPath())) {
            getAbstractTypeClasses().put(getDefaultBindingObjectPath(), getDataObjectClassName());
        }
    }

    protected void initializeGroups() {
        if ((getCriteriaGroup() != null) && (getCriteriaGroup().getItems().isEmpty())) {
            getCriteriaGroup().setItems(getCriteriaFields());
        }
        if (getResultsGroup() != null) {
            if ((getResultsGroup().getItems().isEmpty()) && (getResultFields() != null)) {
                getResultsGroup().setItems(getResultFields());
            }
            if (getResultsGroup().getCollectionObjectClass() == null) {
                getResultsGroup().setCollectionObjectClass(getDataObjectClassName());
            }
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.container.ContainerBase#performApplyModel(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object)
     */
    @Override
    public void performApplyModel(View view, Object model, Component parent) {
        LookupForm lookupForm = (LookupForm) model;

        // TODO: need to check lookupForm.isAtLeastOneRowHasActions() somewhere
        if (!isSuppressActions() && isShowMaintenanceLinks()) {
            ((List<Field>) getResultsGroup().getItems()).add(0, getResultsActionsField());
        }

        if (StringUtils.isNotBlank(lookupForm.getReturnFormKey()) &&
                StringUtils.isNotBlank(lookupForm.getReturnLocation()) && !isHideReturnLinks()) {
            ((List<Field>) getResultsGroup().getItems()).add(0, getResultsReturnField());
        }

        super.performApplyModel(view, model, parent);
    }

    public void applyConditionalLogicForFieldDisplay() {
        // TODO: work into view lifecycle
//	    LookupViewHelperService lookupViewHelperService = (LookupViewHelperService) getViewHelperService();
//		Set<String> readOnlyFields = lookupViewHelperService.getConditionallyReadOnlyPropertyNames();
//		Set<String> requiredFields = lookupViewHelperService.getConditionallyRequiredPropertyNames();
//		Set<String> hiddenFields = lookupViewHelperService.getConditionallyHiddenPropertyNames();
//		if ( (readOnlyFields != null && !readOnlyFields.isEmpty()) ||
//			 (requiredFields != null && !requiredFields.isEmpty()) ||
//			 (hiddenFields != null && !hiddenFields.isEmpty())
//			) {
//			for (Field field : getResultsGroup().getItems()) {
//				if (AttributeField.class.isAssignableFrom(field.getClass())) {
//					AttributeField attributeField = (AttributeField) field;
//					if (readOnlyFields != null && readOnlyFields.contains(attributeField.getBindingInfo().getBindingName())) {
//						attributeField.setReadOnly(true);
//					}
//					if (requiredFields != null && requiredFields.contains(attributeField.getBindingInfo().getBindingName())) {
//						attributeField.setRequired(Boolean.TRUE);
//					}
//					if (hiddenFields != null && hiddenFields.contains(attributeField.getBindingInfo().getBindingName())) {
//						attributeField.setControl(LookupInquiryUtils.generateCustomLookupControlFromExisting(HiddenControl.class, null));
//					}
//				}
//	        }
//		}
    }

    /**
     * Class name for the object the lookup applies to
     *
     * <p>
     * The object class name is used to pick up a dictionary entry which will
     * feed the attribute field definitions and other configuration. In addition
     * it is to configure the <code>Lookupable</code> which will carry out the
     * lookup action
     * </p>
     *
     * @return Class<?> lookup data object class
     */
    public Class<?> getDataObjectClassName() {
        return this.dataObjectClassName;
    }

    /**
     * Setter for the object class name
     *
     * @param dataObjectClassName
     */
    public void setDataObjectClassName(Class<?> dataObjectClassName) {
        this.dataObjectClassName = dataObjectClassName;
    }

    /**
     * @return the hideReturnLinks
     */
    public boolean isHideReturnLinks() {
        return this.hideReturnLinks;
    }

    /**
     * @param hideReturnLinks the hideReturnLinks to set
     */
    public void setHideReturnLinks(boolean hideReturnLinks) {
        this.hideReturnLinks = hideReturnLinks;
    }

    /**
     * @return the suppressActions
     */
    public boolean isSuppressActions() {
        return this.suppressActions;
    }

    /**
     * @param suppressActions the suppressActions to set
     */
    public void setSuppressActions(boolean suppressActions) {
        this.suppressActions = suppressActions;
    }

    /**
     * @return the showMaintenanceLinks
     */
    public boolean isShowMaintenanceLinks() {
        return this.showMaintenanceLinks;
    }

    /**
     * @param showMaintenanceLinks the showMaintenanceLinks to set
     */
    public void setShowMaintenanceLinks(boolean showMaintenanceLinks) {
        this.showMaintenanceLinks = showMaintenanceLinks;
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
     * @return the resultsActionsField
     */
    public Field getResultsActionsField() {
        return this.resultsActionsField;
    }

    /**
     * @param resultsActionsField the resultsActionsField to set
     */
    public void setResultsActionsField(Field resultsActionsField) {
        this.resultsActionsField = resultsActionsField;
    }

    /**
     * @return the resultsReturnField
     */
    public Field getResultsReturnField() {
        return this.resultsReturnField;
    }

    /**
     * @param resultsReturnField the resultsReturnField to set
     */
    public void setResultsReturnField(Field resultsReturnField) {
        this.resultsReturnField = resultsReturnField;
    }

    public Group getCriteriaGroup() {
        return this.criteriaGroup;
    }

    public void setCriteriaGroup(Group criteriaGroup) {
        this.criteriaGroup = criteriaGroup;
    }

    public CollectionGroup getResultsGroup() {
        return this.resultsGroup;
    }

    public void setResultsGroup(CollectionGroup resultsGroup) {
        this.resultsGroup = resultsGroup;
    }

    public List<Component> getCriteriaFields() {
        return this.criteriaFields;
    }

    public void setCriteriaFields(List<Component> criteriaFields) {
        this.criteriaFields = criteriaFields;
    }

    public List<Component> getResultFields() {
        return this.resultFields;
    }

    public void setResultFields(List<Component> resultFields) {
        this.resultFields = resultFields;
    }

    public List<String> getDefaultSortAttributeNames() {
        return this.defaultSortAttributeNames;
    }

    public void setDefaultSortAttributeNames(List<String> defaultSortAttributeNames) {
        this.defaultSortAttributeNames = defaultSortAttributeNames;
    }

    public boolean isDefaultSortAscending() {
        return this.defaultSortAscending;
    }

    public void setDefaultSortAscending(boolean defaultSortAscending) {
        this.defaultSortAscending = defaultSortAscending;
    }

    /**
     * Retrieves the maximum number of records that will be listed
     * as a result of the lookup search
     *
     * @return Integer result set limit
     */
    public Integer getResultSetLimit() {
        return resultSetLimit;
    }

    /**
     * Setter for the result list limit
     *
     * @param resultSetLimit Integer specifying limit
     */
    public void setResultSetLimit(Integer resultSetLimit) {
        this.resultSetLimit = resultSetLimit;
    }

    /**
     * Indicates whether a result set limit has been specified for the
     * view
     *
     * @return true if this instance has a result set limit
     */
    public boolean hasResultSetLimit() {
        return (resultSetLimit != null);
    }

    /**
     * @param returnTarget the returnTarget to set
     */
    public void setReturnTarget(String returnTarget) {
        this.returnTarget = returnTarget;
    }

    /**
     * @return the returnTarget
     */
    public String getReturnTarget() {
        return returnTarget;
    }

    /**
     * @return the returnByScript
     */
    public boolean isReturnByScript() {
        return returnByScript;
    }

    /**
     * Setter for the flag to indicate that lookups will return the value
     * by script and not a post
     *
     * @param returnByScript the returnByScript flag
     */
    public void setReturnByScript(boolean returnByScript) {
        this.returnByScript = returnByScript;
    }
}
