package org.kuali.rice.krad.uif.field;

import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.ActionSecurity;
import org.kuali.rice.krad.uif.widget.LightBox;

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
    public ImageField getActionImage() {
        return action.getActionImage();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setActionImage(org.kuali.rice.krad.uif.field.ImageField)
     */
    public void setActionImage(ImageField actionImage) {
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
     * @see org.kuali.rice.krad.uif.element.Action#getFocusOnAfterSubmit()
     */
    public String getFocusOnAfterSubmit() {
        return action.getFocusOnAfterSubmit();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setFocusOnAfterSubmit(java.lang.String)
     */
    public void setFocusOnAfterSubmit(String focusOnAfterSubmit) {
        action.setFocusOnAfterSubmit(focusOnAfterSubmit);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#isClientSideValidate()
     */
    public boolean isClientSideValidate() {
        return action.isClientSideValidate();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setClientSideValidate(boolean)
     */
    public void setClientSideValidate(boolean clientSideValidate) {
        action.setClientSideValidate(clientSideValidate);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#getClientSideJs()
     */
    public String getClientSideJs() {
        return action.getClientSideJs();
    }

    /**
     * @seeorg.kuali.rice.krad.uif.element.Action#setClientSideJs(java.lang.String)
     */
    public void setClientSideJs(String clientSideJs) {
        action.setClientSideJs(clientSideJs);
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
     * @see org.kuali.rice.krad.uif.element.Action#setBlockValidateDirty(boolean)
     */
    public void setBlockValidateDirty(boolean blockValidateDirty) {
        action.setBlockValidateDirty(blockValidateDirty);
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#isBlockValidateDirty()
     */
    public boolean isBlockValidateDirty() {
        return action.isBlockValidateDirty();
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
     * @see org.kuali.rice.krad.uif.element.Action#getActionImageLocation()
     */
    public String getActionImageLocation() {
        return action.getActionImageLocation();
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Action#setActionImageLocation(java.lang.String)
     */
    public void setActionImageLocation(String actionImageLocation) {
        action.setActionImageLocation(actionImageLocation);
    }
}
