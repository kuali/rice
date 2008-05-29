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

import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;

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
@Entity
@Table(name="EN_RULE_TMPL_OPTN_T")
public class RuleTemplateOption implements WorkflowPersistable {

	private static final long serialVersionUID = 8913119135197149224L;
	@Id
	@Column(name="RULE_TMPL_OPTN_ID")
	private Long ruleTemplateOptionId;
    @Column(name="RULE_TMPL_ID")
	private Long ruleTemplateId;
    @Column(name="RULE_TMPL_OPTN_KEY")
	private String key;
    @Column(name="RULE_TMPL_OPTN_VAL")
	private String value;
    @Version
	@Column(name="DB_LOCK_VER_NBR")
	private Integer lockVerNbr;

    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="RULE_TMPL_ID", insertable=false, updatable=false)
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

