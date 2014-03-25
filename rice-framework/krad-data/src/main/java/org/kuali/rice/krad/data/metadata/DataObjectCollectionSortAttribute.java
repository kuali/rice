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
package org.kuali.rice.krad.data.metadata;

import java.io.Serializable;

/**
* Represents a single field on which to sort a {@link DataObjectCollection} within a {@link DataObjectMetadata}.
*
* <p>
* The collection may hold multiple of these objects to support sorting by multiple fields.
* </p>
*
* @author Kuali Rice Team (rice.collab@kuali.org)
*/
public interface DataObjectCollectionSortAttribute extends Serializable {

    /**
    * Gets attribute name.
    *
    * <p>
    * The attribute name on which to sort the collection.
    * </p>
    *
    * @return attribute name
    */
	String getAttributeName();

    /**
    * Gets the attribute sort.
    *
    * <p>
    * For this attribute, which way should we sort?.
    * </p>
    *
    * @return attribute sort
    */
	SortDirection getSortDirection();

}