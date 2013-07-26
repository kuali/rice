package org.kuali.rice.krad.data.provider.jpa.testbo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TestDataObjectTwoPkFieldsId {

	@Column(name = "PK_PROP")
	String primaryKeyProperty;
	@Column(name = "PK_PROP_TWO")
	String primaryKeyPropertyTwo;

	public String getPrimaryKeyProperty() {
		return primaryKeyProperty;
	}

	public void setPrimaryKeyProperty(String primaryKeyProperty) {
		this.primaryKeyProperty = primaryKeyProperty;
	}

	public String getPrimaryKeyPropertyTwo() {
		return primaryKeyPropertyTwo;
	}

	public void setPrimaryKeyPropertyTwo(String primaryKeyPropertyTwo) {
		this.primaryKeyPropertyTwo = primaryKeyPropertyTwo;
	}
}
