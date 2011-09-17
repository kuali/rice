package org.kuali.rice.kew.api.peopleflow;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;

/**
 * Interface contract for PeopleFlow members.  TODO: ...
 */
public interface PeopleFlowMemberContract extends Identifiable, Versioned {

    /**
     * @return the id for the {@link PeopleFlowContract} this member belongs to.  May be null before persistence.
     */
    String getPeopleFlowId();

    /**
     * @return the {@link MemberType} of this member.  Never null.
     */
    MemberType getMemberType();

    /**
     * @return the id of the member.  This will key in to different types depending on the {@link MemberType} of the
     * instance.
     */
    String getMemberId();

    /**
     * @return the priority of the member.  This is equivalent to the sequential stop in the PeopleFlow, which means
     * lower integer value equates to higher priority.  The minimum priority is 1.
     * May be null if {@link #getDelegatedFromId()} is non-null.
     */
    int getPriority();

    /**
     * @return the id of the {@link PeopleFlowMemberContract} that the instance is a delegate for.  Must be null if
     * this member is not a delegate.
     */
    String getDelegatedFromId();

}
