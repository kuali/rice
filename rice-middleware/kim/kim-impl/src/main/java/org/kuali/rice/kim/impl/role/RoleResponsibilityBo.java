/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.impl.role;

import org.eclipse.persistence.annotations.Convert;
import org.kuali.rice.kim.api.role.RoleResponsibility;
import org.kuali.rice.kim.api.role.RoleResponsibilityContract;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityBo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ROLE_RSP_T")
public class RoleResponsibilityBo extends PersistableBusinessObjectBase implements RoleResponsibilityContract {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ROLE_RSP_ID")
    private String roleResponsibilityId;
    @Column(name = "ROLE_ID")
    private String roleId;
    @Column(name = "RSP_ID")
    private String responsibilityId;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;
    @ManyToOne(targetEntity = ResponsibilityBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "RSP_ID", insertable = false, updatable = false)
    private ResponsibilityBo kimResponsibility;


    public static RoleResponsibility to(RoleResponsibilityBo bo) {
        if (bo == null) {
            return null;
        }

        return RoleResponsibility.Builder.create(bo).build();
    }

    public static RoleResponsibilityBo from(RoleResponsibility immutable) {
        if (immutable == null) {
            return null;
        }

        RoleResponsibilityBo bo = new RoleResponsibilityBo();
        bo.roleResponsibilityId = immutable.getRoleResponsibilityId();
        bo.roleId = immutable.getRoleId();
        bo.responsibilityId = immutable.getResponsibilityId();
        bo.active = immutable.isActive();
        bo.setVersionNumber(immutable.getVersionNumber());

        return bo;
    }

    @Override
    public String getRoleResponsibilityId() {
        return roleResponsibilityId;
    }

    public void setRoleResponsibilityId(String roleResponsibilityId) {
        this.roleResponsibilityId = roleResponsibilityId;
    }

    @Override
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String getResponsibilityId() {
        return responsibilityId;
    }

    public void setResponsibilityId(String responsibilityId) {
        this.responsibilityId = responsibilityId;
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

    public ResponsibilityBo getKimResponsibility() {
        return kimResponsibility;
    }

    public void setKimResponsibility(ResponsibilityBo kimResponsibility) {
        this.kimResponsibility = kimResponsibility;
    }


}
