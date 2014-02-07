package org.kuali.rice.krms.impl.repository;

import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

/**
 * Indicates an AgendaType that is valid for a Context
 */
@Entity
@Table(name = "KRMS_CNTXT_VLD_AGENDA_TYP_T")
public class ContextValidAgendaBo implements Versioned, Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_CNTXT_VLD_AGENDA_TYP_S")
    @GeneratedValue(generator = "KRMS_CNTXT_VLD_AGENDA_TYP_S")
    @Id
    @Column(name = "CNTXT_VLD_AGENDA_ID")
    private String id;

    @Column(name = "CNTXT_ID")
    private String contextId;

    @Column(name = "AGENDA_TYP_ID")
    private String agendaTypeId;

    @Column(name = "VER_NBR")
    @Version
    private Long versionNumber;

    @ManyToOne(targetEntity = KrmsTypeBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "AGENDA_TYP_ID", referencedColumnName = "TYP_ID", insertable = false, updatable = false)
    private KrmsTypeBo agendaType;

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

    public String getAgendaTypeId() {
        return agendaTypeId;
    }

    public void setAgendaTypeId(String agendaTypeId) {
        this.agendaTypeId = agendaTypeId;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public KrmsTypeBo getAgendaType() {
        return agendaType;
    }

    public void setAgendaType(KrmsTypeBo agendaType) {
        this.agendaType = agendaType;
    }
}
