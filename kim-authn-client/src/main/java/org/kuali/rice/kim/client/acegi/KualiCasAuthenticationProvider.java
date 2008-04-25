/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.rice.kim.client.acegi;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.cas.CasAuthenticationProvider;
import org.acegisecurity.providers.cas.CasAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;

/**
 * A {@link CasAuthenticationProvider} implementation that integrates with 
 * Kuali Identity Management (KIM).<p>This 
 * <code>CasAuthenticationProvider</code> is capable of validating {@link
 * UsernamePasswordAuthenticationToken} requests which contains a user name 
 * and authentication source attribute. It can also validate a previously 
 * created {@link CasAuthenticationToken}.</p>
 *
 * Verifies the the <code>UserDetails</code> associated with a CAS 
 * authenticated CAS ticket response.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
*/
public class KualiCasAuthenticationProvider extends CasAuthenticationProvider {
    

    /**
     * This overridden method ...
     * 
     * @see org.acegisecurity.providers.cas.CasAuthenticationProvider#authenticateNow(Authentication authentication)
     */
    private CasAuthenticationToken authenticateNow(Authentication authentication) throws AuthenticationException {
        // Validate
        KualiTicketResponse response = (KualiTicketResponse)this.getTicketValidator().confirmTicketValid(authentication.getCredentials().toString());

        // Check proxy list is trusted
        this.getCasProxyDecider().confirmProxyListTrusted(response.getProxyList());

        // Lookup user details        
        UserDetails userDetails = ((KualiCasAuthoritiesPopulator)this.getCasAuthoritiesPopulator()).getUserDetails(response);        

        // Construct CasAuthenticationToken
        return new CasAuthenticationToken(this.getKey(), userDetails, authentication.getCredentials(),
            userDetails.getAuthorities(), userDetails, response.getProxyList(), response.getProxyGrantingTicketIou());
    }
}