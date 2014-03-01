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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kuali.rice.krad.data.provider.annotation.ExtensionFor;

@Entity
@Table(name = "KRTST_TEST_TABLE_EXT_T")
@ExtensionFor(TestDataObject.class)
public class TestDataObjectExtension implements Serializable {

	@Id
    @OneToOne
	@JoinColumn(name = "PK_PROP")
	private TestDataObject primaryKeyProperty;

	@Column(name = "STR_PROP", length = 40)
	private String extensionProperty;

    public TestDataObject getPrimaryKeyProperty() {
        return primaryKeyProperty;
    }

    public void setPrimaryKeyProperty(TestDataObject primaryKeyProperty) {
        this.primaryKeyProperty = primaryKeyProperty;
    }

    public String getExtensionProperty() {
		return extensionProperty;
	}

	public void setExtensionProperty(String extensionProperty) {
		this.extensionProperty = extensionProperty;
	}
}
