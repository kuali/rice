package org.kuali.rice.kew.api.peopleflow;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;

import java.util.List;

/**
 * Interface contract for PeopleFlowDefinition members.  TODO: ...
 */
public interface PeopleFlowMemberContract {

    /**
     * @return the id of the member.  This will key in to different types depending on the {@link MemberType} of the
     * instance.
     */
    String getMemberId();

    /**
     * @return the {@link MemberType} of this member.  Never null.
     */
    MemberType getMemberType();

    /**
     * @return the priority of the member.  This is equivalent to the sequential stop in the PeopleFlowDefinition, which means
     * lower integer value equates to higher priority.  The minimum priority is 1.
     */
    int getPriority();

    /**
     * @return the list of delegates for this member.  Should never be null but may be an empty list in the case where
     * this member has no delegates
     */
    List<? extends PeopleFlowDelegateContract> getDelegates();

}
