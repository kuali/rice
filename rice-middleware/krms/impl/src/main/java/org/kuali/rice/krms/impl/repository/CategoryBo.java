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
package org.kuali.rice.krms.impl.repository;

import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krms.api.repository.category.CategoryDefinition;
import org.kuali.rice.krms.api.repository.category.CategoryDefinitionContract;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "KRMS_CTGRY_T")
public class CategoryBo implements CategoryDefinitionContract, Versioned, Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_CTGRY_S")
    @GeneratedValue(generator = "KRMS_CTGRY_S")
    @Id
    @Column(name = "CTGRY_ID")
    private String id;

    @Column(name = "NM")
    private String name;

    @Column(name = "NMSPC_CD")
    private String namespace;

    @Version
    @Column(name="VER_NBR", length=8)
    protected Long versionNumber;

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static CategoryDefinition to(CategoryBo bo) {
        if (bo == null) {
            return null;
        }

        return CategoryDefinition.Builder.create(bo).build();
    }

    /**
     * Converts a list of mutable bos to it's immutable counterpart
     *
     * @param bos the list of mutable business objects
     * @return and immutable list containing the immutable objects
     */
    public static List<CategoryDefinition> to(List<CategoryBo> bos) {
        if (bos == null) {
            return null;
        }

        List<CategoryDefinition> categories = new ArrayList<CategoryDefinition>();

        for (CategoryBo p : bos) {
            categories.add(CategoryDefinition.Builder.create(p).build());
        }

        return Collections.unmodifiableList(categories);
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static CategoryBo from(CategoryDefinition im) {
        if (im == null) {
            return null;
        }

        CategoryBo bo = new CategoryBo();
        bo.id = im.getId();
        bo.name = im.getName();
        bo.namespace = im.getNamespace();
        bo.setVersionNumber(im.getVersionNumber());

        return bo;
    }

    public static List<CategoryBo> from(List<CategoryDefinition> ims) {
        if (ims == null) {
            return null;
        }

        List<CategoryBo> bos = new ArrayList<CategoryBo>();

        for (CategoryDefinition im : ims) {
            CategoryBo bo = CategoryBo.from(im);
            ((ArrayList<CategoryBo>) bos).add(bo);
        }

        return Collections.unmodifiableList(bos);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }
}
