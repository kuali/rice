package org.kuali.rice.krad.data.provider.jpa.testbo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.krad.data.provider.annotation.ExtensionFor;

@Entity
@Table(
		name = "KRTST_TEST_TABLE_EXT_T")
@ExtensionFor(TestDataObject.class)
public class TestDataObjectExtension implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(
			name = "PK_PROP")
	String primaryKeyProperty;

	@Column(
			name = "STR_PROP",
			length = 40)
	String extensionProperty;

	public String getPrimaryKeyProperty() {
		return primaryKeyProperty;
	}

	public void setPrimaryKeyProperty(String primaryKeyProperty) {
		this.primaryKeyProperty = primaryKeyProperty;
	}

	public String getExtensionProperty() {
		return extensionProperty;
	}

	public void setExtensionProperty(String extensionProperty) {
		this.extensionProperty = extensionProperty;
	}
}
