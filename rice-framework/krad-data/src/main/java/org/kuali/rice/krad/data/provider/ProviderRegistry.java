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

import java.util.List;

/**
 * Registry of KRAD Data Providers.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ProviderRegistry {

    /**
     * Register a Provider.
     *
     * @param provider the provider to register
     */
    void registerProvider(Provider provider);

    /**
     * Unregister a Provider if it exists.
     *
     * @param provider the provider to unregister
     *
     * @return true if provider was unregistered
     */
    boolean unregisterProvider(Provider provider);

    /**
     * Obtain list of all registered providers.
     *
     * @return non-null and immutable list of all registered providers
     */
    List<Provider> getProviders();

    /**
     * Obtain list of all registered providers of a given type.
     *
     * @param providerType the provider type
     *
     * @return non-null and immutable list of all registered providers of a given type
     */
    List<Provider> getProvidersForType(Class<? extends Provider> providerType);

    /**
     * Obtain list of all registered MetadataProviders
     *
     * @return non-null and immutable list of all registered MetadataProviders
     */
    List<MetadataProvider> getMetadataProviders();

    /**
	 * Obtain the first MetadataProvider which handles the gives type.
     *
     * @return the MetadataProvider which handles the given type, or null if none could be found
	 */
	MetadataProvider getMetadataProvider(Class<?> type);

    /**
     * Return the persistence provider for a given type.
     *
     * @param type the data object type
     *
     * @return persistence provider for given type, or null if no matching provider is registered
     */
    PersistenceProvider getPersistenceProvider(Class<?> type);

}