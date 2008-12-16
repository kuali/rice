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

import org.kuali.rice.core.database.platform.Platform;
import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.dao.RuleTemplateDAO;



public class RuleTemplateDAOJpaImpl implements RuleTemplateDAO {

	@PersistenceContext(unitName="kew-unit")
	private EntityManager entityManager;
	
    public List findAll() {
        return entityManager.createNamedQuery("findAllOrderedByName").getResultList();
    }

    public RuleTemplate findByRuleTemplateName(String ruleTemplateName) {
        
    	Criteria crit = new Criteria(RuleTemplate.class.getName());
        crit.eq("name", ruleTemplateName);
        crit.orderBy("ruleTemplateId", false);
        
        List ruleTemplates =  new QueryByCriteria(entityManager, crit).toQuery().getResultList();
        
        if(ruleTemplates==null||ruleTemplates.size()==0){
        	return null;
        }
        return (RuleTemplate) ruleTemplates.get(0);
    }

    public List findByRuleTemplate(RuleTemplate ruleTemplate) {
        Criteria crit = new Criteria(RuleTemplate.class.getName());
        if (ruleTemplate.getName() != null) {
          crit.rawJpql("UPPER(RULE_TMPL_NM) like '"+ ruleTemplate.getName().toUpperCase() +"'");
        }
        if (ruleTemplate.getDescription() != null) {
          crit.rawJpql("UPPER(RULE_TMPL_DESC) like '"+ ruleTemplate.getDescription().toUpperCase()+"'");
        }
        return new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    }

    public void delete(Long ruleTemplateId) {
    	entityManager.remove(findByRuleTemplateId(ruleTemplateId));
    }

    public RuleTemplate findByRuleTemplateId(Long ruleTemplateId) {
        return entityManager.find(RuleTemplate.class, ruleTemplateId);
     }

    public void save(RuleTemplate ruleTemplate) {
    	if(ruleTemplate.getRuleTemplateId()==null){
    		entityManager.persist(ruleTemplate);
    	}else{
    		OrmUtils.reattach(ruleTemplate, entityManager.merge(ruleTemplate));
    	}
    }

    public Long getNextRuleTemplateId() {
       return getPlatform().getNextValSQL("KREW_RTE_TMPL_S", entityManager);
    }

    protected Platform getPlatform() {
    	return (Platform)GlobalResourceLoader.getService(RiceConstants.DB_PLATFORM);
    }


}
