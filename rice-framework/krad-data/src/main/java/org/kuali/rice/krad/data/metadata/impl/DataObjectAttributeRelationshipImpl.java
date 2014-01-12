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

public class DataObjectAttributeRelationshipImpl implements DataObjectAttributeRelationship {
	private static final long serialVersionUID = 838360378126210069L;

	protected String parentAttributeName;
	protected String childAttributeName;

	public DataObjectAttributeRelationshipImpl() {
	}

	public DataObjectAttributeRelationshipImpl(String parentAttributeName, String childAttributeName) {
		super();
		this.parentAttributeName = parentAttributeName;
		this.childAttributeName = childAttributeName;
	}

	@Override
	public String getParentAttributeName() {
		return parentAttributeName;
	}

	public void setParentAttributeName(String parentAttributeName) {
		this.parentAttributeName = parentAttributeName;
	}

	@Override
	public String getChildAttributeName() {
		return childAttributeName;
	}

	public void setChildAttributeName(String childAttributeName) {
		this.childAttributeName = childAttributeName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[parentAttributeName=").append(parentAttributeName).append(", childAttributeName=")
				.append(childAttributeName).append("]");
		return builder.toString();
	}
}
