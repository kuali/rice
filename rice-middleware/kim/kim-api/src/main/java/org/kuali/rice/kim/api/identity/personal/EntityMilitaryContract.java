package org.kuali.rice.kim.api.identity.personal;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kim.api.identity.CodedAttributeContract;

public interface EntityMilitaryContract extends Versioned, GloballyUnique, Inactivatable, Identifiable {
    /**
     * Gets this {@link EntityDisabilityContract}'s identity id.
     * @return the identity id for this {@link EntityDisabilityContract}, or null if none has been assigned.
     */
    String getEntityId();

    /**
     * Returns the {@link EntityBioDemographicsContract}'s relationship with the military .
     */
    CodedAttributeContract getRelationshipStatus();

    boolean isSelectiveService();

    String getSelectiveServiceNumber();

    DateTime getDischargeDate();
}
