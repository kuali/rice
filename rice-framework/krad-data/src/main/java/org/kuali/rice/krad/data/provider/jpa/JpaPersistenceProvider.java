/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.data.provider.jpa;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceUtil;
import javax.persistence.metamodel.ManagedType;

import org.kuali.rice.core.api.criteria.LookupCustomizer;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.krad.data.CompoundKey;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.provider.PersistenceProvider;
import org.kuali.rice.krad.data.provider.ProviderRegistry;
import org.kuali.rice.krad.data.provider.query.jpa.JpaCriteriaQuery;
import org.kuali.rice.krad.data.provider.util.ReferenceLinker;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

/**
 * JPA PersistenceProvider impl
 */
@Transactional
public class JpaPersistenceProvider implements PersistenceProvider, InitializingBean {

    private EntityManager sharedEntityManager;
    private ProviderRegistry providerRegistry;
    private DataObjectService dataObjectService;
    private ReferenceLinker referenceLinker;

    public EntityManager getSharedEntityManager() {
        return sharedEntityManager;
    }

    public void setSharedEntityManager(EntityManager sharedEntityManager) {
        this.sharedEntityManager = sharedEntityManager;
    }

    @Required
    public void setProviderRegistry(ProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
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
    public <T> T save(T dataObject, PersistenceOption... options) {
        verifyDataObjectWritable(dataObject);

		Set<PersistenceOption> optionSet = Sets.newHashSet(options);

		dataObject = sharedEntityManager.merge(dataObject);

        if (!optionSet.contains(PersistenceOption.SKIP_LINKING)) {
            referenceLinker.linkObjects(dataObject);
        }

		return dataObject;
    }

    @Override
    public <T> T find(Class<T> type, Object id) {
        if (id instanceof CompoundKey) {
			QueryResults<T> results = findMatching(type,
					QueryByCriteria.Builder.forAttributes(((CompoundKey) id).getKeys()));
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

    @Override
    public <T> QueryResults<T> findMatching(Class<T> type, QueryByCriteria queryByCriteria) {
        return new JpaCriteriaQuery(sharedEntityManager).lookup(type, queryByCriteria);
    }

    @Override
    public <T> QueryResults<T> findMatching(Class<T> type, QueryByCriteria queryByCriteria, LookupCustomizer<T> lookupCustomizer) {
        return new JpaCriteriaQuery(sharedEntityManager).lookup(type, queryByCriteria, lookupCustomizer);
    }

    @Override
    public void delete(Object dataObject) {
        verifyDataObjectWritable(dataObject);
        sharedEntityManager.remove(sharedEntityManager.merge(dataObject));
    }

    @Override
    public boolean handles(Class<?> type) {
        try {
            ManagedType<?> managedType = sharedEntityManager.getMetamodel().managedType(type);
            return managedType != null;
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    @Override
    public boolean isProxied(Object dataObject) {
        PersistenceUtil persistenceUtil = sharedEntityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        return persistenceUtil.isLoaded(dataObject);
    }

    @Override
    public Object resolveProxy(Object dataObject) {
        dataObject.equals(null);
        return dataObject;
    }

    protected void verifyDataObjectWritable(Object dataObject) {
        DataObjectMetadata metaData = dataObjectService.getMetadataRepository().getMetadata(dataObject.getClass());
        if (metaData.isReadOnly()) {
            throw new UnsupportedOperationException(dataObject.getClass() + " is read-only");
        }
    }

}
