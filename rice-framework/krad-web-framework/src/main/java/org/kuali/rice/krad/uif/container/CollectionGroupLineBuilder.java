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
package org.kuali.rice.krad.uif.container;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.uif.CssConstants;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.container.collections.LineBuilderContext;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.ControlBase;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.field.RemoteFieldsHolder;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleUtils;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ContextUtils;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewAuthorizer;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.view.ViewPresentationController;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Process configuration from the collection group to prepare components for a new line and invokes the associated
 * layout manager to add the line.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionGroupLineBuilder implements Serializable {
    private static final long serialVersionUID = 981187437246864378L;

    private LineBuilderContext lineBuilderContext;

    public CollectionGroupLineBuilder(LineBuilderContext lineBuilderContext) {
        this.lineBuilderContext = lineBuilderContext;
    }

    /**
     * Invoked to build a line in the collection.
     *
     * <p>First the context for the line is preprocessed in {@link CollectionGroupLineBuilder#preprocessLine()}.
     * After preprocessing the configured layout manager is invoked to place the line into the layout.</p>
     */
    public void buildLine() {
        preprocessLine();

        boolean hasLineFields =
                (lineBuilderContext.getLineFields() != null) && (!lineBuilderContext.getLineFields().isEmpty());
        boolean hasSubCollections = (lineBuilderContext.getSubCollectionFields() != null) &&
                (!lineBuilderContext.getSubCollectionFields().isEmpty());

        // invoke layout manager to build the complete line
        if (hasLineFields || hasSubCollections) {
            lineBuilderContext.getLayoutManager().buildLine(lineBuilderContext);
        }

        // After the lines have been processed and correct value set for readOnly and other properties
        // set the script for enabling/disabling save button
        applyOnChangeForSave(lineBuilderContext.getLineFields());
    }

    /**
     * Performs various preprocessing of the line components and configuration.
     *
     * <p>Preprocessing includes:
     * <ul>
     * <li>Make a copy of the collection groups items and adjust binding and id</li>
     * <li>Process any remotable fields within the line</li>
     * <li>Check line and field authorization</li>
     * <li>Remove fields that should not be rendered</li>
     * <li>Configure client side functionality such as save enable and add line validation</li>
     * <li>Build field groups to hold any configured sub-collections</li>
     * </ul></p>
     */
    public void preprocessLine() {
        List<? extends Component> lineItems = initializeLineItems();

        List<Field> lineFields = processAnyRemoteFieldsHolder(lineBuilderContext.getCollectionGroup(), lineItems);

        adjustFieldBindingAndId(lineFields);

        ContextUtils.updateContextsForLine(lineFields, lineBuilderContext.getCollectionGroup(),
                lineBuilderContext.getCurrentLine(), lineBuilderContext.getLineIndex(),
                lineBuilderContext.getIdSuffix());

        boolean canViewLine = checkViewLineAuthorization();
        if (!canViewLine) {
            return;
        }

        List<Action> actions = ViewLifecycleUtils.getElementsOfTypeDeep(lineBuilderContext.getLineActions(),
                Action.class);
        setFocusOnIdForActions(actions, lineFields);

        boolean canEditLine = checkEditLineAuthorization(lineFields);
        ContextUtils.pushObjectToContextDeep(lineFields, UifConstants.ContextVariableNames.READONLY_LINE, !canEditLine);
        ContextUtils.pushObjectToContextDeep(actions, UifConstants.ContextVariableNames.READONLY_LINE, !canEditLine);

        // check authorization for line fields
        applyLineFieldAuthorizationAndPresentationLogic(!canEditLine, lineFields, actions);

        // remove fields from the line that have render false
        lineFields = removeNonRenderLineFields(lineFields);

        buildSubCollectionFieldGroups();

        // update action parameters for any actions that were added in the line items (as opposed to the line actions)
        List<Action> lineFieldActions = ViewLifecycleUtils.getElementsOfTypeDeep(lineFields, Action.class);
        if (lineFieldActions != null) {
            lineBuilderContext.getCollectionGroup().getCollectionGroupBuilder().initializeActions(lineFieldActions,
                    lineBuilderContext.getCollectionGroup(), lineBuilderContext.getLineIndex());
        }

        setupAddLineControlValidation(lineFields);

        lineBuilderContext.setLineFields(lineFields);
    }

    /**
     * Copies either the collections groups items or add line items to a list of components that will be used
     * for the collection line.
     *
     * @return list of component instance for the collection line
     */
    protected List<? extends Component> initializeLineItems() {
        List<? extends Component> lineItems;

        if (lineBuilderContext.isAddLine()) {
            lineItems = ComponentUtils.copyComponentList(lineBuilderContext.getCollectionGroup().getAddLineItems(),
                    null);
        } else {
            lineItems = ComponentUtils.copyComponentList(lineBuilderContext.getCollectionGroup().getItems(), null);
        }

        return lineItems;
    }

    /**
     * Iterates through the given items checking for {@code RemotableFieldsHolder}, if found
     * the holder is invoked to retrieved the remotable fields and translate to attribute fields.
     *
     * <p>The translated list is then inserted into the returned list at the position of the holder</p>
     *
     * @param group collection group instance to check for any remotable fields holder
     * @param items list of items to process
     */
    public List<Field> processAnyRemoteFieldsHolder(CollectionGroup group, List<? extends Component> items) {
        List<Field> processedItems = new ArrayList<Field>();

        // check for holders and invoke to retrieve the remotable fields and translate
        // translated fields are placed into the processed items list at the position of the holder
        for (Component item : items) {
            if (item instanceof RemoteFieldsHolder) {
                List<InputField> translatedFields = ((RemoteFieldsHolder) item).fetchAndTranslateRemoteFields(group);
                processedItems.addAll(translatedFields);
            } else {
                processedItems.add((Field) item);
            }
        }

        return processedItems;
    }

    /**
     * Adjusts the binding path for the given fields to match the collections path, and sets the container id
     * suffix for the fields so all nested components will get their ids adjusted.
     *
     * @param lineFields list of fields to update
     */
    protected void adjustFieldBindingAndId(List<Field> lineFields) {
        ComponentUtils.prefixBindingPath(lineFields, lineBuilderContext.getBindingPath());

        for (Field field : lineFields) {
            ComponentUtils.updateIdWithSuffix(field, lineBuilderContext.getIdSuffix());
            field.setContainerIdSuffix(lineBuilderContext.getIdSuffix());
        }

        if (lineBuilderContext.isBindToForm()) {
            ComponentUtils.setComponentsPropertyDeep(lineFields, UifPropertyPaths.BIND_TO_FORM, Boolean.valueOf(true));
        }
    }

    /**
     * For any actions with focus id {@link org.kuali.rice.krad.uif.UifConstants.Order#LINE_FIRST}, the focus id
     * is replaced to match to id of the first control in the line.
     *
     * @param actions list of actions to potientially update
     * @param lineFields list of line fields, the control for the first field in the list
     * will be used for the focus id
     */
    protected void setFocusOnIdForActions(List<Action> actions, List<Field> lineFields) {
        for (Action action : actions) {
            if (action == null) {
                continue;
            }

            boolean focusLineFirst = StringUtils.isNotBlank(action.getFocusOnIdAfterSubmit()) &&
                    action.getFocusOnIdAfterSubmit().equalsIgnoreCase(UifConstants.Order.LINE_FIRST.toString());
            boolean lineHasFields = !lineFields.isEmpty();
            if (focusLineFirst && lineHasFields) {
                action.setFocusOnIdAfterSubmit(lineFields.get(0).getId() + UifConstants.IdSuffixes.CONTROL);
            }
        }
    }

    /**
     * If {@link CollectionGroup#isRenderSaveLineActions()} is true and the line has been added by the user, on change
     * script is added to any controls in the line to enable the save action.
     *
     * @param lineFields list of line fields
     */
    protected void applyOnChangeForSave(List<Field> lineFields) {
        boolean isLineNewlyAdded = ((UifFormBase) lineBuilderContext.getModel()).isAddedCollectionItem(
                lineBuilderContext.getCurrentLine());
        boolean saveLineEnabled = lineBuilderContext.getCollectionGroup().isRenderSaveLineActions();

        if (!isLineNewlyAdded && !saveLineEnabled) {
            return;
        }

        for (Field field : lineFields) {
            boolean isInputField = (field instanceof InputField);
            if (field.isHidden() || Boolean.TRUE.equals(field.getReadOnly()) || !isInputField) {
                continue;
            }

            // if control null, assign default
            InputField inputField = (InputField) field;
            if (inputField.getControl() == null) {
                inputField.setControl(ComponentFactory.getTextControl());
            }

            ControlBase control = (ControlBase) ((InputField) field).getControl();

            String onBlurScript = UifConstants.JsFunctions.COLLECTION_LINE_CHANGED + "(this, '" +
                    CssConstants.Classes.NEW_COLLECTION_ITEM + "');";
            onBlurScript = ScriptUtils.appendScript(control.getOnBlurScript(), onBlurScript);

            control.setOnBlurScript(onBlurScript);
        }
    }

    /**
     * Evaluates the render property for the given list of field instances for the line and removes any fields
     * from the returned list that have render false.
     *
     * <p>The conditional render string is also taken into account. This needs to be done here as opposed
     * to during the normal condition evaluation so the the fields are not used while building the
     * collection lines</p>
     *
     * @param lineFields list of fields configured for the line
     * @return list of field instances that should be rendered
     */
    protected List<Field> removeNonRenderLineFields(List<Field> lineFields) {
        List<Field> fields = new ArrayList<Field>();

        ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();

        for (Field lineField : lineFields) {
            String conditionalRender = lineField.getPropertyExpression(UifPropertyPaths.RENDER);

            // evaluate conditional render string if set
            if (StringUtils.isNotBlank(conditionalRender)) {
                Map<String, Object> context = getContextForField(ViewLifecycle.getView(),
                        lineBuilderContext.getCollectionGroup(), lineField);

                // Adjust the condition as ExpressionUtils.adjustPropertyExpressions will only be
                // executed after the collection is built.
                conditionalRender = expressionEvaluator.replaceBindingPrefixes(ViewLifecycle.getView(), lineField,
                        conditionalRender);

                Boolean render = (Boolean) expressionEvaluator.evaluateExpression(context, conditionalRender);
                lineField.setRender(render);
            }

            // only add line field if set to render or if it is hidden by progressive render
            if (lineField.isRender() || StringUtils.isNotBlank(lineField.getProgressiveRender())) {
                fields.add(lineField);
            }
        }

        return fields;
    }

    /**
     * Determines whether the user is authorized to the view the line.
     *
     * @return boolean true if the user can view the line, false if not
     */
    protected boolean checkViewLineAuthorization() {
        boolean canViewLine = true;

        // check view line authorization if collection is not hidden
        if (!lineBuilderContext.isAddLine()) {
            canViewLine = checkViewLineAuthorizationAndPresentationLogic();
        }

        if (!canViewLine) {
            addUnauthorizedBindingInfo();
        }

        return canViewLine;
    }

    /**
     * Invokes the view's configured authorizer and presentation controller to determine if the user has permission
     * to view the line (if a permission has been established).
     *
     * @return true if the user can view the line, false if not
     */
    protected boolean checkViewLineAuthorizationAndPresentationLogic() {
        ViewPresentationController presentationController = ViewLifecycle.getView().getPresentationController();
        ViewAuthorizer authorizer = ViewLifecycle.getView().getAuthorizer();

        Person user = GlobalVariables.getUserSession().getPerson();

        CollectionGroup collectionGroup = lineBuilderContext.getCollectionGroup();

        boolean canViewLine = authorizer.canViewLine(ViewLifecycle.getView(), lineBuilderContext.getModel(),
                collectionGroup, collectionGroup.getPropertyName(), lineBuilderContext.getCurrentLine(), user);
        if (canViewLine) {
            canViewLine = presentationController.canViewLine(ViewLifecycle.getView(), lineBuilderContext.getModel(),
                    collectionGroup, collectionGroup.getPropertyName(), lineBuilderContext.getCurrentLine());
        }

        return canViewLine;
    }

    /**
     * Determines whether the user is authorized to the edit the line.
     *
     * @param lineFields list of fields configured for the line
     * @return boolean true if the user can edit the line, false if not
     */
    protected boolean checkEditLineAuthorization(List<Field> lineFields) {
        boolean canEditLine = !Boolean.TRUE.equals(lineBuilderContext.getCollectionGroup().getReadOnly());

        if (!canEditLine) {
            ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();
            View view = ViewLifecycle.getView();

            for (Field field : lineFields) {
                field.pushObjectToContext(UifConstants.ContextVariableNames.PARENT,
                        lineBuilderContext.getCollectionGroup());
                field.pushAllToContext(view.getContext());
                field.pushObjectToContext(UifConstants.ContextVariableNames.COMPONENT, field);

                expressionEvaluator.evaluatePropertyExpression(view, field.getContext(), field,
                        UifPropertyPaths.READ_ONLY, true);

                if (!Boolean.TRUE.equals(field.getReadOnly())) {
                    canEditLine = true;
                    break;
                }
            }
        }

        if (canEditLine && !lineBuilderContext.isAddLine()) {
            canEditLine = checkEditLineAuthorizationAndPresentationLogic();
        }

        if (!canEditLine) {
            addUnauthorizedBindingInfo();
        }

        return canEditLine;
    }

    /**
     * Invokes the view's configured authorizer and presentation controller to determine if the user has permission
     * to edit the line (if a permission has been established).
     *
     * @return true if the user can edit the line, false if not
     */
    protected boolean checkEditLineAuthorizationAndPresentationLogic() {
        ViewPresentationController presentationController = ViewLifecycle.getView().getPresentationController();
        ViewAuthorizer authorizer = ViewLifecycle.getView().getAuthorizer();

        Person user = GlobalVariables.getUserSession().getPerson();

        CollectionGroup collectionGroup = lineBuilderContext.getCollectionGroup();

        boolean canEditLine = authorizer.canEditLine(ViewLifecycle.getView(), lineBuilderContext.getModel(),
                collectionGroup, collectionGroup.getPropertyName(), lineBuilderContext.getCurrentLine(), user);
        if (canEditLine) {
            canEditLine = presentationController.canEditLine(ViewLifecycle.getView(), lineBuilderContext.getModel(),
                    collectionGroup, collectionGroup.getPropertyName(), lineBuilderContext.getCurrentLine());
        }

        return canEditLine;
    }

    /**
     * Adds a {@link org.kuali.rice.krad.uif.component.BindingInfo} instance for the given binding
     * path to the collection groups unauthorized list.
     */
    protected void addUnauthorizedBindingInfo() {
        if (lineBuilderContext.getCollectionGroup().getUnauthorizedLineBindingInfos() == null) {
            lineBuilderContext.getCollectionGroup().setUnauthorizedLineBindingInfos(new ArrayList<BindingInfo>());
        }

        BindingInfo bindingInfo = new BindingInfo();
        bindingInfo.setDefaults(ViewLifecycle.getView(), lineBuilderContext.getBindingPath());
        lineBuilderContext.getCollectionGroup().getUnauthorizedLineBindingInfos().add(bindingInfo);
    }

    /**
     * Iterates through the line fields and checks the view field authorization using the view's configured authorizer
     * and presentation controller.
     *
     * <p>If the field is viewable, then sets the edit field authorization. Finally iterates
     * through the line actions invoking the authorizer and presentation controller to authorizer the action</p>
     *
     * @param readOnlyLine flag indicating whether the line has been marked as read only (which will force the fields
     * to be read only)
     * @param lineFields list of fields instances for the line
     * @param actionList list of action field instances for the line
     */
    protected void applyLineFieldAuthorizationAndPresentationLogic(boolean readOnlyLine, List<Field> lineFields,
            List<? extends Component> actionList) {
        ViewPresentationController presentationController = ViewLifecycle.getView().getPresentationController();
        ViewAuthorizer authorizer = ViewLifecycle.getView().getAuthorizer();

        Person user = GlobalVariables.getUserSession().getPerson();
        ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();

        CollectionGroup collectionGroup = lineBuilderContext.getCollectionGroup();
        View view = ViewLifecycle.getView();
        ViewModel model = lineBuilderContext.getModel();
        Object currentLine = lineBuilderContext.getCurrentLine();

        for (Field lineField : lineFields) {
            String propertyName = null;
            if (lineField instanceof DataBinding) {
                propertyName = ((DataBinding) lineField).getPropertyName();
            }

            // evaluate expression on fields component security (since apply model phase has not been invoked on
            // them yet
            ComponentSecurity componentSecurity = lineField.getComponentSecurity();

            Map<String, Object> context = getContextForField(ViewLifecycle.getView(), collectionGroup, lineField);
            expressionEvaluator.evaluateExpressionsOnConfigurable(ViewLifecycle.getView(), componentSecurity, context);

            // check view field auth
            if (!lineField.isRender() || lineField.isHidden()) {
                continue;
            }

            boolean canViewField = authorizer.canViewLineField(view, model, collectionGroup,
                    collectionGroup.getPropertyName(), currentLine, lineField, propertyName, user);
            if (canViewField) {
                canViewField = presentationController.canViewLineField(view, model, collectionGroup,
                        collectionGroup.getPropertyName(), currentLine, lineField, propertyName);
            }

            if (!canViewField) {
                // since removing can impact layout, set to hidden
                // TODO: check into encryption setting
                lineField.setHidden(true);

                if (lineField.getPropertyExpressions().containsKey(UifPropertyPaths.HIDDEN)) {
                    lineField.getPropertyExpressions().remove(UifPropertyPaths.HIDDEN);
                }

                continue;
            }

            // check edit field auth
            boolean canEditField = !readOnlyLine;
            if (!readOnlyLine) {
                canEditField = authorizer.canEditLineField(view, model, collectionGroup,
                        collectionGroup.getPropertyName(), currentLine, lineField, propertyName, user);
                if (canEditField) {
                    canEditField = presentationController.canEditLineField(view, model, collectionGroup,
                            collectionGroup.getPropertyName(), currentLine, lineField, propertyName);
                }
            }

            if (readOnlyLine || !canEditField) {
                lineField.setReadOnly(true);

                if (lineField.getPropertyExpressions().containsKey(UifPropertyPaths.READ_ONLY)) {
                    lineField.getPropertyExpressions().remove(UifPropertyPaths.READ_ONLY);
                }
            }
        }

        // check auth on line actions
        List<Action> actions = ViewLifecycleUtils.getElementsOfTypeDeep(actionList, Action.class);
        for (Action action : actions) {
            if (!action.isRender()) {
                continue;
            }

            boolean canPerformAction = authorizer.canPerformLineAction(view, model, collectionGroup,
                    collectionGroup.getPropertyName(), currentLine, action, action.getActionEvent(), action.getId(),
                    user);
            if (canPerformAction) {
                canPerformAction = presentationController.canPerformLineAction(view, model, collectionGroup,
                        collectionGroup.getPropertyName(), currentLine, action, action.getActionEvent(),
                        action.getId());
            }

            if (!canPerformAction) {
                action.setRender(false);

                if (action.getPropertyExpressions().containsKey(UifPropertyPaths.RENDER)) {
                    action.getPropertyExpressions().remove(UifPropertyPaths.RENDER);
                }
            }
        }
    }

    /**
     * For each configured sub collection of the collection group, creates a field group by copying
     * {@link org.kuali.rice.krad.uif.layout.CollectionLayoutManager#getSubCollectionFieldGroupPrototype()} and adds
     * to a list which is stored in the line context.
     */
    protected void buildSubCollectionFieldGroups() {
        CollectionGroup collectionGroup = lineBuilderContext.getCollectionGroup();

        String idSuffix = lineBuilderContext.getIdSuffix();

        // sub collections are not created for the add line
        if (lineBuilderContext.isAddLine() || (collectionGroup.getSubCollections() == null)) {
            return;
        }

        List<FieldGroup> subCollectionFields = new ArrayList<FieldGroup>();
        for (int subLineIndex = 0; subLineIndex < collectionGroup.getSubCollections().size(); subLineIndex++) {
            CollectionGroup subCollectionPrototype = collectionGroup.getSubCollections().get(subLineIndex);
            CollectionGroup subCollectionGroup = ComponentUtils.copy(subCollectionPrototype);

            // verify the sub-collection should be rendered
            boolean renderSubCollection = checkSubCollectionRender(subCollectionGroup);
            if (!renderSubCollection) {
                continue;
            }

            subCollectionGroup.getBindingInfo().setBindByNamePrefix(lineBuilderContext.getBindingPath());
            if (subCollectionGroup.isRenderAddLine()) {
                subCollectionGroup.getAddLineBindingInfo().setBindByNamePrefix(lineBuilderContext.getBindingPath());
            }

            FieldGroup fieldGroupPrototype =
                    lineBuilderContext.getLayoutManager().getSubCollectionFieldGroupPrototype();

            FieldGroup subCollectionFieldGroup = ComponentUtils.copy(fieldGroupPrototype,
                    idSuffix + UifConstants.IdSuffixes.SUB + subLineIndex);
            subCollectionFieldGroup.setGroup(subCollectionGroup);

            subCollectionFieldGroup.setContainerIdSuffix(idSuffix);

            ContextUtils.updateContextForLine(subCollectionFieldGroup, collectionGroup,
                    lineBuilderContext.getCurrentLine(), lineBuilderContext.getLineIndex(),
                    idSuffix + UifConstants.IdSuffixes.SUB + subLineIndex);
            ContextUtils.pushObjectToContextDeep(subCollectionGroup, UifConstants.ContextVariableNames.PARENT_LINE,
                    lineBuilderContext.getCurrentLine());

            subCollectionFields.add(subCollectionFieldGroup);
        }

        ContextUtils.pushObjectToContextDeep(subCollectionFields, UifConstants.ContextVariableNames.PARENT_LINE,
                lineBuilderContext.getCurrentLine());

        lineBuilderContext.setSubCollectionFields(subCollectionFields);
    }

    /**
     * Checks whether the given sub-collection should be rendered, any conditional render string is evaluated.
     *
     * @param subCollectionGroup sub collection group to check render status for
     * @return true if sub collection should be rendered, false if it
     * should not be rendered
     */
    protected boolean checkSubCollectionRender(CollectionGroup subCollectionGroup) {
        String conditionalRender = subCollectionGroup.getPropertyExpression(UifPropertyPaths.RENDER);

        // TODO: check authorizer

        // evaluate conditional render string if set
        if (StringUtils.isNotBlank(conditionalRender)) {
            Map<String, Object> context = new HashMap<String, Object>();
            Map<String, Object> viewContext = ViewLifecycle.getView().getContext();

            if (viewContext != null) {
                context.putAll(viewContext);
            }

            context.put(UifConstants.ContextVariableNames.PARENT, lineBuilderContext.getCollectionGroup());
            context.put(UifConstants.ContextVariableNames.COMPONENT, subCollectionGroup);

            Boolean render = (Boolean) ViewLifecycle.getExpressionEvaluator().evaluateExpression(context,
                    conditionalRender);
            subCollectionGroup.setRender(render);
        }

        return subCollectionGroup.isRender();
    }

    /**
     * Add additional information to the group and fields in the add line to allow for correct
     * add control selection.
     */
    protected void setupAddLineControlValidation(List<Field> lineFields) {
        if (!lineBuilderContext.isAddLine()) {
            return;
        }

        String selector = "";
        for (Field field : lineFields) {
            if (field instanceof InputField) {
                // sets up - skipping these fields in add area during standard form validation calls
                // custom addLineToCollection js call will validate these fields manually on an add
                Control control = ((InputField) field).getControl();

                if (control != null) {
                    control.addStyleClass(CssConstants.Classes.IGNORE_VALID);
                    selector = selector + ",#" + field.getId() + UifConstants.IdSuffixes.CONTROL;
                }
            } else if (field instanceof FieldGroup) {
                List<InputField> fields = ViewLifecycleUtils.getElementsOfTypeDeep(((FieldGroup) field).getGroup(),
                        InputField.class);

                for (InputField nestedField : fields) {
                    Control control = nestedField.getControl();

                    if (control != null) {
                        control.addStyleClass(CssConstants.Classes.IGNORE_VALID);
                        selector = selector + ",#" + nestedField.getId() + UifConstants.IdSuffixes.CONTROL;
                    }
                }
            }
        }

        lineBuilderContext.getCollectionGroup().addDataAttribute(UifConstants.DataAttributes.ADD_CONTROLS,
                selector.replaceFirst(",", ""));
    }

    /**
     * Helper method to build the context for a field (needed because the apply model phase for line fields has
     * not been applied yet and their full context not set)
     *
     * @param view view instance the field belongs to
     * @param collectionGroup collection group instance the field belongs to
     * @param field field instance to build context for
     * @return Map<String, Object> context for field
     */
    protected Map<String, Object> getContextForField(View view, CollectionGroup collectionGroup, Field field) {
        Map<String, Object> context = new HashMap<String, Object>();

        Map<String, Object> viewContext = view.getContext();
        if (viewContext != null) {
            context.putAll(viewContext);
        }

        Map<String, Object> fieldContext = field.getContext();
        if (fieldContext != null) {
            context.putAll(fieldContext);
        }

        context.put(UifConstants.ContextVariableNames.PARENT, collectionGroup);
        context.put(UifConstants.ContextVariableNames.COMPONENT, field);

        return context;
    }
}
