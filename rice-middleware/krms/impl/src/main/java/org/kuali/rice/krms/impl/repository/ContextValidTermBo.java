package org.kuali.rice.krms.impl.repository;

import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

    @Column(name = "TERM_SPEC_ID")
    private String termSpecificationId;

    @Transient
    private Boolean prereq;

    @ManyToOne(targetEntity = TermSpecificationBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "TERM_SPEC_ID", referencedColumnName = "TERM_SPEC_ID", insertable = false, updatable = false)
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
        return termSpecificationId;
    }

    public void setTermSpecificationId(String termSpecificationId) {
        this.termSpecificationId = termSpecificationId;
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
