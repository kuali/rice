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
package org.kuali.rice.kew.rule.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.dao.RuleDelegationDAO;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;


public class RuleDelegationDAOOjbImpl extends PersistenceBrokerDaoSupport implements RuleDelegationDAO {

    public List findByDelegateRuleId(Long ruleId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("delegateRuleId", ruleId);
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleDelegation.class, crit));
    }

    public void save(RuleDelegation ruleDelegation) {
    	this.getPersistenceBrokerTemplate().store(ruleDelegation);
    }
    public List findAllRuleDelegations(){
        Criteria crit = new Criteria();
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleDelegation.class, crit));
    }
    
    public RuleDelegation findByRuleDelegationId(Long ruleDelegationId){
        Criteria crit = new Criteria();
        crit.addEqualTo("ruleDelegationId", ruleDelegationId);
        return (RuleDelegation) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(RuleDelegation.class, crit));

    }
    public void delete(Long ruleDelegationId){
    	this.getPersistenceBrokerTemplate().delete(findByRuleDelegationId(ruleDelegationId));
    }
    
    public List<RuleDelegation> findByResponsibilityIdWithCurrentRule(Long responsibilityId) {
    	Criteria crit = new Criteria();
    	crit.addEqualTo("responsibilityId", responsibilityId);
    	crit.addEqualTo("delegationRuleBaseValues.currentInd", true);
    	Collection delegations = getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleDelegation.class, crit));
    	return new ArrayList<RuleDelegation>(delegations);
    }
    
}
