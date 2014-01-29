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

import org.kuali.rice.core.api.criteria.LookupCustomizer;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.CompoundKey;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.data.provider.PersistenceProvider;
import org.kuali.rice.krad.data.provider.ProviderRegistry;
import org.springframework.beans.factory.annotation.Required;

/**
 * DataObjectService implementation backed by the {@link ProviderRegistry}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ProviderBasedDataObjectService implements DataObjectService {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(ProviderBasedDataObjectService.class);

	protected ProviderRegistry providerRegistry;
	protected MetadataRepository metadataRepository;

    @Override
    public <T> T find(Class<T> type, Object id) {
        return persistenceProviderForType(type).find(type, reduceCompoundKey(id));
    }

    /**
     * If the given id object is an instance of CompoundKey but there is only one entry in the key map, then just grab
     * that single value and treat it as a single id.
     *
     * @param id the potentially CompoundKey to reduce
     * @return the single value from the CompoundKey map if the given id is a CompoundKey with a single entry, otherwise
     *         the original id that was passed in is returned
     */
    protected Object reduceCompoundKey(Object id) {
        if (id instanceof CompoundKey) {
            CompoundKey compoundKey = (CompoundKey)id;
            if (compoundKey.getKeys().size() == 1) {
                id = compoundKey.getKeys().values().iterator().next();
            }
        }
        return id;
    }

    @Override
    public <T> QueryResults<T> findMatching(Class<T> type, QueryByCriteria queryByCriteria) {
        return persistenceProviderForType(type).findMatching(type, queryByCriteria);
    }

    @Override
    public <T> QueryResults<T> findMatching(Class<T> type, QueryByCriteria queryByCriteria, LookupCustomizer<T> lookupCustomizer) {
        return persistenceProviderForType(type).findMatching(type, queryByCriteria, lookupCustomizer);
    }

    @Override
    public void delete(Object dataObject) {
        persistenceProviderForObject(dataObject).delete(dataObject);
    }

    @Override
    public <T> void deleteMatching(Class<T> type, QueryByCriteria queryByCriteria) {
        QueryResults<T> results = findMatching(type, queryByCriteria);
        for (T result: results.getResults()) {
            delete(result);
        }
    }

    @Override
	public <T> T save(T dataObject, PersistenceOption... options) {
        return persistenceProviderForObject(dataObject).save(dataObject, options);
    }

    @Override
    public MetadataRepository getMetadataRepository() {
        return metadataRepository;
    }

    @Override
    public <T> DataObjectWrapper<T> wrap(T dataObject) {
        if (dataObject == null) {
            throw new IllegalArgumentException("data object was null");
        }
		DataObjectMetadata metadata = getMetadataRepository().getMetadata(dataObject.getClass());
		// Checking for metadata and failing here. Otherwise a null gets stored in the wrapper
		// and most later operations on the object will fail with an NPE.
		if (metadata == null) {
			LOG.warn("Non KRAD Data object passed - no metadata found for: " + dataObject.getClass());
			// throw new IllegalArgumentException("Non KRAD Data object passed - no metadata found for: " +
			// dataObject.getClass());
		}
        return new DataObjectWrapperImpl<T>(dataObject, metadata, this);
    }

    @Override
    public <T> boolean supports(Class<T> type) {
        return providerRegistry.getPersistenceProvider(type) != null;
    }

    /**
     * @return PersistenceProvider returned by the ProviderRegistry for the given type
     * @throws RuntimeException if not PersistenceProvider handles given type
     */
    protected PersistenceProvider persistenceProviderForType(Class<?> type) {
        PersistenceProvider provider = providerRegistry.getPersistenceProvider(type);
        if (provider == null) {
            throw new RuntimeException("No PersistenceProvider handles type: " + type);
        }
        return provider;
    }

    /**
	 * @return PersistenceProvider returned by the ProviderRegistry for the given object
	 * @throws RuntimeException
	 *             if not PersistenceProvider handles given type
	 * @throws IllegalArgumentException
	 *             if null object passed in
	 */
    protected PersistenceProvider persistenceProviderForObject(Object object) {
		if (object == null) {
			throw new IllegalArgumentException("data object was null");
		}
		return persistenceProviderForType(object.getClass());
    }

    @Override
    public void flush(Class<?> type){
        PersistenceProvider persistenceProvider = persistenceProviderForType(type);
        if (persistenceProvider == null) {
            throw new RuntimeException("No PersistenceProvider handles type: " + type);
        }
        persistenceProvider.flush(type);
    }

    @Required
    public void setProviderRegistry(ProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

    @Required
    public void setMetadataRepository(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    private static final class DataObjectWrapperImpl<T> extends DataObjectWrapperBase<T> {
        private DataObjectWrapperImpl(T dataObject, DataObjectMetadata metadata, DataObjectService dataObjectService) {
            super(dataObject, metadata, dataObjectService);
        }
    }

}
