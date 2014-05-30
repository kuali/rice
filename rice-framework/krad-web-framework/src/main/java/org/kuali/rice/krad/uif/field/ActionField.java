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
package org.kuali.rice.krad.uif.field;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.validator.ErrorReport;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.Image;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Field that encloses an @{link org.kuali.rice.krad.uif.element.Action} element
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "actionField", parent = "Uif-ActionField"),
        @BeanTag(name = "actionLinkField", parent = "Uif-ActionLinkField")})
public class ActionField extends FieldBase {
    private static final long serialVersionUID = -8495752159848603102L;

    private Action action;

    /**
     * Initializes wrapped action instance.
     */
    public ActionField() {
        action = new Action();
    }

    /**
     * PerformFinalize override - calls super, corrects the field's Label for attribute to point to this field's
     * content.
     *
     * @param model the model
     * @param parent the parent component
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        //determine what id to use for the for attribute of the label, if present
        if (this.getFieldLabel() != null && this.getAction() != null && StringUtils.isNotBlank(
                this.getAction().getId())) {
            this.getFieldLabel().setLabelForComponentId(this.getAction().getId());
        }
    }

    /**
     * Nested action component.
     *
     * @return Action instance
     */
    @BeanTagAttribute
    public Action getAction() {
        return action;
    }

    /**
     * Setter for the nested action component.
     *
     * @param action property value
     */
    public void setAction(Action action) {
        this.action = action;
    }

    /**
     * Delegates {@code methodToCall} property reference to the action.
     * 
     * @return method to call
     * @see org.kuali.rice.krad.uif.element.Action#getMethodToCall()
     */
    @BeanTagAttribute
    public String getMethodToCall() {
        return action.getMethodToCall();
    }

    /**
     * Delegates {@code methodToCall} property reference to the action.
     * 
     * @param methodToCall method to call
     * @see org.kuali.rice.krad.uif.element.Action#setMethodToCall(java.lang.String)
     */
    public void setMethodToCall(String methodToCall) {
        action.setMethodToCall(methodToCall);
    }

    /**
     * Delegates {@code actionLabel} property reference to the action.
     * 
     * @return action label
     * @see org.kuali.rice.krad.uif.element.Action#getActionLabel()
     */
    @BeanTagAttribute
    public String getActionLabel() {
        return action.getActionLabel();
    }

    /**
     * Delegates {@code actionLabel} property reference to the action.
     * 
     * @param actionLabel action label
     * @see org.kuali.rice.krad.uif.element.Action#setActionLabel(java.lang.String)
     */
    public void setActionLabel(String actionLabel) {
        action.setActionLabel(actionLabel);
    }

    /**
     * Delegates {@code actionImage} property reference to the action.
     * 
     * @return action image
     * @see org.kuali.rice.krad.uif.element.Action#getActionImage()
     */
    @ViewLifecycleRestriction
    @BeanTagAttribute
    public Image getActionImage() {
        return action.getActionImage();
    }

    /**
     * Delegates {@code actionImage} property reference to the action.
     * 
     * @param actionImage action image
     * @see org.kuali.rice.krad.uif.element.Action#setActionImage(org.kuali.rice.krad.uif.element.Image)
     */
    public void setActionImage(Image actionImage) {
        action.setActionImage(actionImage);
    }

    /**
     * Delegates to {@link org.kuali.rice.krad.uif.element.Action#getNavigateToPageId()}.
     *
     * @return page ID to navigate to
     */
    @BeanTagAttribute
    public String getNavigateToPageId() {
        return action.getNavigateToPageId();
    }

    /**
     * Setter for {@link Action#setNavigateToPageId(java.lang.String)}.
     *
     * @param navigateToPageId property value
     */
    public void setNavigateToPageId(String navigateToPageId) {
        action.setNavigateToPageId(navigateToPageId);
    }

    /**
     * Delegates to {@link org.kuali.rice.krad.uif.element.Action#getActionEvent()}.
     *
     * @return action event
     */
    @BeanTagAttribute
    public String getActionEvent() {
        return action.getActionEvent();
    }

    /**
     * Setter for {@link org.kuali.rice.krad.uif.element.Action#setActionEvent(java.lang.String)}.
     *
     * @param actionEvent property value
     */
    public void setActionEvent(String actionEvent) {
        action.setActionEvent(actionEvent);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getActionParameters()
     */
    @BeanTagAttribute
    public Map<String, String> getActionParameters() {
        return action.getActionParameters();
    }

    /**
     * Setter for {@link #getActionParameters()}
     * 
     * @param actionParameters action parameters
     */
    public void setActionParameters(Map<String, String> actionParameters) {
        action.setActionParameters(actionParameters);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getAdditionalSubmitData()
     */
    @BeanTagAttribute
    public Map<String, String> getAdditionalSubmitData() {
        return action.getAdditionalSubmitData();
    }

    /**
     * Setter for {@link #getAdditionalSubmitData()}
     *
     * @param additionalSubmitData property value
     */
    public void setAdditionalSubmitData(Map<String, String> additionalSubmitData) {
        action.setAdditionalSubmitData(additionalSubmitData);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#addActionParameter(java.lang.String, java.lang.String)
     */
    public void addActionParameter(String parameterName, String parameterValue) {
        action.addActionParameter(parameterName, parameterValue);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getActionParameter(java.lang.String)
     */
    public String getActionParameter(String parameterName) {
        return action.getActionParameter(parameterName);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getJumpToIdAfterSubmit()
     */
    @BeanTagAttribute
    public String getJumpToIdAfterSubmit() {
        return action.getJumpToIdAfterSubmit();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setJumpToIdAfterSubmit(java.lang.String)
     */

    public void setJumpToIdAfterSubmit(String jumpToIdAfterSubmit) {
        action.setJumpToIdAfterSubmit(jumpToIdAfterSubmit);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getJumpToNameAfterSubmit()
     */
    @BeanTagAttribute
    public String getJumpToNameAfterSubmit() {
        return action.getJumpToNameAfterSubmit();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setJumpToNameAfterSubmit(java.lang.String)
     */
    public void setJumpToNameAfterSubmit(String jumpToNameAfterSubmit) {
        action.setJumpToNameAfterSubmit(jumpToNameAfterSubmit);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getFocusOnIdAfterSubmit()
     */
    @BeanTagAttribute
    public String getFocusOnIdAfterSubmit() {
        return action.getFocusOnIdAfterSubmit();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setFocusOnIdAfterSubmit(java.lang.String)
     */
    public void setFocusOnIdAfterSubmit(String focusOnAfterSubmit) {
        action.setFocusOnIdAfterSubmit(focusOnAfterSubmit);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#isPerformClientSideValidation()
     */
    @BeanTagAttribute
    public boolean isPerformClientSideValidation() {
        return action.isPerformClientSideValidation();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setPerformClientSideValidation(boolean)
     */
    public void setPerformClientSideValidation(boolean clientSideValidate) {
        action.setPerformClientSideValidation(clientSideValidate);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getActionScript()
     */
    @BeanTagAttribute
    public String getActionScript() {
        return action.getActionScript();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setActionScript(java.lang.String)
     */
    public void setActionScript(String actionScript) {
        action.setActionScript(actionScript);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#isPerformDirtyValidation()
     */
    @BeanTagAttribute
    public boolean isPerformDirtyValidation() {
        return action.isPerformDirtyValidation();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setPerformDirtyValidation(boolean)
     */
    public void setPerformDirtyValidation(boolean blockValidateDirty) {
        action.setPerformDirtyValidation(blockValidateDirty);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#isDisabled()
     */
    @BeanTagAttribute
    public boolean isDisabled() {
        return action.isDisabled();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setDisabled(boolean)
     */
    public void setDisabled(boolean disabled) {
        action.setDisabled(disabled);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getDisabledReason()
     */
    @BeanTagAttribute
    public String getDisabledReason() {
        return action.getDisabledReason();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setDisabledReason(java.lang.String)
     */
    public void setDisabledReason(String disabledReason) {
        action.setDisabledReason(disabledReason);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getActionImagePlacement()
     */
    @BeanTagAttribute
    public String getActionImagePlacement() {
        return action.getActionImagePlacement();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setActionImagePlacement(java.lang.String)
     */
    public void setActionImagePlacement(String actionImageLocation) {
        action.setActionImagePlacement(actionImageLocation);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getPreSubmitCall()
     */
    @BeanTagAttribute
    public String getPreSubmitCall() {
        return action.getPreSubmitCall();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setPreSubmitCall(java.lang.String)
     */
    public void setPreSubmitCall(String preSubmitCall) {
        action.setPreSubmitCall(preSubmitCall);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#isAjaxSubmit()
     */
    @BeanTagAttribute
    public boolean isAjaxSubmit() {
        return action.isAjaxSubmit();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setAjaxSubmit(boolean)
     */
    public void setAjaxSubmit(boolean ajaxSubmit) {
        action.setAjaxSubmit(ajaxSubmit);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getSuccessCallback()
     */
    @BeanTagAttribute
    public String getSuccessCallback() {
        return action.getSuccessCallback();
    }

    /**
     * @param successCallback
     * @see org.kuali.rice.krad.uif.element.Action#setSuccessCallback(java.lang.String)
     */
    public void setSuccessCallback(String successCallback) {
        action.setSuccessCallback(successCallback);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getErrorCallback()
     */
    @BeanTagAttribute
    public String getErrorCallback() {
        return action.getErrorCallback();
    }

    /**
     * @param errorCallback
     * @see org.kuali.rice.krad.uif.element.Action#setErrorCallback(java.lang.String)
     */
    public void setErrorCallback(String errorCallback) {
        action.setErrorCallback(errorCallback);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getRefreshId()
     */
    @BeanTagAttribute
    public String getRefreshId() {
        return action.getRefreshId();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setRefreshId(java.lang.String)
     */
    public void setRefreshId(String refreshId) {
        action.setRefreshId(refreshId);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#isDisableBlocking()
     */
    @BeanTagAttribute
    public boolean isDisableBlocking() {
        return action.isDisableBlocking();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setDisableBlocking(boolean)
     */
    public void setDisableBlocking(boolean disableBlocking) {
        action.setDisableBlocking(disableBlocking);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        ArrayList<ErrorReport> reports = new ArrayList<ErrorReport>();
        tracer.addBean(this);

        // Checks that the action is set
        if (getAction() == null) {
            if (Validator.checkExpressions(this, "action")) {
                String currentValues[] = {"action =" + getAction()};
                tracer.createWarning("Action should not be null", currentValues);
            }
        }

        // checks that the label is set
        if (getLabel() == null) {
            if (Validator.checkExpressions(this, "label")) {
                String currentValues[] = {"label =" + getLabel(), "action =" + getAction()};
                tracer.createWarning("Label is null, action should be used instead", currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }
}
