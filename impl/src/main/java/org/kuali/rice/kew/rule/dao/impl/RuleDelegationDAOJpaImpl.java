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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.dao.RuleDelegationDAO;


public class RuleDelegationDAOJpaImpl implements RuleDelegationDAO {
	
	@PersistenceContext(unitName="kew-unit")
	private EntityManager entityManager;
	
    public List findByDelegateRuleId(Long ruleId) {
        Criteria crit = new Criteria(RuleDelegation.class.getName());
        crit.eq("delegateRuleId", ruleId);
        return (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    }

    public void save(RuleDelegation ruleDelegation) {
    	entityManager.merge(ruleDelegation);
    }
    public List findAllRuleDelegations(){
        Criteria crit = new Criteria(RuleDelegation.class.getName());
        return (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    }
    
    public RuleDelegation findByRuleDelegationId(Long ruleDelegationId){
        return entityManager.find(RuleDelegation.class, ruleDelegationId);

    }
    public void delete(Long ruleDelegationId){
    	entityManager.remove(findByRuleDelegationId(ruleDelegationId));
    }
}
