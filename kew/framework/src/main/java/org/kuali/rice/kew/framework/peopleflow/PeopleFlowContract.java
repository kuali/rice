package org.kuali.rice.kew.framework.peopleflow;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.core.api.namespace.Namespace;

import java.util.List;
import java.util.Map;

/**
 * Interface contract for PeopleFlows.  TODO: ...
 */
public interface PeopleFlowContract extends Identifiable, Inactivatable, Versioned {

    /**
     * @return the name for this {@link PeopleFlowContract}.  Will never be null.
     */
    String getName();

    /**
     * @return the name for this {@link PeopleFlowContract}.  Will never be null.
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
     * @return the {@link PeopleFlowMemberContract}s for this {@link PeopleFlowContract}. Will never return null.
     */
    List<? extends PeopleFlowMemberContract> getMembers();

    /**
     * @return the attributes for this {@link PeopleFlowContract}. Will never return null.
     */
    Map<String, String> getAttributes();

}
