/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kew.impl.peopleflow;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kew.api.action.ActionRequestPolicy;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDelegate;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowMember;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowMemberContract;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.framework.group.GroupEbo;
import org.kuali.rice.kim.framework.role.RoleEbo;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KREW_PPL_FLW_MBR_T")
public class PeopleFlowMemberBo implements Serializable, PeopleFlowMemberContract,BusinessObject {

    @Id
    @GeneratedValue(generator = "KREW_PPL_FLW_S")
    @PortableSequenceGenerator(name = "KREW_PPL_FLW_MBR_S")
    @Column(name = "PPL_FLW_MBR_ID", nullable = false)
    private String id;

    @Column(name = "MBR_ID", nullable = false)
    private String memberId;

    @Column(name = "MBR_TYP_CD", nullable = false)
    private String memberTypeCode;

    @Column(name = "ACTN_RQST_PLCY_CD")
    private String actionRequestPolicyCode;

    @Column(name = "RSP_ID", nullable = false)
    private String responsibilityId;

    @Column(name = "PRIO")
    int priority = 1;

    @Version
    @Column(name = "VER_NBR", nullable = false)
    Long versionNumber;

    @ManyToOne
    @JoinColumn(name = "PPL_FLW_ID", nullable = false)
    private PeopleFlowBo peopleFlow;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "peopleFlowMember", orphanRemoval = true)
    List<PeopleFlowDelegateBo> delegates = new ArrayList<PeopleFlowDelegateBo>();

    // non-persisted
    @Transient
    private String memberName;

    @Transient
    private Person person;

    @Transient
    private GroupEbo group;

    @Transient
    private RoleEbo role;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PeopleFlowBo getPeopleFlow() {
        return peopleFlow;
    }

    public void setPeopleFlow(PeopleFlowBo peopleFlow) {
        this.peopleFlow = peopleFlow;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberTypeCode() {
        return memberTypeCode;
    }

    public void setMemberTypeCode(String memberTypeCode) {
        this.memberTypeCode = memberTypeCode;
    }

    public String getActionRequestPolicyCode() {
        return actionRequestPolicyCode;
    }

    public void setActionRequestPolicyCode(String actionRequestPolicyCode) {
        this.actionRequestPolicyCode = actionRequestPolicyCode;
    }

    public String getResponsibilityId() {
        return responsibilityId;
    }

    public void setResponsibilityId(String responsibilityId) {
        this.responsibilityId = responsibilityId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setPerson(Person person) {
        this.person = person;
    }


    public List<PeopleFlowDelegateBo> getDelegates() {
        return delegates;
    }

    public void setDelegates(List<PeopleFlowDelegateBo> delegates) {
        this.delegates = delegates;
    }

    public Person getPerson() {
        if (MemberType.PRINCIPAL.getCode().equals(this.memberTypeCode)) {
            if ((this.person == null) || !this.person.getPrincipalId().equals(this.memberId) ||
                    !this.person.getPrincipalName().equals(this.memberName)) {

                // use member name first
                if (StringUtils.isNotBlank(this.memberName)) {
                    this.person = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(this.memberName);
                } else {
                    this.person = KimApiServiceLocator.getPersonService().getPerson(this.memberId);
                }
            }

            if (this.person != null) {
                this.memberId = this.person.getPrincipalId();
                this.memberName = this.person.getPrincipalName();

                return this.person;
            }
        }



        Person newPerson = null;

        try {
            newPerson = KimApiServiceLocator.getPersonService().getPersonImplementationClass().newInstance();
        } catch (InstantiationException ie) {
            throw new IllegalStateException(ie);
        } catch (IllegalAccessException iae) {
            throw new IllegalStateException(iae);
        }
        return newPerson;
    }

    public GroupEbo getGroup() {
        if (MemberType.GROUP.getCode().equals(memberTypeCode)) {
            ModuleService eboModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(GroupEbo.class);
            group = eboModuleService.retrieveExternalizableBusinessObjectIfNecessary(this, group, "group");
            if (group != null) {
                memberId = group.getId();
                memberName = group.getNamespaceCode() + " : " + group.getName();

            }
        }
        return group;
    }


    public RoleEbo getRole() {
        if (MemberType.ROLE.getCode().equals(memberTypeCode)) {
            ModuleService eboModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(RoleEbo.class);
            role = eboModuleService.retrieveExternalizableBusinessObjectIfNecessary(this, role, "role");
            if (role != null) {
                memberId = role.getId();
                memberName = role.getNamespaceCode() + " : " + role.getName();
            }
        }
        return role;
    }

    public void setMemberName(String memberName) throws InstantiationException, IllegalAccessException {
        this.memberName = memberName;

        // trigger update of related object (only person can be updated by name)
        if (MemberType.PRINCIPAL.getCode().equals(memberTypeCode)) {
            getPerson();
        }
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
        updateRelatedObject();
    }

    // trigger update of related object
    public void updateRelatedObject() {
        if (MemberType.PRINCIPAL.getCode().equals(memberTypeCode)) {
            getPerson();
        } else if (MemberType.GROUP.getCode().equals(memberTypeCode)) {
            getGroup();
        } else if (MemberType.ROLE.getCode().equals(memberTypeCode)) {
            getRole();
        }
    }

    @Override
    public MemberType getMemberType() {
        return MemberType.fromCode(memberTypeCode);
    }

    public void setMemberType(MemberType type) {
        memberTypeCode = type.getCode();
    }

    @Override
    public ActionRequestPolicy getActionRequestPolicy() {
        return ActionRequestPolicy.fromCode(actionRequestPolicyCode);
    }

    public static PeopleFlowMember to(PeopleFlowMemberBo memberBo) {
        if (memberBo == null) {
            return null;
        }
        PeopleFlowMember.Builder member = PeopleFlowMember.Builder.create(memberBo);
        return member.build();
    }

    public static PeopleFlowMemberBo from(PeopleFlowMember member, PeopleFlowBo peopleFlow) {

        if (member == null) {
            return null;
        }
        PeopleFlowMemberBo memberBo = new PeopleFlowMemberBo();
        memberBo.setPeopleFlow(peopleFlow);
        memberBo.setMemberId(member.getMemberId());
        memberBo.setMemberType(member.getMemberType());
        if (member.getActionRequestPolicy() != null) {
            memberBo.setActionRequestPolicyCode(member.getActionRequestPolicy().getCode());
        }
        memberBo.setResponsibilityId(member.getResponsibilityId());
        memberBo.setPriority(member.getPriority());
        memberBo.setDelegates(new ArrayList<PeopleFlowDelegateBo>());
        for (PeopleFlowDelegate delegate : member.getDelegates()) {
            memberBo.getDelegates().add(PeopleFlowDelegateBo.from(delegate, memberBo));
        }
        return memberBo;
    }


    @Override
    public void refresh() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}