package org.kuali.rice.kim.api.entity.privacy;


import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;

public interface EntityPrivacyPreferencesContract extends Versioned, GloballyUnique {

    /**
     * Gets this id of the parent entity object.
     * @return the entity id for this {@link EntityPrivacyPreferencesContract}
     */
    String getEntityId();

    /**
     * This is value designating if Entity Name should be suppressed.
     *
     * <p>
     * This is a boolean value that shows if entity names should be suppressed or not.
     * </p>
     *
     * @return suppressName
     */
    boolean isSuppressName();

    /**
     * This is value designating if Entity Address should be suppressed.
     *
     * <p>
     * This is a boolean value that shows if entity addresses should be suppressed or not.
     * </p>
     *
     * @return suppressAddress
     */
	boolean isSuppressAddress();

    /**
     * This is value designating if Entity Email should be suppressed.
     *
     * <p>
     * This is a boolean value that shows if entity emails should be suppressed or not.
     * </p>
     *
     * @return suppressEmail
     */
	boolean isSuppressEmail();

    /**
     * This is value designating if Entity Phone should be suppressed.
     *
     * <p>
     * This is a boolean value that shows if entity phones should be suppressed or not.
     * </p>
     *
     * @return suppressPhone
     */
	boolean isSuppressPhone();

    /**
     * This is value designating if Entity Personal information should be suppressed.
     *
     * <p>
     * This is a boolean value that shows if entity personal information should be suppressed or not.
     * </p>
     *
     * @return suppressPersonal
     */
	boolean isSuppressPersonal();
}
