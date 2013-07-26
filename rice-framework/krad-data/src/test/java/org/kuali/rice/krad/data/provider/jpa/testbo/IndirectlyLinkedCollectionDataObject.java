package org.kuali.rice.krad.data.provider.jpa.testbo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(
		name = "KRTST_TEST_INDIRECT_COLL_T")
public class IndirectlyLinkedCollectionDataObject {

	@Id
	@Column(name = "PK_COLL_PROP")
	@ManyToMany(mappedBy = "indirectCollection")
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

}
