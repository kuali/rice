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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.RequestParameter;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.FilterableLookupCriteriaControl;
import org.kuali.rice.krad.uif.control.FilterableLookupCriteriaControlPostData;
import org.kuali.rice.krad.uif.control.TextAreaControl;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleUtils;
import org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata;
import org.kuali.rice.krad.uif.lifecycle.initialize.AssignIdsTask;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.FormView;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * View type for lookups.
 *
 * <p>Supports doing a search against a data object class or performing a more advanced query. The view
 * type is primarily made up of two groups, the search (or criteria) group and the results group. Many
 * options are supported on the view to enable/disable certain features, like what actions are available
 * on the search results.</p>
 *
 * <p>Works in conjunction with {@link org.kuali.rice.krad.lookup.Lookupable} which customizes the view and
 * carries out the business functionality</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "lookupView", parent = "Uif-LookupView")
public class LookupView extends FormView {
    private static final long serialVersionUID = 716926008488403616L;

    private Class<?> dataObjectClass;

    private List<Component> criteriaFields;
    private Group criteriaGroup;

    @RequestParameter
    private boolean hideCriteriaOnSearch;

    private List<Component> resultFields;
    private CollectionGroup resultsGroup;

    private List<String> defaultSortAttributeNames;
    private boolean defaultSortAscending;

    @RequestParameter
    private Boolean renderReturnLink;

    @RequestParameter
    private boolean renderResultActions;

    @RequestParameter
    private Boolean renderMaintenanceLinks;

    @RequestParameter
    private boolean multipleValuesSelect;

    @RequestParameter
    private boolean renderLookupCriteria;

    @RequestParameter
    private boolean renderCriteriaActions;

    private Integer resultSetLimit;
    private Integer multipleValuesSelectResultSetLimit;

    private String maintenanceUrlMapping;

    private FieldGroup rangeFieldGroupPrototype;
    private Message rangedToMessage;

    private boolean autoAddActiveCriteria;

    private List<String> additionalSecurePropertyNames;

    public LookupView() {
        super();

        setViewTypeName(ViewType.LOOKUP);

        defaultSortAscending = true;
        autoAddActiveCriteria = true;
        renderLookupCriteria = true;
        renderCriteriaActions = true;
        renderResultActions = true;

        additionalSecurePropertyNames = new ArrayList<String>();
    }

    /**
     * Initializes Lookupable with data object class and sets the abstractTypeClasses map for the
     * lookup object path.
     *
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        // init the view helper with the data object class
        Lookupable lookupable = (Lookupable) getViewHelperService();
        lookupable.setDataObjectClass(dataObjectClass);

        initializeGroups();

        super.performInitialization(model);

        getObjectPathToConcreteClassMapping().put(UifPropertyPaths.LOOKUP_CRITERIA, getDataObjectClass());
        if (StringUtils.isNotBlank(getDefaultBindingObjectPath())) {
            getObjectPathToConcreteClassMapping().put(getDefaultBindingObjectPath(), getDataObjectClass());
        }
    }

    /**
     * Reads the convenience render flags and sets the corresponding component property, processing the criteria
     * fields for any adjustments, and invokes the lookup authorizer to determine whether maintenance links should
     * be shown.
     *
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        LookupForm lookupForm = (LookupForm) model;

        // don't render criteria group footer/actions
        if (!renderCriteriaActions || hideCriteriaOnSearch) {
            criteriaGroup.getFooter().setRender(false);
        }

        // don't render criteria if not supposed to or (hide on search results and displaying the results)
        if (!renderLookupCriteria || (hideCriteriaOnSearch && lookupForm.isDisplayResults())) {
            criteriaGroup.setRender(false);
        }

        // if hide on search results and not displaying search results don't render results
        if (hideCriteriaOnSearch && !lookupForm.isDisplayResults()) {
            resultsGroup.setRender(false);
        }

        boolean returnLinkAllowed = false;
        boolean maintenanceLinksAllowed = false;

        // neither return nor maintenance links are shown for multi-value select
        if (!multipleValuesSelect) {
            // if coming from a quickfinder we will show the return URL
            if ((lookupForm.getInitialRequestParameters() != null) && lookupForm.getInitialRequestParameters()
                    .containsKey(UifParameters.QUICKFINDER_ID)) {
                returnLinkAllowed = true;
            } else {
                maintenanceLinksAllowed = true;
            }
        } else {
            renderResultActions = false;
        }

        // only override view properties if they were not manually configured
        if (renderReturnLink == null) {
            renderReturnLink = returnLinkAllowed;
        }

        if (renderMaintenanceLinks == null) {
            renderMaintenanceLinks = maintenanceLinksAllowed;
        }

        // if maintenance links enabled, verify the user had permission
        if (renderMaintenanceLinks) {
            LookupViewAuthorizerBase lookupAuthorizer = (LookupViewAuthorizerBase) getAuthorizer();

            Person user = GlobalVariables.getUserSession().getPerson();
            renderMaintenanceLinks = lookupAuthorizer.canInitiateMaintenanceDocument(getDataObjectClass().getName(),
                    user);
        }

        convertLookupCriteriaFields(criteriaGroup);

        super.performApplyModel(model, parent);
    }

    /**
     * Forces session persistence on the criteria fields so the search criteria can be validated on post.
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        LookupForm lookupForm = (LookupForm) model;
        String viewId = lookupForm.getViewId();

        Map<String, FilterableLookupCriteriaControlPostData> filterableLookupCriteria = new HashMap<String, FilterableLookupCriteriaControlPostData>();

        List<InputField> fields = ViewLifecycleUtils.getElementsOfTypeDeep(criteriaGroup, InputField.class);

        for (InputField field : fields) {
            field.setForceSessionPersistence(true);

            String propertyName = field.getPropertyName();

            if (field.getControl() instanceof FilterableLookupCriteriaControl) {
                FilterableLookupCriteriaControl control = (FilterableLookupCriteriaControl) field.getControl();
                filterableLookupCriteria.put(propertyName, control.getPostData(propertyName));
            }
        }

        ViewPostMetadata viewPostMetadata = ViewLifecycle.getViewPostMetadata();
        viewPostMetadata.addComponentPostData(viewId, UifConstants.PostMetadata.FILTERABLE_LOOKUP_CRITERIA, filterableLookupCriteria);

        if (lookupForm.isReturnByScript()) {
            getAdditionalHiddenValues().put(UifParameters.RETURN_BY_SCRIPT, "true");
        }
    }

    /**
     * Adds the 'active' property criteria to the criteria fields if the BO is inactivatable and their is
     * not already a lookup field for the active property.
     */
    protected void addActiveCriteriaIfNecessary() {
        boolean isInactivatableClass = Inactivatable.class.isAssignableFrom(dataObjectClass);

        if (!autoAddActiveCriteria || !isInactivatableClass) {
            return;
        }

        boolean hasActiveCriteria = false;
        for (Component field : getCriteriaFields()) {
            if (((InputField) field).getPropertyName().equals(UifPropertyPaths.ACTIVE)) {
                hasActiveCriteria = true;
            }
        }

        if (hasActiveCriteria) {
            return;
        }

        AttributeDefinition attributeDefinition =
                KRADServiceLocatorWeb.getDataDictionaryService().getAttributeDefinition(
                        dataObjectClass.getName(), UifPropertyPaths.ACTIVE);

        LookupInputField activeLookupField;
        if (attributeDefinition == null) {
            activeLookupField = (LookupInputField) ComponentFactory.getNewComponentInstance(
                    ComponentFactory.LOOKUP_ACTIVE_INPUT_FIELD);
        } else {
            activeLookupField = (LookupInputField) ComponentFactory.getNewComponentInstance(
                    ComponentFactory.LOOKUP_INPUT_FIELD);

            activeLookupField.setPropertyName(UifPropertyPaths.ACTIVE);
            activeLookupField.copyFromAttributeDefinition(attributeDefinition);
        }

        getCriteriaFields().add(activeLookupField);
    }

    /**
     * Adds the list of criteria and result fields to their group prototypes, then adds the criteria and result
     * groups to the items for the view.
     */
    protected void initializeGroups() {
        if ((getCriteriaGroup() != null) && (getCriteriaGroup().getItems().isEmpty())) {
            getCriteriaGroup().setItems(getCriteriaFields());
        }

        if (getResultsGroup() != null) {
            if ((getResultsGroup().getItems().isEmpty()) && (getResultFields() != null)) {
                getResultsGroup().setItems(getResultFields());
            }

            if (getResultsGroup().getCollectionObjectClass() == null) {
                getResultsGroup().setCollectionObjectClass(getDataObjectClass());
            }
        }

        if (getItems().isEmpty()) {
            setItems(Arrays.asList(getCriteriaGroup(), getResultsGroup()));
        }
    }

    /**
     * Performs conversions of the lookup criteria fields within the given group's items.
     *
     * <p>Max lengths are removed on text controls so wildcards can be added. Ranged date fields are
     * converted to field groups with the from/to date fields</p>
     */
    protected void convertLookupCriteriaFields(Group lookupGroup) {
        @SuppressWarnings("unchecked")
        List<Component> criteriaGroupItems = (List<Component>) lookupGroup.getItems();

        // holds the index and range field group for replacement into the items
        HashMap<Integer, Component> dateRangeFieldMap = new HashMap<Integer, Component>();

        int rangeIndex = 0;
        for (Component component : criteriaGroupItems) {
            if (component == null) {
                continue;
            }

            if (Group.class.isAssignableFrom(component.getClass())) {
                convertLookupCriteriaFields((Group) component);
            } else if (FieldGroup.class.isAssignableFrom(component.getClass())) {
                convertLookupCriteriaFields(((FieldGroup) component).getGroup());
            } else if (LookupInputField.class.isAssignableFrom(component.getClass())) {
                LookupInputField lookupInputField = (LookupInputField) component;

                // set the max length on the controls to allow for wildcards
                Control control = lookupInputField.getControl();

                if (control instanceof TextControl) {
                    ((TextControl) control).setMaxLength(null);
                } else if (control instanceof TextAreaControl) {
                    ((TextAreaControl) control).setMaxLength(null);
                }

                if (lookupInputField.isRanged()) {
                    FieldGroup rangeFieldGroup = createDateRangeFieldGroup(lookupInputField);

                    dateRangeFieldMap.put(rangeIndex, rangeFieldGroup);
                }
            }

            rangeIndex++;
        }

        // replace original fields with range field groups
        for (Integer index : dateRangeFieldMap.keySet()) {
            criteriaGroupItems.set(index, dateRangeFieldMap.get(index));
        }

        criteriaGroup.setItems(criteriaGroupItems);
    }

    /**
     * Creates a {@link FieldGroup} instance to replace the given lookup input field as a
     * date criteria range.
     *
     * <p>The field group is created by copying {@link LookupView#rangeFieldGroupPrototype}. This can be
     * used to configure how the field group will appear. In addition, the two lookup fields are separated
     * with a message that can be configured with {@link LookupView#rangedToMessage}</p>
     *
     * @param toDate lookup input field that field group should be build for
     *
     * @return field group that contains a from and to lookup input field for searching a date range
     *
     * @see LookupView#rangeFieldGroupPrototype
     * @see LookupView#rangedToMessage
     */
    protected FieldGroup createDateRangeFieldGroup(LookupInputField toDate) {
        // Generate an ID when the "to date" field is out of the normal lifecycle flow
        if (toDate.getId() == null) {
            toDate.setId(AssignIdsTask.generateId(toDate, ViewLifecycle.getView()));
        }

        FieldGroup rangeFieldGroup = ComponentUtils.copy(getRangeFieldGroupPrototype());

        // Copy some properties from the "to date" field to the field group
        rangeFieldGroup.setFieldLabel(ComponentUtils.copy(toDate.getFieldLabel()));
        rangeFieldGroup.setPropertyExpressions(toDate.getPropertyExpressions());
        rangeFieldGroup.setProgressiveRender(toDate.getProgressiveRender());
        rangeFieldGroup.setProgressiveRenderViaAJAX(toDate.isProgressiveRenderViaAJAX());
        rangeFieldGroup.setConditionalRefresh(toDate.getConditionalRefresh());
        rangeFieldGroup.setRefreshWhenChangedPropertyNames(toDate.getRefreshWhenChangedPropertyNames());
        rangeFieldGroup.setForceSessionPersistence(true);

        // Reset some fields for the "to date" field
        toDate.getFieldLabel().setRender(false);
        toDate.setRefreshWhenChangedPropertyNames(null);
        toDate.setForceSessionPersistence(true);

        // Create a "from date" field from the "to date" field
        LookupInputField fromDate = ComponentUtils.copy(toDate,
                KRADConstants.LOOKUP_DEFAULT_RANGE_SEARCH_LOWER_BOUND_LABEL);
        fromDate.getBindingInfo().setBindingName(
                KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + fromDate.getPropertyName());
        fromDate.setPropertyName(
                KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + fromDate.getPropertyName());
        fromDate.setOrder(0);

        // add the criteria fields to the field group
        List<Component> fieldGroupItems = new ArrayList<Component>();
        fieldGroupItems.add(fromDate);
        fieldGroupItems.add(rangedToMessage);
        fieldGroupItems.add(toDate);
        rangeFieldGroup.setItems(fieldGroupItems);

        return rangeFieldGroup;
    }

    /**
     * Class for the data object the lookup applies to.
     *
     * <p>The object class name is used to pick up a dictionary entry which will feed the attribute field
     * definitions and other configuration. In addition it is to configure the
     * {@link org.kuali.rice.krad.lookup.Lookupable} which will carry out the search action</p>
     *
     * @return lookup data object class
     */
    @BeanTagAttribute(name = "dataObjectClass")
    public Class<?> getDataObjectClass() {
        return this.dataObjectClass;
    }

    /**
     * @see LookupView#getDataObjectClass()
     */
    public void setDataObjectClass(Class<?> dataObjectClass) {
        this.dataObjectClass = dataObjectClass;
    }

    /**
     * Convenience setter to configure the lookup data object class by class name.
     *
     * @param dataObjectClassName full class name for the lookup data object
     */
    public void setDataObjectClassName(String dataObjectClassName) {
        try {
            this.dataObjectClass = Class.forName(dataObjectClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to set class for class name: " + dataObjectClassName, e);
        }
    }

    /**
     * Indicates whether a return value link should be rendered for each result row.
     *
     * <p>When the lookup is called from a view (using a {@link org.kuali.rice.krad.uif.widget.QuickFinder} the return
     * link can be returned to allow the user to return a value(s) for a selected row. Note, if this is not manually
     * set the framework will determine when the lookup is called from a quickfinder and turn this flag on</p>
     *
     * @return boolean true if the return link should be rendered for each result row, false if not
     */
    @BeanTagAttribute(name = "renderReturnLink")
    public Boolean isRenderReturnLink() {
        return this.renderReturnLink;
    }

    /**
     * @see LookupView#isRenderReturnLink()
     */
    public void setRenderReturnLink(Boolean renderReturnLink) {
        this.renderReturnLink = renderReturnLink;
    }

    /**
     * Indicates whether the actions column for the search results collection group should be rendered (default
     * is true).
     *
     * <p>Note this is a convenience property for setting the render property on the result collection group</p>
     *
     * @return boolean true if the result actions column should be rendered, false if not
     */
    @BeanTagAttribute(name = "isRenderResultActions")
    public boolean isRenderResultActions() {
        return this.renderResultActions;
    }

    /**
     * @see LookupView#isRenderResultActions()
     */
    public void setRenderResultActions(boolean renderResultActions) {
        this.renderResultActions = renderResultActions;
    }

    /**
     * Indicates whether links for maintenance actions (new, edit, copy, delete) should be rendered.
     *
     * <p>When this property is not manually set it will be enabled by the framework when a lookup is not invoked
     * from a quickfinder (for example a standard link from a menu). Regardless if the flag is manually enabled
     * or enabled by the framework, an additional authorization check will be performed to determine if the user
     * has initiate permission for the maintenance document associated with the lookup data object class. If not,
     * this flag will be disabled</p>
     *
     * @return boolean true if maintenance links should be rendered, false if not
     */
    @BeanTagAttribute(name = "renderMaintenanceLinks")
    public Boolean isRenderMaintenanceLinks() {
        return this.renderMaintenanceLinks;
    }

    /**
     * @see LookupView#isRenderMaintenanceLinks()
     */
    public void setRenderMaintenanceLinks(Boolean renderMaintenanceLinks) {
        this.renderMaintenanceLinks = renderMaintenanceLinks;
    }

    /**
     * Indicates whether multiple values select should be enabled for the lookup.
     *
     * <p>When set to true, the select field is enabled for the lookup results group that allows the user
     * to select one or more rows for returning. The framework will also set the {@link #isRenderReturnLink()}
     * and {@link #isRenderMaintenanceLinks()} properties to false (unless manually overridden)</p>
     *
     * @return true if multiple values select should be enabled, false otherwise
     */
    @BeanTagAttribute(name = "multipleValueSelect")
    public boolean isMultipleValuesSelect() {
        return multipleValuesSelect;
    }

    /**
     * @see LookupView#isMultipleValuesSelect()
     */
    public void setMultipleValuesSelect(boolean multipleValuesSelect) {
        this.multipleValuesSelect = multipleValuesSelect;
    }

    /**
     * List of fields that will be rendered for the lookup criteria.
     *
     * <p>This is a convenience property for setting the items in {@link #getCriteriaGroup()}, which is the
     * group the criteria for the lookup is rendered in. This property can be bypassed and the items set
     * directly in the criteria group (for more flexibility)</p>
     *
     * @return List of components to render as the lookup criteria
     */
    @ViewLifecycleRestriction
    @BeanTagAttribute(name = "criteriaFields", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<Component> getCriteriaFields() {
        return this.criteriaFields;
    }

    /**
     * @see LookupView#getCriteriaFields()
     */
    public void setCriteriaFields(List<Component> criteriaFields) {
        this.criteriaFields = criteriaFields;
    }

    /**
     * Component {@link Group} instance to render as search criteria.
     *
     * <p>Fields that make up the criteria for the lookup will be rendered in this group. This can be used in a few
     * different ways:
     *
     * <ul>
     * <li>Set the group to have the desired layout, style, and other general group properties. Note this
     * is done in the base lookup view. The actual criteria fields can then be configured using
     * {@link #getCriteriaFields()}</li>
     * <li>Configure the criteria group entirely (ignoring criteria fields). This would allow you to do things
     * like have multiple groups for the criteria.</li>
     * </ul></p>
     *
     * <p>Note the footer for the criteria group can contain actions (such as search, clear, custom actions)</p>
     *
     * @return group instance that will hold the search criteria fields
     */
    @ViewLifecycleRestriction
    @BeanTagAttribute(name = "criteriaGroup", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Group getCriteriaGroup() {
        return this.criteriaGroup;
    }

    /**
     * @see LookupView#getCriteriaGroup()
     */
    public void setCriteriaGroup(Group criteriaGroup) {
        this.criteriaGroup = criteriaGroup;
    }

    public boolean isHideCriteriaOnSearch() {
        return hideCriteriaOnSearch;
    }

    public void setHideCriteriaOnSearch(boolean hideCriteriaOnSearch) {
        this.hideCriteriaOnSearch = hideCriteriaOnSearch;
    }

    /**
     * List of fields that will be rendered for the result collection group, each field will be a column
     * (assuming table layout is used).
     *
     * <p>This is a convenience property for setting the items in {@link #getResultsGroup()}, which is the
     * collection group the results for the lookup is rendered in. This property can be bypassed and the items set
     * directly in the results group (for more flexibility)</p>
     *
     * @return List of components to render in the results group
     */
    @ViewLifecycleRestriction
    @BeanTagAttribute(name = "resultFields", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<Component> getResultFields() {
        return this.resultFields;
    }

    /**
     * @see LookupView#getResultFields()
     */
    public void setResultFields(List<Component> resultFields) {
        this.resultFields = resultFields;
    }

    /**
     * Component {@link CollectionGroup} instance to render for the lookup results.
     *
     * <p>After a search is performed, the resulting data objects will be rendered in this collection group. This
     * collection group can be used in two ways:
     *
     * <ul>
     * <li>Set the desired layout, style, and other general collection group properties. Note this is done
     * in the base lookup view. Then the actual fields that are rendered in the collection group can be
     * configured using {@link #getResultFields()}</li>
     * <li>Configure the results group entirely (ignoring result fields)</li>
     * </ul></p>
     *
     * <p>Note actions that are presented for the results can be configured using the
     * {@link org.kuali.rice.krad.uif.container.CollectionGroup#getLineActions()} property</p>
     *
     * @return collection group instance to render for the lookup results
     */
    @ViewLifecycleRestriction
    @BeanTagAttribute(name = "resultsGroup", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public CollectionGroup getResultsGroup() {
        return this.resultsGroup;
    }

    /**
     * @see LookupView#getResultsGroup()
     */
    public void setResultsGroup(CollectionGroup resultsGroup) {
        this.resultsGroup = resultsGroup;
    }

    /**
     * List of property names on the configured data object class that will be used to perform the initial
     * sorting of the search results.
     *
     * @return list of property names valid for the configured data object class
     * @see LookupView#isDefaultSortAscending()
     */
    @BeanTagAttribute(name = "defaultSortAttributeNames", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getDefaultSortAttributeNames() {
        return this.defaultSortAttributeNames;
    }

    /**
     * @see LookupView#getDefaultSortAttributeNames()
     */
    public void setDefaultSortAttributeNames(List<String> defaultSortAttributeNames) {
        this.defaultSortAttributeNames = defaultSortAttributeNames;
    }

    /**
     * Indicates whether the initial sort performed using {@link #getDefaultSortAttributeNames()} is done based
     * on ascending or descending order (default is true, ascending).
     *
     * @return boolean true if ascending sort should be performed, false if descending sort should be
     *         performed
     */
    @BeanTagAttribute(name = "defaultSortAscending")
    public boolean isDefaultSortAscending() {
        return this.defaultSortAscending;
    }

    /**
     * @see LookupView#isDefaultSortAscending()
     */
    public void setDefaultSortAscending(boolean defaultSortAscending) {
        this.defaultSortAscending = defaultSortAscending;
    }

    /**
     * Retrieves the maximum number of records that will be listed as a result of the lookup search.
     *
     * @return Integer result set limit
     */
    @BeanTagAttribute(name = "resultSetLimit")
    public Integer getResultSetLimit() {
        return resultSetLimit;
    }

    /**
     * @see LookupView#getResultSetLimit()
     */
    public void setResultSetLimit(Integer resultSetLimit) {
        this.resultSetLimit = resultSetLimit;
    }

    /**
     * Retrieves the maximum number of records that will be listed as a result of the multiple
     * values select lookup search.
     *
     * @return multiple values select result set limit
     */
    @BeanTagAttribute(name = "multipleValuesSelectResultSetLimit")
    public Integer getMultipleValuesSelectResultSetLimit() {
        return multipleValuesSelectResultSetLimit;
    }

    /**
     * @see LookupView#getMultipleValuesSelectResultSetLimit()
     */
    public void setMultipleValuesSelectResultSetLimit(Integer multipleValuesSelectResultSetLimit) {
        this.multipleValuesSelectResultSetLimit = multipleValuesSelectResultSetLimit;
    }

    /**
     * String that maps to the maintenance controller for the maintenance document (if any) associated with the
     * lookup data object class.
     *
     * <p>Mapping will be used to build the maintenance action links (such as edit, copy, and new). If not given, the
     * default maintenance mapping will be used</p>
     *
     * @return mapping string
     */
    @BeanTagAttribute(name = "maintenanceUrlMapping")
    public String getMaintenanceUrlMapping() {
        return maintenanceUrlMapping;
    }

    /**
     * @see LookupView#getMaintenanceUrlMapping()
     */
    public void setMaintenanceUrlMapping(String maintenanceUrlMapping) {
        this.maintenanceUrlMapping = maintenanceUrlMapping;
    }

    /**
     * Indicates whether the action buttons like search in the criteria group footer should be rendered,
     * defaults to true.
     *
     * @return boolean true if the criteria actions should be rendered, false if not
     */
    public boolean isRenderCriteriaActions() {
        return renderCriteriaActions;
    }

    /**
     * @see LookupView#isRenderCriteriaActions()
     */
    public void setRenderCriteriaActions(boolean renderCriteriaActions) {
        this.renderCriteriaActions = renderCriteriaActions;
    }

    /**
     * Indicates whether the lookup criteria group should be rendered, default to true.
     *
     * <p>Hiding the criteria group can be useful in cases where the criteria is passed in through the request and
     * also the search is executed on the initial request</p>
     *
     * @return boolean true if criteria group should be rendered, false if not
     */
    public boolean isRenderLookupCriteria() {
        return renderLookupCriteria;
    }

    /**
     * @see LookupView#isRenderLookupCriteria()
     */
    public void setRenderLookupCriteria(boolean renderLookupCriteria) {
        this.renderLookupCriteria = renderLookupCriteria;
    }

    /**
     * Field group prototype that will be copied to create any date range field groups.
     *
     * @return field group instance to use for creating range field groups
     */
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    public FieldGroup getRangeFieldGroupPrototype() {
        return rangeFieldGroupPrototype;
    }

    /**
     * @see LookupView#getRangeFieldGroupPrototype()
     */
    public void setRangeFieldGroupPrototype(FieldGroup rangeFieldGroupPrototype) {
        this.rangeFieldGroupPrototype = rangeFieldGroupPrototype;
    }

    /**
     * Component {@link Message} instance to render between the range criteria fields within a range
     * field group.
     *
     * @return message instance for range field group
     */
    @ViewLifecycleRestriction(UifConstants.ViewPhases.INITIALIZE)
    public Message getRangedToMessage() {
        return rangedToMessage;
    }

    /**
     * @see LookupView#getRangedToMessage()
     */
    public void setRangedToMessage(Message rangedToMessage) {
        this.rangedToMessage = rangedToMessage;
    }

    /**
     * Indicates whether the 'active' criteria field must be added automatically for Inactivatable business
     * objects.
     *
     * @return boolean true if active criteria should be added
     */
    public boolean isAutoAddActiveCriteria() {
        return autoAddActiveCriteria;
    }

    /**
     * @see LookupView#isAutoAddActiveCriteria()
     */
    public void setAutoAddActiveCriteria(boolean autoAddActiveCriteria) {
        this.autoAddActiveCriteria = autoAddActiveCriteria;
    }

    /**
     * List of secure property names that are in addition to the
     * {@link org.kuali.rice.krad.uif.component.ComponentSecurity} or
     * {@link org.kuali.rice.krad.datadictionary.AttributeSecurity} attributes.
     *
     * @return list of secure property names
     */
    @BeanTagAttribute(name = "additionalSecurePropertyNames", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getAdditionalSecurePropertyNames() {
        return additionalSecurePropertyNames;
    }

    /**
     * @see LookupView#getAdditionalSecurePropertyNames()
     */
    public void setAdditionalSecurePropertyNames(List<String> additionalSecurePropertyNames) {
        this.additionalSecurePropertyNames = additionalSecurePropertyNames;
    }

}
