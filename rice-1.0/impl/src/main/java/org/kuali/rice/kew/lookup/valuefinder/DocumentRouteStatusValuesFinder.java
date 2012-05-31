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
import java.util.Collection;
import java.util.List;

import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;

/**
 * This is a description of what this class does - chris don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentRouteStatusValuesFinder extends KeyValuesBase {

	private static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}

	/**
	 * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
	 */
	public List getKeyValues() {
		List<KeyLabelPair> keyValues = new ArrayList<KeyLabelPair>();
		List<String> docStatusParentKeys = asSortedList(KEWConstants.DOCUMENT_STATUS_PARENT_TYPES.keySet());

		for (String parentKey : docStatusParentKeys) {
			KeyLabelPair keyLabel = new KeyLabelPair(parentKey,parentKey + " Statuses");
			keyValues.add(keyLabel);

			// each parent key, pending, successful, unsuccessful each has a sub list of real document statuses
			List<String> docStatusCodes = KEWConstants.DOCUMENT_STATUS_PARENT_TYPES.get(parentKey);
			for(String docStatusCode : docStatusCodes){
				KeyLabelPair docStat = new KeyLabelPair(docStatusCode, "- "+ KEWConstants.DOCUMENT_STATUSES.get(docStatusCode));
				keyValues.add(docStat);
			}
		}
		return keyValues;
	}

}
