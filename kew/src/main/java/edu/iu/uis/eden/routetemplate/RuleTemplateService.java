/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import java.util.List;

import edu.iu.uis.eden.XmlLoader;
import edu.iu.uis.eden.xml.export.XmlExporter;

/**
 * A service providing data access for {@link RuleTemplate}s and 
 * {@link RuleTemplateAttribute}s.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface RuleTemplateService extends XmlLoader, XmlExporter {

    public void save(RuleTemplate ruleTemplate);
    public void save(RuleTemplateAttribute ruleTemplateAttribute);
    public void save(RuleBaseValues ruleBaseValues);
    public void save(RuleDelegation ruleDelegation, RuleBaseValues ruleBaseValues);
    public RuleTemplate findByRuleTemplateId(Long ruleTemplateId);
    public List findAll();
    public List findByRuleTemplate(RuleTemplate ruleTemplate);
    public void delete(Long ruleTemplateId);
    
    public void deleteRuleTemplateOption(Long ruleTemplateOptionId);
//    public void deleteRuleTemplateAttribute(Long ruleTemplateAttributeId, List ruleTemplateAttributes);
    public RuleTemplateAttribute findByRuleTemplateAttributeId(Long ruleTemplateAttributeId);
    public RuleTemplate findByRuleTemplateName(String ruleTemplateName);
    public Long getNextRuleTemplateId();
    
}
