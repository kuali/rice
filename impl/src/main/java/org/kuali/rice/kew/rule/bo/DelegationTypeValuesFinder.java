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
package org.kuali.rice.kew.rule.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.web.comparator.KeyLabelComparator;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * A values finder for returning KEW rule delegation type codes.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DelegationTypeValuesFinder extends KeyValuesBase {

	private static final List<KeyLabelPair> delegationTypes = new ArrayList<KeyLabelPair>();
	static {
		delegationTypes.add(new KeyLabelPair("", ""));
		for (String delegationType : KEWConstants.DELEGATION_TYPES.keySet()) {
			delegationTypes.add(new KeyLabelPair(delegationType, KEWConstants.DELEGATION_TYPES.get(delegationType)));
		}
		Collections.sort(delegationTypes, new KeyLabelComparator());
	}
	
	public List<KeyLabelPair> getKeyValues() {
		return delegationTypes;
	}

}
