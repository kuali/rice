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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.WorkflowPersistable;
import edu.iu.uis.eden.exception.ResourceUnavailableException;

/**
 * A model bean which represents a template upon which a rule is created.
 * The RuleTemplate is essentially a collection of {@link RuleAttribute}s
 * (associated vai the {@link RuleTemplateAttribute} bean).
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleTemplate implements WorkflowPersistable {

    private static final long serialVersionUID = -3387940485523951302L;

    /**
     * A list of default rule template option keys.
     */
    public static final String[] DEFAULT_OPTION_KEYS = {
        EdenConstants.RULE_INSTRUCTIONS_CD,
        EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ,
        EdenConstants.ACTION_REQUEST_APPROVE_REQ,
        EdenConstants.ACTION_REQUEST_COMPLETE_REQ,
        EdenConstants.ACTION_REQUEST_FYI_REQ,
        EdenConstants.ACTION_REQUEST_DEFAULT_CD
    };
    
    private Long ruleTemplateId;
    private String name;
    private String description;
    private Integer lockVerNbr;
    private Long delegationTemplateId;
    private RuleTemplate delegationTemplate;

    private List<RuleTemplateAttribute> ruleTemplateAttributes;
    private List<RuleTemplateOption> ruleTemplateOptions;

    // required to be lookupable
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
        // TODO delyea - does this need to check active only attributes?
        while (getRuleTemplateAttributes().size() <= index) {
            getRuleTemplateAttributes().add(new RuleTemplateAttribute());
        }
        return (RuleTemplateAttribute) getRuleTemplateAttributes().get(index);
    }

//    public RuleTemplateOption getRuleTemplateOption(int index) {
//        while (getRuleTemplateOptions().size() <= index) {
//            getRuleTemplateOptions().add(new RuleTemplateOption());
//        }
//        return (RuleTemplateOption) getRuleTemplateOptions().get(index);
//    }

    public List<RuleTemplateAttribute> getRuleTemplateAttributes() {
        return ruleTemplateAttributes;
    }

    public List<RuleTemplateAttribute> getActiveRuleTemplateAttributes() {
        // TODO delyea - fix this once active is persisting
        return getRuleTemplateAttributes();
//        List activeAttributes = new ArrayList();
//        for (Iterator iterator = getRuleTemplateAttributes().iterator(); iterator
//                .hasNext();) {
//            RuleTemplateAttribute templateAttribute = (RuleTemplateAttribute) iterator.next();
//            if (templateAttribute.isActive()) {
//                activeAttributes.add(templateAttribute);
//            }
//        }
//        return activeAttributes;
    }

    /**
     * Returns a List of all WorkflowAttribute objects on this template.
     * 
     * @throws ResourceUnavailableException if one of the WorkflowAttributes cannot be instantiated
     */
//    public List getWorkflowAttributes() throws ResourceUnavailableException {
//    	List workflowAttributes = new ArrayList();
//    	for (Iterator iter = getRuleTemplateAttributes().iterator(); iter.hasNext();) {
//            RuleTemplateAttribute templateAttribute = (RuleTemplateAttribute) iter.next();
//            Object attribute = templateAttribute.getAttribute();
//            if (attribute instanceof WorkflowAttribute) {
//            	workflowAttributes.add(attribute);
//            }
//        }
//    	return workflowAttributes;
//    }
    
//    public RoleAttribute getRoleAttributeByName(String className) throws ResourceUnavailableException {
//        for (Iterator iter = getWorkflowAttributes().iterator(); iter.hasNext();) {
//            WorkflowAttribute attribute = (WorkflowAttribute)iter.next();
//            if (className.equals(attribute.getClass().getName())) {
//                return (RoleAttribute) attribute;
//            }
//        }
//        throw new RuntimeException("Didn't locate RoleAttribute " + className);
//    }

    public void setRuleTemplateAttributes(List<RuleTemplateAttribute> ruleTemplateAttributes) {
        this.ruleTemplateAttributes = ruleTemplateAttributes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
     * @see edu.iu.uis.eden.WorkflowPersistable#copy(boolean)
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
     * Used from the rule quicklinks when doing the onestart focus channel.
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
        RuleTemplateOption option = getRuleTemplateOption(EdenConstants.RULE_INSTRUCTIONS_CD);
        option.setValue(instructions.getValue());
        option.setRuleTemplateOptionId(instructions.getRuleTemplateOptionId());
        option.setLockVerNbr(instructions.getLockVerNbr());
    }

    public void setAcknowledge(RuleTemplateOption acknowledge) {
        RuleTemplateOption option = getRuleTemplateOption(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ);
        option.setValue(acknowledge.getValue());
        option.setRuleTemplateOptionId(acknowledge.getRuleTemplateOptionId());
        option.setLockVerNbr(acknowledge.getLockVerNbr());
    }

    public void setComplete(RuleTemplateOption complete) {
        RuleTemplateOption option = getRuleTemplateOption(EdenConstants.ACTION_REQUEST_COMPLETE_REQ);
        option.setValue(complete.getValue());
        option.setRuleTemplateOptionId(complete.getRuleTemplateOptionId());
        option.setLockVerNbr(complete.getLockVerNbr());
    }

    public void setApprove(RuleTemplateOption approve) {
        RuleTemplateOption option = getRuleTemplateOption(EdenConstants.ACTION_REQUEST_APPROVE_REQ);
        option.setValue(approve.getValue());
        option.setRuleTemplateOptionId(approve.getRuleTemplateOptionId());
        option.setLockVerNbr(approve.getLockVerNbr());
    }

    public void setFyi(RuleTemplateOption fyi) {
        RuleTemplateOption option = getRuleTemplateOption(EdenConstants.ACTION_REQUEST_FYI_REQ);
        option.setValue(fyi.getValue());
        option.setRuleTemplateOptionId(fyi.getRuleTemplateOptionId());
        option.setLockVerNbr(fyi.getLockVerNbr());
    }

    public void setDefaultActionRequestValue(RuleTemplateOption defaultActionRequestValue) {
        RuleTemplateOption option = getRuleTemplateOption(EdenConstants.ACTION_REQUEST_DEFAULT_CD);
        option.setValue(defaultActionRequestValue.getValue());
        option.setRuleTemplateOptionId(defaultActionRequestValue.getRuleTemplateOptionId());
        option.setLockVerNbr(defaultActionRequestValue.getLockVerNbr());
    }

    public RuleTemplateOption getInstructions() {
        return getRuleTemplateOption(EdenConstants.RULE_INSTRUCTIONS_CD);
    }

    public RuleTemplateOption getAcknowledge() {
        return getRuleTemplateOption(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ);
    }

    public RuleTemplateOption getComplete() {
        return getRuleTemplateOption(EdenConstants.ACTION_REQUEST_COMPLETE_REQ);
    }

    public RuleTemplateOption getApprove() {
        return getRuleTemplateOption(EdenConstants.ACTION_REQUEST_APPROVE_REQ);
    }

    public RuleTemplateOption getFyi() {
        return getRuleTemplateOption(EdenConstants.ACTION_REQUEST_FYI_REQ);
    }

    public RuleTemplateOption getDefaultActionRequestValue() {
        return getRuleTemplateOption(EdenConstants.ACTION_REQUEST_DEFAULT_CD);
    }
    
    public String toString() {
        return new ToStringBuilder(this).append("ruleTemplateId", ruleTemplateId)
                                        .append("name", name)
                                        .append("description", description)
                                        .append("delegationTemplateId", delegationTemplateId)
                                        .append("totalRuleTemplateAttributes", getRuleTemplateAttributes() == null ? "null" : "size: " + getRuleTemplateAttributes().size())
                                        .append("activeRuleTemplateAttributes", getActiveRuleTemplateAttributes() == null ? "null" : "size: " + getActiveRuleTemplateAttributes().size())
                                        .append("returnUrl", returnUrl)
                                        .append("lockVerNbr", lockVerNbr)
                                        .append("ruleTemplateOptions", ruleTemplateOptions).toString();
                                 
    }
}