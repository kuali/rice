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

import java.util.List;

/**
 * Represents the metadata for a collection within a data object.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DataObjectCollection extends MetadataChild {

	/**
	 * The elementLabel defines the name to be used for a single object within the collection.
	 * 
	 * For example: "Address" may be the name of one object within the "Addresses" collection.
	 */
	String getElementLabel();

	/**
	 * The minimum number of items which may be in the collection.
	 * 
	 * This is an optional attribute and may not be set by the metadata provider.
	 * 
	 * @return The minimum number of items which must be in this collection or <b>null</b> if unset.
	 */
	Long getMinItems();

	/**
	 * The maximum number of items which may be in the collection.
	 * 
	 * This is an optional attribute and may not be set by the metadata provider.
	 * 
	 * @return The maximum number of items which may be in this collection or <b>null</b> if unset.
	 */
	Long getMaxItems();

	/**
	 * The default ordering of collection items as specified by the metadata provider.
	 * 
	 * @return The list of fields in order by which to sort, or an empty list if none specified.
	 */
	List<DataObjectCollectionSortAttribute> getDefaultOrdering();

	/**
	 * Whether the referenced collection uses a linking object in the underlying implementation. (In case that is
	 * somehow important to using code.)
	 */
	boolean isIndirectCollection();
}