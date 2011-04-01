/*
 * Copyright 2006-2011 The Kuali Foundation
 *
 *  Licensed under the Educational Community License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.opensource.org/licenses/ecl2.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kuali.rice.shareddata.api.campus;

import org.kuali.rice.core.api.mo.GloballyUnique;
import org.kuali.rice.core.api.mo.Versioned;

public interface CampusTypeContract extends Versioned, GloballyUnique {
	
	/**
	 * This is the campus type code for the CampusType.  This is cannot be a null or a blank string.
	 *
	 * <p>
	 * It is a unique abreviation of a campus type.
	 * </p>
	 * @return code for CampusType.  Will never be null or an empty string.
	 */
	String getCode();


	/**
	 * This is the name for the CampusType. 
	 *
	 * <p>
	 * It is a name a campus type.
	 * </p>
	 * @return name for CampusType.
	 */
	String getName();


	/**
	 * This is the active flag for the CampusType. 
	 *
	 * <p>
	 * It is a flag that determines if a campus type is active or not.
	 * </p>
	 * @return active boolean for CampusType.
	 */
	boolean isActive();

}
