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
package org.kuali.rice.krad.data.jpa;

/**
 * Provides a common interface for Filter annotation (attributeResolverClass element) on a data object field that you
 * want to customize and want to dynamically generate a value.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 **/
public interface FilterValue {

    /**
     * Gets the value that has been dynamically generated
     *
     * @return the value that has been dynamically generated
     */
    public abstract Object getValue();
}
