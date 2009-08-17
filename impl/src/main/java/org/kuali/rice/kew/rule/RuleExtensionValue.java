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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;

import org.kuali.rice.core.jpa.annotations.Sequence;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kew.bo.WorkflowPersistable;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.Utilities;


/**
 * The value of an extension to a rule.  Essentially contains a
 * key-value pair containing the key of the extension data and
 * it's value.
 * 
 * @see RuleBaseValues
 * @see RuleExtension
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_RULE_EXT_VAL_T")
@Sequence(name="KREW_RTE_TMPL_S", property="ruleExtensionValueId")
public class RuleExtensionValue implements WorkflowPersistable {

	private static final long serialVersionUID = 8909789087052290261L;
	@Id
	@Column(name="RULE_EXT_VAL_ID")
	private Long ruleExtensionValueId;
    @Column(name="RULE_EXT_ID", insertable=false, updatable=false)
	private Long ruleExtensionId;
    @Column(name="VAL")
	private String value;
    @Column(name="KEY_CD")
	private String key;
    @Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;
    
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="RULE_EXT_ID")
	private RuleExtension extension;
    
    public RuleExtensionValue() {
    }
    
    public RuleExtensionValue(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    @PrePersist
    public void beforeInsert(){
        OrmUtils.populateAutoIncValue(this, KEWServiceLocator.getEntityManagerFactory().createEntityManager());
    }
    
    public RuleExtension getExtension() {
        return extension;
    }
    public void setExtension(RuleExtension extension) {
        this.extension = extension;
    }
    public Integer getLockVerNbr() {
        return lockVerNbr;
    }
    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public Long getRuleExtensionId() {
        return ruleExtensionId;
    }
    public void setRuleExtensionId(Long ruleExtensionId) {
        this.ruleExtensionId = ruleExtensionId;
    }
    public Long getRuleExtensionValueId() {
        return ruleExtensionValueId;
    }
    public void setRuleExtensionValueId(Long ruleExtensionValueId) {
        this.ruleExtensionValueId = ruleExtensionValueId;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public Object copy(boolean preserveKeys) {
        RuleExtensionValue ruleExtensionValueClone = new RuleExtensionValue();
        
        if(key != null){
          ruleExtensionValueClone.setKey(new String(key));
        }
        if(preserveKeys && ruleExtensionValueId != null){
          ruleExtensionValueClone.setRuleExtensionValueId(new Long(ruleExtensionValueId.longValue()));
        }
        if(value != null){
          ruleExtensionValueClone.setValue(new String(value));
        }
                
        return ruleExtensionValueClone;
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof RuleExtensionValue)) return false;
        RuleExtensionValue pred = (RuleExtensionValue) o;
        return Utilities.equals(key, pred.key) && Utilities.equals(value, pred.value);
    }

    public String toString() {
        return "[RuleExtensionValue:"
               +  " ruleExtensionValueId=" + ruleExtensionValueId
               + ", ruleExtensionId=" + ruleExtensionId
               + ", value=" + value
               + ", key=" + key
               + ", lockVerNbr=" + lockVerNbr
               + "]";
            
    }
}
