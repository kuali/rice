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
package edu.iu.uis.eden.doctype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.Core;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowPersistable;
import edu.iu.uis.eden.clientapp.PostProcessorRemote;
import edu.iu.uis.eden.clientapp.vo.DocumentTypeVO;
import edu.iu.uis.eden.docsearch.DocumentSearchGenerator;
import edu.iu.uis.eden.docsearch.DocumentSearchResultProcessor;
import edu.iu.uis.eden.docsearch.SearchableAttribute;
import edu.iu.uis.eden.docsearch.StandardDocumentSearchGenerator;
import edu.iu.uis.eden.docsearch.StandardDocumentSearchResultProcessor;
import edu.iu.uis.eden.docsearch.xml.DocumentSearchXMLResultProcessor;
import edu.iu.uis.eden.docsearch.xml.GenericXMLSearchableAttribute;
import edu.iu.uis.eden.engine.node.Process;
import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.notes.CustomNoteAttribute;
import edu.iu.uis.eden.plugin.attributes.CustomActionListAttribute;
import edu.iu.uis.eden.plugin.attributes.CustomEmailAttribute;
import edu.iu.uis.eden.postprocessor.DefaultPostProcessor;
import edu.iu.uis.eden.postprocessor.PostProcessor;
import edu.iu.uis.eden.postprocessor.PostProcessorRemoteAdapter;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.server.BeanConverter;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.CodeTranslator;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Model bean mapped to ojb representing a document type.  Provides component lookup behavior that
 * can construct {@link ObjectDefinition} objects correctly to account for MessageEntity inheritance.
 * Can also navigate parent hierarchy when getting data/components.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentType implements WorkflowPersistable {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentType.class);

    private static final long serialVersionUID = 1312830153583125069L;

    private Long documentTypeId;
    private Long docTypeParentId;
    private String name;
    private Integer version = new Integer(0);
    private Boolean activeInd;
    private Boolean currentInd;
    private String description;
    private String label;
    private Long previousVersionId;
    private Long routeHeaderId;
    private String docHandlerUrl;
    private String postProcessorName;
    private Long workgroupId;
    private Long blanketApproveWorkgroupId;
    private String blanketApprovePolicy;
    private String messageEntity;
    private Integer lockVerNbr;

    /* these two fields are for the web tier lookupable
     * DocumentType is doing double-duty as a web/business tier object
     */
    private String returnUrl;
    private String actionsUrl;

    /* The default exception workgroup to apply to nodes that lack an exception workgroup definition.
     * Used at parse-time only; not stored in db.
     */
    private Workgroup defaultExceptionWorkgroup;

    private Collection policies;
    private List routeLevels;
    private Collection childrenDocTypes;
    private List<DocumentTypeAttribute> documentTypeAttributes;

    /* New Workflow 2.1 Field */
    private List processes = new ArrayList();
    private String routingVersion = EdenConstants.CURRENT_ROUTING_VERSION;

    /* Workflow 2.2 Fields */
    private String notificationFromAddress;
    private String documentTypeSecurityXml;
    private DocumentTypeSecurity documentTypeSecurity;

    /* Workflow 2.4 XSLT-based email message customization */
    private String customEmailStylesheet;

    public DocumentType() {
        routeLevels = new ArrayList();
        documentTypeAttributes = new ArrayList<DocumentTypeAttribute>();
        policies = new ArrayList();
        version = new Integer(0);
    }


    public DocumentTypePolicy getDefaultApprovePolicy() {
        return getPolicyByName(EdenConstants.DEFAULT_APPROVE_POLICY, Boolean.TRUE);
    }


    public DocumentTypePolicy getInitiatorMustRoutePolicy() {
        return getPolicyByName(EdenConstants.INITIATOR_MUST_ROUTE_POLICY, Boolean.TRUE);
    }

    public DocumentTypePolicy getInitiatorMustSavePolicy() {
        return getPolicyByName(EdenConstants.INITIATOR_MUST_SAVE_POLICY, Boolean.TRUE);
    }

    public DocumentTypePolicy getInitiatorMustCancelPolicy() {
        return getPolicyByName(EdenConstants.INITIATOR_MUST_CANCEL_POLICY, Boolean.TRUE);
    }

    public DocumentTypePolicy getInitiatorMustBlanketApprovePolicy() {
        return getPolicyByName(EdenConstants.INITIATOR_MUST_BLANKET_APPROVE_POLICY, Boolean.TRUE);
    }

    public DocumentTypePolicy getPreApprovePolicy() {
        return getPolicyByName(EdenConstants.PREAPPROVE_POLICY, Boolean.TRUE);
    }

    public DocumentTypePolicy getLookIntoFuturePolicy() {
        return getPolicyByName(EdenConstants.LOOK_INTO_FUTURE_POLICY, Boolean.FALSE);
    }

    public DocumentTypePolicy getSuperUserApproveNotificationPolicy() {
    	return getPolicyByName(DocumentTypePolicyEnum.SEND_NOTIFICATION_ON_SU_APPROVE.getName(), Boolean.FALSE);
    }

    public DocumentTypePolicy getSupportsQuickInitiatePolicy() {
    	return getPolicyByName(EdenConstants.SUPPORTS_QUICK_INITIATE_POLICY, Boolean.TRUE);
    }

    public DocumentTypePolicy getNotifyOnSavePolicy() {
    	return getPolicyByName(EdenConstants.NOTIFY_ON_SAVE_POLICY, Boolean.FALSE);
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
			if (EdenConstants.SEARCHABLE_ATTRIBUTE_TYPE.equals(ruleAttribute.getType())) {
				ObjectDefinition objDef = getAttributeObjectDefinition(ruleAttribute);
				searchableAttribute = (SearchableAttribute) GlobalResourceLoader.getObject(objDef);
			} else if (EdenConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType())) {
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
        if (getActiveInd() == null) {
            return EdenConstants.INACTIVE_LABEL_LOWER;
        }
        return CodeTranslator.getActiveIndicatorLabel(getActiveInd());
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
        this.documentTypeSecurity = new DocumentTypeSecurity(documentTypeSecurityXml);
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
           this.documentTypeSecurity = new DocumentTypeSecurity(documentTypeSecurityXml);
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

    public Boolean getActiveInd() {
        return activeInd;
    }

    public void setActiveInd(java.lang.Boolean activeInd) {
        this.activeInd = activeInd;
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

    /**
     * @deprecated
     */
    public DocumentTypeVO getDocumentTypeVO() {
        return BeanConverter.convertDocumentType(this);
    }

    private DocumentTypeService getDocumentTypeService() {
        return (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
    }

    public Workgroup getSuperUserWorkgroup() {
	Workgroup superUserWorkgroup = getSuperUserWorkgroupNoInheritence();
	if (superUserWorkgroup == null && getParentDocType() != null) {
	    return getParentDocType().getSuperUserWorkgroup();
	}
	return superUserWorkgroup;
    }

    public Workgroup getSuperUserWorkgroupNoInheritence() {
	if (workgroupId == null) {
	    return null;
	}
	return KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(this.workgroupId));
    }

    public void setSuperUserWorkgroupNoInheritence(Workgroup suWorkgroup) {
	if (suWorkgroup == null) {
	    this.workgroupId = null;
	} else {
	    this.workgroupId = suWorkgroup.getWorkflowGroupId().getGroupId();
	}
    }

//    public void setSuperUserWorkgroup(Workgroup suWorkgroup) {
//    	this.workgroupId = suWorkgroup.getWorkflowGroupId().getGroupId();
//    }

    public DocumentType getPreviousVersion() {
        return getDocumentTypeService().findById(previousVersionId);
    }

    public Workgroup getBlanketApproveWorkgroup() {
        return KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(this.blanketApproveWorkgroupId));
    }

    public void setBlanketApproveWorkgroup(Workgroup blanketApproveWorkgroup) {
    	this.blanketApproveWorkgroupId = blanketApproveWorkgroup.getWorkflowGroupId().getGroupId();
    }

	public String getBlanketApprovePolicy() {
		return this.blanketApprovePolicy;
	}

	public void setBlanketApprovePolicy(String blanketApprovePolicy) {
		this.blanketApprovePolicy = blanketApprovePolicy;
	}

    public Workgroup getBlanketApproveWorkgroupWithInheritance() {
    	if (getParentDocType() != null && this.blanketApproveWorkgroupId == null) {
    		return getParentDocType().getBlanketApproveWorkgroupWithInheritance();
    	}
        return KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(this.blanketApproveWorkgroupId));
    }

    public boolean isUserBlanketApprover(WorkflowUser user) {
    	if (EdenConstants.DOCUMENT_TYPE_BLANKET_APPROVE_POLICY_NONE.equalsIgnoreCase(getBlanketApprovePolicy())) {
    		// no one can blanket approve this doc type
    		return false;
    	} else if (EdenConstants.DOCUMENT_TYPE_BLANKET_APPROVE_POLICY_ANY.equalsIgnoreCase(getBlanketApprovePolicy())) {
    		// anyone can blanket approve this doc type
    		return true;
    	}
    	Workgroup blanketApproveGroup = getBlanketApproveWorkgroup();
    	if (blanketApproveGroup != null) {
    		// found blanket approve group on this doc type
    		return blanketApproveGroup.hasMember(user);
    	} else if (this.blanketApproveWorkgroupId != null) {
    		// found no valid workgroup but we have a workgroup id somehow
            throw new WorkflowRuntimeException("Could not locate valid workgroup for given blanket approve workgroup id  '" + this.blanketApproveWorkgroupId + "'");
    	}
    	DocumentType parentDoc = getParentDocType();
    	if (parentDoc != null) {
    		// found parent doc so try to get blanket approver info from it
    		return parentDoc.isUserBlanketApprover(user);
    	}
    	return false;
    }

    public Workgroup getDefaultExceptionWorkgroup() {
        return defaultExceptionWorkgroup;
    }

    public void setDefaultExceptionWorkgroup(Workgroup defaultExceptionWorkgroup) {
        this.defaultExceptionWorkgroup = defaultExceptionWorkgroup;
    }

    public DocumentSearchGenerator getDocumentSearchGenerator() {
    	ObjectDefinition objDef = getAttributeObjectDefinition(EdenConstants.SEARCH_GENERATOR_ATTRIBUTE_TYPE);
    	if (objDef == null) {
    	    DocumentSearchGenerator generator = KEWServiceLocator.getDocumentSearchService().getStandardDocumentSearchGenerator();
    	    generator.setSearchableAttributes(getSearchableAttributes());
    	    return generator;
//            return new StandardDocumentSearchGenerator(getSearchableAttributes());
    	}
        Object searchGenerator = GlobalResourceLoader.getObject(objDef);
        if (searchGenerator == null) {
            throw new WorkflowRuntimeException("Could not locate DocumentSearchGenerator in this JVM or at message entity " + getMessageEntity() + ": " + objDef.getClassName());
        }
        DocumentSearchGenerator docSearchGenerator = (DocumentSearchGenerator)searchGenerator;
        docSearchGenerator.setSearchableAttributes(getSearchableAttributes());
        return docSearchGenerator;
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
			if (EdenConstants.SEARCH_RESULT_PROCESSOR_ATTRIBUTE_TYPE.equals(ruleAttribute.getType())) {
				ObjectDefinition objDef = getAttributeObjectDefinition(ruleAttribute);
				return (DocumentSearchResultProcessor) GlobalResourceLoader.getObject(objDef);
			} else if (EdenConstants.SEARCH_RESULT_XML_PROCESSOR_ATTRIBUTE_TYPE.equals(ruleAttribute.getType())) {
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

    	ObjectDefinition objDef = getAttributeObjectDefinition(EdenConstants.ACTION_LIST_ATTRIBUTE_TYPE);
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
    	ObjectDefinition objDef = getAttributeObjectDefinition(EdenConstants.EMAIL_ATTRIBUTE_TYPE);
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
    	if (ruleAttribute.getMessageEntity() == null) {
    		return new ObjectDefinition(ruleAttribute.getClassName(), this.getMessageEntity());
    	} else {
    		return new ObjectDefinition(ruleAttribute.getClassName(), ruleAttribute.getMessageEntity());
    	}
    }

    public CustomNoteAttribute getCustomNoteAttribute() throws ResourceUnavailableException {
    	ObjectDefinition objDef = getAttributeObjectDefinition(EdenConstants.NOTE_ATTRIBUTE_TYPE);
    	if (objDef == null) {
    		String defaultNoteClass = Core.getCurrentContextConfig().getDefaultNoteClass();
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
        	throw new WorkflowRuntimeException("Could not locate PostProcessor in this JVM or at message entity " + getMessageEntity() + ": " + pname);
        }
    	if (postProcessor instanceof PostProcessorRemote) {
            postProcessor = new PostProcessorRemoteAdapter((PostProcessorRemote)postProcessor);
        }

    	return (PostProcessor)postProcessor;
    }

    public ObjectDefinition getObjectDefinition(String objectName) {
    	return new ObjectDefinition(objectName, getMessageEntity());
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
        if (!activeInd.booleanValue()) {
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
        if (parentDocType.getActiveInd() == null || parentDocType.getActiveInd().booleanValue()) {
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

    public boolean isSuperUser(WorkflowUser user) {
	Workgroup workgroup = getSuperUserWorkgroup();
	if (workgroup == null) {
	    return false;
	}
    	return workgroup.hasMember(user);
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
                          + ", activeInd=" + activeInd
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
     * Returns the message entity for this DocumentType which can be specified on the document type itself,
     * inherited from the parent, or defaults to the configured message entity of the application.
     */
	public String getMessageEntity() {
		if (this.messageEntity != null) {
			return messageEntity;
		}
		String returnVal = null;
		if (getParentDocType() != null) {
			returnVal = getParentDocType().getMessageEntity();
		}
		if (returnVal == null) {
//			returnVal = "KEW";
			returnVal = Core.getCurrentContextConfig().getMessageEntity();
		}
		return returnVal;
	}

	/**
	 * Returns the actual specified message entity for this document type which could be null.
	 */
	public String getActualMessageEntity() {
		return messageEntity;
	}

	public void setMessageEntity(String messageEntity) {
		this.messageEntity = messageEntity;
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
}