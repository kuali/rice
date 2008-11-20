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
package org.kuali.rice.kew.doctype.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.reflect.ObjectDefinition;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.actionlist.CustomActionListAttribute;
import org.kuali.rice.kew.docsearch.DocumentSearchCriteriaProcessor;
import org.kuali.rice.kew.docsearch.DocumentSearchGenerator;
import org.kuali.rice.kew.docsearch.DocumentSearchResultProcessor;
import org.kuali.rice.kew.docsearch.SearchableAttribute;
import org.kuali.rice.kew.docsearch.StandardDocumentSearchCriteriaProcessor;
import org.kuali.rice.kew.docsearch.xml.DocumentSearchXMLResultProcessor;
import org.kuali.rice.kew.docsearch.xml.GenericXMLSearchableAttribute;
import org.kuali.rice.kew.doctype.DocumentTypeAttribute;
import org.kuali.rice.kew.doctype.DocumentTypePolicy;
import org.kuali.rice.kew.doctype.DocumentTypePolicyEnum;
import org.kuali.rice.kew.doctype.DocumentTypeSecurity;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.dto.DTOConverter;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.engine.node.Process;
import org.kuali.rice.kew.exception.ResourceUnavailableException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.mail.CustomEmailAttribute;
import org.kuali.rice.kew.notes.CustomNoteAttribute;
import org.kuali.rice.kew.postprocessor.DefaultPostProcessor;
import org.kuali.rice.kew.postprocessor.PostProcessor;
import org.kuali.rice.kew.postprocessor.PostProcessorRemote;
import org.kuali.rice.kew.postprocessor.PostProcessorRemoteAdapter;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.CodeTranslator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;


/**
 * Model bean mapped to ojb representing a document type.  Provides component lookup behavior that
 * can construct {@link ObjectDefinition} objects correctly to account for ServiceNamespace inheritance.
 * Can also navigate parent hierarchy when getting data/components.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KREW_DOC_TYP_T")
public class DocumentType extends PersistableBusinessObjectBase
{
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentType.class);

    private static final long serialVersionUID = 1312830153583125069L;

    @Id
	@Column(name="DOC_TYP_ID")
	private Long documentTypeId;
    @Column(name="PARNT_ID")
	private Long docTypeParentId;
    @Column(name="DOC_TYP_NM")
	private String name;
    @Column(name="DOC_TYP_VER_NBR")
    private Integer version = new Integer(0);
    @Column(name="ACTV_IND")
	private Boolean active;
    @Column(name="CUR_IND")
	private Boolean currentInd;
    @Column(name="DOC_TYP_DESC")
	private String description;
    @Column(name="LBL")
	private String label;
    @Column(name="PREV_DOC_TYP_VER_NBR")
	private Long previousVersionId;
    @Column(name="DOC_HDR_ID")
	private Long routeHeaderId;
    @Column(name="DOC_HDLR_URL")
	private String docHandlerUrl;
    @Column(name="POST_PRCSR")
	private String postProcessorName;
    @Column(name="GRP_ID")
	//private Long superUserWorkgroupId;
	private String workgroupId;
    @Column(name="BLNKT_APPR_GRP_ID")
	private String blanketApproveWorkgroupId;
    @Column(name="BLNKT_APPR_PLCY")
	private String blanketApprovePolicy;
	@Column(name="RPT_GRP_ID")
	private String reportingWorkgroupId;
    @Column(name="SVC_NMSPC")
	private String serviceNamespace;
    @Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;

    /* these two fields are for the web tier lookupable
     * DocumentType is doing double-duty as a web/business tier object
     */
    @Transient
    private String returnUrl;
    @Transient
    private String actionsUrl;
    @Transient
    private boolean descendHierarchy;

    /* The default exception workgroup to apply to nodes that lack an exception workgroup definition.
     * Used at parse-time only; not stored in db.
     */
    @Transient
    private KimGroup defaultExceptionWorkgroup;

    @OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
           targetEntity=org.kuali.rice.kew.doctype.DocumentTypePolicy.class, mappedBy="documentType")
	private Collection policies;
    @Transient
    private List routeLevels;
    @Transient
    private Collection childrenDocTypes;
    @OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
           targetEntity=org.kuali.rice.kew.doctype.DocumentTypeAttribute.class, mappedBy="documentType")
	private List<DocumentTypeAttribute> documentTypeAttributes;

    /* New Workflow 2.1 Field */
    @Transient
    private List processes = new ArrayList();
    @Column(name="RTE_VER_NBR")
    private String routingVersion = KEWConstants.CURRENT_ROUTING_VERSION;

    /* Workflow 2.2 Fields */
    @Column(name="NOTIFY_ADDR")
	private String notificationFromAddress;
    @Lob
	@Basic(fetch=FetchType.LAZY)
	@Column(name="SEC_XML")
	private String documentTypeSecurityXml;
    @Transient
    private DocumentTypeSecurity documentTypeSecurity;

    /* Workflow 2.4 XSLT-based email message customization */
    @Column(name="EMAIL_XSL")
	private String customEmailStylesheet;

    public DocumentType() {
        routeLevels = new ArrayList();
        documentTypeAttributes = new ArrayList<DocumentTypeAttribute>();
        policies = new ArrayList();
        version = new Integer(0);
    }


    public DocumentTypePolicy getDefaultApprovePolicy() {
        return getPolicyByName(DocumentTypePolicyEnum.DEFAULT_APPROVE.getName(), Boolean.TRUE);
    }

    public DocumentTypePolicy getUseWorkflowSuperUserDocHandlerUrl() {
        return getPolicyByName(DocumentTypePolicyEnum.USE_KEW_SUPERUSER_DOCHANDLER.getName(), Boolean.TRUE);
    }

    public DocumentTypePolicy getInitiatorMustRoutePolicy() {
        return getPolicyByName(DocumentTypePolicyEnum.INITIATOR_MUST_ROUTE.getName(), Boolean.TRUE);
    }

    public DocumentTypePolicy getInitiatorMustSavePolicy() {
        return getPolicyByName(DocumentTypePolicyEnum.INITIATOR_MUST_SAVE.getName(), Boolean.TRUE);
    }

    public DocumentTypePolicy getInitiatorMustCancelPolicy() {
        return getPolicyByName(DocumentTypePolicyEnum.INITIATOR_MUST_CANCEL.getName(), Boolean.TRUE);
    }

    public DocumentTypePolicy getInitiatorMustBlanketApprovePolicy() {
        return getPolicyByName(DocumentTypePolicyEnum.INITIATOR_MUST_BLANKET_APPROVE.getName(), Boolean.TRUE);
    }

    public DocumentTypePolicy getPreApprovePolicy() {
        return getPolicyByName(DocumentTypePolicyEnum.PRE_APPROVE.getName(), Boolean.TRUE);
    }

    public DocumentTypePolicy getLookIntoFuturePolicy() {
        return getPolicyByName(DocumentTypePolicyEnum.LOOK_FUTURE.getName(), Boolean.FALSE);
    }

    public DocumentTypePolicy getSuperUserApproveNotificationPolicy() {
    	return getPolicyByName(DocumentTypePolicyEnum.SEND_NOTIFICATION_ON_SU_APPROVE.getName(), Boolean.FALSE);
    }

    public DocumentTypePolicy getSupportsQuickInitiatePolicy() {
    	return getPolicyByName(DocumentTypePolicyEnum.SUPPORTS_QUICK_INITIATE.getName(), Boolean.TRUE);
    }

    public DocumentTypePolicy getNotifyOnSavePolicy() {
    	return getPolicyByName(DocumentTypePolicyEnum.NOTIFY_ON_SAVE.getName(), Boolean.FALSE);
    }

    public String getUseWorkflowSuperUserDocHandlerUrlValue() {
        if (getUseWorkflowSuperUserDocHandlerUrl() != null) {
            return getUseWorkflowSuperUserDocHandlerUrl().getPolicyDisplayValue();
        }
        return null;
    }

    public String getDefaultApprovePolicyDisplayValue() {
        if (getDefaultApprovePolicy() != null) {
            return getDefaultApprovePolicy().getPolicyDisplayValue();
        }
        return null;
    }

    public String getInitiatorMustRouteDisplayValue() {
        if (getInitiatorMustRoutePolicy() != null) {
            return getInitiatorMustRoutePolicy().getPolicyDisplayValue();
        }
        return null;
    }

    public String getInitiatorMustSaveDisplayValue() {
        if (getInitiatorMustSavePolicy() != null) {
            return getInitiatorMustSavePolicy().getPolicyDisplayValue();
        }
        return null;
    }

    public String getPreApprovePolicyDisplayValue() {
        if (getPreApprovePolicy() != null) {
            return getPreApprovePolicy().getPolicyDisplayValue();
        }
        return null;
    }
    
    public boolean isPolicyDefined(DocumentTypePolicyEnum policyToCheck) {
    	Iterator policyIter = getPolicies().iterator();
        while (policyIter.hasNext()) {
            DocumentTypePolicy policy = (DocumentTypePolicy) policyIter.next();
            if (policyToCheck.getName().equals(policy.getPolicyName())) {
            	return true;
            }
        }
        return getParentDocType() != null && getParentDocType().isPolicyDefined(policyToCheck);
    }

    public void addSearchableAttribute(DocumentTypeAttribute searchableAttribute) {
    	documentTypeAttributes.add(searchableAttribute);
    }

    public boolean hasSearchableAttributes() {
    	return ! getSearchableAttributes().isEmpty();
    }

    public List<SearchableAttribute> getSearchableAttributes() {
    	List<SearchableAttribute> searchAtts = new ArrayList<SearchableAttribute>();
    	if ((documentTypeAttributes == null || documentTypeAttributes.isEmpty())) {
    		if (getParentDocType() != null) {
    			return getParentDocType().getSearchableAttributes();
    		} else {
    			return searchAtts;
    		}
    	}

    	for (Iterator iterator = documentTypeAttributes.iterator(); iterator.hasNext();) {
			DocumentTypeAttribute attribute = (DocumentTypeAttribute) iterator.next();
//			String attributeType = attribute.getRuleAttribute().getType();
			RuleAttribute ruleAttribute = attribute.getRuleAttribute();
			SearchableAttribute searchableAttribute = null;
			if (KEWConstants.SEARCHABLE_ATTRIBUTE_TYPE.equals(ruleAttribute.getType())) {
				ObjectDefinition objDef = getAttributeObjectDefinition(ruleAttribute);
				searchableAttribute = (SearchableAttribute) GlobalResourceLoader.getObject(objDef);
			} else if (KEWConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType())) {
				ObjectDefinition objDef = getAttributeObjectDefinition(ruleAttribute);
				searchableAttribute = (SearchableAttribute) GlobalResourceLoader.getObject(objDef);
				//required to make it work because ruleAttribute XML is required to construct fields
				((GenericXMLSearchableAttribute) searchableAttribute).setRuleAttribute(ruleAttribute);
			}
			if (searchableAttribute != null) {
				searchAtts.add(searchableAttribute);
			}
		}
    	return searchAtts;
    }

    public DocumentTypeAttribute getDocumentTypeAttribute(int index) {
        while (getDocumentTypeAttributes().size() <= index) {
        	DocumentTypeAttribute attribute = new DocumentTypeAttribute();
            //attribute.setDocumentTypeId(this.documentTypeId);
            getDocumentTypeAttributes().add(attribute);
        }
        return (DocumentTypeAttribute) getDocumentTypeAttributes().get(index);
    }

    public void setDocumentTypeAttribute(int index, DocumentTypeAttribute documentTypeAttribute) {
    	documentTypeAttributes.set(index, documentTypeAttribute);
    }

    public String getDocTypeActiveIndicatorDisplayValue() {
        if (getActive() == null) {
            return KEWConstants.INACTIVE_LABEL_LOWER;
        }
        return CodeTranslator.getActiveIndicatorLabel(getActive());
    }

    public Collection getChildrenDocTypes() {
    	if (this.childrenDocTypes == null) {
    		this.childrenDocTypes = KEWServiceLocator.getDocumentTypeService().getChildDocumentTypes(this);
    	}
        return childrenDocTypes;
    }

    public java.lang.Long getDocTypeParentId() {
        return docTypeParentId;
    }

    public void setDocTypeParentId(java.lang.Long docTypeParentId) {
        this.docTypeParentId = docTypeParentId;
    }

    public DocumentType getParentDocType() {
        return KEWServiceLocator.getDocumentTypeService().findById(this.docTypeParentId);
    }

    public Collection getPolicies() {
        return policies;
    }

    public void setPolicies(Collection policies) {
        this.policies = policies;
    }

    public String getDocumentTypeSecurityXml() {
      return documentTypeSecurityXml;
    }

    public void setDocumentTypeSecurityXml(String documentTypeSecurityXml) {
      this.documentTypeSecurityXml = documentTypeSecurityXml;
      if (!Utilities.isEmpty(documentTypeSecurityXml.trim())) {
        this.documentTypeSecurity = new DocumentTypeSecurity(this.getServiceNamespace(), documentTypeSecurityXml);
      }
      else {
        this.documentTypeSecurity = null;
      }
    }

    public DocumentTypeSecurity getDocumentTypeSecurity()  {
      if (this.documentTypeSecurity == null &&
          this.documentTypeSecurityXml != null &&
          !Utilities.isEmpty(documentTypeSecurityXml.trim()))
      {
           this.documentTypeSecurity = new DocumentTypeSecurity(this.getServiceNamespace(), documentTypeSecurityXml);
      }
      if ( (this.documentTypeSecurity == null) && (getParentDocType() != null) ) {
    	  return getParentDocType().getDocumentTypeSecurity();
      }
      return this.documentTypeSecurity;
    }


    public List getRouteLevels() {
        if (routeLevels.isEmpty() && getParentDocType() != null) {
            return getParentRouteLevels(getParentDocType());
        }
        return routeLevels;
    }

    private List getParentRouteLevels(DocumentType parent) {
        if (parent.getRouteLevels() == null) {
            return getParentRouteLevels(parent.getParentDocType());
        } else {
            return parent.getRouteLevels();
        }
    }

    public void setRouteLevels(List routeLevels) {
        this.routeLevels = routeLevels;
    }

    public String getActionsUrl() {
        return actionsUrl;
    }

    public void setActionsUrl(String actions) {
        this.actionsUrl = actions;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(java.lang.Boolean activeInd) {
        this.active = activeInd;
    }

    public java.lang.Boolean getCurrentInd() {
        return currentInd;
    }

    public void setCurrentInd(java.lang.Boolean currentInd) {
        this.currentInd = currentInd;
    }

    public java.lang.String getDescription() {
        return description;
    }

    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    public String getDocHandlerUrl() {
        return resolveDocHandlerUrl(docHandlerUrl);
    }

    public String getUnresolvedDocHandlerUrl() {
    	return docHandlerUrl;
    }

    /**
     * If the doc handler URL has variables in it that need to be replaced, this will look up the values
     * for those variables and replace them in the doc handler URL.
     */
    protected String resolveDocHandlerUrl(String docHandlerUrl) {
    	return Utilities.substituteConfigParameters(docHandlerUrl);
    }

    public void setDocHandlerUrl(java.lang.String docHandlerUrl) {
        this.docHandlerUrl = docHandlerUrl;
    }

    public java.lang.String getLabel() {
        return label;
    }

    public void setLabel(java.lang.String label) {
        this.label = label;
    }

    public java.lang.String getName() {
        return name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }

    public java.lang.String getPostProcessorName() {
        return postProcessorName;
    }

    public void setPostProcessorName(java.lang.String postProcessorName) {
        this.postProcessorName = postProcessorName;
    }

    public java.lang.Long getPreviousVersionId() {
        return previousVersionId;
    }

    public void setPreviousVersionId(java.lang.Long previousVersionId) {
        this.previousVersionId = previousVersionId;
    }

    public java.lang.Long getRouteHeaderId() {
        return routeHeaderId;
    }

    public void setRouteHeaderId(java.lang.Long routeHeaderId) {
        this.routeHeaderId = routeHeaderId;
    }

    public java.lang.Integer getVersion() {
        return version;
    }

    public void setVersion(java.lang.Integer version) {
        this.version = version;
    }

    public java.lang.Long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(java.lang.Long docTypeGrpId) {
        this.documentTypeId = docTypeGrpId;
    }

    public Object copy(boolean preserveKeys) {
        throw new UnsupportedOperationException("The copy method is deprecated and unimplemented!");
    }

    public java.lang.String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(java.lang.String returnUrl) {
        this.returnUrl = returnUrl;
    }

    private DocumentTypePolicy getPolicyByName(String policyName, Boolean defaultValue) {

        Iterator policyIter = getPolicies().iterator();
        while (policyIter.hasNext()) {
            DocumentTypePolicy policy = (DocumentTypePolicy) policyIter.next();
            if (policyName.equals(policy.getPolicyName())) {
                policy.setInheritedFlag(Boolean.FALSE);
                return policy;
            }
        }

        if (getParentDocType() != null) {
            DocumentTypePolicy policy = getParentDocType().getPolicyByName(policyName, defaultValue);
            policy.setInheritedFlag(Boolean.TRUE);
            if (policy.getPolicyValue() == null) {
                policy.setPolicyValue(Boolean.TRUE);
            }
            return policy;
        }
        DocumentTypePolicy policy = new DocumentTypePolicy();
        policy.setPolicyName(policyName);
        policy.setInheritedFlag(Boolean.FALSE);
        policy.setPolicyValue(defaultValue);
        return policy;
    }

    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    private DocumentTypeService getDocumentTypeService() {
        return (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
    }

    public KimGroup getSuperUserWorkgroup() {
	KimGroup superUserWorkgroup = getSuperUserWorkgroupNoInheritence();
	if (superUserWorkgroup == null && getParentDocType() != null) {
	    return getParentDocType().getSuperUserWorkgroup();
	}
	return superUserWorkgroup;
    }

    public KimGroup getSuperUserWorkgroupNoInheritence() {
	if (workgroupId == null) {
	    return null;
	}
	return getIdentityManagementService().getGroup(this.workgroupId);
    }

    public void setSuperUserWorkgroupNoInheritence(KimGroup suWorkgroup) 
    {
		if (suWorkgroup == null) 
		{
		    this.workgroupId = null;
		} 
		else 
		{
		    this.workgroupId = suWorkgroup.getGroupId();
		}
    }
    
    /**
     * Returns true if this DocumentType has a super user group defined.
     */
    public boolean hasSuperUserGroup() {
    	if (this.workgroupId == null) {
    		return getParentDocType() != null && getParentDocType().hasSuperUserGroup(); 
    	}
    	return true;
    }

    public DocumentType getPreviousVersion() {
        return getDocumentTypeService().findById(previousVersionId);
    }

    public KimGroup getBlanketApproveWorkgroup() {
        return getIdentityManagementService().getGroup(blanketApproveWorkgroupId);
    }

    public void setBlanketApproveWorkgroup(KimGroup blanketApproveWorkgroup) {
    	this.blanketApproveWorkgroupId = blanketApproveWorkgroup.getGroupId();
    }

	public String getBlanketApprovePolicy() {
		return this.blanketApprovePolicy;
	}

	public void setBlanketApprovePolicy(String blanketApprovePolicy) {
		this.blanketApprovePolicy = blanketApprovePolicy;
	}

    public KimGroup getBlanketApproveWorkgroupWithInheritance() {
    	if (getParentDocType() != null && this.blanketApproveWorkgroupId == null) {
    		return getParentDocType().getBlanketApproveWorkgroupWithInheritance();
    	}
        return getIdentityManagementService().getGroup(blanketApproveWorkgroupId);
    }

    public boolean isBlanketApprover(String principalId) {
    	if (KEWConstants.DOCUMENT_TYPE_BLANKET_APPROVE_POLICY_NONE.equalsIgnoreCase(getBlanketApprovePolicy())) {
    		// no one can blanket approve this doc type
    		return false;
    	} else if (KEWConstants.DOCUMENT_TYPE_BLANKET_APPROVE_POLICY_ANY.equalsIgnoreCase(getBlanketApprovePolicy())) {
    		// anyone can blanket approve this doc type
    		return true;
    	}
    	if (blanketApproveWorkgroupId != null) {
    		return getIdentityManagementService().isMemberOfGroup(principalId, blanketApproveWorkgroupId);
    	}
    	DocumentType parentDoc = getParentDocType();
    	if (parentDoc != null) {
    		// found parent doc so try to get blanket approver info from it
    		return parentDoc.isBlanketApprover(principalId);
    	}
    	return false;
    }
    
    /**
     * Returns true if either a blanket approve group or blanket approve policy is defined
     * on this Document Type.
     */
    public boolean hasBlanketApproveDefined() {
    	if (StringUtils.isBlank(getBlanketApprovePolicy()) && this.blanketApproveWorkgroupId == null) {
    		return getParentDocType() != null && getParentDocType().hasBlanketApproveDefined(); 
    	}
    	return true;
    }

    public KimGroup getReportingWorkgroup() {
        return getIdentityManagementService().getGroup(this.reportingWorkgroupId);
    }

    public void setReportingWorkgroup(KimGroup reportingWorkgroup) {
    	this.reportingWorkgroupId = reportingWorkgroup.getGroupId();
    }
    
    public KimGroup getDefaultExceptionWorkgroup() {
        return defaultExceptionWorkgroup;
    }

    public void setDefaultExceptionWorkgroup(KimGroup defaultExceptionWorkgroup) {
        this.defaultExceptionWorkgroup = defaultExceptionWorkgroup;
    }

    public DocumentSearchGenerator getDocumentSearchGenerator() {
    	ObjectDefinition objDef = getAttributeObjectDefinition(KEWConstants.SEARCH_GENERATOR_ATTRIBUTE_TYPE);
    	if (objDef == null) {
    		if (getParentDocType() != null) {
    			return getParentDocType().getDocumentSearchGenerator();
    		} else {
                DocumentSearchGenerator generator = KEWServiceLocator.getDocumentSearchService().getStandardDocumentSearchGenerator();
    	    	generator.setSearchableAttributes(getSearchableAttributes());
    	    	return generator;
    		}
    	}
        Object searchGenerator = GlobalResourceLoader.getObject(objDef);
        if (searchGenerator == null) {
            throw new WorkflowRuntimeException("Could not locate DocumentSearchGenerator in this JVM or at service namespace " + getServiceNamespace() + ": " + objDef.getClassName());
        }
        DocumentSearchGenerator docSearchGenerator = (DocumentSearchGenerator)searchGenerator;
        docSearchGenerator.setSearchableAttributes(getSearchableAttributes());
        return docSearchGenerator;
    }

    public DocumentSearchCriteriaProcessor getDocumentSearchCriteriaProcessor() {
    	ObjectDefinition objDef = getAttributeObjectDefinition(KEWConstants.SEARCH_CRITERIA_PROCESSOR_ATTRIBUTE_TYPE);
    	if (objDef == null) {
    		if (getParentDocType() != null) {
    			return getParentDocType().getDocumentSearchCriteriaProcessor();
    		} else {
                return new StandardDocumentSearchCriteriaProcessor();
    		}
    	}
        Object criteriaProcessor = GlobalResourceLoader.getObject(objDef);
        if (criteriaProcessor == null) {
            throw new WorkflowRuntimeException("Could not locate DocumentSearchCriteriaProcessor in this JVM or at service namespace " + getServiceNamespace() + ": " + objDef.getClassName());
        }
        return (DocumentSearchCriteriaProcessor) criteriaProcessor;
    }

    public DocumentSearchResultProcessor getDocumentSearchResultProcessor() {
    	if ((documentTypeAttributes == null || documentTypeAttributes.isEmpty())) {
    		if (getParentDocType() != null) {
    			return getParentDocType().getDocumentSearchResultProcessor();
    		} else {
    		    return KEWServiceLocator.getDocumentSearchService().getStandardDocumentSearchResultProcessor();
// 		    return new StandardDocumentSearchResultProcessor();
    		}
    	}
    	for (Iterator iterator = documentTypeAttributes.iterator(); iterator.hasNext();) {
			DocumentTypeAttribute attribute = (DocumentTypeAttribute) iterator.next();
			RuleAttribute ruleAttribute = attribute.getRuleAttribute();
			if (KEWConstants.SEARCH_RESULT_PROCESSOR_ATTRIBUTE_TYPE.equals(ruleAttribute.getType())) {
				ObjectDefinition objDef = getAttributeObjectDefinition(ruleAttribute);
				return (DocumentSearchResultProcessor) GlobalResourceLoader.getObject(objDef);
			} else if (KEWConstants.SEARCH_RESULT_XML_PROCESSOR_ATTRIBUTE_TYPE.equals(ruleAttribute.getType())) {
				ObjectDefinition objDef = getAttributeObjectDefinition(ruleAttribute);
				DocumentSearchResultProcessor resultProcessor = (DocumentSearchResultProcessor) GlobalResourceLoader.getObject(objDef);
				//required to make it work because ruleAttribute XML is required to construct custom columns
				((DocumentSearchXMLResultProcessor) resultProcessor).setRuleAttribute(ruleAttribute);
				return resultProcessor;
			}
		}
	    return KEWServiceLocator.getDocumentSearchService().getStandardDocumentSearchResultProcessor();
//    	return new StandardDocumentSearchResultProcessor();
    }

    public CustomActionListAttribute getCustomActionListAttribute() throws ResourceUnavailableException {

    	ObjectDefinition objDef = getAttributeObjectDefinition(KEWConstants.ACTION_LIST_ATTRIBUTE_TYPE);
    	if (objDef == null) {
    		return null;
    	}
        try {
            return (CustomActionListAttribute)GlobalResourceLoader.getObject(objDef);
        } catch (RuntimeException e) {
            LOG.error("Error obtaining custom action list attribute: " + objDef, e);
            throw e;
        }

    }

    public CustomEmailAttribute getCustomEmailAttribute() throws ResourceUnavailableException {
    	ObjectDefinition objDef = getAttributeObjectDefinition(KEWConstants.EMAIL_ATTRIBUTE_TYPE);
    	if (objDef == null) {
    		return null;
    	}
    	return (CustomEmailAttribute)GlobalResourceLoader.getObject(objDef);
    }

    public ObjectDefinition getAttributeObjectDefinition(String typeCode) {
    	for (Iterator iter = getDocumentTypeAttributes().iterator(); iter.hasNext();) {
    		RuleAttribute attribute = ((DocumentTypeAttribute)iter.next()).getRuleAttribute();
    		if (attribute.getType().equals(typeCode)) {
    			return getAttributeObjectDefinition(attribute);
    		}
		}
    	if (getParentDocType() != null) {
    		return getParentDocType().getAttributeObjectDefinition(typeCode);
    	}
        return null;
    }

    public ObjectDefinition getAttributeObjectDefinition(RuleAttribute ruleAttribute) {
    	if (ruleAttribute.getServiceNamespace() == null) {
    		return new ObjectDefinition(ruleAttribute.getClassName(), this.getServiceNamespace());
    	} else {
    		return new ObjectDefinition(ruleAttribute.getClassName(), ruleAttribute.getServiceNamespace());
    	}
    }

    public CustomNoteAttribute getCustomNoteAttribute() throws ResourceUnavailableException {
    	ObjectDefinition objDef = getAttributeObjectDefinition(KEWConstants.NOTE_ATTRIBUTE_TYPE);
    	if (objDef == null) {
    		String defaultNoteClass = ConfigContext.getCurrentContextConfig().getDefaultNoteClass();
    		if (defaultNoteClass == null){
    			return null;
    		}
    		objDef = new ObjectDefinition(defaultNoteClass);
    	}
    	return (CustomNoteAttribute)GlobalResourceLoader.getObject(objDef);
    }

    public PostProcessor getPostProcessor()	{
        String pname = getPostProcessorName();
        if (StringUtils.isBlank(pname)) {
            return new DefaultPostProcessor();
        }

    	ObjectDefinition objDef = getObjectDefinition(pname);
    	Object postProcessor = GlobalResourceLoader.getObject(objDef);
        if (postProcessor == null) {
        	throw new WorkflowRuntimeException("Could not locate PostProcessor in this JVM or at service namespace " + getServiceNamespace() + ": " + pname);
        }
    	if (postProcessor instanceof PostProcessorRemote) {
            postProcessor = new PostProcessorRemoteAdapter((PostProcessorRemote)postProcessor);
        }

    	return (PostProcessor)postProcessor;
    }

    public ObjectDefinition getObjectDefinition(String objectName) {
    	return new ObjectDefinition(objectName, getServiceNamespace());
    }

    /**
     * Returns true if this document type defines it's own routing, false if it inherits its routing
     * from a parent document type.
     */
    public boolean isRouteInherited() {
        return processes.isEmpty() && getParentDocType() != null;
    }

    /**
     * Returns the DocumentType which defines the route for this document.  This is the DocumentType
     * from which we inherit our Processes which define our routing.
     */
    public DocumentType getRouteDefiningDocumentType() {
        if (isRouteInherited()) {
            return getParentDocType().getRouteDefiningDocumentType();
        }
        return this;
    }

	public boolean isSearchableAttributesInherited() {
        return documentTypeAttributes.isEmpty() && getParentDocType() != null;
    }

	public boolean isDocTypeActive() {
        if (!active.booleanValue()) {
            return false;
        }
        if (getParentDocType() != null) {
            if (!getParentActiveInd(getParentDocType())) {
                return false;
            }
        }
        return true;
    }

    private boolean getParentActiveInd(DocumentType parentDocType) {
        if (parentDocType.getActive() == null || parentDocType.getActive().booleanValue()) {
            if (parentDocType.getParentDocType() != null) {
                return getParentActiveInd(parentDocType.getParentDocType());
            }
            return true;
        } else {
            return false;
        }
    }

	/**
	 * @param documentTypeAttributes The documentTypeAttributes to set.
	 */
	public void setDocumentTypeAttributes(List<DocumentTypeAttribute> documentTypeAttributes) {
		this.documentTypeAttributes = documentTypeAttributes;
	}

	/**
	 * @return Returns the documentTypeAttributes.
	 */
	public List<DocumentTypeAttribute> getDocumentTypeAttributes() {
		return documentTypeAttributes;
	}

//	public List<DocumentTypeAttribute> getDocumentTypeAttributesWithPotentialInheritance() {
//    	if ((documentTypeAttributes == null || documentTypeAttributes.isEmpty())) {
//    		if (getParentDocType() != null) {
//    			return getParentDocType().getDocumentTypeAttributesWithPotentialInheritance();
//    		} else {
//    			return documentTypeAttributes;
//    		}
//    	}
//		return new ArrayList<DocumentTypeAttribute>();
//	}

    public void addProcess(Process process) {
        processes.add(process);
    }

    /**
     * Gets the processes of this document by checking locally for processes, and if none are
     * present, retrieves them from it's parent document type.  The list returned is an immutable
     * list.  To add processes to a document type, use the addProcess method.
     *
     * NOTE: Since OJB uses direct field access, this will not interfere with the proper
     * mapping of the processes field.
     * @return
     */
    public List getProcesses() {
        if (processes.isEmpty() && getParentDocType() != null) {
            return getParentProcesses(getParentDocType());
        }
        return Collections.unmodifiableList(processes);
    }

    public void setProcesses(List routeNodes) {
        this.processes = routeNodes;
    }

    private List getParentProcesses(DocumentType parent) {
        List parentProcesses = parent.getProcesses();
        if (parentProcesses == null) {
            parentProcesses = getParentProcesses(parent.getParentDocType());
        }
        return parentProcesses;
    }

    public Process getPrimaryProcess() {
    	for (Iterator iterator = getProcesses().iterator(); iterator.hasNext(); ) {
			Process process = (Process) iterator.next();
			if (process.isInitial()) {
				return process;
			}
		}
    	return null;
    }

    public Process getNamedProcess(String name) {
    	for (Iterator iterator = getProcesses().iterator(); iterator.hasNext(); ) {
			Process process = (Process) iterator.next();
			if (Utilities.equals(name, process.getName())) {
				return process;
			}
		}
    	return null;
    }

    public String getRoutingVersion() {
        return routingVersion;
    }

    public void setRoutingVersion(String routingVersion) {
        this.routingVersion = routingVersion;
    }

    public String getNotificationFromAddress() {
    	if (notificationFromAddress == null &&  getParentDocType() != null) {
            return getParentDocType().getNotificationFromAddress();
        }
		return notificationFromAddress;
	}

	public void setNotificationFromAddress(String notificationFromAddress) {
		this.notificationFromAddress = notificationFromAddress;
	}

	public boolean isParentOf(DocumentType documentType) {
        // this is a depth-first search which works for our needs
        for (Iterator iterator = getChildrenDocTypes().iterator(); iterator.hasNext();) {
            DocumentType child = (DocumentType) iterator.next();
            if (child.getName().equals(documentType.getName()) || child.isParentOf(documentType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * this exists because the lookup wants to make a call on a bean method when displaying results and those calls are
     * entered programatically into the framework by method name
     *
     * @return
     */
    public String getLookupParentName() {
    	DocumentType parent = getParentDocType();
    	if (parent == null) {
    		return "Root";
    	}
    	return parent.getName();
    }

    public boolean isSuperUser(String principalId) {
    	KimGroup workgroup = getSuperUserWorkgroup();
		if (workgroup == null) {
			return false;
		}
    	return getIdentityManagementService().isMemberOfGroup(principalId, workgroup.getGroupId());
    }

    public boolean hasPreviousVersion() {
    	if (this.documentTypeId == null) {
    		return false;
    	}
    	return ! this.documentTypeId.equals(this.previousVersionId);
    }

    public String toString() {
        return "[DocumentType: documentTypeId=" + documentTypeId
                          + ", docTypeParentId=" + docTypeParentId
                          + ", name=" + name
                          + ", version=" + version
                          + ", activeInd=" + active
                          + ", currentInd=" + currentInd
                          + ", description=" + description
                          + ", routeHeaderId=" + routeHeaderId
                          + ", docHandlerUrl=" + docHandlerUrl
                          + ", postProcessorName=" + postProcessorName
                          + ", workgroupId=" + workgroupId
                          + ", blanketApproveWorkgroupId=" + blanketApproveWorkgroupId
                          + ", blanketApprovePolicy=" + blanketApprovePolicy
                          + ", lockVerNbr=" + lockVerNbr
                          + ", defaultExceptionWorkgroup=" + defaultExceptionWorkgroup
                          + ", policies=" + policies
                          + ", security=" + documentTypeSecurityXml
                          + ", routeLevels=" + routeLevels
                          + ", childrenDocTypes=" + childrenDocTypes
                          + ", documentTypeAttributes=" + documentTypeAttributes
                          + ", processes=" + processes
                          + ", routingVersion=" + routingVersion
                          + ", notificationFromAddress=" + notificationFromAddress
                          + "]";
    }


    /**
     * Returns the service namespace for this DocumentType which can be specified on the document type itself,
     * inherited from the parent, or defaults to the configured service namespace of the application.
     * 
     * chb:12Nov2008: seems like the accessor should return the field and the auxiliary method "getActualFoo" should
     * be the one to do more elaborate checking
     */
	public String getServiceNamespace() {
		if (this.serviceNamespace != null) {
			return serviceNamespace;
		}
		String returnVal = null;
		if (getParentDocType() != null) {
			returnVal = getParentDocType().getServiceNamespace();
		}
		if (returnVal == null) {
//			returnVal = "KEW";
			returnVal = ConfigContext.getCurrentContextConfig().getServiceNamespace();
		}
		return returnVal;
	}

	/**
	 * Returns the actual specified service namespace for this document type which could be null.
	 */
	public String getActualServiceNamespace() {
		return serviceNamespace;
	}

	public void setServiceNamespace(String ServiceNamespace) {
		this.serviceNamespace = ServiceNamespace;
	}


    /**
     * Gets the name of the custom email stylesheet to use to render email (if any has been set, null otherwise)
     * @return name of the custom email stylesheet to use to render email (if any has been set, null otherwise)
     */
    public String getCustomEmailStylesheet() {
        return customEmailStylesheet;
    }

    /**
     * Sets the name of the custom email stylesheet to use to render email
     * @return name of the custom email stylesheet to use to render email
     */
    public void setCustomEmailStylesheet(String customEmailStylesheet) {
        this.customEmailStylesheet = customEmailStylesheet;
    }

	/**
	 * @return the blanketApproveWorkgroupId
	 */
	public String getBlanketApproveWorkgroupId() {
		return this.blanketApproveWorkgroupId;
	}


	/**
	 * @param blanketApproveWorkgroupId the blanketApproveWorkgroupId to set
	 */
	public void setBlanketApproveWorkgroupId(String blanketApproveWorkgroupId) {
		this.blanketApproveWorkgroupId = blanketApproveWorkgroupId;
	}


	/**
	 * @return the descendHierarchy
	 */
	public boolean isDescendHierarchy() {
		return this.descendHierarchy;
	}


	/**
	 * @param descendHierarchy the descendHierarchy to set
	 */
	public void setDescendHierarchy(boolean descendHierarchy) {
		this.descendHierarchy = descendHierarchy;
	}


    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper()
    {
        // TODO chb - Implement Me!
        return null;
    }

    
    private IdentityManagementService getIdentityManagementService() {
    	return KIMServiceLocator.getIdentityManagementService();
    }
}
