/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.dao.impl;

import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.dao.KimTypeDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimTypeDaoOjb extends PlatformAwareDaoBaseOjb implements KimTypeDao{

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.dao.KimTypeDao#getKimType(java.lang.String)
     */
    public KimTypeImpl getKimType(String kimTypeId) {
        Criteria c = new Criteria();

        c.addEqualTo("kimTypeId", kimTypeId);
        Query query = QueryFactory.newQuery(KimTypeImpl.class, c);
        return (KimTypeImpl)getPersistenceBrokerTemplate().getObjectByQuery(query);
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.dao.KimTypeDao#getKimTypeIdByName(java.lang.String, java.lang.String)
     */
    public KimTypeImpl getKimTypeByName(String namespace, String name) {
        Criteria c = new Criteria();

        c.addEqualTo("name", name);
        c.addEqualTo("namespaceCode", namespace);
        Query query = QueryFactory.newQuery(KimTypeImpl.class, c);
        return (KimTypeImpl)getPersistenceBrokerTemplate().getObjectByQuery(query);
    }

}
