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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.iu.uis.eden.WorkflowPersistable;
import edu.iu.uis.eden.util.Utilities;

/**
 * An extension of a {@link RuleBaseValues}.  Provides attribute-specific data
 * extensions to the rule for a particular {@link RuleAttribute}.  Contains
 * a List of {@link RuleExtensionValue}s.
 * 
 * @see RuleBaseValues
 * @see RuleAttribute
 * @see RuleExtensionValue
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleExtension implements WorkflowPersistable {

	private static final long serialVersionUID = 8178135296413950516L;

	private Long ruleExtensionId;

	private Long ruleTemplateAttributeId;

	private Long ruleBaseValuesId;

	private Integer lockVerNbr;

	private RuleBaseValues ruleBaseValues;

	private RuleTemplateAttribute ruleTemplateAttribute;

	private List<RuleExtensionValue> extensionValues;

	public RuleExtension() {
		extensionValues = new ArrayList<RuleExtensionValue>();
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

	public Object copy(boolean preserveKeys) {
		RuleExtension ruleExtensionClone = new RuleExtension();
		if (preserveKeys && (ruleExtensionId != null)) {
			ruleExtensionClone.setRuleExtensionId(new Long(ruleExtensionId.longValue()));
		}
		if ((extensionValues != null) && !extensionValues.isEmpty()) {
			List extensionValueList = new ArrayList();

			for (Iterator i = extensionValues.iterator(); i.hasNext();) {
				RuleExtensionValue ruleExtensionValue = (RuleExtensionValue) i.next();
				RuleExtensionValue ruleExtensionValueCopy = (RuleExtensionValue) ruleExtensionValue.copy(preserveKeys);
				ruleExtensionValueCopy.setExtension(ruleExtensionClone);
				extensionValueList.add(ruleExtensionValueCopy);
			}
			ruleExtensionClone.setExtensionValues(extensionValueList);
		}

		// if(ruleTemplateAttribute != null){
		// ruleExtensionClone.setRuleTemplateAttribute((RuleTemplateAttribute)ruleTemplateAttribute.copy(preserveKeys));
		// }
		ruleExtensionClone.setRuleTemplateAttribute(getRuleTemplateAttribute());
		ruleExtensionClone.setRuleTemplateAttributeId(getRuleTemplateAttributeId());
		return ruleExtensionClone;
	}

    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof RuleExtension)) return false;
        RuleExtension pred = (RuleExtension) o;
        return Utilities.equals(ruleBaseValues.getRuleTemplate(), pred.getRuleBaseValues().getRuleTemplate()) &&
               Utilities.equals(ruleTemplateAttribute, pred.getRuleTemplateAttribute()) &&
               Utilities.collectionsEquivalent(extensionValues, pred.getExtensionValues());
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