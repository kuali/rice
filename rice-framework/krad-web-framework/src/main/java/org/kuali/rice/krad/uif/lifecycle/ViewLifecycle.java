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

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.servlet.ServletContext;
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
import org.kuali.rice.krad.uif.UifConstants.ViewStatus;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.component.PropertyReplacer;
import org.kuali.rice.krad.uif.component.RequestParameter;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.container.LightTable;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.freemarker.FreeMarkerInlineRenderBootstrap;
import org.kuali.rice.krad.uif.modifier.ComponentModifier;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.BooleanMap;
import org.kuali.rice.krad.uif.util.CloneUtils;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ExpressionUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.kuali.rice.krad.uif.util.ScriptUtils;
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
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;

import freemarker.cache.TemplateCache;
import freemarker.core.Environment;
import freemarker.core.ParseException;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.AllHttpScopesHashModel;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

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

    private static Boolean strict;
    private static Boolean renderInLifecycle;

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
        IllegalStateException illegalState = new IllegalStateException(message);

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
     * Set of mutable element identities.
     */
    private final Set<Long> mutableIdentities;

    /**
     * The original view associated with this context.
     */
    private final View originalView;

    /**
     * The model involved in the current view lifecycle.
     */
    private final Object model;
    
    /**
     * The servlet request handling the view lifecycle.
     */
    private final HttpServletRequest request;

    /**
     * The servlet response handling the view lifecycle.
     */
    private final HttpServletResponse response;

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
     * The FreeMarker environment to use for rendering.
     */
    private Environment freeMarkerEnvironment;

    /**
     * Set of imported FreeMarker templates.
     */
    private Set<String> importedFreeMarkerTemplates;

    /**
     * The FreeMarker writer, for capturing rendered output.
     */
    private StringWriter freeMarkerWriter;

    /**
     * The phase currently active on this lifecycle.
     */
    private ViewLifecyclePhase activePhase;

    /**
     * Pending lifecycle phases.
     */
    private final Deque<ViewLifecyclePhase> pendingPhases;

    /**
     * Private constructor, for spawning an initialization context.
     * 
     * @see #getActiveLifecycle() For access to a thread-local instance.
     */
    private ViewLifecycle() {
        this.originalView = null;
        this.model = null;
        this.request = null;
        this.response = null;
        this.helper = null;
        this.eventRegistrations = null;
        this.mutableIdentities = null;
        this.copy = false;
        this.pendingPhases = null;
    }

    /**
     * Private constructor, for spawning a lifecycle context.
     * 
     * @see #getActiveLifecycle() For access to a thread-local instance.
     */
    private ViewLifecycle(View view, Object model,
            HttpServletRequest request, HttpServletResponse response, boolean copy) {
        this.originalView = view;
        this.model = model;
        this.request = request;
        this.response = response;
        this.helper = view.getViewHelperService();
        this.eventRegistrations = new ArrayList<EventRegistration>();
        this.mutableIdentities = new HashSet<Long>();
        this.copy = copy;
        this.pendingPhases = new LinkedList<ViewLifecyclePhase>();
    }

    /**
     * Get the FreeMarker environment for processing the rendering phase, initializing the
     * environment if needed.
     * 
     * @return The FreeMarker environment for processing the rendering phase, initializing the
     *         environment if needed.
     */
    Environment getFreeMarkerEnvironment() {
        if (freeMarkerEnvironment == null) {
            try {
                Map<String, Object> modelAttrs = new HashMap<String, Object>();
                modelAttrs.put(UifConstants.DEFAULT_MODEL_NAME, getModel());
                modelAttrs.put(KRADConstants.USER_SESSION_KEY, GlobalVariables.getUserSession());
                
                request.setAttribute(UifConstants.DEFAULT_MODEL_NAME, getModel());
                request.setAttribute(KRADConstants.USER_SESSION_KEY, GlobalVariables.getUserSession());
                modelAttrs.put(UifParameters.REQUEST, request);

                StringWriter out = new StringWriter();
                Configuration config = FreeMarkerInlineRenderBootstrap.getFreeMarkerConfig();
                Template template = new Template("", new StringReader(""), config);
                
                ServletContext servletContext = FreeMarkerInlineRenderBootstrap.getServletContext();
                ObjectWrapper objectWrapper = FreeMarkerInlineRenderBootstrap.getObjectWrapper();
                ServletContextHashModel servletContextHashModel = FreeMarkerInlineRenderBootstrap.getServletContextHashModel();
                TaglibFactory taglibFactory = FreeMarkerInlineRenderBootstrap.getTaglibFactory();
                
                AllHttpScopesHashModel global =
                        new AllHttpScopesHashModel(objectWrapper, servletContext, request);
                global.put(FreemarkerServlet.KEY_JSP_TAGLIBS, taglibFactory);
                global.put(FreemarkerServlet.KEY_APPLICATION, servletContextHashModel);
                global.put(FreemarkerServlet.KEY_SESSION,
                        new HttpSessionHashModel(request.getSession(), objectWrapper));
                global.put(FreemarkerServlet.KEY_REQUEST,
                        new HttpRequestHashModel(request, response, objectWrapper));
                global.put(FreemarkerServlet.KEY_REQUEST_PARAMETERS,
                        new HttpRequestParametersHashModel(request));
                global.put(AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE,
                        new RequestContext(request, response, servletContext, modelAttrs));

                Map<String, String> properties = CoreApiServiceLocator.getKualiConfigurationService()
                        .getAllProperties();
                global.put(UifParameters.CONFIG_PROPERTIES, properties);
                
                Environment env = template.createProcessingEnvironment(global, out);
                env.importLib("/krad/WEB-INF/ftl/lib/krad.ftl", "krad");
                env.importLib("/krad/WEB-INF/ftl/lib/spring.ftl", "spring");
                freeMarkerEnvironment = env;
                freeMarkerWriter = out;
                importedFreeMarkerTemplates = new HashSet<String>();
            } catch (IOException e) {
                throw new IllegalStateException("Failed to initialize FreeMarker for rendering", e);
            } catch (TemplateException e) {
                throw new IllegalStateException("Failed to initialize FreeMarker for rendering", e);
            }
        }

        return freeMarkerEnvironment;
    }

    /**
     * Clear the output buffer used during rendering, in preparation for rendering another component using the same environment.
     */
    void clearRenderingBuffer() {
        freeMarkerWriter.getBuffer().setLength(0);
    }

    /**
     * Get all output rendered in the FreeMarker environment. 
     */
    String getRenderedOutput() {
        return freeMarkerWriter.toString();
    }

    /**
     * Report a phase as active on this lifecycle thread.
     * 
     * <p>
     * Since each {@link ViewLifecycle} instance is specific to a thread, only one phase may be
     * active at a time.
     * </p>
     * 
     * @param phase The phase to report as activate. Set as null to when the phase has been
     *        completed to indicate that no phase is active.
     */
    void setActivePhase(ViewLifecyclePhase phase) {
        if (activePhase != null && phase != null) {
            throw new IllegalStateException("Another phase is already active on this lifecycle thread " + activePhase);
        }

        activePhase = phase;
    }

    /**
     * Get the active phase on this lifecycle.
     * 
     * @return The active phase on this lifecycle, or null if no phase is currently active.
     */
    public ViewLifecyclePhase getActivePhase() {
        return activePhase;
    }

    /**
     * Push a pending phase, to be executed directly after the completion of the active phase.
     * 
     * @param pendingPhase The pending phase.
     */
    public void pushPendingPhase(ViewLifecyclePhase pendingPhase) {
        pendingPhases.push(pendingPhase);
    }

    /**
     * Offer a pending phase, to be executed after the completion of the active phase and all
     * currently pending phases.
     * 
     * @param pendingPhase The pending phase.
     */
    public void offerPendingPhase(ViewLifecyclePhase pendingPhase) {
        pendingPhases.offer(pendingPhase);
    }

    /**
     * Import a FreeMarker template for rendering into the current environment.
     * 
     * @param template The path to the FreeMarker template.
     */
    public void importFreeMarkerTemplate(String template) {
        if (template == null || !importedFreeMarkerTemplates.add(template)) {
            // No template for component, or already imported in this lifecycle.
            return;
        }

        try {
            Environment env = getFreeMarkerEnvironment();
            String templateNameString = TemplateCache.getFullTemplatePath(env, "", template);
            env.include(env.getTemplateForInclusion(templateNameString, null, true));
        } catch (ParseException e) {
            throw new IllegalStateException("Error parsing imported template " + template, e);
        } catch (TemplateException e) {
            throw new IllegalStateException("Error importing template " + template, e);
        } catch (IOException e) {
            throw new IllegalStateException("Error importing template " + template, e);
        }
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
     * <p>
     * After the lifecycle has completed, this view becomes the resulting view.
     * </p>
     * 
     * @return The view active within this context.
     */
    public View getView() {
        if (this.view == null) {
            throw new IllegalStateException("Context view is not available");
        }

        return this.view;
    }

    /**
     * Get the model related to the view active within this context.
     * 
     * @return The model related to the view active within this context.
     */
    public Object getModel() {
        if (this.model == null) {
            throw new IllegalStateException("Model is not available");
        }

        return this.model;
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
    public void performApplyModel() {
        ProcessLogger.trace("apply-model:" + view.getId());

        // apply default values if they have not been applied yet
        if (!((ViewModel) model).isDefaultsApplied()) {
            applyDefaultValues(view, view, model);
            ((ViewModel) model).setDefaultsApplied(true);
        }

        // get action flag and edit modes from authorizer/presentation controller
        retrieveEditModesAndActionFlags(view, (UifFormBase) model);

        // set view context for conditional expressions
        setViewContext();

        ProcessLogger.trace("apply-comp-model:" + view.getId());

        offerPendingPhase(new ApplyModelComponentPhase(view, model));
        performPendingPhases();

        ProcessLogger.trace("apply-model-end:" + view.getId());
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
    public void performFinalize() {
        // get script for generating growl messages
        String growlScript = buildGrowlScript();
        ((ViewModel) model).setGrowlScript(growlScript);

        offerPendingPhase(new FinalizeComponentPhase(view, model));
        performPendingPhases();

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
    public void performInitialization() {
        helper.performCustomViewInitialization(model);

        view.assignComponentIds(view);

        // increment the id sequence so components added later to the static view components
        // will not conflict with components on the page when navigation happens
        view.setIdSequence(100000);

        offerPendingPhase(new InitializeComponentPhase(view, model));
        performPendingPhases();

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
        if (component == null) {
            return;
        }

        ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();
        View view = viewLifecycle.getView();

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

        // Push sub-lifecycle phases in reverse order.  These will be processed immediately after
        // the active phase ends, starting with the last phase pushed.

        if (UifConstants.ViewPhases.FINALIZE.equals(endPhase)) {
            pushPendingPhase(new FinalizeComponentPhase(component, model, parent));
        }

        if (UifConstants.ViewPhases.FINALIZE.equals(startPhase)) {
            return;
        }

        if (UifConstants.ViewPhases.FINALIZE.equals(endPhase) ||
                UifConstants.ViewPhases.APPLY_MODEL.equals(endPhase)) {
            pushPendingPhase(new ApplyModelComponentPhase(component, model, parent));
        }

        if (UifConstants.ViewPhases.APPLY_MODEL.equals(startPhase)) {
            return;
        }

        pushPendingPhase(new InitializeComponentPhase(component, model));
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
            ViewLifecycle copyLifecycle = new ViewLifecycle(view, null, null, null, true);
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
    public static ViewLifecycleResult encapsulateLifecycle(View view, Object model,
            HttpServletRequest request, HttpServletResponse response, Runnable lifecycleProcess) {
        ViewLifecycle viewLifecycle = TL_VIEW_LIFECYCLE.get();
        if (viewLifecycle != null) {
            throw new IllegalStateException("Another view context already active on this thread");
        }

        try {
            TL_VIEW_LIFECYCLE.set(viewLifecycle = new ViewLifecycle(view, model, request, response, false));
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
    public static View buildView(View view, Object model,
            HttpServletRequest request, HttpServletResponse response,
            final Map<String, String> parameters) {
        View builtView = ViewLifecycle
                .encapsulateLifecycle(view, model, request, response, new Runnable() {
            @Override
            public void run() {
                ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();
                View view = viewLifecycle.getView();
                Object model = viewLifecycle.getModel();

                // populate view from request parameters
                viewLifecycle.populateViewFromRequestParameters(parameters);

                // backup view request parameters on form for recreating lost
                // views (session timeout)
                ((UifFormBase) model).setViewRequestParameters(view
                        .getViewRequestParameters());

                // invoke initialize phase on the views helper service
                if (LOG.isInfoEnabled()) {
                    LOG.info("performing initialize phase for view: " + view.getId());
                }
                viewLifecycle.performInitialization();

                // do indexing                               
                if (LOG.isDebugEnabled()) {
                    LOG.debug("processing indexing for view: " + view.getId());
                }
                view.index();

                // update status on view
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Updating view status to INITIALIZED for view: " + view.getId());
                }
                view.setViewStatus(ViewStatus.INITIALIZED);

                // Apply Model Phase
                if (LOG.isInfoEnabled()) {
                    LOG.info("performing apply model phase for view: " + view.getId());
                }
                viewLifecycle.performApplyModel();

                // do indexing
                if (LOG.isInfoEnabled()) {
                    LOG.info("reindexing after apply model for view: " + view.getId());
                }
                view.index();

                // Finalize Phase
                if (LOG.isInfoEnabled()) {
                    LOG.info("performing finalize phase for view: " + view.getId());
                }
                viewLifecycle.performFinalize();

                // do indexing
                if (LOG.isInfoEnabled()) {
                    LOG.info("processing final indexing for view: " + view.getId());
                }
                view.index();

                // update status on view
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Updating view status to FINAL for view: " + view.getId());
                }
                view.setViewStatus(ViewStatus.FINAL);
            }
        }).getView();

        // Validation of the page's beans
        if (CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsBoolean(
                UifConstants.VALIDATE_VIEWS_ONBUILD)) {
            ValidationController validator = new ValidationController(true, true, true, true, false);
            Log tempLogger = LogFactory.getLog(ViewLifecycle.class);
            validator.validate(builtView, tempLogger, false);
        }

        return builtView;
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
    public static ViewLifecycleResult performComponentLifecycle(View view, Object model,
            HttpServletRequest request, HttpServletResponse response, final Component component,
            final String origId) {
        return encapsulateLifecycle(view, model, request, response, new Runnable() {
            @Override
            public void run() {
                ViewLifecycle viewLifecycle = getActiveLifecycle();
                View view = viewLifecycle.getView();
                Object model = viewLifecycle.getModel();
                
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

                viewLifecycle.offerPendingPhase(new InitializeComponentPhase(newComponent, model));
                viewLifecycle.performPendingPhases();

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

                viewLifecycle.offerPendingPhase(new ApplyModelComponentPhase(newComponent, model, parent));
                viewLifecycle.performPendingPhases();

                // adjust nestedLevel property on some specific collection cases
                if (newComponent instanceof Container) {
                    ComponentUtils.adjustNestedLevelsForTableCollections((Container) newComponent, 0);
                } else if (newComponent instanceof FieldGroup) {
                    ComponentUtils.adjustNestedLevelsForTableCollections(((FieldGroup) newComponent).getGroup(), 0);
                }

                viewLifecycle.offerPendingPhase(new FinalizeComponentPhase(newComponent, model, parent));
                viewLifecycle.performPendingPhases();

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

    /**
     * Execute all pending lifecycle phases.
     */
    protected void performPendingPhases() {
        Queue<FinalizeComponentPhase> finalizeComponentsToNotify = new LinkedList<FinalizeComponentPhase>();
        Queue<ViewLifecyclePhase> initialPhases = new LinkedList<ViewLifecyclePhase>(pendingPhases);

        while (!pendingPhases.isEmpty()) {
            ViewLifecyclePhase phase = pendingPhases.poll();

            if (phase instanceof FinalizeComponentPhase) {
                boolean registered = false;

                for (EventRegistration registration : eventRegistrations) {
                    registered = registered || registration.getEventComponent() == phase.getComponent();
                }

                if (registered) {
                    finalizeComponentsToNotify.offer((FinalizeComponentPhase) phase);
                }
            }

            phase.run();

            pendingPhases.addAll(phase.getSuccessors());
        }

        Iterator<FinalizeComponentPhase> finalizePhaseIterator = finalizeComponentsToNotify.iterator();
        while (finalizePhaseIterator.hasNext()) {
            FinalizeComponentPhase finalizePhase = finalizePhaseIterator.next();

            assert finalizePhase.isComplete();

            // trigger lifecycle complete event for component
            invokeEventListeners(ViewLifecycle.LifecycleEvent.LIFECYCLE_COMPLETE, view,
                    finalizePhase.getModel(), finalizePhase.getComponent());
        }

        Iterator<ViewLifecyclePhase> initialPhaseIterator = initialPhases.iterator();
        while (initialPhaseIterator.hasNext()) {
            ViewLifecyclePhase top = initialPhaseIterator.next();

            assert top.isComplete();
            view.getViewIndex().indexComponent(top.getComponent());
        }
    }

}
