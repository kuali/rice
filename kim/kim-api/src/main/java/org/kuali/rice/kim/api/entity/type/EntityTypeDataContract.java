package org.kuali.rice.kim.api.entity.type;


import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kim.api.entity.TypeContract;
import org.kuali.rice.kim.api.entity.address.EntityAddressContract;
import org.kuali.rice.kim.api.entity.email.EntityEmail;
import org.kuali.rice.kim.api.entity.email.EntityEmailContract;
import org.kuali.rice.kim.api.entity.phone.EntityPhoneContract;

import java.util.List;

public interface EntityTypeDataContract extends Versioned, GloballyUnique, Inactivatable {
    /**
     * Gets the id of the parent entity object.
     * @return the entity id for this {@link EntityAddressContract}
     */
    String getEntityId();

    /**
     * Gets this entityTypeCode of the {@link EntityAddressContract}'s object.
     * @return the entity type code for this {@link EntityAddressContract}
     */
    String getEntityTypeCode();

    /**
     * Gets this entity Type of the {@link EntityTypeDataContract}'s object.
     * @return the entity type for this {@link EntityTypeDataContract}
     */
	TypeContract getEntityType();

	/**
     * Gets this {@link EntityTypeDataContract}'s List of {@link org.kuali.rice.kim.api.entity.address.EntityAddress}S.
     * @return the List of {@link org.kuali.rice.kim.api.entity.address.EntityAddressContract}S for this {@link EntityTypeDataContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends EntityAddressContract> getAddresses();

	/**
     * Gets this {@link EntityTypeDataContract}'s List of {@link org.kuali.rice.kim.api.entity.email.EntityEmailContract}S.
     * @return the List of {@link org.kuali.rice.kim.api.entity.email.EntityEmailContract}S for this {@link EntityTypeDataContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends EntityEmailContract> getEmailAddresses();
	
	/**
     * Gets this {@link EntityTypeDataContract}'s List of {@link org.kuali.rice.kim.api.entity.phone.EntityPhone}S.
     * @return the List of {@link org.kuali.rice.kim.api.entity.phone.EntityPhoneContract}S for this {@link EntityTypeDataContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends EntityPhoneContract> getPhoneNumbers();
	
	/** 
	 * Returns the default address record for the entity.  If no default is defined, then
	 * it returns the first one found.  If none are defined, it returns null.
	 */
	EntityAddressContract getDefaultAddress();

	/**
	 *  Returns the default email record for the entity.  If no default is defined, then
	 * it returns the first one found.  If none are defined, it returns null.
	 */
	EntityEmailContract getDefaultEmailAddress();

	/** 
	 * Returns the default phone record for the entity.  If no default is defined, then
	 * it returns the first one found.  If none are defined, it returns null.
	 */
	EntityPhoneContract getDefaultPhoneNumber();
}
