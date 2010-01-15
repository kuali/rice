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
 * an affiliation for a KIM entity
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimEntityAffiliation extends DefaultableInactivateable {

    /**
     * Gets this {@link KimEntityAffiliation}'s id.
     * @return the id for this {@link KimEntityAffiliation}, or null if none has been assigned.
     */
	String getEntityAffiliationId();

	/**
     * Gets this {@link KimEntityAffiliation}'s type code.
     * @return the type code for this {@link KimEntityAffiliation}, or null if none has been assigned.
     */
	String getAffiliationTypeCode();
	
	/**
     * Gets this {@link KimEntityAffiliation}'s campus code.
     * @return the campus code for this {@link KimEntityAffiliation}, or null if none has been assigned.
     */
	String getCampusCode();
		
}
