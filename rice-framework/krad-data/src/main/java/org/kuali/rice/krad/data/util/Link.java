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
package org.kuali.rice.krad.data.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* Specifies paths which should be linked during automatic reference linking processes.
*
* <p>If specified on a class, the path(s) will be relative to the class. If specified on a field, the path(s) will be
* relative to the field. If no paths are specified, then the linking will start at the class or field that is
* annotated.</p>
*
* <p>To prevent cascading of reference linking, this annotation can be specified with {@code cascade = false}.</p>
*
* @author Kuali Rice Rice (rice.collab@kuali.org)
*/
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Link {

    /**
    * Indicates whether or not linking should cascade through the specified path(s).
    *
    * @return true if reference linking should be cascaded, false otherwise
    */
    boolean cascade() default true;

    /**
    * Specify the path or paths (relative to the annotated class or field) at which to start the reference linking
    * process. If no path specified, then linking will be performed on the annotated element and cascaded from there.
    *
    * @return the path or paths at which to start reference linking
    */
    String[] path() default {};

}

