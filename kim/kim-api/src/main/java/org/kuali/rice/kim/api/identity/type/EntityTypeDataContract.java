package org.kuali.rice.kim.api.identity.type;


import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kim.api.identity.TypeContract;
import org.kuali.rice.kim.api.identity.address.EntityAddressContract;
import org.kuali.rice.kim.api.identity.email.EntityEmailContract;
import org.kuali.rice.kim.api.identity.phone.EntityPhoneContract;

import java.util.List;

public interface EntityTypeDataContract extends Versioned, GloballyUnique, Inactivatable {
    /**
     * Gets the id of the parent identity object.
     * @return the identity id for this {@link EntityAddressContract}
     */
    String getEntityId();

    /**
     * Gets this entityTypeCode of the {@link EntityAddressContract}'s object.
     * @return the identity type code for this {@link EntityAddressContract}
     */
    String getEntityTypeCode();

    /**
     * Gets this identity Type of the {@link EntityTypeDataContract}'s object.
     * @return the identity type for this {@link EntityTypeDataContract}
     */
	TypeContract getEntityType();

	/**
     * Gets this {@link EntityTypeDataContract}'s List of {@link org.kuali.rice.kim.api.identity.address.EntityAddress}S.
     * @return the List of {@link org.kuali.rice.kim.api.identity.address.EntityAddressContract}S for this {@link EntityTypeDataContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends EntityAddressContract> getAddresses();

	/**
     * Gets this {@link EntityTypeDataContract}'s List of {@link org.kuali.rice.kim.api.identity.email.EntityEmailContract}S.
     * @return the List of {@link org.kuali.rice.kim.api.identity.email.EntityEmailContract}S for this {@link EntityTypeDataContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends EntityEmailContract> getEmailAddresses();
	
	/**
     * Gets this {@link EntityTypeDataContract}'s List of {@link org.kuali.rice.kim.api.identity.phone.EntityPhone}S.
     * @return the List of {@link org.kuali.rice.kim.api.identity.phone.EntityPhoneContract}S for this {@link EntityTypeDataContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends EntityPhoneContract> getPhoneNumbers();
	
	/** 
	 * Returns the default address record for the identity.  If no default is defined, then
	 * it returns the first one found.  If none are defined, it returns null.
	 */
	EntityAddressContract getDefaultAddress();

	/**
	 *  Returns the default email record for the identity.  If no default is defined, then
	 * it returns the first one found.  If none are defined, it returns null.
	 */
	EntityEmailContract getDefaultEmailAddress();

	/** 
	 * Returns the default phone record for the identity.  If no default is defined, then
	 * it returns the first one found.  If none are defined, it returns null.
	 */
	EntityPhoneContract getDefaultPhoneNumber();
}
