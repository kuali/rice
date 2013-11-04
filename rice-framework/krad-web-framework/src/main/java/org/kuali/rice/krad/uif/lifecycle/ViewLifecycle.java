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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.krad.datadictionary.validator.ValidationController;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.freemarker.LifecycleRenderingContext;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.view.DefaultExpressionEvaluator;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Lifecycle object created during the view processing to hold event registrations.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see LifecycleEventListener
 */
public class ViewLifecycle implements Serializable {

    private static Logger LOG = Logger.getLogger(ViewLifecycle.class);

    private static final long serialVersionUID = -4767600614111642241L;

    private static final ThreadLocal<ViewLifecycleProcessor> PROCESSOR = new ThreadLocal<ViewLifecycleProcessor>();

    private static Boolean strict;
    private static Boolean renderInLifecycle;
    private static Boolean asynchronousLifecycle;
    private static Boolean trace;

    /**
     * Enumerates potential lifecycle events.
     */
    public static enum LifecycleEvent {
        
        /**
         * Indicates that the finalize phase processing has been completed on the component.
         */
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
         * Sets the registered Event.
         * 
         * @param event The registered event.
         * @see EventRegistration#getEvent()
         */
        public void setEvent(LifecycleEvent event) {
            this.event = event;
        }

        /**
         * Sets the component.
         * 
         * @param eventComponent The component.
         * @see EventRegistration#getEventComponent()
         */
        public void setEventComponent(Component eventComponent) {
            this.eventComponent = eventComponent;
        }

        /**
         * Sets the event listener.
         * 
         * @param eventListener The event listener.
         * @see EventRegistration#getEventListener()
         */
        public void setEventListener(LifecycleEventListener eventListener) {
            this.eventListener = eventListener;
        }
    }

    /**
     * Determines whether or not the lifecycle is operating in strict mode.
     * 
     * <p>
     * {@link Component#getViewStatus()} is checked at the beginning and end of each lifecycle
     * phase. When operating in strict mode, when a component is in the wrong status for the current
     * phase {@link IllegalStateException} will be thrown. When not in strict mode, warning messages
     * are logged on the console.
     * </p>
     * 
     * <p>
     * This value is controlled by the configuration parameter
     * &quot;krad.uif.lifecycle.strict&quot;. In Rice 2.4, the view lifecycle is *not* strict by
     * default.
     * </p>
     * 
     * @return true for strict operation, false to treat lifecycle violations as warnings 
     */
    public static boolean isStrict() {
        if (strict == null) {
            strict = ConfigContext.getCurrentContextConfig().getBooleanProperty(
                    KRADConstants.ConfigParameters.KRAD_STRICT_LIFECYCLE, false);
        }

        return strict;
    }

    /**
     * Determines whether or not to enable rendering within the lifecycle.
     * 
     * <p>
     * This value is controlled by the configuration parameter
     * &quot;krad.uif.lifecycle.render&quot;.
     * </p>
     * 
     * @return true for rendering within the lifecycle, false if all rendering should be deferred
     *         for Spring view processing
     */
    public static boolean isRenderInLifecycle() {
        if (renderInLifecycle == null) {
            renderInLifecycle = ConfigContext.getCurrentContextConfig().getBooleanProperty(
                    KRADConstants.ConfigParameters.KRAD_RENDER_IN_LIFECYCLE, false);
        }

        return renderInLifecycle;
    }

    /**
     * Determines whether or not to processing view lifecycle phases asynchronously.
     * 
     * <p>
     * This value is controlled by the configuration parameter
     * &quot;krad.uif.lifecycle.asynchronous&quot;.
     * </p>
     * 
     * @return true if view lifecycle phases should be performed asynchronously, false for
     *         synchronous operation
     */
    public static boolean isAsynchronousLifecycle() {
        if (asynchronousLifecycle == null) {
            asynchronousLifecycle = ConfigContext.getCurrentContextConfig().getBooleanProperty(
                    KRADConstants.ConfigParameters.KRAD_VIEW_LIFECYCLE_ASYNCHRONOUS, false);
        }

        return asynchronousLifecycle;
    }

    /**
     * Determines whether or not to log trace details for troubleshooting lifecycle phases.
     * 
     * <p>
     * View lifecycle tracing is very verbose. This feature should only be enabled for
     * troubleshooting purposes.
     * </p>
     * 
     * <p>
     * This value is controlled by the configuration parameter &quot;krad.uif.lifecycle.trace&quot;.
     * </p>
     * 
     * @return true if view lifecycle phase processing information should be logged
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
     * @param view The view to process with the lifecycle.
     * @param model The model to use in processing the lifecycle.
     * @param request The active servlet request.
     * @param response The active servlet response. 
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
            LOG.debug("Spawning sub-lifecycle for component: "
                    + component.getClass() + " " + component.getId()
                    + (parent == null ? "" : " " + parent.getClass() + " " + parent.getId()));
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

                synchProcessor.getExpressionEvaluator().initializeEvaluationContext(model);
                
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
     * @param view The view to perform lifecycle processing on.
     * @param model The model associated with the view.
     * @param request The active servlet request.
     * @param response The active servlet response.
     * @param lifecycleProcess The lifecycle process to encapsulate.
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
     * Gets the helper active within a lifecycle on the current thread.
     * 
     * @return helper active on the current thread
     */
    public static ViewHelperService getHelper() {
        ViewLifecycle active = getActiveLifecycle();

        if (active.helper == null) {
            throw new IllegalStateException("Context view helper is not available");
        }

        return active.helper;
    }

    /**
     * Gets the view active within a lifecycle on the current thread.
     * 
     * @return view active on the current thread
     */
    public static View getView() {
        ViewLifecycle active = getActiveLifecycle();

        if (active.view == null) {
            throw new IllegalStateException("Context view is not available");
        }

        return active.view;
    }

    /**
     * Return an instance of {@link org.kuali.rice.krad.uif.view.ExpressionEvaluator} that can be used for evaluating
     * expressions contained on the view
     *
     * <p>
     * A ExpressionEvaluator must be initialized with a model for expression evaluation. One instance is
     * constructed for the view lifecycle and made available to all components/helpers through this method
     * </p>
     *
     * @return instance of ExpressionEvaluator
     */
    public static ExpressionEvaluator getExpressionEvaluator() {
        ViewLifecycleProcessor processor = PROCESSOR.get();

        if (processor == null) {
            return new DefaultExpressionEvaluator();
        }
        
        return processor.getExpressionEvaluator();
    }

    /**
     * Gets the model related to the view active within this context.
     * 
     * @return model related to the view active within this context
     */
    public static Object getModel() {
        ViewLifecycle active = getActiveLifecycle();

        if (active.model == null) {
            throw new IllegalStateException("Model is not available");
        }

        return active.model;
    }

    /**
     * Gets the servlet request for this lifecycle.
     * 
     * @return servlet request for this lifecycle
     */
    public static HttpServletRequest getRequest() {
        ViewLifecycle active = getActiveLifecycle();

        if (active.model == null) {
            throw new IllegalStateException("Request is not available");
        }

        return active.request;
    }

    /**
     * Gets the current phase of the active lifecycle, or null if no phase is currently active.
     * 
     * @return current phase of the active lifecycle
     */
    public static ViewLifecyclePhase getPhase() {
        ViewLifecycleProcessor processor = PROCESSOR.get();
        return processor == null ? null : processor.getActivePhase();
    }

    /**
     * Gets the rendering context for this lifecycle.
     * 
     * @return rendering context for this lifecycle
     */
    public static LifecycleRenderingContext getRenderingContext() {
        ViewLifecycleProcessor processor = PROCESSOR.get();
        return processor == null ? null : processor.getRenderingContext();
    }

    /**
     * Gets the lifecycle processor active on the current thread.
     * 
     * @return lifecycle processor active on the current thread
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
     * This method is intended only for use by {@link AsynchronousViewLifecycleProcessor} in setting
     * the context for worker threads. Use
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
     * Gets the view context active on the current thread.
     * 
     * @return view context active on the current thread
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
     * @param request The active servlet request.
     * @param response The active servlet response.
     * @param parameters - Map of key values pairs that provide configuration for the
     *        <code>View</code>, this is generally comes from the request and can be the request
     *        parameter Map itself. Any parameters not valid for the View will be filtered out
     */
    public static void buildView(View view, Object model,
            HttpServletRequest request, HttpServletResponse response,
            final Map<String, String> parameters) {
        ViewLifecycle.encapsulateLifecycle(
                view, model, request, response, new ViewLifecycleFullBuild(parameters));

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
     * @param request The active servlet request.
     * @param response The active servlet response.
     * @param component component instance to perform lifecycle for
     * @param origId id of the component within the view, used to pull the current component from the view
     */
    public static void performComponentLifecycle(View view, Object model,
            HttpServletRequest request, HttpServletResponse response, final Component component,
            final String origId) {
        encapsulateLifecycle(
                view, model, request, response, new ViewLifecycleComponentBuild(origId, component));
    }

}
