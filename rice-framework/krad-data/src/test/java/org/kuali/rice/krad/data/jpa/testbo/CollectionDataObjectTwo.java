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
