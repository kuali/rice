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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.metamodel.ManagedType;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.data.CompoundKey;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.provider.PersistenceProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.ChainedPersistenceExceptionTranslator;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

/**
 * Java Persistence API (JPA) implementation of {@link PersistenceProvider}.
 *
 * <p>
 * When creating a new instance of this provider, a reference to a "shared" entity manager (like that created by
 * Spring's {@link org.springframework.orm.jpa.support.SharedEntityManagerBean} must be injected. Additionally, a
 * reference to the {@link DataObjectService} must be injected as well.
 * </p>
 *
 * <p>
 * This class will perform persistence exception translation (converting JPA exceptions to
 * {@link org.springframework.dao.DataAccessException}s. It will scan the
 * {@link org.springframework.beans.factory.BeanFactory} in which it was created to find beans which implement
 * {@link org.springframework.dao.support.PersistenceExceptionTranslator} and use those translators for translation.
 * </p>
 *
 * @see org.springframework.orm.jpa.support.SharedEntityManagerBean
 * @see org.springframework.dao.support.PersistenceExceptionTranslator
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Transactional
public class JpaPersistenceProvider implements PersistenceProvider, BeanFactoryAware {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(JpaPersistenceProvider.class);

    /**
     * Indicates if a JPA {@code EntityManager} flush should be automatically executed when calling
     * {@link org.kuali.rice.krad.data.DataObjectService#save(Object, org.kuali.rice.krad.data.PersistenceOption...)}
     * using a JPA provider.
     *
     * <p>This is recommended for testing only since the change is global and would affect all persistence units.</p>
     */
    public static final String AUTO_FLUSH = "rice.krad.data.jpa.autoFlush";

    private EntityManager sharedEntityManager;
    private DataObjectService dataObjectService;

    private PersistenceExceptionTranslator persistenceExceptionTranslator;

    /**
     * Initialization-on-demand holder idiom for thread-safe lazy loading of configuration.
     */
    private static final class LazyConfigHolder {
        private static final boolean autoFlush = ConfigContext.getCurrentContextConfig().getBooleanProperty(AUTO_FLUSH, false);
    }

    /**
     * Gets the shared {@link EntityManager}.
     *
     * @return The shared {@link EntityManager}.
     */
    public EntityManager getSharedEntityManager() {
        return sharedEntityManager;
    }

    /**
     * Setter for the shared {@link EntityManager}.
     *
     * @param sharedEntityManager The shared {@link EntityManager} to set.
     */
    public void setSharedEntityManager(EntityManager sharedEntityManager) {
        this.sharedEntityManager = sharedEntityManager;
    }

    /**
     * Setter for the {@link DataObjectService}.
     *
     * @param dataObjectService The {@link DataObjectService} to set.
     */
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    /**
     * Returns the {@link DataObjectService}.
     *
     * @return a {@link DataObjectService}
     */
    public DataObjectService getDataObjectService() {
        return this.dataObjectService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "Cannot use PersistenceExceptionTranslator autodetection without ListableBeanFactory");
        }
        this.persistenceExceptionTranslator = detectPersistenceExceptionTranslators((ListableBeanFactory)beanFactory);
    }

    /**
     * Gets any {@link PersistenceExceptionTranslator}s from the {@link BeanFactory}.
     *
     * @param beanFactory The {@link BeanFactory} to use.
     *
     * @return A {@link PersistenceExceptionTranslator} from the {@link BeanFactory}.
     */
    protected PersistenceExceptionTranslator detectPersistenceExceptionTranslators(ListableBeanFactory beanFactory) {
        // Find all translators, being careful not to activate FactoryBeans.
        Map<String, PersistenceExceptionTranslator> pets = BeanFactoryUtils.beansOfTypeIncludingAncestors(beanFactory,
                PersistenceExceptionTranslator.class, false, false);
        ChainedPersistenceExceptionTranslator cpet = new ChainedPersistenceExceptionTranslator();
        for (PersistenceExceptionTranslator pet : pets.values()) {
            cpet.addDelegate(pet);
        }
        // always add one last persistence exception translator as a catch all
        cpet.addDelegate(new DefaultPersistenceExceptionTranslator());
        return cpet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T save(final T dataObject, final PersistenceOption... options) {
        return doWithExceptionTranslation(new Callable<T>() {
            @Override
			public T call() {
                verifyDataObjectWritable(dataObject);

        		Set<PersistenceOption> optionSet = Sets.newHashSet(options);

		        T mergedDataObject = sharedEntityManager.merge(dataObject);

                // We must flush if they pass us a flush option, have auto flush turned on, or are synching keys
                // after save. We are required to flush before synching because we may need to use generated values to
                // perform synchronization and those won't be there until after a flush
                //
                // note that the actual synchronization of keys is handled automatically by the framework after the
                // save has been completed
                if(optionSet.contains(PersistenceOption.FLUSH) || optionSet.contains(PersistenceOption.LINK_KEYS) ||
                        LazyConfigHolder.autoFlush){
					sharedEntityManager.flush();
					// if (sharedEntityManager.getEntityManagerFactory().getCache() != null) {
					// Object dataObjectKey = sharedEntityManager.getEntityManagerFactory().getPersistenceUnitUtil()
					// .getIdentifier(mergedDataObject);
					// if (dataObjectKey != null) {
					// sharedEntityManager.getEntityManagerFactory().getCache()
					// .evict(dataObject.getClass(), dataObjectKey);
					// }
					// }
                }

                return mergedDataObject;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T find(final Class<T> type, final Object id) {
        return doWithExceptionTranslation(new Callable<T>() {
            @Override
			public T call() {
                if (id instanceof CompoundKey) {
			        QueryResults<T> results = findMatching(type,
				        	QueryByCriteria.Builder.andAttributes(((CompoundKey) id).getKeys()).build());
			        if (results.getResults().size() > 1) {
				        throw new NonUniqueResultException("Error Compound Key: " + id + " on class " + type.getName()
					        	+ " returned more than one row.");
			        }
                    if (!results.getResults().isEmpty()) {
				        return results.getResults().get(0);
                    }
			        return null;
                } else {
                    return sharedEntityManager.find(type, id);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> QueryResults<T> findMatching(final Class<T> type, final QueryByCriteria queryByCriteria) {
        return doWithExceptionTranslation(new Callable<QueryResults<T>>() {
            @Override
			public QueryResults<T> call() {
                return new JpaCriteriaQuery(sharedEntityManager).lookup(type, queryByCriteria);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> QueryResults<T> findAll(final Class<T> type) {
        return doWithExceptionTranslation(new Callable<QueryResults<T>>() {
            @Override
            public QueryResults<T> call() {
                return new JpaCriteriaQuery(getSharedEntityManager()).lookup(type, QueryByCriteria.Builder.create().build());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final Object dataObject) {
        doWithExceptionTranslation(new Callable<Object>() {
            @Override
			public Object call() {
                verifyDataObjectWritable(dataObject);
				Object mergedDataObject = sharedEntityManager.merge(dataObject);
				sharedEntityManager.remove(mergedDataObject);
				// if (sharedEntityManager.getEntityManagerFactory().getCache() != null) {
				// Object dataObjectKey = sharedEntityManager.getEntityManagerFactory().getPersistenceUnitUtil()
				// .getIdentifier(mergedDataObject);
				// if (dataObjectKey != null) {
				// sharedEntityManager.getEntityManagerFactory().getCache()
				// .evict(dataObject.getClass(), dataObjectKey);
				// }
				// }
                return null;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void deleteMatching(final Class<T> type, final QueryByCriteria queryByCriteria) {
        doWithExceptionTranslation(new Callable<Object>() {
            @Override
            public Object call() {
                new JpaCriteriaQuery(getSharedEntityManager()).deleteMatching(type, queryByCriteria);
				// If the L2 cache is enabled, items will still be served from the cache
				// So, we need to flush that as well for the given type
				if (sharedEntityManager.getEntityManagerFactory().getCache() != null) {
					sharedEntityManager.getEntityManagerFactory().getCache().evict(type);
				}
                return null;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void deleteAll(final Class<T> type) {
        doWithExceptionTranslation(new Callable<Object>() {
            @Override
            public Object call() {
                new JpaCriteriaQuery(getSharedEntityManager()).deleteAll(type);
				// If the L2 cache is enabled, items will still be served from the cache
				// So, we need to flush that as well for the given type
				if (sharedEntityManager.getEntityManagerFactory().getCache() != null) {
					sharedEntityManager.getEntityManagerFactory().getCache().evict(type);
				}
                return null;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T copyInstance(final T dataObject) {
        return doWithExceptionTranslation(new Callable<T>() {
            @Override
            public T call() {
                return (T) sharedEntityManager.unwrap(JpaEntityManager.class).getDatabaseSession().copy(dataObject);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handles(final Class<?> type) {
        return doWithExceptionTranslation(new Callable<Boolean>() {
            @Override
			public Boolean call() {
                try {
                    ManagedType<?> managedType = sharedEntityManager.getMetamodel().managedType(type);
                    return Boolean.valueOf(managedType != null);
                } catch (IllegalArgumentException iae) {
                    return Boolean.FALSE;
				} catch (IllegalStateException ex) {
					// This catches cases where the entity manager is not initialized or has already been destroyed
					LOG.warn("sharedEntityManager " + sharedEntityManager + " is not in a state to be used: "
							+ ex.getMessage());
					return Boolean.FALSE;
                }
            }
        }).booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush(final Class<?> type) {
        doWithExceptionTranslation(new Callable<Object>() {
            @Override
			public Object call() {
                sharedEntityManager.flush();
				// If the L2 cache is enabled, items will still be served from the cache
				// So, we need to flush that as well for the given type
				if (sharedEntityManager.getEntityManagerFactory().getCache() != null) {
					sharedEntityManager.getEntityManagerFactory().getCache().evict(type);
				}
                return null;
            }
        });
    }

    /**
     * Verifies that the data object can be written to.
     *
     * @param dataObject The data object to check.
     */
    protected void verifyDataObjectWritable(Object dataObject) {
        DataObjectMetadata metaData = dataObjectService.getMetadataRepository().getMetadata(dataObject.getClass());
        if (metaData == null) {
            throw new IllegalArgumentException("Given data object class is not loaded into the MetadataRepository: " + dataObject.getClass());
        }
        if (metaData.isReadOnly()) {
            throw new UnsupportedOperationException(dataObject.getClass() + " is read-only");
        }
    }

    /**
     * Surrounds the transaction with a try/catch block that can use the {@link PersistenceExceptionTranslator} to
     * translate the exception if necessary.
     *
     * @param callable The data operation to invoke.
     * @param <T> The type of the data operation.
     *
     * @return The result from the data operation, if successful.
     */
    protected <T> T doWithExceptionTranslation(Callable<T> callable) {
        try {
            return callable.call();
        }
        catch (RuntimeException ex) {
            throw DataAccessUtils.translateIfNecessary(ex, this.persistenceExceptionTranslator);
        } catch (Exception ex) {
            // this should really never happen based on the internal usage in this class
            throw new RiceRuntimeException("Unexpected checked exception during data access.", ex);
        }
    }

    /**
     * Defines a default {@link PersistenceExceptionTranslator} if no others exist.
     */
    private static final class DefaultPersistenceExceptionTranslator implements PersistenceExceptionTranslator {

        /**
         * {@inheritDoc}
         */
        @Override
        public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
            return EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(ex);
        }

    }

}
