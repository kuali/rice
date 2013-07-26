package org.kuali.rice.kim.impl.common.delegate;

import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_DLGN_MBR_ATTR_DATA_T")
public class DelegateMemberAttributeDataBo extends KimAttributeDataBo {
    @Override
    public String getAssignedToId() {
        return assignedToId;
    }

    @Override
    public void setAssignedToId(String assignedToId) {
        this.assignedToId = assignedToId;
    }

    @Column(name = "DLGN_MBR_ID")
    private String assignedToId;
}
