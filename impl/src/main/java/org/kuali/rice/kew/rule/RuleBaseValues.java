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
package org.kuali.rice.kew.rule;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.lookupable.MyColumns;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.rule.service.RuleService;
import org.kuali.rice.kew.rule.xmlrouting.GenericXMLRuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.CodeTranslator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;


/**
 * A model bean for a Rule within the KEW rules engine.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KREW_RULE_T")
public class RuleBaseValues extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 6137765574728530156L;
    @Id
	@Column(name="RULE_ID")
    @GeneratedValue(strategy=GenerationType.AUTO, generator="KREW_RTE_TMPL_SEQ_GEN")
    @SequenceGenerator(name="KREW_RTE_TMPL_SEQ_GEN", sequenceName="KREW_RTE_TMPL_S") 
	private Long ruleBaseValuesId;
    /**
     * Unique Rule name
     */
    @Column(name="NM")
	private String name;
    @Column(name="RULE_TMPL_ID", insertable=false, updatable=false)
	private Long ruleTemplateId;
    @Column(name="PREV_RULE_VER_NBR")
	private Long previousVersionId;
    @Column(name="ACTV_IND")
	private Boolean activeInd;
    @Column(name="RULE_BASE_VAL_DESC")
	private String description;
    @Column(name="DOC_TYP_NM")
	private String docTypeName;
    @Column(name="DOC_HDR_ID")
	private Long routeHeaderId;
	@Column(name="FRM_DT")
	private Timestamp fromDate;
	@Column(name="TO_DT", nullable=false)
	private Timestamp toDate;
	@Column(name="DACTVN_DT")
	private Timestamp deactivationDate;
    @Column(name="CUR_IND")
	private Boolean currentInd;
    @Column(name="RULE_VER_NBR")
	private Integer versionNbr;
    @Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;
    @Column(name="IGNR_PRVS")
	private Boolean ignorePrevious;
    @Fetch(value = FetchMode.SUBSELECT) 
    @OneToMany(fetch=FetchType.EAGER,cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
           targetEntity=org.kuali.rice.kew.rule.RuleResponsibility.class, mappedBy="ruleBaseValues")
	private List<RuleResponsibility> responsibilities;
    @Fetch(value = FetchMode.SUBSELECT) 
    @OneToMany(fetch=FetchType.EAGER,cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
           targetEntity=org.kuali.rice.kew.rule.RuleExtension.class, mappedBy="ruleBaseValues")
	private List<RuleExtension> ruleExtensions;
    @ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="RULE_TMPL_ID")
	private RuleTemplate ruleTemplate;
    @OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})
	@JoinColumn(name="RULE_EXPR_ID")
	private RuleExpressionDef ruleExpressionDef;
    @Transient
    private RuleBaseValues previousVersion;
    @Column(name="ACTVN_DT")
	private Timestamp activationDate;
    @Column(name="DLGN_IND")
    private Boolean delegateRule = Boolean.FALSE;
    /**
     * Indicator that signifies that this rule is a defaults/template rule which contains
     * template-defined rule defaults for other rules which use the associated template
     */
    @Column(name="TMPL_RULE_IND")
    private Boolean templateRuleInd = Boolean.FALSE;

    // required to be lookupable
    @Transient
    private String returnUrl;
    @Transient
    private String destinationUrl;
    @Transient
    private MyColumns myColumns;

    public RuleBaseValues() {
        responsibilities = new ArrayList<RuleResponsibility>();
        ruleExtensions = new ArrayList<RuleExtension>();
    }

    /**
     * @return the rule expression definition for this rule, if defined
     */
    public RuleExpressionDef getRuleExpressionDef() {
        return ruleExpressionDef;
    }

    /**
     * @param ruleExpressionDef the rule expression definition to set for this rule
     */
    public void setRuleExpressionDef(RuleExpressionDef ruleExpressionDef) {
        this.ruleExpressionDef = ruleExpressionDef;
    }

    public Map getRuleExtensionValueLabels() {
        Map extensionLabels = new HashMap();
        for (Iterator iterator2 = getRuleExtensions().iterator(); iterator2.hasNext();) {
            RuleExtension ruleExtension = (RuleExtension) iterator2.next();
            if (!ruleExtension.getRuleTemplateAttribute().isWorkflowAttribute()) {
                continue;
            }
            WorkflowAttribute workflowAttribute = ruleExtension.getRuleTemplateAttribute().getWorkflowAttribute();

            RuleAttribute ruleAttribute = ruleExtension.getRuleTemplateAttribute().getRuleAttribute();
            if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
                ((GenericXMLRuleAttribute) workflowAttribute).setRuleAttribute(ruleAttribute);
            }
            for (Iterator iterator = workflowAttribute.getRuleRows().iterator(); iterator.hasNext();) {
                Row row = (Row) iterator.next();
                for (Iterator iterator3 = row.getFields().iterator(); iterator3.hasNext();) {
                    Field field = (Field) iterator3.next();
                    if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
                        extensionLabels.put(field.getPropertyName(), field.getFieldLabel());
                    //} else if (!Utilities.isEmpty(field.getDefaultLookupableName())) {
                    //    extensionLabels.put(field.getDefaultLookupableName(), field.getFieldLabel());
                    } else {
                        extensionLabels.put(field.getPropertyName(), field.getFieldLabel());
                    }
                }
            }
        }
        return extensionLabels;
    }

    public String getRuleTemplateName() {
        if (ruleTemplate != null) {
            return ruleTemplate.getName();
        }
        return null;
    }

    public RuleBaseValues getPreviousVersion() {
        if (previousVersion == null) {
            RuleService ruleService = (RuleService) KEWServiceLocator.getService(KEWServiceLocator.RULE_SERVICE);
            return ruleService.findRuleBaseValuesById(previousVersionId);
        }
        return previousVersion;
    }

    public void setPreviousVersion(RuleBaseValues previousVersion) {
        this.previousVersion = previousVersion;
    }

    public RuleResponsibility getResponsibility(int index) {
        while (getResponsibilities().size() <= index) {
            RuleResponsibility ruleResponsibility = new RuleResponsibility();
            ruleResponsibility.setRuleBaseValues(this);
            getResponsibilities().add(ruleResponsibility);
        }
        return (RuleResponsibility) getResponsibilities().get(index);
    }

    public RuleExtension getRuleExtension(int index) {
        while (getRuleExtensions().size() <= index) {
            getRuleExtensions().add(new RuleExtension());
        }
        return (RuleExtension) getRuleExtensions().get(index);
    }

    public RuleExtensionValue getRuleExtensionValue(String key) {
        for (Iterator iter = getRuleExtensions().iterator(); iter.hasNext();) {
            RuleExtension ruleExtension = (RuleExtension) iter.next();
            for (Iterator iterator = ruleExtension.getExtensionValues().iterator(); iterator.hasNext();) {
                RuleExtensionValue ruleExtensionValue = (RuleExtensionValue) iterator.next();
                if (ruleExtensionValue.getKey().equals(key)) {
                    return ruleExtensionValue;
                }
            }
        }
        return null;
    }

    public RuleExtensionValue getRuleExtensionValue(Long ruleTemplateAttributeId, String key) {
        for (Iterator iter = getRuleExtensions().iterator(); iter.hasNext();) {
            RuleExtension ruleExtension = (RuleExtension) iter.next();
            if (ruleExtension.getRuleTemplateAttributeId().equals(ruleTemplateAttributeId)) {
                for (Iterator iterator = ruleExtension.getExtensionValues().iterator(); iterator.hasNext();) {
                    RuleExtensionValue ruleExtensionValue = (RuleExtensionValue) iterator.next();
                    if (ruleExtensionValue.getKey().equals(key)) {
                        return ruleExtensionValue;
                    }
                }
            }
        }
        return null;
    }

    public Long getPreviousVersionId() {
        return previousVersionId;
    }

    public void setPreviousVersionId(Long previousVersion) {
        this.previousVersionId = previousVersion;
    }

    public void addRuleResponsibility(RuleResponsibility ruleResponsibility) {
        addRuleResponsibility(ruleResponsibility, new Integer(getResponsibilities().size()));
    }

    public void addRuleResponsibility(RuleResponsibility ruleResponsibility, Integer counter) {
        boolean alreadyAdded = false;
        int location = 0;
        if (counter != null) {
            for (Iterator responsibilitiesIter = getResponsibilities().iterator(); responsibilitiesIter.hasNext();) {
                RuleResponsibility ruleResponsibilityRow = (RuleResponsibility) responsibilitiesIter.next();
                if (counter.intValue() == location) {
                    ruleResponsibilityRow.setPriority(ruleResponsibility.getPriority());
                    ruleResponsibilityRow.setActionRequestedCd(ruleResponsibility.getActionRequestedCd());
                    ruleResponsibilityRow.setLockVerNbr(ruleResponsibility.getLockVerNbr());
                    ruleResponsibilityRow.setRuleBaseValuesId(ruleResponsibility.getRuleBaseValuesId());
                    ruleResponsibilityRow.setRuleResponsibilityName(ruleResponsibility.getRuleResponsibilityName());
                    ruleResponsibilityRow.setRuleResponsibilityType(ruleResponsibility.getRuleResponsibilityType());
                    ruleResponsibilityRow.setDelegationRules(ruleResponsibility.getDelegationRules());
                    ruleResponsibilityRow.setApprovePolicy(ruleResponsibility.getApprovePolicy());
                    alreadyAdded = true;
                }
                location++;
            }
        }
        if (!alreadyAdded) {
            getResponsibilities().add(ruleResponsibility);
        }
    }

    public RuleTemplate getRuleTemplate() {
        return ruleTemplate;
    }

    public void setRuleTemplate(RuleTemplate ruleTemplate) {
        this.ruleTemplate = ruleTemplate;
    }

    public Long getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }
    
    public DocumentType getDocumentType() {
    	return KEWServiceLocator.getDocumentTypeService().findByName(getDocTypeName());
    }

    public String getDocTypeName() {
        return docTypeName;
    }

    public void setDocTypeName(String docTypeName) {
        this.docTypeName = docTypeName;
    }

    public List<RuleExtension> getRuleExtensions() {
        return ruleExtensions;
    }

    public void setRuleExtensions(List<RuleExtension> ruleExtensions) {
        this.ruleExtensions = ruleExtensions;
    }

    public List<RuleResponsibility> getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(List<RuleResponsibility> responsibilities) {
        this.responsibilities = responsibilities;
    }

    public RuleResponsibility getResponsibility(Long ruleResponsibilityKey) {
        for (Iterator iterator = getResponsibilities().iterator(); iterator.hasNext();) {
            RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
            if (responsibility.getRuleResponsibilityKey() != null
                    && responsibility.getRuleResponsibilityKey().equals(ruleResponsibilityKey)) {
                return responsibility;
            }
        }
        return null;
    }

    public void removeResponsibility(int index) {
        getResponsibilities().remove(index);
    }

    public Boolean getActiveInd() {
        return activeInd;
    }

    public void setActiveInd(Boolean activeInd) {
        this.activeInd = activeInd;
    }

    public String getActiveIndDisplay() {
        if (getActiveInd() == null) {
            return KEWConstants.INACTIVE_LABEL_LOWER;
        }
        return CodeTranslator.getActiveIndicatorLabel(getActiveInd());
    }

    public Boolean getCurrentInd() {
        return currentInd;
    }

    public void setCurrentInd(Boolean currentInd) {
        this.currentInd = currentInd;
    }

    public Timestamp getFromDate() {
        return fromDate;
    }

    public void setFromDate(Timestamp fromDate) {
        this.fromDate = fromDate;
    }

    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getRuleBaseValuesId() {
        return ruleBaseValuesId;
    }

    public void setRuleBaseValuesId(Long ruleBaseValuesId) {
        this.ruleBaseValuesId = ruleBaseValuesId;
    }

    public Timestamp getToDate() {
        return toDate;
    }

    public void setToDate(Timestamp toDate) {
        this.toDate = toDate;
    }

    public Integer getVersionNbr() {
        return versionNbr;
    }

    public void setVersionNbr(Integer versionNbr) {
        this.versionNbr = versionNbr;
    }

    public Object copy(boolean preserveKeys) {
        RuleBaseValues ruleBaseValuesClone = new RuleBaseValues();

        if (preserveKeys && (ruleBaseValuesId != null)) {
            ruleBaseValuesClone.setRuleBaseValuesId(new Long(ruleBaseValuesId.longValue()));
        }
        if (routeHeaderId != null) {
            ruleBaseValuesClone.setRouteHeaderId(new Long(routeHeaderId.longValue()));
        }
        if (ignorePrevious != null) {
            ruleBaseValuesClone.setIgnorePrevious(new Boolean(ignorePrevious.booleanValue()));
        }
        if (activeInd != null) {
            ruleBaseValuesClone.setActiveInd(new Boolean(activeInd.booleanValue()));
        }
        if (currentInd != null) {
            ruleBaseValuesClone.setCurrentInd(new Boolean(currentInd.booleanValue()));
        }
        if (docTypeName != null) {
            ruleBaseValuesClone.setDocTypeName(new String(docTypeName));
        }
        if (fromDate != null) {
            ruleBaseValuesClone.setFromDate(new Timestamp(fromDate.getTime()));
        }
        if (description != null) {
            ruleBaseValuesClone.setDescription(new String(description));
        }
        if (delegateRule != null) {
            ruleBaseValuesClone.setDelegateRule(new Boolean(delegateRule.booleanValue()));
        }
        if ((responsibilities != null) && !responsibilities.isEmpty()) {
            List responsibilityList = new ArrayList();

            for (Iterator i = responsibilities.iterator(); i.hasNext();) {
                RuleResponsibility responsibility = (RuleResponsibility) i.next();
                RuleResponsibility responsibilityCopy = (RuleResponsibility) responsibility.copy(false);
                responsibilityCopy.setRuleBaseValues(ruleBaseValuesClone);
                responsibilityList.add(responsibilityCopy);
            }
            ruleBaseValuesClone.setResponsibilities(responsibilityList);
        }

        if ((ruleExtensions != null) && !ruleExtensions.isEmpty()) {
            List<RuleExtension> ruleExtensionsList = new ArrayList<RuleExtension>();

            for (Iterator i = ruleExtensions.iterator(); i.hasNext();) {
                RuleExtension ruleExtension = (RuleExtension) i.next();
                RuleExtension ruleExtensionCopy = (RuleExtension) ruleExtension.copy(preserveKeys);
                ruleExtensionCopy.setRuleBaseValues(ruleBaseValuesClone);
                ruleExtensionsList.add(ruleExtensionCopy);
            }
            ruleBaseValuesClone.setRuleExtensions(ruleExtensionsList);
        }
        if (toDate != null) {
            ruleBaseValuesClone.setToDate(new Timestamp(toDate.getTime()));
        }
        if (versionNbr != null) {
            ruleBaseValuesClone.setVersionNbr(new Integer(versionNbr.intValue()));
        }
        ruleBaseValuesClone.setActivationDate(getActivationDate());
        ruleBaseValuesClone.setRuleTemplate(getRuleTemplate());
        return ruleBaseValuesClone;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getFromDateString() {
        if (this.fromDate != null) {
            return RiceConstants.getDefaultDateFormat().format(this.fromDate);
        }
        return null;
    }

    public void setFromDateString(String fromDateString) {
        try {
            this.fromDate = new Timestamp(RiceConstants.getDefaultDateFormat().parse(fromDateString).getTime());
        } catch (Exception e) {
        }
    }

    public String getToDateString() {
        if (this.toDate != null) {
            return RiceConstants.getDefaultDateFormat().format(this.toDate);
        }
        return null;
    }

    public void setToDateString(String toDateString) {
        try {
            this.toDate = new Timestamp(RiceConstants.getDefaultDateFormat().parse(toDateString).getTime());
        } catch (Exception e) {
        }
    }

    public Boolean getIgnorePrevious() {
        return ignorePrevious;
    }

    public void setIgnorePrevious(Boolean ignorePrevious) {
        this.ignorePrevious = ignorePrevious;
    }

    public boolean isMatch(DocumentContent docContent) {
        for (Iterator iter = getRuleTemplate().getActiveRuleTemplateAttributes().iterator(); iter.hasNext();) {
            RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iter.next();
            if (!ruleTemplateAttribute.isWorkflowAttribute()) {
                continue;
            }
            WorkflowAttribute routingAttribute = (WorkflowAttribute) ruleTemplateAttribute.getWorkflowAttribute();

            RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
            if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
                ((GenericXMLRuleAttribute) routingAttribute).setRuleAttribute(ruleAttribute);
            }
            String className = ruleAttribute.getClassName();
            List<RuleExtension> editedRuleExtensions = new ArrayList<RuleExtension>();
            for (Iterator iter2 = getRuleExtensions().iterator(); iter2.hasNext();) {
                RuleExtension extension = (RuleExtension) iter2.next();
                if (extension.getRuleTemplateAttribute().getRuleAttribute().getClassName().equals(className)) {
                    editedRuleExtensions.add(extension);
                }
            }
            if (!routingAttribute.isMatch(docContent, editedRuleExtensions)) {
                return false;
            }
        }
        return true;
    }

    public RuleResponsibility findResponsibility(String roleName) {
        for (Iterator iter = getResponsibilities().iterator(); iter.hasNext();) {
            RuleResponsibility resp = (RuleResponsibility) iter.next();
            if (KEWConstants.RULE_RESPONSIBILITY_ROLE_ID.equals(resp.getRuleResponsibilityType())
                    && roleName.equals(resp.getRuleResponsibilityName())) {
                return resp;
            }
        }
        return null;
    }

    public Long getRouteHeaderId() {
        return routeHeaderId;
    }

    public void setRouteHeaderId(Long routeHeaderId) {
        this.routeHeaderId = routeHeaderId;
    }

    public Boolean getDelegateRule() {
        return delegateRule;
    }

    public void setDelegateRule(Boolean isDelegateRule) {
        this.delegateRule = isDelegateRule;
    }

    public Timestamp getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Timestamp activationDate) {
        this.activationDate = activationDate;
    }

    public MyColumns getMyColumns() {
        return myColumns;
    }

    public void setMyColumns(MyColumns additionalColumns) {
        this.myColumns = additionalColumns;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
    }

    public Timestamp getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(Timestamp deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    /**
     * @return whether this is a defaults/template rule
     */
    public Boolean getTemplateRuleInd() {
        return templateRuleInd;
    }

    /**
     * @param templateRuleInd whether this is a defaults/template rule
     */
    public void setTemplateRuleInd(Boolean templateRuleInd) {
        this.templateRuleInd = templateRuleInd;
    }

    /**
     * Get the rule name
     * @return the rule name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the rule name
     * @param name the rule name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
	protected LinkedHashMap<String, Object> toStringMapper() {
		LinkedHashMap<String, Object> mapper = new LinkedHashMap<String, Object>();
        mapper.put("ruleBaseValuesId", ruleBaseValuesId);
        mapper.put("description", description);
        mapper.put("docTypeName", docTypeName);
        mapper.put("routeHeaderId", routeHeaderId);
        mapper.put("delegateRule", delegateRule);
        mapper.put("ignorePrevious", ignorePrevious);
        mapper.put("activeInd", activeInd);
        mapper.put("currentInd", currentInd);
        mapper.put("versionNbr", versionNbr);
        mapper.put("previousVersionId", previousVersionId);
        mapper.put("ruleTemplateId", ruleTemplateId);
        mapper.put("returnUrl", returnUrl);
        mapper.put("responsibilities", responsibilities == null ? responsibilities : "size: " + responsibilities.size());
        mapper.put("lockVerNbr", lockVerNbr).toString();
		return mapper;
	}

}
