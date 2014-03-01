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
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krms.api.repository.term.TermResolverDefinition;
import org.kuali.rice.krms.api.repository.term.TermResolverDefinitionContract;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "KRMS_TERM_RSLVR_T")
public class TermResolverBo implements TermResolverDefinitionContract, Versioned, Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_TERM_RSLVR_S")
    @GeneratedValue(generator = "KRMS_TERM_RSLVR_S")
    @Id
    @Column(name = "TERM_RSLVR_ID")
    private String id;

    @Column(name = "NMSPC_CD")
    private String namespace;

    @Column(name = "NM")
    private String name;

    @Transient
    private String contextId;

    @Column(name = "TYP_ID")
    private String typeId;

    @Column(name = "OUTPUT_TERM_SPEC_ID")
    private String outputId;

    @Column(name = "ACTV")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active = true;

    @Version
    @Column(name="VER_NBR", length=8)
    protected Long versionNumber;

    @ManyToOne(targetEntity = TermSpecificationBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "OUTPUT_TERM_SPEC_ID", insertable = false, updatable = false)
    private TermSpecificationBo output;

    @ManyToMany(targetEntity = TermSpecificationBo.class, cascade = { CascadeType.REFRESH })
    @JoinTable(name = "KRMS_TERM_RSLVR_INPUT_SPEC_T",
            joinColumns = { @JoinColumn(name = "TERM_RSLVR_ID", referencedColumnName = "TERM_RSLVR_ID") },
            inverseJoinColumns = { @JoinColumn(name = "TERM_SPEC_ID", referencedColumnName = "TERM_SPEC_ID") }
            )
    private Set<TermSpecificationBo> prerequisites;

    @OneToMany(targetEntity = TermResolverParameterSpecificationBo.class, fetch = FetchType.LAZY, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "TERM_RSLVR_ID", referencedColumnName = "TERM_RSLVR_ID", insertable = false, updatable = false)
    private Set<TermResolverParameterSpecificationBo> parameterSpecifications;

    @OneToMany(targetEntity = TermResolverAttributeBo.class, fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "TERM_RSLVR_ID", referencedColumnName = "TERM_RSLVR_ID", insertable = false, updatable = false)
    private Set<TermResolverAttributeBo> attributeBos;

    public void setParameterNames(Set<String> pns) {
        if (pns != null) {
            parameterSpecifications = new HashSet<TermResolverParameterSpecificationBo>();

            for (String pn : pns) {
                TermResolverParameterSpecificationBo paramSpecBo = new TermResolverParameterSpecificationBo();
                paramSpecBo.setName(pn);
                paramSpecBo.setTermResolver(this);
                parameterSpecifications.add(paramSpecBo);
            }
        }
    }

    public Set<String> getParameterNames() {
        Set<String> results = Collections.emptySet();
        if (parameterSpecifications != null && parameterSpecifications.size() > 0) {
            results = new HashSet<String>();

            for (TermResolverParameterSpecificationBo parmSpec : parameterSpecifications) {
                results.add(parmSpec.getName());
            }
        }

        return results;
    }

    public Map<String, String> getAttributes() {
        HashMap<String, String> attributes = new HashMap<String, String>();
        if (attributeBos != null) for (TermResolverAttributeBo attr : attributeBos) {
            attributes.put(attr.getAttributeDefinition().getName(), attr.getValue());
        }

        return attributes;
    }

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static TermResolverDefinition to(TermResolverBo bo) {
        if (bo == null) {
            return null;
        }

        return TermResolverDefinition.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static TermResolverBo from(TermResolverDefinition im) {
        if (im == null) {
            return null;
        }

        TermResolverBo bo = new TermResolverBo();
        bo.id = im.getId();
        bo.namespace = im.getNamespace();
        bo.name = im.getName();
        bo.typeId = im.getTypeId();
        bo.output = TermSpecificationBo.from(im.getOutput());
        bo.outputId = im.getOutput().getId();
        bo.setParameterNames(new HashSet<String>());

        for (String paramName : im.getParameterNames()) {
            TermResolverParameterSpecificationBo parmSpecBo = TermResolverParameterSpecificationBo.from(im, paramName);
            bo.parameterSpecifications.add(parmSpecBo);
            parmSpecBo.setTermResolver(bo);
        }

        bo.prerequisites = new HashSet<TermSpecificationBo>();

        for (TermSpecificationDefinition prereq : im.getPrerequisites()) {
            bo.prerequisites.add(TermSpecificationBo.from(prereq));
        }

        // build the set of term resolver attribute BOs
        Set<TermResolverAttributeBo> attrs = new HashSet<TermResolverAttributeBo>();
        // for each converted pair, build an TermResolverAttributeBo and add it to the set 
        TermResolverAttributeBo attributeBo;

        for (Map.Entry<String, String> entry : im.getAttributes().entrySet()) {
            KrmsAttributeDefinitionBo attrDefBo = KrmsRepositoryServiceLocator.getKrmsAttributeDefinitionService().getKrmsAttributeBo(entry.getKey(), im.getNamespace());
            attributeBo = new TermResolverAttributeBo();
            attributeBo.setTermResolverId(im.getId());
            attributeBo.setValue(entry.getValue());
            attributeBo.setAttributeDefinition(attrDefBo);
            ((HashSet<TermResolverAttributeBo>) attrs).add(attributeBo);
        }

        bo.setAttributeBos(attrs);
        bo.active = im.isActive();
        bo.setVersionNumber(im.getVersionNumber());

        return bo;
    }

    public TermSpecificationBo getOutput() {
        return output;
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

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getOutputId() {
        return outputId;
    }

    public void setOutputId(String outputId) {
        this.outputId = outputId;
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

    public void setOutput(TermSpecificationBo output) {
        this.output = output;
    }

    public Set<TermSpecificationBo> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(Set<TermSpecificationBo> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public Set<TermResolverParameterSpecificationBo> getParameterSpecifications() {
        return parameterSpecifications;
    }

    public void setParameterSpecifications(Set<TermResolverParameterSpecificationBo> parameterSpecifications) {
        this.parameterSpecifications = parameterSpecifications;
    }

    public Set<TermResolverAttributeBo> getAttributeBos() {
        return attributeBos;
    }

    public void setAttributeBos(Set<TermResolverAttributeBo> attributeBos) {
        this.attributeBos = attributeBos;
    }
}
