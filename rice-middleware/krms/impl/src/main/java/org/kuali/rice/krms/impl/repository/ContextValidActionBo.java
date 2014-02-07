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

@Entity
@Table(name = "KRMS_CNTXT_VLD_ACTN_TYP_T")
public class ContextValidActionBo implements Versioned, Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_CNTXT_VLD_ACTN_TYP_S")
    @GeneratedValue(generator = "KRMS_CNTXT_VLD_ACTN_TYP_S")
    @Id
    @Column(name = "CNTXT_VLD_ACTN_ID")
    private String id;

    @Column(name = "CNTXT_ID")
    private String contextId;

    @Column(name = "ACTN_TYP_ID")
    private String actionTypeId;

    @Column(name = "VER_NBR")
    @Version
    private Long versionNumber;

    @ManyToOne(targetEntity = KrmsTypeBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "ACTN_TYP_ID", referencedColumnName = "TYP_ID", insertable = false, updatable = false)
    private KrmsTypeBo actionType;

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

    public String getActionTypeId() {
        return actionTypeId;
    }

    public void setActionTypeId(String actionTypeId) {
        this.actionTypeId = actionTypeId;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public KrmsTypeBo getActionType() {
        return actionType;
    }

    public void setActionType(KrmsTypeBo actionType) {
        this.actionType = actionType;
    }
}
