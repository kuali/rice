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
package org.kuali.rice.krad.data.jpa;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines a primary key generator that may be referenced by name when a generator element is specified for the
 * {@link javax.persistence.GeneratedValue} annotation.
 *
 * <p>
 * A portable sequence generator may be specified on the entity class or on the primary key field or property. The scope
 * of the generator name is global to the persistence unit (across all generator types).
 * </p>
 *
 * <p>
 * The term "portable" in this case indicates that native sequences will be used if the target database platform
 * supports them. However, if it does not have native sequence support, then sequence-like behavior will be emulated
 * using an appropriate strategy for the target platform.
 * </p>
 *
 * <pre>
 *   Examples:
 *
 *   &#064;PortableSequenceGenerator(name="EMP_SEQ")
 *
 *   &#064;PortableSequenceGenerator(name="EMP_SEQ", sequenceName="EMP_SEQ", initialValue = 1)
 * </pre>
 *
 * <p>
 * Current, in order for this annotation to work properly, the
 * {@link org.kuali.rice.krad.data.jpa.eclipselink.KradEclipseLinkCustomizer} must be configured for the EclipseLink
 * persistence unit. This can be done manually using
 * {@link org.eclipse.persistence.config.PersistenceUnitProperties#SESSION_CUSTOMIZER}, or it will be done automatically
 * when using {@link org.kuali.rice.krad.data.jpa.eclipselink.KradEclipseLinkEntityManagerFactoryBean}.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface PortableSequenceGenerator {

    /**
     * (Required) A unique generator name that can be referenced by one or more classes to be the generator for primary
     * key values.
     *
     * @return the name of the sequence generator.
     */
    String name();

    /**
     * (Optional) The name of the database sequence object from which to obtain primary key values. If not specified,
     * will default to the name of this generator.
     *
     * @return the sequence name associated with the sequence generator.
     */
    String sequenceName() default "";

    /**
     * (Optional) The value from which the sequence object is to start generating. Only used for generation of the
     * sequence in the schema.
     *
     * <p>
     * If the sequence construct already exists in the database or schema generation is not enabled, then this value is
     * effectively ignored.
     * </p>
     *
     * @return the initial value of the sequence.
     */
    int initialValue() default 1000;

}
