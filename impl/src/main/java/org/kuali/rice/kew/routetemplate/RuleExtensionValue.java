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
package org.kuali.rice.kew.routetemplate;

import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;

import org.kuali.rice.kew.WorkflowPersistable;

import edu.iu.uis.eden.util.Utilities;

/**
 * The value of an extension to a rule.  Essentially contains a
 * key-value pair containing the key of the extension data and
 * it's value.
 * 
 * @see RuleBaseValues
 * @see RuleExtension
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="EN_RULE_EXT_VAL_T")
public class RuleExtensionValue implements WorkflowPersistable {

	private static final long serialVersionUID = 8909789087052290261L;
	@Id
	@Column(name="RULE_EXT_VAL_ID")
	private Long ruleExtensionValueId;
    @Column(name="RULE_EXT_ID")
	private Long ruleExtensionId;
    @Column(name="RULE_EXT_VAL")
	private String value;
    @Column(name="RULE_EXT_VAL_KEY")
	private String key;
    @Version
	@Column(name="DB_LOCK_VER_NBR")
	private Integer lockVerNbr;
    
    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="RULE_EXT_ID", insertable=false, updatable=false)
	private RuleExtension extension;
    
    public RuleExtensionValue() {
    }
    
    public RuleExtensionValue(String key, String value) {
        this.key = key;
        this.value = value;
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
