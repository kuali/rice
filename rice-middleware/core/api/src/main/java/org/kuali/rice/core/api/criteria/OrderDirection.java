/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.core.api.criteria;

import org.kuali.rice.core.api.util.jaxb.EnumStringAdapter;

/**
 * Defines possible directives for how a query is requested to produce count values in it's results.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public enum OrderDirection {

	/**
	 * Indicates that rows will be returned in Ascending order.
	 */
	ASCENDING,
	
	/**
	 * Indicates that rows will be returned in descending order.
	 */
	DESCENDING;

	/**
	 * Returns the value of the count flag.
	 * 
	 * @return the flag
	 */
	public String getDirection() {
		return toString();
	}
	
	static final class Adapter extends EnumStringAdapter<OrderDirection> {
		
		protected Class<OrderDirection> getEnumClass() {
			return OrderDirection.class;
		}
		
	}
	
}
