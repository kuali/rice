/**
 * Copyright 2005-2012 The Kuali Foundation
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
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.field.ImageField;
import org.kuali.rice.krad.uif.view.FormView;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.widget.LightBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Field that presents an action that can be taken on the UI such as submitting
 * the form or invoking a script
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Action extends ContentElementBase {
    private static final long serialVersionUID = 1025672792657238829L;

    private String methodToCall;
    private String navigateToPageId;

    private boolean performClientSideValidation;
    private String actionScript;

    private String jumpToIdAfterSubmit;
    private String jumpToNameAfterSubmit;
    private String focusOnIdAfterSubmit;

    private String actionLabel;
    private Image actionImage;
    private String actionImagePlacement;

    private String actionEvent;
    private Map<String, String> actionParameters;

    private LightBox lightBoxLookup;
    private LightBox lightBoxDirectInquiry;

    private boolean performDirtyValidation;

    private boolean disabled;
    private String disabledReason;

    private String preSubmitCall;
    private boolean ajaxSubmit;

    private String successCallback;
    private String errorCallback;

    public Action() {
        super();

        actionImagePlacement = UifConstants.Position.LEFT.name();

        disabled = false;
        actionParameters = new HashMap<String, String>();
    }

    /**
     * The following finalization is performed:
     *
     * <ul>
     * <li>Add methodToCall action parameter if set and setup event code for
     * setting action parameters</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);
        //clear alt text to avoid screen reader confusion when using image in button with text
        if (actionImage != null && StringUtils.isNotBlank(actionImagePlacement) && StringUtils.isNotBlank(
                actionLabel)) {
            actionImage.setAltText("");
        }

        if (!actionParameters.containsKey(UifConstants.UrlParams.ACTION_EVENT) && StringUtils.isNotBlank(actionEvent)) {
            actionParameters.put(UifConstants.UrlParams.ACTION_EVENT, actionEvent);
        }

        actionParameters.put(UifConstants.UrlParams.SHOW_HOME, "false");
        actionParameters.put(UifConstants.UrlParams.SHOW_HISTORY, "false");

        if (StringUtils.isNotBlank(navigateToPageId)) {
            actionParameters.put(UifParameters.NAVIGATE_TO_PAGE_ID, navigateToPageId);
            if (StringUtils.isBlank(methodToCall)) {
                actionParameters.put(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME,
                        UifConstants.MethodToCallNames.NAVIGATE);
            }
        }

        if (!actionParameters.containsKey(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME) && StringUtils
                .isNotBlank(methodToCall)) {
            actionParameters.put(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME, methodToCall);
        }

        // If there is no lightBox then create the on click script
        if (lightBoxLookup == null) {
            String prefixScript = this.getOnClickScript();
            if (prefixScript == null) {
                prefixScript = "";
            }

            boolean validateFormDirty = false;
            if (view instanceof FormView && isPerformDirtyValidation()) {
                validateFormDirty = ((FormView) view).isApplyDirtyCheck();
            }

            boolean includeDirtyCheckScript = false;
            String writeParamsScript = "";
            if (!actionParameters.isEmpty()) {
                for (String key : actionParameters.keySet()) {
                    String parameterPath = key;
                    if (!key.equals(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME)) {
                        parameterPath = UifPropertyPaths.ACTION_PARAMETERS + "[" + key + "]";
                    }

                    writeParamsScript =
                            writeParamsScript + "writeHiddenToForm('" + parameterPath + "' , '" + actionParameters.get(
                                    key) + "'); ";

                    // Include dirtycheck js function call if the method to call
                    // is refresh, navigate, cancel or close
                    if (validateFormDirty && !includeDirtyCheckScript && key.equals(
                            UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME)) {
                        String keyValue = (String) actionParameters.get(key);
                        if (StringUtils.equals(keyValue, UifConstants.MethodToCallNames.REFRESH) || StringUtils.equals(
                                keyValue, UifConstants.MethodToCallNames.NAVIGATE) || StringUtils.equals(keyValue,
                                UifConstants.MethodToCallNames.CANCEL) || StringUtils.equals(keyValue,
                                UifConstants.MethodToCallNames.CLOSE)) {
                            includeDirtyCheckScript = true;
                        }
                    }
                }
            }

            // TODO possibly fix some other way - this is a workaround, prevents
            // showing history and showing home again on actions which submit
            // the form
            writeParamsScript = writeParamsScript
                    + "writeHiddenToForm('"
                    + UifConstants.UrlParams.SHOW_HISTORY
                    + "', '"
                    + "false"
                    + "'); ";
            writeParamsScript = writeParamsScript
                    + "writeHiddenToForm('"
                    + UifConstants.UrlParams.SHOW_HOME
                    + "' , '"
                    + "false"
                    + "'); ";

            if (StringUtils.isBlank(focusOnIdAfterSubmit)) {
                // if this is blank focus this actionField by default
                focusOnIdAfterSubmit = this.getId();
                writeParamsScript = writeParamsScript + "writeHiddenToForm('focusId' , '" + this.getId() + "'); ";
            } else if (!focusOnIdAfterSubmit.equalsIgnoreCase(UifConstants.Order.FIRST.toString())) {
                // Use the id passed in
                writeParamsScript =
                        writeParamsScript + "writeHiddenToForm('focusId' , '" + focusOnIdAfterSubmit + "'); ";
            } else {
                // First input will be focused, must be first field set to empty
                // string
                writeParamsScript = writeParamsScript + "writeHiddenToForm('focusId' , ''); ";
            }

            if (StringUtils.isBlank(jumpToIdAfterSubmit) && StringUtils.isBlank(jumpToNameAfterSubmit)) {
                jumpToIdAfterSubmit = this.getId();
                writeParamsScript = writeParamsScript + "writeHiddenToForm('jumpToId' , '" + this.getId() + "'); ";
            } else if (StringUtils.isNotBlank(jumpToIdAfterSubmit)) {
                writeParamsScript =
                        writeParamsScript + "writeHiddenToForm('jumpToId' , '" + jumpToIdAfterSubmit + "'); ";
            } else {
                writeParamsScript =
                        writeParamsScript + "writeHiddenToForm('jumpToName' , '" + jumpToNameAfterSubmit + "'); ";
            }

            String postScript = "";
            if (StringUtils.isNotBlank(actionScript)) {
                postScript = actionScript;
            }
            if (isPerformClientSideValidation()) {
                postScript = postScript + "validateAndSubmitUsingFormMethodToCall();";
            }
            String submitScript = "";
            if (ajaxSubmit) {
                submitScript = "ajaxSubmitForm( '"
                        + getMethodToCall()
                        + "',"
                        + successCallback
                        + ", null, null,"
                        + errorCallback
                        + ");";
            } else {
                submitScript = "jQuery('#kualiForm').submit();";
            }
            if (StringUtils.isBlank(postScript)) {
                //if the preSubmitCall evaluates to true then submit the form else don't
                if (StringUtils.isNotBlank(preSubmitCall)) {
                    postScript = "if (" + preSubmitCall + "== true ) {" + submitScript + "}";
                } else {
                    postScript = submitScript;
                }
            }

            if (includeDirtyCheckScript) {
                this.setOnClickScript("e.preventDefault(); if (checkDirty(e) == false) { "
                        + prefixScript
                        + writeParamsScript
                        + postScript
                        + " ; } ");
            } else {
                this.setOnClickScript("e.preventDefault();" + prefixScript + writeParamsScript + postScript);
            }

        } else {
            // When there is a light box - don't add the on click script as it
            // will be prevented from executing
            // Create a script map object which will be written to the form on
            // click event
            StringBuffer sb = new StringBuffer();
            sb.append("{");
            for (String key : actionParameters.keySet()) {
                String optionValue = actionParameters.get(key);
                if (sb.length() > 1) {
                    sb.append(",");
                }
                if (!key.equals(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME)) {
                    sb.append("\"" + UifPropertyPaths.ACTION_PARAMETERS + "[" + key + "]" + "\"");
                } else {
                    sb.append("\"" + key + "\"");
                }
                sb.append(":");
                sb.append("\"" + optionValue + "\"");
            }
            sb.append("}");
            lightBoxLookup.setActionParameterMapString(sb.toString());
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(actionImage);
        components.add(lightBoxLookup);
        components.add(lightBoxDirectInquiry);

        return components;
    }

    /**
     * Name of the method that should be called when the action is selected
     * <p>
     * For a server side call (clientSideCall is false), gives the name of the
     * method in the mapped controller that should be invoked when the action is
     * selected. For client side calls gives the name of the script function
     * that should be invoked when the action is selected
     * </p>
     *
     * @return String name of method to call
     */
    public String getMethodToCall() {
        return this.methodToCall;
    }

    /**
     * Setter for the actions method to call
     *
     * @param methodToCall
     */
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

    /**
     * Label text for the action
     * <p>
     * The label text is used by the template renderers to give a human readable
     * label for the action. For buttons this generally is the button text,
     * while for an action link it would be the links displayed text
     * </p>
     *
     * @return String label for action
     */
    public String getActionLabel() {
        return this.actionLabel;
    }

    /**
     * Setter for the actions label
     *
     * @param actionLabel
     */
    public void setActionLabel(String actionLabel) {
        this.actionLabel = actionLabel;
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
     * @return Image action image
     */
    public Image getActionImage() {
        return this.actionImage;
    }

    /**
     * Setter for the action image field
     *
     * @param actionImage
     */
    public void setActionImage(Image actionImage) {
        this.actionImage = actionImage;
    }

    /**
     * For an <code>Action</code> that is part of a
     * <code>NavigationGroup</code, the navigate to page id can be set to
     * configure the page that should be navigated to when the action is
     * selected
     * <p>
     * Support exists in the <code>UifControllerBase</code> for handling
     * navigation between pages
     * </p>
     *
     * @return String id of page that should be rendered when the action item is
     *         selected
     */
    public String getNavigateToPageId() {
        return this.navigateToPageId;
    }

    /**
     * Setter for the navigate to page id
     *
     * @param navigateToPageId
     */
    public void setNavigateToPageId(String navigateToPageId) {
        this.navigateToPageId = navigateToPageId;
        actionParameters.put(UifParameters.NAVIGATE_TO_PAGE_ID, navigateToPageId);
        this.methodToCall = UifConstants.MethodToCallNames.NAVIGATE;
    }

    /**
     * Name of the event that will be set when the action is invoked
     *
     * <p>
     * Action events can be looked at by the view or components in order to render differently depending on
     * the action requested.
     * </p>
     *
     * @return String action event name
     * @see org.kuali.rice.krad.uif.UifConstants.ActionEvents
     */
    public String getActionEvent() {
        return actionEvent;
    }

    /**
     * Setter for the action event
     *
     * @param actionEvent
     */
    public void setActionEvent(String actionEvent) {
        this.actionEvent = actionEvent;
    }

    /**
     * Parameters that should be sent when the action is invoked
     * <p>
     * Action renderer will decide how the parameters are sent for the action
     * (via script generated hiddens, or script parameters, ...)
     * </p>
     * <p>
     * Can be set by other components such as the <code>CollectionGroup</code>
     * to provide the context the action is in (such as the collection name and
     * line the action applies to)
     * </p>
     *
     * @return Map<String, String> action parameters
     */
    public Map<String, String> getActionParameters() {
        return this.actionParameters;
    }

    /**
     * Setter for the action parameters
     *
     * @param actionParameters
     */
    public void setActionParameters(Map<String, String> actionParameters) {
        this.actionParameters = actionParameters;
    }

    /**
     * Convenience method to add a parameter to the action parameters Map
     *
     * @param parameterName - name of parameter to add
     * @param parameterValue - value of parameter to add
     */
    public void addActionParameter(String parameterName, String parameterValue) {
        if (actionParameters == null) {
            this.actionParameters = new HashMap<String, String>();
        }

        this.actionParameters.put(parameterName, parameterValue);
    }

    /**
     * Get an actionParameter by name
     */
    public String getActionParameter(String parameterName) {
        return this.actionParameters.get(parameterName);
    }

    /**
     * Action Field Security object that indicates what authorization (permissions) exist for the action
     *
     * @return ActionSecurity instance
     */
    @Override
    public ActionSecurity getComponentSecurity() {
        return (ActionSecurity) super.getComponentSecurity();
    }

    /**
     * Override to assert a {@link ActionSecurity} instance is set
     *
     * @param componentSecurity - instance of ActionSecurity
     */
    @Override
    public void setComponentSecurity(ComponentSecurity componentSecurity) {
        if (!(componentSecurity instanceof ActionSecurity)) {
            throw new RiceRuntimeException("Component security for Action should be instance of ActionSecurity");
        }

        super.setComponentSecurity(componentSecurity);
    }

    @Override
    protected Class<? extends ComponentSecurity> getComponentSecurityClass() {
        return ActionSecurity.class;
    }

    /**
     * Setter for the light box lookup widget
     *
     * @param lightBoxLookup <code>LightBoxLookup</code> widget to set
     */
    public void setLightBoxLookup(LightBox lightBoxLookup) {
        this.lightBoxLookup = lightBoxLookup;
    }

    /**
     * LightBoxLookup widget for the field
     * <p>
     * The light box lookup widget will change the lookup behaviour to open the
     * lookup in a light box.
     * </p>
     *
     * @return the <code>DirectInquiry</code> field DirectInquiry
     */
    public LightBox getLightBoxLookup() {
        return lightBoxLookup;
    }

    /**
     * @return the jumpToIdAfterSubmit
     */
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
    public String getJumpToNameAfterSubmit() {
        return this.jumpToNameAfterSubmit;
    }

    /**
     * @param jumpToNameAfterSubmit the jumpToNameAfterSubmit to set
     */
    public void setJumpToNameAfterSubmit(String jumpToNameAfterSubmit) {
        this.jumpToNameAfterSubmit = jumpToNameAfterSubmit;
    }

    /**
     * The id of the field to place focus on in the new page after the new page
     * is retrieved. Passing in "FIRST" will focus on the first visible input
     * element on the form. Passing in the empty string will result in this
     * Action being focused.
     *
     * @return the focusOnAfterSubmit
     */
    public String getFocusOnIdAfterSubmit() {
        return this.focusOnIdAfterSubmit;
    }

    /**
     * @param focusOnIdAfterSubmit the focusOnAfterSubmit to set
     */
    public void setFocusOnIdAfterSubmit(String focusOnIdAfterSubmit) {
        this.focusOnIdAfterSubmit = focusOnIdAfterSubmit;
    }

    /**
     * Indicates whether the form data should be validated on the client side
     *
     * return true if validation should occur, false otherwise
     */
    public boolean isPerformClientSideValidation() {
        return this.performClientSideValidation;
    }

    /**
     * Setter for the client side validation flag
     *
     * @param performClientSideValidation
     */
    public void setPerformClientSideValidation(boolean performClientSideValidation) {
        this.performClientSideValidation = performClientSideValidation;
    }

    /**
     * Client side javascript to be executed when this actionField is clicked.
     * This overrides the default action for this Action so the method
     * called must explicitly submit, navigate, etc. through js, if necessary.
     * In addition, this js occurs AFTER onClickScripts set on this field, it
     * will be the last script executed by the click event. Sidenote: This js is
     * always called after hidden actionParameters and methodToCall methods are
     * written by the js to the html form.
     *
     * @return the actionScript
     */
    public String getActionScript() {
        return this.actionScript;
    }

    /**
     * @param actionScript the actionScript to set
     */
    public void setActionScript(String actionScript) {
        if (!StringUtils.endsWith(actionScript, ";")) {
            actionScript = actionScript + ";";
        }
        this.actionScript = actionScript;
    }

    /**
     * Setter for the light box direct inquiry widget
     *
     * @param lightBoxDirectInquiry <code>LightBox</code> widget to set
     */
    public void setLightBoxDirectInquiry(LightBox lightBoxDirectInquiry) {
        this.lightBoxDirectInquiry = lightBoxDirectInquiry;
    }

    /**
     * LightBox widget for the field
     * <p>
     * The light box widget will change the direct inquiry behaviour to open up
     * in a light box.
     * </p>
     *
     * @return the <code>LightBox</code> field LightBox
     */
    public LightBox getLightBoxDirectInquiry() {
        return lightBoxDirectInquiry;
    }

    /**
     * @param performDirtyValidation the blockValidateDirty to set
     */
    public void setPerformDirtyValidation(boolean performDirtyValidation) {
        this.performDirtyValidation = performDirtyValidation;
    }

    /**
     * @return the blockValidateDirty
     */
    public boolean isPerformDirtyValidation() {
        return performDirtyValidation;
    }

    /**
     * Indicates whether the action (input or button) is disabled (doesn't allow interaction)
     *
     * @return boolean true if the action field is disabled, false if not
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Setter for the disabled indicator
     *
     * @param disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * If the action field is disabled, gives a reason for why which will be displayed as a tooltip
     * on the action field (button)
     *
     * @return String disabled reason text
     * @see {@link #isDisabled()}
     */
    public String getDisabledReason() {
        return disabledReason;
    }

    /**
     * Setter for the disabled reason text
     *
     * @param disabledReason
     */
    public void setDisabledReason(String disabledReason) {
        this.disabledReason = disabledReason;
    }

    public String getActionImagePlacement() {
        return actionImagePlacement;
    }

    /**
     * Set to TOP, BOTTOM, LEFT, RIGHT to position image at that location within the button.
     * For the subclass ActionLinkField only LEFT and RIGHT are allowed.  When set to blank/null/IMAGE_ONLY, the image
     * itself will be the Action, if no value is set the default is ALWAYS LEFT, you must explicitly set
     * blank/null/IMAGE_ONLY to use ONLY the image as the Action.
     *
     * @return
     */
    public void setActionImagePlacement(String actionImagePlacement) {
        this.actionImagePlacement = actionImagePlacement;
    }

    /**
     * Gets the script which needs to be invoked before the form is submitted. The script should return a boolean
     * indicating if the form should be submitted or not.
     *
     * @return String script text that will be invoked before form submission
     */
    public String getPreSubmitCall() {
        return preSubmitCall;
    }

    /**
     * Setter for preSubmitCall
     *
     * @param preSubmitCall
     */
    public void setPreSubmitCall(String preSubmitCall) {
        this.preSubmitCall = preSubmitCall;
    }

    /**
     * When this property is set to true it will submit the form using Ajax instead of the browser submit. Will default
     * to updating the page contents
     *
     * @return boolean
     */
    public boolean isAjaxSubmit() {
        return ajaxSubmit;
    }

    /**
     * Setter for ajaxSubmit
     *
     * @param ajaxSubmit
     */
    public void setAjaxSubmit(boolean ajaxSubmit) {
        this.ajaxSubmit = ajaxSubmit;
    }

    /**
     * Getter for successCallback property. This will be invoked for successful ajax calls
     *
     * @return String
     */

    public String getSuccessCallback() {
        return successCallback;
    }

    /**
     * Setter for successCallback
     *
     * @param successCallback
     */
    public void setSuccessCallback(String successCallback) {
        this.successCallback = successCallback;
    }

    /**
     * Getter for errorCallback. This will be invoked for failed ajax calls
     *
     * @return
     */
    public String getErrorCallback() {
        return errorCallback;
    }

    /**
     * Setter for errorCallback
     *
     * @param errorCallback
     */
    public void setErrorCallback(String errorCallback) {
        this.errorCallback = errorCallback;
    }
}
