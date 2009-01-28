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
package org.kuali.rice.kew.rule.bo;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.kuali.rice.core.jpa.annotations.Sequence;
import org.kuali.rice.kew.bo.KewPersistableBusinessObjectBase;
import org.kuali.rice.kew.bo.WorkflowPersistable;
import org.kuali.rice.kew.rule.RuleTemplateOption;
import org.kuali.rice.kew.util.KEWConstants;


/**
 * A model bean which represents a template upon which a rule is created.
 * The RuleTemplate is essentially a collection of {@link RuleAttribute}s
 * (associated vai the {@link RuleTemplateAttribute} bean).
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KREW_RULE_TMPL_T")
@Sequence(name="KREW_RTE_TMPL_S", property="ruleTemplateId")
@NamedQueries({@NamedQuery(name="findAllOrderedByName", query="SELECT rt FROM RuleTemplate rt ORDER BY rt.name ASC")})
public class RuleTemplate  extends KewPersistableBusinessObjectBase implements WorkflowPersistable  {

    private static final long serialVersionUID = -3387940485523951302L;

    /**
     * A list of default rule template option keys.
     */
    public static final String[] DEFAULT_OPTION_KEYS = {
        KEWConstants.RULE_INSTRUCTIONS_CD,
        KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ,
        KEWConstants.ACTION_REQUEST_APPROVE_REQ,
        KEWConstants.ACTION_REQUEST_COMPLETE_REQ,
        KEWConstants.ACTION_REQUEST_FYI_REQ,
        KEWConstants.ACTION_REQUEST_DEFAULT_CD
    };
    
    @Id
	@Column(name="RULE_TMPL_ID")
	private Long ruleTemplateId;
    @Column(name="NM")
	private String name;
    @Column(name="RULE_TMPL_DESC")
	private String description;

    @Column(name="DLGN_RULE_TMPL_ID", insertable=false, updatable=false)
	private Long delegationTemplateId;
    @OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="DLGN_RULE_TMPL_ID")
	private RuleTemplate delegationTemplate;
    @Fetch(value = FetchMode.SUBSELECT)
    @OneToMany(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
           mappedBy="ruleTemplate")
	private List<RuleTemplateAttribute> ruleTemplateAttributes;
    @OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
           mappedBy="ruleTemplate")
	private List<RuleTemplateOption> ruleTemplateOptions;

    // required to be lookupable
    @Transient
    private String returnUrl;

    public RuleTemplate() {
        ruleTemplateAttributes = new ArrayList<RuleTemplateAttribute>();
        ruleTemplateOptions = new ArrayList<RuleTemplateOption>();
    }
    
 
    /**
     * Removes any non-default rule template options on the template
     */
    public void removeNonDefaultOptions() {
        Iterator<RuleTemplateOption> it = ruleTemplateOptions.iterator();
        while (it.hasNext()) {
            RuleTemplateOption option = it.next();
            // if it's not one of the default options, remove it
            if (!ArrayUtils.contains(DEFAULT_OPTION_KEYS, option.getKey())) {
                it.remove();
            }
        }
    }

    public String getDelegateTemplateName() {
        if (delegationTemplate != null) {
            return delegationTemplate.getName();
        }        
        return "";
    }

    public String getRuleTemplateActionsUrl() {
        return "<a href=\"RuleTemplate.do?methodToCall=report&currentRuleTemplateId=" + ruleTemplateId + "\" >report</a>" /*+ "&nbsp;&nbsp;|&nbsp;&nbsp;<a href=\"RuleTemplate.do?methodToCall=edit&ruleTemplate.ruleTemplateId=" + ruleTemplateId + "\" >edit</a>"*/;
//        		"&nbsp;&nbsp;|&nbsp;&nbsp;<a onclick=\"if (confirm('Delete this record?')){ return true; } else {return false;} \" href=\"RuleTemplate.do?methodToCall=delete&ruleTemplate.ruleTemplateId=" + ruleTemplateId + "&redirectUrl=Lookup.do?methodToCall=search&lookupableImplServiceName=RuleTemplateLookupableImplService\" >delete</a>";
    }

//    public void addRuleTemplateAttribute(RuleTemplateAttribute ruleTemplateAttribute, Integer counter) {
//        boolean alreadyAdded = false;
//        int location = 0;
//        if (counter != null) {
//            for (Iterator templateAttributeIter = getRuleTemplateAttributes().iterator(); templateAttributeIter.hasNext();) {
//                RuleTemplateAttribute ruleTemplateAtt = (RuleTemplateAttribute) templateAttributeIter.next();
//                //                if (ruleTemplateAtt.getRuleAttributeId().longValue() == ruleTemplateAttribute.getRuleAttributeId().longValue()) {
//                if (counter.intValue() == location) {
//                    ruleTemplateAtt.setDefaultValue(ruleTemplateAttribute.getDefaultValue());
//                    ruleTemplateAtt.setDisplayOrder(ruleTemplateAttribute.getDisplayOrder());
//                    ruleTemplateAtt.setLockVerNbr(ruleTemplateAttribute.getLockVerNbr());
//                    ruleTemplateAtt.setRequired(ruleTemplateAttribute.getRequired());
//                    ruleTemplateAtt.setRuleTemplateAttributeId(ruleTemplateAttribute.getRuleTemplateAttributeId());
//                    ruleTemplateAtt.setRuleTemplateId(ruleTemplateAttribute.getRuleTemplateId());
//                    alreadyAdded = true;
//                }
//                location++;
//            }
//        }
//        if (!alreadyAdded) {
//            ruleTemplateAttribute.setDisplayOrder(new Integer(getRuleTemplateAttributes().size() + 1));
//            getRuleTemplateAttributes().add(ruleTemplateAttribute);
//        }
//    }
    
    /**
     * Returns the rule template attribute on this instance whose name matches the name of the rule template attribute
     * passed as a parameter, qualified by it's active state, or null if a match was not found.
     */
    private RuleTemplateAttribute getRuleTemplateAttribute(RuleTemplateAttribute ruleTemplateAttribute, Boolean active) {
        for (RuleTemplateAttribute currentRuleTemplateAttribute: getRuleTemplateAttributes()) {
            if (currentRuleTemplateAttribute.getRuleAttribute().getName().equals(ruleTemplateAttribute.getRuleAttribute().getName())) {
                if (active == null) {
                    return currentRuleTemplateAttribute;
                }
                else if (active.compareTo(currentRuleTemplateAttribute.getActive()) == 0) {
                    return currentRuleTemplateAttribute;
                }
            }
        }
        return null;
    }
    
    public RuleTemplateAttribute getRuleTemplateAttribute(RuleTemplateAttribute ruleTemplateAttribute) {
        return getRuleTemplateAttribute(ruleTemplateAttribute, null);
    }
    
    public boolean containsActiveRuleTemplateAttribute(RuleTemplateAttribute templateAttribute) {
        return (getRuleTemplateAttribute(templateAttribute, Boolean.TRUE) != null);
    }

    public boolean containsRuleTemplateAttribute(RuleTemplateAttribute templateAttribute) {
        return (getRuleTemplateAttribute(templateAttribute, null) != null);
    }

    public RuleTemplateAttribute getRuleTemplateAttribute(int index) {
        while (getRuleTemplateAttributes().size() <= index) {
            getRuleTemplateAttributes().add(new RuleTemplateAttribute());
        }
        return (RuleTemplateAttribute) getRuleTemplateAttributes().get(index);
    }

    public List<RuleTemplateAttribute> getRuleTemplateAttributes() {
    	Collections.sort(ruleTemplateAttributes);
        return ruleTemplateAttributes;
    }

    /**
     * Returns a List of only the active RuleTemplateAttributes on the RuleTemplate
     * sorted according to display order (ascending).
     */
    public List<RuleTemplateAttribute> getActiveRuleTemplateAttributes() {
        List<RuleTemplateAttribute> activeAttributes = new ArrayList<RuleTemplateAttribute>();
        for (Iterator<RuleTemplateAttribute> iterator = getRuleTemplateAttributes().iterator(); iterator.hasNext();) {
            RuleTemplateAttribute templateAttribute = (RuleTemplateAttribute) iterator.next();
            if (templateAttribute.isActive()) {
                activeAttributes.add(templateAttribute);
            }
        }
        Collections.sort(activeAttributes);
        return activeAttributes;
    }
    
    /**
     * This is implemented to allow us to use this collection on the inquiry for RuleTemplate.  In the
     * KNS code it does an explicit check that the property is writable.
     */
    public void setActiveRuleTemplateAttributes(List<RuleTemplateAttribute> ruleTemplateAttributes) {
    	throw new UnsupportedOperationException("setActiveRuleTemplateAttributes is not implemented");
    }

    public void setRuleTemplateAttributes(List<RuleTemplateAttribute> ruleTemplateAttributes) {
        this.ruleTemplateAttributes = ruleTemplateAttributes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public Long getDelegationTemplateId() {
        return delegationTemplateId;
    }

    public void setDelegationTemplateId(Long delegationTemplateId) {
        this.delegationTemplateId = delegationTemplateId;
    }

    public RuleTemplate getDelegationTemplate() {
        return delegationTemplate;
    }

    public void setDelegationTemplate(RuleTemplate delegationTemplate) {
        this.delegationTemplate = delegationTemplate;
    }

    /**
     * Returns a copy of this object and its dependents
     * @see org.kuali.rice.kew.bo.WorkflowPersistable#copy(boolean)
     */
    public Object copy(boolean preserveKeys) {
        RuleTemplate ruleTemplateClone = new RuleTemplate();

        if (description != null) {
            ruleTemplateClone.setDescription(new String(description));
        }
        if (name != null) {
            ruleTemplateClone.setName(new String(name));
        }
        if (preserveKeys && ruleTemplateId != null) {
            ruleTemplateClone.setRuleTemplateId(new Long(ruleTemplateId.longValue()));
        }
        if ((getRuleTemplateAttributes() != null) && !getRuleTemplateAttributes().isEmpty()) {
            List<RuleTemplateAttribute> ruleTemplateAttributeList = new ArrayList<RuleTemplateAttribute>();

            for (RuleTemplateAttribute ruleTemplateAttribute: getRuleTemplateAttributes()) {
                RuleTemplateAttribute ruleTemplateAttributeCopy = (RuleTemplateAttribute) ruleTemplateAttribute.copy(preserveKeys);
                ruleTemplateAttributeCopy.setRuleTemplate(ruleTemplateClone);
                ruleTemplateAttributeList.add(ruleTemplateAttributeCopy);
            }
            ruleTemplateClone.setRuleTemplateAttributes(ruleTemplateAttributeList);
        }
        if ((ruleTemplateOptions != null) && !ruleTemplateOptions.isEmpty()) {
            List<RuleTemplateOption> ruleTemplateOptionList = new ArrayList<RuleTemplateOption>();

            for (RuleTemplateOption ruleTemplateOption: ruleTemplateOptions) {
                RuleTemplateOption ruleTemplateOptionCopy = (RuleTemplateOption) ruleTemplateOption.copy(preserveKeys);
                ruleTemplateOptionCopy.setRuleTemplate(ruleTemplateClone);
                ruleTemplateOptionList.add(ruleTemplateOptionCopy);
            }
            ruleTemplateClone.setRuleTemplateOptions(ruleTemplateOptionList);
        }

        return ruleTemplateClone;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    /**
     * Used from the rule quicklinks when doing the focus channel.
     */
    public String getEncodedName() {
        return URLEncoder.encode(getName());
    }

    public List<RuleTemplateOption> getRuleTemplateOptions() {
        return ruleTemplateOptions;
    }

    public void setRuleTemplateOptions(List<RuleTemplateOption> ruleTemplateOptions) {
        this.ruleTemplateOptions = ruleTemplateOptions;
    }

    public RuleTemplateOption getRuleTemplateOption(String key) {
        for (RuleTemplateOption option: ruleTemplateOptions) {
            if (option.getKey().equals(key)) {
                return option;
            }
        }
        return null;
    }

    public void setInstructions(RuleTemplateOption instructions) {
        RuleTemplateOption option = getRuleTemplateOption(KEWConstants.RULE_INSTRUCTIONS_CD);
        option.setValue(instructions.getValue());
        option.setRuleTemplateOptionId(instructions.getRuleTemplateOptionId());
        option.setLockVerNbr(instructions.getLockVerNbr());
    }

    public void setAcknowledge(RuleTemplateOption acknowledge) {
        RuleTemplateOption option = getRuleTemplateOption(KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ);
        option.setValue(acknowledge.getValue());
        option.setRuleTemplateOptionId(acknowledge.getRuleTemplateOptionId());
        option.setLockVerNbr(acknowledge.getLockVerNbr());
    }

    public void setComplete(RuleTemplateOption complete) {
        RuleTemplateOption option = getRuleTemplateOption(KEWConstants.ACTION_REQUEST_COMPLETE_REQ);
        option.setValue(complete.getValue());
        option.setRuleTemplateOptionId(complete.getRuleTemplateOptionId());
        option.setLockVerNbr(complete.getLockVerNbr());
    }

    public void setApprove(RuleTemplateOption approve) {
        RuleTemplateOption option = getRuleTemplateOption(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
        option.setValue(approve.getValue());
        option.setRuleTemplateOptionId(approve.getRuleTemplateOptionId());
        option.setLockVerNbr(approve.getLockVerNbr());
    }

    public void setFyi(RuleTemplateOption fyi) {
        RuleTemplateOption option = getRuleTemplateOption(KEWConstants.ACTION_REQUEST_FYI_REQ);
        option.setValue(fyi.getValue());
        option.setRuleTemplateOptionId(fyi.getRuleTemplateOptionId());
        option.setLockVerNbr(fyi.getLockVerNbr());
    }

    public void setDefaultActionRequestValue(RuleTemplateOption defaultActionRequestValue) {
        RuleTemplateOption option = getRuleTemplateOption(KEWConstants.ACTION_REQUEST_DEFAULT_CD);
        option.setValue(defaultActionRequestValue.getValue());
        option.setRuleTemplateOptionId(defaultActionRequestValue.getRuleTemplateOptionId());
        option.setLockVerNbr(defaultActionRequestValue.getLockVerNbr());
    }

    public RuleTemplateOption getInstructions() {
        return getRuleTemplateOption(KEWConstants.RULE_INSTRUCTIONS_CD);
    }

    public RuleTemplateOption getAcknowledge() {
        return getRuleTemplateOption(KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ);
    }

    public RuleTemplateOption getComplete() {
        return getRuleTemplateOption(KEWConstants.ACTION_REQUEST_COMPLETE_REQ);
    }

    public RuleTemplateOption getApprove() {
        return getRuleTemplateOption(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
    }

    public RuleTemplateOption getFyi() {
        return getRuleTemplateOption(KEWConstants.ACTION_REQUEST_FYI_REQ);
    }

    public RuleTemplateOption getDefaultActionRequestValue() {
        return getRuleTemplateOption(KEWConstants.ACTION_REQUEST_DEFAULT_CD);
    }
    
    public String toString() {
        return new ToStringBuilder(this).append("ruleTemplateId", ruleTemplateId)
                                        .append("name", name)
                                        .append("description", description)
                                        .append("delegationTemplateId", delegationTemplateId)
                                        .append("totalRuleTemplateAttributes", getRuleTemplateAttributes() == null ? "null" : "size: " + getRuleTemplateAttributes().size())
                                        .append("activeRuleTemplateAttributes", getActiveRuleTemplateAttributes() == null ? "null" : "size: " + getActiveRuleTemplateAttributes().size())
                                        .append("returnUrl", returnUrl)
                                        .append("versionNumber", versionNumber)
                                        .append("ruleTemplateOptions", ruleTemplateOptions).toString();
                                 
    }

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
	protected LinkedHashMap<String, Object> toStringMapper() {
		LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
	    propMap.put("ruleTemplateId", getRuleTemplateId());
	    propMap.put("name", getName());
	    propMap.put("description", getDescription());
	    propMap.put("delegationTemplateId", getDelegationTemplateId());
	    propMap.put("totalRuleTemplateAttributes", getRuleTemplateAttributes() == null ? "null" : "size: " + getRuleTemplateAttributes().size());
	    propMap.put("activeRuleTemplateAttributes", getActiveRuleTemplateAttributes() == null ? "null" : "size: " + getActiveRuleTemplateAttributes().size());
	    propMap.put("returnUrl", getReturnUrl());
	    propMap.put("versionNumber", getVersionNumber());
	    propMap.put("ruleTemplateOptions", getRuleTemplateOptions());
	    	    
	    return propMap;
		
	}
}
