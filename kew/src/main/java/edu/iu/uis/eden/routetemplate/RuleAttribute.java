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
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import edu.iu.uis.eden.WorkflowPersistable;

/**
 * Model bean defining a rule attribute.  Includes the classname of the attribute
 * class, as well as it's name and other information.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleAttribute implements WorkflowPersistable  {

	private static final long serialVersionUID = 1027673603158346349L;
	private Long ruleAttributeId;
    private String name;
    private String label;
    private String type;
    private String className;
    private String description;
    private String xmlConfigData;
    private Integer lockVerNbr;
    private String messageEntity;
    
    private List ruleTemplateAttributes;
    private List validValues;
    
    // required to be lookupable
    private String returnUrl;
    public RuleAttribute() {
        ruleTemplateAttributes = new ArrayList();
        validValues = new ArrayList();
    }
    
    public List getValidValues() {
        return validValues;
    }
    public void setValidValues(List ruleAttributeValidValues) {
        this.validValues = ruleAttributeValidValues;
    }
    public List getRuleTemplateAttributes() {
        return ruleTemplateAttributes;
    }
    public void setRuleTemplateAttributes(List ruleTemplateAttributes) {
        this.ruleTemplateAttributes = ruleTemplateAttributes;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public Integer getLockVerNbr() {
        return lockVerNbr;
    }
    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getRuleAttributeId() {
        return ruleAttributeId;
    }
    public void setRuleAttributeId(Long ruleAttributeId) {
        this.ruleAttributeId = ruleAttributeId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    
    public Object copy(boolean preserveKeys) {
        RuleAttribute ruleAttributeClone = new RuleAttribute();
        if(className != null){
          ruleAttributeClone.setClassName(new String (className));
        }
        if(description != null){
          ruleAttributeClone.setDescription(new String (description));
        }
        if(label != null){
          ruleAttributeClone.setLabel(new String(label));
        }
        if(name != null){
          ruleAttributeClone.setName(new String (name));
        }
        if(preserveKeys && ruleAttributeId != null){
          ruleAttributeClone.setRuleAttributeId(new Long(ruleAttributeId.longValue()));
        }
        if(type != null){
          ruleAttributeClone.setType(new String(type));
        }
        return ruleAttributeClone;
    }

    /**
     * @return Returns the className.
     */
    public String getClassName() {
      return className;
    }
    /**
     * @param className The className to set.
     */
    public void setClassName(String className) {
      this.className = className;
    }
    
    public String getRuleAttributeActionsUrl() {
        return "<a href=\"RuleAttributeReport.do?ruleAttributeId="+ruleAttributeId+"\" >report</a>";
    }
    
    public String getReturnUrl() {
        return returnUrl;
    }
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

	public String getXmlConfigData() {
		return xmlConfigData;
	}

	public void setXmlConfigData(String xmlConfigData) {
		this.xmlConfigData = xmlConfigData;
	}

	public String getMessageEntity() {
		return messageEntity;
	}

	public void setMessageEntity(String messageEntity) {
		this.messageEntity = messageEntity;
	}
	
	public String toString() {
	    return new ToStringBuilder(this).append("name", name)
	                                    .append("ruleAttributeId", ruleAttributeId)
	                                    .append("className", className)
	                                    .append("description", description)
	                                    .append("label", label)
	                                    .append("messageEntity", messageEntity)
	                                    .append("lockVerNbr").toString();
	}
    
}