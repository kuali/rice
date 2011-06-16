/*
 * Copyright 2007-2009 The Kuali Foundation
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
 * visa information for a KIM identity
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface KimEntityVisa {

    /**
     * Gets this {@link KimEntityVisa}'s id.
     * @return the id for this {@link KimEntityVisa}, or null if none has been assigned.
     */
	String getId();
	
	/**
     * Gets this {@link KimEntityVisa}'s identity id.
     * @return the identity id for this {@link KimEntityVisa}, or null if none has been assigned.
     */
	String getEntityId();
	
	/**
     * Gets this {@link KimEntityVisa}'s viss type key.
     * @return the viss type key for this {@link KimEntityVisa}, or null if none has been assigned.
     */
	String getVisaTypeKey();
	
	/**
     * Gets this {@link KimEntityVisa}'s visa entry.
     * @return the visa entry for this {@link KimEntityVisa}, or null if none has been assigned.
     */
	String getVisaEntry();
	
	/**
     * Gets this {@link KimEntityVisa}'s visa id.
     * @return the visa id for this {@link KimEntityVisa}, or null if none has been assigned.
     */
	String getVisaId();
}
