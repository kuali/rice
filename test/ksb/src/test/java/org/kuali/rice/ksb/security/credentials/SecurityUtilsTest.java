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

import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.x509.X509AuthenticationToken;
import org.junit.Test;
import org.kuali.rice.ksb.security.credentials.X509CredentialsSourceTest.KualiX509Certificate;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 0.9
 *
 */
public class SecurityUtilsTest {

    @Test
	public void testUsernamePasswordCredentials() {
		final UsernamePasswordCredentials c = new UsernamePasswordCredentials("test", "test");
		final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) SecurityUtils.convertCredentialsToSecurityContext(c);
		
		assertEquals(token.getPrincipal(), c.getUsername());
		assertEquals(token.getCredentials(), c.getPassword());
	}

    @Test
	public void testX509CertificateCredentials() {
		final X509Credentials c = new X509Credentials(new KualiX509Certificate());
		final X509AuthenticationToken token = (X509AuthenticationToken) SecurityUtils.convertCredentialsToSecurityContext(c);
		
		assertEquals(c.getX509Certificate(), token.getCredentials());
	}

}
