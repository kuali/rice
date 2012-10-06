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
package org.kuali.rice.krad.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.validator.ErrorReport;
import org.kuali.rice.krad.datadictionary.validator.RDValidator;
import org.kuali.rice.krad.datadictionary.validator.TracerToken;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Link;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.LightBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Field that encloses a link element
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LinkField extends FieldBase {
    private static final long serialVersionUID = -1908504471910271148L;

    private Link link;

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
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performInitialization(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        if (StringUtils.isBlank(getLinkText())) {
            setLinkText(this.getLabel());
        }
    }

    /**
     * PerformFinalize override - calls super, corrects the field's Label for attribute to point to this field's
     * content
     *
     * @param view the view
     * @param model the model
     * @param parent the parent component
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        //determine what id to use for the for attribute of the label, if present
        if (this.getFieldLabel() != null && this.getLink() != null && StringUtils.isNotBlank(this.getLink().getId())) {
            this.getFieldLabel().setLabelForComponentId(this.getLink().getId());
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(link);

        return components;
    }

    /**
     * Returns the <code>Link<code/> field.
     *
     * @return The Link field
     */
    public Link getLink() {
        return link;
    }

    /**
     * Setter for the <code>Link<code/>  component.
     *
     * @param link
     */
    public void setLink(Link link) {
        this.link = link;
    }

    /**
     * Returns the label of the <code>Link<code/> field that will be used to render the label of the link.
     *
     * @return The link label
     */
    public String getLinkText() {
        return link.getLinkText();
    }

    /**
     * Setter for the link label. Sets the value on the <code>Link<code/> field.
     *
     * @param linkLabel
     */
    public void setLinkText(String linkLabel) {
        link.setLinkText(linkLabel);
    }

    /**
     * Returns the target of the <code>Link<code/> field that will be used to specify where to open the href.
     *
     * @return The target
     */
    public String getTarget() {
        return link.getTarget();
    }

    /**
     * Setter for the link target. Sets the value on the <code>Link<code/> field.
     *
     * @param target
     */
    public void setTarget(String target) {
        link.setTarget(target);
    }

    /**
     * Returns the href text of the <code>Link<code/> field.
     *
     * @return The href text
     */
    public String getHref() {
        return link.getHref();
    }

    /**
     * Setter for the hrefText. Sets the value on the <code>Link<code/> field.
     *
     * @param hrefText
     */
    public void setHref(String hrefText) {
        link.setHref(hrefText);
    }

    /**
     * Setter for the lightBox
     *
     * @param lightBox
     */
    public void setLightBox(LightBox lightBox) {
        if (link != null) {
            link.setLightBox(lightBox);
        }
    }

    /**
     * Returns the <code>LightBox</code> used to open the link in
     *
     * @return The <code>LightBox</code>
     */
    public LightBox getLightBox() {
        if (link != null) {
            return link.getLightBox();
        }

        return null;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#completeValidation
     */
    @Override
    public ArrayList<ErrorReport> completeValidation(TracerToken tracer) {
        ArrayList<ErrorReport> reports = new ArrayList<ErrorReport>();
        tracer.addBean(this);

        // Checks that the link is set
        if (getLink() == null) {
            if (RDValidator.checkExpressions(this, "link")) {
                ErrorReport error = ErrorReport.createError("Link should be set", tracer);
                error.addCurrentValue("link = " + getLink());
                reports.add(error);
            }
        }

        // Checks that the label is set
        if (getLabel() == null) {
            if (RDValidator.checkExpressions(this, "label")) {
                ErrorReport error = ErrorReport.createWarning("Label is null, link should be used instead", tracer);
                error.addCurrentValue("label =" + getLabel());
                error.addCurrentValue("link =" + getLink());
                reports.add(error);
            }
        }

        reports.addAll(super.completeValidation(tracer.getCopy()));

        return reports;
    }

}
