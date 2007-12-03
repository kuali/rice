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

import edu.iu.uis.eden.WorkflowPersistable;

/**
 * Defines default values and other preset information for a {@link RuleBaseValues} 
 * which is based off of the associated {@link RuleTemplate}.
 * 
 * @see RuleBaseValues
 * @see RuleTemplate
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleTemplateOption implements WorkflowPersistable {

	private static final long serialVersionUID = 8913119135197149224L;
	private Long ruleTemplateOptionId;
    private Long ruleTemplateId;
    private String key;
    private String value;
    private Integer lockVerNbr;

    private RuleTemplate ruleTemplate;
    
    public RuleTemplateOption(){}
    
    public RuleTemplateOption(String key, String value){
        this.key = key;
        this.value = value;
    }

    public Object copy(boolean preserveKeys) {
        RuleTemplateOption ruleTemplateOptionClone = new RuleTemplateOption();

        if (key != null) {
            ruleTemplateOptionClone.setKey(new String(key));
        }
        if (value != null) {
            ruleTemplateOptionClone.setValue(new String(value));
        }
        if (preserveKeys && ruleTemplateOptionId != null) {
            ruleTemplateOptionClone.setRuleTemplateOptionId(new Long(ruleTemplateOptionId.longValue()));
        }
        return ruleTemplateOptionClone;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    public RuleTemplate getRuleTemplate() {
        return ruleTemplate;
    }

    public void setRuleTemplate(RuleTemplate ruleTemplate) {
        this.ruleTemplate = ruleTemplate;
    }

    public Long getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public Long getRuleTemplateOptionId() {
        return ruleTemplateOptionId;
    }

    public void setRuleTemplateOptionId(Long ruleTemplateOptionId) {
        this.ruleTemplateOptionId = ruleTemplateOptionId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
