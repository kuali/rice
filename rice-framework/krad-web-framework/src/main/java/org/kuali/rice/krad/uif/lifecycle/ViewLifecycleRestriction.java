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
package org.kuali.rice.krad.uif.lifecycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for {@link org.kuali.rice.krad.uif.util.LifecycleElement} bean properties to restrict which view
 * lifecycle phases for which the property will be considered while initializing the successor phase queue.
 * 
 * <p>
 * This annotation should be placed on the read method for any properties on the component that
 * should be excluded from the view lifecycle. An optional array of phases at which the property
 * should be included may be provided.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ViewLifecycleRestriction {

    /**
     * Lifecycle phase (including preceding phases) at which to include the annotated bean property.
     * 
     * @return lifecycle phase at which to include the annotated property
     * @see org.kuali.rice.krad.uif.UifConstants.ViewPhases
     */
    String[] value() default {};

    /**
     * Lifecycle phase(s) at which to exclude the annotated bean property.
     *
     * <p>Note when this property is set by itself, all other phases not listed will be included. If value is
     * set as well, only those phases within the value and not listed here will be included.</p>
     *
     * @return set of lifecycle phases at which to exclude the annotated property
     * @see org.kuali.rice.krad.uif.UifConstants.ViewPhases
     */
    String[] exclude() default {};

}
