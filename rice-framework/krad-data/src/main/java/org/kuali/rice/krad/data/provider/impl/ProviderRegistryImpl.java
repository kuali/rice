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
package org.kuali.rice.krad.data.provider.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.Validate;
import org.kuali.rice.krad.data.provider.MetadataProvider;
import org.kuali.rice.krad.data.provider.PersistenceProvider;
import org.kuali.rice.krad.data.provider.Provider;
import org.kuali.rice.krad.data.provider.ProviderRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * ProviderRegistry implementation
 */
public class ProviderRegistryImpl implements ProviderRegistry {
    // Multimap of Provider type -> Provider instance mappings
    // Since all Providers implement Provider, map doubles as list of all registered Providers
    // The implementation is a LinkedHashMultimap to enforce the ordering semantic for PersistenceProvider selection
    private final Multimap<Class<? extends Provider>, Provider> providersByType = LinkedHashMultimap.<Class<? extends Provider>, Provider>create();

    /**
     * Enumerates all Provider-derived interfaces in the type hierarchy of the specified Provider class
     * @param provider the Provider class to inspect
     * @return all Provider-derived interfaces implemented by the Provider
     */
    protected Iterable<Class<? extends Provider>> enumerateProviderInterfaces(Provider provider) {
        List<? extends Class> interfaces = ClassUtils.getAllInterfaces(provider.getClass());
        Iterable<? extends Class> providerInterfaces = Iterables.filter(interfaces, new Predicate<Class>() {
            @Override
            public boolean apply(@Nullable Class input) {
            return Provider.class.isAssignableFrom(input);
            }
        });
        return (Iterable<Class<? extends Provider>>) providerInterfaces;
    }

    @Override
    public synchronized void registerProvider(Provider provider) {
        Validate.notNull(provider, "Provider must be non-null");
        // all providers implement Provider, therefore the Provider.class key will map to the list of
        // every registered Provider instance
        for (Class<? extends Provider> providerInterface: enumerateProviderInterfaces(provider)) {
            providersByType.put(providerInterface, provider);
        }
    }

    @Override
    public synchronized boolean unregisterProvider(Provider provider) {
        Validate.notNull(provider, "Provider must be non-null");
        boolean removed = false;
        Collection<Provider> providers = providersByType.values();

        // {@link java.util.Collection#remove} semantics for multimap is to remove a *single* occurrence of the given object
        // so we need to keep removing the provider until all mapped instances have been removed
        while (providers.remove(provider)) {
            removed = true;
        }

        return removed;
    }

    @Override
    public synchronized List<Provider> getProviders() {
        return Collections.unmodifiableList(new ArrayList<Provider>(providersByType.get(Provider.class)));
    }

    @Override
    public synchronized List<Provider> getProvidersForType(Class<? extends Provider> providerType) {
        Validate.isTrue(providerType != null, "Provider type must be non-null");
        return Collections.unmodifiableList(new ArrayList<Provider>(providersByType.get(providerType)));
    }

    @Override
    public synchronized List<MetadataProvider> getMetadataProviders() {
        Collection<Provider> metadataProviders = providersByType.get(MetadataProvider.class);
        return Collections.unmodifiableList(new ArrayList(metadataProviders));
    }

    @Override
    public synchronized PersistenceProvider getPersistenceProvider(Class<?> type) {
        Validate.notNull(type, "Data object type must be non-null");
        Collection<Provider> persistenceProviders = providersByType.get(PersistenceProvider.class);
        // return a single PersistenceProvider that handles the specified type
        // we just select the first one
        for (Provider provider: persistenceProviders) {
            PersistenceProvider persistenceProvider = (PersistenceProvider) provider;
            if (persistenceProvider.handles(type)) {
                return persistenceProvider;
            }
        }
        return null;
    }

	@Override
	public MetadataProvider getMetadataProvider(Class<?> type) {
		Validate.notNull(type, "Data object type must be non-null");
		Collection<MetadataProvider> metadataProviders = getMetadataProviders();
		// return a single PersistenceProvider that handles the specified type
		// we just select the first one
		for (MetadataProvider provider : metadataProviders) {
			if (provider.handles(type)) {
				return provider;
			}
		}
		return null;
	}

}