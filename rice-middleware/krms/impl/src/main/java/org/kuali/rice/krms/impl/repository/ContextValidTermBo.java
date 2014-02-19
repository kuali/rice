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

@Entity
@Table(name = "KRMS_CNTXT_VLD_TERM_SPEC_T")
public class ContextValidTermBo implements Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_CNTXT_VLD_TERM_SPEC_S")
    @GeneratedValue(generator = "KRMS_CNTXT_VLD_TERM_SPEC_S")
    @Id
    @Column(name = "CNTXT_TERM_SPEC_PREREQ_ID")
    private String id;

    @Column(name = "CNTXT_ID")
    private String contextId;

    @Transient
    private Boolean prereq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TERM_SPEC_ID", referencedColumnName = "TERM_SPEC_ID")
    private TermSpecificationBo termSpecification;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getTermSpecificationId() {
        if (termSpecification != null) {
            return termSpecification.getId();
        }

        return null;
    }

    public Boolean getPrereq() {
        return prereq;
    }

    public void setPrereq(Boolean prereq) {
        this.prereq = prereq;
    }

    public TermSpecificationBo getTermSpecification() {
        return termSpecification;
    }

    public void setTermSpecification(TermSpecificationBo termSpecification) {
        this.termSpecification = termSpecification;
    }
}
