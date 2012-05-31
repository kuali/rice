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
import java.util.List;

import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiMaintenanceForm;

/**
 * A values finder for returning KEW rule delegation type codes.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DelegationTypeValuesFinder extends KeyValuesBase {

	private static final List<KeyLabelPair> delegationTypes = new ArrayList<KeyLabelPair>();
	private static final List<KeyLabelPair> delegationTypesForMaintDocs = new ArrayList<KeyLabelPair>();
	static {
		for (String delegationType : KEWConstants.DELEGATION_TYPES.keySet()) {
			delegationTypes.add(new KeyLabelPair(delegationType, KEWConstants.DELEGATION_TYPES.get(delegationType)));
			// Use a separate delegation types list for the related maintenance docs, since they should disallow the "Both" option.
			if (!KEWConstants.DELEGATION_BOTH.equals(delegationType)) {
				delegationTypesForMaintDocs.add(new KeyLabelPair(delegationType, KEWConstants.DELEGATION_TYPES.get(delegationType)));
			}
		}
	}
	
	public List<KeyLabelPair> getKeyValues() {
		// Return the appropriate delegation types list, depending on whether or not it is needed for a maintenance doc.
		return (GlobalVariables.getKualiForm() instanceof KualiMaintenanceForm) ? delegationTypesForMaintDocs : delegationTypes;
	}

}
