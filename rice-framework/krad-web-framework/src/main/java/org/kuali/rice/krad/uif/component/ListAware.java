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
package org.kuali.rice.krad.uif.component;

/**
 * Components that change their template (or other logic) based on whether they are rendered in a list (as
 * a list item) should implement this interface.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ListAware {

    /**
     * Sets the boolean indicator on the component that indicates whether it is being rendered
     * in a list
     *
     * @param renderedInList boolean true if rendered in a list, false if not
     */
    void setRenderedInList(boolean renderedInList);
}
