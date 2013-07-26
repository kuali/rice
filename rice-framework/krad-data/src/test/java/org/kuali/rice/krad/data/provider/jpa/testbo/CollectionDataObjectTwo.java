package org.kuali.rice.krad.data.provider.jpa.testbo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(
		name = "KRTST_TEST_COLL_TWO_T")
public class CollectionDataObjectTwo {

	@Id
	@Column(name = "PK_PROP")
	String primaryKeyPropertyUsingDifferentName;
	@Id
	@Column(name = "PK_COLL_KEY_PROP")
	String collectionKeyProperty;
	@Column(name = "STR_PROP")
	String stringProperty;

	public String getCollectionKeyProperty() {
		return collectionKeyProperty;
	}

	public void setCollectionKeyProperty(String collectionKeyProperty) {
		this.collectionKeyProperty = collectionKeyProperty;
	}

	public String getStringProperty() {
		return stringProperty;
	}

	public void setStringProperty(String stringProperty) {
		this.stringProperty = stringProperty;
	}

	public String getPrimaryKeyPropertyUsingDifferentName() {
		return primaryKeyPropertyUsingDifferentName;
	}

	public void setPrimaryKeyPropertyUsingDifferentName(String primaryKeyPropertyUsingDifferentName) {
		this.primaryKeyPropertyUsingDifferentName = primaryKeyPropertyUsingDifferentName;
	}

}
