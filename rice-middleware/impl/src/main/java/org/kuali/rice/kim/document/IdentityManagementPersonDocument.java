/**
 * Copyright 2005-2016 The Kuali Foundation
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibilityAction;
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
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegation;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMemberQualifier;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.impl.role.RoleMemberBo;
import org.kuali.rice.kim.impl.services.KimImplServiceLocator;
import org.kuali.rice.kim.impl.type.KimTypeAttributesHelper;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.converters.HashConverter;
import org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory;
import org.kuali.rice.krad.rules.rule.event.DocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - shyu don't forget to fill
 * this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@AttributeOverrides({ @AttributeOverride(name = "documentNumber", column = @Column(name = "FDOC_NBR")) })
@Entity
@Table(name = "KRIM_PERSON_DOCUMENT_T")
public class IdentityManagementPersonDocument extends IdentityManagementKimDocument {

    protected static final long serialVersionUID = -534993712085516925L;

    // principal data                       
    @Column(name = "PRNCPL_ID")
    protected String principalId;

    @Column(name = "PRNCPL_NM")
    protected String principalName;

    @Column(name = "ENTITY_ID")
    protected String entityId;

    //@Type(type="org.kuali.rice.krad.util.HibernateKualiHashType")                       
    @Column(name = "PRNCPL_PSWD")
    @Convert(converter = HashConverter.class)
    protected String password;

    @Column(name = "UNIV_ID")
    protected String univId = "";

    // affiliation data                       
    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = PersonDocumentAffiliation.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false)
    protected List<PersonDocumentAffiliation> affiliations;

    @Transient
    protected String campusCode = "";

    // external identifier data                       
    @Transient
    protected Map<String, String> externalIdentifiers = null;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    protected boolean active;

    // citizenship                       
    @Transient
    protected List<PersonDocumentCitizenship> citizenships;

    // protected List<DocEmploymentInfo> employmentInformations;                       
    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = PersonDocumentName.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false)
    protected List<PersonDocumentName> names;

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = PersonDocumentAddress.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false)
    protected List<PersonDocumentAddress> addrs;

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = PersonDocumentPhone.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false)
    protected List<PersonDocumentPhone> phones;

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = PersonDocumentEmail.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false)
    protected List<PersonDocumentEmail> emails;

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = PersonDocumentGroup.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false)
    protected List<PersonDocumentGroup> groups;

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = PersonDocumentRole.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false)
    protected List<PersonDocumentRole> roles;

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToOne(targetEntity = PersonDocumentPrivacy.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @PrimaryKeyJoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR")
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

    /*
     * sets the principal name.  
     * Principal names are converted to lower case.
     */
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
        if (StringUtils.isBlank(this.principalId)) {
            DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), KimConstants.SequenceNames.KRIM_PRNCPL_ID_S);
            this.principalId = incrementer.nextStringValue();
        }
        if (StringUtils.isBlank(this.entityId)) {
            DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), KimConstants.SequenceNames.KRIM_ENTITY_ID_S);
            this.entityId = incrementer.nextStringValue();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
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
     * @see org.kuali.rice.krad.document.DocumentBase#doRouteStatusChange(org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            setIfRolesEditable();
            KIMServiceLocatorInternal.getUiDocumentService().saveEntityPerson(this);
        }
    }

    @Override
    public void prepareForSave() {
        if (StringUtils.isBlank(getPrivacy().getDocumentNumber())) {
            getPrivacy().setDocumentNumber(getDocumentNumber());
        }
        setEmployeeRecordIds();
        for (PersonDocumentRole role : getRoles()) {
            role.setDocumentNumber(getDocumentNumber());
            for (KimDocumentRoleMember rolePrncpl : role.getRolePrncpls()) {
                rolePrncpl.setDocumentNumber(getDocumentNumber());
                rolePrncpl.setRoleId(role.getRoleId());
                if (StringUtils.isEmpty(rolePrncpl.getRoleMemberId())) {
                    DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), "KRIM_ROLE_MBR_ID_S");
                    rolePrncpl.setRoleMemberId(incrementer.nextStringValue());
                }
                for (KimDocumentRoleQualifier qualifier : rolePrncpl.getQualifiers()) {
                    qualifier.setDocumentNumber(getDocumentNumber());
                    qualifier.setRoleMemberId(rolePrncpl.getRoleMemberId());
                    qualifier.setKimTypId(role.getKimTypeId());
                }
                for (KimDocumentRoleResponsibilityAction responsibilityAction : rolePrncpl.getRoleRspActions()) {
                    responsibilityAction.setDocumentNumber(getDocumentNumber());
                    responsibilityAction.setRoleMemberId(rolePrncpl.getRoleMemberId());
                    responsibilityAction.setRoleResponsibilityId("*");
                }
            }
        }
        if (getDelegationMembers() != null) {
            for (RoleDocumentDelegationMember delegationMember : getDelegationMembers()) {
                delegationMember.setDocumentNumber(getDocumentNumber());
                for (RoleDocumentDelegationMemberQualifier qualifier : delegationMember.getQualifiers()) {
                    qualifier.setDocumentNumber(getDocumentNumber());
                    qualifier.setKimTypId(delegationMember.getRoleBo().getKimTypeId());
                }
                addDelegationMemberToDelegation(delegationMember);
            }
        }
        // important to do this after getDelegationMembers since the addDelegationMemberToDelegation method will create
        // primary and/or secondary delegations for us in a "just-in-time" fashion
        if (getDelegations() != null) {
            List<RoleDocumentDelegation> emptyDelegations = new ArrayList<>();
            for (RoleDocumentDelegation delegation : getDelegations()) {
                delegation.setDocumentNumber(getDocumentNumber());
                if (delegation.getMembers().isEmpty()) {
                    emptyDelegations.add(delegation);
                }
            }
            // remove any empty delegations because we just don't need them
            getDelegations().removeAll(emptyDelegations);
        }
        if (getAddrs() != null) {
            for (PersonDocumentAddress address : getAddrs()) {
                address.setDocumentNumber(getDocumentNumber());
                if (StringUtils.isEmpty(address.getEntityAddressId())) {
                    DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), "KRIM_ENTITY_ADDR_ID_S");
                    address.setEntityAddressId(incrementer.nextStringValue());
                }
            }
        }
        if (getAffiliations() != null) {
            String nextValue = null;

            for (PersonDocumentAffiliation affiliation : getAffiliations()) {
                affiliation.setDocumentNumber(getDocumentNumber());
                if (StringUtils.isEmpty(affiliation.getEntityAffiliationId())) {
                    DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), "KRIM_ENTITY_AFLTN_ID_S");
                    nextValue = incrementer.nextStringValue();
                    affiliation.setEntityAffiliationId(nextValue);
                }
                for (PersonDocumentEmploymentInfo empInfo : affiliation.getEmpInfos()) {
                    empInfo.setDocumentNumber(getDocumentNumber());
                    if (StringUtils.isEmpty(empInfo.getEntityAffiliationId())) {
                        empInfo.setEntityAffiliationId(nextValue);
                    }
                }
            }
        }
        if (getEmails() != null) {
            for (PersonDocumentEmail email : getEmails()) {
                email.setDocumentNumber(getDocumentNumber());
                if (StringUtils.isEmpty(email.getEntityEmailId())) {
                    DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), "KRIM_ENTITY_EMAIL_ID_S");
                    email.setEntityEmailId(incrementer.nextStringValue());
                }
            }
        }
        if (getGroups() != null) {
            for (PersonDocumentGroup group : getGroups()) {
                group.setDocumentNumber(getDocumentNumber());
                if (StringUtils.isEmpty(group.getGroupMemberId())) {
                    DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), "KRIM_GRP_MBR_ID_S");
                    group.setGroupMemberId(incrementer.nextStringValue());
                }
            }
        }
        if (getNames() != null) {
            for (PersonDocumentName name : getNames()) {
                name.setDocumentNumber(getDocumentNumber());
                if (StringUtils.isEmpty(name.getEntityNameId())) {
                    DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), "KRIM_ENTITY_NM_ID_S");
                    name.setEntityNameId(incrementer.nextStringValue());
                }
            }
        }
        if (getPhones() != null) {
            for (PersonDocumentPhone phone : getPhones()) {
                phone.setDocumentNumber(getDocumentNumber());
                if (StringUtils.isEmpty(phone.getEntityPhoneId())) {
                    DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), "KRIM_ENTITY_PHONE_ID_S");
                    phone.setEntityPhoneId(incrementer.nextStringValue());
                }
            }
        }
    }

    @Override
    public void postProcessSave(DocumentEvent event) {
        super.postProcessSave(event);
        // after the save has completed, we want to restore any potentially @Transient state that JPA might have
        // discarded, specifically the delegation members have a lot of this
        resyncTransientState();
    }

    public void resyncTransientState() {
        getDelegationMembers().clear();
        for (RoleDocumentDelegation delegation : getDelegations()) {
            for (RoleDocumentDelegationMember delegationMember : delegation.getMembers()) {

                // RoleDocumentDelegationMember has a number of transient fields that are derived from the role member,
                // we must populate them in order for the person document to work properly when loading an existing
                // person document

                RoleMemberBo roleMember = getUiDocumentService().getRoleMember(delegationMember.getRoleMemberId());
                delegationMember.setRoleMemberMemberId(roleMember.getMemberId());
                delegationMember.setRoleMemberMemberTypeCode(roleMember.getType().getCode());
                delegationMember.setRoleMemberName(getUiDocumentService().getMemberName(MemberType.fromCode(delegationMember.getRoleMemberMemberTypeCode()), delegationMember.getRoleMemberMemberId()));
                delegationMember.setRoleMemberNamespaceCode(getUiDocumentService().getMemberNamespaceCode(MemberType.fromCode(delegationMember.getRoleMemberMemberTypeCode()), delegationMember.getRoleMemberMemberId()));
                delegationMember.setDelegationTypeCode(delegation.getDelegationTypeCode());
                Role role = KimApiServiceLocator.getRoleService().getRole(roleMember.getRoleId());
                delegationMember.setRoleBo(RoleBo.from(role));

                // don't want to be able to "delete" existing delegation members from the person document, so we
                // indicate that we are editing the delegation member, which we are
                delegationMember.setEdit(true);

                getDelegationMembers().add(delegationMember);
            }
        }
    }



    protected void setEmployeeRecordIds() {
        List<EntityEmployment> empInfos = getUiDocumentService().getEntityEmploymentInformationInfo(getEntityId());
        for (PersonDocumentAffiliation affiliation : getAffiliations()) {
            int employeeRecordCounter = CollectionUtils.isEmpty(empInfos) ? 0 : empInfos.size();
            for (PersonDocumentEmploymentInfo empInfo : affiliation.getEmpInfos()) {
                if (CollectionUtils.isNotEmpty(empInfos)) {
                    for (EntityEmployment origEmpInfo : empInfos) {
                        if (origEmpInfo.getId().equals(empInfo.getEntityEmploymentId())) {
                            empInfo.setEmploymentRecordId(origEmpInfo.getEmploymentRecordId());
                        }
                    }
                }
                if (StringUtils.isEmpty(empInfo.getEmploymentRecordId())) {
                    employeeRecordCounter++;
                    empInfo.setEmploymentRecordId(employeeRecordCounter + "");
                }
            }
        }
    }

    public KimTypeAttributesHelper getKimTypeAttributesHelper(String roleId) {
        Role role = KimApiServiceLocator.getRoleService().getRole(roleId);
        KimType kimTypeInfo = KimApiServiceLocator.getKimTypeInfoService().getKimType(role.getKimTypeId());
        return new KimTypeAttributesHelper(kimTypeInfo);
    }

    public void setIfRolesEditable() {
        if (CollectionUtils.isNotEmpty(getRoles())) {
            for (PersonDocumentRole role : getRoles()) {
                role.setEditable(validAssignRole(role));
            }
        }
    }

    public boolean validAssignRole(PersonDocumentRole role) {
        boolean rulePassed = true;
        if (StringUtils.isNotEmpty(role.getNamespaceCode())) {
            Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
            additionalPermissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, role.getNamespaceCode());
            additionalPermissionDetails.put(KimConstants.AttributeConstants.ROLE_NAME, role.getRoleName());
            if (!getDocumentHelperService().getDocumentAuthorizer(this).isAuthorizedByTemplate(this, KimConstants.NAMESPACE_CODE, KimConstants.PermissionTemplateNames.ASSIGN_ROLE, GlobalVariables.getUserSession().getPrincipalId(), additionalPermissionDetails, null)) {
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
        if (documentHelperService == null) {
            documentHelperService = KNSServiceLocator.getDocumentHelperService();
        }
        return this.documentHelperService;
    }

    protected UiDocumentService getUiDocumentService() {
        if (uiDocumentService == null) {
            uiDocumentService = KIMServiceLocatorInternal.getUiDocumentService();
        }
        return this.uiDocumentService;
    }
}
