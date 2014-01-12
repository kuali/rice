/**
 * Copyright 2005-2014 The Kuali Foundation
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

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Outbox item.  An extension of {@link ActionItemActionListExtension} for OJB.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name="KREW_OUT_BOX_ITM_T")
public class OutboxItem extends ActionItemBase {

	private static final long serialVersionUID = 5776214610837858304L;

	public OutboxItem() {}

	public OutboxItem(ActionItem actionItem) {
		this.setActionRequestCd(actionItem.getActionRequestCd());
		this.setActionRequestId(actionItem.getActionRequestId());
		this.setDateAssigned(actionItem.getDateAssigned());
		this.setDelegationType(actionItem.getDelegationType());
		this.setDelegatorPrincipalId(actionItem.getDelegatorPrincipalId());
		this.setDelegatorGroupId(actionItem.getDelegatorGroupId());
		this.setDocHandlerURL(actionItem.getDocHandlerURL());
		this.setDocLabel(actionItem.getDocLabel());
		this.setDocName(actionItem.getDocName());
		this.setDocTitle(actionItem.getDocTitle());
		this.setResponsibilityId(actionItem.getResponsibilityId());
		this.setRoleName(actionItem.getRoleName());
		this.setDocumentId(actionItem.getDocumentId());
		this.setPrincipalId(actionItem.getPrincipalId());
		this.setGroupId(actionItem.getGroupId());
		this.setRequestLabel(actionItem.getRequestLabel());
	}

}
