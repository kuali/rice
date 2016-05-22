/**
 * Copyright 2005-2016 The Kuali Foundation
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
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.container.collections.LineBuilderContext;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.ControlBase;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.field.RemoteFieldsHolder;
import org.kuali.rice.krad.uif.layout.CollectionLayoutManager;
import org.kuali.rice.krad.uif.layout.TableLayoutManagerBase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleUtils;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ContextUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
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
import java.util.Iterator;
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
    private List<Field> unauthorizedFields = new ArrayList<Field>();

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
        boolean hasSubCollections =
                (lineBuilderContext.getSubCollectionFields() != null) && (!lineBuilderContext.getSubCollectionFields()
                        .isEmpty());

        // invoke layout manager and setup edit line dialog to build the complete line
        if (hasLineFields || hasSubCollections) {
            // setup the edit dialog if its not an add line
            if (!lineBuilderContext.isAddLine()) {
                setupEditLineDetails();
            }

            // add the lineDialogs to the lineActions
            List<Component> actions = new ArrayList<Component>();
            actions.addAll(lineBuilderContext.getLineActions());
            List<DialogGroup> dialogGroups = lineBuilderContext.getCollectionGroup().getLineDialogs();
            if (Boolean.TRUE.equals(lineBuilderContext.getCollectionGroup().getReadOnly())) {
                for (DialogGroup group : dialogGroups) {
                    group.setReadOnly(true);
                }
            }
            actions.addAll(dialogGroups);
            lineBuilderContext.setLineActions(actions);
            lineBuilderContext.getCollectionGroup().getLineDialogs().clear();

            // invoke the layout manager
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

        adjustFieldBindingAndId(lineFields, lineBuilderContext.getBindingPath());

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

        if (!canEditLine) {
            Iterator<Action> actionsIterator = actions.iterator();
            while (actionsIterator.hasNext()) {
                Action action = actionsIterator.next();
                if (action.getId().startsWith(ComponentFactory.EDIT_LINE_IN_DIALOG_ACTION + "_" +
                        lineBuilderContext.getCollectionGroup().getId())) {
                    actionsIterator.remove();
                    break;
                }
            }
            lineBuilderContext.setLineActions(actions);
        }

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
            } else if (item instanceof Field) {
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
     * @param bindingPath binding path to add
     */
    protected void adjustFieldBindingAndId(List<Field> lineFields, String bindingPath) {
        for (Field lineField : lineFields) {
            adjustFieldBinding(lineField, bindingPath);
            adjustFieldId(lineField);
        }

        if (lineBuilderContext.isBindToForm()) {
            ComponentUtils.setComponentsPropertyDeep(lineFields, UifPropertyPaths.BIND_TO_FORM, Boolean.valueOf(true));
        }
    }

    /**
     * Adjusts the binding path for the given field to match the collections path.
     *
     * @param lineField field to update
     * @param bindingPath binding path to add
     */
    protected void adjustFieldBinding(Field lineField, String bindingPath) {
        if (lineField instanceof DataBinding && ((DataBinding) lineField).getBindingInfo().isBindToForm()) {
            BindingInfo bindingInfo = ((DataBinding) lineField).getBindingInfo();
            bindingInfo.setCollectionPath(null);
            bindingInfo.setBindingName(bindingInfo.getBindingName() + "[" + lineBuilderContext.getLineIndex() + "]");
        } else {
            ComponentUtils.prefixBindingPath(lineField, bindingPath);
        }
    }

    /**
     * Adjusts the id suffix for the given field.
     *
     * @param lineField field to update
     */
    protected void adjustFieldId(Field lineField) {
        ComponentUtils.updateIdWithSuffix(lineField, lineBuilderContext.getIdSuffix());

        lineField.setContainerIdSuffix(lineBuilderContext.getIdSuffix());
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

            boolean focusLineFirst = StringUtils.isNotBlank(action.getFocusOnIdAfterSubmit()) && action
                    .getFocusOnIdAfterSubmit().equalsIgnoreCase(UifConstants.Order.LINE_FIRST.toString());
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
            canEditLine = checkEditLineAuthorizationAndPresentationLogic(lineBuilderContext.getCollectionGroup(),
                    lineBuilderContext.getModel(), lineBuilderContext.getCurrentLine());
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
    protected boolean checkEditLineAuthorizationAndPresentationLogic(CollectionGroup collectionGroup, ViewModel model,
            Object currentLine) {
        ViewPresentationController presentationController = ViewLifecycle.getView().getPresentationController();
        ViewAuthorizer authorizer = ViewLifecycle.getView().getAuthorizer();

        Person user = GlobalVariables.getUserSession().getPerson();

        boolean canEditLine = authorizer.canEditLine(ViewLifecycle.getView(), model, collectionGroup,
                collectionGroup.getPropertyName(), currentLine, user);
        if (canEditLine) {
            canEditLine = presentationController.canEditLine(ViewLifecycle.getView(), model, collectionGroup,
                    collectionGroup.getPropertyName(), currentLine);
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

        // set the parent line on the context of every sub-collection field
        for (FieldGroup subCollectionField : subCollectionFields) {
            Group group = subCollectionField.getGroup();
            if (group != null && group instanceof CollectionGroup) {
                CollectionGroup collectionGroup1 = (CollectionGroup) group;
                ContextUtils.pushObjectToContextDeep(collectionGroup1.getItems(),
                        UifConstants.ContextVariableNames.PARENT_LINE, lineBuilderContext.getCurrentLine());
            }
        }

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
     * Add additional information to the fields in the add line to allow for correct add control selection.
     *
     * @param lineFields list of fields instances for the line
     */
    protected void setupAddLineControlValidation(List<Field> lineFields) {
        // don't process for anything but an add line
        if (!lineBuilderContext.isAddLine()) {
            return;
        }

        // set up skipping fields with the given selectors in add area during standard form validation calls
        // custom addLineToCollection js call will validate these fields manually on an add
        List<String> selectors = new ArrayList<String>();
        String lineFieldSelector = UifConstants.IdSuffixes.CONTROL;
        String nestedLineFieldSelector = UifConstants.IdSuffixes.ADD_LINE + UifConstants.IdSuffixes.CONTROL;

        // apply changes to and collect selectors from all fields and field groups
        for (Field lineField : lineFields) {
            if (lineField instanceof InputField) {
                setupAddLineControlValidation((InputField) lineField, selectors, lineFieldSelector);
            } else if (lineField instanceof FieldGroup) {
                Group group = ((FieldGroup) lineField).getGroup();
                List<InputField> nestedLineFields = ViewLifecycleUtils.getElementsOfTypeDeep(group, InputField.class);

                for (InputField nestedLineField : nestedLineFields) {
                    setupAddLineControlValidation(nestedLineField, selectors, nestedLineFieldSelector);
                }
            }
        }

        // add collected selectors to data attributes
        lineBuilderContext.getCollectionGroup().addDataAttribute(
                UifConstants.DataAttributes.ADD_CONTROLS, StringUtils.join(selectors, ","));
    }

    /**
     * Add additional information to a field in the add line to allow for correct add control selection.
     *
     * @param lineField field instance for the line
     * @param selectors list of selectors
     * @param suffix id suffix to add
     */
    protected void setupAddLineControlValidation(InputField lineField, List<String> selectors, String suffix) {
        Control control = lineField.getControl();

        // ignore automatic validation and grab the selector for manual validation
        if (control != null) {
            control.addStyleClass(CssConstants.Classes.IGNORE_VALID);
            selectors.add("#" + lineField.getId() + suffix);
        }
    }

    /**
     * Setup edit line dialog group with the line fields
     *
     * <p>The items for a dialog are created from line fields and added if not provided by the user, but
     * if they are then each item is processed.</p>
     */
    protected void setupEditLineDetails() {
        CollectionGroup group = lineBuilderContext.getCollectionGroup();

        if (!group.isEditWithDialog()) {
            return;
        }

        for (DialogGroup lineDialog : group.getLineDialogs()) {
            String dialogId = lineDialog.getId();

            UifFormBase form = (UifFormBase) lineBuilderContext.getModel();
            if (group.getCollectionGroupBuilder().refreshEditLineDialogContents(lineDialog, form, group,
                    lineBuilderContext.getLineIndex()) && lineDialog.getId().contains(
                    ComponentFactory.EDIT_LINE_DIALOG)) {
                form.setUpdateComponent(lineDialog);
                // if there are no custom user items or if there are no items, then use the line fields as items
                if (lineDialog.getItems() == null || lineDialog.getItems().isEmpty() || !group
                        .isCustomEditLineDialog()) {
                    List<Field> lineFields = lineBuilderContext.getLineFields();

                    // process and set items
                    lineDialog.setItems(processDialogFieldsFromLineFields(lineFields, dialogId));

                    // process the sub-collection items
                    List<Component> items = new ArrayList<Component>(lineDialog.getItems());
                    items.addAll(processDialogSubFieldsFromLineSubFields(lineDialog));
                    items.addAll(processDialogSubFieldsFromRowDetails(lineDialog));
                    lineDialog.setItems(items);
                } else { // user provided dialog items
                    List<Component> dialogFields = new ArrayList<>(lineDialog.getItems());
                    List<Component> dialogComponents = new ArrayList<>();
                    int fieldIndex = 0;
                    int subIndex = 0;

                    // for every user provided dialog item, find its corresponding line field and set the binding info
                    for (Component dialogField : dialogFields) {
                        if (dialogField instanceof DataField) {
                            DataField dataField = (DataField) dialogField;
                            DataField lineField = findItemInLineFields(dataField);

                            if (lineField != null) {
                                dataField.getBindingInfo().setCollectionPath(
                                        lineField.getBindingInfo().getCollectionPath());
                                // set the line field to read-only
                                lineField.setReadOnly(true);
                            } else {
                                // update the binding info on the custom field
                                dataField.getBindingInfo().setCollectionPath(group.getBindingInfo().getBindingName());
                            }

                            dataField.getBindingInfo().setBindByNamePrefix(UifPropertyPaths.DIALOG_DATA_OBJECT);
                            dialogComponents.add(dataField);
                        } else if (dialogField instanceof FieldGroup) {
                            FieldGroup fieldGroup = (FieldGroup) dialogField;

                            if (fieldGroup.getGroup() instanceof CollectionGroup) {
                                dialogComponents.add(getNewFieldGroup(fieldGroup, (CollectionGroup) fieldGroup.
                                        getGroup(), lineDialog, fieldIndex, subIndex, null));
                                subIndex++;
                            }
                        } else if (dialogField instanceof CollectionGroup) {
                            CollectionGroup collectionGroup = (CollectionGroup) dialogField;
                            FieldGroup fieldGroupPrototype =
                                    lineBuilderContext.getLayoutManager().getSubCollectionFieldGroupPrototype();

                            dialogComponents.add(getNewFieldGroup(fieldGroupPrototype, collectionGroup, lineDialog,
                                    fieldIndex, subIndex, UifPropertyPaths.DIALOG_DATA_OBJECT));
                            subIndex++;
                        } else {
                            ComponentUtils.prefixBindingPath(dialogField, UifPropertyPaths.DIALOG_DATA_OBJECT);
                            dialogComponents.add(dialogField);
                        }
                        fieldIndex++;
                    }
                    lineDialog.setItems(dialogComponents);
                }
            }

        }

        // set all collection fields and sub-collection fields to readOnly
        if (lineBuilderContext.getCollectionGroup().isEditWithDialog()) {
            for (Field lineField : lineBuilderContext.getLineFields()) {
                if (lineField instanceof InputField) {
                    lineField.setReadOnly(Boolean.TRUE);
                }
            }
            List<FieldGroup> subLineFields = lineBuilderContext.getSubCollectionFields();
            if (subLineFields != null) {
                for (FieldGroup subLineField : subLineFields) {
                    subLineField.setReadOnly(Boolean.TRUE);
                }
            }
        }
    }

    /**
     * Helper method to build sub-collection fields for the given dialog using the line's sub-collection fields
     *
     * @param lineDialog the line dialog to build the sub-collection for
     * @return the list of created sub-collection fields
     */
    private List<FieldGroup> processDialogSubFieldsFromLineSubFields(DialogGroup lineDialog) {
        // process the subcollections
        List<FieldGroup> subCollectionFields = lineBuilderContext.getSubCollectionFields();
        List<FieldGroup> newSubCollectionFields = new ArrayList<FieldGroup>();
        int fieldIndex = lineDialog.getItems().size();
        int subIndex = 0;

        for (FieldGroup subCollectionFieldGroup : subCollectionFields) {
            CollectionGroup subCollectionGroup = (CollectionGroup) subCollectionFieldGroup.getGroup();

            // make a copy of the sub-group for the dialog
            newSubCollectionFields.add(getNewFieldGroup(subCollectionFieldGroup, subCollectionGroup, lineDialog,
                    fieldIndex, subIndex, UifPropertyPaths.DIALOG_DATA_OBJECT));
            fieldIndex++;
            subIndex++;

            List<Component> components = ViewLifecycleUtils.getElementsOfTypeDeep(subCollectionGroup.getItems(),
                    Component.class);
            for (Component component : components) {
                component.setReadOnly(Boolean.TRUE);
            }
        }

        return newSubCollectionFields;
    }

    /**
     * Helper method to build sub-collection fields for the given dialog using the row details group
     *
     * @param lineDialog the line dialog to build the sub-collection for
     * @return the list of created sub-collection fields
     */
    private List<Field> processDialogSubFieldsFromRowDetails(DialogGroup lineDialog) {
        List<Field> newSubCollectionFields = new ArrayList<Field>();

        // process the row details group
        CollectionLayoutManager layoutManager = (CollectionLayoutManager) lineBuilderContext.
                getCollectionGroup().getLayoutManager();

        // only the table layout manager has row details
        if (layoutManager instanceof TableLayoutManagerBase) {
            TableLayoutManagerBase tableLayoutManagerBase = (TableLayoutManagerBase) layoutManager;
            Group rowDetailsGroup = tableLayoutManagerBase.getRowDetailsGroup();

            if (rowDetailsGroup != null) {
                List<Component> subCollectionComponents = new ArrayList<Component>(rowDetailsGroup.getItems());

                int fieldIndex = lineDialog.getItems().size();
                int subIndex = 0;

                // for each item in the row details group create a field group to add the the line dialog's items
                for (Component component : subCollectionComponents) {
                    if (component instanceof CollectionGroup) {
                        CollectionGroup subCollectionGroup = (CollectionGroup) component;

                        boolean renderSubCollection = checkSubCollectionRender(subCollectionGroup);
                        if (!renderSubCollection) {
                            continue;
                        }

                        FieldGroup fieldGroupPrototype = lineBuilderContext.
                                getLayoutManager().getSubCollectionFieldGroupPrototype();
                        newSubCollectionFields.add(getNewFieldGroup(fieldGroupPrototype, subCollectionGroup, lineDialog,
                                fieldIndex, subIndex, UifPropertyPaths.DIALOG_DATA_OBJECT));
                        subIndex++;
                    } else if (component instanceof Field) {
                        Field subCollectionField = (Field) component;
                        Field newSubCollectionField = getNewFieldForEditLineDialog(subCollectionField,
                                lineDialog.getId() + UifConstants.IdSuffixes.FIELDSET + Integer.toString(fieldIndex++));

                        newSubCollectionFields.add(newSubCollectionField);
                    }

                    ContextUtils.pushObjectToContextDeep(newSubCollectionFields,
                            UifConstants.ContextVariableNames.PARENT_LINE,
                            ((UifFormBase) lineBuilderContext.getModel()).getDialogDataObject());
                }
            }
        }

        return newSubCollectionFields;
    }

    /**
     * Helper method that creates a new field group for a given sub-collection.
     *
     * @param fieldGroupPrototype the field group prototype to use
     * @param subCollectionGroup the sub-collection to create field group for
     * @param lineDialog the line dialog that the field group should be in
     * @param fieldIndex the index to apply for the field group
     * @param subIndex the index to apply for the sub-collection
     * @return the created field group
     */
    private FieldGroup getNewFieldGroup(FieldGroup fieldGroupPrototype, CollectionGroup subCollectionGroup,
            DialogGroup lineDialog, int fieldIndex, int subIndex, String bindingPrefix) {
        FieldGroup newSubCollectionFieldGroup = ComponentUtils.copy(fieldGroupPrototype);
        newSubCollectionFieldGroup.setId(lineDialog.getId() +
                UifConstants.IdSuffixes.FIELDSET + Integer.toString(fieldIndex));
        newSubCollectionFieldGroup.pushObjectToContext(UifConstants.ContextVariableNames.PARENT, lineDialog);

        CollectionGroup newSubCollectionGroup = ComponentUtils.copy(subCollectionGroup);
        newSubCollectionGroup.setId(newSubCollectionFieldGroup.getId() + UifConstants.IdSuffixes.SUB +
                Integer.toString(subIndex));
        if (bindingPrefix != null) {
            newSubCollectionGroup.getBindingInfo().setBindByNamePrefix(bindingPrefix);
        }

        if (newSubCollectionGroup.getBindingInfo().getBindingName() == null) {
            newSubCollectionGroup.getBindingInfo().setBindingName(newSubCollectionGroup.getPropertyName());
        }

        newSubCollectionGroup.pushObjectToContext(UifConstants.ContextVariableNames.PARENT, lineDialog);
        newSubCollectionGroup.addDataAttribute(UifConstants.
                ContextVariableNames.PARENT, lineDialog.getId());

        if (newSubCollectionGroup.isRenderAddLine()) {
            newSubCollectionGroup.getAddLineBindingInfo().setBindByNamePrefix(newSubCollectionGroup.
                    getBindingInfo().getBindByNamePrefix());
            newSubCollectionGroup.getAddLineBindingInfo().setBindingName(newSubCollectionGroup.
                    getBindingInfo().getBindingName());
            String addBindingPath = UifPropertyPaths.NEW_COLLECTION_LINES + "['" +
                    newSubCollectionGroup.getBindingInfo().getBindByNamePrefix() + "." +
                    newSubCollectionGroup.getBindingInfo().getBindingName() + "']";
            Object addLine = ObjectPropertyUtils.getPropertyValue(lineBuilderContext.getModel(), addBindingPath);
            if (addLine != null) {
                ObjectPropertyUtils.setPropertyValue(lineBuilderContext.getModel(), addBindingPath, null);
            }
        }

        // set the new collection group's line actions to refresh the entire
        // dialog not just the sub-collection
        List<Action> subLineActions = ViewLifecycleUtils.getElementsOfTypeDeep(newSubCollectionGroup.getLineActions(),
                Action.class);
        setupSubCollectionActions(subLineActions, lineDialog.getId(),
                lineBuilderContext.getCollectionGroup().getBindingInfo().getBindingName(),
                lineBuilderContext.getLineIndex());

        // initialize the new sub-collections line actions
        lineBuilderContext.getCollectionGroup().getCollectionGroupBuilder().initializeLineActions(subLineActions,
                ViewLifecycle.getView(), newSubCollectionGroup, lineBuilderContext.getCurrentLine(),
                lineBuilderContext.getLineIndex());

        // get the add line actions for this group
            List<Component> subAddLineComponents = new ArrayList<Component>(newSubCollectionGroup.getAddLineActions());
            if (newSubCollectionGroup.getAddBlankLineAction() != null) {
                subAddLineComponents.add(newSubCollectionGroup.getAddBlankLineAction());
            }
            List<Action> subAddLineActions = ViewLifecycleUtils.getElementsOfTypeDeep(subAddLineComponents,
                    Action.class);

            // initialize the new sub-collections add line actions
            setupSubCollectionActions(subAddLineActions, lineDialog.getId(), lineBuilderContext.
                            getCollectionGroup().getBindingInfo().getBindingName(), lineBuilderContext.getLineIndex()
            );

        newSubCollectionFieldGroup.setGroup(newSubCollectionGroup);

        ContextUtils.updateContextForLine(newSubCollectionFieldGroup, lineBuilderContext.
                        getCollectionGroup(), ((UifFormBase) lineBuilderContext.getModel()).
                        getDialogDataObject(), lineBuilderContext.getLineIndex(),
                lineBuilderContext.getIdSuffix() + UifConstants.IdSuffixes.SUB + subIndex
        );
        ContextUtils.pushObjectToContextDeep(newSubCollectionGroup, UifConstants.ContextVariableNames.PARENT_LINE,
                ((UifFormBase) lineBuilderContext.getModel()).getDialogDataObject());
        return newSubCollectionFieldGroup;
    }

    /**
     * Helper method that builds line dialog fields from the line fields
     *
     * @param lineFields the fields in the component
     * @param prefix the prefix to use in the id for the new fields
     * @return the list of created fields
     */
    private List<Field> processDialogFieldsFromLineFields(List<Field> lineFields, String prefix) {
        List<Field> newLineFields = new ArrayList<Field>();

        // for each line field create and add a corresponding dialog field
        int fieldIndex = 0;
        for (Field lineField : lineFields) {
            if (!(lineField instanceof FieldGroup)) {
                Field newLineField = getNewFieldForEditLineDialog(lineField, prefix +
                        UifConstants.IdSuffixes.FIELDSET + Integer.toString(fieldIndex));
                newLineFields.add(newLineField);
                fieldIndex++;
            }
        }
        return newLineFields;
    }

    /**
     * Helper method to create a new field that is a copy of a given field for the edit line dialog.
     *
     * @param field the field to copy
     * @param id the id of the new field
     * @return the new field
     */
    private Field getNewFieldForEditLineDialog(Field field, String id) {
        Field newLineField = ComponentUtils.copy(field, id);

        // set the line field to point to the dialog's data object
        if (newLineField instanceof DataField) {
            ((DataField) newLineField).getBindingInfo().setBindByNamePrefix(UifPropertyPaths.DIALOG_DATA_OBJECT);
        }
        return newLineField;
    }

    /**
     * Helper method to setup edit line dialog's sub-collection's line actions.
     *
     * @param actions the actions to setup
     * @param dialogId the id of the dialog the sub-collection is in
     * @param bindingName the binding name of the dialog's sub-collection
     * @param lineIndex the index of the line being edited in the dialog
     */
    private void setupSubCollectionActions(List<Action> actions, String dialogId, String bindingName, int lineIndex) {
        for (Action action : actions) {
            action.setDialogDismissOption("REQUEST");
            action.setRefreshId(StringUtils.substring(dialogId, dialogId.indexOf("_") + 1, dialogId.lastIndexOf("_")));
            String actionScript = UifConstants.JsFunctions.SHOW_EDIT_LINE_DIALOG + "('" +
                    dialogId + "', '" + bindingName + "', " + lineIndex + ");";
            action.setRefreshedByAction(false);
            action.setSuccessCallback("jQuery.unblockUI();" + actionScript);
            action.setOnClickScript("jQuery('#" + dialogId +
                "').one('hide.bs.modal', function(e) { jQuery.blockUI({ message: '<h1>Editing line ...</h1>' }); });");
            action.addActionParameter(UifParameters.DIALOG_ID, dialogId);
        }
    }

    /**
     * Helper method that gets a line item that corresponds to the given field
     *
     * <p>In this case, the corresponding line item for a field is one where the field's property names
     * are the same.</p>
     *
     * @param dataItem the data field to get the line item for
     */
    private DataField findItemInLineFields(DataField dataItem) {
        for (Field field : lineBuilderContext.getLineFields()) {
            if (field instanceof DataField) {
                if (dataItem.getPropertyName().equals(((DataField) field).getPropertyName())) {
                    return (DataField) field;
                }
            }
        }
        return null;
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
