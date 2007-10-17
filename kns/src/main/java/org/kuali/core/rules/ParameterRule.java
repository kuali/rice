/*
 * Copyright 2007 The Kuali Foundation Licensed under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.kuali.core.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Parameter;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.rice.KNSServiceLocator;

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
	 * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
	 */
	@Override
	protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
		boolean result = super.processCustomRouteDocumentBusinessRules( document );

		result &= checkWorkgroup( document.getDocumentHeader().getWorkflowDocument()
				.getInitiatorNetworkId(), (Parameter)getOldBo(), (Parameter)getNewBo() );

		return result;
	}

	protected boolean checkWorkgroup(String initiatorUserId, Parameter oldBO, Parameter newBO) {
		boolean result = true;
		// don't check if workgroup is blank
		if ( StringUtils.isNotBlank( newBO.getParameterWorkgroupName() ) ) {
			// check that the workgroup exists
			result = KNSServiceLocator.getKualiGroupService().groupExists(
					newBO.getParameterWorkgroupName() );
			if ( result ) {
				// get the initiator user record
				UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();
				if ( oldBO == null || StringUtils.isBlank( oldBO.getParameterWorkgroupName() ) ) { // creating
																									// a
																									// new
																									// parameter
					result = user.isMember( newBO.getParameterWorkgroupName() );
					if ( !result ) {
						putFieldError( "parameterWorkgroupName",
								"error.document.parameter.workgroupName.notinnew", newBO
										.getParameterWorkgroupName() );
					}
				} else { // editing an existing parameter
					result = user.isMember( oldBO.getParameterWorkgroupName() )
							&& user.isMember( newBO.getParameterWorkgroupName() );
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
