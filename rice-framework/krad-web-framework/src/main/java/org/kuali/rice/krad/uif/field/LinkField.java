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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.element.Link;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.widget.LightBox;

/**
 * Field that encloses a link element.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "linkField", parent = "Uif-LinkField")
public class LinkField extends FieldBase {
    private static final long serialVersionUID = -1908504471910271148L;

    private Link link;

    private String sortAs;

    public LinkField() {
        super();
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Set the linkLabel if blank to the Field label</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        super.performInitialization(model);

        if (StringUtils.isBlank(getLinkText())) {
            setLinkText(this.getLabel());
        }
    }

    /**
     * PerformFinalize override - calls super, corrects the field's Label for attribute to point to this field's
     * content
     *
     * @param model the model
     * @param parent the parent component
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        //determine what id to use for the for attribute of the label, if present
        if (this.getFieldLabel() != null && this.getLink() != null && StringUtils.isNotBlank(this.getLink().getId())) {
            this.getFieldLabel().setLabelForComponentId(this.getLink().getId());
        }
    }

    /**
     * Returns the <code>Link</code> field.
     *
     * @return The Link field
     */
    @BeanTagAttribute(type= BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public Link getLink() {
        return link;
    }

    /**
     * Setter for the <code>Link</code>  component.
     *
     * @param link
     */
    public void setLink(Link link) {
        this.link = link;
    }

    /**
     * Returns the label of the <code>Link</code> field that will be used to render the label of the link.
     *
     * @return The link label
     */
    @BeanTagAttribute
    public String getLinkText() {
        return link.getLinkText();
    }

    /**
     * Setter for the link label. Sets the value on the <code>Link</code> field.
     *
     * @param linkLabel
     */
    public void setLinkText(String linkLabel) {
        link.setLinkText(linkLabel);
    }

    /**
     * The id of the DialogGroup to use when the openInDialog property is true for this LinkField's link.
     *
     * <p>The DialogGroup should only contain an iframe for its items.  When not set, a default dialog
     * will be used.</p>
     *
     * @return the id of the dialog to use for this link
     */
    @BeanTagAttribute
    public String getLinkDialogId() {
        return link.getLinkDialogId();
    }

    /**
     * @see LinkField#getLinkDialogId()
     */
    public void setLinkDialogId(String linkDialogId) {
        link.setLinkDialogId(linkDialogId);
    }

    /**
     * Indicates whether the link's URL should be opened in a dialog.
     *
     * <p>
     * If set the target attribute is ignored and the URL is opened in a dialog instead.
     * </p>
     *
     * @return true to open link in a dialog, false if not (follow standard target attribute)
     */
    @BeanTagAttribute
    public boolean isOpenInDialog() {
        return link.isOpenInDialog();
    }

    /**
     * @see LinkField#isOpenInDialog()
     */
    public void setOpenInDialog(boolean openInDialog) {
        link.setOpenInDialog(openInDialog);
    }


    /**
     * Returns the target of the <code>Link</code> field that will be used to specify where to open the href.
     *
     * @return The target
     */
    @BeanTagAttribute
    public String getTarget() {
        return link.getTarget();
    }

    /**
     * Setter for the link target. Sets the value on the <code>Link</code> field.
     *
     * @param target
     */
    public void setTarget(String target) {
        link.setTarget(target);
    }

    /**
     * Returns the href text of the <code>Link</code> field.
     *
     * @return The href text
     */
    @BeanTagAttribute
    public String getHref() {
        return link.getHref();
    }

    /**
     * Setter for the hrefText. Sets the value on the <code>Link</code> field.
     *
     * @param hrefText
     */
    public void setHref(String hrefText) {
        link.setHref(hrefText);
    }

    @BeanTagAttribute(name = "sortAs")
    public String getSortAs() {
        return sortAs;
    }

    public void setSortAs(String sortAs) {
        if (!(sortAs.equals(UifConstants.TableToolsValues.DATE) || sortAs.equals(UifConstants.TableToolsValues.NUMERIC) || sortAs.equals(UifConstants.TableToolsValues.STRING))) {
            throw new IllegalArgumentException("invalid sortAs value of " + sortAs + ", allowed: " + UifConstants.TableToolsValues.DATE + "|" + UifConstants.TableToolsValues.NUMERIC + "|" + UifConstants.TableToolsValues.STRING);
        }
        this.sortAs = sortAs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Checks that the link is set
        if (getLink() == null) {
            if (Validator.checkExpressions(this, "link")) {
                String currentValues[] = {"link = " + getLink()};
                tracer.createError("Link should be set", currentValues);
            }
        }

        // Checks that the label is set
        if (getLabel() == null) {
            if (Validator.checkExpressions(this, "label")) {
                String currentValues[] = {"label =" + getLabel(), "link =" + getLink()};
                tracer.createWarning("Label is null, link should be used instead", currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }
}
