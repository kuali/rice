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

import org.kuali.rice.krad.uif.container.Group;

/**
 * Component that renders a standard application header including a logo, navigation, and toolbar.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ApplicationHeader extends Header {
    private static final long serialVersionUID = 6213942245727420161L;

    private NavigationBar applicationNavigation;

    private Group applicationToolbar;

    public ApplicationHeader() {
        super();
    }

    /**
     * Navigation bar component that is used to render the application navigation within the header.
     *
     * @return navigation bar instance
     */
    public NavigationBar getApplicationNavigation() {
        return applicationNavigation;
    }

    /**
     * @see ApplicationHeader#getApplicationNavigation()
     */
    public void setApplicationNavigation(NavigationBar applicationNavigation) {
        this.applicationNavigation = applicationNavigation;
    }

    /**
     * Convenience setter for configuring the application logo (brand image) for the navigation bar.
     *
     * @param applicationLogo image instance to use as logo
     * @see NavigationBar#setBrandImage(org.kuali.rice.krad.uif.element.Image)
     */
    public void setApplicationLogo(Image applicationLogo) {
        if (applicationNavigation == null) {
            throw new RuntimeException("App navigation is null, cannot set application logo");
        } else {
            applicationNavigation.setBrandImage(applicationLogo);
        }
    }

    /**
     * Group that is rendered below the navigation bar with a toolbar style.
     *
     * <p>Common group toolbar for placing things such as user actions, links/dropdowns to other application
     * actions</p>
     *
     * @return Group instance
     */
    public Group getApplicationToolbar() {
        return applicationToolbar;
    }

    /**
     * @see ApplicationHeader#getApplicationToolbar()
     */
    public void setApplicationToolbar(Group applicationToolbar) {
        this.applicationToolbar = applicationToolbar;
    }
}
