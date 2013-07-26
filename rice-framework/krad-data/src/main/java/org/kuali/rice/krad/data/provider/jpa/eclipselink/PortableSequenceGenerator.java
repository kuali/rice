/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.data.provider.jpa.eclipselink;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface PortableSequenceGenerator {

    /**
     * (Required) A unique generator name that can be referenced
     * by one or more classes to be the generator for primary key
     * values.
     */
    String name();

    /**
     * The name of the database sequence object from which to obtain primary key values.
     */
    String sequenceName();

    /** (Optional) The catalog of the sequence generator.
     */
    String catalog() default "";

    /** (Optional) The schema of the sequence generator.
     */
    String schema() default "";

    /**
     * (Optional) The value from which the sequence object
     * is to start generating.
     */
    int initialValue() default 1;

    /**
     * (Optional) The amount to increment by when allocating
     * sequence numbers from the sequence.
     */
    int allocationSize() default 50;

}
