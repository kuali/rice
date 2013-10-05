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
package org.kuali.rice.krad.uif.lifecycle;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.data.DataObjectUtils;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.messages.MessageService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.ClientSideState;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.component.PropertyReplacer;
import org.kuali.rice.krad.uif.component.RequestParameter;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.container.LightTable;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.field.ActionField;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.field.RemoteFieldsHolder;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.modifier.ComponentModifier;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.BooleanMap;
import org.kuali.rice.krad.uif.util.CloneUtils;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ExpressionUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPathExpressionParser;
import org.kuali.rice.krad.uif.util.ObjectPathExpressionParser.PathEntry;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewAuthorizer;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.view.ViewPresentationController;
import org.kuali.rice.krad.uif.widget.Widget;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.GrowlMessage;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.valuefinder.ValueFinder;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.util.MethodInvoker;

/**
 * Lifecycle object created during the view processing to hold event registrations.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see LifecycleEventListener
 */
@SuppressWarnings("deprecation")
public class ViewLifecycle implements ViewLifecycleResult, Serializable {

    private static Logger LOG = Logger.getLogger(ViewLifecycle.class);

    private static final long serialVersionUID = -4767600614111642241L;

    /**
     * Thread local view context reference.
     */
    private static ThreadLocal<ViewLifecycle> TL_VIEW_LIFECYCLE = new ThreadLocal<ViewLifecycle>();

    /**
     * Enumerates potential lifecycle events.
     */
    public static enum LifecycleEvent {
        LIFECYCLE_COMPLETE
    }

    /**
     * Registration of an event.
     */
    protected class EventRegistration implements Serializable {

        private static final long serialVersionUID = -5077429381388641016L;

        /**
         * The event to listen for.
         */
        private LifecycleEvent event;

        /**
         * The component to notify when the event passes.
         */
        private Component eventComponent;

        /**
         * The event listener.
         */
        private LifecycleEventListener eventListener;

        /**
         * Private constructor.
         * 
         * @param event The event to listen for.
         * @param eventComponent The component to notify.
         * @param eventListener The event listener.
         */
        private EventRegistration(LifecycleEvent event, Component eventComponent,
                LifecycleEventListener eventListener) {
            this.event = event;
            this.eventComponent = eventComponent;
            this.eventListener = eventListener;
        }

        /**
         * Event the registration is for.
         * 
         * @return The event this registration is for.
         * @see LifecycleEvent
         */
        public LifecycleEvent getEvent() {
            return event;
        }

        /**
         * Component instance the event should occur for/on.
         * 
         * @return Component instance for event
         */
        public Component getEventComponent() {
            return eventComponent;
        }

        /**
         * Listener class that should be invoked when the event occurs.
         * 
         * @return LifecycleEventListener instance
         */
        public LifecycleEventListener getEventListener() {
            return eventListener;
        }

        /**
         * @see EventRegistration#getEvent()
         */
        public void setEvent(LifecycleEvent event) {
            this.event = event;
        }

        /**
         * @see EventRegistration#getEventComponent()
         */
        public void setEventComponent(Component eventComponent) {
            this.eventComponent = eventComponent;
        }

        /**
         * @see EventRegistration#getEventListener()
         */
        public void setEventListener(LifecycleEventListener eventListener) {
            this.eventListener = eventListener;
        }
    }

    /**
     * List of event registrations.
     */
    private final List<EventRegistration> eventRegistrations;

    /**
     * The helper service active on this context.
     */
    private final ViewHelperService helper;

    /**
     * Set of mutable element identities.
     */
    private final Set<Long> mutableIdentities;

    /**
     * The original view associated with this context.
     */
    private final View originalView;

    /**
     * A mutable copy of the view private to this execution context.
     */
    private View view;

    /**
     * A mutable copy of the refresh component, private to this execution context.
     */
    private Component refreshComponent;

    /**
     * Flag indicating if the purpose of this lifecycle context is to encapsulate a view copy.
     */
    private final boolean copy;

    /**
     * Private constructor, for spawning an initialization context.
     * 
     * @see #getActiveLifecycle() For access to a thread-local instance.
     */
    private ViewLifecycle() {
        this.originalView = null;
        this.helper = null;
        this.eventRegistrations = null;
        this.mutableIdentities = null;
        this.copy = false;
    }

    /**
     * Private constructor, for spawning a lifecycle context.
     * 
     * @see #getActiveLifecycle() For access to a thread-local instance.
     */
    private ViewLifecycle(View view, boolean copy) {
        this.originalView = view;
        this.helper = view.getViewHelperService();
        this.eventRegistrations = new ArrayList<EventRegistration>();
        this.mutableIdentities = new HashSet<Long>();
        this.copy = copy;
    }

    /**
     * Helper method for optimzing a call to
     * {@link ViewModelUtils#getPropertyTypeByClassAndView(View, String)} while parsing a path
     * expression for an attribute definition.
     *
     * @param formClass The view's form class.
     * @param modelClasses The view's model classes mapping.
     * @param rootPath The root path of the parse.
     * @param parentPath The parent path of the current parse entry.
     * @return The name of the dictionary entry to check at the current parse node.
     */
    private String getDictionaryEntryName(Object model, Map<String, Class<?>> modelClasses, String rootPath,
            String parentPath) {
        String modelClassPath = getModelClassPath(rootPath, parentPath);

        if (modelClassPath == null) {
            return null;
        }

        Class<?> dictionaryModelClass = modelClasses.get(modelClassPath);

        // full match
        if (dictionaryModelClass != null) {
            return dictionaryModelClass.getName();
        }

        // in case of partial match, holds the class that matched and the
        // property so we can get by reflection
        Class<?> modelClass = null;
        String modelProperty = modelClassPath;

        int bestMatchLength = 0;
        int modelClassPathLength = modelClassPath.length();

        // check if property path matches one of the modelClass entries
        for (Entry<String, Class<?>> modelClassEntry : modelClasses.entrySet()) {
            String path = modelClassEntry.getKey();
            int pathlen = path.length();

            if (modelClassPath.startsWith(path) && pathlen > bestMatchLength
                    && modelClassPathLength > pathlen && modelClassPath.charAt(pathlen + 1) == '.') {
                bestMatchLength = pathlen;
                modelClass = modelClassEntry.getValue();
                modelProperty = modelClassPath.substring(pathlen + 1);
            }
        }

        if (modelClass != null) {
            // if a partial match was found, look up the property type based on matched model class
            dictionaryModelClass = ObjectPropertyUtils.getPropertyType(modelClass, modelProperty);
        }

        if (dictionaryModelClass == null) {
            // If no full or partial match, look up based on the model directly
            dictionaryModelClass = ObjectPropertyUtils.getPropertyType(model, modelClassPath);
        }

        return dictionaryModelClass == null ? null : dictionaryModelClass.getName();
    }

    /**
     * Helper method for forming the model class path while parsing a path expression.
     *
     * @param rootPath The root parse path.
     * @param parentPath The parent path of the current parse node.
     * @return A model class path formed by concatenating the root path and parent path with a dot
     *         separator, then removing all collection index/key references.
     */
    private String getModelClassPath(String rootPath, String parentPath) {
        if (rootPath == null && parentPath == null) {
            return null;
        }

        StringBuilder modelClassPathBuilder = new StringBuilder();

        if (rootPath != null) {
            modelClassPathBuilder.append(rootPath);
        }

        if (parentPath != null) {
            if (rootPath != null) modelClassPathBuilder.append('.');
            modelClassPathBuilder.append(parentPath);
        }

        int bracketCount = 0;
        int leftBracketPos = -1;
        for (int i=0; i < modelClassPathBuilder.length(); i++) {
            char c = modelClassPathBuilder.charAt(i);

            if (c == '[') {
                bracketCount++;
                if (bracketCount == 1) leftBracketPos = i;
            }

           if (c == ']') {
               bracketCount--;

               if (bracketCount < 0) {
                   throw new IllegalArgumentException("Unmatched ']' at " + i + " " + modelClassPathBuilder);
               }

               if (bracketCount == 0) {
                   modelClassPathBuilder.delete(leftBracketPos, i + 1);
                   i -= i + 1 - leftBracketPos;
                   leftBracketPos = -1;
               }
           }
        }

        if (bracketCount > 0) {
            throw new IllegalArgumentException("Unmatched '[' at " + leftBracketPos + " " + modelClassPathBuilder);
        }

        return modelClassPathBuilder.toString();
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#applyDefaultValuesForCollectionLine(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.container.CollectionGroup, java.lang.Object)
     */
    public void applyDefaultValuesForCollectionLine(View view, Object model, CollectionGroup collectionGroup,
            Object line) {
        // retrieve all data fields for the collection line
        List<DataField> dataFields = ComponentUtils.getComponentsOfTypeDeep(collectionGroup.getAddLineItems(),
                DataField.class);
        for (DataField dataField : dataFields) {
            String bindingPath = "";
            if (StringUtils.isNotBlank(dataField.getBindingInfo().getBindByNamePrefix())) {
                bindingPath = dataField.getBindingInfo().getBindByNamePrefix() + ".";
            }
            bindingPath += dataField.getBindingInfo().getBindingName();

            populateDefaultValueForField(view, line, dataField, bindingPath);
        }
    }

    /**
     * Gets global objects for the context map and pushes them to the context for the component
     * 
     * @param view view instance for component
     * @param component component instance to push context to
     */
    public Map<String, Object> getCommonContext(Component component) {
        Map<String, Object> context = new HashMap<String, Object>();

        View view = ViewLifecycle.getActiveLifecycle().getView();
        Map<String, Object> viewContext = view.getContext();
        if (viewContext != null) {
            context.putAll(view.getContext());
        }

        context.put(UifConstants.ContextVariableNames.THEME_IMAGES, view.getTheme().getImageDirectory());
        context.put(UifConstants.ContextVariableNames.COMPONENT, component);

        return context;
    }

    /**
     * Get the helper active on this context.
     * 
     * @return the helper
     */
    public ViewHelperService getHelper() {
        if (this.helper == null) {
            throw new IllegalStateException("Context view helper is not available");
        }

        return this.helper;
    }

    /**
     * Get the original, immutable, view instance.
     * 
     * @return The original, immutable, view instance that the lifecycle context view is based on.
     */
    public View getOriginalView() {
        if (this.view == null) {
            throw new IllegalStateException("Context original view is not available");
        }

        return this.originalView;
    }

    /**
     * Get the view active within this context.
     * 
     * <p>After the lifecycle has completed, this view becomes the resulting view.
     * 
     * @see ViewLifecycleResult#getRefreshComponent()
     */
    public View getView() {
        if (this.view == null) {
            throw new IllegalStateException("Context view is not available");
        }

        return this.view;
    }

    /**
     * @see ViewLifecycleResult#getRefreshComponent()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Component getRefreshComponent() {
        return this.refreshComponent;
    }

    /**
     * Invoked when an event occurs to invoke registered listeners.
     * 
     * @param event event that has occurred
     * @param view view instance the lifecycle is being executed for
     * @param model object containing the model data
     * @param eventComponent component instance the event occurred on/for
     * @see LifecycleEvent
     */
    public void invokeEventListeners(LifecycleEvent event, View view, Object model, Component eventComponent) {
        for (EventRegistration registration : eventRegistrations) {
            if (registration.getEvent().equals(event) && (registration.getEventComponent() == eventComponent)) {
                registration.getEventListener().processEvent(event, view, model, eventComponent);
            }
        }
    }

    /**
     * Executes the ApplyModel phase. During this phase each component of the tree if invoked to
     * setup any state based on the given model data
     * 
     * <p>
     * Part of the view lifecycle that applies the model data to the view. Should be called after
     * the model has been populated before the view is rendered. The main things that occur during
     * this phase are:
     * <ul>
     * <li>Generation of dynamic fields (such as collection rows)</li>
     * <li>Execution of conditional logic (hidden, read-only, required settings based on model
     * values)</li>
     * </ul>
     * </p>
     * 
     * <p>
     * The update phase can be called multiple times for the view's lifecycle (typically only once
     * per request)
     * </p>
     * 
     * @param view View instance that the model should be applied to
     * @param model Top level object containing the data (could be the form or a top level business
     *        object, dto)
     */
    public void performApplyModel(Object model) {
        View view = ViewLifecycle.getActiveLifecycle().getView();

        ProcessLogger.trace("apply-model:" + view.getId());

        // apply default values if they have not been applied yet
        if (!((ViewModel) model).isDefaultsApplied()) {
            applyDefaultValues(view, view, model);
            ((ViewModel) model).setDefaultsApplied(true);
        }

        // get action flag and edit modes from authorizer/presentation controller
        retrieveEditModesAndActionFlags(view, (UifFormBase) model);

        // set view context for conditional expressions
        setViewContext(view, model);

        ProcessLogger.trace("apply-comp-model:" + view.getId());

        Map<String, Integer> visitedIds = new HashMap<String, Integer>();
        performComponentApplyModel(view, view, model, visitedIds);

        ProcessLogger.trace("apply-model-end:" + view.getId());
    }

    /**
     * Performs the Initialization phase for the given <code>Component</code> by these steps:
     * 
     * <ul>
     * <li>For <code>DataField</code> instances, set defaults from the data dictionary.</li>
     * <li>Invoke the initialize method on the component. Here the component can setup defaults and
     * do other initialization that is specific to that component.</li>
     * <li>Invoke any configured <code>ComponentModifier</code> instances for the component.</li>
     * <li>Call the component to get the List of components that are nested within and recursively
     * call this method to initialize those components.</li>
     * <li>Call custom initialize hook for service overrides</li>
     * </ul>
     * 
     * <p>
     * Note the order various initialize points are called, this can sometimes be an important
     * factor to consider when initializing a component
     * </p>
     * 
     * <p>
     * Can be called for component instances constructed via code or prototypes to initialize the
     * constructed component
     * </p>
     * 
     * @param model object instance containing the view data
     * @param component component instance that should be initialized
     * @throws RiceRuntimeException if the component id or factoryId is not specified
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#performComponentInitialization(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    public void performComponentInitialization(Object model, Component component) {
        if (component == null) {
            return;
        }

        if (StringUtils.isBlank(component.getId())) {
            throw new RiceRuntimeException("Id is not set, this should not happen unless a component is misconfigured");
        }

        // TODO: duplicate ID check

        View view = ViewLifecycle.getActiveLifecycle().getView();

        LOG.debug("Initializing component: " + component.getId() + " with type: " + component.getClass());

        // add initial state to the view index for component refreshes
        if (!(component instanceof View)) {
            view.getViewIndex().addInitialComponentStateIfNeeded(component);
        }

        // the component can have an expression graph for which the expressions need pulled to
        // the list the expression service will evaluate
        ExpressionUtils.populatePropertyExpressionsFromGraph(component, true);

        // invoke component to initialize itself after properties have been set
        component.performInitialization(model);

        // move expressions on property replacers and component modifiers
        List<PropertyReplacer> componentPropertyReplacers = component.getPropertyReplacers();
        if (componentPropertyReplacers != null) {
            for (PropertyReplacer replacer : componentPropertyReplacers) {
                ExpressionUtils.populatePropertyExpressionsFromGraph(replacer, true);
            }
        }

        List<ComponentModifier> componentModifiers = component.getComponentModifiers();
        if (componentModifiers != null) {
            for (ComponentModifier modifier : component.getComponentModifiers()) {
                ExpressionUtils.populatePropertyExpressionsFromGraph(modifier, true);
            }
        }

        // for attribute fields, set defaults from dictionary entry
        if (component instanceof DataField) {
            initializeDataFieldFromDataDictionary(view, model, (DataField) component);
        }

        if (component instanceof Container) {
            // invoke hook point for adding components through code
            helper.addCustomContainerComponents(model, (Container) component);

            // process any remote fields holder that might be in the containers items, collection items will get
            // processed as the lines are being built
            if (!(component instanceof CollectionGroup)) {
                processAnyRemoteFieldsHolder(view, model, (Container) component);
            }
        }

        // for collection groups set defaults from dictionary entry
        if (component instanceof CollectionGroup) {
            // TODO: initialize from dictionary
        }

        // invoke initialize service hook
        helper.performCustomInitialization(component);

        // invoke component modifiers setup to run in the initialize phase
        runComponentModifiers(component, null, UifConstants.ViewPhases.INITIALIZE);

        // initialize nested components
        for (Component nestedComponent : component.getComponentsForLifecycle()) {
            performComponentInitialization(model, nestedComponent);
        }

        // initialize component prototypes
        for (Component nestedComponent : component.getComponentPrototypes()) {
            performComponentInitialization(model, nestedComponent);
        }
    }

    /**
     * The last phase before the view is rendered. Here final preparations can be made based on the
     * updated view state
     * 
     * <p>
     * The finalize phase runs after the apply model phase and can be called multiple times for the
     * view's lifecylce (however typically only once per request)
     * </p>
     * 
     * @param view view instance that should be finalized for rendering
     * @param model top level object containing the data public void performFinalize(Object model);
     */
    /**
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object)
     */
    public void performFinalize(Object model) {
        // get script for generating growl messages
        String growlScript = buildGrowlScript();
        ((ViewModel) model).setGrowlScript(growlScript);

        performComponentFinalize(view, view, model, null);

        String clientStateScript = buildClientSideStateScript(model);
        view.setPreLoadScript(ScriptUtils.appendScript(view.getPreLoadScript(), clientStateScript));

        helper.performCustomViewFinalize(model);
    }

    /**
     * Performs the Initialization phase for the <code>View</code>. During this phase each component
     * of the tree is invoked to setup state based on the configuration and request options.
     * 
     * <p>
     * The initialize phase is only called once per <code>View</code> lifecycle
     * </p>
     * 
     * <p>
     * Note the <code>View</code> instance also contains the context Map that was created based on
     * the parameters sent to the view service
     * </p>
     * 
     * @param view View instance that should be initialized
     * @param model object instance containing the view data
     */
    public void performInitialization(Object model) {
        helper.performCustomViewInitialization(model);
        
        view.assignComponentIds(view);

        // increment the id sequence so components added later to the static view components
        // will not conflict with components on the page when navigation happens
        view.setIdSequence(100000);
        performComponentInitialization(model, view);

        // initialize the expression evaluator impl
        helper.getExpressionEvaluator().initializeEvaluationContext(model);

        // get the list of dialogs from the view and then set the refreshedByAction on the dialog to true.
        // This will leave the component in the viewIndex to be updated using an AJAX call
        // TODO: Figure out a better way to store dialogs only if it is rendered using an ajax request
        for (Component dialog : view.getDialogs()) {
            dialog.setRefreshedByAction(true);
        }
    }

    /**
     * Uses reflection to find all fields defined on the <code>View</code> instance that have the
     * <code>RequestParameter</code> annotation (which indicates the field may be populated by the
     * request).
     * 
     * <p>
     * The <code>View</code> instance is inspected for fields that have the
     * <code>RequestParameter</code> annotation and if corresponding parameters
     * are found in the request parameter map, the request value is used to set
     * the view property. The Map of parameter name/values that match are placed
     * in the view so they can be later retrieved to rebuild the view. Custom
     * <code>ViewServiceHelper</code> implementations can add additional
     * parameter key/value pairs to the returned map if necessary.
     * </p>
     *
     * <p>
     * For each field found, if there is a corresponding key/value pair in the request parameters,
     * the value is used to populate the field. In addition, any conditional properties of
     * <code>PropertyReplacers</code> configured for the field are cleared so that the request
     * parameter value does not get overridden by the dictionary conditional logic
     * </p>
     * 
     * @see org.kuali.rice.krad.uif.component.RequestParameter
     */
    public void populateViewFromRequestParameters(Map<String, String> parameters) {
        View view = ViewLifecycle.getActiveLifecycle().getView();

        // build Map of property replacers by property name so that we can remove them
        // if the property was set by a request parameter
        Map<String, Set<PropertyReplacer>> viewPropertyReplacers = new HashMap<String, Set<PropertyReplacer>>();
        List<PropertyReplacer> propertyReplacerSource = view.getPropertyReplacers();
        if (propertyReplacerSource != null) {
            for (PropertyReplacer replacer : propertyReplacerSource) {
                String replacerPropertyName = replacer.getPropertyName();
                Set<PropertyReplacer> propertyReplacers = viewPropertyReplacers.get(replacerPropertyName);

                if (propertyReplacers == null) {
                    viewPropertyReplacers.put(replacerPropertyName,
                            propertyReplacers = new HashSet<PropertyReplacer>());
                }

                propertyReplacers.add(replacer);
            }
        }

        Map<String, Annotation> annotatedFields = CloneUtils.getFieldsWithAnnotation(view.getClass(),
                RequestParameter.class);

        // for each request parameter allowed on the view, if the request contains a value use
        // to set on View, and clear and conditional expressions or property replacers for that field
        Map<String, String> viewRequestParameters = new HashMap<String, String>();
        for (String fieldToPopulate : annotatedFields.keySet()) {
            RequestParameter requestParameter = (RequestParameter) annotatedFields.get(fieldToPopulate);

            // use specified parameter name if given, else use field name to retrieve parameter value
            String requestParameterName = requestParameter.parameterName();
            if (StringUtils.isBlank(requestParameterName)) {
                requestParameterName = fieldToPopulate;
            }

            if (!parameters.containsKey(requestParameterName)) {
                continue;
            }

            String fieldValue = parameters.get(requestParameterName);
            if (StringUtils.isNotBlank(fieldValue)) {
                viewRequestParameters.put(requestParameterName, fieldValue);
                ObjectPropertyUtils.setPropertyValue(view, fieldToPopulate, fieldValue);

                // remove any conditional configuration so value is not
                // overridden later during the apply model phase
                if (view.getPropertyExpressions().containsKey(fieldToPopulate)) {
                    view.getPropertyExpressions().remove(fieldToPopulate);
                }

                if (viewPropertyReplacers.containsKey(fieldToPopulate)) {
                    Set<PropertyReplacer> propertyReplacers = viewPropertyReplacers.get(fieldToPopulate);
                    for (PropertyReplacer replacer : propertyReplacers) {
                        view.getPropertyReplacers().remove(replacer);
                    }
                }
            }
        }

        view.setViewRequestParameters(viewRequestParameters);
    }

    /**
     * Update the reference objects listed in referencesToRefresh of the model
     *
     * <p>
     * The the individual references in the referencesToRefresh string are separated by
     * KRADConstants.REFERENCES_TO_REFRESH_SEPARATOR).
     * </p>
     *
     * @param model top level object containing the data
     * @param referencesToRefresh list of references to refresh (
     */
    public void refreshReferences(Object model, String referencesToRefresh) {
        for (String reference : StringUtils.split(referencesToRefresh, KRADConstants.REFERENCES_TO_REFRESH_SEPARATOR)) {
            if (StringUtils.isBlank(reference)) {
                continue;
            }

            //ToDo: handle add line

            if (DataObjectUtils.isNestedAttribute(reference)) {
                String parentPath = DataObjectUtils.getNestedAttributePrefix(reference);
                Object parentObject = ObjectPropertyUtils.getPropertyValue(model, parentPath);
                String referenceObjectName = DataObjectUtils.getNestedAttributePrimitive(reference);

                if (parentObject == null) {
                    LOG.warn("Unable to refresh references for " + referencesToRefresh +
                            ". Object not found in model. Nothing refreshed.");
                    continue;
                }

                refreshReference(parentObject, referenceObjectName);
            } else {
                refreshReference(model, reference);
            }
        }
    }

    /**
     * Registers the given component as a listener for the lifecycle complete event for the given
     * event component.
     * 
     * <p>
     * The {@link LifecycleEvent#LIFECYCLE_COMPLETE} is thrown immediately after the finalize phase
     * has been completed for a component. This can be useful if a component needs to set state
     * after the lifecycle has been completed on another component (for example, it might depend on
     * properties of that component that are set during the finalize phase of that component)
     * </p>
     * 
     * @param eventComponent component the event will occur for
     * @param listenerComponent component to invoke when the event is thrown
     * @see LifecycleEvent
     * @see LifecycleEventListener
     */
    public void registerLifecycleCompleteListener(Component eventComponent, LifecycleEventListener listenerComponent) {
        EventRegistration eventRegistration = new EventRegistration(LifecycleEvent.LIFECYCLE_COMPLETE, eventComponent,
                listenerComponent);

        eventRegistrations.add(eventRegistration);
    }

    /**
     * Runs the lifecycle process for the given component starting at the given start phase and
     * ending with the given end phase
     * 
     * <p>
     * Start or end phase can be null to indicate the first phase or last phase respectively
     * </p>
     * 
     * @param model object providing the view data
     * @param component component to run the lifecycle phases for
     * @param parent parent component for the component being processed
     * @param startPhase lifecycle phase to start with, or null to indicate the first phase
     * @param endPhase lifecycle phase to end with, or null to indicate the last phase
     */
    public void spawnSubLifecyle(Object model, Component component, Component parent,
            String startPhase, String endPhase) {
        View view = ViewLifecycle.getActiveLifecycle().getView();

        if (component == null) {
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Spawning sub-lifecycle for component: " + component.getId());
        }

        if (StringUtils.isBlank(component.getId())) {
            view.assignComponentIds(component);
        }

        if (StringUtils.isBlank(startPhase)) {
            startPhase = UifConstants.ViewPhases.INITIALIZE;
        } else if (!UifConstants.ViewPhases.INITIALIZE.equals(startPhase) && !UifConstants.ViewPhases.APPLY_MODEL
                .equals(startPhase) && !UifConstants.ViewPhases.FINALIZE.equals(startPhase)) {
            throw new RuntimeException("Invalid start phase given: " + startPhase);
        }

        if (StringUtils.isBlank(endPhase)) {
            endPhase = UifConstants.ViewPhases.FINALIZE;
        } else if (!UifConstants.ViewPhases.INITIALIZE.equals(endPhase) && !UifConstants.ViewPhases.APPLY_MODEL.equals(
                endPhase) && !UifConstants.ViewPhases.FINALIZE.equals(endPhase)) {
            throw new RuntimeException("Invalid end phase given: " + endPhase);
        }

        if (UifConstants.ViewPhases.INITIALIZE.equals(startPhase)) {
            performComponentInitialization(model, component);
            view.getViewIndex().indexComponent(component);

            startPhase = UifConstants.ViewPhases.APPLY_MODEL;
        }

        if (UifConstants.ViewPhases.INITIALIZE.equals(endPhase)) {
            return;
        }

        component.pushObjectToContext(UifConstants.ContextVariableNames.PARENT, parent);

        if (UifConstants.ViewPhases.APPLY_MODEL.equals(startPhase)) {
            performComponentApplyModel(view, component, model, new HashMap<String, Integer>());
            view.getViewIndex().indexComponent(component);
        }

        if (UifConstants.ViewPhases.APPLY_MODEL.equals(endPhase)) {
            return;
        }

        performComponentFinalize(view, component, model, parent);
        view.getViewIndex().indexComponent(component);
    }

    /**
     * Checks against the visited ids to see if the id is duplicate, if so it is adjusted to make an
     * unique id by appending an unique sequence number
     * 
     * @param id id to adjust if necessary
     * @param visitedIds tracks components ids that have been seen for adjusting duplicates
     * @return original or adjusted id
     */
    protected String adjustIdIfNecessary(String id, Map<String, Integer> visitedIds) {
        String adjustedId = id;

        if (visitedIds.containsKey(id)) {
            Integer nextAdjustSeq = visitedIds.get(id);
            adjustedId = id + nextAdjustSeq;

            // verify the adjustedId does not already exist
            while (visitedIds.containsKey(adjustedId)) {
                nextAdjustSeq = nextAdjustSeq + 1;
                adjustedId = id + nextAdjustSeq;
            }

            visitedIds.put(adjustedId, Integer.valueOf(1));

            nextAdjustSeq = nextAdjustSeq + 1;
            visitedIds.put(id, nextAdjustSeq);
        } else {
            visitedIds.put(id, Integer.valueOf(1));
        }

        return adjustedId;
    }

    /**
     * Invokes the view's configured {@link ViewAuthorizer} and {@link ViewPresentationController}
     * to set state of the component
     * 
     * <p>
     * The following authorization is done here: Fields: edit, view, required, mask, and partial
     * mask Groups: edit and view Actions: take action
     * </p>
     * 
     * <p>
     * Note additional checks are also done for fields that are part of a collection group. This
     * authorization is found in {@link org.kuali.rice.krad.uif.container.CollectionGroupBuilder}
     * </p>
     * 
     * @param view view instance the component belongs to and from which the authorizer and
     *        presentation controller will be pulled
     * @param component component instance to authorize
     * @param model model object containing the data for the view
     */
    protected void applyAuthorizationAndPresentationLogic(View view, Component component, ViewModel model) {
        ViewPresentationController presentationController = view.getPresentationController();
        ViewAuthorizer authorizer = view.getAuthorizer();

        // if user session is not established cannot perform authorization
        if (GlobalVariables.getUserSession() == null) {
            return;
        }

        Person user = GlobalVariables.getUserSession().getPerson();

        // if component not flagged for render no need to check auth and controller logic
        if (!component.isRender()) {
            return;
        }

        // check top level view edit authorization
        if (component instanceof View) {
            if (!view.isReadOnly()) {
                boolean canEditView = authorizer.canEditView(view, model, user);
                if (canEditView) {
                    canEditView = presentationController.canEditView(view, model);
                }
                view.setReadOnly(!canEditView);
            }
        }

        // perform group authorization and presentation logic
        else if (component instanceof Group) {
            Group group = (Group) component;

            // if group is not hidden, do authorization for viewing the group
            if (!group.isHidden()) {
                boolean canViewGroup = authorizer.canViewGroup(view, model, group, group.getId(), user);
                if (canViewGroup) {
                    canViewGroup = presentationController.canViewGroup(view, model, group, group.getId());
                }
                group.setHidden(!canViewGroup);
                group.setRender(canViewGroup);
            }

            // if group is editable, do authorization for editing the group
            if (!group.isReadOnly()) {
                boolean canEditGroup = authorizer.canEditGroup(view, model, group, group.getId(), user);
                if (canEditGroup) {
                    canEditGroup = presentationController.canEditGroup(view, model, group, group.getId());
                }
                group.setReadOnly(!canEditGroup);
            }
        }

        // perform field authorization and presentation logic
        else if (component instanceof Field && !(component instanceof ActionField)) {
            Field field = (Field) component;

            String propertyName = null;
            if (field instanceof DataBinding) {
                propertyName = ((DataBinding) field).getPropertyName();
            }

            // if field is not hidden, do authorization for viewing the field
            if (!field.isHidden()) {
                boolean canViewField = authorizer.canViewField(view, model, field, propertyName, user);
                if (canViewField) {
                    canViewField = presentationController.canViewField(view, model, field, propertyName);
                }
                field.setHidden(!canViewField);
                field.setRender(canViewField);
            }

            // if field is not readOnly, check edit authorization
            if (!field.isReadOnly()) {
                // check field edit authorization
                boolean canEditField = authorizer.canEditField(view, model, field, propertyName, user);
                if (canEditField) {
                    canEditField = presentationController.canEditField(view, model, field, propertyName);
                }
                field.setReadOnly(!canEditField);
            }

            // if field is not already required, invoke presentation logic to determine if it should be
            if ((field.getRequired() == null) || !field.getRequired().booleanValue()) {
                // boolean fieldIsRequired = 
                presentationController.fieldIsRequired(view, model, field, propertyName);
            }

            if (field instanceof DataField) {
                DataField dataField = (DataField) field;

                // check mask authorization
                boolean canUnmaskValue = authorizer.canUnmaskField(view, model, dataField, dataField.getPropertyName(),
                        user);
                if (!canUnmaskValue) {
                    dataField.setApplyMask(true);
                    dataField.setMaskFormatter(dataField.getDataFieldSecurity().getAttributeSecurity().
                            getMaskFormatter());
                } else {
                    // check partial mask authorization
                    boolean canPartiallyUnmaskValue = authorizer.canPartialUnmaskField(view, model, dataField,
                            dataField.getPropertyName(), user);
                    if (!canPartiallyUnmaskValue) {
                        dataField.setApplyMask(true);
                        dataField.setMaskFormatter(
                                dataField.getDataFieldSecurity().getAttributeSecurity().getPartialMaskFormatter());
                    }
                }
            }
        }

        // perform action authorization and presentation logic
        else if (component instanceof ActionField || component instanceof Action) {
            Action action = null;
            if (component instanceof ActionField) {
                action = ((ActionField) component).getAction();
            } else {
                action = (Action) component;
            }

            boolean canTakeAction = authorizer.canPerformAction(view, model, action, action.getActionEvent(),
                    action.getId(), user);
            if (canTakeAction) {
                canTakeAction = presentationController.canPerformAction(view, model, action, action.getActionEvent(),
                        action.getId());
            }
            action.setRender(canTakeAction);
        }

        // perform widget authorization and presentation logic
        else if (component instanceof Widget) {
            Widget widget = (Widget) component;

            // if widget is not hidden, do authorization for viewing the widget
            if (!widget.isHidden()) {
                boolean canViewWidget = authorizer.canViewWidget(view, model, widget, widget.getId(), user);
                if (canViewWidget) {
                    canViewWidget = presentationController.canViewWidget(view, model, widget, widget.getId());
                }
                widget.setHidden(!canViewWidget);
                widget.setRender(canViewWidget);
            }

            // if widget is not readOnly, check edit authorization
            if (!widget.isReadOnly()) {
                boolean canEditWidget = authorizer.canEditWidget(view, model, widget, widget.getId(), user);
                if (canEditWidget) {
                    canEditWidget = presentationController.canEditWidget(view, model, widget, widget.getId());
                }
                widget.setReadOnly(!canEditWidget);
            }
        }
    }

    /**
     * Iterates through the view components picking up data fields and applying an default value
     * configured
     * 
     * @param view view instance we are applying default values for
     * @param component component that should be checked for default values
     * @param model model object that values should be set on
     */
    protected void applyDefaultValues(View view, Component component, Object model) {
        if (component == null) {
            return;
        }

        // if component is a data field apply default value
        if (component instanceof DataField) {
            DataField dataField = ((DataField) component);

            // need to make sure binding is initialized since this could be on a page we have not initialized yet
            dataField.getBindingInfo().setDefaults(view, dataField.getPropertyName());

            populateDefaultValueForField(view, model, dataField, dataField.getBindingInfo().getBindingPath());
        }

        for (Component nested : component.getComponentsForLifecycle()) {
            applyDefaultValues(view, nested, model);
        }

        // if view, need to add all pages since only one will be on the lifecycle
        if (component instanceof View) {
            for (Component nested : ((View) component).getItems()) {
                applyDefaultValues(view, nested, model);
            }
        }

    }

    /**
     * Builds script that will initialize configuration parameters and component state on the client
     * 
     * <p>
     * Here client side state is initialized along with configuration variables that need exposed to
     * script
     * </p>
     * 
     * @param view view instance that is being built
     * @param model model containing the client side state map
     */
    protected String buildClientSideStateScript(Object model) {
        Map<String, Object> clientSideState = ((ViewModel) model).getClientStateForSyncing();

        // script for initializing client side state on load
        String clientStateScript = "";
        if (!clientSideState.isEmpty()) {
            clientStateScript = ScriptUtils.buildFunctionCall(UifConstants.JsFunctions.INITIALIZE_VIEW_STATE,
                    clientSideState);
        }

        // add necessary configuration parameters
        String kradImageLocation = CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                UifConstants.ConfigProperties.KRAD_IMAGES_URL);
        clientStateScript += ScriptUtils.buildFunctionCall(UifConstants.JsFunctions.SET_CONFIG_PARM,
                UifConstants.ClientSideVariables.KRAD_IMAGE_LOCATION, kradImageLocation);

        String kradURL = CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                UifConstants.ConfigProperties.KRAD_URL);
        clientStateScript += ScriptUtils.buildFunctionCall(UifConstants.JsFunctions.SET_CONFIG_PARM,
                UifConstants.ClientSideVariables.KRAD_URL, kradURL);

        String applicationURL = CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                KRADConstants.ConfigParameters.APPLICATION_URL);
        clientStateScript += ScriptUtils.buildFunctionCall(UifConstants.JsFunctions.SET_CONFIG_PARM,
                UifConstants.ClientSideVariables.APPLICATION_URL, applicationURL);

        return clientStateScript;
    }

    /**
     * Builds JS script that will invoke the show growl method to display a growl message when the
     * page is rendered
     * 
     * <p>
     * A growl call will be created for any explicit growl messages added to the message map.
     * </p>
     * 
     * <p>
     * Growls are only generated if @{link
     * org.kuali.rice.krad.uif.view.View#isGrowlMessagingEnabled()} is enabled. If not, the growl
     * messages are set as info messages for the page
     * </p>
     * 
     * @param view view instance for which growls are being generated
     * @return JS script string for generated growl messages
     */
    protected String buildGrowlScript() {
        String growlScript = "";

        MessageService messageService = KRADServiceLocatorWeb.getMessageService();

        MessageMap messageMap = GlobalVariables.getMessageMap();
        for (GrowlMessage growl : messageMap.getGrowlMessages()) {
            if (view.isGrowlMessagingEnabled()) {
                String message = messageService.getMessageText(growl.getNamespaceCode(), growl.getComponentCode(),
                        growl.getMessageKey());

                if (StringUtils.isNotBlank(message)) {
                    if (growl.getMessageParameters() != null) {
                        message = message.replace("'", "''");
                        message = MessageFormat.format(message, (Object[]) growl.getMessageParameters());
                    }

                    // escape single quotes in message or title since that will cause problem with plugin
                    message = message.replace("'", "\\'");

                    String title = growl.getTitle();
                    if (StringUtils.isNotBlank(growl.getTitleKey())) {
                        title = messageService.getMessageText(growl.getNamespaceCode(), growl.getComponentCode(),
                                growl.getTitleKey());
                    }
                    title = title.replace("'", "\\'");

                    growlScript =
                            growlScript + "showGrowl('" + message + "', '" + title + "', '" + growl.getTheme() + "');";
                }
            } else {
                ErrorMessage infoMessage = new ErrorMessage(growl.getMessageKey(), growl.getMessageParameters());
                infoMessage.setNamespaceCode(growl.getNamespaceCode());
                infoMessage.setComponentCode(growl.getComponentCode());

                messageMap.putInfoForSectionId(KRADConstants.GLOBAL_INFO, infoMessage);
            }
        }

        return growlScript;
    }

    /**
     * Recursively drills down the property path (if nested) to find an AttributeDefinition, the
     * first attribute definition found will be returned
     * 
     * <p>
     * e.g. suppose parentPath is 'document' and propertyPath is 'account.subAccount.name', first
     * the property type for document will be retrieved using the view metadata and used as the
     * dictionary entry, with the propertyPath as the dictionary attribute, if an attribute
     * definition exists it will be returned. Else, the first part of the property path is added to
     * the parent, making the parentPath 'document.account' and the propertyPath 'subAccount.name',
     * the method is then called again to perform the process with those parameters. The recursion
     * continues until an attribute field is found, or the propertyPath is no longer nested
     * </p>
     * 
     * @param view view instance containing the field
     * @param field field we are attempting to find a supporting attribute definition for
     * @param propertyPath path of the property to use as dictionary attribute and to drill down on
     * @return AttributeDefinition if found, or Null
     */
    protected AttributeDefinition findNestedDictionaryAttribute(final View view, final Object model, DataField field,
            String propertyPath) {
        // attempt to find definition for parent and property
        String fieldBindingPrefix = null;
        String dictionaryAttributePath = propertyPath;

        if (field.getBindingInfo().isBindToMap()) {
            fieldBindingPrefix = "";
            if (!field.getBindingInfo().isBindToForm() && StringUtils.isNotBlank(
                    field.getBindingInfo().getBindingObjectPath())) {
                fieldBindingPrefix = field.getBindingInfo().getBindingObjectPath();
            }
            if (StringUtils.isNotBlank(field.getBindingInfo().getBindByNamePrefix())) {
                if (StringUtils.isNotBlank(fieldBindingPrefix)) {
                    fieldBindingPrefix += "." + field.getBindingInfo().getBindByNamePrefix();
                } else {
                    fieldBindingPrefix = field.getBindingInfo().getBindByNamePrefix();
                }
            }

            dictionaryAttributePath = field.getBindingInfo().getBindingName();
        }

        if (StringUtils.isEmpty(dictionaryAttributePath)) {
            return null;
        }

        final DataDictionaryService dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        final String rootPath = fieldBindingPrefix;
        final Map<String, Class<?>> modelClasses = view.getObjectPathToConcreteClassMapping();

        class AttributePathEntry implements PathEntry {
            AttributeDefinition attributeDefinition;
            String dictionaryAttributeName;
            String dictionaryObjectEntry;

            @Override
            public Object parse(String parentPath, Object node, String next) {
                if (next == null) {
                    return node;
                }

                if (attributeDefinition != null || node == null) {
                    return null;
                }

                String dictionaryEntryName =
                        getDictionaryEntryName(model, modelClasses, rootPath, parentPath);

                if (dictionaryEntryName != null) {
                    attributeDefinition = dataDictionaryService
                            .getAttributeDefinition(dictionaryEntryName, next);

                    if (attributeDefinition != null) {
                        dictionaryObjectEntry = dictionaryEntryName;
                        dictionaryAttributeName = next;
                        return null;
                    }
                }

                return node;
            }
        }

        AttributePathEntry attributePathEntry = new AttributePathEntry();
        ObjectPathExpressionParser
                .parsePathExpression(attributePathEntry, dictionaryAttributePath, attributePathEntry);

        // if a definition was found, update the fields dictionary properties
        if (attributePathEntry.attributeDefinition != null) {
            field.setDictionaryObjectEntry(attributePathEntry.dictionaryObjectEntry);
            field.setDictionaryAttributeName(attributePathEntry.dictionaryAttributeName);
        }

        return attributePathEntry.attributeDefinition;
    }

    /**
     * Sets properties of the <code>InputField</code> (if blank) to the corresponding attribute
     * entry in the data dictionary
     * 
     * @param view view instance containing the field
     * @param field data field instance to initialize
     */
    protected void initializeDataFieldFromDataDictionary(View view, Object model, DataField field) {
        AttributeDefinition attributeDefinition = null;

        String dictionaryAttributeName = field.getDictionaryAttributeName();
        String dictionaryObjectEntry = field.getDictionaryObjectEntry();

        // if entry given but not attribute name, use field name as attribute
        // name
        if (StringUtils.isNotBlank(dictionaryObjectEntry) && StringUtils.isBlank(dictionaryAttributeName)) {
            dictionaryAttributeName = field.getPropertyName();
        }

        // if dictionary entry and attribute set, attempt to find definition
        if (StringUtils.isNotBlank(dictionaryAttributeName) && StringUtils.isNotBlank(dictionaryObjectEntry)) {
            attributeDefinition = KRADServiceLocatorWeb.getDataDictionaryService()
                    .getAttributeDefinition(dictionaryObjectEntry, dictionaryAttributeName);
        }

        // if definition not found, recurse through path
        if (attributeDefinition == null) {
            
            BindingInfo fieldBindingInfo = field.getBindingInfo();
            String collectionPath = fieldBindingInfo.getCollectionPath();
            String propertyPath;
            
            if (StringUtils.isNotBlank(collectionPath)) {
                StringBuilder propertyPathBuilder = new StringBuilder();

                String bindingObjectPath = fieldBindingInfo.getBindingObjectPath();
                if (StringUtils.isNotBlank(bindingObjectPath)) {
                    propertyPathBuilder.append(bindingObjectPath).append('.');
                }

                propertyPathBuilder.append(collectionPath).append('.');

                String bindByNamePrefix = fieldBindingInfo.getBindByNamePrefix();
                if (StringUtils.isNotBlank(bindByNamePrefix)) {
                    propertyPathBuilder.append(bindByNamePrefix).append('.');
                }

                propertyPathBuilder.append(fieldBindingInfo.getBindingName());
                propertyPath = propertyPathBuilder.toString();

            } else {
                propertyPath = field.getBindingInfo().getBindingPath();
            }

            attributeDefinition = findNestedDictionaryAttribute(view, model, field, propertyPath);
        }

        // if a definition was found, initialize field from definition
        if (attributeDefinition != null) {
            field.copyFromAttributeDefinition(view, attributeDefinition);
        }

        // if control still null, assign default
        if (field instanceof InputField) {
            InputField inputField = (InputField) field;
            if (inputField.getControl() == null) {
                Control control = ComponentFactory.getTextControl();
                view.assignComponentIds(control);

                inputField.setControl(control);
            }
        }
    }

    /**
     * Invokes the finalize method for the component (if configured) and sets the render output for
     * the component to the returned method string (if method is not a void type)
     * 
     * @param view view instance that contains the component
     * @param component component to run finalize method for
     * @param model top level object containing the data
     */
    protected void invokeMethodFinalizer(View view, Component component, Object model) {
        String finalizeMethodToCall = component.getFinalizeMethodToCall();
        MethodInvoker finalizeMethodInvoker = component.getFinalizeMethodInvoker();

        if (StringUtils.isBlank(finalizeMethodToCall) && (finalizeMethodInvoker == null)) {
            return;
        }

        if (finalizeMethodInvoker == null) {
            finalizeMethodInvoker = new MethodInvoker();
        }

        // if method not set on invoker, use finalizeMethodToCall, note staticMethod could be set(don't know since
        // there is not a getter), if so it will override the target method in prepare
        if (StringUtils.isBlank(finalizeMethodInvoker.getTargetMethod())) {
            finalizeMethodInvoker.setTargetMethod(finalizeMethodToCall);
        }

        // if target class or object not set, use view helper service
        if ((finalizeMethodInvoker.getTargetClass() == null) && (finalizeMethodInvoker.getTargetObject() == null)) {
            finalizeMethodInvoker.setTargetObject(view.getViewHelperService());
        }

        // setup arguments for method
        List<Object> additionalArguments = component.getFinalizeMethodAdditionalArguments();
        if (additionalArguments == null) {
            additionalArguments = new ArrayList<Object>();
        }

        Object[] arguments = new Object[2 + additionalArguments.size()];
        arguments[0] = component;
        arguments[1] = model;

        int argumentIndex = 1;
        for (Object argument : additionalArguments) {
            argumentIndex++;
            arguments[argumentIndex] = argument;
        }
        finalizeMethodInvoker.setArguments(arguments);

        // invoke finalize method
        try {
            LOG.debug("Invoking finalize method: "
                    + finalizeMethodInvoker.getTargetMethod()
                    + " for component: "
                    + component.getId());
            finalizeMethodInvoker.prepare();

            Class<?> methodReturnType = finalizeMethodInvoker.getPreparedMethod().getReturnType();
            if (StringUtils.equals("void", methodReturnType.getName())) {
                finalizeMethodInvoker.invoke();
            } else {
                String renderOutput = (String) finalizeMethodInvoker.invoke();

                component.setSelfRendered(true);
                component.setRenderedHtmlOutput(renderOutput);
            }
        } catch (Exception e) {
            LOG.error("Error invoking finalize method for component: " + component.getId(), e);
            throw new RuntimeException("Error invoking finalize method for component: " + component.getId(), e);
        }
    }

    /**
     * Applies the model data to a component of the View instance
     * 
     * <p>
     * The component is invoked to to apply the model data. Here the component can generate any
     * additional fields needed or alter the configured fields. After the component is invoked a
     * hook for custom helper service processing is invoked. Finally the method is recursively
     * called for all the component children
     * </p>
     * 
     * @param view view instance the component belongs to
     * @param component the component instance the model should be applied to
     * @param model top level object containing the data
     * @param visitedIds tracks components ids that have been seen for adjusting duplicates
     */
    protected void performComponentApplyModel(View view, Component component, Object model,
            Map<String, Integer> visitedIds) {
        if (component == null) {
            return;
        }

        // ProcessLogger.ntrace("comp-model:", ":" + component.getClass().getSimpleName(), 500);
        // ProcessLogger.countBegin("comp-model");

        // set context on component for evaluating expressions
        component.pushAllToContext(getCommonContext(component));

        ExpressionEvaluator expressionEvaluator = helper.getExpressionEvaluator();
        
        List<PropertyReplacer> componentPropertyReplacers = component.getPropertyReplacers();
        if (componentPropertyReplacers != null) {
            for (PropertyReplacer replacer : componentPropertyReplacers) {
                expressionEvaluator.evaluateExpressionsOnConfigurable(view, replacer, component.getContext());
            }
        }

        List<ComponentModifier> componentModifiers = component.getComponentModifiers();
        if (componentModifiers != null) {
            for (ComponentModifier modifier : component.getComponentModifiers()) {
                expressionEvaluator.evaluateExpressionsOnConfigurable(view, modifier, component.getContext());
            }
        }

        expressionEvaluator.evaluateExpressionsOnConfigurable(view, component, component.getContext());

        // evaluate expressions on component security
        ComponentSecurity componentSecurity = component.getComponentSecurity();
        expressionEvaluator.evaluateExpressionsOnConfigurable(view, componentSecurity, component.getContext());

        // evaluate expressions on the binding info object
        if (component instanceof DataBinding) {
            BindingInfo bindingInfo = ((DataBinding) component).getBindingInfo();
            expressionEvaluator.evaluateExpressionsOnConfigurable(view, bindingInfo, component.getContext());
        }

        // set context evaluate expressions on the layout manager
        if (component instanceof Container) {
            LayoutManager layoutManager = ((Container) component).getLayoutManager();

            if (layoutManager != null) {
                layoutManager.pushAllToContext(getCommonContext(component));
                layoutManager.pushObjectToContext(UifConstants.ContextVariableNames.PARENT, component);
                layoutManager.pushObjectToContext(UifConstants.ContextVariableNames.MANAGER, layoutManager);

                expressionEvaluator.evaluateExpressionsOnConfigurable(view, layoutManager,
                        layoutManager.getContext());

                layoutManager.setId(adjustIdIfNecessary(layoutManager.getId(), visitedIds));
            }
        }

        // sync the component with previous client side state
        syncClientSideStateForComponent(component, ((ViewModel) model).getClientStateForSyncing());

        // invoke authorizer and presentation controller to set component state
        applyAuthorizationAndPresentationLogic(view, component, (ViewModel) model);

        // adjust ids for duplicates if necessary
        //component.setId(adjustIdIfNecessary(component.getId(), visitedIds));

        // invoke component to perform its conditional logic
        Map<String, Object> parentContext = component.getContext();
        Component parent = parentContext == null ? null : (Component) parentContext
                .get(UifConstants.ContextVariableNames.PARENT);

        component.performApplyModel(model, parent);

        // invoke service override hook
        helper.performCustomApplyModel(component, model);

        // invoke component modifiers configured to run in the apply model phase
        runComponentModifiers(component, model, UifConstants.ViewPhases.APPLY_MODEL);

        // ProcessLogger.countEnd("comp-model", view.getId() + " " + component.getClass() + " " + component.getId());

        // get children and recursively perform conditional logic
        Queue<Component> nested = new LinkedList<Component>();
        for (Component nestedComponent : component.getComponentsForLifecycle()) {
            if (nestedComponent != null) {
                nested.offer(nestedComponent);
            }
        }

        if (!nested.isEmpty()) {
            // ProcessLogger.countBegin("comp-nest");
            while (!nested.isEmpty()) {
                Component nestedComponent = nested.poll();
                nestedComponent.pushObjectToContext(UifConstants.ContextVariableNames.PARENT, component);
                performComponentApplyModel(view, nestedComponent, model, visitedIds);
            }
            // ProcessLogger.countEnd("comp-nest", view.getId() + " " + component.getClass() + " " + component.getId());
        }
    }

    /**
     * Update state of the given component and does final preparation for rendering
     * 
     * @param view view instance the component belongs to
     * @param component the component instance that should be updated
     * @param model top level object containing the data
     * @param parent parent component for the component being finalized
     */
    protected void performComponentFinalize(View view, Component component, Object model, Component parent) {
        if (component == null) {
            return;
        }

        // implement readonly request overrides
        ViewModel viewModel = (ViewModel) model;
        if ((component instanceof DataBinding) && view.isSupportsRequestOverrideOfReadOnlyFields() && !viewModel
                .getReadOnlyFieldsList().isEmpty()) {
            String propertyName = ((DataBinding) component).getPropertyName();
            if (viewModel.getReadOnlyFieldsList().contains(propertyName)) {
                component.setReadOnly(true);
            }
        }

        // invoke configured method finalizers
        invokeMethodFinalizer(view, component, model);

        // invoke component to update its state
        component.performFinalize(model, parent);

        // invoke service override hook
        helper.performCustomFinalize(component, model, parent);

        // invoke component modifiers setup to run in the finalize phase
        runComponentModifiers(component, model, UifConstants.ViewPhases.FINALIZE);

        // add the components template to the views list of components
        if (!component.isSelfRendered() && StringUtils.isNotBlank(component.getTemplate())) {
            view.addViewTemplate(component.getTemplate());
        }

        if (component instanceof Container) {
            LayoutManager layoutManager = ((Container) component).getLayoutManager();

            if (layoutManager != null) {
                view.addViewTemplate(layoutManager.getTemplate());
            }
        }

        // get components children and recursively update state
        for (Component nestedComponent : component.getComponentsForLifecycle()) {
            performComponentFinalize(view, nestedComponent, model, component);
        }

        // trigger lifecycle complete event for component
        ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();
        viewLifecycle.invokeEventListeners(ViewLifecycle.LifecycleEvent.LIFECYCLE_COMPLETE, view, model, component);
    }

    /**
     * Applies the default value configured for the given field (if any) to the line given object
     * property that is determined by the given binding path
     * 
     * <p>
     * Checks for a configured default value or default value class for the field. If both are
     * given, the configured static default value will win. In addition, if the default value
     * contains an el expression it is evaluated against the initial context
     * </p>
     * 
     * @param view view instance the field belongs to
     * @param object object that should be populated
     * @param dataField field to check for configured default value
     * @param bindingPath path to the property on the object that should be populated
     */
    protected void populateDefaultValueForField(View view, Object object, DataField dataField, String bindingPath) {
        // check for configured default value
        String defaultValue = dataField.getDefaultValue();
        Object[] defaultValues = dataField.getDefaultValues();

        if (!ObjectPropertyUtils.isReadableProperty(object, bindingPath)
                || !ObjectPropertyUtils.isWritableProperty(object, bindingPath)) {
            return;
        }

        Object currentValue = ObjectPropertyUtils.getPropertyValue(object, bindingPath);

        // Default value only applies when the value being set is null (has no value on the form)
        if (currentValue != null) {
            return;
        }

        ExpressionEvaluator expressionEvaluator = helper.getExpressionEvaluator();
        Map<String, String> expressionGraph = dataField.getExpressionGraph();

        if (StringUtils.isBlank(defaultValue)) {
            String defaultValuesExpression = null;
            // Check for expression, this would exist in a comma seperated list case that uses expressions
            if (expressionGraph != null && expressionGraph.containsKey(UifConstants.ComponentProperties.DEFAULT_VALUES)) {
                defaultValuesExpression = expressionGraph.get(UifConstants.ComponentProperties.DEFAULT_VALUES);
            }

            // evaluate and set if defaultValues are backed by an expression
            if (defaultValuesExpression != null && expressionEvaluator.containsElPlaceholder(
                    defaultValuesExpression)) {
                Map<String, Object> context = view.getPreModelContext();
                context.putAll(dataField.getContext());
                defaultValuesExpression = expressionEvaluator.replaceBindingPrefixes(view, object,
                        defaultValuesExpression);

                defaultValuesExpression = expressionEvaluator.evaluateExpressionTemplate(context,
                        defaultValuesExpression);

                ObjectPropertyUtils.setPropertyValue(object, bindingPath, defaultValuesExpression);
            } else if (defaultValues != null) {
                ObjectPropertyUtils.setPropertyValue(object, bindingPath, defaultValues);
            }
        } else {
            if (StringUtils.isBlank(defaultValue) && (dataField.getDefaultValueFinderClass() != null)) {
                ValueFinder defaultValueFinder = DataObjectUtils.newInstance(dataField.getDefaultValueFinderClass());
                defaultValue = defaultValueFinder.getValue();
            }

            if (expressionGraph != null && expressionGraph.containsKey(UifConstants.ComponentProperties.DEFAULT_VALUE)) {
                defaultValue = expressionGraph.get(UifConstants.ComponentProperties.DEFAULT_VALUE);
            }

            // populate default value if given and path is valid
            if (StringUtils.isNotBlank(defaultValue)) {
                expressionEvaluator = helper.getExpressionEvaluator();
                // evaluate if defaultValue is backed by an expression
                if (expressionEvaluator.containsElPlaceholder(defaultValue)) {
                    Map<String, Object> context = view.getPreModelContext();
                    context.putAll(dataField.getContext());
                    defaultValue = expressionEvaluator.replaceBindingPrefixes(view, object, defaultValue);
                    defaultValue = expressionEvaluator.evaluateExpressionTemplate(context, defaultValue);
                }
                
                ObjectPropertyUtils.setPropertyValue(object, bindingPath, defaultValue);
            }
        }
    }

    /**
     * Iterates through the containers configured items checking for
     * <code>RemotableFieldsHolder</code>, if found the holder is invoked to retrieved the remotable
     * fields and translate to attribute fields. The translated list is then inserted into the
     * container item list at the position of the holder
     * 
     * @param view view instance containing the container
     * @param model object instance containing the view data
     * @param container container instance to check for any remotable fields holder
     */
    protected void processAnyRemoteFieldsHolder(View view, Object model, Container container) {
        List<Component> processedItems = new ArrayList<Component>();

        // check for holders and invoke to retrieve the remotable fields and translate
        // translated fields are placed into the container item list at the position of the holder
        for (Component item : container.getItems()) {
            if (item instanceof RemoteFieldsHolder) {
                List<InputField> translatedFields = ((RemoteFieldsHolder) item).fetchAndTranslateRemoteFields(view,
                        model, container);
                processedItems.addAll(translatedFields);
            } else {
                processedItems.add(item);
            }
        }

        // updated container items
        container.setItems(processedItems);
    }

    /**
     * Perform a database or data dictionary based refresh of a specific property object
     *
     * <p>
     * The object needs to be of type PersistableBusinessObject.
     * </p>
     *
     * @param parentObject parent object that references the object to be refreshed
     * @param referenceObjectName property name of the parent object to be refreshed
     */
    protected void refreshReference(Object parentObject, String referenceObjectName) {
        if (!(parentObject instanceof PersistableBusinessObject)) {
            LOG.warn("Could not refresh reference " + referenceObjectName + " off class " + parentObject.getClass()
                    .getName() + ". Class not of type PersistableBusinessObject");
            return;
        }
        
        LegacyDataAdapter legacyDataAdapter = KRADServiceLocatorWeb.getLegacyDataAdapter();
        DataDictionaryService dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();

        if (legacyDataAdapter.hasReference(parentObject.getClass(), referenceObjectName)
                || legacyDataAdapter.hasCollection(parentObject.getClass(), referenceObjectName)) {
            // refresh via database mapping
            legacyDataAdapter.retrieveReferenceObject(parentObject, referenceObjectName);
        } else if (dataDictionaryService.hasRelationship(parentObject.getClass().getName(), referenceObjectName)) {
            // refresh via data dictionary mapping
            Object referenceObject = DataObjectUtils.getPropertyValue(parentObject, referenceObjectName);
            if (!(referenceObject instanceof PersistableBusinessObject)) {
                LOG.warn("Could not refresh reference " + referenceObjectName + " off class " + parentObject.getClass()
                        .getName() + ". Class not of type PersistableBusinessObject");
                return;
            }

            referenceObject = legacyDataAdapter.retrieve((PersistableBusinessObject) referenceObject);
            if (referenceObject == null) {
                LOG.warn("Could not refresh reference " + referenceObjectName + " off class " + parentObject.getClass()
                        .getName() + ".");
                return;
            }

            try {
                KRADUtils.setObjectProperty(parentObject, referenceObjectName, referenceObject);
            } catch (Exception e) {
                LOG.error("Unable to refresh persistable business object: " + referenceObjectName + "\n" + e
                        .getMessage());
                throw new RuntimeException(
                        "Unable to refresh persistable business object: " + referenceObjectName + "\n" + e
                                .getMessage());
            }
        } else {
            LOG.warn("Could not refresh reference " + referenceObjectName + " off class " + parentObject.getClass()
                    .getName() + ".");
        }
    }

    /**
     * Invokes the configured <code>PresentationController</code> and </code>Authorizer</code> for
     * the view to get the exported action flags and edit modes that can be used in conditional
     * logic
     * 
     * @param view view instance that is being built and presentation/authorizer pulled for
     * @param model Object that contains the model data
     */
    protected void retrieveEditModesAndActionFlags(View view, UifFormBase model) {
        ViewPresentationController presentationController = view.getPresentationController();
        ViewAuthorizer authorizer = view.getAuthorizer();

        Set<String> actionFlags = presentationController.getActionFlags(view, model);
        Set<String> editModes = presentationController.getEditModes(view, model);

        // if user session is not established cannot invoke authorizer
        if (GlobalVariables.getUserSession() != null) {
            Person user = GlobalVariables.getUserSession().getPerson();

            actionFlags = authorizer.getActionFlags(view, model, user, actionFlags);
            editModes = authorizer.getEditModes(view, model, user, editModes);
        }

        view.setActionFlags(new BooleanMap(actionFlags));
        view.setEditModes(new BooleanMap(editModes));
    }

    /**
     * Runs any configured <code>ComponentModifiers</code> for the given component that match the
     * given run phase and who run condition evaluation succeeds
     * 
     * <p>
     * If called during the initialize phase, the performInitialization method will be invoked on
     * the <code>ComponentModifier</code> before running
     * </p>
     * 
     * @param view view instance for context
     * @param component component instance whose modifiers should be run
     * @param model model object for context
     * @param runPhase current phase to match on
     */
    protected void runComponentModifiers(Component component, Object model, String runPhase) {
        List<ComponentModifier> componentModifiers = component.getComponentModifiers();
        if (componentModifiers == null) {
            return;
        }

        for (ComponentModifier modifier : component.getComponentModifiers()) {
            // if run phase is initialize, invoke initialize method on modifier first
            if (StringUtils.equals(runPhase, UifConstants.ViewPhases.INITIALIZE)) {
                modifier.performInitialization(model, component);
            }

            // check run phase matches
            if (StringUtils.equals(modifier.getRunPhase(), runPhase)) {
                // check condition (if set) evaluates to true
                boolean runModifier = true;
                if (StringUtils.isNotBlank(modifier.getRunCondition())) {
                    Map<String, Object> context = new HashMap<String, Object>();
                    context.put(UifConstants.ContextVariableNames.COMPONENT, component);
                    context.put(UifConstants.ContextVariableNames.VIEW, ViewLifecycle.getActiveLifecycle().getView());

                    String conditionEvaluation = helper.getExpressionEvaluator()
                            .evaluateExpressionTemplate(context, modifier.getRunCondition());
                    runModifier = Boolean.parseBoolean(conditionEvaluation);
                }

                if (runModifier) {
                    modifier.performModification(model, component);
                }
            }
        }
    }

    /**
     * Sets up the view context which will be available to other components through their context
     * for conditional logic evaluation
     * 
     * @param view view instance to set context for
     * @param model object containing the view data
     */
    protected void setViewContext(View view, Object model) {
        view.pushAllToContext(view.getPreModelContext());

        // evaluate view expressions for further context
        for (Entry<String, String> variableExpression : view.getExpressionVariables().entrySet()) {
            String variableName = variableExpression.getKey();
            Object value = helper.getExpressionEvaluator().evaluateExpression(view.getContext(),
                    variableExpression.getValue());
            view.pushObjectToContext(variableName, value);
        }
    }

    /**
     * Updates the properties of the given component instance with the value found from the
     * corresponding map of client state (if found)
     * 
     * @param component component instance to update
     * @param clientSideState map of state to sync with
     */
    @SuppressWarnings("unchecked")
    protected void syncClientSideStateForComponent(Component component, Map<String, Object> clientSideState) {
        // find the map of state that was sent for component (if any)
        Map<String, Object> componentState = null;
        if (component instanceof View) {
            componentState = clientSideState;
        } else {
            if (clientSideState.containsKey(component.getId())) {
                componentState = (Map<String, Object>) clientSideState.get(component.getId());
            }
        }

        // if state was sent, match with fields on the component that are annotated to have client state
        if ((componentState != null) && (!componentState.isEmpty())) {
            Map<String, Annotation> annotatedFields = CloneUtils.getFieldsWithAnnotation(component.getClass(),
                    ClientSideState.class);

            for (Entry<String, Annotation> annotatedField : annotatedFields.entrySet()) {
                ClientSideState clientSideStateAnnot = (ClientSideState) annotatedField.getValue();

                String variableName = clientSideStateAnnot.variableName();
                if (StringUtils.isBlank(variableName)) {
                    variableName = annotatedField.getKey();
                }

                if (componentState.containsKey(variableName)) {
                    Object value = componentState.get(variableName);
                    ObjectPropertyUtils.setPropertyValue(component, annotatedField.getKey(), value);
                }
            }
        }
    }

    /**
     * Determine the identity of a lifecycle element.
     * 
     * @param element The lifecycle element.
     * @return An identifier for the element, unique to its instance.
     */
    private static long getIdentity(LifecycleElement element) {
        return System.identityHashCode(element) + (element.getClass().hashCode() >>> 32);
    }

    /**
     * Encapsulate a new view initialization process on the current thread.
     */
    public static <T> T encapsulateInitialization(Callable<T> initializationProcess) {
        ViewLifecycle oldViewContext = TL_VIEW_LIFECYCLE.get();

        try {
            TL_VIEW_LIFECYCLE.set(new ViewLifecycle());

            try {
                return initializationProcess.call();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalStateException("Unexpected initialization error", e);
            }

        } finally {
            if (oldViewContext == null) {
                TL_VIEW_LIFECYCLE.remove();
            } else {
                TL_VIEW_LIFECYCLE.set(oldViewContext);
            }
        }
    }

    /**
     * Encapsulate a new view lifecycle process on the current thread.
     * 
     * @param lifecycleProcess The lifecycle process to encapsulate.
     * @return
     */
    public static View getMutableCopy(View view) {
        ViewLifecycle oldViewContext = TL_VIEW_LIFECYCLE.get();

        try {
            ViewLifecycle copyLifecycle = new ViewLifecycle(view, true); 
            TL_VIEW_LIFECYCLE.set(copyLifecycle);

            return view.copy();
            
        } finally {
            if (oldViewContext == null) {
                TL_VIEW_LIFECYCLE.remove();
            } else {
                TL_VIEW_LIFECYCLE.set(oldViewContext);
            }
        }
    }
    
    /**
     * Encapsulate a new view lifecycle process on the current thread.
     * 
     * @param lifecycleProcess The lifecycle process to encapsulate.
     * @return
     */
    public static ViewLifecycleResult encapsulateLifecycle(View view, Runnable lifecycleProcess) {
        ViewLifecycle viewLifecycle = TL_VIEW_LIFECYCLE.get();
        if (viewLifecycle != null) {
            throw new IllegalStateException("Another view context already active on this thread");
        }

        try {
            TL_VIEW_LIFECYCLE.set(viewLifecycle = new ViewLifecycle(view, false));
            viewLifecycle.view = view;

            lifecycleProcess.run();

            return viewLifecycle;
        } finally {
            TL_VIEW_LIFECYCLE.remove();
        }
    }

    /**
     * Get the view context active on the current thread.
     * 
     * @return The view context active on the current thread.
     */
    public static ViewLifecycle getActiveLifecycle() {
        ViewLifecycle viewLifecycle = TL_VIEW_LIFECYCLE.get();

        if (viewLifecycle == null) {
            throw new IllegalStateException("No view lifecycle is active on this thread");
        }

        return viewLifecycle;
    }

    /**
     * Determine any view context is active on the current thread.
     * 
     * @return True if a view context is active on the current thread.
     */
    public static boolean isActive() {
        ViewLifecycle viewLifecycle = TL_VIEW_LIFECYCLE.get();
        return viewLifecycle != null;
    }

    /**
     * Determine if view initialization is active on the current thread.
     * 
     * @return True if a view initialization context is active on the current thread.
     */
    public static boolean isInitializing() {
        ViewLifecycle viewLifecycle = TL_VIEW_LIFECYCLE.get();
        return viewLifecycle != null && viewLifecycle.originalView == null;
    }

    /**
     * Determine if a view lifecycle context is active on the current thread.
     * 
     * @return True if a view lifecycle context is active on the current thread.
     */
    public static boolean isLifecycleActive() {
        ViewLifecycle viewLifecycle = TL_VIEW_LIFECYCLE.get();
        return viewLifecycle != null && viewLifecycle.originalView != null;
    }

    /**
     * Determine if a lifecycle element is mutable within the current lifecycle.
     * 
     * @param element The lifecycle element.
     * @return True if the lifecycle element has been registered as mutable within the current
     *         lifecycle.
     */
    public static boolean isMutable(LifecycleElement element) {
        ViewLifecycle viewLifecycle = TL_VIEW_LIFECYCLE.get();

        if (viewLifecycle == null || viewLifecycle.mutableIdentities == null) {
            throw new IllegalStateException("No view lifecycle is active on this thread");
        }

        // TODO: Isolate mutability to same lifecycle
        // viewLifecycle.mutableIdentities.contains(getIdentity(element));
        return true;
    }

    /**
     * Determine if a call to {@link #getMutableCopy(View)} is in progress.
     * 
     * @return True if a call to {@link #getMutableCopy(View)} is in progress.
     */
    public static boolean isCopyActive() {
        ViewLifecycle viewLifecycle = TL_VIEW_LIFECYCLE.get();
        return viewLifecycle != null && viewLifecycle.copy;
    }

    /**
     * Performs the complete component lifecycle on the component passed in for use during a refresh process.
     *
     * <p>
     * Performs the complete component lifecycle on the component passed in, in this order:
     * performComponentInitialization, performComponentApplyModel, and performComponentFinalize.
     * </p>
     * 
     * <p>
     * Some adjustments are made to account for the
     * component being processed without its parent. The component within the view (contained on the form) is
     * retrieved to obtain the context to use (such as parent). The created components id is then updated to match
     * the current id within the view.
     * </p>
     *
     * @param view view instance the component belongs to
     * @param model object containing the full view data
     * @param component component instance to perform lifecycle for
     * @param origId id of the component within the view, used to pull the current component from the view
     * @return The results of performing the lifecycle on the provided view.
     * @see {@link org.kuali.rice.krad.uif.service.ViewHelperService#performComponentLifecycle(
     *org.kuali.rice.krad.uif.view.View, java.lang.Object, org.kuali.rice.krad.uif.component.Component,
     *      java.lang.String)
     * @see {@link #performComponentInitialization(org.kuali.rice.krad.uif.view.View, Object,
     *      org.kuali.rice.krad.uif.component.Component)}
     * @see {@link #performComponentApplyModel(View, Component, Object)}
     * @see {@link #performComponentFinalize(View, Component, Object, Component, Map)}
     */
    public static ViewLifecycleResult performComponentLifecycle(
            View view, final Object model, final Component component, final String origId) {
        return encapsulateLifecycle(view, new Runnable() {
            @Override
            public void run() {
                ViewLifecycle viewLifecycle = getActiveLifecycle();
                View view = viewLifecycle.getView();
                Component newComponent = component.copy();

                Component origComponent = view.getViewIndex().getComponentById(origId);

                view.assignComponentIds(newComponent);

                Map<String, Object> origContext = origComponent.getContext();

                Component parent = origContext == null ? null : (Component) origContext
                        .get(UifConstants.ContextVariableNames.PARENT);

                // update context on all components within the refresh component to catch context set by parent
                if (origContext != null) {
                    newComponent.pushAllToContext(origContext);

                    List<Component> nestedComponents = ComponentUtils.getAllNestedComponents(newComponent);
                    for (Component nestedComponent : nestedComponents) {
                        nestedComponent.pushAllToContext(origContext);
                    }
                }

                // make sure the dataAttributes are the same as original
                newComponent.setDataAttributes(origComponent.getDataAttributes());

                // initialize the expression evaluator
                view.getViewHelperService().getExpressionEvaluator().initializeEvaluationContext(model);

                // the expression graph for refreshed components is captured in the view index (initially it might expressions
                // might have come from a parent), after getting the expression graph then we need to populate the expressions
                // on the configurable for which they apply
                Map<String, String> expressionGraph = view.getViewIndex().getComponentExpressionGraphs().get(
                        newComponent.getBaseId());
                newComponent.setExpressionGraph(expressionGraph);
                ExpressionUtils.populatePropertyExpressionsFromGraph(newComponent, false);

                // binding path should stay the same
                if (newComponent instanceof DataBinding) {
                    ((DataBinding) newComponent).setBindingInfo(((DataBinding) origComponent).getBindingInfo());
                    ((DataBinding) newComponent).getBindingInfo().setBindingPath(
                            ((DataBinding) origComponent).getBindingInfo().getBindingPath());
                }

                // copy properties that are set by parent components in the full view lifecycle
                if (newComponent instanceof Field) {
                    ((Field) newComponent).setLabelRendered(((Field) origComponent).isLabelRendered());
                }

                if (origComponent.isRefreshedByAction()) {
                    newComponent.setRefreshedByAction(true);
                }

                // reset data if needed
                if (newComponent.isResetDataOnRefresh()) {
                    // TODO: this should handle groups as well, going through nested data fields
                    if (newComponent instanceof DataField) {
                        // TODO: should check default value

                        // clear value
                        ObjectPropertyUtils.initializeProperty(model,
                                ((DataField) newComponent).getBindingInfo().getBindingPath());
                    }
                }

                viewLifecycle.performComponentInitialization(model, newComponent);

                // adjust IDs for suffixes that might have been added by a parent component during the full view lifecycle
                String suffix = StringUtils.replaceOnce(origComponent.getId(), origComponent.getBaseId(), "");
                if (StringUtils.isNotBlank(suffix)) {
                    ComponentUtils.updateIdWithSuffix(newComponent, suffix);
                    ComponentUtils.updateChildIdsWithSuffixNested(newComponent, suffix);
                }

                // for collections that are nested in the refreshed group, we need to adjust the binding prefix and
                // set the sub collection id prefix from the original component (this is needed when the group being
                // refreshed is part of another collection)
                if (newComponent instanceof Group || newComponent instanceof FieldGroup) {
                    List<CollectionGroup> origCollectionGroups = ComponentUtils.getComponentsOfTypeShallow(
                            origComponent,
                            CollectionGroup.class);
                    List<CollectionGroup> collectionGroups = ComponentUtils.getComponentsOfTypeShallow(newComponent,
                            CollectionGroup.class);

                    for (int i = 0; i < collectionGroups.size(); i++) {
                        CollectionGroup origCollectionGroup = origCollectionGroups.get(i);
                        CollectionGroup collectionGroup = collectionGroups.get(i);

                        String prefix = origCollectionGroup.getBindingInfo().getBindByNamePrefix();
                        if (StringUtils.isNotBlank(prefix) && StringUtils.isBlank(
                                collectionGroup.getBindingInfo().getBindByNamePrefix())) {
                            ComponentUtils.prefixBindingPath(collectionGroup, prefix);
                        }

                        String lineSuffix = origCollectionGroup.getSubCollectionSuffix();
                        collectionGroup.setSubCollectionSuffix(lineSuffix);
                    }

                    // Handle LightTables, as well
                    List<LightTable> origLightTables = ComponentUtils.getComponentsOfTypeShallow(origComponent,
                            LightTable.class);
                    List<LightTable> lightTables = ComponentUtils.getComponentsOfTypeShallow(newComponent,
                            LightTable.class);

                    for (int i = 0; i < lightTables.size(); i++) {
                        LightTable origLightTable = origLightTables.get(i);
                        LightTable lightTable = lightTables.get(i);

                        String prefix = origLightTable.getBindingInfo().getBindByNamePrefix();
                        if (StringUtils.isNotBlank(prefix) && StringUtils.isBlank(
                                lightTable.getBindingInfo().getBindByNamePrefix())) {
                            ComponentUtils.prefixBindingPath(lightTable, prefix);
                        }
                    }
                }

                // if disclosed by action and request was made, make sure the component will display
                if (newComponent.isDisclosedByAction()) {
                    ComponentUtils.setComponentPropertyFinal(newComponent, UifPropertyPaths.RENDER, true);
                    ComponentUtils.setComponentPropertyFinal(newComponent, UifPropertyPaths.HIDDEN, false);
                }

                viewLifecycle.performComponentApplyModel(view, newComponent, model, new HashMap<String, Integer>());
                view.getViewIndex().indexComponent(newComponent);

                // adjust nestedLevel property on some specific collection cases
                if (newComponent instanceof Container) {
                    ComponentUtils.adjustNestedLevelsForTableCollections((Container) newComponent, 0);
                } else if (newComponent instanceof FieldGroup) {
                    ComponentUtils.adjustNestedLevelsForTableCollections(((FieldGroup) newComponent).getGroup(), 0);
                }

                viewLifecycle.performComponentFinalize(view, newComponent, model, parent);

                // make sure id, binding, and label settings stay the same as initial
                if (newComponent instanceof Group || newComponent instanceof FieldGroup) {
                    List<Component> nestedGroupComponents = ComponentUtils.getAllNestedComponents(newComponent);
                    List<Component> originalNestedGroupComponents = ComponentUtils
                            .getAllNestedComponents(origComponent);

                    for (Component nestedComponent : nestedGroupComponents) {
                        Component origNestedComponent = ComponentUtils.findComponentInList(
                                originalNestedGroupComponents,
                                nestedComponent.getId());

                        if (origNestedComponent != null) {
                            // update binding
                            if (nestedComponent instanceof DataBinding) {
                                ((DataBinding) nestedComponent).setBindingInfo(
                                        ((DataBinding) origNestedComponent).getBindingInfo());
                                ((DataBinding) nestedComponent).getBindingInfo().setBindingPath(
                                        ((DataBinding) origNestedComponent).getBindingInfo().getBindingPath());
                            }

                            // update label rendered flag
                            if (nestedComponent instanceof Field) {
                                ((Field) nestedComponent).setLabelRendered(((Field) origNestedComponent)
                                        .isLabelRendered());
                            }

                            if (origNestedComponent.isRefreshedByAction()) {
                                nestedComponent.setRefreshedByAction(true);
                            }
                        }
                    }
                }

                // get script for generating growl messages
                String growlScript = viewLifecycle.buildGrowlScript();
                ((ViewModel) model).setGrowlScript(growlScript);

                view.getViewIndex().indexComponent(newComponent);
                
                viewLifecycle.refreshComponent = newComponent;
            }
        });
    }

    /**
     * Indicate that a lifecycle element is mutable within the current lifecycle.
     * 
     * @param element The lifecycle element.
     */
    public static void setMutable(LifecycleElement element) {
        ViewLifecycle viewLifecycle = TL_VIEW_LIFECYCLE.get();

        if (viewLifecycle == null) {
            throw new IllegalStateException("No view lifecycle is active on this thread");
        }

        viewLifecycle.mutableIdentities.add(getIdentity(element));
    }

}
