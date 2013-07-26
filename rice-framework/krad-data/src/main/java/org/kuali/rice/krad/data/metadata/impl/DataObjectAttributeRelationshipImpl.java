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
