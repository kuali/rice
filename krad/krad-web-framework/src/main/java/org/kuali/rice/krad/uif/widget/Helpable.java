/*
 * Copyright 2006-2012 The Kuali Foundation
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

/**
 * Interface for components that support help.
 */
public interface Helpable {

    /**
     * Help configuration object for the container
     *
     * <p>
     * External help information can be configured for the container. The
     * <code>Help</code> object can the configuration for rendering a link to
     * that help information.
     * </p>
     *
     * @return Help for container
     */
    public Help getHelp();

    /**
     * Setter for the containers help content
     *
     * @param help
     */
    public void setHelp(Help help);

    /**
     *  Getter for the component's tooltip.
     *
     *  This is used by the helper to set the help content to the tooltip.
     *
     *  @return Tooltip of the container
     */
    //ToDo: replace with setTooltipOfComponent
    public Tooltip getTooltipOfComponent();

}
