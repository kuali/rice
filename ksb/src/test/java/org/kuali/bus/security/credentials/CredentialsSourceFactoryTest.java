package org.kuali.bus.security.credentials;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.kuali.rice.security.credentials.CredentialsSource;
import org.kuali.rice.security.credentials.CredentialsSourceFactory;
import org.kuali.rice.security.credentials.CredentialsSource.CredentialsType;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:13 $
 * @since 0.9
 *
 */
public class CredentialsSourceFactoryTest extends TestCase {
	
	private CredentialsSourceFactory credentialsSourceFactory;

	@Override
	protected void setUp() throws Exception {
		final CredentialsSourceFactory credentialsSourceFactory = new CredentialsSourceFactory();
		final List<CredentialsSource> credentialsSources = Arrays.asList(new CredentialsSource[] {new UsernamePasswordCredentialsSource("test", "Test"), new CasProxyTicketCredentialsSource()}); 

		credentialsSourceFactory.setCredentialsSources(credentialsSources);
		credentialsSourceFactory.afterPropertiesSet();
		
		this.credentialsSourceFactory = credentialsSourceFactory;
		super.setUp();
	}
	
	public void testCredentialsSourceExistsUsernamePassword() {
		final CredentialsSource cs = this.credentialsSourceFactory.getCredentialsForType(CredentialsType.USERNAME_PASSWORD);
		assertNotNull(cs);
		assertEquals(CredentialsType.USERNAME_PASSWORD, cs.getSupportedCredentialsType());
	}

	public void testCredentialsSourceExistsCas() {
		final CredentialsSource cs = this.credentialsSourceFactory.getCredentialsForType(CredentialsType.CAS);
		assertNotNull(cs);
		assertEquals(CredentialsType.CAS, cs.getSupportedCredentialsType());
	}

	public void testCredentialsSourceNotExists() {
		final CredentialsSource cs = this.credentialsSourceFactory.getCredentialsForType(CredentialsType.JAAS);
		assertNull(cs);
	}
}
