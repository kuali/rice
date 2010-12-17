/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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


/**
 * A defined role on a {@link RoleAttribute}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Role implements java.io.Serializable {

	private static final long serialVersionUID = 1211399058525182383L;
	private String name;
    private String baseName;
    private String label;
    
    private String returnUrl;;
    
    public Role() {
    }
    
    public String getReturnUrl() {
        return returnUrl;
    }
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
    
    public Role(Class attributeClass, String roleName, String roleLabel) {
        this.label = roleLabel;
        this.baseName = roleName;
        this.name = constructRoleValue(attributeClass.getName(), roleName);
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String roleLabel) {
        this.label = roleLabel;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String roleName) {
        this.name = roleName;
    }
    public String getBaseName() {
        return baseName;
    }
    
    public static String constructRoleValue(String attributeClassName, String roleName) {
    	return attributeClassName + "!" + roleName;
    }
}
