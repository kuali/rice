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
import javax.persistence.Embeddable;

@Embeddable
public class TestDataObjectTwoPkFieldsId {

	@Column(name = "PK_PROP")
	String primaryKeyProperty;
	@Column(name = "PK_PROP_TWO")
	String primaryKeyPropertyTwo;

	public String getPrimaryKeyProperty() {
		return primaryKeyProperty;
	}

	public void setPrimaryKeyProperty(String primaryKeyProperty) {
		this.primaryKeyProperty = primaryKeyProperty;
	}

	public String getPrimaryKeyPropertyTwo() {
		return primaryKeyPropertyTwo;
	}

	public void setPrimaryKeyPropertyTwo(String primaryKeyPropertyTwo) {
		this.primaryKeyPropertyTwo = primaryKeyPropertyTwo;
	}
}
