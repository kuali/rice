package org.kuali.rice.krad.data.provider.jpa.testbo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(
		name = "KRTST_SOME_OTHER_COLL_T")
public class SomeOtherCollection implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Temporal(TemporalType.DATE)
	@Column(
			name = "DATE_PROP")
	Date collectionDateProperty;
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

	public Date getCollectionDateProperty() {
		return collectionDateProperty;
	}

	public void setCollectionDateProperty(Date collectionDateProperty) {
		this.collectionDateProperty = collectionDateProperty;
	}

}
