/*
 * Copyright 2005-2008 The Kuali Foundation
 *
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
package org.kuali.rice.kew.rule.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.kuali.rice.core.framework.persistence.jpa.OrmUtils;
import org.kuali.rice.core.framework.persistence.jpa.criteria.Criteria;
import org.kuali.rice.core.framework.persistence.jpa.criteria.QueryByCriteria;
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
    	if(ruleDelegation.getRuleDelegationId()==null){
    		entityManager.persist(ruleDelegation);
    	}else{
    		OrmUtils.merge(entityManager, ruleDelegation);
    	}
    }
    public List findAllCurrentRuleDelegations(){
        Criteria crit = new Criteria(RuleDelegation.class.getName());
        crit.eq("delegationRuleBaseValues.currentInd", true);
        return (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    }

    public RuleDelegation findByRuleDelegationId(Long ruleDelegationId){
        return entityManager.find(RuleDelegation.class, ruleDelegationId);

    }
    public void delete(Long ruleDelegationId){
    	entityManager.remove(findByRuleDelegationId(ruleDelegationId));
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<RuleDelegation> findByResponsibilityIdWithCurrentRule(Long responsibilityId) {
    	Criteria crit = new Criteria(RuleDelegation.class.getName());
    	crit.eq("responsibilityId", responsibilityId);
    	crit.eq("delegationRuleBaseValues.currentInd", true);
    	Collection delegations = new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    	return new ArrayList<RuleDelegation>(delegations);
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kew.rule.dao.RuleDelegationDAO#search(java.lang.String, java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean, java.util.Map, java.lang.String)
     */
    public List<RuleDelegation> search(String parentRuleBaseVaueId, String parentResponsibilityId, String docTypeName, Long ruleId,
            Long ruleTemplateId, String ruleDescription, String workgroupId,
            String workflowId, String delegationType, Boolean activeInd,
            Map extensionValues, String workflowIdDirective) {
        // TODO jjhanso - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kew.rule.dao.RuleDelegationDAO#search(java.lang.String, java.lang.Long, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.Boolean, java.util.Map, java.util.Collection)
     */
    public List<RuleDelegation> search(String parentRuleBaseVaueId, String parentResponsibilityId, String docTypeName, Long ruleTemplateId,
            String ruleDescription, Collection<String> workgroupIds,
            String workflowId, String delegationType, Boolean activeInd,
            Map extensionValues, Collection actionRequestCodes) {
        // TODO jjhanso - THIS METHOD NEEDS JAVADOCS
        return null;
    }

}
