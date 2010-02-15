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
package org.kuali.rice.kns.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.bo.BusinessObject;

/**
 * Mock lookupable helper service for the LookupResultsService test 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class LookupResultsDDBoLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {

	/**
	 * Just sends back whatever someValue was sent in - or "A" as some value if nothing else was out there
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
	 */
	public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
		final String valueToPopulate = (fieldValues.containsKey("someValue")) ? fieldValues.get("someValue") : "A";
		final LookupResultsDDBo result = new LookupResultsDDBo(valueToPopulate);
		List<LookupResultsDDBo> results = new ArrayList<LookupResultsDDBo>();
		results.add(result);
		return results;
	}

}
