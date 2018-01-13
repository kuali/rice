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

import java.util.Set;

/**
 * An predicate that contains other predicates.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface CompositePredicate extends Predicate {

	/**
	 * Returns an unmodifiable set of predicates contained within
	 * this composite predicate.  This set could be empty but should
	 * never be null.
	 * 
	 * @return the set of predicates contained within this composite
	 */
	Set<Predicate> getPredicates();
	
}
