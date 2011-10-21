/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kew.doctype.bo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.core.api.config.CoreConfigHelper;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceRemoteServiceConnectionException;
import org.kuali.rice.core.api.reflect.ObjectDefinition;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.kew.actionlist.CustomActionListAttribute;
import org.kuali.rice.kew.api.KEWPropertyConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.doctype.DocumentTypeContract;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.doctype.ApplicationDocumentStatus;
import org.kuali.rice.kew.doctype.DocumentTypeAttribute;
import org.kuali.rice.kew.doctype.DocumentTypePolicy;
import org.kuali.rice.kew.doctype.DocumentTypePolicyEnum;
import org.kuali.rice.kew.doctype.DocumentTypeSecurity;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.engine.node.ProcessDefinitionBo;
import org.kuali.rice.kew.exception.ResourceUnavailableException;
import org.kuali.rice.kew.framework.document.attribute.SearchableAttribute;
import org.kuali.rice.kew.impl.document.lookup.DocumentLookupGenerator;
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
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Model bean mapped to ojb representing a document type.  Provides component lookup behavior that
 * can construct {@link ObjectDefinition} objects correctly to account for application id inheritance.
 * Can also navigate parent hierarchy when getting data/components.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
//@Sequence(name="KREW_DOC_HDR_S", property="documentTypeId")
@Table(name = "KREW_DOC_TYP_T")
@NamedQueries({
        @NamedQuery(name = "DocumentType.QuickLinks.FindLabelByTypeName", query = "SELECT label FROM DocumentType WHERE name = :docTypeName AND currentInd = 1"),
        @NamedQuery(name = "DocumentType.QuickLinks.FindInitiatedDocumentTypesListByInitiatorWorkflowId", query = "SELECT DISTINCT dt.name, dt.label FROM DocumentType dt, DocumentRouteHeaderValue drhv " +
                "WHERE drhv.initiatorWorkflowId = :initiatorWorkflowId AND drhv.documentTypeId = dt.documentTypeId AND dt.active = 1 AND dt.currentInd = 1 " +
                "ORDER BY UPPER(dt.label)")
})
public class DocumentType extends PersistableBusinessObjectBase implements MutableInactivatable, DocumentTypeEBO, DocumentTypeContract {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentType.class);

    private static final long serialVersionUID = 1312830153583125069L;

    @Id
    @GeneratedValue(generator = "KREW_DOC_HDR_S")
    @GenericGenerator(name = "KREW_DOC_HDR_S", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "KREW_DOC_HDR_S"),
            @Parameter(name = "value_column", value = "id")
    })
    @Column(name = "DOC_TYP_ID")
    private String documentTypeId;
    @Column(name = "PARNT_ID")
    private String docTypeParentId;
    @Column(name = "DOC_TYP_NM")
    private String name;
    @Column(name = "DOC_TYP_VER_NBR")
    private Integer version = new Integer(0);
    @Column(name = "ACTV_IND")
    private Boolean active;
    @Column(name = "CUR_IND")
    private Boolean currentInd;
    @Column(name = "DOC_TYP_DESC")
    private String description;
    @Column(name = "LBL")
    private String label;
    @Column(name = "PREV_DOC_TYP_VER_NBR")
    private String previousVersionId;
    /**
     * The id of the document which caused the last modification of this document type.
     * Null if this doc type was never modified via a document routing (UI).
     */
    @Column(name = "DOC_HDR_ID")
    private String documentId;

    @Column(name = "HELP_DEF_URL")
    private String unresolvedHelpDefinitionUrl;

    @Column(name = "DOC_SEARCH_HELP_URL")
    private String unresolvedDocSearchHelpUrl;

    @Column(name = "DOC_HDLR_URL")
    private String unresolvedDocHandlerUrl;
    @Column(name = "POST_PRCSR")
    private String postProcessorName;
    @Column(name = "GRP_ID")
    //private Long superUserWorkgroupId;
    private String workgroupId;
    @Column(name = "BLNKT_APPR_GRP_ID")
    private String blanketApproveWorkgroupId;
    @Column(name = "BLNKT_APPR_PLCY")
    private String blanketApprovePolicy;
    @Column(name = "RPT_GRP_ID")
    private String reportingWorkgroupId;
    @Column(name = "APPL_ID")
    private String actualApplicationId;


    /* these two fields are for the web tier lookupable
     * DocumentType is doing double-duty as a web/business tier object
     */
    @Transient
    private String returnUrl;
    @Transient
    private String actionsUrl;
    @Transient
    private Boolean applyRetroactively = Boolean.FALSE;

    /* The default exception workgroup to apply to nodes that lack an exception workgroup definition.
     * Used at parse-time only; not stored in db.
     */
    @Transient
    private Group defaultExceptionWorkgroup;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "documentType")
    @Fetch(value = FetchMode.SELECT)
    private Collection<DocumentTypePolicy> documentTypePolicies;

    /* This property contains the list of valid ApplicationDocumentStatus values, 
    * if defined, for the document type.  If these status values are defined, only these
    * values may be assigned as the status.  If not valid values are defined, the status may
    * be set to any value by the client.
    */
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "documentType")
    @Fetch(value = FetchMode.SELECT)
    private List<ApplicationDocumentStatus> validApplicationStatuses;

    @Transient
    private List routeLevels;
    @Transient
    private Collection childrenDocTypes;
    @Fetch(value = FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "documentType")
    @OrderBy("orderIndex ASC")
    private List<DocumentTypeAttribute> documentTypeAttributes;

    /* New Workflow 2.1 Field */
    @Fetch(value = FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "documentType")
    private List<ProcessDefinitionBo> processes = new ArrayList();
    @Column(name = "RTE_VER_NBR")
    private String routingVersion = KEWConstants.CURRENT_ROUTING_VERSION;

    /* Workflow 2.2 Fields */
    @Column(name = "NOTIFY_ADDR")
    private String actualNotificationFromAddress;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "SEC_XML")
    private String documentTypeSecurityXml;
    @Transient
    private DocumentTypeSecurity documentTypeSecurity;

    /* Workflow 2.4 XSLT-based email message customization */
    @Column(name = "EMAIL_XSL")
    private String customEmailStylesheet;

    public DocumentType() {
        routeLevels = new ArrayList();
        documentTypeAttributes = new ArrayList<DocumentTypeAttribute>();
        documentTypePolicies = new ArrayList<DocumentTypePolicy>();
        version = new Integer(0);
        label = null;
    }

    public void populateDataDictionaryEditableFields(Set<String> propertyNamesEditableViaUI, DocumentType dataDictionaryEditedType) {
        String currentPropertyName = "";
        try {
            for (String propertyName : propertyNamesEditableViaUI) {
                currentPropertyName = propertyName;
                if (KEWPropertyConstants.PARENT_DOC_TYPE_NAME.equals(propertyName)) {
                    // this is trying to set the parent document type name so lets set the entire parent document
                    String parentDocumentTypeName = (String) ObjectUtils.getPropertyValue(dataDictionaryEditedType, propertyName);
                    if (StringUtils.isNotBlank(parentDocumentTypeName)) {
                        DocumentType parentDocType = KEWServiceLocator.getDocumentTypeService().findByName(parentDocumentTypeName);
                        if (ObjectUtils.isNull(parentDocType)) {
                            throw new WorkflowRuntimeException("Could not find valid document type for document type name '" + parentDocumentTypeName + "' to set as Parent Document Type");
                        }
                        setDocTypeParentId(parentDocType.getDocumentTypeId());
                    }
                }
//                else if (!FIELD_PROPERTY_NAME_DOCUMENT_TYPE_ID.equals(propertyName)) {
                else {
                    LOG.info("*** COPYING PROPERTY NAME FROM OLD BO TO NEW BO: " + propertyName);
                    ObjectUtils.setObjectProperty(this, propertyName, ObjectUtils.getPropertyValue(dataDictionaryEditedType, propertyName));
                }
            }
        } catch (FormatException e) {
            throw new WorkflowRuntimeException("Error setting property '" + currentPropertyName + "' in Document Type", e);
        } catch (IllegalAccessException e) {
            throw new WorkflowRuntimeException("Error setting property '" + currentPropertyName + "' in Document Type", e);
        } catch (InvocationTargetException e) {
            throw new WorkflowRuntimeException("Error setting property '" + currentPropertyName + "' in Document Type", e);
        } catch (NoSuchMethodException e) {
            throw new WorkflowRuntimeException("Error setting property '" + currentPropertyName + "' in Document Type", e);
        } catch (Exception e) {
            throw new WorkflowRuntimeException("Error setting property '" + currentPropertyName + "' in Document Type", e);
        }
    }

    public DocumentTypePolicy getAllowUnrequestedActionPolicy() {
        return getPolicyByName(DocumentTypePolicyEnum.ALLOW_UNREQUESTED_ACTION.getName(), Boolean.TRUE);
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

    /**
     * This method returns a DocumentTypePolicy object related to the DocumentStatusPolicy defined for this document type.
     */
    public DocumentTypePolicy getDocumentStatusPolicy() {
        return getPolicyByName(DocumentTypePolicyEnum.DOCUMENT_STATUS_POLICY.getName(), KEWConstants.DOCUMENT_STATUS_POLICY_KEW_STATUS);
    }

    /**
     * This method returns a DocumentTypePolicy object related to the DocumentStatusPolicy defined for this document type.
     */
    public DocumentTypePolicy getSuPostprocessorOverridePolicy() {
        return getPolicyByName(DocumentTypePolicyEnum.ALLOW_SU_POSTPROCESSOR_OVERRIDE_POLICY.getName(), Boolean.TRUE);
    }
    
    public DocumentTypePolicy getRegenerateActionRequestsOnChange() {
    	return getPolicyByName(DocumentTypePolicyEnum.REGENERATE_ACTION_REQUESTS_ON_CHANGE.getName(), Boolean.TRUE);
    }

    /**
     * This method returns a boolean denoting whether the KEW Route Status is to be displayed.
     * The KEW Route Status is updated by the workflow engine regardless of whether it is to be displayed or not.
     *
     * @return true  - if the status is to be displayed  (Policy is set to either use KEW (default) or both)
     *         false - if the KEW Route Status is not to be displayed
     */
    public Boolean isKEWStatusInUse() {
        if (isPolicyDefined(DocumentTypePolicyEnum.DOCUMENT_STATUS_POLICY)) {
            String policyValue = getPolicyByName(DocumentTypePolicyEnum.DOCUMENT_STATUS_POLICY.getName(), KEWConstants.DOCUMENT_STATUS_POLICY_KEW_STATUS).getPolicyStringValue();
            return (policyValue == null || "".equals(policyValue)
                    || KEWConstants.DOCUMENT_STATUS_POLICY_KEW_STATUS.equalsIgnoreCase(policyValue)
                    || KEWConstants.DOCUMENT_STATUS_POLICY_BOTH.equalsIgnoreCase(policyValue)) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    /**
     * This method returns a boolean denoting whether the Application Document Status is to be used for this document type.
     *
     * @return true  - if the status is to be displayed  (Policy is set to either use the application document status or both)
     *         false - if only the KEW Route Status is to be displayed (default)
     */
    public Boolean isAppDocStatusInUse() {
        if (isPolicyDefined(DocumentTypePolicyEnum.DOCUMENT_STATUS_POLICY)) {
            String policyValue = getPolicyByName(DocumentTypePolicyEnum.DOCUMENT_STATUS_POLICY.getName(), KEWConstants.DOCUMENT_STATUS_POLICY_KEW_STATUS).getPolicyStringValue();
            return (KEWConstants.DOCUMENT_STATUS_POLICY_APP_DOC_STATUS.equalsIgnoreCase(policyValue)
                    || KEWConstants.DOCUMENT_STATUS_POLICY_BOTH.equalsIgnoreCase(policyValue)) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * This method returns a boolean denoting if both the KEW Route Status and the Application Document Status
     * are to be used in displays.
     *
     * @return true  - if both the KEW Route Status and Application Document Status are to be displayed.
     *         false - if only one status is to be displayed.
     */
    public Boolean areBothStatusesInUse() {
        if (isPolicyDefined(DocumentTypePolicyEnum.DOCUMENT_STATUS_POLICY)) {
            String policyValue = getPolicyByName(DocumentTypePolicyEnum.DOCUMENT_STATUS_POLICY.getName(), KEWConstants.DOCUMENT_STATUS_POLICY_KEW_STATUS).getPolicyStringValue();
            return (KEWConstants.DOCUMENT_STATUS_POLICY_BOTH.equalsIgnoreCase(policyValue)) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            return Boolean.FALSE;
        }
    }

    public String getUseWorkflowSuperUserDocHandlerUrlValue() {
        if (getUseWorkflowSuperUserDocHandlerUrl() != null) {
            return getUseWorkflowSuperUserDocHandlerUrl().getPolicyDisplayValue();
        }
        return null;
    }

    public String getAllowUnrequestedActionPolicyDisplayValue() {
        if (getAllowUnrequestedActionPolicy() != null) {
            return getAllowUnrequestedActionPolicy().getPolicyDisplayValue();
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

    public boolean isPolicyDefined(DocumentTypePolicyEnum policyToCheck) {
        Iterator<DocumentTypePolicy> policyIter = getDocumentTypePolicies().iterator();
        while (policyIter.hasNext()) {
            DocumentTypePolicy policy = policyIter.next();
            if (policyToCheck.getName().equals(policy.getPolicyName())) {
                return true;
            }
        }
        return getParentDocType() != null && getParentDocType().isPolicyDefined(policyToCheck);
    }

    public List<DocumentTypeAttribute> getDocumentTypeAttributes(String... attributeTypes) {
        List<DocumentTypeAttribute> filteredAttributes = new ArrayList<DocumentTypeAttribute>();
        if (CollectionUtils.isNotEmpty(documentTypeAttributes)) {
            if (attributeTypes == null) {
                filteredAttributes.addAll(documentTypeAttributes);
            } else {
                List<String> attributeTypeList = Arrays.asList(attributeTypes);
                for (DocumentTypeAttribute documentTypeAttribute : documentTypeAttributes) {
                    RuleAttribute ruleAttribute = documentTypeAttribute.getRuleAttribute();
                    if (attributeTypeList.contains(ruleAttribute.getType())) {
                        filteredAttributes.add(documentTypeAttribute);
                    }
                }
            }
        }
        if (filteredAttributes.isEmpty() && getParentDocType() != null) {
            return getParentDocType().getDocumentTypeAttributes(attributeTypes);
        }
        return Collections.unmodifiableList(filteredAttributes);
    }

    public boolean hasSearchableAttributes() {
        return !getSearchableAttributes().isEmpty();
    }

    public List<DocumentTypeAttribute> getSearchableAttributes() {
        return getDocumentTypeAttributes(KEWConstants.SEARCHABLE_ATTRIBUTE_TYPE, KEWConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE);
    }

    public DocumentTypeAttribute getCustomizerAttribute() {
        List<DocumentTypeAttribute> documentTypeAttributes = getDocumentTypeAttributes(KEWConstants.DOCUMENT_LOOKUP_CUSTOMIZER_ATTRIBUTE_TYPE);
        if (documentTypeAttributes.size() > 1) {
            throw new IllegalStateException("Encountered more than one DocumentLookupCustomizer attribute on this document type: " + getName());
        }
        if (documentTypeAttributes.isEmpty()) {
            return null;
        }
        return documentTypeAttributes.get(0);
    }

    public List<ExtensionHolder<SearchableAttribute>> loadSearchableAttributes() {
        List<DocumentTypeAttribute> searchableAttributes = getSearchableAttributes();
        List<ExtensionHolder<SearchableAttribute>> loadedAttributes = new ArrayList<ExtensionHolder<SearchableAttribute>>();
        for (DocumentTypeAttribute documentTypeAttribute : searchableAttributes) {
            RuleAttribute ruleAttribute = documentTypeAttribute.getRuleAttribute();
            try {
                Object attributeService = KEWServiceLocator.getRuleAttributeService().loadRuleAttributeService(ruleAttribute, getApplicationId());
                if (attributeService == null) {
                    throw new WorkflowRuntimeException("Failed to locate searchable attribute, attribute did not exist: " + ruleAttribute);
                }
                if (!(attributeService instanceof SearchableAttribute)) {
                    throw new WorkflowRuntimeException("Service for given attribute was found, but it does not implement SearchableAttribute: " + attributeService);
                }
                ExtensionDefinition extensionDefinition = KewApiServiceLocator.getExtensionRepositoryService().getExtensionById(ruleAttribute.getId());
                loadedAttributes.add(new ExtensionHolder<SearchableAttribute>(ruleAttribute, extensionDefinition, (SearchableAttribute)attributeService));
            } catch (RiceRemoteServiceConnectionException e) {
                LOG.warn("Unable to connect to load searchable attribute for " + ruleAttribute, e);
            }
        }
        return loadedAttributes;
    }

    public static final class ExtensionHolder<T> {

        private final RuleAttribute ruleAttribute;
        private final ExtensionDefinition extensionDefinition;
        private final T extension;

        public ExtensionHolder(RuleAttribute ruleAttribute, ExtensionDefinition extensionDefinition, T extension) {
            this.ruleAttribute = ruleAttribute;
            this.extensionDefinition = extensionDefinition;
            this.extension = extension;
        }

        public RuleAttribute getRuleAttribute() {
            return ruleAttribute;
        }

        public ExtensionDefinition getExtensionDefinition() {
            return extensionDefinition;
        }

        public T getExtension() {
            return extension;
        }
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
            this.childrenDocTypes = KEWServiceLocator.getDocumentTypeService().getChildDocumentTypes(getDocumentTypeId());
        }
        return childrenDocTypes;
    }

    public String getDocTypeParentId() {
        return docTypeParentId;
    }

    public void setDocTypeParentId(String docTypeParentId) {
        this.docTypeParentId = docTypeParentId;
    }

    public DocumentType getParentDocType() {
        return KEWServiceLocator.getDocumentTypeService().findById(this.docTypeParentId);
    }

    public Collection<DocumentTypePolicy> getDocumentTypePolicies() {
        return documentTypePolicies;
    }

    public void setDocumentTypePolicies(Collection<DocumentTypePolicy> policies) {
        this.documentTypePolicies = policies;
    }
    
    @Override
    public Map<org.kuali.rice.kew.api.doctype.DocumentTypePolicy, String> getPolicies() {
        Map<org.kuali.rice.kew.api.doctype.DocumentTypePolicy, String> policies = new HashMap<org.kuali.rice.kew.api.doctype.DocumentTypePolicy, String>();
        if (this.documentTypePolicies != null) {
            for (DocumentTypePolicy policy : this.documentTypePolicies) {
                policies.put(org.kuali.rice.kew.api.doctype.DocumentTypePolicy.fromCode(policy.getPolicyName()), policy.getPolicyValue().toString());
            }
        }
        return policies;
    }

    public List<ApplicationDocumentStatus> getValidApplicationStatuses() {
        return this.validApplicationStatuses;
    }

    public void setValidApplicationStatuses(
            List<ApplicationDocumentStatus> validApplicationStatuses) {
        this.validApplicationStatuses = validApplicationStatuses;
    }

    public String getDocumentTypeSecurityXml() {
        return documentTypeSecurityXml;
    }

    public void setDocumentTypeSecurityXml(String documentTypeSecurityXml) {
        this.documentTypeSecurityXml = documentTypeSecurityXml;
        if (!org.apache.commons.lang.StringUtils.isEmpty(documentTypeSecurityXml.trim())) {
            this.documentTypeSecurity = new DocumentTypeSecurity(this.getApplicationId(), documentTypeSecurityXml);
        } else {
            this.documentTypeSecurity = null;
        }
    }

    public DocumentTypeSecurity getDocumentTypeSecurity() {
        if (this.documentTypeSecurity == null &&
                this.documentTypeSecurityXml != null &&
                !org.apache.commons.lang.StringUtils.isEmpty(documentTypeSecurityXml.trim())) {
            this.documentTypeSecurity = new DocumentTypeSecurity(this.getApplicationId(), documentTypeSecurityXml);
        }
        if ((this.documentTypeSecurity == null) && (getParentDocType() != null)) {
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
    
    @Override
    public boolean isCurrent() {
        if (currentInd == null) {
            return true;
        }
        return currentInd.booleanValue();
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

    /**
     * This method gets the document handler url from this object or from a parent document type and resolves any
     * potential variables that may be in use
     */
    public String getDocHandlerUrl() {
        return resolveDocHandlerUrl(getUnresolvedInheritedDocHandlerUrl(false));
    }

    /**
     * This method retrieves the unresolved document handler URL either from this object or from a parent document type
     * object. If the forDisplayPurposes value is true the value returned will be invalid for system use.
     * <p/>
     * This method will first call the {@link #getUnresolvedDocHandlerUrl()} method to check for a value on this object.
     * If none is found a parent document type must exist because the document handler URL is required and is used. The
     * system will use inheritance to find the document handler url from a document type somewhere in the hierarchy.
     *
     * @param forDisplayPurposes - if true then the string returned will have a label explaining where the value came from
     * @return the unresolved document handler URL value or a displayable value with sourcing information
     */
    protected String getUnresolvedInheritedDocHandlerUrl(boolean forDisplayPurposes) {
        if (StringUtils.isNotBlank(getUnresolvedDocHandlerUrl())) {
            // this object has a direct value set, so return it
            return getUnresolvedDocHandlerUrl();
        }
        // check for a parent document to see if the doc handler url can be inherited
        DocumentType docType = getParentDocType();
        if (ObjectUtils.isNotNull(docType)) {
            String parentValue = docType.getUnresolvedDocHandlerUrl();
            if (StringUtils.isNotBlank(parentValue)) {
                // found a parent value set on the immediate parent object so return it
                if (forDisplayPurposes) {
                    parentValue += " " + KEWConstants.DOCUMENT_TYPE_INHERITED_VALUE_INDICATOR;
                }
                return parentValue;
            }
            // no valid value exists on the immediate parent, so check the hierarchy
            return docType.getUnresolvedInheritedDocHandlerUrl(forDisplayPurposes);
        }
        return null;
    }

    /**
     * Returns the same value as the {@link #getUnresolvedInheritedDocHandlerUrl(boolean)} method but will also have label
     * information about whether the value returned came from this object or the parent document type associated with this object
     */
    public String getDisplayableUnresolvedDocHandlerUrl() {
        return getUnresolvedInheritedDocHandlerUrl(true);
    }

    /**
     * EMPTY METHOD. Use {@link #setUnresolvedDocHandlerUrl(String)} instead.
     *
     * @deprecated
     */
    public void setDisplayableUnresolvedDocHandlerUrl(String displayableUnresolvedDocHandlerUrl) {
        // do nothing
    }

    /**
     * @return the unresolvedDocHandlerUrl
     */
    public String getUnresolvedDocHandlerUrl() {
        return this.unresolvedDocHandlerUrl;
    }

    /**
     * @param unresolvedDocHandlerUrl the unresolvedDocHandlerUrl to set
     */
    public void setUnresolvedDocHandlerUrl(String unresolvedDocHandlerUrl) {
        this.unresolvedDocHandlerUrl = unresolvedDocHandlerUrl;
    }

    /**
     * If the doc handler URL has variables in it that need to be replaced, this will look up the values
     * for those variables and replace them in the doc handler URL.
     */
    protected String resolveDocHandlerUrl(String docHandlerUrl) {
        if (StringUtils.isBlank(docHandlerUrl)) {
            return "";
        }
        return Utilities.substituteConfigParameters(getApplicationId(), docHandlerUrl);
    }

    /**
     * Use {@link #setDocHandlerUrl(String)} to add a document handler url to this object.
     *
     * @deprecated
     */
    public void setDocHandlerUrl(java.lang.String docHandlerUrl) {
        setUnresolvedDocHandlerUrl(docHandlerUrl);
    }

    /**
     * @return the unresolvedHelpDefinitionUrl
     */
    public String getUnresolvedHelpDefinitionUrl() {
        return this.unresolvedHelpDefinitionUrl;
    }

    /**
     * @param unresolvedHelpDefinitionUrl the unresolvedHelpDefinitionUrl to set
     */
    public void setUnresolvedHelpDefinitionUrl(String unresolvedHelpDefinitionUrl) {
        this.unresolvedHelpDefinitionUrl = unresolvedHelpDefinitionUrl;
    }

    /**
     * This method gets the help definition url from this object and resolves any
     * potential variables that may be in use
     */
    public String getHelpDefinitionUrl() {
        return resolveHelpUrl(getUnresolvedHelpDefinitionUrl());
    }

    /**
     * If a help URL has variables in it that need to be replaced, this will look up the values
     * for those variables and replace them.
     */
    protected String resolveHelpUrl(String helpDefinitionUrl) {
        if (StringUtils.isBlank(helpDefinitionUrl)) {
            return "";
        }
        return Utilities.substituteConfigParameters(helpDefinitionUrl);
    }

    /**
     * @return the unresolvedDocSearchHelpUrl
     */
    public String getUnresolvedDocSearchHelpUrl() {
        return this.unresolvedDocSearchHelpUrl;
    }

    /**
     * @param unresolvedDocSearchHelpUrl the unresolvedDocSearchHelpUrl to set
     */
    public void setUnresolvedDocSearchHelpUrl(String unresolvedDocSearchHelpUrl) {
        this.unresolvedDocSearchHelpUrl = unresolvedDocSearchHelpUrl;
    }

    /**
     * This method gets the doc search help url from this object and resolves any
     * potential variables that may be in use
     */
    public String getDocSearchHelpUrl() {
        return resolveHelpUrl(getUnresolvedDocSearchHelpUrl());
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

    public PostProcessor getPostProcessor() {
        String pname = getPostProcessorName();

        if (StringUtils.equals(pname, KEWConstants.POST_PROCESSOR_NON_DEFINED_VALUE)) {
            return new DefaultPostProcessor();
        }
        if (StringUtils.isBlank(pname)) {
            if (getParentDocType() != null) {
                return getParentDocType().getPostProcessor();
            } else {
                return new DefaultPostProcessor();
            }
        }

        ObjectDefinition objDef = getObjectDefinition(pname);
        Object postProcessor = GlobalResourceLoader.getObject(objDef);

        if (postProcessor == null) {
            throw new WorkflowRuntimeException("Could not locate PostProcessor in this JVM or at application id " + getApplicationId() + ": " + pname);
        }
        if (postProcessor instanceof PostProcessorRemote) {
            postProcessor = new PostProcessorRemoteAdapter((PostProcessorRemote) postProcessor);
        }

        return (PostProcessor) postProcessor;
    }

    /**
     * This method gets the post processor class value. If the forDisplayPurposes value is true
     * the value will be invalid for system use.
     * <p/>
     * This method will first call the {@link #getPostProcessorName()} method to check the value on this object.
     * If none is found the system checks for a parent document type.  If a valid parent type exists for this document type
     * then the system will use inheritance from that parent document type as long as at least one document type in the
     * hierarchy has a value set.  If no value is set on any parent document type or if no parent document type exists the
     * system will return null.
     *
     * @param forDisplayPurposes - if true then the string returned will have a label explaining where the value came from
     * @return the post processor class value or a displayable value with sourcing information
     */
    protected String getInheritedPostProcessorName(boolean forDisplayPurposes) {
        if (StringUtils.isNotBlank(getPostProcessorName())) {
            // this object has a post processor class so return it
            return getPostProcessorName();
        }
        if (ObjectUtils.isNotNull(getParentDocType())) {
            // direct parent document type exists
            String parentValue = getParentDocType().getPostProcessorName();
            if (StringUtils.isNotBlank(parentValue)) {
                // found a post processor class set on the immediate parent object so return it
                if (forDisplayPurposes) {
                    parentValue += " " + KEWConstants.DOCUMENT_TYPE_INHERITED_VALUE_INDICATOR;
                }
                return parentValue;
            }
            // did not find a valid value on the immediate parent, so use hierarchy
            return getParentDocType().getInheritedPostProcessorName(forDisplayPurposes);
        }
        return null;
    }

    public java.lang.String getPostProcessorName() {
        return postProcessorName;
    }

    public void setPostProcessorName(java.lang.String postProcessorName) {
        this.postProcessorName = postProcessorName;
    }

    public String getDisplayablePostProcessorName() {
        return getInheritedPostProcessorName(true);
    }

    /**
     * EMPTY METHOD. Use {@link #setPostProcessorName(String)} instead.
     *
     * @deprecated
     */
    public void setDisplayablePostProcessorName(String displayablePostProcessorName) {
        // do nothing
    }

    public String getPreviousVersionId() {
        return previousVersionId;
    }

    public void setPreviousVersionId(String previousVersionId) {
        this.previousVersionId = previousVersionId;
    }

    public java.lang.String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(java.lang.String documentId) {
        this.documentId = documentId;
    }

    public java.lang.Integer getVersion() {
        return version;
    }

    public void setVersion(java.lang.Integer version) {
        this.version = version;
    }

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String docTypeGrpId) {
        this.documentTypeId = docTypeGrpId;
    }
    
    @Override
    public String getId() {
        return getDocumentTypeId();
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

        Iterator policyIter = getDocumentTypePolicies().iterator();
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

    private DocumentTypePolicy getPolicyByName(String policyName, String defaultValue) {

        Iterator policyIter = getDocumentTypePolicies().iterator();
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
        policy.setPolicyValue(Boolean.TRUE);
        policy.setPolicyStringValue(defaultValue);
        return policy;
    }

    private DocumentTypeService getDocumentTypeService() {
        return (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
    }

    public Group getSuperUserWorkgroup() {
        Group superUserWorkgroup = getSuperUserWorkgroupNoInheritence();
        if (superUserWorkgroup == null && getParentDocType() != null) {
            return getParentDocType().getSuperUserWorkgroup();
        }
        return superUserWorkgroup;
    }

    public Group getSuperUserWorkgroupNoInheritence() {
        if (workgroupId == null) {
            return null;
        }
        return getGroupService().getGroup(this.workgroupId);
    }

    public void setSuperUserWorkgroupNoInheritence(Group suWorkgroup) {
        this.workgroupId = null;
        if (ObjectUtils.isNotNull(suWorkgroup)) {
            this.workgroupId = suWorkgroup.getId();
        }
    }

    /**
     * Set the immediate super user workgroup id field
     * @param suWorkgroupId the super user workgroup id
     */
    public void setSuperUserWorkgroupIdNoInheritence(String suWorkgroupId) {
        this.workgroupId = suWorkgroupId;
    }

    /**
     * Returns true if this DocumentType has a super user group defined.
     */
    public boolean isSuperUserGroupDefined() {
        if (this.workgroupId == null) {
            return getParentDocType() != null && getParentDocType().isSuperUserGroupDefined();
        }
        return true;
    }

    public DocumentType getPreviousVersion() {
        return getDocumentTypeService().findById(previousVersionId);
    }

    public Group getBlanketApproveWorkgroup() {
        if (StringUtils.isBlank(blanketApproveWorkgroupId)) {
            return null;
        }
        return getGroupService().getGroup(blanketApproveWorkgroupId);
    }

    public void setBlanketApproveWorkgroup(Group blanketApproveWorkgroup) {
        this.blanketApproveWorkgroupId = null;
        if (ObjectUtils.isNotNull(blanketApproveWorkgroup)) {
            this.blanketApproveWorkgroupId = blanketApproveWorkgroup.getId();
        }
    }

    public String getBlanketApprovePolicy() {
        return this.blanketApprovePolicy;
    }

    public void setBlanketApprovePolicy(String blanketApprovePolicy) {
        this.blanketApprovePolicy = blanketApprovePolicy;
    }

    public Group getBlanketApproveWorkgroupWithInheritance() {
        if (getParentDocType() != null && this.blanketApproveWorkgroupId == null) {
            return getParentDocType().getBlanketApproveWorkgroupWithInheritance();
        }
        return getGroupService().getGroup(blanketApproveWorkgroupId);
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
            return getGroupService().isMemberOfGroup(principalId, blanketApproveWorkgroupId);
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
    public boolean isBlanketApproveGroupDefined() {
        if (StringUtils.isBlank(getBlanketApprovePolicy()) && this.blanketApproveWorkgroupId == null) {
            return getParentDocType() != null && getParentDocType().isBlanketApproveGroupDefined();
        }
        return true;
    }

    /**
     * @return the reportingWorkgroupId
     */
    public String getReportingWorkgroupId() {
        return this.reportingWorkgroupId;
    }

    /**
     * @param reportingWorkgroupId the reportingWorkgroupId to set
     */
    public void setReportingWorkgroupId(String reportingWorkgroupId) {
        this.reportingWorkgroupId = reportingWorkgroupId;
    }

    public Group getReportingWorkgroup() {
    	if (StringUtils.isBlank(this.reportingWorkgroupId)) {
    		return null;
    	}
        return getGroupService().getGroup(this.reportingWorkgroupId);
    }

    public void setReportingWorkgroup(Group reportingWorkgroup) {
        this.reportingWorkgroupId = null;
        if (ObjectUtils.isNotNull(reportingWorkgroup)) {
            this.reportingWorkgroupId = reportingWorkgroup.getId();
        }
    }

    public Group getDefaultExceptionWorkgroup() {
        return defaultExceptionWorkgroup;
    }

    public void setDefaultExceptionWorkgroup(Group defaultExceptionWorkgroup) {
        this.defaultExceptionWorkgroup = defaultExceptionWorkgroup;
    }


    public DocumentLookupGenerator getDocumentSearchGenerator() {
        ObjectDefinition objDef = getAttributeObjectDefinition(KEWConstants.SEARCH_GENERATOR_ATTRIBUTE_TYPE);
        if (objDef == null) {
            if (getParentDocType() != null) {
                return getParentDocType().getDocumentSearchGenerator();
            } else {
                return KEWServiceLocator.getDocumentSearchService().getStandardDocumentSearchGenerator();
            }
        }

        Object searchGenerator;
        try {
            searchGenerator = GlobalResourceLoader.getObject(objDef);
        } catch (RiceRemoteServiceConnectionException e) {
            LOG.warn("Unable to connect to load searchGenerator for " + this.getName() + ".  Using DocumentLookupGeneratorImpl as default.");
            LOG.warn(e.getMessage());
            return KEWServiceLocator.getDocumentSearchService().getStandardDocumentSearchGenerator();
        }

        if (searchGenerator == null) {
            throw new WorkflowRuntimeException("Could not locate DocumentLookupGenerator in this JVM or at application id " + getApplicationId() + ": " + objDef.getClassName());
        }

        return (DocumentLookupGenerator) searchGenerator;
    }

    public DocumentTypeAttribute getDocumentSearchResultProcessorAttribute() {
        if (documentTypeAttributes != null) {
            for (DocumentTypeAttribute attribute : documentTypeAttributes) {
                RuleAttribute ruleAttribute = attribute.getRuleAttribute();
                if (KEWConstants.SEARCH_RESULT_PROCESSOR_ATTRIBUTE_TYPE.equals(ruleAttribute.getType()) ||
                        KEWConstants.SEARCH_RESULT_XML_PROCESSOR_ATTRIBUTE_TYPE.equals(ruleAttribute.getType())) {
                    return attribute;
                }
            }
        }
        if (getParentDocType() != null) {
            return getParentDocType().getDocumentSearchResultProcessorAttribute();
        }
        return null;
    }

    public CustomActionListAttribute getCustomActionListAttribute() throws ResourceUnavailableException {

        ObjectDefinition objDef = getAttributeObjectDefinition(KEWConstants.ACTION_LIST_ATTRIBUTE_TYPE);
        if (objDef == null) {
            return null;
        }
        try {
            return (CustomActionListAttribute) GlobalResourceLoader.getObject(objDef);
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
        return (CustomEmailAttribute) GlobalResourceLoader.getObject(objDef);
    }

    public ObjectDefinition getAttributeObjectDefinition(String typeCode) {
        for (Iterator iter = getDocumentTypeAttributes().iterator(); iter.hasNext();) {
            RuleAttribute attribute = ((DocumentTypeAttribute) iter.next()).getRuleAttribute();
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
        if (ruleAttribute.getApplicationId() == null) {
            return new ObjectDefinition(ruleAttribute.getResourceDescriptor(), this.getApplicationId());
        } else {
            return new ObjectDefinition(ruleAttribute.getResourceDescriptor(), ruleAttribute.getApplicationId());
        }
    }

    public CustomNoteAttribute getCustomNoteAttribute() throws ResourceUnavailableException {
        ObjectDefinition objDef = getAttributeObjectDefinition(KEWConstants.NOTE_ATTRIBUTE_TYPE);
        if (objDef == null) {
            String defaultNoteClass = ConfigContext.getCurrentContextConfig().getDefaultKewNoteClass();
            if (defaultNoteClass == null) {
                // attempt to use deprecated parameter
                defaultNoteClass = ConfigContext.getCurrentContextConfig().getDefaultKewNoteClass();
                if (ObjectUtils.isNull(defaultNoteClass)) {
                    return null;
                }
            }
            objDef = new ObjectDefinition(defaultNoteClass);
        }
        return (CustomNoteAttribute) GlobalResourceLoader.getObject(objDef);
    }

    public ObjectDefinition getObjectDefinition(String objectName) {
        return new ObjectDefinition(objectName, getApplicationId());
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

    public boolean isDocTypeActive() {
        if (!getActive().booleanValue()) {
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

    public void addProcess(ProcessDefinitionBo process) {
        processes.add(process);
    }

    /**
     * Gets the processes of this document by checking locally for processes, and if none are
     * present, retrieves them from it's parent document type.  The list returned is an immutable
     * list.  To add processes to a document type, use the addProcess method.
     * <p/>
     * NOTE: Since OJB uses direct field access, this will not interfere with the proper
     * mapping of the processes field.
     *
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

    public ProcessDefinitionBo getPrimaryProcess() {
        for (Iterator iterator = getProcesses().iterator(); iterator.hasNext();) {
            ProcessDefinitionBo process = (ProcessDefinitionBo) iterator.next();
            if (process.isInitial()) {
                return process;
            }
        }
        return null;
    }

    public ProcessDefinitionBo getNamedProcess(String name) {
        for (Iterator iterator = getProcesses().iterator(); iterator.hasNext();) {
            ProcessDefinitionBo process = (ProcessDefinitionBo) iterator.next();
            if (org.apache.commons.lang.ObjectUtils.equals(name, process.getName())) {
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

    /**
     * @return the actualNotificationFromAddress
     */
    public String getActualNotificationFromAddress() {
        return this.actualNotificationFromAddress;
    }

    /**
     * @param actualNotificationFromAddress the actualNotificationFromAddress to set
     */
    public void setActualNotificationFromAddress(String actualNotificationFromAddress) {
        this.actualNotificationFromAddress = actualNotificationFromAddress;
    }

    /**
     * Returns the same value as the {@link #getNotificationFromAddress()} method but will also have label information if
     * the value is inherited from a parent document type
     */
    public String getDisplayableNotificationFromAddress() {
        return getNotificationFromAddress(true);
    }

    /**
     * EMPTY METHOD. Use {@link #setActualNotificationFromAddress(String)} instead.
     *
     * @deprecated
     */
    public void setDisplayableNotificationFromAddress(String displayableNotificationFromAddress) {
        // do nothing
    }

    public String getNotificationFromAddress() {
        return getNotificationFromAddress(false);
    }

    /**
     * This method gets the notification from address value. If the forDisplayPurposes value is true
     * the notification from address value will be invalid for system use
     * <p/>
     * This method will first call the {@link #getActualNotificationFromAddress()} method to check the value on this object.
     * If none is found the system checks for a parent document type.  If a valid parent type exists for this document type
     * then the system will use inheritance from that parent document type as long as at least one document type in the
     * hierarchy has a value set.  If no value is set on any parent document type or if no parent document type exists the
     * system will return null
     *
     * @param forDisplayPurposes - if true then the string returned will have a label explaining where the value came from
     * @return the notification from address value or a displayable value with sourcing information
     */
    protected String getNotificationFromAddress(boolean forDisplayPurposes) {
        if (StringUtils.isNotBlank(getActualNotificationFromAddress())) {
            // this object has an address so return it
            return getActualNotificationFromAddress();
        }
        if (ObjectUtils.isNotNull(getParentDocType())) {
            // direct parent document type exists
            String parentNotificationFromAddress = getParentDocType().getActualNotificationFromAddress();
            if (StringUtils.isNotBlank(parentNotificationFromAddress)) {
                // found an address set on the immediate parent object so return it
                if (forDisplayPurposes) {
                    parentNotificationFromAddress += " " + KEWConstants.DOCUMENT_TYPE_INHERITED_VALUE_INDICATOR;
                }
                return parentNotificationFromAddress;
            }
            // did not find a valid address on the immediate parent to use hierarchy
            return getParentDocType().getNotificationFromAddress(forDisplayPurposes);
        }
        return null;
    }

    /**
     * Use {@link #setActualNotificationFromAddress(String)} instead
     *
     * @deprecated
     */
    public void setNotificationFromAddress(String notificationFromAddress) {
        setActualNotificationFromAddress(notificationFromAddress);
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
        Group workgroup = getSuperUserWorkgroup();
        if (workgroup == null) {
            return false;
        }
        return getGroupService().isMemberOfGroup(principalId, workgroup.getId());
    }

    public boolean hasPreviousVersion() {
        if (this.documentTypeId == null) {
            return false;
        }
        return !this.documentTypeId.equals(this.previousVersionId);
    }

    /**
     * @return the actual application id
     */
    public String getActualApplicationId() {
        return this.actualApplicationId;
    }

    /**
     * @param actualApplicationId the actualApplicationId to set
     */
    public void setActualApplicationId(String actualApplicationId) {
        this.actualApplicationId = actualApplicationId;
    }

    /**
     * Returns the application id for this DocumentType which can be specified on the document type itself,
     * inherited from the parent, or defaulted to the configured application id of the application.
     */
    public String getApplicationId() {
        return getApplicationId(false);
    }

    /**
     * This method gets the string for the application id value. If the forDisplayPurposes value is true
     * the application id value will be invalid for system use.
     * <p/>
     * This method will first call the {@link #getActualApplicationId()} method to check for a value on this object. If
     * none is found a parent document type is used.  If a valid parent type exists for this document type then the system
     * will use inheritance from that parent document type as long as at least one document type in the hierarchy has a
     * value set.  If no value is set on any parent document type or if no parent document type exists for this object the
     * system default is used: {@link CoreConfigHelper#getApplicationId()}
     *
     * @param forDisplayPurposes - if true then the string returned will have a label explaining where the value came from
     * @return the application id value or a displayable value with sourcing information
     */
    protected String getApplicationId(boolean forDisplayPurposes) {
        if (StringUtils.isNotBlank(getActualApplicationId())) {
            // this object has a application id set, so return it
            return getActualApplicationId();
        }
        // this object has no application id... check for a parent document type
        if (ObjectUtils.isNotNull(getParentDocType())) {
            // direct parent document type exists
            String parentValue = getParentDocType().getActualApplicationId();
            if (StringUtils.isNotBlank(parentValue)) {
                // found a parent value set on the immediate parent object so return it
                if (forDisplayPurposes) {
                    parentValue += " " + KEWConstants.DOCUMENT_TYPE_INHERITED_VALUE_INDICATOR;
                }
                return parentValue;
            }
            // no valid application id on direct parent, so use hierarchy to find correct value
            return getParentDocType().getApplicationId(forDisplayPurposes);
        }
        String defaultValue = CoreConfigHelper.getApplicationId();
        if (forDisplayPurposes) {
            defaultValue += " " + KEWConstants.DOCUMENT_TYPE_SYSTEM_DEFAULT_INDICATOR;
        }
        return defaultValue;
    }

    /**
     * Returns the same value as the {@link #getApplicationId()} method but will also have label information about
     * where the application id came from (ie: inherited from the parent document type)
     */
    public String getDisplayableApplicationId() {
        return getApplicationId(true);
    }

    /**
     * Gets the name of the custom email stylesheet to use to render email (if any has been set, null otherwise)
     *
     * @return name of the custom email stylesheet to use to render email (if any has been set, null otherwise)
     */
    public String getCustomEmailStylesheet() {
        return customEmailStylesheet;
    }

    /**
     * Sets the name of the custom email stylesheet to use to render email
     *
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
     * @return the applyRetroactively
     */
    public Boolean getApplyRetroactively() {
        return this.applyRetroactively;
    }

    /**
     * @param applyRetroactively the applyRetroactively to set
     */
    public void setApplyRetroactively(Boolean applyRetroactively) {
        this.applyRetroactively = applyRetroactively;
    }

    private GroupService getGroupService() {
        return KimApiServiceLocator.getGroupService();
    }

    /**
     * @see org.kuali.rice.krad.bo.MutableInactivatable#isActive()
     */
    public boolean isActive() {
        boolean bRet = false;

        if (active != null) {
            bRet = active.booleanValue();
        }

        return bRet;
    }

    /**
     * @see org.kuali.rice.krad.bo.MutableInactivatable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = Boolean.valueOf(active);
    }
    
    @Override
    public Integer getDocumentTypeVersion() {
        return version;
    }

    @Override
    public String getParentId() {
        return docTypeParentId;
    }

    @Override
    public String getBlanketApproveGroupId() {
        return blanketApproveWorkgroupId;
    }

    @Override
    public String getSuperUserGroupId() {
        return workgroupId;
    }

    public static org.kuali.rice.kew.api.doctype.DocumentType to(DocumentType documentTypeBo) {
        if (documentTypeBo == null) {
            return null;
        }
        org.kuali.rice.kew.api.doctype.DocumentType.Builder builder = org.kuali.rice.kew.api.doctype.DocumentType.Builder.create(documentTypeBo);
        builder.setDocHandlerUrl(documentTypeBo.getUnresolvedDocHandlerUrl());
        builder.setApplicationId(documentTypeBo.getActualApplicationId());
        return builder.build();
    }

    public static DocumentType from(org.kuali.rice.kew.api.doctype.DocumentTypeContract dt) {
        // DocumentType BO and DTO are not symmetric
        // set what fields we can
        DocumentType ebo = new DocumentType();
        //ebo.setActionsUrl();
        ebo.setDocumentTypeId(dt.getId());
        ebo.setActive(dt.isActive());
        ebo.setActualApplicationId(dt.getApplicationId());
        //ebo.setActualNotificationFromAddress();
        ebo.setBlanketApproveWorkgroupId(dt.getBlanketApproveGroupId());
        ebo.setCurrentInd(dt.isCurrent());
        ebo.setDescription(dt.getDescription());
        ebo.setVersionNumber(dt.getVersionNumber());
        ebo.setVersion(dt.getDocumentTypeVersion());
        ebo.setUnresolvedDocHandlerUrl(dt.getDocHandlerUrl());
        ebo.setUnresolvedDocSearchHelpUrl(dt.getDocSearchHelpUrl());
        ebo.setUnresolvedHelpDefinitionUrl(dt.getHelpDefinitionUrl());
        ebo.setLabel(dt.getLabel());
        ebo.setName(dt.getName());
        ebo.setDocTypeParentId(dt.getParentId());
        ebo.setPostProcessorName(dt.getPostProcessorName());
        ebo.setSuperUserWorkgroupIdNoInheritence(dt.getSuperUserGroupId());
        List<DocumentTypePolicy> policies = new ArrayList<DocumentTypePolicy>();
        if (dt.getPolicies() != null) {
            for (Map.Entry<org.kuali.rice.kew.api.doctype.DocumentTypePolicy, String> entry: dt.getPolicies().entrySet()) {
                // NOTE: The policy value is actually a boolean field stored to a Decimal(1) column (although the db column is named PLCY_NM)
                // I'm not sure what the string value should be but the BO is simply toString'ing the Boolean value
                // so I am assuming here that "true"/"false" are the acceptable values
                policies.add(new DocumentTypePolicy(entry.getKey().getCode(), "true".equals(entry.getValue())));
            }
        }
        ebo.setDocumentTypePolicies(policies);
        return ebo;
    }
}
