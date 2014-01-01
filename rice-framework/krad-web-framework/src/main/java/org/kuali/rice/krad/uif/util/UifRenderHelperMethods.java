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
package org.kuali.rice.krad.uif.util;

import org.kuali.rice.krad.uif.component.ListAware;
import org.kuali.rice.krad.uif.element.Header;

import java.io.Serializable;

/**
 * Collection of helper methods that can be exposed to a render engine.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifRenderHelperMethods implements Serializable {
    private static final long serialVersionUID = 5105182075655826814L;

    /**
     * Indicates whether the given class is a {@link org.kuali.rice.krad.uif.element.Header}
     * component class.
     *
     * @param componentClass class to check
     * @return boolean true if class is a header class, false if not
     */
    public boolean isHeader(Class<?> componentClass) {
        return Header.class.isAssignableFrom(componentClass);
    }

    /**
     * Indicates whether the given class is a {org.kuali.rice.krad.uif.component.ListAware}
     * component class.
     *
     * @param componentClass class to check
     * @return boolean true if class is a list aware class, false if not
     */
    public boolean isListAware(Class<?> componentClass) {
        return ListAware.class.isAssignableFrom(componentClass);
    }
}
