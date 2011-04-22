/*
 * e * Copyright 2011 The Kuali Foundation Licensed under the Educational
 * Community License, Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at http://www.opensource.org/licenses/ecl1.php Unless required by applicable
 * law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.uif.container;

import org.kuali.rice.kns.uif.core.Component;

/**
 * This is a description of what this class does - Administrator don't forget to
 * fill this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PageGroup extends Group {
    private static final long serialVersionUID = 7571981300587270274L;

    private boolean autoFocus;

    /**
     * Perform finalize here adds to its document ready script the
     * setupValidator js function for setting up the validator for this view.
     * 
     * @see org.kuali.rice.kns.uif.container.ContainerBase#performFinalize(org.kuali.rice.kns.uif.container.View,
     *      java.lang.Object, org.kuali.rice.kns.uif.core.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        String prefixScript = "";
        if (this.getOnDocumentReadyScript() != null) {
            prefixScript = this.getOnDocumentReadyScript();
        }
        this.setOnDocumentReadyScript(prefixScript + "\nsetupValidator();");
    }

    /**
     * onDocumentReady script configured on the <code>View</code> gets placed in
     * a document ready jQuery block
     * 
     * @see org.kuali.rice.kns.uif.core.ComponentBase#getSupportsOnDocumentReady()
     */
    @Override
    public boolean getSupportsOnDocumentReady() {
        return true;
    }

    /**
     * When this is true, the first field of the kualiForm will be focused by
     * default, unless the parameter focusId is set on the form (by an
     * actionField), then that field will be focused instead. When this setting
     * if false, no field will be focused.
     * 
     * @return the autoFocus
     */
    public boolean isAutoFocus() {
        return this.autoFocus;
    }

    /**
     * @param autoFocus
     *            the autoFocus to set
     */
    public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }

}
