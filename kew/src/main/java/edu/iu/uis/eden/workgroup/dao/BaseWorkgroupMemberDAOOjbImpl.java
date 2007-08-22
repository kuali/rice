/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.workgroup.dao;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.workgroup.BaseWorkgroupMember;

public class BaseWorkgroupMemberDAOOjbImpl extends PersistenceBrokerDaoSupport implements BaseWorkgroupMemberDAO {

    public List findByWorkflowId(String workflowId) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("workflowId", workflowId);
        criteria.addEqualTo("workgroup.currentInd", Boolean.TRUE);
        return (List)getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(BaseWorkgroupMember.class, criteria));        		
    }

}