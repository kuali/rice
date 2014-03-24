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
package org.kuali.rice.krad.data.provider;

import org.apache.log4j.Logger;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides a mechanism for registering Providers in the {@link ProviderRegistry} using Spring.
 *
 * @see Provider
 * @see ProviderRegistry
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ProviderRegistrar implements InitializingBean {

    private static final Logger LOG = Logger.getLogger(ProviderRegistrar.class);

    /**
     * The provider registry.
     */
    protected ProviderRegistry providerRegistry;

    /**
     * The providers currently assigned to the registry.
     */
    protected List<Provider> providers = Collections.unmodifiableList(Collections.<Provider>emptyList());

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if ( getProviders() != null ) {
            if ( getProviderRegistry() != null ) {
                for ( Provider provider : getProviders() ) {
                    LOG.info( "Registering data module provider for "+ provider);
                    getProviderRegistry().registerProvider(provider);
                }
            } else {
                LOG.error( "Provider registry not initialized.");
            }
        }
    }

    /**
     * Sets the list of providers for this module.
     *
     * @param providers list of providers
     */
    public void setProviders(List<Provider> providers) {
        this.providers = Collections.unmodifiableList(new ArrayList<Provider>(providers));
    }

    /**
     * Gets the list of providers for this module.
     *
     * @return the list of providers for this module.
     */
    public List<Provider> getProviders() {
        return providers;
    }

    /**
     * Gets the provider registry.
     *
     * @return the provider registry.
     */
    public ProviderRegistry getProviderRegistry() {
        if(this.providerRegistry == null){
            return KradDataServiceLocator.getProviderRegistry();
        }
        return this.providerRegistry;
    }

    /**
     * Setter for the provider registry.
     *
     * @param providerRegistry the provider registry to set.
     */
    public void setProviderRegistry(ProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

}
