package org.kuali.rice.security.credentials;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.security.credentials.CredentialsSource.CredentialsType;
import org.springframework.beans.factory.InitializingBean;

/**
 * CredentialsSourceFactory constructs or returns an existing instance of a
 * CredentialsSource based on the type of Credentials requested.
 * <p>
 * It will return null if it cannot find or create an instance of the required
 * type.
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:13 $
 * @since 0.9
 * 
 */
public final class CredentialsSourceFactory implements InitializingBean {

	private final Log log = LogFactory.getLog(this.getClass());

	private List<CredentialsSource> credentialsSources;

	private Map<CredentialsType, CredentialsSource> credentialsSourcesByType = new HashMap<CredentialsType, CredentialsSource>();

	public CredentialsSource getCredentialsForType(
			final CredentialsType credentialsType) {
		return credentialsSourcesByType.get(credentialsType);
	}

	public void afterPropertiesSet() throws Exception {
		if (credentialsSources != null) {
			for (final CredentialsSource credentialsSource : this.credentialsSources) {
				this.credentialsSourcesByType.put(credentialsSource
						.getSupportedCredentialsType(), credentialsSource);
			}
		} else {
			log
					.warn("No CredentialsSources set.  No security will be provided on the bus.");
		}
	}

	public void setCredentialsSources(
			final List<CredentialsSource> credentialsSources) {
		this.credentialsSources = credentialsSources;
	}
}
