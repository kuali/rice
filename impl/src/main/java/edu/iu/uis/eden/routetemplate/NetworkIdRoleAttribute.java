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
package edu.iu.uis.eden.routetemplate;

import java.util.ArrayList;
import java.util.List;

import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.user.AuthenticationUserId;

/**
 * A generic Role Attribute that can be used to route to a Network ID.  Can take as configuration the
 * label to use for the element name in the XML.  This allows for re-use of this component in different
 * contexts.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NetworkIdRoleAttribute extends AbstractIdRoleAttribute {

    private static final long serialVersionUID = 8784036641718163820L;
    
    private static final String NETWORK_ID_ROLE_NAME = "networkId";
    private static final String ATTRIBUTE_ELEMENT = "NetworkIdRoleAttribute";
    private static final String NETWORK_ID_ELEMENT = "networkId";
        
    public List<Role> getRoleNames() {
	List<Role> roleNames = new ArrayList<Role>();
	roleNames.add(new Role(getClass(), NETWORK_ID_ROLE_NAME, "Network ID"));
	return roleNames;
    }
    
    protected String getAttributeElementName() {
	return ATTRIBUTE_ELEMENT;
    }
    
    protected Id resolveId(String id) {
	return id == null ? null : new AuthenticationUserId(id);
    }
    
    protected String getIdName() {
	return NETWORK_ID_ELEMENT;
    }

    public String getNetworkId() {
	return getIdValue();
    }

    public void setNetworkId(String networkId) {
        setIdValue(networkId);
    }

}
