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
package org.kuali.rice.kim.bo.role.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kim.bo.role.KimRoleTypeService;
import org.kuali.rice.kim.bo.role.RoleMember;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public abstract class KimRoleTypeServiceBase implements KimRoleTypeService {

	private static Logger LOG = Logger.getLogger(KimRoleTypeServiceBase.class);
	/**
	 * Performs a simple check that the qualifier on the role matches the qualification.
	 * Extra qualification attributes are ignored.
	 * 
	 * @see org.kuali.rice.kim.bo.role.KimRoleTypeService#doesRoleCriteriaMatchQualifications(java.util.Map, java.util.Map)
	 */
	public boolean doesRoleQualifierMatchQualification(java.util.Map<String,String> qualification, java.util.Map<String,String> roleQualifier) {
		
		for ( Map.Entry<String, String> entry : roleQualifier.entrySet() ) {
			if ( !qualification.containsKey(entry.getKey() ) ) {
				return false;
			}
			if ( !StringUtils.equals(qualification.get(entry.getKey()), entry.getValue()) ) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Simple pass-thru for {@link #doesRoleQualifierMatchQualification(Map, Map)}
	 * 
	 * @see org.kuali.rice.kim.bo.role.KimRoleTypeService#doesRoleMemberMatchQualification(java.util.Map, org.kuali.rice.kim.bo.role.RoleMember)
	 */
	public boolean doesRoleMemberMatchQualification(
			Map<String, String> qualification, RoleMember roleMember) {
		return doesRoleQualifierMatchQualification(qualification, roleMember.getQualifierAsMap());
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.KimRoleTypeService#doRoleQualifiersMatchQualification(java.util.Map, java.util.List)
	 */
	public boolean doRoleQualifiersMatchQualification(
			Map<String, String> qualification,
			List<Map<String, String>> roleQualifierList) {
		for ( Map<String,String> roleQualifier : roleQualifierList ) {
			// if one matches, return true
			if ( doesRoleQualifierMatchQualification(qualification, roleQualifier) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.role.KimRoleTypeService#doRoleMembersMatchQualification(java.util.Map, java.util.List)
	 */
	public boolean doRoleMembersMatchQualification(
			Map<String, String> qualification, List<RoleMember> roleMemberList) {
		for ( RoleMember roleMember : roleMemberList ) {
			if ( doesRoleMemberMatchQualification(qualification, roleMember) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.role.KimRoleTypeService#getMembersThatMatchQualification(java.util.Map, java.util.List)
	 */
	public List<RoleMember> getMembersThatMatchQualification(
			Map<String, String> qualification, List<RoleMember> roleMemberList) {
		List<RoleMember> members = new ArrayList<RoleMember>();
		for ( RoleMember roleMember : roleMemberList ) {
			if ( doesRoleMemberMatchQualification(qualification, roleMember) ) {
				members.add(roleMember);
			}
		}
		return members;
	}
	
	/**
	 * Returns null, to indicate that there is no custom workflow document needed for this type.
	 * 
	 * @see org.kuali.rice.kim.bo.types.KimTypeService#getWorkflowDocumentTypeName()
	 */
	public String getWorkflowDocumentTypeName() {
		return null;
	}

	/**
	 * This is the default implementation.  It calls into the service for each attribute to
	 * validate it there.  No combination validation is done.  That should be done
	 * by overriding this method.
	 * 
	 * @see org.kuali.rice.kim.bo.types.KimTypeService#validateAttributes(org.kuali.rice.kim.bo.types.KimAttributeContainer)
	 */
	public Map<String,String> validateAttributes(Map<String,String> attributes) {
		Map<String,String> validationErrors = new HashMap<String, String>();
		// call the individual field validators
		for ( Map.Entry<String,String> attribute : attributes.entrySet() ) {
			List<String> attributeErrors = validateAttribute( attribute.getKey(), attribute.getValue() );
			if ( attributeErrors != null ) {
				for ( String err : attributeErrors ) {
					validationErrors.put(attribute.getKey(), err);
				}
			}
		}
		return validationErrors;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.role.KimRoleTypeService#getAllImpliedQualifications(java.util.Map)
	 */
	public List<Map<String, String>> getAllImpliedQualifications(
			Map<String, String> qualification) {
		ArrayList<Map<String,String>> impliedQualifications = new ArrayList<Map<String,String>>( 1 );
		impliedQualifications.add(qualification);
		return impliedQualifications;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.role.KimRoleTypeService#getAllImplyingQualifications(java.util.Map)
	 */
	public List<Map<String, String>> getAllImplyingQualifications(
			Map<String, String> qualification) {
		ArrayList<Map<String,String>> implyingQualifications = new ArrayList<Map<String,String>>( 1 );
		implyingQualifications.add(qualification);
		return implyingQualifications;
	}
}
