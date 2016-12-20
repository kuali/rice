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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="KRTST_PARENT_OF_UPDATABLE_T")
public class ParentObjectWithUpdatableChild {

    @Id
    @Column(name="PK_COL",length=8,precision=0)
    Long primaryKey;

    @Column(name="UPDATABLE_CHILD_KEY_COL",length=10)
    String updatableChildsKey;

    @OneToOne(fetch=FetchType.EAGER,cascade= {CascadeType.ALL},orphanRemoval=true)
    @JoinColumn(name="UPDATABLE_CHILD_KEY_COL",referencedColumnName="PK_COL",updatable=false,insertable=false)
    UpdatableChildObject updatableChild;

    public Long getPrimaryKey() {
        return this.primaryKey;
    }

    public void setPrimaryKey(Long primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getUpdatableChildsKey() {
        return this.updatableChildsKey;
    }

    public void setUpdatableChildsKey(String updatableChildsKey) {
        this.updatableChildsKey = updatableChildsKey;
    }

    public UpdatableChildObject getUpdatableChild() {
        return this.updatableChild;
    }

    public void setUpdatableChild(UpdatableChildObject updatableChild) {
        this.updatableChild = updatableChild;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ParentObjectWithUpdatableChild [");
        if (this.primaryKey != null) {
            builder.append("primaryKey=").append(this.primaryKey).append(", ");
        }
        if (this.updatableChildsKey != null) {
            builder.append("updatableChildsKey=").append(this.updatableChildsKey).append(", ");
        }
        if (this.updatableChild != null) {
            builder.append("updatableChild=").append(this.updatableChild);
        }
        builder.append("]");
        return builder.toString();
    }



}
