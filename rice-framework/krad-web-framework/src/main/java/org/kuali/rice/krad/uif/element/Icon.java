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
package org.kuali.rice.krad.uif.element;

import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;

import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;

/**
 * Element that presents an Icon.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "icon", parent = "Uif-Icon")})
public class Icon extends ContentElementBase {
    private static final long serialVersionUID = 6771810101056598912L;

    private String iconClass;

    public Icon() {
        super();
    }

    /**
     * Icon Class for the action
     *
     * <p>
     * Bootstrap Icon Class to be rendered on this Action
     * </p>
     *
     * @return label for action
     */
    @BeanTagAttribute
    public String getIconClass() {
        return iconClass;
    }

    /**
     * Setter for the Icon Class
     *
     * @param iconClass
     */
    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer){
        tracer.addBean(this);

        // Checks that a source is set
        if(getIconClass()==null){
            if(!Validator.checkExpressions(this, "iconClass")){
                String currentValues [] = {"iconClass ="+getIconClass()};
                tracer.createError("iconClass must be set",currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }
}
