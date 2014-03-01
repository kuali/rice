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
package org.kuali.rice.krad.web.bind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the form annotated with this annotation has change tracking enabled.
 *
 * <p>Use this on a subclass of {@link org.kuali.rice.krad.web.form.UifFormBase} to enable this change tracking.
 * Subclasses of a form with change tracking enabled can specify this annotation and {@code enabled = false} to disable
 * changing tracking.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ChangeTracking {

    /**
     * True if change tracking should be enabled for the annotated form, false if not.
     *
     * @return true if change tracking enabled, false otherwise
     */
    boolean enabled() default true;

}

