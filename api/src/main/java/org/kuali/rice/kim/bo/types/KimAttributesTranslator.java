/*
 * Copyright 2008 The Kuali Foundation
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

import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface KimAttributesTranslator {
	
	/**
	 * Returns whether the given set of attributes can be translated by this implementation.
	 */
    boolean supportsTranslationOfAttributes( List<String> attributeNames );
    
    /**
     * Get a list of all supported attribute names this implementation can handle.
     */
    List<String> getSupportedAttributeNames();
    
    /**
     * Returns the list of attribute names which will be output from this translator.
     */
    List<String> getResultAttributeNames();
    
    /**
     * Perform the conversion of the given attributes.
     * 
     * Note that this method should not change the passed in AttributeSet.
     */
    AttributeSet convertAttributes( AttributeSet attributes );

}
