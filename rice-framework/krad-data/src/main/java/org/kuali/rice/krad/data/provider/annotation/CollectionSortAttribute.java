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
package org.kuali.rice.krad.data.provider.annotation;

import org.kuali.rice.krad.data.metadata.SortDirection;

/**
 * Defines an attribute on which to sort a collection.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public @interface CollectionSortAttribute {

    /**
     * Gets the attribute name to sort on.
     *
     * @return the attribute name to sort on.
     */
	String value();

    /**
     * Gets the direction which to sort.
     *
     * @return the direction which to sort.
     */
	SortDirection sortDirection() default SortDirection.ASCENDING;
}
