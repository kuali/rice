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
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krms.api.repository.category.CategoryDefinition;
import org.kuali.rice.krms.api.repository.function.FunctionDefinition;
import org.kuali.rice.krms.api.repository.function.FunctionDefinitionContract;
import org.kuali.rice.krms.api.repository.function.FunctionParameterDefinition;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KRMS_FUNC_T")
public class FunctionBo implements MutableInactivatable, FunctionDefinitionContract, Versioned, Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_FUNC_S")
    @GeneratedValue(generator = "KRMS_FUNC_S")
    @Id
    @Column(name = "FUNC_ID")
    private String id;

    @Column(name = "NMSPC_CD")
    private String namespace;

    @Column(name = "NM")
    private String name;

    @Column(name = "DESC_TXT")
    private String description;

    @Column(name = "RTRN_TYP")
    private String returnType;

    @Column(name = "TYP_ID")
    private String typeId;

    @Column(name = "ACTV")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active = true;

    @Version
    @Column(name="VER_NBR", length=8)
    protected Long versionNumber;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "function")
    private List<FunctionParameterBo> parameters;

    @ManyToMany(targetEntity = CategoryBo.class, cascade = { CascadeType.REFRESH })
    @JoinTable(name = "KRMS_FUNC_CTGRY_T",
            joinColumns = { @JoinColumn(name = "FUNC_ID", referencedColumnName = "FUNC_ID") },
            inverseJoinColumns = { @JoinColumn(name = "CTGRY_ID", referencedColumnName = "CTGRY_ID") })
    private List<CategoryBo> categories;

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static FunctionDefinition to(FunctionBo bo) {
        if (bo == null) {
            return null;
        }

        return FunctionDefinition.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static FunctionBo from(FunctionDefinition im) {
        if (im == null) {
            return null;
        }

        FunctionBo bo = new FunctionBo();
        bo.id = im.getId();
        bo.namespace = im.getNamespace();
        bo.name = im.getName();
        bo.description = im.getDescription();
        bo.returnType = im.getReturnType();
        bo.typeId = im.getTypeId();
        bo.active = im.isActive();
        bo.setVersionNumber(im.getVersionNumber());
        bo.parameters = new ArrayList<FunctionParameterBo>();

        for (FunctionParameterDefinition parm : im.getParameters()) {
            FunctionParameterBo functionParameterBo = FunctionParameterBo.from(parm);
            functionParameterBo.setFunction(bo);
            bo.parameters.add(functionParameterBo);
        }

        bo.categories = new ArrayList<CategoryBo>();

        for (CategoryDefinition category : im.getCategories()) {
            bo.categories.add(CategoryBo.from(category));
        }

        bo.setVersionNumber(im.getVersionNumber());
        return bo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public boolean getActive() {
        return active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<FunctionParameterBo> getParameters() {
        return parameters;
    }

    public void setParameters(List<FunctionParameterBo> parameters) {
        this.parameters = parameters;
    }

    public List<CategoryBo> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryBo> categories) {
        this.categories = categories;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }
}
