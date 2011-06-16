package org.kuali.rice.kim.api.identity.affiliation;

import org.kuali.rice.core.api.mo.common.Defaultable;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kim.api.identity.TypeContract;

public interface EntityAffiliationContract extends Versioned, GloballyUnique, Defaultable, Inactivatable, Identifiable {

    /**
     * Gets this id of the parent identity object.
     * @return the identity id for this {@link EntityAddressContract}
     */
    String getEntityId();

	/**
     * Gets this {@link KimEntityAffiliation}'s type.
     * @return the type for this {@link KimEntityAffiliation}, or null if none has been assigned.
     */
	EntityAffiliationTypeContract getAffiliationType();

	/**
     * Gets this {@link KimEntityAffiliation}'s campus code.
     * @return the campus code for this {@link KimEntityAffiliation}, or null if none has been assigned.
     */
	String getCampusCode();
}
