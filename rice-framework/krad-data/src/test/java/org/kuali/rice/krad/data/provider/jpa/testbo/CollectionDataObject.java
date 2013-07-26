package org.kuali.rice.krad.data.provider.jpa.testbo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(
		name = "KRTST_TEST_COLL_T")
public class CollectionDataObject {

	@Id
	@Column(name = "PK_PROP")
	String primaryKeyProperty;
	@Id
	@Column(name = "PK_COLL_KEY_PROP")
	String collectionKeyProperty;
	@Column(name = "STR_PROP")
	String stringProperty;

	public String getPrimaryKeyProperty() {
		return primaryKeyProperty;
	}

	public void setPrimaryKeyProperty(String primaryKeyProperty) {
		this.primaryKeyProperty = primaryKeyProperty;
	}

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

}
