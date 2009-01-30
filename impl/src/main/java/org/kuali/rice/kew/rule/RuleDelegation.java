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
package org.kuali.rice.kew.rule;

import java.util.LinkedHashMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kuali.rice.core.jpa.annotations.Sequence;
import org.kuali.rice.kew.bo.KewPersistableBusinessObjectBase;
import org.kuali.rice.kew.util.KEWConstants;


/**
 * A model bean representing the delegation of a rule from a responsibility to
 * another rule.  Specifies the delegation type which can be either 
 * {@link KEWConstants#DELEGATION_PRIMARY} or {@link KEWConstants#DELEGATION_SECONDARY}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KREW_DLGN_RSP_T")
@Sequence(name="KREW_RTE_TMPL_S", property="ruleDelegationId")
public class RuleDelegation extends KewPersistableBusinessObjectBase {
    
	private static final long serialVersionUID = 7989203310473741293L;
	@Id
	@Column(name="DLGN_RULE_ID")
	private Long ruleDelegationId;
    @Column(name="RSP_ID", insertable=false, updatable=false)
	private Long responsibilityId;
    @Column(name="DLGN_RULE_BASE_VAL_ID", insertable=false, updatable=false)
	private Long delegateRuleId;
    @Column(name="DLGN_TYP")
    private String delegationType = KEWConstants.DELEGATION_PRIMARY;
    
    @OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="DLGN_RULE_BASE_VAL_ID")
	private RuleBaseValues delegationRuleBaseValues;
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="RULE_RSP_ID")
	private RuleResponsibility ruleResponsibility;
    
    public RuleDelegation() {
    }

    public Object copy(boolean preserveKeys) {
        RuleDelegation clone = new RuleDelegation();
        if (ruleDelegationId != null && preserveKeys) {
            clone.setRuleDelegationId(new Long(ruleDelegationId.longValue()));
        }
        clone.setDelegationRuleBaseValues(delegationRuleBaseValues);
        clone.setDelegateRuleId(delegationRuleBaseValues.getRuleBaseValuesId());
        if (delegationType != null) {
            clone.setDelegationType(new String(delegationType));
        }
        return clone;
    }

    public Long getDelegateRuleId() {
        return delegateRuleId;
    }
    public void setDelegateRuleId(Long delegateRuleId) {
        this.delegateRuleId = delegateRuleId;
    }
    public RuleBaseValues getDelegationRuleBaseValues() {
        return delegationRuleBaseValues;
    }
    public void setDelegationRuleBaseValues(RuleBaseValues delegationRuleBaseValues) {
        this.delegationRuleBaseValues = delegationRuleBaseValues;
    }
    public String getDelegationType() {
        return delegationType;
    }
    public void setDelegationType(String delegationType) {
        this.delegationType = delegationType;
    }
    public Long getRuleDelegationId() {
        return ruleDelegationId;
    }
    public void setRuleDelegationId(Long ruleDelegationId) {
        this.ruleDelegationId = ruleDelegationId;
    }
    public RuleResponsibility getRuleResponsibility() {
        return ruleResponsibility;
    }
    public void setRuleResponsibility(RuleResponsibility ruleResponsibility) {
        this.ruleResponsibility = ruleResponsibility;
    }
    public Long getResponsibilityId() {
        return responsibilityId;
    }
    public void setResponsibilityId(Long ruleResponsibilityId) {
        this.responsibilityId = ruleResponsibilityId;
    }

	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap map = new LinkedHashMap();
		map.put("ruleDelegationId", ruleDelegationId);
		map.put("responsibilityId", responsibilityId);
		map.put("delegateRuleId", delegateRuleId);
		map.put("delegationType", delegationType);
		return map;
	}
    
    
}

