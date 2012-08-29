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

import org.kuali.rice.krad.ricedictionaryvalidator.ErrorReport;
import org.kuali.rice.krad.ricedictionaryvalidator.RDValidator;
import org.kuali.rice.krad.ricedictionaryvalidator.TracerToken;
import org.kuali.rice.krad.ricedictionaryvalidator.XmlBeanParser;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.widget.LightBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Content element that renders a link
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Link extends ContentElementBase {
    private static final long serialVersionUID = 8989868231938336068L;

    private String linkText;
    private String target;
    private String href;

    private LightBox lightBox;

    public Link() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(lightBox);

        return components;
    }

    /**
     * Returns the label of the link
     *
     * @return The link label
     */
    public String getLinkText() {
        return linkText;
    }

    /**
     * Setter for the link label
     *
     * @param linkText
     */
    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    /**
     * Returns the target that will be used to specify where to open the href
     *
     * @return The target
     */
    public String getTarget() {
        return target;
    }

    /**
     * Setter for the link target
     *
     * @param target
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Returns the href text
     *
     * @return The href text
     */
    public String getHref() {
        return href;
    }

    /**
     * Setter for the hrefText
     *
     * @param href
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Returns the <code>LightBox</code> used to open the link in
     *
     * @return The <code>LightBox</code>
     */
    public LightBox getLightBox() {
        return lightBox;
    }

    /**
     * Setter for the lightBox
     *
     * @param lightBox
     */
    public void setLightBox(LightBox lightBox) {
        this.lightBox = lightBox;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#completeValidation
     */
    @Override
    public ArrayList<ErrorReport> completeValidation(TracerToken tracer, XmlBeanParser parser){
        ArrayList<ErrorReport> reports=new ArrayList<ErrorReport>();
        tracer.addBean(this);

        // Checks that href is set
        if(getHref()==null){
            if(!RDValidator.checkExpressions(this)){
                ErrorReport error = new ErrorReport(ErrorReport.ERROR);
                error.setValidationFailed("Href must be set");
                error.setBeanLocation(tracer.getBeanLocation());
                error.addCurrentValue("href ="+getHref());
                error.addCurrentValue("ExpressionGraph = "+getExpressionGraph());
                error.addCurrentValue("ExpressionProperty = "+getPropertyExpressions());
                error.addCurrentValue("ExpressionRender = "+getRefreshExpressionGraph());
                reports.add(error);
            }
        }

        // Checks that the text is set
        if(getLinkText()==null){
            if(!RDValidator.checkExpressions(this)){
                ErrorReport error = new ErrorReport(ErrorReport.ERROR);
                error.setValidationFailed("LinkText must be set");
                error.setBeanLocation(tracer.getBeanLocation());
                error.addCurrentValue("linkText ="+getLinkText());
                reports.add(error);
            }
        }

        reports.addAll(super.completeValidation(tracer.getCopy(),parser));

        return reports;
    }
}
