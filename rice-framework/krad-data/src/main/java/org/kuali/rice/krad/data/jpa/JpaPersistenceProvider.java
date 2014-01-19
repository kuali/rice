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

import com.google.common.collect.Sets;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.criteria.LookupCustomizer;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.data.CompoundKey;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.data.config.ConfigConstants;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.provider.PersistenceProvider;
import org.kuali.rice.krad.data.provider.util.ReferenceLinker;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.ChainedPersistenceExceptionTranslator;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.metamodel.ManagedType;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Java Persistence API (JPA) implementation of {@link PersistenceProvider}.
 *
 * <p>When creating a new instance of this provider, a reference to a "shared" entity manager (like that created by
 * Spring's {@link org.springframework.orm.jpa.support.SharedEntityManagerBean} must be injected. Additionally, a
 * reference to the {@link DataObjectService} must be injected as well.</p>
 *
 * <p>This class will perform persistence exception translation (converting JPA exceptions to
 * {@link org.springframework.dao.DataAccessException}s. It will scan the
 * {@link org.springframework.beans.factory.BeanFactory} in which it was created to find beans which implement
 * {@link org.springframework.dao.support.PersistenceExceptionTranslator} and use those translators for translation.</p>
 *
 * @see org.springframework.orm.jpa.support.SharedEntityManagerBean
 * @see org.springframework.dao.support.PersistenceExceptionTranslator
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Transactional
public class JpaPersistenceProvider implements PersistenceProvider, InitializingBean, BeanFactoryAware {

    private EntityManager sharedEntityManager;
    private DataObjectService dataObjectService;

    private ReferenceLinker referenceLinker;
    private PersistenceExceptionTranslator persistenceExceptionTranslator;

    /**
     * Initialization-on-demand holder idiom for thread-safe lazy loading of configuration.
     */
    private static final class LazyConfigHolder {
        private static final boolean autoFlush = ConfigContext.getCurrentContextConfig().getBooleanProperty(ConfigConstants.JPA_AUTO_FLUSH, false);
    }

    public EntityManager getSharedEntityManager() {
        return sharedEntityManager;
    }

    public void setSharedEntityManager(EntityManager sharedEntityManager) {
        this.sharedEntityManager = sharedEntityManager;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.referenceLinker = new ReferenceLinker(dataObjectService);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ListableBeanFactory)) {
            throw new IllegalArgumentException(
                    "Cannot use PersistenceExceptionTranslator autodetection without ListableBeanFactory");
        }
        this.persistenceExceptionTranslator = detectPersistenceExceptionTranslators((ListableBeanFactory)beanFactory);
    }

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

    @Override
    public <T> T save(final T dataObject, final PersistenceOption... options) {
        return doWithExceptionTranslation(new Callable<T>() {
            public T call() {
                verifyDataObjectWritable(dataObject);

        		Set<PersistenceOption> optionSet = Sets.newHashSet(options);

		        T mergedDataObject = sharedEntityManager.merge(dataObject);

                if (optionSet.contains(PersistenceOption.LINK)) {
                    referenceLinker.linkObjects(mergedDataObject);
                }

                if(optionSet.contains(PersistenceOption.FLUSH) || LazyConfigHolder.autoFlush){
			        sharedEntityManager.flush();
                }

        		return mergedDataObject;
            }
        });
    }

    @Override
    public <T> T find(final Class<T> type, final Object id) {
        return doWithExceptionTranslation(new Callable<T>() {
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

    @Override
    public <T> QueryResults<T> findMatching(final Class<T> type, final QueryByCriteria queryByCriteria) {
        return doWithExceptionTranslation(new Callable<QueryResults<T>>() {
            public QueryResults<T> call() {
                return new JpaCriteriaQuery(sharedEntityManager).lookup(type, queryByCriteria);
            }
        });
    }

    @Override
    public <T> QueryResults<T> findMatching(final Class<T> type, final QueryByCriteria queryByCriteria,
            final LookupCustomizer<T> lookupCustomizer) {
        return doWithExceptionTranslation(new Callable<QueryResults<T>>() {
            public QueryResults<T> call() {
                return new JpaCriteriaQuery(sharedEntityManager).lookup(type, queryByCriteria, lookupCustomizer);
            }
        });
    }

    @Override
    public void delete(final Object dataObject) {
        doWithExceptionTranslation(new Callable<Object>() {
            public Object call() {
                verifyDataObjectWritable(dataObject);
                sharedEntityManager.remove(sharedEntityManager.merge(dataObject));
                return null;
            }
        });
    }

    @Override
    public boolean handles(final Class<?> type) {
        return doWithExceptionTranslation(new Callable<Boolean>() {
            public Boolean call() {
                try {
                    ManagedType<?> managedType = sharedEntityManager.getMetamodel().managedType(type);
                    return Boolean.valueOf(managedType != null);
                } catch (IllegalArgumentException iae) {
                    return Boolean.FALSE;
                }
            }
        }).booleanValue();
    }

    @Override
    public void flush(final Class<?> type) {
        doWithExceptionTranslation(new Callable<Object>() {
            public Object call() {
                sharedEntityManager.flush();
                return null;
            }
        });
    }

    protected void verifyDataObjectWritable(Object dataObject) {
        DataObjectMetadata metaData = dataObjectService.getMetadataRepository().getMetadata(dataObject.getClass());
        if (metaData == null) {
            throw new IllegalArgumentException("Given data object class is not loaded into the MetadataRepository: " + dataObject.getClass());
        }
        if (metaData.isReadOnly()) {
            throw new UnsupportedOperationException(dataObject.getClass() + " is read-only");
        }
    }

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

    private static final class DefaultPersistenceExceptionTranslator implements PersistenceExceptionTranslator {

        @Override
        public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
            return EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(ex);
        }

    }

}
