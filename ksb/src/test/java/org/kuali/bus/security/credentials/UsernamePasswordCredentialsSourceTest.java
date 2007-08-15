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

import junit.framework.TestCase;

import org.kuali.rice.security.credentials.Credentials;
import org.kuali.rice.security.credentials.CredentialsSource.CredentialsType;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.3 $ $Date: 2007-08-15 15:49:52 $
 * @since 0.9
 * 
 */
public class UsernamePasswordCredentialsSourceTest extends TestCase {

	private static final String USERNAME = "username";

	private static final String PASSWORD = "password";

	private UsernamePasswordCredentialsSource credentialsSource;

	protected void setUp() throws Exception {
		this.credentialsSource = new UsernamePasswordCredentialsSource(
				USERNAME, PASSWORD);
		super.setUp();
	}

	public void testGetter() {
		final Credentials c = this.credentialsSource
				.getCredentials("http://www.cnn.com");
		assertNotNull(c);
		assertTrue(c instanceof UsernamePasswordCredentials);

		final UsernamePasswordCredentials upc = (UsernamePasswordCredentials) c;
		assertEquals(USERNAME, upc.getUsername());
		assertEquals(PASSWORD, upc.getPassword());

		assertEquals(CredentialsType.USERNAME_PASSWORD,
				this.credentialsSource.getSupportedCredentialsType());
	}
}
