package org.kuali.rice.kew.api.peopleflow;

import org.kuali.rice.kew.api.action.DelegationType;

public interface PeopleFlowDelegateContract {

    String getMemberId();

    MemberType getMemberType();

    DelegationType getDelegationType();

}
