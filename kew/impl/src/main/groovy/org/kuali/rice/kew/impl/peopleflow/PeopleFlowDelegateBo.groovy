package org.kuali.rice.kew.impl.peopleflow

import org.kuali.rice.core.api.delegation.DelegationType
import org.kuali.rice.core.api.membership.MemberType
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDelegate

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

import org.kuali.rice.kew.api.peopleflow.PeopleFlowDelegateContract
import org.kuali.rice.kew.api.action.ActionRequestPolicy
import org.kuali.rice.kim.api.identity.Person
import org.kuali.rice.kim.framework.group.GroupEbo
import org.kuali.rice.kim.framework.role.RoleEbo
import org.kuali.rice.kim.api.services.KimApiServiceLocator

import org.kuali.rice.krad.service.ModuleService
import org.kuali.rice.krad.service.KRADServiceLocatorWeb

/**
 * mapped entity for PeopleFlow members
 */
class PeopleFlowDelegateBo extends PersistableBusinessObjectBase implements PeopleFlowDelegateContract {

    String id
    String peopleFlowMemberId
    String memberId
    String memberTypeCode
    String actionRequestPolicyCode
    String delegationTypeCode
    String responsibilityId

    // non-persisted
    String memberName;
    Person person;
    GroupEbo group;
    RoleEbo role;

    public Person getPerson() {
        if (MemberType.PRINCIPAL.getCode().equals(memberTypeCode)) {
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

    @Override
    DelegationType getDelegationType() {
        return DelegationType.fromCode(delegationTypeCode)
    }

    void setDelegationType(DelegationType delegationType) {
        this.delegationTypeCode = delegationType.getCode()
    }

    public static PeopleFlowDelegate to(PeopleFlowDelegateBo delegateBo) {
        if (delegateBo == null) {
            return null
        }
        PeopleFlowDelegate.Builder builder = PeopleFlowDelegate.Builder.create(delegateBo)
        return builder.build()
    }

    public static PeopleFlowDelegateBo from(PeopleFlowDelegate delegate) {
        if (delegate == null) {
            return null
        }
        PeopleFlowDelegateBo delegateBo = new PeopleFlowDelegateBo()
        delegateBo.setMemberId(delegate.getMemberId())
        delegateBo.setMemberType(delegate.getMemberType())
        if (delegate.getActionRequestPolicy() != null) {
            delegateBo.setActionRequestPolicyCode(delegate.getActionRequestPolicy().getCode())
        }
        if (delegate.getDelegationType() != null) {
            delegateBo.setDelegationTypeCode(delegate.getDelegationType().getCode())
        }
        delegateBo.setResponsibilityId(delegate.getResponsibilityId())
        return delegateBo
    }

}
