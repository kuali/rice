/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.document.authorization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementRoleDocumentAuthorizer extends IdentityManagementKimDocumentAuthorizer {

	@Override
	protected void addPermissionDetails(BusinessObject businessObject,
			Map<String, String> attributes) {
		super.addPermissionDetails(businessObject, attributes);
		attributes.put(KimAttributes.ENTITY_TYPE_CODE, KimConstants.EntityTypes.PERSON);
	}
	
	public Set<String> getReadOnlyEntityPropertyNames(Document document, Person user, Set<String> securePotentiallyReadOnlyEntityPropertyNames) {
		Set<String> readOnlyEntityPropertyNames = new HashSet<String>();
		for (String securePotentiallyReadOnlyEntityPropertyName : securePotentiallyReadOnlyEntityPropertyNames) {
			Map<String,String> collectionOrFieldLevelPermissionDetails = new HashMap<String,String>();
			collectionOrFieldLevelPermissionDetails.put(KimAttributes.PROPERTY_NAME, securePotentiallyReadOnlyEntityPropertyName);
			if (!isAuthorizedByTemplate(document, KimConstants.NAMESPACE_CODE, KimConstants.PermissionTemplateNames.MODIFY_ENTITY, user.getPrincipalId(), collectionOrFieldLevelPermissionDetails, null)) {
				readOnlyEntityPropertyNames.add(securePotentiallyReadOnlyEntityPropertyName);
			}
		}
		return readOnlyEntityPropertyNames;
	}

}
