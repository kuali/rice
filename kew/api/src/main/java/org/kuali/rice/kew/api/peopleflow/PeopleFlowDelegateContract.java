package org.kuali.rice.kew.api.peopleflow;

import org.kuali.rice.kew.api.action.ActionRequestPolicy;
import org.kuali.rice.kew.api.action.DelegationType;

public interface PeopleFlowDelegateContract {

    String getMemberId();

    MemberType getMemberType();

    /**
     * Returns the action request policy to use for this people flow member.  This value is only applicable in the
     * case where the {@code MemberType} is {@code ROLE}.  If the member type is anything else, this value will not
     * be considered and should ideally be set to null
     *
     * @return the action request policy to use for this people flow member if it is a role member, null if this
     * member has no request policy
     */
    ActionRequestPolicy getActionRequestPolicy();

    DelegationType getDelegationType();

    /**
     * Returns the responsibility id of this people flow delegate member.  This is a unique id which KEW can use to help
     * identify and track the responsibility represented by this delegation.  It will be associated with any action
     * requests that are generated from this people flow delegation.
     *
     * @return the responsibility id for this people flow membership delegate
     */
    String getResponsibilityId();

}
