package org.kuali.rice.kim.impl.responsibility;

import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.common.attribute.KimAttributeData;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_RSP_ATTR_DATA_T")
public class ResponsibilityAttributeBo extends KimAttributeDataBo {
    /**
     * Converts a mutable bo to its immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static KimAttributeData to(ResponsibilityAttributeBo bo) {
        if (bo == null) {
            return null;
        }

        return KimAttributeData.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static ResponsibilityAttributeBo from(KimAttributeData im) {
        if (im == null) {
            return null;
        }

        ResponsibilityAttributeBo bo = new ResponsibilityAttributeBo();
        bo.setId(im.getId());
        bo.assignedToId = im.getAssignedToId();
        bo.setKimAttribute(KimAttributeBo.from(im.getKimAttribute()));
        final KimAttribute attribute = im.getKimAttribute();
        bo.setKimAttributeId((attribute == null ? null : attribute.getId()));
        bo.setAttributeValue(bo.getAttributeValue());
        final KimType type = im.getKimType();
        bo.setKimTypeId((type == null ? null : type.getId()));
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());

        return bo;
    }

    public String getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(String assignedToId) {
        this.assignedToId = assignedToId;
    }

    @Column(name = "RSP_ID")
    private String assignedToId;
}
