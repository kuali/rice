package org.kuali.rice.kim.impl.common.delegate

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.Type
import org.kuali.rice.kim.api.common.delegate.DelegateType
import org.kuali.rice.kim.api.common.delegate.DelegateTypeContract
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.springframework.util.AutoPopulatingList

@Entity
@Table(name = "KRIM_DLGN_T")
public class DelegateBo extends PersistableBusinessObjectBase implements DelegateTypeContract {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "DLGN_ID")
    String delegationId;

    @Column(name = "ROLE_ID")
    String roleId;

    @Type(type = "yes_no")
    @Column(name = "ACTV_IND")
    boolean active = true;

    @Column(name = "KIM_TYP_ID")
    String kimTypeId;

    @Column(name = "DLGN_TYP_CD")
    String delegationTypeCode;

    @OneToMany(targetEntity = DelegateMemberBo.class, cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "DLGN_ID", insertable = false, updatable = false)
    List<DelegateMemberBo> members = new AutoPopulatingList(DelegateMemberBo.class);



    public static DelegateType to(DelegateBo bo) {
        return DelegateType.Builder.create(bo).build()
    }

    public static DelegateBo from(DelegateType immutable) {
        return new DelegateBo(
                delegationId: immutable.delegationId,
                roleId: immutable.roleId,
                active: immutable.active,
                kimTypeId: immutable.kimTypeId,
                delegationTypeCode: immutable.delegationTypeCode,
        );
    }
}
