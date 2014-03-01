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
import org.kuali.rice.krms.api.repository.proposition.PropositionParameter;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterContract;
import org.kuali.rice.krms.api.repository.term.TermDefinition;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "KRMS_PROP_PARM_T")
public class PropositionParameterBo implements PropositionParameterContract, Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_PROP_PARM_S")
    @GeneratedValue(generator = "KRMS_PROP_PARM_S")
    @Id
    @Column(name = "PROP_PARM_ID")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "PROP_ID")
    private PropositionBo proposition;

    @Column(name = "PARM_VAL")
    private String value;

    @Column(name = "PARM_TYP_CD")
    private String parameterType;

    @Column(name = "SEQ_NO")
    private Integer sequenceNumber;

    @Transient
    private TermDefinition termValue;

    @Column(name = "VER_NBR")
    @Version
    private Long versionNumber;

    public TermDefinition getTermValue() {
        return termValue;
    }

    public void setTermValue(TermDefinition termValue) {
        if (termValue != null) {
            value = termValue.getId();
        }

        this.termValue = termValue;
    }

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static PropositionParameter to(PropositionParameterBo bo) {
        if (bo == null) {
            return null;
        }

        return PropositionParameter.Builder.create(bo).build();
    }

    /**
     * Converts a list of mutable bos to it's immutable counterpart
     *
     * @param bos the list of smutable business objects
     * @return and immutable list containing the immutable objects
     */
    public static List<PropositionParameter> to(List<PropositionParameterBo> bos) {
        if (bos == null) {
            return null;
        }

        List<PropositionParameter> parms = new ArrayList<PropositionParameter>();

        for (PropositionParameterBo p : bos) {
            parms.add(PropositionParameter.Builder.create(p).build());
        }

        return Collections.unmodifiableList(parms);
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static PropositionParameterBo from(PropositionParameter im) {
        if (im == null) {
            return null;
        }

        PropositionParameterBo bo = new PropositionParameterBo();
        bo.id = im.getId();

        // we don't set proposition here, it gets set in PropositionBo.from

        bo.value = im.getValue();
        bo.setTermValue(im.getTermValue());
        bo.parameterType = im.getParameterType();
        bo.sequenceNumber = im.getSequenceNumber();
        bo.setVersionNumber(im.getVersionNumber());

        return bo;
    }

    public static List<PropositionParameterBo> from(List<PropositionParameter> ims) {
        if (ims == null) {
            return null;
        }

        List<PropositionParameterBo> bos = new ArrayList<PropositionParameterBo>();
        for (PropositionParameter im : ims) {
            PropositionParameterBo bo = new PropositionParameterBo();
            bo.id = im.getId();

            // we don't set proposition here, it gets set in PropositionBo.from

            bo.value = im.getValue();
            bo.parameterType = im.getParameterType();
            bo.sequenceNumber = im.getSequenceNumber();
            bo.setVersionNumber(im.getVersionNumber());
            bos.add(bo);
        }

        return Collections.unmodifiableList(bos);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPropId() {
        if (proposition != null) {
            return proposition.getId();
        }

        return null;
    }

    public PropositionBo getProposition() {
        return proposition;
    }

    public void setProposition(PropositionBo proposition) {
        this.proposition = proposition;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
