/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kns.dao.impl;

import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria.QueryByCriteriaType;
import org.kuali.rice.kns.bo.LookupResults;
import org.kuali.rice.kns.bo.SelectedObjectIds;
import org.kuali.rice.kns.dao.PersistedLookupMetadataDao;
import org.kuali.rice.kns.util.KNSPropertyConstants;

public class PersistedLookupMetadataDaoJpa implements PersistedLookupMetadataDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PersistedLookupMetadataDaoJpa.class);
    
    @PersistenceContext
	private EntityManager entityManager;
    
    /**
     * @see org.kuali.rice.kns.dao.PersistedLookupMetadataDao#deleteOldLookupResults(java.sql.Timestamp)
     */
    public void deleteOldLookupResults(Timestamp expirationDate) {
        Criteria criteria = new Criteria(LookupResults.class.getName());
        criteria.lt(KNSPropertyConstants.LOOKUP_DATE, expirationDate);
        new QueryByCriteria(entityManager, criteria, QueryByCriteriaType.DELETE).toQuery().executeUpdate();
    }

    /**
     * @see org.kuali.rice.kns.dao.PersistedLookupMetadataDao#deleteOldSelectedObjectIds(java.sql.Timestamp)
     */
    public void deleteOldSelectedObjectIds(Timestamp expirationDate) {
        Criteria criteria = new Criteria(SelectedObjectIds.class.getName());
        criteria.lt(KNSPropertyConstants.LOOKUP_DATE, expirationDate);
        new QueryByCriteria(entityManager, criteria, QueryByCriteriaType.DELETE).toQuery().executeUpdate();
    }

    /**
     * @return the entityManager
     */
    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    /**
     * @param entityManager the entityManager to set
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
