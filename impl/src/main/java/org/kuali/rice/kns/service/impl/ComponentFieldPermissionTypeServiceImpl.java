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
package org.kuali.rice.kns.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ComponentFieldPermissionTypeServiceImpl extends KimPermissionTypeServiceBase {

	/**
	 * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	protected boolean performMatch(AttributeSet inputAttributeSet, AttributeSet storedAttributeSet) {		
		if (StringUtils.isEmpty(inputAttributeSet.get(KimConstants.KIM_ATTRIB_COMPONENT_CLASS)) || StringUtils.isEmpty(inputAttributeSet.get(KimConstants.KIM_ATTRIB_PROPERTY_NAME))) {
        	throw new RuntimeException("Both " + KimConstants.KIM_ATTRIB_COMPONENT_CLASS + " and " + KimConstants.KIM_ATTRIB_PROPERTY_NAME + " should not be blank or null.");
		}		
		
		return inputAttributeSet.get(KimConstants.KIM_ATTRIB_COMPONENT_CLASS).equals(storedAttributeSet.get(KimConstants.KIM_ATTRIB_COMPONENT_CLASS))
			&& inputAttributeSet.get(KimConstants.KIM_ATTRIB_PROPERTY_NAME).startsWith(storedAttributeSet.get(KimConstants.KIM_ATTRIB_PROPERTY_NAME));
	}

}
