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
package edu.iu.uis.eden.routetemplate;

import java.util.Collections;
import java.util.List;

import edu.iu.uis.eden.routetemplate.dao.RuleDelegationDAO;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleDelegationServiceImpl implements RuleDelegationService {

    private RuleDelegationDAO dao;
    
    public List findByDelegateRuleId(Long ruleId) {
        if (ruleId == null) return Collections.EMPTY_LIST;
        return dao.findByDelegateRuleId(ruleId);
    }

    public void save(RuleDelegation ruleDelegation) {
        dao.save(ruleDelegation);
    }
    
    public void setRuleDelegationDAO(RuleDelegationDAO dao) {
        this.dao = dao;
    }
    public List findAllRuleDelegations(){
        return dao.findAllRuleDelegations();
    }
    public void delete(Long ruleDelegationId){
        dao.delete(ruleDelegationId);
    }
    
    public RuleDelegation findByRuleDelegationId(Long ruleDelegationId){
        return dao.findByRuleDelegationId(ruleDelegationId);
    }
}
