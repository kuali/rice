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
package org.kuali.rice.krad.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.UifPropertyPaths;
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
public class ActionField extends FieldBase {
    private static final long serialVersionUID = 1025672792657238829L;

    private String methodToCall;
    private String navigateToPageId;

    private String clientSideJs;

    private String jumpToIdAfterSubmit;
    private String jumpToNameAfterSubmit;
    private String focusOnAfterSubmit;

    private String actionLabel;
    private ImageField actionImageField;
    private String actionImageLocation = "LEFT";

    private Map<String, String> actionParameters;

    private LightBox lightBoxLookup;

    private LightBox lightBoxDirectInquiry;

    private boolean blockValidateDirty;

    public ActionField() {
        super();
        actionParameters = new HashMap<String, String>();
    }

    /**
     * The following initialization is performed:
     * <ul>
     * <li>Set the actionLabel if blank to the Field label</li>
     * </ul>
     * 
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performInitialization(org.kuali.rice.krad.uif.view.View)
     */
    @Override
    public void performInitialization(View view) {
        super.performInitialization(view);

        if (StringUtils.isBlank(actionLabel)) {
            actionLabel = this.getLabel();
        }
    }

    /**
     * The following finalization is performed:
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
        if(actionImageField != null && StringUtils.isNotBlank(actionImageLocation) && StringUtils.isNotBlank(actionLabel)){
            actionImageField.setAltText("");
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

        if (!actionParameters.containsKey(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME)
                && StringUtils.isNotBlank(methodToCall)) {
            actionParameters.put(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME, methodToCall);
        }

        // If there is no lightBox then create the on click script
        if (lightBoxLookup == null) {
            String prefixScript = this.getOnClickScript();
            if (prefixScript == null) {
                prefixScript = "";
            }

            boolean validateFormDirty = false;
            if (view instanceof FormView && !isBlockValidateDirty()) {
                validateFormDirty = ((FormView) view).isValidateDirty();
            }

            boolean includeDirtyCheckScript = false;
            String writeParamsScript = "";
            if (!actionParameters.isEmpty()) {
                for (String key : actionParameters.keySet()) {
                    String parameterPath = key;
                    if (!key.equals(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME)) {
                        parameterPath = UifPropertyPaths.ACTION_PARAMETERS + "[" + key + "]";
                    }

                    writeParamsScript = writeParamsScript + "writeHiddenToForm('" + parameterPath + "' , '"
                            + actionParameters.get(key) + "'); ";

                    // Include dirtycheck js function call if the method to call
                    // is refresh, navigate, cancel or close
                    if (validateFormDirty && !includeDirtyCheckScript
                            && key.equals(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME)) {
                        String keyValue = (String) actionParameters.get(key);
                        if (StringUtils.equals(keyValue, UifConstants.MethodToCallNames.REFRESH)
                                || StringUtils.equals(keyValue, UifConstants.MethodToCallNames.NAVIGATE)
                                || StringUtils.equals(keyValue, UifConstants.MethodToCallNames.CANCEL)
                                || StringUtils.equals(keyValue, UifConstants.MethodToCallNames.CLOSE)) {
                            includeDirtyCheckScript = true;
                        }
                    }
                }
            }

            // TODO possibly fix some other way - this is a workaround, prevents
            // showing history and showing home again on actions which submit
            // the form
            writeParamsScript = writeParamsScript + "writeHiddenToForm('" + UifConstants.UrlParams.SHOW_HISTORY
                    + "', '" + "false" + "'); ";
            writeParamsScript = writeParamsScript + "writeHiddenToForm('" + UifConstants.UrlParams.SHOW_HOME + "' , '"
                    + "false" + "'); ";

            if (StringUtils.isBlank(focusOnAfterSubmit)) {
                // if this is blank focus this actionField by default
                focusOnAfterSubmit = this.getId();
                writeParamsScript = writeParamsScript + "writeHiddenToForm('focusId' , '" + this.getId() + "'); ";
            } else if (!focusOnAfterSubmit.equalsIgnoreCase(UifConstants.Order.FIRST)) {
                // Use the id passed in
                writeParamsScript = writeParamsScript + "writeHiddenToForm('focusId' , '" + focusOnAfterSubmit + "'); ";
            } else {
                // First input will be focused, must be first field set to empty
                // string
                writeParamsScript = writeParamsScript + "writeHiddenToForm('focusId' , ''); ";
            }

            if (StringUtils.isBlank(jumpToIdAfterSubmit) && StringUtils.isBlank(jumpToNameAfterSubmit)) {
                jumpToIdAfterSubmit = this.getId();
                writeParamsScript = writeParamsScript + "writeHiddenToForm('jumpToId' , '" + this.getId() + "'); ";
            } else if (StringUtils.isNotBlank(jumpToIdAfterSubmit)) {
                writeParamsScript = writeParamsScript + "writeHiddenToForm('jumpToId' , '" + jumpToIdAfterSubmit
                        + "'); ";
            } else {
                writeParamsScript = writeParamsScript + "writeHiddenToForm('jumpToName' , '" + jumpToNameAfterSubmit
                        + "'); ";
            }

            String postScript = "";
            if (StringUtils.isNotBlank(clientSideJs)) {
                postScript = clientSideJs;
            } else {
                postScript = "writeHiddenToForm('renderFullView' , 'true'); jq('#kualiForm').submit();";
            }

            if (includeDirtyCheckScript) {
                this.setOnClickScript("e.preventDefault(); if (checkDirty(e) == false) { " + prefixScript
                        + writeParamsScript + postScript + " ; } ");
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
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getNestedComponents()
     */
    @Override
    public List<Component> getNestedComponents() {
        List<Component> components = super.getNestedComponents();

        components.add(actionImageField);
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
     * <p>
     * When the action image field is set (and render is true) the image will be
     * used to present the action as opposed to the default (input submit). For
     * action link templates the image is used for the link instead of the
     * action link text
     * </p>
     * 
     * @return ImageField action image
     */
    public ImageField getActionImageField() {
        return this.actionImageField;
    }

    /**
     * Setter for the action image field
     * 
     * @param actionImageField
     */
    public void setActionImageField(ImageField actionImageField) {
        this.actionImageField = actionImageField;
    }

    /**
     * For an <code>ActionField</code> that is part of a
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
     * @param parameterName
     *            - name of parameter to add
     * @param parameterValue
     *            - value of parameter to add
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
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getSupportsOnClick()
     */
    @Override
    public boolean getSupportsOnClick() {
        return true;
    }

    /**
     * Setter for the light box lookup widget
     * 
     * @param lightBoxLookup
     *            <code>LightBoxLookup</code> widget to set
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
     * jumpToNameAfterSubmit will result in this ActionField being jumped to by
     * default if it is present on the new page. WARNING: jumpToIdAfterSubmit
     * always takes precedence over jumpToNameAfterSubmit, if set.
     * 
     * @param jumpToIdAfterSubmit
     *            the jumpToIdAfterSubmit to set
     */
    public void setJumpToIdAfterSubmit(String jumpToIdAfterSubmit) {
        this.jumpToIdAfterSubmit = jumpToIdAfterSubmit;
    }

    /**
     * The name to jump to in the next page, the element with this name will be
     * jumped to automatically when the new page is retrieved after a submit.
     * Passing in nothing for both jumpToIdAfterSubmit and jumpToNameAfterSubmit
     * will result in this ActionField being jumped to by default if it is
     * present on the new page. WARNING: jumpToIdAfterSubmit always takes
     * precedence over jumpToNameAfterSubmit, if set.
     * 
     * @return the jumpToNameAfterSubmit
     */
    public String getJumpToNameAfterSubmit() {
        return this.jumpToNameAfterSubmit;
    }

    /**
     * @param jumpToNameAfterSubmit
     *            the jumpToNameAfterSubmit to set
     */
    public void setJumpToNameAfterSubmit(String jumpToNameAfterSubmit) {
        this.jumpToNameAfterSubmit = jumpToNameAfterSubmit;
    }

    /**
     * The id of the field to place focus on in the new page after the new page
     * is retrieved. Passing in "FIRST" will focus on the first visible input
     * element on the form. Passing in the empty string will result in this
     * ActionField being focused.
     * 
     * @return the focusOnAfterSubmit
     */
    public String getFocusOnAfterSubmit() {
        return this.focusOnAfterSubmit;
    }

    /**
     * @param focusOnAfterSubmit
     *            the focusOnAfterSubmit to set
     */
    public void setFocusOnAfterSubmit(String focusOnAfterSubmit) {
        this.focusOnAfterSubmit = focusOnAfterSubmit;
    }

    /**
     * Client side javascript to be executed when this actionField is clicked.
     * This overrides the default action for this ActionField so the method
     * called must explicitly submit, navigate, etc. through js, if necessary.
     * In addition, this js occurs AFTER onClickScripts set on this field, it
     * will be the last script executed by the click event. Sidenote: This js is
     * always called after hidden actionParameters and methodToCall methods are
     * written by the js to the html form.
     * 
     * @return the clientSideJs
     */
    public String getClientSideJs() {
        return this.clientSideJs;
    }

    /**
     * @param clientSideJs
     *            the clientSideJs to set
     */
    public void setClientSideJs(String clientSideJs) {
        if (!StringUtils.endsWith(clientSideJs, ";")) {
            clientSideJs = clientSideJs + ";";
        }
        this.clientSideJs = clientSideJs;
    }

    /**
     * Setter for the light box direct inquiry widget
     * 
     * @param lightBoxDirectInquiry
     *            <code>LightBox</code> widget to set
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
     * @param blockValidateDirty
     *            the blockValidateDirty to set
     */
    public void setBlockValidateDirty(boolean blockValidateDirty) {
        this.blockValidateDirty = blockValidateDirty;
    }

    /**
     * @return the blockValidateDirty
     */
    public boolean isBlockValidateDirty() {
        return blockValidateDirty;
    }

    public String getActionImageLocation() {
        return actionImageLocation;
    }

    /**
     * Set to TOP, BOTTOM, LEFT, RIGHT to position image at that location within the button.
     * For the subclass ActionLinkField only LEFT and RIGHT are allowed.  When set to blank/null, the image
     * itself will be the ActionField, if no value is set the default is ALWAYS LEFT, you must explicitly set
     * blank/null to use ONLY the image as the ActionField.
     * @return
     */
    public void setActionImageLocation(String actionImageLocation) {
        this.actionImageLocation = actionImageLocation;
    }
}
