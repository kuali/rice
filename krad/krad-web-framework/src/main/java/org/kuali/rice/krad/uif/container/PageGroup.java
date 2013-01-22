/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.uif.container;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.view.FormView;
import org.kuali.rice.krad.uif.view.View;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "page", parent = "Uif-Page"),
        @BeanTag(name = "disclosure-page", parent = "Uif-Disclosure-Page"),
        @BeanTag(name = "documentPage", parent = "Uif-DocumentPage"),
        @BeanTag(name = "inquiryPage", parent = "Uif-InquiryPage"),
        @BeanTag(name = "lookupPage", parent = "Uif-LookupPage"),
        @BeanTag(name = "maintenancePage", parent = "Uif-MaintenancePage")})
public class PageGroup extends Group {
    private static final long serialVersionUID = 7571981300587270274L;

    private boolean autoFocus = false;

    /**
     * Perform finalize here adds to its document ready script the
     * setupValidator js function for setting up the validator for this view.
     *
     * @see org.kuali.rice.krad.uif.container.ContainerBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        this.addDataAttribute("type", "Page");

        String prefixScript = "";
        if (this.getOnDocumentReadyScript() != null) {
            prefixScript = this.getOnDocumentReadyScript();
        }

        if (view instanceof FormView && ((FormView) view).isValidateClientSide()) {
            this.setOnDocumentReadyScript(prefixScript + "\nsetupPage(true);");
        } else {
            this.setOnDocumentReadyScript(prefixScript + "\nsetupPage(false);");
        }
    }

    /**
     * When this is true, the first field of the kualiForm will be focused by
     * default, unless the parameter focusId is set on the form (by an
     * actionField), then that field will be focused instead. When this setting
     * if false, no field will be focused.
     *
     * @return the autoFocus
     */
    @BeanTagAttribute(name = "autoFocus")
    public boolean isAutoFocus() {
        return this.autoFocus;
    }

    /**
     * @param autoFocus the autoFocus to set
     */
    public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#completeValidation
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Checks that no invalid items are present
        for (int i = 0; i < getItems().size(); i++) {
            if (getItems().get(i).getClass() == PageGroup.class || getItems().get(i).getClass()
                    == NavigationGroup.class) {
                String currentValues[] = {"item(" + i + ").class =" + getItems().get(i).getClass()};
                tracer.createError("Items in PageGroup cannot be PageGroup or NaviagtionGroup", currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }
}
