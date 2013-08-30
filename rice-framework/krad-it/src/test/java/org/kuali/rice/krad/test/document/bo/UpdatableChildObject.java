/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.test.document.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="KRTST_UPDATABLE_CHILD_T")
public class UpdatableChildObject {

    @Id
    @Column(name="PK_COL",length=10)
    String childKey;

    @Column(name="SOME_DATA_COL",length=40)
    String someData;

    public UpdatableChildObject() {
    }

    public UpdatableChildObject( String childKey, String someData ) {
        this.childKey = childKey;
        this.someData = someData;
    }

    public String getChildKey() {
        return this.childKey;
    }

    public void setChildKey(String childKey) {
        this.childKey = childKey;
    }

    public String getSomeData() {
        return this.someData;
    }

    public void setSomeData(String someData) {
        this.someData = someData;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UpdatableChildObject [");
        if (this.childKey != null) {
            builder.append("childKey=").append(this.childKey).append(", ");
        }
        if (this.someData != null) {
            builder.append("someData=").append(this.someData);
        }
        builder.append("]");
        return builder.toString();
    }

}
