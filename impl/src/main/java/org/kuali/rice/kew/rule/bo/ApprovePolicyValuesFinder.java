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
package org.kuali.rice.kew.rule.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;

/**
 * A values finder for returning KEW approve policy codes.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ApprovePolicyValuesFinder extends KeyValuesBase {

	private static final List<KeyValue> APPROVED_PROLICIES;
	static {
		final List<KeyValue> temp = new ArrayList<KeyValue>();
		for (String delegationType : KEWConstants.APPROVE_POLICIES.keySet()) {
			temp.add(new ConcreteKeyValue(delegationType, KEWConstants.APPROVE_POLICIES.get(delegationType)));
		}
		APPROVED_PROLICIES = Collections.unmodifiableList(temp);
	}
	
	@Override
	public List<KeyValue> getKeyValues() {
		return APPROVED_PROLICIES;
	}

}
