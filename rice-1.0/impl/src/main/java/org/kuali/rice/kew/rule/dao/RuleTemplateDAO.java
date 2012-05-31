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
package org.kuali.rice.kew.rule.dao;

import java.util.List;

import org.kuali.rice.kew.rule.bo.RuleTemplate;


public interface RuleTemplateDAO {

    public void save(RuleTemplate ruleTemplate);
    public List findByRuleTemplate(RuleTemplate ruleTemplate);
    public List findAll();
    public RuleTemplate findByRuleTemplateId(Long ruleTemplateId);
    public void delete(Long ruleTemplateId);
    public RuleTemplate findByRuleTemplateName(String ruleTemplateName);
    public Long getNextRuleTemplateId();
    
}
