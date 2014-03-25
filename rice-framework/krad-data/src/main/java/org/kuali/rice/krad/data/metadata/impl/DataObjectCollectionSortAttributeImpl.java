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
package org.kuali.rice.krad.data.metadata.impl;

import org.kuali.rice.krad.data.metadata.DataObjectCollectionSortAttribute;
import org.kuali.rice.krad.data.metadata.SortDirection;

/**
* {@inheritDoc}
*/
public class DataObjectCollectionSortAttributeImpl implements DataObjectCollectionSortAttribute {
	private static final long serialVersionUID = 2221451853788207680L;

	protected String attributeName;
	protected SortDirection sortDirection = SortDirection.ASCENDING;

	public DataObjectCollectionSortAttributeImpl() {
	}

	public DataObjectCollectionSortAttributeImpl(String attributeName, SortDirection sortDirection) {
		super();
		this.attributeName = attributeName;
		this.sortDirection = sortDirection;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public String getAttributeName() {
		return attributeName;
	}

    /**
    * Sets attribute name.
    *
    * @param attributeName attribute name
    */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public SortDirection getSortDirection() {
		return sortDirection;
	}

    /**
    * Sets sort direction.
    *
    * @param sortDirection sort direction.
    */
	public void setSortDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[attributeName=").append(attributeName).append(" ").append(sortDirection).append("]");
		return builder.toString();
	}

}
