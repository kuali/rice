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

import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;

/**
* {@inheritDoc}
*/
public class DataObjectAttributeRelationshipImpl implements DataObjectAttributeRelationship {
	private static final long serialVersionUID = 838360378126210069L;

	protected String parentAttributeName;
	protected String childAttributeName;

	public DataObjectAttributeRelationshipImpl() {
	}

    /**
    * Gets results where the actual rows are requested.
    *
    * @param queryClass the type of the results to return.
    * @param criteria the criteria to use to get the results.
    * @param ojbCriteria the implementation-specific criteria.
    * @param flag the indicator to whether the row count is requested in the results.
    * @return results where the actual rows are requested.
    */
	public DataObjectAttributeRelationshipImpl(String parentAttributeName, String childAttributeName) {
		super();
		this.parentAttributeName = parentAttributeName;
		this.childAttributeName = childAttributeName;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public String getParentAttributeName() {
		return parentAttributeName;
	}

    /**
    * Sets the parent attribute name.
    *
    * @param parentAttributeName parent attribute name
    */
	public void setParentAttributeName(String parentAttributeName) {
		this.parentAttributeName = parentAttributeName;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public String getChildAttributeName() {
		return childAttributeName;
	}

    /**
    * Sets the child attribute name.
    *
    * @param childAttributeName child attribute name
    */
    public void setChildAttributeName(String childAttributeName) {
		this.childAttributeName = childAttributeName;
	}

    /**
    * {@inheritDoc}
    */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[parentAttributeName=").append(parentAttributeName).append(", childAttributeName=")
				.append(childAttributeName).append("]");
		return builder.toString();
	}
}
