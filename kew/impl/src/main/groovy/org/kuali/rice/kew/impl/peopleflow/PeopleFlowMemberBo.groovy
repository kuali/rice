package org.kuali.rice.kew.impl.peopleflow

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.kew.api.peopleflow.PeopleFlowMemberContract
import org.kuali.rice.kew.api.peopleflow.MemberType

import org.kuali.rice.kew.api.peopleflow.PeopleFlowMemberDefinition;

/**
 * mapped entity for PeopleFlowDefinition members
 */
class PeopleFlowMemberBo extends PersistableBusinessObjectBase implements PeopleFlowMemberContract {
    def String id
    def String peopleFlowId
    def String memberTypeCode
    def String memberId
    def int priority
    def String delegatedFromId

    MemberType getMemberType() {
        return MemberType.getByCode(memberTypeCode);
    }

    public static PeopleFlowMemberBo from(PeopleFlowMemberDefinition member) {
        PeopleFlowMemberBo result = new PeopleFlowMemberBo();

        result.id = member.getId();
        result.peopleFlowId = member.getPeopleFlowId();
        result.memberTypeCode = member.getMemberType().code;
        result.memberId = member.getMemberId();
        result.priority = member.getPriority();
        result.delegatedFromId = member.getDelegatedFromId();
        result.setVersionNumber(member.getVersionNumber());
    }

    public static PeopleFlowMemberDefinition to(PeopleFlowMemberBo bo) {
        return PeopleFlowMemberDefinition.Builder.create(bo).build();
    }

}
