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
import org.kuali.rice.kim.bo.types.KimAttributesTranslator;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.KimTypeService;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimTypeServiceBase implements KimTypeService {

	protected List<String> acceptedAttributeNames;
	
	protected List<KimAttributesTranslator> kimAttributesTranslators;

	/**
	 * Returns null, to indicate that there is no custom workflow document needed for this type.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#getWorkflowDocumentTypeName()
	 */
	public String getWorkflowDocumentTypeName() {
		return null;
	}

	/**
	 * 
	 * This method matches input attribute set entries and standard attribute set entries using liternal string match.
	 * 
	 * @param qualification
	 * @param roleQualifier
	 * @return
	 */
	public boolean performMatch(final AttributeSet inputAttributeSet, final AttributeSet standardAttributeSet) {
		for ( Map.Entry<String, String> entry : standardAttributeSet.entrySet() ) {
			if ( !inputAttributeSet.containsKey(entry.getKey() ) ) {
				return false;
			}
			if ( !StringUtils.equals(inputAttributeSet.get(entry.getKey()), entry.getValue()) ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * This method matches input attribute set entries and standard attribute set entries using wild card match.
	 * "*" is the only wildcard supported currently.
	 * 
	 * @param qualification
	 * @param roleQualifier
	 * @return
	 */
	public boolean performMatchUsingWildcard(final AttributeSet inputAttributeSet, final AttributeSet standardAttributeSet) {
		for ( Map.Entry<String, String> entry : standardAttributeSet.entrySet() ) {
			if ( !inputAttributeSet.containsKey(entry.getKey() ) ) {
				return false;
			}
			if ( !matchInputWithWildcard(inputAttributeSet.get(entry.getKey()), entry.getValue()) ) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean matchInputWithWildcard(String inputStr, String matchStr){
		inputStr.replaceAll("*", "([0-9a-zA-Z-_$]*)");
		if(matchStr.matches(inputStr)){
			return true;
		}
		return false;
	}
	
	public AttributeSet translateInputAttributeSet(final AttributeSet qualification){
		AttributeSet translatedQualification = new AttributeSet();
		translatedQualification.putAll(qualification);
		List<String> attributeNames;
		for(KimAttributesTranslator translator: getKimAttributesTranslators()){
			attributeNames = new ArrayList<String>();
			attributeNames.addAll(translatedQualification.keySet());
			if(translator.supportsTranslationOfAttributes(attributeNames)){
				translatedQualification = translator.translateAttributes(translatedQualification);
			}
		}
		return translatedQualification;
	}
	
	/**
	 * 
	 * This method ...
	 * 
	 * @param qualification
	 * @param roleQualifierList
	 * @return
	 */
	public boolean performMatches(final AttributeSet inputAttributeSet, final List<AttributeSet> standardAttributeSets){
		for ( AttributeSet standardAttributeSet : standardAttributeSets ) {
			// if one matches, return true
			if ( performMatch(inputAttributeSet, standardAttributeSet) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This is the default implementation.  It calls into the service for each attribute to
	 * validate it there.  No combination validation is done.  That should be done
	 * by overriding this method.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#validateAttributes(AttributeSet)
	 */
	public AttributeSet validateAttributes(final AttributeSet attributes) {
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
	public List<String> getAcceptedAttributeNames() {
		return this.acceptedAttributeNames;
	}

	public void setAcceptedAttributeNames(List<String> acceptedQualificationAttributeNames) {
		this.acceptedAttributeNames = acceptedQualificationAttributeNames;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.support.KimRoleTypeService#supportsQualificationAttributes(java.util.List)
	 */
	public boolean supportsAttributes(List<String> attributeNames) {
		for ( String attributeName : attributeNames ) {
			if ( !acceptedAttributeNames.contains( attributeName ) ) {
				return false;
			}
		}
		return true;
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
	
	/**
	 * @return the kimAttributesTranslators
	 */
	public List<KimAttributesTranslator> getKimAttributesTranslators() {
		return this.kimAttributesTranslators;
	}

	/**
	 * @param kimAttributesTranslators the kimAttributesTranslators to set
	 */
	public void setKimAttributesTranslators(
			List<KimAttributesTranslator> kimAttributesTranslators) {
		this.kimAttributesTranslators = kimAttributesTranslators;
	}

}
