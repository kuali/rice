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
package org.kuali.rice.krad.data.provider.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates whether the attribute contents should be serialized in the given context(s).
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Serialized {

    /**
     * If true, this indicates that the marked field should be serialized for the indicated contexts, and if false
     * then the marked field should not.
     *
     * @return whether the marked field should be serialized
     */
    boolean enabled() default true;

    /**
     * The serialization contexts that {@link Serialized} applies to.
     *
     * @return the serialization contexts
     */
    SerializationContext[] forContexts() default { SerializationContext.ALL };
}
