/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kim.bo.ui;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityBo;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.springframework.util.AutoPopulatingList;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_PND_ROLE_RSP_T")
public class KimDocumentRoleResponsibility extends KimDocumentBoActivatableBase {

    private static final long serialVersionUID = -4465768714850961538L;

    @PortableSequenceGenerator(name = "KRIM_ROLE_RSP_ID_S")
    @GeneratedValue(generator = "KRIM_ROLE_RSP_ID_S")
    @Id
    @Column(name = "ROLE_RSP_ID")
    protected String roleResponsibilityId;

    @Column(name = "ROLE_ID")
    protected String roleId;

    @Column(name = "RSP_ID")
    protected String responsibilityId;

    // temporary default value in lieu of optimistic locking                       
//    @Column(name = "VER_NBR")
//    protected Long versionNumber = 0L;

    @Transient
    protected ResponsibilityBo kimResponsibility;

    @OneToMany(targetEntity = KimDocumentRoleResponsibilityAction.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumns({ 
            @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false) 
            ,@JoinColumn(name = "ROLE_RSP_ID", referencedColumnName = "ROLE_RSP_ID", insertable = false, updatable = false) 
    })
    protected List<KimDocumentRoleResponsibilityAction> roleRspActions = new AutoPopulatingList<KimDocumentRoleResponsibilityAction>(KimDocumentRoleResponsibilityAction.class);

    @Transient
    protected String name;

    @Transient
    protected String namespaceCode;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public void setRoleResponsibilityId(String roleResponsibilityId) {
        this.roleResponsibilityId = roleResponsibilityId;
    }

    /**
	 * @return the roleResponsibilityId
	 */
    public String getRoleResponsibilityId() {
        return this.roleResponsibilityId;
    }

    /**
	 * @return the kimResponsibility
	 */
    public ResponsibilityBo getKimResponsibility() {
        if (kimResponsibility == null && responsibilityId != null) {
            //TODO: this needs to be changed to use the KimResponsibilityInfo object                       
            // but the changes are involved in the UiDocumentService based on the copyProperties method used                       
            // to move the data to/from the document/real objects                       
            Responsibility info = KimApiServiceLocator.getResponsibilityService().getResponsibility(getResponsibilityId());
            kimResponsibility = ResponsibilityBo.from(info);
        }
        return this.kimResponsibility;
    }

    /**
	 * @param responsibilityId the responsibilityId to set
	 */
    public void setResponsibilityId(String responsibilityId) {
        this.responsibilityId = responsibilityId;
    }

    /**
	 * @param kimResponsibility the kimResponsibility to set
	 */
    public void setKimResponsibility(ResponsibilityBo kimResponsibility) {
        this.kimResponsibility = kimResponsibility;
    }

    /**
	 * @return the responsibilityId
	 */
    public String getResponsibilityId() {
        return this.responsibilityId;
    }

    /**
	 * @return the roleRspActions
	 */
    public KimDocumentRoleResponsibilityAction getRoleRspAction() {
        if (this.roleRspActions != null && this.roleRspActions.size() > 0)
            return this.roleRspActions.get(0);
        return null;
    }

    /**
	 * @return the roleRspActions
	 */
    public List<KimDocumentRoleResponsibilityAction> getRoleRspActions() {
        return this.roleRspActions;
    }

    /**
	 * @param roleRspActions the roleRspActions to set
	 */
    public void setRoleRspActions(List<KimDocumentRoleResponsibilityAction> roleRspActions) {
        this.roleRspActions = roleRspActions;
    }

    public String getName() {
        if (null == kimResponsibility) {
            getKimResponsibility();
        }
        if (null == kimResponsibility) {
            return "";
        }
        return kimResponsibility.getName();
    }

    public String getNamespaceCode() {
        if (null == kimResponsibility) {
            getKimResponsibility();
        }
        if (null == kimResponsibility) {
            return "";
        }
        return kimResponsibility.getNamespaceCode();
    }
}
