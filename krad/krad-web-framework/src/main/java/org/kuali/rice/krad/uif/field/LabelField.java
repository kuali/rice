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

import org.kuali.rice.krad.uif.UifConstants.Position;
import org.kuali.rice.krad.uif.component.Component;

import java.util.List;

/**
 * Contains a label for another <code>Field</code> instance
 * <p>
 * The <code>LabelField</code> exists so that the label can be placed separate
 * from the component in a layout manager such as the
 * <code>GridLayoutManager</code>. It addition it can be used to style the label
 * (from the inherited styleClass and style properties)
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabelField extends FieldBase {
    private static final long serialVersionUID = -6491546893195180114L;

    private String labelText;
    private String labelForComponentId;

    private boolean renderColon;

    private String requiredMessagePlacement;
    private MessageField requiredMessageField;

    public LabelField() {
        renderColon = true;

        requiredMessagePlacement = Position.LEFT;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getNestedComponents()
     */
    @Override
    public List<Component> getNestedComponents() {
        List<Component> components = super.getNestedComponents();

        components.add(requiredMessageField);

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
     * <code>MessageField</code> instance that will display a required indicator
     * 
     * <p>
     * To indicate a field must have a value (required input) the required
     * message field can be set to display an indicator or message along with
     * the label. The message field also dictates the styling of the required
     * message
     * </p>
     * 
     * @return MessageField instance
     */
    public MessageField getRequiredMessageField() {
        return this.requiredMessageField;
    }

    /**
     * Setter for the required message field
     * 
     * @param requiredMessageField
     */
    public void setRequiredMessageField(MessageField requiredMessageField) {
        this.requiredMessageField = requiredMessageField;
    }

    /**
     * Indicates where the required message field should be placed in relation
     * to the label field, valid options are 'LEFT' and 'RIGHT'
     * 
     * @return the requiredMessagePlacement
     */
    public String getRequiredMessagePlacement() {
        return this.requiredMessagePlacement;
    }

    /**
     * Setter for the required message field placement
     * 
     * @param requiredMessagePlacement
     */
    public void setRequiredMessagePlacement(String requiredMessagePlacement) {
        this.requiredMessagePlacement = requiredMessagePlacement;
    }

}
