/**
 * Copyright 2005-2014 The Kuali Foundation
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


/**
 * Base interface for predicates in the form of:
 * 
 * <tt>EXISTS ( SELECT 1 FROM xxxxx WHERE a = b ... )</tt>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since Rice 2.4.2
 *
 */
public interface SubQueryPredicate extends Predicate {

    /**
     * The data type against which the subquery should run.
     */
    String getSubQueryType();
    
    /**
     * 
     * A predicate to apply to the inner (sub-query).
     */
    Predicate getSubQueryPredicate();
		    
}
