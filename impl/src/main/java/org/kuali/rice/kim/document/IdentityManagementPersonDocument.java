/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEmploymentInformationInfo;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleQualifier;
import org.kuali.rice.kim.bo.ui.PersonDocumentAddress;
import org.kuali.rice.kim.bo.ui.PersonDocumentAffiliation;
import org.kuali.rice.kim.bo.ui.PersonDocumentCitizenship;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmail;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmploymentInfo;
import org.kuali.rice.kim.bo.ui.PersonDocumentGroup;
import org.kuali.rice.kim.bo.ui.PersonDocumentName;
import org.kuali.rice.kim.bo.ui.PersonDocumentPhone;
import org.kuali.rice.kim.bo.ui.PersonDocumentPrivacy;
import org.kuali.rice.kim.bo.ui.PersonDocumentRole;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMemberQualifier;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This is a description of what this class does - shyu don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
@Entity
@Table(name="KRIM_PERSON_DOCUMENT_T")
public class IdentityManagementPersonDocument extends IdentityManagementKimDocument {

    protected static final long serialVersionUID = -534993712085516925L;
    // principal data
    
    @GeneratedValue(generator="KRIM_PRNCPL_ID_S")
	@GenericGenerator(name="KRIM_PRNCPL_ID_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KRIM_PRNCPL_ID_S"),
			@Parameter(name="value_column",value="id")
		})
	@Column(name="prncpl_id")
    protected String principalId;
    @Column(name="prncpl_nm")
    protected String principalName;
    @GeneratedValue(generator="KRIM_ENTITY_ID_S")
	@GenericGenerator(name="KRIM_ENTITY_ID_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KRIM_ENTITY_ID_S"),
			@Parameter(name="value_column",value="id")
		})
	@Column(name="entity_id")
    protected String entityId;
    @Type(type="rice-hash")
    @Column(name="password")
    protected String password;
    
    // ext id - now hard coded for "tax id" & "univ id"
    @Column(name="tax_id")
    protected String taxId = "";
    @Column(name="univ_id")
    protected String univId = "";
    // affiliation data
    @OneToMany(targetEntity=PersonDocumentAffiliation.class, fetch=FetchType.LAZY, cascade={CascadeType.ALL})
    @JoinColumn(name="FDOC_NBR",insertable=false,updatable=false)
    protected List<PersonDocumentAffiliation> affiliations;

    @Transient
    protected String campusCode = "";
    // external identifier data
    @Transient
    protected Map<String, String> externalIdentifiers = null;

    @Column(name="ACTV_IND")
	@Type(type="yes_no")
    protected boolean active;

    // citizenship
    @Transient
    protected List<PersonDocumentCitizenship> citizenships;
    // protected List<DocEmploymentInfo> employmentInformations;
    @OneToMany(targetEntity=PersonDocumentName.class, fetch=FetchType.LAZY, cascade={CascadeType.ALL})
    @JoinColumn(name="FDOC_NBR",insertable=false,updatable=false)
    protected List<PersonDocumentName> names;
    @OneToMany(targetEntity=PersonDocumentAddress.class, fetch=FetchType.LAZY, cascade={CascadeType.ALL})
    @JoinColumn(name="FDOC_NBR",insertable=false,updatable=false)
    protected List<PersonDocumentAddress> addrs;
    @OneToMany(targetEntity=PersonDocumentPhone.class, fetch=FetchType.LAZY, cascade={CascadeType.ALL})
    @JoinColumn(name="FDOC_NBR",insertable=false,updatable=false)
    protected List<PersonDocumentPhone> phones;
    @OneToMany(targetEntity=PersonDocumentEmail.class, fetch=FetchType.LAZY, cascade={CascadeType.ALL})
    @JoinColumn(name="FDOC_NBR",insertable=false,updatable=false)
    protected List<PersonDocumentEmail> emails;
    @OneToMany(targetEntity=PersonDocumentRole.class, fetch=FetchType.LAZY, cascade={CascadeType.ALL})
    @JoinColumn(name="FDOC_NBR",insertable=false,updatable=false)
    protected List<PersonDocumentGroup> groups;
    @OneToMany(targetEntity=PersonDocumentGroup.class, fetch=FetchType.LAZY, cascade={CascadeType.ALL})
    @JoinColumn(name="FDOC_NBR",insertable=false,updatable=false)
    protected List<PersonDocumentRole> roles;

    @ManyToOne(targetEntity=PersonDocumentPrivacy.class, fetch=FetchType.LAZY, cascade={CascadeType.ALL})
    @JoinColumn(name="FDOC_NBR",insertable=false,updatable=false)
    protected PersonDocumentPrivacy privacy;

    public IdentityManagementPersonDocument() {
        affiliations = new ArrayList<PersonDocumentAffiliation>();
        citizenships = new ArrayList<PersonDocumentCitizenship>();
        // employmentInformations = new ArrayList<DocEmploymentInfo>();
        names = new ArrayList<PersonDocumentName>();
        addrs = new ArrayList<PersonDocumentAddress>();
        phones = new ArrayList<PersonDocumentPhone>();
        emails = new ArrayList<PersonDocumentEmail>();
        groups = new ArrayList<PersonDocumentGroup>();
        roles = new ArrayList<PersonDocumentRole>();
        privacy = new PersonDocumentPrivacy();
        this.active = true;
        // privacy.setDocumentNumber(documentNumber);
    }

    public String getPrincipalId() {
        return this.principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getPrincipalName() {
        return this.principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getEntityId() {
        return this.entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public List<PersonDocumentAffiliation> getAffiliations() {
        return this.affiliations;
    }

    public void setAffiliations(List<PersonDocumentAffiliation> affiliations) {
        this.affiliations = affiliations;
    }

    public String getCampusCode() {
        return this.campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public Map<String, String> getExternalIdentifiers() {
        return this.externalIdentifiers;
    }

    public void setExternalIdentifiers(Map<String, String> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<PersonDocumentCitizenship> getCitizenships() {
        return this.citizenships;
    }

    public void setCitizenships(List<PersonDocumentCitizenship> citizenships) {
        this.citizenships = citizenships;
    }

    public List<PersonDocumentName> getNames() {
        return this.names;
    }

    public void setNames(List<PersonDocumentName> names) {
        this.names = names;
    }

    public List<PersonDocumentAddress> getAddrs() {
        return this.addrs;
    }

    public void setAddrs(List<PersonDocumentAddress> addrs) {
        this.addrs = addrs;
    }

    public List<PersonDocumentPhone> getPhones() {
        return this.phones;
    }

    public void setPhones(List<PersonDocumentPhone> phones) {
        this.phones = phones;
    }

    public List<PersonDocumentEmail> getEmails() {
        return this.emails;
    }

    public void setEmails(List<PersonDocumentEmail> emails) {
        this.emails = emails;
    }

    public void setGroups(List<PersonDocumentGroup> groups) {
        this.groups = groups;
    }

    public List<PersonDocumentRole> getRoles() {
        return this.roles;
    }

    public void setRoles(List<PersonDocumentRole> roles) {
        this.roles = roles;
    }

    public List<PersonDocumentGroup> getGroups() {
        return this.groups;
    }

    public String getTaxId() {
        return taxId = KimCommonUtils.decryptExternalIdentifier(taxId, KimConstants.PersonExternalIdentifierTypes.TAX);
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getUnivId() {
        return this.univId;
    }

    public void setUnivId(String univId) {
        this.univId = univId;
    }

    public PersonDocumentPrivacy getPrivacy() {
        return this.privacy;
    }

    public void setPrivacy(PersonDocumentPrivacy privacy) {
        this.privacy = privacy;
    }

    public void initializeDocumentForNewPerson() {
        if(StringUtils.isBlank(this.principalId)){
            this.principalId = getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_PRNCPL_ID_S).toString();
        }
        if(StringUtils.isBlank(this.entityId)){
            this.entityId = getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_ENTITY_ID_S).toString();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        List<PersonDocumentEmploymentInfo> empInfos = new ArrayList<PersonDocumentEmploymentInfo>();
        for (PersonDocumentAffiliation affiliation : getAffiliations()) {
            empInfos.addAll(affiliation.getEmpInfos());
        }

        managedLists.add(empInfos);
        managedLists.add(getAffiliations());
        managedLists.add(getCitizenships());
        managedLists.add(getPhones());
        managedLists.add(getAddrs());
        managedLists.add(getEmails());
        managedLists.add(getNames());
        managedLists.add(getGroups());
        managedLists.add(getRoles());
        return managedLists;
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
        	setIfRolesEditable();
            KIMServiceLocator.getUiDocumentService().saveEntityPerson(this);
        }
    }

    
    @Override
    public void prepareForSave(){
        if (StringUtils.isBlank(getPrivacy().getDocumentNumber())) {
            getPrivacy().setDocumentNumber(
                    getDocumentNumber());
        }
        setEmployeeRecordIds();
        for (PersonDocumentRole role : getRoles()) {
            for (KimDocumentRoleMember rolePrncpl : role.getRolePrncpls()) {
                rolePrncpl.setDocumentNumber(getDocumentNumber());
                for (KimDocumentRoleQualifier qualifier : rolePrncpl.getQualifiers()) {
                    qualifier.setDocumentNumber(getDocumentNumber());
                    qualifier.setKimTypId(role.getKimTypeId());
                }
            }
        }
        if(getDelegationMembers()!=null){
            for(RoleDocumentDelegationMember delegationMember: getDelegationMembers()){
                delegationMember.setDocumentNumber(getDocumentNumber());
                for (RoleDocumentDelegationMemberQualifier qualifier: delegationMember.getQualifiers()) {
                    qualifier.setDocumentNumber(getDocumentNumber());
                    qualifier.setKimTypId(delegationMember.getRoleImpl().getKimTypeId());
                }
                addDelegationMemberToDelegation(delegationMember);
            }
        }
    }

    protected void setEmployeeRecordIds(){
    	List<KimEntityEmploymentInformationInfo> empInfos = getUiDocumentService().getEntityEmploymentInformationInfo(getEntityId());
        for(PersonDocumentAffiliation affiliation: getAffiliations()) {
            int employeeRecordCounter = CollectionUtils.isEmpty(empInfos) ? 0 : empInfos.size();
            for(PersonDocumentEmploymentInfo empInfo: affiliation.getEmpInfos()){
                if(CollectionUtils.isNotEmpty(empInfos)){
                    for(KimEntityEmploymentInformationInfo origEmpInfo: empInfos){
                        if (origEmpInfo.getEntityEmploymentId().equals(empInfo.getEntityEmploymentId())) {
                            empInfo.setEmploymentRecordId(origEmpInfo.getEmploymentRecordId());
                        }
                    }
                }
                if(StringUtils.isEmpty(empInfo.getEmploymentRecordId())){
                    employeeRecordCounter++;
                    empInfo.setEmploymentRecordId(employeeRecordCounter+"");
                }
            }
        }
    }

    public KimTypeAttributesHelper getKimTypeAttributesHelper(String roleId) {
        KimRoleInfo roleInfo = KIMServiceLocator.getRoleService().getRole(roleId);
        KimTypeInfo kimTypeInfo = KIMServiceLocator.getTypeInfoService().getKimType(roleInfo.getKimTypeId());
        return new KimTypeAttributesHelper(kimTypeInfo);
        //addDelegationRoleKimTypeAttributeHelper(roleId, helper);
    }

    @Override
	public void beforeInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
		super.beforeInsert(persistenceBroker);
        taxId = KimCommonUtils.encryptExternalIdentifier(taxId, KimConstants.PersonExternalIdentifierTypes.TAX);
	}
	
	@Override
	public void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
		super.beforeUpdate(persistenceBroker);
        taxId = KimCommonUtils.encryptExternalIdentifier(taxId, KimConstants.PersonExternalIdentifierTypes.TAX);
	}
	
	@Override
	public void afterLookup(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        super.afterLookup(persistenceBroker);
        taxId = KimCommonUtils.decryptExternalIdentifier(taxId, KimConstants.PersonExternalIdentifierTypes.TAX);
	}
	
	@Override
	public void beforeInsert() {
		super.beforeInsert();
		taxId = KimCommonUtils.encryptExternalIdentifier(taxId, KimConstants.PersonExternalIdentifierTypes.TAX);
    }

	@Override
	public void beforeUpdate() {
		super.beforeUpdate();
        taxId = KimCommonUtils.encryptExternalIdentifier(taxId, KimConstants.PersonExternalIdentifierTypes.TAX);
	}
	
	@javax.persistence.PostLoad 
	public void afterLookup(){
        taxId = KimCommonUtils.decryptExternalIdentifier(taxId, KimConstants.PersonExternalIdentifierTypes.TAX);
	}
	
	public void setIfRolesEditable(){
		if(CollectionUtils.isNotEmpty(getRoles())){
			for(PersonDocumentRole role: getRoles()){
				role.setEditable(validAssignRole(role));
			}
		}
	}

	public boolean validAssignRole(PersonDocumentRole role){
        boolean rulePassed = true;
        if(StringUtils.isNotEmpty(role.getNamespaceCode())){
	        Map<String,String> additionalPermissionDetails = new HashMap<String,String>();
	        additionalPermissionDetails.put(KimAttributes.NAMESPACE_CODE, role.getNamespaceCode());
	        additionalPermissionDetails.put(KimAttributes.ROLE_NAME, role.getRoleName());
			if (!getDocumentHelperService().getDocumentAuthorizer(this).isAuthorizedByTemplate(
					this,
					KimConstants.NAMESPACE_CODE,
					KimConstants.PermissionTemplateNames.ASSIGN_ROLE,
					GlobalVariables.getUserSession().getPrincipalId(),
					additionalPermissionDetails, null)){
	            rulePassed = false;
			}
        }
        return rulePassed;
	}

	@Transient
	protected transient DocumentHelperService documentHelperService;
	@Transient
	protected transient UiDocumentService uiDocumentService;
		
	protected DocumentHelperService getDocumentHelperService() {
	    if ( documentHelperService == null ) {
	        documentHelperService = KNSServiceLocator.getDocumentHelperService();
		}
	    return this.documentHelperService;
	}

	protected UiDocumentService getUiDocumentService() {
	    if (uiDocumentService == null ) {
	    	uiDocumentService = KIMServiceLocator.getUiDocumentService();
		}
	    return this.uiDocumentService;
	}

}
