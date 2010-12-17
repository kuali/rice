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
package org.kuali.rice.kew.rule;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.identity.Id;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.workgroup.GroupNameId;

/**
 * A generic Role Attribute that can be used to route to a Workgroup Name.  Can take as configuration the
 * label to use for the element name in the XML.  This allows for re-use of this component in different
 * contexts.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkgroupRoleAttribute extends AbstractIdRoleAttribute {

    private static final long serialVersionUID = 5562142284908152678L;
    
    private static final String WORKGROUP_ROLE_NAME = "workgroupName";
    private static final String ATTRIBUTE_ELEMENT = "WorkgroupRoleAttribute";
    private static final String WORKGROUP_ELEMENT = "workgroupName";
        
    public List<Role> getRoleNames() {
	List<Role> roleNames = new ArrayList<Role>();
	roleNames.add(new Role(getClass(), WORKGROUP_ROLE_NAME, "Workgroup Name"));
	return roleNames;
    }
    
    protected String getAttributeElementName() {
	return ATTRIBUTE_ELEMENT;
    }
    
    protected Id resolveId(String id) {
    	if (StringUtils.isBlank(id)) {
    		return null;
    	}
    	String groupName = Utilities.parseGroupName(id);
    	String namespace = Utilities.parseGroupNamespaceCode(id);
    	return new GroupNameId(namespace, groupName);
    }
    
    protected String getIdName() {
	return WORKGROUP_ELEMENT;
    }

    public String getWorkgroupName() {
	return getIdValue();
    }

    public void setWorkgroupName(String workgroupName) {
        setIdValue(workgroupName);
    }

}
