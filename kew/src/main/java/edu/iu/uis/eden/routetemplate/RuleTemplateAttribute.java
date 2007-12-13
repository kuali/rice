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

import java.util.List;

import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowPersistable;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;

/**
 * A model bean which services as the link between a {@link RuleTemplate} and
 * a {@link RuleAttribute}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleTemplateAttribute implements WorkflowPersistable, Comparable {

    private static final long serialVersionUID = -3580049225424553828L;
    private Long ruleTemplateAttributeId;
    private Long ruleTemplateId;
    private Long ruleAttributeId;
    private Boolean required;
    private Boolean active;
    private Integer displayOrder;
    private String defaultValue;
    private Integer lockVerNbr;

    private RuleTemplate ruleTemplate;
    private RuleAttribute ruleAttribute;
    private List ruleExtensions;

    public RuleTemplateAttribute() {
	this.required = Boolean.FALSE;
	this.active = Boolean.TRUE;
    }

    public int compareTo(Object obj) {
	if (obj instanceof RuleTemplateAttribute) {
	    RuleTemplateAttribute comparedObject = (RuleTemplateAttribute) obj;

	    if ((this.getDisplayOrder() != null) && (comparedObject.getDisplayOrder() != null)) {
		return this.getDisplayOrder().compareTo(comparedObject.getDisplayOrder());
	    }
	}
	return 0;
    }

    public Object getAttribute() {
	try {
	    ObjectDefinition objectDefinition = new ObjectDefinition(getRuleAttribute().getClassName(), getRuleAttribute().getMessageEntity());
	    Object attribute = GlobalResourceLoader.getObject(objectDefinition);
	    if (attribute == null) {
		throw new WorkflowRuntimeException("Could not find attribute " + objectDefinition);
	    }
	    if (attribute instanceof WorkflowAttribute) {
		((WorkflowAttribute) attribute).setRequired(required.booleanValue());
	    }
	    return attribute;
	} catch (Exception e) {
	    throw new RuntimeException("Caught error attempting to load attribute class: " + getRuleAttribute().getClassName(), e);
	}
    }

    public boolean isWorkflowAttribute() {
	try {
	    Object attributeObject = getAttribute();//GlobalResourceLoader.getResourceLoader().getObject(new ObjectDefinition(getRuleAttribute().getClassName()));
	    if (attributeObject == null) {
		return false;
	    }
	    Class attributeClass = attributeObject.getClass();
	    return WorkflowAttribute.class.isAssignableFrom(attributeClass);
	} catch (Exception e) {
	    throw new RuntimeException("Caught error attempting to load WorkflowAttribute class: " + getRuleAttribute().getClassName(), e);
	}
    }

    public boolean isRuleValidationAttribute() {
	// just check the type here to avoid having to load the class from the class loader if it's not actually there
	return EdenConstants.RULE_VALIDATION_ATTRIBUTE_TYPE.equals(getRuleAttribute().getType());
    }

    /**
     * Instantiates and returns a new instance of the WorkflowAttribute class configured on this template.
     * The calling code should be sure to call isWorkflowAttribute first to verify the type of this attribute
     * is that of a WorkflowAttribute.  Otherwise a RuntimeException will be thrown.
     */
    public WorkflowAttribute getWorkflowAttribute() {
	try {
	    ObjectDefinition objectDefinition = new ObjectDefinition(getRuleAttribute().getClassName(), getRuleAttribute().getMessageEntity());
	    WorkflowAttribute workflowAttribute = (WorkflowAttribute) GlobalResourceLoader.getResourceLoader().getObject(objectDefinition);
	    if (workflowAttribute == null) {
		throw new WorkflowRuntimeException("Could not find workflow attribute " + objectDefinition);
	    }
	    workflowAttribute.setRequired(required.booleanValue());
	    return workflowAttribute;
	} catch (Exception e) {
	    throw new RuntimeException("Caught exception instantiating new " + getRuleAttribute().getClassName(), e);
	}
    }

    /**
     * Instantiates and returns a new instance of the RuleValidationAttribute class configured on this template.
     * The calling code should be sure to call isRuleValidationAttribute first to verify the type of this attribute
     * is that of a RuleValidationAttribute.  Otherwise a RuntimeException will be thrown.
     */
    public RuleValidationAttribute getRuleValidationAttribute() {
	try {
	    return (RuleValidationAttribute) getAttribute();
	} catch (Exception e) {
	    throw new RuntimeException("Caught exception instantiating new " + getRuleAttribute().getClassName(), e);
	}
    }

    public List getRuleExtensions() {
	return ruleExtensions;
    }

    public void setRuleExtensions(List ruleExtensions) {
	this.ruleExtensions = ruleExtensions;
    }

    public RuleAttribute getRuleAttribute() {
	if (ruleAttribute == null && ruleAttributeId != null) {
	    ruleAttribute = ((RuleAttributeService) KEWServiceLocator.getService(KEWServiceLocator.RULE_ATTRIBUTE_SERVICE)).findByRuleAttributeId(ruleAttributeId);
	}
	return ruleAttribute;
    }

    public void setRuleAttribute(RuleAttribute ruleAttribute) {
	this.ruleAttribute = ruleAttribute;
    }

    public RuleTemplate getRuleTemplate() {
	return ruleTemplate;
    }

    public void setRuleTemplate(RuleTemplate ruleTemplate) {
	this.ruleTemplate = ruleTemplate;
    }

    public String getDefaultValue() {
	return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
	this.defaultValue = defaultValue;
    }

    public Integer getDisplayOrder() {
	return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
	this.displayOrder = displayOrder;
    }

    public Integer getLockVerNbr() {
	return lockVerNbr;
    }

    public void setLockVerNbr(Integer lockVerNbr) {
	this.lockVerNbr = lockVerNbr;
    }

    public boolean isRequired() {
        return (getRequired() == null) || (getRequired().booleanValue());
    }

    public Boolean getRequired() {
	return required;
    }

    public void setRequired(Boolean required) {
	this.required = required;
    }

    public boolean isActive() {
        return (getActive() == null) || (getActive().booleanValue());
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getRuleAttributeId() {
	return ruleAttributeId;
    }

    public void setRuleAttributeId(Long ruleAttributeId) {
	this.ruleAttributeId = ruleAttributeId;
    }

    public Long getRuleTemplateAttributeId() {
	return ruleTemplateAttributeId;
    }

    public void setRuleTemplateAttributeId(Long ruleTemplateAttributeId) {
	this.ruleTemplateAttributeId = ruleTemplateAttributeId;
    }

    public Long getRuleTemplateId() {
	return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
	this.ruleTemplateId = ruleTemplateId;
    }

    public Object copy(boolean preserveKeys) {
	RuleTemplateAttribute ruleTemplateAttributeClone = new RuleTemplateAttribute();
	if (defaultValue != null) {
	    ruleTemplateAttributeClone.setDefaultValue(new String(defaultValue));
	}
	if (displayOrder != null) {
	    ruleTemplateAttributeClone.setDisplayOrder(new Integer(displayOrder.intValue()));
	}
	if (required != null) {
	    ruleTemplateAttributeClone.setRequired(new Boolean(required.booleanValue()));
	}
        if (active != null) {
            ruleTemplateAttributeClone.setActive(Boolean.valueOf(active.booleanValue()));
        }
	if (ruleAttribute != null) {
	    ruleTemplateAttributeClone.setRuleAttribute((RuleAttribute) ruleAttribute.copy(preserveKeys));
	}
	if (preserveKeys && ruleTemplateAttributeId != null) {
	    ruleTemplateAttributeClone.setRuleTemplateAttributeId(new Long(ruleTemplateAttributeId.longValue()));
	}
	return ruleTemplateAttributeClone;
    }
}