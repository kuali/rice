package org.kuali.rice.kim.api.entity.citizenship;


import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kim.api.entity.TypeContract;

import java.sql.Timestamp;

public interface EntityCitizenshipContract extends Versioned, GloballyUnique, Inactivatable, Identifiable {
	/**
     * Gets this id of the parent entity object.
     * @return the entity id for this {@link EntityCitizenshipContract}
     */
    String getEntityId();

    /**
     * Gets this {@link EntityCitizenshipContract}'s citizenship status object.
     * @return the Type object of citizenship status for this {@link EntityCitizenshipContract}, or null if none has been assigned.
     */
	TypeContract getStatus();

	/**
     * Gets this {@link EntityCitizenshipContract}'s country code.
     * @return the country code for this {@link EntityCitizenshipContract}, or null if none has been assigned.
     */
	String getCountryCode();

	/**
     * Gets this {@link EntityCitizenshipContract}'s start date.
     * @return the start date for this {@link EntityCitizenshipContract}, or null if none has been assigned.
     */
	Timestamp getStartDate();

	/**
     * Gets this {@link EntityCitizenshipContract}'s end date.
     * @return the end date for this {@link EntityCitizenshipContract}, or null if none has been assigned.
     */
	Timestamp getEndDate();
}
