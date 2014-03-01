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
import org.kuali.rice.krms.api.repository.term.TermParameterDefinition;
import org.kuali.rice.krms.api.repository.term.TermParameterDefinitionContract;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Entity
@Table(name = "KRMS_TERM_PARM_T")
public class TermParameterBo implements TermParameterDefinitionContract, Serializable {

    private static final long serialVersionUID = 1l;

    public static final String TERM_PARM_SEQ_NAME = "KRMS_TERM_PARM_S";

    @PortableSequenceGenerator(name = TERM_PARM_SEQ_NAME)
    @GeneratedValue(generator = TERM_PARM_SEQ_NAME)
    @Id
    @Column(name = "TERM_PARM_ID")
    private String id;

    @Column(name = "NM")
    private String name;

    @Column(name = "VAL")
    private String value;

    @Version
    @Column(name="VER_NBR", length=8)
    protected Long versionNumber;

    @ManyToOne()
    @JoinColumn(name = "TERM_ID", referencedColumnName = "TERM_ID")
    private TermBo term;

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static TermParameterDefinition to(TermParameterBo bo) {
        if (bo == null) {
            return null;
        }

        return TermParameterDefinition.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static TermParameterBo from(TermParameterDefinition im) {
        if (im == null) {
            return null;
        }

        TermParameterBo bo = new TermParameterBo();
        bo.id = im.getId();

        // we don't set term here, it gets set in TermBo.from

        bo.name = im.getName();
        bo.value = im.getValue();
        bo.setVersionNumber(im.getVersionNumber());

        return bo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTermId() {
        if (term != null) {
            return term.getId();
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public TermBo getTerm() {
        return term;
    }

    public void setTerm(TermBo term) {
        this.term = term;
    }
}
