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
package org.kuali.rice.kim.service;

import java.util.List;

import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.role.dto.ResponsibilityActionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface ResponsibilityService {

    // --------------------
    // Responsibility Methods
    // --------------------

    /**
     * Get the responsibility object with the given ID.
     */
    KimResponsibilityInfo getResponsibility(String responsibilityId);
    
 	/** 
 	 * Return the responsibility object for the given unique combination of namespace,
 	 * component and responsibility name.
 	 */
    List<KimResponsibilityInfo> getResponsibilitiesByName( String responsibilityName );
    
    /**
     * Check whether the principal has the given responsibility within the passed qualifier.
     */
    boolean hasResponsibility( String principalId, String responsibilityName, AttributeSet qualification, AttributeSet responsibilityDetails );
    
   	List<ResponsibilityActionInfo> getResponsibilityActions( String responsibilityName, AttributeSet qualification, AttributeSet responsibilityDetails );
}
