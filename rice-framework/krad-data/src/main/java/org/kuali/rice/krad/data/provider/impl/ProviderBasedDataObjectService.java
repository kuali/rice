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

import com.google.common.collect.Sets;
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
import org.kuali.rice.krad.data.util.ReferenceLinker;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.Set;

/**
 * DataObjectService implementation backed by the {@link ProviderRegistry}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ProviderBasedDataObjectService implements DataObjectService {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(ProviderBasedDataObjectService.class);

    /**
     * The provider registry.
     */
	protected ProviderRegistry providerRegistry;

    /**
     * The metadata repository.
     */
	protected MetadataRepository metadataRepository;

    /**
     * The reference linker.
     */
    protected ReferenceLinker referenceLinker;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T find(Class<T> type, Object id) {
        return persistenceProviderForType(type).find(type, reduceCompoundKey(id));
    }

    /**
     * If the given id object is an instance of CompoundKey but there is only one entry in the key map, then just grab
     * that single value and treat it as a single id.
     *
     * @param id the potentially CompoundKey to reduce
     *
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

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> QueryResults<T> findMatching(Class<T> type, QueryByCriteria queryByCriteria) {
        return persistenceProviderForType(type).findMatching(type, queryByCriteria);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> QueryResults<T> findAll(Class<T> type) {
        return persistenceProviderForType(type).findAll(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T findUnique(Class<T> type, QueryByCriteria queryByCriteria) {
        QueryResults<T> results = findMatching(type, queryByCriteria);
        if (results.getResults().isEmpty()) {
            return null;
        } else if (results.getResults().size() > 1) {
            throw new IncorrectResultSizeDataAccessException("Attempted to find single result but found more than "
                    + "one for class " + type + " and criteria " + queryByCriteria, 1, results.getResults().size());
        } else {
            return results.getResults().get(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Object dataObject) {
        persistenceProviderForObject(dataObject).delete(dataObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void deleteMatching(Class<T> type, QueryByCriteria queryByCriteria) {
        persistenceProviderForType(type).deleteMatching(type, queryByCriteria);
    }

    /**
     *
     *{@inheritDoc}
     */
    @Override
    public <T> void deleteAll(Class<T> type) {
        persistenceProviderForType(type).deleteAll(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public <T> T save(T dataObject, PersistenceOption... options) {
        Set<PersistenceOption> optionSet = Sets.newHashSet(options);
        T saved = persistenceProviderForObject(dataObject).save(dataObject, options);
        if (optionSet.contains(PersistenceOption.LINK_KEYS)) {
            DataObjectWrapper<T> wrapper = wrap(saved);
            wrapper.linkForeignKeys(true);
        }
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetadataRepository getMetadataRepository() {
        return metadataRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T copyInstance(T dataObject) {
        return persistenceProviderForObject(dataObject).copyInstance(dataObject);
    }

    /**
     * {@inheritDoc}
     */
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
        return new DataObjectWrapperImpl<T>(dataObject, metadata, this, referenceLinker);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> boolean supports(Class<T> type) {
        return providerRegistry.getPersistenceProvider(type) != null;
    }

    /**
     * Gets the PersistenceProvider returned by the ProviderRegistry for the given type.
     *
     * @param type the type for which to get the provider.
     *
     * @return the PersistenceProvider returned by the ProviderRegistry for the given type.
     *
     * @throws RuntimeException if not PersistenceProvider handles given type.
     */
    protected PersistenceProvider persistenceProviderForType(Class<?> type) {
        PersistenceProvider provider = providerRegistry.getPersistenceProvider(type);
        if (provider == null) {
            throw new RuntimeException("No PersistenceProvider handles type: " + type);
        }
        return provider;
    }

    /**
	 * Gets the PersistenceProvider returned by the ProviderRegistry for the given object.
     *
     * @param object the object for which to get the provider.
     *
     * @return the PersistenceProvider returned by the ProviderRegistry for the given object.
     *
	 * @throws RuntimeException if not PersistenceProvider handles given type.
	 * @throws IllegalArgumentException if null object passed in.
	 */
    protected PersistenceProvider persistenceProviderForObject(Object object) {
		if (object == null) {
			throw new IllegalArgumentException("data object was null");
		}
		return persistenceProviderForType(object.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush(Class<?> type){
        PersistenceProvider persistenceProvider = persistenceProviderForType(type);
        if (persistenceProvider == null) {
            throw new RuntimeException("No PersistenceProvider handles type: " + type);
        }
        persistenceProvider.flush(type);
    }

    /**
     * Setter for the provider registry.
     *
     * @param providerRegistry the provider registry to set.
     */
    @Required
    public void setProviderRegistry(ProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

    /**
     * Setter for the metadata repository.
     *
     * @param metadataRepository the metadata repository to set.
     */
    @Required
    public void setMetadataRepository(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    /**
     * Gets the reference linker.
     *
     * @return the reference linker.
     */
    public ReferenceLinker getReferenceLinker() {
        return referenceLinker;
    }

    /**
     * Setter for the reference linker.
     *
     * @param referenceLinker the reference linker to set.
     */
    @Required
    public void setReferenceLinker(ReferenceLinker referenceLinker) {
        this.referenceLinker = referenceLinker;
    }

    /**
     * Defines a very basic implementation for {@link DataObjectWrapperBase}.
     * @param <T> the type of the data object to wrap.
     */
    private static final class DataObjectWrapperImpl<T> extends DataObjectWrapperBase<T> {

        /**
         * Creates a data object wrapper.
         *
         * @param dataObject the data object to wrap.
         * @param metadata the metadata of the data object.
         * @param dataObjectService the data object service to use.
         * @param referenceLinker the reference linker implementation.
         */
        private DataObjectWrapperImpl(T dataObject, DataObjectMetadata metadata, DataObjectService dataObjectService,
                ReferenceLinker referenceLinker) {
            super(dataObject, metadata, dataObjectService, referenceLinker);
        }
    }

}
