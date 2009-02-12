/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
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
 * Represents the entity type associated with a particular entity. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface KimEntityEntityType extends Inactivateable {

	String getEntityTypeCode();
	
	EntityType getEntityType();
	
	/**
	 * Return the list of EntityAddress objects associated with this EntityType.
	 * 
	 * The returned list will never be null, the implementation will generate an
	 * empty list as needed.
	 */
	List<KimEntityAddress> getAddresses();

	/**
	 * Return the list of EntityEmail objects associated with this EntityType.
	 * 
	 * The returned list will never be null, the implementation will generate an
	 * empty list as needed.
	 */
	List<KimEntityEmail> getEmailAddresses();
	
	/**
	 * Return the list of EntityPhone objects associated with this EntityType.
	 * 
	 * The returned list will never be null, the implementation will generate an
	 * empty list as needed.
	 */
	List<KimEntityPhone> getPhoneNumbers();
	
	/** Returns the default address record for the entity.  If no default is defined, then
	 * it returns the first one found.  If none are defined, it returns null.
	 */
	KimEntityAddress getDefaultAddress();

	/** Returns the default email record for the entity.  If no default is defined, then
	 * it returns the first one found.  If none are defined, it returns null.
	 */
	KimEntityEmail getDefaultEmailAddress();

	/** Returns the default phone record for the entity.  If no default is defined, then
	 * it returns the first one found.  If none are defined, it returns null.
	 */
	KimEntityPhone getDefaultPhoneNumber();
}
