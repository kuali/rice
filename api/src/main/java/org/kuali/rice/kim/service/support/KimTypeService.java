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
package org.kuali.rice.kim.service.support;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.types.KimAttributesTranslator;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 *  This is the base service interface for handling type-specific behavior.  Types can be attached
 *  to various objects (currently groups and roles) in KIM to add additional attributes and
 *  modify their behavior.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface KimTypeService {

	/** 
	 * Get the workflow document type which is needed to route objects with this type.
	 * 
	 * If no special document type is needed, this method must return null.
	 */
	String getWorkflowDocumentTypeName();
	
	/**
	 * Perform validation on the attributes of an object.  The resultant map
	 * will contain (attributeName,errorMessage) pairs from the validation process.
	 * An empty map or null indicates that there were no errors.
	 * 
	 * This method can be used to perform compound validations across multiple
	 * attributes attached to an object.
	 */
	AttributeSet validateAttributes( AttributeSet attributes );
		
	/**
	 * This method matches two attribute sets. Will be overriden by child classes according to their functional needs.
	 */
    boolean performMatch(final AttributeSet inputAttributeSet, final AttributeSet standardAttributeSet);
    
    boolean performMatches(final AttributeSet inputAttributeSet, final List<AttributeSet> standardAttributeSet);
    
    /**
     * 
     * This method returns the translators configured with this service.
     * 
     * @return
     */
	List<KimAttributesTranslator> getKimAttributesTranslators();
	
	/**
	 * 
	 * This method translates an input attribute set using the configured translators.
	 * 
	 * @param inputAttributeSet
	 * @return
	 */
	AttributeSet translateInputAttributeSet(final AttributeSet inputAttributeSet);

    /** 
     * Return a list of attribute names that will be accepted by this role type.  They
     * are either understood directly, or can be translated by this service into that
     * required. 
     */
    List<String> getAcceptedAttributeNames();
    
    /**
     * Given a list of attribute names, determine whether this service can convert that set of parameters.
     */
    boolean supportsAttributes( List<String> attributeNames );

}
