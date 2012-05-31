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
package org.kuali.rice.kew.web;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationResult {

	private boolean authorized;
	private List<String> messages;

	public AuthorizationResult(boolean authorized, List<String> messages) {
		this.authorized = authorized;
		this.messages = messages;
	}

	public AuthorizationResult(boolean authorized) {
		this(authorized, new ArrayList<String>());
	}

	public AuthorizationResult(boolean authorized, String message) {
		this(authorized);
		getMessages().add(message);
	}

	public boolean isAuthorized() {
		return authorized;
	}

	public List<String> getMessages() {
		return messages;
	}

}
