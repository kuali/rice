/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.bus.security.credentials;

import org.kuali.rice.security.credentials.Credentials;
import org.springframework.util.Assert;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.3 $ $Date: 2007-08-15 15:49:56 $
 * @since 0.9
 *
 */
public final class UsernamePasswordCredentials implements Credentials {

	private final String username;
	
	private final String password;
	
	public UsernamePasswordCredentials(final String username, final String password) {
		this.username = username;
		this.password = password;
		
		Assert.notNull(this.username, "username cannote be null.");
		Assert.notNull(this.password, "password cannote be null.");
	}

	public String getPassword() {
		return this.password;
	}

	public String getUsername() {
		return this.username;
	}
	
	
	
}
