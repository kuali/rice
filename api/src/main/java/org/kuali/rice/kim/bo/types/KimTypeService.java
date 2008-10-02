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
package org.kuali.rice.kim.bo.types;

import java.util.List;
import java.util.Map;

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
	Map<String,String> validateAttributes( Map<String,String> attributes );
	
	/** Validates a single attribute data element.
	 * 
	 * @return List of validation error message.  Returning an empty list or null indicates that
	 * there are no errors.
	 */
	List<String> validateAttribute( String attributeName, String attributeValue );
	
	/** Provide an absolute URL for performing a lookup on this field. 
	 * 
	 * Return null for no lookup.
	 */
	String getLookupUrl( String attributeName );
	
	/** Provides the absolute inquiry URL for the given attribute.  All attributes on the
	 * KimAttributeContainer will be passed so that other field values may be used if needed.
	 * (As in the case of a multi-part primary key.)
	 * 
	 * Return null for no inquiry URL.
	 */
	String getInquiryUrl( String attributeName, Map<String,String> relevantAttributeData );
	
	/** Return a data dictionary AttributeDefinition for use rendering in the UI. */
	// QUESTION: Do some DD classes need to be moved into the API for use like this?  ANSWER: Yes
	// AND: is that feasible if we are looking toward a web service implementation
	// do we need a DTO for some DD artifacts?
	//AttributeDefinition getAttributeDefinition( String attributeName );
	
	// more?
	
}
