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

import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krms.api.repository.category.CategoryDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinitionContract;

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
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KRMS_TERM_SPEC_T")
public class TermSpecificationBo implements TermSpecificationDefinitionContract, Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_TERM_SPEC_S")
    @GeneratedValue(generator = "KRMS_TERM_SPEC_S")
    @Id
    @Column(name = "TERM_SPEC_ID")
    private String id;

    @Column(name = "NM")
    private String name;

    @Column(name = "NMSPC_CD")
    private String namespace;

    @Column(name = "TYP")
    private String type;

    @Column(name = "DESC_TXT")
    private String description;

    @Column(name = "ACTV")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active = true;

    @Column(name = "VER_NBR")
    @Version
    private Long versionNumber;

    @ManyToMany(targetEntity = CategoryBo.class, cascade = { CascadeType.REFRESH })
    @JoinTable(name = "KRMS_TERM_SPEC_CTGRY_T",
            joinColumns = { @JoinColumn(name = "TERM_SPEC_ID", referencedColumnName = "TERM_SPEC_ID") },
            inverseJoinColumns = { @JoinColumn(name = "CTGRY_ID", referencedColumnName = "CTGRY_ID") })
    private List<CategoryBo> categories = new ArrayList<CategoryBo>();

    @OneToMany(orphanRemoval = true, mappedBy = "termSpecification")
    private List<ContextValidTermBo> contextValidTerms = new ArrayList<ContextValidTermBo>();

    @Transient
    private List<String> contextIds = new ArrayList<String>();

    @Transient
    private List<ContextBo> contexts = new ArrayList<ContextBo>();

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static TermSpecificationDefinition to(TermSpecificationBo bo) {
        if (bo == null) {
            return null;
        }

        return TermSpecificationDefinition.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static TermSpecificationBo from(TermSpecificationDefinition im) {
        if (im == null) {
            return null;
        }

        TermSpecificationBo bo = new TermSpecificationBo();
        bo.id = im.getId();
        bo.namespace = im.getNamespace();
        bo.name = im.getName();
        bo.type = im.getType();
        bo.description = im.getDescription();
        bo.categories = new ArrayList<CategoryBo>();

        for (CategoryDefinition category : im.getCategories()) {
            bo.categories.add(CategoryBo.from(category));
        }

        bo.contextIds.addAll(im.getContextIds());
        bo.active = im.isActive();
        bo.setVersionNumber(im.getVersionNumber());

        return bo;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public List<CategoryBo> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryBo> categories) {
        this.categories = categories;
    }

    public List<String> getContextIds() {
        return contextIds;
    }

    public void setContextIds(List<String> contextIds) {
        this.contextIds = contextIds;
    }

    public List<ContextBo> getContexts() {
        return contexts;
    }

    public void setContexts(List<ContextBo> contexts) {
        this.contexts = contexts;
    }

    public List<ContextValidTermBo> getContextValidTerms() {
        return contextValidTerms;
    }

    public void setContextValidTerms(List<ContextValidTermBo> contextValidTerms) {
        this.contextValidTerms = contextValidTerms;
    }
}
