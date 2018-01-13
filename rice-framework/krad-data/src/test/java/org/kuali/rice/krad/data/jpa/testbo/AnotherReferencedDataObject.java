/**
 * Copyright 2005-2018 The Kuali Foundation
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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@IdClass(AnotherReferencedDataObjectId.class)
@Entity
@Table(name = "KRTST_TEST_ANOTHER_REF_OBJ_T")
public class AnotherReferencedDataObject {

	@Id
	@Column(name = "STR_PROP")
	String stringProperty;

	@Id
	@Column(name = "DATE_PROP")
	@Temporal(TemporalType.DATE)
	Date dateProperty;

	@Column(name = "OTHER_STR_PROP")
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

	public Date getDateProperty() {
		return dateProperty;
	}

	public void setDateProperty(Date dateProperty) {
		this.dateProperty = dateProperty;
	}

}
