/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kns.inquiry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.authorization.BusinessObjectAuthorizerBase;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.util.KNSConstants;

public class InquiryAuthorizerBase extends BusinessObjectAuthorizerBase implements
		InquiryAuthorizer {
	/**
	 * Implement this method to flag sections as restricted and get the
	 * framework to check the permission for you
	 * 
	 * @return Set of section ids that need to be hidden for particular users
	 */
	public Set<String> getSecurePotentiallyHiddenSectionIds() {
		return new HashSet<String>();
	}
}
