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
package org.kuali.rice.kim.inquiry;

import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RoleMemberInquirableImpl extends KualiInquirableImpl {

	protected final String ROLE_ID = "roleId";
	protected final String NAME = "name";
	protected final String NAME_TO_DISPLAY = "nameToDisplay";
	protected final String TEMPLATE_NAME = "template.name";
	protected final String NAMESPACE_CODE = "namespaceCode";
	protected final String TEMPLATE_NAMESPACE_CODE = "template.namespaceCode";
	protected final String DETAIL_OBJECTS = "detailObjects";
	protected final String ASSIGNED_TO_ROLES = "assignedToRolesToDisplay";
	protected final String ATTRIBUTE_DATA_ID = "attributeDataId";

    protected String getKimAttributeLabelFromDD(String attributeName){
    	return KNSServiceLocatorWeb.getDataDictionaryService().getAttributeLabel(KimAttributes.class, attributeName);
    }

    protected RoleImpl getRoleImpl(String roleId){ 
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put("roleId", roleId);
		return (RoleImpl)getBusinessObjectService().findByPrimaryKey(RoleImpl.class, criteria);
    }

}
