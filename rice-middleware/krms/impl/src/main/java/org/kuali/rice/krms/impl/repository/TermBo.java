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
import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermDefinitionContract;
import org.kuali.rice.krms.api.repository.term.TermParameterDefinition;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Entity
@Table(name = "KRMS_TERM_T")
public class TermBo implements TermDefinitionContract, Serializable {

    private static final long serialVersionUID = 1l;

    public static final String TERM_SEQ_NAME = "KRMS_TERM_S";

    @PortableSequenceGenerator(name = TERM_SEQ_NAME) @GeneratedValue(generator = TERM_SEQ_NAME) @Id @Column(
            name = "TERM_ID")
    private String id;

    @Column(name = "TERM_SPEC_ID")
    private String specificationId;

    @Column(name = "DESC_TXT")
    private String description;

    @Version @Column(name = "VER_NBR", length = 8)
    protected Long versionNumber;

    // new-ing up an empty one allows the TermBo lookup to work on fields in the term specification:
    @ManyToOne(targetEntity = TermSpecificationBo.class, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "TERM_SPEC_ID", referencedColumnName = "TERM_SPEC_ID", insertable = false, updatable = false)
    private TermSpecificationBo specification = new TermSpecificationBo();

    @OneToMany(targetEntity = TermParameterBo.class, orphanRemoval = true,
            cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.PERSIST},
            mappedBy = "term"
    )
    private List<TermParameterBo> parameters;

    @Transient
    private Map<String, String> parametersMap = new HashMap<String, String>();

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static TermDefinition to(TermBo bo) {
        if (bo == null) {
            return null;
        }

        return TermDefinition.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static TermBo from(TermDefinition im) {
        if (im == null) {
            return null;
        }

        TermBo bo = new TermBo();
        bo.id = im.getId();
        bo.specificationId = im.getSpecification().getId();
        bo.description = im.getDescription();
        bo.specification = TermSpecificationBo.from(im.getSpecification());
        bo.parameters = new ArrayList<TermParameterBo>();

        for (TermParameterDefinition parm : im.getParameters()) {
            TermParameterBo termParmBo = TermParameterBo.from(parm);
            bo.parameters.add(termParmBo);
            termParmBo.setTerm(bo);
        }

        bo.versionNumber = im.getVersionNumber();

        return bo;
    }

    public TermSpecificationBo getSpecification() {
        return specification;
    }

    public void setSpecification(TermSpecificationBo specification) {
        this.specification = specification;
    }

    public List<TermParameterBo> getParameters() {
        return parameters;
    }

    public Map<String, String> getParametersMap() {
        return parametersMap;
    }

    public void setParameters(List<TermParameterBo> parameters) {
        this.parameters = parameters;
    }

    public void exportToParametersMap() {
        // merge in TermParameterBo values 
        if (parameters != null) {
            for (TermParameterBo param : parameters) {
                parametersMap.put(param.getName(), param.getValue());
            }
        }
    }

    public void importFromParametersMap() {
        if (parameters == null) {
            parameters = new ArrayList<TermParameterBo>();
        } else {
            parameters.clear();
        }

        for (Entry<String, String> paramEntry : parametersMap.entrySet()) {
            TermParameterDefinition termDef = TermParameterDefinition.Builder.create(null, id, paramEntry.getKey(),
                    paramEntry.getValue()).build();
            parameters.add(TermParameterBo.from(termDef));
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecificationId() {
        return specificationId;
    }

    public void setSpecificationId(String specificationId) {
        this.specificationId = specificationId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }
}
