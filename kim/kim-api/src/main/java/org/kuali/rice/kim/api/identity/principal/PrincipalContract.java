package org.kuali.rice.kim.api.identity.principal;


import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

public interface PrincipalContract extends Inactivatable, Versioned, GloballyUnique {
   /**
     * Gets this {@link PrincipalContract}'s id.
     * @return the id for this {@link PrincipalContract}, or null if none has been assigned.
     */
	String getPrincipalId();
	
	/**
     * Gets this {@link PrincipalContract}'s name.
     * @return the name for this {@link PrincipalContract}, this value cannot be null.
     */
	String getPrincipalName();
	
	/**
     * Gets this {@link PrincipalContract}'s password.
     * @return the password for this {@link PrincipalContract}, or null if none has been assigned.
     */
	String getPassword();
	
	/**
     * Gets this {@link PrincipalContract}'s identity id.
     * @return the identity id for this {@link PrincipalContract}, or null if none has been assigned.
     */
	String getEntityId();
	
}
