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
package org.kuali.rice.krad.dao.impl;

import org.kuali.rice.krad.dao.PersistedLookupMetadataDao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.sql.Timestamp;

@Deprecated
public class PersistedLookupMetadataDaoJpa implements PersistedLookupMetadataDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PersistedLookupMetadataDaoJpa.class);
    
	private EntityManager entityManager;
    
    @Override
    public void deleteOldLookupResults(Timestamp expirationDate) {
        Query query = entityManager.createNamedQuery("LookupResults.deleteOldLookupResults");
        query.setParameter("expirationDate", expirationDate);
        query.executeUpdate();
    }

    @Override
    public void deleteOldSelectedObjectIds(Timestamp expirationDate) {
        Query query = entityManager.createNamedQuery("SelectedObjectIds.deleteOldSelectedObjectIds");
        query.setParameter("expirationDate", expirationDate);
        query.executeUpdate();
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
