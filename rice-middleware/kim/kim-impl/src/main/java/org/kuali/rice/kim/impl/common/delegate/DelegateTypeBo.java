package org.kuali.rice.kim.impl.common.delegate;

import org.eclipse.persistence.annotations.Convert;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.common.delegate.DelegateTypeContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.springframework.util.AutoPopulatingList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KRIM_DLGN_T")
public class DelegateTypeBo extends PersistableBusinessObjectBase implements DelegateTypeContract {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "DLGN_ID")
    private String delegationId;
    @Column(name = "ROLE_ID")
    private String roleId;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active = true;
    @Column(name = "KIM_TYP_ID")
    private String kimTypeId;
    @Column(name = "DLGN_TYP_CD")
    private String delegationTypeCode;
    @OneToMany(targetEntity = DelegateMemberBo.class, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "DLGN_ID", insertable = false, updatable = false)
    private List<DelegateMemberBo> members = new AutoPopulatingList<DelegateMemberBo>(DelegateMemberBo.class);

    public void setDelegationType(DelegationType type) {
        this.delegationTypeCode = type.getCode();
    }

    @Override
    public DelegationType getDelegationType() {
        return DelegationType.fromCode(this.delegationTypeCode);
    }

    public static DelegateType to(DelegateTypeBo bo) {
        return DelegateType.Builder.create(bo).build();
    }

    public static DelegateTypeBo from(DelegateType immutable) {
        // build list of DelegateMemberBo
        ArrayList<DelegateMemberBo> tmpMembers = new ArrayList<DelegateMemberBo>();
        for (DelegateMember member : immutable.getMembers()) {
            tmpMembers.add(DelegateMemberBo.from(member));
        }

        DelegateTypeBo bo = new DelegateTypeBo();
        bo.setDelegationId(immutable.getDelegationId());
        bo.setRoleId(immutable.getRoleId());
        bo.setActive(immutable.isActive());
        bo.setKimTypeId(immutable.getKimTypeId());
        bo.setDelegationTypeCode(immutable.getDelegationType().getCode());
        bo.setMembers(tmpMembers);
        return bo;
    }

    @Override
    public String getDelegationId() {
        return delegationId;
    }

    public void setDelegationId(String delegationId) {
        this.delegationId = delegationId;
    }

    @Override
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public boolean getActive() {
        return active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getKimTypeId() {
        return kimTypeId;
    }

    public void setKimTypeId(String kimTypeId) {
        this.kimTypeId = kimTypeId;
    }

    public String getDelegationTypeCode() {
        return delegationTypeCode;
    }

    public void setDelegationTypeCode(String delegationTypeCode) {
        this.delegationTypeCode = delegationTypeCode;
    }

    @Override
    public List<DelegateMemberBo> getMembers() {
        return members;
    }

    public void setMembers(List<DelegateMemberBo> members) {
        this.members = members;
    }

}
