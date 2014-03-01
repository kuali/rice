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
import org.kuali.rice.krms.api.repository.function.FunctionParameterDefinition;
import org.kuali.rice.krms.api.repository.function.FunctionParameterDefinitionContract;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "KRMS_FUNC_PARM_T")
public class FunctionParameterBo implements FunctionParameterDefinitionContract, Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_FUNC_S")
    @GeneratedValue(generator = "KRMS_FUNC_S")
    @Id
    @Column(name = "FUNC_PARM_ID")
    private String id;

    @Column(name = "NM")
    private String name;

    @Column(name = "DESC_TXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="FUNC_ID")
    private FunctionBo function;

    @Column(name = "TYP")
    private String parameterType;

    @Column(name = "SEQ_NO")
    private Integer sequenceNumber;

    @Transient
    protected Long versionNumber;

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static FunctionParameterDefinition to(FunctionParameterBo bo) {
        if (bo == null) {
            return null;
        }

        return FunctionParameterDefinition.Builder.create(bo).build();
    }

    /**
     * Converts a list of mutable bos to it's immutable counterpart
     *
     * @param bos the list of mutable business objects
     * @return and immutable list containing the immutable objects
     */
    public static List<FunctionParameterDefinition> to(List<FunctionParameterBo> bos) {
        if (bos == null) {
            return null;
        }

        List<FunctionParameterDefinition> parms = new ArrayList<FunctionParameterDefinition>();

        for (FunctionParameterBo p : bos) {
            parms.add(FunctionParameterDefinition.Builder.create(p).build());
        }

        return Collections.unmodifiableList(parms);
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static FunctionParameterBo from(FunctionParameterDefinition im) {
        if (im == null) {
            return null;
        }
        FunctionParameterBo bo = new FunctionParameterBo();
        bo.id = im.getId();
        bo.name = im.getName();
        bo.description = im.getDescription();

        // not setting function here, it will be set in FunctionBo.from

        bo.parameterType = im.getParameterType();
        bo.sequenceNumber = im.getSequenceNumber();
        bo.setVersionNumber(im.getVersionNumber());
        return bo;
    }

    public static List<FunctionParameterBo> from(List<FunctionParameterDefinition> ims) {
        if (ims == null) {
            return null;
        }

        List<FunctionParameterBo> bos = new ArrayList<FunctionParameterBo>();
        for (FunctionParameterDefinition im : ims) {
            FunctionParameterBo bo = FunctionParameterBo.from(im);
            ((ArrayList<FunctionParameterBo>) bos).add(bo);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFunctionId() {
        if (function != null){
            return function.getId();
        }

        return null;
    }

    public FunctionBo getFunction() {
        return function;
    }

    public void setFunction(FunctionBo function) {
        this.function = function;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }
}
