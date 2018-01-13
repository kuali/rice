/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kim.bo.ui;

import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.springframework.util.AutoPopulatingList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googleroles.com)
 *
 */
@Entity
@Table(name = "KRIM_PND_DLGN_T")
public class RoleDocumentDelegation extends KimDocumentBoActivatableBase {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_DLGN_ID_S")
    @GeneratedValue(generator = "KRIM_DLGN_ID_S")
    @Id
    @Column(name = "DLGN_ID")
    protected String delegationId;

    @Column(name = "ROLE_ID")
    protected String roleId;

    @Column(name = "KIM_TYP_ID")
    protected String kimTypeId;

    @Column(name = "DLGN_TYP_CD")
    protected String delegationTypeCode;

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = RoleDocumentDelegationMember.class, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumns({ 
        @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false), 
        @JoinColumn(name = "DLGN_ID", referencedColumnName = "DLGN_ID", insertable = false, updatable = false) })
    private List<RoleDocumentDelegationMember> members = new AutoPopulatingList<RoleDocumentDelegationMember>(RoleDocumentDelegationMember.class);

    @Transient
    private RoleDocumentDelegationMember member = new RoleDocumentDelegationMember();

    @Transient
    protected List<KimDocumentRoleQualifier> qualifiers = new AutoPopulatingList<KimDocumentRoleQualifier>(KimDocumentRoleQualifier.class);

    public String getRoleId() {
        return this.roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getKimTypeId() {
        return this.kimTypeId;
    }

    public void setKimTypeId(String typeId) {
        this.kimTypeId = typeId;
    }

    public String getDelegationTypeCode() {
        return this.delegationTypeCode;
    }

    public void setDelegationTypeCode(String delegationTypeCode) {
        this.delegationTypeCode = delegationTypeCode;
    }

    public String getDelegationId() {
        return this.delegationId;
    }

    public void setDelegationId(String delegationId) {
        this.delegationId = delegationId;
    }

    /**
	 * @return the qualifiers
	 */
    public List<KimDocumentRoleQualifier> getQualifiers() {
        return this.qualifiers;
    }

    /**
	 * @param qualifiers the qualifiers to set
	 */
    public void setQualifiers(List<KimDocumentRoleQualifier> qualifiers) {
        this.qualifiers = qualifiers;
    }

    public int getNumberOfQualifiers() {
        return qualifiers == null ? 0 : qualifiers.size();
    }

    /**
	 * @return the members
	 */
    public List<RoleDocumentDelegationMember> getMembers() {
        return this.members;
    }

    /**
	 * @param members the members to set
	 */
    public void setMembers(List<RoleDocumentDelegationMember> members) {
        this.members = members;
    }

    /**
	 * @return the member
	 */
    public RoleDocumentDelegationMember getMember() {
        return this.member;
    }

    /**
	 * @param member the member to set
	 */
    public void setMember(RoleDocumentDelegationMember member) {
        this.member = member;
    }

    public boolean isDelegationPrimary() {
        return DelegationType.PRIMARY.getCode().equals(getDelegationTypeCode());
    }

    public boolean isDelegationSecondary() {
        return DelegationType.SECONDARY.getCode().equals(getDelegationTypeCode());
    }
}
