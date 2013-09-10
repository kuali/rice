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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kuali.rice.krad.data.jpa.eclipselink.PortableSequenceGenerator;

@Entity
@Table(name="KRTST_PARENT_GEN_KEY_T")
public class ParentObjectWithGeneratedKey {

    @Id
    @Column(name="GENERATED_PK_COL",length=8,precision=0)
    @GeneratedValue(generator="KRTST_GENERATED_PK_S")
    @PortableSequenceGenerator(name="KRTST_GENERATED_PK_S")
    Long generatedKey;

    @OneToMany(fetch=FetchType.LAZY,cascade= {CascadeType.ALL}, orphanRemoval=true)
    @JoinColumn(name="GENERATED_PK_COL",referencedColumnName="GENERATED_PK_COL",updatable=false,insertable=false)
    List<ChildOfParentObjectWithGeneratedKey> children = new ArrayList<ChildOfParentObjectWithGeneratedKey>();

    public Long getGeneratedKey() {
        return this.generatedKey;
    }

    public void setGeneratedKey(Long generatedKey) {
        this.generatedKey = generatedKey;
    }

    public List<ChildOfParentObjectWithGeneratedKey> getChildren() {
        return this.children;
    }

    public void setChildren(List<ChildOfParentObjectWithGeneratedKey> children) {
        this.children = children;
    }

    public ChildOfParentObjectWithGeneratedKey getChildByKey( Long childKey ) {
        for ( ChildOfParentObjectWithGeneratedKey child : children ) {
            if ( child.childKey.equals(childKey) ) {
                return child;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ParentObjectWithGeneratedKey [");
        if (this.generatedKey != null) {
            builder.append("generatedKey=").append(this.generatedKey).append(", ");
        }
        if (this.children != null) {
            builder.append("children=").append(this.children);
        }
        builder.append("]");
        return builder.toString();
    }


}
