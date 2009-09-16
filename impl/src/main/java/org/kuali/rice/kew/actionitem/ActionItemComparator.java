/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.actionitem;

import java.util.Comparator;

import org.kuali.rice.kew.actionrequest.ActionRequestValue;


/**
 * Compares an action item to another action item.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionItemComparator implements Comparator {

	public int compare(Object object1, Object object2) throws ClassCastException {
		ActionItem actionItem1 = (ActionItem)object1;
		ActionItem actionItem2 = (ActionItem)object2;
		int actionCodeValue = ActionRequestValue.compareActionCode(actionItem1.getActionRequestCd(), actionItem2.getActionRequestCd(), true);
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
