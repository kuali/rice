package org.kuali.rice.kim.impl.common.delegate

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table
import org.kuali.rice.kim.api.common.delegate.DelegateMember
import org.kuali.rice.kim.api.common.delegate.DelegateMemberContract
import org.kuali.rice.kim.impl.membership.AbstractMemberBo
import org.springframework.util.AutoPopulatingList
import org.kuali.rice.core.api.mo.common.Attributes

@Entity
@Table(name = "KRIM_DLGN_MBR_T")
public class DelegateMemberBo extends AbstractMemberBo implements DelegateMemberContract {

    @Id
    @Column(name = "DLGN_MBR_ID")
    String delegationMemberId;
    @Column(name = "DLGN_ID")
    String delegationId;
    @Column(name = "ROLE_MBR_ID")
    String roleMemberId;

    @OneToMany(targetEntity = DelegateMemberAttributeDataBo.class, cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "DLGN_MBR_ID", referencedColumnName = "DLGN_MBR_ID", insertable = false, updatable = false)
    List<DelegateMemberAttributeDataBo> attributes = new AutoPopulatingList(DelegateMemberAttributeDataBo.class);

    /**
     * Returns Attributes derived from the internal List of DelegateMemberAttributeDataBos.  This field is
     * not exposed in the DelegateMemberContract as it is not a required field in the DelegateMember DTO
     * @return
     */
    public Attributes getQualifier() {
        Map<String,String> attribs = new HashMap<String,String>();

        if (attributes == null) {
            return attribs;
        }
        for (DelegateMemberAttributeDataBo attr: attributes) {
            attribs.put(attr.getKimAttribute().getAttributeName(), attr.getAttributeValue());
        }
        return Attributes.fromMap(attribs)
    }


    public static DelegateMember to(DelegateMemberBo bo) {
        DelegateMember.Builder builder = DelegateMember.Builder.create()
        builder.delegationMemberId = bo.delegationMemberId
        builder.activeFromDate = bo.activeFromDate
        builder.activeToDate = bo.activeToDate
        builder.delegationId = bo.delegationId
        builder.memberId = bo.memberId
        builder.roleMemberId = bo.roleMemberId
        builder.typeCode = bo.typeCode
    }

    public static DelegateMemberBo from(Delegate immutable) {
        return new DelegateMemberBo(
                delegationMemberId: immutable.delegationMemberId,
                activeFromDate: immutable.activeFromDate,
                activeToDate: immutable.activeToDate,
                delegationId: immutable.delegationId,
                memberId: immutable.memberId,
                roleMemberId: immutable.roleMemberId,
                typeCode: immutable.memberTypeCode,
        )
    }
}
