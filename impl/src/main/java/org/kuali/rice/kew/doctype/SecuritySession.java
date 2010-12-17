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
package org.kuali.rice.kew.doctype;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.kns.UserSession;


/**
 * Caches information about various security constraints for a single user which have already been
 * analyzed and don't need to be analyzed again.  For example, once it's been determined that
 * a user is a member of a workgroup or a role, it is not necessary to requery the
 * workgroup or role for information on whether that user is a member.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SecuritySession implements Serializable {

	private static final long serialVersionUID = 2542191040889845305L;

	private final UserSession userSession;
	private Map<String, Boolean> authenticatedWorkgroups = new HashMap<String, Boolean>();
	private Map<String, Boolean> passesRoleSecurity = new HashMap<String, Boolean>();

	private Map<String, DocumentTypeSecurity> documentTypeSecurity = new HashMap<String, DocumentTypeSecurity>();

	public SecuritySession(UserSession userSession) {
		this.userSession = userSession;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public Map<String, Boolean> getPassesRoleSecurity() {
		return passesRoleSecurity;
	}

	public void setPassesRoleSecurity(Map<String, Boolean> passesRoleSecurity) {
		this.passesRoleSecurity = passesRoleSecurity;
	}

	public Map<String, Boolean> getAuthenticatedWorkgroups() {
		return authenticatedWorkgroups;
	}

	public void setAuthenticatedWorkgroups(Map<String, Boolean> authenticatedWorkgroups) {
		this.authenticatedWorkgroups = authenticatedWorkgroups;
	}

	public Map<String, DocumentTypeSecurity> getDocumentTypeSecurity() {
		return documentTypeSecurity;
	}

	public void setDocumentTypeSecurity(Map<String, DocumentTypeSecurity> documentTypeSecurity) {
		this.documentTypeSecurity = documentTypeSecurity;
	}

}
