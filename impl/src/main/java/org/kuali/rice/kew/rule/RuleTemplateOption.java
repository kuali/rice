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
package org.kuali.rice.kew.rule;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.core.framework.persistence.jpa.OrmUtils;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.service.KEWServiceLocator;

import javax.persistence.*;
import java.io.Serializable;


/**
 * Defines default values and other preset information for a {@link RuleBaseValues} 
 * which is based off of the associated {@link RuleTemplate}.
 * 
 * @see RuleBaseValues
 * @see RuleTemplate
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_RULE_TMPL_OPTN_T")
//@Sequence(name="KREW_RULE_TMPL_OPTN_S", property="ruleTemplateOptionId")
public class RuleTemplateOption implements Serializable {

	private static final long serialVersionUID = 8913119135197149224L;
	@Id
	@GeneratedValue(generator="KREW_RULE_TMPL_OPTN_S")
	@GenericGenerator(name="KREW_RULE_TMPL_OPTN_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREW_RULE_TMPL_OPTN_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="RULE_TMPL_OPTN_ID")
	private Long ruleTemplateOptionId;
    @Column(name="RULE_TMPL_ID", insertable=false, updatable=false)
	private Long ruleTemplateId;
    @Column(name="KEY_CD")
	private String key;
    @Column(name="VAL")
	private String value;
    @Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;

    @ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="RULE_TMPL_ID")
	private RuleTemplate ruleTemplate;
    
    public RuleTemplateOption(){}
    
    public RuleTemplateOption(String key, String value){
        this.key = key;
        this.value = value;
    }

    //@PrePersist
    public void beforeInsert(){
        OrmUtils.populateAutoIncValue(this, KEWServiceLocator.getEntityManagerFactory().createEntityManager());
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

