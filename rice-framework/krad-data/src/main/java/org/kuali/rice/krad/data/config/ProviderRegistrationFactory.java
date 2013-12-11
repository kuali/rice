/*
 * Copyright 2006-2013 The Kuali Foundation
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

import org.apache.log4j.Logger;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.provider.Provider;
import org.kuali.rice.krad.data.provider.ProviderRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides method to register Providers outside of ModuleConfiguration
 */
public class ProviderRegistrationFactory implements InitializingBean{
    private static final Logger LOG = Logger.getLogger(ProviderRegistrationFactory.class);

    protected ProviderRegistry providerRegistry;

    @SuppressWarnings("unchecked")
    protected List<Provider> providers = Collections.unmodifiableList(Collections.EMPTY_LIST);

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
     * Sets the list of providers for this module
     * @param providers list of providers
     */
    public void setProviders(List<Provider> providers) {
        this.providers = Collections.unmodifiableList(new ArrayList<Provider>(providers));
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public ProviderRegistry getProviderRegistry() {
        if(this.providerRegistry == null){
            return KradDataServiceLocator.getProviderRegistry();
        }
        return this.providerRegistry;
    }

    public void setProviderRegistry(ProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
    }
}
