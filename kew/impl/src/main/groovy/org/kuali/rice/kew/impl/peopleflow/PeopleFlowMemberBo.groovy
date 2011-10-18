package org.kuali.rice.kew.impl.peopleflow

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.kew.api.peopleflow.PeopleFlowMemberContract
import org.kuali.rice.core.api.membership.MemberType

import org.kuali.rice.kew.api.peopleflow.PeopleFlowMember

import org.kuali.rice.kew.api.peopleflow.PeopleFlowDelegate

import org.kuali.rice.kew.api.action.ActionRequestPolicy;
import org.kuali.rice.kim.api.identity.Person

import org.kuali.rice.kim.api.KimConstants
import org.kuali.rice.kim.api.services.KimApiServiceLocator
import org.kuali.rice.kim.framework.group.GroupEbo
import org.kuali.rice.kim.framework.role.RoleEbo

import org.kuali.rice.krad.service.KRADServiceLocatorWeb
import org.kuali.rice.krad.service.ModuleService;

/**
 * mapped entity for PeopleFlow members
 */
class PeopleFlowMemberBo extends PersistableBusinessObjectBase implements PeopleFlowMemberContract {

    String id
    String peopleFlowId
    String memberId
    private String memberTypeCode
    String actionRequestPolicyCode
    String responsibilityId
    int priority = 1;

    // non-persisted
    String memberName;
    Person person;
    GroupEbo group;
    RoleEbo role;

    List<PeopleFlowDelegateBo> delegates = new ArrayList<PeopleFlowDelegateBo>();

    public Person getPerson() {
        if (KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)) {
            if ((this.person == null) || !person.getPrincipalId().equals(memberId)) {
                this.person = KimApiServiceLocator.personService.getPerson(memberId);
            }
        }

        if (this.person != null) {
            return this.person;
        }

        return KimApiServiceLocator.personService.personImplementationClass.newInstance();
    }

    public GroupEbo getGroup() {
        ModuleService eboModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(GroupEbo.class);
        group = eboModuleService.retrieveExternalizableBusinessObjectIfNecessary(this, group, "group");

        return group;
    }

    public RoleEbo getRole() {
        ModuleService eboModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(RoleEbo.class);
        role = eboModuleService.retrieveExternalizableBusinessObjectIfNecessary(this, role, "role");

        return role;
    }

    String getMemberTypeCode() {
        return memberTypeCode
    }

    @Override
    MemberType getMemberType() {
        return MemberType.fromCode(memberTypeCode)
    }

    void setMemberType(MemberType type) {
        memberTypeCode = type.getCode()
    }

    @Override
    ActionRequestPolicy getActionRequestPolicy() {
        return ActionRequestPolicy.fromCode(actionRequestPolicyCode)
    }

    public static PeopleFlowMember to(PeopleFlowMemberBo memberBo) {
        if (memberBo == null) {
            return null;
        }
        PeopleFlowMember.Builder member = PeopleFlowMember.Builder.create(memberBo);
        return member.build();
    }

    public static PeopleFlowMemberBo from(PeopleFlowMember member) {
        if (member == null) {
            return null;
        }
        PeopleFlowMemberBo memberBo = new PeopleFlowMemberBo();
        memberBo.setMemberId(member.getMemberId());
        memberBo.setMemberTypeCode(member.getMemberType().getCode());
        if (member.getActionRequestPolicy() != null) {
            memberBo.setActionRequestPolicyCode(member.getActionRequestPolicy().getCode());
        }
        memberBo.setResponsibilityId(member.getResponsibilityId());
        memberBo.setPriority(member.getPriority());
        memberBo.setDelegates(new ArrayList<PeopleFlowDelegateBo>());
        for (PeopleFlowDelegate delegate : member.getDelegates()) {
            memberBo.getDelegates().add(PeopleFlowDelegateBo.from(delegate));
        }
        return memberBo;
    }

}
