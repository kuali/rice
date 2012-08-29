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
import org.kuali.rice.krad.ricedictionaryvalidator.ErrorReport;
import org.kuali.rice.krad.ricedictionaryvalidator.RDValidator;
import org.kuali.rice.krad.ricedictionaryvalidator.TracerToken;
import org.kuali.rice.krad.ricedictionaryvalidator.XmlBeanParser;
import org.kuali.rice.krad.uif.UifConstants.Position;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Content element that renders a label
 *
 * <p>
 * Contains options for adding a colon to the label along with a required message
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Label extends ContentElementBase {
    private static final long serialVersionUID = -6491546893195180114L;

    private String labelText;
    private String labelForComponentId;

    private boolean renderColon;

    private Position requiredMessagePlacement;
    private Message requiredMessage;

    private Message richLabelMessage;
    private List<Component> inlineComponents;

    public Label() {
        renderColon = true;

        requiredMessagePlacement = Position.LEFT;
    }

    /**
     * Sets up rich message content for the label, if any exists
     *
     * @see Component#performApplyModel(org.kuali.rice.krad.uif.view.View, Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performApplyModel(View view, Object model, Component parent) {
        super.performApplyModel(view, model, parent);

        if (richLabelMessage == null
                && labelText != null
                && labelText.contains(KRADConstants.MessageParsing.LEFT_TOKEN)
                &&
                labelText.contains(KRADConstants.MessageParsing.RIGHT_TOKEN)) {
            Message message = ComponentFactory.getMessage();
            view.assignComponentIds(message);
            message.setMessageText(labelText);
            message.setInlineComponents(inlineComponents);
            message.setGenerateSpan(false);
            view.getViewHelperService().performComponentInitialization(view, model, message);
            this.setRichLabelMessage(message);
        }
    }

    /**
     * The following finalization is performed:
     *
     * <ul>
     * <li>If label text is blank, set render to false for field</li>
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        if (StringUtils.isBlank(getLabelText())) {
            setRender(false);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(requiredMessage);
        components.add(richLabelMessage);

        return components;
    }

    /**
     * Indicates the id for the component the label applies to
     * <p>
     * Used for setting the labelFor attribute of the corresponding HTML
     * element. Note this gets set automatically by the framework during the
     * initialize phase
     * </p>
     *
     * @return String component id
     */
    public String getLabelForComponentId() {
        return this.labelForComponentId;
    }

    /**
     * Setter for the component id the label applies to
     *
     * @param labelForComponentId
     */
    public void setLabelForComponentId(String labelForComponentId) {
        this.labelForComponentId = labelForComponentId;
    }

    /**
     * Text that will display as the label
     *
     * @return String label text
     */
    public String getLabelText() {
        return this.labelText;
    }

    /**
     * Setter for the label text
     *
     * @param labelText
     */
    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    /**
     * Indicates whether a colon should be rendered after the label text,
     * generally used when the label appears to the left of the field's control
     * or value
     *
     * @return boolean true if a colon should be rendered, false if it should
     *         not be
     */
    public boolean isRenderColon() {
        return this.renderColon;
    }

    /**
     * Setter for the render colon indicator
     *
     * @param renderColon
     */
    public void setRenderColon(boolean renderColon) {
        this.renderColon = renderColon;
    }

    /**
     * <code>Message</code> instance that will display a required indicator
     *
     * <p>
     * To indicate a field must have a value (required input) the required
     * message field can be set to display an indicator or message along with
     * the label. The message field also dictates the styling of the required
     * message
     * </p>
     *
     * @return Message instance
     */
    public Message getRequiredMessage() {
        return this.requiredMessage;
    }

    /**
     * Setter for the required message field
     *
     * @param requiredMessage
     */
    public void setRequiredMessage(Message requiredMessage) {
        this.requiredMessage = requiredMessage;
    }

    /**
     * Indicates where the required message field should be placed in relation
     * to the label field, valid options are 'LEFT' and 'RIGHT'
     *
     * @return Position the requiredMessage placement
     */
    public Position getRequiredMessagePlacement() {
        return this.requiredMessagePlacement;
    }

    /**
     * Setter for the required message field placement
     *
     * @param requiredMessagePlacement
     */
    public void setRequiredMessagePlacement(Position requiredMessagePlacement) {
        this.requiredMessagePlacement = requiredMessagePlacement;
    }

    /**
     * Gets the Message that represents the rich message content of the label if labelText is using rich message tags.
     * <b>DO NOT set this
     * property directly unless you need full control over the message structure.</b>
     *
     * @return Message with rich message structure, null if no rich message structure
     */
    public Message getRichLabelMessage() {
        return richLabelMessage;
    }

    /**
     * Sets the Message that represents the rich message content of the label if it is using rich message tags.  <b>DO
     * NOT set this
     * property directly unless you need full control over the message structure.</b>
     *
     * @param richLabelMessage
     */
    public void setRichLabelMessage(Message richLabelMessage) {
        this.richLabelMessage = richLabelMessage;
    }

    /**
     * Gets the inlineComponents used by index in a Label that has rich message component index tags in its labelText
     *
     * @return the Label's inlineComponents
     */
    public List<Component> getInlineComponents() {
        return inlineComponents;
    }

    /**
     * Sets the inlineComponents used by index in a Label that has rich message component index tags in its labelText
     *
     * @param inlineComponents
     */
    public void setInlineComponents(List<Component> inlineComponents) {
        this.inlineComponents = inlineComponents;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#completeValidation
     */
    @Override
    public ArrayList<ErrorReport> completeValidation(TracerToken tracer, XmlBeanParser parser){
        ArrayList<ErrorReport> reports=new ArrayList<ErrorReport>();
        tracer.addBean(this);

        // Checks that text is set if the component is rendered
        if(isRender() && getLabelText()==null){
            if(!RDValidator.checkExpressions(this)) {
                ErrorReport error = new ErrorReport(ErrorReport.ERROR);
                error.setValidationFailed("LabelText should be set if render is true");
                error.setBeanLocation(tracer.getBeanLocation());
                error.addCurrentValue("render = "+isRender());
                error.addCurrentValue("labelText ="+getLabelText());
                reports.add(error);
            }
        }

        reports.addAll(super.completeValidation(tracer.getCopy(),parser));

        return reports;
    }
}
