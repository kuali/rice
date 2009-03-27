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
package org.kuali.rice.kew.lookup.valuefinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.web.KeyValue;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * This is a description of what this class does - chris don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class SavedSearchValuesFinder extends KeyValuesBase {

	/**
	 * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
	 */
	public List getKeyValues() {
		List<KeyLabelPair> savedSearchValues = new ArrayList<KeyLabelPair>();
		savedSearchValues.add(new KeyLabelPair("", "Searches"));
		savedSearchValues.add(new KeyLabelPair("*ignore*", "-----"));
		savedSearchValues.add(new KeyLabelPair("*ignore*", "-Named Searches"));
		List<KeyValue> namedSearches = KEWServiceLocator.getDocumentSearchService().getNamedSearches(GlobalVariables.getUserSession().getPrincipalId());
		for (KeyValue keyValue : namedSearches) {
			KeyLabelPair keyLabel = new KeyLabelPair(keyValue.getKey(),keyValue.getValue());
			//TODO: truncate the label?
			savedSearchValues.add(keyLabel);
		}
		savedSearchValues.add(new KeyLabelPair("*ignore*", "-----"));
		savedSearchValues.add(new KeyLabelPair("*ignore*", "-Recent Searches"));
		List<KeyValue> mostRecentSearches = KEWServiceLocator.getDocumentSearchService().getMostRecentSearches(GlobalVariables.getUserSession().getPrincipalId());
		for (KeyValue keyValue : mostRecentSearches) {
			KeyLabelPair keyLabel = new KeyLabelPair(keyValue.getKey(),keyValue.getValue());
			//TODO: truncate the label?
			savedSearchValues.add(keyLabel);
		}
		return savedSearchValues;
	}

}
