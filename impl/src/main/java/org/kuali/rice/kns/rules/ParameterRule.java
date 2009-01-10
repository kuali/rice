/*
 * Copyright 2007 The Kuali Foundation Licensed under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

/**
 * This is a description of what this class does - kellerj don't forget to fill
 * this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ParameterRule extends MaintenanceDocumentRuleBase {

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
	 */
	@Override
	protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
		boolean result = super.processCustomRouteDocumentBusinessRules( document );

		result &= checkWorkgroup( document.getDocumentHeader().getWorkflowDocument()
				.getRouteHeader().getInitiatorPrincipalId(), (Parameter)getOldBo(), (Parameter)getNewBo() );

		return result;
	}

	protected boolean checkWorkgroup(String initiatorPrincipalId, Parameter oldBO, Parameter newBO) {
		boolean result = true;
		// don't check if workgroup is blank
		if ( StringUtils.isNotBlank( newBO.getParameterWorkgroupName() ) ) {
			// check that the workgroup exists
			result = org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().getGroupByName(Utilities.parseGroupNamespaceCode(newBO.getParameterWorkgroupName()), Utilities.parseGroupName(newBO.getParameterWorkgroupName()) ) != null;
			if ( result ) {
				// get the initiator user record
			    	Person user = org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().getPerson(initiatorPrincipalId);
			    	if (user == null) {
			    	    LOG.error( "Unable to find initiator user: " + initiatorPrincipalId + " via user service" );
			    	    throw new RuntimeException( "Unable to find initiator user: " + initiatorPrincipalId + " via user service");
			    	}
				if ( oldBO == null || StringUtils.isBlank( oldBO.getParameterWorkgroupName() ) ) { // creating
																									// a
																									// new
																									// parameter
					result = KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), Utilities.parseGroupNamespaceCode(newBO.getParameterWorkgroupName()), Utilities.parseGroupName(newBO.getParameterWorkgroupName()) );
					if ( !result ) {
						putFieldError( "parameterWorkgroupName",
								"error.document.parameter.workgroupName.notinnew", newBO
										.getParameterWorkgroupName() );
					}
				} else { // editing an existing parameter
					result = KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), Utilities.parseGroupNamespaceCode(newBO.getParameterWorkgroupName()), Utilities.parseGroupName(oldBO.getParameterWorkgroupName()) )
							&& KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), Utilities.parseGroupNamespaceCode(newBO.getParameterWorkgroupName()), Utilities.parseGroupName(newBO.getParameterWorkgroupName()) );
					if ( !result ) {
						putFieldError( "parameterWorkgroupName",
								"error.document.parameter.workgroupName.notinboth" );
					}
				}
			} else {
				putFieldError( "parameterWorkgroupName",
						"error.document.parameter.workgroupName.invalid", newBO
								.getParameterWorkgroupName() );
			}
		}
		return result;
	}

}

