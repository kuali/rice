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

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.ClientSideState;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.component.PropertyReplacer;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.field.ActionField;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.modifier.ComponentModifier;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.CloneUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewAuthorizer;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.view.ViewPresentationController;
import org.kuali.rice.krad.uif.widget.Widget;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Lifecycle phase processing task for applying the model to a component.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ApplyModelComponentPhase extends AbstractViewLifecyclePhase {

    private final Component parent;
    private final Map<String, Integer> visitedIds;
    private final Map<String, Object> commonContext;

    /**
     * Create a new lifecycle phase processing task for applying the model to a component.
     * 
     * @param component The component instance the model should be applied to
     * @param model Top level object containing the data
     * @param parent The parent component.
     * @param visitedIds Tracks components ids that have been seen for adjusting duplicates.
     * @param parentPhase The apply model phase processed on the parent component.
     */
    private ApplyModelComponentPhase(Component component, Object model, Component parent,
            Map<String, Integer> visitedIds, ApplyModelComponentPhase parentPhase) {
        super(component, model, parentPhase == null ?
                Collections.<ViewLifecyclePhase> emptyList() :
                Collections.<ViewLifecyclePhase> singletonList(parentPhase));
        this.parent = parent;
        this.visitedIds = visitedIds;
        
        Map<String, Object> commonContext = new HashMap<String, Object>();

        View view = ViewLifecycle.getActiveLifecycle().getView();
        Map<String, Object> viewContext = view.getContext();
        if (viewContext != null) {
            commonContext.putAll(view.getContext());
        }

        commonContext.put(UifConstants.ContextVariableNames.THEME_IMAGES, view.getTheme().getImageDirectory());
        commonContext.put(UifConstants.ContextVariableNames.COMPONENT, getComponent());

        this.commonContext = Collections.unmodifiableMap(commonContext);
    }

    /**
     * Create a new lifecycle phase processing task for applying the model to a component.
     * 
     * @param component The component.
     * @param model The model
     */
    public ApplyModelComponentPhase(Component component, Object model) {
        this(component, model, null);
    }

    /**
     * Create a new lifecycle phase processing task for applying the model to a component.
     * 
     * @param component The component.
     * @param model The model
     * @param parent The parent component.
     */
    public ApplyModelComponentPhase(Component component, Object model, Component parent) {
        this(component, model, parent, Collections.synchronizedMap(new HashMap<String, Integer>()), null);
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getViewPhase()
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.APPLY_MODEL;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getStartViewStatus()
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.INITIALIZED;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getEndViewStatus()
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.MODEL_APPLIED;
    }

    /**
     * Gets global objects for the context map and pushes them to the context for the component
     * 
     * @param view view instance for component
     * @param component component instance to push context to
     */
    public Map<String, Object> getCommonContext() {
        return commonContext;
    }

    /**
     * Checks against the visited ids to see if the id is duplicate, if so it is adjusted to make an
     * unique id by appending an unique sequence number
     * 
     * @param id id to adjust if necessary
     * @param visitedIds tracks components ids that have been seen for adjusting duplicates
     * @return original or adjusted id
     */
    public String adjustIdIfNecessary(String id) {
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
     * Applies the model data to a component of the View instance
     * 
     * <p>
     * The component is invoked to to apply the model data. Here the component can generate any
     * additional fields needed or alter the configured fields. After the component is invoked a
     * hook for custom helper service processing is invoked. Finally the method is recursively
     * called for all the component children
     * </p>
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

        if (parent != null) {
            component.pushObjectToContext(UifConstants.ContextVariableNames.PARENT, parent);
        }

        ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();
        View view = viewLifecycle.getView();
        ViewHelperService helper = viewLifecycle.getHelper();
        // ProcessLogger.ntrace("comp-model:", ":" + component.getClass().getSimpleName(), 500);
        // ProcessLogger.countBegin("comp-model");

        Map<String, Object> commonContext = getCommonContext();

        // set context on component for evaluating expressions
        component.pushAllToContext(commonContext);

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
                layoutManager.pushAllToContext(commonContext);
                layoutManager.pushObjectToContext(UifConstants.ContextVariableNames.PARENT, component);
                layoutManager.pushObjectToContext(UifConstants.ContextVariableNames.MANAGER, layoutManager);

                expressionEvaluator.evaluateExpressionsOnConfigurable(view, layoutManager,
                        layoutManager.getContext());

                layoutManager.setId(adjustIdIfNecessary(layoutManager.getId()));
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
        viewLifecycle.runComponentModifiers(component, model, UifConstants.ViewPhases.APPLY_MODEL);

        // ProcessLogger.countEnd("comp-model", view.getId() + " " + component.getClass() + " " + component.getId());

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
                successors.add(new ApplyModelComponentPhase(nestedComponent, model, component, visitedIds, this));
            }
        }
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

}
