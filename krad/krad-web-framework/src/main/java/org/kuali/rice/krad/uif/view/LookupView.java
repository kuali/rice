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
package org.kuali.rice.krad.uif.view;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.RequestParameter;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.Link;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
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
@BeanTag(name = "lookupView-bean", parent = "Uif-LookupView")
public class LookupView extends FormView {
    private static final long serialVersionUID = 716926008488403616L;

    private Class<?> dataObjectClassName;

    private Group criteriaGroup;
    private CollectionGroup resultsGroup;

    private FieldGroup resultsActionsFieldGroup;
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

    private String maintenanceUrlMapping;

    public LookupView() {
        super();

        setViewTypeName(ViewType.LOOKUP);
        setApplyDirtyCheck(false);
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Set the abstractTypeClasses map for the lookup object path</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.container.ContainerBase#performInitialization(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        initializeGroups();

        // since we don't have these as prototypes need to assign ids here
        view.assignComponentIds(getCriteriaGroup());
        view.assignComponentIds(getResultsGroup());

        if (getItems().isEmpty()) {
            setItems(Arrays.asList(getCriteriaGroup(), getResultsGroup()));
        }

        super.performInitialization(view, model);

        // if this is a multi-value lookup, don't show return column
        if (multipleValuesSelect) {
            hideReturnLinks = true;
        }

        getObjectPathToConcreteClassMapping().put(UifPropertyPaths.LOOKUP_CRITERIA, getDataObjectClassName());
        if (StringUtils.isNotBlank(getDefaultBindingObjectPath())) {
            getObjectPathToConcreteClassMapping().put(getDefaultBindingObjectPath(), getDataObjectClassName());
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
            ((List<Component>) getResultsGroup().getItems()).add(0, getResultsActionsFieldGroup());
        }

        if (StringUtils.isNotBlank(lookupForm.getReturnFormKey()) &&
                StringUtils.isNotBlank(lookupForm.getReturnLocation()) && !isHideReturnLinks()) {
            ((List<Component>) getResultsGroup().getItems()).add(0, getResultsReturnField());
        }

        super.performApplyModel(view, model, parent);
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getComponentPrototypes()
     */
    @Override
    public List<Component> getComponentPrototypes() {
        List<Component> components = super.getComponentPrototypes();

        components.add(resultsActionsFieldGroup);
        components.add(resultsReturnField);

        return components;
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
        //				if (InputField.class.isAssignableFrom(field.getClass())) {
        //					InputField attributeField = (InputField) field;
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
    @BeanTagAttribute(name="dataObjectClassName")
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
    @BeanTagAttribute(name="hideReturnLinks")
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
    @BeanTagAttribute(name="isSuppressActions")
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
    @BeanTagAttribute(name="showMaintenanceLinks")
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
    @BeanTagAttribute(name="multipleValueSelect")
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
    @BeanTagAttribute(name="resultActionsFieldGroup",type= BeanTagAttribute.AttributeType.SINGLEBEAN)
    public FieldGroup getResultsActionsFieldGroup() {
        return this.resultsActionsFieldGroup;
    }

    /**
     * @param resultsActionsFieldGroup the resultsActionsField to set
     */
    public void setResultsActionsFieldGroup(FieldGroup resultsActionsFieldGroup) {
        this.resultsActionsFieldGroup = resultsActionsFieldGroup;
    }

    /**
     * @return the resultsReturnField
     */
    @BeanTagAttribute(name="resultReturnField",type= BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Field getResultsReturnField() {
        return this.resultsReturnField;
    }

    /**
     * @param resultsReturnField the resultsReturnField to set
     */
    public void setResultsReturnField(Field resultsReturnField) {
        this.resultsReturnField = resultsReturnField;
    }

    @BeanTagAttribute(name="criteriaGroup",type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Group getCriteriaGroup() {
        return this.criteriaGroup;
    }

    public void setCriteriaGroup(Group criteriaGroup) {
        this.criteriaGroup = criteriaGroup;
    }

    @BeanTagAttribute(name="resultsGroup",type= BeanTagAttribute.AttributeType.SINGLEBEAN)
    public CollectionGroup getResultsGroup() {
        return this.resultsGroup;
    }

    public void setResultsGroup(CollectionGroup resultsGroup) {
        this.resultsGroup = resultsGroup;
    }

    @BeanTagAttribute(name="criteriaFields",type= BeanTagAttribute.AttributeType.LISTBEAN)
    public List<Component> getCriteriaFields() {
        return this.criteriaFields;
    }

    public void setCriteriaFields(List<Component> criteriaFields) {
        this.criteriaFields = criteriaFields;
    }

    @BeanTagAttribute(name="resultFields",type= BeanTagAttribute.AttributeType.LISTBEAN)
    public List<Component> getResultFields() {
        return this.resultFields;
    }

    public void setResultFields(List<Component> resultFields) {
        this.resultFields = resultFields;
    }

    @BeanTagAttribute(name="defaultSortAttributeNames",type= BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getDefaultSortAttributeNames() {
        return this.defaultSortAttributeNames;
    }

    public void setDefaultSortAttributeNames(List<String> defaultSortAttributeNames) {
        this.defaultSortAttributeNames = defaultSortAttributeNames;
    }

    @BeanTagAttribute(name="defaultSortAscending")
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
    @BeanTagAttribute(name="resultSetLimit")
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
    @BeanTagAttribute(name="returnTarget")
    public String getReturnTarget() {
        return returnTarget;
    }

    /**
     * @return the returnByScript
     */
    @BeanTagAttribute(name="returnByScript")
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

    /**
     * String that maps to the maintenance controller for the maintenance document (if any) associated with the
     * lookup data object class
     *
     * <p>
     * Mapping will be used to build the maintenance action links (such as edit, copy, and new). If not given, the
     * default maintenance mapping will be used
     * </p>
     *
     * @return String mapping string
     */
    @BeanTagAttribute(name="maintenanceUrlMapping")
    public String getMaintenanceUrlMapping() {
        return maintenanceUrlMapping;
    }

    /**
     * Setter for the URL mapping string that will be used to build up maintenance action URLs
     *
     * @param maintenanceUrlMapping
     */
    public void setMaintenanceUrlMapping(String maintenanceUrlMapping) {
        this.maintenanceUrlMapping = maintenanceUrlMapping;
    }
}
