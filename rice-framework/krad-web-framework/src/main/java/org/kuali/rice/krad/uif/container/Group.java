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
package org.kuali.rice.krad.uif.container;

import org.kuali.rice.krad.uif.widget.Disclosure;
import org.kuali.rice.krad.uif.widget.Scrollpane;

/**
 * Common interface for group components. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface Group extends Container {

    /**
     * Binding prefix string to set on each of the groups <code>DataField</code> instances
     *
     * <p>
     * As opposed to setting the bindingPrefix on each attribute field instance,
     * it can be set here for the group. During initialize the string will then
     * be set on each attribute field instance if the bindingPrefix is blank and
     * not a form field
     * </p>
     *
     * @return String binding prefix to set
     */
    String getFieldBindByNamePrefix();

    /**
     * Setter for the field binding prefix
     *
     * @param fieldBindByNamePrefix
     */
    void setFieldBindByNamePrefix(String fieldBindByNamePrefix);

    /**
     * Object binding path to set on each of the group's
     * <code>InputField</code> instances
     *
     * <p>
     * When the attributes of the group belong to a object whose path is
     * different from the default then this property can be given to set each of
     * the attributes instead of setting the model path on each one. The object
     * path can be overridden at the attribute level. The object path is set to
     * the fieldBindingObjectPath during the initialize phase.
     * </p>
     *
     * @return String model path to set
     * @see org.kuali.rice.krad.uif.component.BindingInfo#getBindingObjectPath()
     */
    String getFieldBindingObjectPath();

    /**
     * Setter for the field object binding path
     *
     * @param fieldBindingObjectPath
     */
    void setFieldBindingObjectPath(String fieldBindingObjectPath);

    /**
     * Disclosure widget that provides collapse/expand functionality for the
     * group
     *
     * @return Disclosure instance
     */
    Disclosure getDisclosure();

    /**
     * Setter for the group's disclosure instance
     *
     * @param disclosure
     */
    void setDisclosure(Disclosure disclosure);

    /**
     * Scrollpane widget that provides scrolling functionality for the
     * group
     *
     * @return Scrollpane instance
     */
    Scrollpane getScrollpane();

    /**
     * Setter for the group's scrollpane instance
     *
     * @param scrollpane
     */
    void setScrollpane(Scrollpane scrollpane);

    /**
     * Determine the group should be rendered on initial load, or if a loading message should be rendered instead.
     *
     * @return True if a loading message should be rendered, false if the group should be rendered now.
     */
    boolean isRenderLoading();

    /**
     * Getter for headerText
     * 
     * @return headerText
     */
    String getHeaderText();
    
    /**
     * Setter for headerText.
     * 
     * @param headerText value
     */
    void setHeaderText(String headerText);

    /**
     * Setter for renderFooter.
     * 
     * @param renderFooter value
     */
    void setRenderFooter(boolean renderFooter);

    /**
     * This method ...
     * 
     * @return
     */
    String getWrapperTag();

    /**
     * This method ...
     * 
     * @param footer
     */
    void setWrapperTag(String footer);
    
}
