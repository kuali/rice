/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif.widget;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

/**
 * Used for rendering a lightbox in the UI to display the result of a submit in
 * a light box. This is used for the quickfinder lookup.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LightBoxLookup extends WidgetBase {
    private static final long serialVersionUID = -8571541274489677888L;

    private String actionParameterMapString;

    public LightBoxLookup() {
        super();
    }

    /**
     * Setter for the action parameter map javascript string
     * 
     * @param the
     *            action parameter map javascript string
     */
    public void setActionParameterMapString(String actionParameterMapString) {
        this.actionParameterMapString = actionParameterMapString;
    }

    /**
     * Action parameter map javascript string
     * <p>
     * The action parameter map string will be used to write these parameters to
     * the form.
     * </p>
     * 
     * @return the action parameter map javascript string
     */
    public String getActionParameterMapString() {
        return actionParameterMapString;
    }

}
