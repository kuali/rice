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
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.MethodInvokerConfig;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.lifecycle.LifecycleEventListener;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;

/**
 * Widget for navigating to a lookup from a field (called a quickfinder).
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "quickFinder", parent = "Uif-QuickFinder"),
        @BeanTag(name = "quickFinderByScript", parent = "Uif-QuickFinderByScript"),
        @BeanTag(name = "collectionQuickFinder", parent = "Uif-CollectionQuickFinder")})
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
    private String lookupCollectionId;

    private Map<String, String> fieldConversions;
    private Map<String, String> lookupParameters;
    private Map<String, String> additionalLookupParameters;

    private Action quickfinderAction;

    private String lookupDialogId;
    private boolean openInDialog;

    // lookup view options
    private Boolean renderReturnLink;
    private Boolean renderResultActions;
    private Boolean autoSearch;
    private Boolean renderLookupCriteria;
    private Boolean renderCriteriaActions;
    private Boolean hideCriteriaOnSearch;
    private Boolean renderMaintenanceLinks;
    private Boolean multipleValuesSelect;

    private String callbackMethodToCall;
    private MethodInvokerConfig callbackMethod;
    private Map<String, String> callbackContext;

    public QuickFinder() {
        super();

        fieldConversions = new HashMap<String, String>();
        lookupParameters = new HashMap<String, String>();
        lookupDialogId = "";
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
     * Inherits readOnly from parent if not explicitly populated.
     * 
     * {@inheritDoc}
     */
    @Override
    public void afterEvaluateExpression() {
        super.afterEvaluateExpression();
        
        if (getReadOnly() == null) {
            Component parent = ViewLifecycle.getPhase().getParent();
            setReadOnly(parent == null ? null : parent.getReadOnly());
        }
    }

    /**
     * The following finalization is performed:
     *
     * <ul>
     * <li>Sets up the quickfinder based on whether the parent is an input field or collection group</li>
     * <li>Adds action parameters to the quickfinder action based on the quickfinder configuration</li>
     * <li>Adds callback parameters to post data if present</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        if (parent instanceof Component && Boolean.TRUE.equals(((Component) parent).getReadOnly())) {
            setRender(false);
        }

        if (!isRender()) {
            return;
        }
        
        View view = ViewLifecycle.getActiveLifecycle().getView();

        if (parent instanceof InputField) {
            setupForInputField(view, model, (InputField) parent);

            // add field conversions as accessible binding paths
            if (isRender()) {
                for (String toField : fieldConversions.values()) {
                    ViewLifecycle.getViewPostMetadata().addAccessibleBindingPath(toField);
                }
            }
        } else if (parent instanceof CollectionGroup) {
            setupForCollectionGroup(view, model, (CollectionGroup) parent);
        }

        setupQuickfinderAction(view, model, parent);

        addCallbackParametersIfPresent();
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
        if (parentObjectClass != null) {
            return KRADServiceLocatorWeb.getLegacyDataAdapter().getDataObjectRelationship(parentObject,
                    parentObjectClass, propertyName, "", true, true, false);
        }

        return null;
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

            if (!StringUtils.startsWith(toField, bindingInfo.getBindingPathPrefix())) {
                String adjustedToFieldPath = bindingInfo.getPropertyAdjustedBindingPath(toField);
                adjustedFieldConversions.put(fromField, adjustedToFieldPath);
            }  else {
                adjustedFieldConversions.put(fromField, toField);
            }
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

        if (isRender() && StringUtils.isBlank(getLookupCollectionId())) {
            setLookupCollectionId(collectionGroup.getId());
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
    protected void setupQuickfinderAction(View view, Object model, LifecycleElement parent) {
        quickfinderAction.setId(getId() + UifConstants.IdSuffixes.ACTION);

        if (openInDialog) {
            String lightboxScript = UifConstants.JsFunctions.SHOW_LOOKUP_DIALOG + "(\"" + quickfinderAction.getId()
                    + "\"," + returnByScript + ",\"" + lookupDialogId + "\");";

            quickfinderAction.setActionScript(lightboxScript);
        }

        quickfinderAction.addActionParameter(UifParameters.BASE_LOOKUP_URL, baseLookupUrl);

        Class dataObjectClass = getDataObjectClass(dataObjectClassName);
        ModuleService responsibleModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(dataObjectClass);
        if (responsibleModuleService != null && responsibleModuleService.isExternalizable(dataObjectClass)) {
            if (ExternalizableBusinessObject.class.isAssignableFrom(dataObjectClass)) {
                Class implementationClass = responsibleModuleService.getExternalizableBusinessObjectImplementation(dataObjectClass.asSubclass(
                        ExternalizableBusinessObject.class));
                if (implementationClass != null) {
                    dataObjectClassName = implementationClass.getName();
                }
            }
        }

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
        addActionParameterIfNotNull(UifParameters.LOOKUP_COLLECTION_ID, lookupCollectionId);
        addActionParameterIfNotNull(UifParameters.QUICKFINDER_ID, getId());

        //insert additional lookup parameters.
        if (additionalLookupParameters != null) {
            //copy additional parameters to actionParameters
            Map<String, String> actionParameters = quickfinderAction.getActionParameters();
            actionParameters.putAll(additionalLookupParameters);
            quickfinderAction.setActionParameters(actionParameters);
        }
    }

    private Class<?> getDataObjectClass(String className) {
        Class<?> dataObjectClass;

        try {
            dataObjectClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to get class for name: " + className, e);
        }

        return dataObjectClass;
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
     * Adds post context data for the quickfinder so when the lookup return occurs the focus and jump point
     * of the quickfinder action can be retrieved.
     *
     * {@inheritDoc}
     */
    @Override
    public void processEvent(ViewLifecycle.LifecycleEvent lifecycleEvent, View view, Object model,
            LifecycleElement eventComponent) {
        Action finalQuickfinderAction = (Action) eventComponent;

        // add post metadata for focus point when the associated lookup returns
        ViewLifecycle.getViewPostMetadata().addComponentPostData(this,
                UifConstants.PostMetadata.QUICKFINDER_FOCUS_ID, finalQuickfinderAction.getFocusOnIdAfterSubmit());
        ViewLifecycle.getViewPostMetadata().addComponentPostData(this,
                UifConstants.PostMetadata.QUICKFINDER_JUMP_TO_ID, finalQuickfinderAction.getJumpToIdAfterSubmit());
    }

    /**
     * Adds callback method and its parameters to post data so that when a refresh occurs it knows
     * which view is returned from and possibly which collection line the quickfinder was on.
     */
    protected void addCallbackParametersIfPresent() {
        if (StringUtils.isNotBlank(callbackMethodToCall)) {
            ViewLifecycle.getViewPostMetadata().addComponentPostData(this,
                    UifConstants.PostMetadata.QUICKFINDER_CALLBACK_METHOD_TO_CALL, callbackMethodToCall);
        }

        if (callbackMethod != null) {
            ViewLifecycle.getViewPostMetadata().addComponentPostData(this,
                    UifConstants.PostMetadata.QUICKFINDER_CALLBACK_METHOD, callbackMethod);
        }

        if (callbackContext != null && !callbackContext.isEmpty()) {
            ViewLifecycle.getViewPostMetadata().addComponentPostData(this,
                    UifConstants.PostMetadata.QUICKFINDER_CALLBACK_CONTEXT, callbackContext);
        }
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
    @BeanTagAttribute
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
    @BeanTagAttribute
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
    @BeanTagAttribute
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
    @BeanTagAttribute
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
    @BeanTagAttribute
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
    @BeanTagAttribute
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
    @BeanTagAttribute
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
    @BeanTagAttribute
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
    @BeanTagAttribute
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
    @BeanTagAttribute
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
    @BeanTagAttribute
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
    @BeanTagAttribute
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
    @BeanTagAttribute
    public Boolean getRenderCriteriaActions() {
        return this.renderCriteriaActions;
    }

    /**
     * @see QuickFinder#getRenderCriteriaActions()
     */
    public void setRenderCriteriaActions(Boolean renderCriteriaActions) {
        this.renderCriteriaActions = renderCriteriaActions;
    }

    /**
     * Indicates whether the lookup criteria should be hidden when a search is executed.
     *
     * @return boolean true if criteria should be hidden, false if not
     */
    @BeanTagAttribute
    public Boolean getHideCriteriaOnSearch() {
        return hideCriteriaOnSearch;
    }

    /**
     * @see QuickFinder#getHideCriteriaOnSearch()
     */
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
    @BeanTagAttribute
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
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.BYTYPE)
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
     * The id of the DialogGroup to use when the openInDialog property is true.
     *
     * <p>The DialogGroup should only contain an iframe for its items.  When not set, a default dialog
     * will be used.</p>
     *
     * @return the id of the dialog to use for this quickfinder
     */
    @BeanTagAttribute
    public String getLookupDialogId() {
        return lookupDialogId;
    }

    /**
     * @see QuickFinder#getLookupDialogId()
     */
    public void setLookupDialogId(String lookupDialogId) {
        this.lookupDialogId = lookupDialogId;
    }

    /**
     * True if the quickfinder's lookup should be opened in a dialog; true is the default setting for the
     * bean.
     *
     * @return true if the lookup should be opened in a dialog, false to open in a new window
     */
    @BeanTagAttribute
    public boolean isOpenInDialog() {
        return openInDialog;
    }

    /**
     * @see QuickFinder#isOpenInDialog()
     */
    public void setOpenInDialog(boolean openInDialog) {
        this.openInDialog = openInDialog;
    }

    /**
     * Indicates whether the invoked lookup view should allow multiple values to be selected and returned.
     *
     * @return true if multi-value lookup should be requested, false for normal lookup
     */
    @BeanTagAttribute
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
    @BeanTagAttribute
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
     * For the case of multi-value lookup, indicates the collection id that should be populated with
     * the return results.
     *
     * <p>Note when the quickfinder is associated with a {@link CollectionGroup}, this property is
     * set automatically from the collection id associated with the group</p>
     *
     * @return collection id
     */
    @BeanTagAttribute
    public String getLookupCollectionId() {
        return lookupCollectionId;
    }

    /**
     * @see QuickFinder#getLookupCollectionId()
     */
    public void setLookupCollectionId(String lookupCollectionId) {
        this.lookupCollectionId = lookupCollectionId;
    }

    /**
     * The additional parameters that were passed to the quickFinder.
     *
     * @return additionalLookupParameters - map of additional lookup parameters
     */
    @BeanTagAttribute
    public Map<String, String> getAdditionalLookupParameters() {
        return additionalLookupParameters;
    }

    /**
     * @see QuickFinder#getAdditionalLookupParameters()
     */
    public void setAdditionalLookupParameters(Map<String, String> additionalLookupParameters) {
        this.additionalLookupParameters = additionalLookupParameters;
    }

    /**
     * The name of the callback method to invoke in the view helper service that checks
     * request parameters to indicate what view is being returned from.
     *
     * @return callbackMethodToCall - the name of the callback method
     */
    public String getCallbackMethodToCall() {
        return callbackMethodToCall;
    }

    /**
     * @see QuickFinder#getCallbackMethodToCall()
     */
    public void setCallbackMethodToCall(String callbackMethodToCall) {
        this.callbackMethodToCall = callbackMethodToCall;
    }

    /**
     * The specific method invoker to use to invoke the callback method to call.
     *
     * @return callbackMethod - the method invoker
     */
    public MethodInvokerConfig getCallbackMethod() {
        return callbackMethod;
    }

    /**
     * @see QuickFinder#getCallbackMethod()
     */
    public void setCallbackMethod(MethodInvokerConfig callbackMethod) {
        this.callbackMethod = callbackMethod;
    }

    /**
     * The context of parameters to be provided to the callback method to call.
     *
     * @return callbackContext - map of parameters
     */
    public Map<String, String> getCallbackContext() {
        return callbackContext;
    }

    /**
     * @see QuickFinder#getCallbackContext()
     */
    public void setCallbackContext(Map<String, String> callbackContext) {
        this.callbackContext = callbackContext;
    }
}
