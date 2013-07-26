package org.kuali.rice.krad.data.metadata.impl;

import org.kuali.rice.krad.data.metadata.DataObjectCollectionSortAttribute;
import org.kuali.rice.krad.data.metadata.SortDirection;

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

	@Override
	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	@Override
	public SortDirection getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[attributeName=").append(attributeName).append(" ").append(sortDirection).append("]");
		return builder.toString();
	}

}
