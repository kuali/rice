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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.bo.user.KualiModuleUserProperty;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.dao.KualiModuleUserPropertyDao;
import org.springmodules.orm.ojb.PersistenceBrokerTemplate;

public class KualiModuleUserPropertyDaoOjb extends PlatformAwareDaoBaseOjb implements KualiModuleUserPropertyDao {

    public void save(KualiModuleUserProperty property) {
        getPersistenceBrokerTemplate().store( property );
    }

    public void save(Collection<KualiModuleUserProperty> properties) {
        PersistenceBrokerTemplate pbt = getPersistenceBrokerTemplate();
        for ( KualiModuleUserProperty prop : properties ) {
            pbt.store( prop );
        }
    }

    public Collection<KualiModuleUserProperty> getPropertiesForUser(UniversalUser user) {
        if ( user == null || user.getPersonUniversalIdentifier() == null ) {
            return new ArrayList<KualiModuleUserProperty>();
        }
        return getPropertiesForUser( user.getPersonUniversalIdentifier() );
    }

    public Collection<KualiModuleUserProperty> getPropertiesForUser(String personUniversalIdentifier) {
        if ( personUniversalIdentifier == null ) {
            return new ArrayList<KualiModuleUserProperty>();
        }
        Criteria criteria = new Criteria();
        criteria.addEqualTo( "personUniversalIdentifier", personUniversalIdentifier );
        return getPersistenceBrokerTemplate().getCollectionByQuery( QueryFactory.newQuery( KualiModuleUserProperty.class, criteria ) );
    }

}
