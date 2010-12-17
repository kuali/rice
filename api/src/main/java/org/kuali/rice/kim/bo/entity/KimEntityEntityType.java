/*
 * Copyright 2007-2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.bo.entity;

import java.util.List;

import org.kuali.rice.kim.bo.reference.EntityType;
import org.kuali.rice.kns.bo.Inactivateable;

/**
 * the entity type for a KIM entity. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimEntityEntityType extends Inactivateable {

	String getEntityTypeCode();
	
	EntityType getEntityType();

	/**
     * Gets this {@link KimEntityEntityType}'s List of {@link KimEntityAddress}S.
     * @return the List of {@link KimEntityAddress}S for this {@link KimEntityEntityType}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends KimEntityAddress> getAddresses();

	/**
     * Gets this {@link KimEntityEntityType}'s List of {@link KimEntityEmail}S.
     * @return the List of {@link KimEntityEmail}S for this {@link KimEntityEntityType}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends KimEntityEmail> getEmailAddresses();
	
	/**
     * Gets this {@link KimEntityEntityType}'s List of {@link KimEntityPhone}S.
     * @return the List of {@link KimEntityPhone}S for this {@link KimEntityEntityType}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends KimEntityPhone> getPhoneNumbers();
	
	/** 
	 * Returns the default address record for the entity.  If no default is defined, then
	 * it returns the first one found.  If none are defined, it returns null.
	 */
	KimEntityAddress getDefaultAddress();

	/**
	 *  Returns the default email record for the entity.  If no default is defined, then
	 * it returns the first one found.  If none are defined, it returns null.
	 */
	KimEntityEmail getDefaultEmailAddress();

	/** 
	 * Returns the default phone record for the entity.  If no default is defined, then
	 * it returns the first one found.  If none are defined, it returns null.
	 */
	KimEntityPhone getDefaultPhoneNumber();
}
