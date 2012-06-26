package org.kuali.rice.krad.uif.field;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.ActionSecurity;
import org.kuali.rice.krad.uif.element.Image;
import org.kuali.rice.krad.uif.widget.LightBox;

import java.util.List;
import java.util.Map;

/**
 * Field that encloses an @{link org.kuali.rice.krad.uif.element.Action} element
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionField extends FieldBase {
    private static final long serialVersionUID = -8495752159848603102L;

    private Action action;

    public ActionField() {
        action = new Action();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(action);

        return components;
    }

    /**
     * Nested action component
     *
     * @return Action instance
     */
    public Action getAction() {
        return action;
    }

    /**
     * Setter for the nested action component
     *
     * @param action
     */
    public void setAction(Action action) {
        this.action = action;
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getMethodToCall()
     */
    public String getMethodToCall() {
        return action.getMethodToCall();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setMethodToCall(java.lang.String)
     */
    public void setMethodToCall(String methodToCall) {
        action.setMethodToCall(methodToCall);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getActionLabel()
     */
    public String getActionLabel() {
        return action.getActionLabel();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setActionLabel(java.lang.String)
     */
    public void setActionLabel(String actionLabel) {
        action.setActionLabel(actionLabel);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getActionImage()
     */
    public Image getActionImage() {
        return action.getActionImage();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setActionImage(org.kuali.rice.krad.uif.element.Image)
     */
    public void setActionImage(Image actionImage) {
        action.setActionImage(actionImage);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getNavigateToPageId()
     */
    public String getNavigateToPageId() {
        return action.getNavigateToPageId();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setNavigateToPageId(java.lang.String)
     */
    public void setNavigateToPageId(String navigateToPageId) {
        action.setNavigateToPageId(navigateToPageId);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getActionEvent()
     */
    public String getActionEvent() {
        return action.getActionEvent();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setActionEvent(java.lang.String)
     */
    public void setActionEvent(String actionEvent) {
        action.setActionEvent(actionEvent);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getActionParameters()
     */
    public Map<String, String> getActionParameters() {
        return action.getActionParameters();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setActionParameters(java.util.Map<java.lang.String,java.lang.String>)
     */
    public void setActionParameters(Map<String, String> actionParameters) {
        action.setActionParameters(actionParameters);
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
     * @see org.kuali.rice.krad.uif.element.Action#getComponentSecurity()
     */
    public ActionSecurity getComponentSecurity() {
        return action.getComponentSecurity();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setComponentSecurity(org.kuali.rice.krad.uif.component.ComponentSecurity)
     */
    public void setComponentSecurity(ComponentSecurity componentSecurity) {
        action.setComponentSecurity(componentSecurity);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setLightBoxLookup(org.kuali.rice.krad.uif.widget.LightBox)
     */
    public void setLightBoxLookup(LightBox lightBoxLookup) {
        action.setLightBoxLookup(lightBoxLookup);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getLightBoxLookup()
     */
    public LightBox getLightBoxLookup() {
        return action.getLightBoxLookup();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getJumpToIdAfterSubmit()
     */
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
    public String getActionScript() {
        return action.getActionScript();
    }

    /**
     * @seeorg.kuali.rice.krad.uif.element.Action#setactionScript(java.lang.String)
     */
    public void setActionScript(String actionScript) {
        action.setActionScript(actionScript);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setLightBoxDirectInquiry(org.kuali.rice.krad.uif.widget.LightBox)
     */
    public void setLightBoxDirectInquiry(LightBox lightBoxDirectInquiry) {
        action.setLightBoxDirectInquiry(lightBoxDirectInquiry);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getLightBoxDirectInquiry()
     */
    public LightBox getLightBoxDirectInquiry() {
        return action.getLightBoxDirectInquiry();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#isPerformDirtyValidation()
     */
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
     *
     * @return
     */
    public String getSuccessCallback() {
        return action.getSuccessCallback();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setSuccessCallback(java.lang.String)
     *
     * @param successCallback
     */
    public void setSuccessCallback(String successCallback) {
        action.setSuccessCallback(successCallback);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getErrorCallback()
     *
     * @return
     */
    public String getErrorCallback() {
        return action.getErrorCallback();
    }
    /**
     * @see org.kuali.rice.krad.uif.element.Action#setErrorCallback(java.lang.String)
     *
     * @param errorCallback
     */

    public void setErrorCallback(String errorCallback) {
        action.setErrorCallback(errorCallback);
    }
}
