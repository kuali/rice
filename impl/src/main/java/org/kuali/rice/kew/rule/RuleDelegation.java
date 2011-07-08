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
package org.kuali.rice.kew.rule;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.kew.api.action.DelegationType;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


/**
 * A model bean representing the delegation of a rule from a responsibility to
 * another rule.  Specifies the delegation type which can be either
 * {@link KEWConstants#DELEGATION_PRIMARY} or {@link KEWConstants#DELEGATION_SECONDARY}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_DLGN_RSP_T")
//@Sequence(name="KREW_RTE_TMPL_S", property="ruleDelegationId")
public class RuleDelegation extends PersistableBusinessObjectBase {

	private static final long serialVersionUID = 7989203310473741293L;
	@Id
	@GeneratedValue(generator="KREW_RTE_TMPL_S")
	@GenericGenerator(name="KREW_RTE_TMPL_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREW_RTE_TMPL_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="DLGN_RULE_ID")
	private String ruleDelegationId;
    @Column(name="RSP_ID")
	private String responsibilityId;
    @Column(name="DLGN_RULE_BASE_VAL_ID", insertable=false, updatable=false)
	private String delegateRuleId;
    @Column(name="DLGN_TYP")
    private String delegationType = DelegationType.PRIMARY.getCode();

    @OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="DLGN_RULE_BASE_VAL_ID")
	private RuleBaseValues delegationRuleBaseValues;
//    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
//	@JoinColumn(name="RULE_RSP_ID")
//	private RuleResponsibility ruleResponsibility;

    public RuleDelegation() {
    }

    public Object copy(boolean preserveKeys) {
        RuleDelegation clone = new RuleDelegation();
        if (ruleDelegationId != null && preserveKeys) {
            clone.setRuleDelegationId(ruleDelegationId);
        }
        clone.setDelegationRuleBaseValues(delegationRuleBaseValues);
        clone.setDelegateRuleId(delegationRuleBaseValues.getRuleBaseValuesId());
        if (delegationType != null) {
            clone.setDelegationType(new String(delegationType));
        }
        return clone;
    }

    public String getDelegateRuleId() {
        return delegateRuleId;
    }
    public void setDelegateRuleId(String delegateRuleId) {
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
    public String getRuleDelegationId() {
        return ruleDelegationId;
    }
    public void setRuleDelegationId(String ruleDelegationId) {
        this.ruleDelegationId = ruleDelegationId;
    }

    /**
     * Returns the most recent RuleResponsibility for the responsibility
     * id on this RuleDelegation.
     */
    public RuleResponsibility getRuleResponsibility() {
    	if ( getResponsibilityId() == null ) {
    		return null;
    	}
    	return KEWServiceLocator.getRuleService().findRuleResponsibility(getResponsibilityId());
    }

    public DocumentType getDocumentType() {
        return this.getDelegationRuleBaseValues().getDocumentType();
    }

    public String getResponsibilityId() {
        return responsibilityId;
    }
    public void setResponsibilityId(String ruleResponsibilityId) {
        this.responsibilityId = ruleResponsibilityId;
    }

	/**
	 * An override of the refresh() method that properly preserves the RuleBaseValues instance. If the delegationRuleBaseValues property
	 * becomes null as a result of the refresh() method on the PersistableBusinessObjectBase superclass, an attempt is made to retrieve
	 * it by calling refreshReferenceObject() for the property. If that also fails, then the RuleBaseValues instance that was in-place
	 * prior to the refresh() superclass call will be used as the delegationRuleBaseValues property's value. This override is necessary
	 * in order to prevent certain exceptions during the cancellation of a rule delegation maintenance document.
	 * 
	 * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#refresh()
	 * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#refreshReferenceObject(java.lang.String)
	 */
	@Override
	public void refresh() {
		RuleBaseValues oldRuleBaseValues = this.getDelegationRuleBaseValues();
		super.refresh();
		if (this.getDelegationRuleBaseValues() == null) {
			this.refreshReferenceObject("delegationRuleBaseValues");
			if (this.getDelegationRuleBaseValues() == null) {
				this.setDelegationRuleBaseValues(oldRuleBaseValues);
			}
		}
	}
}

