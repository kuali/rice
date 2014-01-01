/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.data.jpa.testbo;

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
