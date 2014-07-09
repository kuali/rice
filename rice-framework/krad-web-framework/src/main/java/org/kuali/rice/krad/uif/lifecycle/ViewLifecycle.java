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
package org.kuali.rice.krad.uif.lifecycle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.krad.datadictionary.validator.ValidationController;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.freemarker.LifecycleRenderingContext;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.DefaultExpressionEvaluator;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.ExpressionEvaluatorFactory;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.UifFormBase;

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
    private static Boolean trace;

    private final List<EventRegistration> eventRegistrations;
    private final View view;

    private final ComponentPostMetadata refreshComponentPostMetadata;

    final ViewHelperService helper;

    final Object model;

    final HttpServletRequest request;
    private ViewPostMetadata viewPostMetadata;
    
    private Set<String> visitedIds;

    /**
     * Private constructor, for spawning a lifecycle context.
     *
     * @param view The view to process with the lifecycle
     * @param model The model to use in processing the lifecycle
     * @param refreshComponentPostMetadata when a refresh lifecycle is requested, post metadata for the component
     * that is being refreshed
     * @param request The active servlet request
     * @see #getActiveLifecycle() For access to a thread-local instance.
     */
    private ViewLifecycle(View view, Object model, ComponentPostMetadata refreshComponentPostMetadata,
            HttpServletRequest request) {
        this.view = view;
        this.model = model;
        this.request = request;
        this.refreshComponentPostMetadata = refreshComponentPostMetadata;
        this.helper = view.getViewHelperService();
        this.eventRegistrations = new ArrayList<EventRegistration>();
    }

    /**
     * Encapsulate a new view lifecycle process on the current thread.
     *
     * @param view The view to perform lifecycle processing on.
     * @param model The model associated with the view.
     * @param request The active servlet request.
     * @param lifecycleProcess The lifecycle process to encapsulate.
     */
    public static void encapsulateLifecycle(View view, Object model, HttpServletRequest request,
            Runnable lifecycleProcess) {
        encapsulateLifecycle(view, model, null, null, request, lifecycleProcess);
    }

    /**
     * Encapsulate a new view lifecycle process on the current thread.
     *
     * @param lifecycleProcess The lifecycle process to encapsulate.
     */
    public static void encapsulateLifecycle(View view, Object model, ViewPostMetadata viewPostMetadata,
            ComponentPostMetadata refreshComponentPostMetadata, HttpServletRequest request, Runnable lifecycleProcess) {
        ViewLifecycleProcessor processor = PROCESSOR.get();
        if (processor != null) {
            throw new IllegalStateException("Another lifecycle is already active on this thread");
        }

        try {
            ViewLifecycle viewLifecycle = new ViewLifecycle(view, model, refreshComponentPostMetadata, request);
            processor = isAsynchronousLifecycle() ? new AsynchronousViewLifecycleProcessor(viewLifecycle) :
                    new SynchronousViewLifecycleProcessor(viewLifecycle);
            PROCESSOR.set(processor);

            if (viewPostMetadata != null) {
                viewLifecycle.viewPostMetadata = viewPostMetadata;
            }

            lifecycleProcess.run();

        } finally {
            PROCESSOR.remove();
        }
    }

    /**
     * Performs preliminary processing on a view, prior to caching.
     *
     * <p>Logic evaluated at this preliminary phase result in global modifications to the view's
     * subcomponents, so this method can be used apply additional logic to the View that is both
     * pre-evaluated and shared by all instances of the component.</p>
     *
     * @param view view to preprocess
     */
    public static void preProcess(View view) {
        encapsulateLifecycle(view, null, null, new ViewLifecyclePreProcessBuild());
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
     * @param view view instance that should be built
     * @param model object instance containing the view data
     * @param request The active servlet request.
     * @param parameters - Map of key values pairs that provide configuration for the
     * <code>View</code>, this is generally comes from the request and can be the request
     * parameter Map itself. Any parameters not valid for the View will be filtered out
     */
    public static ViewPostMetadata buildView(View view, Object model, HttpServletRequest request,
            final Map<String, String> parameters) {
        ViewPostMetadata postMetadata = new ViewPostMetadata(view.getId());

        ViewLifecycle.encapsulateLifecycle(view, model, postMetadata, null, request, new ViewLifecycleBuild(parameters,
                null));

        // Validation of the page's beans
        if (CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsBoolean(
                UifConstants.VALIDATE_VIEWS_ONBUILD)) {
            ValidationController validator = new ValidationController(true, true, true, true, false);
            Log tempLogger = LogFactory.getLog(ViewLifecycle.class);
            validator.validate(view, tempLogger, false);
        }

        return postMetadata;
    }

    /**
     * Performs a lifecycle process to rebuild the component given by the update id.
     *
     * @param view view instance the component belongs to
     * @param model object containing the full view data
     * @param request The active servlet request.
     * @param viewPostMetadata post metadata for the view
     * @param componentId id of the component within the view, used to pull the current component from the view
     * @return component instance the lifecycle has been run on
     */
    public static Component performComponentLifecycle(View view, Object model, HttpServletRequest request,
            ViewPostMetadata viewPostMetadata, String componentId) {
        if (viewPostMetadata == null) {
            UifFormBase form = (UifFormBase) model;

            throw new RuntimeException("View post metadata is null which cannot occur for refresh. Form id: "
                    + form.getFormKey() + ", requested form id: " + form.getRequestedFormKey());
        }

        ComponentPostMetadata componentPostMetadata = viewPostMetadata.getComponentPostMetadata(componentId);
        if ((componentPostMetadata == null) || componentPostMetadata.isDetachedComponent()) {
            if (componentPostMetadata == null) {
                componentPostMetadata = viewPostMetadata.initializeComponentPostMetadata(componentId);
            }

            setupStandaloneComponentForRefresh(view, componentId, componentPostMetadata);
        }

        Map<String, List<String>> refreshPathMappings = componentPostMetadata.getRefreshPathMappings();

        encapsulateLifecycle(view, model, viewPostMetadata, componentPostMetadata, request, new ViewLifecycleBuild(null,
                refreshPathMappings));

        return ObjectPropertyUtils.getPropertyValue(view, componentPostMetadata.getPath());
    }

    /**
     * Before running the lifecycle on a component that is not attached to a view, we need to retrieve the component,
     * add it to the dialogs list, and setup its refresh paths.
     *
     * @param view view instance the component should be attached to
     * @param componentId id for the component the lifecycle should be run on
     * @param componentPostMetadata post metadata instance for the component
     * @return instance of component post metadata configured for the component
     */
    protected static void setupStandaloneComponentForRefresh(View view, String componentId,
            ComponentPostMetadata componentPostMetadata) {
        Component refreshComponent = (Component) KRADServiceLocatorWeb.getDataDictionaryService().getDictionaryBean(
                componentId);

        if ((refreshComponent == null) || !(refreshComponent instanceof Group)) {
            throw new RuntimeException("Refresh component was null or not a group instance");
        }

        List<Group> dialogs = new ArrayList<Group>();
        if ((view.getDialogs() != null) && !view.getDialogs().isEmpty()) {
            dialogs.addAll(view.getDialogs());
        }

        dialogs.add((Group) refreshComponent);
        view.setDialogs(dialogs);

        String refreshPath = UifPropertyPaths.DIALOGS + "[" + (view.getDialogs().size() - 1) + "]";
        componentPostMetadata.setPath(refreshPath);

        List<String> refreshPaths = new ArrayList<String>();
        refreshPaths.add(refreshPath);

        Map<String, List<String>> refreshPathMappings = new HashMap<String, List<String>>();

        refreshPathMappings.put(UifConstants.ViewPhases.INITIALIZE, refreshPaths);
        refreshPathMappings.put(UifConstants.ViewPhases.APPLY_MODEL, refreshPaths);
        refreshPathMappings.put(UifConstants.ViewPhases.FINALIZE, refreshPaths);

        componentPostMetadata.setRefreshPathMappings(refreshPathMappings);

        componentPostMetadata.setDetachedComponent(true);
    }

    /**
     * Indicates if the component the phase is being run on is a component being refreshed (if this is a full
     * lifecycle this method will always return false).
     *
     * @return boolean true if the component is being refreshed, false if not
     */
    public static boolean isRefreshComponent(String viewPhase, String viewPath) {
        if (!ViewLifecycle.isRefreshLifecycle()) {
            return false;
        }

        return StringUtils.equals(getRefreshComponentPhasePath(viewPhase), viewPath);
    }

    /**
     * When a refresh lifecycle is being processed, returns the phase path (path at the current phase) for
     * the component being refreshed.
     *
     * @return path for refresh component at this phase, or null if this is not a refresh lifecycle
     */
    public static String getRefreshComponentPhasePath(String viewPhase) {
        if (!ViewLifecycle.isRefreshLifecycle()) {
            return null;
        }

        ComponentPostMetadata refreshComponentPostMetadata = ViewLifecycle.getRefreshComponentPostMetadata();
        if (refreshComponentPostMetadata == null) {
            return null;
        }

        String refreshPath = refreshComponentPostMetadata.getPath();
        if (refreshComponentPostMetadata.getPhasePathMapping() != null) {
            Map<String, String> phasePathMapping = refreshComponentPostMetadata.getPhasePathMapping();

            // find the path for the element at this phase (if it was the same as the final path it will not be
            // in the phase path mapping
            if (phasePathMapping.containsKey(viewPhase)) {
                refreshPath = phasePathMapping.get(viewPhase);
            }
        }

        return refreshPath;
    }

    /**
     * Invoked when an event occurs to invoke registered listeners.
     *
     * @param event event that has occurred
     * @param view view instance the lifecycle is being executed for
     * @param model object containing the model data
     * @param eventElement component instance the event occurred on/for
     * @see LifecycleEvent
     */
    public void invokeEventListeners(LifecycleEvent event, View view, Object model, LifecycleElement eventElement) {
        synchronized (eventRegistrations) {
            for (EventRegistration registration : eventRegistrations) {
                if (registration.getEvent().equals(event) && (registration.getEventComponent() == eventElement)) {
                    registration.getEventListener().processEvent(event, view, model, eventElement);
                }
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

        synchronized (eventRegistrations) {
            eventRegistrations.add(eventRegistration);
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
     * for Spring view processing
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
     * synchronous operation
     */
    public static boolean isAsynchronousLifecycle() {
        Config config = ConfigContext.getCurrentContextConfig();
        return config != null && config.getBooleanProperty(
                KRADConstants.ConfigParameters.KRAD_VIEW_LIFECYCLE_ASYNCHRONOUS, false);
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
            Config config = ConfigContext.getCurrentContextConfig();
            if (config == null) {
                return false;
            }

            trace = config.getBooleanProperty(KRADConstants.ConfigParameters.KRAD_VIEW_LIFECYCLE_TRACE, false);
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
        IllegalStateException illegalState = new IllegalStateException(message + "\nPhase: " + ViewLifecycle.getPhase(),
                cause);

        if (ViewLifecycle.isStrict()) {
            throw illegalState;
        } else {
            if(LOG.isTraceEnabled()) {
                LOG.trace(illegalState.getMessage(), illegalState);
            }
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
            ExpressionEvaluatorFactory expressionEvaluatorFactory =
                    KRADServiceLocatorWeb.getExpressionEvaluatorFactory();

            if (expressionEvaluatorFactory == null) {
                return new DefaultExpressionEvaluator();
            } else {
                return expressionEvaluatorFactory.createExpressionEvaluator();
            }
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
     * Gets the set of visited IDs for use during the apply model phase.
     * 
     * @return The set of visited IDs for use during the apply model phase.
     */
    public static Set<String> getVisitedIds() {
        ViewLifecycle active = getActiveLifecycle();

        if (active.visitedIds == null) {
            synchronized (active) {
                if (active.visitedIds == null) {
                    active.visitedIds = Collections.synchronizedSet(new HashSet<String>());
                }
            }
        }

        return active.visitedIds;
    }

    /**
     * Returns the view post metadata instance associated with the view and lifecycle.
     *
     * @return view post metadata instance
     */
    public static ViewPostMetadata getViewPostMetadata() {
        ViewLifecycle active = getActiveLifecycle();

        if (active.model == null) {
            throw new IllegalStateException("Post Metadata is not available");
        }

        return active.viewPostMetadata;
    }
    
    /**
     * When the lifecycle is processing a component refresh, returns a
     * {@link org.kuali.rice.krad.uif.lifecycle.ComponentPostMetadata} instance for the component being
     * refresh.
     *
     * @return post metadata for the component being refreshed
     */
    public static ComponentPostMetadata getRefreshComponentPostMetadata() {
        ViewLifecycle active = getActiveLifecycle();

        if (active == null) {
            throw new IllegalStateException("No lifecycle is active");
        }

        return active.refreshComponentPostMetadata;
    }

    /**
     * Indicates whether the lifecycle is processing a component refresh.
     *
     * @return boolean true if the lifecycle is refreshing a component, false for the full lifecycle
     */
    public static boolean isRefreshLifecycle() {
        ViewLifecycle active = getActiveLifecycle();

        if (active == null) {
            throw new IllegalStateException("No lifecycle is active");
        }

        return (active.refreshComponentPostMetadata != null);
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
        try {
            return processor == null ? null : processor.getActivePhase();
        } catch (IllegalStateException e) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("No lifecycle phase is active on the current processor", e);
            }
            return null;
        }
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
     * {@link #encapsulateLifecycle(View, Object, HttpServletRequest, Runnable)}
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
     * Enumerates potential lifecycle events.
     */
    public static enum LifecycleEvent {

        // Indicates that the finalize phase processing has been completed on the component.
        LIFECYCLE_COMPLETE
    }

    /**
     * Registration of an event.
     */
    protected class EventRegistration implements Serializable {
        private static final long serialVersionUID = -5077429381388641016L;

        // the event to listen for.
        private LifecycleEvent event;

        // the component to notify when the event passes.
        private Component eventComponent;

        // the event listener.
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

}
