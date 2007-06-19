package org.kuali.bus.security.credentials;

import junit.framework.TestCase;

import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.x509.X509AuthenticationToken;
import org.kuali.bus.security.credentials.X509CredentialsSourceTest.KualiX509Certificate;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:13 $
 * @since 0.9
 *
 */
public class SecurityUtilsTest extends TestCase {
	
	public void testUsernamePasswordCredentials() {
		final UsernamePasswordCredentials c = new UsernamePasswordCredentials("test", "test");
		final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) SecurityUtils.convertCredentialsToSecurityContext(c);
		
		assertEquals(token.getPrincipal(), c.getUsername());
		assertEquals(token.getCredentials(), c.getPassword());
	}
	
	public void testX509CertificateCredentials() {
		final X509Credentials c = new X509Credentials(new KualiX509Certificate());
		final X509AuthenticationToken token = (X509AuthenticationToken) SecurityUtils.convertCredentialsToSecurityContext(c);
		
		assertEquals(c.getX509Certificate(), token.getCredentials());
	}

}
