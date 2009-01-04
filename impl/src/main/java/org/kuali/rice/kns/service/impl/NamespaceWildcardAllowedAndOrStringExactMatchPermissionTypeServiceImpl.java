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

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.impl.NamespacePermissionTypeServiceImpl;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NamespaceWildcardAllowedAndOrStringExactMatchPermissionTypeServiceImpl
		extends NamespacePermissionTypeServiceImpl {
	protected List<String> inputRequiredAttributes;
	protected String exactMatchStringAttributeName;
	protected boolean namespaceRequiredOnStoredAttributeSet;

	protected boolean performMatch(AttributeSet inputAttributeSet,
			AttributeSet storedAttributeSet) {
		validateRequiredAttributesAgainstReceived(inputRequiredAttributes,
				inputAttributeSet, REQUESTED_DETAILS_RECEIVED_ATTIBUTES_NAME);
		if (namespaceRequiredOnStoredAttributeSet) {
			return namespaceMatches(inputAttributeSet, storedAttributeSet)
					&& (!storedAttributeSet
							.containsKey(exactMatchStringAttributeName) || inputAttributeSet
							.get(exactMatchStringAttributeName).equals(
									exactMatchStringAttributeName));
		}
		return (storedAttributeSet.containsKey(exactMatchStringAttributeName) && inputAttributeSet
				.get(exactMatchStringAttributeName).equals(
						inputAttributeSet.get(exactMatchStringAttributeName)))
				|| (storedAttributeSet
						.containsKey(KimAttributes.NAMESPACE_CODE) && namespaceMatches(
						inputAttributeSet, storedAttributeSet));

	}

	public void setExactMatchStringAttributeName(
			String exactMatchStringAttributeName) {
		this.exactMatchStringAttributeName = exactMatchStringAttributeName;
		inputRequiredAttributes = new ArrayList<String>();
		inputRequiredAttributes.add(KimAttributes.NAMESPACE_CODE);
		inputRequiredAttributes.add(exactMatchStringAttributeName);
	}

	public void setNamespaceRequiredOnStoredAttributeSet(
			boolean namespaceRequiredOnStoredAttributeSet) {
		this.namespaceRequiredOnStoredAttributeSet = namespaceRequiredOnStoredAttributeSet;
	}
}