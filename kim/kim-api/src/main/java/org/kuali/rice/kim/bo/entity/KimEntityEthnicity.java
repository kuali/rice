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
 * ethnicity information for a KIM identity
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface KimEntityEthnicity {

    /**
     * Gets this {@link KimEntityEthnicity}'s id.
     * @return the id for this {@link KimEntityEthnicity}, or null if none has been assigned.
     */
	String getId();
	
	/**
     * Gets this {@link KimEntityEthnicity}'s identity id.
     * @return the identity id for this {@link KimEntityEthnicity}, or null if none has been assigned.
     */
	String getEntityId();
	
	/**
     * Gets this {@link KimEntityEthnicity}'s ethnicity code.
     * @return the ethnicity code for this {@link KimEntityEthnicity}, or null if none has been assigned.
     */
	String getEthnicityCode();
	
	/**
     * Gets this {@link KimEntityEthnicity}'s unmasked ethnicity code.
     * @return the unmasked ethnicity code for this {@link KimEntityEthnicity}, or null if none has been assigned.
     */
	String getEthnicityCodeUnmasked();
	
	/**
     * Gets this {@link KimEntityEthnicity}'s sub-ethnicity code.
     * @return the sub-ethnicity code for this {@link KimEntityEthnicity}, or null if none has been assigned.
     */
	String getSubEthnicityCode();
	
	/**
     * Gets this {@link KimEntityEthnicity}'s unmasked sub-ethnicity code.
     * @return the unmasked sub-ethnicity code for this {@link KimEntityEthnicity}, or null if none has been assigned.
     */
    String getSubEthnicityCodeUnmasked();

	boolean isSuppressPersonal();
}
