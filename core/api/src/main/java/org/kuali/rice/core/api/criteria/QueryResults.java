/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.core.api.criteria;

import java.util.List;

/**
 * Contains a collection of results from a query.  This interface exists as a
 * utility for services that want to implement it to represents results from
 * criteria-based (or other) queries.
 * 
 * @param <T> the type of the objects contained within the results list
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */

public interface QueryResults<T> {
	
	/**
	 * Return the list of results that are contained within.  This list can
	 * be empty but it should never be null.
	 * 
	 * @return the list of results
	 */
	List<T> getResults();
	
}
