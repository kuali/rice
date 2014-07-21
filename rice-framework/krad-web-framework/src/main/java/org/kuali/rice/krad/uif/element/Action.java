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
package org.kuali.rice.krad.uif.element;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.container.DialogGroup;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.util.UrlInfo;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.FormView;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Field that presents an action that can be taken on the UI such as submitting the form or invoking a script.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "action", parent = "Uif-Action"),
        @BeanTag(name = "actionImage", parent = "Uif-ActionImage"),
        @BeanTag(name = "button", parent = "Uif-PrimaryActionButton"),
        @BeanTag(name = "secondaryButton", parent = "Uif-SecondaryActionButton"),
        @BeanTag(name = "buttonLarge", parent = "Uif-PrimaryActionButton-Large"),
        @BeanTag(name = "secondaryButtonLarge", parent = "Uif-SecondaryActionButton-Large"),
        @BeanTag(name = "buttonSmall", parent = "Uif-PrimaryActionButton-Small"),
        @BeanTag(name = "secondaryButtonSmall", parent = "Uif-SecondaryActionButton-Small"),
        @BeanTag(name = "buttonMini", parent = "Uif-PrimaryActionButton-Mini"),
        @BeanTag(name = "secondaryButtonMini", parent = "Uif-SecondaryActionButton-Mini"),
        @BeanTag(name = "actionLink", parent = "Uif-ActionLink"),
        @BeanTag(name = "navigationActionLink", parent = "Uif-NavigationActionLink"),
        @BeanTag(name = "navigationButton", parent = "Uif-NavigationActionButton"),
        @BeanTag(name = "secondaryNavigationActionButton", parent = "Uif-SecondaryNavigationActionButton")})
public class Action extends ContentElementBase {
    private static final long serialVersionUID = 1025672792657238829L;

    private String methodToCall;
    private String actionEvent;
    private String navigateToPageId;
    private List<String> fieldsToSend;

    private String actionScript;
    private UrlInfo actionUrl;

    private String actionLabel;
    private boolean renderInnerTextSpan;

    private Image actionImage;
    private String actionImagePlacement;

    private String iconClass;
    private String actionIconPlacement;

    private String jumpToIdAfterSubmit;
    private String jumpToNameAfterSubmit;
    private String focusOnIdAfterSubmit;

    private boolean performClientSideValidation;
    private boolean performDirtyValidation;
    private boolean clearDirtyOnAction;
    private boolean dirtyOnAction;

    private String preSubmitCall;
    private String confirmationPromptText;
    private DialogGroup confirmationDialog;

    private String dialogDismissOption;
    private String dialogResponse;

    private boolean ajaxSubmit;
    private String ajaxReturnType;
    private String refreshId;
    private String refreshPropertyName;

    private String successCallback;
    private String errorCallback;

    private String loadingMessageText;
    private boolean disableBlocking;

    private Map<String, String> additionalSubmitData;
    private Map<String, String> actionParameters;

    private boolean evaluateDisabledOnKeyUp;

    private boolean defaultEnterKeyAction;

    private boolean disabled;
    private String disabledReason;
    private String disabledExpression;
    private String disabledConditionJs;
    private List<String> disabledConditionControlNames;

    private List<String> disabledWhenChangedPropertyNames;
    private List<String> enabledWhenChangedPropertyNames;

    /**
     * Sets initial field values and initializes collections.
     */
    public Action() {
        super();

        actionImagePlacement = UifConstants.Position.LEFT.name();
        actionIconPlacement = UifConstants.Position.LEFT.name();

        ajaxSubmit = true;

        successCallback = "";
        errorCallback = "";
        preSubmitCall = "";

        additionalSubmitData = new HashMap<String, String>();
        actionParameters = new HashMap<String, String>();

        disabled = false;
        disabledWhenChangedPropertyNames = new ArrayList<String>();
        enabledWhenChangedPropertyNames = new ArrayList<String>();
    }

    /**
     * Sets the disabledExpression, if any, evaluates it and sets the disabled property.
     *
     * @param model top level object containing the data (could be the form or a
     * @param parent parent component
     */
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        disabledExpression = this.getPropertyExpression("disabled");
        if (disabledExpression != null) {
            ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();

            disabledExpression = expressionEvaluator.replaceBindingPrefixes(ViewLifecycle.getView(), this,
                    disabledExpression);
            disabled = (Boolean) expressionEvaluator.evaluateExpression(this.getContext(), disabledExpression);
        }

        if (actionUrl != null) {
            ViewLifecycle.getExpressionEvaluator().populatePropertyExpressionsFromGraph(actionUrl, false);
            ViewLifecycle.getExpressionEvaluator().evaluateExpressionsOnConfigurable(ViewLifecycle.getView(),
                    actionUrl, this.getContext());
        }

        if (StringUtils.isNotBlank(confirmationPromptText) && (confirmationDialog != null) && StringUtils.isBlank(
                confirmationDialog.getPromptText())) {
            confirmationDialog.setPromptText(confirmationPromptText);
        }

        addConfirmDialogToView();
    }

    /**
     * For confirm text without a dialog, add instance of yes no dialog to view so it is already available
     * on the client for dynamic dialog creation.
     */
    protected void addConfirmDialogToView() {
        if (StringUtils.isBlank(confirmationPromptText) || (confirmationDialog != null)) {
            return;
        }

        boolean containsYesNoDialog = false;

        List<Group> viewDialogs = ViewLifecycle.getView().getDialogs();
        if (viewDialogs == null) {
            viewDialogs = new ArrayList<Group>();
        } else {
            for (Group dialogGroup : viewDialogs) {
                if (StringUtils.equals(ComponentFactory.YES_NO_DIALOG, dialogGroup.getId())) {
                    containsYesNoDialog = true;
                }
            }
        }

        if (!containsYesNoDialog) {
            Group confirmDialog = ComponentFactory.getYesNoDialog();
            confirmDialog.setId(ComponentFactory.YES_NO_DIALOG);

            viewDialogs.add(confirmDialog);
        }
    }

    /**
     * The following finalization is performed:
     *
     * <ul>
     * <li>Add methodToCall action parameter if set and setup event code for
     * setting action parameters</li>
     * <li>Invoke method to build the data attributes and submit data for the action</li>
     * <li>Compose the final onclick script for the action</li>
     * <li>Parses the disabled expressions, if any, to equivalent javascript and evaluates the disable/enable when
     * changed property names</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        View view = ViewLifecycle.getView();
        ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();

        if (StringUtils.isNotEmpty(disabledExpression)
                && !disabledExpression.equalsIgnoreCase("true")
                && !disabledExpression.equalsIgnoreCase("false")) {
            disabledConditionControlNames = new ArrayList<String>();
            disabledConditionJs = ViewLifecycle.getExpressionEvaluator().parseExpression(disabledExpression,
                    disabledConditionControlNames, this.getContext());
        }

        List<String> adjustedDisablePropertyNames = new ArrayList<String>();
        for (String propertyName : disabledWhenChangedPropertyNames) {
            adjustedDisablePropertyNames.add(expressionEvaluator.replaceBindingPrefixes(view, this, propertyName));
        }
        disabledWhenChangedPropertyNames = adjustedDisablePropertyNames;

        List<String> adjustedEnablePropertyNames = new ArrayList<String>();
        for (String propertyName : enabledWhenChangedPropertyNames) {
            adjustedEnablePropertyNames.add(expressionEvaluator.replaceBindingPrefixes(view, this, propertyName));
        }
        enabledWhenChangedPropertyNames = adjustedEnablePropertyNames;

        // clear alt text to avoid screen reader confusion when using image in button with text
        if (actionImage != null && StringUtils.isNotBlank(actionImagePlacement) && StringUtils.isNotBlank(actionLabel)) {
            actionImage.setAltText("");
        }

        // when icon only is set, add the icon class to the action
        if (StringUtils.isNotBlank(iconClass) && (UifConstants.ICON_ONLY_PLACEMENT.equals(actionIconPlacement)
                || StringUtils.isBlank(actionLabel))) {
            getCssClasses().add(iconClass);

            // force icon only placement
            actionIconPlacement = UifConstants.ICON_ONLY_PLACEMENT;
        }

        if (!actionParameters.containsKey(UifConstants.UrlParams.ACTION_EVENT) && StringUtils.isNotBlank(actionEvent)) {
            actionParameters.put(UifConstants.UrlParams.ACTION_EVENT, actionEvent);
        }

        if (StringUtils.isNotBlank(navigateToPageId)) {
            actionParameters.put(UifParameters.NAVIGATE_TO_PAGE_ID, navigateToPageId);
            if (StringUtils.isBlank(methodToCall)) {
                this.methodToCall = UifConstants.MethodToCallNames.NAVIGATE;
            }
        }

        if (!actionParameters.containsKey(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME) && StringUtils
                .isNotBlank(methodToCall)) {
            actionParameters.put(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME, methodToCall);
        }

        setupRefreshAction(view);

        // Apply dirty check if it is enabled for the view and the action requires it
        if (view instanceof FormView) {
            performDirtyValidation = performDirtyValidation && ((FormView) view).isApplyDirtyCheck();
        }

        if (StringUtils.isBlank(getActionScript()) && (actionUrl != null) && actionUrl.isFullyConfigured()) {
            String actionScript = ScriptUtils.buildFunctionCall(UifConstants.JsFunctions.REDIRECT, actionUrl.getHref());
            setActionScript(actionScript);
        }

        // add the method to call as an available method
        if (StringUtils.isNotBlank(methodToCall)) {
            ViewLifecycle.getViewPostMetadata().addAvailableMethodToCall(methodToCall);
        }

        // add additional submit data as accessible binding paths, and method to call as accessible method
        if (isRender()) {
            for (String additionalSubmitPath : additionalSubmitData.keySet()) {
                ViewLifecycle.getViewPostMetadata().addAccessibleBindingPath(additionalSubmitPath);
            }

            if (StringUtils.isNotBlank(methodToCall)) {
                ViewLifecycle.getViewPostMetadata().addAccessibleMethodToCall(methodToCall);
            }
        }

        buildActionData(view, model, parent);
    }

    /**
     * When the action is updating a component sets up the refresh script for the component (found by the
     * given refresh id or refresh property name.
     *
     * @param view view instance the action belongs to
     */
    protected void setupRefreshAction(View view) {
        // if refresh property or id is given, make return type update component
        // TODO: what if the refresh id is the page id? we should set the return type as update page
        if (StringUtils.isNotBlank(refreshPropertyName) || StringUtils.isNotBlank(refreshId)) {
            ajaxReturnType = UifConstants.AjaxReturnTypes.UPDATECOMPONENT.getKey();
        }

        // if refresh property name is given, adjust the binding and then attempt to find the
        // component in the view index
        Component refreshComponent = null;
        if (StringUtils.isNotBlank(refreshPropertyName)) {
            // TODO: does this support all binding prefixes?
            if (refreshPropertyName.startsWith(UifConstants.NO_BIND_ADJUST_PREFIX)) {
                refreshPropertyName = StringUtils.removeStart(refreshPropertyName, UifConstants.NO_BIND_ADJUST_PREFIX);
            } else if (StringUtils.isNotBlank(view.getDefaultBindingObjectPath())) {
                refreshPropertyName = view.getDefaultBindingObjectPath() + "." + refreshPropertyName;
            }

            DataField dataField = view.getViewIndex().getDataFieldByPath(refreshPropertyName);
            if (dataField != null) {
                refreshComponent = dataField;
                refreshId = refreshComponent.getId();
            }
        } else if (StringUtils.isNotBlank(refreshId)) {
            Component component = view.getViewIndex().getComponentById(refreshId);
            if (component != null) {
                refreshComponent = component;
            }
        }

        if (refreshComponent != null) {
            refreshComponent.setRefreshedByAction(true);
        }
    }

    /**
     * Builds the data attributes that will be read client side to determine how to
     * handle the action and the additional data that should be submitted with the action
     *
     * <p>
     * Note these data attributes will be exposed as a data map client side. The simple attributes (non object
     * value) are also written out as attributes on the action element.
     * </p>
     *
     * @param view view instance the action belongs to
     * @param model model object containing the view data
     * @param parent component the holds the action
     */
    protected void buildActionData(View view, Object model, LifecycleElement parent) {
        HashMap<String, String> actionDataAttributes = new HashMap<String, String>();

        Map<String, String> dataDefaults =
                (Map<String, String>) (KRADServiceLocatorWeb.getDataDictionaryService().getDictionaryBean(
                        UifConstants.ACTION_DEFAULTS_MAP_ID));

        // map properties to data attributes
        addActionDataSettingsValue(actionDataAttributes, dataDefaults, UifConstants.ActionDataAttributes.AJAX_SUBMIT,
                Boolean.toString(ajaxSubmit));
        addActionDataSettingsValue(actionDataAttributes, dataDefaults,
                UifConstants.ActionDataAttributes.SUCCESS_CALLBACK, this.successCallback);
        addActionDataSettingsValue(actionDataAttributes, dataDefaults, UifConstants.ActionDataAttributes.ERROR_CALLBACK,
                this.errorCallback);
        addActionDataSettingsValue(actionDataAttributes, dataDefaults,
                UifConstants.ActionDataAttributes.PRE_SUBMIT_CALL, this.preSubmitCall);
        addActionDataSettingsValue(actionDataAttributes, dataDefaults,
                UifConstants.ActionDataAttributes.LOADING_MESSAGE, this.loadingMessageText);
        addActionDataSettingsValue(actionDataAttributes, dataDefaults,
                UifConstants.ActionDataAttributes.DISABLE_BLOCKING, Boolean.toString(this.disableBlocking));
        addActionDataSettingsValue(actionDataAttributes, dataDefaults,
                UifConstants.ActionDataAttributes.AJAX_RETURN_TYPE, this.ajaxReturnType);
        addActionDataSettingsValue(actionDataAttributes, dataDefaults, UifConstants.ActionDataAttributes.REFRESH_ID,
                this.refreshId);
        addActionDataSettingsValue(actionDataAttributes, dataDefaults, UifConstants.ActionDataAttributes.VALIDATE,
                Boolean.toString(this.performClientSideValidation));
        addActionDataSettingsValue(actionDataAttributes, dataDefaults,
                UifConstants.ActionDataAttributes.DIRTY_ON_ACTION, Boolean.toString(this.dirtyOnAction));
        addActionDataSettingsValue(actionDataAttributes, dataDefaults, UifConstants.ActionDataAttributes.CLEAR_DIRTY,
                Boolean.toString(this.clearDirtyOnAction));
        addActionDataSettingsValue(actionDataAttributes, dataDefaults,
                UifConstants.ActionDataAttributes.PERFORM_DIRTY_VALIDATION, Boolean.toString(
                this.performDirtyValidation));
        addActionDataSettingsValue(actionDataAttributes, dataDefaults, UifConstants.ActionDataAttributes.FIELDS_TO_SEND,
                ScriptUtils.translateValue(this.fieldsToSend));

        if (confirmationDialog != null) {
            addDataAttribute(UifConstants.ActionDataAttributes.CONFIRM_DIALOG_ID, confirmationDialog.getId());
        } else if (StringUtils.isNotBlank(confirmationPromptText)) {
            addDataAttribute(UifConstants.ActionDataAttributes.CONFIRM_PROMPT_TEXT, confirmationPromptText);
        }

        if (StringUtils.isNotBlank(dialogDismissOption)) {
            addDataAttribute(UifConstants.DataAttributes.DISMISS_DIALOG_OPTION, dialogDismissOption);
        }

        if (StringUtils.isNotBlank(dialogResponse)) {
            addDataAttribute(UifConstants.DataAttributes.DISMISS_RESPONSE, dialogResponse);
        }

        // all action parameters should be submitted with action
        Map<String, String> submitData = new HashMap<String, String>();
        for (String key : actionParameters.keySet()) {
            String parameterPath = key;
            if (!key.equals(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME)) {
                parameterPath = UifPropertyPaths.ACTION_PARAMETERS + "[" + key + "]";
            }
            submitData.put(parameterPath, actionParameters.get(key));
        }

        for (String key : additionalSubmitData.keySet()) {
            submitData.put(key, additionalSubmitData.get(key));
        }

        // if focus id not set default to focus on action
        if (focusOnIdAfterSubmit.equalsIgnoreCase(UifConstants.Order.NEXT_INPUT.toString())) {
            focusOnIdAfterSubmit = UifConstants.Order.NEXT_INPUT.toString() + ":" + this.getId();
        }

        addActionDataSettingsValue(actionDataAttributes, dataDefaults, UifConstants.ActionDataAttributes.FOCUS_ID,
                focusOnIdAfterSubmit);

        if (StringUtils.isNotBlank(jumpToIdAfterSubmit)) {
            addActionDataSettingsValue(actionDataAttributes, dataDefaults, UifConstants.ActionDataAttributes.JUMP_TO_ID,
                    jumpToIdAfterSubmit);
        } else if (StringUtils.isNotBlank(jumpToNameAfterSubmit)) {
            addActionDataSettingsValue(actionDataAttributes, dataDefaults,
                    UifConstants.ActionDataAttributes.JUMP_TO_NAME, jumpToNameAfterSubmit);
        }

        addActionDataSettingsValue(actionDataAttributes, dataDefaults, UifConstants.DataAttributes.SUBMIT_DATA,
                ScriptUtils.toJSON(submitData));

        // build final onclick script
        String onClickScript = this.getOnClickScript();
        if (StringUtils.isNotBlank(actionScript)) {
            onClickScript = ScriptUtils.appendScript(onClickScript, actionScript);
        } else {
            onClickScript = ScriptUtils.appendScript(onClickScript, "actionInvokeHandler(this);");
        }

        //stop action if the action is disabled
        if (disabled) {
            this.addStyleClass("disabled");
            this.setSkipInTabOrder(true);
        }

        // on click script becomes a data attribute for use in a global handler on the client
        addActionDataSettingsValue(actionDataAttributes, dataDefaults, UifConstants.DataAttributes.ONCLICK,
                KRADUtils.convertToHTMLAttributeSafeString(onClickScript));

        if (!actionDataAttributes.isEmpty()) {
            this.getDataAttributes().putAll(actionDataAttributes);
        }

        this.addDataAttribute(UifConstants.DataAttributes.ROLE, UifConstants.RoleTypes.ACTION);

        // add data attribute if this is the primary action
        if (this.isDefaultEnterKeyAction()) {
            this.addDataAttribute(UifConstants.DataAttributes.DEFAULT_ENTER_KEY_ACTION,
                    Boolean.toString(this.isDefaultEnterKeyAction()));
        }
    }

    /**
     * Adds the value passed to the valueMap with the key specified, if the value does not match the
     * value which already exists in defaults (to avoid having to write out extra data that can later
     * be derived from the defaults in the js client-side).
     *
     * @param valueMap the data map being constructed
     * @param defaults defaults for validation messages
     * @param key the variable name being added
     * @param value the value set on this object as a String equivalent
     */
    protected void addActionDataSettingsValue(Map<String, String> valueMap, Map<String, String> defaults, String key,
            String value) {
        if (StringUtils.isBlank(value)) {
            return;
        }

        String defaultValue = defaults.get(key);
        if (defaultValue == null || !value.equals(defaultValue)) {
            valueMap.put(key, value);
        }
    }

    /**
     * Name of the method that should be called when the action is selected
     *
     * <p>
     * For a server side call (clientSideCall is false), gives the name of the
     * method in the mapped controller that should be invoked when the action is
     * selected. For client side calls gives the name of the script function
     * that should be invoked when the action is selected
     * </p>
     *
     * @return name of method to call
     */
    @BeanTagAttribute
    public String getMethodToCall() {
        return this.methodToCall;
    }

    /**
     * Setter for the actions method to call.
     *
     * @param methodToCall method to call
     */
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

    /**
     * Label text for the action
     *
     * <p>
     * The label text is used by the template renderers to give a human readable
     * label for the action. For buttons this generally is the button text,
     * while for an action link it would be the links displayed text
     * </p>
     *
     * @return label for action
     */
    @BeanTagAttribute
    public String getActionLabel() {
        return this.actionLabel;
    }

    /**
     * Setter for the actions label.
     *
     * @param actionLabel action label
     */
    public void setActionLabel(String actionLabel) {
        this.actionLabel = actionLabel;
    }

    /**
     * When true, a span will be rendered around the actionLabel text.
     *
     * @return true if rendering a span around actionLabel, false otherwise
     */
    @BeanTagAttribute
    public boolean isRenderInnerTextSpan() {
        return renderInnerTextSpan;
    }

    /**
     * Setter for {@link org.kuali.rice.krad.uif.element.Action#isRenderInnerTextSpan()}.
     *
     * @param renderInnerTextSpan property value
     */
    public void setRenderInnerTextSpan(boolean renderInnerTextSpan) {
        this.renderInnerTextSpan = renderInnerTextSpan;
    }

    /**
     * Image to use for the action
     *
     * <p>
     * When the action image component is set (and render is true) the image will be
     * used to present the action as opposed to the default (input submit). For
     * action link templates the image is used for the link instead of the
     * action link text
     * </p>
     *
     * @return action image
     */
    @BeanTagAttribute
    public Image getActionImage() {
        return this.actionImage;
    }

    /**
     * Setter for the action image field.
     *
     * @param actionImage action image
     */
    public void setActionImage(Image actionImage) {
        this.actionImage = actionImage;
    }

    /**
     * The css class (some which exist in bootstrap css) to use to render an icon for this action.
     *
     * @return the icon css class
     */
    @BeanTagAttribute
    public String getIconClass() {
        return iconClass;
    }

    /**
     * Setter for {@link org.kuali.rice.krad.uif.element.Action#getIconClass()}.
     *
     * @param iconClass property value
     */
    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    /**
     * For an <code>Action</code> that is part of a
     * <code>NavigationGroup</code>, the navigate to page id can be set to
     * configure the page that should be navigated to when the action is
     * selected.
     *
     * <p>
     * Support exists in the <code>UifControllerBase</code> for handling
     * navigation between pages.
     * </p>
     *
     * @return id of page that should be rendered when the action item is
     *         selected
     */
    @BeanTagAttribute
    public String getNavigateToPageId() {
        return this.navigateToPageId;
    }

    /**
     * Setter for {@link #getNavigateToPageId()}.
     *
     * @param navigateToPageId property value
     */
    public void setNavigateToPageId(String navigateToPageId) {
        this.navigateToPageId = navigateToPageId;
    }

    /**
     * Limits the field data to send on a refresh methodToCall server call to the names/group id/field id
     * specified in this list.
     *
     * <p>The names in the list should be the propertyNames of the fields sent with this request.  A wildcard("*")
     * can be used at the END of a name to specify all fields with names that begin with the string
     * before the wildcard.  If the array contains 1 item with the keyword "NONE", then no form fields are sent.
     * In addition, A group id or field id with the "#" id selector prefix can be used to send all inputs which
     * are nested within them. Note that this only limits the fields which exist on the form and data required
     * by the KRAD framework is still sent (eg, methodToCall, formKey, sessionId, etc.)</p>
     *
     * @return the only input fields to send by name with the action request
     */
    @BeanTagAttribute
    public List<String> getFieldsToSend() {
        return fieldsToSend;
    }

    /**
     * @see Action#fieldsToSend
     */
    public void setFieldsToSend(List<String> fieldsToSend) {
        this.fieldsToSend = fieldsToSend;
    }

    /**
     * Name of the event that will be set when the action is invoked
     *
     * <p>
     * Action events can be looked at by the view or components in order to render differently depending on
     * the action requested.
     * </p>
     *
     * @return action event name
     * @see org.kuali.rice.krad.uif.UifConstants.ActionEvents
     */
    @BeanTagAttribute
    public String getActionEvent() {
        return actionEvent;
    }

    /**
     * Setter for {@link #getActionEvent()}.
     *
     * @param actionEvent property value
     */
    public void setActionEvent(String actionEvent) {
        this.actionEvent = actionEvent;
    }

    /**
     * Map of additional data that will be posted when the action is invoked.
     *
     * <p>
     * Each entry in this map will be sent as a request parameter when the action is chosen. Note this in
     * addition to the form data that is sent. For example, suppose the model contained a property named
     * number and a boolean named showActive, we can send values for this properties by adding the following
     * entries to this map:
     * {'number':'a13', 'showActive', 'true'}
     * </p>
     *
     * <p>
     * The additionalSubmitData map is different from the actionParameters map. All name/value pairs given as
     * actionParameters populated the form map actionParameters. While name/value pair given in additionalSubmitData
     * populate different form (model) properties.
     * </p>
     *
     * @return additional key/value pairs to submit
     */
    @BeanTagAttribute
    public Map<String, String> getAdditionalSubmitData() {
        return additionalSubmitData;
    }

    /**
     * Setter for map holding additional data to post.
     *
     * @param additionalSubmitData property value
     */
    public void setAdditionalSubmitData(Map<String, String> additionalSubmitData) {
        this.additionalSubmitData = additionalSubmitData;
    }

    /**
     * Parameters that should be sent when the action is invoked
     *
     * <p>
     * Action renderer will decide how the parameters are sent for the action
     * (via script generated hiddens, or script parameters, ...)
     * </p>
     *
     * <p>
     * Can be set by other components such as the <code>CollectionGroup</code>
     * to provide the context the action is in (such as the collection name and
     * line the action applies to)
     * </p>
     *
     * @return action parameters
     */
    @BeanTagAttribute
    public Map<String, String> getActionParameters() {
        return this.actionParameters;
    }

    /**
     * Setter for {@link #getActionParameters()}.
     *
     * @param actionParameters property value
     */
    public void setActionParameters(Map<String, String> actionParameters) {
        this.actionParameters = actionParameters;
    }

    /**
     * Convenience method to add a parameter to the action parameters Map.
     *
     * @param parameterName name of parameter to add
     * @param parameterValue value of parameter to add
     */
    public void addActionParameter(String parameterName, String parameterValue) {
        if (actionParameters == null) {
            this.actionParameters = new HashMap<String, String>();
        }

        this.actionParameters.put(parameterName, parameterValue);
    }

    /**
     * Gets an action parameter by name.
     *
     * @param parameterName parameter name
     * @return action parameter
     */
    public String getActionParameter(String parameterName) {
        return this.actionParameters.get(parameterName);
    }

    /**
     * Action Security object that indicates what authorization (permissions) exist for the action.
     *
     * @return ActionSecurity instance
     */
    public ActionSecurity getActionSecurity() {
        return (ActionSecurity) super.getComponentSecurity();
    }

    /**
     * Override to assert a {@link ActionSecurity} instance is set.
     *
     * @param componentSecurity instance of ActionSecurity
     */
    @Override
    public void setComponentSecurity(ComponentSecurity componentSecurity) {
        if ((componentSecurity != null) && !(componentSecurity instanceof ActionSecurity)) {
            throw new RiceRuntimeException("Component security for Action should be instance of ActionSecurity");
        }

        super.setComponentSecurity(componentSecurity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeComponentSecurity() {
        if (getComponentSecurity() == null) {
            setComponentSecurity(KRADUtils.createNewObjectFromClass(ActionSecurity.class));
        }
    }

    /**
     * Indicates whether or not to perform action auth.
     *
     * @return true to perform action auth
     */
    @BeanTagAttribute
    public boolean isPerformActionAuthz() {
        initializeComponentSecurity();

        return getActionSecurity().isPerformActionAuthz();
    }

    /**
     * Setter for {@link #isPerformActionAuthz()}.
     *
     * @param performActionAuthz property value
     */
    public void setPerformActionAuthz(boolean performActionAuthz) {
        initializeComponentSecurity();

        getActionSecurity().setPerformActionAuthz(performActionAuthz);
    }

    /**
     * Indicates whether or not to perform line action auth.
     *
     * @return true to perform line action auth
     */
    @BeanTagAttribute
    public boolean isPerformLineActionAuthz() {
        initializeComponentSecurity();

        return getActionSecurity().isPerformLineActionAuthz();
    }

    /**
     * Setter for {@link #isPerformActionAuthz()}.
     *
     * @param performLineActionAuthz property value
     */
    public void setPerformLineActionAuthz(boolean performLineActionAuthz) {
        initializeComponentSecurity();

        getActionSecurity().setPerformLineActionAuthz(performLineActionAuthz);
    }

    /**
     * Gets the id to jump to after submit.
     *
     * @return the jumpToIdAfterSubmit
     */
    @BeanTagAttribute
    public String getJumpToIdAfterSubmit() {
        return this.jumpToIdAfterSubmit;
    }

    /**
     * The id to jump to in the next page, the element with this id will be
     * jumped to automatically when the new page is retrieved after a submit.
     * Using "TOP" or "BOTTOM" will jump to the top or the bottom of the
     * resulting page. Passing in nothing for both jumpToIdAfterSubmit and
     * jumpToNameAfterSubmit will result in this Action being jumped to by
     * default if it is present on the new page. WARNING: jumpToIdAfterSubmit
     * always takes precedence over jumpToNameAfterSubmit, if set.
     *
     * @param jumpToIdAfterSubmit the jumpToIdAfterSubmit to set
     */
    public void setJumpToIdAfterSubmit(String jumpToIdAfterSubmit) {
        this.jumpToIdAfterSubmit = jumpToIdAfterSubmit;
    }

    /**
     * The name to jump to in the next page, the element with this name will be
     * jumped to automatically when the new page is retrieved after a submit.
     * Passing in nothing for both jumpToIdAfterSubmit and jumpToNameAfterSubmit
     * will result in this Action being jumped to by default if it is
     * present on the new page. WARNING: jumpToIdAfterSubmit always takes
     * precedence over jumpToNameAfterSubmit, if set.
     *
     * @return the jumpToNameAfterSubmit
     */
    @BeanTagAttribute
    public String getJumpToNameAfterSubmit() {
        return this.jumpToNameAfterSubmit;
    }

    /**
     * Setter for {@link #getJumpToIdAfterSubmit()}.
     *
     * @param jumpToNameAfterSubmit the jumpToNameAfterSubmit to set
     */
    public void setJumpToNameAfterSubmit(String jumpToNameAfterSubmit) {
        this.jumpToNameAfterSubmit = jumpToNameAfterSubmit;
    }

    /**
     * The element to place focus on in the new page after the new page
     * is retrieved.
     *
     * <p>The following are allowed:
     * <ul>
     * <li>A valid element id</li>
     * <li>"FIRST" will focus on the first visible input element on the form</li>
     * <li>"SELF" will result in this Action being focused (action bean defaults to "SELF")</li>
     * <li>"LINE_FIRST" will result in the first input of the collection line to be focused (if available)</li>
     * <li>"NEXT_INPUT" will result in the next available input that exists after this Action to be focused
     * (only if this action still exists on the page)</li>
     * </ul>
     * </p>
     *
     * @return the focusOnAfterSubmit
     */
    @BeanTagAttribute
    public String getFocusOnIdAfterSubmit() {
        return this.focusOnIdAfterSubmit;
    }

    /**
     * Setter for {@link #getFocusOnIdAfterSubmit()}.
     *
     * @param focusOnIdAfterSubmit the focusOnAfterSubmit to set
     */
    public void setFocusOnIdAfterSubmit(String focusOnIdAfterSubmit) {
        this.focusOnIdAfterSubmit = focusOnIdAfterSubmit;
    }

    /**
     * Indicates whether the form data should be validated on the client side.
     *
     * @return true if validation should occur, false otherwise
     */
    @BeanTagAttribute
    public boolean isPerformClientSideValidation() {
        return this.performClientSideValidation;
    }

    /**
     * Setter for the client side validation flag.
     *
     * @param performClientSideValidation property value
     */
    public void setPerformClientSideValidation(boolean performClientSideValidation) {
        this.performClientSideValidation = performClientSideValidation;
    }

    /**
     * Client side javascript to be executed when this actionField is clicked.
     *
     * <p>
     * This overrides the default action for this Action so the method
     * called must explicitly submit, navigate, etc. through js, if necessary.
     * In addition, this js occurs AFTER onClickScripts set on this field, it
     * will be the last script executed by the click event. Sidenote: This js is
     * always called after hidden actionParameters and methodToCall methods are
     * written by the js to the html form.
     * </p>
     *
     * @return the actionScript
     */
    @BeanTagAttribute
    public String getActionScript() {
        return this.actionScript;
    }

    /**
     * Setter for {@link #getActionScript()}.
     *
     * @param actionScript the actionScript to set
     */
    public void setActionScript(String actionScript) {
        if (StringUtils.isNotBlank(actionScript) && !StringUtils.endsWith(actionScript, ";")) {
            actionScript = actionScript + ";";
        }

        this.actionScript = actionScript;
    }

    /**
     * Url to open when the action item is selected
     *
     * <p>
     * This makes the action behave like a standard link. Instead of posting the form, the configured URL will
     * simply be opened (using window.open). For using standard post actions these does not need to be configured.
     * </p>
     *
     * @return Url info instance for the configuration action link
     */
    @BeanTagAttribute
    public UrlInfo getActionUrl() {
        return actionUrl;
    }

    /**
     * Setter for {@link #getActionUrl()}.
     *
     * @param actionUrl property value
     */
    public void setActionUrl(UrlInfo actionUrl) {
        this.actionUrl = actionUrl;
    }

    /**
     * Setter for {@link #isPerformDirtyValidation()}.
     *
     * @param performDirtyValidation the blockValidateDirty to set
     */
    public void setPerformDirtyValidation(boolean performDirtyValidation) {
        this.performDirtyValidation = performDirtyValidation;
    }

    /**
     * Indicates whether or not to perform dirty validation.
     *
     * @return true to perform dirty validation
     */
    @BeanTagAttribute
    public boolean isPerformDirtyValidation() {
        return performDirtyValidation;
    }

    /**
     * True to make this action clear the dirty flag before submitting.
     *
     * <p>This will clear both the dirtyForm flag on the form and the count of fields considered dirty on the
     * client-side.  This will only be performed if this action is a request based action.</p>
     *
     * @return true if the dirty
     */
    @BeanTagAttribute
    public boolean isClearDirtyOnAction() {
        return clearDirtyOnAction;
    }

    /**
     * Setter for {@link #isClearDirtyOnAction()}.
     *
     * @param clearDirtyOnAction property value
     */
    public void setClearDirtyOnAction(boolean clearDirtyOnAction) {
        this.clearDirtyOnAction = clearDirtyOnAction;
    }

    /**
     * When true, this action will mark the form dirty by incrementing the dirty field count, but if this action
     * refreshes the entire view this will be lost (most actions only refresh the page)
     *
     * <p>This will increase count of fields considered dirty on the
     * client-side by 1.  This will only be performed if this action is a request based action.</p>
     *
     * @return true if this action is considered dirty, false otherwise
     */
    @BeanTagAttribute
    public boolean isDirtyOnAction() {
        return dirtyOnAction;
    }

    /**
     * Set to true, if this action is considered one that changes the form's data (makes the form dirty).
     *
     * @param dirtyOnAction property value
     */
    public void setDirtyOnAction(boolean dirtyOnAction) {
        this.dirtyOnAction = dirtyOnAction;
    }

    /**
     * Indicates whether the action (input or button) is disabled (doesn't allow interaction).
     *
     * @return true if the action field is disabled, false if not
     */
    @BeanTagAttribute
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Setter for the disabled indicator.
     *
     * @param disabled property value
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * If the action field is disabled, gives a reason for why which will be displayed as a tooltip
     * on the action field (button).
     *
     * @return disabled reason text
     * @see #isDisabled()
     */
    @BeanTagAttribute
    public String getDisabledReason() {
        return disabledReason;
    }

    /**
     * Setter for the disabled reason text.
     *
     * @param disabledReason property value
     */
    public void setDisabledReason(String disabledReason) {
        this.disabledReason = disabledReason;
    }

    /**
     * Gets the action image placement.
     *
     * @return action image placement
     */
    @BeanTagAttribute
    public String getActionImagePlacement() {
        return actionImagePlacement;
    }

    /**
     * Set to TOP, BOTTOM, LEFT, RIGHT to position image at that location within the button.
     * For the subclass ActionLinkField only LEFT and RIGHT are allowed.  When set to blank/null/IMAGE_ONLY, the image
     * itself will be the Action, if no value is set the default is ALWAYS LEFT, you must explicitly set
     * blank/null/IMAGE_ONLY to use ONLY the image as the Action.
     *
     * @param actionImagePlacement action image placement indicator
     */
    public void setActionImagePlacement(String actionImagePlacement) {
        this.actionImagePlacement = actionImagePlacement;
    }

    /**
     * Gets the action icon placement.
     *
     * @return action icon placement
     */
    @BeanTagAttribute
    public String getActionIconPlacement() {
        return actionIconPlacement;
    }

    /**
     * Setter for {@link #getActionIconPlacement()}.
     *
     * @param actionIconPlacement property value
     */
    public void setActionIconPlacement(String actionIconPlacement) {
        this.actionIconPlacement = actionIconPlacement;
    }

    /**
     * Gets the script which needs to be invoked before the form is submitted
     *
     * <p>
     * The preSubmitCall can carry out custom logic for the action before the submit occurs. The value should
     * be given as one or more lines of script and should return a boolean. If false is returned from the call,
     * the submit is not carried out. Furthermore, the preSubmitCall can refer to the request object through the
     * variable 'kradRequest' or 'this'. This gives full access over the request for doing such things as
     * adding additional data
     * </p>
     *
     * <p>
     * Examples 'return doFunction(kradRequest);', 'var valid=true;return valid;'
     * </p>
     *
     * <p>
     * The preSubmit call will be invoked both for ajax and non-ajax submits
     * </p>
     *
     * @return script text that will be invoked before form submission
     */
    @BeanTagAttribute
    public String getPreSubmitCall() {
        return preSubmitCall;
    }

    /**
     * Setter for {@link #getPreSubmitCall()}.
     *
     * @param preSubmitCall property value
     */
    public void setPreSubmitCall(String preSubmitCall) {
        this.preSubmitCall = preSubmitCall;
    }

    /**
     * Text to display as a confirmation of the action.
     *
     * <p>When this text is displayed the user will receive a confirmation when the action is taken. The user
     * can then cancel the action, or continue. If set, {@link Action#getConfirmationDialog()} will be used
     * to build the dialog. Otherwise, the dialog is created dynamically on the client.</p>
     *
     * @return text to display in a confirmation for the action
     */
    public String getConfirmationPromptText() {
        return confirmationPromptText;
    }

    /**
     * @see Action#getConfirmationPromptText()
     */
    public void setConfirmationPromptText(String confirmationPromptText) {
        this.confirmationPromptText = confirmationPromptText;
    }

    /**
     * Dialog to use an a confirmation for the action.
     *
     * <p>For custom confirmation dialogs this can be set to any valid dialog group. It is expected that the
     * dialog have at least one action with the dialog response of 'true' to continue the action.</p>
     *
     * @return dialog group instance to use an a confirmation
     */
    public DialogGroup getConfirmationDialog() {
        return confirmationDialog;
    }

    /**
     * @see Action#getConfirmationDialog()
     */
    public void setConfirmationDialog(DialogGroup confirmationDialog) {
        this.confirmationDialog = confirmationDialog;
    }

    /**
     * If the action is within a {@link org.kuali.rice.krad.uif.container.DialogGroup} it can be configured to
     * dismiss the dialog using this property.
     *
     * <p>A dialog can be dismissed at various points of the action using the values:
     *    IMMEDIATE - dismiss dialog right away (and do nothig further)
     *    PRESUBMIT - run the action presubmit (which can include validation), if successful close the dialog and
     *                do nothing further
     *    REQUEST - carry out the action request as usual and dismiss the dialog when the server request is made
     * </p>
     *
     * <p>Note the id for the dialog that will be dismissed is automatically associated with the action when
     * the dialog is shown.</p>
     *
     * @return String option for dismissing a dialog
     */
    public String getDialogDismissOption() {
        return dialogDismissOption;
    }

    /**
     * @see Action#getDialogDismissOption()
     */
    public void setDialogDismissOption(String dialogDismissOption) {
        this.dialogDismissOption = dialogDismissOption;
    }

    /**
     * If the action is within a {@link org.kuali.rice.krad.uif.container.DialogGroup} it can be configured to
     * return a response using this property.
     *
     * <p>Dialogs can be used to get a response from a user, either a simple confirmation (true or false), or to
     * choice from a list of options. The responses for the dialog are created with action components. The property
     * specifies the action value that should be returned (when chosen) to the dialog response handlers. For example,
     * in a simple confirmation one action will have a dialog response 'false', and the other will have a dialog
     * response 'true'.</p>
     *
     * @return String dialog response value
     */
    public String getDialogResponse() {
        return dialogResponse;
    }

    /**
     * @see Action#getDialogResponse()
     */
    public void setDialogResponse(String dialogResponse) {
        this.dialogResponse = dialogResponse;
    }

    /**
     * When this property is set to true it will submit the form using Ajax instead of the browser submit. Will default
     * to updating the page contents
     *
     * @return boolean
     */
    @BeanTagAttribute
    public boolean isAjaxSubmit() {
        return ajaxSubmit;
    }

    /**
     * Setter for {@link #isAjaxSubmit()}.
     *
     * @param ajaxSubmit property value
     */
    public void setAjaxSubmit(boolean ajaxSubmit) {
        this.ajaxSubmit = ajaxSubmit;
    }

    /**
     * Gets the return type for the ajax call
     *
     * <p>
     * The ajax return type indicates how the response content will be handled in the client. Typical
     * examples include updating a component, the page, or doing a redirect.
     * </p>
     *
     * @return return type
     * @see org.kuali.rice.krad.uif.UifConstants.AjaxReturnTypes
     */
    @BeanTagAttribute
    public String getAjaxReturnType() {
        return this.ajaxReturnType;
    }

    /**
     * Setter for the type of ajax return.
     *
     * @param ajaxReturnType property value
     */
    public void setAjaxReturnType(String ajaxReturnType) {
        this.ajaxReturnType = ajaxReturnType;
    }

    /**
     * Indicates if the action response should be displayed in a lightbox.
     *
     * @return true if response should be rendered in a lightbox, false if not
     */
    @BeanTagAttribute
    public boolean isDisplayResponseInLightBox() {
        return StringUtils.equals(this.ajaxReturnType, UifConstants.AjaxReturnTypes.DISPLAYLIGHTBOX.getKey());
    }

    /**
     * Setter for indicating the response should be rendered in a lightbox.
     *
     * @param displayResponseInLightBox property value
     */
    public void setDisplayResponseInLightBox(boolean displayResponseInLightBox) {
        if (displayResponseInLightBox) {
            this.ajaxReturnType = UifConstants.AjaxReturnTypes.DISPLAYLIGHTBOX.getKey();
        }
        // if display lightbox is false and it was previously true, set to default of update page
        else if (StringUtils.equals(this.ajaxReturnType, UifConstants.AjaxReturnTypes.DISPLAYLIGHTBOX.getKey())) {
            this.ajaxReturnType = UifConstants.AjaxReturnTypes.UPDATEPAGE.getKey();
        }
    }

    /**
     * Gets the script which will be invoked on a successful ajax call
     *
     * <p>
     * The successCallback can carry out custom logic after a successful ajax submission has been made. The
     * value can contain one or more script statements. In addition, the response contents can be accessed
     * through the variable 'responseContents'
     * </p>
     *
     * <p>
     * Examples 'handleSuccessfulUpdate(responseContents);'
     * </p>
     *
     * <p>
     * The successCallback may only be specified when {@link #isAjaxSubmit()} is true
     * </p>
     *
     * @return script to be executed when the action is successful
     */
    @BeanTagAttribute
    public String getSuccessCallback() {
        return successCallback;
    }

    /**
     * Setter for successCallback.
     *
     * @param successCallback property value
     */
    public void setSuccessCallback(String successCallback) {
        this.successCallback = successCallback;
    }

    /**
     * Gets the script which will be invoked when the action fails due to problems in the ajax call or
     * the return of an incident report
     *
     * <p>
     * The errorCallback can carry out custom logic after a failed ajax submission. The
     * value can contain one or more script statements. In addition, the response contents can be accessed
     * through the variable 'responseContents'
     * </p>
     *
     * <p>
     * Examples 'handleFailedUpdate(responseContents);'
     * </p>
     *
     * <p>
     * The errorCallback may only be specified when {@link #isAjaxSubmit()} is true
     * </p>
     *
     * @return script to be executed when the action is successful
     */
    @BeanTagAttribute
    public String getErrorCallback() {
        return errorCallback;
    }

    /**
     * Setter for {@link #getErrorCallback()}.
     *
     * @param errorCallback property value
     */
    public void setErrorCallback(String errorCallback) {
        this.errorCallback = errorCallback;
    }

    /**
     * Id for the component that should be refreshed after the action completes
     *
     * <p>
     * Either refresh id or refresh property name can be set to configure the component that should
     * be refreshed after the action completes. If both are blank, the page will be refreshed
     * </p>
     *
     * @return valid component id
     */
    @BeanTagAttribute
    public String getRefreshId() {
        return refreshId;
    }

    /**
     * Setter for the {@link #getRefreshId()}.
     *
     * @param refreshId property value
     */
    public void setRefreshId(String refreshId) {
        this.refreshId = refreshId;
    }

    /**
     * Property name for the {@link org.kuali.rice.krad.uif.field.DataField} that should be refreshed after the action
     * completes
     *
     * <p>
     * Either refresh id or refresh property name can be set to configure the component that should
     * be refreshed after the action completes. If both are blank, the page will be refreshed
     * </p>
     *
     * <p>
     * Property name will be adjusted to use the default binding path unless it contains the form prefix
     * </p>
     *
     * @return valid property name with an associated DataField
     * @see org.kuali.rice.krad.uif.UifConstants#NO_BIND_ADJUST_PREFIX
     */
    @BeanTagAttribute
    public String getRefreshPropertyName() {
        return refreshPropertyName;
    }

    /**
     * Setter for the property name of the DataField that should be refreshed.
     *
     * @param refreshPropertyName property value
     */
    public void setRefreshPropertyName(String refreshPropertyName) {
        this.refreshPropertyName = refreshPropertyName;
    }

    /**
     * Gets the loading message used by action's blockUI.
     *
     * @return String if String is not null, used in place of loading message
     */
    @BeanTagAttribute
    public String getLoadingMessageText() {
        return loadingMessageText;
    }

    /**
     * When this property is set, it is used in place of the loading message text used by the blockUI.
     *
     * @param loadingMessageText property value
     */
    public void setLoadingMessageText(String loadingMessageText) {
        this.loadingMessageText = loadingMessageText;
    }

    /**
     * Indicates whether blocking for the action should be disabled
     *
     * <p>
     * By default when an action is invoked part of the page or the entire window is blocked until
     * the action completes. If this property is set to true the blocking will not be displayed.
     * </p>
     *
     * <p>
     * Currently if an action returns a file download, this property should be set to true. If not, the blocking
     * will never get unblocked (because the page does not get notification a file was downloaded)
     * </p>
     *
     * @return true if blocking should be disabled, false if not
     */
    @BeanTagAttribute
    public boolean isDisableBlocking() {
        return disableBlocking;
    }

    /**
     * Setter for disabling blocking when the action is invoked.
     *
     * @param disableBlocking property value
     */
    public void setDisableBlocking(boolean disableBlocking) {
        this.disableBlocking = disableBlocking;
    }

    /**
     * Evaluate the disable condition on controls which disable it on each key up event.
     *
     * @return true if evaluate on key up, false otherwise
     */
    @BeanTagAttribute
    public boolean isEvaluateDisabledOnKeyUp() {
        return evaluateDisabledOnKeyUp;
    }

    /**
     * Setter for {@link #isEvaluateDisabledOnKeyUp()}.
     *
     * @param evaluateDisabledOnKeyUp property value
     */
    public void setEvaluateDisabledOnKeyUp(boolean evaluateDisabledOnKeyUp) {
        this.evaluateDisabledOnKeyUp = evaluateDisabledOnKeyUp;
    }

    /**
     * Evaluate if this action is the default action for a page, view, group, or section.
     *
     * @return true if this action is default, false otherwise
     */
    @BeanTagAttribute(name = "defaultEnterKeyAction")
    public boolean isDefaultEnterKeyAction() {
        return this.defaultEnterKeyAction;
    }

    /**
     * @see  #isDefaultEnterKeyAction()
     */
    public void setDefaultEnterKeyAction(boolean defaultEnterKeyAction) {
        this.defaultEnterKeyAction = defaultEnterKeyAction;
    }

    /**
     * Get the disable condition js derived from the springEL, cannot be set.
     *
     * @return the disableConditionJs javascript to be evaluated
     */
    public String getDisabledConditionJs() {
        return disabledConditionJs;
    }

    /**
     * Sets the disabled condition javascript.
     *
     * @param disabledConditionJs property value
     */
    protected void setDisabledConditionJs(String disabledConditionJs) {
        this.disabledConditionJs = disabledConditionJs;
    }

    /**
     * Gets a list of control names to add handlers to for disable functionality, cannot be set.
     *
     * @return control names to add handlers to for disable
     */
    public List<String> getDisabledConditionControlNames() {
        return disabledConditionControlNames;
    }

    /**
     * Set disabled condition control names.
     *
     * @param disabledConditionControlNames property value
     */
    public void setDisabledConditionControlNames(List<String> disabledConditionControlNames) {
        this.disabledConditionControlNames = disabledConditionControlNames;
    }

    /**
     * Gets the property names of fields that when changed, will disable this component.
     *
     * @return the property names to monitor for change to disable this component
     */
    @BeanTagAttribute
    public List<String> getDisabledWhenChangedPropertyNames() {
        return disabledWhenChangedPropertyNames;
    }

    /**
     * Sets the property names of fields that when changed, will disable this component.
     *
     * @param disabledWhenChangedPropertyNames property value
     */
    public void setDisabledWhenChangedPropertyNames(List<String> disabledWhenChangedPropertyNames) {
        this.disabledWhenChangedPropertyNames = disabledWhenChangedPropertyNames;
    }

    /**
     * Gets the property names of fields that when changed, will enable this component.
     *
     * @return the property names to monitor for change to enable this component
     */
    @BeanTagAttribute
    public List<String> getEnabledWhenChangedPropertyNames() {
        return enabledWhenChangedPropertyNames;
    }

    /**
     * Sets the property names of fields that when changed, will enable this component.
     *
     * @param enabledWhenChangedPropertyNames property value
     */
    public void setEnabledWhenChangedPropertyNames(List<String> enabledWhenChangedPropertyNames) {
        this.enabledWhenChangedPropertyNames = enabledWhenChangedPropertyNames;
    }

    /**
     * Sets the disabled expression.
     *
     * @param disabledExpression property value
     */
    protected void setDisabledExpression(String disabledExpression) {
        this.disabledExpression = disabledExpression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Checks that an action is set
        if (getJumpToIdAfterSubmit() != null && getJumpToNameAfterSubmit() != null) {
            String currentValues[] = {"jumpToIdAfterSubmit =" + getJumpToIdAfterSubmit(),
                    "jumpToNameAfterSubmit =" + getJumpToNameAfterSubmit()};
            tracer.createWarning("Only 1 jumpTo property should be set", currentValues);
        }
        super.completeValidation(tracer.getCopy());
    }
}
