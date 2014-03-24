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
 * Identifies the annotated class as an extension object for the given baseline class.
 * 
 * <p>Inclusion of this annotation will perform the necessary wiring within JPA.</p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExtensionFor {

	/**
     * (Required) The class for which this one is an extension.
     *
     * @return the class for which this one is an extension.
     */
	Class<?> value();

	/**
     * (Optional) The name of the property on the source object which will hold the extension object.
     *
     * @return the name of the property on the source object which will hold the extension object.
     */
	String extensionPropertyName() default "extension";
}
