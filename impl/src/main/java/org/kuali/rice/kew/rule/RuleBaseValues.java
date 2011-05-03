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
package org.kuali.rice.kew.rule;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
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
/*import org.kuali.rice.kim.api.group.Group;*/
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.springframework.util.AutoPopulatingList;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;


/**
 * A model bean for a Rule within the KEW rules engine.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_RULE_T")
//@Sequence(name="KREW_RTE_TMPL_S", property="ruleBaseValuesId")
public class RuleBaseValues extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 6137765574728530156L;
    @Id
    @GeneratedValue(generator="KREW_RTE_TMPL_S")
	@GenericGenerator(name="KREW_RTE_TMPL_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREW_RTE_TMPL_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="RULE_ID")
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
	private String documentId;
	@Column(name="FRM_DT")
	private Timestamp fromDate;
	@Column(name="TO_DT")
	private Timestamp toDate;
	@Column(name="DACTVN_DT")
	private Timestamp deactivationDate;
    @Column(name="CUR_IND")
	private Boolean currentInd;
    @Column(name="RULE_VER_NBR")
	private Integer versionNbr;
    @Column(name="FRC_ACTN")
	private Boolean forceAction;
    @Fetch(value = FetchMode.SELECT)
    @OneToMany(fetch=FetchType.EAGER,cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},mappedBy="ruleBaseValues")
	private List<RuleResponsibility> responsibilities;
    @Fetch(value = FetchMode.SELECT)
    @OneToMany(fetch=FetchType.EAGER,cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},mappedBy="ruleBaseValues")
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
    @Transient
    private List<PersonRuleResponsibility> personResponsibilities;
    @Transient
    private List<GroupRuleResponsibility> groupResponsibilities;
    @Transient
    private List<RoleRuleResponsibility> roleResponsibilities;
    @Transient
    private Map<String, String> fieldValues;
    @Transient
    private String groupReviewerName;
    @Transient
    private String groupReviewerNamespace;
    @Transient
    private String personReviewer;
    @Transient
    private String personReviewerType;

    public RuleBaseValues() {
        responsibilities = new ArrayList<RuleResponsibility>();
        ruleExtensions = new ArrayList<RuleExtension>();
        personResponsibilities = new AutoPopulatingList(PersonRuleResponsibility.class);
        groupResponsibilities = new AutoPopulatingList(GroupRuleResponsibility.class);
        roleResponsibilities = new AutoPopulatingList(RoleRuleResponsibility.class);
        fieldValues = new HashMap<String, String>();
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
                    //} else if (!org.apache.commons.lang.StringUtils.isEmpty(field.getDefaultLookupableName())) {
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
        if (previousVersion == null && previousVersionId != null) {
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
                    ruleResponsibilityRow.setVersionNumber(ruleResponsibility.getVersionNumber());
                    ruleResponsibilityRow.setRuleBaseValuesId(ruleResponsibility.getRuleBaseValuesId());
                    ruleResponsibilityRow.setRuleResponsibilityName(ruleResponsibility.getRuleResponsibilityName());
                    ruleResponsibilityRow.setRuleResponsibilityType(ruleResponsibility.getRuleResponsibilityType());
                    //ruleResponsibilityRow.setDelegationRules(ruleResponsibility.getDelegationRules());
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

    public String getToDateString() {
        if (this.toDate != null) {
            return RiceConstants.getDefaultDateFormat().format(this.toDate);
        }
        return null;
    }

    public Boolean getForceAction() {
        return forceAction;
    }

    public void setForceAction(Boolean forceAction) {
        this.forceAction = forceAction;
    }

    public boolean isActive(Date date) {
    	boolean isAfterFromDate = getFromDate() == null || date.after(getFromDate());
    	boolean isBeforeToDate = getToDate() == null || date.before(getToDate());
    	return getActiveInd() && isAfterFromDate && isBeforeToDate;
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

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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

	public List<PersonRuleResponsibility> getPersonResponsibilities() {
		return this.personResponsibilities;
	}

	public void setPersonResponsibilities(List<PersonRuleResponsibility> personResponsibilities) {
		this.personResponsibilities = personResponsibilities;
	}

	public List<GroupRuleResponsibility> getGroupResponsibilities() {
		return this.groupResponsibilities;
	}

	public void setGroupResponsibilities(List<GroupRuleResponsibility> groupResponsibilities) {
		this.groupResponsibilities = groupResponsibilities;
	}

	public List<RoleRuleResponsibility> getRoleResponsibilities() {
		return this.roleResponsibilities;
	}

	public void setRoleResponsibilities(List<RoleRuleResponsibility> roleResponsibilities) {
		this.roleResponsibilities = roleResponsibilities;
	}

	/**
	 * @return the fieldValues
	 */
	public Map<String, String> getFieldValues() {
		return this.fieldValues;
	}

	/**
	 * @param fieldValues the fieldValues to set
	 */
	public void setFieldValues(Map<String, String> fieldValues) {
		this.fieldValues = fieldValues;
	}

    public String getGroupReviewerName() {
        return this.groupReviewerName;
    }

    public String getGroupReviewerNamespace() {
        return this.groupReviewerNamespace;
    }

    public String getPersonReviewer() {
        return this.personReviewer;
    }

    public void setGroupReviewerName(String groupReviewerName) {
        this.groupReviewerName = groupReviewerName;
    }

    public void setGroupReviewerNamespace(String groupReviewerNamespace) {
        this.groupReviewerNamespace = groupReviewerNamespace;
    }

    public void setPersonReviewer(String personReviewer) {
        this.personReviewer = personReviewer;
    }

    /*public Group getKimGroupImpl() {
        return new GroupImpl;
    }*/

    public PersonImpl getPersonImpl() {
        return new PersonImpl();
    }

    public String getPersonReviewerType() {
        return this.personReviewerType;
    }

    public void setPersonReviewerType(String personReviewerType) {
        this.personReviewerType = personReviewerType;
    }
}
