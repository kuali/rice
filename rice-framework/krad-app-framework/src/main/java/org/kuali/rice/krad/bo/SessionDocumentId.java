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
package org.kuali.rice.krad.bo;

import org.kuali.rice.krad.data.jpa.IdClassBase;

/**
 * ID Class for SessionDocument.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class SessionDocumentId extends IdClassBase {

    private static final long serialVersionUID = 2455522802669321846L;

    private String sessionId;
	private String documentNumber;
	private String principalId;
	private String ipAddress;

	public SessionDocumentId() {}

	public SessionDocumentId(String documentNumber, String sessionId, String principalId, String ipAddress) {
		this.documentNumber = documentNumber;
		this.sessionId = sessionId;
		this.principalId = principalId;
		this.ipAddress = ipAddress;
	}

	public String getDocumentNumber() {
		return this.documentNumber;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public String getPrincipalId() {
		return this.principalId;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

}
