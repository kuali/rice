package org.kuali.rice.kew.impl.peopleflow

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.kew.api.peopleflow.PeopleFlowMemberContract
import org.kuali.rice.kew.api.peopleflow.MemberType

import org.kuali.rice.kew.api.peopleflow.PeopleFlowMember
import org.springframework.util.MultiValueMap
import org.springframework.util.LinkedMultiValueMap
import org.apache.commons.collections.CollectionUtils
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDelegate
import org.kuali.rice.kew.api.action.DelegationType;

/**
 * mapped entity for PeopleFlow members
 */
class PeopleFlowMemberBo extends PersistableBusinessObjectBase implements PeopleFlowMemberContract {

    String id
    String peopleFlowId
    String memberId
    String memberTypeCode
    int priority

    List<PeopleFlowDelegateBo> delegates = new ArrayList<PeopleFlowDelegateBo>();

    @Override
    MemberType getMemberType() {
        return MemberType.fromCode(memberTypeCode);
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
        memberBo.setPriority(member.getPriority());
        memberBo.setDelegates(new ArrayList<PeopleFlowDelegateBo>());
        for (PeopleFlowDelegate delegate : member.getDelegates()) {
            memberBo.getDelegates().add(PeopleFlowDelegateBo.from(delegate));
        }
        return memberBo;
    }

}
