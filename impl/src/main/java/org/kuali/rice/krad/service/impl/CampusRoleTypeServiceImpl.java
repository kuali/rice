/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;

public class CampusRoleTypeServiceImpl extends KimRoleTypeServiceBase {

	{
		workflowRoutingAttributes.add( KimConstants.AttributeConstants.CAMPUS_CODE );
		requiredAttributes.add( KimConstants.AttributeConstants.CAMPUS_CODE );
//		checkRequiredAttributes = true;
	}

	/**
	 * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#getAttributeDefinitions(java.lang.String)
	 */
	@Override
	public AttributeDefinitionMap getAttributeDefinitions(String kimTypeId) {
		AttributeDefinitionMap map = super.getAttributeDefinitions(kimTypeId);
		for (AttributeDefinition definition : map.values()) {
			if (KimConstants.AttributeConstants.CAMPUS_CODE.equals(definition.getName())) {
				definition.setRequired(Boolean.TRUE);
			}
		}
		return map;
	}
}
