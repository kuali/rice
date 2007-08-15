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

import org.acegisecurity.Authentication;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.x509.X509AuthenticationToken;
import org.kuali.rice.security.credentials.Credentials;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.3 $ $Date: 2007-08-15 15:49:56 $
 * @since 0.9
 *
 */
public class SecurityUtils {

	private SecurityUtils() {
		// nothing to do
	}
	
	public static Authentication convertCredentialsToSecurityContext(final Credentials credentials) {
		if (credentials instanceof X509Credentials) {
			final X509Credentials c = (X509Credentials) credentials;
			return new X509AuthenticationToken(c.getX509Certificate());
		} else if (credentials instanceof UsernamePasswordCredentials) {
			final UsernamePasswordCredentials c = (UsernamePasswordCredentials) credentials;
			return new UsernamePasswordAuthenticationToken(c.getUsername(), c.getPassword());
		}
		
		return null;
	}
	
}
