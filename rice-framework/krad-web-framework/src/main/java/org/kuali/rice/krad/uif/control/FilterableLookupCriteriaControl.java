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
package org.kuali.rice.krad.uif.control;

import java.util.Map;

/**
 * Control instance that implements a callback for processing lookup search criteria.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface FilterableLookupCriteriaControl {

    /**
     * Returns the post data for the filterable lookup criteria control using the property name as a key.
     *
     * @param propertyName the property name for the control
     *
     * @return the post data for the filterable lookup criteria control
     */
    FilterableLookupCriteriaControlPostData getPostData(String propertyName);

    /**
     * Invoked to perform filtering of the search criteria.
     *
     * @param propertyName the propertyName of the current component
     * @param searchCriteria the search criteria to be filtered
     * @param postData the post data to consider during the search
     *
     * @return filtered search criteria
     */
    Map<String, String> filterSearchCriteria(String propertyName, Map<String, String> searchCriteria, FilterableLookupCriteriaControlPostData postData);

}