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
package org.kuali.rice.kew.actionrequest;

import org.kuali.rice.kim.bo.entity.KimPrincipal;

/**
 * Represents an ActionRequest recipient who is a KimGroup
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimPrincipalRecipient implements Recipient {

	private static final long serialVersionUID = 1L;
	private KimPrincipal principal;

	public KimPrincipalRecipient(String principalId) {
		this(ActionRequestFactory.getIdentityManagementService().getPrincipal(principalId));
	}

	public KimPrincipalRecipient(KimPrincipal principal) {
		if (principal == null) {
			throw new IllegalArgumentException("Attempted to create a KimPrincipalRecipient with a null KimPrincipal!");
		}
		this.principal = principal;
	}

	public KimPrincipal getPrincipal() {
		return this.principal;
	}

	public String getPrincipalId() {
		return getPrincipal().getPrincipalId();
	}


}
