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

import org.kuali.rice.kns.bo.DefaultableInactivateable;

/**
 * name information for a KIM entity
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimEntityName extends DefaultableInactivateable {
	
    /**
     * Gets this {@link KimEntityName}'s id.
     * @return the id for this {@link KimEntityName}, or null if none has been assigned.
     */
	String getEntityNameId();
	
	/**
     * Gets this {@link KimEntityName}'s type code.
     * @return the type code for this {@link KimEntityName}, or null if none has been assigned.
     */
	String getNameTypeCode();
	
	/**
     * Gets this {@link KimEntityName}'s first name.
     * @return the first name for this {@link KimEntityName}, or null if none has been assigned.
     */
	String getFirstName();
	
	/**
     * Gets this {@link KimEntityName}'s unmasked first name.
     * @return the unmasked first name for this {@link KimEntityName}, or null if none has been assigned.
     */
	String getFirstNameUnmasked();
	
	/**
     * Gets this {@link KimEntityName}'s middle name.
     * @return the middle name for this {@link KimEntityName}, or null if none has been assigned.
     */
	String getMiddleName();
	
	/**
     * Gets this {@link KimEntityName}'s unmasked middle name.
     * @return the unmasked middle name for this {@link KimEntityName}, or null if none has been assigned.
     */
	String getMiddleNameUnmasked();
	
	/**
     * Gets this {@link KimEntityName}'s last name.
     * @return the last name for this {@link KimEntityName}, or null if none has been assigned.
     */
	String getLastName();
	
	/**
     * Gets this {@link KimEntityName}'s unmasked last name.
     * @return the unmasked last name for this {@link KimEntityName}, or null if none has been assigned.
     */
	String getLastNameUnmasked();
	
	/**
     * Gets this {@link KimEntityName}'s title.
     * @return the title for this {@link KimEntityName}, or null if none has been assigned.
     */
	String getTitle();
	
	/**
     * Gets this {@link KimEntityName}'s unmasked title.
     * @return the unmasked title for this {@link KimEntityName}, or null if none has been assigned.
     */
	String getTitleUnmasked();
	
	/**
     * Gets this {@link KimEntityName}'s suffix.
     * @return the suffix for this {@link KimEntityName}, or null if none has been assigned.
     */
	String getSuffix();
	
	/**
     * Gets this {@link KimEntityName}'s unmasked suffix.
     * @return the unmasked suffix for this {@link KimEntityName}, or null if none has been assigned.
     */
	String getSuffixUnmasked();
	
	/**
	 * Return the entire name as the person or system wants it displayed.
	 */
	String getFormattedName();
	
	/**
     * Gets this {@link KimEntityName}'s unmasked formatted name.
     */
	String getFormattedNameUnmasked();
	
	boolean isSuppressName();
}
