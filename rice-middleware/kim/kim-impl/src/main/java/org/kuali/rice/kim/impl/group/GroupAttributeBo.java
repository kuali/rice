package org.kuali.rice.kim.impl.group;

import org.eclipse.persistence.annotations.Customizer;
import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.common.attribute.KimAttributeData;
import org.kuali.rice.kim.api.common.attribute.KimAttributeDataContract;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.krad.data.provider.jpa.eclipselink.EclipseLinkSequenceCustomizer;

import org.kuali.rice.krad.data.platform.generator.Sequence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Customizer(EclipseLinkSequenceCustomizer.class)
@Sequence(name="KRIM_GRP_ATTR_DATA_ID_S", property="id")
@Table(name = "KRIM_GRP_ATTR_DATA_T")
public class GroupAttributeBo extends KimAttributeDataBo implements KimAttributeDataContract {
    private static final long serialVersionUID = 6380313567330578976L;
    @Column(name = "GRP_ID")
    private String assignedToId;

    public static KimAttributeData to(GroupAttributeBo bo) {
        if (bo == null) {
            return null;
        }

        return KimAttributeData.Builder.create(bo).build();
    }

    public static GroupAttributeBo from(KimAttributeData im) {
        if (im == null) {
            return null;
        }

        GroupAttributeBo bo = new GroupAttributeBo();
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
    }

    @Override
    public String getAssignedToId() {
        return assignedToId;
    }

    @Override
    public void setAssignedToId(String assignedToId) {
        this.assignedToId = assignedToId;
    }


}
