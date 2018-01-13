/**
 * Copyright 2005-2018 The Kuali Foundation
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

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.collections.LineBuilderContext;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleUtils;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ContextUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.FormView;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds out the {@link org.kuali.rice.krad.uif.field.Field} instances for a collection group with a
 * series of steps that interact with the configured {@link org.kuali.rice.krad.uif.layout.CollectionLayoutManager}
 * to assemble the fields as necessary for the layout.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionGroupBuilder implements Serializable {

    private static final long serialVersionUID = -4762031957079895244L;
    private static Log LOG = LogFactory.getLog(CollectionGroupBuilder.class);

    /**
     * Invoked within the lifecycle to carry out the collection build process.
     *
     * <p>The corresponding collection is retrieved from the model and iterated
     * over to create the necessary fields. The binding path for fields that
     * implement {@code DataBinding} is adjusted to point to the collection
     * line it is apart of. For example, field 'number' of collection 'accounts'
     * for line 1 will be set to 'accounts[0].number', and for line 2
     * 'accounts[1].number'. Finally parameters are set on the line's action
     * fields to indicate what collection and line they apply to.</p>
     *
     * <p>Only the lines that are to be rendered (as specified by the displayStart
     * and displayLength properties of the CollectionGroup) will be built.</p>
     *
     * @param view View instance the collection belongs to
     * @param model Top level object containing the data
     * @param collectionGroup CollectionGroup component for the collection
     */
    public void build(View view, Object model, CollectionGroup collectionGroup) {
        // create add line
        if (collectionGroup.isRenderAddLine() && !Boolean.TRUE.equals(collectionGroup.getReadOnly()) &&
                !collectionGroup.isRenderAddBlankLineButton()) {
            buildAddLine(view, model, collectionGroup);
        }

        // if add line button enabled setup to refresh the collection group
        if (collectionGroup.isRenderAddBlankLineButton() && (collectionGroup.getAddBlankLineAction() != null)) {
            collectionGroup.getAddBlankLineAction().setRefreshId(collectionGroup.getId());
        }

        // get the collection for this group from the model
        List<Object> modelCollection = ObjectPropertyUtils.getPropertyValue(model,
                collectionGroup.getBindingInfo().getBindingPath());

        if (modelCollection == null) {
            return;
        }

        // filter inactive model
        List<Integer> showIndexes = performCollectionFiltering(view, model, collectionGroup, modelCollection);

        if (collectionGroup.getDisplayCollectionSize() != -1 && showIndexes.size() > collectionGroup
                .getDisplayCollectionSize()) {
            // remove all indexes in showIndexes beyond the collection's size limitation
            List<Integer> newShowIndexes = new ArrayList<Integer>();
            Integer counter = 0;

            for (int index = 0; index < showIndexes.size(); index++) {
                newShowIndexes.add(showIndexes.get(index));

                counter++;

                if (counter == collectionGroup.getDisplayCollectionSize()) {
                    break;
                }
            }

            showIndexes = newShowIndexes;
        }

        // dataTables needs to know the number of filtered elements for rendering purposes
        List<IndexedElement> filteredIndexedElements = buildFilteredIndexedCollection(showIndexes, modelCollection);
        collectionGroup.setFilteredCollectionSize(filteredIndexedElements.size());

        buildLinesForDisplayedRows(filteredIndexedElements, view, model, collectionGroup);
    }

    /**
     * Build a filtered and indexed version of the model collection based on showIndexes.
     *
     * <p>The items in the returned collection contain
     * <ul>
     * <li>an <b>index</b> property which refers to the original position within the unfiltered model collection</li>
     * <li>an <b>element</b> property which is a reference to the element in the model collection</li>
     * </ul>
     * </p>
     *
     * @param showIndexes A List of indexes to model collection elements that were not filtered out
     * @param modelCollection the model collection
     * @return a filtered and indexed version of the model collection
     *
     * @see IndexedElement
     */
    private List<IndexedElement> buildFilteredIndexedCollection(List<Integer> showIndexes,
            List<Object> modelCollection) {
        // apply the filtering in a way that preserves the original indices for binding path use
        List<IndexedElement> filteredIndexedElements = new ArrayList<IndexedElement>(modelCollection.size());

        for (Integer showIndex : showIndexes) {
            filteredIndexedElements.add(new IndexedElement(showIndex, modelCollection.get(showIndex)));
        }

        return filteredIndexedElements;
    }

    /**
     * Build the lines for the collection rows to be rendered.
     *
     * @param filteredIndexedElements a filtered and indexed list of the model collection elements
     * @param view View instance the collection belongs to
     * @param model Top level object containing the data
     * @param collectionGroup CollectionGroup component for the collection
     */
    protected void buildLinesForDisplayedRows(List<IndexedElement> filteredIndexedElements, View view, Object model,
            CollectionGroup collectionGroup) {

        // if we are doing server paging, but the display length wasn't set (which will be the case on the page render)
        // then only render one line.  Needed to force the table to show up in the page.
        if (collectionGroup.isUseServerPaging() && collectionGroup.getDisplayLength() == -1) {
            collectionGroup.setDisplayLength(1);
        }

        int displayStart = (collectionGroup.getDisplayStart() != -1 && collectionGroup.isUseServerPaging()) ?
                collectionGroup.getDisplayStart() : 0;

        int displayLength = (collectionGroup.getDisplayLength() != -1 && collectionGroup.isUseServerPaging()) ?
                collectionGroup.getDisplayLength() : filteredIndexedElements.size() - displayStart;

        // make sure we don't exceed the size of our collection
        int displayEndExclusive =
                (displayStart + displayLength > filteredIndexedElements.size()) ? filteredIndexedElements.size() :
                        displayStart + displayLength;

        // get a view of the elements that will be displayed on the page (if paging is enabled)
        List<IndexedElement> renderedIndexedElements = filteredIndexedElements.subList(displayStart,
                displayEndExclusive);

        // for each unfiltered collection row to be rendered, build the line fields
        for (IndexedElement indexedElement : renderedIndexedElements) {
            Object currentLine = indexedElement.element;

            String bindingPathPrefix =
                    collectionGroup.getBindingInfo().getBindingPrefixForNested() + "[" + indexedElement.index + "]";

            // initialize the line dialogs, like edit line dialog
            initializeEditLineDialog(collectionGroup, indexedElement.index, currentLine, model);

            List<Component> actionComponents = new ArrayList<>(ComponentUtils.copy(collectionGroup.getLineActions()));

            // initialize the line actions
            List<? extends Component> lineActions = initializeLineActions(actionComponents, view, collectionGroup,
                    currentLine, indexedElement.index);

            LineBuilderContext lineBuilderContext = new LineBuilderContext(indexedElement.index, currentLine,
                    bindingPathPrefix, false, (ViewModel) model, collectionGroup, lineActions);

            getCollectionGroupLineBuilder(lineBuilderContext).buildLine();
        }
    }

    /**
     * Helper method to initialize the edit line dialog and add it to the line dialogs for the group.
     *
     * @param collectionGroup the collection group to initialize the line dialogs for
     * @param lineIndex the current line index
     * @param currentLine the data object bound to the current line
     * @param model the view's data
     */
    protected void initializeEditLineDialog(CollectionGroup collectionGroup, int lineIndex, Object currentLine,
            Object model) {
        if (!collectionGroup.isEditWithDialog()) {
            return;
        }

        String lineSuffix = UifConstants.IdSuffixes.LINE + Integer.toString(lineIndex);

        // use the edit line dialog prototype to initialilze the edit line dialog
        DialogGroup editLineDialog = ComponentUtils.copy(collectionGroup.getEditLineDialogPrototype());
        editLineDialog.setId(ComponentFactory.EDIT_LINE_DIALOG + "_" + collectionGroup.getId() + lineSuffix);
        editLineDialog.setRetrieveViaAjax(true);

        if (refreshEditLineDialogContents(editLineDialog, model, collectionGroup, lineIndex)) {
            // if this is an edit line, set up the edit line dialog and add it to the list of dialogs
            currentLine = ((UifFormBase) model).getDialogDataObject();
            setupEditLineDialog(editLineDialog, collectionGroup, lineIndex, lineSuffix, currentLine);
        }

        // add the edit line dialog to the list of line dialogs for the group
        if (collectionGroup.getLineDialogs() == null || collectionGroup.getLineDialogs().isEmpty()) {
            collectionGroup.setLineDialogs((new ArrayList<DialogGroup>()));
        }
        collectionGroup.getLineDialogs().add(editLineDialog);
    }

    /**
     * Helper method to create and setup the edit line dialog for the indexed line.
     *
     * @param editLineDialog the dialog to setup for editing the line
     * @param group the collection group to create line dialogs for
     * @param lineIndex the current line index
     * @param lineSuffix the line suffix to use on dialog component id's
     * @param currentLine the data object bound to the current line
     */
    protected void setupEditLineDialog(DialogGroup editLineDialog, CollectionGroup group, int lineIndex,
            String lineSuffix, Object currentLine) {
        // use the edit line dialog's save action prototype to initialilze the edit line dialog's save action
        Action editLineInDialogSaveAction = ComponentUtils.copy(group.getEditInDialogSaveActionPrototype());
        editLineInDialogSaveAction.setId(editLineDialog.getId() + "_" +
                ComponentFactory.EDIT_LINE_IN_DIALOG_SAVE_ACTION + Integer.toString(lineIndex));

        // setup the cancel action for the edit line dialog
        Action cancelEditLineInDialogAction = (Action) ComponentFactory.
                getNewComponentInstance(ComponentFactory.DIALOG_DISMISS_ACTION);
        cancelEditLineInDialogAction.setId(editLineDialog.getId() + "_" +
                ComponentFactory.DIALOG_DISMISS_ACTION + Integer.toString(lineIndex));
        cancelEditLineInDialogAction.setRefreshId(group.getId());
        cancelEditLineInDialogAction.setMethodToCall(UifConstants.MethodToCallNames.CLOSE_EDIT_LINE_DIALOG);
        cancelEditLineInDialogAction.setDialogDismissOption("REQUEST");

        // add the created save action to the dialog's footer items
        List<Component> actionComponents = new ArrayList<Component>();
        if (editLineDialog.getFooter().getItems() != null) {
            actionComponents.addAll(editLineDialog.getFooter().getItems());
        }

        actionComponents.add(editLineInDialogSaveAction);
        actionComponents.add(cancelEditLineInDialogAction);
        editLineDialog.getFooter().setItems(actionComponents);

        // initialize the dialog actions
        List<Action> actions = ViewLifecycleUtils.getElementsOfTypeDeep(actionComponents, Action.class);
        group.getCollectionGroupBuilder().initializeActions(actions, group, lineIndex);
        editLineDialog.getFooter().setItems(actionComponents);

        // set the header actions (for example the close button/icon) to refresh the underlying edit line
        // collection and resetting the edit line dialog
        if (editLineDialog.getHeader().getUpperGroup().getItems() != null) {
            List<Action> headerActions = ViewLifecycleUtils.getElementsOfTypeDeep(editLineDialog.getHeader().
                    getUpperGroup().getItems(), Action.class);
            initializeActions(headerActions, group, lineIndex);
            for (Action headerAction : headerActions) {
                headerAction.setRefreshId(group.getId());
                headerAction.setMethodToCall(UifConstants.MethodToCallNames.CLOSE_EDIT_LINE_DIALOG);
                headerAction.setDialogDismissOption("REQUEST");
                headerAction.setActionScript(null);
            }
        }

        // update the context of the dialog for the current line
        ContextUtils.updateContextForLine(editLineDialog, group, currentLine, lineIndex, lineSuffix);
    }

    /**
     * Performs any filtering necessary on the collection before building the collection fields.
     *
     * <p>If showInactive is set to false and the collection line type implements {@code Inactivatable},
     * invokes the active collection filter. Then any {@link CollectionFilter} instances configured for the collection
     * group are invoked to filter the collection. Collections lines must pass all filters in order to be
     * displayed</p>
     *
     * @param view view instance that contains the collection
     * @param model object containing the views data
     * @param collectionGroup collection group component instance that will display the collection
     * @param collection collection instance that will be filtered
     */
    protected List<Integer> performCollectionFiltering(View view, Object model, CollectionGroup collectionGroup,
            Collection<?> collection) {
        List<Integer> filteredIndexes = new ArrayList<Integer>();
        for (int i = 0; i < collection.size(); i++) {
            filteredIndexes.add(Integer.valueOf(i));
        }

        if (Inactivatable.class.isAssignableFrom(collectionGroup.getCollectionObjectClass()) && !collectionGroup
                .isShowInactiveLines()) {
            List<Integer> activeIndexes = collectionGroup.getActiveCollectionFilter().filter(view, model,
                    collectionGroup);
            filteredIndexes = ListUtils.intersection(filteredIndexes, activeIndexes);
        }

        for (CollectionFilter collectionFilter : collectionGroup.getFilters()) {
            List<Integer> indexes = collectionFilter.filter(view, model, collectionGroup);
            filteredIndexes = ListUtils.intersection(filteredIndexes, indexes);
            if (filteredIndexes.isEmpty()) {
                break;
            }
        }

        return filteredIndexes;
    }

    /**
     * Builds the fields for holding the collection add line and if necessary makes call to setup
     * the new line instance.
     *
     * @param view view instance the collection belongs to
     * @param collectionGroup collection group the layout manager applies to
     * @param model Object containing the view data, should extend UifFormBase
     * if using framework managed new lines
     */
    protected void buildAddLine(View view, Object model, CollectionGroup collectionGroup) {
        // initialize new line if one does not already exist
        initializeNewCollectionLine(view, model, collectionGroup, false);

        String addLineBindingPath = collectionGroup.getAddLineBindingInfo().getBindingPath();
        List<? extends Component> actionComponents = getAddLineActionComponents(view, model, collectionGroup);

        Object addLine = ObjectPropertyUtils.getPropertyValue(model, addLineBindingPath);

        boolean bindToForm = false;
        if (StringUtils.isBlank(collectionGroup.getAddLinePropertyName())) {
            bindToForm = true;
        }

        LineBuilderContext lineBuilderContext = new LineBuilderContext(-1, addLine, addLineBindingPath, bindToForm,
                (ViewModel) model, collectionGroup, actionComponents);

        getCollectionGroupLineBuilder(lineBuilderContext).buildLine();
    }

    /**
     * Creates new {@code Action} instances for the line.
     *
     * <p>Adds context to the action fields for the given line so that the line the action was performed on can be
     * determined when that action is selected</p>
     *
     * @param lineActions the actions to copy
     * @param view view instance the collection belongs to
     * @param collectionGroup collection group component for the collection
     * @param collectionLine object instance for the current line
     * @param lineIndex index of the line the actions should apply to
     */
    protected List<? extends Component> initializeLineActions(List<? extends Component> lineActions, View view,
            CollectionGroup collectionGroup, Object collectionLine, int lineIndex) {
        List<Component> actionComponents = new ArrayList<Component>(ComponentUtils.copy(lineActions));

        // if it is edit with dialog, then add the edit line action to the group's line actions
        if (collectionGroup.isEditWithDialog()) {
            Action editLineActionForDialog = setupEditLineActionForDialog(collectionGroup,
                    UifConstants.IdSuffixes.LINE + Integer.toString(lineIndex), lineIndex,
                    actionComponents.size());
            actionComponents.add(editLineActionForDialog);
        }

        for (Component actionComponent : actionComponents) {
            view.getViewHelperService().setElementContext(actionComponent, collectionGroup);
        }

        String lineSuffix = UifConstants.IdSuffixes.LINE + Integer.toString(lineIndex);
        ContextUtils.updateContextsForLine(actionComponents, collectionGroup, collectionLine, lineIndex, lineSuffix);

        ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();
        for (Component actionComponent : actionComponents) {
            expressionEvaluator.evaluatePropertyExpression(view, actionComponent.getContext(), actionComponent,
                    UifPropertyPaths.ID, true);
        }

        ComponentUtils.updateIdsWithSuffixNested(actionComponents, lineSuffix);

        List<Action> actions = ViewLifecycleUtils.getElementsOfTypeDeep(actionComponents, Action.class);
        initializeActions(actions, collectionGroup, lineIndex);

        return actionComponents;
    }

    /**
     * Helper method to setup the edit line action to show the dialog
     *
     * @param collectionGroup the collection group the line belongs to
     * @param lineSuffix the line index of the current line
     * @param lineIndex the current line index
     * @param actionIndex the action index used in the id
     * @return the line action for edit line in dialog
     */
    protected Action setupEditLineActionForDialog(CollectionGroup collectionGroup, String lineSuffix, int lineIndex,
            int actionIndex) {

        Action action = ComponentUtils.copy(collectionGroup.getEditWithDialogActionPrototype());

        action.setId(ComponentFactory.EDIT_LINE_IN_DIALOG_ACTION + "_" + collectionGroup.getId() +
                lineSuffix + UifConstants.IdSuffixes.ACTION + actionIndex);

        String actionScript = UifConstants.JsFunctions.SHOW_EDIT_LINE_DIALOG + "('" +
                ComponentFactory.EDIT_LINE_DIALOG + "_" + collectionGroup.getId() + lineSuffix + "', '" +
                collectionGroup.getBindingInfo().getBindingName() + "', " + lineIndex + ");";
        action.setActionScript(actionScript);

        return action;
    }

    /**
     * Updates the action parameters, jump to, refresh id, and validation configuration for the list of actions
     * associated with the given collection group and line index.
     *
     * @param actions list of action components to update
     * @param collectionGroup collection group instance the actions belong to
     * @param lineIndex index of the line the actions are associate with
     */
    public void initializeActions(List<Action> actions, CollectionGroup collectionGroup, int lineIndex) {
        for (Action action : actions) {
            if (ComponentUtils.containsPropertyExpression(action, UifPropertyPaths.ACTION_PARAMETERS, true)) {
                // need to update the actions expressions so our settings do not get overridden
                action.getPropertyExpressions().put(
                        UifPropertyPaths.ACTION_PARAMETERS + "['" + UifParameters.SELECTED_COLLECTION_PATH + "']",
                        UifConstants.EL_PLACEHOLDER_PREFIX + "'" + collectionGroup.getBindingInfo().getBindingPath() +
                                "'" + UifConstants.EL_PLACEHOLDER_SUFFIX
                );
                action.getPropertyExpressions().put(
                        UifPropertyPaths.ACTION_PARAMETERS + "['" + UifParameters.SELECTED_COLLECTION_ID + "']",
                        UifConstants.EL_PLACEHOLDER_PREFIX + "'" + collectionGroup.getId() +
                                "'" + UifConstants.EL_PLACEHOLDER_SUFFIX
                );
                action.getPropertyExpressions().put(
                        UifPropertyPaths.ACTION_PARAMETERS + "['" + UifParameters.SELECTED_LINE_INDEX + "']",
                        UifConstants.EL_PLACEHOLDER_PREFIX + "'" + Integer.toString(lineIndex) +
                                "'" + UifConstants.EL_PLACEHOLDER_SUFFIX
                );
                action.getPropertyExpressions().put(
                        UifPropertyPaths.ACTION_PARAMETERS + "['" + UifParameters.LINE_INDEX + "']",
                        UifConstants.EL_PLACEHOLDER_PREFIX + "'" + Integer.toString(lineIndex) +
                                "'" + UifConstants.EL_PLACEHOLDER_SUFFIX
                );
            } else {
                action.addActionParameter(UifParameters.SELECTED_COLLECTION_PATH,
                        collectionGroup.getBindingInfo().getBindingPath());
                action.addActionParameter(UifParameters.SELECTED_COLLECTION_ID, collectionGroup.getId());
                action.addActionParameter(UifParameters.SELECTED_LINE_INDEX, Integer.toString(lineIndex));
                action.addActionParameter(UifParameters.LINE_INDEX, Integer.toString(lineIndex));
            }

            if (StringUtils.isBlank(action.getRefreshId()) && StringUtils.isBlank(action.getRefreshPropertyName())) {
                action.setRefreshId(collectionGroup.getId());
            }

            // if marked for validation, add call to validate the line and set validation flag to false
            // so the entire form will not be validated
            if (action.isPerformClientSideValidation()) {
                String preSubmitScript = "var valid=" + UifConstants.JsFunctions.VALIDATE_LINE + "('" +
                        collectionGroup.getBindingInfo().getBindingPath() + "'," + Integer.toString(lineIndex) +
                        ");";

                // prepend custom presubmit script which should evaluate to a boolean
                if (StringUtils.isNotBlank(action.getPreSubmitCall())) {
                    preSubmitScript = ScriptUtils.appendScript(preSubmitScript,
                            "if (valid){valid=function(){" + action.getPreSubmitCall() + "}();}");
                }

                preSubmitScript += " return valid;";

                action.setPreSubmitCall(preSubmitScript);
                action.setPerformClientSideValidation(false);
            }
        }
    }

    /**
     * Creates new {@code Component} instances for the add line
     *
     * <p>
     * Adds context to the action fields for the add line so that the collection
     * the action was performed on can be determined
     * </p>
     *
     * @param view view instance the collection belongs to
     * @param model top level object containing the data
     * @param collectionGroup collection group component for the collection
     */
    protected List<? extends Component> getAddLineActionComponents(View view, Object model,
            CollectionGroup collectionGroup) {
        String lineSuffix = UifConstants.IdSuffixes.ADD_LINE;

        List<? extends Component> lineActionComponents = ComponentUtils.copyComponentList(
                collectionGroup.getAddLineActions(), lineSuffix);

        List<Action> actions = ViewLifecycleUtils.getElementsOfTypeDeep(lineActionComponents, Action.class);

        if (collectionGroup.isAddWithDialog() && (collectionGroup.getAddLineDialog().getFooter() != null) &&
                !collectionGroup.getAddLineDialog().getFooter().getItems().isEmpty()) {
            List<Action> addLineDialogActions = ViewLifecycleUtils.getElementsOfTypeDeep(
                    collectionGroup.getAddLineDialog().getFooter().getItems(), Action.class);

            if (addLineDialogActions != null) {
                actions.addAll(addLineDialogActions);
            }
        }

        for (Action action : actions) {
            action.addActionParameter(UifParameters.SELECTED_COLLECTION_PATH,
                    collectionGroup.getBindingInfo().getBindingPath());
            action.addActionParameter(UifParameters.SELECTED_COLLECTION_ID, collectionGroup.getId());
            action.setJumpToIdAfterSubmit(collectionGroup.getId());
            action.addActionParameter(UifParameters.ACTION_TYPE, UifParameters.ADD_LINE);

            boolean isPageUpdateAction = StringUtils.isNotBlank(action.getAjaxReturnType())
                    && action.getAjaxReturnType().equals(UifConstants.AjaxReturnTypes.UPDATEPAGE.getKey());

            if (StringUtils.isBlank(action.getRefreshId()) && !isPageUpdateAction) {
                action.setRefreshId(collectionGroup.getId());
            }

            if (collectionGroup.isAddWithDialog() && view instanceof FormView && ((FormView) view)
                    .isValidateClientSide()) {
                action.setPerformClientSideValidation(true);
            }

            if (action.isPerformClientSideValidation()) {
                String preSubmitScript = "var valid=" + UifConstants.JsFunctions.VALIDATE_ADD_LINE + "('" +
                        collectionGroup.getId() + "');";

                // prepend custom presubmit script which should evaluate to a boolean
                if (StringUtils.isNotBlank(action.getPreSubmitCall())) {
                    preSubmitScript = ScriptUtils.appendScript(preSubmitScript,
                            "if (valid){valid=function(){" + action.getPreSubmitCall() + "}();}");
                }

                preSubmitScript += "return valid;";

                action.setPreSubmitCall(preSubmitScript);
                action.setPerformClientSideValidation(false);
            } else if (collectionGroup.isAddWithDialog()) {
                action.setPreSubmitCall("closeLightbox(); return true;");
            }
        }

        // get add line for context
        String addLinePath = collectionGroup.getAddLineBindingInfo().getBindingPath();
        Object addLine = ObjectPropertyUtils.getPropertyValue(model, addLinePath);

        ContextUtils.updateContextForLine(collectionGroup.getAddLineDialog(), collectionGroup, addLine, -1, lineSuffix);
        ContextUtils.updateContextsForLine(actions, collectionGroup, addLine, -1, lineSuffix);

        return lineActionComponents;
    }

    /**
     * Initializes a new instance of the collection data object class for the add line.
     *
     * <p>If the add line property was not specified for the collection group the new lines will be
     * added to the generic map on the {@code UifFormBase}, else it will be added to the property given by
     * the addLineBindingInfo</p>
     *
     * <p>New line will only be created if the current line property is null or clearExistingLine is true.
     * In the case of a new line default values are also applied</p>
     */
    public void initializeNewCollectionLine(View view, Object model, CollectionGroup collectionGroup,
            boolean clearExistingLine) {
        Object newLine = null;

        // determine if we are binding to generic form map or a custom property
        if (StringUtils.isBlank(collectionGroup.getAddLinePropertyName())) {
            // bind to form map
            if (!(model instanceof UifFormBase)) {
                throw new RuntimeException(
                        "Cannot create new collection line for group: " + collectionGroup.getPropertyName()
                                + ". Model does not extend " + UifFormBase.class.getName()
                );
            }

            // get new collection line map from form
            Map<String, Object> newCollectionLines = ObjectPropertyUtils.getPropertyValue(model,
                    UifPropertyPaths.NEW_COLLECTION_LINES);
            if (newCollectionLines == null) {
                newCollectionLines = new HashMap<String, Object>();
                ObjectPropertyUtils.setPropertyValue(model, UifPropertyPaths.NEW_COLLECTION_LINES, newCollectionLines);
            }

            // set binding path for add line
            String newCollectionLineKey = KRADUtils.translateToMapSafeKey(
                    collectionGroup.getBindingInfo().getBindingPath());
            String addLineBindingPath = UifPropertyPaths.NEW_COLLECTION_LINES + "['" + newCollectionLineKey + "']";
            collectionGroup.getAddLineBindingInfo().setBindingPath(addLineBindingPath);

            // if there is not an instance available or we need to clear create a new instance
            if (!newCollectionLines.containsKey(newCollectionLineKey) || (newCollectionLines.get(newCollectionLineKey)
                    == null) || clearExistingLine) {
                // create new instance of the collection type for the add line
                newLine = KRADUtils.createNewObjectFromClass(collectionGroup.getCollectionObjectClass());
                newCollectionLines.put(newCollectionLineKey, newLine);
            }
        } else {
            // bind to custom property
            Object addLine = ObjectPropertyUtils.getPropertyValue(model,
                    collectionGroup.getAddLineBindingInfo().getBindingPath());
            if ((addLine == null) || clearExistingLine) {
                newLine = KRADUtils.createNewObjectFromClass(collectionGroup.getCollectionObjectClass());
                ObjectPropertyUtils.setPropertyValue(model, collectionGroup.getAddLineBindingInfo().getBindingPath(),
                        newLine);
            }
        }

        // apply default values if a new line was created
        if (newLine != null) {
            ViewLifecycle.getHelper().applyDefaultValuesForCollectionLine(collectionGroup, newLine);
        }
    }

    /**
     * Helper method that checks if this is a refresh lifecycle and if the component to be refreshed is the
     * dialog group, and if the action parameters bind to the same object as the collection's current line, and
     * if they are then it returns true.
     *
     * @param dialogGroup the dialog group to check for
     * @param model the form data
     * @param collectionGroup the collection group the line belongs to
     * @param lineIndex the current line index
     * @return
     */
    public boolean refreshEditLineDialogContents(DialogGroup dialogGroup, Object model, CollectionGroup collectionGroup,
            int lineIndex) {
        UifFormBase formBase = (UifFormBase) model;
        String selectedCollectionPath = formBase.getActionParamaterValue(UifParameters.SELECTED_COLLECTION_PATH);
        String selectedLineIndex = formBase.getActionParamaterValue(UifParameters.SELECTED_LINE_INDEX);

        if (ViewLifecycle.isRefreshLifecycle()
                && StringUtils.equals(dialogGroup.getId(), ViewLifecycle.getRefreshComponentId())
                && (StringUtils.equals(selectedCollectionPath, collectionGroup.getBindingInfo().getBindingPath())
                        || StringUtils.startsWith(selectedCollectionPath, UifPropertyPaths.DIALOG_DATA_OBJECT))
                && StringUtils.equals(selectedLineIndex, Integer.toString(lineIndex))) {
            return true;
        }
        return false;
    }

    /**
     * Returns an instance of {@link CollectionGroupLineBuilder} for building the line.
     *
     * @param lineBuilderContext context of line for initializing line builder
     * @return CollectionGroupLineBuilder instance
     */
    public CollectionGroupLineBuilder getCollectionGroupLineBuilder(LineBuilderContext lineBuilderContext) {
        return new CollectionGroupLineBuilder(lineBuilderContext);
    }

    /**
     * Wrapper object to enable filtering of a collection while preserving original indices
     */
    private static class IndexedElement {

        /**
         * The index associated with the given element
         */
        final int index;

        /**
         * The element itself
         */
        final Object element;

        /**
         * Constructs an {@link org.kuali.rice.krad.uif.container.CollectionGroupBuilder.IndexedElement}
         *
         * @param index the index to associate with the element
         * @param element the element itself
         */
        private IndexedElement(int index, Object element) {
            this.index = index;
            this.element = element;
        }
    }
}
