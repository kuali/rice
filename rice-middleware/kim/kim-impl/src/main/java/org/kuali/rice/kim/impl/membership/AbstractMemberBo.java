package org.kuali.rice.kim.impl.membership;

import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.impl.common.active.ActiveFromToBo;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractMemberBo extends ActiveFromToBo {

    @Column(name = "MBR_ID")
    private String memberId;
    @Column(name = "MBR_TYP_CD")
    private String typeCode;

    public String getMemberId() {
        return this.memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public void setType(MemberType type) {
        typeCode = type.getCode();
    }

    public MemberType getType() {
        return MemberType.fromCode(typeCode);
    }


}
