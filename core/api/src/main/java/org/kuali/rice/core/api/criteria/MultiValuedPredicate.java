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
 * Represents an predicate which contains multiple {@link CriteriaValue}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface MultiValuedPredicate extends PropertyPathPredicate {
	
	/**
	 * Gets the list of criteria values for this predicate.  All values should
	 * be of the same parameterized CriteriaValue type.  The returned list can
	 * be empty but should never be null.
	 * 
	 * @return the list of criteria values for this predicate
	 */
	List<CriteriaValue<?>> getValues();
    
}
