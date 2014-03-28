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
 * BETA: Indicates that you want the krad-data module to auto-generate the listed types of KRAD views upon startup.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Beta
public @interface UifAutoCreateViews {

    /**
     * Gets the list of view types to autogenerate on startup.
     *
     * @return the list of view types to autogenerate on startup.
     */
	UifAutoCreateViewType[] value() default UifAutoCreateViewType.ALL;
}
