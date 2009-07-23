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
package org.kuali.rice.kew.rule.bo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.core.jpa.annotations.Sequence;
import org.kuali.rice.kew.bo.KewPersistableBusinessObjectBase;
import org.kuali.rice.kew.bo.WorkflowPersistable;


/**
 * Model bean defining a rule attribute.  Includes the classname of the attribute
 * class, as well as it's name and other information.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KREW_RULE_ATTR_T")
@Sequence(name="KREW_RTE_TMPL_S", property="ruleAttributeId")
@NamedQueries({
  @NamedQuery(name="RuleAttribute.FindById",  query="select ra from RuleAttribute ra where ra.ruleAttributeId = :ruleAttributeId"),
  @NamedQuery(name="RuleAttribute.FindByName",  query="select ra from RuleAttribute ra where ra.name = :name"),
  @NamedQuery(name="RuleAttribute.FindByClassName",  query="select ra from RuleAttribute ra where ra.className = :className"),
  @NamedQuery(name="RuleAttribute.GetAllRuleAttributes",  query="select ra from RuleAttribute ra")
})
public class RuleAttribute extends KewPersistableBusinessObjectBase implements WorkflowPersistable  {

	private static final long serialVersionUID = 1027673603158346349L;
	@Id
	@Column(name="RULE_ATTR_ID")
	private Long ruleAttributeId;
    @Column(name="NM")
	private String name;
    @Column(name="LBL")
	private String label;
    @Column(name="RULE_ATTR_TYP_CD")
	private String type;
    @Column(name="CLS_NM")
	private String className;
    @Column(name="DESC_TXT")
	private String description;
    @Lob
	@Basic(fetch=FetchType.LAZY)
	@Column(name="XML")
	private String xmlConfigData;

    @Column(name="SVC_NMSPC")
	private String serviceNamespace;
    
    @OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
           targetEntity=org.kuali.rice.kew.rule.bo.RuleTemplateAttribute.class, mappedBy="ruleAttribute")
	private List ruleTemplateAttributes;
    @Transient
    private List validValues;
    
    // required to be lookupable
    @Transient
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

	public String getServiceNamespace() {
		return serviceNamespace;
	}

	public void setServiceNamespace(String ServiceNamespace) {
		this.serviceNamespace = ServiceNamespace;
	}
		
	@Override
	protected LinkedHashMap<String, Object> toStringMapper() {
		LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
	    propMap.put("ruleAttributeId", getRuleAttributeId());
	    propMap.put("name", getName());
	    propMap.put("label", getLabel());
	    propMap.put("description", getDescription());
	    propMap.put("type", getType());
	    propMap.put("className", getClassName());
	    propMap.put("serviceNamespace", getServiceNamespace());
	    return propMap;
	}

}
