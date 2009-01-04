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

import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ParameterPermissionTypeServiceImpl extends
		NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImpl {
	/**
	 * @see org.kuali.rice.kns.service.impl.NamespaceOrComponentPermissionTypeServiceImpl#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet,
	 *      org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	protected boolean performMatch(AttributeSet inputAttributeSet,
			AttributeSet storedAttributeSet) {
		return super.performMatch(inputAttributeSet, storedAttributeSet)
				&& (!storedAttributeSet
						.containsKey(KimAttributes.PARAMETER_NAME) || inputAttributeSet
						.get(KimAttributes.PARAMETER_NAME).equals(
								KimAttributes.PARAMETER_NAME));
	}

	@Override
	public void setExactMatchStringAttributeName(
			String exactMatchStringAttributeName) {
		super.setExactMatchStringAttributeName(exactMatchStringAttributeName);
		inputRequiredAttributes.add(KimAttributes.PARAMETER_NAME);
	}
}
