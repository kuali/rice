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

import com.google.common.annotations.Beta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a property which should be inherited from another data object class.
 *
 * <p>Allows for the label to be overridden, but nothing else.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InheritProperty {

    /**
     * Gets the name of the property to be inherited.
     *
     * @return the name of the property to be inherited.
     */
	String name();

    /**
     * Gets the label to override.
     *
     * @return the label to override.
     */
	Label label() default @Label("");

    /**
     * BETA: Gets the hints which can be passed through when auto-generating the input fields for an attribute.
     *
     * @return the hints which can be passed through when auto-generating the input fields for an attribute.
     */
    @Beta
	UifDisplayHints displayHints() default @UifDisplayHints(@UifDisplayHint(UifDisplayHintType.NONE));
}
