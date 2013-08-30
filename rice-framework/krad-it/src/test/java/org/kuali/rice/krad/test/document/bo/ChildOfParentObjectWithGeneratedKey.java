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
@Table(name="KRTST_PARENT_GEN_KEY_CHILD_T")
public class ChildOfParentObjectWithGeneratedKey {

    @Id
    @Column(name="GENERATED_PK_COL",length=8,precision=0)
    Long generatedKey;

    @Id
    @Column(name="CHILDS_PK_COL",length=8,precision=0)
    Long childKey;

    public ChildOfParentObjectWithGeneratedKey() {
    }

    public ChildOfParentObjectWithGeneratedKey( Long childKey ) {
        this.childKey = childKey;
    }

    public Long getGeneratedKey() {
        return this.generatedKey;
    }

    public void setGeneratedKey(Long generatedKey) {
        this.generatedKey = generatedKey;
    }

    public Long getChildKey() {
        return this.childKey;
    }

    public void setChildKey(Long childKey) {
        this.childKey = childKey;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ChildOfParentObjectWithGeneratedKey [");
        if (this.generatedKey != null) {
            builder.append("generatedKey=").append(this.generatedKey).append(", ");
        }
        if (this.childKey != null) {
            builder.append("childKey=").append(this.childKey);
        }
        builder.append("]");
        return builder.toString();
    }
}
