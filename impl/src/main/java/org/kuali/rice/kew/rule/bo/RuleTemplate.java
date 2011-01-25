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
package org.kuali.rice.kew.rule.bo;

 import org.apache.commons.lang.ArrayUtils;
 import org.hibernate.annotations.Fetch;
 import org.hibernate.annotations.FetchMode;
 import org.hibernate.annotations.GenericGenerator;
 import org.hibernate.annotations.Parameter;
 import org.kuali.rice.kew.rule.Role;
 import org.kuali.rice.kew.rule.RoleAttribute;
 import org.kuali.rice.kew.rule.RuleTemplateOption;
 import org.kuali.rice.kew.rule.WorkflowAttribute;
 import org.kuali.rice.kew.util.KEWConstants;
 import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

 import javax.persistence.*;
 import java.net.URLEncoder;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;


/**
 * A model bean which represents a template upon which a rule is created.
 * The RuleTemplate is essentially a collection of {@link RuleAttribute}s
 * (associated vai the {@link RuleTemplateAttribute} bean).
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_RULE_TMPL_T")
//@Sequence(name="KREW_RTE_TMPL_S", property="ruleTemplateId")
@NamedQueries({@NamedQuery(name="findAllOrderedByName", query="SELECT rt FROM RuleTemplate rt ORDER BY rt.name ASC")})
public class RuleTemplate  extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = -3387940485523951302L;

    /**
     * A list of default rule template option keys.
     */
    public static final String[] DEFAULT_OPTION_KEYS = {
        //KEWConstants.RULE_INSTRUCTIONS_CD,
        KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ,
        KEWConstants.ACTION_REQUEST_APPROVE_REQ,
        KEWConstants.ACTION_REQUEST_COMPLETE_REQ,
        KEWConstants.ACTION_REQUEST_FYI_REQ,
        KEWConstants.ACTION_REQUEST_DEFAULT_CD
    };
    
    @Id
    @GeneratedValue(generator="KREW_RTE_TMPL_S")
	@GenericGenerator(name="KREW_RTE_TMPL_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREW_RTE_TMPL_S"),
			@Parameter(name="value_column",value="id")
	})
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
    @Fetch(value = FetchMode.SELECT)
    @OneToMany(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
           mappedBy="ruleTemplate")
	private List<RuleTemplateAttribute> ruleTemplateAttributes;
    @Fetch(value = FetchMode.SELECT)
    @OneToMany(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
           mappedBy="ruleTemplate", orphanRemoval=true)
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
     * @return
     */
    public List<RuleTemplateAttribute> getActiveRuleTemplateAttributes() {
        List<RuleTemplateAttribute> activeAttributes = new ArrayList<RuleTemplateAttribute>();
        for (RuleTemplateAttribute templateAttribute : getRuleTemplateAttributes())
        {
            if (templateAttribute.isActive())
            {
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
/*
    public void setInstructions(RuleTemplateOption instructions) {
        RuleTemplateOption option = getRuleTemplateOption(KEWConstants.RULE_INSTRUCTIONS_CD);
        option.setValue(instructions.getValue());
        option.setRuleTemplateOptionId(instructions.getRuleTemplateOptionId());
        option.setLockVerNbr(instructions.getLockVerNbr());
    }
*/
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
/*
    public RuleTemplateOption getInstructions() {
        return getRuleTemplateOption(KEWConstants.RULE_INSTRUCTIONS_CD);
    }
*/
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
    
    /**
     * Returns a List of Roles from all RoleAttributes attached to this template.
     * @return list of roles
     */
    public List<Role> getRoles() {
    	List<Role> roles = new ArrayList<Role>();
    	List<RuleTemplateAttribute> ruleTemplateAttributes = getActiveRuleTemplateAttributes();
		Collections.sort(ruleTemplateAttributes);
        for (RuleTemplateAttribute ruleTemplateAttribute : ruleTemplateAttributes)
        {
            if (!ruleTemplateAttribute.isWorkflowAttribute())
            {
                continue;
            }
            WorkflowAttribute workflowAttribute = ruleTemplateAttribute.getWorkflowAttribute();
            if (workflowAttribute instanceof RoleAttribute)
            {
                RoleAttribute roleAttribute = (RoleAttribute) workflowAttribute;
                roles.addAll(roleAttribute.getRoleNames());
            }
        }
		return roles;
    }
}
