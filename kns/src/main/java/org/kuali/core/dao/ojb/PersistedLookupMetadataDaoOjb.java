/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.dao.ojb;

import java.sql.Timestamp;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.LookupResults;
import org.kuali.core.bo.SelectedObjectIds;
import org.kuali.core.dao.PersistedLookupMetadataDao;

public class PersistedLookupMetadataDaoOjb extends PlatformAwareDaoBaseOjb implements PersistedLookupMetadataDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PersistedLookupMetadataDaoOjb.class);
    
    /**
     * @see org.kuali.core.dao.PersistedLookupMetadataDao#deleteOldLookupResults(java.sql.Timestamp)
     */
    public void deleteOldLookupResults(Timestamp expirationDate) {
        Criteria criteria = new Criteria();
        criteria.addLessThan(PropertyConstants.LOOKUP_DATE, expirationDate);
        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(LookupResults.class, criteria));
    }

    /**
     * @see org.kuali.core.dao.PersistedLookupMetadataDao#deleteOldSelectedObjectIds(java.sql.Timestamp)
     */
    public void deleteOldSelectedObjectIds(Timestamp expirationDate) {
        Criteria criteria = new Criteria();
        criteria.addLessThan(PropertyConstants.LOOKUP_DATE, expirationDate);
        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(SelectedObjectIds.class, criteria));
    }
}
