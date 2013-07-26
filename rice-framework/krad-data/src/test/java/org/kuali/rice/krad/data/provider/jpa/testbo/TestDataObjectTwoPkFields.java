package org.kuali.rice.krad.data.provider.jpa.testbo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.kuali.rice.core.api.util.type.KualiDecimal;

@Entity
@Table(name = "KRTST_TEST_TABLE_2_T")
public class TestDataObjectTwoPkFields {

	@EmbeddedId
	TestDataObjectTwoPkFieldsId id = new TestDataObjectTwoPkFieldsId();

	@Column(
			name = "STR_PROP",
			length = 50)
	String stringProperty;

	@Version
	@Column(
			name = "VER_NBR",
			length = 8,
			precision = 0)
	Long versionNumber;

	@Transient
	Date dateProperty;

	@Transient
	KualiDecimal currencyProperty;

	public String getStringProperty() {
		return stringProperty;
	}

	public void setStringProperty(String stringProperty) {
		this.stringProperty = stringProperty;
	}

	public String getPrimaryKeyProperty() {
		return id.getPrimaryKeyProperty();
	}

	public void setPrimaryKeyProperty(String primaryKeyProperty) {
		id.setPrimaryKeyProperty(primaryKeyProperty);
	}

	public String getPrimaryKeyPropertyTwo() {
		return id.getPrimaryKeyPropertyTwo();
	}

	public void setPrimaryKeyPropertyTwo(String primaryKeyPropertyTwo) {
		id.setPrimaryKeyPropertyTwo(primaryKeyPropertyTwo);
	}

	public Date getDateProperty() {
		return dateProperty;
	}

	public void setDateProperty(Date dateProperty) {
		this.dateProperty = dateProperty;
	}

	public KualiDecimal getCurrencyProperty() {
		return currencyProperty;
	}

	public void setCurrencyProperty(KualiDecimal currencyProperty) {
		this.currencyProperty = currencyProperty;
	}

}
