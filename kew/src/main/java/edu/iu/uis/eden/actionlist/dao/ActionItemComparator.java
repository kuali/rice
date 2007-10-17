/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.actionlist.dao;

import java.util.Comparator;

import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;

/**
 * Compares an action item to another action item.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionItemComparator implements Comparator {

	public int compare(Object object1, Object object2) throws ClassCastException {
		ActionItem actionItem1 = (ActionItem)object1;
		ActionItem actionItem2 = (ActionItem)object2;
		int actionCodeValue = ActionRequestValue.compareActionCode(actionItem1.getActionRequestCd(), actionItem2.getActionRequestCd());
		if (actionCodeValue != 0) {
			return actionCodeValue;
		}
		int recipientTypeValue = ActionRequestValue.compareRecipientType(actionItem1.getRecipientTypeCode(), actionItem2.getRecipientTypeCode());
		if (recipientTypeValue != 0) {
			return recipientTypeValue;
		}
		return ActionRequestValue.compareDelegationType(actionItem1.getDelegationType(), actionItem2.getDelegationType());
	}

}
