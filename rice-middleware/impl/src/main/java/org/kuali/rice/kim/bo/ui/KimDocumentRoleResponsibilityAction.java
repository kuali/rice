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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kew.api.util.CodeTranslator;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityBo;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name = "KRIM_PND_ROLE_RSP_ACTN_MT")
public class KimDocumentRoleResponsibilityAction extends KimDocumentBoEditableBase {

    private static final long serialVersionUID = 696663543888096105L;

    @PortableSequenceGenerator(name = "KRIM_ROLE_RSP_ACTN_ID_S")
    @GeneratedValue(generator = "KRIM_ROLE_RSP_ACTN_ID_S")
    @Id
    @Column(name = "ROLE_RSP_ACTN_ID")
    protected String roleResponsibilityActionId;

    @Column(name = "ROLE_RSP_ID")
    protected String roleResponsibilityId;

    @Column(name = "ROLE_MBR_ID")
    protected String roleMemberId;

    @Column(name = "ACTN_TYP_CD")
    protected String actionTypeCode;

    @Column(name = "ACTN_PLCY_CD")
    protected String actionPolicyCode;

    @Column(name = "PRIORITY_NBR")
    protected Integer priorityNumber;

    @Column(name = "FRC_ACTN")
    @Convert(converter = BooleanYNConverter.class)
    protected boolean forceAction;

    // temporary default value in lieu of optimistic locking                       
//    @Column(name = "VER_NBR")
//    protected Long versionNumber = (long) 0;

    @Transient
    protected ResponsibilityBo kimResponsibility;

    @ManyToOne(targetEntity = RoleResponsibilityBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "ROLE_RSP_ID", referencedColumnName = "ROLE_RSP_ID", insertable = false, updatable = false)
    protected RoleResponsibilityBo roleResponsibility;

    /*{
		roleResponsibility = new RoleResponsibilityImpl();
		roleResponsibility.setKimResponsibility(new KimResponsibilityImpl());
		roleResponsibility.getKimResponsibility().setTemplate(new KimResponsibilityTemplateImpl());
	}*/
    /**
	 * @return the kimResponsibility
	 */
    public ResponsibilityBo getKimResponsibility() {
        if (kimResponsibility == null && getRoleResponsibility() != null) {
            //TODO: this needs to be changed to use the KimResponsibilityInfo object                       
            // but the changes are involved in the UiDocumentService based on the copyProperties method used                       
            // to move the data to/from the document/real objects                       
            Responsibility info = KimApiServiceLocator.getResponsibilityService().getResponsibility(getRoleResponsibility().getResponsibilityId());
            kimResponsibility = ResponsibilityBo.from(info);
        }
        return kimResponsibility;
    }

    /**
	 * @param kimResponsibility the kimResponsibility to set
	 */
    public void setKimResponsibility(ResponsibilityBo kimResponsibility) {
        this.kimResponsibility = kimResponsibility;
    }

    public String getRoleResponsibilityActionId() {
        return this.roleResponsibilityActionId;
    }

    public void setRoleResponsibilityActionId(String roleResponsibilityResolutionId) {
        this.roleResponsibilityActionId = roleResponsibilityResolutionId;
    }

    public String getRoleResponsibilityId() {
        return this.roleResponsibilityId;
    }

    public void setRoleResponsibilityId(String roleResponsibilityId) {
        this.roleResponsibilityId = roleResponsibilityId;
    }

    public String getActionTypeCode() {
        return this.actionTypeCode;
    }

    public void setActionTypeCode(String actionTypeCode) {
        this.actionTypeCode = actionTypeCode;
    }

    public Integer getPriorityNumber() {
        return this.priorityNumber;
    }

    public void setPriorityNumber(Integer priorityNumber) {
        this.priorityNumber = priorityNumber;
    }

    public String getActionPolicyCode() {
        return this.actionPolicyCode;
    }

    public void setActionPolicyCode(String actionPolicyCode) {
        this.actionPolicyCode = actionPolicyCode;
    }

    public String getRoleMemberId() {
        return this.roleMemberId;
    }

    public void setRoleMemberId(String roleMemberId) {
        this.roleMemberId = roleMemberId;
    }

    /**
	 * 
	 * This method fore readonlyalterdisplay
	 * 
	 * @return
	 */
    public String getActionPolicyDescription() {
        return CodeTranslator.approvePolicyLabels.get(this.actionPolicyCode);
    }

    /**
	 * 
	 * This method fore readonlyalterdisplay
	 * 
	 * @return
	 */
    public String getActionTypeDescription() {
        return CodeTranslator.arLabels.get(this.actionTypeCode);
    }

    /**
	 * @return the roleResponsibility
	 */
    public RoleResponsibilityBo getRoleResponsibility() {
        if (roleResponsibility == null && roleResponsibilityId != null) {
            //TODO: this needs to be changed to use the KimResponsibilityInfo object                       
            // but the changes are involved in the UiDocumentService based on the copyProperties method used                       
            // to move the data to/from the document/real objects                       
            roleResponsibility = KradDataServiceLocator.getDataObjectService().find(RoleResponsibilityBo.class, getRoleResponsibilityId());
        }
        return roleResponsibility;
    }

    /**
	 * @param roleResponsibility the roleResponsibility to set
	 */
    public void setRoleResponsibility(RoleResponsibilityBo roleResponsibility) {
        this.roleResponsibility = roleResponsibility;
    }

    /**
	 * @return the forceAction
	 */
    public boolean isForceAction() {
        return this.forceAction;
    }

    /**
	 * @param forceAction the forceAction to set
	 */
    public void setForceAction(boolean forceAction) {
        this.forceAction = forceAction;
    }
}
