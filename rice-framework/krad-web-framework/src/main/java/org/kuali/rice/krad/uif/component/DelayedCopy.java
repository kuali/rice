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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import org.kuali.rice.krad.datadictionary.Copyable;
import org.kuali.rice.krad.uif.util.LifecycleAwareList;

/**
 * Annotation for {@link Copyable} fields to indicate that a delayed copy proxy should be used
 * instead of the original component when performing a deep copying on the field.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DelayedCopy {

    /**
     * May be used to indicate the presence of this annotation on the field referring to the object
     * should be used to determine if this field should be delayed.
     * 
     * <p>
     * For example, {@link LifecycleAwareList} is a delegating list wrapper and since it implements
     * the {@link List} interface is treated as a {@link List} rather than a {@link Copyable} when
     * performing a deep copy. The presence of {@DelayedCopy} with {@link #inherit()}
     * set to true on the delegate indicates that the items in the delegated list should be delayed
     * only if the undelegated list reference also has the {@link DelayedCopy} annotation.
     * </p>
     * 
     * @return True if the parent field determines whether or not to delay copying the field, false
     *         to always delay copy of the annotated field.
     */
    boolean inherit() default false;

}
