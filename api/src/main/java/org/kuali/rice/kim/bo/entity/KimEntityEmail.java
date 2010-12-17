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

/**
 * email information for a KIM entity
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimEntityEmail extends KimDefaultableEntityTypeData {

    /**
     * Gets this {@link KimEntityEmail}'s id.
     * @return the id for this {@link KimEntityEmail}, or null if none has been assigned.
     */
	String getEntityEmailId();
	
	/**
     * Gets this {@link KimEntityEmail}'s email type code.
     * @return the email type code for this {@link KimEntityEmail}, or null if none has been assigned.
     */
	String getEmailTypeCode();
	
	/**
     * Gets this {@link KimEntityEmail}'s entity type code.
     * @return the entity type code for this {@link KimEntityEmail}, or null if none has been assigned.
     */
	String getEntityTypeCode();
	
	/**
     * Gets this {@link KimEntityEmail}'s email address.
     * @return the email address for this {@link KimEntityEmail}, or null if none has been assigned.
     */
	String getEmailAddress();
	
	/**
     * Gets this {@link KimEntityEmail}'s unmasked email address.
     * @return the unmasked email address for this {@link KimEntityEmail}, or null if none has been assigned.
     */
	String getEmailAddressUnmasked();
	
	boolean isSuppressEmail();
}
