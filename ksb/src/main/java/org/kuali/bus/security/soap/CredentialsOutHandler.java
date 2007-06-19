package org.kuali.bus.security.soap;

import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.codehaus.xfire.security.wss4j.WSS4JOutHandler;
import org.kuali.bus.security.credentials.UsernamePasswordCredentials;
import org.kuali.rice.security.credentials.Credentials;
import org.kuali.rice.security.credentials.CredentialsSource;
import org.springframework.util.Assert;

import edu.iu.uis.eden.messaging.ServiceInfo;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:14 $
 * @since 0.9
 * 
 */
public class CredentialsOutHandler extends WSS4JOutHandler {

	private final CredentialsSource credentialsSource;

	private final ServiceInfo serviceInfo;

	public CredentialsOutHandler(final CredentialsSource credentialsSource,
			final ServiceInfo serviceInfo) {
		Assert.notNull(credentialsSource, "credentialsSource cannot be null.");
		Assert.notNull(serviceInfo, "serviceInfo cannot be null.");
		this.credentialsSource = credentialsSource;
		this.serviceInfo = serviceInfo;

		final Credentials credentials = this.credentialsSource
				.getCredentials(this.serviceInfo.getEndpointUrl());

		Assert.isTrue(credentials instanceof UsernamePasswordCredentials,
				"Credentials must be of type usernamepassword.");

		final UsernamePasswordCredentials c = (UsernamePasswordCredentials) credentials;
		setProperty(WSHandlerConstants.USER, c.getUsername());
	}

	public WSPasswordCallback getPassword(final String username,
			final int doAction, final String clsProp, final String refProp,
			final RequestData reqData) throws WSSecurityException {
		final UsernamePasswordCredentials c = (UsernamePasswordCredentials) this.credentialsSource
				.getCredentials(this.serviceInfo.getEndpointUrl());

		return new WSPasswordCallback(c.getUsername(), c.getPassword(), null,
				WSPasswordCallback.USERNAME_TOKEN);
	}
}
