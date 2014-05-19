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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifConstants.Position;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentBase;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.component.DelayedCopy;
import org.kuali.rice.krad.uif.element.Label;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.MessageStructureUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADUtils;

/**
 * Base class for <code>Field</code> implementations
 *
 * <p>
 * Sets the component type name so that all field templates have a fixed
 * contract
 * </p>
 *
 * <p>
 * Holds a nested <code>Label</code> with configuration for rendering the
 * label and configuration on label placement.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "fieldBase-bean", parent = "Uif-FieldBase"),
    @BeanTag(name = "fieldBase-withLabel-bean", parent = "Uif-FieldBase-withLabel")})
public class FieldBase extends ComponentBase implements Field {
    private static final long serialVersionUID = -5888414844802862760L;

    private String shortLabel;
    
    @DelayedCopy
    private Label fieldLabel;
    
    private boolean labelRendered;

    public FieldBase() {
        super();

        labelRendered = false;
    }

    /**
     * The following finalization is performed:
     *
     * <ul>
     * <li>Set the labelForComponentId to this component id</li>
     * <li>Set the label text on the label field from the field's label property
     * </li>
     * <li>Set the render property on the label's required message field if this
     * field is marked as required</li>
     * <li>If label placement is right, set render colon to false</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        if (fieldLabel != null) {
            fieldLabel.setLabelForComponentId(this.getId());

            if ((getRequired() != null) && getRequired().booleanValue()) {
                View view = ViewLifecycle.getView();
                if (view.getViewTypeName() != null && view.getViewTypeName().equals(UifConstants.ViewType.MAINTENANCE)) {
                    fieldLabel.setRenderRequiredIndicator(!view.isReadOnly());
                } else {
                    fieldLabel.setRenderRequiredIndicator(!isReadOnly());
                }
            } else {
                setRequired(false);
                fieldLabel.setRenderRequiredIndicator(false);
            }

            fieldLabel.addStyleClass("uif-labelBlock");


            fieldLabel.addDataAttribute(UifConstants.DataAttributes.LABEL_FOR, this.getId());
            if(StringUtils.isNotBlank(this.getFieldLabel().getLabelText())){
                this.addDataAttribute(UifConstants.DataAttributes.LABEL,
                        MessageStructureUtils.translateStringMessage(this.getFieldLabel().getLabelText()));
            }
        }
    }

    /**
     * Helper method for suffixing the ids of the fields nested components
     *
     * @param component component to adjust id for
     * @param suffix suffix to append to id
     */
    protected void setNestedComponentIdAndSuffix(Component component, String suffix) {
        if (component != null) {
            String fieldId = getId();
            fieldId += suffix;

            component.setId(fieldId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getComponentTypeName() {
        return "field";
    }

    /**
     * @see org.kuali.rice.krad.uif.field.Field#getLabel
     */
    @BeanTagAttribute(name = "label")
    public String getLabel() {
        if (fieldLabel != null) {
            return fieldLabel.getLabelText();
        }

        return "";
    }

    /**
     * @see org.kuali.rice.krad.uif.field.Field#setLabel(java.lang.String)
     */
    public void setLabel(String labelText) {
        if (StringUtils.isNotBlank(labelText) && this.fieldLabel == null) {
            this.fieldLabel = ComponentFactory.getLabel();
        }

        if (this.fieldLabel != null) {
            this.fieldLabel.setLabelText(labelText);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.field.Field#getLabelStyleClasses
     */
    @BeanTagAttribute(name="labelStyleClasses",type= BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getLabelStyleClasses() {
        if (fieldLabel != null) {
            return fieldLabel.getCssClasses();
        }

        return null;
    }

    /**
     * @see org.kuali.rice.krad.uif.field.Field#setLabelStyleClasses
     */
    public void setLabelStyleClasses(List<String> labelStyleClasses) {
        if (labelStyleClasses != null && this.fieldLabel == null) {
            this.fieldLabel = ComponentFactory.getLabel();
        }

        if (this.fieldLabel != null) {
            this.fieldLabel.setCssClasses(labelStyleClasses);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.field.Field#getLabelColSpan
     */
    @BeanTagAttribute(name="labelColSpan")
    public int getLabelColSpan() {
        if (fieldLabel != null) {
            return fieldLabel.getColSpan();
        }

        return 1;
    }

    /**
     * @see org.kuali.rice.krad.uif.field.Field#setLabelColSpan
     */
    public void setLabelColSpan(int labelColSpan) {
        if (this.fieldLabel == null) {
            this.fieldLabel = ComponentFactory.getLabel();
        }

        if (this.fieldLabel != null) {
            this.fieldLabel.setColSpan(labelColSpan);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.field.Field#getShortLabel()
     */
    @BeanTagAttribute(name="shortLabel")
    public String getShortLabel() {
        return this.shortLabel;
    }

    /**
     * @see org.kuali.rice.krad.uif.field.Field#setShortLabel(java.lang.String)
     */
    public void setShortLabel(String shortLabel) {
        this.shortLabel = shortLabel;
    }

    /**
     * Sets whether the label should be displayed
     *
     * <p>
     * Convenience method for configuration that sets the render indicator on
     * the fields <code>Label</code> instance
     * </p>
     *
     * @param showLabel true if label should be displayed, false if the label
     * should not be displayed
     */
    public void setShowLabel(boolean showLabel) {
        if (fieldLabel != null) {
            fieldLabel.setRender(showLabel);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.field.Field#getLabel
     */
    @ViewLifecycleRestriction
    @BeanTagAttribute(name="fieldLabel",type= BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Label getFieldLabel() {
        return this.fieldLabel;
    }

    /**
     * @see org.kuali.rice.krad.uif.field.Field#getLabel
     */
    public Label getFieldLabelIfNotRendered() {
        return isLabelRendered() ? null : this.fieldLabel;
    }

    /**
     * @see org.kuali.rice.krad.uif.field.Field#setFieldLabel
     */
    public void setFieldLabel(Label fieldLabel) {
        this.fieldLabel = fieldLabel;
    }


    /**
     * @see org.kuali.rice.krad.uif.field.Field#isLabelRendered()
     */
    @BeanTagAttribute(name="labelRendered")
    public boolean isLabelRendered() {
        return this.labelRendered;
    }

    /**
     * @see org.kuali.rice.krad.uif.field.Field#setLabelRendered(boolean)
     */
    public void setLabelRendered(boolean labelRendered) {
        this.labelRendered = labelRendered;
    }
    
    /**
     * @see org.kuali.rice.krad.uif.field.Field#getFieldSecurity()
     */
    public FieldSecurity getFieldSecurity() {
        return (FieldSecurity) super.getComponentSecurity();
    }

    /**
     * Override to assert a {@link FieldSecurity} instance is set
     *
     * @param componentSecurity instance of FieldSecurity
     */
    @Override
    public void setComponentSecurity(ComponentSecurity componentSecurity) {
        if ((componentSecurity != null) && !(componentSecurity instanceof FieldSecurity)) {
            throw new RiceRuntimeException("Component security for Field should be instance of FieldSecurity");
        }

        super.setComponentSecurity(componentSecurity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeComponentSecurity() {
        if (getComponentSecurity() == null) {
            setComponentSecurity(KRADUtils.createNewObjectFromClass(FieldSecurity.class));
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.field.FieldSecurity#isEditInLineAuthz()
     */
    public Boolean isEditInLineAuthz() {
        initializeComponentSecurity();

        return getFieldSecurity().isEditInLineAuthz();
    }

    /**
     * @see org.kuali.rice.krad.uif.field.FieldSecurity#setEditInLineAuthz(Boolean)
     */
    public void setEditInLineAuthz(Boolean editInLineAuthz) {
        initializeComponentSecurity();

        getFieldSecurity().setEditInLineAuthz(editInLineAuthz);
    }

    /**
     * @see org.kuali.rice.krad.uif.field.FieldSecurity#isViewInLineAuthz()
     */
    public Boolean isViewInLineAuthz() {
        initializeComponentSecurity();

        return getFieldSecurity().isViewInLineAuthz();
    }

    /**
     * @see org.kuali.rice.krad.uif.field.FieldSecurity#setViewInLineAuthz(Boolean)
     */
    public void setViewInLineAuthz(Boolean viewInLineAuthz) {
        initializeComponentSecurity();

        getFieldSecurity().setViewInLineAuthz(viewInLineAuthz);
    }

}
