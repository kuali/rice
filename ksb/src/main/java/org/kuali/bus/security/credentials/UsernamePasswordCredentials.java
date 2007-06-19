package org.kuali.bus.security.credentials;

import org.kuali.rice.security.credentials.Credentials;
import org.springframework.util.Assert;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:12 $
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
