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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.data.DataObjectUtils;
import org.kuali.rice.krad.datadictionary.validator.ValidationController;
import org.kuali.rice.krad.messages.MessageService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.component.PropertyReplacer;
import org.kuali.rice.krad.uif.component.RequestParameter;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.container.LightTable;
import org.kuali.rice.krad.uif.container.PageGroup;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.freemarker.LifecycleRenderingContext;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.BooleanMap;
import org.kuali.rice.krad.uif.util.CloneUtils;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ExpressionUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewAuthorizer;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.view.ViewPresentationController;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.GrowlMessage;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.valuefinder.ValueFinder;
import org.kuali.rice.krad.web.form.UifFormBase;

/**
 * Lifecycle object created during the view processing to hold event registrations.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see LifecycleEventListener
 */
@SuppressWarnings("deprecation")
public class ViewLifecycle implements Serializable {

    private static Logger LOG = Logger.getLogger(ViewLifecycle.class);

    private static final long serialVersionUID = -4767600614111642241L;

    private static final ThreadLocal<ViewLifecycleProcessor> PROCESSOR = new ThreadLocal<ViewLifecycleProcessor>();

    /**
     * Enumerates potential lifecycle events.
     */
    public static enum LifecycleEvent {
        LIFECYCLE_COMPLETE
    }

    private static Boolean strict;
    private static Boolean renderInLifecycle;
    private static Boolean asynchronousLifecycle;
    private static Boolean trace;

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
     * Determine whether or not the lifecycle is operating in strict mode. In general, strict mode
     * is preferred and should be used in development. However, due to it's recent addition several
     * applications may not operate in a stable manner in strict mode so its use is optional. Once
     * strictness violations have been resolved, then strict mode may be enabled to ensure that
     * further violations are not introduced.
     * 
     * <p>
     * This value is controlled by the configuration parameter
     * &quot;krad.uif.lifecycle.strict&quot;. In Rice 2.4, the view lifecycle is *not* strict by
     * default.
     * </p>
     * 
     * @return True if exceptions will be thrown due to strictness violations, false if a warning
     *         should be logged instead.
     */
    public static boolean isStrict() {
        if (strict == null) {
            strict = ConfigContext.getCurrentContextConfig().getBooleanProperty(
                    KRADConstants.ConfigParameters.KRAD_STRICT_LIFECYCLE, false);
        }

        return strict;
    }

    /**
     * Determine whether or not to enable rendering within the lifecycle.
     * 
     * <p>
     * This value is controlled by the configuration parameter
     * &quot;krad.uif.lifecycle.render&quot;.
     * </p>
     * 
     * @return True if rendering will be performed within the lifecycle, false if all rendering
     *         should be deferred for Spring view processing.
     */
    public static boolean isRenderInLifecycle() {
        if (renderInLifecycle == null) {
            renderInLifecycle = ConfigContext.getCurrentContextConfig().getBooleanProperty(
                    KRADConstants.ConfigParameters.KRAD_RENDER_IN_LIFECYCLE, false);
        }

        return renderInLifecycle;
    }

    /**
     * Determine whether or not to processing view lifecycle phases asynchronously.
     * 
     * <p>
     * This value is controlled by the configuration parameter
     * &quot;krad.uif.lifecycle.asynchronous&quot;.
     * </p>
     * 
     * @return True if view lifecycle phases should be performed asynchronously, false for
     *         synchronous operation.
     */
    public static boolean isAsynchronousLifecycle() {
        if (asynchronousLifecycle == null) {
            asynchronousLifecycle = ConfigContext.getCurrentContextConfig().getBooleanProperty(
                    KRADConstants.ConfigParameters.KRAD_VIEW_LIFECYCLE_ASYNCHRONOUS, false);
        }

        return asynchronousLifecycle;
    }

    /**
     * Determine whether or not to log trace details at the info level for troubleshooting lifecycle
     * phases.
     * 
     * <p>
     * This value is controlled by the configuration parameter
     * &quot;krad.uif.lifecycle.trace&quot;.
     * </p>
     * 
     * @return True if view lifecycle phases processing information should be logged at the info level.
     */
    public static boolean isTrace() {
        if (trace == null) {
            trace = ConfigContext.getCurrentContextConfig().getBooleanProperty(
                    KRADConstants.ConfigParameters.KRAD_VIEW_LIFECYCLE_TRACE, false);
        }

        return trace;
    }

    /**
     * Report an illegal state in the view lifecycle.
     * 
     * <p>
     * When {@link #isStrict()} returns true, {@link IllegalStateException} will be thrown.
     * Otherwise, a warning will be logged.
     * </p>
     * 
     * @param message The message describing the illegal state.
     * @throws IllegalStateException If strict mode is enabled.
     */
    public static void reportIllegalState(String message) {
        reportIllegalState(message, null);
    }

    /**
     * Report an illegal state in the view lifecycle.
     * 
     * <p>
     * When {@link #isStrict()} returns true, {@link IllegalStateException} will be thrown.
     * Otherwise, a warning will be logged.
     * </p>
     * 
     * @param message The message describing the illegal state.
     * @param cause The (potential) cause of the illegal state.
     * @throws IllegalStateException If strict mode is enabled.
     */
    public static void reportIllegalState(String message, Throwable cause) {
        IllegalStateException illegalState = new IllegalStateException(
                message + "\nPhase: " + ViewLifecycle.getPhase(), cause);

        if (ViewLifecycle.isStrict()) {
            throw illegalState;
        } else {
            LOG.warn(illegalState.getMessage(), illegalState);
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
     * The view being processed by this lifecycle.
     */
    private final View view;

    /**
     * The model involved in the current view lifecycle.
     */
    final Object model;

    /**
     * The active servlet request for this lifecycle.
     */
    final HttpServletRequest request;

    /**
     * The active servlet response for this lifecycle.
     */
    final HttpServletResponse response;

    /**
     * Private constructor, for spawning a lifecycle context.
     * 
     * @see #getActiveLifecycle() For access to a thread-local instance.
     */
    private ViewLifecycle(View view, Object model,
            HttpServletRequest request, HttpServletResponse response) {
        this.view = view;
        this.model = model;
        this.request = request;
        this.response = response;
        this.helper = view.getViewHelperService();
        this.eventRegistrations = Collections.synchronizedList(new ArrayList<EventRegistration>());
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
     * Uses reflection to find all fields defined on the <code>View</code> instance that have the
     * <code>RequestParameter</code> annotation (which indicates the field may be populated by the
     * request).
     * 
     * <p>
     * The <code>View</code> instance is inspected for fields that have the
     * <code>RequestParameter</code> annotation and if corresponding parameters are found in the
     * request parameter map, the request value is used to set the view property. The Map of
     * parameter name/values that match are placed in the view so they can be later retrieved to
     * rebuild the view. Custom <code>ViewServiceHelper</code> implementations can add additional
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
        View view = getView();

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
     * Runs the lifecycle process for the given component from the initialize phase through the
     * phase prior to the currently active phase.
     * 
     * @param model object providing the view data
     * @param component component to run the lifecycle phases for
     * @param parent parent component for the component being processed
     */
    public static void spawnSubLifecyle(Object model, Component component, Component parent) {
        spawnSubLifecyle(model, component, parent, null, null, true);
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
     * @param asynchronous True to spawn the component lifecycle in the next available worker, false
     *        to spawn immediately in the same thread.
     */
    public static void spawnSubLifecyle(Object model, Component component, Component parent,
            String startPhase, String endPhase, boolean asynchronous) {
        if (component == null) {
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Spawning sub-lifecycle for component: " + component.getId());
        }

        if (StringUtils.isBlank(startPhase)) {
            String compStatus = component.getViewStatus();

            if (UifConstants.ViewStatus.CREATED.equals(compStatus)) {
                startPhase = UifConstants.ViewPhases.INITIALIZE;
            } else if (UifConstants.ViewStatus.INITIALIZED.equals(compStatus)) {
                startPhase = UifConstants.ViewPhases.APPLY_MODEL;
            } else if (UifConstants.ViewStatus.MODEL_APPLIED.equals(compStatus)) {
                startPhase = UifConstants.ViewPhases.FINALIZE;
            } else {
                reportIllegalState("View lifecycle has already been applied to "
                        + component.getClass().getName() + " " + component.getId());
            }

        } else if (!UifConstants.ViewPhases.INITIALIZE.equals(startPhase) && !UifConstants.ViewPhases.APPLY_MODEL
                .equals(startPhase) && !UifConstants.ViewPhases.FINALIZE.equals(startPhase)) {
            throw new RuntimeException("Invalid start phase given: " + startPhase);
        }

        if (StringUtils.isBlank(endPhase)) {
            ViewLifecyclePhase activePhase = ViewLifecycle.getPhase();

            if (activePhase != null && UifConstants.ViewPhases.APPLY_MODEL.equals(activePhase.getViewPhase())) {
                if (!UifConstants.ViewPhases.INITIALIZE.equals(startPhase)) {
                    return;
                }
                endPhase = UifConstants.ViewPhases.INITIALIZE;

            } else if (activePhase != null && UifConstants.ViewPhases.FINALIZE.equals(activePhase.getViewPhase())) {
                if (!UifConstants.ViewPhases.INITIALIZE.equals(startPhase)
                        && !UifConstants.ViewPhases.APPLY_MODEL.equals(startPhase)) {
                    return;
                }
                endPhase = UifConstants.ViewPhases.APPLY_MODEL;

            } else {
                endPhase = UifConstants.ViewPhases.FINALIZE;
            }

        } else if (!UifConstants.ViewPhases.INITIALIZE.equals(endPhase) && !UifConstants.ViewPhases.APPLY_MODEL.equals(
                endPhase) && !UifConstants.ViewPhases.FINALIZE.equals(endPhase)) {
            throw new RuntimeException("Invalid end phase given: " + endPhase);
        }

        // Push sub-lifecycle phases in reverse order.  These will be processed immediately after
        // the active phase ends, starting with the last phase pushed.
        ViewLifecycleProcessor processor = getProcessor();

        if (asynchronous) {

            // Perform sub-lifecycle immediately in the same thread
            FinalizeComponentPhase finalPhase = null;
            if (UifConstants.ViewPhases.FINALIZE.equals(endPhase)) {
                finalPhase = LifecyclePhaseFactory.finalize(component, model, 0, parent);
            }

            if (UifConstants.ViewPhases.FINALIZE.equals(startPhase)) {
                processor.pushPendingPhase(finalPhase);
                return;
            }

            ApplyModelComponentPhase applyModelPhase = null;
            if (UifConstants.ViewPhases.FINALIZE.equals(endPhase) ||
                    UifConstants.ViewPhases.APPLY_MODEL.equals(endPhase)) {
                applyModelPhase = LifecyclePhaseFactory.applyModel(
                        component, model, 0, parent, finalPhase, new HashSet<String>());
            }

            if (UifConstants.ViewPhases.APPLY_MODEL.equals(startPhase)) {
                processor.pushPendingPhase(applyModelPhase);
                return;
            }

            InitializeComponentPhase initializePhase = 
                    LifecyclePhaseFactory.initialize(component, model, 0, parent, applyModelPhase);
            processor.pushPendingPhase(initializePhase);

        } else {

            ViewLifecycleProcessor synchProcessor =
                    new SynchronousViewLifecycleProcessor(processor.getLifecycle());
            try {
                PROCESSOR.set(synchProcessor);

                // Perform sub-lifecycle immediately in the same thread
                if (UifConstants.ViewPhases.INITIALIZE.equals(startPhase)) {
                    synchProcessor.performPhase(LifecyclePhaseFactory.initialize(component, model, 0, parent, null));
                }

                if (UifConstants.ViewPhases.INITIALIZE.equals(endPhase)) {
                    return;
                }

                if (UifConstants.ViewPhases.INITIALIZE.equals(startPhase) ||
                        UifConstants.ViewPhases.APPLY_MODEL.equals(startPhase)) {
                    synchProcessor.performPhase(LifecyclePhaseFactory.applyModel(component, model, 0, parent, null, new HashSet<String>()));
                }

                if (UifConstants.ViewPhases.APPLY_MODEL.equals(endPhase)) {
                    return;
                }

                synchProcessor.performPhase(LifecyclePhaseFactory.finalize(component, model, 0, parent));

            } finally {
                PROCESSOR.set(processor);
            }
        }
    }

    /**
     * Encapsulate a new view lifecycle process on the current thread.
     * 
     * @param lifecycleProcess The lifecycle process to encapsulate.
     * @return
     */
    public static void encapsulateLifecycle(View view, Object model,
            HttpServletRequest request, HttpServletResponse response, Runnable lifecycleProcess) {
        ViewLifecycleProcessor processor = PROCESSOR.get();
        if (processor != null) {
            throw new IllegalStateException("Another lifecycle is already active on this thread");
        }

        try {
            ViewLifecycle viewLifecycle = new ViewLifecycle(view, model, request, response);
            processor = isAsynchronousLifecycle()
                    ? new AsynchronousViewLifecycleProcessor(viewLifecycle)
                    : new SynchronousViewLifecycleProcessor(viewLifecycle);
            PROCESSOR.set(processor);

            lifecycleProcess.run();

        } finally {
            PROCESSOR.remove();
        }
    }

    /**
     * Get the helper active on this context.
     * 
     * @return the helper
     */
    public static ViewHelperService getHelper() {
        ViewLifecycle active = getActiveLifecycle();

        if (active.helper == null) {
            throw new IllegalStateException("Context view helper is not available");
        }

        return active.helper;
    }

    /**
     * Get the view active within this context.
     * 
     * <p>
     * After the lifecycle has completed, this view becomes the resulting view.
     * </p>
     * 
     * @return The view active within this context.
     */
    public static View getView() {
        ViewLifecycle active = getActiveLifecycle();

        if (active.view == null) {
            throw new IllegalStateException("Context view is not available");
        }

        return active.view;
    }

    /**
     * Get the model related to the view active within this context.
     * 
     * @return The model related to the view active within this context.
     */
    public static Object getModel() {
        ViewLifecycle active = getActiveLifecycle();

        if (active.model == null) {
            throw new IllegalStateException("Model is not available");
        }

        return active.model;
    }

    /**
     * Get the current phase of the active lifecycle.
     * 
     * @return The current phase of the active lifecycle, or null if no phase is currently active.
     */
    public static ViewLifecyclePhase getPhase() {
        ViewLifecycleProcessor processor = PROCESSOR.get();
        return processor == null ? null : processor.getActivePhase();
    }

    /**
     * Get the rendering context for this lifecycle.
     * 
     * @return The rendering context for this lifecycle.
     */
    public static LifecycleRenderingContext getRenderingContext() {
        ViewLifecycleProcessor processor = PROCESSOR.get();
        return processor == null ? null : processor.getRenderingContext();
    }

    /**
     * Get the lifecycle processor active on the current thread.
     * 
     * @return The lifecycle processor active on the current thread.
     */
    public static ViewLifecycleProcessor getProcessor() {
        ViewLifecycleProcessor processor = PROCESSOR.get();

        if (processor == null) {
            throw new IllegalStateException("No view lifecycle is active on this thread");
        }

        return processor;
    }

    /**
     * Note a processor as active on the current thread.
     * 
     * <p>
     * This method is intended only for use by {@link AsynchronousLifecycleWorker} in setting the
     * context for worker threads. Use
     * {@link #encapsulateLifecycle(View, Object, HttpServletRequest, HttpServletResponse, Runnable)}
     * to populate an appropriate processor for for web request and other transaction threads.
     * </p>
     * 
     * @param processor The processor to activate on the current thread.
     */
    static void setProcessor(ViewLifecycleProcessor processor) {
        ViewLifecycleProcessor active = PROCESSOR.get();

        if (active != null && processor != null) {
            throw new IllegalStateException("Another lifecycle processor is already active on this thread");
        }

        if (processor == null) {
            PROCESSOR.remove();
        } else {
            PROCESSOR.set(processor);
        }
    }

    /**
     * Get the view context active on the current thread.
     * 
     * @return The view context active on the current thread.
     */
    public static ViewLifecycle getActiveLifecycle() {
        return getProcessor().getLifecycle();
    }

    /**
     * Determine if a lifecycle processor is active on the current thread.
     * 
     * @return True if a lifecycle processor is active on the current thread.
     */
    public static boolean isActive() {
        return PROCESSOR.get() != null;
    }

    /**
     * Executes the view lifecycle on the given <code>View</code> instance which will prepare it for
     * rendering
     * 
     * <p>
     * Any configuration sent through the options Map is used to initialize the View. This map
     * contains present options the view is aware of and will typically come from request
     * parameters. e.g. For maintenance Views there is the maintenance type option (new, edit, copy)
     * </p>
     * 
     * <p>
     * After view retrieval, applies updates to the view based on the model data which Performs
     * dynamic generation of fields (such as collection rows), conditional logic, and state updating
     * (conditional hidden, read-only, required).
     * </p>
     * 
     * @param view - view instance that should be built
     * @param model - object instance containing the view data
     * @param parameters - Map of key values pairs that provide configuration for the
     *        <code>View</code>, this is generally comes from the request and can be the request
     *        parameter Map itself. Any parameters not valid for the View will be filtered out
     * 
     * @return A copy of the view, built for rendering.
     */
    public static void buildView(View view, Object model,
            HttpServletRequest request, HttpServletResponse response,
            final Map<String, String> parameters) {
        ViewLifecycle.encapsulateLifecycle(view, model, request, response, new Runnable() {
            @Override
            public void run() {
                ViewLifecycleProcessor processor = getProcessor();
                ViewLifecycle viewLifecycle = processor.getLifecycle();
                View view = ViewLifecycle.getView();
                ViewHelperService helper = ViewLifecycle.getHelper();
                UifFormBase model = (UifFormBase) ViewLifecycle.getModel();

                if (isTrace()) {
                    ProcessLogger.trace("begin-view-lifecycle:" + view.getId());
                }
                
                // populate view from request parameters
                viewLifecycle.populateViewFromRequestParameters(parameters);

                // backup view request parameters on form for recreating lost
                // views (session timeout)
                model.setViewRequestParameters(view.getViewRequestParameters());

                // invoke initialize phase on the views helper service
                if (LOG.isInfoEnabled()) {
                    LOG.info("performing initialize phase for view: " + view.getId());
                }

                helper.performCustomViewInitialization(model);

                processor.performPhase(LifecyclePhaseFactory.initialize(view, model, 0, null, null));

                if (isTrace()) {
                    ProcessLogger.trace("initialize:" + view.getId());
                }
                
                // Apply Model Phase
                if (LOG.isInfoEnabled()) {
                    LOG.info("performing apply model phase for view: " + view.getId());
                }
                
                // apply default values if they have not been applied yet
                if (!model.isDefaultsApplied()) {
                    viewLifecycle.applyDefaultValues(view, view, model);
                    model.setDefaultsApplied(true);
                }

                // get action flag and edit modes from authorizer/presentation controller
                viewLifecycle.retrieveEditModesAndActionFlags(view, (UifFormBase) model);

                // set view context for conditional expressions
                viewLifecycle.setViewContext();

                processor.performPhase(LifecyclePhaseFactory.applyModel(view, model));

                if (isTrace()) {
                    ProcessLogger.trace("apply-model:" + view.getId());
                }

                // Finalize Phase
                if (LOG.isInfoEnabled()) {
                    LOG.info("performing finalize phase for view: " + view.getId());
                }

                // get script for generating growl messages
                String growlScript = viewLifecycle.buildGrowlScript();
                ((ViewModel) model).setGrowlScript(growlScript);

                processor.performPhase(LifecyclePhaseFactory.finalize(view, model, 0, null));
                
                if (isTrace()) {
                    ProcessLogger.trace("finalize:" + view.getId());
                }
            }
        });

        // Validation of the page's beans
        if (CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsBoolean(
                UifConstants.VALIDATE_VIEWS_ONBUILD)) {
            ValidationController validator = new ValidationController(true, true, true, true, false);
            Log tempLogger = LogFactory.getLog(ViewLifecycle.class);
            validator.validate(view, tempLogger, false);
        }
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
    public static void performComponentLifecycle(View view, Object model,
            HttpServletRequest request, HttpServletResponse response, final Component component,
            final String origId) {
        encapsulateLifecycle(view, model, request, response, new Runnable() {
            @Override
            public void run() {
                ViewLifecycleProcessor processor = getProcessor();
                View view = ViewLifecycle.getView();
                Object model = ViewLifecycle.getModel();

                if (isTrace()) {
                    ProcessLogger.trace("begin-component-lifecycle:" + component.getId());
                }
                
                Component newComponent = component;
                Component origComponent = view.getViewIndex().getComponentById(origId);

                // check if the component is nested in a box layout in order to
                // reapply the layout item style
                List<String> origCss = origComponent.getCssClasses();
                if (origCss != null && (model instanceof UifFormBase)
                        && ((UifFormBase) model).isUpdateComponentRequest()) {

                    if (origCss.contains(UifConstants.BOX_LAYOUT_HORIZONTAL_ITEM_CSS)) {
                        component.addStyleClass(UifConstants.BOX_LAYOUT_HORIZONTAL_ITEM_CSS);
                    } else if (origCss.contains(UifConstants.BOX_LAYOUT_VERTICAL_ITEM_CSS)) {
                        component.addStyleClass(UifConstants.BOX_LAYOUT_VERTICAL_ITEM_CSS);
                    }
                }

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
                
                if (isTrace()) {
                    ProcessLogger.trace("ready:" + newComponent.getId());
                }
                
                processor.performPhase(LifecyclePhaseFactory.initialize(newComponent, model));

                if (isTrace()) {
                    ProcessLogger.trace("initialize:" + newComponent.getId());
                }

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

                processor.performPhase(LifecyclePhaseFactory.applyModel(newComponent, model, parent));

                if (isTrace()) {
                    ProcessLogger.trace("apply-model:" + newComponent.getId());
                }

                // adjust nestedLevel property on some specific collection cases
                if (newComponent instanceof Container) {
                    ComponentUtils.adjustNestedLevelsForTableCollections((Container) newComponent, 0);
                } else if (newComponent instanceof FieldGroup) {
                    ComponentUtils.adjustNestedLevelsForTableCollections(((FieldGroup) newComponent).getGroup(), 0);
                }

                processor.performPhase(LifecyclePhaseFactory.finalize(newComponent, model, parent));

                if (isTrace()) {
                    ProcessLogger.trace("finalize:" + newComponent.getId());
                }

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
                String growlScript = processor.getLifecycle().buildGrowlScript();
                ((ViewModel) model).setGrowlScript(growlScript);

                view.getViewIndex().indexComponent(newComponent);

                PageGroup page = view.getCurrentPage();
                // regenerate server message content for page
                page.getValidationMessages().generateMessages(false, view, model, page);

                if (isTrace()) {
                    ProcessLogger.trace("end-component-lifecycle:" + newComponent.getId());
                }
            }
        });
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
     * Applies the default value configured for the given field (if any) to the line given object
     * property that is determined by the given binding path
     * 
     * @param view view instance the field belongs to
     * @param object object that should be populated
     * @param dataField field to check for configured default value
     * @param bindingPath path to the property on the object that should be populated
     */
    public void populateDefaultValueForField(View view, Object object, DataField dataField, String bindingPath) {
        if (!ObjectPropertyUtils.isReadableProperty(object, bindingPath)
                || !ObjectPropertyUtils.isWritableProperty(object, bindingPath)) {
            return;
        }

        Object currentValue = ObjectPropertyUtils.getPropertyValue(object, bindingPath);

        // Default value only applies when the value being set is null (has no value on the form)
        if (currentValue != null) {
            return;
        }

        Object defaultValue = getDefaultValueForField(view, object, dataField);

        ObjectPropertyUtils.setPropertyValue(object, bindingPath, defaultValue);
    }

    /**
     * Retrieves the default value that is configured for the given data field
     * 
     * <p>
     * The field's default value is determined in the following order:
     * 
     * <ol>
     * <li>If default value on field is non-blank</li>
     * <li>If expression is found for default value</li>
     * <li>If default value finder class is configured for field</li>
     * <li>If an expression is found for default values</li>
     * <li>If default values on field is not null</li>
     * </ol>
     * </p>
     * 
     * @param view view instance the field belongs to
     * @param object object that should be populated
     * @param dataField field to retrieve default value for
     * @return Object default value for field or null if value was not found
     */
    protected Object getDefaultValueForField(View view, Object object, DataField dataField) {
        Object defaultValue = null;

        if (StringUtils.isNotBlank(dataField.getDefaultValue())) {
            defaultValue = dataField.getDefaultValue();
        } else if ((dataField.getExpressionGraph() != null) && dataField.getExpressionGraph().containsKey(
                UifConstants.ComponentProperties.DEFAULT_VALUE)) {
            defaultValue = dataField.getExpressionGraph().get(UifConstants.ComponentProperties.DEFAULT_VALUE);
        } else if (dataField.getDefaultValueFinderClass() != null) {
            ValueFinder defaultValueFinder = DataObjectUtils.newInstance(dataField.getDefaultValueFinderClass());

            defaultValue = defaultValueFinder.getValue();
        } else if ((dataField.getExpressionGraph() != null) && dataField.getExpressionGraph().containsKey(
                UifConstants.ComponentProperties.DEFAULT_VALUES)) {
            defaultValue = dataField.getExpressionGraph().get(UifConstants.ComponentProperties.DEFAULT_VALUES);
        } else if (dataField.getDefaultValues() != null) {
            defaultValue = dataField.getDefaultValues();
        }

        ExpressionEvaluator expressionEvaluator = getHelper().getExpressionEvaluator();

        if ((defaultValue != null) && (defaultValue instanceof String) && expressionEvaluator
                .containsElPlaceholder((String) defaultValue)) {
            Map<String, Object> context = view.getPreModelContext();
            context.putAll(dataField.getContext());

            defaultValue = expressionEvaluator.replaceBindingPrefixes(view, object, (String) defaultValue);
            defaultValue = expressionEvaluator.evaluateExpressionTemplate(context, (String) defaultValue);
        }

        return defaultValue;
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
     * Sets up the view context which will be available to other components through their context
     * for conditional logic evaluation.
     */
    protected void setViewContext() {
        view.pushAllToContext(view.getPreModelContext());

        // evaluate view expressions for further context
        for (Entry<String, String> variableExpression : view.getExpressionVariables().entrySet()) {
            String variableName = variableExpression.getKey();
            Object value = helper.getExpressionEvaluator().evaluateExpression(view.getContext(),
                    variableExpression.getValue());
            view.pushObjectToContext(variableName, value);
        }
    }

}
