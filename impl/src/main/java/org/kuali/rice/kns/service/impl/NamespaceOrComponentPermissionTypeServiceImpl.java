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
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.impl.NamespacePermissionTypeServiceImpl;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NamespaceOrComponentPermissionTypeServiceImpl extends NamespacePermissionTypeServiceImpl {

	{
		inputRequiredAttributes.add(KimAttributes.COMPONENT_NAME);
	}

	/**
	 * @see org.kuali.rice.kns.service.impl.NamespaceCodePermissionTypeServiceImpl#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	protected boolean performMatch(AttributeSet inputAttributeSet, AttributeSet storedAttributeSet) {
		// Checking the namespace first
		boolean namespaceMatch = super.performMatch(inputAttributeSet, storedAttributeSet);

		// Checking component class.
		if (StringUtils.isEmpty(storedAttributeSet.get(KimAttributes.COMPONENT_NAME))) {
        	return namespaceMatch;
		}

		// Both must match at this point
		return namespaceMatch 
				&& inputAttributeSet.get(KimAttributes.COMPONENT_NAME).equals(storedAttributeSet.get(KimAttributes.COMPONENT_NAME));
	}

}