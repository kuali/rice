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
package org.kuali.rice.krad.data.jpa.eclipselink;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.kuali.rice.krad.data.jpa.KradEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

import java.util.Map;

/**
 * A KRAD-managed {@link javax.persistence.EntityManagerFactory} factory bean which can be used to configure an
 * EclipseLink persistence unit using JPA.
 *
 * <p>This class inherits the behavior from {@link KradEntityManagerFactoryBean} but adds the following:</p>
 *
 * <ul>
 *     <li>Sets the {@link org.springframework.orm.jpa.JpaVendorAdapter} to {@link EclipseLinkJpaVendorAdapter}</li>
 *     <li>Detects if JTA is being used and, if so sets a JPA property value for
 *         {@link PersistenceUnitProperties#TARGET_SERVER} to {@link JtaTransactionController} which allows for
 *         EclipseLink integration with JTA.</li>
 *     <li>Configures an EclipseLink "customizer" which allows for a configurable sequence management strategy</li>
 *     <li>Disables the shared cache (defined by {@link PersistenceUnitProperties#CACHE_SHARED_DEFAULT} by default</li>
 * </ul>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KradEclipseLinkEntityManagerFactoryBean extends KradEntityManagerFactoryBean {

    /**
     * Creates a KRAD-managed {@link javax.persistence.EntityManagerFactory} factory bean.
     */
    public KradEclipseLinkEntityManagerFactoryBean() {
        super.setJpaVendorAdapter(new EclipseLinkJpaVendorAdapter());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadCustomJpaDefaults(Map<String, String> jpaProperties) {
        if (getPersistenceUnitManager().getDefaultJtaDataSource() != null) {
            jpaProperties.put(PersistenceUnitProperties.TARGET_SERVER, JtaTransactionController.class.getName());
        }
        jpaProperties.put(PersistenceUnitProperties.SESSION_CUSTOMIZER, KradEclipseLinkCustomizer.class.getName());
        jpaProperties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
    }

}
