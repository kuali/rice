package org.kuali.bus.security.credentials;

import junit.framework.TestCase;

import org.kuali.rice.security.credentials.Credentials;
import org.kuali.rice.security.credentials.CredentialsSource.CredentialsType;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:13 $
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
