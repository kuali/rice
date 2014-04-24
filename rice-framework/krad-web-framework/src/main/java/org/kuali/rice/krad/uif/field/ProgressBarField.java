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
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.element.ProgressBar;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Field wrapper for the ProgressBar element
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "progressBarField-bean", parent = "Uif-ProgressBarField"),
        @BeanTag(name = "stepProgressBarField-bean", parent = "Uif-StepProgressBarField")})
public class ProgressBarField extends FieldBase {
    private static final long serialVersionUID = 2002441871716395985L;

    private ProgressBar progressBar;

    public ProgressBarField() {
        super();
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
        if (this.getFieldLabel() != null && this.getProgressBar() != null && StringUtils.isNotBlank(
                this.getProgressBar().getId())) {
            this.getFieldLabel().setLabelForComponentId(this.getProgressBar().getId());
        }
    }

    /**
     * The ProgressBar to be shown for this field
     *
     * @return the ProgressBar
     */
    @BeanTagAttribute(name = "progressBar")
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * @see ProgressBarField#getProgressBar()
     */
    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }
}
