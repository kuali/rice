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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *  Defines a filter generator that will alter the query for an annotated field.
 *
 * <pre>
 *   Examples:
 *
 *   &#064;FilterGenerator(attributeName = "accountTypeCode",attributeResolverClass =
 *         "org.kuali.rice.krad.data.bo.TestQueryCustomizerValue")
 *         private TestRelatedExtension accountExtension;
 * </pre>
 *
 * <p>
 * Currently, in order for this annotation to work properly, the
 * {@link org.kuali.rice.krad.data.jpa.eclipselink.KradEclipseLinkCustomizer} must be configured for the EclipseLink
 * persistence unit. This can be done manually using
 * {@link org.eclipse.persistence.config.PersistenceUnitProperties#SESSION_CUSTOMIZER}, or it will be done automatically
 * when using {@link org.kuali.rice.krad.data.jpa.eclipselink.KradEclipseLinkEntityManagerFactoryBean}.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 **/
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface FilterGenerator {

    /**
     * (Required) The attribute name to have the customization applied in the query.
     *
     * @return the attribute name to have the customization applied in the query.
     */
    String attributeName();

    /**
     * (Optional) The operator that will be used for this fragment expression.
     *
     * <p>Defaults to EQUAL.</p>
     *
     * @return the operator that will be used for this fragment expression.
     */
    FilterOperators operator() default FilterOperators.EQUAL;

    /**
     * (Optional) The value that the attribute named will be used to build expression fragment.
     *
     * <p>Defaults to an empty string.</p>
     *
     * @return the value that the attribute named will be used to build expression fragment.
     */
    String attributeValue() default "";

    /**
     * (Optional) The class that resolves the value that the attribute named will be used to build expression fragment.
     *
     * <p>Defaults to the Void class.</p>
     *
     * @return the class that resolves the value that the attribute named will be used to build expression fragment.
     */
    Class<?> attributeResolverClass() default Void.class;
}
