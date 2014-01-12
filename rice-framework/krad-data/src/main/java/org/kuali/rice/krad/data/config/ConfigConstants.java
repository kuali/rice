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
package org.kuali.rice.krad.data.config;

/**
 * A class containing configuration-related constants.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class ConfigConstants {

    private ConfigConstants() {}

    /**
     * Indicates if a JPA {@code EntityManager} flush should be automatically executed when calling
     * {@link org.kuali.rice.krad.data.DataObjectService#save(Object, org.kuali.rice.krad.data.PersistenceOption...)}
     * using a JPA provider. This is recommended for testing only since the change is global and would affect all
     * persistence units.
     */
    public static final String JPA_AUTO_FLUSH = "rice.krad.data.jpa.autoFlush";

    /**
     * Prefix for property names used to identify the classname for a
     * {@link org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer} to use for a given platform.
     * To construct a full property name, concatenate this prefix with the platform name.
     *
     * @see org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory
     */
    public static final String PLATFORM_INCREMENTER_PREFIX = "rice.krad.data.platform.incrementer.";

    /**
     * Prefix for property names that are passed to the JPA persistence context as JPA properties.
     *
     * <p>To use this, concatenate this prefix with the persistence context name. The total prefix (including the
     * persistence context) will then be stripped before being passed to the JPA entity manager factory when using
     * the {@link org.kuali.rice.krad.data.jpa.KradEntityManagerFactoryBean}.</p>
     *
     * @see org.kuali.rice.krad.data.jpa.KradEntityManagerFactoryBean
     */
    public static final String JPA_PROPERTY_PREFIX = "rice.krad.jpa.";

    /**
     * Prefix for property names that are passed to *all* JPA persistence contexts as JPA properties.
     *
     * <p>The total prefix will then be stripped before being passed to all JPA entity manager factories that use the
     * {@link org.kuali.rice.krad.data.jpa.KradEntityManagerFactoryBean}.</p>
     *
     * @see org.kuali.rice.krad.data.jpa.KradEntityManagerFactoryBean
     */
    public static final String GLOBAL_JPA_PROPERTY_PREFIX = JPA_PROPERTY_PREFIX + "global.";

}
