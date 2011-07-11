package org.kuali.rice.kim.api.identity.name;

import org.kuali.rice.core.api.mo.common.Defaultable;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kim.api.identity.TypeContract;

public interface EntityNameContract extends Versioned, GloballyUnique, Defaultable, Inactivatable, Identifiable {
    /**
     * Gets this {@link Entity}'s id.
     * @return the id for this {@link EntityNameContract}, or null if none has been assigned.
     */
	String getEntityId();

	/**
     * Gets this {@link EntityNameContract}'s TypeContract.
     * @return the type for this {@link EntityNameContract}, or null if none has been assigned.
     */
	TypeContract getNameType();

	/**
     * Gets this {@link EntityNameContract}'s first name.
     * @return the first name for this {@link EntityNameContract}, or null if none has been assigned.
     */
	String getFirstName();

	/**
     * Gets this {@link EntityNameContract}'s unmasked first name.
     * @return the unmasked first name for this {@link EntityNameContract}, or null if none has been assigned.
     */
	String getFirstNameUnmasked();

	/**
     * Gets this {@link EntityNameContract}'s middle name.
     * @return the middle name for this {@link EntityNameContract}, or null if none has been assigned.
     */
	String getMiddleName();

	/**
     * Gets this {@link EntityNameContract}'s unmasked middle name.
     * @return the unmasked middle name for this {@link EntityNameContract}, or null if none has been assigned.
     */
	String getMiddleNameUnmasked();

	/**
     * Gets this {@link EntityNameContract}'s last name.
     * @return the last name for this {@link EntityNameContract}, or null if none has been assigned.
     */
	String getLastName();

	/**
     * Gets this {@link EntityNameContract}'s unmasked last name.
     * @return the unmasked last name for this {@link EntityNameContract}, or null if none has been assigned.
     */
	String getLastNameUnmasked();

	/**
     * Gets this {@link EntityNameContract}'s title.
     * @return the title for this {@link EntityNameContract}, or null if none has been assigned.
     */
	String getTitle();

	/**
     * Gets this {@link EntityNameContract}'s unmasked title.
     * @return the unmasked title for this {@link EntityNameContract}, or null if none has been assigned.
     */
	String getTitleUnmasked();

	/**
     * Gets this {@link EntityNameContract}'s suffix.
     * @return the suffix for this {@link EntityNameContract}, or null if none has been assigned.
     */
	String getSuffix();

	/**
     * Gets this {@link EntityNameContract}'s unmasked suffix.
     * @return the unmasked suffix for this {@link EntityNameContract}, or null if none has been assigned.
     */
	String getSuffixUnmasked();

	/**
	 * Return the entire name as the person or system wants it displayed.
     * @return the complete name in the format of "lastName, firstName middleName"
	 */
	String getFormattedName();

	/**
     * Gets this {@link EntityNameContract}'s unmasked formatted name.
     * @return the complete name in the format of "lastName, firstName middleName"
     */
	String getFormattedNameUnmasked();

    /**
     * Returns a boolean value that determines if email fields should be suppressed.
     * @return boolean value that determines if email should be suppressed.
     */
	boolean isSuppressName();
}
