package org.kuali.rice.krad.data.provider.jpa.testbo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "KRTST_TEST_YARDO_T")
public class YetAnotherReferencedDataObject {

	@Id
	@Column(name = "ID")
	String id;
	@Column(name = "OTHER_STR_PROP")
	String someOtherStringProperty;

	public String getSomeOtherStringProperty() {
		return someOtherStringProperty;
	}

	public void setSomeOtherStringProperty(String someOtherStringProperty) {
		this.someOtherStringProperty = someOtherStringProperty;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
