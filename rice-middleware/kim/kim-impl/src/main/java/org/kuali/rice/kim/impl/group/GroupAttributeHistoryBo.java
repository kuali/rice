package org.kuali.rice.kim.impl.group;

import org.eclipse.persistence.annotations.Customizer;
import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.common.attribute.KimAttributeData;
import org.kuali.rice.kim.api.common.attribute.KimAttributeDataHistory;
import org.kuali.rice.kim.api.common.attribute.KimAttributeDataHistoryContract;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataHistoryBo;
import org.kuali.rice.krad.data.provider.jpa.eclipselink.EclipseLinkSequenceCustomizer;

import org.kuali.rice.krad.data.platform.generator.Sequence;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Customizer(EclipseLinkSequenceCustomizer.class)
@Sequence(name="KRIM_HIST_GRP_ATTR_DATA_ID_S", property="id")
//@Sequence(name="KRIM_GRP_ATTR_DATA_ID_S", property="id")
@Table(name = "KRIM_HIST_GRP_ATTR_DATA_T")
public class GroupAttributeHistoryBo extends KimAttributeDataHistoryBo implements KimAttributeDataHistoryContract
{
    private static final long serialVersionUID = -1358263879165065051L;
    @Column(name = "GRP_ID")
    private String assignedToId;

    @Column(name="GRP_HIST_ID")
    private Long assignedToHistoryId;

    @Override
    public Long getAssignedToHistoryId() {
        return assignedToHistoryId;
    }

    @Override
    public void setAssignedToHistoryId(Long assignedToHistoryId) {
        this.assignedToHistoryId = assignedToHistoryId;
    }

    @Override
    public String getAssignedToId() {
        return assignedToId;
    }

    @Override
    public void setAssignedToId(String assignedToId) {
        this.assignedToId = assignedToId;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static KimAttributeDataHistory to(GroupAttributeHistoryBo bo) {
        if (bo == null) {
            return null;
        }

        return KimAttributeDataHistory.Builder.create(bo).build();
    }

    /*public static GroupAttributeHistoryBo from(KimAttributeData im, Timestamp fromDate, Timestamp toDate) {
        if (im == null) {
            return null;
        }

        GroupAttributeHistoryBo bo = new GroupAttributeHistoryBo();
        bo.setId(im.getId());
        bo.setAssignedToId(im.getAssignedToId());
        bo.setKimAttribute(KimAttributeBo.from(im.getKimAttribute()));
        final KimAttribute attribute = im.getKimAttribute();
        bo.setKimAttributeId((attribute == null ? null : attribute.getId()));
        bo.setAttributeValue(bo.getAttributeValue());
        bo.setKimTypeId(im.getKimTypeId());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());

        return bo;
    }*/

    public static GroupAttributeHistoryBo from(KimAttributeDataHistory im) {
        if (im == null) {
            return null;
        }

        GroupAttributeHistoryBo bo = new GroupAttributeHistoryBo();
        bo.setId(im.getId());
        //bo.setHistoryId(im.getHistoryId());
        bo.setAssignedToId(im.getAssignedToId());
        bo.setAssignedToHistoryId(im.getAssignedToHistoryId());
        bo.setKimAttribute(KimAttributeBo.from(im.getKimAttribute()));
        final KimAttribute attribute = im.getKimAttribute();
        bo.setKimAttributeId((attribute == null ? null : attribute.getId()));
        bo.setAttributeValue(bo.getAttributeValue());
        bo.setKimTypeId(im.getKimTypeId());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());

        return bo;
    }

}
