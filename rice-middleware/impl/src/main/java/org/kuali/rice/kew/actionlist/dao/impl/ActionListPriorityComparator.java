/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kew.actionlist.dao.impl;

import java.util.Comparator;

import org.kuali.rice.kew.actionitem.ActionItemComparator;
import org.kuali.rice.kew.api.action.ActionItemContract;


/**
 * Compares an action item to another action item and determines if one
 * item has a higher priority than the other.
 * Therefore, calling code needs to ensure that the document and user on the item
 * are the same.  If action items for different documents are passed in, then the
 * compare method should always return 0.
 *
 * If the response returned from compare is less than 0, it means the first argument is
 * lower priority than the second.  If a value greater than 0 is returned it means the
 * first argument has a higher priority then the second.  If the result returned is 0, then
 * the two action items have the same priority.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionListPriorityComparator implements Comparator<ActionItemContract> {

	private ActionItemComparator itemComparator = new ActionItemComparator();

	@Override
    public int compare(ActionItemContract actionItem1, ActionItemContract actionItem2) throws ClassCastException {
		if (requiresComparison(actionItem1, actionItem2)) {
			return itemComparator.compare(actionItem1, actionItem2);
		}
		return 0;
	}

	/**
	 * Returns whether or not the two action items require comparison.  The Action List only operates
	 * on Action Items for a single user and we only care about comparing the action items if they are
	 * on the same document and for the same user.
	 */
	protected boolean requiresComparison(ActionItemContract actionItem1, ActionItemContract actionItem2) {
		return actionItem1.getDocumentId().equals(actionItem2.getDocumentId()) &&
			actionItem1.getPrincipalId().equals(actionItem2.getPrincipalId());
	}

}
