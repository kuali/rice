package org.kuali.rice.kew.impl.peopleflow

import org.apache.commons.collections.CollectionUtils
import org.kuali.rice.kew.api.action.DelegationType
import org.kuali.rice.kew.api.peopleflow.MemberType
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDelegate
import org.kuali.rice.kew.api.peopleflow.PeopleFlowMember
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDelegateContract
import org.kuali.rice.kew.api.action.ActionRequestPolicy

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

    @Override
    MemberType getMemberType() {
        return MemberType.fromCode(memberTypeCode)
    }

    @Override
    ActionRequestPolicy getActionRequestPolicy() {
        return ActionRequestPolicy.fromCode(actionRequestPolicyCode)
    }

    @Override
    DelegationType getDelegationType() {
        return DelegationType.fromCode(delegationTypeCode)
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
        delegateBo.setMemberTypeCode(delegate.getMemberType().getCode())
        if (delegate.getActionRequestPolicy() != null) {
            delegateBo.setActionRequestPolicyCode(delegate.getActionRequestPolicy().getCode())
        }
        delegateBo.setDelegationTypeCode(delegate.getDelegationType().getCode())
        delegateBo.setResponsibilityId(delegate.getResponsibilityId())
        return delegateBo
    }

}
