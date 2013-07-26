package org.kuali.rice.kim.impl.group;

import org.eclipse.persistence.annotations.Customizer;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.api.group.GroupMemberContract;
import org.kuali.rice.kim.impl.membership.AbstractMemberBo;
import org.kuali.rice.krad.data.provider.jpa.eclipselink.EclipseLinkSequenceCustomizer;

import org.kuali.rice.krad.data.platform.generator.Sequence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Customizer(EclipseLinkSequenceCustomizer.class)
@Sequence(name="KRIM_GRP_MBR_ID_S", property="id")
@Table(name = "KRIM_GRP_MBR_T")
public class GroupMemberBo extends AbstractMemberBo implements GroupMemberContract {

    private static final long serialVersionUID = 6773749266062306217L;
    @Id
    @Column(name = "GRP_MBR_ID")
    private String id;

    @Column(name = "GRP_ID")
    private String groupId;

    public static GroupMember to(GroupMemberBo bo) {
        if (bo == null) {
            return null;
        }

        return GroupMember.Builder.create(bo).build();
    }

    public static GroupMemberBo from(GroupMember im) {
        if (im == null) {
            return null;
        }

        GroupMemberBo bo = new GroupMemberBo();
        bo.setId(im.getId());
        bo.setGroupId(im.getGroupId());
        bo.setMemberId(im.getMemberId());
        bo.setTypeCode(im.getType().getCode());
        bo.setActiveFromDateValue(im.getActiveFromDate() == null? null : new Timestamp(
                im.getActiveFromDate().getMillis()));
        bo.setActiveToDateValue(im.getActiveToDate() == null ? null : new Timestamp(
                im.getActiveToDate().getMillis()));
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        return bo;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }



}
