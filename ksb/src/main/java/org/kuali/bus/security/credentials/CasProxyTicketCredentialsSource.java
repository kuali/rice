/*
 * Copyright 2005 - 2007 The Kuali Foundation.
 * 
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

import java.io.IOException;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.cas.CasAuthenticationToken;
import org.acegisecurity.ui.cas.CasProcessingFilter;
import org.kuali.rice.security.credentials.Credentials;
import org.kuali.rice.security.credentials.CredentialsSource;

import edu.yale.its.tp.cas.proxy.ProxyTicketReceptor;

/**
 * Retrieves a proxy ticket for the user based on their provided Proxy Granting
 * Ticket. This assumes that the Proxy GrantingTicket is available from a
 * ThreadLocal and that the service is protected by Acegi.
 * <p>
 * Note: this class can be used for user-to-service authentication.
 * <p>
 * This assumes the services are stateless and will not maintain an HttpSession
 * so each call for credentials will result in a new Proxy Ticket being granted.
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:12 $
 * @since 0.9
 * @see ThreadLocal
 * @see ProxyTicketReceptor
 */
public final class CasProxyTicketCredentialsSource implements CredentialsSource {

    public Credentials getCredentials(final String serviceEndpoint) {
    	final String proxyGrantingTicketIou = ((CasAuthenticationToken) (SecurityContextHolder
                .getContext()).getAuthentication()).getProxyGrantingTicketIou();
            try {
                final String proxyTicket = ProxyTicketReceptor.getProxyTicket(
                    proxyGrantingTicketIou, serviceEndpoint);
                return new UsernamePasswordCredentials(
                        CasProcessingFilter.CAS_STATELESS_IDENTIFIER, proxyTicket);
                } catch (final IOException e) {
                    return null;
                }
	}

    public CredentialsType getSupportedCredentialsType() {
        return CredentialsType.CAS;
    }
}
