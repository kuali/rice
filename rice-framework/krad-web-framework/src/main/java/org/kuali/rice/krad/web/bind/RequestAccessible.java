/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.web.bind;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for binding to indicate a property is accessible (allows updates).
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestAccessible {

    /**
     * HTTP methods that annotation should apply to.
     *
     * <p>For example you might want to allow updates for GET only. For other
     * request methods, the default view security level will be used (unless the RequestProtected
     * annotation is also present)</p>
     */
    RequestMethod[] method() default {};

    /**
     * List of method names on the controller class that the binding should occur for.
     *
     * <p>For example you might want to restrict binding for only certain methodToCall(s). For other
     * methodToCalls not in the list, no binding will occur.</p>
     */
    String[] methodToCalls() default {};
}
