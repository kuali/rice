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

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.springframework.util.Assert;

/**
 * Populates the <code>UserDetails</code> associated with a CAS authenticated
 * user by reading the response.
 *  
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KualiCasAuthoritiesPopulatorImpl implements KualiCasAuthoritiesPopulator {
    private KualiUserDetailsService userDetailsService;
    
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
    }
    
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = (KualiUserDetailsService)userDetailsService;
    }
    
    public UserDetails getUserDetails(String casUserId)
        throws AuthenticationException {
        return this.userDetailsService.loadUserByUsername(casUserId);
    }
    
    public UserDetails getUserDetails(KualiTicketResponse response) 
        throws AuthenticationException {
        return this.userDetailsService.loadUserByTicketResponse(response);
    }

}
