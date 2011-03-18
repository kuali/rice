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

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.core.util.CollectionUtils;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * An extension of a {@link RuleBaseValues}.  Provides attribute-specific data
 * extensions to the rule for a particular {@link RuleAttribute}.  Contains
 * a List of {@link RuleExtensionValue}s.
 * 
 * @see RuleBaseValues
 * @see RuleAttribute
 * @see RuleExtensionValue
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_RULE_EXT_T")
//@Sequence(name="KREW_RTE_TMPL_S", property="ruleExtensionId")
public class RuleExtension implements Serializable {

	private static final long serialVersionUID = 8178135296413950516L;

	@Id
	@GeneratedValue(generator="KREW_RTE_TMPL_S")
	@GenericGenerator(name="KREW_RTE_TMPL_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREW_RTE_TMPL_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="RULE_EXT_ID")
	private Long ruleExtensionId;

	@Column(name="RULE_TMPL_ATTR_ID", insertable=false, updatable=false)
	private Long ruleTemplateAttributeId;

	@Column(name="RULE_ID", insertable=false, updatable=false)
	private Long ruleBaseValuesId;

	@Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="RULE_ID")
	private RuleBaseValues ruleBaseValues;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="RULE_TMPL_ATTR_ID")
	private RuleTemplateAttribute ruleTemplateAttribute;

	@OneToMany(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE}, mappedBy="extension")
	@Fetch(value = FetchMode.SELECT)
	private List<RuleExtensionValue> extensionValues;

	public RuleExtension() {
		extensionValues = new ArrayList<RuleExtensionValue>();
	}

	//@PrePersist
    public void beforeInsert(){
        OrmUtils.populateAutoIncValue(this, KEWServiceLocator.getEntityManagerFactory().createEntityManager());
    }
	
	public List<RuleExtensionValue> getExtensionValues() {
		return extensionValues;
	}

	public void setExtensionValues(List<RuleExtensionValue> extensionValues) {
		this.extensionValues = extensionValues;
	}

	public RuleTemplateAttribute getRuleTemplateAttribute() {
		return ruleTemplateAttribute;
	}

	public void setRuleTemplateAttribute(RuleTemplateAttribute ruleTemplateAttribute) {
		this.ruleTemplateAttribute = ruleTemplateAttribute;
	}

	public RuleExtensionValue getRuleExtensionValue(int index) {
		while (getExtensionValues().size() <= index) {
			getExtensionValues().add(new RuleExtensionValue());
		}
		return (RuleExtensionValue) getExtensionValues().get(index);
	}

	public RuleBaseValues getRuleBaseValues() {
		return ruleBaseValues;
	}

	public void setRuleBaseValues(RuleBaseValues ruleBaseValues) {
		this.ruleBaseValues = ruleBaseValues;
	}

	public Integer getLockVerNbr() {
		return lockVerNbr;
	}

	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}

	public Long getRuleBaseValuesId() {
		return ruleBaseValuesId;
	}

	public void setRuleBaseValuesId(Long ruleBaseValuesId) {
		this.ruleBaseValuesId = ruleBaseValuesId;
	}

	public Long getRuleExtensionId() {
		return ruleExtensionId;
	}

	public void setRuleExtensionId(Long ruleExtensionId) {
		this.ruleExtensionId = ruleExtensionId;
	}

	public Long getRuleTemplateAttributeId() {
		return ruleTemplateAttributeId;
	}

	public void setRuleTemplateAttributeId(Long ruleTemplateAttributeId) {
		this.ruleTemplateAttributeId = ruleTemplateAttributeId;
	}

    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof RuleExtension)) return false;
        RuleExtension pred = (RuleExtension) o;
        return ObjectUtils.equals(ruleBaseValues.getRuleTemplate(), pred.getRuleBaseValues().getRuleTemplate()) &&
               ObjectUtils.equals(ruleTemplateAttribute, pred.getRuleTemplateAttribute()) &&
               CollectionUtils.collectionsEquivalent(extensionValues, pred.getExtensionValues());
    }

    public String toString() {
        return "[RuleExtension:"
               +  " ruleExtensionId=" + ruleExtensionId
               + ", ruleTemplateAttributeId=" + ruleTemplateAttributeId
               + ", ruleBaseValuesId=" + ruleBaseValuesId
               + ", ruleBaseValues=" + ruleBaseValues
               + ", ruleTemplateAttribute=" + ruleTemplateAttribute
               + ", extensionValues=" + extensionValues
               + ", lockVerNbr=" + lockVerNbr
               + "]";
    }

}
