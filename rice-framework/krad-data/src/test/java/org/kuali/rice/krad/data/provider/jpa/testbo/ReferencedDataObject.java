package org.kuali.rice.krad.data.provider.jpa.testbo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.krad.data.provider.annotation.BusinessKey;
import org.kuali.rice.krad.data.provider.annotation.Label;

@Entity
@Table(name = "KRTST_TEST_REF_OBJ_T")
public class ReferencedDataObject {

	@Id
	@Column(name = "STR_PROP")
	String stringProperty;
	@Column(name = "OTHER_STR_PROP")
	@BusinessKey
	@Label("RDOs Business Key")
	String someOtherStringProperty;

	public String getStringProperty() {
		return stringProperty;
	}

	public void setStringProperty(String stringProperty) {
		this.stringProperty = stringProperty;
	}

	public String getSomeOtherStringProperty() {
		return someOtherStringProperty;
	}

	public void setSomeOtherStringProperty(String someOtherStringProperty) {
		this.someOtherStringProperty = someOtherStringProperty;
	}

}
