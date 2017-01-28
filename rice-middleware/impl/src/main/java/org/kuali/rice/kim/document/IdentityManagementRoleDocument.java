/**
 * Copyright 2005-2017 The Kuali Foundation
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
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.responsibility.ResponsibilityService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRolePermission;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibility;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibilityAction;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegation;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMemberQualifier;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityInternalService;
import org.kuali.rice.kim.impl.services.KimImplServiceLocator;
import org.kuali.rice.kim.impl.type.IdentityManagementTypeAttributeTransactionalDocument;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.web.struts.form.IdentityManagementRoleDocumentForm;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.util.AutoPopulatingList;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name = "KRIM_ROLE_DOCUMENT_T")
public class IdentityManagementRoleDocument extends IdentityManagementTypeAttributeTransactionalDocument {

    private static final long serialVersionUID = 1L;

    // principal data
    @Column(name = "ROLE_ID")
    protected String roleId;

    @Column(name = "ROLE_TYP_ID")
    protected String roleTypeId;

    @Transient
    protected String roleTypeName;

    @Column(name = "ROLE_NMSPC")
    protected String roleNamespace = "";

    @Column(name = "ROLE_NM")
    protected String roleName = "";

    @Column(name = "DESC_TXT")
    protected String roleDescription = "";

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    protected boolean active = true;

    @Transient
    protected boolean editing;

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = KimDocumentRolePermission.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false)
    protected List<KimDocumentRolePermission> permissions = new AutoPopulatingList<KimDocumentRolePermission>(KimDocumentRolePermission.class);

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = KimDocumentRoleResponsibility.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false)
    protected List<KimDocumentRoleResponsibility> responsibilities = new AutoPopulatingList<KimDocumentRoleResponsibility>(KimDocumentRoleResponsibility.class);

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = KimDocumentRoleMember.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false)
    protected List<KimDocumentRoleMember> modifiedMembers = new AutoPopulatingList<KimDocumentRoleMember>(KimDocumentRoleMember.class);

    @Transient
    private List<RoleDocumentDelegationMember> delegationMembers = new AutoPopulatingList<RoleDocumentDelegationMember>(RoleDocumentDelegationMember.class);

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = RoleDocumentDelegation.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false)
    private List<RoleDocumentDelegation> delegations = new AutoPopulatingList<RoleDocumentDelegation>(RoleDocumentDelegation.class);

    @Transient
    protected List<KimDocumentRoleMember> searchResultMembers = new ArrayList<KimDocumentRoleMember>();

    @Transient
    protected List<KimDocumentRoleMember> members = new ArrayList<KimDocumentRoleMember>();

    @Transient
    private transient ResponsibilityService responsibilityService;

    @Transient
    private transient ResponsibilityInternalService responsibilityInternalService;

    public IdentityManagementRoleDocument() {
    }

    /**
	 * @return the active
	 */
    public boolean isActive() {
        return this.active;
    }

    /**
	 * @param active the active to set
	 */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
	 * @param roleId the roleId to set
	 */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
	 * @return the roleName
	 */
    public String getRoleName() {
        return this.roleName;
    }

    /**
	 * @param roleName the roleName to set
	 */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
	 * @return the roleDescription
	 */
    public String getRoleDescription() {
        return this.roleDescription;
    }

    /**
	 * @param roleDescription the roleDescription to set
	 */
    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    /**
	 * @return the roleNamespace
	 */
    public String getRoleNamespace() {
        return this.roleNamespace;
    }

    /**
	 * @param roleNamespace the roleNamespace to set
	 */
    public void setRoleNamespace(String roleNamespace) {
        this.roleNamespace = roleNamespace;
    }

    /**
	 * @return the roleTypeId
	 */
    public String getRoleTypeId() {
        return this.roleTypeId;
    }

    /**
	 * @param roleTypeId the roleTypeId to set
	 */
    public void setRoleTypeId(String roleTypeId) {
        this.roleTypeId = roleTypeId;
    }

    /**
	 * @return the roleTypeName
	 */
    public String getRoleTypeName() {
        if (roleTypeName == null) {
            if (kimType != null) {
                roleTypeName = kimType.getName();
            } else if (roleTypeId != null) {
                setKimType(KimApiServiceLocator.getKimTypeInfoService().getKimType(roleTypeId));
                if (kimType != null) {
                    roleTypeName = kimType.getName();
                }
            }
        }
        return this.roleTypeName;
    }

    /**
	 * @param roleTypeName the roleTypeName to set
	 */
    public void setRoleTypeName(String roleTypeName) {
        this.roleTypeName = roleTypeName;
    }

    /**
	 * @return the delegationMembers
	 */
    @Override
    public List<RoleDocumentDelegationMember> getDelegationMembers() {
        return this.delegationMembers;
    }

    /**
	 * @param delegationMembers the delegationMembers to set
	 */
    @Override
    public void setDelegationMembers(List<RoleDocumentDelegationMember> delegationMembers) {
        this.delegationMembers = delegationMembers;
    }

    /**
	 * @return the permissions
	 */
    public List<KimDocumentRolePermission> getPermissions() {
        return this.permissions;
    }

    /**
	 * @param permissions the permissions to set
	 */
    public void setPermissions(List<KimDocumentRolePermission> permissions) {
        this.permissions = permissions;
    }

    /**
	 * @return the responsibilities
	 */
    public List<KimDocumentRoleResponsibility> getResponsibilities() {
        return this.responsibilities;
    }

    /**
	 * @param responsibilities the responsibilities to set
	 */
    public void setResponsibilities(List<KimDocumentRoleResponsibility> responsibilities) {
        this.responsibilities = responsibilities;
    }

    /**
	 * @return the members
	 */
    public List<KimDocumentRoleMember> getMembers() {
        return this.members;
    }

    public enum RoleMemberMetaDataType implements Comparator<KimDocumentRoleMember> {

        MEMBER_ID("memberId"), MEMBER_NAME("memberName"), FULL_MEMBER_NAME("memberFullName");

        private final String attributeName;

        RoleMemberMetaDataType(String anAttributeName) {
            this.attributeName = anAttributeName;
        }

        public String getAttributeName() {
            return attributeName;
        }

        @Override
        public int compare(KimDocumentRoleMember m1, KimDocumentRoleMember m2) {
            if (m1 == null && m2 == null) {
                return 0;
            } else if (m1 == null) {
                return -1;
            } else if (m2 == null) {
                return 1;
            }
            if (this.getAttributeName().equals(MEMBER_ID.getAttributeName())) {
                return m1.getMemberId().compareToIgnoreCase(m2.getMemberId());
            } else if (this.getAttributeName().equals(FULL_MEMBER_NAME.getAttributeName())) {
                return m1.getMemberFullName().compareToIgnoreCase(m2.getMemberFullName());
            }
            return m1.getMemberName().compareToIgnoreCase(m2.getMemberName());
        }
    }

    public void setMemberMetaDataTypeToSort(Integer columnNumber) {
        switch(columnNumber) {
            case 1:
                this.memberMetaDataType = RoleMemberMetaDataType.MEMBER_ID;
                break;
            case 2:
                this.memberMetaDataType = RoleMemberMetaDataType.MEMBER_NAME;
                break;
            case 3:
                this.memberMetaDataType = RoleMemberMetaDataType.FULL_MEMBER_NAME;
                break;
            default:
                this.memberMetaDataType = RoleMemberMetaDataType.MEMBER_NAME;
                break;
        }
    }

    @Transient
    protected RoleMemberMetaDataType memberMetaDataType = RoleMemberMetaDataType.MEMBER_NAME;

    public RoleMemberMetaDataType getMemberMetaDataType() {
        return memberMetaDataType;
    }

    public void setMemberMetaDataType(RoleMemberMetaDataType memberMetaDataType) {
        this.memberMetaDataType = memberMetaDataType;
    }

    /**
	 * @return the members
	 */
    public KimDocumentRoleMember getMember(String roleMemberId) {
        if (StringUtils.isEmpty(roleMemberId)) {
            return null;
        }
        for (KimDocumentRoleMember roleMember : getMembers()) {
            if (roleMemberId.equals(roleMember.getRoleMemberId())) {
                return roleMember;
            }
        }
        return null;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(List<KimDocumentRoleMember> members) {
        this.members = members;
    }

    /**
     * @return the modifiedMembers
     */
    public List<KimDocumentRoleMember> getModifiedMembers() {
        return this.modifiedMembers;
    }

    /**
     * @param modifiedMembers the modifiedMembers to set
     */
    public void setModifiedMembers(List<KimDocumentRoleMember> modifiedMembers) {
        this.modifiedMembers = modifiedMembers;
    }

    /**
     * @return the searchResultMembers
     */
    public List<KimDocumentRoleMember> getSearchResultMembers() {
        return this.searchResultMembers;
    }

    /**
     * @param searchResultMembers the searchResultMembers to set
     */
    public void setSearchResultMembers(List<KimDocumentRoleMember> searchResultMembers) {
        this.searchResultMembers = searchResultMembers;
    }

    public void addResponsibility(KimDocumentRoleResponsibility roleResponsibility) {
        if (!getResponsibilityInternalService().areActionsAtAssignmentLevelById(roleResponsibility.getResponsibilityId())) {
            roleResponsibility.getRoleRspActions().add(getNewRespAction(roleResponsibility));
        }
        getResponsibilities().add(roleResponsibility);
    }

    protected KimDocumentRoleResponsibilityAction getNewRespAction(KimDocumentRoleResponsibility roleResponsibility) {
        KimDocumentRoleResponsibilityAction roleRspAction = new KimDocumentRoleResponsibilityAction();
        roleRspAction.setKimResponsibility(roleResponsibility.getKimResponsibility());
        roleRspAction.setRoleResponsibilityId(roleResponsibility.getRoleResponsibilityId());
        return roleRspAction;
    }

    public void addDelegationMember(RoleDocumentDelegationMember newDelegationMember) {
        getDelegationMembers().add(newDelegationMember);
    }

    public void addMember(KimDocumentRoleMember member) {
        DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S);
        member.setRoleMemberId(incrementer.nextStringValue());
        setupMemberRspActions(member);
        getModifiedMembers().add(member);
    }

    public KimDocumentRoleMember getBlankMember() {
        KimDocumentRoleMember member = new KimDocumentRoleMember();
        KimDocumentRoleQualifier qualifier;
        if (getDefinitions() != null) {
            for (KimAttributeField key : getDefinitions()) {
                qualifier = new KimDocumentRoleQualifier();
                qualifier.setKimAttrDefnId(getKimAttributeDefnId(key));
                member.getQualifiers().add(qualifier);
            }
        }
        setupMemberRspActions(member);
        return member;
    }

    public RoleDocumentDelegationMember getBlankDelegationMember() {
        RoleDocumentDelegationMember member = new RoleDocumentDelegationMember();
        RoleDocumentDelegationMemberQualifier qualifier;
        if (getDefinitions() != null) {
            for (KimAttributeField key : getDefinitions()) {
                qualifier = new RoleDocumentDelegationMemberQualifier();
                setAttrDefnIdForDelMemberQualifier(qualifier, key);
                member.getQualifiers().add(qualifier);
            }
        }
        return member;
    }

    public void setupMemberRspActions(KimDocumentRoleMember member) {
        member.getRoleRspActions().clear();
        for (KimDocumentRoleResponsibility roleResp : getResponsibilities()) {
            if (getResponsibilityInternalService().areActionsAtAssignmentLevelById(roleResp.getResponsibilityId())) {
                KimDocumentRoleResponsibilityAction action = new KimDocumentRoleResponsibilityAction();
                action.setRoleResponsibilityId("*");
                action.setRoleMemberId(member.getRoleMemberId());
                member.getRoleRspActions().add(action);
                break;
            }
        }
    }

    public void updateMembers(IdentityManagementRoleDocumentForm roleDocumentForm) {
        for (KimDocumentRoleMember member : roleDocumentForm.getRoleDocument().getMembers()) {
            roleDocumentForm.getRoleDocument().setupMemberRspActions(member);
        }
    }

    public void updateMembers(KimDocumentRoleResponsibility newResponsibility) {
        for (KimDocumentRoleMember member : getMembers()) {
            setupMemberRspActions(newResponsibility, member);
        }
    }

    public void setupMemberRspActions(KimDocumentRoleResponsibility roleResp, KimDocumentRoleMember member) {
        if ((member.getRoleRspActions() == null || member.getRoleRspActions().size() < 1) && getResponsibilityInternalService().areActionsAtAssignmentLevelById(roleResp.getResponsibilityId())) {
            KimDocumentRoleResponsibilityAction action = new KimDocumentRoleResponsibilityAction();
            action.setRoleResponsibilityId("*");
            action.setRoleMemberId(member.getRoleMemberId());
            if (member.getRoleRspActions() == null) {
                member.setRoleRspActions(new ArrayList<KimDocumentRoleResponsibilityAction>());
            }
            member.getRoleRspActions().add(action);
        }
    }

    protected void setAttrDefnIdForDelMemberQualifier(RoleDocumentDelegationMemberQualifier qualifier, KimAttributeField definition) {
        qualifier.setKimAttrDefnId(definition.getId());
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#doRouteStatusChange(org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            KIMServiceLocatorInternal.getUiDocumentService().saveRole(this);
        }
    }

    public void initializeDocumentForNewRole() {
        if (StringUtils.isBlank(this.roleId)) {
            DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), KimConstants.SequenceNames.KRIM_ROLE_ID_S);
            this.roleId = incrementer.nextStringValue();
        }
        if (StringUtils.isBlank(this.roleTypeId)) {
            this.roleTypeId = "1";
        }
    }

    public String getRoleId() {
        if (StringUtils.isBlank(this.roleId)) {
            initializeDocumentForNewRole();
        }
        return roleId;
    }

    @Override
    public void prepareForSave() {
        String roleId;
        if (StringUtils.isBlank(getRoleId())) {
            DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), KimConstants.SequenceNames.KRIM_ROLE_ID_S);
            roleId = incrementer.nextStringValue();
            setRoleId(roleId);
        } else {
            roleId = getRoleId();
        }
        if (getPermissions() != null) {
            for (KimDocumentRolePermission permission : getPermissions()) {
                permission.setRoleId(roleId);
                permission.setDocumentNumber(getDocumentNumber());
                if (StringUtils.isBlank(permission.getRolePermissionId())) {
                    DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), KimConstants.SequenceNames.KRIM_ROLE_PERM_ID_S);
                    permission.setRolePermissionId(incrementer.nextStringValue());
                }
            }
        }
        if (getResponsibilities() != null) {
            for (KimDocumentRoleResponsibility responsibility : getResponsibilities()) {
                String nextRoleResponsibilityId = null;

                if (StringUtils.isBlank(responsibility.getRoleResponsibilityId())) {
                    DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), KimConstants.SequenceNames.KRIM_ROLE_RSP_ID_S);
                    nextRoleResponsibilityId = incrementer.nextStringValue();
                    responsibility.setRoleResponsibilityId(nextRoleResponsibilityId);
                } else{
                    responsibility.setDocumentNumber(getDocumentNumber());
                    responsibility.setVersionNumber(null);
                }

                responsibility.setRoleId(roleId);
                if (!getResponsibilityInternalService().areActionsAtAssignmentLevelById(responsibility.getResponsibilityId())) {
                    if (StringUtils.isBlank(responsibility.getRoleRspActions().get(0).getRoleResponsibilityActionId())) {
                        DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), KimConstants.SequenceNames.KRIM_ROLE_RSP_ACTN_ID_S);
                        responsibility.getRoleRspActions().get(0).setRoleResponsibilityActionId(incrementer.nextStringValue());
                    }

                    if (StringUtils.isBlank(responsibility.getRoleRspActions().get(0).getRoleResponsibilityId())) {
                        if (StringUtils.isBlank(nextRoleResponsibilityId)) {
                            responsibility.getRoleRspActions().get(0).setRoleResponsibilityId(responsibility.getRoleResponsibilityId());
                        } else {
                            responsibility.getRoleRspActions().get(0).setRoleResponsibilityId(nextRoleResponsibilityId);
                        }
                    }

                    responsibility.getRoleRspActions().get(0).setRoleMemberId("*");
                    responsibility.getRoleRspActions().get(0).setDocumentNumber(getDocumentNumber());
                }
            }
        }
        if (getModifiedMembers() != null) {
            for (KimDocumentRoleMember member : getModifiedMembers()) {
                member.setDocumentNumber(getDocumentNumber());
                member.setRoleId(roleId);
                if (StringUtils.isBlank(member.getRoleMemberId())) {
                    DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S);
                    member.setRoleMemberId(incrementer.nextStringValue());
                }
                for (KimDocumentRoleQualifier qualifier : member.getQualifiers()) {
                    qualifier.setDocumentNumber(getDocumentNumber());
                    qualifier.setRoleMemberId(member.getRoleMemberId());
                    qualifier.setKimTypId(getKimType().getId());
                }
                for (KimDocumentRoleResponsibilityAction roleRespAction : member.getRoleRspActions()) {
                    if (StringUtils.isBlank(roleRespAction.getRoleResponsibilityActionId())) {
                        DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), KimConstants.SequenceNames.KRIM_ROLE_RSP_ACTN_ID_S);
                        roleRespAction.setRoleResponsibilityActionId(incrementer.nextStringValue());
                        roleRespAction.setDocumentNumber(getDocumentNumber());
                    }
                    roleRespAction.setRoleMemberId(member.getRoleMemberId());
                    roleRespAction.setDocumentNumber(getDocumentNumber());
                    if (!StringUtils.equals(roleRespAction.getRoleResponsibilityId(), "*")) {
                        for (KimDocumentRoleResponsibility responsibility : getResponsibilities()) {
                            if (StringUtils.equals(roleRespAction.getKimResponsibility().getId(), responsibility.getResponsibilityId())) {
                                roleRespAction.setRoleResponsibilityId(responsibility.getRoleResponsibilityId());
                            }
                        }
                    }
                    // This will cause a problem with JPA
//                    if (roleRespAction.getVersionNumber() == null) {
//                        roleRespAction.setVersionNumber(new Long(1));
//                    }
                }
            }
        }
        if (getDelegationMembers() != null) {
            for (RoleDocumentDelegationMember delegationMember : getDelegationMembers()) {
                delegationMember.setDocumentNumber(getDocumentNumber());
                addDelegationMemberToDelegation(delegationMember);
            }
            for (RoleDocumentDelegation delegation : getDelegations()) {
                delegation.setDocumentNumber(getDocumentNumber());
                delegation.setKimTypeId(getKimType().getId());
                List<RoleDocumentDelegationMember> membersToRemove = new AutoPopulatingList<RoleDocumentDelegationMember>(RoleDocumentDelegationMember.class);
                for (RoleDocumentDelegationMember member : delegation.getMembers()) {
                    if (delegation.getDelegationId().equals(member.getDelegationId()) && delegation.getDelegationTypeCode().equals(member.getDelegationTypeCode())) {
                        for (RoleDocumentDelegationMemberQualifier qualifier : member.getQualifiers()) {
                            qualifier.setKimTypId(getKimType().getId());
                            qualifier.setDocumentNumber(getDocumentNumber());
                        }
                    } else {
                        membersToRemove.add(member);
                    }
                }
                if (!membersToRemove.isEmpty()) {
                    for (RoleDocumentDelegationMember member : membersToRemove) {
                        delegation.getMembers().remove(member);
                    }
                }
                delegation.setRoleId(roleId);
            }
        }
    }

    public ResponsibilityService getResponsibilityService() {
        if (responsibilityService == null) {
            responsibilityService = KimApiServiceLocator.getResponsibilityService();
        }
        return responsibilityService;
    }

    public ResponsibilityInternalService getResponsibilityInternalService() {
        if (responsibilityInternalService == null) {
            responsibilityInternalService = KimImplServiceLocator.getResponsibilityInternalService();
        }
        return responsibilityInternalService;
    }

    /**
	 * @return the editing
	 */
    public boolean isEditing() {
        return this.editing;
    }

    /**
	 * @param editing the editing to set
	 */
    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    /**
	 * @return the delegations
	 */
    @Override
    public List<RoleDocumentDelegation> getDelegations() {
        return this.delegations;
    }

    /**
	 * @param delegations the delegations to set
	 */
    @Override
    public void setDelegations(List<RoleDocumentDelegation> delegations) {
        this.delegations = delegations;
    }

    @Override
    public void setKimType(KimType kimType) {
        super.setKimType(kimType);
        if (kimType != null) {
            setRoleTypeId(kimType.getId());
            setRoleTypeName(kimType.getName());
        }
    }
}
