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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Class level annotation that indicates that a mapping inherited from a superclass should be "unmapped" in the child
 * class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface RemoveMapping {

    /**
     * (Required) The property name of the mapping to remove.
     *
     * @return the property name of the mapping to remove.
     */
    String name();

}
