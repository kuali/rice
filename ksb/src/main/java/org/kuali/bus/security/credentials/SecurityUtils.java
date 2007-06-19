package org.kuali.bus.security.credentials;

import org.acegisecurity.Authentication;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.x509.X509AuthenticationToken;
import org.kuali.rice.security.credentials.Credentials;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:11 $
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
