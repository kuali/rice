/*
 * Copyright 2005-2007 The Kuali Foundation
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
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.dao.RuleTemplateDAO;
import org.springmodules.orm.ojb.PersistenceBrokerCallback;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;


public class RuleTemplateDAOOjbImpl extends PersistenceBrokerDaoSupport implements RuleTemplateDAO {


    public List<RuleTemplate> findAll() {
        QueryByCriteria query = new QueryByCriteria(RuleTemplate.class);
        query.addOrderByAscending("name");
        return (List)this.getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    public RuleTemplate findByRuleTemplateName(String ruleTemplateName) {
        Criteria crit = new Criteria();
        crit.addEqualTo("name", ruleTemplateName);
        QueryByCriteria query = new QueryByCriteria(RuleTemplate.class, crit);
        query.addOrderByDescending("ruleTemplateId");

        Iterator ruleTemplates = this.getPersistenceBrokerTemplate().getCollectionByQuery(query).iterator();
        while(ruleTemplates.hasNext()) {
            return (RuleTemplate) ruleTemplates.next();
        }
        return null;
    }

    public List<RuleTemplate> findByRuleTemplate(RuleTemplate ruleTemplate) {
        Criteria crit = new Criteria();
        if (ruleTemplate.getName() != null) {
          crit.addSql("UPPER(RULE_TMPL_NM) like '"+ ruleTemplate.getName().toUpperCase() +"'");
        }
        if (ruleTemplate.getDescription() != null) {
          crit.addSql("UPPER(RULE_TMPL_DESC) like '"+ ruleTemplate.getDescription().toUpperCase()+"'");
        }
        return new ArrayList<RuleTemplate>(this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleTemplate.class, crit)));
    }

    public void delete(String ruleTemplateId) {
    	this.getPersistenceBrokerTemplate().delete(findByRuleTemplateId(ruleTemplateId));
    }

    public RuleTemplate findByRuleTemplateId(String ruleTemplateId) {
        Criteria crit = new Criteria();
        crit.addEqualTo("ruleTemplateId", ruleTemplateId);
        return (RuleTemplate) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(RuleTemplate.class, crit));
    }

    public void save(RuleTemplate ruleTemplate) {
    	this.getPersistenceBrokerTemplate().store(ruleTemplate);
    }

    public String getNextRuleTemplateId() {
        return (String)this.getPersistenceBrokerTemplate().execute(new PersistenceBrokerCallback() {
            public Object doInPersistenceBroker(PersistenceBroker broker) {
            	  return String.valueOf(getPlatform().getNextValSQL("KREW_RTE_TMPL_S", broker));
            }
        });
    }

    protected DatabasePlatform getPlatform() {
    	return (DatabasePlatform)GlobalResourceLoader.getService(RiceConstants.DB_PLATFORM);
    }


}
