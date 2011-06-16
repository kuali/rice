package org.kuali.rice.kim.api.identity.email;

import org.kuali.rice.core.api.mo.common.Defaultable;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kim.api.identity.TypeContract;

public interface EntityEmailContract extends Versioned, GloballyUnique, Defaultable, Inactivatable, Identifiable {
       /**
     * Gets this id of the parent identity object.
     * @return the identity id for this {@link EntityEmailContract}
     */
    String getEntityId();

    /**
     * Gets this entityTypeCode of the {@link EntityEmailContract}'s object.
     * @return the identity type code for this {@link EntityEmailContract}
     */
    String getEntityTypeCode();

	/**
     * Gets this {@link org.kuali.rice.kim.api.identity.email.EntityEmail}'s type code.
     * @return the type code for this {@link org.kuali.rice.kim.api.identity.email.EntityEmail}, or null if none has been assigned.
     */
	TypeContract getEmailType();

	/**
     * Gets this {@link EntityEmail}'s email address.
     * @return the email address for this {@link EntityEmail}, or null if none has been assigned.
     */
	String getEmailAddress();

	/**
     * Gets this {@link EntityEmail}'s unmasked email address.
     * @return the unmasked email address for this {@link EntityEmail}, or null if none has been assigned.
     */
	String getEmailAddressUnmasked();
    /**
     * Returns a boolean value that determines if email fields should be suppressed.
     * @return boolean value that determines if email should be suppressed.
     */
	boolean isSuppressEmail();
}
