/*
 * Copyright 2007-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.role;


import org.kuali.rice.kim.framework.common.delegate.DelegationTypeService;
import org.kuali.rice.kim.impl.type.KimTypeServiceBase;

import java.util.Map;

public class KimDelegationTypeServiceBase extends KimTypeServiceBase implements DelegationTypeService {

	public boolean doesDelegationQualifierMatchQualification(
			Map<String, String> qualification, Map<String, String> delegationQualifier) {
		boolean matches = true;
		for (String qualificationKey : qualification.keySet()) {
			if (delegationQualifier.containsKey(qualificationKey)) {
				matches = matches && qualification.get(qualificationKey).equals(delegationQualifier.get(qualificationKey));
			}
		}
		return matches;
	}
	
}
