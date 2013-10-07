/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.krad.uif.lifecycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MethodInvoker;

/**
 * Lifecycle phase processing task for applying the model to a component.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FinalizeComponentPhase extends AbstractViewLifecyclePhase {
    
    private final Logger LOG = LoggerFactory.getLogger(FinalizeComponentPhase.class);

    private final Component parent;

    /**
     * Create a new lifecycle phase processing task for finalizing a component.
     * 
     * @param component The component instance the model should be applied to
     * @param model Top level object containing the data
     * @param parentPhase The finalize phase processed on the parent component.
     */
    private FinalizeComponentPhase(Component component, Object model, Component parent,
            FinalizeComponentPhase parentPhase) {
        super(component, model, parentPhase == null ?
                Collections.<ViewLifecyclePhase> emptyList() :
                Collections.<ViewLifecyclePhase> singletonList(parentPhase));
        this.parent = parent;
    }

    /**
     * Create a new lifecycle phase processing task for finalizing a component.
     * 
     * @param component the component instance that should be updated
     * @param model top level object containing the data
     */
    public FinalizeComponentPhase(Component component, Object model) {
        this(component, model, null);
    }

    /**
     * Create a new lifecycle phase processing task for finalizing a component.
     * 
     * @param component the component instance that should be updated
     * @param model top level object containing the data
     * @param parent parent component for the component being finalized
     */
    public FinalizeComponentPhase(Component component, Object model, Component parent) {
        this(component, model, parent, null);
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getViewPhase()
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.FINALIZE;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getStartViewStatus()
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.MODEL_APPLIED;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getEndViewStatus()
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.FINAL;
    }

    /**
     * Update state of the component and perform final preparation for rendering.
     * 
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#performLifecyclePhase()
     */
    @Override
    protected void performLifecyclePhase() {
        Component component = getComponent();
        Object model = getModel();

        if (component == null) {
            return;
        }

        ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();
        View view = viewLifecycle.getView();
        ViewHelperService helper = viewLifecycle.getHelper();

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
        viewLifecycle.runComponentModifiers(component, model, UifConstants.ViewPhases.FINALIZE);

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

    }

    /**
     * Define all nested lifecycle components, and component prototypes, as successors.
     * 
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#initializeSuccessors(java.util.List)
     */
    @Override
    protected void initializeSuccessors(List<ViewLifecyclePhase> successors) {
        Component component = getComponent();
        Object model = getModel();

        // initialize nested components
        for (Component nestedComponent : component.getComponentsForLifecycle()) {
            if (nestedComponent != null) {
                successors.add(new FinalizeComponentPhase(nestedComponent, model, component, this));
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

}
