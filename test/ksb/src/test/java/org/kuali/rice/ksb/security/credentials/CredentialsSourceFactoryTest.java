/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.ksb.security.credentials;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.security.credentials.CredentialsSource;
import org.kuali.rice.core.api.security.credentials.CredentialsSourceFactory;
import org.kuali.rice.core.api.security.credentials.CredentialsSource.CredentialsType;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 0.9
 *
 */
public class CredentialsSourceFactoryTest {
	
	private CredentialsSourceFactory credentialsSourceFactory;

	@Before
	public void setUp() throws Exception {
		final CredentialsSourceFactory credentialsSourceFactory = new CredentialsSourceFactory();
		final List<CredentialsSource> credentialsSources = Arrays.asList(new CredentialsSource[] {new UsernamePasswordCredentialsSource("test", "Test"), new CasProxyTicketCredentialsSource()}); 

		credentialsSourceFactory.setCredentialsSources(credentialsSources);
		credentialsSourceFactory.afterPropertiesSet();
		
		this.credentialsSourceFactory = credentialsSourceFactory;
	}

    @Test
	public void testCredentialsSourceExistsUsernamePassword() {
		final CredentialsSource cs = this.credentialsSourceFactory.getCredentialsForType(CredentialsType.USERNAME_PASSWORD);
		assertNotNull(cs);
		assertEquals(CredentialsType.USERNAME_PASSWORD, cs.getSupportedCredentialsType());
	}

    @Test
	public void testCredentialsSourceExistsCas() {
		final CredentialsSource cs = this.credentialsSourceFactory.getCredentialsForType(CredentialsType.CAS);
		assertNotNull(cs);
		assertEquals(CredentialsType.CAS, cs.getSupportedCredentialsType());
	}

    @Test
	public void testCredentialsSourceNotExists() {
		final CredentialsSource cs = this.credentialsSourceFactory.getCredentialsForType(CredentialsType.JAAS);
		assertNull(cs);
	}
}
