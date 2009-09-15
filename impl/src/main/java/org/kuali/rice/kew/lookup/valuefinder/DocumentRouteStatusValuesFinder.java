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
package org.kuali.rice.kew.lookup.valuefinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * This is a description of what this class does - chris don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentRouteStatusValuesFinder extends KeyValuesBase {

	/**
	 * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
	 */
	public List getKeyValues() {
		List<KeyLabelPair> keyValues = new ArrayList<KeyLabelPair>();
		Set<String> docStatusKeys = KEWConstants.DOCUMENT_STATUSES.keySet();
		for (String string : docStatusKeys) {
			KeyLabelPair keyLabel = new KeyLabelPair(string,KEWConstants.DOCUMENT_STATUSES.get(string));
			keyValues.add(keyLabel);
		}
		return keyValues;
	}

}
