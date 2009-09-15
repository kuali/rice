/*
 * Copyright 2007-2009 The Kuali Foundation
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

import java.util.LinkedHashMap;

import org.kuali.rice.kew.util.CodeTranslator;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.impl.ResponsibilityServiceImpl;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimDocumentRoleResponsibilityAction extends KimDocumentBoBase {
	private static final long serialVersionUID = 696663543888096105L;
	protected String roleResponsibilityActionId;
	protected String roleResponsibilityId;
	protected String roleMemberId;
	protected String actionTypeCode;
	protected String actionPolicyCode;
	protected Integer priorityNumber;
	protected boolean forceAction;
	protected KimResponsibilityImpl kimResponsibility;
	protected RoleResponsibilityImpl roleResponsibility;
	
	/*{
		roleResponsibility = new RoleResponsibilityImpl();
		roleResponsibility.setKimResponsibility(new KimResponsibilityImpl());
		roleResponsibility.getKimResponsibility().setTemplate(new KimResponsibilityTemplateImpl());
	}*/
	
	/**
	 * @return the kimResponsibility
	 */
	public KimResponsibilityImpl getKimResponsibility() {
		try {
			if ( ObjectUtils.isNull( kimResponsibility ) && ObjectUtils.isNotNull( getRoleResponsibility() ) ) {
				//TODO: this needs to be changed to use the KimResponsibilityInfo object
				// but the changes are involved in the UiDocumentService based on the copyProperties method used
				// to move the data to/from the document/real objects
				kimResponsibility = ((ResponsibilityServiceImpl)KIMServiceLocator.getResponsibilityService()).getResponsibilityImpl(getRoleResponsibility().getResponsibilityId());
			}
		} catch( RuntimeException ex ) {
			ex.printStackTrace();
			throw ex;
		}
		return kimResponsibility;
	}
	/**
	 * @param kimResponsibility the kimResponsibility to set
	 */
	public void setKimResponsibility(KimResponsibilityImpl kimResponsibility) {
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
	
	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap lhm = new LinkedHashMap();
		lhm.put( "roleResponsibilityActionId", roleResponsibilityActionId );
		lhm.put( "roleResponsibilityId", roleResponsibilityId );
		lhm.put( "roleMemberId", roleMemberId );
		lhm.put( "actionTypeCode", actionTypeCode );
		return lhm;
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
		return (String)CodeTranslator.approvePolicyLabels.get(this.actionPolicyCode);
	}

	/**
	 * 
	 * This method fore readonlyalterdisplay
	 * 
	 * @return
	 */
	public String getActionTypeDescription() {
		return (String)CodeTranslator.arLabels.get(this.actionTypeCode);
	}
	/**
	 * @return the roleResponsibility
	 */
	public RoleResponsibilityImpl getRoleResponsibility() {
		if ( ObjectUtils.isNull( roleResponsibility ) && roleResponsibilityId != null ) {
			//TODO: this needs to be changed to use the KimResponsibilityInfo object
			// but the changes are involved in the UiDocumentService based on the copyProperties method used
			// to move the data to/from the document/real objects
			roleResponsibility = ((ResponsibilityServiceImpl)KIMServiceLocator.getResponsibilityService()).getRoleResponsibilityImpl(getRoleResponsibilityId());
		}
		return roleResponsibility;
	}
	/**
	 * @param roleResponsibility the roleResponsibility to set
	 */
	public void setRoleResponsibility(RoleResponsibilityImpl roleResponsibility) {
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
