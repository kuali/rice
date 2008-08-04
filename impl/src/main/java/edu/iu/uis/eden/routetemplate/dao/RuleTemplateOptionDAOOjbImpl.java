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
package edu.iu.uis.eden.routetemplate.dao;

import org.apache.ojb.broker.query.QueryByCriteria;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.routetemplate.RuleTemplateOption;

public class RuleTemplateOptionDAOOjbImpl extends PersistenceBrokerDaoSupport implements RuleTemplateOptionDAO {

  
  /*
   * (non-Javadoc)
   * @see edu.iu.uis.eden.routetemplate.dao.RuleTemplateOptionDAO#delete(java.lang.Long)
   */
  public void delete(Long ruleTemplateOptionId) {
	  this.getPersistenceBrokerTemplate().delete(findByRuleTemplateOptionId(ruleTemplateOptionId));
  }

  /*
   * (non-Javadoc)
   * @see edu.iu.uis.eden.routetemplate.dao.RuleTemplateOptionDAO#findByRuleTemplateOptionId(java.lang.Long)
   */
  public RuleTemplateOption findByRuleTemplateOptionId(Long ruleTemplateOptionId) {
  	RuleTemplateOption ruleTemplateOption = new RuleTemplateOption();
    ruleTemplateOption.setRuleTemplateOptionId(ruleTemplateOptionId);
    return (RuleTemplateOption) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(ruleTemplateOption));
  }
  
  public void save (RuleTemplateOption ruleTemplateOption){
	  this.getPersistenceBrokerTemplate().store(ruleTemplateOption);
  }
}