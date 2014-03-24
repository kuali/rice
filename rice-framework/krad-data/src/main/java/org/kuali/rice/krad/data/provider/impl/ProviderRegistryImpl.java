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
import org.apache.log4j.Logger;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.provider.MetadataProvider;
import org.kuali.rice.krad.data.provider.PersistenceProvider;
import org.kuali.rice.krad.data.provider.Provider;
import org.kuali.rice.krad.data.provider.ProviderRegistry;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Defines a basic ProviderRegistry implementation.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ProviderRegistryImpl implements ProviderRegistry {

    private static final Logger LOG = Logger.getLogger(ProviderRegistry.class);

    private static final String GET_DATA_OBJECT_SERVICE_METHOD_NAME = "getDataObjectService";
    private static final String SET_DATA_OBJECT_SERVICE_METHOD_NAME = "setDataObjectService";

    // Multimap of Provider type -> Provider instance mappings
    // Since all Providers implement Provider, map doubles as list of all registered Providers
    // The implementation is a LinkedHashMultimap to enforce the ordering semantic for PersistenceProvider selection
    private final Multimap<Class<? extends Provider>, Provider> providersByType = LinkedHashMultimap.<Class<? extends Provider>, Provider>create();

    /**
     * Enumerates all Provider-derived interfaces in the type hierarchy of the specified Provider class.
     *
     * @param provider the Provider class to inspect.
     * @return all Provider-derived interfaces implemented by the Provider.
     */
    protected Iterable<Class<? extends Provider>> enumerateProviderInterfaces(Provider provider) {
        List<? extends Class> interfaces = ClassUtils.getAllInterfaces(provider.getClass());
        Iterable<? extends Class> providerInterfaces = Iterables.filter(interfaces, new Predicate<Class>() {
            @Override
            public boolean apply(Class input) {
            return Provider.class.isAssignableFrom(input);
            }
        });
        return (Iterable<Class<? extends Provider>>) providerInterfaces;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void registerProvider(Provider provider) {
        Validate.notNull(provider, "Provider must be non-null");

        if (hasDataObjectServiceMethod(provider, GET_DATA_OBJECT_SERVICE_METHOD_NAME, new Class[] { })) {
            injectDataObjectService(provider);
        }

        // all providers implement Provider, therefore the Provider.class key will map to the list of
        // every registered Provider instance
        for (Class<? extends Provider> providerInterface: enumerateProviderInterfaces(provider)) {
            providersByType.put(providerInterface, provider);
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<Provider> getProviders() {
        return Collections.unmodifiableList(new ArrayList<Provider>(providersByType.get(Provider.class)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<Provider> getProvidersForType(Class<? extends Provider> providerType) {
        Validate.isTrue(providerType != null, "Provider type must be non-null");
        return Collections.unmodifiableList(new ArrayList<Provider>(providersByType.get(providerType)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<MetadataProvider> getMetadataProviders() {
        Collection<Provider> metadataProviders = providersByType.get(MetadataProvider.class);
        return Collections.unmodifiableList(new ArrayList(metadataProviders));
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * Determines if the given {@link Provider} has the given method.
     *
     * @param provider the {@link org.kuali.rice.krad.data.provider.Provider} to check.
     * @param methodName the method name to check for.
     * @param args the arguments for the method.
     * @return TRUE if the Provider has the given method name, FALSE otherwise.
     */
    protected boolean hasDataObjectServiceMethod(Provider provider, String methodName, Class[] args) {
        Method methodToFind;

        try {
            methodToFind = unwrapProxy(provider).getClass().getMethod(methodName, args);
        } catch (Exception e) {
            return false;
        }

        return (methodToFind != null);
    }

    /**
     * Returns the object being proxied, otherwise the given object is returned.
     *
     * @param bean The proxy to get the underlying object.
     * @return object being proxied, otherwise the given object is returned.
     * @throws Exception if errors while getting the underlying object.
     */
    private Object unwrapProxy(Object bean) throws Exception {

		/*
		 * If the given object is a proxy, set the return value as the object
		 * being proxied, otherwise return the given object.
		 */
        if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {

            Advised advised = (Advised) bean;

            bean = advised.getTargetSource().getTarget();
        }

        return bean;
    }

    /**
     * Method attempts to inject a {@link DataObjectService} if the getter method returns null and a setter method
     * exists.
     *
     * @param provider The {@link Provider} to check for getter and setter methods.
     */
    private void injectDataObjectService(Provider provider) {
        try {
            Method getterMethod = unwrapProxy(provider).getClass().getMethod(GET_DATA_OBJECT_SERVICE_METHOD_NAME);
            if (getterMethod.invoke(unwrapProxy(provider)) == null) {
                if (hasDataObjectServiceMethod(provider, SET_DATA_OBJECT_SERVICE_METHOD_NAME,
                        new Class[] { DataObjectService.class })) {

                    Method setterMethod = unwrapProxy(provider).getClass().getMethod(
                            SET_DATA_OBJECT_SERVICE_METHOD_NAME, new Class[]{DataObjectService.class});
                    setterMethod.invoke(unwrapProxy(provider), KradDataServiceLocator.getDataObjectService());
                }
            }
        } catch (Exception e) {
            LOG.warn("Error injecting DataObjectService while registering provider:  " + provider.getClass());
        }
    }

}