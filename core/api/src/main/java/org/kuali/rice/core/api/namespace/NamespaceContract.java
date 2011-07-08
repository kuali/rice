package org.kuali.rice.core.api.namespace;

import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.ImmutableInactivatable;

/**
 * Defines the contract for a Namespace.  A namespace is a mechanism for partitioning of data into
 * areas of responsibility.  Since much of the Kuali Rice middleware is shared across multiple integrating
 * applications, this notion of a namespace is a critical element in keeping related data elements
 * grouped together and isolated from those to which they should have no relation or access.
 */
public interface NamespaceContract extends Versioned, GloballyUnique, ImmutableInactivatable, Coded {

    /**
     * This the id of the application which owns this Namespace.  This cannot be null or a blank string.
     *
     * <p>
     * It is a way of assigning the Namespace to a specific rice application or rice ecosystem.
     * </p>
     *
     * @return application id
     */
    String getApplicationId();

    /**
     * This the name for the Namespace.  This can be null or a blank string.
     *
     * @return name
     */
    String getName();
}
