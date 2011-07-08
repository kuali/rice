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
package org.kuali.rice.kew.rule.service;

import java.util.List;

import org.kuali.rice.core.framework.impex.xml.XmlExporter;
import org.kuali.rice.core.framework.impex.xml.XmlLoader;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;


/**
 * A service providing data access for {@link RuleTemplate}s and 
 * {@link RuleTemplateAttribute}s.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface RuleTemplateService extends XmlLoader, XmlExporter {

    public void save(RuleTemplate ruleTemplate);
    public void save(RuleTemplateAttribute ruleTemplateAttribute);
    public void saveRuleDefaults(RuleDelegation ruleDelegation, RuleBaseValues ruleBaseValues);
    public RuleTemplate findByRuleTemplateId(String ruleTemplateId);
    public List<RuleTemplate> findAll();
    public List findByRuleTemplate(RuleTemplate ruleTemplate);
    public void delete(String ruleTemplateId);
    
    public void deleteRuleTemplateOption(String ruleTemplateOptionId);
//    public void deleteRuleTemplateAttribute(Long ruleTemplateAttributeId, List ruleTemplateAttributes);
    public RuleTemplateAttribute findByRuleTemplateAttributeId(String ruleTemplateAttributeId);
    public RuleTemplate findByRuleTemplateName(String ruleTemplateName);
    public String getNextRuleTemplateId();
    
}
