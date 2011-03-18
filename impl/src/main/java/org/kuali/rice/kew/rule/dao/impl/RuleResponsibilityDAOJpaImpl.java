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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.dao.RuleResponsibilityDAO;


public class RuleResponsibilityDAOJpaImpl implements RuleResponsibilityDAO {
	
	@PersistenceContext(unitName="kew-unit")
	private EntityManager entityManager;
    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.rice.kew.rule.dao.RuleTemplateAttributeDAO#delete(java.lang.Long)
     */
    public void delete(Long ruleResponsibilityId) {
    	entityManager.remove(findByRuleResponsibilityId(ruleResponsibilityId));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.rice.kew.rule.dao.RuleTemplateAttributeDAO#findByRuleTemplateAttributeId(java.lang.Long)
     */
    public RuleResponsibility findByRuleResponsibilityId(Long ruleResponsibilityId) {
        return entityManager.find(RuleResponsibility.class, ruleResponsibilityId);
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
