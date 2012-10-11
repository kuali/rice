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

import org.kuali.rice.krad.datadictionary.validator.ErrorReport;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
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
    public void completeValidation(ValidationTrace tracer){
        ArrayList<ErrorReport> reports=new ArrayList<ErrorReport>();
        tracer.addBean(this);

        if(tracer.getValidationStage()== ValidationTrace.BUILD){

            // Checks that href is set
            if(getHref()==null){
                if(!Validator.checkExpressions(this, "href")){
                    String currentValues [] = {"href ="+getHref()};
                    tracer.createError("Href must be set",currentValues);
                }
            }

            // Checks that the text is set
            if(getLinkText()==null){
                if(!Validator.checkExpressions(this, "linkText")){
                    String currentValues [] = {"linkText = "+getLinkText()};
                    tracer.createError("LinkText must be set",currentValues);
                }
            }

        }

        super.completeValidation(tracer.getCopy());
    }
}
