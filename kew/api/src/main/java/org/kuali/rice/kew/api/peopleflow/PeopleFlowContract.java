package org.kuali.rice.kew.api.peopleflow;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

import java.util.List;
import java.util.Map;

/**
 * Contract interface for a PeopleFlow.  A PeopleFlow is simply a collections of members and their (optional) delegates.
 * Each member of a PeopleFlow has a priority number assigned to it, which indicates the order in which members should
 * be processed during execution of the flow.
 *
 * <p>Priority is ordered by the lowest priority number being the "beginning" of the flow.  It is possible for one or
 * more members to have the same priority number, in which case they should be processed in parallel.</p>
 *
 * <p>Members of a flow can be one of either a principal, group, or role which is defined by the
 * {@link PeopleFlowMemberContract}.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface PeopleFlowContract extends Identifiable, Inactivatable, Versioned {

    /**
     * @return the name for this {@link PeopleFlowContract}.  Will never be null.
     */
    String getName();

    /**
     * @return the namespace for this {@link PeopleFlowContract}.  Will never be null.
     */
    String getNamespace();

    /**
     * @return the type id for this {@link PeopleFlowContract}.  Will never be null.
     */
    String getTypeId();

    /**
     * @return the name for this {@link PeopleFlowContract}.  May be null, but not empty.
     */
    String getDescription();

    /**
     * Returns the list of members for this flow, sorted from lowest to highest priority number.
     *
     * @return the {@link PeopleFlowMemberContract}s for this {@link PeopleFlowContract}. Will never return null.
     */
    List<? extends PeopleFlowMemberContract> getMembers();

    /**
     * @return the attributes for this {@link PeopleFlowContract}. Will never return null.
     */
    Map<String, String> getAttributes();

}
