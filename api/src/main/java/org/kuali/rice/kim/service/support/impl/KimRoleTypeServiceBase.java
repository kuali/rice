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
package org.kuali.rice.kim.service.support.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.KimRoleTypeService;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimRoleTypeServiceBase implements KimRoleTypeService {

//	private static Logger LOG = Logger.getLogger(KimRoleTypeServiceBase.class);
	
	protected List<String> acceptedQualificationAttributeNames; 
	
	/**
	 * Performs a simple check that the qualifier on the role matches the qualification.
	 * Extra qualification attributes are ignored.
	 * 
	 * @see KimRoleTypeService#doesRoleQualifierMatchQualification(AttributeSet, AttributeSet)
	 */
	public boolean doesRoleQualifierMatchQualification(AttributeSet qualification, AttributeSet roleQualifier) {
		
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
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#doRoleQualifiersMatchQualification(AttributeSet, List)
	 */
	public boolean doRoleQualifiersMatchQualification(
			AttributeSet qualification,
			List<AttributeSet> roleQualifierList) {
		for ( AttributeSet roleQualifier : roleQualifierList ) {
			// if one matches, return true
			if ( doesRoleQualifierMatchQualification(qualification, roleQualifier) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return an empty list since this method should not be called by the role service for this service type.
	 * Subclasses which are application role types should override this method.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#getPrincipalIdsFromApplicationRole(java.lang.String, AttributeSet)
	 */
	public List<String> getPrincipalIdsFromApplicationRole(String roleName,
			AttributeSet qualification) {
		return new ArrayList<String>(0);
	}
	
	/**
	 * Default to not being an application role type.  Always returns false.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#isApplicationRoleType()
	 */
	public boolean isApplicationRoleType() {
		return false;
	}
	
	/**
	 * Returns null, to indicate that there is no custom workflow document needed for this type.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#getWorkflowDocumentTypeName()
	 */
	public String getWorkflowDocumentTypeName() {
		return null;
	}

	/**
	 * This is the default implementation.  It calls into the service for each attribute to
	 * validate it there.  No combination validation is done.  That should be done
	 * by overriding this method.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#validateAttributes(AttributeSet)
	 */
	public AttributeSet validateAttributes(AttributeSet attributes) {
		AttributeSet validationErrors = new AttributeSet();
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
	 * Simple implementation, simply returns the passed in qualification in a single-element list.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#getAllImpliedQualifications(AttributeSet)
	 */
	public List<AttributeSet> getAllImpliedQualifications(
			AttributeSet qualification) {
		ArrayList<AttributeSet> impliedQualifications = new ArrayList<AttributeSet>( 1 );
		impliedQualifications.add(qualification);
		return impliedQualifications;
	}
	
	/**
	 * Simple implementation, simply returns the passed in qualification in a single-element list.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#getAllImplyingQualifications(AttributeSet)
	 */
	public List<AttributeSet> getAllImplyingQualifications(
			AttributeSet qualification) {
		ArrayList<AttributeSet> implyingQualifications = new ArrayList<AttributeSet>( 1 );
		implyingQualifications.add(qualification);
		return implyingQualifications;
	}

	/**
	 * Returns null to indicate no errors (does not perform any validation.)
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#validateAttribute(java.lang.String, java.lang.String)
	 */
	public List<String> validateAttribute(String attributeName, String attributeValue) {
		return null;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#getAcceptedQualificationAttributeNames()
	 */
	public List<String> getAcceptedQualificationAttributeNames() {
		return this.acceptedQualificationAttributeNames;
	}

	public void setAcceptedQualificationAttributeNames(List<String> acceptedQualificationAttributeNames) {
		this.acceptedQualificationAttributeNames = acceptedQualificationAttributeNames;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#supportsQualificationAttributes(java.util.List)
	 */
	public boolean supportsQualificationAttributes(List<String> attributeNames) {
		for ( String attributeName : attributeNames ) {
			if ( !acceptedQualificationAttributeNames.contains( attributeName ) ) {
				return false;
			}
		}
		return true;
	}	
	/**
	 * No conversion performed.  Simply returns the passed in Map.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#convertQualificationAttributesToRequired(AttributeSet)
	 */
	public AttributeSet convertQualificationAttributesToRequired(
			AttributeSet qualificationAttributes) {
		return qualificationAttributes;
	}
	
	/**
	 * Returns null - no inquiry.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#getInquiryUrl(java.lang.String, java.util.Map)
	 */
	public String getInquiryUrl(String attributeName, AttributeSet relevantAttributeData) {
		return null;
	}
	
	/**
	 * Returns null - no lookup.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#getLookupUrl(java.lang.String)
	 */
	public String getLookupUrl(String attributeName) {
		return null;
	}
}
