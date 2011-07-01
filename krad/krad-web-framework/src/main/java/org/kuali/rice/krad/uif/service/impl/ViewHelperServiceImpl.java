/*
 * Copyright 2007 The Kuali Foundation Licensed under the Educational Community
 * License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.krad.uif.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.inquiry.Inquirable;
import org.kuali.rice.krad.lookup.valuefinder.ValueFinder;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.authorization.Authorizer;
import org.kuali.rice.krad.uif.authorization.PresentationController;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.core.BindingInfo;
import org.kuali.rice.krad.uif.core.Component;
import org.kuali.rice.krad.uif.core.DataBinding;
import org.kuali.rice.krad.uif.core.PropertyReplacer;
import org.kuali.rice.krad.uif.core.RequestParameter;
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.modifier.ComponentModifier;
import org.kuali.rice.krad.uif.service.ExpressionEvaluatorService;
import org.kuali.rice.krad.uif.service.ViewDictionaryService;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.BooleanMap;
import org.kuali.rice.krad.uif.util.CloneUtils;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.uif.widget.Inquiry;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.web.spring.form.UifFormBase;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * Default Implementation of <code>ViewHelperService</code>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewHelperServiceImpl implements ViewHelperService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ViewHelperServiceImpl.class);

    private transient DataDictionaryService dataDictionaryService;
    private transient ExpressionEvaluatorService expressionEvaluatorService;
    private transient ViewDictionaryService viewDictionaryService;

    /**
     * Uses reflection to find all fields defined on the <code>View</code>
     * instance that have the <code>RequestParameter</code> annotation (which
     * indicates the field may be populated by the request). For each field
     * found, if there is a corresponding key/value pair in the request
     * parameters, the value is used to populate the field. In addition, any
     * conditional properties of <code>PropertyReplacers</code> configured for
     * the field are cleared so that the request parameter value does not get
     * overridden by the dictionary conditional logic
     * 
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#populateViewFromRequestParameters(org.kuali.rice.krad.uif.container.View,
     *      java.util.Map)
     */
    @Override
    public void populateViewFromRequestParameters(View view, Map<String, String> parameters) {
        // build set of view properties that can be populated
        Set<String> fieldNamesToPopulate = new HashSet<String>();

        Field[] fields = CloneUtils.getFields(view.getClass(), true);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            RequestParameter requestParameter = field.getAnnotation(RequestParameter.class);
            if (requestParameter != null) {
                // use specified parameter name if given, else use field name
                if (StringUtils.isNotBlank(requestParameter.parameterName())) {
                    fieldNamesToPopulate.add(requestParameter.parameterName());
                } else {
                    fieldNamesToPopulate.add(field.getName());
                }
            }
        }

        // build Map of property replacers by property name
        Map<String, Set<PropertyReplacer>> viewPropertyReplacers = new HashMap<String, Set<PropertyReplacer>>();
        for (PropertyReplacer replacer : view.getPropertyReplacers()) {
            Set<PropertyReplacer> propertyReplacers = new HashSet<PropertyReplacer>();
            if (viewPropertyReplacers.containsKey(replacer.getPropertyName())) {
                propertyReplacers = viewPropertyReplacers.get(replacer.getPropertyName());
            }
            propertyReplacers.add(replacer);

            viewPropertyReplacers.put(replacer.getPropertyName(), propertyReplacers);
        }

        // build map of view parameter key/values and populate view fields
        Map<String, String> viewRequestParameters = new HashMap<String, String>();
        for (String fieldToPopulate : fieldNamesToPopulate) {
            if (parameters.containsKey(fieldToPopulate)) {
                String fieldValue = parameters.get(fieldToPopulate);

                if (StringUtils.isNotBlank(fieldValue)) {
                    viewRequestParameters.put(fieldToPopulate, fieldValue);
                    ObjectPropertyUtils.setPropertyValue(view, fieldToPopulate, fieldValue);

                    // remove any conditional configuration so value is not
                    // overridden later during the apply model phase
                    String conditionalProperty = StringUtils.substring(fieldToPopulate, 0, 1).toLowerCase()
                            + StringUtils.substring(fieldToPopulate, 1, fieldToPopulate.length());
                    conditionalProperty = UifConstants.EL_CONDITIONAL_PROPERTY_PREFIX + conditionalProperty;
                    ObjectPropertyUtils.setPropertyValue(view, conditionalProperty, fieldValue, true);

                    if (viewPropertyReplacers.containsKey(fieldToPopulate)) {
                        Set<PropertyReplacer> propertyReplacers = viewPropertyReplacers.get(fieldToPopulate);
                        for (PropertyReplacer replacer : propertyReplacers) {
                            view.getPropertyReplacers().remove(replacer);
                        }
                    }
                }
            }
        }

        view.setViewRequestParameters(viewRequestParameters);
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#performInitialization(org.kuali.rice.krad.uif.container.View,
     *      java.util.Map)
     */
    @Override
    public void performInitialization(View view) {
        // process component ids for duplicates
        ComponentUtils.processIds(view, new HashMap<String, Integer>());

        performComponentInitialization(view, view);
    }
    
    /**
     * Performs the complete component lifecycle on the component passed in, in this order:
     * performComponentInitialization, performComponentApplyModel, and performComponentFinalize.
     * 
     * @see {@link #performComponentInitialization(View, Component)}
     * @see {@link #performComponentApplyModel(View, Component, Object)}
     * @see {@link #performComponentFinalize(View, Component, Object, Component)}
     * @param form
     * @param component
     */
    public void performComponentLifecycle(UifFormBase form, Component component, String origId){
        Component origComponent = form.getView().getViewIndex().getComponentById(origId);
        
        Component parent = (Component) origComponent.getContext().get(UifConstants.ContextVariableNames.PARENT);
        component.getContext().putAll(origComponent.getContext());
        
        performComponentInitialization(form.getView(), component);
        performComponentApplyModel(form.getView(), component, form);
        performComponentFinalize(form.getView(), component, form, parent);
        
        component.setId(origId);
    }

    /**
     * Performs initialization of a component by these steps:
     * 
     * <ul>
     * <li>For <code>AttributeField</code> instances, set defaults from the data
     * dictionary.</li>
     * <li>Invoke the initialize method on the component. Here the component can
     * setup defaults and do other initialization that is specific to that
     * component.</li>
     * <li>Invoke any configured <code>ComponentModifier</code> instances for
     * the component.</li>
     * <li>Call the component to get the List of components that are nested
     * within and recursively call this method to initialize those components.</li>
     * <li>Call custom initialize hook for service overrides</li>
     * </ul>
     * 
     * <p>
     * Note the order various initialize points are called, this can sometimes
     * be an important factor to consider when initializing a component
     * </p>
     * 
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#performComponentInitialization(org.kuali.rice.krad.uif.container.View,
     *      org.kuali.rice.krad.uif.core.Component)
     */
    public void performComponentInitialization(View view, Component component) {
        if (component == null) {
            return;
        }

        LOG.debug("Initializing component: " + component.getId() + " with type: " + component.getClass());

        // invoke component to initialize itself after properties have been set
        component.performInitialization(view);

        // for attribute fields, set defaults from dictionary entry
        if (component instanceof AttributeField) {
            initializeAttributeFieldFromDataDictionary(view, (AttributeField) component);
        }

        // for collection groups set defaults from dictionary entry
        if (component instanceof CollectionGroup) {
            // TODO: initialize from dictionary
        }

        // invoke component initializers setup to run in the initialize phase
        runComponentModifiers(view, component, null, UifConstants.ViewPhases.INITIALIZE);

        // initialize nested components
        for (Component nestedComponent : component.getNestedComponents()) {
            performComponentInitialization(view, nestedComponent);
        }

        // initialize property replacements (if components)
        for (PropertyReplacer replacer : component.getPropertyReplacers()) {
            if (Component.class.isAssignableFrom(replacer.getReplacement().getClass())) {
                performComponentInitialization(view, (Component) replacer.getReplacement());
            }
        }

        // invoke initialize service hook
        performCustomInitialization(view, component);
    }

    /**
     * Sets properties of the <code>AttributeField</code> (if blank) to the
     * corresponding attribute entry in the data dictionary
     * 
     * @param view
     *            - view instance containing the field
     * @param field
     *            - field instance to initialize
     */
    protected void initializeAttributeFieldFromDataDictionary(View view, AttributeField field) {
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
            attributeDefinition = getDataDictionaryService().getAttributeDefinition(dictionaryObjectEntry,
                    dictionaryAttributeName);
        }

        // if definition not found, recurse through path
        if (attributeDefinition == null) {
            String propertyPath = field.getBindingInfo().getBindingPath();
            if (StringUtils.isNotBlank(field.getBindingInfo().getCollectionPath())) {
                propertyPath = field.getBindingInfo().getCollectionPath();
                if (StringUtils.isNotBlank(field.getBindingInfo().getBindByNamePrefix())) {
                    propertyPath += "." + field.getBindingInfo().getBindByNamePrefix();
                }
                propertyPath += "." + field.getBindingInfo().getBindingName();
            }
            
            attributeDefinition = findNestedDictionaryAttribute(view, field, null, propertyPath);
        }

        // if a definition was found, initialize field from definition
        if (attributeDefinition != null) {
            field.copyFromAttributeDefinition(attributeDefinition);
        }

        if (field.getControl() == null) {
            field.setControl(ComponentFactory.getTextControl());
        }
    }
    
    /**
     * Recursively drills down the property path (if nested) to find an
     * AttributeDefinition, the first attribute definition found will be
     * returned
     * 
     * <p>
     * e.g. suppose parentPath is 'document' and propertyPath is
     * 'account.subAccount.name', first the property type for document will be
     * retrieved using the view metadata and used as the dictionary entry, with
     * the propertyPath as the dictionary attribute, if an attribute definition
     * exists it will be returned. Else, the first part of the property path is
     * added to the parent, making the parentPath 'document.account' and the
     * propertyPath 'subAccount.name', the method is then called again to
     * perform the process with those parameters. The recursion continues until
     * an attribute field is found, or the propertyPath is no longer nested
     * </p>
     * 
     * @param view
     *            - view instance containing the field
     * @param field
     *            - field we are attempting to find a supporting attribute
     *            definition for
     * @param parentPath
     *            - parent path to use for getting the dictionary entry
     * @param propertyPath
     *            - path of the property relative to the parent, to use as
     *            dictionary attribute and to drill down on
     * @return AttributeDefinition if found, or Null
     */
    protected AttributeDefinition findNestedDictionaryAttribute(View view, AttributeField field, String parentPath,
            String propertyPath) {
        AttributeDefinition attributeDefinition = null;

        // attempt to find definition for parent and property
        String dictionaryAttributeName = propertyPath;
        String dictionaryObjectEntry = null;
        
        if (field.getBindingInfo().isBindToMap()) {
            parentPath = "";
            if (!field.getBindingInfo().isBindToForm() && StringUtils.isNotBlank(field.getBindingInfo().getBindingObjectPath())) {
                parentPath = field.getBindingInfo().getBindingObjectPath();
            }
            if (StringUtils.isNotBlank(field.getBindingInfo().getBindByNamePrefix())) {
                if (StringUtils.isNotBlank(parentPath)) {
                    parentPath += "." + field.getBindingInfo().getBindByNamePrefix();
                }
                else {
                    parentPath = field.getBindingInfo().getBindByNamePrefix();
                }
            }
            
            dictionaryAttributeName = field.getBindingInfo().getBindingName();
        }

        if (StringUtils.isNotBlank(parentPath)) {
            Class<?> dictionaryModelClass = ViewModelUtils.getPropertyType(view, parentPath);
            if (dictionaryModelClass != null) {
                dictionaryObjectEntry = dictionaryModelClass.getName();

                attributeDefinition = getDataDictionaryService().getAttributeDefinition(dictionaryObjectEntry,
                        dictionaryAttributeName);
            }
        }

        // if definition not found and property is still nested, recurse down
        // one level
        if ((attributeDefinition == null) && StringUtils.contains(propertyPath, ".")) {
            String nextParentPath = StringUtils.substringBefore(propertyPath, ".");
            if (StringUtils.isNotBlank(parentPath)) {
                nextParentPath = parentPath + "." + nextParentPath;
            }
            String nextPropertyPath = StringUtils.substringAfter(propertyPath, ".");

            return findNestedDictionaryAttribute(view, field, nextParentPath, nextPropertyPath);
        }

        // if a definition was found, update the fields dictionary properties
        if (attributeDefinition != null) {
            field.setDictionaryAttributeName(dictionaryAttributeName);
            field.setDictionaryObjectEntry(dictionaryObjectEntry);
        }

        return attributeDefinition;
    }

    /**
     * Determines the dictionary class that is associated with the given
     * <code>AttributeField</code>
     * 
     * @param view
     *            - view instance for field
     * @param field
     *            - field instance to determine dictionary class for
     * @return Class<?> dictionary class or null if not found
     */
    protected Class<?> getDictionaryModelClass(View view, AttributeField field) {
        return ViewModelUtils.getParentObjectClassForMetadata(view, field);
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#performApplyModel(org.kuali.rice.krad.uif.container.View,
     *      java.lang.Object)
     */
    @Override
    public void performApplyModel(View view, Object model) {
        // get action flag and edit modes from authorizer/presentation
        // controller
        invokeAuthorizerPresentationController(view, (UifFormBase) model);

        // set view context for conditional expressions
        setViewContext(view, model);

        performComponentApplyModel(view, view, model);
    }

    /**
     * Invokes the configured <code>PresentationController</code> and
     * </code>Authorizer</code> for the view to get the exported action flags
     * and edit modes that can be used in conditional logic
     * 
     * @param view
     *            - view instance that is being built and
     *            presentation/authorizer pulled for
     * @param model
     *            - Object that contains the model data
     */
    protected void invokeAuthorizerPresentationController(View view, UifFormBase model) {
        PresentationController presentationController = ObjectUtils.newInstance(view.getPresentationControllerClass());
        Authorizer authorizer = ObjectUtils.newInstance(view.getAuthorizerClass());

        Person user = GlobalVariables.getUserSession().getPerson();

        Set<String> actionFlags = presentationController.getActionFlags(model);
        actionFlags = authorizer.getActionFlags(model, user, actionFlags);
        
        view.setActionFlags(new BooleanMap(actionFlags));

        Set<String> editModes = presentationController.getEditModes(model);
        editModes = authorizer.getEditModes(model, user, editModes);
        
        view.setEditModes(new BooleanMap(editModes));
    }

    /**
     * Sets up the view context which will be available to other components
     * through their context for conditional logic evaluation
     * 
     * @param view
     *            - view instance to set context for
     * @param model
     *            - object containing the view data
     */
    protected void setViewContext(View view, Object model) {
        view.getContext().putAll(getPreModelContext(view));

        // evaluate view expressions for further context
        for (Entry<String, String> variableExpression : view.getExpressionVariables().entrySet()) {
            String variableName = variableExpression.getKey();
            Object value = getExpressionEvaluatorService().evaluateExpression(model, view.getContext(),
                    variableExpression.getValue());
            view.pushObjectToContext(variableName, value);
        }
    }
    
    /**
     * Returns the general context that is available before the apply model
     * phase (during the initialize phase)
     * 
     * @param view
     *            - view instance for context
     * @return Map<String, Object> context map
     */
    protected Map<String, Object> getPreModelContext(View view) {
        Map<String, Object> context = new HashMap<String, Object>();

        context.put(UifConstants.ContextVariableNames.VIEW, view);

        Properties properties = KRADServiceLocator.getKualiConfigurationService().getAllProperties();
        context.put(UifConstants.ContextVariableNames.CONFIG_PROPERTIES, properties);
        context.put(UifConstants.ContextVariableNames.CONSTANTS, KRADConstants.class);

        return context;
    }

    /**
     * Applies the model data to a component of the View instance
     * 
     * <p>
     * The component is invoked to to apply the model data. Here the component
     * can generate any additional fields needed or alter the configured fields.
     * After the component is invoked a hook for custom helper service
     * processing is invoked. Finally the method is recursively called for all
     * the component children
     * </p>
     * 
     * @param view
     *            - view instance the component belongs to
     * @param component
     *            - the component instance the model should be applied to
     * @param model
     *            - top level object containing the data
     */
    protected void performComponentApplyModel(View view, Component component, Object model) {
        if (component == null) {
            return;
        }

        // evaluate expressions on properties
        component.getContext().putAll(getCommonContext(view, component));
        getExpressionEvaluatorService().evaluateObjectProperties(component, model, component.getContext());

        if (component instanceof Container) {
            LayoutManager layoutManager = ((Container) component).getLayoutManager();

            if (layoutManager != null) {
                layoutManager.getContext().putAll(getCommonContext(view, component));
                layoutManager.pushObjectToContext(UifConstants.ContextVariableNames.PARENT, component);
                layoutManager.pushObjectToContext(UifConstants.ContextVariableNames.MANAGER, layoutManager);
                getExpressionEvaluatorService().evaluateObjectProperties(layoutManager, model,
                        layoutManager.getContext());
            }
        }

        if (component instanceof DataBinding) {
            BindingInfo bindingInfo = ((DataBinding) component).getBindingInfo();
            getExpressionEvaluatorService().evaluateObjectProperties(bindingInfo, model, component.getContext());
        }

        // invoke component to perform its conditional logic
        Component parent = (Component) component.getContext().get(UifConstants.ContextVariableNames.PARENT);
        component.performApplyModel(view, model, parent);

        // invoke service override hook
        performCustomApplyModel(view, component, model);

        // invoke component modifiers configured to run in the apply model phase
        runComponentModifiers(view, component, model, UifConstants.ViewPhases.APPLY_MODEL);

        // get children and recursively perform conditional logic
        for (Component nestedComponent : component.getNestedComponents()) {
            if (nestedComponent != null) {
                nestedComponent.pushObjectToContext(UifConstants.ContextVariableNames.PARENT, component);
            }

            performComponentApplyModel(view, nestedComponent, model);
        }
    }

    /**
     * Runs any configured <code>ComponentModifiers</code> for the given
     * component that match the given run phase and who run condition evaluation
     * succeeds
     * 
     * @param view
     *            - view instance for context
     * @param component
     *            - component instance whose modifiers should be run
     * @param model
     *            - model object for context
     * @param runPhase
     *            - current phase to match on
     */
    protected void runComponentModifiers(View view, Component component, Object model, String runPhase) {
        for (ComponentModifier modifier : component.getComponentModifiers()) {
            // check run phase matches
            if (StringUtils.equals(modifier.getRunPhase(), runPhase)) {
                // check condition (if set) evaluates to true
                boolean runModifier = true;
                if (StringUtils.isNotBlank(modifier.getRunCondition())) {
                    Map<String, Object> context = new HashMap<String, Object>();
                    context.put(UifConstants.ContextVariableNames.COMPONENT, component);
                    context.put(UifConstants.ContextVariableNames.VIEW, view);

                    String conditionEvaluation = getExpressionEvaluatorService().evaluateExpressionTemplate(model,
                            context, modifier.getRunCondition());
                    runModifier = Boolean.parseBoolean(conditionEvaluation);
                }

                if (runModifier) {
                    if (StringUtils.equals(runPhase, UifConstants.ViewPhases.APPLY_MODEL)
                            || StringUtils.equals(runPhase, UifConstants.ViewPhases.FINALIZE)) {
                        modifier.performModification(view, model, component);
                    } else {
                        modifier.performModification(view, component);
                    }
                }
            }
        }
    }

    /**
     * Gets global objects for the context map and pushes them to the context
     * for the component
     * 
     * @param view
     *            - view instance for component
     * @param component
     *            - component instance to push context to
     */
    protected Map<String, Object> getCommonContext(View view, Component component) {
        Map<String, Object> context = new HashMap<String, Object>();

        context.putAll(view.getContext());
        context.put(UifConstants.ContextVariableNames.COMPONENT, component);

        return context;
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#performFinalize(org.kuali.rice.krad.uif.container.View,
     *      java.lang.Object)
     */
    @Override
    public void performFinalize(View view, Object model) {
        performComponentFinalize(view, view, model, null);
    }

    /**
     * Update state of the given component and does final preparation for
     * rendering
     * 
     * @param view
     *            - view instance the component belongs to
     * @param component
     *            - the component instance that should be updated
     * @param model
     *            - top level object containing the data
     * @param parent
     *            - Parent component for the component being finalized
     */
    protected void performComponentFinalize(View view, Component component, Object model, Component parent) {
        if (component == null) {
            return;
        }
        
        // invoke configured method finalizers
        invokeMethodFinalizer(view, component);
        
        // invoke component to update its state
        component.performFinalize(view, model, parent);

        // invoke service override hook
        performCustomFinalize(view, component, model, parent);

        // invoke component initializers setup to run in the finalize phase
        runComponentModifiers(view, component, model, UifConstants.ViewPhases.FINALIZE);

        // get components children and recursively update state
        for (Component nestedComponent : component.getNestedComponents()) {
            performComponentFinalize(view, nestedComponent, model, component);
        }
    }
    
    /**
     * Invokes the finalize method for the component (if configured) and sets
     * the render output for the component to the returned method string (if
     * method is not a void type)
     * 
     * @param view
     *            - view instance that contains the component
     * @param component
     *            - component to run finalize method for
     */
    protected void invokeMethodFinalizer(View view, Component component) {
        String finalizeMethodToCall = component.getFinalizeMethodToCall();
        MethodInvoker finalizeMethodInvoker = component.getFinalizeMethodInvoker();

        if (StringUtils.isBlank(finalizeMethodToCall) && (finalizeMethodInvoker == null)) {
            return;
        }

        if (finalizeMethodInvoker == null) {
            finalizeMethodInvoker = new MethodInvoker();
        }

        // if method not set on invoker, use renderingMethodToCall, note staticMethod could be set(don't know since
        // there is not a getter), if so it will override the target method in prepare
        if (StringUtils.isBlank(finalizeMethodInvoker.getTargetMethod())) {
            finalizeMethodInvoker.setTargetMethod(finalizeMethodToCall);
        }

        // if target class or object not set, use view helper service
        if ((finalizeMethodInvoker.getTargetClass() == null) && (finalizeMethodInvoker.getTargetObject() == null)) {
            finalizeMethodInvoker.setTargetObject(view.getViewHelperService());
        }

        // add the component instance as an argument
        Object[] arguments = new Object[1];
        arguments[0] = component;
        finalizeMethodInvoker.setArguments(arguments);

        // invoke method and get render output
        try {
            LOG.debug("Invoking render method: " + finalizeMethodInvoker.getTargetMethod() + " for component: "
                    + component.getId());
            finalizeMethodInvoker.prepare();

            Class<?> methodReturnType = finalizeMethodInvoker.getPreparedMethod().getReturnType();
            if (StringUtils.equals("void", methodReturnType.getName())) {
                finalizeMethodInvoker.invoke();
            } else {
                String renderOutput = (String) finalizeMethodInvoker.invoke();

                component.setSelfRendered(true);
                component.setRenderOutput(renderOutput);
            }
        } catch (Exception e) {
            LOG.error("Error invoking rendering method for component: " + component.getId(), e);
            throw new RuntimeException("Error invoking rendering method for component: " + component.getId(), e);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#processCollectionAddLine(org.kuali.rice.krad.uif.container.View,
     *      java.lang.Object, java.lang.String)
     */
    @Override
    public void processCollectionAddLine(View view, Object model, String collectionPath) {
        // get the collection group from the view
        CollectionGroup collectionGroup = view.getViewIndex().getCollectionGroupByPath(collectionPath);
        if (collectionGroup == null) {
            logAndThrowRuntime("Unable to get collection group component for path: " + collectionPath);
        }

        // get the collection instance for adding the new line
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model, collectionPath);
        if (collection == null) {
            logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
        }

        // now get the new line we need to add
        String addLinePath = collectionGroup.getAddLineBindingInfo().getBindingPath();
        Object addLine = ObjectPropertyUtils.getPropertyValue(model, addLinePath);
        if (addLine == null) {
            logAndThrowRuntime("Add line instance not found for path: " + addLinePath);
        }

        processBeforeAddLine(view, collectionGroup, model, addLine);

        // validate the line to make sure it is ok to add
        boolean isValidLine = performAddLineValidation(view, collectionGroup, model, addLine);
        if (isValidLine) {
            // TODO: should check to see if there is an add line method on the
            // collection parent and if so call that instead of just adding to
            // the collection (so that sequence can be set)
            collection.add(addLine);

            // make a new instance for the add line
            collectionGroup.initializeNewCollectionLine(view, model, collectionGroup, true);
        }

        processAfterAddLine(view, collectionGroup, model, addLine);
    }

    /**
     * Performs validation on the new collection line before it is added to the
     * corresponding collection
     * 
     * @param view
     *            - view instance that the action was taken on
     * @param collectionGroup
     *            - collection group component for the collection
     * @param addLine
     *            - new line instance to validate
     * @param model
     *            - object instance that contain's the views data
     * @return boolean true if the line is valid and it should be added to the
     *         collection, false if it was not valid and should not be added to
     *         the collection
     */
    protected boolean performAddLineValidation(View view, CollectionGroup collectionGroup, Object model, Object addLine) {
        boolean isValid = true;

        // TODO: this should invoke rules, sublclasses like the document view
        // should create the document add line event

        return isValid;
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#processCollectionDeleteLine(org.kuali.rice.krad.uif.container.View,
     *      java.lang.Object, java.lang.String, int)
     */
    public void processCollectionDeleteLine(View view, Object model, String collectionPath, int lineIndex) {
        // get the collection group from the view
        CollectionGroup collectionGroup = view.getViewIndex().getCollectionGroupByPath(collectionPath);
        if (collectionGroup == null) {
            logAndThrowRuntime("Unable to get collection group component for path: " + collectionPath);
        }

        // get the collection instance for adding the new line
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model, collectionPath);
        if (collection == null) {
            logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
        }

        // TODO: look into other ways of identifying a line so we can deal with
        // unordered collections
        if (collection instanceof List) {
            Object deleteLine = ((List<Object>) collection).get(lineIndex);

            // validate the delete action is allowed for this line
            boolean isValid = performDeleteLineValidation(view, collectionGroup, deleteLine);
            if (isValid) {
                ((List<Object>) collection).remove(lineIndex);
            }
        } else {
            logAndThrowRuntime("Only List collection implementations are supported for the delete by index method");
        }
    }

    /**
     * Performs validation on the collection line before it is removed from the
     * corresponding collection
     * 
     * @param view
     *            - view instance that the action was taken on
     * @param collectionGroup
     *            - collection group component for the collection
     * @param deleteLine
     *            - line that will be removed
     * @return boolean true if the action is allowed and the line should be
     *         removed, false if the line should not be removed
     */
    protected boolean performDeleteLineValidation(View view, CollectionGroup collectionGroup, Object deleteLine) {
        boolean isValid = true;

        // TODO: this should invoke rules, sublclasses like the document view
        // should create the document delete line event

        return isValid;
    }

    /**
     * Finds the <code>Inquirable</code> configured for the given data object
     * class and delegates to it for building the inquiry URL
     * 
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#buildInquiryLink(java.lang.Object,
     *      java.lang.String, org.kuali.rice.krad.uif.widget.Inquiry)
     */
    public void buildInquiryLink(Object dataObject, String propertyName, Inquiry inquiry) {
        Inquirable inquirable = getViewDictionaryService().getInquirable(dataObject.getClass(), inquiry.getViewName());
        if (inquirable != null) {
            inquirable.buildInquirableLink(dataObject, propertyName, inquiry);
        } else {
            // inquirable not found, no inquiry link can be set
            inquiry.setRender(false);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#applyDefaultValues(org.kuali.rice.krad.uif.container.View,
     *      org.kuali.rice.krad.web.spring.form.UifFormBase)
     */
    public void applyDefaultValues(View view, UifFormBase model) {
        // retrieve all attribute fields for the view and apply their configured
        // default value to the model
        Map<String, AttributeField> attributeFields = view.getViewIndex().getAttributeFieldIndex();
        for (Entry<String, AttributeField> attributeFieldEntry : attributeFields.entrySet()) {
            String bindingPath = attributeFieldEntry.getKey();
            AttributeField attributeField = attributeFieldEntry.getValue();

            populateDefaultValueForField(view, model, attributeField, bindingPath);
        }
        
        // update form indicator
        model.setDefaultsApplied(true);
    }
    
    /**
     * @see org.kuali.rice.krad.uif.service.ViewHelperService#applyDefaultValuesForCollectionLine(org.kuali.rice.krad.uif.container.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.container.CollectionGroup,
     *      java.lang.Object)
     */
    public void applyDefaultValuesForCollectionLine(View view, Object model, CollectionGroup collectionGroup,
            Object line) {
        // retrieve all attribute fields for the collection line
        List<AttributeField> attributeFields = ComponentUtils.getComponentsOfTypeDeep(collectionGroup.getAddLineFields(),
                AttributeField.class);
        for (AttributeField attributeField : attributeFields) {
            String bindingPath = "";
            if (StringUtils.isNotBlank(attributeField.getBindingInfo().getBindByNamePrefix())) {
                bindingPath = attributeField.getBindingInfo().getBindByNamePrefix() + ".";
            }
            bindingPath += attributeField.getBindingInfo().getBindingName();

            populateDefaultValueForField(view, line, attributeField, bindingPath);
        }
    }

    /**
     * Applies the default value configured for the given field (if any) to the
     * line given object property that is determined by the given binding path
     * 
     * <p>
     * Checks for a configured default value or default value class for the
     * field. If both are given, the configured static default value will win.
     * In addition, if the default value contains an el expression it is
     * evaluated against the initial context
     * </p>
     * 
     * @param view
     *            - view instance the field belongs to
     * @param model
     *            - object that should be populated
     * @param attributeField
     *            - field to check for configured default value
     * @param bindingPath
     *            - path to the property on the object that should be populated
     */
    protected void populateDefaultValueForField(View view, Object object, AttributeField attributeField,
            String bindingPath) {
        // check for configured default value
        String defaultValue = attributeField.getDefaultValue();
        if (StringUtils.isBlank(defaultValue) && (attributeField.getDefaultValueFinderClass() != null)) {
            ValueFinder defaultValueFinder = ObjectUtils.newInstance(attributeField.getDefaultValueFinderClass());
            defaultValue = defaultValueFinder.getValue();
        }

        // populate default value if given and path is valid
        if (StringUtils.isNotBlank(defaultValue) && ObjectPropertyUtils.isWritableProperty(object, bindingPath)) {
            if (getExpressionEvaluatorService().containsElPlaceholder(defaultValue)) {
                Map<String, Object> context = getPreModelContext(view);
                defaultValue = getExpressionEvaluatorService().evaluateExpressionTemplate(null, context, defaultValue);
            }

            // TODO: this should go through our formatters
            ObjectPropertyUtils.setPropertyValue(object, bindingPath, defaultValue);
        }
    }

    /**
     * Hook for service overrides to perform custom initialization on the
     * component
     * 
     * @param view
     *            - view instance containing the component
     * @param component
     *            - component instance to initialize
     */
    protected void performCustomInitialization(View view, Component component) {

    }

    /**
     * Hook for service overrides to perform custom apply model logic on the
     * component
     * 
     * @param view
     *            - view instance containing the component
     * @param component
     *            - component instance to apply model to
     * @param model
     *            - Top level object containing the data (could be the form or a
     *            top level business object, dto)
     */
    protected void performCustomApplyModel(View view, Component component, Object model) {

    }

    /**
     * Hook for service overrides to perform custom component finalization
     * 
     * @param view
     *            - view instance containing the component
     * @param component
     *            - component instance to update
     * @param model
     *            - Top level object containing the data
     * @param parent
     *            - Parent component for the component being finalized
     */
    protected void performCustomFinalize(View view, Component component, Object model, Component parent) {

    }

    /**
     * Hook for service overrides to process the new collection line before it
     * is added to the collection
     * 
     * @param view
     *            - view instance that is being presented (the action was taken
     *            on)
     * @param collectionGroup
     *            - collection group component for the collection the line will
     *            be added to
     * @param model
     *            - object instance that contain's the views data
     * @param addLine
     *            - the new line instance to be processed
     */
    protected void processBeforeAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine) {

    }

    /**
     * Hook for service overrides to process the new collection line after it
     * has been added to the collection
     * 
     * @param view
     *            - view instance that is being presented (the action was taken
     *            on)
     * @param collectionGroup
     *            - collection group component for the collection the line that
     *            was added
     * @param model
     *            - object instance that contain's the views data
     * @param addLine
     *            - the new line that was added
     */
    protected void processAfterAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine) {

    }

    protected void logAndThrowRuntime(String message) {
        LOG.error(message);
        throw new RuntimeException(message);
    }

    protected DataDictionaryService getDataDictionaryService() {
        if (this.dataDictionaryService == null) {
            this.dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }

        return this.dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    protected ExpressionEvaluatorService getExpressionEvaluatorService() {
        if (this.expressionEvaluatorService == null) {
            this.expressionEvaluatorService = KRADServiceLocatorWeb.getExpressionEvaluatorService();
        }

        return this.expressionEvaluatorService;
    }

    public void setExpressionEvaluatorService(ExpressionEvaluatorService expressionEvaluatorService) {
        this.expressionEvaluatorService = expressionEvaluatorService;
    }

    public ViewDictionaryService getViewDictionaryService() {
        if (this.viewDictionaryService == null) {
            this.viewDictionaryService = KRADServiceLocatorWeb.getViewDictionaryService();
        }
        return this.viewDictionaryService;
    }

    public void setViewDictionaryService(ViewDictionaryService viewDictionaryService) {
        this.viewDictionaryService = viewDictionaryService;
    }
}
