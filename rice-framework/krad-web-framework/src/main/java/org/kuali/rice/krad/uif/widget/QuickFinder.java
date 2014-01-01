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
package org.kuali.rice.krad.uif.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.DataObjectRelationship;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.lifecycle.LifecycleEventListener;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;

/**
 * Widget for navigating to a lookup from a field (called a quickfinder).
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "quickFinder-bean", parent = "Uif-QuickFinder"),
        @BeanTag(name = "quickFinderByScript-bean", parent = "Uif-QuickFinderByScript"),
        @BeanTag(name = "collectionQuickFinder-bean", parent = "Uif-CollectionQuickFinder")})
public class QuickFinder extends WidgetBase implements LifecycleEventListener {
    private static final long serialVersionUID = 3302390972815386785L;

    // lookup configuration
    private String baseLookupUrl;
    private String dataObjectClassName;
    private String viewName;

    private boolean returnByScript;
    private String readOnlyLookupFields;
    private String referencesToRefresh;
    private String lookupCollectionName;

    private Map<String, String> fieldConversions;
    private Map<String, String> lookupParameters;

    private Action quickfinderAction;
    private LightBox lightBox;

    // lookup view options
    private Boolean renderReturnLink;
    private Boolean renderResultActions;
    private Boolean autoSearch;
    private Boolean renderLookupCriteria;
    private Boolean renderCriteriaActions;
    private Boolean hideCriteriaOnSearch;
    private Boolean renderMaintenanceLinks;
    private Boolean multipleValuesSelect;

    public QuickFinder() {
        super();

        fieldConversions = new HashMap<String, String>();
        lookupParameters = new HashMap<String, String>();
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Registers an event on the quickfinder action</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        super.performInitialization(model);

        ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();
        viewLifecycle.registerLifecycleCompleteListener(quickfinderAction, this);
    }

    /**
     * The following finalization is performed:
     *
     * <ul>
     * <li>Sets up the quickfinder based on whether the parent is an input field or collection group</li>
     * <li>Adds action parameters to the quickfinder action based on the quickfinder configuration</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, Component parent) {
        super.performFinalize(model, parent);

        if (parent.isReadOnly()) {
            setRender(false);
        }

        if (!isRender()) {
            return;
        }
        
        View view = ViewLifecycle.getActiveLifecycle().getView();

        if (parent instanceof InputField) {
            setupForInputField(view, model, (InputField) parent);
        } else if (parent instanceof CollectionGroup) {
            setupForCollectionGroup(view, model, (CollectionGroup) parent);
        }

        setupQuickfinderAction(view, model, parent);
    }

    /**
     * If quickfinder not manually configured attempts to find a relationship to build the quickfinder on, then also
     * adjusts the path for any configured field conversions, lookup parameters, and refresh refreshes.
     *
     * @param view view instance the quickfinder is associated with
     * @param model object containing the view data
     * @param inputField input field instance the quickfinder should apply to
     */
    protected void setupForInputField(View view, Object model, InputField inputField) {
        // if quickfinder class name not specified, attempt to find a relationship to build the quickfinder from
        if (StringUtils.isBlank(dataObjectClassName)) {
            DataObjectRelationship relationship = getRelationshipForField(view, model, inputField);

            // if no relationship found cannot have a quickfinder
            if (relationship == null) {
                setRender(false);

                return;
            }

            dataObjectClassName = relationship.getRelatedClass().getName();

            if ((fieldConversions == null) || fieldConversions.isEmpty()) {
                generateFieldConversions(relationship);
            }

            if ((lookupParameters == null) || lookupParameters.isEmpty()) {
                generateLookupParameters(relationship);
            }
        }

        // adjust paths based on associated attribute field
        updateFieldConversions(inputField.getBindingInfo());
        updateLookupParameters(inputField.getBindingInfo());
        updateReferencesToRefresh(inputField.getBindingInfo());

        // add the quickfinders action as an input field addon
        inputField.addPostInputAddon(quickfinderAction);
    }

    /**
     * Retrieves any {@link org.kuali.rice.krad.bo.DataObjectRelationship} that is associated with the given
     * field and has a configured lookup view.
     *
     * @param view view instance the quickfinder is associated with
     * @param model object containing the view data
     * @param field input field instance the quickfinder should apply to
     * @return data object relationship for the field, or null if one could not be found
     */
    protected DataObjectRelationship getRelationshipForField(View view, Object model, InputField field) {
        String propertyName = field.getBindingInfo().getBindingName();

        // get object instance and class for parent
        Object parentObject = ViewModelUtils.getParentObjectForMetadata(view, model, field);
        Class<?> parentObjectClass = null;
        if (parentObject != null) {
            parentObjectClass = parentObject.getClass();
        }

        // get relationship from metadata service
        return KRADServiceLocatorWeb.getLegacyDataAdapter().getDataObjectRelationship(parentObject,
                parentObjectClass, propertyName, "", true, true, false);
    }

    /**
     * Generates the lookup field conversions based on the references from the given relationship.
     *
     * @param relationship relationship field conversions will be generated from
     */
    protected void generateFieldConversions(DataObjectRelationship relationship) {
        fieldConversions = new HashMap<String, String>();

        for (Map.Entry<String, String> entry : relationship.getParentToChildReferences().entrySet()) {
            String fromField = entry.getValue();
            String toField = entry.getKey();

            fieldConversions.put(fromField, toField);
        }
    }

    /**
     * Generates the lookup parameters based on the references from the given relationship.
     *
     * @param relationship relationship lookup parameters will be generated from
     */
    protected void generateLookupParameters(DataObjectRelationship relationship) {
        lookupParameters = new HashMap<String, String>();

        for (Map.Entry<String, String> entry : relationship.getParentToChildReferences().entrySet()) {
            String fromField = entry.getKey();
            String toField = entry.getValue();

            if (relationship.getUserVisibleIdentifierKey() == null || relationship.getUserVisibleIdentifierKey().equals(
                    fromField)) {
                lookupParameters.put(fromField, toField);
            }
        }
    }

    /**
     * Adjusts the path on the field conversion to property to match the binding path prefix of the
     * given {@link org.kuali.rice.krad.uif.component.BindingInfo}.
     *
     * @param bindingInfo binding info instance to copy binding path prefix from
     */
    protected void updateFieldConversions(BindingInfo bindingInfo) {
        Map<String, String> adjustedFieldConversions = new HashMap<String, String>();
        for (String fromField : fieldConversions.keySet()) {
            String toField = fieldConversions.get(fromField);
            String adjustedToFieldPath = bindingInfo.getPropertyAdjustedBindingPath(toField);

            adjustedFieldConversions.put(fromField, adjustedToFieldPath);
        }

        this.fieldConversions = adjustedFieldConversions;
    }

    /**
     * Adjusts the path on the lookup parameter from property to match the binding path prefix of the
     * given {@link org.kuali.rice.krad.uif.component.BindingInfo}.
     *
     * @param bindingInfo binding info instance to copy binding path prefix from
     */
    protected void updateLookupParameters(BindingInfo bindingInfo) {
        Map<String, String> adjustedLookupParameters = new HashMap<String, String>();
        for (String fromField : lookupParameters.keySet()) {
            String toField = lookupParameters.get(fromField);
            String adjustedFromFieldPath = bindingInfo.getPropertyAdjustedBindingPath(fromField);

            adjustedLookupParameters.put(adjustedFromFieldPath, toField);
        }

        this.lookupParameters = adjustedLookupParameters;
    }

    /**
     * Adjust the path on the referencesToRefresh parameter to match the binding path prefix of the
     * given {@link org.kuali.rice.krad.uif.component.BindingInfo}.
     *
     * @param bindingInfo binding info instance to copy binding path prefix from
     */
    protected void updateReferencesToRefresh(BindingInfo bindingInfo) {
        List<String> adjustedReferencesToRefresh = new ArrayList<String>();

        if (referencesToRefresh == null) {
            referencesToRefresh = new String();
        }

        for (String reference : StringUtils.split(referencesToRefresh, KRADConstants.REFERENCES_TO_REFRESH_SEPARATOR)) {
            adjustedReferencesToRefresh.add(bindingInfo.getPropertyAdjustedBindingPath(reference));
        }

        this.referencesToRefresh = StringUtils.join(adjustedReferencesToRefresh,
                KRADConstants.REFERENCES_TO_REFRESH_SEPARATOR);
    }

    /**
     * Configures the quickfinder for the given collection group instance by setting the data object class,
     * field conversions, and lookup collection name (if necessary).
     *
     * @param view view instance the quickfinder is associated with
     * @param model object containing the view data
     * @param collectionGroup collection group instance to build quickfinder for
     */
    protected void setupForCollectionGroup(View view, Object model, CollectionGroup collectionGroup) {
        // check to see if data object class is configured for lookup, if so we will assume it should be enabled
        // if not and the class configured for the collection group is lookupable, use that
        if (StringUtils.isBlank(getDataObjectClassName())) {
            Class<?> collectionObjectClass = collectionGroup.getCollectionObjectClass();

            boolean isCollectionClassLookupable = KRADServiceLocatorWeb.getViewDictionaryService().isLookupable(
                    collectionObjectClass);
            if (isCollectionClassLookupable) {
                setDataObjectClassName(collectionObjectClass.getName());

                // use PK fields for collection class as default field conversions
                if ((fieldConversions == null) || fieldConversions.isEmpty()) {
                    List<String> collectionObjectPKFields =
                            KRADServiceLocatorWeb.getLegacyDataAdapter().listPrimaryKeyFieldNames(
                                    collectionObjectClass);

                    fieldConversions = new HashMap<String, String>();
                    for (String pkField : collectionObjectPKFields) {
                        fieldConversions.put(pkField, pkField);
                    }
                }
            } else {
                // no available data object class to lookup so disable quickfinder
                setRender(false);
            }
        }

        // set the lookup return collection name to this collection path
        if (isRender() && StringUtils.isBlank(getLookupCollectionName())) {
            setLookupCollectionName(collectionGroup.getBindingInfo().getBindingPath());
        }
    }

    /**
     * Adjusts the id for the quickfinder action, and then adds action parameters for passing along the
     * quickfinder configuration to the lookup view.
     *
     * @param view view instance the quickfinder is associated with
     * @param model object containing the view data
     * @param parent component instance the quickfinder is associated with
     */
    protected void setupQuickfinderAction(View view, Object model, Component parent) {
        quickfinderAction.setId(getId() + UifConstants.IdSuffixes.ACTION);

        if ((lightBox != null) && lightBox.isRender()) {
            String lightboxScript = UifConstants.JsFunctions.CREATE_LIGHTBOX_POST + "(\"" + quickfinderAction.getId()
                    + "\"," + lightBox.getTemplateOptionsJSString() + "," + returnByScript + ");";

            quickfinderAction.setActionScript(lightboxScript);
        }

        quickfinderAction.addActionParameter(UifParameters.BASE_LOOKUP_URL, baseLookupUrl);
        quickfinderAction.addActionParameter(UifParameters.DATA_OBJECT_CLASS_NAME, dataObjectClassName);

        if (!fieldConversions.isEmpty()) {
            quickfinderAction.addActionParameter(UifParameters.CONVERSION_FIELDS, KRADUtils.buildMapParameterString(
                    fieldConversions));
        }

        if (!lookupParameters.isEmpty()) {
            quickfinderAction.addActionParameter(UifParameters.LOOKUP_PARAMETERS, KRADUtils.buildMapParameterString(
                    lookupParameters));
        }

        addActionParameterIfNotNull(UifParameters.VIEW_NAME, viewName);
        addActionParameterIfNotNull(UifParameters.READ_ONLY_FIELDS, readOnlyLookupFields);
        addActionParameterIfNotNull(UifParameters.RENDER_RETURN_LINK, renderReturnLink);
        addActionParameterIfNotNull(UifParameters.RENDER_RESULT_ACTIONS, renderResultActions);
        addActionParameterIfNotNull(UifParameters.REFERENCES_TO_REFRESH, referencesToRefresh);
        addActionParameterIfNotNull(UifParameters.AUTO_SEARCH, autoSearch);
        addActionParameterIfNotNull(UifParameters.RENDER_LOOKUP_CRITERIA, renderLookupCriteria);
        addActionParameterIfNotNull(UifParameters.RENDER_CRITERIA_ACTIONS, renderCriteriaActions);
        addActionParameterIfNotNull(UifParameters.HIDE_CRITERIA_ON_SEARCH, hideCriteriaOnSearch);
        addActionParameterIfNotNull(UifParameters.RENDER_MAINTENANCE_LINKS, renderMaintenanceLinks);
        addActionParameterIfNotNull(UifParameters.MULTIPLE_VALUES_SELECT, multipleValuesSelect);
        addActionParameterIfNotNull(UifParameters.LOOKUP_COLLECTION_NAME, lookupCollectionName);
        addActionParameterIfNotNull(UifParameters.QUICKFINDER_ID, getId());
    }

    /**
     * Utility method to add an action parameter to the quickfinder action if the given parameter value
     * is non blank.
     *
     * @param parameterName name of the parameter to add
     * @param parameterValue value for the parameter to add
     */
    protected void addActionParameterIfNotNull(String parameterName, Object parameterValue) {
        if ((parameterValue != null) && StringUtils.isNotBlank(parameterValue.toString())) {
            quickfinderAction.addActionParameter(parameterName, parameterValue.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(quickfinderAction);
        components.add(lightBox);

        return components;
    }

    /**
     * Adds post context data for the quickfinder so when the lookup return occurs the focus and jump point
     * of the quickfinder action can be retrieved.
     *
     * @see org.kuali.rice.krad.uif.lifecycle.LifecycleEventListener#processEvent(org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent,
     *      org.kuali.rice.krad.uif.view.View, java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void processEvent(ViewLifecycle.LifecycleEvent lifecycleEvent, View view, Object model,
            Component eventComponent) {
        Action finalQuickfinderAction = (Action) eventComponent;

        // add post metadata for focus point when the associated lookup returns
        view.getViewIndex().addPostContextEntry(getId(), UifConstants.PostContextKeys.QUICKFINDER_FOCUS_ID,
                finalQuickfinderAction.getFocusOnIdAfterSubmit());
        view.getViewIndex().addPostContextEntry(getId(), UifConstants.PostContextKeys.QUICKFINDER_JUMP_TO_ID,
                finalQuickfinderAction.getJumpToIdAfterSubmit());
    }

    /**
     * Returns the URL for the lookup for which parameters will be added.
     *
     * <p>The base URL includes the domain, context, and controller mapping for the lookup invocation. Parameters are
     * then added based on configuration to complete the URL. This is generally defaulted to the application URL and
     * internal KRAD servlet mapping, but can be changed to invoke another application such as the Rice standalone
     * server</p>
     *
     * @return lookup base URL
     */
    @BeanTagAttribute(name = "baseLookupUrl")
    public String getBaseLookupUrl() {
        return this.baseLookupUrl;
    }

    /**
     * @see QuickFinder#getBaseLookupUrl()
     */
    public void setBaseLookupUrl(String baseLookupUrl) {
        this.baseLookupUrl = baseLookupUrl;
    }

    /**
     * Full class name the lookup should be provided for.
     *
     * <p>This is passed on to the lookup request for the data object the lookup should be rendered for. This is then
     * used by the lookup framework to select the lookup view (if more than one lookup view exists for the same
     * data object class name, the {@link #getViewName()} property should be specified to select the view to
     * render).</p>
     *
     * @return lookup class name
     */
    @BeanTagAttribute(name = "dataOjbectClassName")
    public String getDataObjectClassName() {
        return this.dataObjectClassName;
    }

    /**
     * @see QuickFinder#getDataObjectClassName()
     */
    public void setDataObjectClassName(String dataObjectClassName) {
        this.dataObjectClassName = dataObjectClassName;
    }

    /**
     * When multiple target lookup views exists for the same data object class, the view name can be set to
     * determine which one to use.
     *
     * <p>When creating multiple lookup views for the same data object class, the view name can be specified for the
     * different versions (for example 'simple' and 'advanced'). When multiple lookup views exist the view name must
     * be sent with the data object class for the request. Note the view id can be alternatively used to uniquely
     * identify the lookup view</p>
     *
     * @return String name of lookup view
     */
    @BeanTagAttribute(name = "viewName")
    public String getViewName() {
        return this.viewName;
    }

    /**
     * @see QuickFinder#getViewName()
     */
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    /**
     * Indicates whether the lookup return should occur through script, or by refresing the page (making server
     * request).
     *
     * <p>For quickfinders that do not need any additional server side action, return through script can be
     * much faster and prevents a page refresh.</p>
     *
     * @return boolean true if the return should occur through script, false if not (default)
     */
    public boolean isReturnByScript() {
        return returnByScript;
    }

    /**
     * @see QuickFinder#isReturnByScript()
     */
    public void setReturnByScript(boolean returnByScript) {
        this.returnByScript = returnByScript;
    }

    /**
     * Comma delimited String of property names on the lookup view that should be read only.
     *
     * <p>When requesting a lookup view, property names for fields that are rendered as search criteria can be marked
     * as read-only. This is usually done when a lookup parameter for that property is sent in and the user should
     * not be allowed to change the value</p>
     *
     * @return property names (delimited by a comma) whose criteria fields should be read-only on the
     *         lookup view
     */
    @BeanTagAttribute(name = "readOnlyLookupFields")
    public String getReadOnlyLookupFields() {
        return this.readOnlyLookupFields;
    }

    /**
     * @see QuickFinder#setReadOnlyLookupFields(java.lang.String)
     */
    public void setReadOnlyLookupFields(String readOnlyLookupFields) {
        this.readOnlyLookupFields = readOnlyLookupFields;
    }

    /**
     * List of property names on the model that should be refreshed when the lookup returns.
     *
     * <p>Note this is only relevant when the return by script option is not enabled (meaning the server will be
     * invoked
     * on the lookup return call)</p>
     *
     * <p>When a lookup return call is made (to return a result value) the controller refresh method will be invoked.
     * If
     * refresh properties are configured, a call to refresh those references from the database will be made. This is
     * useful if the lookup returns a foreign key field and the related record is needed.</p>
     *
     * @return list of property names to refresh
     */
    @BeanTagAttribute(name = "referencesToRefresh")
    public String getReferencesToRefresh() {
        return this.referencesToRefresh;
    }

    /**
     * @see QuickFinder#getReferencesToRefresh()
     */
    public void setReferencesToRefresh(String referencesToRefresh) {
        this.referencesToRefresh = referencesToRefresh;
    }

    /**
     * Map that determines what properties from a result lookup row (if selected) will be returned to properties on
     * the calling view.
     *
     * <p>The purpose of using the lookup is to search for a particular value and return that value to the form being
     * completed. In order for the lookup framework to return the field back to us, we must specify the name of the
     * field on the data object class whose value we need, and the name of the field on the calling view. Furthermore,
     * we can choose to have the lookup return additional fields that populate other form fields or informational
     * properties (see ‘Field Queries and Informational Properties’). These pairs of fields are known as
     * ‘field conversions’.</p>
     *
     * <p>The fieldConversions property is a Map. Each entry represents a field that will be returned back from the
     * lookup, with the entry key being the field name on the data object class, and the entry value being the field
     * name on the calling view. It is helpful to think of this as a from-to mapping. Pulling from the data object
     * field (map key) to the calling view field (map value).</p>
     *
     * @return mapping of lookup data object property names to view property names
     */
    @BeanTagAttribute(name = "fieldConversions", type = BeanTagAttribute.AttributeType.MAPVALUE)
    public Map<String, String> getFieldConversions() {
        return this.fieldConversions;
    }

    /**
     * @see QuickFinder#getFieldConversions()
     */
    public void setFieldConversions(Map<String, String> fieldConversions) {
        this.fieldConversions = fieldConversions;
    }

    /**
     * Map that determines what properties from a calling view will be sent to properties on that are rendered
     * for the lookup view's search fields (they can be hidden).
     *
     * <p> When invoking a lookup view, we can pre-populate search fields on the lookup view with data from the view
     * that called the lookup. The user can then perform the search with these values, or (if edited is allowed or
     * the fields are not hidden) change the passed in values. When the lookup is invoked, the values for the
     * properties configured within the lookup parameters Map will be pulled and passed along as values for the
     * lookup view properties</p>
     *
     * @return mapping of calling view properties to lookup view search fields
     */
    @BeanTagAttribute(name = "lookupParameters", type = BeanTagAttribute.AttributeType.MAPVALUE)
    public Map<String, String> getLookupParameters() {
        return this.lookupParameters;
    }

    /**
     * @see QuickFinder#getLookupParameters()
     */
    public void setLookupParameters(Map<String, String> lookupParameters) {
        this.lookupParameters = lookupParameters;
    }

    /**
     * Indicates whether the return links for lookup results should be rendered.
     *
     * <p>A lookup view can be invoked to allow the user to select a value (or set of values) to return back to the
     * calling view. For single value lookups this is done with a return link that is rendered for each row. This
     * return link can be disabled by setting this property to false</p>
     *
     * @return true if the return link should not be shown, false if it should be
     */
    @BeanTagAttribute(name = "renderReturnLink")
    public Boolean getRenderReturnLink() {
        return this.renderReturnLink;
    }

    /**
     * @see QuickFinder#getRenderReturnLink()
     */
    public void setRenderReturnLink(Boolean renderReturnLink) {
        this.renderReturnLink = renderReturnLink;
    }

    /**
     * Indicates whether the maintenance actions (or others) are rendered on the invoked lookup view.
     *
     * <p>By default a lookup view will add an actions column for the result table that display maintenance links (in
     * addition to a new link at the top of the page) if a maintenance action is available. Custom links can also be
     * added to the action column as necessary. This flag can be set to true to suppress the rendering of the actions
     * for the lookup call.</p>
     *
     * @return true if actions should be rendered, false if not
     */
    @BeanTagAttribute(name = "renderResultActions")
    public Boolean getRenderResultActions() {
        return renderResultActions;
    }

    /**
     * @see QuickFinder#getRenderResultActions()
     */
    public void setRenderResultActions(Boolean renderResultActions) {
        this.renderResultActions = renderResultActions;
    }

    /**
     * Indicates whether the search should be executed when first rendering the lookup view.
     *
     * <p>By default the lookup view is rendered, the user enters search values and executes the results. This flag can
     * be set to true to indicate the search should be performed before showing the screen to the user. This is
     * generally used when search criteria is being passed in as well</p>
     *
     * @return true if the search should be performed initially, false if not
     */
    @BeanTagAttribute(name = "autoSearch")
    public Boolean getAutoSearch() {
        return this.autoSearch;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute#name()
     */
    public void setAutoSearch(Boolean autoSearch) {
        this.autoSearch = autoSearch;
    }

    /**
     * Indicates whether the lookup criteria (search group) should be enabled on the invoked lookup view.
     *
     * <p> Setting the this to false will not display the lookup criteria but only the results. Therefore this is only
     * useful when setting {@link #getAutoSearch()} to true and passing in criteria</p>
     *
     * @return true if lookup criteria should be displayed, false if not
     */
    @BeanTagAttribute(name = "renderLookupCriteria")
    public Boolean getRenderLookupCriteria() {
        return this.renderLookupCriteria;
    }

    /**
     * @see QuickFinder#getRenderLookupCriteria()
     */
    public void setRenderLookupCriteria(Boolean renderLookupCriteria) {
        this.renderLookupCriteria = renderLookupCriteria;
    }

    /**
     * Indicates whether the criteria actions (footer) should be rendered on the invoked lookup view.
     *
     * @return boolean true if actions should be rendered (default), false if not
     */
    @BeanTagAttribute(name = "renderCriteriaActions")
    public Boolean getRenderCriteriaActions() {
        return this.renderCriteriaActions;
    }

    /**
     * @see QuickFinder#getRenderCriteriaActions()
     */
    public void setRenderCriteriaActions(Boolean renderCriteriaActions) {
        this.renderCriteriaActions = renderCriteriaActions;
    }

    public Boolean getHideCriteriaOnSearch() {
        return hideCriteriaOnSearch;
    }

    public void setHideCriteriaOnSearch(Boolean hideCriteriaOnSearch) {
        this.hideCriteriaOnSearch = hideCriteriaOnSearch;
    }

    /**
     * Indicates whether the maintenance action links should be rendered for the invoked lookup view.
     *
     * <p>If a maintenance view exists for the data object associated with the lookup view, the framework will add
     * links to initiate a new maintenance document. This flag can be used to disable the rendering of these links</p>
     *
     * <p> Note this serves similar purpose to {@link #getRenderResultActions()} but the intent is to only remove the
     * maintenance links in this situation, not the complete actions column</p>
     *
     * @return true if maintenance links should be shown on the lookup view, false if not
     */
    @BeanTagAttribute(name = "renderMaintenanceLinks")
    public Boolean getRenderMaintenanceLinks() {
        return this.renderMaintenanceLinks;
    }

    /**
     * @see QuickFinder#getRenderMaintenanceLinks()
     */
    public void setRenderMaintenanceLinks(Boolean renderMaintenanceLinks) {
        this.renderMaintenanceLinks = renderMaintenanceLinks;
    }

    /**
     * Action component that is used to rendered for the field for invoking the quickfinder action (bringing up the
     * lookup).
     *
     * <p>Through the action configuration the image (or link, button) rendered for the quickfinder can be modified. In
     * addition to other action component settings</p>
     *
     * @return Action instance rendered for quickfinder
     */
    @BeanTagAttribute(name = "quickfinderAction", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Action getQuickfinderAction() {
        return this.quickfinderAction;
    }

    /**
     * @see QuickFinder#getQuickfinderAction()
     */
    public void setQuickfinderAction(Action quickfinderAction) {
        this.quickfinderAction = quickfinderAction;
    }

    /**
     * Lightbox widget that will be used to view the invoked lookup view.
     *
     * <p>Note if the lightbox is not configured, or set to not render the lookup will be invoked based on
     * the action alone (for example a new tab/window)</p>
     *
     * @return lightbox instance for viewing the lookup
     */
    @BeanTagAttribute(name = "lightBox", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public LightBox getLightBox() {
        return lightBox;
    }

    /**
     * @see QuickFinder#getLightBox()
     */
    public void setLightBox(LightBox lightBox) {
        this.lightBox = lightBox;
    }

    /**
     * Indicates whether the invoked lookup view should allow multiple values to be selected and returned.
     *
     * @return true if multi-value lookup should be requested, false for normal lookup
     */
    @BeanTagAttribute(name = "MultipleValuesSelect")
    public Boolean getMultipleValuesSelect() {
        return multipleValuesSelect;
    }

    /**
     * @see QuickFinder#getMultipleValuesSelect()
     */
    public void setMultipleValuesSelect(Boolean multipleValuesSelect) {
        this.multipleValuesSelect = multipleValuesSelect;
    }

    /**
     * For the case of multi-value lookup, indicates the collection that should be populated with
     * the return results.
     *
     * <p>Note when the quickfinder is associated with a {@link CollectionGroup}, this property is
     * set automatically from the collection name associated with the group</p>
     *
     * @return collection name (must be full binding path)
     */
    @BeanTagAttribute(name = "lookupCollectionName")
    public String getLookupCollectionName() {
        return lookupCollectionName;
    }

    /**
     * @see QuickFinder#getLookupCollectionName()
     */
    public void setLookupCollectionName(String lookupCollectionName) {
        this.lookupCollectionName = lookupCollectionName;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DictionaryBeanBase#copyProperties(Object)
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        QuickFinder quickFinderCopy = (QuickFinder) component;

        quickFinderCopy.setBaseLookupUrl(this.baseLookupUrl);
        quickFinderCopy.setDataObjectClassName(this.dataObjectClassName);
        quickFinderCopy.setViewName(this.viewName);
        quickFinderCopy.setReferencesToRefresh(this.referencesToRefresh);

        if (fieldConversions != null) {
            quickFinderCopy.setFieldConversions(new HashMap<String, String>(this.fieldConversions));
        }

        if (lookupParameters != null) {
            quickFinderCopy.setLookupParameters(new HashMap<String, String>(this.lookupParameters));
        }

        quickFinderCopy.setReturnByScript(this.returnByScript);
        quickFinderCopy.setReadOnlyLookupFields(this.readOnlyLookupFields);
        quickFinderCopy.setRenderReturnLink(this.renderReturnLink);
        quickFinderCopy.setRenderResultActions(this.renderResultActions);
        quickFinderCopy.setAutoSearch(this.autoSearch);
        quickFinderCopy.setRenderLookupCriteria(this.renderLookupCriteria);
        quickFinderCopy.setRenderCriteriaActions(this.renderCriteriaActions);
        quickFinderCopy.setHideCriteriaOnSearch(this.hideCriteriaOnSearch);
        quickFinderCopy.setRenderMaintenanceLinks(this.renderMaintenanceLinks);
        quickFinderCopy.setMultipleValuesSelect(this.multipleValuesSelect);
        quickFinderCopy.setLookupCollectionName(this.lookupCollectionName);

        if (lightBox != null) {
            quickFinderCopy.setLightBox((LightBox) this.lightBox.copy());
        }

        if (this.quickfinderAction != null) {
            quickFinderCopy.setQuickfinderAction((Action) this.quickfinderAction.copy());
        }
    }
}
