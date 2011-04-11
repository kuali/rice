/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kuali.rice.kns.uif.service.impl;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.authorization.Authorizer;
import org.kuali.rice.kns.uif.authorization.PresentationController;
import org.kuali.rice.kns.uif.container.CollectionGroup;
import org.kuali.rice.kns.uif.container.Container;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.core.Component;
import org.kuali.rice.kns.uif.core.PropertyReplacer;
import org.kuali.rice.kns.uif.core.RequestParameter;
import org.kuali.rice.kns.uif.field.AttributeField;
import org.kuali.rice.kns.uif.layout.LayoutManager;
import org.kuali.rice.kns.uif.modifier.ComponentModifier;
import org.kuali.rice.kns.uif.service.ExpressionEvaluatorService;
import org.kuali.rice.kns.uif.service.ViewDictionaryService;
import org.kuali.rice.kns.uif.service.ViewHelperService;
import org.kuali.rice.kns.uif.util.CloneUtils;
import org.kuali.rice.kns.uif.util.ObjectPropertyUtils;
import org.kuali.rice.kns.uif.util.ViewModelUtils;
import org.kuali.rice.kns.uif.widget.Inquiry;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.spring.form.UifFormBase;

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
     * @see org.kuali.rice.kns.uif.service.ViewHelperService#populateViewFromRequestParameters(org.kuali.rice.kns.uif.container.View,
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
                }
                else {
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
     * @see org.kuali.rice.kns.uif.service.ViewHelperService#performInitialization(org.kuali.rice.kns.uif.container.View,
     *      java.util.Map)
     */
    @Override
    public void performInitialization(View view) {
        performComponentInitialization(view, view);
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
     * @see org.kuali.rice.kns.uif.service.ViewHelperService#performComponentInitialization(org.kuali.rice.kns.uif.container.View,
     *      org.kuali.rice.kns.uif.core.Component)
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

            // add attribute field to the view's index
            view.getViewIndex().addAttributeField((AttributeField) component);
        }

        // for collection groups set defaults from dictionary entry
        if (component instanceof CollectionGroup) {
            // TODO: initialize from dictionary

            // add collection group to the view's index
            view.getViewIndex().addCollection((CollectionGroup) component);
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
        // determine attribute name and entry within the dictionary to lookup
        String dictionaryAttributeName = field.getDictionaryAttributeName();
        String dictionaryObjectEntry = field.getDictionaryObjectEntry();

        // if entry given but not attribute name, use field name as attribute
        // name
        if (StringUtils.isNotBlank(dictionaryObjectEntry) && StringUtils.isBlank(dictionaryAttributeName)) {
            dictionaryAttributeName = field.getPropertyName();
        }

        // if both dictionary names not given and the field is from a model,
        // determine class based on the View and use field name as attribute
        // name
        if (StringUtils.isBlank(dictionaryAttributeName) && StringUtils.isBlank(dictionaryObjectEntry)
                && !field.getBindingInfo().isBindToForm()) {
            dictionaryAttributeName = field.getPropertyName();
            Class<?> dictionaryModelClass = getDictionaryModelClass(view, field);
            if (dictionaryModelClass != null) {
                dictionaryObjectEntry = dictionaryModelClass.getName();
            }
        }

        // if we were able to find a dictionary attribute and object, call
        // data dictionary service to get AttributeDefinition
        if (StringUtils.isNotBlank(dictionaryAttributeName) && StringUtils.isNotBlank(dictionaryObjectEntry)) {
            AttributeDefinition attributeDefinition = getDataDictionaryService().getAttributeDefinition(
                    dictionaryObjectEntry, dictionaryAttributeName);
            if (attributeDefinition != null) {
                field.copyFromAttributeDefinition(attributeDefinition);
            }

            // update field with attribute and entry name
            field.setDictionaryAttributeName(dictionaryAttributeName);
            field.setDictionaryObjectEntry(dictionaryObjectEntry);
        }
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
     * @see org.kuali.rice.kns.uif.service.ViewHelperService#performApplyModel(org.kuali.rice.kns.uif.container.View,
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
        view.setActionFlags(actionFlags);

        Set<String> editModes = presentationController.getEditModes(model);
        editModes = authorizer.getEditModes(model, user, editModes);
        view.setEditModes(editModes);
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
        view.pushObjectToContext(UifConstants.ContextVariableNames.VIEW, view);

        Properties properties = KNSServiceLocator.getKualiConfigurationService().getAllProperties();
        view.pushObjectToContext(UifConstants.ContextVariableNames.CONFIG_PROPERTIES, properties);
        
        view.pushObjectToContext(UifConstants.ContextVariableNames.CONSTANTS, KNSConstants.class);

        // evaluate view expressions for further context
        for (Entry<String, String> variableExpression : view.getExpressionVariables().entrySet()) {
            String variableName = variableExpression.getKey();
            Object value = getExpressionEvaluatorService().evaluateExpression(model, view.getContext(),
                    variableExpression.getValue());
            view.pushObjectToContext(variableName, value);
        }
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

        // evaluate properties
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

        // invoke component to perform its conditional logic
        component.performApplyModel(view, model);

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
                    modifier.performModification(view, component);
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
     * @see org.kuali.rice.kns.uif.service.ViewHelperService#performFinalize(org.kuali.rice.kns.uif.container.View,
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
     * @see org.kuali.rice.kns.uif.service.ViewHelperService#processCollectionAddLine(org.kuali.rice.kns.uif.container.View,
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
     * @see org.kuali.rice.kns.uif.service.ViewHelperService#processCollectionDeleteLine(org.kuali.rice.kns.uif.container.View,
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
        }
        else {
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
     * @see org.kuali.rice.kns.uif.service.ViewHelperService#buildInquiryLink(java.lang.Object,
     *      java.lang.String, org.kuali.rice.kns.uif.widget.Inquiry)
     */
    public void buildInquiryLink(Object dataObject, String propertyName, Inquiry inquiry) {
        Inquirable inquirable = getViewDictionaryService().getInquirable(dataObject.getClass(),
                inquiry.getViewName());
        if (inquirable != null) {
            inquirable.buildInquirableLink(dataObject, propertyName, inquiry);
        }
        else {
            // inquirable not found, no inquiry link can be set
            inquiry.setRender(false);
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
            this.dataDictionaryService = KNSServiceLocatorWeb.getDataDictionaryService();
        }

        return this.dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    protected ExpressionEvaluatorService getExpressionEvaluatorService() {
        if (this.expressionEvaluatorService == null) {
            this.expressionEvaluatorService = KNSServiceLocatorWeb.getExpressionEvaluatorService();
        }

        return this.expressionEvaluatorService;
    }

    public void setExpressionEvaluatorService(ExpressionEvaluatorService expressionEvaluatorService) {
        this.expressionEvaluatorService = expressionEvaluatorService;
    }

    public ViewDictionaryService getViewDictionaryService() {
        if (this.viewDictionaryService == null) {
            this.viewDictionaryService = KNSServiceLocatorWeb.getViewDictionaryService();
        }
        return this.viewDictionaryService;
    }

    public void setViewDictionaryService(ViewDictionaryService viewDictionaryService) {
        this.viewDictionaryService = viewDictionaryService;
    }

}
