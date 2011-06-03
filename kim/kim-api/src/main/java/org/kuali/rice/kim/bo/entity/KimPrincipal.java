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

import java.io.Serializable;

/**
 * principal for a KIM entity
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface KimPrincipal extends Serializable {
    
    /**
     * Gets this {@link KimPrincipal}'s id.
     * @return the id for this {@link KimPrincipal}, or null if none has been assigned.
     */
	String getPrincipalId();
	
	/**
     * Gets this {@link KimPrincipal}'s name.
     * @return the name for this {@link KimPrincipal}, or null if none has been assigned.
     */
	String getPrincipalName();
	
	/**
     * Gets this {@link KimPrincipal}'s password.
     * @return the password for this {@link KimPrincipal}, or null if none has been assigned.
     */
	String getPassword();
	
	/**
     * Gets this {@link KimPrincipal}'s entity id.
     * @return the entity id for this {@link KimPrincipal}, or null if none has been assigned.
     */
	String getEntityId();
	
	boolean isActive();
}
